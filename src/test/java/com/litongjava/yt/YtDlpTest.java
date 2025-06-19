package com.litongjava.yt;

import com.litongjava.yt.broker.LongProcessBroker;
import com.litongjava.yt.builder.YtDlpOption;
import com.litongjava.yt.builder.YtDlpOptionBuilder;
import com.litongjava.yt.utils.SnowflakeId;

public class YtDlpTest {

  private void downloadMp3() {
    // Example: Download the video as audio and convert it to mp3 format
    long id = SnowflakeId.id();
    String url = "https://www.youtube.com/watch?v=AMCUqgu_cTM";
    YtDlpOption options = new YtDlpOptionBuilder().url(url).audio() // Enable audio extraction
        .audioFormat("mp3") // Set the output audio format to mp3
        .output("downloads/" + id + "/%(title)s.%(ext)s").build();

    // Call the download audio method
    String result = YtDlp.execute(options);
    System.out.println("reuslt:");
    System.out.println(result);
  }

  public static void main(String[] args) {
    //downlodSubtitle();
    //downloadMp3();
    //listFormat();
    // test1();
    // test2();
  }

  private static void downlodSubtitle() {
    long id = SnowflakeId.id();
    String url = "https://www.youtube.com/watch?v=AMCUqgu_cTM";
    YtDlpOption options = new YtDlpOptionBuilder().url(url)
        //
        .output("downloads/" + id + "/%(title)s.%(ext)s")
        //
        .writeSub() // or writeAutoSub()
        //.subLang("en") //There are no subtitles for the requested languages
        .skipDownload().build();

    // Call the download audio method
    String result = YtDlp.execute(options);
    System.out.println("reuslt:");
    System.out.println(result);
  }

  private static void listFormat() {
    String format = YtDlp.getAvailableFormats("https://www.youtube.com/watch?v=PnHMAVXpKg8");
    System.out.println("format: " + format);
  }

  private static void test2() {
    LongProcessBroker longProcessBroker = new LongProcessBroker("yt-dlp.exe", "https://www.youtube.com/watch?v=PnHMAVXpKg8");
    longProcessBroker.addProcessStreamChangeEventListener(
        // Print the changed stream output to the console
        event -> System.out.println("event.getChangedString() = " + event.getChangedString()));
    longProcessBroker.execute();
  }

  private static void test1() {
    YtDlpOptionBuilder ytDlpOptionBuilder = new YtDlpOptionBuilder();
    ytDlpOptionBuilder.url("https://www.youtube.com/watch?v=PnHMAVXpKg8").output("%(title)s.%(ext)s");
    YtDlpOption options = ytDlpOptionBuilder.build();
    YtDlp.execute(options);
  }
}