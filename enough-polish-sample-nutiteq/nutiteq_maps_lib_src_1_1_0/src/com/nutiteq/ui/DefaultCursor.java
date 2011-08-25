package com.nutiteq.ui;

import javax.microedition.lcdui.Graphics;

import com.nutiteq.components.Point;

/**
 * Default cursor implementation used in {@link com.nutiteq.MapComponent}.
 * Paints a cross with width and height of 10 pixels. Default color used in
 * library is red.
 */
public class DefaultCursor implements Cursor {
  private final int color;

  /**
   * Constructor for default cursor.
   * 
   * @param cursorColor
   *          color for the cursor (in format 0xAARRGGBB)
   */
  public DefaultCursor(final int cursorColor) {
    color = cursorColor;
  }

  public void paint(final Graphics g, final int screenX, final int screenY, final int displayWidth,
      final int displayHeight) {
    g.setClip(screenX - 5, screenY - 5, 10, 10);
    g.setColor(color);
    g.drawLine(screenX - 5, screenY, screenX + 5, screenY);
    g.drawLine(screenX, screenY - 5, screenX, screenY + 5);
  }

  public Point getPointOnDisplay(final int displayWidth, final int displayHeight) {
    return new Point(displayWidth / 2, displayHeight / 2);
  }
}
