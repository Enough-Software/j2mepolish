package com.nutiteq.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Icon used for marking a place on map
 */
public class PlaceIcon implements Placemark {
  private final Image icon;
  private final int anchorX;
  private final int anchorY;

  /**
   * Create a icon object with default anchor point.
   * 
   * @param icon
   *          image used for place marking
   */
  public PlaceIcon(final Image icon) {
    this(icon, icon.getWidth() / 2, icon.getHeight() / 2);
  }

  /**
   * Create a icon object with custom image placement. For example for balloon
   * image, that should point to a place on map, bottom center should be defined
   * as anchor point for correct image placement.
   * 
   * @param icon
   *          place image
   * @param anchorX
   *          x coordinate on icon anchor point
   * @param anchorY
   *          y coordinate on icon anchor point
   */
  public PlaceIcon(final Image icon, final int anchorX, final int anchorY) {
    this.icon = icon;
    this.anchorX = anchorX;
    this.anchorY = anchorY;
  }

  /**
   * Not part of public API
   */
  public Image getIcon() {
    return icon;
  }

  public int getWidth(final int zoom) {
    return icon.getWidth();
  }

  public int getHeight(final int zoom) {
    return icon.getHeight();
  }

  public int getAnchorX(final int zoom) {
    return anchorX;
  }

  public int getAnchorY(final int zoom) {
    return anchorY;
  }

  public void paint(final Graphics g, final int screenX, final int screenY, final int zoom) {
    g.drawImage(icon, screenX, screenY, Graphics.TOP | Graphics.LEFT);
  }
}
