package com.litongjava.youtube.handler;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;

import com.litongjava.youtube.utils.MediaFileResponder;

import nexus.io.tio.boot.http.TioRequestContext;
import nexus.io.tio.http.common.HttpRequest;
import nexus.io.tio.http.common.HttpResponse;
import nexus.io.tio.http.server.handler.HttpRequestHandler;
import nexus.io.tio.http.server.util.CORSUtils;
import nexus.io.tio.utils.hutool.FilenameUtils;
import nexus.io.yt.utils.YtDlpUtils;

/**
 * Serves finished media under the download folder only. Rejects path traversal,
 * symlinks escaping the folder, hidden entries and non-media files (logs etc.).
 */
public class DownloadFileHandler implements HttpRequestHandler {
  private static final String PREFIX = "/" + YtDlpUtils.DOWNLOAD_FOLDER + "/";
  private static final Set<String> ALLOWED = Set.of("mp3", "mp4", "m4a", "webm", "opus", "vtt", "srt");

  @Override
  public HttpResponse handle(HttpRequest request) throws Exception {
    HttpResponse response = TioRequestContext.getResponse();
    CORSUtils.enableCORS(response);

    File file = resolve(request.getRequestLine().getPath());
    if (file == null) {
      response.setStatus(404);
      return response;
    }
    return MediaFileResponder.serve(request, response, file);
  }

  static File resolve(String requestPath) throws IOException {
    if (requestPath == null || !requestPath.startsWith(PREFIX)) {
      return null;
    }
    String relative;
    try {
      // The router passes the raw path; decode so non-ASCII names work. Validation
      // below runs on the decoded value, so "%2e%2e" cannot bypass it.
      relative = URLDecoder.decode(requestPath.substring(PREFIX.length()).replace("+", "%2B"), StandardCharsets.UTF_8);
    } catch (IllegalArgumentException e) {
      return null;
    }
    if (relative.indexOf('\0') >= 0) {
      return null;
    }
    for (String segment : relative.split("/")) {
      if (segment.isEmpty() || segment.startsWith(".") || segment.contains("\\")) {
        return null;
      }
    }
    String suffix = FilenameUtils.getSuffix(relative);
    if (suffix == null || !ALLOWED.contains(suffix.toLowerCase(Locale.ROOT))) {
      return null;
    }
    Path root = Paths.get(YtDlpUtils.DOWNLOAD_FOLDER).toAbsolutePath().normalize();
    Path target;
    try {
      target = root.resolve(relative).normalize();
    } catch (InvalidPathException e) {
      return null;
    }
    if (!target.startsWith(root) || !Files.isRegularFile(target)) {
      return null;
    }
    if (!target.toRealPath().startsWith(root.toRealPath())) {
      return null;
    }
    return target.toFile();
  }
}
