package com.nutiteq.maps;

import java.io.IOException;

import com.mgmaps.utils.Tools;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.components.ZoomRange;
import com.nutiteq.config.StoredMapConfig;
import com.nutiteq.fs.FileSystem;
import com.nutiteq.log.Log;
import com.nutiteq.maps.projections.EPSG3785;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;
import com.nutiteq.utils.Utils;

/**
 *<p>
 * <strong>Note:</strong> to use stored map You need to also insert
 * {@link com.nutiteq.fs.FileSystem} into map component using
 * {@link com.nutiteq.BasicMapComponent#setFileSystem(FileSystem)}
 *</p>
 */
public class StoredMap extends EPSG3785 implements GeoMap, UnstreamedMap {

  private static final String CONFIG_FILENAME = "cache.conf";

  private final String path;

  // properties
  protected String name;
  private ZoomRange zoomRange;
  private int tilesPerFile;
  private int hashSize;
  // file extension
  private String fileExt;
  // tiles per file X & Y
  private int tpfx;
  private int tpfy;

  private boolean initializeConf;

  private StoredMapConfig config;

  private WgsPoint centerLocation;

  private int centerZoom;

  private static final int DEF_MIN_ZOOM = 1;
  private static final int DEF_MAX_ZOOM = 17;

  /**
   * Constructor for StoredMap. Default map name is the last component of the
   * path.
   * 
   * @param name
   *          map name
   * @param path
   *          root path for the stored map data
   * @param readCacheConf
   *          whether to read tiles_per_file and hash_size settings from
   *          cache.conf
   */
  public StoredMap(final String name, final String path, final boolean readCacheConf) {
    this(name, path, readCacheConf, new StringCopyright(name));
  }

  /**
   * Constructor for StoredMap. Default map name is the last component of the
   * path.
   * 
   * @param name
   *          map name
   * @param path
   *          root path for the stored map data
   * @param readCacheConf
   *          whether to read tiles_per_file and hash_size settings from
   *          cache.conf
   * @param copyright
   *          Text/image for the copyright overlay
   */
  public StoredMap(final String name, final String path, final boolean readCacheConf, final Copyright copyright) {
    super(copyright, 256, DEF_MIN_ZOOM, DEF_MAX_ZOOM);
    setName(name);
    String p = path;
    while (p.endsWith("/")) {
      p = p.substring(0, p.length() - 1);
    }
    if (p.startsWith("file://")) {
      p = p.substring(7);
    }
    if (p.startsWith("/")) {
      p = p.substring(1);
    }
    this.path = p;

    // default property values
    this.zoomRange = new ZoomRange(DEF_MIN_ZOOM, DEF_MAX_ZOOM);
    this.tilesPerFile = 1;
    this.tpfx = 1;
    this.tpfy = 1;
    this.hashSize = 1;
    this.fileExt = "mgm";
    this.config = new StoredMapConfig(tilesPerFile, tpfx, tpfy, hashSize);
    this.initializeConf = readCacheConf;
  }

  /**
   * Read cache.conf
   */
  public void initializeConfigUsingFs(final FileSystem fs) {
    this.initializeConf = false;
    this.config = readConfig(fs);

  }

  public StoredMapConfig readConfig(final FileSystem fs) {
    try {
      final String filename = path + "/" + CONFIG_FILENAME;
      final byte[] data = fs.readFile(filename);
      final String sdata = new String(data);
      final String[] lines = Utils.split(sdata, "\n");
      for (int i = 0; i < lines.length; i++) {
        // split into at most 2 tokens
        final String[] tokens = Tools.split(lines[i].trim(), '=', false, 2);
        if (tokens.length == 2) {
          final String name = tokens[0].trim().toLowerCase();
          final String value = tokens[1].trim();

          // ignore empty values
          if (value.length() == 0) {
            continue;
          }

          // ignore comments
          if (name.startsWith("#")) {
            continue;
          }

          if (name.equals("tiles_per_file")) {
            final int tpf = Integer.parseInt(value);
            if (tpf > 0 && (tpf & (-tpf)) == tpf) {
              setTilesPerFile(tpf);
            } else {
              throw new IOException("Invalid tiles_per_file");
            }
          } else if (name.equals("hash_size")) {
            final int hs = Integer.parseInt(value);
            if (hs >= 1 && hs < 100) {
              setHashSize(hs);
            } else {
              throw new IOException("Invalid hash_size");
            }
          } else if (name.equals("center")) {
            try {
              final String[] xyz = Tools.split(value.trim(), ',', false, 4);
              double lat = Float.parseFloat(xyz[0].trim());
              double lon = Float.parseFloat(xyz[1].trim());
              int zoom = Integer.parseInt(xyz[2].trim());
              Log.debug("center zoom found = " + lat + " " + lon + " " + zoom);
              setCenterLocation(new WgsPoint(lon, lat), zoom);
            } catch (final Exception ex) {

              throw new IOException("invalid center location");
            }
          }
        }
      }
    } catch (final IOException ex) {
      Log.error("Error reading " + CONFIG_FILENAME);
      Log.printStackTrace(ex);
      return null;
    }
    return new StoredMapConfig(tilesPerFile, tpfx, tpfy, hashSize);
  }

