package com.nutiteq.ui;

import javax.microedition.lcdui.Graphics;

import com.nutiteq.components.ZoomRange;

/**
 * Element for displaying zoom range and current zoom on screen. Zoom indicator
 * will be set visible, for the time defined in {@link #displayTime()}, after
 * zoom action (zoom in/out) has occurred.
 */
public interface ZoomIndicator {
  /**
   * Is this element visible
   * 
   * @return is element visible
   */
  boolean isVisible();

  /**
   * Paint element on screen
   * 
   * <strong>Note:</strong> if you change clip on graphics, please restore it to
   * previous state.
   * 
   * @param g
   *          graphics object to paint on
   * @param currentZoom
   *          current zoom
   * @param displayWidth
   *          display width
   * @param displayHeight
   *          display height
   */
  void paint(Graphics g, int currentZoom, int displayWidth, int displayHeight);

  /**
   * Set zoom range that is available
   * 
   * @param zoomRange
   *          available zoom range
   */
  void setZoomRange(ZoomRange zoomRange);

  /**
   * How long should element be visible after zoom keys have been pressed
   * 
   * @return display time in milliseconds
   */
  long displayTime();

  /**
   * Set element visible
   * 
   * @param visible
   *          show/hide this element from screen
   */
  void setVisible(boolean visible);
}
