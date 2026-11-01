package com.litongjava.youtube.handler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.litongjava.youtube.utils.MediaFileResponderAccess;

public class ContentDispositionTest {
  @Test
  public void encodesUtf8AndSanitizesFallback() {
    assertEquals("attachment; filename=\"__ a_b.mp3\"; filename*=UTF-8''%E4%B8%AD%E6%96%87%20a%22b.mp3",
        MediaFileResponderAccess.contentDisposition("中文 a\"b.mp3"));
  }
}
