package com.nutiteq.task;

import java.io.IOException;
import java.io.InputStream;

import com.nutiteq.cache.Cache;
import com.nutiteq.components.MapTile;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.io.ResourceStreamWaiter;
import com.nutiteq.listeners.ErrorListener;
import com.nutiteq.log.Log;
import com.nutiteq.net.DataPostingDownloadable;
import com.nutiteq.net.DownloadCounter;
import com.nutiteq.net.DownloadStreamOpener;
import com.nutiteq.net.DownloadStreamWaiter;
import com.nutiteq.net.StreamedTilesDownloadable;
import com.nutiteq.utils.IOUtils;

public class RetrieveNetworkResourceTask extends NetworkTask implements DownloadStreamWaiter {
  private final ResourceRequestor downloadable;
  private final ErrorListener errorListener;
  private Cache networkCache;
  private String downloadableUrl;

  public RetrieveNetworkResourceTask(final ResourceRequestor downloadable,
      final ErrorListener errorListener, final int cacheLevel) {
    this.downloadable = downloadable;
    this.errorListener = errorListener;
  }

  public void execute() {
    execute(getDownloadStreamOpener(), getNetworkCache(), getDownloadCounter());
  }

  private void execute(final DownloadStreamOpener opener, final Cache networkCache,
      final DownloadCounter downloadCounter) {
    this.networkCache = networkCache;
    downloadableUrl = downloadable.resourcePath();
    if (downloadableUrl == null || "".equals(downloadableUrl)) {
      Log.error("Null or empty url from downloadable!");
      downloadable.notifyError();
      return;
    }

    //TODO jaanus : check also if resource has caching value
    final byte[] cacheData = networkCache == null ? null : networkCache.get(downloadableUrl);

    if (cacheData != null && downloadable instanceof ResourceDataWaiter) {
      if (downloadCounter != null) {
        downloadCounter.cacheHit(downloadableUrl, cacheData.length);
      }
      ((ResourceDataWaiter) downloadable).dataRetrieved(cacheData);
    } else if (downloadable instanceof DataPostingDownloadable) {
      opener.openInputStream(this, (DataPostingDownloadable) downloadable);
    } else {
      // TODO jaanus : handle download separator
      opener.openInputStream(this, downloadableUrl);
    }
  }

  public void streamOpened(final InputStream stream) throws IOException {
    final DownloadCounter downloadCounter = getDownloadCounter();
    if (downloadCounter != null) {
      downloadCounter.networkRequest(downloadableUrl);
    }

    if (downloadable instanceof ResourceStreamWaiter) {
      ((ResourceStreamWaiter) downloadable).streamOpened(stream, downloadCounter, networkCache);
    } else {
      final byte[] downloadedData = IOUtils.readFully(stream);
      if (networkCache != null) {
        networkCache.cache(downloadableUrl, downloadedData, downloadable.getCachingLevel());
      }

      if (downloadCounter != null) {
        downloadCounter.downloaded(downloadedData.length);
      }

      ((ResourceDataWaiter) downloadable).dataRetrieved(downloadedData);
    }

    if (downloadCounter != null) {
      downloadCounter.downloadCompleted();
    }
  }

  public void error(final int errorCode, final String message) {
    Log.error("Network error "+errorCode+ " : "+message);
      if (getDownloadCounter() != null) {
      getDownloadCounter().downloadCompleted();
    }

    //TODO jaanus : this can't be normal
    //error handling for tiles is done outside
    if (downloadable instanceof MapTile || downloadable instanceof StreamedTilesDownloadable) {
      return;
    }

    downloadable.notifyError();
    if (errorListener != null) {
      switch (errorCode) {
      case DownloadStreamOpener.NETWORK_ERROR:
        errorListener.networkError("Network error: " + message);
        return;
      case DownloadStreamOpener.RESPONCE_NOT_OK:
        errorListener.networkError("Response code was not 200");
        return;
      case DownloadStreamOpener.SECURITY_EXCEPTION:
        errorListener.networkError("Security exception: " + message);
        return;
      case DownloadStreamOpener.TOO_MANY_REDIRECTS:
        errorListener.networkError("Redirects: " + message);
        return;
      }
    }
  }

  public ResourceRequestor getDownloadable() {
    return downloadable;
  }

  public void notifyError() {
    downloadable.notifyError();
  }
}
