package com.nutiteq.maps.projections;

import henson.midp.Float11;

import com.nutiteq.components.MapPos;
import com.nutiteq.components.Point;
import com.nutiteq.maps.BaseMap;
import com.nutiteq.ui.Copyright;
import com.nutiteq.utils.Utils;

/**
 * Abstract class for doing WGS84 coordinates calculations to map pixels in
 * EPSG3785 (Spherical Mercator projection) and back.
 */
public abstract class EPSG3785 extends BaseMap implements Projection {
  public EPSG3785(final Copyright copyright, final int tileSize, final int minZoom,
      final int maxZoom) {
    super(copyright, tileSize, minZoom, maxZoom);
  }

  public EPSG3785(final String copyright, final int tileSize, final int minZoom, final int maxZoom) {
    super(copyright, tileSize, minZoom, maxZoom);
  }

  public Point mapPosToWgs(final MapPos pos) {
    final int tileSizeLog = Utils.log2(getTileSize());
    final int shift = pos.getZoom() + tileSizeLog - 1;
    final int mapSize = 1 << (pos.getZoom() + tileSizeLog);
    final int tmpX = pos.getX() & (mapSize - 1);

    // x
    final double tx = ((double) tmpX) / (1 << shift) - 1D;
    final int lon = (int) (tx * 180000000D);

    // y
    double ty = ((double) pos.getY()) / (1 << shift) - 1D;
    ty *= Math.PI;
    final double ey = Float11.toDegrees(Float11.atan(Float11.exp(ty)));
    final int lat = (int) ((ey * 2000000D) - 90000000D);

    return new Point(lon, -lat);
  }

  public MapPos wgsToMapPos(final Point wgs, final int zoom) {
    final int tileSizeLog = Utils.log2(getTileSize());
    final int shift = zoom + tileSizeLog - 1;

    final int pX = wgs.getX();
    int pY = wgs.getY();

    // x
    final double x = pX / 180000000D + 1D;
    final int cx = (int) (x * (1 << shift));

    // y
    if (pY > 85051128) {
      pY = 85051128;
    }

    if (pY < -85051128) {
      pY = -85051128;
    }

    final double sinf = Math.sin(Float11.toRadians(-pY / 1000000D));
    final double y = Float11.log((1D + sinf) / (1D - sinf)) / (Math.PI * 2D) + 1D;
    final int cy = (int) (y * (1 << shift));

    final int bit = 1 << zoom + tileSizeLog;
    final int tmpX = cx & (bit - 1);
    return new MapPos(tmpX, cy, zoom);
  }
}
