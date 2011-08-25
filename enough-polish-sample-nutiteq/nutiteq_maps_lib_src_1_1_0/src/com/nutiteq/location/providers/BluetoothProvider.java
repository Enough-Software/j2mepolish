package com.nutiteq.location.providers;

import java.io.IOException;
import java.io.InputStream;

import com.nutiteq.components.WgsPoint;
import com.nutiteq.location.LocationListener;
import com.nutiteq.location.LocationMarker;
import com.nutiteq.location.LocationSource;
import com.nutiteq.log.Log;
import com.nutiteq.utils.IOUtils;
import com.nutiteq.utils.Utils;

/**
 * Provider reading NMEA data from given connection source.
 */
public class BluetoothProvider extends Thread implements LocationSource {
  private static final String RMC_SENTANCE_START = "$GPRMC";
  private static final String RMC_STATUS_ACTIVE = "A";
  private boolean running;
  private final String url;
  private InputStream is;
  private boolean connected;
  private WgsPoint location;
  private LocationMarker marker;
  private final LocationDataConnectionProvider connectionProvider;
  private LocationDataConnection connection;
  private boolean firstTry = true;
  private LocationListener[] listeners = new LocationListener[0];

  /**
   * Create provider
   * 
   * @param connectionProvider
   *          object for connection handling
   * @param url
   *          url for connection
   */
  public BluetoothProvider(final LocationDataConnectionProvider connectionProvider, final String url) {
    this.connectionProvider = connectionProvider;
    this.url = url;
  }

  public WgsPoint getLocation() {
    return location;
  }

  public int getStatus() {
    return connected ? STATUS_CONNECTED : STATUS_CONNECTING;
  }

  public void run() {
    running = true;
    while (running) {
      connect();
      while (running && connected) {
        final String line = readLine();
        if (line.length() == 0) {
          disconnect();
          continue;
        }
        Log.debug(line);
        parseData(line);
        for (int i = 0; i < listeners.length; i++) {
          listeners[i].setLocation(location);
        }
        sleepSomeTime(10);
      }
      sleepSomeTime(5000);
    }
    Log.debug("BT done");
  }

  private void parseData(final String line) {
    if (line.length() == 0) {
      return;
    }

    final String[] split = Utils.split(line, ",");
    if (RMC_SENTANCE_START.equals(split[0]) && RMC_STATUS_ACTIVE.equals(split[2])) {
      final double lat = Utils.parseDecimalDegree(split[3], split[4]);
      final double lon = Utils.parseDecimalDegree(split[5], split[6]);
      location = new WgsPoint(lon, lat);
    }
  }

  private void sleepSomeTime(final long howLong) {
    if (!running) {
      return;
    }
    synchronized (this) {
      try {
        Log.debug("" + howLong);
        wait(howLong);
      } catch (final InterruptedException ignore) {
      }
    }
  }

  private String readLine() {
    try {
      final StringBuffer sb = new StringBuffer();
      int c = 0;
      for (int i = 0;; i++) {
        c = is.read();
        if (c == -1 || c == '\0' || c == '\n') {
          break;
        }
        if (c != '\r') {
          sb.append((char) c);
        }
      }
      return sb.toString();
    } catch (final Exception ex) {
      Log.error("Error receiving data");
      Log.printStackTrace(ex);
      // disconnect if an error occurs
      disconnect();
      return "";
    }
  }

  public void disconnect() {
    Log.info("BT disconnect");
    IOUtils.closeStream(is);
    connection.disconnect();
    connected = false;
  }

  private void connect() {
    Log.info("Connect");
    try {
      connection = connectionProvider.openConnection(url);
      is = connection.openInputStream();
      connected = true;
    } catch (final IOException e) {
      Log.error("BT connect " + e.getMessage());
      Log.printStackTrace(e);
      if (firstTry) {
        quit();
      }
    }
    firstTry = false;
    Log.info("End connect");
  }

  public LocationMarker getLocationMarker() {
    return marker;
  }

  public void quit() {
    if (!running) {
      return;
    }
    running = false;
    disconnect();
    marker.quit();
    synchronized (this) {
      notify();
    }
  }

  public void setLocationMarker(final LocationMarker marker) {
    this.marker = marker;
    marker.setLocationSource(this);
    addLocationListener(marker);
  }

  public void addLocationListener(final LocationListener listener) {
    final LocationListener[] newListeners = new LocationListener[listeners.length + 1];
    System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
    newListeners[listeners.length] = listener;
    listeners = newListeners;
  }
}
