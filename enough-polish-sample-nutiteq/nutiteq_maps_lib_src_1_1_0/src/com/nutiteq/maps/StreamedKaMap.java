package com.nutiteq.maps;

import com.nutiteq.components.MapTile;
import com.nutiteq.utils.Utils;

public class StreamedKaMap extends BaseKaMap implements StreamedMap {
  private final String baseurl;

  public StreamedKaMap(final String baseurl, final String copyright, final int tileSize,
      final int[] scales, final int minZoom, final int maxZoom) {
    super(copyright, tileSize, scales, minZoom, maxZoom);
    this.baseurl = Utils.prepareForParameters(baseurl);
  }

  public String buildStreamedPath(final MapTile[] tiles) {
    final StringBuffer urlBuf = new StringBuffer(baseurl);
    final MapTile firstTile = tiles[0];

    final int zoomIndex = firstTile.getZoom() - getMinZoom();
    final int halfMapHeight = tileMapHeight[zoomIndex] / 2;
    final int halfMapWidth = tileMapWidth[zoomIndex] / 2;

    urlBuf.append("z=");
    urlBuf.append(scales[zoomIndex]);
    urlBuf.append("&t=");
    urlBuf.append(firstTile.getX() - halfMapWidth);
    urlBuf.append(',');
    urlBuf.append(firstTile.getY() - halfMapHeight);
    for (int i = 1; i < tiles.length; i++) {
      final MapTile obj = tiles[i];
      urlBuf.append(',');
      urlBuf.append(obj.getX() - halfMapWidth);
      urlBuf.append(',');
      urlBuf.append(obj.getY() - halfMapHeight);
    }
    return urlBuf.toString();
  }
}
