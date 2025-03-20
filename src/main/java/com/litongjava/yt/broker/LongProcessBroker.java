package com.litongjava.yt.broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.litongjava.yt.event.ProcessStreamChangeEvent;
import com.litongjava.yt.event.ProcessStreamChangeEventListener;

public class LongProcessBroker {

  private final String[] command;

  List<ProcessStreamChangeEventListener> listeners = new ArrayList<>();
  List<String> strings = new ArrayList<>();

  public LongProcessBroker(String... command) {
    this.command = command;
  }

  public void execute() {
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true);
    try {
      Process process = processBuilder.start();
      try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String s;
        while (process.isAlive()) {
          while ((s = bufferedReader.readLine()) != null) {
            strings.add(s);
            List<String> unmodifiableList = Collections.unmodifiableList(strings);
            for (ProcessStreamChangeEventListener listener : listeners) {
              listener.onStreamChange(new ProcessStreamChangeEvent(this, unmodifiableList, s));
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void addProcessStreamChangeEventListener(ProcessStreamChangeEventListener listener) {
    listeners.add(listener);
  }

  public void removeProcessStreamChangeEventListener(ProcessStreamChangeEventListener listener) {
    listeners.remove(listener);
  }

  public void removeProcessStreamChangeEventListener(int index) {
    listeners.remove(index);
  }
}