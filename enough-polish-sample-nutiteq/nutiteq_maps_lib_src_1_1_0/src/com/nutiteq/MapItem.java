package com.nutiteq;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import com.nutiteq.components.Line;
import com.nutiteq.components.OnMapElement;
import com.nutiteq.components.Place;
import com.nutiteq.components.PlaceInfo;
import com.nutiteq.components.Polygon;
import com.nutiteq.components.WgsBoundingBox;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.components.ZoomRange;
import com.nutiteq.controls.ControlKeysHandler;
import com.nutiteq.controls.OnScreenZoomControls;
import com.nutiteq.fs.FileSystem;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.kml.KmlService;
import com.nutiteq.listeners.ErrorListener;
import com.nutiteq.listeners.MapListener;
import com.nutiteq.listeners.OnMapElementListener;
import com.nutiteq.location.LocationSource;
import com.nutiteq.maps.GeoMap;
import com.nutiteq.net.DownloadCounter;
import com.nutiteq.net.DownloadStreamOpener;
import com.nutiteq.ui.Cursor;
import com.nutiteq.ui.DownloadDisplay;
import com.nutiteq.ui.PanningStrategy;
import com.nutiteq.ui.ZoomIndicator;

/**
 * A high level wrapper around {@link MapComponent}, to be used inside Forms.
 */
public class MapItem extends CustomItem implements MapListener {
  private final int elementWidth;
  private final int elementHeight;
  private int lastElementWidth;
  private int lastElementHeight;
  private final MapComponent mapDisplay;
  private MapListener mapListener;

  /**
   * Constructor for map component wrapper, that can be used in high level UI
   * components.
   * 
   * @param label
   *          name for the item, can be null
   * @param licenseKey
   *          license key issued by Nutiteq LLC
   * @param midlet
   *          MIDlet instance for the application
   * @param width
   *          component preferred width.
   * @param height
   *          component preferred height.
   * @param startPoint
   *          initial middle point on map (in WGS84)
   * @param zoom
   *          initial zoom level for display
   */
  public MapItem(final String label, final String licenseKey, final MIDlet midlet, final int width, final int height,
      final WgsPoint startPoint, final int zoom) {
    super(label);
    elementWidth = width;
    elementHeight = height;
    lastElementWidth = width;
    lastElementHeight = height;
    mapDisplay = new MapComponent(licenseKey, midlet, elementWidth, elementHeight, startPoint, zoom);
    mapDisplay.setMapListener(this);
  }

  /**
   * Constructor for map component wrapper, that can be used in high level UI
   * components.
   * 
   * @param label
   *          name for the item, can be null
   * @param licenseKey
   *          license key issued by Nutiteq LLC
   * @param vendor
   *          vendor name for license check
   * @param appname
   *          application name for license check
   * @param width
   *          component preferred width.
   * @param height
   *          component preferred height.
   * @param startPoint
   *          initial middle point on map (in WGS84)
   * @param zoom
   *          initial zoom level for display
   */
  public MapItem(final String label, final String licenseKey, final String vendor, final String appname, final int width, final int height,
      final WgsPoint startPoint, final int zoom) {
    super(label);
    elementWidth = width;
    elementHeight = height;
    lastElementWidth = width;
    lastElementHeight = height;
    mapDisplay = new MapComponent(licenseKey, vendor, appname, elementWidth, elementHeight, startPoint, zoom);
    mapDisplay.setMapListener(this);
  }

  /**
   * Initialize needed resources for mapping and start internal threads. This is
   * a required step for application
   */
  public void startMapping() {
    mapDisplay.startMapping();
  }

  protected int getMinContentHeight() {
    return elementWidth;
  }

  protected int getMinContentWidth() {
    return elementHeight;
  }

  protected int getPrefContentHeight(final int h) {
    return elementHeight;
  }

  protected int getPrefContentWidth(final int w) {
    return elementWidth;
  }

  protected void paint(final Graphics g, final int w, final int h) {
    if (w != lastElementWidth || h != lastElementHeight) {
      lastElementWidth = w;
      lastElementHeight = h;
      mapDisplay.resize(w, h);
    }

    mapDisplay.paint(g);
  }

  /**
   * Define Control Keys codes for map manipulation. For example typical Select
   * key: <code>mapItem.defineControlKey(ControlKeys.SELECT_KEY, -5);</code>
   * 
   * @param keyCode
   *          map component key code, see
   *          {@link com.nutiteq.controls.ControlKeys ControlKeys}
   * @param keyValue
   *          device key code, for some can use Canvas constants like
   *          <code>Canvas.KEY_NUM2</code>
   */
  public void defineControlKey(final int keyCode, final int keyValue) {
    mapDisplay.defineControlKey(keyCode, keyValue);
  }

