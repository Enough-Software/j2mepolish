package com.nutiteq.components;

import com.mgmaps.utils.Tools;

/**
 * This class implements a point on a map, identified by WGS84 coordinates,
 * longitude (x) and latitude (y). Both values are converted to int by multiplying with 1000000 
 */
public class Point {
  private final int x; // lon
  private final int y; // lat

  /**
   * Constructor for Point.
   */
  public Point(final int x, final int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  /**
   * Not part of public API
   * 
   * @return point as double wgs
   */
  public WgsPoint toWgsPoint() {
    return new WgsPoint((double) x / 1000000, (double) y / 1000000);
  }

  /**
   * Convert a point to a string.
   * 
   * @param p
   *          point to convert
   * @return the string "y.yyyyyyN, x.xxxxxxE"
   */
  public static String toString(final Point p) {
    return p == null ? "" : (Tools.formatCoord(p.y, true, false) + ", " + Tools.formatCoord(p.x,
        false, false));
  }

  /**
   * Non-static toString().
   * 
   * @return string representation
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return toString(this);
  }

  /**
   * Check for equality.
   * 
   * @param other
   *          other object to check against
   * @return true if equal, false otherwise
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(final Object other) {
    if (other == null || !(other instanceof Point)) {
      return false;
    }

    final Point p = (Point) other;
    return x == p.x && y == p.y;
  }

  public int hashCode() {
    throw new RuntimeException("hashCode() not implemented");
  }
}
