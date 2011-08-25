package com.nutiteq.ui;

import java.util.TimerTask;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.task.Task;

public class ZoomDelayTimerTask extends TimerTask {
  public static final int ZOOM_DELAY_TIME = 1000;
  private final BasicMapComponent mapComponent;
  private final Task tileSearch;

  public ZoomDelayTimerTask(final BasicMapComponent downloader, final Task tileSearch) {
    this.mapComponent = downloader;
    this.tileSearch = tileSearch;
  }

  public void run() {
    mapComponent.enqueue(tileSearch);
    mapComponent.removeZoomDelay();
  }
}
