package com.nutiteq.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.nutiteq.cache.Cache;
import com.nutiteq.components.Distance;
import com.nutiteq.components.DurationTime;
import com.nutiteq.components.Line;
import com.nutiteq.components.Route;
import com.nutiteq.components.RouteInstruction;
import com.nutiteq.components.RouteSummary;
import com.nutiteq.components.WgsBoundingBox;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.core.MappingCore;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.log.Log;
import com.nutiteq.utils.IOUtils;
import com.nutiteq.utils.Utils;

/**
 * Routing service using CloudMade routing, version 0.3.
 */
public class CloudMadeDirections implements DirectionsService, ResourceRequestor,
    ResourceDataWaiter {
  private static final String ATTRIBUTE_LONGITUDE = "lon";
  private static final String ATTRIBUTE_LATITUDE = "lat";
  private static final String ROUTE_POINT_TAG = "rtept";
  private static final String WAYPOINT_TAG = "wpt";
  private static final String DESCRIPTION_TAG = "desc";
  private static final String TURN_TAG = "turn";
  
  private static final String ROUTE_EXTENSION_TAG = "extensions";
  private static final String TIME_TAG = "time";
  private static final String DISTANCE_TAG = "distance";
  private static final String OFFSET_TAG = "offset";


  public static final String ROUTE_TYPE_CAR = "car";
  public static final String ROUTE_TYPE_FOOT = "foot";
  public static final String ROUTE_TYPE_BICYCLE = "bicycle";

  public static final String ROUTE_TYPE_MODIFIER_SHORTEST = "shortest";

  private static final String BASEURL = "http://routes.cloudmade.com/";

  private static final String RESPONSE_TYPE = "gpx";
  private static final String API_VERSION = "0.3";

  private final DirectionsWaiter directionsWaiter;
  private final WgsPoint start;
  private final WgsPoint end;
  private final String routeType;
  private final String routeTypeModifier;
  private final String apiKey;
  private boolean canceled;
  private String cloudMadeToken = null;

  
  public CloudMadeDirections(final DirectionsWaiter directionsWaiter, final WgsPoint start,
      final WgsPoint end, final String routeType, final String apiKey, final String userId) {
    this(directionsWaiter, start, end, routeType, "", apiKey, userId);
  }

  public CloudMadeDirections(final DirectionsWaiter directionsWaiter, final WgsPoint start,
      final WgsPoint end, final String routeType, final String routeTypeModifier,
      final String apiKey,final String userId){
    this(CloudMadeToken.getCloudMadeToken(apiKey,userId), directionsWaiter, start, end, routeType, routeTypeModifier,apiKey);
  }

  
  /**
   * @param token CloudMade token
   * @param directionsWaiter listener for directions result (callback)
   * @param start start point
   * @param end end point of route
   * @param routeType route type: ROUTE_TYPE_CAR, ROUTE_TYPE_FOOT or ROUTE_TYPE_BICYCLE
   * @param routeTypeModifier ROUTE_TYPE_MODIFIER_SHORTEST (default is FASTEST)
   * @param apiKey your CloudMade HTTP API key, get it from www.cloudmade.com
   */
  public CloudMadeDirections(final String token, final DirectionsWaiter directionsWaiter, final WgsPoint start,
      final WgsPoint end, final String routeType, final String routeTypeModifier, final String apiKey
      ) {
    this.directionsWaiter = directionsWaiter;
    this.start = start;
    this.end = end;
    this.routeType = routeType;
    this.routeTypeModifier = routeTypeModifier;
    this.cloudMadeToken=token;
    this.apiKey=apiKey;
    
  }

  public void execute() {
    MappingCore.getInstance().getTasksRunner().enqueueDownload(this, Cache.CACHE_LEVEL_NONE);
  }

  public String resourcePath() {
    final StringBuffer url = new StringBuffer(BASEURL);
    url.append(apiKey).append("/api/").append(API_VERSION).append("/");
    url.append(start.getLat()).append(",").append(start.getLon());
    url.append(",").append(end.getLat()).append(",").append(end.getLon());
    url.append("/").append(routeType);
    if (routeTypeModifier != null && !"".equals(routeTypeModifier)) {
      url.append("/").append(routeTypeModifier);
    }
    url.append(".").append(RESPONSE_TYPE);
    url.append("?token=").append(cloudMadeToken);

    return url.toString();
  }

  public void notifyError() {
    Log.error("CloudMade Directions network error");
    directionsWaiter.networkError();
  }

  public void dataRetrieved(final byte[] data) {
      Log.debug("CloudMade Directions data retrieved, bytes:"+data.length);

      if (canceled) {
      return;
    }

    final Reader reader = Utils.createInputStreamReader(data);
    final Route route = readRoute(reader);
    if (route.getRouteLine().getPoints().length < 2) {
      directionsWaiter.routingParsingError(new String(data));
    } else {
      directionsWaiter.routeFound(route);
    }
    IOUtils.closeReader(reader);
  }

  protected Route readRoute(final Reader reader) {
    final Vector wayPoints = new Vector();
    final Vector instructionPoints = new Vector();
    RouteSummary summary = null;
    
    final KXmlParser parser = new KXmlParser();
    try {
      parser.setInput(reader);
      int eventType = parser.getEventType();
      while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG) {
          final String tagName = parser.getName();
          if (ROUTE_EXTENSION_TAG.equals(tagName) && summary == null) {
              summary = readRouteSummary(parser);
          }else if (WAYPOINT_TAG.equals(tagName)) {
            final String lat = parser.getAttributeValue(null, ATTRIBUTE_LATITUDE);
            final String lon = parser.getAttributeValue(null, ATTRIBUTE_LONGITUDE);
            wayPoints.addElement(Utils.parseWgsFromString(lon, lat));
          }else if (ROUTE_POINT_TAG.equals(tagName)) {
            instructionPoints.addElement(readInstruction(parser, instructionPoints.size()));
          }
        }
        eventType = parser.next();
      }
    } catch (final Exception e) {
      Log.error("Route: read " + e.getMessage());
      Log.printStackTrace(e);
    }

    final WgsPoint[] points = new WgsPoint[wayPoints.size()];
    wayPoints.copyInto(points);
    final RouteInstruction[] instructions = new RouteInstruction[instructionPoints.size()];
    instructionPoints.copyInto(instructions);

    return new Route(summary, new Line(points), instructions);
  }

  private RouteSummary readRouteSummary(final KXmlParser parser) throws IOException {
      DurationTime totalTime = null;
      Distance distance = null;
      WgsBoundingBox boundingBox = null;
      try {
        int eventType = parser.next();
        while (!ROUTE_EXTENSION_TAG.equals(parser.getName())) {
          if (XmlPullParser.START_TAG == eventType) {
            final String tagName = parser.getName();
            if (TIME_TAG.equals(tagName)) {
              totalTime = parseDuration(parser.nextText());
            } else if (DISTANCE_TAG.equals(tagName)) {
              distance = readDistance(parser);
            } 
          }
          eventType = parser.next();
        }
      } catch (final XmlPullParserException e) {
        Log.printStackTrace(e);
      }
      return new RouteSummary(totalTime, distance, boundingBox);
    }
  
  private Distance readDistance(final KXmlParser parser) throws IOException {
    String distanceString;
    try {
      distanceString = parser.nextText();

      try {
        return new Distance(Float.parseFloat(distanceString), "m");
      } catch (final NumberFormatException e) {
        Log.error("NumberFormatException in readDistance");
        Log.printStackTrace(e);
        return new Distance(0, "");
      }

    } catch (XmlPullParserException e1) {
      Log.error("XML parsing exception in readDistance");
      e1.printStackTrace();
      return new Distance(0, "");
    }
  }
  
  protected DurationTime parseDuration(final String time) {
      if (time == null || "".equals(time.trim())) {
        return new DurationTime(0, 0, 0, 0);
      }
      
      try {
        int timeInt = Integer.parseInt(time);
        final int daysInt = timeInt / 86400;
        final int hoursInt = (int)(timeInt/3600) - (daysInt*24);
        final int minutesInt = (int)(timeInt/60) - (hoursInt*60) - (daysInt*60*24);
        final int secondsInt = timeInt - (minutesInt*60)- (hoursInt*60*60) - (daysInt*60*60*24);
        return new DurationTime(daysInt, hoursInt, minutesInt, secondsInt);
      } catch (final NumberFormatException e) {
        Log.error("Error parsing duration from " + time);
        Log.printStackTrace(e);
        return null;
      }
    }

  
  private RouteInstruction readInstruction(final KXmlParser parser, final int count)
      throws Exception {
    final String lat = parser.getAttributeValue(null, ATTRIBUTE_LATITUDE);
    final String lon = parser.getAttributeValue(null, ATTRIBUTE_LONGITUDE);
    final WgsPoint location = Utils.parseWgsFromString(lon, lat);

    String description = null;
    int eventType = parser.next();
    if (DESCRIPTION_TAG.equals(parser.getName())) {
      description = parser.nextText();
    }
    eventType = parser.next();
    eventType = parser.next();
    
    DurationTime time = null;
    Distance distance = null;
    int turn = IMAGE_ROUTE_START;
    while (!ROUTE_EXTENSION_TAG.equals(parser.getName())) {
        if (XmlPullParser.START_TAG == eventType) {
                final String tagName = parser.getName();
                if (TIME_TAG.equals(tagName)) {
                    time = parseDuration(parser.nextText());
                } else if (DISTANCE_TAG.equals(tagName)) {
                    distance = readDistance(parser);
                } else if (TURN_TAG.equals(tagName)) {
                    turn = parseTurn(parser.nextText());
                }else if (OFFSET_TAG.equals(tagName)) {
                    String offset = parser.nextText();
                    if(offset.equals("0")){
                        turn=DirectionsService.IMAGE_ROUTE_START;
                    }
                }
            }
        eventType = parser.next();
      }
    
    return new RouteInstruction(count, turn, time, description, distance, location);
  }

  private int parseTurn(String turn) {
        if (turn.equals("TSLR") || turn.equals("TR")) {
            return DirectionsService.IMAGE_ROUTE_RIGHT;
        }
        if (turn.equals("TSLL") || turn.equals("TL")) {
            return DirectionsService.IMAGE_ROUTE_LEFT;
        }

        // all other cases (C, EXITn ... are taken as Stright/Continue
        return DirectionsService.IMAGE_ROUTE_STRAIGHT;
    }

    public void cancel() {
        canceled = true;
    }

  public int getCachingLevel() {
    return Cache.CACHE_LEVEL_NONE;
  }
  
}
