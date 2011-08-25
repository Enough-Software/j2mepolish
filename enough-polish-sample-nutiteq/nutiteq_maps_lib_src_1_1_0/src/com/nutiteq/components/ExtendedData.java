package com.nutiteq.components;

/**
 * This class implements a point on a map, identified by WGS84 coordinates,
 * longitude (x) and latitude (y).
 */
public class ExtendedData {
  private final String name; 
  private final String value; 

  /**
   * Constructor for Point.
   */
  public ExtendedData(final String name, final String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public static String toString(final ExtendedData p) {
    return p == null ? "" : "name = "+ p.name + " value = "+p.value;
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

  public int hashCode() {
    throw new RuntimeException("hashCode() not implemented");
  }
}
