package com.nutiteq.components;

public class Cell {
  private final String cellId;
  private final String lac;
  private final String mcc;
  private final String mnc;

  public Cell(final String cellId, final String lac, final String mcc, final String mnc) {
    this.cellId = cellId;
    this.lac = lac;
    this.mcc = mcc;
    this.mnc = mnc;
  }

  public String getCellId() {
    return cellId;
  }

  public String getLac() {
    return lac;
  }

  public String getMcc() {
    return mcc;
  }

  public String getMnc() {
    return mnc;
  }
}
