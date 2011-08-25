package com.nutiteq.location.cellid;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import com.nutiteq.components.WgsPoint;
import com.nutiteq.log.Log;
import com.nutiteq.utils.IOUtils;
import com.nutiteq.utils.Utils;

/**
 * CellId using <a
 * href="https://labs.ericsson.com/apis/mobile-location/">Ericsson Labs Mobile
 * Location API</a>
 */
public class EricssonLocationApiService extends OnlineCellIdService {
  private static final String BASEURL = "http://cellid.labs.ericsson.net/json/lookup?";
  private String ericssonKey;

  public EricssonLocationApiService(String ericssonKey) {
    this.ericssonKey = ericssonKey;
  }

  public String createRequestUrl(final String cellId, final String lac, final String mcc, final String mnc) {
    return new StringBuffer(BASEURL).append("cellid=").append(cellId).append("&mcc=").append(mcc).append("&mnc=").append(mnc).append("&lac=").append(lac).append("&key=").append(ericssonKey).toString();
  }

  public void parseResponse(final CellIdResponseWaiter responseWaiter, final String response) {
    parseJSON(responseWaiter,response);
  }

  public void parseJSON(final CellIdResponseWaiter responseWaiter, String jsonString) {
    try {
      JSONObject o = new JSONObject(jsonString);
      JSONObject pos = o.getJSONObject("position");
      String longitude = pos.getString("longitude");
      String latitude = pos.getString("latitude");
      String accuracy = pos.getString("accuracy");
      String cellName = pos.optString("name");
      Log.debug("Cellid parsed: lon=" + longitude + " lat=" + latitude + " acc=" + accuracy + " name=" + cellName);
      final WgsPoint location = Utils.parseWgsFromString(longitude, latitude);
      if (location == null) {
        responseWaiter.notifyError();
      }
      responseWaiter.locationRetrieved(location);

    } catch (Exception e) {
      responseWaiter.notifyError();
      Log.error("Can't read location" + e.getMessage());
      Log.printStackTrace(e);
    }
  }
}
