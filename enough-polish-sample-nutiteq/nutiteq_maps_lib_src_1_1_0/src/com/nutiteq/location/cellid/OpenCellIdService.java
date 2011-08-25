package com.nutiteq.location.cellid;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import com.nutiteq.components.WgsPoint;
import com.nutiteq.log.Log;
import com.nutiteq.utils.IOUtils;
import com.nutiteq.utils.Utils;

/**
 * CellId servic on top of <a href="http://www.opencellid.org/">OpenCellId</a>
 */
public class OpenCellIdService extends OnlineCellIdService {
  private static final String BASEURL = "http://www.opencellid.org/cell/get?";
  private static final String RESPONSE_TAG = "rsp";
  private static final String CELL_TAG = "cell";
  private static final String RESPONSE_STATUS_OK = "ok";
  private String requestedCellId;

  public String createRequestUrl(final String cellId, final String lac, final String mcc,
      final String mnc) {
    this.requestedCellId = Integer.toString(Integer.parseInt(cellId, 16));
    return new StringBuffer(BASEURL).append("cellid=").append(requestedCellId).append("&mcc=")
        .append(mcc).append("&mnc=").append(mnc).append("&lac=").append(Integer.parseInt(lac, 16))
        .toString();
  }

  public void parseResponse(final CellIdResponseWaiter responseWaiter, final String response) {
    final ByteArrayInputStream is = new ByteArrayInputStream(response.getBytes());
    InputStreamReader reader;
    try {
      reader = new InputStreamReader(is, "utf-8");
    } catch (final Exception e) {
      reader = new InputStreamReader(is);
    }

    parseResponse(responseWaiter, reader);
    IOUtils.closeReader(reader);
    IOUtils.closeStream(is);
  }

  protected void parseResponse(final CellIdResponseWaiter responseWaiter,
      final InputStreamReader reader) {
    final KXmlParser parser = new KXmlParser();
    try {
      parser.setInput(reader);
      int eventType = parser.getEventType();
      while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG) {
          final String tagName = parser.getName();
          if (RESPONSE_TAG.equals(tagName)) {
            final String responseStatus = parser.getAttributeValue(null, "stat");
            if (!RESPONSE_STATUS_OK.equals(responseStatus)) {
              responseWaiter.notifyError();
              break;
            }
          } else if (CELL_TAG.equals(tagName)) {
            final String samples = parser.getAttributeValue(null, "nbSamples");
            final String lat = parser.getAttributeValue(null, "lat");
            final String lon = parser.getAttributeValue(null, "lon");
            final String range = parser.getAttributeValue(null, "range");
            final String readCellId = parser.getAttributeValue(null, "cellId");

            if ("0".equals(samples) || !requestedCellId.equals(readCellId)) {
              responseWaiter.cantLocate();
              break;
            }

            final WgsPoint location = Utils.parseWgsFromString(lon, lat);
            if (location == null) {
              responseWaiter.notifyError();
            }

            responseWaiter.locationRetrieved(location);
          }
        }

        eventType = parser.next();
      }
    } catch (final Exception e) {
      responseWaiter.notifyError();
      Log.error("Can't read location" + e.getMessage());
      Log.printStackTrace(e);
    }
  }
}
