package com.nutiteq.task;

import com.nutiteq.cache.Cache;
import com.nutiteq.net.DownloadCounter;
import com.nutiteq.net.DownloadStreamOpener;

public abstract class NetworkTask implements Task {
  private DownloadStreamOpener downloadStreamOpener;
  private Cache networkCache;
  private DownloadCounter downloadCounter;

  public void initialize(final DownloadStreamOpener downloadStreamOpener, final Cache networkCache,
      final DownloadCounter downloadCounter) {
    this.downloadStreamOpener = downloadStreamOpener;
    this.networkCache = networkCache;
    this.downloadCounter = downloadCounter;
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

  public abstract void notifyError();
}
