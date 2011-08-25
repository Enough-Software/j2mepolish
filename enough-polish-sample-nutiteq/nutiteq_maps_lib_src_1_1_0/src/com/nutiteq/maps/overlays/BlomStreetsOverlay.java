package com.nutiteq.maps.overlays;

import com.nutiteq.components.MapTile;
import com.nutiteq.maps.MapTileOverlay;

public class BlomStreetsOverlay implements MapTileOverlay {
  private static final String BASEURL = "http://www.blomurbex.com/v02/GetTile";
  private final String key;

  public BlomStreetsOverlay(final String key) {
    this.key = key;
  }

  public String getOverlayTileUrl(final MapTile tile) {
    final int x = tile.getX() / 256;
    final int y = tile.getY() / 256;
    final int zoom = tile.getZoom();

    final StringBuffer url = new StringBuffer(BASEURL);
    url.append("?USERTOKEN=");
    url.append(key);
    url.append("&SRS=EPSG%3A3785&LAYER=");
    url.append("ORTHO");
    url.append("&ID=");
    for (int i = zoom - 1; i >= 0; i--) {
      url.append((((y >> i) & 1) << 1) + ((x >> i) & 1));
    }
    url.append("&TRANSPARENT=true&OVERLAY=generic");

    return url.toString();
  }
}