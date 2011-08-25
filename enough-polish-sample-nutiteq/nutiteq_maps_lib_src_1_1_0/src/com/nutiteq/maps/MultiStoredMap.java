package com.nutiteq.maps;

import henson.midp.Float11;

import java.io.IOException;
import java.util.Vector;

import com.mgmaps.utils.Tools;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.MultiMapSingleConfig;
import com.nutiteq.components.TileMapBounds;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.config.StoredMapConfig;
import com.nutiteq.fs.FileSystem;
import com.nutiteq.log.Log;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;
import com.nutiteq.utils.Utils;

public class MultiStoredMap extends StoredMap {
  private static final String CONFIG_FILENAME = "cache.conf";
  private final String baseBath;
  private boolean needToInitialize = true;
  private final String name;
  private MultiMapSingleConfig[] areaConfigurations;
  private final String fileExt;
  private StoredMapConfig storedMapConfig;

//  private ZoomRange zoomRange = new ZoomRange(Integer.MAX_VALUE,Integer.MIN_VALUE);
  
/**
 * Enables to use multiple stored map packages. 
 * All packages must be in the same directory tree, as next level sub-directories, and must use the same map name
 * example usage: mapComponent.setMap(new MultiStoredMap("OpenStreetMap", "root1/maps"));
 *  
 * @param name Name of map. Must be the same as used in package directory structure
 * @param basePath File System root directory for the map packages, e.g. "root1/maps"
 */

  public MultiStoredMap(final String name, final String basePath) {
    this(name,basePath,new StringCopyright(name));
  }

  /**
   * Enables to use multiple stored map packages. 
   * All packages must be in the same directory tree, as next level sub-directories, and must use the same map name
   * example usage: mapComponent.setMap(new MultiStoredMap("OpenStreetMap", "root1/maps",new StringCopyright("(c) OSM")));
   *  
   * @param name Name of map. Must be the same as used in package directory structure
   * @param basePath File System root directory for the map packages, e.g. "root1/maps"
   * @param copyright CopyRight text/image for the overlay
   */

    public MultiStoredMap(final String name, final String basePath, final Copyright copyright ) {
      super(name, "", false, copyright);
      this.name = name;
      String p = basePath;
      if (p.startsWith("/")) {
        p = p.substring(1);
      }
      this.baseBath = p;
      this.fileExt = "mgm";
    }

  
  
  public void initializeConfigUsingFs(final FileSystem fs) {
    final Vector confs = new Vector();
    try {
      final Vector listFiles = fs.listFiles(baseBath);
      for (int i = 0; i < listFiles.size(); i++) {
        final String file = (String) listFiles.elementAt(i);
        Log.debug(file);
        if (file.startsWith(".")) {
          continue;
        }

        final String maybeTilesDir = baseBath + "/" + file;
        if (fs.isDirectory(maybeTilesDir) && containsCacheConf(fs, maybeTilesDir)) {
          Log.debug("found cache conf in " + maybeTilesDir);
          final MultiMapSingleConfig conf = loadConfig(fs, maybeTilesDir);
          if (conf != null && conf.isValid()) {
            Log.debug("adding conf for: " + conf.toString());
            confs.addElement(conf);
          }
        }
      }
    } catch (final IOException e) {
      Log.error("MultiStoredMap: " + e.getClass() + " : " + e.getMessage());
      Log.printStackTrace(e);
    }

    Log.debug("loaded " + confs.size() + " confs");
    areaConfigurations = new MultiMapSingleConfig[confs.size()];
    confs.copyInto(areaConfigurations);

    if (areaConfigurations.length == 0) {
      throw new RuntimeException("No cofigurations found in " + baseBath);
    }

    needToInitialize = false;
  }

  public String buildPath(final int mapX, final int mapY, final int zoom) {
      if (areaConfigurations != null) {
            for (int i = 0; i < areaConfigurations.length; i++) {
                final MultiMapSingleConfig config = areaConfigurations[i];
                if (config.contains(mapX, mapY, zoom, getTileSize())) {
                    return buildPath(config.getTilesDir(), mapX, mapY, zoom,
                            config.getHashSize(), config.getTilesPerFile(),
                            config.getTpfx(), config.getTpfy());
                }
            }
        }else{
            Log.debug("areaConfigurations is null");
        }
      
    Log.debug(new StringBuffer("could_not_map_").append(mapX).append("_").append(mapY).append("_")
        .append(zoom).toString());
    return "";
  }

