package com.nutiteq.kml;

import com.nutiteq.components.LineStyle;
import com.nutiteq.components.PolyStyle;

public class KmlStyle {
  private final String styleId;
  private final float iconScale;
  private final String iconHref;
  private LineStyle lineStyle;
  private PolyStyle polyStyle;

  public KmlStyle(final String styleId, final float iconScale, final String iconHref) {
    this.styleId = styleId;
    this.iconScale = iconScale;
    this.iconHref = iconHref;
  }

  public String getStyleId() {
    return styleId;
  }

  public String getIconUrl() {
    return iconHref;
  }

  public float getIconScale() {
    return iconScale;
  }

  public void setLineStyle(final LineStyle lineStyle) {
    this.lineStyle = lineStyle;
  }

  public LineStyle getLineStyle() {
    return lineStyle;
  }

  public void setPolyStyle(final PolyStyle polyStyle) {
    this.polyStyle = polyStyle;
  }

  public PolyStyle getPolyStyle() {
    return polyStyle;
  }
}
