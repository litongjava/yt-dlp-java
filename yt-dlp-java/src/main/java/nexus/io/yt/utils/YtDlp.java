package nexus.io.yt.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nexus.io.tio.utils.commandline.ProcessResult;
import nexus.io.tio.utils.commandline.ProcessUtils;
import nexus.io.tio.utils.environment.EnvUtils;
import nexus.io.tio.utils.snowflake.SnowflakeIdUtils;
import nexus.io.yt.builder.YtDlpOption;
import nexus.io.yt.builder.YtDlpOptionBuilder;
import nexus.io.yt.consts.YtDlpConst;

public class YtDlp {
  public static final Logger log = LoggerFactory.getLogger(YtDlp.class);

  // Download URLs for different operating systems
  public static final String windows_download_url = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe";
  public static final String linux_download_url = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_linux";
  public static final String mac_os_download_url = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_macos";

  public static final String TASK_LOG_FOLDER = "yt-dlp-task-log";

  private static final Set<String> SECRET_OPTIONS = Set.of("-p", "--password", "--video-password",
      "--ap-password", "--client-certificate-password", "--twofactor", "-2");

  /**
   * Runs the official updater on the configured executable (installs it first if missing).
   * The caller must check the exit code; package-manager installations may reject self-update.
   * Run during a maintenance window, without concurrent downloads using this executable.
   */
  public static synchronized ProcessResult updateYtDlp() throws IOException, InterruptedException {
    String executable = Paths.get(downloadYtDlp()).toAbsolutePath().normalize().toString();
    return runUpdate(List.of(executable));
  }

  static ProcessResult runUpdate(List<String> executableCommand) throws IOException, InterruptedException {
    List<String> command = new ArrayList<>(executableCommand);
    command.add("--ignore-config");
    command.add("--update");
    long id = SnowflakeIdUtils.id();
    File logDir = Files.createDirectories(Paths.get(TASK_LOG_FOLDER, Long.toString(id))).toFile();
    ProcessResult result = ProcessUtils.execute(logDir, new ProcessBuilder(command));
    result.setTaskId(id);
    return result;
  }

