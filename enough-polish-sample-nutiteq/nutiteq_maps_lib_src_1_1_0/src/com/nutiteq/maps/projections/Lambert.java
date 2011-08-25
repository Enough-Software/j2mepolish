package com.nutiteq.maps.projections;

import henson.midp.Float11;

import com.nutiteq.components.Point;
import com.nutiteq.maps.BaseMap;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;

public abstract class Lambert extends BaseMap implements Projection {
  private static double n = 0.85417585805D; // pre-calculated value
  private static final double F = 1.7988478514D; // pre-calculated value
  private static double p0 = 4020205.4790000D; // pre-calculated value

  // Coordinates of Origin
  private static final double FALSE_NORTHING = 6375000.0000000D; // False Northing
  private static final double FALSE_EASTING = 500000.0000000D; // False Easting

  private final Ellipsoid ellipsoid;

  public Lambert(final Ellipsoid ellipsoid, final Copyright copyright, final int tileSize,
      final int minZoom, final int maxZoom) {
    super(copyright, tileSize, minZoom, maxZoom);
    this.ellipsoid = ellipsoid;
  }

  public Lambert(final Ellipsoid ellipsoid, final String copyright, final int tileSize,
      final int minZoom, final int maxZoom) {
    this(ellipsoid, new StringCopyright(copyright), tileSize, minZoom, maxZoom);
  }

  public Point fromWgs(final Point from) {
    final double L = Math.toRadians(from.getX() / 1000000D);
    final double B = Math.toRadians(from.getY() / 1000000D);

    final double Lo = 24 * Math.PI / 180;

    final double t = Math.sqrt((1 - Math.sin(B))
        / (1 + Math.sin(B))
        * Float11.pow((1 + ellipsoid.getEccentricity() * Math.sin(B))
            / (1 - ellipsoid.getEccentricity() * Math.sin(B)), ellipsoid.getEccentricity()));

    final double theta = n * (L - Lo);
    final double p = ellipsoid.getEquatorialRadius() * F * Float11.pow(t, n);

    final double x = p * Math.sin(theta) + FALSE_EASTING;
    final double y = p0 - p * Math.cos(theta) + FALSE_NORTHING;
    return new Point((int) x, (int) y);
  }

  public Point toWgs(final Point from) {
    final double Lo = 24 * Math.PI / 180;
    double ux = from.getX() - FALSE_EASTING;
    double uy = from.getY() - FALSE_NORTHING;
    final double sx = ux;
    ux = uy;
    uy = sx;

    final double theta = Float11.atan(uy / (p0 - ux));
    final double tmpL = theta / n + Lo;

    double p = p0 - ux;
    p *= p;
    uy *= uy;
    p += uy;
    p = Math.sqrt(p);

    final double t = Float11.pow(p / (ellipsoid.getEquatorialRadius() * F), 1 / n);

    final double u = Math.PI / 2D - 2D * Float11.atan(t);

    //TODO jaanus : some of these calculations could be precalculated?
    final double tmpB = u
        + (Float11.pow(ellipsoid.getEccentricity(), 2) / 2 + 5
            * Float11.pow(ellipsoid.getEccentricity(), 2)
            * Float11.pow(ellipsoid.getEccentricity(), 2) / 24
            + Float11.pow(ellipsoid.getEccentricity(), 6) / 12 + 13 * Float11.pow(ellipsoid
            .getEccentricity(), 8) / 360)
        * Math.sin(2 * u)
        + (7 * Float11.pow(ellipsoid.getEccentricity(), 4) / 48 + 29
            * Float11.pow(ellipsoid.getEccentricity(), 6) / 240 + 811 * Float11.pow(ellipsoid
            .getEccentricity(), 8) / 11520)
        * Math.sin(4 * u)
        + (7 * Float11.pow(ellipsoid.getEccentricity(), 6) / 120 + 81 * Float11.pow(ellipsoid
            .getEccentricity(), 8) / 1120) * Math.sin(6 * u);

    return new Point((int) (Math.toDegrees(tmpL) * 1000000), (int) (Math.toDegrees(tmpB) * 1000000));
  }
}
