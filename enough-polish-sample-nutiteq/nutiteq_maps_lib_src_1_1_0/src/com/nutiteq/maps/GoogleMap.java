/*
 * Created on Aug 14, 2008
 */
package com.nutiteq.maps;

import com.nutiteq.maps.projections.EPSG3785;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;

public class GoogleMap extends EPSG3785 implements GeoMap, UnstreamedMap {

  private int mt;

  public static final GoogleMap GOOGLE_MAP = new GoogleMap(0);
  public static final GoogleMap GOOGLE_SAT = new GoogleMap(1);

  // used for conversion
  private static final char[] SAT_LETTER = { 'q', 'r', 't', 's' };

  public GoogleMap(final int mt) {
    this(new StringCopyright("Map data © Google"), mt);
  }

  public GoogleMap(final Copyright copyright, final int mt) {
    super(copyright, 256, 0, 22);
  }

  public String buildPath(final int x, final int y, final int zoom) {
    final int tmpX = x >> 8;
    final int tmpY = y >> 8;
    final StringBuffer buf = new StringBuffer();
    switch (mt) {
    case 0:
      buf.append("http://mt");
      buf.append((tmpX + tmpY) & 3);
      buf.append(".google.com/mt?v=nq.83&n=404&x=");
      // buf.append((zoom == 0) ? 0 : (tmpX&((1<<zoom)-1)));
      buf.append(tmpX);
      buf.append("&y=");
      // buf.append((zoom == 0) ? 0 : (tmpY&((1<<zoom)-1)));
      buf.append(tmpY);
      buf.append("&zoom=");
      buf.append(17 - zoom);
      break;

    case 1:
      buf.append("http://khm");
      buf.append((tmpX + tmpY) & 3);
      buf.append(".google.com/kh?v=99&n=404&t=t");
      for (int i = zoom - 1; i >= 0; i--) {
        buf.append(SAT_LETTER[(((tmpY >> i) & 1) << 1) + ((tmpX >> i) & 1)]);
      }
      break;
    }
    return buf.toString();
  }
}
