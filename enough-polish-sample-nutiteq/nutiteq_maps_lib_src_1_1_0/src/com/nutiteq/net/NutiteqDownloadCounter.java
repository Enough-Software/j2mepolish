package com.nutiteq.net;

import com.nutiteq.log.Log;

/**
 * Default download counter used in library. For every network request adds an
 * estimate of 100 bytes of overhead for creating the connection.
 */
public class NutiteqDownloadCounter implements DownloadCounter {
  private int networkRequests;
  private int cacheHits;
  private int loadedFromCache;
  private int downloaded;
  private NetworkListener networkListener;

  public void networkRequest(final String url) {
    if (networkListener != null) {
      networkListener.downloadStarted();
    }
    networkRequests++;
    // average HTTP overhead is usually at least 500-1000 bytes
    downloaded += 500;
  }

  public void cacheHit(final String url, final int length) {
    Log.debug("Cache hit: " + url);
    cacheHits++;
    loadedFromCache += length;
    if (cacheHits % 10 == 0) {
      Log.info("Total loaded from cache " + loadedFromCache);
    }
  }

  public void downloaded(final int dataLength) {
    if (networkListener != null) {
      networkListener.dataMoved();
    }
    downloaded += dataLength;
    if (networkRequests % 10 == 0) {
      Log.info("Total downloaded " + downloaded);
    }
  }

  public int getDownloadedBytes() {
    return downloaded;
  }

  public void setNetworkListener(final NetworkListener networkListener) {
    this.networkListener = networkListener;
  }

  public void downloadCompleted() {
    if (networkListener != null) {
      networkListener.downloadCompleted();
    }
  }

  public int getNumberOfNetworkRequests() {
    return networkRequests;
  }

  public int getNumberOfCacheHits() {
    return cacheHits;
  }

  public int getBytesLoadedFromCache() {
    return loadedFromCache;
  }
}
