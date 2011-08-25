package com.nutiteq.components;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.mgmaps.utils.Tools;

/**
 * Place label to be displayed on map if a place is highlighted.
 */
public class PlaceLabel implements Label {
  /**
   * Don't display label
   */
  public static final int DISPLAY_NONE = 0;
  /**
   * Display label on screen, right over the place.
   */
  public static final int DISPLAY_CENTER = 1;
  /**
   * Display label on bottom of map componeny
   */
  public static final int DISPLAY_BOTTOM = 2;
  /**
   * Display label on top of map component
   */
  public static final int DISPLAY_TOP = 3;

  private final String label;
  private final int displayStyle;
  private Placemark icon;
  private int lastZoom;

  /**
   * PlaceLabel constructor.
   * 
   * @param label
   *          string label for place
   * @param displayStyle
   *          display style for the label
   */
  public PlaceLabel(final String label, final int displayStyle) {
    this.label = label;
    this.displayStyle = displayStyle;
  }

  /**
   * Create label with default display style (label displayed right over place
   * on map)
   * 
   * @param name
   *          string label for place
   */
  public PlaceLabel(final String name) {
    this(name, DISPLAY_CENTER);
  }

  /**
   * Not part of public API
   */
  public String getLabel() {
    return label;
  }

  /**
   * Not part of public API
   */
  public void paint(final Graphics g, final int screenX, final int screenY, final int displayWidth,
      final int displayHeight) {
    g.setFont(Font.getDefaultFont());
    switch (displayStyle) {
    case DISPLAY_NONE:
      return;
    case DISPLAY_CENTER:
      paintToCenter(g, screenX, screenY, icon, displayWidth, displayHeight, lastZoom);
      return;
    case DISPLAY_BOTTOM:
      paintToBottom(g, displayWidth, displayHeight);
      return;
    case DISPLAY_TOP:
      paintToTop(g, displayWidth, displayHeight);
      return;
    }
  }

  private void paintToTop(final Graphics g, final int displayWidth, final int displayHeight) {
    final Font defaultFont = Font.getDefaultFont();
    final String[] nameSplit = Tools.wrapText(label, defaultFont, 3 * (displayWidth / 4), 0);
    final int textHeight = nameSplit.length * defaultFont.getHeight();
    g.setColor(0xFFFFFFFF);
    g.fillRect(0, 0, displayWidth, textHeight);
    // text
    g.setColor(0xFF000000);
    for (int i = 0; i < nameSplit.length; i++) {
      final int len = defaultFont.stringWidth(nameSplit[i]);
      g.drawString(nameSplit[i], (displayWidth - len) / 2, i * defaultFont.getHeight(),
          Graphics.TOP | Graphics.LEFT);
    }
  }

  private void paintToBottom(final Graphics g, final int displayWidth, final int displayHeight) {
    final Font defaultFont = Font.getDefaultFont();
    final String[] nameSplit = Tools.wrapText(label, defaultFont, 3 * (displayWidth / 4), 0);
    final int textHeight = nameSplit.length * defaultFont.getHeight();
    final int top = displayHeight - textHeight;
    g.setColor(0xFFFFFFFF);
    g.fillRect(0, displayHeight - textHeight, displayWidth, textHeight);
    // text
    g.setColor(0xFF000000);
    for (int i = 0; i < nameSplit.length; i++) {
      final int len = defaultFont.stringWidth(nameSplit[i]);
      g.drawString(nameSplit[i], (displayWidth - len) / 2, top + i * defaultFont.getHeight(),
          Graphics.TOP | Graphics.LEFT);
    }
  }

  private void paintToCenter(final Graphics g, final int screenX, final int screenY,
      final Placemark icon, final int displayWidth, final int displayHeight, final int zoom) {
    // TODO jaanus : try to reduce the number of calculations
    // TODO jaanus : font style
    final Font defaultFont = Font.getDefaultFont();
    final String[] nameSplit = Tools.wrapText(label, defaultFont, 3 * (displayWidth / 4), 0);
    int textAnchorX = 0;
    for (int i = 0; i < nameSplit.length; i++) {
      final int len = (defaultFont.stringWidth(nameSplit[i]) / 2) + 2;
      if (textAnchorX < len) {
        textAnchorX = len;
      }
    }
// for line, then icon can be null
    int iconY;
    if(icon!=null){
        iconY = icon.getAnchorY(zoom);    
    }else{
        iconY = 8; 
    }
        
    
    final int textAnchorY = iconY + 3
        + (defaultFont.getHeight() * nameSplit.length);
    final int topX = screenX - textAnchorX;
    final int topY = screenY - textAnchorY;
    final int textWidth = textAnchorX * 2;
    final int textHeight = (defaultFont.getHeight() * nameSplit.length) + 2;
    // rectangle (leave more to the right)
    g.setColor(0xFFFFFFFF);
    g.fillRect(topX, topY, textWidth + 2, textHeight);
    g.setColor(0xFF000000);
    g.drawRect(topX, topY, textWidth + 1, textHeight - 1);
    // text
    g.setColor(0xFF000000);
    //Start from last line
    final int lines = nameSplit.length;
    for (int i = lines - 1; i >= 0; i--) {
      g.drawString(nameSplit[i], screenX, screenY - iconY - 2 - (lines - i - 1)
          * defaultFont.getHeight(), Graphics.BOTTOM | Graphics.HCENTER);
    }
  }

  public boolean equals(final Object obj) {
    if (!(obj instanceof PlaceLabel)) {
      return false;
    }

    final PlaceLabel other = (PlaceLabel) obj;

    return label.equals(other.label) && displayStyle == other.displayStyle;
  }

  public int hashCode() {
    throw new RuntimeException("hasCode() not implemented!");
  }

  public String toString() {
        return label;
  }

 public void setUsedIcon(final Placemark icon) {
    this.icon = icon;
  }

  public void setZoom(final int zoom) {
    lastZoom = zoom;
  }

  public boolean pointOnLabel(final int screenX, final int screenY, final int displayWidth,
      final int displayHeight, final int clickX, final int clickY) {
    //TODO jaanus : implement
    return false;
  }

  public void labelClicked(final int screenX, final int screenY, final int displayWidth,
      final int displayHeight, final int clickX, final int clickY) {
    //TODO jaanus : implement
  }

  public Point getViewUpdate(final int screenX, final int screenY, final int displayWidth,
      final int displayHeight) {
    //TODO jaanus : implement
    return new Point(0, 0);
  }
}
