package com.nutiteq.ui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nutiteq.utils.Utils;

public class CustomNTCopyright implements Copyright {
  private final Image imageNt;
  private final Image imageOsm;
  private final Font textFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
  private final int boxHeight;
  private final int boxWidth;
  private static final String COPYRIGHT = "CC-by-SA";

  public CustomNTCopyright() {
    imageNt = Utils.createImage("/images/ntico.png");
    imageOsm = Utils.createImage("/images/osmico.png");

    boxHeight = Math.max(imageNt.getHeight(), textFont.getHeight()) + 4;
    boxWidth = textFont.stringWidth(COPYRIGHT) + imageNt.getWidth() + imageOsm.getWidth() + 4;
  }

  public void paint(final Graphics g, final int screenWidth, final int screenHeight) {
    g.setColor(0xFF000000);
    g.fillRoundRect(2, screenHeight - boxHeight - 3, boxWidth, boxHeight, 10, 10);
    g.setColor(0xFFFFFFFF);
    g.fillRoundRect(3, screenHeight - boxHeight - 2, boxWidth - 2, boxHeight - 2, 10, 10);
    g.setColor(0xFF000000);
    g.setFont(textFont);
    g.drawString(COPYRIGHT, 4, screenHeight - 4, Graphics.LEFT | Graphics.BOTTOM);
    g.drawImage(imageNt, boxWidth, screenHeight - 5, Graphics.RIGHT | Graphics.BOTTOM);
    g.drawImage(imageOsm, boxWidth-imageOsm.getWidth(), screenHeight - 5, Graphics.RIGHT | Graphics.BOTTOM);
  }
}
