package nexus.io.yt.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public class YtDlpUtilsOfflineTest {

  @Test
  public void findMediaSkipsLogsHiddenAndIntermediateFiles() throws Exception {
    Path dir = Files.createTempDirectory("ytdlp-test");
    Files.createFile(dir.resolve("stdout.log"));
    Files.createFile(dir.resolve(".hidden.mp4"));
    Files.createFile(dir.resolve("title.f137.mp4"));
    Files.createFile(dir.resolve("title.mp4.part"));
    assertNull(YtDlpUtils.findMedia(dir.toFile(), ".mp4"));

    Files.createFile(dir.resolve("title.mp4"));
    assertEquals(new File(dir.toFile(), "title.mp4"), YtDlpUtils.findMedia(dir.toFile(), ".mp4"));
  }

  @Test
  public void maskSecretsHidesProxyCredentialsAndPasswords() {
    List<String> masked = YtDlp.maskSecrets(
        List.of("yt-dlp", "--proxy", "http://user:pa@ss@host:1080", "--password", "secret", "url"));
    assertEquals(List.of("yt-dlp", "--proxy", "http://****@host:1080", "--password", "****", "url"), masked);
  }

  @Test(expected = IllegalArgumentException.class)
  public void rejectsPathLikeVideoId() throws Exception {
    YtDlpUtils.downloadMp3("../../etc", true);
  }
}
