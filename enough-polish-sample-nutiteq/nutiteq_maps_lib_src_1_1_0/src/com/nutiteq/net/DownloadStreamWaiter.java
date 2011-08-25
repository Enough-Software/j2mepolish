package com.nutiteq.net;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for objects that are waiting for download stream.
 */
public interface DownloadStreamWaiter {
  /**
   * Give opened stream to waiter.
   * 
   * @param stream
   *          opened stream
   * @throws IOException
   *           exception thrown during data reading from stream
   */
  void streamOpened(final InputStream stream) throws IOException;

  /**
   * Error notification with codes from {@link DownloadStreamOpener}
   * 
   * @param errorCode
   *          error code
   * @param message
   *          additional message
   */
  void error(int errorCode, String message);
}
