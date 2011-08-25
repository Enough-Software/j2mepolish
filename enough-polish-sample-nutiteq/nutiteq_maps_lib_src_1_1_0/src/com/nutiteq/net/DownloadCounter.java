package com.nutiteq.net;

/**
 * Interface for defining download counter objects
 */
public interface DownloadCounter {
  /**
   * Successful network request has been made.
   * 
   * @param url
   *          request url
   */
  void networkRequest(String url);

  /**
   * Data for given url was retrieved from cache
   * 
   * @param url
   *          request url
   * @param dataLength
   *          loaded data size
   */
  void cacheHit(String url, int dataLength);

  /**
   * Notify about donwloaded bytes
   * 
   * @param dataLength
   *          number of bytes downloaded
   */
  void downloaded(int dataLength);

  int getDownloadedBytes();

  void setNetworkListener(NetworkListener networkListener);

  /**
   * Download request was completed. Called after read and response processing
   * action returns.
   */
  void downloadCompleted();

  int getNumberOfNetworkRequests();

  int getNumberOfCacheHits();

  int getBytesLoadedFromCache();
}
