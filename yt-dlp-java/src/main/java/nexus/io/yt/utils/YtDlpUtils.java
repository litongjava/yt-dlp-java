package nexus.io.yt.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import nexus.io.tio.utils.commandline.ProcessResult;
import nexus.io.tio.utils.environment.EnvUtils;
import nexus.io.yt.builder.YtDlpOption;
import nexus.io.yt.builder.YtDlpOptionBuilder;
import nexus.io.yt.consts.YtDlpConst;

public class YtDlpUtils {

  public static final String URL_TEMPLATE = "https://www.youtube.com/watch?v=%s";
  public static final String DOWNLOAD_FOLDER = "download";

  /**
   * Prefer H.264 + M4A (plays everywhere), then any MP4 video (may be AV1), a
   * single MP4, and finally any streams merged into MP4.
   */
  public static final String MP4_FORMAT = "bv*[ext=mp4][vcodec^=avc1]+ba[ext=m4a]/bv*[ext=mp4]+ba[ext=m4a]/b[ext=mp4]/bv*+ba";

  /**
   * Output name inside the media folder. The title is truncated to 150 bytes so
   * that long (e.g. CJK) titles do not exceed the 255-byte file-name limit.
   */
  public static final String OUTPUT_TEMPLATE = "%(title).150B.%(ext)s";

  private static final Pattern VIDEO_ID = Pattern.compile("[A-Za-z0-9_-]{1,64}");
  /** yt-dlp intermediate stream files such as {@code title.f137.mp4}. */
  private static final Pattern INTERMEDIATE = Pattern.compile(".*\\.f\\d+(-[A-Za-z0-9]+)?\\.[^.]+$");
  private static final String TEMP_FOLDER = ".tmp";

  /** Striped locks: serializes downloads of the same folder without an unbounded map. */
  private static final ReentrantLock[] LOCKS = new ReentrantLock[64];
  static {
    for (int i = 0; i < LOCKS.length; i++) {
      LOCKS[i] = new ReentrantLock();
    }
  }

  public static ProcessResult downloadMp4(String videoId, boolean quiet) throws IOException, InterruptedException {
    return downloadMp4(videoId, quiet, EnvUtils.getStr(YtDlpConst.YT_DLP_PROXY));
  }

  public static ProcessResult downloadMp4(String videoId, boolean quiet, String proxy)
      throws IOException, InterruptedException {
    YtDlpOptionBuilder builder = builder(proxy, EnvUtils.getStr(YtDlpConst.YT_DLP_COOKIES)).noPlaylist()
        .format(MP4_FORMAT).mergeOutputFormat("mp4");
    return downloadMedia(videoId, "mp4", ".mp4", builder, quiet);
  }

  public static ProcessResult downloadMp3(String videoId, boolean quiet) throws IOException, InterruptedException {
    return downloadAudio(videoId, quiet, "mp3", "mp3", ".mp3");
  }

  public static ProcessResult downloadMp3(String proxy, String videoId, boolean quiet)
      throws IOException, InterruptedException {
    String cookies = EnvUtils.getStr(YtDlpConst.YT_DLP_COOKIES);
    return downloadMp3(proxy, cookies, videoId, quiet);
  }

  public static ProcessResult downloadMp3(String proxy, String cookies, String videoId, boolean quiet)
      throws IOException, InterruptedException {
    return downloadAudio(proxy, cookies, videoId, quiet, "mp3", "mp3", ".mp3");
  }

  public static String getDownlaodFolder(String videoId, String subFolderName) {
    String folder = DOWNLOAD_FOLDER + File.separator + videoId + File.separator + subFolderName;
    return folder;
  }

  public static ProcessResult downloadAudio(String proxy, String cookies, String videoId, boolean quiet, String format,
      String subFolderName, String suffix) throws IOException, InterruptedException {
    YtDlpOptionBuilder builder = builder(proxy, cookies).noPlaylist().audio().audioFormat(format);
    return downloadMedia(videoId, subFolderName, suffix, builder, quiet);
  }

  public static ProcessResult downloadAudio(String videoId, boolean quiet, String format, String subFolderName,
      String suffix) throws IOException, InterruptedException {
    YtDlpOptionBuilder builder = builder().noPlaylist().audio().audioFormat(format);
    return downloadMedia(videoId, subFolderName, suffix, builder, quiet);
  }

