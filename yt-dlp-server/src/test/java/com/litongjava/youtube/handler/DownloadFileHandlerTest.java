package com.litongjava.youtube.handler;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class DownloadFileHandlerTest {

  @Test
  public void resolvesOnlyMediaInsideDownloadFolder() throws Exception {
    Path dir = Paths.get("download", "unitTestId0", "mp3");
    Files.createDirectories(dir);
    Path media = Files.write(dir.resolve("a.mp3"), new byte[] { 1 });
    Path cjk = Files.write(dir.resolve("中文 1+1.mp3"), new byte[] { 1 });
    Files.write(dir.resolve("stderr.log"), new byte[] { 1 });
    try {
      assertNotNull(DownloadFileHandler.resolve("/download/unitTestId0/mp3/a.mp3"));
      assertNull(DownloadFileHandler.resolve("/download/unitTestId0/mp3/stderr.log"));
      assertNull(DownloadFileHandler.resolve("/download/../pom.xml"));
      assertNull(DownloadFileHandler.resolve("/download/unitTestId0/../../pom.xml"));
      assertNull(DownloadFileHandler.resolve("/download/unitTestId0/mp3/.tmp/x.mp3"));
      assertNull(DownloadFileHandler.resolve("/other/a.mp3"));
      assertNotNull(DownloadFileHandler.resolve("/download/unitTestId0/mp3/%E4%B8%AD%E6%96%87%201+1.mp3"));
      assertNull(DownloadFileHandler.resolve("/download/%2e%2e/pom.xml"));
      assertNull(DownloadFileHandler.resolve("/download/unitTestId0/mp3/..%2f..%2f..%2fpom.xml"));
    } finally {
      Files.deleteIfExists(media);
      Files.deleteIfExists(cjk);
      Files.deleteIfExists(dir.resolve("stderr.log"));
      Files.deleteIfExists(dir);
      Files.deleteIfExists(dir.getParent());
      try {
        Files.deleteIfExists(dir.getParent().getParent());
      } catch (java.nio.file.DirectoryNotEmptyException ignored) {
        // download/ also holds real downloads
      }
    }
  }
}
