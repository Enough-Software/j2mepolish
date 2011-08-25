package com.nutiteq.components;

import javax.microedition.lcdui.Graphics;

import com.nutiteq.maps.GeoMap;

public interface OnMapElement {
  void paint(final Graphics g, final MapPos middlePoint, final int displayCenterX,
      final int displayCenterY, final Rectangle changedMapArea);

  void calculatePosition(final GeoMap displayedMap, final int zoomLevel);

  boolean isVisible(final int mapViewX, final int mapViewY, final int mapViewWidth,
      final int mapViewHeight, final int zoom);

  boolean isCentered(final MapPos cursorPoint);

  int distanceInPixels(MapPos cursorOnMap);

  WgsPoint[] getPoints();
  
  Label getLabel();
}
