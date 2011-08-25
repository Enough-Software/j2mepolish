package com.nutiteq.components;

public class PolyStyle {
  public static final int DEFAULT_COLOR = 0x660000FF;
  public static final PolyStyle DEFAULT_STYLE = new PolyStyle(DEFAULT_COLOR);
  private final int color;

  public PolyStyle(final int color) {
    this.color = color;
  }

  public int getColor() {
    return color;
  }
}
