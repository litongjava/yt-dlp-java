package com.litongjava.yt.utils;

import java.io.IOException;

import org.junit.Test;

import com.litongjava.tio.utils.commandline.ProcessResult;
import com.litongjava.tio.utils.environment.EnvUtils;

public class YtDlpUtilsTest {

  @Test
  public void downlodSubtitle() {
    try {
      ProcessResult result = YtDlpUtils.downlodSubtitle("AMCUqgu_cTM", true);
      System.out.println(result.getOutput());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void downloadMp3() {
    try {
      ProcessResult downloadMp3 = YtDlpUtils.downloadMp3("AMCUqgu_cTM", true);
      String file = downloadMp3.getPath();
      System.out.println(file);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void downloadMp4() {
    ProcessResult result;
    try {
      result = YtDlpUtils.downloadMp4("AMCUqgu_cTM", true);
      if (result.getPath() != null) {
        String file = result.getPath();
        System.out.println(file);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void listFormat() {
    try {
      ProcessResult result = YtDlpUtils.getAvailableFormats("https://www.youtube.com/watch?v=PnHMAVXpKg8");
      System.out.println(result.getStdOut());
      System.out.println(result.getStdErr());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void getPlayList() {
    EnvUtils.load();
    try {
      String url = "https://www.youtube.com/playlist?list=xxxx";
      ProcessResult result = YtDlpUtils.getPlayList(url);
      System.out.println("out:" + result.getStdOut());
      System.out.println("err" + result.getStdErr());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
