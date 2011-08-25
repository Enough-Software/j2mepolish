package com.nutiteq.location.cellid;


/**
 * <p>
 * Motorola implementation for accessing data required for cell id positioning.
 * </p>
 * <p>
 * <strong>NOTE:</strong> MIDlet must be trusted (digitally signed by a CA
 * trusted by the device) for this to work.
 * </p>
 */
public class MotorolaCellIdDataReader implements CellIdDataReader {
  public String getCellId() {
    return System.getProperty("CellID");
  }

  public String getLac() {
    return System.getProperty("LocAreaCode");
  }

  public String getMcc() {
    final String imsi = System.getProperty("IMSI");
    return imsi.substring(0, 3);
  }

  public String getMnc() {
    final String imsi = System.getProperty("IMSI");
    return imsi.substring(3, 5);
  }

}
