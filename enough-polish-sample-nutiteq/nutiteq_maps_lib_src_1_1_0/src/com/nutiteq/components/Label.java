package com.nutiteq.components;

import javax.microedition.lcdui.Graphics;

public interface Label {
  /**
   * String representation of this label
   * 
   * @return label as string
   */
  String getLabel();

  /**
   * Paint label to screen
   * 
   * @param g
   *          graphics object to be drawn on
   * @param screenX
   *          place x location on screen
   * @param screenY
   *          place y location on screen
   * @param displayWidth
   *          available display width
   * @param displayHeight
   *          available display height
   */
  void paint(Graphics g, int screenX, int screenY, int displayWidth, int displayHeight);

  /**
   * Is given screen point on label
   * 
   * @param screenX
   *          place x location on screen
   * @param screenY
   *          place y location on screen
   * @param displayWidth
   *          available display width
   * @param displayHeight
   *          available display height
   * @param pointX
   *          point location x on display
   * @param pointY
   *          point location y on display
   * @return true if point on label
   */
  boolean pointOnLabel(int screenX, int screenY, int displayWidth, int displayHeight, int pointX,
      int pointY);

  /**
   * Notify label about the click and click location on screen
   * 
   * @param screenX
   *          place x location on screen
   * @param screenY
   *          place y location on screen
   * @param displayWidth
   *          available display width
   * @param displayHeight
   *          available display height
   * @param clickX
   *          click location x on display
   * @param clickY
   *          click location y on display
   */
  void labelClicked(int screenX, int screenY, int displayWidth, int displayHeight, int clickX,
      int clickY);

  /**
   * Get pixels needed for proper label display.
   * 
   * @param screenX
   *          object location x on screen
   * @param screenY
   *          object location y on screen
   * @param displayWidth
   *          available display width
   * @param displayHeight
   *          available display height
   * @return pixel (x/y) required for proper display
   */
  Point getViewUpdate(int screenX, int screenY, int displayWidth, int displayHeight);
}
