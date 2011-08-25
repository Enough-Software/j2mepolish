package com.nutiteq.components;

public class AnimatedLine extends Line implements Animated {
  private final float framerate;

  public AnimatedLine(final WgsPoint[] points, final float framerate) {
    super(points);
    this.framerate = framerate;
  }

  public AnimatedLine(final WgsPoint[] points, final LineStyle style, final float framerate) {
    super(points, style);
    this.framerate = framerate;
  }

  public float getFramerate() {
    return framerate;
  }
}
