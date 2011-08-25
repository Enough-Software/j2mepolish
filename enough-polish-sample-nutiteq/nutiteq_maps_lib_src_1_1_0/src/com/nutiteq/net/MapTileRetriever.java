package com.nutiteq.net;

import com.nutiteq.cache.Cache;
import com.nutiteq.components.MapTile;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.maps.UnstreamedMap;

public class MapTileRetriever implements ResourceRequestor, ResourceDataWaiter {
  private final MapTile tile;
  private final UnstreamedMap map;
  private boolean error;

  public MapTileRetriever(final MapTile tile, final UnstreamedMap map) {
    this.tile = tile;
    this.map = map;
  }

  public String resourcePath() {
      final String path = map.buildPath(tile.getX(), tile.getY(), tile.getZoom());

      final int areaParamIndex = path.indexOf("|a=");
      if (areaParamIndex > 0) {
        return path.substring(0, areaParamIndex);
      } else {
        return path;
      }
  }

  public void notifyError() {
    error = true;
  }

  public void dataRetrieved(final byte[] data) {
    tile.setImagesData(new byte[][] { data });
  }

  public boolean hadError() {
    return error;
  }

  public int getCachingLevel() {
    return Cache.CACHE_LEVEL_MEMORY | Cache.CACHE_LEVEL_PERSISTENT;
  }
}
