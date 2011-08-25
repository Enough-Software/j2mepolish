package net.sourceforge.jmicropolygon;

import java.util.Stack;

import javax.microedition.lcdui.Graphics;

import com.nutiteq.components.MapPos;
import com.nutiteq.components.Rectangle;

/**
 * Polygon rendering for J2ME MIDP 2.0.
 * 
 * <p>
 * This needs MIDP 2.0 for the fillTriangle() method.
 * </p>
 * 
 * @author <a href="mailto:simonturner@users.sourceforge.net">Simon Turner</a>
 * @version $Id: PolygonGraphics.java,v 1.4 2007/02/27 21:40:13 simonturner Exp
 *          $
 */
public class PolygonGraphics {

  /**
   * Draw a polygon
   * 
   * @param g
   *          The Graphics object to draw the polygon onto
   * @param xPoints
   *          The x-points of the polygon
   * @param yPoints
   *          The y-points of the polygon
   */
  public static void drawPolygon(final Graphics g, final int[] xPoints, final int[] yPoints) {
    final int max = xPoints.length - 1;
    for (int i = 0; i < max; i++) {
      g.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
    }
    g.drawLine(xPoints[max], yPoints[max], xPoints[0], yPoints[0]);
  }

  /**
   * Hash a polygon
   * 
   * @param g
   *          The Graphics object to draw the polygon onto
   * @param xPoints
   *          The x-points of the polygon
   * @param yPoints
   *          The y-points of the polygon
   * @param screenArea
   * @return
   */
  public static void hashPolygon(final Graphics g, final int[] xPoints, final int[] yPoints,
      final int displayWidth, final int displayHeight, final int minScreenY,
      final Rectangle screenArea) {
    final Stack stack = new Stack();
    hashPolygon(g, xPoints, yPoints, stack, displayWidth, displayHeight, minScreenY, screenArea);
    while (!stack.isEmpty()) {
      hashPolygon(g, (int[]) stack.pop(), (int[]) stack.pop(), stack, displayWidth, displayHeight,
          minScreenY, screenArea);
    }
  }

  public static boolean cursorOnPolygon(final MapPos[] points, final int cursorX, final int cursorY) {
    final Stack stack = new Stack();
    if (cursorOnPolygon(points, stack, cursorX, cursorY)) {
      return true;
    }

    while (!stack.isEmpty()) {
      if (cursorOnPolygon((MapPos[]) stack.pop(), stack, cursorX, cursorY)) {
        return true;
      }
    }

    return false;
  }

  private static boolean cursorOnPolygon(MapPos[] points, final Stack stack, final int cursorX,
      final int cursorY) {
    boolean withinBounds = false;
    while (points.length > 2) {
      // a, b & c represents a candidate triangle to draw. 
      // a is the left-most point of the polygon
      final int a = indexOfLeastX(points);
      // b is the point after a
      final int b = (a + 1) % points.length;
      // c is the point before a
      final int c = (a > 0) ? a - 1 : points.length - 1;
      // The value leastInternalIndex holds the index of the left-most 
      // polygon point found within the candidate triangle, if any.
      int leastInternalIndex = -1;
      boolean leastInternalSet = false;
      // If only 3 points in polygon, skip the tests
      if (points.length > 3) {
        // Check if any of the other points are within the candidate triangle
        for (int i = 0; i < points.length; i++) {
          if (i != a && i != b && i != c) {
            if (GeomUtils.withinBounds(points[i].getX(), points[i].getY(), points[a].getX(),
                points[a].getY(), points[b].getX(), points[b].getY(), points[c].getX(), points[c]
                    .getY())) {
              // Is this point the left-most point within the candidate triangle?
              if (!leastInternalSet || points[i].getX() < points[leastInternalIndex].getX()) {
                leastInternalIndex = i;
                leastInternalSet = true;
              }
            }
          }
        }
      }
      // No internal points found, fill the triangle, and reservoir-dog the polygon
      if (!leastInternalSet) {
        withinBounds = pointInTriangle(points, a, b, c, cursorX, cursorY) | withinBounds;
        //g.fillTriangle(xPoints[a], yPoints[a], xPoints[b], yPoints[b], xPoints[c], yPoints[c]);
        points = trimEar(points, a);
        // Internal points found, split the polygon into two, using the line between
        // "a" (left-most point of the polygon) and leastInternalIndex (left-most  
        // polygon-point within the candidate triangle) and recurse with each new polygon
      } else {
        final MapPos[][] split = split(points, a, leastInternalIndex);
        stack.push(split[1]);
        stack.push(split[0]);
        break;
      }
    }

    return withinBounds;
  }

