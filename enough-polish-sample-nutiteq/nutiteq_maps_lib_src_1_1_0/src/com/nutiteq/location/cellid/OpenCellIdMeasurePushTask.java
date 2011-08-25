package com.nutiteq.location.cellid;

import com.nutiteq.cache.Cache;
import com.nutiteq.components.Cell;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.log.Log;

public class OpenCellIdMeasurePushTask implements ResourceRequestor, ResourceDataWaiter {
  private static final String BASEURL = "http://www.opencellid.org/measure/add?";
  private final String developerKey;
  private final WgsPoint pushedLocation;
  private final Cell pushedCell;
  private final CellIdDataFeederListener feederListener;

  public OpenCellIdMeasurePushTask(final String developerKey, final WgsPoint pushedLocation,
      final Cell pushedCell, final CellIdDataFeederListener feederListener) {
    this.developerKey = developerKey;
    this.pushedLocation = pushedLocation;
    this.pushedCell = pushedCell;
    this.feederListener = feederListener;
  }

  public String resourcePath() {
    final int cellId10 = Integer.parseInt(pushedCell.getCellId(), 16);
    final StringBuffer addMeasureUrl = new StringBuffer(BASEURL);
    addMeasureUrl.append("key=").append(developerKey);
    addMeasureUrl.append("&mnc=").append(pushedCell.getMnc());
    addMeasureUrl.append("&mcc=").append(pushedCell.getMcc());
    addMeasureUrl.append("&lac=").append(Integer.parseInt(pushedCell.getLac(), 16));
    addMeasureUrl.append("&cellid=").append(cellId10);
    addMeasureUrl.append("&lat=").append(pushedLocation.getLat());
    addMeasureUrl.append("&lon=").append(pushedLocation.getLon());
    return addMeasureUrl.toString();
  }

  public void notifyError() {
    Log.error("Data feed error!");
    if (feederListener != null) {
      feederListener.notifyError(pushedLocation);
    }
  }

  public void dataRetrieved(final byte[] data) {
    final String response = new String(data);
    if (response.indexOf("stat=\"ok\"") > 0) {
      if (feederListener != null) {
        feederListener.pushSuccess(pushedLocation, pushedCell);
      }
    } else {
      if (feederListener != null) {
        feederListener.pushFailed(pushedLocation, pushedCell);
      }
    }
  }

  public int getCachingLevel() {
    return Cache.CACHE_LEVEL_NONE;
  }
}
