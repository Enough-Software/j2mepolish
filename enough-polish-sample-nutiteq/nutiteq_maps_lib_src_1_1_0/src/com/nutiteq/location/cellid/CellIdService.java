package com.nutiteq.location.cellid;

/**
 * Interface for defining custom cellid services.
 */
public interface CellIdService {
  /**
   * Set object waiting for response from this service
   * 
   * @param responseWaiter
   *          response waiting object
   */
  void setResponseWaiter(CellIdResponseWaiter responseWaiter);

  /**
   * Retrieve location with given cellid information
   * 
   * @param cellId
   *          cell id as hex number
   * @param lac
   *          location area code as hex number
   * @param mcc
   *          mobile country code
   * @param mnc
   *          mobile network code
   */
  void retrieveLocation(String cellId, String lac, String mcc, String mnc);
}
