package com.nutiteq.io;

import com.nutiteq.cache.Cache;
import com.nutiteq.fs.FileSystem;
import com.nutiteq.net.DownloadCounter;
import com.nutiteq.net.DownloadStreamOpener;
import com.nutiteq.task.NetworkTask;
import com.nutiteq.task.RetrieveNetworkResourceTask;
import com.nutiteq.task.Task;
import com.nutiteq.task.TasksRunner;
import com.nutiteq.utils.Utils;

public class RetrieveResourceTask implements Task {
  private final ResourceRequestor resourceRequestor;
  private DownloadStreamOpener downloadStreamOpener;
  private Cache networkCache;
  private DownloadCounter downloadCounter;
  private TasksRunner tasksRunner;
  private FileSystem fileSystem;

  public RetrieveResourceTask(final ResourceRequestor resourceRequestor) {
    this(resourceRequestor, null);
  }

  public RetrieveResourceTask(final ResourceRequestor resourceRequestor,
      final TasksRunner tasksRunner) {
    this.resourceRequestor = resourceRequestor;
    this.tasksRunner = tasksRunner;
  }

  public void execute() {
    Task executed = null;

    switch (Utils.getResourceType(resourceRequestor.resourcePath())) {
    case Utils.RESOURCE_TYPE_NETWORK:
      executed = new RetrieveNetworkResourceTask(resourceRequestor, null, resourceRequestor
          .getCachingLevel());
      ((NetworkTask) executed).initialize(downloadStreamOpener, networkCache, downloadCounter);
      break;
    case Utils.RESOURCE_TYPE_FILE:
      throw new RuntimeException("No file support yet");
    case Utils.RESOURCE_TYPE_JAR:
      executed = new RetrieveJarResourceTask(resourceRequestor);
      break;
    default:
      throw new RuntimeException("Don't know what to do with " + resourceRequestor.resourcePath());
    }

    //TODO jaanus : check this
    if (tasksRunner == null) {
      executed.execute();
    } else {
      tasksRunner.enqueue(executed);
    }
  }

  public void initialize(final DownloadStreamOpener downloadStreamOpener, final Cache networkCache,
      final DownloadCounter downloadCounter, final TasksRunner tasksRunner,
      final FileSystem fileSystem) {
    this.downloadStreamOpener = downloadStreamOpener;
    this.networkCache = networkCache;
    this.downloadCounter = downloadCounter;
    this.tasksRunner = tasksRunner;
    this.fileSystem = fileSystem;
  }

  public DownloadStreamOpener getDownloadStreamOpener() {
    return downloadStreamOpener;
  }

  public Cache getNetworkCache() {
    return networkCache;
  }

  public DownloadCounter getDownloadCounter() {
    return downloadCounter;
  }

  public FileSystem getFileSystem() {
    return fileSystem;
  }
}
