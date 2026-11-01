package com.litongjava.youtube.config;

import com.litongjava.youtube.handler.YoutubeDownloadHandler;

import nexus.io.context.BootConfiguration;
import nexus.io.tio.boot.http.handler.common.HttpFileDataHandler;
import nexus.io.tio.boot.server.TioBootServer;
import nexus.io.tio.http.server.router.HttpRequestRouter;

public class WebHelloConfig implements BootConfiguration {

  public void config() {

    TioBootServer server = TioBootServer.me();
    HttpRequestRouter r = server.getRequestRouter();
    if (r != null) {
      YoutubeDownloadHandler downloadHandler = new YoutubeDownloadHandler();
      r.add("/xyz", downloadHandler);

      HttpFileDataHandler dataHandler = new HttpFileDataHandler(false);
      r.add("/download/**", dataHandler);
    }
  }
}