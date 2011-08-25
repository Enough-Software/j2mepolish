package com.nutiteq.ui;

import javax.microedition.lcdui.Graphics;

/**
 * Interface for copyright notice to be displayed on map vie
 */
public interface Copyright {
  /**
   * Paint copyright notice on map view
   * 
   * @param g
   *          graphics object to paint on
   * @param displayWidth
   *          map view width
   * @param displayHeight
   *          map view height
   */
  void paint(Graphics g, int displayWidth, int displayHeight);
}
