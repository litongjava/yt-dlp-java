package com.litongjava.yt;

import java.io.IOException;

import org.junit.Test;

import com.litongjava.tio.utils.commandline.ProcessResult;
import com.litongjava.yt.builder.YtDlpOption;
import com.litongjava.yt.builder.YtDlpOptionBuilder;

public class YtDlpTest {

  @Test
  public void downloadMp3WithProxy() {
    String url = "https://www.youtube.com/watch?v=ZwHILfCE0yI";
    YtDlpOption option = new YtDlpOptionBuilder().proxy("http://127.0.0.1:10808").audio().audioFormat("mp3")
        .output("%(title)s.%(ext)s").url(url).build();
    
    try {
      ProcessResult downloadMp3 = YtDlp.execute("ytdlp_logs", option);
      String file = downloadMp3.getPath();
      System.out.println(file);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

}
