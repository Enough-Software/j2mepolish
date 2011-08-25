package com.nutiteq.maps.projections;

public class Ellipsoid {
  private final double equatorialRadius;
  private final double polarRadius;
  private final double eccentricity;
  private final double inverseFlattening;

  /**
   * GRS80 ellipsoid
   */
  public static final Ellipsoid GRS80 = new Ellipsoid(6378137.0, 6356752.31414);
  // E = 0.081819191043495395466605394631969
  public static final Ellipsoid GRS80_2 = new Ellipsoid(6378137.0, 6356752.3141);
  // E = 0.081819191119888192709998327707243

  /**
   * WGS84 ellipsoid
   */
  public static final Ellipsoid WGS84 = new Ellipsoid(6378137.0, 6356752.314245);
  // E = 0.081819190842964302361054726967476
  public static final Ellipsoid WGS84_2 = new Ellipsoid(6378137.0, 6356752.3142);
  // E = 0.081819190928906199466877879557762
  public static final Ellipsoid WGS84_3 = new Ellipsoid(6378137.0, 6356752.314245179);
  // E = 0.081819190842622444592385648655019
  
  public static final Ellipsoid WGRS8084 = new Ellipsoid(6378137.0, 6356752.3);
  // E = 0.081819218048344747117551469344841
  
  /**
   * Krasovsky ellipsoid
   */
  public static final Ellipsoid KRASOVSKY = new Ellipsoid(6378245.0, 6356863.0);
  // E = 0.081813369872039993164418410983635

  /**
   * create a new ellipsoid and precompute its parameters
   * 
   * @param equatorialRadius
   *          ellipsoid long axis (in meters)
   * @param polarRadius
   *          ellipsoid short axis (in meters)
   */
  public Ellipsoid(final double equatorialRadius, final double polarRadius) {
    this.equatorialRadius = equatorialRadius;
    this.polarRadius = polarRadius;
    final double e2 = (equatorialRadius * equatorialRadius - polarRadius * polarRadius)
        / (equatorialRadius * equatorialRadius);
    eccentricity = Math.sqrt(e2);
    inverseFlattening = equatorialRadius / (equatorialRadius - polarRadius);
  }

  public double getEquatorialRadius() {
    return equatorialRadius;
  }

  public double getPolarRadius() {
    return polarRadius;
  }

  public double getEccentricity() {
    return eccentricity;
  }
  
  public double getInverseFlattening() {
    return inverseFlattening;
  }
}
