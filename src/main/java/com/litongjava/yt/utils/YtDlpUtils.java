package com.litongjava.yt.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.litongjava.yt.YtDlp;
import com.litongjava.yt.builder.YtDlpOption;
import com.litongjava.yt.builder.YtDlpOptionBuilder;

public class YtDlpUtils {

  public static final String URL_TEMPLATE = "https://www.youtube.com/watch?v=%s";
  public static final String DOWNLOAD_FOLDER = "download";

  public static File downloadMp4(String videoId, boolean quiet) {
    String url = String.format(URL_TEMPLATE, videoId);
    String folder = DOWNLOAD_FOLDER + "/" + videoId + "/mp4";
    String output = folder + "/%(title)s.%(ext)s";

    YtDlpOptionBuilder builder = new YtDlpOptionBuilder().url(url)
        // 优先 mp4 视频+音频，否则回退到 mp4
        .format("bestvideo[ext=mp4]+bestaudio[ext=m4a]/mp4").output(output);

    if (quiet) {
      builder.quiet();
    }

    YtDlpOption options = builder.build();
    YtDlp.execute(options);

    File[] files = new File(folder).listFiles();
    return (files != null && files.length > 0) ? files[0] : null;
  }

  public static File downloadMp3(String videoId, boolean quiet) {
    String url = String.format(URL_TEMPLATE, videoId);
    String folder = DOWNLOAD_FOLDER + "/" + videoId + "/mp3";
    String output = folder + "/%(title)s.%(ext)s";

    YtDlpOptionBuilder builder = new YtDlpOptionBuilder().url(url).audio().audioFormat("mp3").output(output);
    if (quiet) {
      builder.quiet();
    }
    YtDlpOption options = builder.build();

    YtDlp.execute(options);
    File[] listFiles = new File(folder).listFiles();
    if (listFiles != null && listFiles.length > 0) {
      return listFiles[0];
    }
    return null;
  }

  public static String downlodSubtitle(String videoId, boolean quiet) {
    String url = String.format(URL_TEMPLATE, videoId);
    String folder = DOWNLOAD_FOLDER + "/" + videoId + "/sub";
    String output = folder + "/%(title)s.%(ext)s";
    YtDlpOptionBuilder builder = new YtDlpOptionBuilder().url(url).output(output).writeSub().skipDownload();
    if (quiet) {
      builder.quiet();
    }
    YtDlpOption options = builder.build();
    return execute(videoId, folder, options);
  }

  public static String downloadAutoSubtitle(String videoId, boolean quiet) {

    String url = "https://www.youtube.com/watch?v=%s";
    url = String.format(url, videoId);
    String folder = DOWNLOAD_FOLDER + "/" + videoId + "/vtt";
    String output = folder + "/%(title)s.%(ext)s";

    YtDlpOptionBuilder builder = new YtDlpOptionBuilder().url(url).output(output).writeAutoSub().skipDownload();
    if (quiet) {
      builder.quiet();
    }
    YtDlpOption options = builder.build();
    return execute(videoId, folder, options);
  }

  public static String downloadAutoSubtitle(String videoId, String type, boolean quiet) {
    String url = "https://www.youtube.com/watch?v=%s";
    url = String.format(url, videoId);
    String folder = DOWNLOAD_FOLDER + "/" + videoId + "/" + type;
    String output = folder + "/%(title)s.%(ext)s";

    YtDlpOptionBuilder builder = new YtDlpOptionBuilder().url(url).output(output).writeSub().writeAutoSub().convertSubs(type).skipDownload();
    if (quiet) {
      builder.quiet();
    }
    YtDlpOption options = builder.build();
    return execute(videoId, folder, options);
  }

  private static String execute(String id, String folder, YtDlpOption options) {
    YtDlp.execute(options);
    File[] listFiles = new File(folder).listFiles();
    if (listFiles != null && listFiles.length > 0) {
      File subtitleFile = listFiles[0];
      try {
        List<String> lines = Files.readAllLines(subtitleFile.toPath(), StandardCharsets.UTF_8);
        subtitleFile.delete();
        Files.delete(Paths.get(DOWNLOAD_FOLDER, id + ""));
        return String.join(System.lineSeparator(), lines);
      } catch (IOException e) {
        throw new RuntimeException("Failed to read file", e);
      }
    }
    return null;
  }
}