  /**
   * Change control keys handler used for actions mapping.
   * 
   * @param keysHandler
   *          new keys mapping handler
   */
  public void setControlKeysHandler(final ControlKeysHandler keysHandler) {
    mapDisplay.setControlKeysHandler(keysHandler);
  }

  protected void keyPressed(final int keyCode) {
    mapDisplay.keyPressed(keyCode);
  }

  protected void keyReleased(final int keyCode) {
    mapDisplay.keyReleased(keyCode);
  }

  protected void keyRepeated(final int keyCode) {
    mapDisplay.keyRepeated(keyCode);
  }

  protected void pointerDragged(final int x, final int y) {
    mapDisplay.pointerDragged(x, y);
  }

  protected void pointerPressed(final int x, final int y) {
    mapDisplay.pointerPressed(x, y);
  }

  protected void pointerReleased(final int x, final int y) {
    mapDisplay.pointerReleased(x, y);
  }

  /**
   * Zoom in map 1 step
   */
  public void zoomIn() {
    mapDisplay.zoomIn();
  }

  /**
   * Zoom out map 1 step
   */
  public void zoomOut() {
    mapDisplay.zoomOut();
  }

  /**
   * Set map listener for receiving callback events from library.
   * &quot;needRepaint&quot; action forwarded from MapListener is used for
   * notifying if displayed map is complete or not. NB! must be AFTER
   * startMapping() call
   * 
   * @param mL
   *          Listener class reference, e.g. <code>this</code>
   */
  public void setMapListener(final MapListener mL) {
    mapListener = mL;
  }

  /**
   * Set listener for component error events (connection errors, license errors,
   * parsing errors etc
   * 
   * @param eL
   *          Listener class reference, e.g. <code>this</code>
   */
  public void setErrorListener(final ErrorListener eL) {
    mapDisplay.setErrorListener(eL);
  }

  /**
   * Set middle point of map
   * 
   * @param wgs
   *          a WgsPoint object
   * @param zoom
   *          zoom level, 0 - world to max zoom (17 typically)
   */
  public void setMiddlePoint(final WgsPoint wgs, final int zoom) {
    mapDisplay.setMiddlePoint(wgs, zoom);
  }

  /**
   * Set Center point of map
   * 
   * @param lon
   *          longitude, in WGS84 decimal degrees
   * @param lat
   *          latitude, in WGS84 decimal degrees
   * @param zoom
   *          zoom level, 0 - world to max zoom (17 typically)
   */
  public void setMiddlePoint(final double lon, final double lat, final int zoom) {
    mapDisplay.setMiddlePoint(lon, lat, zoom);
  }

  /**
   * Get current center point of map
   * 
   * @return a WgsPoint object
   */
  public WgsPoint getMiddlePoint() {
    return mapDisplay.getMiddlePoint();
  }

  /**
   * Event for clicking on map (used in MapListener)
   */
  public void mapClicked(final WgsPoint p) {
    if (mapListener != null) {
      mapListener.mapClicked(p);
    }
  }

  /**
   * Event for moving of map (used in MapListener)
   */
  public void mapMoved() {
    if (mapListener != null) {
      mapListener.mapMoved();
    }
  }

  /**
   * Event if map needs repainting (used in MapListener)
   */
  public void needRepaint(final boolean mapIsComplete) {
    repaint();
    if (mapListener != null) {
      mapListener.needRepaint(mapIsComplete);
    }
  }

  /**
   * Add Place to map
   * 
   * @param place
   *          a Place object
   */
  public void addPlace(final Place place) {
    mapDisplay.addPlace(place);
  }

  /**
   * Add elements to be displayed on map
   * 
   * @param elements
   *          elements to be added for display
   */
  public void addOnMapElements(final OnMapElement[] elements) {
    mapDisplay.addOnMapElements(elements);
  }

  /**
   * Add many places to map
   * 
   * @param places
   *          array of Place objects
   */
  public void addPlaces(final Place[] places) {
    mapDisplay.addPlaces(places);
  }

  /**
   * Remove Place from map
   * 
   * @param place
   *          the Place object to be removed
   */
  public void removePlace(final Place place) {
    mapDisplay.removePlace(place);
  }

  /**
   * remove several places from map
   * 
   * @param places
   *          array of Place objects
   */
  public void removePlaces(final Place[] places) {
    mapDisplay.removePlaces(places);
  }

