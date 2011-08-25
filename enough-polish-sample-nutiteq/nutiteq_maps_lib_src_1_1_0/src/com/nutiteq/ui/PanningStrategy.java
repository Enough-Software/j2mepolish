package com.nutiteq.ui;

/**
 * General interface for panning strategy
 */
public interface PanningStrategy {
  /**
   * Start panning 'thread'
   */
  void start();

  /**
   * Quit panning 'thread'
   */
  void quit();

  /**
   * Start panning with directions information. Also notify if panning was
   * started by key controls or some other event (for example on screen controls
   * with stylus)
   * 
   * @param xMove
   *          change on x axis
   * @param yMove
   *          change on y axis
   * @param panningWithKeys
   *          has panning been started by key events
   */
  void startPanning(final int xMove, final int yMove, final boolean panningWithKeys);

  /**
   * Is panning implementation still running
   * 
   * @return if panning 'thread' is still working
   */
  boolean isPanning();

  /**
   * Stop current pan action.
   */
  void stopPanning();

  /**
   * Notify panning 'thread' about key repeated events
   * 
   * @param keyCode
   *          code for key repeated
   */
  void keyRepeated(final int keyCode);

  /**
   * Will be called from library, after strategy has been entered into
   * {@link com.nutiteq.MapComponent} or {@link com.nutiteq.MapItem}
   * 
   * @param pannableObject
   *          map object on which to perform panning actions
   */
  void setMapComponent(final Pannable pannableObject);
}
