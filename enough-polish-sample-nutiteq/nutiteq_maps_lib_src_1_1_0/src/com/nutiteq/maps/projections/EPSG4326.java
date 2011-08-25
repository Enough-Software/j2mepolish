package com.nutiteq.maps.projections;

import com.nutiteq.components.MapPos;
import com.nutiteq.components.Point;
import com.nutiteq.maps.BaseMap;
import com.nutiteq.maps.GeoMap;
import com.nutiteq.ui.Copyright;

/**
 * Abstract class for doing WGS84 coordinates calculations to map pixels in
 * EPSG4326 (Plate-carree (flat) projection, to be used with WMS) and back.
 */
public abstract class EPSG4326 extends BaseMap implements Projection, GeoMap {
  public EPSG4326(final Copyright copyright, final int tileSize, final int minZoom,
      final int maxZoom) {
    super(copyright, tileSize, minZoom, maxZoom);
  }

  public EPSG4326(final String copyright, final int tileSize, final int minZoom, final int maxZoom) {
    super(copyright, tileSize, minZoom, maxZoom);
  }

  // only ratios >= 1 are accepted
  private double ratio = 1.0;

  public Point mapPosToWgs(final MapPos pos) {
    final int mapWidth = getMapWidth(pos.getZoom());
    final int mapHeight = getMapHeight(pos.getZoom());
    final int tmpX = pos.getX() % mapWidth;

    // compute map y offset
    final int offset = (int) Math.floor(mapHeight - mapHeight / ratio + 0.5) / 2;
    // x
    final double tx = ((double) tmpX) / (mapWidth / 2) - 1D;
    final int lon = (int) (tx * 180000000D);
    // y
    final double ty = (pos.getY() - offset) / ((mapHeight / 2) / ratio) - 1D;
    final int lat = (int) (ty * 90000000D);

    return new Point(lon, -lat);
  }

  public MapPos wgsToMapPos(final Point wgs, final int zoom) {
    final int mapWidth = getMapWidth(zoom);
    final int mapHeight = getMapHeight(zoom);
    // compute map y offset
    final int offset = (int) Math.floor(mapHeight - mapHeight / ratio + 0.5) >> 1;
    final int pX = wgs.getX();
    final int pY = wgs.getY();

    // x
    final double x = pX / 180000000D + 1D;
    final int cx = (int) (x * (mapWidth / 2));

    // y
    final double y = -pY / 90000000D + 1D;
    final int cy = (int) (y * (mapHeight / 2) / ratio) + offset;

    final int tmpX = cx % mapWidth;
    return new MapPos(tmpX, cy, zoom);
  }

  /**
   * Set ratio between width and height. Use 1 for a "square world map" or 2 for
   * a stretched map. Only values >= 1 are accepted
   * 
   * @param ratio
   *          width/height ratio
   */
  public void setWidthHeightRatio(final double ratio) {
    if (ratio < 1.0) {
      throw new IllegalArgumentException("Ratio must be >= 1");
    }
    this.ratio = ratio;
  }

  public double getRatio() {
    return ratio;
  }
}
