package com.nutiteq;

import java.util.Enumeration;
import java.util.Timer;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

import com.mgmaps.cache.ScreenCache;
import com.mgmaps.utils.Tools;
import com.nutiteq.cache.Cache;
import com.nutiteq.components.ImageBuffer;
import com.nutiteq.components.KmlPlace;
import com.nutiteq.components.Label;
import com.nutiteq.components.Line;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.MapTile;
import com.nutiteq.components.OnMapElement;
import com.nutiteq.components.Place;
import com.nutiteq.components.PlaceInfo;
import com.nutiteq.components.PlaceLabel;
import com.nutiteq.components.Point;
import com.nutiteq.components.Polygon;
import com.nutiteq.components.Rectangle;
import com.nutiteq.components.TileMapBounds;
import com.nutiteq.components.WgsBoundingBox;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.components.ZoomRange;
import com.nutiteq.controls.ControlKeys;
import com.nutiteq.controls.ControlKeysHandler;
import com.nutiteq.controls.OnScreenZoomControls;
import com.nutiteq.controls.UserDefinedKeysMapping;
import com.nutiteq.core.MappingCore;
import com.nutiteq.fs.FileSystem;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.kml.KmlService;
import com.nutiteq.kml.KmlServicesHandler;
import com.nutiteq.license.License;
import com.nutiteq.license.LicenseKeyCheck;
import com.nutiteq.listeners.ErrorListener;
import com.nutiteq.listeners.MapListener;
import com.nutiteq.listeners.OnMapElementListener;
import com.nutiteq.location.LocationSource;
import com.nutiteq.log.Log;
import com.nutiteq.maps.GeoMap;
import com.nutiteq.maps.MapTileOverlay;
import com.nutiteq.maps.MapTilesRequestor;
import com.nutiteq.net.DownloadCounter;
import com.nutiteq.net.DownloadHandler;
import com.nutiteq.net.DownloadStreamOpener;
import com.nutiteq.task.MapTileSearchTask;
import com.nutiteq.task.Task;
import com.nutiteq.task.TasksRunner;
import com.nutiteq.task.TileOverlayRetriever;
import com.nutiteq.ui.Cursor;
import com.nutiteq.ui.DisplayUpdater;
import com.nutiteq.ui.DownloadDisplay;
import com.nutiteq.ui.ImageProcessor;
import com.nutiteq.ui.Pannable;
import com.nutiteq.ui.PanningStrategy;
import com.nutiteq.ui.RepaintTimerTask;
import com.nutiteq.ui.ZoomDelayTimerTask;
import com.nutiteq.ui.ZoomIndicator;
import com.nutiteq.ui.ZoomIndicatorCheckTask;
import com.nutiteq.utils.Utils;

/**
 * <p>
 * Main class for integration between implementing application and maps library
 * without any default values.
 * </p>
 * <p>
 * Best practice for map display handling would be using one instance trough
 * whole application life circle. This means calling <code>startMapping()</code>
 * and <code>stopMapping()</code> only once.
 * </p>
 * <p>
 * <strong>Required steps for basic functionality initialization:</strong>
 * <ul>
 * <li>set used map by calling {@link #setMap(GeoMap)}</li>
 * <li>for panning/zooming with keys define control keys by setting keys handler
 * with {@link #setControlKeysHandler(ControlKeysHandler)} or define individual
 * keys by calling {@link #defineControlKey(int, int)}</li>
 * <li>set {@link com.nutiteq.ui.PanningStrategy} if panning with keys will be
 * used</li>
 * <li>set {@link com.nutiteq.listeners.MapListener} for receiving map view
 * update notifications</li>
 * </ul>
 * </p>
 */
