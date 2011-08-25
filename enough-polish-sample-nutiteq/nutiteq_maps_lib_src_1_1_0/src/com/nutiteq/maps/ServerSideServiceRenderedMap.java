package com.nutiteq.maps;

import javax.microedition.lcdui.Image;

import com.nutiteq.components.MapPos;
import com.nutiteq.components.Point;
import com.nutiteq.components.TileMapBounds;
import com.nutiteq.components.ZoomRange;
import com.nutiteq.kml.KmlService;
import com.nutiteq.task.Task;
import com.nutiteq.ui.Copyright;

public class ServerSideServiceRenderedMap implements GeoMap, UnstreamedMap {
  private final String renderServerUrl;
  private final GeoMap baseMap;
  private final KmlService service;

  public ServerSideServiceRenderedMap(final String renderServerUrl, final GeoMap baseMap,
      final KmlService service) {
    this.renderServerUrl = renderServerUrl;
    this.baseMap = baseMap;
    this.service = service;
  }

  public void addTileOverlay(final MapTileOverlay overlay) {
    baseMap.addTileOverlay(overlay);
  }

  public Copyright getCopyright() {
    return baseMap.getCopyright();
  }

  public int getMapHeight(final int zoom) {
    return baseMap.getMapHeight(zoom);
  }

  public int getMapWidth(final int zoom) {
    return baseMap.getMapHeight(zoom);
  }

  public int getMaxZoom() {
    return baseMap.getMaxZoom();
  }

  public int getMinZoom() {
    return baseMap.getMinZoom();
  }

  public Image getMissingTileImage() {
    return baseMap.getMissingTileImage();
  }

  public TileMapBounds getTileMapBounds(final int zoom) {
    return baseMap.getTileMapBounds(zoom);
  }

  public MapTileOverlay getTileOverlay() {
    return baseMap.getTileOverlay();
  }

  public int getTileSize() {
    return baseMap.getTileSize();
  }

  public ZoomRange getZoomRange() {
    return baseMap.getZoomRange();
  }

  public Point mapPosToWgs(final MapPos pos) {
    return baseMap.mapPosToWgs(pos);
  }

  public void setMissingTileImage(final Image missingTile) {
    baseMap.setMissingTileImage(missingTile);
  }

  public MapPos wgsToMapPos(final Point wgs, final int zoom) {
    return baseMap.wgsToMapPos(wgs, zoom);
  }

  public MapPos zoom(final MapPos middlePoint, final int zoomSteps) {
    return baseMap.zoom(middlePoint, zoomSteps);
  }

  public String buildPath(final int mapX, final int mapY, final int zoom) {
    return ((UnstreamedMap) baseMap).buildPath(mapX, mapY, zoom);
  }

  public Task getInitializationTask() {
    return baseMap.getInitializationTask();
  }

}
