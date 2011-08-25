package com.nutiteq.components;

import javax.microedition.lcdui.Graphics;

import net.sourceforge.jmicropolygon.PolygonGraphics;

import com.nutiteq.maps.GeoMap;
import com.nutiteq.utils.Utils;

/**
 * A generic polygon object. At the moment only available in blue.
 */
public class Polygon implements OnMapElement {
  private final WgsPoint[] coordinates;
  private final MapPos[] mapPositions;
  private final boolean serverSideRender;
  private Point boundingBoxMin;
  private Point boundingBoxMax;
  private PolyStyle style;
  private final Label label;

  public Polygon(final WgsPoint[] coordinates) {
    this(coordinates, PolyStyle.DEFAULT_STYLE, false);
  }

  public Polygon(final WgsPoint[] coordinates, final PolyStyle style) {
    this(coordinates, style, false);
  }

  public Polygon(final WgsPoint[] coordinates, final PolyStyle style, final Label label) {
    this(coordinates, style, label, false);
  }

  public Polygon(final WgsPoint[] coordinates, final boolean serverSideRender) {
    this(coordinates, PolyStyle.DEFAULT_STYLE, serverSideRender);
  }

  public Polygon(final WgsPoint[] coordinates, final PolyStyle style, final boolean serverSideRender) {
    this(coordinates, style, null, serverSideRender);
  }

  public Polygon(final WgsPoint[] coordinates, final PolyStyle style, final Label label,
      final boolean serverSideRender) {
    this.coordinates = coordinates;
    this.style = style;
    this.label = label;
    mapPositions = new MapPos[coordinates.length];
    this.serverSideRender = serverSideRender;
  }

  protected Polygon(final MapPos[] positions) {
    coordinates = null;
    style = null;
    label = null;
    mapPositions = positions;
    serverSideRender = false;

    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;
    for (int i = 0; i < mapPositions.length; i++) {
      final MapPos pos = mapPositions[i];
      minX = Math.min(minX, pos.getX());
      minY = Math.min(minY, pos.getY());
      maxX = Math.max(maxX, pos.getX());
      maxY = Math.max(maxY, pos.getY());
    }
    boundingBoxMin = new Point(minX, minY);
    boundingBoxMax = new Point(maxX, maxY);
  }

  public boolean isVisible(final int viewX, final int viewY, final int viewWidth,
      final int viewHeight, final int zoom) {
    final MapPos start = mapPositions[0];
    for (int i = 1; i < mapPositions.length; i++) {
      final MapPos end = mapPositions[i];
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
    //TODO jaanus : check toScreenArea. looks like height or screen y is incorrect
    final Rectangle changedAreaOnScreen = Utils.areaToScreen(changedMapArea, middlePoint.getX()
        - displayCenterX, middlePoint.getY() - displayCenterY, displayCenterX * 2,
        displayCenterY * 2);
    //Set current color
    g.setColor(style.getColor());

    int left;
    int top;
    int dx;
    int dy;

    //Try to draw filled polygon
    //TODO jaanus : shares some logic with line?
    final int[] xPoints = new int[mapPositions.length];
    final int[] yPoints = new int[mapPositions.length];
    int minScreenY = Integer.MAX_VALUE;
    for (int i = 0; i < xPoints.length; i++) {
      left = mapPositions[i].getX() - middlePoint.getX() + displayCenterX;
      top = mapPositions[i].getY() - middlePoint.getY() + displayCenterY;

      if (i < mapPositions.length - 1) {
        dx = mapPositions[i + 1].getX() - mapPositions[i].getX();
        dy = mapPositions[i + 1].getY() - mapPositions[i].getY();
      } else {
        dx = mapPositions[0].getX() - mapPositions[i].getX();
        dy = mapPositions[0].getY() - mapPositions[i].getY();
      }

      //Draw border lines
      g.drawLine(left, top, left + dx, top + dy);

      xPoints[i] = left + dx;
      yPoints[i] = top + dy;

      minScreenY = Math.min(minScreenY, yPoints[i]);
    }

    //Hash the area
    PolygonGraphics.hashPolygon(g, xPoints, yPoints, displayCenterX * 2, displayCenterY * 2,
        minScreenY, changedAreaOnScreen);
  }

  public void calculatePosition(final GeoMap displayedMap, final int zoomLevel) {
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;
    for (int i = 0; i < coordinates.length; i++) {
      mapPositions[i] = displayedMap.wgsToMapPos(coordinates[i].toInternalWgs(), zoomLevel);
      final MapPos pos = mapPositions[i];
      minX = Math.min(minX, pos.getX());
      minY = Math.min(minY, pos.getY());
      maxX = Math.max(maxX, pos.getX());
      maxY = Math.max(maxY, pos.getY());
    }
    boundingBoxMin = new Point(minX, minY);
    boundingBoxMax = new Point(maxX, maxY);
  }

  public boolean isCentered(final MapPos cursorPoint) {
    if (!viewWithinBoundingBox(cursorPoint.getX(), cursorPoint.getY(), 1, 1)) {
      return false;
    }

    final MapPos[] copy = new MapPos[mapPositions.length];
    System.arraycopy(mapPositions, 0, copy, 0, mapPositions.length);
    return PolygonGraphics.cursorOnPolygon(copy, cursorPoint.getX(), cursorPoint.getY());
  }

  public int distanceInPixels(final MapPos cursorOnMap) {
    //TODO jaanus : check this
    return 0;
  }

  public Label getLabel() {
    return label;
  }

  public WgsPoint[] getPoints() {
      return coordinates;
    }
  
  private boolean viewWithinBoundingBox(final int viewX, final int viewY, final int viewW,
      final int viewH) {
    return Utils.rectanglesIntersect(boundingBoxMin.getX(), boundingBoxMin.getY(), boundingBoxMax
        .getX()
        - boundingBoxMin.getX(), boundingBoxMax.getY() - boundingBoxMin.getY(), viewX, viewY,
        viewW, viewH);
  }

  public void setStyle(final PolyStyle style) {
    this.style = style;
  }
}
