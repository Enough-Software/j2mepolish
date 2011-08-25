package com.nutiteq.polish.screens;

import javax.microedition.lcdui.CustomItem;


import com.nutiteq.MapComponent;
import com.nutiteq.components.Line;
import com.nutiteq.components.OnMapElement;
import com.nutiteq.components.Place;
import com.nutiteq.components.PlaceInfo;
import com.nutiteq.components.Polygon;
import com.nutiteq.components.WgsBoundingBox;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.components.ZoomRange;
import com.nutiteq.controls.ControlKeysHandler;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.kml.KmlService;
import com.nutiteq.listeners.MapListener;
import com.nutiteq.listeners.OnMapElementListener;
import com.nutiteq.location.LocationSource;
import com.nutiteq.log.Log;
import com.nutiteq.maps.GeoMap;
import com.nutiteq.net.DownloadStreamOpener;
import com.nutiteq.net.Downloadable;
import com.nutiteq.polish.J2MEPolishSample;
import com.nutiteq.ui.Cursor;
import com.nutiteq.ui.PanningStrategy;
import javax.microedition.lcdui.Graphics;

/**
 * A high level wrapper around {@link MapComponent}, to be used inside Forms.
 */
public class MapItem extends CustomItem implements MapListener, OnMapElementListener {
  private int elementWidth;
  private int elementHeight;
  private int lastElementWidth;
  private int lastElementHeight;
  public MapComponent mapDisplay;
  private MapListener mapListener;

  /**
   * Constructor for map component wrapper, that can be used mapScreen
   */
  public MapItem(final int width, final int height) {
    super("");
    Log.enableAll();
    elementWidth = width;
    elementHeight = height;
    lastElementWidth = width;
    lastElementHeight = height;
    mapDisplay = new MapComponent("abcdtrial", J2MEPolishSample.instance, width, height,
        J2MEPolishSample.STARTUP, 17);
    mapDisplay.setMapListener(this);
  }

  /**
   * Initialize needed resources for mapping and start internal threads. This is
   * a required step for application
   */
  public void startMapping() {
    mapDisplay.startMapping();
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
   * notifying if displayed map is complete or not.
   * 
   * @param mL
   *          Listener class reference, e.g. <code>this</code>
   */
  public void setMapListener(final MapListener mL) {
    mapListener = mL;
  }

  /**
   * Set Place listener to receive place "mouseover" and selection events
   * 
   * @param pL
   *          Listener class reference, e.g. <code>this</code>
   */
  public void setOnMapListener(final OnMapElementListener pL) {
    mapDisplay.setOnMapElementListener(pL);
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
    mapDisplay.setMiddlePoint(new WgsPoint(lon, lat), zoom);
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
   * Should default controls for moving and zooming be displayed on screen.
   * Defaults to false. Can be used with Pointer (stylus).
   * 
   * @param showControls
   *          should on-screen graphical controls be displayed
   */
  public void showDefaultControlsOnScreen(final boolean showControls) {
    //mapDisplay.showDefaultControlsOnScreen(showControls);
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
   * Get internal log for library. Log levels in release are INFO and ERROR.
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
   * Set GPS marker to be displayed on map
   * 
   * @param marker
   *          marker to be displayed
   */
  public void setLocationSource(final LocationSource marker) {
    mapDisplay.setLocationSource(marker);
  }

  /**
   * Remove displayed GPS marker
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


  public void resize(final int width, final int height) {
    elementWidth = width;
    elementHeight = height;
    mapDisplay.resize(width, height);
  }

	/*
	 * (non-Javadoc)
	 * @see com.nutiteq.listeners.OnMapElementListener#elementClicked(com.nutiteq.components.OnMapElement)
	 */
	public void elementClicked(OnMapElement element) {
	    System.out.println("element '" + element.toString() + "' clicked");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.nutiteq.listeners.OnMapElementListener#elementEntered(com.nutiteq.components.OnMapElement)
	 */
	public void elementEntered(OnMapElement element) {
	    System.out.println("element '" + element.toString() + "' entered");		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.nutiteq.listeners.OnMapElementListener#elementLeft(com.nutiteq.components.OnMapElement)
	 */
	public void elementLeft(OnMapElement element) {
	    System.out.println("element '" + element.toString() + "' left");
	}
}
