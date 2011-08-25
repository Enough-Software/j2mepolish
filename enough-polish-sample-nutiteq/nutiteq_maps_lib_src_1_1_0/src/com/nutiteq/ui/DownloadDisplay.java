package com.nutiteq.ui;

import javax.microedition.lcdui.Graphics;

import com.nutiteq.net.DownloadCounter;

/**
 * Interface for implementing overlays showing network activity.
 */
public interface DownloadDisplay {

  void setDownloadCounter(DownloadCounter downloadCounter);

  void setDisplayUpdater(DisplayUpdater updater);

  boolean isVisible();

  void paint(Graphics g, int displayWidth, int displayHeight);
}
