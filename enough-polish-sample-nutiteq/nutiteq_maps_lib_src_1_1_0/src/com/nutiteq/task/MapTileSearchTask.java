package com.nutiteq.task;

import java.util.Vector;

import com.nutiteq.cache.Cache;
import com.nutiteq.components.MapTile;
import com.nutiteq.io.StreamedTileRetriever;
import com.nutiteq.io.UnstreamedTileRetriever;
import com.nutiteq.maps.GeoMap;
import com.nutiteq.maps.MapTilesRequestor;
import com.nutiteq.maps.StoredMap;
import com.nutiteq.maps.StreamedMap;
import com.nutiteq.maps.UnstreamedMap;

public class MapTileSearchTask implements Task {
  private final MapTilesRequestor tilesRequestor;
  private final GeoMap[] tileSearchStrategy;
  private final TasksRunner taskRunner;
  private final int strategyIndex;
  private MapTile[] toRetrieve;

  public MapTileSearchTask(final MapTilesRequestor tilesRequestor,
      final GeoMap[] tileSearchStrategy, final TasksRunner taskRunner) {
    this(tilesRequestor, tileSearchStrategy, taskRunner, 0, new MapTile[0]);
  }

  public MapTileSearchTask(final MapTilesRequestor tilesRequestor,
      final GeoMap[] tileSearchStrategy, final TasksRunner taskRunner, final int strategyIndex,
      final MapTile[] toRetrieve) {
    this.tilesRequestor = tilesRequestor;
    this.tileSearchStrategy = tileSearchStrategy;
    this.taskRunner = taskRunner;
    this.strategyIndex = strategyIndex;
    this.toRetrieve = toRetrieve;
  }

  public void execute() {
    if (strategyIndex == tileSearchStrategy.length) {
      finishRetrieval(toRetrieve);
      //start next search
      retrieveSuccess();
      return;
    }

    final GeoMap map = tileSearchStrategy[strategyIndex];

    if (strategyIndex == 0) {
      toRetrieve = appendTiles(toRetrieve, pullNeededTiles(map, tilesRequestor));
    }

    if (toRetrieve.length == 0) {
      return;
    }

    if (map instanceof StoredMap) {
      //TODO jaanus : try to megre this with unstreamed/streamed implementation
      final StoredMap sMap = (StoredMap) map;
      if (sMap.isInitializeConf()) {
        sMap.initializeConfigUsingFs(taskRunner.getFileSystem());
      }

      taskRunner.enqueue(new ReadStoredMapTileTask(this, toRetrieve, sMap, taskRunner
          .getFileSystem()));
    } else if (map instanceof UnstreamedMap) {
      taskRunner.enqueueDownload(
          new UnstreamedTileRetriever(this, toRetrieve, (UnstreamedMap) map),
          Cache.CACHE_LEVEL_NONE);
    } else if (map instanceof StreamedMap) {
      toRetrieve = findStreamedFromCache(toRetrieve, (StreamedMap) map, taskRunner
          .getNetworkCache());
      if (toRetrieve.length == 0) {
        //TODO jaanus : check this
        retrieveSuccess();
        return;
      }
      taskRunner.enqueueDownload(new StreamedTileRetriever(this, toRetrieve, (StreamedMap) map),
          Cache.CACHE_LEVEL_NONE);
    }
  }

  private MapTile[] findStreamedFromCache(final MapTile[] tiles, final StreamedMap map,
      final Cache networkCache) {
    if (networkCache == null) {
      return tiles;
    }

    final Vector uncached = new Vector();
    for (int i = 0; i < tiles.length; i++) {
      final MapTile t = tiles[i];
      if (networkCache.contains(t.getIDString())) {
        final byte[] tc = networkCache.get(t.getIDString());
        if (tc != null) {
          t.setImagesData(new byte[][] { tc });
        } else { // file not found from cache
          uncached.addElement(t);
        }
      } else {
        uncached.addElement(t);
      }
    }

    final MapTile[] result = new MapTile[uncached.size()];
    uncached.copyInto(result);
    return result;
  }

  private MapTile[] appendTiles(final MapTile[] toRetrieve, final MapTile[] additionalTiles) {
    final int titalSize = toRetrieve.length + additionalTiles.length;
    final MapTile[] result = new MapTile[titalSize];

    System.arraycopy(toRetrieve, 0, result, 0, toRetrieve.length);
    System.arraycopy(additionalTiles, 0, result, toRetrieve.length, additionalTiles.length);

    return result;
  }

  private MapTile[] pullNeededTiles(final GeoMap map, final MapTilesRequestor tilesRequestor) {
    MapTile[] result = new MapTile[0];
    if (map instanceof UnstreamedMap) {
      final MapTile required = tilesRequestor.getRequiredTile();
      if (required != null) {
        result = new MapTile[] { required };
      }
    } else {
      result = tilesRequestor.getAllRequiredTiles();
    }

    return result;
  }

  private void finishRetrieval(final MapTile[] failedTiles) {
    for (int i = 0; i < failedTiles.length; i++) {
      failedTiles[i].notifyError();
    }
  }

  public void retrieveErrorFor(final MapTile[] errorTiles) {
    taskRunner.enqueue(new MapTileSearchTask(tilesRequestor, tileSearchStrategy, taskRunner,
        strategyIndex + 1, errorTiles));
  }

  public void retrieveSuccess() {
    taskRunner.enqueue(new MapTileSearchTask(tilesRequestor, tileSearchStrategy, taskRunner));
  }
}
