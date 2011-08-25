package com.nutiteq.listeners;

import com.nutiteq.components.WgsPoint;

/**
 * Listen for map events.
 */
public interface MapListener {

  /**
   * Called after map has been moved.
   */
  void mapMoved();

  /**
   * Called when a point on the map is clicked (when place is not selected).
   * 
   * @param p
   *          point clicked in WGS84
   */
  void mapClicked(final WgsPoint p);

  /**
   * Called when the map is changed in some way and repaint needs to be called
   * on the container object.
   * 
   * @param mapIsComplete
   *          is current map view complete (if FALSE, then some map tiles are
   *          not yet downloaded)
   */
  void needRepaint(boolean mapIsComplete);
}
