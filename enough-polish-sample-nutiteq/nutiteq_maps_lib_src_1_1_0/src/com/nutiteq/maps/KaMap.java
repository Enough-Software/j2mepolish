package com.nutiteq.maps;

import com.nutiteq.utils.Utils;

/**
 * Map type to be used with standard <a
 * href="http://trac.openlayers.org/wiki/Layer/KaMap">OpenLayers kaMap layer</a>
 */
//TODO jaanus : describe min/max zoom mapping and what to do if some values removed
public class KaMap extends BaseKaMap implements UnstreamedMap {
  private final String tileUrlPattern;

  /**
   * 
   * @param baseurl
   *          URL of tile.php
   * @param copyright
   *          Map copyright string displayed on screen
   * @param group
   *          displayed layer group
   * @param imageFormat
   *          tile image format
   * @param layerName
   *          Layer name
   * @param tileSize
   *          map tile size
   * @param scales
   *          used scales
   * @param minZoom
   *          map min zoom
   * @param maxZoom
   *          map max zoom
   */
  public KaMap(final String baseurl, final String copyright, final String group,
      final String imageFormat, final String layerName, final int tileSize, final int scales[],
      final int minZoom, final int maxZoom) {
    super(copyright, tileSize, scales, minZoom, maxZoom);

    final StringBuffer urlBuffer = new StringBuffer(Utils.prepareForParameters(baseurl));

    urlBuffer.append("g=").append(group);
    urlBuffer.append("&i=").append(imageFormat);
    urlBuffer.append("&map=").append(layerName);

    tileUrlPattern = urlBuffer.toString();
  }

  public String buildPath(final int mapX, final int mapY, final int zoom) {
    final int zoomIndex = zoom - getMinZoom();
    final StringBuffer result = new StringBuffer(tileUrlPattern);
    result.append("&t=").append(mapY - tileMapHeight[zoomIndex] / 2);
    result.append("&l=").append(mapX - tileMapWidth[zoomIndex] / 2);
    result.append("&s=").append(scales[zoomIndex]);
    return result.toString();
  }
}
