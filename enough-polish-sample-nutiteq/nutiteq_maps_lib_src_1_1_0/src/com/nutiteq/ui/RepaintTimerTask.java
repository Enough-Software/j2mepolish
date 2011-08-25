package com.nutiteq.ui;

import java.util.TimerTask;

import com.nutiteq.BasicMapComponent;

public class RepaintTimerTask extends TimerTask {
  private final BasicMapComponent component;

  public RepaintTimerTask(final BasicMapComponent mapComponent) {
    this.component = mapComponent;
  }

  public void run() {
    component.repaint(true);
  }
}
