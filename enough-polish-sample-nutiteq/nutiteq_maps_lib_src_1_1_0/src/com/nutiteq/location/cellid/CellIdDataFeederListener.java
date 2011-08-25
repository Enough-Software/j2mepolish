package com.nutiteq.location.cellid;

import com.nutiteq.components.Cell;
import com.nutiteq.components.WgsPoint;

public interface CellIdDataFeederListener {
  void notifyError(WgsPoint pushedLocation);

  void pushSuccess(WgsPoint pushedLocation, Cell pushedCell);

  void pushFailed(WgsPoint pushedLocation, Cell pushedCell);
}
