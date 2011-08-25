package com.nutiteq.listeners;

import com.nutiteq.components.Place;

/**
 * Listener for place events.
 * 
 * @deprecated Starting from 1.0.0, use {@link OnMapElementListener} instead.
 *             This class will be removed in 1.0.3. For migration to
 *             OnMapElementListener all event methods (place*()) should be
 *             replaced with corresponding methods in OnMapElementListener
 */
public interface PlaceListener extends OnMapElementListener {

  /**
   * Called when a place is clicked.
   * 
   * @param p
   *          the Place object
   */
  void placeClicked(Place p);

  /**
   * Called when a place becomes centered (map center is within place icon)
   * 
   * @param p
   *          the Place object
   */
  void placeEntered(Place p);

  /**
   * Called when a place is no longer centered
   * 
   * @param p
   *          the Place object
   */
  void placeLeft(Place p);
}
