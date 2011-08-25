package com.nutiteq.location;

/**
 * Interface for class calling {@link com.nutiteq.helpers.BluetoothGpsBrowser}.
 */
public interface LocationSourceWaiter {
  /**
   * Receive location source from helper
   * 
   * @param locationSource
   *          location source selected from browser
   */
  void setLocationSource(final LocationSource locationSource);

  /**
   * Handle location browsing cancellation.
   */
  void browsingCanceled();
}
