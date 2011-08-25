package com.nutiteq.location;

import javax.microedition.lcdui.Graphics;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.components.MapPos;

/**
 * Interface for graphical representation of location data.
 */
public interface LocationMarker extends LocationListener {
  /**
   * Set location source into marker. Could me used for source state status
   * retrieving
   * 
   * @param locationSource
   *          location source associated with this marker
   */
  void setLocationSource(final LocationSource locationSource);

  //TODO jaanus
  void paint(Graphics g, MapPos middlePoint, int displayCenterX, int displayCenterY);

  /**
   * Force marker position update after zoom
   */
  void updatePosition();

  //TODO jaanus
  void setMapComponent(BasicMapComponent basicMapComponent);

  /**
   * Notify marker to quit updates
   */
  void quit();

  void setTrackingEnabled(boolean enabled);
}
