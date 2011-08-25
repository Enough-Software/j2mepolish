package com.nutiteq.location;

import com.nutiteq.components.WgsPoint;

/**
 * Coordinates data source for {@link LocationMarker}.
 */
public interface LocationSource {
  int STATUS_CONNECTING = 1;
  int STATUS_CONNECTED = 2;
  int STATUS_CONNECTION_LOST = 4;
  int STATUS_CANT_LOCATE = 8;

  /**
   * Get status code for location source.
   * 
   * @return status code
   */
  int getStatus();

  WgsPoint getLocation();

  /**
   * Set location marker where to push location updates
   * 
   * @param marker
   *          marker displayed on screen
   */
  void setLocationMarker(final LocationMarker marker);

  /**
   * Get location marker used on screen
   * 
   * @return associated location marker
   */
  LocationMarker getLocationMarker();

  /**
   * Start location polling
   */
  void start();

  /**
   * Quit location polling
   */
  void quit();

  /**
   * Add listener for location coordinates
   * 
   * @param listener
   *          listener to be added
   */
  void addLocationListener(LocationListener listener);
}
