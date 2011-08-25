package com.nutiteq.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.mgmaps.utils.Tools;
import com.nutiteq.log.Log;
import com.nutiteq.maps.GeoMap;
import com.nutiteq.maps.MapTilesRequestor;
import com.nutiteq.maps.StoredMap;
import com.nutiteq.maps.StreamedMap;
import com.nutiteq.maps.UnstreamedMap;

/**
 * Object containing map tile location on the map (x, y coordinates of
 * upper-left corner on whole map) and image data for that tile.
 */
public class MapTile {
  private final int x;
  private final int y;
  private final int zoom;
  private final GeoMap map;
  private byte[][] imageData;
  private byte[][] overlayData;
  private final MapTilesRequestor requestor;

  private int failCount;
  private int dataSize;

  /**
   * Not part of public API
   */
  public MapTile(final int mapX, final int mapY, final int zoom, final GeoMap map,
      final MapTilesRequestor screen) {
    //get the pixel coordinates of upper left corner for this tile
    x = mapX - mapX % map.getTileSize();
    y = mapY - mapY % map.getTileSize();
    this.zoom = zoom;
    this.map = map;
    this.requestor = screen;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getZoom() {
    return zoom;
  }

  public GeoMap getMap() {
    return map;
  }

  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof MapTile)) {
      return false;
    }

    final MapTile other = (MapTile) obj;
    return x == other.x && y == other.y && zoom == other.zoom && map.equals(other.map);
  }

  public int hashCode() {
    throw new RuntimeException("hashcode() has not been implemented!");
  }

  /**
   * Not part of public API Check if the map tile is visible.
   * 
   * @param centerPos
   *          the position of the center of the map
   * @return true if visible, false otherwise
   */
  public boolean isVisible(final MapPos centerPos, final GeoMap displayedMap,
      final int screenCenterX, final int screenCenterY) {
    if (map != displayedMap || zoom != centerPos.getZoom()) {
      return false;
    }

    final int tileSize = map.getTileSize();
    // get screen coords, given the center position
    final int left = x - centerPos.getX() + screenCenterX;
    final int top = y - centerPos.getY() + screenCenterY;

    // out of screen or mode/zoom not matching?
    return MapPos.checkBounds(left, top, tileSize, tileSize, screenCenterX * 2, screenCenterY * 2);
  }

  /**
   * Not part of public API
   */
  public String getIDString() {
    //TODO jaanus : oh crap! resolve this mess!
    return (map instanceof UnstreamedMap) ? ((UnstreamedMap) map).buildPath(x, y, zoom)
        : ((StreamedMap) map).buildStreamedPath(new MapTile[] { this });
  }

  /**
   * Not part of public API
   */
  public long getSize() {
    return dataSize + 100; // x, y, zoom, type, overhead
  }

  /**
   * Not part of public API
   */
  public void notifyError() {
    failCount++;
    setImagesData(null);
  }

  /**
   * Not part of public API
   */
  public void setImagesData(final byte[][] data) {
    if (data == null || data.length == 0 || data[0].length == 0) {
      failCount++;
      Log.error("Could not retrieve " + getIDString());
      requestor.tileRetrieved(this);
      return;
    }

    imageData = data;

    for (int i = 0; i < data.length; i++) {
      dataSize += data[i].length;
    }

    requestor.tileRetrieved(this);
  }

  /**
   * Not part of public API
   */
  public Image getImage() {
    if (imageData == null) {
      return map.getMissingTileImage();
    }

    final String tileUrl = getIDString();
    final int areaParamIndex = tileUrl.indexOf("|a=");

    try {
      final int tileSize = map.getTileSize();
      final Image tileImage = Image.createImage(tileSize, tileSize);
      final Graphics imageGraphics = tileImage.getGraphics();
      for (int i = 0; i < imageData.length; i++) {
        Image tmp = Image.createImage(imageData[i], 0, imageData[i].length);
        if (areaParamIndex > 0) {
          final String areaParam = tileUrl.substring(areaParamIndex + 3);
          if ("0,0".equals(areaParam)) {
            tmp = Tools.scaleImage20(tmp, 0, 0, 1);
          } else if ("0,1".equals(areaParam)) {
            tmp = Tools.scaleImage20(tmp, 0, tileSize / 2, 1);
          } else if ("1,0".equals(areaParam)) {
            tmp = Tools.scaleImage20(tmp, tileSize / 2, 0, 1);
          } else {
            tmp = Tools.scaleImage20(tmp, tileSize / 2, tileSize / 2, 1);
          }
        }
        imageGraphics.drawImage(tmp, 0, 0, Graphics.TOP | Graphics.LEFT);
      }

      if (overlayData != null) {
        for (int i = 0; i < overlayData.length; i++) {
          final Image tmp = Image.createImage(overlayData[i], 0, overlayData[i].length);
          imageGraphics.drawImage(tmp, 0, 0, Graphics.TOP | Graphics.LEFT);
        }
      }
      return tileImage;
    } catch (final IllegalArgumentException e) {
      Log.printStackTrace(e);
      Log.error(getIDString() + " error");
      return null;
    }
  }

  /**
   * Not part of public API
   */
  public boolean tryAgain() {
    return imageData == null && !(map instanceof StoredMap) && failCount > 0 && failCount < 3;
  }

  public byte[] getData() {
    //TODO jaanus : check this
    return imageData == null ? new byte[0] : imageData[0];
  }

  public void setOverlayData(final byte[][] overlay) {
    this.overlayData = overlay;
    if (overlay == null || overlay.length == 0 || overlay[0].length == 0) {
      return;
    }

    requestor.updateTile(this);
  }
}
