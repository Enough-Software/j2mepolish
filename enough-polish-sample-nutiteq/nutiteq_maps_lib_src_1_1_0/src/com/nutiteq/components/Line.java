package com.nutiteq.components;

import henson.midp.Float11;

import javax.microedition.lcdui.Graphics;

import com.nutiteq.maps.GeoMap;
import com.nutiteq.utils.Utils;

/**
 * Line object to be displayed on map
 */
public class Line implements OnMapElement {
  private final WgsPoint[] points;
  private MapPos[] positions;
  private Point boundingBoxMin;
  private Point boundingBoxMax;
  private LineStyle style;
  private final boolean serverSideRender;
  private final Label label;

  /**
   * Constructor for line object with default style
   * 
   * @param points
   *          points in line (in WGS84)
   */
  public Line(final WgsPoint[] points) {
    this(points, new LineStyle(LineStyle.DEFAULT_COLOR, LineStyle.DEFAULT_WIDTH), false);
  }

  public Line(final WgsPoint[] points, final boolean serverSideRender) {
    this(points, new LineStyle(LineStyle.DEFAULT_COLOR, LineStyle.DEFAULT_WIDTH), serverSideRender);
  }

  /**
   * Constructor for line with user defined style
   * 
   * @param points
   *          points in line (in WGS84)
   * @param style
   *          line style
   */
  public Line(final WgsPoint[] points, final LineStyle style) {
    this(points, style, false);
  }

  public Line(final WgsPoint[] points, final LineStyle style, final Label label) {
    this(points, style, label, false);
  }

  public Line(final WgsPoint[] points, final LineStyle style, final boolean serverSideRender) {
    this(points, style, null, serverSideRender);
  }

  public Line(final WgsPoint[] points, final LineStyle style, final Label label,
      final boolean serverSideRender) {
    this.points = points;
    this.style = style;
    this.label = label;
    this.serverSideRender = serverSideRender;
  }

  public boolean isVisible(final int viewX, final int viewY, final int viewWidth,
      final int viewHeight, final int zoom) {
    if (!viewWithinBoundingBox(viewX, viewY, viewWidth, viewHeight)) {
      return false;
    }

    for (int j = 0; j < positions.length - 1; j++) {
      // work with point pairs
      final MapPos start = positions[j];
      final MapPos end = positions[j + 1];

      //TODO jaanus : check this hack! Problem in NutiGuide
      if (start == null || end == null) {
        continue;
      }

      if (Utils.rectanglesIntersect(start.getX(), start.getY(), end.getX() - start.getX(), end
          .getY()
          - start.getY(), viewX, viewY, viewWidth, viewHeight)) {
        return true;
      }
    }

    return false;
  }

  public void paint(final Graphics g, final MapPos middlePoint, final int displayCenterX,
      final int displayCenterY, final Rectangle changedMapArea) {
    if (serverSideRender) {
      return;
    }
    g.setColor(style.getColor());
    final int lineWidth = style.getWidth();
    for (int j = 0; j < positions.length - 1; j++) {
      // work with point pairs
      final MapPos start = positions[j];
      final MapPos end = positions[j + 1];
      if (Utils.rectanglesIntersect(start.getX(), start.getY(), end.getX() - start.getX(), end
          .getY()
          - start.getY(), changedMapArea.getX(), changedMapArea.getY(), changedMapArea.getWidth(),
          changedMapArea.getHeight())) {
        final int startX = start.getX() - middlePoint.getX() + displayCenterX;
        final int startY = start.getY() - middlePoint.getY() + displayCenterY;
        final int endX = end.getX() - middlePoint.getX() + displayCenterX;
        final int endY = end.getY() - middlePoint.getY() + displayCenterY;
        if (lineWidth < 3) {
          final int xLength = Math.abs(endX - startX);
          final int yLength = Math.abs(endY - startY);
          final int xModify = xLength > yLength ? 0 : 1;
          final int yModify = xLength > yLength ? 1 : 0;
          for (int i = 0; i < lineWidth; i++) {
            g.drawLine(startX + xModify * i, startY + yModify * i, endX + xModify * i, endY
                + yModify * i);
          }
        } else {
          drawSegment(g, startX, startY, endX, endY, lineWidth);
        }
      }
    }
  }