  /**
   * Returns the cached file or downloads it. Concurrent calls for the same
   * folder are serialized; intermediate files are written to a temporary folder
   * so that an incomplete download is never returned as a result.
   *
   * @return a result with {@code path} set when the file exists, otherwise the
   *         yt-dlp process result (check exit code and stderr).
   */
  private static ProcessResult downloadMedia(String videoId, String subFolderName, String suffix,
      YtDlpOptionBuilder builder, boolean quiet) throws IOException, InterruptedException {
    if (videoId == null || !VIDEO_ID.matcher(videoId).matches()) {
      throw new IllegalArgumentException("Invalid video id: " + videoId);
    }
    String folder = getDownlaodFolder(videoId, subFolderName);
    File outDir = new File(folder);

    ReentrantLock lock = LOCKS[Math.floorMod(folder.hashCode(), LOCKS.length)];
    lock.lockInterruptibly();
    try {
      File cached = findMedia(outDir, suffix);
      if (cached != null) {
        return ProcessResult.fromFile(cached, true);
      }

      // A relative temp path is resolved against home by yt-dlp.
      Path tempDir = Paths.get(folder, TEMP_FOLDER);
      builder.paths("home:" + folder).paths("temp:" + TEMP_FOLDER).output(OUTPUT_TEMPLATE)
          .url(String.format(URL_TEMPLATE, videoId));
      if (quiet) {
        builder.quiet();
      }

      ProcessResult result;
      try {
        result = YtDlp.execute(builder.build());
      } finally {
        deleteRecursively(tempDir);
      }

      File file = findMedia(outDir, suffix);
      if (file != null) {
        ProcessResult fileResult = ProcessResult.fromFile(file, false);
        fileResult.setTaskId(result.getTaskId());
        fileResult.setExitCode(result.getExitCode());
        fileResult.setElapsed(result.getElapsed());
        return fileResult;
      }
      return result;
    } finally {
      lock.unlock();
    }
  }

  /** Finds a completed media file, ignoring logs, hidden and intermediate files. */
  static File findMedia(File outDir, String suffix) {
    File[] files = outDir.listFiles();
    if (files == null) {
      return null;
    }
    for (File file : files) {
      String name = file.getName();
      if (file.isFile() && name.endsWith(suffix) && !name.startsWith(".") && !INTERMEDIATE.matcher(name).matches()) {
        return file;
      }
    }
    return null;
  }

  private static void deleteRecursively(Path dir) throws IOException {
    if (!Files.exists(dir)) {
      return;
    }
    try (Stream<Path> walk = Files.walk(dir)) {
      walk.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
    }
  }

  public static ProcessResult downlodSubtitle(String videoId, boolean quiet) throws IOException, InterruptedException {
    return downlodSubtitle(videoId, quiet, null);
  }

  public static ProcessResult downlodSubtitle(String videoId, boolean quiet, String proxy)
      throws IOException, InterruptedException {

    String folder = DOWNLOAD_FOLDER + "/" + videoId + "/sub";
    String suffix = ".vtt";

    File outDir = new File(folder);
    File[] listFiles = outDir.listFiles();

    if (listFiles != null && listFiles.length > 0) {
      for (File file : listFiles) {
        if (file.getName().endsWith(suffix)) {
          List<String> lines = Files.readAllLines(file.toPath());
          String subtitle = String.join(System.lineSeparator(), lines);
          return new ProcessResult(subtitle, true);
        }
      }

    }

    String url = String.format(URL_TEMPLATE, videoId);

    String output = folder + "/%(title)s.%(ext)s";
    YtDlpOptionBuilder builder = new YtDlpOptionBuilder().url(url).output(output).writeSub().skipDownload();
    applyProxy(builder, proxy);
    if (quiet) {
      builder.quiet();
    }
    YtDlpOption options = builder.build();

    ProcessResult execute = YtDlp.execute(folder, options);
    listFiles = outDir.listFiles();
    if (listFiles != null && listFiles.length > 0) {
      for (File file : listFiles) {
        if (file.getName().endsWith(suffix)) {
          List<String> lines = Files.readAllLines(file.toPath());
          String subtitle = String.join(System.lineSeparator(), lines);
          return new ProcessResult(subtitle);
        }
      }

    }
    return execute;

  }

  public static ProcessResult downloadAutoSubtitle(String videoId, boolean quiet)
      throws IOException, InterruptedException {
    return downloadAutoSubtitle(videoId, quiet, null);
  }

  public static ProcessResult downloadAutoSubtitle(String videoId, boolean quiet, String proxy)
      throws IOException, InterruptedException {
    String url = String.format(URL_TEMPLATE, videoId);
    String folder = DOWNLOAD_FOLDER + "/" + videoId + "/vtt";
    String output = folder + "/%(title)s.%(ext)s";

    YtDlpOptionBuilder builder = new YtDlpOptionBuilder().url(url).output(output).writeAutoSub().skipDownload();
    if (quiet) {
      builder.quiet();
    }

    applyProxy(builder, proxy);

    YtDlpOption options = builder.build();
    return YtDlp.execute(folder, options);
  }

