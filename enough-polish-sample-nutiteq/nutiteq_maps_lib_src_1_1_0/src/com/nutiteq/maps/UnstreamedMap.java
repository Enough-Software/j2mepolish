package com.nutiteq.maps;

/**
 * Interface for map definition, that retrieves every map tile separately.
 * 
 * @see StreamedMap
 */
public interface UnstreamedMap {
  /**
   * Build path for the tile image. Images reading from network and application
   * jar is supported.
   * 
   * @param mapX
   *          tile top-left corner x on pixel map
   * @param mapY
   *          tile top-left corner x on pixel map
   * @param zoom
   *          zoom level
   * @return path for the tile image.  
   *    For on-line URL must start with "http://" or "https://"; 
   *    for filesystem starts with "file://"; 
   *    for application package resource starts with "/"
   *          
   */
  String buildPath(int mapX, int mapY, int zoom);
}
