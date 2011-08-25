package com.nutiteq.task;

import java.io.IOException;
import java.io.InputStream;

import com.mgmaps.utils.Tools;
import com.nutiteq.components.MapTile;
import com.nutiteq.config.StoredMapConfig;
import com.nutiteq.fs.FileSystem;
import com.nutiteq.fs.FileSystemConnection;
import com.nutiteq.log.Log;
import com.nutiteq.maps.StoredMap;
import com.nutiteq.utils.IOUtils;

public class ReadStoredMapTileTask implements Task {
  private final MapTileSearchTask mapTileSearchTask;
  private final StoredMap map;
  private final FileSystem fs;
  private final MapTile[] toRetrieve;

  public ReadStoredMapTileTask(final MapTileSearchTask mapTileSearchTask,
      final MapTile[] toRetrieve, final StoredMap sMap, final FileSystem fileSystem) {
    this.mapTileSearchTask = mapTileSearchTask;
    this.toRetrieve = toRetrieve;
    this.map = sMap;
    this.fs = fileSystem;
  }

  public void execute() {
    final MapTile tile = toRetrieve[0];
    final int mx = tile.getX() / map.getTileSize() & ((1 << tile.getZoom()) - 1);
    final int my = tile.getY() / map.getTileSize();
    String filename = map.buildPath(tile.getX(), tile.getY(), tile.getZoom());

    Log.debug("ReadStoredMapTileTask "+filename);
    // TODO Jaak this could be incompatible with tilesperfile>1 config
    final int areaParamIndex = filename.indexOf("|a=");
    if (areaParamIndex > 0) {
      filename = filename.substring(0, areaParamIndex);
    } 
    Log.debug("ReadStoredMapTileTask fixed "+filename);
    
    if ("".equals(filename)) {
      mapTileSearchTask.retrieveErrorFor(toRetrieve);
      return;
    }

    final byte[] filedata;
    final StoredMapConfig conf = map.getConfig();
    try {
      if (conf.getTilesPerFile() > 1) {
        filename = filename.substring(0, filename.indexOf('|'));
        filedata = readMapTile(fs, filename, conf.getTilesPerFile(), mx % conf.getTpfx(), my
            % conf.getTpfy());
      } else {
        filedata = fs.readFile(filename);
      }
      tile.setImagesData(new byte[][] { filedata });
      mapTileSearchTask.retrieveSuccess();
    } catch (final Exception e) {
      Log.error("Error reading tile: " + e.getMessage());
      Log.printStackTrace(e);
      mapTileSearchTask.retrieveErrorFor(toRetrieve);
    }
  }

  /**
   * Read map tile from a file with multiple tiles.
   * 
   * @param filename
   *          file to read from
   * @param tilesPerFile
   *          maximum number of tiles stored in a file (power of two)
   * @param dx
   *          x-index of tile in block stored in file
   * @param dy
   *          y-index of tile in block stored in file
   * @return map tile data
   */
  private byte[] readMapTile(final FileSystem fs, final String filename, final int tilesPerFile,
      final int dx, final int dy) throws IOException {
    Log.debug("Loading file:///" + filename);

    FileSystemConnection fconn = null;
    InputStream is;
    try {
      fconn = fs.openConnectionToFile(filename);

      is = fconn.openInputStream();

      // read header
      int toRead = 6 * tilesPerFile + 2;
      final byte[] header = new byte[toRead];
      long ch = 0;
      int rd = 0;
      while ((rd < toRead) && (ch >= 0)) {
        ch = is.read(header, rd, toRead - rd);
        if (ch > 0) {
          rd += ch;
        }
      }

      // search for the tile
      final int numberOfTilesStored = (Tools.unsigned(header[0]) << 8) + Tools.unsigned(header[1]);
      int offset = -1;
      int offset2 = -1;
      final int n6 = numberOfTilesStored * 6;
      for (int i6 = 0; i6 < n6; i6 += 6) {
        if ((header[2 + i6] == dx || header[2 + i6] + 256 == dx)
            && (header[3 + i6] == dy || header[3 + i6] + 256 == dy)) {
          offset2 = (Tools.unsigned(header[4 + i6]) << 24) + (Tools.unsigned(header[5 + i6]) << 16)
              + (Tools.unsigned(header[6 + i6]) << 8) + (Tools.unsigned(header[7 + i6]));
          offset = (i6 == 0) ? toRead : ((Tools.unsigned(header[i6 - 2]) << 24)
              + (Tools.unsigned(header[i6 - 1]) << 16) + (Tools.unsigned(header[i6]) << 8) + (Tools
              .unsigned(header[i6 + 1])));
          break;
        }
      }

      if (offset < 0) {
        throw new IllegalArgumentException("Tile not found");
      }

      // seek
      IOUtils.skip(is, offset - toRead);

      // read data
      ch = 0;
      rd = 0;
      toRead = offset2 - offset;
      final byte[] result = new byte[toRead];
      while ((rd < toRead) && (ch >= 0)) {
        ch = is.read(result, rd, (toRead - rd) > IOUtils.BUFSIZE ? IOUtils.BUFSIZE : (toRead - rd));
        if (ch > 0) {
          rd += ch;
        }
      }

      return result;
    } finally {
      fconn.close();
    }
  }
}
