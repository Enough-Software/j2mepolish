package com.nutiteq.components;

/**
 * Generic distance object. Holds the distance value and unit of measure.
 */
public class Distance {
  private final String unitOfMeasure;
  private final float value;

  public Distance(final float value, final String unitOfMeasure) {
    this.value = value;
    this.unitOfMeasure = unitOfMeasure;

  }

  public float getValue() {
    return value;
  }

  public String getUnitOfMeasure() {
    return unitOfMeasure;
  }
}
