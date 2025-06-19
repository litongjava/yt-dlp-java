package com.litongjava.yt.utils;

import java.io.File;

import org.junit.Test;

public class YtDlpUtilsTest {

  @Test
  public void downlodSubtitle() {
    String subTitle = YtDlpUtils.downlodSubtitle("AMCUqgu_cTM", true);
    System.out.println(subTitle);
  }

  @Test
  public void downloadMp3() {
    File file = YtDlpUtils.downloadMp3("AMCUqgu_cTM", true);
    System.out.println(file.getName());
    System.out.println(file.exists());
  }
  
  @Test
  public void downloadMp4() {
    File file = YtDlpUtils.downloadMp4("AMCUqgu_cTM", true);
    System.out.println(file.getName());
    System.out.println(file.exists());
  }

}
