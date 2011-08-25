package com.nutiteq.maps;

import javax.microedition.lcdui.Image;

import com.nutiteq.components.MapPos;
import com.nutiteq.components.Point;
import com.nutiteq.components.TileMapBounds;
import com.nutiteq.components.ZoomRange;
import com.nutiteq.task.Task;
import com.nutiteq.ui.Copyright;

/**
 * Defines the basic properties for displayed map.
 */
public interface GeoMap {
  
  
  /**
   * Get task that will be executed after <b>this</b> is set with {@link com.nutiteq.BasicMapComponent.setMap(GeoMap)}
   * 
   * @return Task to execute.
   */
  Task getInitializationTask();
  /**
   * Minimum zoom level for implementing map.
   * 
   * @return minimum zoom level
   */
  int getMinZoom();

  /**
   * Maximum zoom level for implementing map.
   * 
   * @return maximum zoom for the map
   */
  int getMaxZoom();

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

  /**
   * Get tile size for implemented map
   * 
   * @return tile size in pixels
   */
  int getTileSize();

  /**
   * Get map width based on zoom level
   * 
   * @param zoom
   *          internal zoom (world in 256x256 pizels is 0)
   * @return map width
   */
  int getMapWidth(final int zoom);

  /**
   * Get map height based on zoom level
   * 
   * @param zoom
   *          internal zoom (world in 256x256 pizels is 0)
   * @return map height
   */
  int getMapHeight(final int zoom);

  /**
   * Do zoom calculations for screen middle point
   * 
   * @param middlePoint
   *          current middle point
   * @param zoomSteps
   *          zoom steps (positive is zoom in, negative zoom out)
   * @return return new screen middle point location on new zoom level
   */
  MapPos zoom(final MapPos middlePoint, final int zoomSteps);

  /**
   * Get zoom range for the map
   * 
   * @return zoom range (min zoom, max zoom) for the map
   */
  ZoomRange getZoomRange();

  /**
   * Map copyright string painted on screen
   * 
   * @return copyright string
   */
  Copyright getCopyright();

  /**
   * Get map bounds for given zoom level. This is used to check if retrieved
   * tile is valid and also stop panning outside map coverage.
   * 
   * @param zoom
   *          current zoom level
   * @return minimum and maximum points on the tile map
   */
  TileMapBounds getTileMapBounds(final int zoom);

  /**
   * Add overlay for displaying tiles with additional data on map tiles.
   * 
   * @param overlay
   *          overlay to be used for additional data
   */
  void addTileOverlay(MapTileOverlay overlay);

  /**
   * Get overlay used for map
   * 
   * @return overlay used for additional data
   */
  MapTileOverlay getTileOverlay();

  /**
   * Get image used for showing missing image.
   * 
   * @return image used
   */
  Image getMissingTileImage();

  /**
   * Set image used for missing tiles
   * 
   * @param missingTile
   *          image used
   */
  void setMissingTileImage(Image missingTile);
}
