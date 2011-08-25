package com.nutiteq.maps;

import com.nutiteq.components.MapTile;

/**
 * Interface for streamed map data handling
 */
public interface StreamedMap {
  /**
   * Create path for retrieving given tiles as one file
   * 
   * @param tiles
   *          tiles to be retrieved
   * @return path for given tiles
   */
  String buildStreamedPath(MapTile[] tiles);
}
