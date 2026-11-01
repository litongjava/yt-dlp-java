package com.litongjava.yt.builder;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import nexus.io.yt.builder.YtDlpOption;
import nexus.io.yt.builder.YtDlpOptionBuilder;

public class YtDlpOptionBuilderTest {

  @Test
  public void shouldBuildProxyOption() {
    String url = "https://www.youtube.com/watch?v=ZwHILfCE0yI";
    YtDlpOption option = new YtDlpOptionBuilder().proxy("http://127.0.0.1:10808").audio().audioFormat("mp3")
        .output("%(title)s.%(ext)s").url(url).build();

    assertEquals(List.of("--proxy", "http://127.0.0.1:10808", "-x", "--audio-format", "mp3", "--output",
        "%(title)s.%(ext)s", url), option.toArgs());
  }

  @Test
  public void jsRuntimeOptionHasNoTrailingSpace() {
    List<String> args = new YtDlpOptionBuilder().jsRuntimesDeno("/opt/deno").build().toArgs();
    assertEquals(List.of("--js-runtimes", "deno:/opt/deno"), args);
  }

  @Test
  public void valuesWithSpacesStayOneArgument() {
    List<String> args = new YtDlpOptionBuilder().cookies("/my  dir/cookies.txt").build().toArgs();
    assertEquals(List.of("--cookies", "/my  dir/cookies.txt"), args);
  }
}
