package com.nutiteq.listeners;

import com.nutiteq.components.OnMapElement;

/**
 * Listener for events displayed on map
 */
public interface OnMapElementListener {
  /**
   * Element clicked
   * 
   * @param element
   *          clicked element
   */
  void elementClicked(OnMapElement element);

  /**
   * Element gained focus
   * 
   * @param element
   *          focused element
   */
  void elementEntered(OnMapElement element);

  /**
   * Element lost focus
   * 
   * @param element
   *          unfocused element
   */
  void elementLeft(OnMapElement element);
}
