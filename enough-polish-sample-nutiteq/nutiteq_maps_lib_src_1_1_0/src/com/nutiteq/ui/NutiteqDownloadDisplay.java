package com.nutiteq.ui;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.nutiteq.net.DownloadCounter;
import com.nutiteq.net.NetworkListener;

/**
 * Default overlay for showing network activity.
 */
public class NutiteqDownloadDisplay implements DownloadDisplay, NetworkListener {
  private static final int KILO_BYTE = 1024;
  private static final int MEGA_BYTE = 1024 * 1024;

  private DownloadCounter downloadCounter;
  private int lastPainted;
  private String downloaded = "0kB";
  private boolean visible;

  private static final Font FONT = Font
      .getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);

  private static final long HIDE_TIME = 3000;

  private DisplayUpdater updater;

  private final Timer timer = new Timer();
  private long hideCallTime;

  public void setDownloadCounter(final DownloadCounter counter) {
    downloadCounter = counter;
    downloadCounter.setNetworkListener(this);
  }

  public void setDisplayUpdater(final DisplayUpdater updater) {
    this.updater = updater;
  }

  public boolean isVisible() {
    return visible;
  }

  public void paint(final Graphics g, final int displayWidth, final int displayHeight) {
    if (lastPainted != downloadCounter.getDownloadedBytes()) {
      downloaded = bytesToHumanReadable(downloadCounter.getDownloadedBytes());
      lastPainted = downloadCounter.getDownloadedBytes();
    }

    g.setClip(0, 0, displayWidth, displayHeight);
    final int stringWidth = FONT.stringWidth(downloaded);
    g.setColor(0xFFFFFFFF);
    g.fillRect(displayWidth - stringWidth - 2, 0, stringWidth + 2, FONT.getHeight());
    g.setFont(FONT);
    g.setColor(0xFF000000);
    g.drawString(downloaded, displayWidth, 0, Graphics.TOP | Graphics.RIGHT);
  }

  private String bytesToHumanReadable(final int downloadedBytes) {
    if (downloadedBytes < KILO_BYTE) {
      return Integer.toString(downloadedBytes) + " B";
    } else if (downloadedBytes < MEGA_BYTE) {
      return Integer.toString(downloadedBytes / KILO_BYTE) + " kB";
    } else {
      return formatWithPlacesAfterPoint(Float.toString(downloadedBytes / (float) MEGA_BYTE), 2)
          + " MB";
    }
  }

  protected String formatWithPlacesAfterPoint(final String floatString, final int places) {
    final int indexOfP = floatString.indexOf(".");
    if (indexOfP < 0 || floatString.length() < indexOfP + 1 + places) {
      return floatString;
    }

    return floatString.substring(0, indexOfP + 1 + places);
  }

  public void downloadStarted() {
    if (!visible) {
      visible = true;
      if (updater != null) {
        updater.repaint();
      }
    }
  }

  public void dataMoved() {
    hideCallTime = 0;
    if (updater != null) {
      updater.repaint();
    }
  }

  public void downloadCompleted() {
    if (updater == null) {
      return;
    }

    hideCallTime = System.currentTimeMillis();

    timer.schedule(new TimerTask() {
      public void run() {
        if (hideCallTime == 0 || System.currentTimeMillis() - hideCallTime < HIDE_TIME) {
          return;
        }

        visible = false;
        updater.repaint();
      }
    }, HIDE_TIME);
  }
}