  static MapPos[][] split(final MapPos[] points, final int aIndex, final int bIndex) {
    int firstLen, secondLen;
    if (bIndex < aIndex) {
      firstLen = (points.length - aIndex) + bIndex + 1;
    } else {
      firstLen = (bIndex - aIndex) + 1;
    }
    secondLen = (points.length - firstLen) + 2;
    final MapPos[] first = new MapPos[firstLen];
    final MapPos[] second = new MapPos[secondLen];
    for (int i = 0; i < firstLen; i++) {
      final int index = (aIndex + i) % points.length;
      first[i] = points[index];
    }
    for (int i = 0; i < secondLen; i++) {
      final int index = (bIndex + i) % points.length;
      second[i] = points[index];
    }
    final MapPos[][] result = new MapPos[][] { first, second };
    return result;
  }

  static MapPos[] trimEar(final MapPos[] points, final int earIndex) {
    final MapPos[] newPoints = new MapPos[points.length - 1];
    int p = 0;
    for (int i = 0; i < points.length; i++) {
      if (i != earIndex) {
        newPoints[p] = points[i];
        p++;
      }
    }
    return newPoints;
  }

  private final static boolean pointInTriangle(final MapPos[] points, final int a, final int b,
      final int c, final int cursorX, final int cursorY) {
    final int min_x = GeomUtils.min(points[a].getX(), points[b].getX(), points[c].getX());
    final int min_y = GeomUtils.min(points[a].getY(), points[b].getY(), points[c].getY());
    final int max_x = GeomUtils.max(points[a].getX(), points[b].getX(), points[c].getX());
    final int max_y = GeomUtils.max(points[a].getY(), points[b].getY(), points[c].getY());

    int h = max_y - min_y;
    int w = max_x - min_x;
    //TODO jaanus : what is this?
    h += min_y;
    w += min_x;

    //Check if the screen center point is within the triangle
    return GeomUtils.withinBounds(cursorX, cursorY, points[a].getX(), points[a].getY(), points[b]
        .getX(), points[b].getY(), points[c].getX(), points[c].getY());
  }

  static int indexOfLeastX(final MapPos[] elements) {
    int index = 0;
    int least = elements[0].getX();
    for (int i = 1; i < elements.length; i++) {
      if (elements[i].getX() < least) {
        index = i;
        least = elements[i].getX();
      }
    }
    return index;
  }

