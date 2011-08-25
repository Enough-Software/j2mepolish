package com.nutiteq.wrappers.rimui;

import net.rim.device.api.ui.DrawStyle;

/**
 * Wrapper implementation around RIM UI graphics to enable similar API to J2ME
 * graphics. Only methods used in library have been implemented.
 */
public class Graphics {
  public static final int HCENTER = 1;
  public static final int VCENTER = 2;
  public static final int LEFT = 4;
  public static final int RIGHT = 8;
  public static final int TOP = 16;
  public static final int BOTTOM = 32;
  public static final int BASELINE = 64;

  public static final int SOLID = 0;
  public static final int DOTTED = 1;
  private final net.rim.device.api.ui.Graphics buffered;
  private net.rim.device.api.ui.Font internalFont = net.rim.device.api.ui.Font.getDefault();
  private int pushCount;
  private int color;

  private int translateX;
  private int translateY;
  private int clipX;
  private int clipY;
  
  /**
   * Wrap 'native' RIM UI Graphics class
   * 
   * @param buffered
   *          graphics to be wrapped
   */
  public Graphics(final net.rim.device.api.ui.Graphics buffered) {
    this.buffered = buffered;
  }

  public void translate(int x, int y){
        this.clipX -= x;
        this.clipY -= y;
        this.translateX += x;
        this.translateY += y;
  }
  
  /**
   * get RIM UI Graphics class, enables to use native methods of it
   * @return RIM native Graphics
   */

  public net.rim.device.api.ui.Graphics getNative() {
      return this.buffered;
  }
  
  public void setColor(final int color) {
    this.color = color;
    buffered.setColor(color);
  }

  public void drawRect(final int x, final int y, final int width, final int height) {
    buffered.drawRect(x, y, width, height);
  }

  public void drawImage(final Image image, final int x, final int y, final int anchor) {
    image.getGraphics().popAll();

    int drawX = x;
    int drawY = y;

    if ((anchor & BOTTOM) == BOTTOM) {
      drawY -= image.getHeight();
    }

    if ((anchor & LEFT) != LEFT) {
      final int imageWidth = image.getWidth();
      if ((anchor & RIGHT) == RIGHT) {
        drawX -= imageWidth;
      } else if ((anchor & HCENTER) == HCENTER) {
        drawX -= imageWidth / 2;
      }
    }

    buffered.drawBitmap(drawX, drawY, image.getWidth(), image.getHeight(), image.getNativeImage(),
        0, 0);
  }

  /**
   * Not implemented yet
   * 
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   * @param x3
   * @param y3
   */
  public void fillTriangle(final int x1, final int y1, final int x2, final int y2, final int x3,
      final int y3) {
    final int[] xPoints = new int[4];
    final int[] yPoints = new int[4];

    xPoints[0] = x1;
    xPoints[1] = x2;
    xPoints[2] = x3;
    xPoints[3] = x1;

    yPoints[0] = y1;
    yPoints[1] = y2;
    yPoints[2] = y3;
    yPoints[3] = y1;

    buffered.setColor(color);
    buffered.drawFilledPath(xPoints, yPoints, null, null);
  }

  public void setClip(final int x, final int y, final int width, final int height) {
    popAll();
    buffered.pushContext(x, y, width, height, 0, 0);
    pushCount++;
  }

  public void drawLine(final int startX, final int startY, final int endX, final int endY) {
    buffered.drawLine(startX, startY, endX, endY);
  }

  public void drawString(final String string, final int x, final int y, final int anchor) {
    int rimAnchor = DrawStyle.RIGHT;
    int drawX = x;
    final int drawY = y;
    if ((anchor & BOTTOM) == BOTTOM) {
      rimAnchor = DrawStyle.BOTTOM | rimAnchor;
    } else {
      rimAnchor = DrawStyle.TOP | rimAnchor;
    }

    if ((anchor & LEFT) != LEFT) {
      final int stringWidth = internalFont.getAdvance(string);
      if ((anchor & RIGHT) == RIGHT) {
        drawX -= stringWidth;
      } else if ((anchor & HCENTER) == HCENTER) {
        drawX -= stringWidth / 2;
      }
    }
    buffered.drawText(string, drawX, drawY, rimAnchor);
  }

  public void fillRect(final int x, final int y, final int width, final int height) {
    buffered.fillRect(x, y, width, height);
  }

  public void setFont(final Font font) {
    internalFont = font.getNativeFont();
    buffered.setFont(internalFont);
  }

  public void drawRGB(final int[] rgbData, final int offset, final int scanlength, final int x,
      final int y, final int width, final int height, final boolean processAlpha) {
    buffered.drawRGB(rgbData, offset, scanlength, x, y, width, height);
  }

  public int getClipX() {
    return buffered.getClippingRect().x;
  }

  public int getClipY() {
    return buffered.getClippingRect().y;
  }

  public int getClipWidth() {
    return buffered.getClippingRect().width;
  }

  public int getClipHeight() {
    return buffered.getClippingRect().height;
  }
  
  public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
  {
    buffered.drawRoundRect(x + this.translateX, y + this.translateY, width + 1, height + 1, arcWidth, arcHeight);
  }

  public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
  {
    buffered.fillRoundRect(x + this.translateX, y + this.translateY, width, height, arcWidth, arcHeight);
  }


  /**
   * Pop graphics contexts, that might have been pushed in
   * {@link #setClip(int, int, int, int)}. This method will not fail, when no
   * pushes have been performed
   */
  public void popAll() {
    while (pushCount > 0) {
      buffered.popContext();
      pushCount--;
    }
  }
}
