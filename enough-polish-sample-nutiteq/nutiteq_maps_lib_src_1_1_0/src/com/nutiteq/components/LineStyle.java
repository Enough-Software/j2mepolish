package com.nutiteq.components;

/**
 * Style object for Line
 */
public class LineStyle {
  public static final int DEFAULT_COLOR = 0xFF0000FF;
  public static final int DEFAULT_WIDTH = 2;
  public static final LineStyle DEFAULT_STYLE = new LineStyle(DEFAULT_COLOR, DEFAULT_WIDTH);

  private final int color;
  private final int width;

  /**
   * Constructor for line style object
   * 
   * @param color
   *          line color in format ARGB
   * @param width
   *          line width
   */
  public LineStyle(final int color, final int width) {
    this.color = color;
    this.width = width;
  }

  /**
   * Not part of public API
   * 
   * @return line color
   */
  public int getColor() {
    return color;
  }

  /**
   * Not part of public API
   * 
   * @return line width
   */
  public int getWidth() {
    return width;
  }
}
