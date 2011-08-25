package com.nutiteq.maps.projections;

import com.nutiteq.components.MapPos;
import com.nutiteq.components.Point;

/**
 * Interface for projection system calculation implementations
 */
public interface Projection {
  /**
   * Convert a point on pixel map to WGS84 coordinates (decimal coordinates *
   * 1000000)
   * 
   * @param pos
   *          pixel point on the map (on 256x256 pixels map of the world 0E 0N
   *          is located at map pixel 128x : 128y)
   * @return point in internally used WGS84 format (decimal degrees * 1000000)
   */
  Point mapPosToWgs(final MapPos pos);

  /**
   * Convert WGS84 coordinates to pixel point on map
   * 
   * @param wgs
   *          WGS84 coordinates (decimal coordinates * 1000000)
   * @param zoom
   *          zoom level for the map
   * @return pixel position on map for the coordinates
   */
  MapPos wgsToMapPos(final Point wgs, int zoom);
}
