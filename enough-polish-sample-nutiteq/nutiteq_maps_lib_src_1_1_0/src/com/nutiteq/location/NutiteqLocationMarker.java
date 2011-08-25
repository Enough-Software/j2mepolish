package com.nutiteq.location;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Graphics;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.Placemark;
import com.nutiteq.components.WgsPoint;

/**
 * Default implementation for location marker. Supports separate placemarks for
 * indicating connection state (either connected or connection lost).
 */
public class NutiteqLocationMarker extends TimerTask implements LocationMarker {
  private WgsPoint lastWgsLocation;
  private BasicMapComponent mapComponent;
  private MapPos mapPosition;
  private final Placemark placemarkConnected;
  private final int updateInterval;
  private boolean running;
  private boolean track;
  private final Placemark placemarkConnectionLost;
  private final int lastStatus;
  private LocationSource locationSource;
  //TODO jaanus : check this timer stuff
  private final Timer timer = new Timer();

  /**
   * 
   * @param placemark
   *          graphical part of marker, painted on map
   * @param updateInterval
   *          update interval for marker display. If 0, then screen will be
   *          updated when location is received from location source
   * @param track
   *          move center of map to received location
   */
  public NutiteqLocationMarker(final Placemark placemark, final int updateInterval,
      final boolean track) {
    this(placemark, placemark, updateInterval, track);
  }

  /**
   * 
   * @param placemarkConnected
   *          marker for location source connected
   * @param connectionLost
   *          marker for connection to location source lost
   * @param updateInterval
   *          update interval for marker display. If 0, then screen will be
   *          updated when location is received from location source
   * @param track
   *          move center of map to received location
   */
  public NutiteqLocationMarker(final Placemark placemarkConnected, final Placemark connectionLost,
      final int updateInterval, final boolean track) {
    this.placemarkConnected = placemarkConnected;
    this.placemarkConnectionLost = connectionLost;
    this.updateInterval = updateInterval;
    this.track = track;
    lastStatus = LocationSource.STATUS_CONNECTING;
    if (updateInterval != 0) {
      timer.schedule(this, updateInterval, updateInterval);
    }
  }

  public void run() {
    update();
  }

  /**
   * Not part of public API
   */
  public void paint(final Graphics g, final MapPos middlePoint, final int screenCenterX,
      final int screenCenterY) {
    if (mapPosition == null) {
      return;
    }
    //TODO jaanus : same calculations as in Place
    final int screenX = mapPosition.getX() - middlePoint.getX() + screenCenterX
        - placemarkConnected.getAnchorX(0);
    final int screenY = mapPosition.getY() - middlePoint.getY() + screenCenterY
        - placemarkConnected.getAnchorY(0);
    if (LocationSource.STATUS_CONNECTED == locationSource.getStatus()) {
      placemarkConnected.paint(g, screenX, screenY, 0);
    } else {
      placemarkConnectionLost.paint(g, screenX, screenY, 0);
    }
  }

  public void setLocation(final WgsPoint wgsPoint) {
    if (wgsPoint == null) {
      return;
    }

    lastWgsLocation = wgsPoint;

    if (updateInterval == 0) {
      update();
    }
  }

  private void update() {
    if (mapComponent == null || lastWgsLocation == null) {
      return;
    }

    mapPosition = mapComponent.getMapPosition(lastWgsLocation);
    if (track) {
      mapComponent.setMiddlePoint(lastWgsLocation);
    } else {
      //TODO jaanus : check this paint update hack
      mapComponent.panMap(0, 0);
    }
  }

  /**
   * Not part of public API
   */
  public void setMapComponent(final BasicMapComponent mapComponent) {
    this.mapComponent = mapComponent;
  }

  /**
   * Not part of public API
   */
  public void updatePosition() {
    if (lastWgsLocation == null) {
      return;
    }
    mapPosition = mapComponent.getMapPosition(lastWgsLocation);
  }

  public void setLocationSource(final LocationSource source) {
    this.locationSource = source;
  }

  public void quit() {
    timer.cancel();
  }

  public void setTrackingEnabled(final boolean enabled) {
    track = enabled;
  }
}
