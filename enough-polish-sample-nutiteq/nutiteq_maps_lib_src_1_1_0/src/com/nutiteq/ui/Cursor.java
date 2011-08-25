package com.nutiteq.ui;

import javax.microedition.lcdui.Graphics;

import com.nutiteq.components.Point;

/**
 * Interface for screen cursor used for places highlighting and selecting
 */
public interface Cursor {
  /**
   * Paint cursor on the screen. At the moment the &quot;selection point&quot;
   * will be always in displayed objects ({@link com.nutiteq.MapComponent} or
   * {@link com.nutiteq.MapItem}) center.
   * 
   * @param g
   *          graphics object to paint on
   * @param screenX
   *          cursor selection point x on screen
   * @param screenY
   *          cursor selection point y on screen
   * @param displayWidth
   *          displayed area (screen) width
   * @param displayHeight
   *          displayed area (screen) height
   */
  void paint(Graphics g, int screenX, int screenY, int displayWidth, int displayHeight);

  /**
   * Get the &quote;select&quote; point on screen for cursor. This point will be
   * used for place/map clicks, etc.
   * 
   * @param displayWidth
   *          map view width
   * @param displayHeight
   *          map view height
   * @return point in pixels
   */
  Point getPointOnDisplay(int displayWidth, int displayHeight);
}