  public static ProcessResult downloadAutoSubtitle(String videoId, String type, boolean quiet)
      throws IOException, InterruptedException {
    return downloadAutoSubtitle(videoId, type, quiet, null);
  }

  public static ProcessResult downloadAutoSubtitle(String videoId, String type, boolean quiet, String proxy)
      throws IOException, InterruptedException {
    String url = String.format(URL_TEMPLATE, videoId);
    String folder = DOWNLOAD_FOLDER + "/" + videoId + "/" + type;
    String output = folder + "/%(title)s.%(ext)s";

    YtDlpOptionBuilder builder = new YtDlpOptionBuilder().url(url).output(output).writeSub().writeAutoSub()
        .convertSubs(type).skipDownload();
    applyProxy(builder, proxy);
    if (quiet) {
      builder.quiet();
    }
    YtDlpOption options = builder.build();
    return YtDlp.execute(folder, options);
  }

  public static YtDlpOptionBuilder builder(String proxy, String cookies) {
    YtDlpOptionBuilder builder = new YtDlpOptionBuilder();
    applyProxy(builder, proxy);
    applyCookies(builder, cookies);
    applyJsRruntimes(builder);
    return builder;
  }

  public static YtDlpOptionBuilder builder() {
    YtDlpOptionBuilder builder = new YtDlpOptionBuilder();
    applyProxy(builder);
    applyCookies(builder);
    applyJsRruntimes(builder);
    return builder;
  }

  public static void applyCookies(YtDlpOptionBuilder builder) {
    String cookies = EnvUtils.getStr(YtDlpConst.YT_DLP_COOKIES);
    applyCookies(builder, cookies);
  }

  public static void applyProxy(YtDlpOptionBuilder builder) {
    String proxy = EnvUtils.getStr(YtDlpConst.YT_DLP_PROXY);
    applyProxy(builder, proxy);

  }

  public static void applyProxy(YtDlpOptionBuilder builder, String proxy) {
    if (proxy != null) {
      String value = proxy.trim();
      if (!value.isEmpty()) {
        builder.proxy(value);
      }
    }
  }

  public static void applyCookies(YtDlpOptionBuilder builder, String cookies) {
    if (cookies != null) {
      String value = cookies.trim();
      if (!value.isEmpty()) {
        builder.cookies(value);
      }
    }
  }

  public static void applyJsRruntimes(YtDlpOptionBuilder builder) {
    String denoBinPath = null;
    String os = System.getProperty("os.name").toLowerCase();
    if (os.startsWith("windows")) {
      String str = EnvUtils.getStr(YtDlpConst.DENO_BIN_WIN);
      if (str != null && Files.exists(Paths.get(str))) {
        denoBinPath = str;
      }
    } else if (os.startsWith("linux")) {

      String str = EnvUtils.getStr(YtDlpConst.DENO_BIN_LINUX);
      if (str != null && Files.exists(Paths.get(str))) {
        denoBinPath = str;
      }
    } else {
      String str = EnvUtils.getStr(YtDlpConst.DENO_BIN_MAC);
      if (str != null && Files.exists(Paths.get(str))) {
        denoBinPath = str;
      }
    }

    if (denoBinPath != null) {
      String value = denoBinPath.trim();
      if (!value.isEmpty()) {
        builder.jsRuntimesDeno(value);
      }
    }
  }

  /**
   * Retrieves the available formats for the specified URL.
   *
   * @param url The URL of the video.
   * @return The available formats as a String.
   * @throws InterruptedException
   * @throws IOException
   */
  public static ProcessResult getAvailableFormats(String url) throws IOException, InterruptedException {
    YtDlpOptionBuilder ytDlpOptionBuilder = new YtDlpOptionBuilder();
    YtDlpOption options = ytDlpOptionBuilder.url(url).listFormats().build();
    return YtDlp.execute(options);
  }

  public static ProcessResult getPlayList(String url) throws IOException, InterruptedException {
    YtDlpOptionBuilder ytDlpOptionBuilder = new YtDlpOptionBuilder();
    YtDlpOptionBuilder builder = ytDlpOptionBuilder.url(url).flatPlaylist().dumpSingleJson();
    return YtDlp.execute(builder);
  }

  /**
   * /usr/bin/yt-dlp_linux --print "%(upload_date>%Y-%m-%d)s" https://www.youtube.com/watch?v=Mfxoo_E969Y
   * @param videoId
   * @param quiet
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public static ProcessResult getUploadDate(String videoId, boolean quiet) throws IOException, InterruptedException {
    YtDlpOptionBuilder builder = builder();
    String url = String.format(URL_TEMPLATE, videoId);
    //String template = "%(upload_date>%Y-%m-%d)s";
    builder.print("upload_date").url(url);
    return YtDlp.execute(builder);
  }

}
