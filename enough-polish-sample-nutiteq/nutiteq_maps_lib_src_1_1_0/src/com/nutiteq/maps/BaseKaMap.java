package com.nutiteq.maps;

import com.nutiteq.components.MapPos;
import com.nutiteq.components.Point;
import com.nutiteq.components.TileMapBounds;
import com.nutiteq.maps.projections.EPSG4326;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;

public abstract class BaseKaMap extends EPSG4326 implements GeoMap {
  private static final int WORLD_HEIGHT_DEGREES = 180;
  private static final int WORLD_WIDTH_DEGREES = 360;
  //dots per inch
  private static final int DPI = 72;
  //inches per decimal degree?
  private static final double INCHES_PER_DECIMAL_DEGREE = 4374754D;

  private final int[] mapWidth;
  private final int[] mapHeight;
  protected final int[] scales;
  protected final int[] tileMapWidth;
  protected final int[] tileMapHeight;

  public BaseKaMap(final String copyright, final int tileSize, final int scales[],
      final int minZoom, final int maxZoom) {
    this(new StringCopyright(copyright), tileSize, scales, minZoom, maxZoom);
  }

  public BaseKaMap(final Copyright copyright, final int tileSize, final int scales[],
      final int minZoom, final int maxZoom) {
    super(copyright, tileSize, minZoom, maxZoom);

    this.scales = scales;
    mapWidth = new int[scales.length];
    mapHeight = new int[scales.length];
    tileMapWidth = new int[scales.length];
    tileMapHeight = new int[scales.length];
    for (int i = 0; i < scales.length; i++) {
      mapWidth[i] = calculateMapEdge(WORLD_WIDTH_DEGREES, scales[i]);
      mapHeight[i] = calculateMapEdge(WORLD_HEIGHT_DEGREES, scales[i]);
      tileMapWidth[i] = calculateTileMapEdge(mapWidth[i], tileSize);
      tileMapHeight[i] = calculateTileMapEdge(mapHeight[i], tileSize);
    }
  }

  private int calculateMapEdge(final int edgeDegrees, final int scale) {
    // based on http://lists.maptools.org/pipermail/ka-map-users/2006-June/001644.html
    return (int) Math.floor((edgeDegrees * DPI * INCHES_PER_DECIMAL_DEGREE) / scale + 0.5);
  }

  private int calculateTileMapEdge(final int edgeLength, final int tileSize) {
    final int tileEdge = edgeLength + (tileSize - (edgeLength / 2) % tileSize) * 2;
    return tileEdge - tileEdge % tileSize;
  }

  public Point mapPosToWgs(final MapPos pos) {
    final int zoom = pos.getZoom() - getMinZoom();
    final MapPos converted = pos.copy();
    // because map might not fill the whole tile area we need to remove 'buffer'
    // area around map, to have calculations with real map size
    converted.setX(pos.getX() - (tileMapWidth[zoom] - mapWidth[zoom]) / 2);
    converted.setY(pos.getY() - (tileMapHeight[zoom] - mapHeight[zoom]) / 2);
    return super.mapPosToWgs(converted);
  }

  public MapPos wgsToMapPos(final Point wgs, final int zoom) {
    final MapPos result = super.wgsToMapPos(wgs, zoom);
    final int mapZoom = zoom - getMinZoom();
    //add buffer size to have real position on map tile
    result.setX(result.getX() + (tileMapWidth[mapZoom] - mapWidth[mapZoom]) / 2);
    result.setY(result.getY() + (tileMapHeight[mapZoom] - mapHeight[mapZoom]) / 2);
    return result;
  }

  public int getMapHeight(final int zoom) {
    return mapHeight[zoom - getMinZoom()];
  }

  public int getMapWidth(final int zoom) {
    return mapWidth[zoom - getMinZoom()];
  }

  protected int[] getMapWidth() {
    return mapWidth;
  }

  protected int[] getMapHeight() {
    return mapHeight;
  }

  protected int[] getTileMapWidth() {
    return tileMapWidth;
  }

  protected int[] getTileMapHeight() {
    return tileMapHeight;
  }

  public MapPos zoom(final MapPos middlePoint, final int zoomSteps) {
    final Point currentMiddle = mapPosToWgs(middlePoint);
    return wgsToMapPos(currentMiddle, middlePoint.getZoom() + zoomSteps);
  }

  public TileMapBounds getTileMapBounds(final int zoom) {
    final int zoomIndex = zoom - getMinZoom();
    final MapPos min = new MapPos(0, 0, zoom);
    final MapPos max = new MapPos(tileMapWidth[zoomIndex] - 1, tileMapHeight[zoomIndex] - 1, zoom);
    return new TileMapBounds(min, max);
  }
}
