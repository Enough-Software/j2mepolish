package com.nutiteq.io;

import com.nutiteq.cache.Cache;
import com.nutiteq.components.MapTile;
import com.nutiteq.maps.UnstreamedMap;
import com.nutiteq.task.MapTileSearchTask;
import com.nutiteq.utils.Utils;

public class UnstreamedTileRetriever implements ResourceRequestor, ResourceDataWaiter {
  private final MapTileSearchTask mapTileSearchTask;
  private final UnstreamedMap map;
  private final MapTile[] toRetrieve;

  public UnstreamedTileRetriever(final MapTileSearchTask mapTileSearchTask, final MapTile[] toRetrieve, final UnstreamedMap map) {
    this.mapTileSearchTask = mapTileSearchTask;
    this.toRetrieve = toRetrieve;
    this.map = map;
  }

  public int getCachingLevel() {
    final int resourceType = Utils.getResourceType(resourcePath());
    return resourceType == Utils.RESOURCE_TYPE_NETWORK ? Cache.CACHE_LEVEL_MEMORY | Cache.CACHE_LEVEL_PERSISTENT : Cache.CACHE_LEVEL_MEMORY;
  }

  public void notifyError() {
    mapTileSearchTask.retrieveErrorFor(toRetrieve);
  }

  public String resourcePath() {
    final MapTile tile = toRetrieve[0];
    final String path = map.buildPath(tile.getX(), tile.getY(), tile.getZoom());

    final int areaParamIndex = path.indexOf("|a=");
    if (areaParamIndex > 0) {
      return path.substring(0, areaParamIndex);
    } else {
      return path;
    }
  }

  public void dataRetrieved(final byte[] data) {
    toRetrieve[0].setImagesData(new byte[][] { data });
    mapTileSearchTask.retrieveSuccess();
  }
}