  /**
   * Add single line to map
   * 
   * @param line
   *          a Line object
   */
  public void addLine(final Line line) {
    mapDisplay.addLine(line);
  }

  /**
   * Add multiple lines to map
   * 
   * @param lines
   *          array of Line object
   * @see Line#Line(WgsPoint[] points, com.nutiteq.components.LineStyle style)
   */
  public void addLines(final Line[] lines) {
    mapDisplay.addLines(lines);
  }

  /**
   * Remove previously added line
   * 
   * @param line
   *          line to be removed
   */
  public void removeLine(final Line line) {
    mapDisplay.removeLine(line);
  }

  /**
   * Remove multiple lines
   * 
   * @param lines
   *          lines to be removed
   */
  public void removeLines(final Line[] lines) {
    mapDisplay.removeLines(lines);
  }

  /**
   * Set zoom controls to be displayed on screen and used for touch screen
   * zooming.
   * 
   * @param controls
   *          zoom controls to be used
   */
  public void setOnScreenZoomControls(final OnScreenZoomControls controls) {
    mapDisplay.setOnScreenZoomControls(controls);
  }

  /**
   * Show map zoom scale after zoom action. Defaults to false.
   * 
   * @param showInicator
   *          should the zoom indicator be shown
   */
  public void showZoomLevelIndicator(final boolean showInicator) {
    mapDisplay.showZoomLevelIndicator(showInicator);
  }

  /**
   * Get Bounding Box of current map view
   * 
   * @return a Bounding Box object
   */
  public WgsBoundingBox getBoundingBox() {
    return mapDisplay.getBoundingBox();
  }

  /**
   * Set bounding box for the view. Finds the best zoom level for the bounding
   * box view.
   * 
   * @param bBox
   *          are to be displayed (in WGS84 coordinates)
   */
  public void setBoundingBox(final WgsBoundingBox bBox) {
    mapDisplay.setBoundingBox(bBox);
  }

  /**
   * get max and min zoom of current map (typically 0...18)
   * 
   * @return a ZoomRange object
   */
  public ZoomRange getZoomRange() {
    return mapDisplay.getZoomRange();
  }

  /**
   * Set map zoom without changing position.
   * 
   * @param newZoom
   *          new zoom level
   */
  public void setZoom(final int newZoom) {
    mapDisplay.setZoom(newZoom);
  }

  /**
   * Get the current zoom level.
   * 
   * @return current zoom level
   */
  public int getZoom() {
    return mapDisplay.getZoom();
  }

  /**
   * Add KML layer to the map Usage example:
   * <code>mapItem.addKmlService(new KmlUrlReader("http://www.panoramio.com/panoramio.kml?LANG=en_US.utf8",true));</code>
   * 
   * @param service
   *          Reference to KML Service, can be own implementation or use
   *          KmlUrlReader
   * @see com.nutiteq.kml.KmlUrlReader#KmlUrlReader(String, boolean)
   */
  public void addKmlService(final KmlService service) {
    mapDisplay.addKmlService(service);
  }

  /**
   * List currently added KML Services
   * 
   * @return array of KmlServices
   */

  public KmlService[] getKmlServices() {
    return mapDisplay.getKmlServices();
  }

  /**
   * Remove previously added kml service
   * 
   * @param service
   *          service to be removed
   */
  public void removeKmlService(final KmlService service) {
    mapDisplay.removeKmlService(service);
  }

  /**
   * Stop threads started by MapComponent. Called before application exit to
   * clean library resources.
   */
  public void stopMapping() {
    mapDisplay.stopMapping();
  }

  /**
   * Change base map
   * 
   * @param newMap
   *          reference to the map object
   * @see com.nutiteq.maps.CloudMade#CloudMade(String, int, int)
   */
  public void setMap(final GeoMap newMap) {
    mapDisplay.setMap(newMap);
  }

  /**
   * Retrieve currently used map.
   * 
   * @return currently displayed map
   */
  public GeoMap getMap() {
    return mapDisplay.getMap();
  }

  /**
   * Get additional info for internally handled (retrieved from kml service)
   * objects.
   * 
   * @param place
   *          place associated with internal data
   * @return info object with additional data
   */
  public PlaceInfo getAdditionalInfo(final Place place) {
    return mapDisplay.getAdditionalInfo(place);
  }

  /**
   * Replace the default cursor.
   * 
   * @param newCursor
   *          cursor implementation
   */
  public void setCursor(final Cursor newCursor) {
    mapDisplay.setCursor(newCursor);
  }

