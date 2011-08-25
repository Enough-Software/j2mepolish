package com.nutiteq.listeners;

public interface ErrorListener {
  /**
   * Library has encountered a network related error.
   * 
   * @param message
   *          error message from library
   */
  void networkError(String message);

  /**
   * Invalid license was used
   * 
   * @param message
   *          error message from library
   */
  void licenseError(String message);
}
