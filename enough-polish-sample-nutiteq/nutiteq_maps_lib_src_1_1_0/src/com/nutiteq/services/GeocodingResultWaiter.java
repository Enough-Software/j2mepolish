package com.nutiteq.services;

import com.nutiteq.components.KmlPlace;

/**
 * Interface for objects waiting on Geocoding service results
 */
public interface GeocodingResultWaiter {
  /**
   * Received results from server.
   * 
   * @param kmlPlaces
   *          results found
   */
  void searchResults(final KmlPlace[] kmlPlaces);

  void errors(final int errorCode);
}
