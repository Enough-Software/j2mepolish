package com.nutiteq.location.cellid;

import com.mgmaps.utils.Tools;
import com.nutiteq.cache.Cache;
import com.nutiteq.core.MappingCore;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;

public abstract class OnlineCellIdService implements CellIdService, ResourceRequestor,
    ResourceDataWaiter {
  private CellIdResponseWaiter responseWaiter;
  private String requestUrl;

  public void setResponseWaiter(final CellIdResponseWaiter responseWaiter) {
    this.responseWaiter = responseWaiter;
  }

  public void retrieveLocation(final String cellId, final String lac, final String mcc,
      final String mnc) {
    requestUrl = createRequestUrl(cellId, lac, mcc, mnc);
    MappingCore.getInstance().getTasksRunner().enqueueDownload(this, Cache.CACHE_LEVEL_PERSISTENT);
  }

  public String resourcePath() {
    return requestUrl;
  }

  public void notifyError() {
    responseWaiter.notifyError();
  }

  public void dataRetrieved(final byte[] data) {
    final String response = Tools.byteArrayToString(data);
    parseResponse(responseWaiter, response);
  }

  public int getCachingLevel() {
    return Cache.CACHE_LEVEL_NONE;
  }

  public abstract void parseResponse(final CellIdResponseWaiter responseWaiter,
      final String response);

  public abstract String createRequestUrl(final String cellId, final String lac, final String mcc,
      final String mnc);
}
