package com.nutiteq.kml;

import com.nutiteq.components.WgsBoundingBox;

/**
 * Interface for kml services implementation. If inserted into library, results
 * display and update will be handled automatically by library.
 */
public interface KmlService {
  /**
   * Does the service need an update. Called after move method.
   * 
   * @param boundingBox
   *          bounding box for new map location (coordinates in WGS84)
   * @param zoom
   *          current zoom level
   * @return does the service need an update
   */
  boolean needsUpdate(final WgsBoundingBox boundingBox, final int zoom);

  /**
   * Called after if needsUpdate has returned true.
   * 
   * @param boundingBox
   *          bounding box for screen view of the map (coordinates in WGS84)
   * @param zoom
   *          zoom level used
   * @return url for retrieving displayed kml.
   */
  String getServiceUrl(final WgsBoundingBox boundingBox, final int zoom);

  /**
   * Maximum number of results returned from this service. If, for some reason,
   * server returns more results, extra results will be ignored.
   * 
   * @return number of placemarks returned by this service on one request.
   */
  int maxResults();

  String getDefaultIcon();
}
