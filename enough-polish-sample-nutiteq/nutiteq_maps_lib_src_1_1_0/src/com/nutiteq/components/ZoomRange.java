package com.nutiteq.components;

/**
 * Zoom range supported by map
 */
public class ZoomRange {
  private final int minZoom;
  private final int maxZoom;

  /**
   * Map zoom range
   * 
   * @param minZoom
   *          minimum zoom
   * @param maxZoom
   *          mazimum zoom
   */
  public ZoomRange(final int minZoom, final int maxZoom) {
    this.minZoom = minZoom;
    this.maxZoom = maxZoom;
  }

  public int getMinZoom() {
    return minZoom;
  }

  public int getMaxZoom() {
    return maxZoom;
  }
}