  private void setCenterLocation(WgsPoint wgsPoint, int zoom) {
    this.centerLocation = wgsPoint;
    this.centerZoom = zoom;
  }

  public WgsPoint getCenterLocation() {
    Log.debug("returning centerLocation "+centerLocation);
    return centerLocation;
  }

  public int getCenterZoom() {
    Log.debug("returning centerZoom"+centerZoom);
    return centerZoom;
  }

  /**
   * Set the name of this map.
   * 
   * @param name
   *          new name
   */
  public void setName(final String name) {
    this.name = name;
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

  public String buildPath(final int mapX, final int mapY, final int zoom) {
    final int mx = mapX / getTileSize() & ((1 << zoom) - 1);
    final int my = mapY / getTileSize();
    final StringBuffer result = new StringBuffer(path);
    result.append('/');
    result.append(name);
    result.append('_');
    result.append(zoom);
    result.append('/');
    if (hashSize > 1) {
      result.append((int) ((((long) mx) * getTileSize()) + my) % hashSize);
      result.append('/');
    }
    result.append((tilesPerFile > 1) ? (mx / tpfx) : mx);
    result.append('_');
    result.append((tilesPerFile > 1) ? (my / tpfy) : my);
    result.append('.');
    result.append(fileExt);

    // put dx and dy in filename, it's used as map tile ID
    if (tilesPerFile > 1) {
      result.append('|');
      result.append(mx % tpfx);
      result.append('_');
      result.append(my % tpfy);
    }
    return result.toString();
  }

  // set parameters

  /**
   * Set tiles per file.
   * 
   * @param tilesPerFile
   *          new value for tiles per file
   */
  public void setTilesPerFile(final int tilesPerFile) {
    this.tilesPerFile = tilesPerFile;
    final int tpflog = Utils.log2(tilesPerFile);
    tpfx = 1 << (tpflog / 2 + tpflog % 2);
    tpfy = 1 << (tpflog / 2);
  }

  /**
   * Set maximum zoom level.
   * 
   * @param maxZoom
   *          new max zoom level
   */
  public void setMaxZoom(final int maxZoom) {
    this.zoomRange = new ZoomRange(this.zoomRange.getMinZoom(), maxZoom);
  }

  /**
   * Set minimum zoom level.
   * 
   * @param minZoom
   *          new min zoom level
   */
  public void setMinZoom(final int minZoom) {
    this.zoomRange = new ZoomRange(minZoom, this.zoomRange.getMaxZoom());
  }

  /**
   * Set zoom range.
   * 
   * @param zoomRange
   *          new zoom range
   */
  public void setZoomRange(final ZoomRange zoomRange) {
    this.zoomRange = zoomRange;
  }

  /**
   * Set hash size.
   * 
   * @param hashSize
   *          new hash size value.
   */
  public void setHashSize(final int hashSize) {
    this.hashSize = hashSize;
  }

  /**
   * Get the hash size.
   * 
   * @return hashSize, default 1
   */
  public int getHashSize() {
    return hashSize;
  }

  /**
   * Get the number of tiles per file.
   * 
   * @return tilesPerFile, default 1
   */
  public int getTilesPerFile() {
    return tilesPerFile;
  }

  /**
   * Set extension for files used by stored maps.
   * 
   * @param fileExt
   *          file extension
   */
  public void setFileExtension(final String fileExt) {
    this.fileExt = fileExt;
  }

  /**
   * Get extension for files used by stored maps.
   * 
   * @return file extension, default "mgm"
   */
  public String getFileExtension() {
    return fileExt;
  }

  public StoredMapConfig getConfig() {
    return config;
  }

  public boolean isInitializeConf() {
    return initializeConf;
  }
}
