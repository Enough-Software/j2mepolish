package com.nutiteq.io;

import java.io.IOException;
import java.io.InputStream;

import com.mgmaps.utils.Tools;
import com.nutiteq.cache.Cache;
import com.nutiteq.components.MapTile;
import com.nutiteq.log.Log;
import com.nutiteq.maps.StreamedMap;
import com.nutiteq.net.DownloadCounter;
import com.nutiteq.task.MapTileSearchTask;
import com.nutiteq.utils.Utils;

public class StreamedTileRetriever implements ResourceRequestor, ResourceStreamWaiter {
  private final MapTileSearchTask mapTileSearchTask;
  private final MapTile[] tiles;
  private final StreamedMap map;
  private int downloaded;

  public StreamedTileRetriever(final MapTileSearchTask mapTileSearchTask,
      final MapTile[] toRetrieve, final StreamedMap map) {
    this.mapTileSearchTask = mapTileSearchTask;
    this.tiles = toRetrieve;
    this.map = map;
  }

  public int getCachingLevel() {
    return Cache.CACHE_LEVEL_NONE;
  }

  public void notifyError() {
    mapTileSearchTask.retrieveErrorFor(tiles);
  }

  public String resourcePath() {
    return map.buildStreamedPath(tiles);
  }

  public void streamOpened(final InputStream is, final DownloadCounter counter,
      final Cache networkCache) throws IOException {
    //TODO jaanus : maybe add tile coordinates check
    try {
      for (; downloaded < tiles.length; downloaded++) {
        // read a line
        final String line = Tools.readLine2(is);

        final String[] splits = Utils.split(line.trim(), ",");
        // get length
        final int len = Integer.parseInt(splits[2]);
        // read data
        final byte[] data = new byte[len];
        int ch = 0;
        int rd = 0;
        while ((rd != len) && (ch != -1)) {
          ch = is.read(data, rd, len - rd);
          if (ch > 0) {
            rd += ch;
          }
        }
        // read \r\n
        is.read();

        // notify downloaded
        tiles[downloaded].setImagesData(new byte[][] { data });
        if (networkCache != null) {
          networkCache.cache(tiles[downloaded].getIDString(), data, Cache.CACHE_LEVEL_MEMORY
              | Cache.CACHE_LEVEL_PERSISTENT);
        }
        if (counter != null) {
          counter.downloaded(len + line.length());
        }
      }
      mapTileSearchTask.retrieveSuccess();
    } catch (final Exception e) {
      Log.error("Streamed.read(): " + e.getMessage());
      Log.printStackTrace(e);
      final MapTile[] failed = new MapTile[tiles.length - downloaded];
      System.arraycopy(tiles, downloaded, failed, 0, failed.length);
      mapTileSearchTask.retrieveErrorFor(failed);
    }
  }
}
