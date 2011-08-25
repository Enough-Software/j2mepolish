package com.nutiteq.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.mgmaps.utils.Tools;
import com.nutiteq.cache.Cache;
import com.nutiteq.components.Distance;
import com.nutiteq.components.DurationTime;
import com.nutiteq.components.Line;
import com.nutiteq.components.Route;
import com.nutiteq.components.RouteInstruction;
import com.nutiteq.components.RouteSummary;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.core.MappingCore;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.kml.KmlReader;
import com.nutiteq.kml.KmlStylesCache;

public class YourNavigationDirections extends KmlReader implements DirectionsService,
    ResourceRequestor, ResourceDataWaiter {

  private static final String DISTANCE_TAG = "distance";

  public static final String MOVE_METHOD_CAR = "motorcar";
  public static final String MOVE_METHOD_BICYCLE = "bicycle";
  public static final String MOVE_METHOD_FOOT = "foot";
  public static final int ROUTE_TYPE_FASTEST = 1;
  public static final int ROUTE_TYPE_SHORTEST = 0;
  private final DirectionsWaiter directionsWaiter;
  private static final String BASEURL = "http://www.yournavigation.org/api/1.0/gosmore.php?";
  private final String routingUrl;
  private static final int ERROR_MESSAGE_LENGHT = 27;
  private static final String ERROR_MESSAGE = "Unable to calculate a route";

  public YourNavigationDirections(final DirectionsWaiter directionsWaiter, final WgsPoint from,
      final WgsPoint to, final String moveType, final int routeType) {
    super(null, null, null, new KmlStylesCache(), null);
    //TODO jaanus : what input to check?
    this.directionsWaiter = directionsWaiter;
    final StringBuffer urlBuffer = new StringBuffer(BASEURL);
    urlBuffer.append("flat=").append(from.getLat());
    urlBuffer.append("&flon=").append(from.getLon());
    urlBuffer.append("&tlat=").append(to.getLat());
    urlBuffer.append("&tlon=").append(to.getLon());
    urlBuffer.append("&v=").append(moveType);
    urlBuffer.append("&fast=").append(routeType);
    urlBuffer.append("&layer=mapnik");
    routingUrl = urlBuffer.toString();
  }

  public void execute() {
    MappingCore.getInstance().getTasksRunner().enqueueDownload(this, Cache.CACHE_LEVEL_NONE);
  }

  public String resourcePath() {
    return routingUrl;
  }

  public void notifyError() {
    directionsWaiter.networkError();
  }

  public void dataRetrieved(final byte[] data) {
    if (data.length == ERROR_MESSAGE_LENGHT
        && Tools.byteArrayToString(data).indexOf(ERROR_MESSAGE) >= 0) {
      directionsWaiter.routingErrors(ERROR_ROUTE_NOT_FOUND);
    } else {
      readRoute(data);
    }
  }

  private void readRoute(final byte[] data) {
    final ByteArrayInputStream is = new ByteArrayInputStream(data);
    InputStreamReader reader;
    try {
      reader = new InputStreamReader(is, "utf-8");
    } catch (final Exception e) {
      reader = new InputStreamReader(is);
    }

    float distance = 0;
    Line line = null;
    boolean success = false;
    final KXmlParser parser = new KXmlParser();
    try {
      parser.setInput(reader);
      int eventType = parser.getEventType();
      while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG) {
          final String tagName = parser.getName();
          if (DISTANCE_TAG.equals(tagName)) {
            distance = Float.parseFloat(parser.nextText());
          } else if (LINE_STRING_TAG.equals(tagName)) {
            line = readLine(parser);
          }
        }
        eventType = parser.next();
      }
      success = true;
    } catch (final XmlPullParserException e) {
      directionsWaiter.routingParsingError("XmlPullParserException: " + e.getMessage());
    } catch (final IOException e) {
      directionsWaiter.routingParsingError("IOException: " + e.getMessage());
    }

    if (!success) {
      return;
    }

    //TODO jaanus : is this ok?
    if (line == null) {
      directionsWaiter.routingErrors(ERROR_ROUTE_NOT_FOUND);
      return;
    }

    final Distance routeDistance = new Distance(distance, "km");
    //TODO jaanus : try to create bounding box?
    final RouteSummary summary = new RouteSummary(new DurationTime(), routeDistance, null);
    final Route route = new Route(summary, line, new RouteInstruction[0]);
    directionsWaiter.routeFound(route);
  }

  public void cancel() {
    //TODO jaanus
  }
}
