package com.nutiteq.location.providers;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;

import com.nutiteq.components.WgsPoint;
import com.nutiteq.location.LocationListener;
import com.nutiteq.location.LocationMarker;
import com.nutiteq.location.LocationSource;
import com.nutiteq.log.Log;

/**
 * This is a wrapper class around JSR179
 */
public class LocationAPIProvider implements Runnable, LocationSource,
    javax.microedition.location.LocationListener {
  private Criteria criteria;
  private LocationProvider locationProvider;
  private int status;
  private LocationMarker marker;
  private final int updateInterval;
  private LocationListener[] listeners = new LocationListener[0];
  private WgsPoint wgsLocation;
  private final int accuracy;

  /**
   * Create location provider with given update interval (in milliseconds) and
   * without any accuracy requirements.
   * 
   * @param updateInterval
   *          location update time in milliseconds
   */
  public LocationAPIProvider(final long updateInterval) {
    this(updateInterval, Criteria.NO_REQUIREMENT);
  }

  /**
   * <p>
   * Create location provider with given update interval (in milliseconds) and
   * without an accuracy requirement.
   * </p>
   * <p>
   * On <strong>Nokia</strong> phones this could trigger assisted positioning
   * for faster location finding.
   * </p>
   * 
   * @param updateInterval
   *          location update time in milliseconds
   * @param accuracy
   *          accuracy for positioning (suggested value would be starting from
   *          500)
   */
  public LocationAPIProvider(final long updateInterval, final int accuracy) {
    this.accuracy = accuracy;
    this.updateInterval = (int) (updateInterval / 1000);
    status = STATUS_CONNECTING;
  }

  public void start() {
    final Thread t = new Thread(this);
    t.start();
  }

  public void run() {
    criteria = new Criteria();
    criteria.setHorizontalAccuracy(accuracy);
    try {
      locationProvider = LocationProvider.getInstance(criteria);
      locationProvider
          .setLocationListener(this, updateInterval, updateInterval, updateInterval / 2);
    } catch (final LocationException e) {
      Log.printStackTrace(e);
      Log.error("LocationAPIProvider initialization failed: " + e.getMessage());
    }
  }

  public WgsPoint getLocation() {
    return wgsLocation;
  }

  public int getStatus() {
    return status;
  }

  public LocationMarker getLocationMarker() {
    return marker;
  }

  public void quit() {
    final Thread t = new Thread() {
      public void run() {
        locationProvider.setLocationListener(null, 0, 0, 0);
      }
    };
    t.start();
    marker.quit();
  }

  public void setLocationMarker(final LocationMarker marker) {
    this.marker = marker;
    addLocationListener(marker);
    this.marker.setLocationSource(this);
  }

  public void addLocationListener(final LocationListener listener) {
    final LocationListener[] newListeners = new LocationListener[listeners.length + 1];
    System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
    newListeners[listeners.length] = listener;
    listeners = newListeners;
  }

  public void locationUpdated(final LocationProvider provider, final Location location) {
    if (provider.getState() != LocationProvider.AVAILABLE) {
      status = STATUS_CONNECTION_LOST;
    } else {
      try {
        status = STATUS_CONNECTED;
        wgsLocation = new WgsPoint(location.getQualifiedCoordinates().getLongitude(), location
            .getQualifiedCoordinates().getLatitude());
        for (int i = 0; i < listeners.length; i++) {
          listeners[i].setLocation(wgsLocation);
        }
      } catch (final Exception e) {
        status = STATUS_CANT_LOCATE;
      }
    }
  }

  public void providerStateChanged(final LocationProvider provider, final int newState) {
    if (provider.getState() != LocationProvider.AVAILABLE) {
      status = STATUS_CONNECTION_LOST;
    } else {
      status = STATUS_CONNECTED;
    }
  }
}