  /**
   * Remove given elements from map display
   * 
   * @param elements
   *          elements to be removed
   */
  public void removeOnMapElements(final OnMapElement[] elements) {
    mapDisplay.removeOnMapElements(elements);
  }

  /**
   * Get internal log for library.
   * 
   * @return internal log
   */
  public String getLibraryLog() {
    return mapDisplay.getLibraryLog();
  }

  /**
   * Set download stream opener, that creates connections to downloaded
   * resources
   * 
   * @param opener
   *          opener to be used for resources reading
   */
  public void setDownloadStreamOpener(final DownloadStreamOpener opener) {
    mapDisplay.setDownloadStreamOpener(opener);
  }

  /**
   * Set panning strategy for map component. If not set, the strategy will
   * default to {@link com.nutiteq.ui.ThreadDrivenPanning}
   * 
   * @param panningStrategy
   *          new panning strategy
   */
  public void setPanningStrategy(final PanningStrategy panningStrategy) {
    mapDisplay.setPanningStrategy(panningStrategy);
  }

  /**
   * Set search strategy for map tile.
   * 
   * @param tileSearchStrategy
   *          search strategy to be used
   */
  public void setTileSearchStrategy(final GeoMap[] tileSearchStrategy) {
    mapDisplay.setTileSearchStrategy(tileSearchStrategy);
  }

  /**
   * Set location source with GPS marker to be displayed on map
   * 
   * @param marker
   *          source to be used
   */
  public void setLocationSource(final LocationSource marker) {
    mapDisplay.setLocationSource(marker);
  }

  /**
   * Remove used location source
   */
  public void removeLocationSource() {
    mapDisplay.removeLocationSource();
  }

  /**
   * Set cache for networking. Currently cached data is:
   * <ul>
   * <li>map tiles to rms level</li>
   * <li>kml icons to memory and rms</li>
   * </ul>
   * 
   * @param cache
   */
  public void setNetworkCache(final com.nutiteq.cache.Cache cache) {
    mapDisplay.setNetworkCache(cache);
  }

  public void addPolygon(final Polygon polygon) {
    mapDisplay.addPolygon(polygon);
  }

  public void addPolygons(final Polygon[] polygons) {
    mapDisplay.addPolygons(polygons);
  }

  public void removePolygon(final Polygon polygon) {
    mapDisplay.removePolygon(polygon);
  }

  public void removePolygons(final Polygon[] polygons) {
    mapDisplay.removePolygons(polygons);
  }

  /**
   * Enqueue new download task to be executed by library.
   * 
   * @param downloadable
   *          resource to be downloaded
   * @param cacheLevel
   *          at which cache levels should response be cached
   */
  public void enqueueDownload(final ResourceRequestor downloadable, final int cacheLevel) {
    mapDisplay.enqueueDownload(downloadable, cacheLevel);
  }

  /**
   * Set zoom indicator to be painted on display
   * 
   * @param zoomIndicator
   *          zoom indicator to use
   */
  public void setZoomLevelIndicator(final ZoomIndicator zoomIndicator) {
    mapDisplay.setZoomLevelIndicator(zoomIndicator);
  }

  /**
   * Enable network traffic counter using default implementation (
   * {@link com.nutiteq.net.NutiteqDownloadCounter}).
   */
  public void enableDownloadCounter() {
    mapDisplay.enableDownloadCounter();
  }

  /**
   * Set download counter used for gathering information about network traffic
   * 
   * @param downloadCounter
   *          implementation used
   */
  public void setDownloadCounter(final DownloadCounter downloadCounter) {
    mapDisplay.setDownloadCounter(downloadCounter);
  }

  public DownloadCounter getDownloadCounter() {
    return mapDisplay.getDownloadCounter();
  }

  /**
   * Enable network traffic overlay using default implementation for painting (
   * {@link com.nutiteq.ui.NutiteqDownloadDisplay}).
   */
  public void enableDownloadDisplay() {
    mapDisplay.enableDownloadDisplay();
  }

  /**
   * Set used implementation for network traffic display on map.
   * 
   * @param display
   *          display used for info show
   */
  public void setDownloadDisplay(final DownloadDisplay display) {
    mapDisplay.setDownloadDisplay(display);
  }

  public void setFileSystem(final FileSystem fs) {
    mapDisplay.setFileSystem(fs);
  }

  public void setOnMapElementListener(final OnMapElementListener listener) {
    mapDisplay.setOnMapElementListener(listener);
  }
}
