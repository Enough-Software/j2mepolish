package com.nutiteq.ui;

import java.util.TimerTask;

import com.nutiteq.BasicMapComponent;

public class ZoomIndicatorCheckTask extends TimerTask {
  private final BasicMapComponent mapComponent;

  public ZoomIndicatorCheckTask(final BasicMapComponent mapComponent) {
    this.mapComponent = mapComponent;
  }

  public void run() {
    mapComponent.zoomLevelIndicatorCheck();
  }
}
