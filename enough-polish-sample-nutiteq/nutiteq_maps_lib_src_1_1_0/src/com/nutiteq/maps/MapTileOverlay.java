package com.nutiteq.maps;

import com.nutiteq.components.MapTile;

/**
 * Overlay information service for map tiles.
 */
public interface MapTileOverlay {
  /**
   * Generate overlay tile url for given map tile.
   * 
   * @param tile
   *          tile for which to get additional information
   * @return url for overlay tile
   */
  String getOverlayTileUrl(MapTile tile);
}
