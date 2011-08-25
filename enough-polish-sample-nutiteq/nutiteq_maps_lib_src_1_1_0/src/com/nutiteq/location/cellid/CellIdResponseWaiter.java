package com.nutiteq.location.cellid;

import com.nutiteq.components.WgsPoint;

/**
 * Interface for classes waiting location information from
 * {@link com.nutiteq.location.cellid.CellIdService}
 */
public interface CellIdResponseWaiter {

  void notifyError();

  void cantLocate();

  void locationRetrieved(WgsPoint wgsPoint);
}
