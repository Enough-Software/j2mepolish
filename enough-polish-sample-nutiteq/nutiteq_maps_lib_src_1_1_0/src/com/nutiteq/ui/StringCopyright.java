package com.nutiteq.ui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * Default copyright implementation painting notice in lower-right corner of map
 * display.
 */
public class StringCopyright implements Copyright {
  private final Font copyrightFont;
  private final String copyright;

  public static final int COPYRIGHT_FONT_SMALL = Font.SIZE_SMALL;
  public static final int COPYRIGHT_FONT_MEDIUM = Font.SIZE_MEDIUM;
  public static final int COPYRIGHT_FONT_LARGE = Font.SIZE_LARGE;

  /**
   * Create copyright notice
   * 
   * @param copyright
   *          copyright text
   */
  public StringCopyright(final String copyright) {
    this(copyright, COPYRIGHT_FONT_SMALL);
  }

  /**
   * Create copyright notice with given font size.
   * 
   * @param copyright
   *          copyright text.
   * @param fontSize
   *          font size defined in J2ME implementation (small, medium, large).
   */
  public StringCopyright(final String copyright, final int fontSize) {
    this.copyright = copyright;
    copyrightFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, fontSize);
  }

  public void paint(final Graphics g, final int screenWidth, final int screenHeight) {
    final int screenX = screenWidth - 1;
    final int screenY = screenHeight - 1;
    g.setFont(copyrightFont);
    g.setColor(0xFFFFFFFF);
    g.drawString(copyright, screenX - 1, screenY - 1, Graphics.BOTTOM | Graphics.RIGHT);
    g.setColor(0xFF000000);
    g.drawString(copyright, screenX, screenY, Graphics.BOTTOM | Graphics.RIGHT);
  }
}
