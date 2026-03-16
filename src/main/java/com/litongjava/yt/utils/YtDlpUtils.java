package com.litongjava.yt.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.litongjava.tio.utils.commandline.ProcessResult;
import com.litongjava.yt.builder.YtDlpOption;
import com.litongjava.yt.builder.YtDlpOptionBuilder;

public class YtDlpUtils {

  public static final String URL_TEMPLATE = "https://www.youtube.com/watch?v=%s";
  public static final String DOWNLOAD_FOLDER = "download";

  public static ProcessResult downloadMp4(String videoId, boolean quiet) throws IOException, InterruptedException {
    return downloadMp4(videoId, quiet, null);
  }

  public static ProcessResult downloadMp4(String videoId, boolean quiet, String proxy)
      throws IOException, InterruptedException {
    String folderName = "mp4";
    String format = "bestvideo[ext=mp4]+bestaudio[ext=m4a]/mp4";
    String suffix = ".mp4";
    return downloadVideo(videoId, quiet, proxy, format, folderName, suffix);
  }

  public static ProcessResult downloadMp3(String videoId, boolean quiet) throws IOException, InterruptedException {
    return downloadMp3(videoId, quiet, null);
  }

  public static ProcessResult downloadMp3(String videoId, boolean quiet, String proxy)
      throws IOException, InterruptedException {
    String folderName = "mp3";
    String format = "mp3";
    String suffix = ".mp3";
    return downloadAudio(videoId, quiet, proxy, format, folderName, suffix);
  }

  private static ProcessResult downloadVideo(String videoId, boolean quiet, String proxy, String format,
      String subFolderName, String suffix) throws IOException, InterruptedException {
    String folder = DOWNLOAD_FOLDER + File.separator + videoId + File.separator + subFolderName;

    File outDir = new File(folder);
    File[] listFiles = outDir.listFiles();

    if (listFiles != null && listFiles.length > 0) {
      for (File file : listFiles) {
        if (file.getName().endsWith(suffix)) {
          return ProcessResult.fromFile(file, true);
        }
      }

    }

    String url = String.format(URL_TEMPLATE, videoId);
    String output = folder + "/%(title)s.%(ext)s";

    YtDlpOptionBuilder builder = new YtDlpOptionBuilder().url(url).audio().format(format).output(output);
    applyProxy(builder, proxy);
    if (quiet) {
      builder.quiet();
    }
    YtDlpOption options = builder.build();

    ProcessResult result = YtDlp.execute(folder, options);
    listFiles = outDir.listFiles();
    if (listFiles != null && listFiles.length > 0) {
      for (File file : listFiles) {
        if (file.getName().endsWith(suffix)) {
          return ProcessResult.fromFile(file, true);
        }
      }
    }
    return result;
  }

  private static ProcessResult downloadAudio(String videoId, boolean quiet, String proxy, String format,
      String subFolderName, String suffix) throws IOException, InterruptedException {
    String folder = DOWNLOAD_FOLDER + File.separator + videoId + File.separator + subFolderName;

    File outDir = new File(folder);
    File[] listFiles = outDir.listFiles();

    if (listFiles != null && listFiles.length > 0) {
      for (File file : listFiles) {
        if (file.getName().endsWith(suffix)) {
          return ProcessResult.fromFile(file, true);
        }
      }
    }

    String url = String.format(URL_TEMPLATE, videoId);
    String output = folder + "/%(title)s.%(ext)s";

    YtDlpOptionBuilder builder = new YtDlpOptionBuilder().url(url).audio().audioFormat(format).output(output);
    applyProxy(builder, proxy);
    if (quiet) {
      builder.quiet();
    }
    YtDlpOption options = builder.build();

    ProcessResult result = YtDlp.execute(folder, options);
    // Long taskId = result.getTaskId();
    listFiles = outDir.listFiles();
    if (listFiles != null && listFiles.length > 0) {
      for (File file : listFiles) {
        if (file.getName().endsWith(suffix)) {
          // file.renameTo(new File(folder, taskId + suffix));
          return ProcessResult.fromFile(file, true);
        }
      }
    }
    return result;
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
    applyProxy(builder, proxy);
    if (quiet) {
      builder.quiet();
    }
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

  private static void applyProxy(YtDlpOptionBuilder builder, String proxy) {
    if (proxy != null) {
      String value = proxy.trim();
      if (!value.isEmpty()) {
        builder.proxy(value);
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
}
