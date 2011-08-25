package com.nutiteq.maps;

import com.nutiteq.components.MapTile;
import com.nutiteq.maps.projections.EPSG3785;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.CustomNTCopyright;
import com.nutiteq.ui.StringCopyright;
import com.nutiteq.utils.Utils;

/**
 * Streamed maps based on Nutiteq map tiles streaming server.
 */
public class NutiteqStreamedMap extends EPSG3785 implements GeoMap, StreamedMap {

  private final String baseUrl;
  private static final String NUTITEQ_MAPSERVER = "http://aws.nutiteq.com/mapstream.php?ts=128&";
  public static final NutiteqStreamedMap OPENSTREETMAP = new NutiteqStreamedMap(NUTITEQ_MAPSERVER, new CustomNTCopyright(),
      128, 0, 19);

  public NutiteqStreamedMap(final String baseUrl, final String copyright, final int tileSize,
      final int minZoom, final int maxZoom) {
    this(baseUrl, new StringCopyright(copyright), tileSize, minZoom, maxZoom);
  }

  public NutiteqStreamedMap(final String baseUrl, final Copyright copyright, final int tileSize,
      final int minZoom, final int maxZoom) {
    super(copyright, tileSize, minZoom, maxZoom);
    this.baseUrl = Utils.prepareForParameters(baseUrl);
  }

  public String buildStreamedPath(final MapTile[] tiles) {
    final StringBuffer urlBuf = new StringBuffer(baseUrl);
    final MapTile firstTile = tiles[0];
    urlBuf.append("z=");
    urlBuf.append(firstTile.getZoom());
    urlBuf.append("&ver=2");
    urlBuf.append("&t=");
    urlBuf.append(firstTile.getX() / getTileSize());
    urlBuf.append(',');
    urlBuf.append(firstTile.getY() / getTileSize());
    for (int i = 1; i < tiles.length; i++) {
      final MapTile obj = tiles[i];
      urlBuf.append(',');
      urlBuf.append(obj.getX() / getTileSize());
      urlBuf.append(',');
      urlBuf.append(obj.getY() / getTileSize());
    }
    return urlBuf.toString();
  }
}
