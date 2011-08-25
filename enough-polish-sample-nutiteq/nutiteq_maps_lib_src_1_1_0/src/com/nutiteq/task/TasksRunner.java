package com.nutiteq.task;

import com.nutiteq.cache.Cache;
import com.nutiteq.fs.FileSystem;
import com.nutiteq.license.LicenseKeyCheck;
import com.nutiteq.listeners.ErrorListener;
import com.nutiteq.net.DownloadCounter;
import com.nutiteq.net.DownloadHandler;
import com.nutiteq.net.DownloadRequestor;
import com.nutiteq.net.DownloadStreamOpener;

public interface TasksRunner extends DownloadHandler {
  boolean hasMoreTasks();

  Object getNextTask();

  void taskCompleted();

  void setWorker(TaskWorker next);

  void quit();

  void enqueue(Task task);

  void enqueueDownloadRequestor(DownloadRequestor requestor, int cacheLevel);

  void setDownloadCounter(DownloadCounter downloadCounter);

  void setDownloadStreamOpener(DownloadStreamOpener opener);

  void setErrorListener(ErrorListener errorListener);

  void setNetworkCache(Cache networkCache);

  void startWorker();

  void setFileSystem(FileSystem fs);

  Cache getNetworkCache();

  FileSystem getFileSystem();

  void setLicenceKeyCheck(LicenseKeyCheck licenseKeyCheck);
}