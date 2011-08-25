package com.nutiteq.net;

public interface Downloadable {
  /**
   * Get object URL.
   * 
   * @return the URL for the object
   */
  String getUrl();

  /**
   * Notify the object that an error occurred.
   */
  void notifyError();
}
