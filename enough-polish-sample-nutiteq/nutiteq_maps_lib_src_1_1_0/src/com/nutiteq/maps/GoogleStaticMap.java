package com.nutiteq.maps;

import com.nutiteq.components.MapPos;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.maps.projections.EPSG3785;
import com.nutiteq.ui.StringCopyright;

public class GoogleStaticMap extends EPSG3785 implements GeoMap, UnstreamedMap {
  private static final String BASEURL = "http://maps.google.com/staticmap?";

  public static final String IMAGE_FORMAT_GIF = "gif";
  public static final String IMAGE_FORMAT_JPEG = "jpg-baseline";
  public static final String IMAGE_FORMAT_PNG_8 = "png8";
  public static final String IMAGE_FORMAT_PNG_32 = "png32";
  public static final String MAP_TYPE_ROADMAP = "roadmap";
  public static final String MAP_TYPE_MOBILE = "mobile";
  public static final String MAP_TYPE_SATELLITE = "satellite";
  public static final String MAP_TYPE_TERRAIN = "terrain";
  public static final String MAP_TYPE_HYBRID = "hybrid";

  private final String developerKey;
  private final String imageFormat;
  private final String mapType;
  private final boolean sensor;

  /**
   * Create new GoogleStaticMap to be displayed.
   * 
   * @param developerKey
   *          identifies the Maps API key
   * @param tileSize
   *          size of tiles requested. Currently only 256 is supported
   * @param minZoom
   *          min zoom of map
   * @param maxZoom
   *          max zoom of map
   * @param imageFormat
   *          defines the format of the resulting image. There are several
   *          possible formats including GIF, JPEG and PNG types. Which format
   *          you use depends on how you intend to present the image. JPEG
   *          typically provides greater compression, while GIF and PNG provide
   *          greater detail
   * @param mapType
   *          defines the type of map to construct. There are several possible
   *          maptype values, including satellite, terrain, hybrid, and mobile
   * @param sensor
   *          specifies whether the application requesting the static map is
   *          using a sensor to determine the user's location. This parameter is
   *          now required for all static map requests.
   */
  public GoogleStaticMap(final String developerKey, final int tileSize, final int minZoom,
      final int maxZoom, final String imageFormat, final String mapType, final boolean sensor) {
    super(new StringCopyright(""), tileSize, minZoom, maxZoom);
    this.developerKey = developerKey;
    this.imageFormat = imageFormat;
    this.mapType = mapType;
    this.sensor = sensor;
  }

  public String buildPath(final int mapX, final int mapY, final int zoom) {
    final StringBuffer url = new StringBuffer(BASEURL);
    final MapPos tileMiddle = new MapPos(mapX + getTileSize() / 2, mapY + getTileSize() / 2, zoom);
    final WgsPoint middleWgs = mapPosToWgs(tileMiddle).toWgsPoint();
    url.append("center=").append(middleWgs.getLat()).append(",").append(middleWgs.getLon());
    url.append("&format=").append(imageFormat);
    url.append("&zoom=").append(zoom);
    url.append("&size=").append(getTileSize()).append("x").append(getTileSize());
    url.append("&maptype=").append(mapType);
    url.append("&key=").append(developerKey);
    url.append("&sensor=").append(sensor);
    return url.toString();
  }
}