  /**
   * Hash a polygon
   * 
   * @param g
   *          The Graphics object to draw the polygon onto
   * @param xPoints
   *          The x-points of the polygon
   * @param yPoints
   *          The y-points of the polygon
   * @param stack
   *          The Stack
   * @return
   */
  private static void hashPolygon(final Graphics g, int[] xPoints, int[] yPoints,
      final Stack stack, final int displayWidth, final int displayHeight, final int minScreenY,
      final Rectangle screenArea) {
    while (xPoints.length > 2) {
      // a, b & c represents a candidate triangle to draw. 
      // a is the left-most point of the polygon
      final int a = GeomUtils.indexOfLeast(xPoints);
      // b is the point after a
      final int b = (a + 1) % xPoints.length;
      // c is the point before a
      final int c = (a > 0) ? a - 1 : xPoints.length - 1;
      // The value leastInternalIndex holds the index of the left-most 
      // polygon point found within the candidate triangle, if any.
      int leastInternalIndex = -1;
      boolean leastInternalSet = false;
      // If only 3 points in polygon, skip the tests
      if (xPoints.length > 3) {
        // Check if any of the other points are within the candidate triangle
        for (int i = 0; i < xPoints.length; i++) {
          if (i != a && i != b && i != c) {
            if (GeomUtils.withinBounds(xPoints[i], yPoints[i], xPoints[a], yPoints[a], xPoints[b],
                yPoints[b], xPoints[c], yPoints[c])) {
              // Is this point the left-most point within the candidate triangle?
              if (!leastInternalSet || xPoints[i] < xPoints[leastInternalIndex]) {
                leastInternalIndex = i;
                leastInternalSet = true;
              }
            }
          }
        }
      }
      // No internal points found, fill the triangle, and reservoir-dog the polygon
      if (!leastInternalSet) {
        hashTriangle(g, xPoints, yPoints, a, b, c, displayWidth, displayHeight, minScreenY,
            screenArea);
        //g.fillTriangle(xPoints[a], yPoints[a], xPoints[b], yPoints[b], xPoints[c], yPoints[c]);
        final int[][] trimmed = GeomUtils.trimEar(xPoints, yPoints, a);
        xPoints = trimmed[0];
        yPoints = trimmed[1];
        // Internal points found, split the polygon into two, using the line between
        // "a" (left-most point of the polygon) and leastInternalIndex (left-most  
        // polygon-point within the candidate triangle) and recurse with each new polygon
      } else {
        final int[][][] split = GeomUtils.split(xPoints, yPoints, a, leastInternalIndex);
        final int[][] poly1 = split[0];
        final int[][] poly2 = split[1];
        //                fillPolygon(g, poly1[0], poly1[1]);
        //                fillPolygon(g, poly2[0], poly2[1]);
        stack.push(poly2[1]);
        stack.push(poly2[0]);
        stack.push(poly1[1]);
        stack.push(poly1[0]);
        break;
      }
    }

    g.setClip(0, 0, displayWidth, displayHeight);
  }

  /**
   * Hashes the triangle and returns the boolean value indicating if the center
   * of the screen is in that triangle.
   * 
   * @param g
   *          The graphics context
   * @param xPoints
   *          The x-coordinates of polygon
   * @param yPoints
   *          The y-coordinates of polygon
   * @param a
   *          The index of first corner of triangle in the given polygon
   *          coordinates
   * @param b
   *          The index of second corner of triangle in the given polygon
   *          coordinates
   * @param c
   *          The index of third corner of triangle in the given polygon
   *          coordinates
   * @param screenArea
   * @return True if screen center point is in the triangle, false otherwise.
   */
  private final static void hashTriangle(final Graphics g, final int[] xPoints,
      final int[] yPoints, final int a, final int b, final int c, final int displayWidth,
      final int displayHeight, final int minScreenY, final Rectangle screenArea) {
    final int min_x = GeomUtils.min(xPoints[a], xPoints[b], xPoints[c]);
    final int min_y = GeomUtils.min(yPoints[a], yPoints[b], yPoints[c]);
    final int max_x = GeomUtils.max(xPoints[a], xPoints[b], xPoints[c]);
    final int max_y = GeomUtils.max(yPoints[a], yPoints[b], yPoints[c]);

    int h = max_y - min_y;
    int w = max_x - min_x;
    //TODO jaanus : what is this?
    h += min_y;
    w += min_x;

    //TODO jaanus : remove + 3
    if ((w < min_x || w > 0) && (h < min_y || h > 0) && (displayWidth < 0 || displayWidth > min_x)
        && (displayHeight < 0 || displayHeight > min_y)) {
      for (int i = minScreenY; i < max_y; i += 5) {
        g.setClip(min_x, i, max_x - min_x, 1);
        g.fillTriangle(xPoints[a], yPoints[a], xPoints[b], yPoints[b], xPoints[c], yPoints[c]);
      }
    }
  }
}