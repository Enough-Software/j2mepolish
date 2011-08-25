package com.nutiteq.location.cellid;

import com.nutiteq.cache.Cache;
import com.nutiteq.components.Cell;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.core.MappingCore;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.location.LocationListener;
import com.nutiteq.log.Log;

public class OpenCellIdDataFeeder implements LocationListener {
  private WgsPoint lastLocation;
  private final CellIdDataReader cellIdDataReader;
  private final String developerKey;
  private final CellIdDataFeederListener feederListener;

  public OpenCellIdDataFeeder(final String developerKey,
      final CellIdDataFeederListener feederListener) {
    this.developerKey = developerKey;
    this.feederListener = feederListener;
    cellIdDataReader = new SonyEricssonCellIdDataReader();
  }

  public void setLocation(final WgsPoint location) {
    try {
      if (location == null
          || (lastLocation != null && WgsPoint.distanceInMeters(lastLocation, location) < 300)) {
        Log.debug(location + " not pushed");
        return;
      }

      final String cellId = cellIdDataReader.getCellId();
      final String lac = cellIdDataReader.getLac();
      final String mcc = cellIdDataReader.getMcc();
      final String mnc = cellIdDataReader.getMnc();

      if (cellId == null || lac == null || mcc == null || mnc == null) {
        Log.error("Could not push " + cellId + " : " + lac + " : " + mcc + " : " + mnc);
        return;
      }

      final WgsPoint pushedLocation = location.toInternalWgs().toWgsPoint();
      lastLocation = pushedLocation;

      final ResourceRequestor pushTask = new OpenCellIdMeasurePushTask(developerKey,
          pushedLocation, new Cell(cellId, lac, mcc, mnc), feederListener);

      MappingCore.getInstance().getTasksRunner().enqueueDownload(pushTask, Cache.CACHE_LEVEL_NONE);
    } catch (final Exception e) {
      Log.error("Push error " + e.getMessage());
    }
  }
}
