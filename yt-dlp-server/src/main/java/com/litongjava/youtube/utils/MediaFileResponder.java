package com.litongjava.youtube.utils;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import nexus.io.tio.boot.utils.HttpFileDataUtils;
import nexus.io.tio.http.common.HttpRequest;
import nexus.io.tio.http.common.HttpResponse;
import nexus.io.tio.utils.http.ContentTypeUtils;
import nexus.io.tio.utils.hutool.FilenameUtils;

/** Sends a local file with cache negotiation, Range support and a download file name. */
public final class MediaFileResponder {

  private MediaFileResponder() {
  }

  public static HttpResponse serve(HttpRequest request, HttpResponse response, File file) {
    String name = file.getName();
    long fileLength = file.length();
    long lastModified = file.lastModified();
    String etag = HttpFileDataUtils.generateETag(file, lastModified, fileLength);

    String suffix = FilenameUtils.getSuffix(name);
    String contentType = ContentTypeUtils.getContentType(suffix);
    HttpFileDataUtils.setCacheHeaders(response, lastModified, etag, contentType, suffix);
    response.setContentDisposition(contentDisposition(name));

    if (HttpFileDataUtils.isClientCacheValid(request, lastModified, etag)) {
      response.setStatus(304);
      return response;
    }
    String range = request.getHeader("range");
    if (range != null && range.startsWith("bytes=")) {
      return HttpFileDataUtils.handleRangeRequest(response, file, range, fileLength, contentType);
    }
    return HttpFileDataUtils.handleFullFileRequest(response, file, fileLength, contentType);
  }

  /**
   * RFC 6266 / RFC 5987: ASCII fallback plus UTF-8 {@code filename*}, so that
   * non-ASCII titles survive and header-breaking characters are removed.
   */
  static String contentDisposition(String name) {
    String fallback = name.replaceAll("[^\\x20-\\x7E]|[\"\\\\]", "_");
    String encoded = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
    return "attachment; filename=\"" + fallback + "\"; filename*=UTF-8''" + encoded;
  }
}
