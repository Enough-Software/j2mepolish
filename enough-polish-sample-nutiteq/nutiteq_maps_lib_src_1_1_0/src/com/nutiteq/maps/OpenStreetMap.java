package com.nutiteq.maps;

import com.nutiteq.maps.projections.EPSG3785;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;

/**
 * Default implementation using Open Street Map tiles server.
 */
public class OpenStreetMap extends EPSG3785 implements GeoMap, UnstreamedMap {
  private static final String OSM_MAPNIK_URL = "http://tile.openstreetmap.org/";

  public static final int MIN_ZOOM = 0;
  public static final int MAX_ZOOM = 17;

  public static final int TILE_SIZE = 256;

  /**
   * Instance if Open Street Map
   */
  public static final OpenStreetMap MAPNIK = new OpenStreetMap(OSM_MAPNIK_URL, TILE_SIZE, MIN_ZOOM,
      MAX_ZOOM);

  private final String baseUrl;

  public OpenStreetMap(final String baseUrl, final int tileSize, final int minZoom,
      final int maxZoom) {
    this(new StringCopyright("OpenStreetMap"), baseUrl, tileSize, minZoom, maxZoom);
  }

  public OpenStreetMap(final Copyright copyright, final String baseUrl, final int tileSize,
      final int minZoom, final int maxZoom) {
    super(copyright, tileSize, minZoom, maxZoom);
    this.baseUrl = baseUrl;
  }

  public String buildPath(final int mapX, final int mapY, final int zoom) {
    final StringBuffer result = new StringBuffer();

    result.append(baseUrl);
    result.append(zoom);
    result.append('/');
    result.append((mapX / getTileSize()) & ((1 << zoom) - 1));
    result.append('/');
    result.append(mapY / getTileSize());
    result.append(".png");
    return result.toString();
  }
}
