package com.nutiteq.components;

import com.nutiteq.utils.Utils;

/**
 * Defines map bounds on pixel map. Definition is done by valid minimum and
 * maximum points on map. For example for a map, that has world at zoom level 0
 * in 256x256 tile, map bounds on zoom 0 would be min = (0, 0) and max = (255,
 * 255)
 */
public class TileMapBounds {
  private final MapPos minPoint;
  private final MapPos maxPoint;

  /**
   * Create tile map bounds.
   * 
   * @param minPoint
   *          minimum valid point on map
   * @param maxPoint
   *          maximum valid point on map
   */
  public TileMapBounds(final MapPos minPoint, final MapPos maxPoint) {
    this.minPoint = minPoint;
    this.maxPoint = maxPoint;
  }

  /**
   * Is given position on tile map within map bounds
   * 
   * @param mapX
   *          position x
   * @param mapY
   *          position y
   * @return if given point is within map bounds
   */
  public boolean isWithinBounds(final int mapX, final int mapY) {
    return mapX >= minPoint.getX() && mapX <= maxPoint.getX() && mapY >= minPoint.getY()
        && mapY <= maxPoint.getY();
  }

  /**
   * Calculate map position correction for it to be within map bounds.
   * 
   * @param middlePoint
   *          point for witch to get the correction
   * @return correction needed on x/y axis
   */
  public MapPos calculateCorrection(final MapPos middlePoint) {
    int correctionX = 0;
    int correctionY = 0;
    if (middlePoint.getX() < minPoint.getX()) {
      correctionX = middlePoint.getX() - minPoint.getX();
    } else if (middlePoint.getX() > maxPoint.getX()) {
      correctionX = middlePoint.getX() - maxPoint.getX();
    }

    if (middlePoint.getY() < minPoint.getY()) {
      correctionY = middlePoint.getY() - minPoint.getY();
    } else if (middlePoint.getY() > maxPoint.getY()) {
      correctionY = middlePoint.getY() - maxPoint.getY();
    }

    return new MapPos(-correctionX, -correctionY, 0);
  }

  public MapPos getMaxPoint() {
    return maxPoint;
  }

  public MapPos getMinPoint() {
    return minPoint;
  }

  public int getZoomLevel() {
    return minPoint.getZoom();
  }

  public boolean intersectsWithBounds(final int mapX, final int mapY, final int tileSize) {
    int areaWidth = maxPoint.getX() - minPoint.getX();
    areaWidth = areaWidth == 0 ? 1 : areaWidth;
    int areaHeight = maxPoint.getY() - minPoint.getY();
    areaHeight = areaHeight == 0 ? 1 : areaHeight;
    return Utils.rectanglesIntersect(mapX, mapY, tileSize, tileSize, minPoint.getX(), minPoint
        .getY(), areaWidth, areaHeight);
  }

  public String toString() {
    return new StringBuffer("TMB ").append(minPoint.toString()).append(" <> ").append(
        maxPoint.toString()).append(" <> ").append(maxPoint.getX() - minPoint.getX()).append(":")
        .append(maxPoint.getY() - minPoint.getY()).toString();
  }
}
