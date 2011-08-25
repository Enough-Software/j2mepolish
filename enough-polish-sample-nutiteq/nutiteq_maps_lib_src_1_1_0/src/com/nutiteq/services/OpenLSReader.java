package com.nutiteq.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.java4ever.apime.io.GZIP;
import com.nutiteq.components.Distance;
import com.nutiteq.components.DurationTime;
import com.nutiteq.components.Line;
import com.nutiteq.components.Route;
import com.nutiteq.components.RouteInstruction;
import com.nutiteq.components.RouteSummary;
import com.nutiteq.components.WgsBoundingBox;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.log.Log;
import com.nutiteq.utils.Utils;

public class OpenLSReader {
  private static final String ROUTE_SUMMARY_TAG = "xls:RouteSummary";
  private static final String TOTAL_TIME_TAG = "xls:TotalTime";
  private static final String TOTAL_DISTANCE_TAG = "xls:TotalDistance";
  private static final String BOUNDING_BOX_TAG = "xls:BoundingBox";
  private static final String POSITION_TAG = "gml:pos";
  private static final String LINE_STRING_TAG = "gml:LineString";
  private static final String ROUTE_INSTRUCTIONS_LIST_TAG = "xls:RouteInstructionsList";
  private static final String ROUTE_INSTRUCTION_TAG = "xls:RouteInstruction";
  private static final String INSTRUCTION_TAG = "xls:Instruction";
  private static final String DISTANCE_TAG = "xls:distance";
  private static final String POINT_TAG = "xls:Point";
  private static final String ERROR_TAG = "xls:Error";
  private static final String ERROR_LIST_TAG = "xls:ErrorList";

  private final byte[] data;
  private final DirectionsWaiter waiter;

  //TODO jaanus : directions waiter should only be in one place. all here or remove from here
  public OpenLSReader(final byte[] data, final DirectionsWaiter waiter) {
    this.data = data;
    this.waiter = waiter;
  }

  public Route read() throws IOException {
    final ByteArrayInputStream bais = new ByteArrayInputStream(GZIP.inflate(data));
    InputStreamReader reader;
    try {
      reader = new InputStreamReader(bais, "utf-8");
    } catch (final Exception e) {
      reader = new InputStreamReader(bais);
    }
    return read(reader);
  }

  protected Route read(final InputStreamReader isr) throws IOException {
    final KXmlParser parser = new KXmlParser();
    RouteSummary summary = null;
    Line routeLine = null;
    RouteInstruction[] instructions = null;
    try {
      parser.setInput(isr);
      int eventType = parser.getEventType();
      while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG) {
          final String tagName = parser.getName();
          if (ROUTE_SUMMARY_TAG.equals(tagName)) {
            summary = readRouteSummary(parser);
          } else if (LINE_STRING_TAG.equals(tagName)) {
            routeLine = readRouteLine(parser);
          } else if (ROUTE_INSTRUCTIONS_LIST_TAG.equals(tagName)) {
            instructions = readRouteInstrictions(parser);
          } else if (ERROR_LIST_TAG.equals(tagName)) {
            final int errorCodes = readErrorCodes(parser);
            waiter.routingErrors(errorCodes);
            return null;
          }
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    }

    return new Route(summary, routeLine, instructions);
  }

