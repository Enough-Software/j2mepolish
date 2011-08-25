package com.nutiteq.ui;


/**
 * Thread driven panning implementation, that will pan map at 35 ms intervals.
 * Based on panning time, the panning speed will increase. At the beginning map
 * will be moved by one pixel, the by two pixels and at the end by four pixels.
 * 
 * This implementation has additional checks using keyRepeated events to check,
 * if panning is still active. If no keyRepeat events have been received for two
 * seconds, then panning is considered abandoned and is stopped. Current panning
 * action will be stopped, not the thread.
 * 
 * Abandoned panning actions can be created, when key is pressed for moving and
 * then some system interruption (incoming call, etc.) happens. Then the key
 * released event is never received by {@link com.nutiteq.MapComponent} and
 * panning is never stopped.
 * 
 * NOTE - for this implementation to work correctly,
 * {@link com.nutiteq.MapComponent} needs to receive keyRepeated events.
 */
public class ThreadDrivenPanning extends Thread implements PanningStrategy {
  private static final int ABANDONED_PANNING_TIME = 2000;

  private static final int PAN_TIMEOUT = 35; // ms

  private Pannable pannable;
  private int moveX;
  private int moveY;
  private boolean running;
  private int count;

  private long lastKeyRepeat;

  private boolean panningWithKeys;

  public void run() {
    running = true;

    while (running) {
      try {
        while (moveX == 0 && moveY == 0 && running) {
          count = 0;
          synchronized (this) {
            wait();
          }
        }

        if (!running) {
          break;
        }

        if (panningWithKeys
            && (System.currentTimeMillis() - lastKeyRepeat) > ABANDONED_PANNING_TIME) {
          stopPanning();
        }

        count++;

        int dx = moveX;
        int dy = moveY;

        if (count >= 10 && count < 15) {
          dx = dx * 2;
          dy = dy * 2;
        } else if (count >= 15) {
          dx = dx * 4;
          dy = dy * 4;
        }

        pannable.panMap(dx, dy);

        synchronized (this) {
          wait(PAN_TIMEOUT);
        }
      } catch (final InterruptedException ignore) {
      }
    }
  }

  public synchronized void startPanning(final int directionX, final int directionY,
      final boolean panningWithKeys) {
    this.panningWithKeys = panningWithKeys;
    lastKeyRepeat = System.currentTimeMillis();
    moveX = directionX;
    moveY = directionY;
    notify();
  }

  public synchronized boolean isPanning() {
    return moveX != 0 || moveY != 0;
  }

  public synchronized void stopPanning() {
    moveX = 0;
    moveY = 0;
  }

  public synchronized void quit() {
    running = false;
    notify();
  }

  public void keyRepeated(final int keyCode) {
    lastKeyRepeat = System.currentTimeMillis();
  }

  public void setMapComponent(final Pannable mapComponent) {
    pannable = mapComponent;
  }
}
