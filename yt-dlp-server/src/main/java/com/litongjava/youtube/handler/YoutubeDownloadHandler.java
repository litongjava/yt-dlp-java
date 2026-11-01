package com.litongjava.youtube.handler;

import java.io.File;

import nexus.io.tio.boot.http.TioRequestContext;
import nexus.io.tio.boot.utils.HttpFileDataUtils;
import nexus.io.tio.http.common.HttpRequest;
import nexus.io.tio.http.common.HttpResponse;
import nexus.io.tio.http.server.handler.HttpRequestHandler;
import nexus.io.tio.http.server.util.CORSUtils;
import nexus.io.tio.utils.commandline.ProcessResult;
import nexus.io.tio.utils.http.ContentTypeUtils;
import nexus.io.tio.utils.hutool.FilenameUtils;
import nexus.io.yt.utils.YtDlpUtils;

public class YoutubeDownloadHandler implements HttpRequestHandler {

  @Override
  public HttpResponse handle(HttpRequest httpRequest) throws Exception {
    HttpResponse response = TioRequestContext.getResponse();
    CORSUtils.enableCORS(response);

    String videoId = httpRequest.getParam("id");
    ProcessResult result = YtDlpUtils.downloadMp3(videoId, false);

    String path = result.getPath();
    File file = new File(path);
    if (file != null) {
      if (!file.exists()) {
        response.setStatus(404);
        return response;
      }

      String name = file.getName();
      // 生成 ETag
      long fileLength = file.length();
      long lastModified = file.lastModified();

      String etag = HttpFileDataUtils.generateETag(file, lastModified, fileLength);

      // 设置缓存相关头部
      String suffix = FilenameUtils.getSuffix(name);
      String contentType = ContentTypeUtils.getContentType(suffix);
      HttpFileDataUtils.setCacheHeaders(response, lastModified, etag, contentType, suffix);

      // 检查客户端缓存
      if (HttpFileDataUtils.isClientCacheValid(httpRequest, lastModified, etag)) {
        response.setStatus(304); // Not Modified
        return response;
      }
      // 检查是否存在 Range 头信息
      String range = httpRequest.getHeader("range");
      if (range != null && range.startsWith("bytes=")) {
        return HttpFileDataUtils.handleRangeRequest(response, file, range, fileLength, contentType);
      } else {
        return HttpFileDataUtils.handleFullFileRequest(response, file, fileLength, contentType);
      }

    } else {
      response.setJson(result);
    }
    return response;

  }
}