package com.litongjava.yt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

import com.litongjava.tio.utils.commandline.ProcessResult;
import com.litongjava.tio.utils.commandline.ProcessUtils;
import com.litongjava.tio.utils.snowflake.SnowflakeIdUtils;
import com.litongjava.yt.builder.YtDlpOption;
import com.litongjava.yt.builder.YtDlpOptionBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class YtDlp {

  // Download URLs for different operating systems
  public static final String windows_download_url = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe";
  public static final String linux_download_url = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_linux";
  public static final String mac_os_download_url = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp";

  /**
   * Downloads the yt-dlp executable if it does not already exist.
   */
  public static void downloadYtDlp() {
    File file = ytDlpFile();
    if (file.exists()) {
      // If the file already exists, no need to download it
      return;
    }

    String downloadUrl = ytDlpUrl();
    HttpURLConnection connection = null;

    try {
      URL url = new URL(downloadUrl);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(15000);
      connection.setReadTimeout(15000);

      // Start the connection
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        try (InputStream inputStream = connection.getInputStream(); OutputStream outputStream = Files.newOutputStream(file.toPath())) {
          byte[] buffer = new byte[4096];
          int bytesRead;
          while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
          }
          System.out.println("Download completed: " + file.getAbsolutePath());
        }

        // add permissions
        if (!file.setExecutable(true)) {
          System.err.println("Failed to set executable permission for " + file.getAbsolutePath());
        }
      } else {
        System.err.println("Download failed, server returned response code: " + responseCode);
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  /**
   * Returns the file where yt-dlp is or should be downloaded.
   *
   * @return File object representing the yt-dlp executable.
   */
  public static File ytDlpFile() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.startsWith("windows")) {
      return new File("yt-dlp.exe");
    } else if (os.startsWith("linux")) {
      return new File("yt-dlp_linux");
    }
    return new File("yt-dlp");
  }

  /**
   * Returns the download URL for the current operating system.
   *
   * @return A string representing the download URL.
   */
  public static String ytDlpUrl() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.startsWith("windows")) {
      return windows_download_url;
    } else if (os.startsWith("linux")) {
      return linux_download_url;
    }
    return mac_os_download_url;
  }

  /**
   * Downloads the audio for the specified URL.
   * Options must be built using the audio() method of YtDlpOptionBuilder.
   *
   * @param options The options for downloading and converting audio.
   * @return The output generated by yt-dlp.
   * @throws InterruptedException 
   * @throws IOException 
   */
  public static ProcessResult execute(String logDir, YtDlpOption option) throws IOException, InterruptedException {
    String options = option.getStringBuilder() + option.getUrl();
    return execute(logDir, options);
  }

  /**
   * Internal execute method that downloads yt-dlp (if needed) and runs the command.
   *
   * @param option The command-line options.
   * @return The output from the command execution.
   * @throws InterruptedException 
   * @throws IOException 
   */
  private static ProcessResult execute(String logDir, String options) throws IOException, InterruptedException {
    YtDlp.downloadYtDlp();

    long id = SnowflakeIdUtils.id();
    String commandString = "./" + ytDlpFile().getPath() + "  " + options;
    log.info("Task id: " + id + " Executing command: " + commandString);
    String[] commandArray = commandString.split("  ");

    ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
    ProcessResult result = ProcessUtils.execute(new File(logDir), processBuilder);
    result.setTaskId(id);
    return result;
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
    long id = SnowflakeIdUtils.id();
    return YtDlp.execute(id + "", options);
  }
}
