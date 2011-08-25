package com.nutiteq.task;

import com.nutiteq.cache.Cache;
import com.nutiteq.components.MapTile;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.log.Log;
import com.nutiteq.maps.MapTileOverlay;

public class TileOverlayRetriever implements ResourceRequestor, ResourceDataWaiter {
  private final MapTile mt;
  private final MapTileOverlay overlay;

  public TileOverlayRetriever(final MapTile mt, final MapTileOverlay overlay) {
    this.mt = mt;
    this.overlay = overlay;
  }

  public String resourcePath() {
    return overlay.getOverlayTileUrl(mt);
  }

  public void notifyError() {
    Log.error("Error on overlay download");
  }

  public void dataRetrieved(final byte[] data) {
    mt.setOverlayData(new byte[][] { data });
  }

  public int getCachingLevel() {
    return Cache.CACHE_LEVEL_MEMORY;
  }
}
