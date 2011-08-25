package com.nutiteq.maps.projections;

import com.nutiteq.ui.Copyright;

/**
 * L-EST97 projection used in Estonia, based on Lambert conformal conic
 * projection.
 */
public abstract class EPSG3301 extends Lambert {
  public EPSG3301(final Copyright copyright, final int tileSize, final int minZoom,
      final int maxZoom) {
    super(Ellipsoid.GRS80, copyright, tileSize, minZoom, maxZoom);
  }

  public EPSG3301(final String copyright, final int tileSize, final int minZoom, final int maxZoom) {
    super(Ellipsoid.GRS80, copyright, tileSize, minZoom, maxZoom);
  }
}
