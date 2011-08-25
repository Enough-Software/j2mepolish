package com.nutiteq.net;

import java.io.InputStream;

import com.mgmaps.utils.Tools;
import com.nutiteq.cache.Cache;
import com.nutiteq.components.MapTile;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.io.ResourceStreamWaiter;
import com.nutiteq.log.Log;
import com.nutiteq.maps.StreamedMap;
import com.nutiteq.utils.Utils;

public class StreamedTilesDownloadable implements ResourceRequestor, ResourceStreamWaiter {
  private final MapTile[] tiles;
  private final StreamedMap map;
  private int downloaded;

  public StreamedTilesDownloadable(final MapTile[] tiles, final StreamedMap map) {
    this.tiles = tiles;
    this.map = map;
  }

  public String resourcePath() {
    return map.buildStreamedPath(tiles);
  }

  public void notifyError() {

  }

  public void streamOpened(final InputStream is, final DownloadCounter counter,
      final Cache networkCache) {
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
    } catch (final Exception e) {
      Log.error("Streamed.read(): " + e.getMessage());
    }
  }

  public MapTile[] getFailedTiles() {
    final MapTile[] failed = new MapTile[tiles.length - downloaded];
    System.arraycopy(tiles, downloaded, failed, 0, failed.length);
    return failed;
  }

  public int getCachingLevel() {
    return Cache.CACHE_LEVEL_NONE;
  }
}
