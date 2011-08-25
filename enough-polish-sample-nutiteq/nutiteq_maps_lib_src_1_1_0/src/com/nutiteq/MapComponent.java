package com.nutiteq;

import javax.microedition.midlet.MIDlet;

import com.nutiteq.cache.CachingChain;
import com.nutiteq.cache.MemoryCache;
import com.nutiteq.cache.RmsCache;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.controls.OnScreenZoomControls;
import com.nutiteq.license.LicenseKeyCheck;
import com.nutiteq.maps.MapTilesRequestor;
import com.nutiteq.maps.NutiteqStreamedMap;
import com.nutiteq.net.NutiteqDownloadCounter;
import com.nutiteq.ui.DefaultCursor;
import com.nutiteq.ui.DefaultZoomIndicator;
import com.nutiteq.ui.NutiteqDownloadDisplay;
import com.nutiteq.ui.ThreadDrivenPanning;
import com.nutiteq.utils.Utils;

/**
 * <p>
 * An extension for {@link BasicMapComponent} initialized with basic default
 * values.
 * </p>
 * 
 * <p>
 * Default caching strategy for currently supported features:
 * <ul>
 * <li>128 * 1024 bytes memory cache for map tiles and icons used in KML</li>
 * <li>A record store cache for KML icons and map tiles. Record store name
 * prefix is "ML_NETWORK_CACHE", and cache is spread over 5 stores with size of
 * 64 * 1024 bytes.</li>
 * </ul>
 * </p>
 */
public class MapComponent extends BasicMapComponent implements MapTilesRequestor {
  /**
   * Constructor for map display object. Defines available paint area (width,
   * height), middle point for map display at start (defined in WGS84), and zoom
   * level at start.
   * 
   * @param licenseKey
   *          License key issued by Nutiteq LLC
   * @param vendor
   *          Vendor name used by library for license check
   * @param appname
   *          Application name used by library for license check
   * @param width
   *          map view width
   * @param height
   *          map view height
   * @param middlePoint
   *          middle point at start (defined in WGS84)
   * @param zoom
   *          zoom level at start
   */
  public MapComponent(final String licenseKey, final String vendor, final String appname,
      final int width, final int height, final WgsPoint middlePoint, final int zoom) {
    super(licenseKey, vendor, appname, width, height, middlePoint, zoom);
  }

  /**
   * Constructor for map display object. Defines available paint area (width,
   * height), middle point for map display at start (defined in WGS84), and zoom
   * level at start.
   * 
   * @param licenseKey
   *          License key issued by Nutiteq LLC
   * @param midlet
   *          MIDlet class for the application. Used by library for reading
   *          midlet name and vendor from jad for license verification.
   * @param width
   *          map view width
   * @param height
   *          map view height
   * @param middlePoint
   *          middle point at start (defined in WGS84)
   * @param zoom
   *          zoom level at start
   */
  public MapComponent(final String licenseKey, final MIDlet midlet, final int width,
      final int height, final WgsPoint middlePoint, final int zoom) {
    this(licenseKey, midlet.getAppProperty(LicenseKeyCheck.MIDLET_VENDOR_ATTRIBUTE), midlet
        .getAppProperty(LicenseKeyCheck.MIDLET_NAME_ATTRIBUTE), width, height, middlePoint, zoom);
  }

  /**
   * Initialize needed resources for mapping and start internal threads. This is
   * a required step for application
   */
  public void startMapping() {
    if (hasMappingStarted()) {
      return;
    }

    if (!isMapSet()) {
      setMap(NutiteqStreamedMap.OPENSTREETMAP);
    }

    if (!isNetworkCacheSet()) {
      final MemoryCache memoryCache = new MemoryCache(128 * 1024);
      final RmsCache rmsCache = new RmsCache("ML_NETWORK_CACHE", 64 * 1024, 5);
      setNetworkCache(new CachingChain(new com.nutiteq.cache.Cache[] { memoryCache, rmsCache }));
    }

    if (!isPanningStrategySet()) {
      setPanningStrategy(new ThreadDrivenPanning());
    }

    super.startMapping();
  }

  /**
   * Show map zoom scale after zoom action. For default no zoom indicator is
   * shown. This method overwrites values set in
   * {@link #setZoomLevelIndicator(com.nutiteq.ui.ZoomIndicator)} with
   * {@link com.nutiteq.ui.DefaultZoomIndicator} (when showIndicator is true) or
   * with null (no indicator is used).
   * 
   * @param showInicator
   *          should the zoom indicator be shown
   */
  public void showZoomLevelIndicator(final boolean showInicator) {
    setZoomLevelIndicator(showInicator ? new DefaultZoomIndicator(0, 1) : null);
  }

  /**
   * Enable network traffic counter using default implementation (
   * {@link com.nutiteq.net.NutiteqDownloadCounter}).
   */
  public void enableDownloadCounter() {
    if (!isDownloadCounterPresent()) {
      setDownloadCounter(new NutiteqDownloadCounter());
    }
  }

  /**
   * Enable network traffic overlay using default implementation for painting (
   * {@link com.nutiteq.ui.NutiteqDownloadDisplay}).
   */
  public void enableDownloadDisplay() {
    enableDownloadCounter();
    setDownloadDisplay(new NutiteqDownloadDisplay());
  }

  /**
   * Show default zoom controls with +/- in upper left corner. Use default
   * library implementation {@link com.nutiteq.controls.OnScreenZoomControls}
   * with image '/images/m-l-controlls.png' from library resources.
   */
  public void showDefaultOnScreenZoomControls() {
    setOnScreenZoomControls(new OnScreenZoomControls(Utils
        .createImage(OnScreenZoomControls.DEFAULT_ZOOM_IMAGE)));
  }

  /**
   * <p>
   * Show default cursor (red cross) on screen.
   * </p>
   * <p>
   * <strong>Note:</note> without setting cursor here or with
   * {@link BasicMapComponent#setCursor(com.nutiteq.ui.Cursor)} places/map can't
   * be clicked on handsets without touch screen.
   * </p>
   */
  public void showDefaultCursor() {
    setCursor(new DefaultCursor(0xFFFF0000));
  }
}
