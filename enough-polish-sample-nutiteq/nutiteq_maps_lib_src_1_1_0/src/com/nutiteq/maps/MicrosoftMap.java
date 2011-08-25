/*
 * Created on Aug 14, 2008
 */
package com.nutiteq.maps;

import com.nutiteq.maps.projections.EPSG3785;
import com.nutiteq.ui.StringCopyright;

public class MicrosoftMap extends EPSG3785 implements GeoMap, UnstreamedMap {

  private int mt;

  public static final MicrosoftMap LIVE_MAP = new MicrosoftMap(0);

  public MicrosoftMap(final int mt) {
    super(new StringCopyright("Map data © Microsoft"), 256, 1, 17);
  }

  public String buildPath(final int x, final int y, final int zoom) {
    final int tmpX = x >> 8;
    final int tmpY = y >> 8;
    final StringBuffer buf = new StringBuffer();
    switch (mt) {
    case 0:
      buf.append("http://r");
      buf.append(((tmpY & 1) << 1) + (tmpX & 1));
      buf.append(".ortho.tiles.virtualearth.net/tiles/r");
      for (int i = zoom - 1; i >= 0; i--) {
        buf.append((((tmpY >> i) & 1) << 1) + ((tmpX >> i) & 1));
      }
      buf.append(".png?g=373&shading=hill");
      break;
    }
    return buf.toString();
  }
}
