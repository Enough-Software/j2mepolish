package com.nutiteq.ui;

/**
 * Unthreaded panning implementation that uses key repeated events for panning.
 * 
 * Map moving speed is progressive, based on panning action length. At first map
 * is moved by one pixel, then by 2, 4 and 8 pixels.
 * 
 * NOTE - because {@link com.nutiteq.MapComponent} has an unthreaded
 * implementation, this panning strategy can't be used with default on screen
 * controls for panning with stylus. With stylus there are no continuous events
 * created and there is no way to know if panning is still active.
 */
public class EventDrivenPanning implements PanningStrategy {
  private Pannable pannable;
  private int xMove;
  private int yMove;
  private int count;

  public boolean isPanning() {
    return false;
  }

  public void quit() {

  }

  public void start() {

  }

  public void startPanning(final int xMove, final int yMove, final boolean panningWithKeys) {
    this.xMove = xMove;
    this.yMove = yMove;
    pannable.panMap(xMove, yMove);
    count = 1;
  }

  public void stopPanning() {

  }

  public void keyRepeated(final int keyCode) {
    count++;
    int dx = xMove;
    int dy = yMove;

    if (count >= 5 && count < 10) {
      dx = dx * 2;
      dy = dy * 2;
    } else if (count >= 10 && count < 15) {
      dx = dx * 4;
      dy = dy * 4;
    } else if (count >= 15 && count < 30) {
      dx = dx * 8;
      dy = dy * 8;
    } else if (count >= 30) {
      dx = dx * 16;
      dy = dy * 16;
    }

    pannable.panMap(dx, dy);
  }

  public void setMapComponent(final Pannable mapComponent) {
    pannable = mapComponent;
  }
}
