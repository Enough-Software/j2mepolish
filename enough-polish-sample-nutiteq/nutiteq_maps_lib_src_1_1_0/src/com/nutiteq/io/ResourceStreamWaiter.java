package com.nutiteq.io;

import java.io.IOException;
import java.io.InputStream;

import com.nutiteq.cache.Cache;
import com.nutiteq.net.DownloadCounter;

/**
 * Defines resource that handles data reading by itself.
 */
public interface ResourceStreamWaiter {
  /**
   * Stream for resource has been opened
   * 
   * @param stream
   *          input stream to data
   * @param counter
   *          download counter
   * @param networkCache
   * @throws IOException
   *           thrown on read error
   */
  void streamOpened(InputStream stream, DownloadCounter counter, Cache networkCache)
      throws IOException;
}
