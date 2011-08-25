package com.nutiteq.components;

import com.nutiteq.utils.Utils;

/**
 * Implements a projected position on a map as a set of 3 numbers: (x,y)
 * projected pixel coordinates, (zoom) zoom level.
 */
public class MapPos {
  private int x;
  private int y;
  private int zoom;

  /**
   * Constructor for MapPos.
   * 
   * @param x
   *          x coordinate
   * @param y
   *          y coordinate
   * @param zoom
   *          zoom
   */
  public MapPos(final int x, final int y, final int zoom) {
    this.x = x;
    this.y = y;
    this.zoom = zoom;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getZoom() {
    return zoom;
  }

  public void setX(final int x) {
    this.x = x;
  }

  public void setY(final int y) {
    this.y = y;
  }

  public void setZoom(final int zoom) {
    this.zoom = zoom;
  }

  /**
   * Not part of public API Check if an image bounded by left, top, right,
   * bottom can be displayed.
   * 
   * @param x
   *          top-left corner
   * @param y
   *          top-left corner
   * @param width
   *          image width
   * @param height
   *          image height
   * @return true if visible, false otherwise
   */
  public static boolean checkBounds(final int x, final int y, final int width, final int height,
      final int screenWidth, final int screenHeight) {
    return !(x < -width || y < -height || x >= screenWidth || y >= screenHeight);
  }

  /**
   * Not part of public API Create a copy of this object.
   * 
   * @return the newly created copy
   */
  public MapPos copy() {
    return new MapPos(x, y, zoom);
  }

  /**
   * Equals only compares x, y, zoom.
   * 
   * @param o
   *          other map pos object
   * @return true if equal, false otherwise
   */
  public boolean equals(final Object o) {
    if (o == null || !(o instanceof MapPos)) {
      return false;
    }

    final MapPos other = (MapPos) o;
    return x == other.x && y == other.y && zoom == other.zoom;
  }

  public int hashCode() {
    throw new RuntimeException("hashcode() has not been implemented!");
  }

  /**
   * Not part of public API
   */
  public boolean isVisible(final MapPos middlePoint, final int displayCenterX,
      final int displayCenterY) {
    return Utils.rectanglesIntersect(x, y, 1, 1, middlePoint.x - displayCenterX, middlePoint.y
        - displayCenterY, displayCenterX * 2, displayCenterY * 2);
  }

  /**
   * Not part of public API
   */
  public int distanceInPixels(final MapPos pos) {
    return distanceFromPointInPixels(pos.x, pos.y);
  }

  private int distanceFromPointInPixels(final int otherX, final int otherY) {
    // TODO jaanus : check this
    final int distanceX = Math.abs(x - otherX);
    final int distanceY = Math.abs(y - otherY);
    return (int) Math.floor(Math.sqrt(distanceX * distanceX + distanceY * distanceY));
  }

  public int distanceFromLineInPixels(final int x1, final int y1, final int x2, final int y2) {
    if (x1 == x2 && y1 == y2) {
      return distanceFromPointInPixels(x1, y1);
    }
    //http://www.ahristov.com/tutorial/geometry-games/point-line-distance.html
    final double normalLength = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    return (int) Math.floor(Math.abs((x - x1) * (y2 - y1) - (y - y1) * (x2 - x1)) / normalLength);

    //from http://stackoverflow.com/questions/910882/how-can-i-tell-if-a-point-is-nearby-a-certain-line
    //    return (int) Math.floor(Math.abs((x2 - x1) * (y1 - y) - (x1 - x) * (y2 - y1))
    //        / Math.sqrt(Float11.pow(x2 - x1, 2) + Float11.pow(y2 - y1, 2)));

    //http://www.ocforums.com/archive/index.php/t-421519.html
    //    final int width = Math.abs(x2 - x1);
    //    final int height = Math.abs(y2 - y1);
    //    final int dx = x - x1;
    //    final int dy = y - y1;
    //    int distance;
    //
    //    if (dx != 0 && width != 0) // if both lines are not vertical
    //    {
    //      final double angleToCorner = Math.atan(height / width);
    //      final double angleToPoint = Math.atan(dy / dx);
    //      distance = (int) Math.floor(Math.sqrt(dx * dx + dy * dy)
    //          * Math.sin(Math.abs(angleToCorner - angleToPoint)));
    //    } else // one or both slopes have zero divisor (are vertical)
    //    {
    //      distance = Math.abs(dx);
    //    }
    //
    //    return distance;
  }

  public int distanceFromLineInPixels(final Point pOne, final Point pTwo) {
    return distanceFromLineInPixels(pOne.getX(), pOne.getY(), pTwo.getX(), pTwo.getY());
  }

  public String toString() {
    return new StringBuffer("map_pos:").append(x).append(":").append(y).append(":").append(zoom)
        .toString();
  }
}
