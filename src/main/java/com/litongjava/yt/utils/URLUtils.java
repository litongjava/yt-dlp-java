package com.litongjava.yt.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class URLUtils {

  public static HttpURLConnection getConnection(String targetUrl)
      throws MalformedURLException, IOException, ProtocolException {
    HttpURLConnection connection;
    @SuppressWarnings("deprecation")
    URL url = new URL(targetUrl);
    connection = (HttpURLConnection) url.openConnection();
    connection.setConnectTimeout(15000);
    connection.setReadTimeout(15000);
    return connection;
  }
}
