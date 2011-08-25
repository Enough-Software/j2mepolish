package com.nutiteq.location.cellid;


/**
 * Implementation interface for retrieving data required for CellId location
 * positioning.
 */
public interface CellIdDataReader {
  /**
   * Get CellId
   * 
   * @return cell id
   */
  String getCellId();

  /**
   * Get mobile country code
   * 
   * @return mobile country code
   */
  String getMcc();

  /**
   * Get mobile network code
   * 
   * @return mobile network code
   */
  String getMnc();

  /**
   * Get location area code
   * 
   * @return location area code
   */
  String getLac();
}
