package com.nutiteq.task;

import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.io.RetrieveResourceTask;
import com.nutiteq.listeners.ErrorListener;
import com.nutiteq.net.DownloadRequestor;

public class DownloadRequestorTask implements Task {
  private final DownloadRequestor requestor;
  private final ErrorListener errorListener;
  private final TasksRunner tasksRunner;

  public DownloadRequestorTask(final DownloadRequestor requestor,
      final ErrorListener errorListener, final TasksRunner tasksRunner) {
    this.requestor = requestor;
    this.errorListener = errorListener;
    this.tasksRunner = tasksRunner;
  }

  public void execute() {
    ResourceRequestor processed;
    while ((processed = requestor.getDownloadable()) != null) {
      //TODO jaanus : check this
      final RetrieveResourceTask downloadTask = new RetrieveResourceTask(processed);
      tasksRunner.enqueue(downloadTask);
    }
  }
}
