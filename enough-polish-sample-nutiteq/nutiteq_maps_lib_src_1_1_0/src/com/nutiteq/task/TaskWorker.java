package com.nutiteq.task;

import java.util.Timer;
import java.util.TimerTask;

import com.nutiteq.cache.Cache;
import com.nutiteq.fs.FileSystem;
import com.nutiteq.io.RetrieveResourceTask;
import com.nutiteq.license.LicenseKeyCheck;
import com.nutiteq.log.Log;
import com.nutiteq.net.DownloadCounter;
import com.nutiteq.net.DownloadStreamOpener;

public class TaskWorker extends Thread {
  private boolean stopping;
  private TasksRunner tasksRunner;
  private DownloadStreamOpener downloadStreamOpener;
  private Cache networkCache;
  private Object currentObject;
  private DownloadCounter downloadCounter;
  private FileSystem fileSystem;
  private LicenseKeyCheck licenseKeyCheck;
  private final Timer timeoutTimer = new Timer();

  public TaskWorker(final TasksRunner tasksRunner, final DownloadStreamOpener downloadStreamOpener,
      final Cache networkCache, final DownloadCounter downloadCounter, final FileSystem fileSystem,
      final LicenseKeyCheck licenseKeyCheck) {
    this.tasksRunner = tasksRunner;
    this.downloadStreamOpener = downloadStreamOpener;
    this.networkCache = networkCache;
    this.downloadCounter = downloadCounter;
    this.fileSystem = fileSystem;
    this.licenseKeyCheck = licenseKeyCheck;
  }

  public void run() {
    setPriority(MIN_PRIORITY);
    while (!stopping) {
      synchronized (this) {
        // is queue empty?
        if (!tasksRunner.hasMoreTasks()) {
          try {
            wait();
          } catch (final InterruptedException ignore) {
          }
        }

        currentObject = tasksRunner.getNextTask();
      }

      if (stopping) {
        break;
      }

      if (currentObject == null) {
        continue;
      }

      executeTask(currentObject, true);
      currentObject = null;
    }

    tasksRunner = null;
    downloadStreamOpener = null;
    networkCache = null;
    downloadCounter = null;
    fileSystem = null;
    licenseKeyCheck = null;
  }

  protected void executeTask(final Object currentObject, final boolean onErrorInitializeNewWorker) {
    boolean done = false;
    try {
      boolean isNetworkTask = false;
      if (currentObject instanceof NetworkTask) {
        isNetworkTask = true;
        ((NetworkTask) currentObject).initialize(downloadStreamOpener, networkCache,
            downloadCounter);
        if (licenseKeyCheck != null) {
          tasksRunner.enqueueDownload(licenseKeyCheck, Cache.CACHE_LEVEL_NONE);
          licenseKeyCheck = null;
        }
      } else if (currentObject instanceof RetrieveResourceTask) {
        ((RetrieveResourceTask) currentObject).initialize(downloadStreamOpener, networkCache,
            downloadCounter, tasksRunner, fileSystem);
      }

      Timeout timeout = null;
      if (isNetworkTask) {
        timeout = new Timeout(currentObject, this, System.currentTimeMillis());
        timeoutTimer.schedule(timeout, downloadStreamOpener.getTimeout());
      }

      ((Task) currentObject).execute();
      done = true;

      if (timeout != null) {
        timeout.cancel();
      }
    } catch (final Exception e) {
        Log.error("Error in task runner: " + e.getMessage());
        Log.printStackTrace(e);
      notifyError(currentObject);
    } finally {
      if (done) {
        tasksRunner.taskCompleted();
      } else if (onErrorInitializeNewWorker && !stopping) {
        Log.debug("TW: create new worker");
        stopping = true;
        final TaskWorker next = new TaskWorker(tasksRunner, downloadStreamOpener, networkCache,
            downloadCounter, fileSystem, licenseKeyCheck);
        tasksRunner.setWorker(next);
        next.start();
      }
    }
  }

  private void notifyError(final Object executed) {
    if (executed instanceof NetworkTask) {
      ((NetworkTask) executed).notifyError();
    }
  }

  public void setDownloadStreamOpener(final DownloadStreamOpener opener) {
    downloadStreamOpener = opener;
  }

  public void setNetworkCache(final Cache networkCache) {
    this.networkCache = networkCache;
  }

  public synchronized void quit() {
    stopping = true;
    notify();
  }

  public void setDownloadCounter(final DownloadCounter downloadCounter) {
    this.downloadCounter = downloadCounter;
  }

  public void setFileSystem(final FileSystem fs) {
    this.fileSystem = fs;

  }

  public Cache getNetworkCache() {
    return networkCache;
  }

  public FileSystem getFileSystem() {
    return fileSystem;
  }

  public void setLicenceKeyCheck(final LicenseKeyCheck licenseKeyCheck) {
    this.licenseKeyCheck = licenseKeyCheck;
  }

  private static class Timeout extends TimerTask {
    private final Object executed;
    private final TaskWorker worker;
    private final long startTime;

    public Timeout(final Object currentObject, final TaskWorker worker, final long startTime) {
      this.executed = currentObject;
      this.worker = worker;
      this.startTime = startTime;
    }

    public void run() {
      if (worker.currentObject == executed) {
        Log.debug("TO: interrupt after " + (System.currentTimeMillis() - startTime));
        worker.interrupt();
      }
    }
  }
}