  /**
   * Downloads the yt-dlp executable if it does not already exist.
   *
   * @return absolute path of the executable. A relative name would be resolved
   *         against PATH by {@link ProcessBuilder}, not the working directory.
   */
  public static synchronized String downloadYtDlp() throws MalformedURLException, ProtocolException, IOException {
    Path target = Paths.get(getYtDlpName()).toAbsolutePath().normalize();
    if (Files.exists(target)) {
      return target.toString();
    }

    String downloadUrl = getYtDlpUrl();
    HttpURLConnection connection = null;
    Path tmp = target.resolveSibling(target.getFileName() + ".download");
    try {
      connection = URLUtils.getConnection(downloadUrl);
      connection.setRequestMethod("GET");
      connection.setInstanceFollowRedirects(true);

      int responseCode = connection.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK) {
        throw new IOException("yt-dlp download failed, HTTP " + responseCode);
      }
      // Write to a temporary file first so an interrupted download is never
      // mistaken for an installed executable on the next call.
      try (InputStream inputStream = connection.getInputStream()) {
        Files.copy(inputStream, tmp, StandardCopyOption.REPLACE_EXISTING);
      }
      if (!tmp.toFile().setExecutable(true)) {
        log.error("Failed to set executable permission for {}", tmp);
      }
      try {
        Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE);
      } catch (AtomicMoveNotSupportedException e) {
        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
      }
      log.info("yt-dlp download completed: {}", target);
    } finally {
      Files.deleteIfExists(tmp);
      if (connection != null) {
        connection.disconnect();
      }
    }
    return target.toString();
  }

  /**
   * Returns the file where yt-dlp is or should be downloaded.
   *
   * @return File object representing the yt-dlp executable.
   */
  public static String getYtDlpName() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.startsWith("windows")) {
      String str = EnvUtils.getStr(YtDlpConst.YT_DLP_BIN_WIN);
      if (str != null && Files.exists(Paths.get(str))) {
        return str;
      }
      return "yt-dlp.exe";
    } else if (os.startsWith("linux")) {

      String str = EnvUtils.getStr(YtDlpConst.YT_DLP_BIN_LINUX);
      if (str != null && Files.exists(Paths.get(str))) {
        return str;
      }
      return "yt-dlp_linux";
    }
    String str = EnvUtils.getStr(YtDlpConst.YT_DLP_BIN_MAC);
    if (str != null && Files.exists(Paths.get(str))) {
      return str;
    }
    return "yt-dlp_macos";
  }

  /**
   * Returns the download URL for the current operating system.
   *
   * @return A string representing the download URL.
   */
  public static String getYtDlpUrl() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.startsWith("windows")) {
      return windows_download_url;
    } else if (os.startsWith("linux")) {
      return linux_download_url;
    }
    return mac_os_download_url;
  }

  /**
   * Runs yt-dlp with the given options. Process output is written to
   * {@code logDir/stdout.log} and {@code logDir/stderr.log}.
   */
  public static ProcessResult execute(String logDir, YtDlpOption option) throws IOException, InterruptedException {
    return run(new File(logDir), option.toArgs());
  }

  public static ProcessResult execute(YtDlpOptionBuilder builder) throws IOException, InterruptedException {
    return execute(builder.build());
  }

  public static ProcessResult execute(YtDlpOption option) throws IOException, InterruptedException {
    return run(null, option.toArgs());
  }

  /**
   * Legacy entry point: arguments separated by two spaces.
   *
   * @deprecated use {@link #execute(String, YtDlpOption)}; values containing two
   *             consecutive spaces are split.
   */
  @Deprecated
  public static ProcessResult execute(String logDir, String options) throws IOException, InterruptedException {
    return run(new File(logDir), splitLegacy(options));
  }

  /**
   * @deprecated use {@link #execute(YtDlpOption)}; values containing two
   *             consecutive spaces are split.
   */
  @Deprecated
  public static ProcessResult execute(String options) throws IOException, InterruptedException {
    return run(null, splitLegacy(options));
  }

  private static List<String> splitLegacy(String options) {
    List<String> list = new ArrayList<>();
    for (String s : options.split("  ")) {
      if (!s.isEmpty()) {
        list.add(s);
      }
    }
    return list;
  }

  /**
   * @param logDir directory for stdout/stderr logs; {@code null} uses
   *               {@code yt-dlp-task-log/<taskId>}.
   */
  public static ProcessResult run(File logDir, List<String> args) throws IOException, InterruptedException {
    String ytDlpPath = YtDlp.downloadYtDlp();

    long id = SnowflakeIdUtils.id();
    List<String> command = new ArrayList<>(args.size() + 1);
    command.add(ytDlpPath);
    command.addAll(args);
    if (log.isInfoEnabled()) {
      log.info("Task id: {} Executing command: {}", id, String.join(" ", maskSecrets(command)));
    }

    if (logDir == null) {
      logDir = new File(TASK_LOG_FOLDER, Long.toString(id));
    }
    ProcessResult result = ProcessUtils.execute(logDir, command);
    result.setTaskId(id);
    return result;
  }

  /** Hides credentials (proxy user info, passwords) before logging. */
  static List<String> maskSecrets(List<String> command) {
    List<String> masked = new ArrayList<>(command.size());
    for (int i = 0; i < command.size(); i++) {
      String arg = command.get(i);
      String prev = i > 0 ? command.get(i - 1) : null;
      if (prev != null && SECRET_OPTIONS.contains(prev)) {
        masked.add("****");
      } else if (prev != null && "--proxy".equals(prev)) {
        masked.add(arg.replaceAll("(?<=://)[^/]*@", "****@"));
      } else {
        masked.add(arg);
      }
    }
    return masked;
  }

}