public class BasicMapComponent extends BaseMapComponent implements MapTilesRequestor, Pannable,
    DisplayUpdater, DownloadHandler {
  private static final int REPAINT_CALL_DELAY = 1000;
  private int displayWidth;
  private int displayHeight;

  private int displayCenterX;
  private int displayCenterY;

  private int displayX;
  private int displayY;

  private MapPos middlePoint;

  // tile display
  private int tileX;
  private int tileY;
  private int tileW;
  private int tileH;

  private static final int[][] MOVES = { { 0, -1 }, { 0, +1 }, { -1, 0 }, { +1, 0 } };
  private static final int MOVE_UP = 0;
  private static final int MOVE_DOWN = 1;
  private static final int MOVE_LEFT = 2;
  private static final int MOVE_RIGHT = 3;

  /**
   * Pixel tolerance used for stylus phones to distinguish between screen click
   * and drag events
   */
  public static final int STYLUS_CLICK_TOLERANCE = 5;
  /**
   * Pixel tolerance used for phones without stylus to distinguish between
   * screen click and drag events
   */
  public static final int FINGER_CLICK_TOLERANCE = 10;

  private ControlKeysHandler controlKeysHandler;
  private Cursor cursor;

  private GeoMap displayedMap;

  private KmlServicesHandler kmlServicesHandler;
  private com.nutiteq.cache.Cache networkCache;

  private final TasksRunner taskRunner;

  private ScreenCache screenCache;

  private MapListener mapListener;
  private OnMapElementListener onMapElementListener;
  private ErrorListener errorListener;

  private ImageBuffer mapBuffer;
  private int mapBufferMoveX;
  private int mapBufferMoveY;

  private Image screenBuffer;
  private Graphics screenBufferGraphics;

  private Image zoomBuffer;
  private Graphics zoomBufferGraphics;
  private int zoomBufferX;
  private int zoomBufferY;

  private final Vector displayedElements = new Vector();

  private OnMapElement centeredElement;
  private MapPos centeredClickMapPos;

  private int pointerX = -1;
  private int pointerY = -1;
  private int pointerXMove = 0;
  private int pointerYMove = 0;
  private boolean dragged;

  private OnScreenZoomControls onScreenZoomControls;

  private PanningStrategy panning;

  private final Vector neededTiles = new Vector();

  private final Timer timer;
  private RepaintTimerTask repaintTask;

  private ZoomDelayTimerTask zoomDelay;
  private ZoomIndicator zoomLevelIndicator;
  //TODO jaanus : maybe can find a better way then tasks
  private ZoomIndicatorCheckTask indicatorCheck;

  private long lastRepaintCallTime;
  private License license = License.LICENSE_CHECKING;

  private boolean isMapComplete;
  private long lastZoomCall;

  private final Vector changedAreas = new Vector();

  private LocationSource locationSource;
  private GeoMap[] tileSearchStrategy;

  private final LicenseKeyCheck licenseKeyCheck;

  private boolean paintingScreen;
  private TileMapBounds tileMapBounds;

  private boolean mappingStarted;

  private final WgsPoint startWgs;
  private final int startZoom;
  private DownloadCounter downloadCounter;
  private DownloadDisplay downloadDisplay;
  private boolean looseFocusOnDrag;
  private int touchTolerance = STYLUS_CLICK_TOLERANCE;
  private final Vector overlayQueue = new Vector();

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
  public BasicMapComponent(final String licenseKey, final String vendor, final String appname,
      final int width, final int height, final WgsPoint middlePoint, final int zoom) {
    displayWidth = width;
    displayHeight = height;
    displayCenterX = width / 2;
    displayCenterY = height / 2;
    displayX = 0;
    displayY = 0;

    taskRunner = MappingCore.getInstance().getTasksRunner();

    timer = new Timer();

    startWgs = middlePoint;
    startZoom = zoom;

    licenseKeyCheck = new LicenseKeyCheck(this, licenseKey, appname, vendor);
  }

  private void createScreenCache() {
    screenCache = com.mgmaps.cache.ScreenCache.createScreenCache((2 + ((displayWidth - 2) / displayedMap.getTileSize()))
        * (2 + ((displayHeight - 2) / displayedMap.getTileSize())));
  }

  /**
   * Enables processing of all map images. Applied to global cache, so works for all images, not just one map service
   * Set to null to disable it
   * @param processor specific processor. Try e.g. NightModeImageProcessor()
   */
  public void setImageProcessor(final ImageProcessor processor){
      com.mgmaps.cache.ScreenCache.getInstance().setImageProcessor(processor);
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
  public BasicMapComponent(final String licenseKey, final MIDlet midlet, final int width,
      final int height, final WgsPoint middlePoint, final int zoom) {
    this(licenseKey, midlet.getAppProperty(LicenseKeyCheck.MIDLET_VENDOR_ATTRIBUTE), midlet
        .getAppProperty(LicenseKeyCheck.MIDLET_NAME_ATTRIBUTE), width, height, middlePoint, zoom);
  }

  /**
   * Initialize needed resources for mapping and start internal threads. This is
   * a required step for application
   */
  public void startMapping() {
    if (mappingStarted) {
      return;
    }
    mappingStarted = true;
    
    initializeMiddlePoint();

    mapBuffer = new ImageBuffer(2, displayWidth, displayHeight);

    screenBuffer = Image.createImage(displayWidth, displayHeight);
    screenBufferGraphics = screenBuffer.getGraphics();

    zoomBuffer = Image.createImage(displayWidth, displayHeight);
    zoomBufferGraphics = zoomBuffer.getGraphics();

    tileMapBounds = displayedMap.getTileMapBounds(middlePoint.getZoom());

    if (networkCache != null) {
      networkCache.initialize();
      taskRunner.setNetworkCache(networkCache);
    }

    if (downloadCounter != null) {
      taskRunner.setDownloadCounter(downloadCounter);
    }

    taskRunner.startWorker();

    taskRunner.setLicenceKeyCheck(licenseKeyCheck);

    recalculateMapPosition(displayedElements);
    computeTilesToDisplay();
    enqueueTiles();

    //maybe map was not set before. initialize now. just in case :)
    setZoomLevelIndicator(zoomLevelIndicator);

    mapMoved(); // to force KML reading, if KML was added before startMapping()

  }

  protected void initializeMiddlePoint() {
    if (middlePoint == null) {
      middlePoint = displayedMap.wgsToMapPos(startWgs.toInternalWgs(), checkValidZoom(startZoom,
          displayedMap));
    }
  }

  private void initializeKmlServiceshandler() {
    kmlServicesHandler = new KmlServicesHandler(this, taskRunner);
  }

  /**
   * Changes map view size
   * 
   * @param width
   *          new map view width
   * @param height
   *          new map view height
   */
  public void resize(final int width, final int height) {
    displayWidth = width;
    displayHeight = height;
    displayCenterX = width / 2;
    displayCenterY = height / 2;

    fullScreenUpdate();

    createScreenCache();

    // if mapping started
    if (mapBuffer != null) {
      mapBuffer.resize(width, height);
      zoomBuffer = Utils.resizeImageAndCopyPrevious(width, height, zoomBuffer);
      zoomBufferGraphics = zoomBuffer.getGraphics();
      screenBuffer = Utils.resizeImageAndCopyPrevious(width, height, screenBuffer);
      screenBufferGraphics = screenBuffer.getGraphics();

      computeTilesToDisplay();
      enqueueTiles();
    }
  }

  public void fullScreenUpdate() {
    changedAreas.setSize(0);
    changedAreas.addElement(getScreenAreaOnMap());
  }

  private Rectangle getScreenAreaOnMap() {
    return new Rectangle(middlePoint.getX() - displayCenterX, middlePoint.getY() - displayCenterY,
        displayWidth, displayHeight);
  }

  /**
   * Return current view width
   * 
   * @return view width
   */
  public int getWidth() {
    return displayWidth;
  }

  /**
   * Return current view height
   * 
   * @return view height
   */
  public int getHeight() {
    return displayHeight;
  }

  /**
   * Define middle point location in WGS84
   * 
   * @param lon
   *          degrees in wgs
   * @param lat
   *          degrees in wgs
   * @param zoom
   *          zoom level to be displayed
   */
  public void setMiddlePoint(final double lon, final double lat, final int zoom) {
    setMiddlePoint(new WgsPoint(lon, lat), zoom);
  }

  /**
   * Define middle point location in WGS84
   * 
   * @param point
   *          coordinates in WGS84
   * @param zoom
   *          zoom level to be displayed
   */
  public void setMiddlePoint(final WgsPoint point, final int zoom) {
    if (point == null) {
      return;
    }
    final int newZoom = checkValidZoom(zoom, displayedMap);
    middlePoint = displayedMap.wgsToMapPos(point.toInternalWgs(), newZoom);
    tileMapBounds = displayedMap.getTileMapBounds(middlePoint.getZoom());
    recalculateMapPosition(displayedElements);
    fullScreenUpdate();
    computeTilesToDisplay();
    enqueueTiles();
    mapMoved();
    repaint();
  }

  /**
   * Define middle point in WGS84, without changing zoom level
   * 
   * @param wgs
   *          coordinates in WGS84
   */
  public void setMiddlePoint(final WgsPoint wgs) {
    moveMap(wgs);
  }

  private int checkValidZoom(final int zoom, final GeoMap map) {
    if (zoom >= map.getMinZoom() && zoom <= map.getMaxZoom()) {
      return zoom;
    }

    if (zoom > map.getMaxZoom()) {
      return map.getMaxZoom();
    }

    return map.getMinZoom();
  }

  /**
   * Paint the map component. As default view will be painted to 0, 0 on
   * graphics object. This can be changed with
   * {@link #setScreenPosition(int, int)}
   * 
   * @param g
   *          graphics object provided by implementing application
   */
  public void paint(final Graphics g) {
    paintAt(g, displayX, displayY);
  }

  /**
   * Paint map view to give position on screen
   * 
   * @param g
   *          graphics object to paint on
   * @param paintX
   *          screen position x
   * @param paintY
   *          screen position y
   */
  public void paintAt(final Graphics g, final int paintX, final int paintY) {
    if (g == null || screenBuffer == null || mapBuffer == null) {
      return;
    }

    if (!license.isValid()) {
      g.setClip(paintX, paintY, displayWidth, displayHeight);
      g.setColor(0xFFFFFFFF);
      g.fillRect(paintX, paintY, displayWidth, displayHeight);
      final Font font = Font.getDefaultFont();
      final int textX = (displayWidth - font.stringWidth(license.getMessage())) / 2;
      final int textY = (displayHeight - font.getHeight()) / 2;
      g.setColor(0xFFFF0000);
      g.drawString(license.getMessage(), textX > 0 ? textX : 0, textY > 0 ? textY : 0, Graphics.TOP
          | Graphics.LEFT);
      return;
    }

    paintScreen(screenBufferGraphics);
    g.setClip(paintX, paintY, displayWidth, displayHeight);
    g.drawImage(screenBuffer, paintX, paintY, Graphics.TOP | Graphics.LEFT);
  }

  private void paintScreen(final Graphics g) {
    paintingScreen = true;
    g.setClip(0, 0, displayWidth, displayHeight);
    final Rectangle changed = paintMap(mapBuffer);

    OnMapElement nextCentered;
    if (cursor != null) {
      nextCentered = centeredPlaceCheck(displayedElements);

      handleActiveIconChange(centeredElement, nextCentered);
    } else {
      nextCentered = centeredElement;
    }
    paintPlaces(mapBuffer, displayedElements, changed, nextCentered);

    g.drawImage(mapBuffer.getFrontImage(), 0, 0, Graphics.TOP | Graphics.LEFT);

    //paint map copyright
    displayedMap.getCopyright().paint(g, displayWidth, displayHeight);

    //TODO jaanus : this should be removed after places go to map buffer
    g.setClip(0, 0, displayWidth, displayHeight);
    if (locationSource != null) {
      locationSource.getLocationMarker().paint(g, middlePoint, displayCenterX, displayCenterY);
    }

    if (cursor != null) {
      cursor.paint(g, displayWidth / 2, displayHeight / 2, displayWidth, displayHeight);
    }

    if (onScreenZoomControls != null) {
      onScreenZoomControls.paint(g, displayWidth, displayHeight);
    }

    if (zoomLevelIndicator != null && zoomLevelIndicator.isVisible()) {
      zoomLevelIndicator.paint(g, middlePoint.getZoom(), displayWidth, displayHeight);
    }

    if (downloadDisplay != null && downloadDisplay.isVisible()) {
      downloadDisplay.paint(g, displayWidth, displayHeight);
    }

    paintTitle(g, centeredElement);

    paintingScreen = false;
  }

  private void paintTitle(final Graphics g, final OnMapElement centered) {
    if (centered == null) {
      return;
    }

    final Label label = centered.getLabel();
    if (label == null) {
      return;
    }

    g.setClip(0, 0, displayWidth, displayHeight);

    final int placeScreenX;
    final int placeScreenY;

    //place with point
    //place with poly/line
    //poly/line

    if (centered instanceof Place && ((Place) centered).getMapPosition() != null) {
      final Place cPlace = (Place) centered;
      placeScreenX = cPlace.getMapPosition().getX() - middlePoint.getX() + displayWidth / 2;
      placeScreenY = cPlace.getMapPosition().getY() - middlePoint.getY() + displayHeight / 2;
    } else if (cursor != null) {
      //on line/polygon put the label where cursor is
      final Point clickPoint = cursor.getPointOnDisplay(displayWidth, displayHeight);
      placeScreenX = clickPoint.getX();
      placeScreenY = clickPoint.getY();
    } else {
      placeScreenX = centeredClickMapPos.getX() - middlePoint.getX() + displayCenterX;
      placeScreenY = centeredClickMapPos.getY() - middlePoint.getY() + displayCenterY;
    }

    //TODO jaanus : resolve this hack
    if (label instanceof PlaceLabel) {
      ((PlaceLabel) label).setZoom(middlePoint.getZoom());
    }

    label.paint(g, placeScreenX, placeScreenY, displayWidth, displayHeight);
  }

  private OnMapElement centeredPlaceCheck(final Vector places) {
    if (cursor == null || !mappingStarted || middlePoint == null) {
      return null;
    }

    OnMapElement centered = null;
    for (int i = 0; i < places.size(); i++) {
      final OnMapElement current = (OnMapElement) places.elementAt(i);

      final Point cursorOnScreen = cursor.getPointOnDisplay(displayWidth, displayHeight);
      final MapPos cursorOnMap = new MapPos(middlePoint.getX() - displayCenterX
          + cursorOnScreen.getX(), middlePoint.getY() - displayCenterY + cursorOnScreen.getY(),
          middlePoint.getZoom());

      final boolean isCentered = current.isCentered(cursorOnMap);

      if (isCentered) {
        centered = current;
      }

    }

    return centered;
  }

  private void paintPlaces(final ImageBuffer buffer, final Vector places,
      final Rectangle changedArea, final OnMapElement centered) {
    if (!mappingStarted || middlePoint == null) {
      return;
    }

    final Graphics g = buffer.getFrontGraphics();
    final Rectangle screenChange = Utils.areaToScreen(changedArea, middlePoint.getX()
        - displayCenterX, middlePoint.getY() - displayCenterY, displayWidth, displayHeight);

    for (int i = 0; i < places.size(); i++) {
      final OnMapElement current = (OnMapElement) places.elementAt(i);

      //is element is in changed area
      if (current.isVisible(changedArea.getX(), changedArea.getY(), changedArea.getWidth(),
          changedArea.getHeight(), middlePoint.getZoom())) {
        g.setClip(screenChange.getX(), screenChange.getY(), screenChange.getWidth(), screenChange
            .getHeight());
        current.paint(g, middlePoint, displayCenterX, displayCenterY, changedArea);
      }
    }

    //TODO jaanus : take this one level up
    //paint centered on top of other elements
    if (centered != null && (centered instanceof Place)) {
      g.setClip(0, 0, displayWidth, displayHeight);
      centered.paint(g, middlePoint, displayCenterX, displayCenterY, changedArea);
    }

    if (cursor == null) {
      //without cursor nothing can be done here
      return;
    }

    if (centeredElement != null && centeredElement != centered) {
      if (onMapElementListener != null) {
        onMapElementListener.elementLeft(centeredElement);
      }
    }

    if (centeredElement != centered && centered != null) {
      if (onMapElementListener != null) {
        onMapElementListener.elementEntered(centered);
      }
    }

    centeredElement = centered;
  }

  //***** OPTIMIZATION data ******
  //                        | Test runs | x | y | time 
  // 2008.08.25 15:20 WTK   | 10000     | 1 | 0 | 8071
  // 2008.08.25 15:20 WTK   | 10000     | 3 | 0 | 9949
  // 2008.08.25 15:20 6500c | 10000     | 3 | 0 | 61851
  // 2008.08.25 15:20 W960  | 10000     | 3 | 0 | 27763 (?)
  // 2008.08.25 15:20 N95   | 10000     | 3 | 0 | 20365 (?)
  // Map paint change
  // 2008.08.27 10:50 WTK   | 10000     | 1 | 0 | 18071
  // Three polygons added
  // 2008.08.27 11:00 WTK   | 10000     | 1 | 0 | 23932
  protected Rectangle paintMap(final ImageBuffer buffer) {
    final Graphics g = buffer.getBackGraphics();
    g.setClip(0, 0, displayWidth, displayHeight);
    //view was moved since last paint
    if (mapBufferMoveX != 0) {
      changedAreas.addElement(new Rectangle(middlePoint.getX()
          + (mapBufferMoveX < 0 ? displayCenterX + mapBufferMoveX : -displayCenterX), middlePoint
          .getY()
          - displayCenterY, Math.abs(mapBufferMoveX) + 1, displayHeight));
    }

    if (mapBufferMoveY != 0) {
      changedAreas.addElement(new Rectangle(middlePoint.getX() - displayCenterX, middlePoint.getY()
          + (mapBufferMoveY < 0 ? displayCenterY + mapBufferMoveY : -displayCenterY), displayWidth,
          Math.abs(mapBufferMoveY) + 1));
    }

    //copy previous map image
    g.drawImage(buffer.getFrontImage(), mapBufferMoveX, mapBufferMoveY, Graphics.TOP
        | Graphics.LEFT);

    mapBufferMoveX = 0;
    mapBufferMoveY = 0;

    Rectangle[] changed = new Rectangle[changedAreas.size()];

    try {
      //TODO jaanus : check this. for some reason thrown some times on android emulator
      changedAreas.copyInto(changed);
    } catch (final IndexOutOfBoundsException e) {
      Log.error("Copy again: " + e.getMessage());
      changed = new Rectangle[0];
    }

    changedAreas.setSize(0);

    final Rectangle change = calculateChangedArea(changed);

    final int changeScreenX = change.getX() - middlePoint.getX() + displayCenterX;
    final int changeScreenY = change.getY() - middlePoint.getY() + displayCenterY;
    g.setClip(changeScreenX, changeScreenY, change.getWidth(), change.getHeight());
    g.setColor(0xFFFFFFFF);
    g.fillRect(changeScreenX, changeScreenY, change.getWidth(), change.getHeight());

    final int tileSize = displayedMap.getTileSize();

    // paint zoom buffer first
    if (Utils.rectanglesIntersect(zoomBufferX, zoomBufferY, displayWidth, displayHeight, change
        .getX(), change.getY(), change.getWidth(), change.getHeight())) {
      // TODO jaanus : when all tiles are present, no need for zoom buffer
      // paint
      g.drawImage(zoomBuffer, -(middlePoint.getX() - displayCenterX - zoomBufferX), -(middlePoint
          .getY()
          - displayCenterY - zoomBufferY), Graphics.TOP | Graphics.LEFT);
    }

    boolean complete = true;
    // print in order
    for (int i = 0; i < tileW; i++) {
      for (int j = 0; j < tileH; j++) {
        complete = paintTile(g, new MapTile(tileX + i * tileSize, tileY + j * tileSize, middlePoint
            .getZoom(), displayedMap, this), middlePoint, change)
            && complete;
      }
    }

    buffer.flip();
    isMapComplete = complete;

    return change;
  }

  protected Rectangle calculateChangedArea(final Rectangle[] changed) {
    Rectangle result = new Rectangle(0, 0, 0, 0);
    for (int i = 0; i < changed.length; i++) {
      final Rectangle area = changed[i];
      if (i == 0) {
        result = area;
        continue;
      }

      if (area.getWidth() == 0 && area.getHeight() == 0) {
        continue;
      }

      //add the change from this area to previous calculation
      result = Utils.mergeAreas(result, area);
    }
    return result;
  }

  /**
   * Paint a map tile.
   * 
   * @param g
   *          graphics object
   * @param mt
   *          map tile to paint
   * @param centerCopy
   *          copy of the current map center
   * @param change
   */
  private boolean paintTile(final Graphics g, final MapTile mt, final MapPos centerCopy,
      final Rectangle change) {
    // search in screen cache
    int pos = screenCache.find(mt);
    if (pos < 0) {
      if (networkCache != null && networkCache.contains(mt.getIDString(), Cache.CACHE_LEVEL_MEMORY)) {
        final byte[] data = networkCache.get(mt.getIDString());
        mt.setImagesData(new byte[][] { data });
        pos = screenCache.add(mt, middlePoint, displayedMap, displayCenterX, displayCenterY, false);
      }
    }
    // if found in raw tiles, paint it
    if (pos >= 0
        && Utils.rectanglesIntersect(mt.getX(), mt.getY(), displayedMap.getTileSize(), displayedMap
            .getTileSize(), change.getX(), change.getY(), change.getWidth(), change.getHeight())) {
      screenCache.paint(g, pos, centerCopy, displayCenterX, displayCenterY);
    }

    return pos >= 0;
  }

  /**
   * Get map center point value in lon/lat
   * 
   * @return middle point on screen in WGS84
   */
  public WgsPoint getMiddlePoint() {
    if (displayedMap == null || middlePoint == null) {
      return null;
    }

    return displayedMap.mapPosToWgs(middlePoint).toWgsPoint();
  }

  /**
   * Zoom in one level
   */
  public void zoomIn() {
    if (middlePoint.getZoom() == displayedMap.getMaxZoom()) {
      return;
    }

    cleanMapBuffer();
    middlePoint = displayedMap.zoom(middlePoint, 1);
    tileMapBounds = displayedMap.getTileMapBounds(middlePoint.getZoom());
    createZoomBufferAndUpdateScreen(-1, true);
  }

  /**
   * Zoom out one level
   */
  public void zoomOut() {
    if (middlePoint.getZoom() == displayedMap.getMinZoom()) {
      return;
    }

    cleanMapBuffer();
    middlePoint = displayedMap.zoom(middlePoint, -1);
    tileMapBounds = displayedMap.getTileMapBounds(middlePoint.getZoom());
    createZoomBufferAndUpdateScreen(1, true);
  }

  private void createZoomBufferAndUpdateScreen(final int scaleDown, final boolean needZoomDelay) {
    // kind of a hack for pointer events. if map is dragged and pointer is
    // released outside painted area (when map is not full screen) the pointer
    // location values are not reset.
    pointerX = -1;
    pointerY = -1;

    fullScreenUpdate();
    final int absScaleDown = (scaleDown > 0) ? scaleDown : -scaleDown;
    final Image frontImage = mapBuffer.getFrontImage();
    final int scaledWidth = (scaleDown > 0) ? (frontImage.getWidth() >> absScaleDown) : frontImage
        .getWidth();
    final int scaledHeight = (scaleDown > 0) ? (frontImage.getHeight() >> absScaleDown)
        : frontImage.getHeight();
    final Image scaled = (scaleDown > 0) ? Tools.scaleImage05(frontImage, absScaleDown) : Tools
        .scaleImage20(frontImage, absScaleDown);

    zoomBufferX = middlePoint.getX() - displayCenterX;
    zoomBufferY = middlePoint.getY() - displayCenterY;

    zoomBufferGraphics.setClip(0, 0, zoomBuffer.getWidth(), zoomBuffer.getHeight());
    zoomBufferGraphics.setColor(0xFFFFFFFF);
    zoomBufferGraphics.fillRect(0, 0, zoomBuffer.getWidth(), zoomBuffer.getHeight());
    if (scaledWidth > 0 && scaledHeight > 0) {
      zoomBufferGraphics.drawImage(scaled, (zoomBuffer.getWidth() - scaledWidth) / 2, (zoomBuffer
          .getHeight() - scaledHeight) / 2, Graphics.TOP | Graphics.LEFT);
    }

    computeTilesToDisplay();

    if (zoomDelay == null && needZoomDelay) {
      zoomDelay = new ZoomDelayTimerTask(this, new MapTileSearchTask(this, tileSearchStrategy,
          taskRunner));
      timer.schedule(zoomDelay, ZoomDelayTimerTask.ZOOM_DELAY_TIME);
    }

    enqueueTiles();
    recalculateMapPosition(displayedElements);

    isMapComplete = onScreenTilesPresent();

    if (needZoomDelay && zoomLevelIndicator != null && indicatorCheck == null) {
      lastZoomCall = System.currentTimeMillis();
      zoomLevelIndicator.setVisible(true);
      timer.schedule(new ZoomIndicatorCheckTask(this), Math.max(zoomLevelIndicator.displayTime(),
          100));
    }

    if (locationSource != null) {
      locationSource.getLocationMarker().updatePosition();
    }

    repaint();
    mapMoved();
  }

  private void cleanMapBuffer() {
    fullScreenUpdate();
    paintMap(mapBuffer);
  }

  private void mapMoved() {
    if (mapListener != null) {
      mapListener.mapMoved();
    }

    if (kmlServicesHandler != null && displayedMap != null && middlePoint != null) {
      kmlServicesHandler.mapMoved(getBoundingBox(), middlePoint.getZoom());
    }
  }

  /**
   * Set map listener for receiving map related callback events from library
   * 
   * @param mL
   *          class implementing MapListener interface
   */
  public void setMapListener(final MapListener mL) {
    mapListener = mL;
  }

  /**
   * Set listener for receiving events related to objects shown on map.
   * 
   * @param listener
   */
  public void setOnMapElementListener(final OnMapElementListener listener) {
    onMapElementListener = listener;
  }

  /**
   * Set listener for library errors
   * 
   * @param errorListener
   *          class implementing ErrorListener interface
   */
  public void setErrorListener(final ErrorListener errorListener) {
    this.errorListener = errorListener;
    taskRunner.setErrorListener(errorListener);
  }

  /**
   * Set zoom controls to be displayed on screen and used for touch screen
   * zooming.
   * 
   * @param zoomControls
   *          zoom controls to be used
   */
  public void setOnScreenZoomControls(final OnScreenZoomControls zoomControls) {
    onScreenZoomControls = zoomControls;
  }

  /*
   * Move view on map up
   */
  private void moveUp(final boolean fromKeys) {
    moveMap(MOVE_UP, fromKeys);
  }

  /*
   * Move view on map down
   */
  private void moveDown(final boolean fromKeys) {
    moveMap(MOVE_DOWN, fromKeys);
  }

  /*
   * Move view on map left
   */
  private void moveLeft(final boolean fromKeys) {
    moveMap(MOVE_LEFT, fromKeys);
  }

  /*
   * Move view on map right
   */
  private void moveRight(final boolean fromKeys) {
    moveMap(MOVE_RIGHT, fromKeys);
  }

  private void moveMap(final int moveDirection, final boolean fromKeys) {
    panning.startPanning(MOVES[moveDirection][0], MOVES[moveDirection][1], fromKeys);
  }

  /**
   * Move view on map by number of pixels
   * 
   * @param panX
   *          number of pixels to be moved left/right
   * @param panY
   *          number of pixels to be moved up/down
   */
  public void panMap(final int panX, final int panY) {
    if (paintingScreen) {
      return;
    }

    mapBufferMoveX -= panX;
    mapBufferMoveY -= panY;

    middlePoint.setX(middlePoint.getX() + panX);
    middlePoint.setY(middlePoint.getY() + panY);

    computeTilesToDisplay();
    enqueueTiles();
    repaint();
  }

  /**
   * Handle key pressed event
   * 
   * @param keyCode
   *          key code forwarded by implementing application
   */
  public void keyPressed(final int keyCode) {
    if (controlKeysHandler == null) {
      return;
    }
    final int actionCode = controlKeysHandler.getKeyActionCode(keyCode);
    if (actionCode == ControlKeys.MOVE_UP_KEY) {
      moveUp(true);
    } else if (actionCode == ControlKeys.MOVE_DOWN_KEY) {
      moveDown(true);
    } else if (actionCode == ControlKeys.MOVE_LEFT_KEY) {
      moveLeft(true);
    } else if (actionCode == ControlKeys.MOVE_RIGHT_KEY) {
      moveRight(true);
    } else if (actionCode == ControlKeys.ZOOM_IN_KEY) {
      zoomIn();
    } else if (actionCode == ControlKeys.ZOOM_OUT_KEY) {
      zoomOut();
    } else if (actionCode == ControlKeys.SELECT_KEY) {
      if (centeredElement != null) {
        if (onMapElementListener != null) {
          onMapElementListener.elementClicked(centeredElement);
        }
      } else if (cursor != null && mapListener != null) {
        final Point cursorOnDisplay = cursor.getPointOnDisplay(displayWidth, displayHeight);
        final MapPos cursorOnMap = new MapPos(middlePoint.getX() - displayCenterX
            + cursorOnDisplay.getX(), middlePoint.getY() - displayCenterY + cursorOnDisplay.getY(),
            middlePoint.getZoom());
        mapListener.mapClicked(displayedMap.mapPosToWgs(cursorOnMap).toWgsPoint());
      }
    }
  }

  /**
   * Handle key released event
   * 
   * @param keyCode
   *          key code forwarded by implementing application
   */
  public void keyReleased(final int keyCode) {
    if (panning.isPanning()) {
      panning.stopPanning();
      mapMoved();
    }
  }

  /**
   * Handle key repeated event
   * 
   * @param keyCode
   *          key code forwarded by implementing application
   */
  public void keyRepeated(final int keyCode) {
    panning.keyRepeated(keyCode);
  }

  /**
   * Handle pointer dragged event
   * 
   * @param x
   *          pixels dragged
   * @param y
   *          pixels dragged
   */
  public void pointerDragged(final int x, final int y) {
    final int componentX = x - displayX;
    final int componentY = y - displayY;

    pointerXMove += Math.abs(pointerX - x - displayX);
    pointerYMove += Math.abs(pointerY - y - displayY);

    if (centeredElement != null
        && centeredElement instanceof Place
        && ((Place) centeredElement).pointOnLabel(middlePoint, displayWidth, displayHeight,
            componentX, componentY)) {
      return;
    }

    if (pointerXMove + pointerYMove > touchTolerance && looseFocusOnDrag) {
      handleActiveIconChange(centeredElement, null);
      centeredElement = null;
    }

    // don't pan, when pointer is dragged on control button
    if (pointerXMove + pointerYMove >= touchTolerance
        && (onScreenZoomControls == null || (onScreenZoomControls != null && onScreenZoomControls
            .getControlAction(componentX, componentY) == -1))) {
      panMap(pointerX - componentX, pointerY - componentY);
//      Log.debug("panned in dragging");
    }

    dragged = true;

    pointerX = componentX;
    pointerY = componentY;
  }

  /**
   * Handle pointer pressed event
   * 
   * @param x
   *          position on screen
   * @param y
   *          position on screen
   */
  public void pointerPressed(final int x, final int y) {
    pointerXMove = 0;
    pointerYMove = 0;

    final int componentX = x - displayX;
    final int componentY = y - displayY;

    int controlAction = -1;
    if (onScreenZoomControls != null
        && (controlAction = onScreenZoomControls.getControlAction(componentX, componentY)) != -1) {
      switch (controlAction) {
      }
    }
    pointerX = componentX;
    pointerY = componentY;
  }

  /**
   * Handle pointer released event
   * 
   * @param x
   *          position on screen
   * @param y
   *          position on screen
   */
  public void pointerReleased(final int x, final int y) {
    final int componentX = x - displayX;
    final int componentY = y - displayY;

    final int controlAction = getPossibleControlAction(componentX, componentY);
    if (controlAction != -1) {
      switch (controlAction) {
      case OnScreenZoomControls.CONTROL_ZOOM_IN:
        zoomIn();
        break;
      case OnScreenZoomControls.CONTROL_ZOOM_OUT:
        zoomOut();
        break;
      }
    } else if (dragged && (pointerXMove >= touchTolerance || pointerYMove >= touchTolerance)) {
      dragged = false;
      mapMoved();
//      Log.debug("moved in released");
    } else {
      dragged = false;
      handlePlaceOrMapClick(componentX, componentY, displayedElements);
//      Log.debug("click handled");
    }

    pointerX = -1;
    pointerY = -1;
  }

  private int getPossibleControlAction(final int componentX, final int componentY) {
    if (onScreenZoomControls == null) {
      return -1;
    }

    if (centeredElement != null
        && centeredElement instanceof Place
        && ((Place) centeredElement).pointOnLabel(middlePoint, displayWidth, displayHeight,
            componentX, componentY)) {
      return -1;
    }

    return onScreenZoomControls.getControlAction(componentX, componentY);
  }

  private void handlePlaceOrMapClick(final int screenX, final int screenY,
      final Vector onMapElements) {
    final MapPos clickOnMap = new MapPos(middlePoint.getX() - displayCenterX + screenX, middlePoint
        .getY()
        - displayCenterY + screenY, middlePoint.getZoom());

    if (centeredLabelClick(centeredElement, screenX, screenY)) {
      final boolean centeredIsPlace = centeredElement instanceof Place;
      if (onMapElementListener != null) {
        onMapElementListener.elementClicked(centeredElement);
      }

      if (centeredIsPlace) {
        ((Place) centeredElement).labelClicked(middlePoint, displayWidth, displayHeight, screenX,
            screenY);
      }

      repaint();
      return;
    }

    OnMapElement centered = null;
    int distanceFromCenter = Integer.MAX_VALUE;

    for (int i = 0; i < onMapElements.size(); i++) {
      final OnMapElement current = (OnMapElement) onMapElements.elementAt(i);
      if (current.isCentered(clickOnMap)
      //&& current.distanceInPixels(clickOnMap) < distanceFromCenter
      ) {
        centered = current;
        distanceFromCenter = current.distanceInPixels(clickOnMap);
      }
    }

    final OnMapElement previousCentered = centeredElement;
    centeredElement = centered;
    centeredClickMapPos = clickOnMap;

    handleActiveIconChange(previousCentered, centered);

    if (onMapElementListener != null) {
      handlePossibleCenteredChangeNotification(previousCentered, centered);
    }

    if (centeredElement instanceof Place) {
      final Point viewUpdate = centeredElement == null ? null : ((Place) centeredElement)
          .getLabelViewUpdate(middlePoint, displayWidth, displayHeight);
      if (viewUpdate != null) {
        panMap(viewUpdate.getX(), viewUpdate.getY());
      }
    }

    if (centered == null && mapListener != null) {
      mapListener.mapClicked(displayedMap.mapPosToWgs(clickOnMap).toWgsPoint());
    }

    repaint();
  }

  private void handleActiveIconChange(final OnMapElement previousCentered,
      final OnMapElement centered) {
    if (previousCentered != null && previousCentered instanceof Place) {
      final Place previous = (Place) previousCentered;
      changedAreas.addElement(previous.toMapArea(middlePoint.getZoom()));
      previous.setIsActive(false);
    }
    if (centered != null && centered instanceof Place) {
      final Place centeredPlace = (Place) centered;
      changedAreas.addElement(centeredPlace.toMapArea(middlePoint.getZoom()));
      centeredPlace.setIsActive(true);
    }
  }

  private boolean centeredLabelClick(final OnMapElement centeredPlace, final int clickX,
      final int clickY) {
    if (centeredPlace == null || !(centeredPlace instanceof Place)) {
      return false;
    }

    return ((Place) centeredPlace).pointOnLabel(middlePoint, displayWidth, displayHeight, clickX,
        clickY);
  }

  private void handlePossibleCenteredChangeNotification(final OnMapElement previousCentered,
      final OnMapElement centered) {

    if (previousCentered != null && previousCentered == centered) {
      if (onMapElementListener != null) {
        onMapElementListener.elementClicked(previousCentered);
      }
    } else if (previousCentered != null && previousCentered != centered) {
      if (onMapElementListener != null) {
        onMapElementListener.elementLeft(previousCentered);
      }
    } else if (previousCentered != centered && centered != null) {
      if (onMapElementListener != null) {
        onMapElementListener.elementEntered(centered);
      }
    }
  }

  /**
   * Set key code values for defined control keys. Using this method will set
   * the control keys handler to default implementation
   * {@link com.nutiteq.controls.UserDefinedKeysMapping}
   * 
   * @param actionCode
   *          internal code for action {@link com.nutiteq.controls.ControlKeys
   *          ControlKeys}
   * @param keyCode
   *          key code value for defined key
   */
  public void defineControlKey(final int actionCode, final int keyCode) {
    //TODO jaanus : deprecate it and use setKeysHandler instead
    if (controlKeysHandler == null || !(controlKeysHandler instanceof UserDefinedKeysMapping)) {
      controlKeysHandler = new UserDefinedKeysMapping();
      //      ((UserDefinedKeysMapping) controlKeysHandler).defineKey(ControlKeys.ZOOM_OUT_KEY,
      //          Canvas.KEY_STAR);
      //      ((UserDefinedKeysMapping) controlKeysHandler).defineKey(ControlKeys.ZOOM_IN_KEY,
      //          Canvas.KEY_POUND);
    }
    ((UserDefinedKeysMapping) controlKeysHandler).defineKey(actionCode, keyCode);
  }

  /**
   * Change control keys handler used for actions mapping.
   * 
   * @param keysHandler
   *          new keys mapping handler
   */
  public void setControlKeysHandler(final ControlKeysHandler keysHandler) {
    controlKeysHandler = keysHandler;
  }

  private void computeTilesToDisplay() {
    if (middlePoint == null) {
      return;
    }

    final int tileSize = displayedMap.getTileSize();

    // get top-left corner
    final int x = middlePoint.getX() - displayCenterX;
    final int y = middlePoint.getY() - displayCenterY;
    // compute -(x%SIZE) and -(y%SIZE)
    final int xx = (x >= 0) ? (x % tileSize) : (tileSize - (-x) % tileSize);
    final int yy = (y >= 0) ? (y % tileSize) : (tileSize - (-y) % tileSize);

    final int maxi = ((displayWidth + xx) / tileSize) + 1;
    final int maxj = ((displayHeight + yy) / tileSize) + 1;

    // corner of the top-left tile
    tileX = x;
    tileY = y;
    // number of tiles
    tileW = maxi;
    tileH = maxj;
  }

  /**
   * Enqueue map tiles to download, in "radial" order.
   */
  private void enqueueTiles() {
    final int tileSize = displayedMap.getTileSize();
    for (int k = 0; k <= (tileW >> 1) + (tileH >> 1); k++) {
      for (int i = Math.max(0, k - (tileH >> 1)); i <= (tileW >> 1); i++) {
        final int j = k - i;
        if (j < 0 || j > (tileH >> 1)) {
          continue;
        }

        final int i1 = (tileW >> 1) - i - 1;
        final int j1 = (tileH >> 1) - j - 1;
        final int i2 = (tileW >> 1) + i;
        final int j2 = (tileH >> 1) + j;
        if (i2 < tileW && j2 < tileH) {
          enqueueTile(new MapTile(tileX + i2 * tileSize, tileY + j2 * tileSize, middlePoint
              .getZoom(), displayedMap, this));
        }
        if (i2 < tileW && j1 >= 0) {
          enqueueTile(new MapTile(tileX + i2 * tileSize, tileY + j1 * tileSize, middlePoint
              .getZoom(), displayedMap, this));
        }
        if (i1 >= 0 && j2 < tileH) {
          enqueueTile(new MapTile(tileX + i1 * tileSize, tileY + j2 * tileSize, middlePoint
              .getZoom(), displayedMap, this));
        }
        if (i1 >= 0 && j1 >= 0) {
          enqueueTile(new MapTile(tileX + i1 * tileSize, tileY + j1 * tileSize, middlePoint
              .getZoom(), displayedMap, this));
        }
      }
    }// for k

    if (neededTiles.size() > 0 && zoomDelay == null) {
      taskRunner.enqueue(new MapTileSearchTask(this, tileSearchStrategy, taskRunner));
    }
  }

  /**
   * Enqueue a tile.
   * 
   * @param mt
   *          map tile
   */
  private void enqueueTile(final MapTile mt) {
    if (!tileMapBounds.isWithinBounds(mt.getX(), mt.getY())) {
      return;
    }

    // enqueue map tiles that are not already downloaded
    //TODO jaanus : check this synchronization
    synchronized (screenCache) {
      // enqueue only if not in offline mode and not already cached
      // search in screen cache first
      if ((screenCache.find(mt) > 0) || neededTiles.contains(mt)) {
        return;
      }

      if (networkCache != null && networkCache.contains(mt.getIDString(), Cache.CACHE_LEVEL_MEMORY)) {
        return;
      }

      neededTiles.addElement(mt);
    }
  }

  /**
   * Not part of public API
   */
  public MapTile getRequiredTile() {
    return firstVisibleTile(neededTiles);
  }

  /**
   * Not part of public API
   */
  public boolean requiresMoreTiles() {
    return neededTiles.size() > 0;
  }

  /**
   * Not part of public API
   */
  public MapTile[] getAllRequiredTiles() {
    final Vector willBeDownloaded = new Vector();
    for (int i = 0; i < neededTiles.size(); i++) {
      final MapTile tile = (MapTile) neededTiles.elementAt(i);
      if (tile.isVisible(middlePoint, displayedMap, displayCenterX, displayCenterY)) {
        willBeDownloaded.addElement(tile);
      }
    }
    neededTiles.setSize(0);

    if (willBeDownloaded.size() == 0) {
      return new MapTile[0];
    }

    final MapTile[] downloaded = new MapTile[willBeDownloaded.size()];
    willBeDownloaded.copyInto(downloaded);

    return downloaded;
  }

  private MapTile firstVisibleTile(final Vector tiles) {
    while (tiles.size() > 0) {
      final MapTile tile = (MapTile) tiles.elementAt(0);
      tiles.removeElement(tile);
      if (tile.isVisible(middlePoint, displayedMap, displayCenterX, displayCenterY)
          && screenCache.find(tile) < 0) {
        return tile;
      }
    }

    return null;
  }

  /**
   * Not in public API. Will be obfuscated.
   * 
   * Notify when a tile was downloaded.
   * 
   * @param mt
   *          the tile downloaded
   */
  public void tileRetrieved(final MapTile mt) {
    tileRetrieved(mt, false);
  }

  private void tileRetrieved(final MapTile mt, final boolean update) {
    if (mt.isVisible(middlePoint, displayedMap, displayCenterX, displayCenterY) && mt.tryAgain()) {
      //if image is missing (some download error), try again
      enqueueTile(mt);
      if (neededTiles.size() > 0) {
        taskRunner.enqueue(new MapTileSearchTask(this, tileSearchStrategy, taskRunner));
      }
      return;
    }

    //TODO jaanus : check this synchronization
    synchronized (screenCache) {
      // add it to the screen cache if it is visible
      if (mt.isVisible(middlePoint, displayedMap, displayCenterX, displayCenterY)) {
        screenCache.add(mt, middlePoint, displayedMap, displayCenterX, displayCenterY, update);

        changedAreas.addElement(new Rectangle(mt.getX(), mt.getY(), displayedMap.getTileSize(),
            displayedMap.getTileSize()));

        isMapComplete = onScreenTilesPresent();

        if (System.currentTimeMillis() - lastRepaintCallTime >= REPAINT_CALL_DELAY) {
          repaint();
        } else if (repaintTask == null) {
          // if last repaint was called less then a second ago and no timer has
          // been started calculate the time from last repaint and schedule
          // timer based on that
          final long delay = REPAINT_CALL_DELAY
              - (System.currentTimeMillis() - lastRepaintCallTime);
          repaintTask = new RepaintTimerTask(this);
          timer.schedule(repaintTask, delay > 0 ? delay : 1);
        }
      }
      // Add overlays, if present

      final MapTileOverlay overlay = displayedMap.getTileOverlay();

      if (overlay != null && !update) {
        overlayQueue.addElement(mt);
      }

      synchronized (overlayQueue) {
        if (isMapComplete && overlay != null && overlayQueue != null && !overlayQueue.isEmpty()) {

          final Enumeration e = overlayQueue.elements();
          while (e.hasMoreElements()) {
            final MapTile overlayMt = (MapTile) e.nextElement();
            enqueueDownload(new TileOverlayRetriever(overlayMt, overlay), Cache.CACHE_LEVEL_MEMORY);
          }

          overlayQueue.removeAllElements();

        }

      }

    }
  }

  /**
   * Not part of public API
   */
  public void updateTile(final MapTile mapTile) {
    tileRetrieved(mapTile, true);
  }

  private boolean onScreenTilesPresent() {
    boolean allPresent = true;
    final int tileSize = displayedMap.getTileSize();
    final int zoom = middlePoint.getZoom();

    for (int i = 0; i < tileW; i++) {
      for (int j = 0; j < tileH; j++) {
        final MapTile t = new MapTile(tileX + i * tileSize, tileY + j * tileSize, zoom,
            displayedMap, this);
        if (screenCache.find(t) >= 0) {
          continue;
        }

        allPresent = false;
        break;
      }
    }

    return allPresent;
  }

  public void repaint() {
    lastRepaintCallTime = System.currentTimeMillis();
    if (mapListener != null) {
      mapListener.needRepaint(isMapComplete);
    }
  }

  /**
   * Remove a place from previously added places
   * 
   * @param place
   *          place to be removed
   */
  public void removePlace(final Place place) {
    if (place == null) {
      return;
    }
    removePlaces(new Place[] { place });
  }

  /**
   * Remove a line from previously added lines
   * 
   * @param line
   *          line to be removed
   */
  public void removeLine(final Line line) {
    if (line == null) {
      return;
    }

    removeLines(new Line[] { line });
  }

  /**
   * Remove lines from previously added lines
   * 
   * @param lines
   *          lines to be removed
   */
  public void removeLines(final Line[] lines) {
    removeOnMapElements(lines);
  }

  /**
   * Remove given elements from map display
   * 
   * @param elements
   *          elements to be removed
   */
  public void removeOnMapElements(final OnMapElement[] elements) {
    if (elements == null) {
      return;
    }

    boolean removed = false;
    for (int i = 0; i < elements.length; i++) {
      if (elements[i] == centeredElement) {
        centeredElement = null;
      }
      removed = displayedElements.removeElement(elements[i]) || removed;
    }
    fullScreenUpdate();
    if (removed) {
      repaint();
    }
  }

  private void removeAllKmlElements() {
    final boolean removed = !displayedElements.isEmpty();
    displayedElements.removeAllElements();
    fullScreenUpdate();
    if (removed) {
      repaint();
    }
  }

  /**
   * Remove places from previously added places
   * 
   * @param places
   *          places to be removed
   */
  public void removePlaces(final Place[] places) {
    removeOnMapElements(places);
  }

  /**
   * Remove all places.
   */
  public void removeAllPlaces() {
    //TODO jaanus : what should be done with internal places? at the moment the 
    //display side is wiped, but data will remain in handler
    removeAllKmlElements();
  }

  /**
   * Add new places to the map and remove all other places previously on the
   * map.
   * 
   * @param places
   *          places to be displayed on map
   */
  public void replacePlaces(final Place[] places) {
    // we're using Vector for the places, we should just removeAll then add
    // because it's less expensive than calling contains() each time

    // avoid additional repaints
    //TODO jaanus : separate internal places handling? it is a mess at the moment
    displayedElements.removeAllElements();
    addOnMapElements(places, true);
  }

  /**
   * Add a place to be displayed on map
   * 
   * @param place
   *          place to be displayed on map (if visible)
   */
  public void addPlace(final Place place) {
    addOnMapElement(place);
  }

  /**
   * Add a place to be displayed on map first (Z-order)
   * 
   * @param place
   *          place to be displayed on map (if visible)
   */
  public void addPlaceFirst(final Place place) {
    //TODO jaanus : this is so wrong. is it for "GPS"?
    if (place == null) {
      return;
    }

    final boolean calculatePlaceLocationAndRepaint = displayedMap != null && middlePoint != null;
    
    if (!displayedElements.contains(place)) {
      displayedElements.insertElementAt(place, 0);
      if (calculatePlaceLocationAndRepaint) {
        place.calculatePosition(displayedMap, middlePoint.getZoom());
      }
    }

    if (!calculatePlaceLocationAndRepaint) {
      return;
    }

    //TODO jaanus : optimize this
    fullScreenUpdate();

    //TODO jaanus : handler force update after kml place icon has been downloaded without this hack
    repaint();
  }

  /**
   * Add places to be displayed on map
   * 
   * @param places
   *          places to be displayed on map (if visible)
   */
  public void addPlaces(final Place[] places) {
    addPlaces(places, true);
  }

  /**
   * Not part of public API.
   * 
   * Add place to map with possibility to skip repaint.
   * 
   * @param places
   *          places to be added
   * @param updateScreen
   *          should screen be updated after places have been added
   */
  public void addPlaces(final Place[] places, final boolean updateScreen) {
    addOnMapElements(places, updateScreen);
  }

  /**
   * Add line to be displayed on map
   * 
   * @param line
   *          line to be displayed
   */
  public void addLine(final Line line) {
    addOnMapElement(line);
  }

  /**
   * Add lines to be displayed on map
   * 
   * @param lines
   *          lines to be displayed
   */
  public void addLines(final Line[] lines) {
    addOnMapElements(lines, true);
  }

  /**
   * Add polygon to be displayed on map
   * 
   * @param polygon
   *          polygon to be displayed
   */
  public void addPolygon(final Polygon polygon) {
    addOnMapElement(polygon);
  }

  /**
   * Add multiple polygons for display
   * 
   * @param polygons
   *          polygons to be added
   */
  public void addPolygons(final Polygon[] polygons) {
    addOnMapElements(polygons, true);
  }

  /**
   * Remove polygon from map
   * 
   * @param polygon
   *          polygon to be removed
   */
  public void removePolygon(final Polygon polygon) {
    removeOnMapElements(new OnMapElement[] { polygon });
  }

  /**
   * Remove multiple polygons from map
   * 
   * @param polygons
   *          polygons to be removed
   */
  public void removePolygons(final Polygon[] polygons) {
    removeOnMapElements(polygons);
  }

  /**
   * Add elements to be displayed on map
   * 
   * @param elements
   *          elements to be added for display
   */
  public void addOnMapElements(final OnMapElement[] elements) {
    addOnMapElements(elements, true);
  }

  /**
   * Not part of public API
   * 
   * @param elements
   * @param updateScreen
   */
  public void addOnMapElements(final OnMapElement[] elements, final boolean updateScreen) {
    boolean added = false;

    final boolean calculatePlaceLocationAndRepaint = displayedMap != null && middlePoint != null;

    for (int i = 0; i < elements.length; i++) {
      //TODO jaanus : test removeElement/addElement speed
      if (!displayedElements.contains(elements[i])) {
        displayedElements.addElement(elements[i]);
        added = true;
        if (calculatePlaceLocationAndRepaint) {
          elements[i].calculatePosition(displayedMap, middlePoint.getZoom());
        }
      }
    }

    if (!calculatePlaceLocationAndRepaint) {
      return;
    }

    //TODO jaanus : optimize this
    fullScreenUpdate();

    //TODO jaanus : handler force update after kml place icon has been downloaded without this hack
    if (added || updateScreen) {
      repaint();
    }
  }

  private void addOnMapElement(final OnMapElement element) {
    if (element == null) {
      return;
    }

    addOnMapElements(new OnMapElement[] { element }, true);
  }

  /**
   * Get bounding box for current map view.
   * 
   * @return bounding box with WGS84 coordinates for current map views left
   *         bottom and right top corners.
   */
  public WgsBoundingBox getBoundingBox() {
    if (middlePoint == null || displayedMap == null) {
      return null;
    }

    //TODO jaanus : set it in one place
    if (tileMapBounds == null) {
      tileMapBounds = displayedMap.getTileMapBounds(middlePoint.getZoom());
    }

    //TODO jaanus : make it clearer
    //these points are on tiles
    final MapPos mapMinPoint = tileMapBounds.getMinPoint();
    final MapPos mapMaxPoint = tileMapBounds.getMaxPoint();

    // left-bottom (SW) coordinate
    final int viewMinX = Math.max(middlePoint.getX() - displayCenterX, mapMinPoint.getX());
    final int viewMinY = Math.min(middlePoint.getY() + displayCenterY, mapMaxPoint.getY());
    final MapPos posMin = new MapPos(viewMinX, viewMinY, middlePoint.getZoom());
    // right-top (NE) coordinate
    final int viewMaxX = Math.min(middlePoint.getX() + displayCenterX, mapMaxPoint.getX());
    final int viewMaxY = Math.max(middlePoint.getY() - displayCenterY, mapMinPoint.getY());
    final MapPos posMax = new MapPos(viewMaxX, viewMaxY, middlePoint.getZoom());
    return new WgsBoundingBox(displayedMap.mapPosToWgs(posMin).toWgsPoint(), displayedMap
        .mapPosToWgs(posMax).toWgsPoint());
  }

  /**
   * Set bounding box for the view. Finds the best zoom level for the bounding
   * box view.
   * 
   * @param bBox
   *          are to be displayed (in WGS84 coordinates)
   */
  public void setBoundingBox(final WgsBoundingBox bBox) {
    if (bBox == null) {
      return;
    }

    WgsPoint newCenter = null;
    int zoomLevel = findZoomForBoundingBoxView(displayedMap, bBox);
    if (zoomLevel == -1) {
      //this means that could not fit the view on this maps zoom range.
      //set middlepoint and zoom to minimum
      newCenter = bBox.getBoundingBoxCenter();
      zoomLevel = displayedMap.getMinZoom();
    } else {
      final Point minPoint = bBox.getWgsMin().toInternalWgs();
      final Point maxPoint = bBox.getWgsMax().toInternalWgs();
      newCenter = new Point(minPoint.getX() + (maxPoint.getX() - minPoint.getX()) / 2, minPoint
          .getY()
          + (maxPoint.getY() - minPoint.getY()) / 2).toWgsPoint();
    }

    tileMapBounds = displayedMap.getTileMapBounds(zoomLevel);
    setMiddlePoint(newCenter, zoomLevel);
  }

  private int findZoomForBoundingBoxView(final GeoMap usedMap, final WgsBoundingBox bBox) {
    if (bBox == null) {
      return -1;
    }

    int zoom = usedMap.getMaxZoom();
    final Point minPoint = bBox.getWgsMin().toInternalWgs();
    final Point maxPoint = bBox.getWgsMax().toInternalWgs();

    while (true) {
      final MapPos posMin = usedMap.wgsToMapPos(minPoint, zoom);
      final MapPos posMax = usedMap.wgsToMapPos(maxPoint, zoom);
      //y values is top->down, so need to subtract max from min, to avoid Math.abs() :P
      if (posMax.getX() - posMin.getX() <= displayWidth
          && posMin.getY() - posMax.getY() <= displayHeight) {
        return zoom;
      } else {
        zoom--;
        if (zoom < usedMap.getMinZoom()) {
          return -1;
        }
      }
    }
  }

  private void recalculateMapPosition(final Vector places) {
    final int zoomLevel = middlePoint.getZoom();

    for (int i = 0; i < places.size(); i++) {
      final OnMapElement element = (OnMapElement) places.elementAt(i);
      element.calculatePosition(displayedMap, zoomLevel);
    }
  }

  /**
   * Stop threads started by MapComponent. Called before application exit to
   * clean library resources.
   */
  public void stopMapping() {
    if (!mappingStarted) {
      return;
    }

    mappingStarted = false;

    panning.quit();

    if (locationSource != null) {
      locationSource.quit();
    }

    if (networkCache != null) {
      networkCache.deinitialize();
    }

    //TODO jaanus : check this
    MappingCore.clean();
    //TODO jaanus : is this ever needed?

    mapBuffer.clean();
    mapBuffer = null;

    System.gc();
  }

  /**
   * Not in public API. Will be removed by obfuscation
   * 
   * @param fromTimer
   */
  public void repaint(final boolean fromTimer) {
    repaint();
    if (fromTimer) {
      repaintTask = null;
    }
  }

  /**
   * Not in public API. Will be removed by obfuscation
   * 
   * @param license
   */
  public void setLicense(final License license) {
    this.license = license;
    if (license == License.LICENSE_NETWORK_ERROR) {
//      taskRunner.quit();
      this.license = License.OFFLINE;
    } else if (!license.isValid()) {
      stopMapping();

      if (errorListener != null) {
        errorListener.licenseError(license.getMessage());
      }
    }

    Log.info("License: " + license);
    repaint();
  }

  /**
   * Change used map
   * 
   * @param newMap
   *          new map to be displayed
   */
  public void setMap(final GeoMap newMap) {
    setMap(newMap, true);
  }

  private void setMap(final GeoMap newMap, final boolean createStrategy) {
    final WgsBoundingBox currentView = getBoundingBox();
    final WgsPoint middleWgs = getMiddlePoint();

    displayedMap = newMap;

    if (createStrategy) {
      tileSearchStrategy = new GeoMap[] { displayedMap };
    }

    if (screenCache == null) {
      createScreenCache();
    }

    screenCache.resize((2 + ((displayWidth - 2) / displayedMap.getTileSize()))
        * (2 + ((displayHeight - 2) / displayedMap.getTileSize())));

    neededTiles.setSize(0);
    //mapTilesRetriever.cancelAllRunning();

    if (zoomBufferGraphics != null) {
      zoomBufferGraphics.setColor(0xFFFFFFFF);
      zoomBufferGraphics.fillRect(0, 0, displayWidth, displayHeight);
    }

    //initialize map size
    setZoomLevelIndicator(zoomLevelIndicator);

    //TODO jaanus : impove this
    //find optimal zoom
    final int viewZoom = findZoomForBoundingBoxView(displayedMap, currentView);
    
    Task task = displayedMap.getInitializationTask();
    if (task != null) {
      taskRunner.enqueue(task);
    }
    
    setMiddlePoint(middleWgs, viewZoom == -1 ? displayedMap.getMinZoom() : viewZoom);
  }

  /**
   * Get zoom range for currently used map
   * 
   * @return zoom range for map
   */
  public ZoomRange getZoomRange() {
    return displayedMap.getZoomRange();
  }

  /**
   * Add kml service to be handled (updated, parsed, painted) internally by
   * library.
   * 
   * @param service
   *          new service to be displayed on map
   */
  public void addKmlService(final KmlService service) {
    if (kmlServicesHandler == null) {
      initializeKmlServiceshandler();
    }

    kmlServicesHandler.addService(service);
    mapMoved();
  }

  /**
   * Get list of used kml services
   * 
   * @return kml services inserted by application
   */
  public KmlService[] getKmlServices() {
    if (kmlServicesHandler == null) {
      return new KmlService[0];
    }
    return kmlServicesHandler.getServices();
  }

  /**
   * Remove previously added kml service
   * 
   * @param service
   *          service to be removed
   */
  public void removeKmlService(final KmlService service) {
    if (kmlServicesHandler == null) {
      return;
    }
    kmlServicesHandler.removeService(service);
  }

  /**
   * Get additional info for place displayed by internally handled service.
   * 
   * @param place
   *          place associated with internal data
   * @return information object containing additional data for place
   */
  public PlaceInfo getAdditionalInfo(final Place place) {
    PlaceInfo result = null;
    if (kmlServicesHandler != null) {
      result = kmlServicesHandler.getAdditionalInfo(place);
    }

    return result;
  }

  /**
   * Get KML places which were read by KML parser.
   * 
   * @param KML
   *          service TODO service for which KML places are needed
   * @return array of KmlPlace
   */

  public KmlPlace[] getKmlPlaces(final KmlService service) {

    if (kmlServicesHandler != null && service != null) {
      return kmlServicesHandler.getKmlPlaces(service);
    }

    return null;
  }

  /**
   * Retrieve currently used map.
   * 
   * @return currently displayed map
   */
  public GeoMap getMap() {
    return displayedMap;
  }

  /**
   * Get internal log for library.
   * 
   * @return internal log
   */
  public String getLibraryLog() {
    return Log.getLog();
  }

  /**
   * Not part of public API. Removed by obfuscator
   */
  public void removeZoomDelay() {
    zoomDelay = null;
  }

  /**
   * Not part of public API. Removed by obfuscator.
   * 
   * @return middle point location on pixal map
   */
  public MapPos getInternalMiddlePoint() {
    return middlePoint;
  }

  /**
   * Set map size on screen.
   * 
   * @param w
   *          map width
   * @param h
   *          map height
   */
  public void setSize(final int w, final int h) {
    resize(w, h);
  }

  /**
   * Set position on screen for top-left corner.
   * 
   * @param x
   *          left
   * @param y
   *          top
   */
  public void setScreenPosition(final int x, final int y) {
    displayX = x;
    displayY = y;
  }

  /**
   * Set bounds on screen.
   * 
   * @param x
   *          left
   * @param y
   *          top
   * @param w
   *          width
   * @param h
   *          height
   * @see BasicMapComponent#setScreenPosition(int,int)
   * @see BasicMapComponent#setSize(int,int)
   */
  public void setScreenBounds(final int x, final int y, final int w, final int h) {
    setSize(w, h);
    setScreenPosition(x, y);
  }

  /**
   * Set the screen position for the center of the displayed map.
   * 
   * @param x
   *          screen position X
   * @param y
   *          screen position Y
   */
  public void setScreenCenter(final int x, final int y) {
    //TODO jaanus : ???
    setScreenPosition(x - getWidth() / 2, y - getHeight() / 2);
  }

  /**
   * Get the screen position for the top-left corner.
   */
  public int getScreenLeft() {
    return displayX;
  }

  /**
   * Get the screen position for the top-left corner.
   */
  public int getScreenTop() {
    return displayY;
  }

  /**
   * Get map center point. Synonym for getMiddlePoint
   * 
   * @return middle point on screen in WGS84
   */
  public WgsPoint getCenterPoint() {
    return getMiddlePoint();
  }

  /**
   * Set tolerance for detecting click events instead of dragging on
   * touchscreen. Defaults to 5 pixels.
   * 
   * @param pixels
   *          needed to be moved for map dragging
   */
  public void setTouchClickTolerance(final int pixels) {
    touchTolerance = pixels;
  }

  /**
   * 
   * Move map without changing zoom.
   * 
   * @param lon
   *          new map center in WGS84
   * @param lat
   *          new map center in WGS84
   */
  public void moveMap(final double lon, final double lat) {
    moveMap(new WgsPoint(lon, lat));
  }

  /**
   * Move map without changing zoom.
   * 
   * @param point
   *          new map center in WGS84
   */
  public void moveMap(final WgsPoint point) {
    if (point == null) {
      return;
    }

    final MapPos mp = displayedMap.wgsToMapPos(point.toInternalWgs(), middlePoint.getZoom());
    panMap(mp.getX() - middlePoint.getX(), mp.getY() - middlePoint.getY());
    mapMoved();
  }

  /**
   * Set map zoom without changing position.
   * 
   * @param newZoom
   *          new zoom level
   */
  public void setZoom(final int newZoom) {
    final int currentZoom = middlePoint.getZoom();
    if (currentZoom == newZoom) {
      return;
    }

    cleanMapBuffer();

    int dif = newZoom - currentZoom;
    if (currentZoom < newZoom && newZoom > displayedMap.getMaxZoom()) {
      dif = displayedMap.getMaxZoom() - currentZoom;
    } else if (newZoom < displayedMap.getMinZoom()) {
      dif = displayedMap.getMinZoom() - currentZoom;
    }

    middlePoint = displayedMap.zoom(middlePoint, dif);
    tileMapBounds = displayedMap.getTileMapBounds(middlePoint.getZoom());
    createZoomBufferAndUpdateScreen(-dif, true);
  }

  /**
   * Set cursor used on screen for places selection, etc.
   * 
   * @param newCursor
   *          cursor implementation
   */
  public void setCursor(final Cursor newCursor) {
    cursor = newCursor;
  }

  /**
   * Get the current zoom level.
   * 
   * @return current zoom level
   */
  public int getZoom() {
    return middlePoint.getZoom();
  }

  /**
   * Get the current map listener.
   * 
   * @return the current map listener
   */
  public MapListener getMapListener() {
    return mapListener;
  }

  /**
   * Get current listener for objects on map
   * 
   * @return elements listener
   */
  public OnMapElementListener getOnMapElementListener() {
    return onMapElementListener;
  }

  /**
   * Get the current error listener.
   * 
   * @return the current error listener
   */
  public ErrorListener getErrorListener() {
    return errorListener;
  }

  /**
   * Change the implementation for download stream opening. As default a direct
   * connection without any additional headers is used.
   */
  public void setDownloadStreamOpener(final DownloadStreamOpener opener) {
    taskRunner.setDownloadStreamOpener(opener);
  }

  /**
   * Not part of public API
   */
  public void zoomLevelIndicatorCheck() {
    //TODO jaanus : check if null check is needed for obfuscator
    if (zoomLevelIndicator != null
        && System.currentTimeMillis() - lastZoomCall >= zoomLevelIndicator.displayTime()) {
      zoomLevelIndicator.setVisible(false);
      repaint();
    } else {
      //schedule new task calculating the delay
      timer.schedule(new ZoomIndicatorCheckTask(this), Math.max(zoomLevelIndicator.displayTime(),
          100)
          - (System.currentTimeMillis() - lastZoomCall));
    }
  }

  /**
   * Set search strategy for map tile.
   * 
   * @param searched
   *          search strategy to be used
   */
  public void setTileSearchStrategy(final GeoMap[] searched) {
    this.tileSearchStrategy = searched;
    setMap(searched[0], false);
  }

  /**
   * Set panning strategy for map component. This is required for map panning
   * with keys.
   * 
   * @param panningStrategy
   *          new panning strategy
   */
  public void setPanningStrategy(final PanningStrategy panningStrategy) {
    if (panning != null) {
      panning.stopPanning();
      panning.quit();
    }

    panning = panningStrategy;
    panning.setMapComponent(this);
    panning.start();
  }

  /**
   * Set location source with GPS marker to be displayed. Here the location
   * retrieving thread is also started.
   * 
   * @param source
   *          source to be used
   */
  public void setLocationSource(final LocationSource source) {
    if (locationSource != null) {
      locationSource.quit();
    }

    if (source == null) {
      return;
    }

    locationSource = source;
    locationSource.getLocationMarker().setMapComponent(this);
    locationSource.start();
  }

  /**
   * Set cache for networking. Currently data is cached:
   * <ul>
   * <li>map tiles to rms level</li>
   * <li>kml icons to memory and rms</li>
   * </ul>
   * 
   * @param cache
   */
  public void setNetworkCache(final com.nutiteq.cache.Cache cache) {
    networkCache = cache;
  }

  /**
   * Not part of public API
   */
  public MapPos getMapPosition(final WgsPoint wgsLocation) {
    return displayedMap.wgsToMapPos(wgsLocation.toInternalWgs(), middlePoint.getZoom());
  }

  /**
   * Remove used location source
   */
  public void removeLocationSource() {
    if (locationSource != null) {
      locationSource.quit();
      locationSource = null;
      repaint();
    }
  }

  /**
   * Enqueue download requestor to be handled by library.
   * 
   * @param downloadable
   *          resource to be downloaded
   * @param cacheLevel
   *          at which cache levels should response be cached
   */
  public void enqueueDownload(final ResourceRequestor downloadable, final int cacheLevel) {
    taskRunner.enqueueDownload(downloadable, cacheLevel);
  }

  /**
   * Set zoom indicator to be painted on display. This method will overwrite
   * default values set in
   * {@link com.nutiteq.MapComponent#showZoomLevelIndicator(boolean)}.
   * 
   * @param zoomIndicator
   *          zoom indicator to use
   */
  public void setZoomLevelIndicator(final ZoomIndicator zoomIndicator) {
    zoomLevelIndicator = zoomIndicator;
    if (displayedMap == null || zoomLevelIndicator == null) {
      return;
    }
    zoomLevelIndicator.setZoomRange(displayedMap.getZoomRange());
  }

  /**
   * Set used implementation for network traffic display on map.
   * 
   * @param display
   *          display used for info show
   */
  public void setDownloadDisplay(final DownloadDisplay display) {
    downloadDisplay = display;
    downloadDisplay.setDownloadCounter(downloadCounter);
    downloadDisplay.setDisplayUpdater(this);
  }

  /**
   * Set download counter used for gathering information about network traffic
   * 
   * @param counter
   *          implementation used
   */
  public void setDownloadCounter(final DownloadCounter counter) {
    downloadCounter = counter;
    if (downloadDisplay != null) {
      downloadDisplay.setDownloadCounter(downloadCounter);
    }
  }

  /**
   * On touch screen phones, if some object has been selected, should focus be
   * lost on map drag.
   * 
   * @param looseFocus
   *          should object focus be lost
   */
  public void looseFocusOnDrag(final boolean looseFocus) {
    this.looseFocusOnDrag = looseFocus;
  }

  public void loosePlaceFocus() {
    if (centeredElement == null) {
      return;
    }
    handleActiveIconChange(centeredElement, null);
    centeredElement = null;
  }

  /**
   * Set focus on given place.
   * 
   * @param focusOn
   *          place to be focused on
   */
  public void focusOnPlace(final Place focusOn) {
    if (focusOn == null) {
      loosePlaceFocus();
      return;
    }

    if (focusOn == centeredElement) {
      return;
    }

    focusOn.calculatePosition(displayedMap, middlePoint.getZoom());

    setMiddlePoint(focusOn.getWgs());

    //copy/paste from handlePlaceOrMapClick
    handleActiveIconChange(centeredElement, focusOn);
    if (onMapElementListener != null) {
      handlePossibleCenteredChangeNotification(centeredElement, focusOn);
    }

    if (centeredElement instanceof Place) {
      final Point viewUpdate = centeredElement == null ? null : ((Place) centeredElement)
          .getLabelViewUpdate(middlePoint, displayWidth, displayHeight);
      if (viewUpdate != null) {
        panMap(viewUpdate.getX(), viewUpdate.getY());
      }
    }

    centeredElement = focusOn;
    repaint();
  }

  /**
   * Get places currently visible on map view.
   * 
   * @return visible places
   */
  public Place[] getVisiblePlaces() {
    final Vector visible = new Vector();

    for (int i = 0; i < displayedElements.size(); i++) {
      final Object p = displayedElements.elementAt(i);
      if (!(p instanceof Place)) {
        continue;
      }

      final Place place = (Place) p;
      if (place.isVisible(middlePoint.getX() - displayCenterX, middlePoint.getY() - displayCenterY,
          displayWidth, displayHeight, middlePoint.getZoom())) {
        visible.addElement(place);
      }
    }

    final Place[] result = new Place[visible.size()];
    visible.copyInto(result);
    return result;
  }

  /**
   * Set file system to be used for {@link com.nutiteq.maps.StoredMap} handling
   * 
   * @param fs
   *          platform dependent file system to be used
   */
  public void setFileSystem(final FileSystem fs) {
    taskRunner.setFileSystem(fs);
  }

  public DownloadCounter getDownloadCounter() {
    return downloadCounter;
  }

  protected boolean hasMappingStarted() {
    return mappingStarted;
  }

  protected boolean isNetworkCacheSet() {
    return networkCache != null;
  }

  protected boolean isPanningStrategySet() {
    return panning != null;
  }

  protected boolean isMapSet() {
    return tileSearchStrategy != null;
  }

  protected boolean isDownloadCounterPresent() {
    return downloadCounter != null;
  }

  /**
   * Not part of public API
   * 
   * @param task
   */
  public void enqueue(final Task task) {
    taskRunner.enqueue(task);
  }

/**
 * @return all KML Places from all loaded KML Services 
 */
public KmlPlace[] getKmlPlaces() {
    if (kmlServicesHandler != null ) {
        return kmlServicesHandler.getKmlPlaces();
      }
return null;  
}

/**
 * removes all KML Services added so far
 */
    public void removeKmlService() {
        if (kmlServicesHandler == null) {
            return;
        }
        KmlService[] services = kmlServicesHandler.getServices();
        for (int i = 0; i < services.length; i++) {
            kmlServicesHandler.removeService(services[i]);
        }
    }
 
}