  /**
   * Draw a line as a polygon.
   * 
   * @param g
   *          Graphics object
   * @param x1
   *          first point
   * @param y1
   *          first point
   * @param x2
   *          second point
   * @param y2
   *          second point
   */
  private void drawSegment(final Graphics g, final int x1, final int y1, final int x2,
      final int y2, final int lineWidth) {
    final double phi = Float11.atan2(y2 - y1, x2 - x1);
    final double sinphi = Math.sin(phi);
    final double cosphi = Math.cos(phi);
    final int dx = (int) (lineWidth / 2D * sinphi);
    final int dy = (int) (lineWidth / 2D * cosphi);
    final int xx1 = x1 + dx;
    final int yy1 = y1 - dy;
    final int xx2 = x1 - dx;
    final int yy2 = y1 + dy;
    final int xx3 = x2 + dx;
    final int yy3 = y2 - dy;
    final int xx4 = x2 - dx;
    final int yy4 = y2 + dy;
    g.fillTriangle(xx1, yy1, xx2, yy2, xx3, yy3);
    g.fillTriangle(xx4, yy4, xx2, yy2, xx3, yy3);
  }

  /**
   * Not part of public API. Removed by obfuscator
   */
  public void calculatePosition(final GeoMap displayedMap, final int zoomLevel) {
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;
    positions = new MapPos[points.length];
    for (int j = 0; j < points.length; j++) {
      positions[j] = displayedMap.wgsToMapPos(points[j].toInternalWgs(), zoomLevel);
      final MapPos pos = positions[j];
      minX = Math.min(minX, pos.getX());
      minY = Math.min(minY, pos.getY());
      maxX = Math.max(maxX, pos.getX());
      maxY = Math.max(maxY, pos.getY());
    }
    boundingBoxMin = new Point(minX, minY);
    boundingBoxMax = new Point(maxX, maxY);
  }

  /**
   * Set line style.
   */
  public void setStyle(final LineStyle lineStyle) {
    style = lineStyle;
  }

  public WgsPoint[] getPoints() {
    return points;
  }

  /**
   * Not part of public API. Removed by obfuscator
   */
  public LineStyle getLineStyle() {
    return style;
  }

  public boolean isCentered(final MapPos cursorPoint) {
    if (!viewWithinBoundingBox(cursorPoint.getX(), cursorPoint.getY(), 1, 1)) {
      return false;
    }

    final int lineWidth = style.getWidth();

    //put a box around cursor
    final Rectangle cursorBox = new Rectangle(cursorPoint.getX() - lineWidth, cursorPoint.getY()
        - lineWidth, lineWidth * 2, lineWidth * 2);

    for (int i = 0; i < positions.length - 1; i++) {
      final MapPos one = positions[i];
      final MapPos two = positions[i + 1];

      if (!Utils.rectanglesIntersect(one.getX(), one.getY(), two.getX() - one.getX(), two.getY()
          - one.getY(), cursorBox.getX(), cursorBox.getY(), cursorBox.getWidth(), cursorBox
          .getHeight())) {
        continue;
      }

      final int cursorDistance = cursorPoint.distanceFromLineInPixels(one.getX(), one.getY(), two
          .getX(), two.getY());

      if (lineWidth >= cursorDistance) {
        return true;
      }
    }

    return false;
  }

  private boolean viewWithinBoundingBox(final int viewX, final int viewY, final int viewW,
      final int viewH) {
    return Utils.rectanglesIntersect(boundingBoxMin.getX(), boundingBoxMin.getY(), boundingBoxMax
        .getX()
        - boundingBoxMin.getX(), boundingBoxMax.getY() - boundingBoxMin.getY(), viewX, viewY,
        viewW, viewH);
  }

  public int distanceInPixels(final MapPos cursorOnMap) {
    //TODO jaanus : check this
    return 0;
  }

  public Label getLabel() {
    return label;
  }
}
