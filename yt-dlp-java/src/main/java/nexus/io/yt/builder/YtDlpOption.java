package nexus.io.yt.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YtDlpOption {
  private final List<String> args;
  private final String url;

  public YtDlpOption(List<String> args, String url) {
    this.args = Collections.unmodifiableList(new ArrayList<>(args));
    this.url = url;
  }

  /** Options in order, without the executable and the URL. */
  public List<String> getArgs() {
    return args;
  }

  public String getUrl() {
    return url;
  }

  /** Options followed by the URL (if any), one element per process argument. */
  public List<String> toArgs() {
    List<String> list = new ArrayList<>(args);
    if (url != null && !url.isEmpty()) {
      list.add(url);
    }
    return list;
  }

  /** Human-readable command line, for logs only. Not suitable for re-parsing. */
  public String toCommand() {
    return String.join(" ", toArgs());
  }
}
