package com.litongjava.youtube;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.litongjava.youtube.config.WebHelloConfig;

import nexus.io.tio.boot.TioApplication;
import nexus.io.tio.boot.server.TioBootServer;
import nexus.io.tio.utils.commandline.ProcessResult;
import nexus.io.yt.utils.YtDlp;

public class YoutubeApp {
  public static void main(String[] args) throws Exception {
    if (java.util.Arrays.asList(args).contains("--update-yt-dlp")) {
      ProcessResult result = YtDlp.updateYtDlp();
      if (result.getStdOut() != null) {
        System.out.print(result.getStdOut());
      }
      if (result.getStdErr() != null) {
        System.err.print(result.getStdErr());
      }
      System.exit(result.getExitCode() == 0 ? 0 : 1);
      return;
    }
    long start = System.currentTimeMillis();
    // 1. 虚拟线程工厂（用于 work 线程）
    ThreadFactory workTf = Thread.ofVirtual().name("t-io-v-", 1).factory();

    // 2. 设置 readWorkers 使用的线程工厂
    TioBootServer server = TioBootServer.me();
    server.setWorkThreadFactory(workTf);
    server.setWorkThreadNum(Runtime.getRuntime().availableProcessors() * 8);

    // 3. 创建业务虚拟线程 Executor（每任务一个虚拟线程）
    ThreadFactory bizTf = Thread.ofVirtual().name("t-biz-v-", 0).factory();

    ExecutorService bizExecutor = Executors.newThreadPerTaskExecutor(bizTf);

    server.setBizExecutor(bizExecutor);
    
    WebHelloConfig webHelloConfig = new WebHelloConfig();
    TioApplication.run(YoutubeApp.class, webHelloConfig, args);
    long end = System.currentTimeMillis();
    System.out.println((end - start) + "ms");
  }
}
