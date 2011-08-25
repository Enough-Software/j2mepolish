package com.nutiteq.location.providers;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.nutiteq.components.WgsPoint;
import com.nutiteq.location.LocationListener;
import com.nutiteq.location.LocationMarker;
import com.nutiteq.location.LocationSource;
import com.nutiteq.log.Log;

/**
 * Location provider wrapped around Android GPS implementation. At the moment
 * supports only WGS coordinates retrieval.
 */
public class AndroidGPSProvider implements LocationSource, android.location.LocationListener {
  private LocationMarker marker;
  private LocationListener[] listeners = new LocationListener[0];
  private WgsPoint wgsLocation;
  private final LocationManager locationManager;
  private int status = STATUS_CONNECTING;
  private final long updateInterval;
  private final String provider;

  /**
   * Create new location provider using GPS positioning.
   * 
   * @param locationManager
   *          android location manager
   * @param updateInterval
   *          update interval
   */
  public AndroidGPSProvider(final LocationManager locationManager, final long updateInterval) {
    this.locationManager = locationManager;
    this.updateInterval = updateInterval;
    provider = LocationManager.GPS_PROVIDER;
  }

  /**
   * Create new location provider using defined Android positioning option.
   * 
   * @param locationManager
   *          android location manager
   * @param provider
   *          used provider (either LocationManager.GPS_PROVIDER or
   *          LocationManager.NETWORK_PROVIDER)
   * @param updateInterval
   *          update interval
   */
  public AndroidGPSProvider(final LocationManager locationManager, final String provider,
      final long updateInterval) {
    this.locationManager = locationManager;
    this.provider = provider;
    this.updateInterval = updateInterval;
  }

  public void addLocationListener(final LocationListener listener) {
    final LocationListener[] newListeners = new LocationListener[listeners.length + 1];
    System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
    newListeners[listeners.length] = listener;
    listeners = newListeners;
  }

  public WgsPoint getLocation() {
    return wgsLocation;
  }

  public LocationMarker getLocationMarker() {
    return marker;
  }

  public int getStatus() {
    return status;
  }

  public void quit() {
    locationManager.removeUpdates(this);
    if (marker != null) {
      marker.quit();
    }
  }

  public void setLocationMarker(final LocationMarker marker) {
    this.marker = marker;
    marker.setLocationSource(this);
    addLocationListener(marker);
  }

  public void start() {
    locationManager.requestLocationUpdates(provider, updateInterval, 1L, this);
  }

  public void onLocationChanged(final Location location) {
    status = STATUS_CONNECTED;
    Log.info("onLocationChanged : " + location);
    if (location == null) {
      return;
    }

    wgsLocation = new WgsPoint(location.getLongitude(), location.getLatitude());
    for (int i = 0; i < listeners.length; i++) {
      listeners[i].setLocation(wgsLocation);
    }
  }

  public void onProviderDisabled(final String provider) {
    Log.info("onProviderDisabled : " + provider);
    status = STATUS_CONNECTION_LOST;
  }

  public void onProviderEnabled(final String provider) {
    Log.info("onProviderEnabled : " + provider);
    status = STATUS_CONNECTING;
  }

  public void onStatusChanged(final String provider, final int status, final Bundle extras) {
    Log.info("onStatusChanged : " + provider);
    if (status == android.location.LocationProvider.AVAILABLE) {
      this.status = STATUS_CONNECTED;
    } else {
      this.status = STATUS_CONNECTION_LOST;
    }
  }
}
