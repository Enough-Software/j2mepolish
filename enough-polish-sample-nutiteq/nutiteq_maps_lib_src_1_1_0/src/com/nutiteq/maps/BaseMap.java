package com.nutiteq.maps;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nutiteq.components.MapPos;
import com.nutiteq.components.TileMapBounds;
import com.nutiteq.components.ZoomRange;
import com.nutiteq.task.Task;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;
import com.nutiteq.utils.Utils;

/**
 * <p>
 * Base class for maps that handles some common logic between different map
 * types.
 * </p>
 * <p>
 * Handles properties:
 * <ul>
 * <li>copyright string</li>
 * <li>tile size</li>
 * <li>zoom range (min/max zoom)</li>
 * </ul>
 * </p>
 * <p>
 * Default map size, map bounds and zoom handling is implemented based on
 * OpenStreetMap system. Feel free to override these based on your specific
 * needs.
 * </p>
 */
public abstract class BaseMap implements GeoMap {
  private Copyright copyright;
  private final int tileSize;
  private final ZoomRange zoomRange;
  private MapTileOverlay overlay;
  private Image missingTile;

  public BaseMap(final Copyright copyright, final int tileSize, final int minZoom, final int maxZoom) {
    this.copyright = copyright;
    this.tileSize = tileSize;
    this.zoomRange = new ZoomRange(minZoom, maxZoom);
  }

  public BaseMap(final String copyright, final int tileSize, final int minZoom, final int maxZoom) {
    this(new StringCopyright(copyright), tileSize, minZoom, maxZoom);
  }
  
  public Task getInitializationTask() {
    return null;
  }

  public Copyright getCopyright() {
    return copyright;
  }

  public int getTileSize() {
    return tileSize;
  }

  public int getMaxZoom() {
    return zoomRange.getMaxZoom();
  }

  public int getMinZoom() {
    return zoomRange.getMinZoom();
  }

  public ZoomRange getZoomRange() {
    return zoomRange;
  }

  /**
   * <p>
   * Handles zoom for display middle point (as default the red cross on screen)
   * by assuming that map size between single zoom levels is always magnitude of
   * two (next map size is two times smaller or bigger).
   * </p>
   * <p>
   * For example for OpenStreetMap zoom level 0 world size is 256x256 pixels and
   * center point will be in 128x128. If zoomed in one level world size will be
   * 512x512 pixels and center point will be at 256x256.
   * </p>
   * 
   * @param middlePoint
   *          map position on witch to perform zoom action
   * @param zoomSteps
   *          zoom steps needed to handle
   * @return maps position on new zoom level
   */
  public MapPos zoom(final MapPos middlePoint, final int zoomSteps) {
    int x = middlePoint.getX();
    int y = middlePoint.getY();
    int zoom = middlePoint.getZoom();
    if (zoomSteps > 0) {
      // zoom in
      x <<= zoomSteps;
      y <<= zoomSteps;
    } else {
      x >>= -zoomSteps;
      y >>= -zoomSteps;
    }
    zoom += zoomSteps;

    return new MapPos(x, y, zoom);
  }

  /**
   * Get map bounds for specified zoom level.
   * 
   * @param zoom
   *          zoom level for witch to get map bounds
   * @return bounds for zoom level for given map
   */
  public TileMapBounds getTileMapBounds(final int zoom) {
    final MapPos min = new MapPos(0, 0, zoom);
    final MapPos max = new MapPos(getMapWidth(zoom) - 1, getMapHeight(zoom) - 1, zoom);
    return new TileMapBounds(min, max);
  }

  /**
   * Get map height for given zoom level. Default implementation is done for
   * OpenStreetMap system (map tile edge is power of 2)
   * 
   * @param zoom
   *          zoom level for witch to get map height
   * @return map height
   */
  public int getMapHeight(final int zoom) {
    return 1 << (zoom + Utils.log2(getTileSize()));
  }

  /**
   * Get map width for given zoom level. Default implementation is done for
   * OpenStreetMap system (map tile edge is power of 2)
   * 
   * @param zoom
   *          zoom level for witch to get map width
   * @return map width
   */
  public int getMapWidth(final int zoom) {
    return 1 << (zoom + Utils.log2(getTileSize()));
  }

  public void addTileOverlay(final MapTileOverlay overlay) {
    this.overlay = overlay;
  }

  public MapTileOverlay getTileOverlay() {
    return overlay;
  }

  public void setMissingTileImage(final Image missingTile) {
    this.missingTile = missingTile;
  }

  public Image getMissingTileImage() {
    if (missingTile == null) {
      missingTile = Image.createImage(getTileSize(), getTileSize());
      final Graphics g = missingTile.getGraphics();
      g.setColor(0xFFFF0000);
      g.fillRect(0, 0, getTileSize(), getTileSize());
    }

    return missingTile;
  }

  public void setCopyright(final Copyright copyright) {
    this.copyright = copyright;
  }
}
