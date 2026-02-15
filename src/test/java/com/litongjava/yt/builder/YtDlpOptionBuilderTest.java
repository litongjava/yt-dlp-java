package com.litongjava.yt.builder;

import org.junit.Test;

public class YtDlpOptionBuilderTest {

  @Test
  public void shouldBuildProxyOption() {
    String url = "https://www.youtube.com/watch?v=ZwHILfCE0yI";
    YtDlpOption option = new YtDlpOptionBuilder().proxy("http://127.0.0.1:10808").audio().audioFormat("mp3")
        .output("%(title)s.%(ext)s").url(url).build();

    String command = option.toCommand();

    System.out.println(command);
  }
}
