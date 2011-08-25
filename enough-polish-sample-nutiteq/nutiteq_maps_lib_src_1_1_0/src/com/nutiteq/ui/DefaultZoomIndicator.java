package com.nutiteq.ui;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nutiteq.components.Rectangle;
import com.nutiteq.components.ZoomRange;
import com.nutiteq.log.Log;

/**
 * <p>
 * Default zoom indicator used, when
 * {@link com.nutiteq.MapComponent#showZoomLevelIndicator(boolean)} is called.
 * </p>
 * <p>
 * If custom zoom indicator is used, then zoom images used for default indicator
 * (zoom1.png, zoom2.png, zoom3.png) can be removed from library jar.
 * </p>
 */
public class DefaultZoomIndicator implements ZoomIndicator {
  private int maxZoom;
  private int zoomRange;
  private static final int SHOW_TIME = 2000;

  private static final int WIDTH = 17;
  private static final int SLIDER_HEIGHT = 9;
  private static final int PART_HEIGHT = 13;
  private static final int PART_OFFSET = 7;

  private Image verticalPart;
  private Image horisontalPart;
  private Image slider;
  private boolean visible;

  public DefaultZoomIndicator(final int minZoom, final int maxZoom) {
    setZoomRange(new ZoomRange(minZoom, maxZoom));
    try {
      verticalPart = Image.createImage("/images/zoom2.png");
      horisontalPart = Image.createImage("/images/zoom3.png");
      slider = Image.createImage("/images/zoom1.png");
    } catch (final IOException e) {
      Log.printStackTrace(e);
    }
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(final boolean visible) {
    this.visible = visible;
  }

  public void paint(final Graphics g, final int zoom, final int displayWidth,
      final int displayHeight) {
    final Rectangle clip = new Rectangle(g.getClipX(), g.getClipY(), g.getClipWidth(), g
        .getClipHeight());

    for (int i = 0; i < zoomRange; i++) {
      g
          .setClip(displayWidth - 2 - WIDTH, 2 + PART_OFFSET * i + (i > 0 ? 1 : 0), WIDTH,
              PART_HEIGHT);
      g.drawImage(horisontalPart, displayWidth - 1 - 2, 2 + 5 + PART_OFFSET * i, Graphics.TOP
          | Graphics.RIGHT);
      g.drawImage(verticalPart, displayWidth - 6 - 2, 2 + PART_OFFSET * i, Graphics.TOP
          | Graphics.RIGHT);
    }

    g.setClip(displayWidth - 2 - WIDTH, 4 + PART_OFFSET * (maxZoom - zoom), WIDTH, SLIDER_HEIGHT);
    g.drawImage(slider, displayWidth - 2, 4 + PART_OFFSET * (maxZoom - zoom), Graphics.TOP
        | Graphics.RIGHT);

    g.setClip(clip.getX(), clip.getY(), clip.getX(), clip.getY());
  }

  public void setZoomRange(final ZoomRange zRange) {
    maxZoom = zRange.getMaxZoom();
    zoomRange = maxZoom - zRange.getMinZoom() + 1;
  }

  public long displayTime() {
    return SHOW_TIME;
  }
}
