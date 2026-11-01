package nexus.io.yt.event;

import java.util.EventListener;

public interface ProcessStreamChangeEventListener extends EventListener {
  void onStreamChange(ProcessStreamChangeEvent event);
}