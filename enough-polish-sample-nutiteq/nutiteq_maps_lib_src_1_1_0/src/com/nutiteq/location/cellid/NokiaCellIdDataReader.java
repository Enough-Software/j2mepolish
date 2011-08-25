package com.nutiteq.location.cellid;

import com.nutiteq.utils.Utils;

public class NokiaCellIdDataReader implements CellIdDataReader {
  public String getCellId() {
    String cellid = System.getProperty("Cell-ID");

    if (cellid == null || "".equals(cellid)) {
      cellid = System.getProperty("com.nokia.mid.cellid");
    }

    return cellid;
  }

  public String getLac() {
    return System.getProperty("com.nokia.mid.lac");
  }

  public String getMcc() {
    return System.getProperty("com.nokia.mid.countrycode");
  }

  public String getMnc() {
    String mnc = System.getProperty("com.nokia.mid.networkid");
    if (mnc != null && !"".equals(mnc)) {
      mnc = Utils.split(mnc, " ")[0];
    }
    return mnc;
  }
}
