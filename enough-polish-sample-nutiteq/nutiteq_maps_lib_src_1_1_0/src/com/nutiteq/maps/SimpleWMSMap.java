package com.nutiteq.maps;

import com.mgmaps.utils.Tools;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.maps.projections.EPSG4326;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;
import com.nutiteq.utils.Utils;

/**
 * Simple WMS map implementation using EPSG4326 projection for WMS version
 * 1.1.1. Creates a WMS request for every needed tile.
 */
//TODO jaanus : explain widthHeightRatio with images!
public class SimpleWMSMap extends EPSG4326 implements GeoMap, UnstreamedMap {
  private final String baseurl;

  /**
   * Constructor for the simple WMS implementation
   * 
   * @param baseurl
   *          base URL for the service
   * @param tileSize
   *          map tile size
   * @param minZoom
   *          minimum zoom for the map
   * @param maxZoom
   *          maximum zoom for the map
   * @param layer
   *          LAYERS parameter
   * @param format
   *          FORMAT parameter
   * @param style
   *          STYLE parameter
   * @param request
   *          REQUEST parameter
   * @param copyright
   *          copyright string displayed on map
   */
  public SimpleWMSMap(final String baseurl, final int tileSize, final int minZoom,
      final int maxZoom, final String layer, final String format, final String style,
      final String request, final String copyright) {
    this(baseurl, tileSize, minZoom, maxZoom, layer, format, style, request, new StringCopyright(
        copyright));
  }

  public SimpleWMSMap(final String baseurl, final int tileSize, final int minZoom,
      final int maxZoom, final String layer, final String format, final String style,
      final String request, final Copyright copyright) {
    super(copyright, tileSize, minZoom, maxZoom);
    final StringBuffer base = new StringBuffer(Utils.prepareForParameters(baseurl));
    base.append("LAYERS=").append(Tools.urlEncode(layer));
    base.append("&FORMAT=").append(Tools.urlEncode(format));
    base.append("&SERVICE=WMS&VERSION=1.1.1");
    base.append("&REQUEST=").append(Tools.urlEncode(request));
    base.append("&STYLES=").append(Tools.urlEncode(style));
    base.append("&EXCEPTIONS=").append(Tools.urlEncode("application/vnd.ogc.se_inimage"));
    base.append("&SRS=EPSG%3A4326&BBOX=");
    this.baseurl = base.toString();
  }

  public String buildPath(final int mapX, final int mapY, final int zoom) {
    final StringBuffer result = new StringBuffer(baseurl);

    final MapPos minPos = new MapPos(mapX, mapY + getTileSize(), zoom);
    final MapPos maxPos = new MapPos(mapX + getTileSize(), mapY, zoom);
    final WgsPoint minWgs = mapPosToWgs(minPos).toWgsPoint();
    final WgsPoint maxWgs = mapPosToWgs(maxPos).toWgsPoint();

    result.append(minWgs.getLon()).append(",").append(minWgs.getLat()).append(",");
    result.append(maxWgs.getLon()).append(",").append(maxWgs.getLat());

    result.append("&WIDTH=").append(getTileSize()).append("&HEIGHT=").append(getTileSize());
    return result.toString();
  }
}
