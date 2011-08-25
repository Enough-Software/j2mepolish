package com.nutiteq.components;

import javax.microedition.lcdui.Graphics;

/**
 * Interface for defining custom placemarks. Defines the size and relative
 * position (anchor point) to place. If image is used and it will be centered on
 * place point then anchorX would be image.getWidth() / 2.
 * 
 * Placemark can also have different properties based on zoom level. For example
 * in placemarks showing person location an icon can be shown at country level.
 * But on city level additional data, for example name, can be added to
 * placemark paint. (Check custom elements example for additional details).
 * 
 * How and if the placemark will be shown will be calculated outside placemark.
 */
public interface Placemark {
  /**
   * Get width of the placemark, based on current zoom level
   * 
   * @param zoom
   *          view zoom level
   * @return placemark width
   */
  int getWidth(final int zoom);

  /**
   * Get height of the placemark, based on current zoom level
   * 
   * @param zoom
   *          view zoom level
   * @return placemark height
   */
  int getHeight(final int zoom);

  /**
   * Get placemarks relative position x
   * 
   * @param zoom
   *          current zoom level
   * @return anchor x
   */
  int getAnchorX(final int zoom);

  /**
   * Get placemarks relative position y.
   * 
   * @param zoom
   *          current zoom level
   * @return anchor y
   */
  int getAnchorY(final int zoom);

  /**
   * Paint placemark to screen. screenX and screenY are 0, 0 for placemark. How
   * it should be placed on screen is calculated outside placemark based on
   * placemark size and anchor point.
   * 
   * @param g
   *          graphics object to paint on
   * @param screenX
   *          x point to paint on screen
   * @param screenY
   *          y point to paint on screen
   */
  void paint(final Graphics g, final int screenX, final int screenY, final int zoom);
}
