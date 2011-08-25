package com.mgmaps.utils;

import com.nutiteq.task.LocalTask;

public class AsyncRunner extends Thread {
  private final LocalTask task;

  public AsyncRunner(final LocalTask task) {
    this.task = task;
  }

  public void run() {
    task.execute();
  }
}