  //TODO jaanus : refactor this
  public String buildPath(final String path, final int mapX, final int mapY, final int zoom,
      final int hashSize, final int tilesPerFile, final int tpfx, final int tpfy) {
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

  public StoredMapConfig getConfig() {
    //TODO jaanus : needed for reading in ReadStoredMapTileTask. change it!
    if (storedMapConfig == null) {
      storedMapConfig = new StoredMapConfig(areaConfigurations[0].getTilesPerFile(),
          areaConfigurations[0].getTpfx(), areaConfigurations[0].getTpfy(), areaConfigurations[0]
              .getTilesPerFile());
    }
    return storedMapConfig;
  }

  private MultiMapSingleConfig loadConfig(final FileSystem fs, final String tilesDir)
      throws IOException {
    final MultiMapSingleConfig result = new MultiMapSingleConfig(tilesDir, name);
    final Vector areas = new Vector();
    final byte[] data = fs.readFile(tilesDir + "/" + CONFIG_FILENAME);
    final String sdata = new String(data);
    Log.debug("read config >> " + sdata);
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
            result.setTilesPerFile(tpf);
          } else {
            throw new IOException("Invalid tiles_per_file");
          }
        } else if (name.equals("hash_size")) {
          final int hs = Integer.parseInt(value);
          if (hs >= 1 && hs < 100) {
            result.setHashSize(hs);
          } else {
            throw new IOException("Invalid hash_size");
          }
        }
      } else if (lines[i].indexOf(":") > 0) {
        final String[] areaTokens = Tools.split(lines[i].trim(), ':', false, 3);
        if (areaTokens.length != 3) {
          continue;
        }

        final String zoomLevels = areaTokens[0];
        final int minZoom = Integer.parseInt(zoomLevels.substring(0, zoomLevels.indexOf('-')));
        final int maxZoom = Integer.parseInt(zoomLevels.substring(zoomLevels.indexOf('-') + 1));
        
        if(minZoom<result.getMinZoom()){
         result.setMinZoom(minZoom);
        }
        if(maxZoom>result.getMaxZoom()){
            result.setMaxZoom(maxZoom);
        }
        
        final WgsPoint areaMin = WgsPoint.parsePoint(WgsPoint.FORMAT_LAT_LON, areaTokens[1], ",");
        final WgsPoint areaMax = WgsPoint.parsePoint(WgsPoint.FORMAT_LAT_LON, areaTokens[2], ",");
        for (int z = minZoom; z <= maxZoom; z++) {
          final MapPos min = wgsToMapPos(new WgsPoint(areaMin.getLon(), areaMax.getLat())
              .toInternalWgs(), z);
          final MapPos max = wgsToMapPos(new WgsPoint(areaMax.getLon(), areaMin.getLat())
              .toInternalWgs(), z);
          areas.addElement(new TileMapBounds(min, max));
        }
      }
    }

    Log.debug("found " + areas.size() + " areas for " + tilesDir);
    final TileMapBounds[] bounds = new TileMapBounds[areas.size()];
    areas.copyInto(bounds);

    result.setTileBounds(bounds);

    return result;
  }

  private boolean containsCacheConf(final FileSystem fs, final String maybeTilesDir)
      throws IOException {
    final Vector files = fs.listFiles(maybeTilesDir);

    for (int i = 0; i < files.size(); i++) {
      final String file = (String) files.elementAt(i);
      if (CONFIG_FILENAME.equals(file)) {
        return true;
      }
    }

    return false;
  }

  public boolean isInitializeConf() {
    return needToInitialize;
  }
  
 /* 
public int getMaxZoom() {
    return zoomRange.getMaxZoom();
}

public int getMinZoom() {
    return zoomRange.getMinZoom();
}

public ZoomRange getZoomRange() {
    return zoomRange;
}
*/
public int[] getSupportedZoomLevels() {
      if (areaConfigurations==null){
          return new int[0];
      }
   int levels = 0; // will have bitmap of supported levels
   for (int i=0;i<areaConfigurations.length;i++){
      TileMapBounds[] bounds = areaConfigurations[i].getTileBounds();
      for (int j=0;j<bounds.length;j++){
//          Log.debug("found zoom level " + bounds[j].getZoomLevel());
          levels|=(int) Float11.pow(2,bounds[j].getZoomLevel()); // set n'th bit
      }
  }

   // number of bits set?
   byte c = 0; 
   for(int i=0;i<32;i++){ // 32 bits per int
       if((levels & (1<<i))>0){
        c++;
       }
   }
   // create result array
   int[] levelsArray = new int[c];
   byte j=0;
   for(int i=0;i<32;i++){
       if((levels & (1<<i)) > 0){
           levelsArray[j++] = i;
       }
   }
  
   return levelsArray;
  }
  
}