  private int readErrorCodes(final KXmlParser parser) throws IOException {
    final Vector errors = new Vector();
    try {
      int eventType = parser.next();
      while (!ERROR_LIST_TAG.equals(parser.getName())) {
        if (eventType == XmlPullParser.START_TAG && ERROR_TAG.equals(parser.getName())) {
          errors.addElement(parser.getAttributeValue(null, "errorCode"));
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    }

    if (errors.size() == 0) {
      return 0;
    }

    int result = Integer.parseInt((String) errors.elementAt(0));

    for (int i = 1; i < errors.size(); i++) {
      result = result | Integer.parseInt((String) errors.elementAt(i));
    }
    return result;
  }

  private RouteInstruction[] readRouteInstrictions(final KXmlParser parser) throws IOException {
    final Vector instructions = new Vector();
    try {
      int eventType = parser.next();
      while (!ROUTE_INSTRUCTIONS_LIST_TAG.equals(parser.getName())) {
        if (eventType == XmlPullParser.START_TAG && ROUTE_INSTRUCTION_TAG.equals(parser.getName())) {
          instructions.addElement(readInstructionPoint(parser));
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    }

    final RouteInstruction[] result = new RouteInstruction[instructions.size()];
    instructions.copyInto(result);

    return result;
  }

  private RouteInstruction readInstructionPoint(final KXmlParser parser) throws IOException {
    final String pointDescription = parser.getAttributeValue(null, "description");
    final DurationTime duration = parseDuration(parser.getAttributeValue(null, "duration"));
    final int pointNumber = Integer.parseInt(parser.getAttributeValue(null, "tour"));
    final int icon;
    String instruction = null;
    Distance distance = null;
    WgsPoint point = null;

    if ("".equals(pointDescription)) {
      icon = pointNumber == 1 ? DirectionsService.IMAGE_ROUTE_START
          : DirectionsService.IMAGE_ROUTE_END;
    } else {
      icon = Integer.parseInt(pointDescription);
    }

    try {
      int eventType = parser.next();
      while (!ROUTE_INSTRUCTION_TAG.equals(parser.getName())) {
        if (eventType == XmlPullParser.START_TAG) {
          final String tagName = parser.getName();
          if (INSTRUCTION_TAG.equals(tagName)) {
            instruction = parser.nextText();
          } else if (DISTANCE_TAG.equals(tagName)) {
            distance = readDistance(parser);
          } else if (POINT_TAG.equals(tagName)) {
            point = parseCoordinates(parser.nextText());
          }
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    }

    return new RouteInstruction(pointNumber, icon, duration, instruction, distance, point);
  }

  private Line readRouteLine(final KXmlParser parser) throws IOException {
    final Vector points = new Vector();
    try {
      int eventType = parser.next();
      while (!LINE_STRING_TAG.equals(parser.getName())) {
        if (eventType == XmlPullParser.START_TAG && POSITION_TAG.equals(parser.getName())) {
          points.addElement(parseCoordinates(parser.nextText()));
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    }

    final WgsPoint[] routePoints = new WgsPoint[points.size()];
    points.copyInto(routePoints);

    return new Line(routePoints);
  }

  private RouteSummary readRouteSummary(final KXmlParser parser) throws IOException {
    DurationTime totalTime = null;
    Distance distance = null;
    WgsBoundingBox boundingBox = null;
    try {
      int eventType = parser.next();
      while (!ROUTE_SUMMARY_TAG.equals(parser.getName())) {
        if (XmlPullParser.START_TAG == eventType) {
          final String tagName = parser.getName();
          if (TOTAL_TIME_TAG.equals(tagName)) {
            totalTime = parseDuration(parser.nextText());
          } else if (TOTAL_DISTANCE_TAG.equals(tagName)) {
            distance = readDistance(parser);
          } else if (BOUNDING_BOX_TAG.equals(tagName)) {
            boundingBox = readBoundingBox(parser);
          }
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    }
    return new RouteSummary(totalTime, distance, boundingBox);
  }

  private Distance readDistance(final KXmlParser parser) {
    final String distanceString = parser.getAttributeValue(null, "value");
    final String uom = parser.getAttributeValue(null, "uom");
    try {
      return new Distance(Float.parseFloat(distanceString), uom);
    } catch (final NumberFormatException e) {
      Log.printStackTrace(e);
      return new Distance(0, "");
    }
  }

  private WgsBoundingBox readBoundingBox(final KXmlParser parser) throws IOException {
    final Vector points = new Vector();
    try {
      int eventType = parser.next();
      while (!BOUNDING_BOX_TAG.equals(parser.getName())) {
        if (XmlPullParser.START_TAG == eventType) {
          final String tagName = parser.getName();
          if (POSITION_TAG.equals(tagName)) {
            points.addElement(parseCoordinates(parser.nextText()));
          }
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    }
    if (points.size() != 2) {
      return null;
    }

    final WgsPoint[] coordinates = new WgsPoint[2];
    points.copyInto(coordinates);
    final WgsPoint min = new WgsPoint(Math.min(coordinates[0].getLon(), coordinates[1].getLon()),
        Math.min(coordinates[0].getLat(), coordinates[1].getLat()));
    final WgsPoint max = new WgsPoint(Math.max(coordinates[0].getLon(), coordinates[1].getLon()),
        Math.max(coordinates[0].getLat(), coordinates[1].getLat()));
    return new WgsBoundingBox(min, max);
  }

  private WgsPoint parseCoordinates(final String coordinates) {
    final String[] split = Utils.split(coordinates, " ");
    if (split.length != 2) {
      return null;
    }

    try {
      return new WgsPoint(Double.parseDouble(split[1].trim()), Double.parseDouble(split[0].trim()));
    } catch (final NumberFormatException e) {
      Log.printStackTrace(e);
      return null;
    }
  }

  protected DurationTime parseDuration(final String time) {
    if (time == null || "".equals(time.trim())) {
      return new DurationTime(0, 0, 0, 0);
    }

    try {
      final int daysInt = time.indexOf("D") > 0 ? Integer.parseInt(time.substring(1, time
          .indexOf("D"))) : 0;
      final int hoursInt = Integer.parseInt(time
          .substring(time.indexOf("T") + 1, time.indexOf("H")));
      final int minutesInt = Integer.parseInt(time.substring(time.indexOf("H") + 1, time
          .indexOf("M")));
      final int secondsInt = Integer.parseInt(time.substring(time.indexOf("M") + 1, time
          .indexOf("S")));
      return new DurationTime(daysInt, hoursInt, minutesInt, secondsInt);
    } catch (final NumberFormatException e) {
      Log.error("Error parsing duration from " + time);
      Log.printStackTrace(e);
      return null;
    }
  }
}
