package com.nutiteq.location.cellid;

import com.nutiteq.components.WgsPoint;
import com.nutiteq.utils.Utils;

public class HackIdService extends OnlineCellIdService {
  private static final String BASEURL = "http://8.17.168.73/cell.php?";

  public String createRequestUrl(final String cellId, final String lac, final String mcc,
      final String mnc) {
    return new StringBuffer(BASEURL).append("myl=").append(mcc).append(":").append(mnc).append(":")
        .append(lac).append(":").append(cellId).toString();
  }

  public void parseResponse(final CellIdResponseWaiter responseWaiter, final String response) {
    if (response.indexOf("Lat=") < 0) {
      responseWaiter.cantLocate();
    } else {
      final String[] split = Utils.split(response, ";");
      final String latString = split[0];
      final String lonString = split[1];
      final double lon = Double.parseDouble(Utils.split(lonString, "=")[1]);
      final double lat = Double.parseDouble(Utils.split(latString, "=")[1]);
      responseWaiter.locationRetrieved(new WgsPoint(lon, lat));
    }
  }
}
