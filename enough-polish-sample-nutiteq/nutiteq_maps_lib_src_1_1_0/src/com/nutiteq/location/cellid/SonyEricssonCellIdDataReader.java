package com.nutiteq.location.cellid;


/**
 * <p>
 * Cell id positioning data reader for SonyEricsson devices. SonyEricsson
 * devices on JP7.3+ are supported.
 * </p>
 */
public class SonyEricssonCellIdDataReader implements CellIdDataReader {
  public String getCellId() {
    return System.getProperty("com.sonyericsson.net.cellid");
  }

  public String getLac() {
    return System.getProperty("com.sonyericsson.net.lac");
  }

  public String getMcc() {
    return System.getProperty("com.sonyericsson.net.cmcc");
  }

  public String getMnc() {
    return System.getProperty("com.sonyericsson.net.cmnc");
  }
}
