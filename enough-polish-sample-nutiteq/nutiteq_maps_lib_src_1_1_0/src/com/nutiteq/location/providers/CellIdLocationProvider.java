package com.nutiteq.location.providers;

import java.util.Timer;
import java.util.TimerTask;

import com.nutiteq.components.WgsPoint;
import com.nutiteq.location.LocationListener;
import com.nutiteq.location.LocationMarker;
import com.nutiteq.location.LocationSource;
import com.nutiteq.location.cellid.CellIdDataReader;
import com.nutiteq.location.cellid.CellIdResponseWaiter;
import com.nutiteq.location.cellid.CellIdService;
import com.nutiteq.location.cellid.OpenCellIdService;
import com.nutiteq.log.Log;

/**
 * Generic cell id location positioning handler. Supports single location query
 * or polling at given interval. Pushes retrieved location in WGS84 to
 * {@link com.nutiteq.location.LocationListener}. If location could not be
 * determined, then <code>null</code> will be pushed into marker and status
 * changes to {@link LocationSource#STATUS_CANT_LOCATE}.
 */
public class CellIdLocationProvider extends TimerTask implements CellIdResponseWaiter,
    LocationSource {
  private int status = STATUS_CONNECTING;
  private WgsPoint location;
  private final CellIdDataReader dataReader;
  private LocationMarker marker;
  private final long updateInterval;
  private final Timer timer = new Timer();
  private final CellIdService cellIdService;
  private boolean retrievingLocation;
  private LocationListener[] listeners = new LocationListener[0];

  /**
   * Create location provider for single query
   * 
   * @param dataReader
   *          data reader used for cellid data accessing
   */
  public CellIdLocationProvider(final CellIdDataReader dataReader) {
    this(dataReader, new OpenCellIdService(), 0);
  }

  public CellIdLocationProvider(final CellIdDataReader dataReader, final long updateInterval) {
    this(dataReader, new OpenCellIdService(), updateInterval);
  }

  public CellIdLocationProvider(final CellIdDataReader cellIdDataReader,
      final CellIdService cellIdService) {
    this(cellIdDataReader, cellIdService, 0);
  }

  /**
   * Create location provider that queries for location at given interval
   * 
   * @param dataReader
   *          data reader used for cellid data access
   * @param updateInterval
   *          update interval for location query
   */
  public CellIdLocationProvider(final CellIdDataReader dataReader,
      final CellIdService cellIdService, final long updateInterval) {
    this.dataReader = dataReader;
    this.cellIdService = cellIdService;
    this.updateInterval = updateInterval;
  }

  public void run() {
    pushLocation();
  }

  public void start() {
    if (updateInterval != 0) {
      timer.schedule(this, 100, updateInterval);
      return;
    }

    pushLocation();
  }

  private void pushLocation() {
    if (dataReader.getCellId() == null) {
      status = STATUS_CANT_LOCATE;
    } else if (!retrievingLocation) {
      status = STATUS_CONNECTING;
      cellIdService.setResponseWaiter(this);
      final String cellId = dataReader.getCellId();
      final String lac = dataReader.getLac();
      final String mcc = dataReader.getMcc();
      final String mnc = dataReader.getMnc();
      Log.debug("cellid > " + cellId + " : lac >" + lac + " : mcc > " + mcc + " : mnc > " + mnc);
      cellIdService.retrieveLocation(cellId, lac, mcc, mnc);
      retrievingLocation = true;
    }
  }

  public int getStatus() {
    return status;
  }

  public void notifyError() {
    status = STATUS_CANT_LOCATE;
  }

  public LocationMarker getLocationMarker() {
    return marker;
  }

  public void quit() {
    timer.cancel();
    marker.quit();
  }

  public void setLocationMarker(final LocationMarker marker) {
    this.marker = marker;
    addLocationListener(marker);
    marker.setLocationSource(this);
  }

  public void cantLocate() {
    status = STATUS_CANT_LOCATE;
  }

  public void locationRetrieved(final WgsPoint wgsPoint) {
    status = STATUS_CONNECTED;
    location = wgsPoint;
    for (int i = 0; i < listeners.length; i++) {
      listeners[i].setLocation(location);
    }
    retrievingLocation = false;
  }

  public WgsPoint getLocation() {
    return location;
  }

  public void addLocationListener(final LocationListener listener) {
    final LocationListener[] newListeners = new LocationListener[listeners.length + 1];
    System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
    newListeners[listeners.length] = listener;
    listeners = newListeners;
  }
}
