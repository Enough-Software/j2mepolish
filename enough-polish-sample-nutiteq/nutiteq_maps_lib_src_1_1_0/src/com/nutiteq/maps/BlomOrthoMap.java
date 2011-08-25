package com.nutiteq.maps;

import com.nutiteq.maps.projections.EPSG3785;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;

public class BlomOrthoMap extends EPSG3785 implements UnstreamedMap {
  private static final String BASEURL = "http://www.blomurbex.com/v02/GetTile";
  private final String licenseKey;
  private final String layer;

  public static final String ORTHO = "ORTHO";
  public static final String NORTH = "NORTH";
  public static final String WEST = "WEST";
  public static final String EAST = "EAST";
  public static final String SOUTH = "SOUTH";

  public BlomOrthoMap(final String licenseKey, final String layer) {
    this(new StringCopyright("BLOM"), licenseKey, layer);
  }

  public BlomOrthoMap(final Copyright copyright, final String licenseKey, final String layer) {
    super(copyright, 256, 5, 20);
    this.licenseKey = licenseKey;
    this.layer = layer;
  }

  public String buildPath(final int mapX, final int mapY, final int zoom) {
    final int tmpX = mapX / 256;
    final int tmpY = mapY / 256;

    final StringBuffer url = new StringBuffer(BASEURL);
    url.append("?USERTOKEN=");
    url.append(licenseKey);
    url.append("&SRS=EPSG%3A3785&LAYER=");
    url.append(layer);
    url.append("&ID=");

    for (int i = zoom - 1; i >= 0; i--) {
      url.append((((tmpY >> i) & 1) << 1) + ((tmpX >> i) & 1));
    }

    return url.toString();
  }
}
