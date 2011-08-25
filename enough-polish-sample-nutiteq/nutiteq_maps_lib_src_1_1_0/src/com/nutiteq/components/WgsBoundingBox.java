package com.nutiteq.components;

/**
 * Bounding box containing corner coordinates in WGS84.
 */
public class WgsBoundingBox {
  private final WgsPoint wgsMin;
  private final WgsPoint wgsMax;

  public WgsBoundingBox(final WgsPoint wgsMin, final WgsPoint wgsMax) {
    this.wgsMin = wgsMin;
    this.wgsMax = wgsMax;
  }

  public WgsBoundingBox(final double wgsMinLon, final double wgsMinLat, final double wgsMaxLon,
      final double wgsMaxLat) {
    this(new WgsPoint(wgsMinLon, wgsMinLat), new WgsPoint(wgsMaxLon, wgsMaxLat));
  }

  public WgsPoint getWgsMin() {
    return wgsMin;
  }

  public WgsPoint getWgsMax() {
    return wgsMax;
  }

  /**
   * Calculate approximate center for this bounding box
   * 
   * @return center point for bounding box in WGS84
   */
  public WgsPoint getBoundingBoxCenter() {
    return new WgsPoint((wgsMin.getLon() + wgsMax.getLon()) / 2,
        (wgsMin.getLat() + wgsMax.getLat()) / 2);
  }

  public String toString() {
    return new StringBuffer("BBox min: ").append(wgsMin.toString()).append(" max: ").append(
        wgsMax.toString()).toString();
  }
}
