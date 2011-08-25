package com.nutiteq.components;

import com.nutiteq.log.Log;
import com.nutiteq.utils.Utils;

public class MultiMapSingleConfig {
  private final String tilesDir;
  private final String name;
  private int tilesPerFile = 1;
  private int hashSize = 1;
  private TileMapBounds[] bounds;
  private int tpfx;
  private int tpfy;
  private ZoomRange zoomRange = new ZoomRange(Integer.MAX_VALUE,Integer.MIN_VALUE);
  
  public MultiMapSingleConfig(final String tilesDir, final String name) {
    this.tilesDir = tilesDir;
    this.name = name;
  }

  public void setTilesPerFile(final int tilesPerFile) {
    this.tilesPerFile = tilesPerFile;
    final int tpflog = Utils.log2(tilesPerFile);
    tpfx = 1 << (tpflog / 2 + tpflog % 2);
    tpfy = 1 << (tpflog / 2);
  }

  public void setHashSize(final int hashSize) {
    this.hashSize = hashSize;
  }

  public void setTileBounds(final TileMapBounds[] bounds) {
    this.bounds = bounds;
  }

  public TileMapBounds[] getTileBounds() {
      return this.bounds;
  }
  
  public boolean isValid() {
    if (tilesPerFile <= 0) {
      Log.error("Conf for " + tilesDir + " invalid: tilesPerFile = " + tilesPerFile);
      return false;
    }

    if (bounds.length == 0) {
      Log.error("Conf for " + tilesDir + " invalid: zero areas defined");
      return false;
    }

    return true;
  }

  public String toString() {
    return new StringBuffer("tilesDir = '").append(tilesDir).append("' name = ").append(name)
        .append(" tilesPerFile = '").append(tilesPerFile).append("' areas defined = ").append(
            bounds.length).toString();
  }

  public boolean contains(final int mapX, final int mapY, final int zoom, final int tileSize) {
    for (int i = bounds.length - 1; i >= 0; i--) {
      if (bounds[i].getZoomLevel() != zoom) {
        continue;
      }

      if (bounds[i].intersectsWithBounds(mapX, mapY, tileSize)) {
        return true;
      }
    }

    return false;
  }

  public int getTpfx() {
    return tpfx;
  }

  public int getTpfy() {
    return tpfy;
  }

  public String getTilesDir() {
    return tilesDir;
  }

  public int getTilesPerFile() {
    return tilesPerFile;
  }

  public int getHashSize() {
    return hashSize;
  }
  
  public int getMaxZoom() {
      return zoomRange.getMaxZoom();
    }

    public int getMinZoom() {
      return zoomRange.getMinZoom();
    }

    public ZoomRange getZoomRange() {
      return zoomRange;
    }
  
  public void setMaxZoom(final int maxZoom) {
    this.zoomRange = new ZoomRange(this.zoomRange.getMinZoom(), maxZoom);
  }


  public void setMinZoom(final int minZoom) {
    this.zoomRange = new ZoomRange(minZoom, this.zoomRange.getMaxZoom());
  }


  public void setZoomRange(final ZoomRange zoomRange) {
    this.zoomRange = zoomRange;
  }
}
