package com.nutiteq.maps;

import com.nutiteq.components.MapPos;
import com.nutiteq.components.Point;
import com.nutiteq.components.ZoomRange;
import com.nutiteq.maps.projections.EPSG3301;
import com.nutiteq.ui.StringCopyright;

public class Regio extends EPSG3301 implements GeoMap, UnstreamedMap {
  public static final int TYPE_PNG = 0;
  public static final int TYPE_ORTO = 1;
  public static final int TYPE_HYBRID = 2;

  public static final String[] MAP_TYPE_STRINGS = new String[] { "variant2png", "delfiorto",
      "hybrid" };

  public static final int TILE_EDGE_PX = 256;
  public static final int TILE_MIN_E = 365000;
  public static final int TILE_MIN_N = 6308000;
  public static final int TILE_MAX_E = 749000;
  public static final int TILE_MAX_N = 6692000;
  public static final int MIN_E = 365000;
  public static final int MIN_N = 6308000;
  public static final int MAX_E = 749000;
  public static final int MAX_N = 6692000;
  public static final int MIN_ZOOM = 0;
  public static final int MAX_ZOOM = 11;
  private static final ZoomRange Z_RANGE = new ZoomRange(MIN_ZOOM, MAX_ZOOM);
  public static final int MAP_WIDTH_HEIGHT_METERS = MAX_E - MIN_E;

  private final int mapType;
  private final String baseurl;
  private final String user;

  public Regio(final String baseurl, final String user) {
    this(TYPE_PNG, baseurl, user);
  }

  public Regio(final int type, final String baseurl, final String user) {
    super(new StringCopyright("Regio"), 256, MIN_ZOOM, MAX_ZOOM);
    mapType = type;
    this.baseurl = baseurl;
    this.user = user;
  }

  public Point mapPosToWgs(final MapPos pos) {
    return toWgs(mapPointToEastingNorthing(pos, pos.getZoom()));
  }

  private Point mapPointToEastingNorthing(final MapPos pos, final int zoom) {
    final int mapWidthHeightInPixels = getMapWidth(zoom);
    final float pixelInMeters = MAP_WIDTH_HEIGHT_METERS / (float) mapWidthHeightInPixels;
    final int easting = (int) (pos.getX() * pixelInMeters + MIN_E);
    final int northing = (int) (MAX_N - pos.getY() * pixelInMeters);
    return new Point(easting, northing);
  }

  public MapPos wgsToMapPos(final Point wgs, final int zoom) {
    //calculate easting/northing
    final Point enPoint = fromWgs(wgs);
    final int eastingFromMapZero = enPoint.getX() - MIN_E;
    final int northingFromMapZero = enPoint.getY() - MIN_N;

    final int mapWidthHeightInPixels = getMapWidth(zoom);
    final int pointX = (int) ((eastingFromMapZero / (float) MAP_WIDTH_HEIGHT_METERS) * mapWidthHeightInPixels);
    final int pointY = (int) (mapWidthHeightInPixels - ((northingFromMapZero / (float) MAP_WIDTH_HEIGHT_METERS) * mapWidthHeightInPixels));
    return new MapPos(pointX, pointY, zoom);
  }

  public String buildPath(final int mapX, final int mapY, final int zoom) {
    final int x = mapX / TILE_EDGE_PX;
    final int y = mapY / TILE_EDGE_PX;
    if (mapType == TYPE_HYBRID) {
      final StringBuffer result = new StringBuffer();
      result.append(getTileUrl(x, y, zoom, TYPE_ORTO));
      //TODO jaanus : download separator
      //result.append(Downloader.DOWNLOAD_SEPARATOR);
      result.append(getTileUrl(x, y, zoom, TYPE_HYBRID));
      return result.toString();
    } else {
      return getTileUrl(x, y, zoom, mapType).toString();
    }
  }

  private String getTileUrl(final int x, final int y, final int zoom, final int type) {
    final StringBuffer url = new StringBuffer(baseurl);
    url.append("?user=").append(user).append("&map=").append(MAP_TYPE_STRINGS[type]).append(
        "&tile=");

    //TODO check this
    url.append("2");

    for (int i = zoom - 1; i >= 0; i--) {
      //TODO jaanus : remove magic from here
      url.append((((y >> i) & 1) << 1) + ((x >> i) & 1));
    }

    return url.toString();
  }
}
