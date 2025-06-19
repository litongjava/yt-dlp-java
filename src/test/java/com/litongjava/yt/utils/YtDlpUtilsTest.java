package com.litongjava.yt.utils;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.litongjava.tio.utils.commandline.ProcessResult;
import com.litongjava.yt.YtDlp;

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
      File file = downloadMp3.getFile();
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
      if (result.getFile() != null) {
        File file = result.getFile();
        System.out.println(file.getName());
        System.out.println(file.exists());
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
      ProcessResult result = YtDlp.getAvailableFormats("https://www.youtube.com/watch?v=PnHMAVXpKg8");
      System.out.println(result.getStdOut());
      System.out.println(result.getStdErr());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

}
