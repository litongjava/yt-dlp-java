package com.litongjava.youtube.handler;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.litongjava.youtube.utils.MediaFileResponder;

import nexus.io.tio.boot.http.TioRequestContext;
import nexus.io.tio.http.common.HttpRequest;
import nexus.io.tio.http.common.HttpResponse;
import nexus.io.tio.http.server.handler.HttpRequestHandler;
import nexus.io.tio.http.server.util.CORSUtils;
import nexus.io.tio.utils.commandline.ProcessResult;
import nexus.io.yt.utils.YtDlpUtils;

public class YoutubeDownloadHandler implements HttpRequestHandler {
  private static final Logger log = LoggerFactory.getLogger(YoutubeDownloadHandler.class);
  private static final Pattern VIDEO_ID = Pattern.compile("[A-Za-z0-9_-]{11}");

  private final boolean video;

  public YoutubeDownloadHandler() {
    this(false);
  }

  public YoutubeDownloadHandler(boolean video) {
    this.video = video;
  }

  @Override
  public HttpResponse handle(HttpRequest httpRequest) throws Exception {
    HttpResponse response = TioRequestContext.getResponse();
    CORSUtils.enableCORS(response);

    String videoId = httpRequest.getParam("id");
    if (videoId == null || !VIDEO_ID.matcher(videoId).matches()) {
      return error(response, 400, "id must be an 11-character YouTube video id", null);
    }

    ProcessResult result;
    try {
      result = video ? YtDlpUtils.downloadMp4(videoId, false) : YtDlpUtils.downloadMp3(videoId, false);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw e;
    } catch (Exception e) {
      log.error("yt-dlp failed to start for {}", videoId, e);
      return error(response, 502, "yt-dlp failed to start: " + e.getMessage(), null);
    }

    String path = result.getPath();
    if (path == null || path.isBlank()) {
      log.warn("No output for {} (task {}, exit {})", videoId, result.getTaskId(), result.getExitCode());
      return error(response, 502, "download produced no file", result);
    }
    File file = new File(path);
    if (!file.isFile()) {
      return error(response, 404, "file not found", null);
    }
    return MediaFileResponder.serve(httpRequest, response, file);
  }

  private static HttpResponse error(HttpResponse response, int status, String message, ProcessResult result) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("ok", false);
    body.put("error", message);
    if (result != null) {
      body.put("taskId", result.getTaskId());
      body.put("exitCode", result.getExitCode());
      body.put("detail", errorLines(result.getStdErr()));
    }
    response.setJson(body);
    response.setStatus(status);
    return response;
  }

  /** Only yt-dlp ERROR lines, so stderr noise and local details are not echoed to clients. */
  private static String errorLines(String stderr) {
    if (stderr == null) {
      return null;
    }
    return stderr.lines().filter(l -> l.startsWith("ERROR")).limit(5).collect(Collectors.joining("\n"));
  }
}
