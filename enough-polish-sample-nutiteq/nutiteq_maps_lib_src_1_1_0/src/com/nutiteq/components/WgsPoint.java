package com.nutiteq.components;

import henson.midp.Float11;

/**
 * Point object for moving WGS84 coordinates between library and implementing
 * application
 */
public class WgsPoint {
  public static final int FORMAT_LAT_LON = 1;

  private final double lon;
  private final double lat;

  // "average" radius of the earth
  private static final double EARTH_RADIUS = 6367454D;

  public WgsPoint(final double lon, final double lat) {
    this.lon = lon;
    this.lat = lat;
  }

  /**
   * Get coordinate latitude
   * 
   * @return latitude
   */
  public double getLat() {
    return lat;
  }

  /**
   * Get coordinate longitude
   * 
   * @return longitude
   */
  public double getLon() {
    return lon;
  }

  public Point toInternalWgs() {
    return new Point((int) (lon * 1000000D), (int) (lat * 1000000D));
  }

  public String toString() {
    return new StringBuffer().append(lon).append("E ").append(lat).append("N").toString();
  }

  /**
   * Check if two WgsPoint objects refer to the same coordinates.
   * 
   * @param obj
   *          the other object
   * @return true if the coordinates are the same, false otherwise
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || !(obj instanceof WgsPoint)) {
      return false;
    }

    final WgsPoint p = (WgsPoint) obj;
    return lat == p.getLat() && lon == p.getLon();
  }

  public int hashCode() {
    throw new RuntimeException("hashCode() not implemented!");
  }

  /**
   * Compute distance in meters between two points.
   * 
   * @param p1
   *          first point
   * @param p2
   *          second point
   * @return distance in meters
   */
  public static double distanceInMeters(final WgsPoint p1, final WgsPoint p2) {
    final double p1latr = Math.toRadians(p1.lat);
    final double p2latr = Math.toRadians(p2.lat);
    final double dlat = (p2latr - p1latr) / 2;
    final double dlon = Math.toRadians(p2.lon - p1.lon) / 2;
    final double s1 = Math.sin(dlat);
    final double s2 = Math.sin(dlon);
    final double h = s1 * s1 + Math.cos(p1latr) * Math.cos(p2latr) * s2 * s2;
    final double dist = 2 * EARTH_RADIUS * Float11.asin(Math.sqrt(h));

    return dist;
  }
  
  /**
   * True course at p1 using great circle distance.
   * @param p1 first point
   * @param p2 second point
   * @return course in degrees as a number between 0 and 360
   */
  public static double initialTrueCourse(final WgsPoint p1, final WgsPoint p2) {
    final double dlon = Math.toRadians(p2.lon-p1.lon);
    final double p1latr = Math.toRadians(p1.lat);
    final double p2latr = Math.toRadians(p2.lat);
    
    // check for poles
    final double c1 = Math.cos(p1latr);
    if (c1 < 0.00001) {
      return (p1latr > 0) ? 180 : 0;
    }

    final double c2 = Math.cos(p2latr);
    final double at1 = Math.sin(dlon)*c2;
    final double at2 = c1*Math.sin(p2latr)-Math.sin(p1latr)*c2*Math.cos(dlon);
    final double at = Float11.atan2(at1, at2);
    final double atd = Math.toDegrees(at);
    
    if (atd < 0) {
      return atd + 360;
    } else if (atd >= 360) {
      return atd - 360;
    } else {
      return atd;
    }
  }

  /**
   * True course at p2 using great circle distance.
   * final_true_course(p1,p2) = initial_true_course(p2,p1)-180.
   * @param p1 first point
   * @param p2 second point
   * @return course in degrees as a number between 0 and 360
   */
  public static double finalTrueCourse(final WgsPoint p1, final WgsPoint p2) {
    final double crs = initialTrueCourse(p2, p1) + 180;
    if (crs >= 360) {
      return crs - 360;
    } else {
      return crs;
    }
  }

  public static WgsPoint parsePoint(final int format, final String string, final String separator) {
    final double one = Double.parseDouble(string.substring(0, string.indexOf(separator)).trim());
    final double two = Double.parseDouble(string.substring(string.indexOf(separator) + 1).trim());
    return format == FORMAT_LAT_LON ? new WgsPoint(two, one) : new WgsPoint(one, two);
  }
}
