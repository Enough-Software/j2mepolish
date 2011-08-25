package com.nutiteq.maps.projections;

import com.nutiteq.components.MapPos;
import com.nutiteq.components.Point;
import com.nutiteq.maps.BaseMap;
import com.nutiteq.maps.GeoMap;
import com.nutiteq.ui.Copyright;

/**
 * Abstract class for doing WGS84 coordinates calculations to map pixels in
 * EPSG4326 (Plate-carree (flat) projection, to be used with WMS) and back.
 * 
 * Supports custom per-zoom scale specification and standard parallel (phi0)
 */
public abstract class EPSG4326v2 extends BaseMap implements Projection, GeoMap {
  private final double equatorLength;
  
  public EPSG4326v2(final Ellipsoid ellipsoid, final Copyright copyright, final int tileSize, final int minZoom,
      final int maxZoom) {
    super(copyright, tileSize, minZoom, maxZoom);
    equatorLength = 2 * Math.PI * ellipsoid.getEquatorialRadius();
  }

  public EPSG4326v2(final Ellipsoid ellipsoid, final String copyright, final int tileSize, final int minZoom, final int maxZoom) {
    super(copyright, tileSize, minZoom, maxZoom);
    equatorLength = 2 * Math.PI * ellipsoid.getEquatorialRadius();
  }

  // only ratios <= 1 are accepted (?)
  private double yRatio = 1.0;
  // whether to add offset for ratio > 1
  private boolean addYOffset = true;
  // cos(standard parallel)
  private double xRatio = 1.0;
  // scales
  private double[] scales = null;
  
  public int getMapWidth(int zoom) {
    if (scales == null) {
      return super.getMapWidth(zoom);
    } else {
      return (int) Math.floor(equatorLength/scales[zoom-getMinZoom()]+0.5);
    }
  }
  
  public int getMapHeight(int zoom) {
    if (scales == null) {
      return super.getMapHeight(zoom);
    } else {
      return getMapWidth(zoom);
    }
  }

  public Point mapPosToWgs(final MapPos pos) {
    final int mapWidth = getMapWidth(pos.getZoom());
    final int mapHeight = getMapHeight(pos.getZoom());
    final int tmpX = pos.getX() % mapWidth;

    // compute map y offset
    final int yOffset = addYOffset ? 0 : ((int) Math.floor(mapHeight - mapHeight / yRatio + 0.5) / 2);
    // x
    final double tx = tmpX / xRatio / (mapWidth / 2) - 1D;
    final int lon = (int) (tx * 180000000D);
    // y
    final double ty = (pos.getY() - yOffset) / yRatio / (mapHeight / 2) - 1D;
    final int lat = (int) (ty * 90000000D);

    return new Point(lon, -lat);
  }

  public MapPos wgsToMapPos(final Point wgs, final int zoom) {
    final int mapWidth = getMapWidth(zoom);
    final int mapHeight = getMapHeight(zoom);
    // compute map y offset
    final int yOffset = addYOffset ? 0 : ((int) Math.floor(mapHeight - mapHeight / yRatio + 0.5) / 2);
    final int pX = wgs.getX();
    final int pY = wgs.getY();

    // x
    final double x = pX / 180000000D + 1D;
    final int cx = (int) (x * (mapWidth / 2) * xRatio);

    // y
    final double y = -pY / 90000000D + 1D;
    final int cy = (int) (y * (mapHeight / 2) * yRatio) + yOffset;

    final int tmpX = cx % mapWidth;
    return new MapPos(tmpX, cy, zoom);
  }

  /**
   * Set ratio between width and height. Use 1 for a "square world map" or 2 for
   * a stretched map. Only values >= 1 are accepted.
   * 
   * @param yRatio
   *          width/height ratio
   */
  public void setWidthHeightRatio(final double yRatio) {
    if (yRatio < 1.0) {
      throw new IllegalArgumentException("Ratio must be >= 1");
    }
    this.yRatio = yRatio;
  }
  
  /**
   * Set standard parallel (used for determining x ratio).
   * @param phi0 standard parallel
   */
  public void setStandardParallel(final double phi0) {
    if (phi0 <= -90.0 || phi0 >= 90.0) {
      throw new IllegalArgumentException("Standard parallel must be within [-90,90]");
    }
    this.xRatio = Math.cos(phi0*Math.PI/180);
  }
  
  /**
   * Set an array of scales used for various zoom levels.
   * @param scales null for default scales
   */
  public void setScales(double[] scales) {
    this.scales = scales;
  }
  
  /**
   * Whether to add Y offset when ratio > 1.
   * @param addYOffset
   */
  public void setAddYOffset(final boolean addYOffset) {
    this.addYOffset = addYOffset;
  }
  
  public boolean getAddYOffset() {
    return addYOffset;
  }

  public double getYRatio() {
    return yRatio;
  }
  
  public double getXRatio() {
    return xRatio;
  }
  
  public double[] getScales() {
    return scales;
  }

  public MapPos zoom(final MapPos middlePoint, final int zoomSteps) {
    if (scales == null) {
      return super.zoom(middlePoint, zoomSteps);
    } else {
      final Point currentMiddle = mapPosToWgs(middlePoint);
      return wgsToMapPos(currentMiddle, middlePoint.getZoom() + zoomSteps);
    }
  }
}
