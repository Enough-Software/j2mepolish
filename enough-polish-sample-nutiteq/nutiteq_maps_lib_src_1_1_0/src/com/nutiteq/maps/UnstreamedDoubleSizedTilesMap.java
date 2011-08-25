package com.nutiteq.maps;

import javax.microedition.lcdui.Image;

import com.mgmaps.utils.Tools;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.Point;
import com.nutiteq.components.ZoomRange;
import com.nutiteq.ui.Copyright;

public class UnstreamedDoubleSizedTilesMap extends BaseMap implements GeoMap, UnstreamedMap {
  private final GeoMap resizedMap;
  private Image resizedMissingTileImage;
  private final boolean resizeOnlyLastLevel;

/**
 * Resizes ("zooms in") all map images for the map, exactly 2 times
 * 
 * @param resizedMap Map which will be used as resized map, could be any map service of library
 */
  public UnstreamedDoubleSizedTilesMap(final GeoMap resizedMap) {
    this(resizedMap, false);
  }

/**
 * Digitally zooms map images for the map
 * 
 * @param resizedMap Map which will be used as resized map, could be any map service of library
 * @param resizeOnlyLastLevel If true then enable to "digitally zoom" last map level only, otherwise zoom all maps.
 */
  public UnstreamedDoubleSizedTilesMap(final GeoMap resizedMap, final boolean resizeOnlyLastLevel) {
    super("", 64, 0, 1);
    this.resizedMap = resizedMap;
    this.resizeOnlyLastLevel = resizeOnlyLastLevel;
  }

  public void addTileOverlay(final MapTileOverlay overlay) {
    resizedMap.addTileOverlay(overlay);
  }

  public Copyright getCopyright() {
    return resizedMap.getCopyright();
  }

  public int getMapHeight(final int zoom) {
    return resizedMap.getMapHeight(zoom);
  }

  public int getMapWidth(final int zoom) {
    return resizedMap.getMapWidth(zoom);
  }

  public int getMaxZoom() {
    return resizedMap.getMaxZoom() + 1;
  }

  public int getMinZoom() {
    if (resizeOnlyLastLevel) {
      return resizedMap.getMinZoom();
    } else {
      return resizedMap.getMinZoom() + 1;
    }
  }

  public Image getMissingTileImage() {
    if (resizedMissingTileImage == null) {
      resizedMissingTileImage = Tools.scaleImage20(resizedMap.getMissingTileImage(), 1);
    }

    return resizedMissingTileImage;
  }

  public MapTileOverlay getTileOverlay() {
    return resizedMap.getTileOverlay();
  }

  public int getTileSize() {
    return resizedMap.getTileSize();
  }

  public ZoomRange getZoomRange() {
    return resizedMap.getZoomRange();
  }

  public Point mapPosToWgs(final MapPos pos) {
    return resizedMap.mapPosToWgs(pos);
  }

  public void setMissingTileImage(final Image missingTile) {
    resizedMap.setMissingTileImage(missingTile);
  }

  public MapPos wgsToMapPos(final Point wgs, final int zoom) {
    return resizedMap.wgsToMapPos(wgs, zoom);
  }

  public MapPos zoom(final MapPos middlePoint, final int zoomSteps) {
    return resizedMap.zoom(middlePoint, zoomSteps);
  }

  public String buildPath(final int mapX, final int mapY, final int zoom) {
    if (resizeOnlyLastLevel && zoom < getMaxZoom()) {
      return ((UnstreamedMap) resizedMap).buildPath(mapX, mapY, zoom);
    }

    final StringBuffer tilePath = new StringBuffer(((UnstreamedMap) resizedMap).buildPath(mapX / 2, mapY / 2, zoom - 1));
    final int mapXMod = mapX % (getTileSize() * 2);
    final int mapYMod = mapY % (getTileSize() * 2);
    tilePath.append("|a=").append(mapXMod / getTileSize());
    tilePath.append(",").append(mapYMod / getTileSize());
    return tilePath.toString();
  }
}
