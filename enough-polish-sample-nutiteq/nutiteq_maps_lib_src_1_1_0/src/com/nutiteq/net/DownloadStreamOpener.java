package com.nutiteq.net;

/**
 * Interface for opening stream to downloaded resource. Library implementation
 * assumes, that stream closing will be handled by stream opener class.
 */
public interface DownloadStreamOpener {
  /**
   * Error code for network error
   */
  int NETWORK_ERROR = 1;
  /**
   * Error code for response with other responce code then 200.
   */
  int RESPONCE_NOT_OK = 2;
  /**
   * Error code for security exception, when network connection was opened.
   */
  int SECURITY_EXCEPTION = 3;
  /**
   * Maximum number of HTTP redirects has been reached.
   */
  int TOO_MANY_REDIRECTS = 4;

  /**
   * Open a connection to given network resource and pass opened stream to
   * streamWaiter.
   * 
   * @param streamWaiter
   *          object asking for stream to be opened
   * @param url
   *          URL to network resource
   */
  void openInputStream(DownloadStreamWaiter streamWaiter, String url);

  void openInputStream(DownloadStreamWaiter streamWaiter,
      DataPostingDownloadable postingDownloadable);

  long getTimeout();
}
