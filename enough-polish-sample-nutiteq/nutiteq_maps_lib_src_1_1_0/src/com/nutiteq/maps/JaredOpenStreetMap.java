package com.nutiteq.maps;

import com.nutiteq.maps.projections.EPSG3785;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;

/**
 * Implementation for map that uses OpenStreetMap tiles from jar. File names are
 * put together as
 * <code>/{path to tiles in jar}/(zoom level)_(tile x)_(tile y).png</code>
 */
public class JaredOpenStreetMap extends EPSG3785 implements GeoMap, UnstreamedMap {
  private final String tilesLocationPath;

  /**
   * Create map with tiles located at jar root.
   * 
   * @param tileSize
   *          map tile size
   * @param minZoom
   *          minimum zoom for the map
   * @param maxZoom
   *          maximum zoom for the map
   */
  public JaredOpenStreetMap(final int tileSize, final int minZoom, final int maxZoom) {
    this("/", tileSize, minZoom, maxZoom);
  }

  /**
   * Create map with tiles in defined path. For example:
   * <code>/resources/map_tiles/</code>.
   * 
   * @param tilesLocationPath
   *          path to directory for files
   * @param tileSize
   *          map tile size
   * @param minZoom
   *          minimum zoom for the map
   * @param maxZoom
   *          maximum zoom for the map
   */
  public JaredOpenStreetMap(final String tilesLocationPath, final int tileSize, final int minZoom,
      final int maxZoom) {
    this(new StringCopyright("OpenStreetMap"), tilesLocationPath, tileSize, minZoom, maxZoom);
  }

  public JaredOpenStreetMap(final Copyright copyright, final String tilesLocationPath,
      final int tileSize, final int minZoom, final int maxZoom) {
    super(copyright, tileSize, minZoom, maxZoom);
    this.tilesLocationPath = tilesLocationPath;
  }

  public String buildPath(final int mapX, final int mapY, final int zoom) {
    final StringBuffer result = new StringBuffer(tilesLocationPath);
    result.append(zoom);
    result.append('_');
    result.append((mapX / getTileSize()) & ((1 << zoom) - 1));
    result.append('_');
    result.append(mapY / getTileSize());
    result.append(".png");
    return result.toString();
  }
}
