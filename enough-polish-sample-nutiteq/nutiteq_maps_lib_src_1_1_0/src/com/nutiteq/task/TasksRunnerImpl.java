package com.nutiteq.task;

import com.mgmaps.utils.Queue;
import com.nutiteq.cache.Cache;
import com.nutiteq.fs.FileSystem;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.io.RetrieveResourceTask;
import com.nutiteq.license.LicenseKeyCheck;
import com.nutiteq.listeners.ErrorListener;
import com.nutiteq.net.DefaultDownloadStreamOpener;
import com.nutiteq.net.DownloadCounter;
import com.nutiteq.net.DownloadHandler;
import com.nutiteq.net.DownloadRequestor;
import com.nutiteq.net.DownloadStreamOpener;

public class TasksRunnerImpl implements DownloadHandler, TasksRunner {
  private final Queue executionQueue;

  private Object currentObject;

  private boolean stopping;
  private boolean started;

  private ErrorListener errorListener;

  private TaskWorker worker;

  public TasksRunnerImpl(final Queue tasksQueue) {
    executionQueue = tasksQueue;
    worker = new TaskWorker(this, new DefaultDownloadStreamOpener(), null, null, null, null);
  }

  public boolean hasMoreTasks() {
    return !executionQueue.isEmpty();
  }

  public Object getNextTask() {
    currentObject = executionQueue.pop();
    return currentObject;
  }

  /**
   * Enqueue task for execution.
   */
  public void enqueue(final Task o) {
    if (!stopping && (!o.equals(currentObject)) && executionQueue.find(o) == null) {
      executionQueue.push(o);
      if (!started) {
        return;
      }
      synchronized (worker) {
        worker.notify();
      }
    }
  }

  public void quit() {
    stopping = true;
    synchronized (worker) {
      worker.quit();
      worker=null;
    }
  }

  public void setErrorListener(final ErrorListener errorListener) {
    this.errorListener = errorListener;
  }

  public synchronized void enqueueDownload(final ResourceRequestor d, final int cacheLevel) {
    final RetrieveResourceTask task = new RetrieveResourceTask(d);
    enqueue(task);
  }

  public synchronized void enqueueDownloadRequestor(final DownloadRequestor requestor,
      final int cacheLevel) {
    final DownloadRequestorTask task = new DownloadRequestorTask(requestor, errorListener, this);
    enqueue(task);
  }

  public void setDownloadStreamOpener(final DownloadStreamOpener opener) {
    worker.setDownloadStreamOpener(opener);
  }

  public void setNetworkCache(final Cache networkCache) {
    worker.setNetworkCache(networkCache);
  }

  public void setWorker(final TaskWorker next) {
    worker = next;
  }

  public void taskCompleted() {
    currentObject = null;
  }

  public void startWorker() {
    started = true;
    synchronized (worker) {
        if(!worker.isAlive()){
            worker.start();
        }
    }
  }

  public void setDownloadCounter(final DownloadCounter downloadCounter) {
    worker.setDownloadCounter(downloadCounter);
  }

  public void setFileSystem(final FileSystem fs) {
    worker.setFileSystem(fs);
  }

  public Cache getNetworkCache() {
    return worker.getNetworkCache();
  }

  public FileSystem getFileSystem() {
    return worker.getFileSystem();
  }

  public void setLicenceKeyCheck(final LicenseKeyCheck licenseKeyCheck) {
    worker.setLicenceKeyCheck(licenseKeyCheck);
  }
}
