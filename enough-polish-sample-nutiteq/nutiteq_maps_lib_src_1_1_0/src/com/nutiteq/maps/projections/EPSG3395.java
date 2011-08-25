package com.nutiteq.maps.projections;

import henson.midp.Float11;

import com.nutiteq.components.MapPos;
import com.nutiteq.components.Point;
import com.nutiteq.maps.BaseMap;
import com.nutiteq.ui.Copyright;
import com.nutiteq.utils.Utils;

/**
 * Abstract class for doing WGS84 coordinates calculations to map pixels in
 * EPSG3395 (WGS84 Elliptical Mercator projection) and back.
 */
public abstract class EPSG3395 extends BaseMap implements Projection {
  private final double eccentricity;

  public EPSG3395(final Ellipsoid ellipsoid, final Copyright copyright, final int tileSize, final int minZoom,
      final int maxZoom) {
    super(copyright, tileSize, minZoom, maxZoom);
    eccentricity = ellipsoid.getEccentricity();
  }

  public EPSG3395(final Ellipsoid ellipsoid, final String copyright, final int tileSize, final int minZoom, final int maxZoom) {
    super(copyright, tileSize, minZoom, maxZoom);
    eccentricity = ellipsoid.getEccentricity();
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
    final double y = Float11.exp(ty * Math.PI);
    ty = Math.PI / 2.0 - Float11.atan(y) * 2.0;
    for (int i = 0; i < 4; i++) {
      final double sph = eccentricity * Math.sin(ty);
      ty = Math.PI / 2.0 - Float11.atan(y * Float11.pow((1.0 - sph) / (1.0 + sph), eccentricity / 2.0)) * 2.0;
    }
    final double ey = Float11.toDegrees(ty);
    final int lat = (int) (ey * 1000000D);

    return new Point(lon, lat);
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
    if (pY > 84000000) {
      pY = 84000000;
    }

    if (pY < -80000000) {
      pY = -80000000;
    }

    final double latrad = Float11.toRadians(-pY / 1000000.0D);
    final double sinf = Math.sin(latrad);
    final double lrs = eccentricity * sinf;
    final double y = Float11.log(Math.tan(Math.PI / 4.0 + latrad / 2.0)
        * Float11.pow((1.0 - lrs) / (1.0 + lrs), eccentricity / 2.0))
        / Math.PI + 1D;

    final int cy = (int) (y * (1 << shift));

    final int bit = 1 << zoom + tileSizeLog;
    final int tmpX = cx & (bit - 1);
    return new MapPos(tmpX, cy, zoom);
  }
}
