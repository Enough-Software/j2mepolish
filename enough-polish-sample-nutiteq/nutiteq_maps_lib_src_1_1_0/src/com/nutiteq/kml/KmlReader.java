package com.nutiteq.kml;

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;

import javax.microedition.lcdui.Image;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.java4ever.apime.io.GZIP;
import com.nutiteq.cache.Cache;
import com.nutiteq.cache.ImageWaiter;
import com.nutiteq.components.ExtendedDataMap;
import com.nutiteq.components.KmlPlace;
import com.nutiteq.components.Line;
import com.nutiteq.components.LineStyle;
import com.nutiteq.components.OnMapElement;
import com.nutiteq.components.Place;
import com.nutiteq.components.PlaceIcon;
import com.nutiteq.components.PolyStyle;
import com.nutiteq.components.Polygon;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.log.Log;
import com.nutiteq.net.ImageWaitingDownloadable;
import com.nutiteq.task.TasksRunner;
import com.nutiteq.utils.IOUtils;
import com.nutiteq.utils.Utils;

public class KmlReader implements ResourceRequestor, ResourceDataWaiter {
  private static final String PLACEMARK_TAG = "Placemark";
  private static final String PLACEMARK_NAME_TAG = "name";
  private static final String PLACEMARK_POINT_TAG = "Point";
  private static final String PLACEMARK_POINT_COORDINATES_TAG = "coordinates";
  private static final String STYLE_TAG = "Style";
  private static final String STYLE_MAP_TAG = "StyleMap";
  private static final String STYLE_ICON_SCALE_TAG = "scale";
  private static final String STYLE_ICON_HREF_TAG = "href";
  private static final String PLACEMARK_DESCRIPTION_TAG = "description";
  private static final String PLACEMARK_ADDRESS_TAG = "address";
  private static final String PLACEMARK_SNIPPET_TAG = "Snippet";
  private static final String PLACEMARK_XDATA_TAG = "ExtendedData";
  private static final String PLACEMARK_STYLE_URL_TAG = "styleUrl";
  private static final String STYLE_MAP_PAIR_TAG = "Pair";
  private static final String KEY_TAG = "key";
  private static final String POLYGON_TAG = "Polygon";
  public static final String LINE_STRING_TAG = "LineString";
  private static final String LINE_STYLE_TAG = "LineStyle";
  private static final String POLY_STYLE_TAG = "PolyStyle";
  private static final String COLOR_TAG = "color";
  private static final String WIDTH_TAG = "width";
  private static final String DATA_TAG = "Data";
  private static final String VALUE_TAG = "value";

  private static Image defaultKmlIcon = Image.createImage(18, 18);

  private final KmlService service;
  private final KmlElementsWaiter servicesHandler;
  private final KmlStylesCache stylesCache;
  private final String serviceUrl;
  private final TasksRunner tasksRunner;
  private final boolean responsePacked;
  private int placeCount;
  private static final String DEFAULT_KML_ICON = "/images/def_kml.png";

  public KmlReader(final KmlElementsWaiter servicesHandler, final KmlService service,
      final String serviceUrl, final KmlStylesCache stylesCache, final TasksRunner tasksRunner) {
    this(servicesHandler, service, serviceUrl, stylesCache, tasksRunner, false);
  }

  public KmlReader(final KmlElementsWaiter servicesHandler, final KmlService service,
          final String serviceUrl, final KmlStylesCache stylesCache, final TasksRunner tasksRunner, final String defaultIcon) {
        this(servicesHandler, service, serviceUrl, stylesCache, tasksRunner, false, defaultIcon);
      }

  public KmlReader(final KmlElementsWaiter servicesHandler, final KmlService service,
          final String serviceUrl, final KmlStylesCache stylesCache, final TasksRunner tasksRunner,
          final boolean responsePacked){
      this(servicesHandler, service, serviceUrl, stylesCache, tasksRunner, false, DEFAULT_KML_ICON);
  }
  
  public KmlReader(final KmlElementsWaiter servicesHandler, final KmlService service,
      final String serviceUrl, final KmlStylesCache stylesCache, final TasksRunner tasksRunner,
      final boolean responsePacked, final String defaultIcon) {
    this.servicesHandler = servicesHandler;
    this.service = service;
    this.serviceUrl = serviceUrl;
    this.stylesCache = stylesCache;
    this.tasksRunner = tasksRunner;
    this.responsePacked = responsePacked;

      try {
        defaultKmlIcon = Image.createImage(defaultIcon);
      } catch (final IOException e) {
        defaultKmlIcon = Image.createImage(18, 18);
      }

  }

  
  
  public String resourcePath() {
    return serviceUrl;
  }

  public void dataRetrieved(final byte[] data) {
    boolean guessPacked = false;
    if(data[0]==31 && data[1]==-117){
        guessPacked=true;
        Log.debug("Guess response packed based on first 2 data bytes"); 
    }else{
        Log.debug("Response NOT packed based on first 2 data bytes");
    }
        
    final byte[] finalData = (responsePacked || guessPacked) ? GZIP.inflate(data) : data;
    // remove BOM (ie the first 3 bytes)
    if(finalData[0]==0xef && finalData[1]==0xbb && finalData[2]==0xbf){
        System.arraycopy(finalData, 3, finalData, 0, finalData.length-3);
    }
        
    final Reader reader = Utils.createInputStreamReader(finalData);
//    final ByteArrayInputStream bais = new ByteArrayInputStream(finalData);
    try {
      servicesHandler.addKmlPlaces(service, read(reader, service.maxResults()));
    } catch (final IOException e) {
      Log.printStackTrace(e);
      notifyError();
    } finally {
      IOUtils.closeReader(reader);
    }
    Log.debug("KmlReader done, read places: "+placeCount);
    
  }

  public void notifyError() {
    //TODO jaanus : ignore?
  }

  protected KmlPlace[] read(final Reader reader, final int maxResults) throws IOException {
    final Vector places = new Vector();
    final KXmlParser parser = new KXmlParser();
    try {
      parser.setInput(reader);
      int eventType = parser.getEventType();
      while (eventType != XmlPullParser.END_DOCUMENT && places.size() < maxResults) {
        if (eventType == XmlPullParser.START_TAG) {
          final String tagName = parser.getName();
          if (PLACEMARK_TAG.equals(tagName)) {
            final KmlPlace place = readPlace(parser);
            if (place != null) {
              places.addElement(place);
            }
          } else if (STYLE_TAG.equals(tagName)) {
            stylesCache.addStyle(readStyle(parser));
          } else if (STYLE_MAP_TAG.equals(tagName)) {
            stylesCache.addStyleMap(readStyleMap(parser));
          }
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.error("KmlReader: read " + e.getMessage());
      Log.printStackTrace(e);
    }

    final KmlPlace[] result = new KmlPlace[places.size()];
    places.copyInto(result);
    placeCount = places.size();
    return result;
  }

  private KmlStyleMap readStyleMap(final KXmlParser parser) throws IOException {
    KmlStyleMap map = null;
    try {
      map = new KmlStyleMap(parser.getAttributeValue(null, "id"));
      int eventType = parser.next();
      while (!STYLE_MAP_TAG.equals(parser.getName())) {
        if (XmlPullParser.START_TAG == eventType) {
          if (STYLE_MAP_PAIR_TAG.equals(parser.getName())) {
            addStylePair(map, parser);
          }
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    }
    return map;
  }

  private void addStylePair(final KmlStyleMap map, final KXmlParser parser) throws IOException {
    String key = null;
    String styleUrl = null;
    try {
      int eventType = parser.next();
      while (!STYLE_MAP_PAIR_TAG.equals(parser.getName())) {
        if (XmlPullParser.START_TAG == eventType) {
          final String tagName = parser.getName();
          if (KEY_TAG.equals(tagName)) {
            key = parser.nextText();
          } else if (PLACEMARK_STYLE_URL_TAG.equals(tagName)) {
            styleUrl = parser.nextText().trim();
          }
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    }

    map.addPair(key, styleUrl);
  }

  private KmlStyle readStyle(final KXmlParser parser) throws IOException {
    String id = null;
    float scale = 0.0f;
    String href = null;
    LineStyle lineStyle = null;
    PolyStyle polyStyle = null;
    try {
      id = parser.getAttributeValue(null, "id");
      int eventType = parser.next();
      while (!STYLE_TAG.equals(parser.getName())) {
        if (XmlPullParser.START_TAG == eventType) {
          final String tagName = parser.getName();
          if (STYLE_ICON_SCALE_TAG.equals(tagName)) {
            scale = Float.parseFloat(parser.nextText());
          } else if (STYLE_ICON_HREF_TAG.equals(tagName)) {
            href = parser.nextText().trim();
          } else if (LINE_STYLE_TAG.equals(tagName)) {
            lineStyle = readLineStyle(parser);
          } else if (POLY_STYLE_TAG.equals(tagName)) {
            polyStyle = readPolyStyle(parser);
          }
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    }

    final KmlStyle result = new KmlStyle(id, scale, href);
    result.setLineStyle(lineStyle);
    result.setPolyStyle(polyStyle);

    return result;
  }

  private KmlPlace readPlace(final KXmlParser parser) throws IOException {
    String name = null;
    WgsPoint point = null;
    String description = null;
    String styleUrl = null;
    String address = null;
    String snippet = null;
    ExtendedDataMap extendedDataMap = new ExtendedDataMap();
    
    final Vector kmlElements = new Vector();
    try {
      int eventType = parser.next();
      while (!PLACEMARK_TAG.equals(parser.getName())) {
        if (eventType == XmlPullParser.START_TAG) {
          final String tagName = parser.getName();
          if (PLACEMARK_NAME_TAG.equals(tagName)) {
            name = parser.nextText();
          } else if (PLACEMARK_POINT_TAG.equals(tagName)) {
            point = readPoint(parser);
          } else if (PLACEMARK_DESCRIPTION_TAG.equals(tagName)) {
            description = parser.nextText();
          } else if (PLACEMARK_STYLE_URL_TAG.equals(tagName)) {
            styleUrl = parser.nextText().trim();
          } else if (PLACEMARK_ADDRESS_TAG.equals(tagName)) {
            address = parser.nextText();
          } else if (PLACEMARK_SNIPPET_TAG.equals(tagName)) {
            snippet = parser.nextText();
          } else if (PLACEMARK_XDATA_TAG.equals(tagName)) {
             extendedDataMap = readXData(parser); 
          } else if (POLYGON_TAG.equals(tagName)) {
            kmlElements.addElement(readPolygon(parser));
          } else if (LINE_STRING_TAG.equals(tagName)) {
            kmlElements.addElement(readLine(parser));
          }
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.error("read place: " + e.getMessage());
      Log.printStackTrace(e);
      return null;
    }

    final Place place = new Place(0, name, new PlaceIcon(defaultKmlIcon), point);
    final String placeIconUrl = stylesCache.resolveImageUrl(styleUrl);
    //TODO jaanus : check this
    if (tasksRunner != null && placeIconUrl != null && servicesHandler instanceof ImageWaiter) {
      tasksRunner.enqueueDownload(new ImageWaitingDownloadable((ImageWaiter) servicesHandler,
          placeIconUrl), Cache.CACHE_LEVEL_MEMORY | Cache.CACHE_LEVEL_PERSISTENT);
    }

    final OnMapElement[] elements = new OnMapElement[kmlElements.size()];
    kmlElements.copyInto(elements);

    //TODO jaanus : maybe can find a better way for this
    final KmlStyle style = stylesCache.getStyle(styleUrl == null ? "" : styleUrl.substring(1));
    final LineStyle lineStyle = style == null ? LineStyle.DEFAULT_STYLE
        : style.getLineStyle() == null ? LineStyle.DEFAULT_STYLE : style.getLineStyle();
    final PolyStyle polyStyle = style == null ? PolyStyle.DEFAULT_STYLE
        : style.getPolyStyle() == null ? PolyStyle.DEFAULT_STYLE : style.getPolyStyle();
    for (int i = 0; i < elements.length; i++) {
      if (elements[i] instanceof Line) {
        ((Line) elements[i]).setStyle(lineStyle);
      } else if (elements[i] instanceof Polygon) {
        ((Polygon) elements[i]).setStyle(polyStyle);
      }
    }

    place.setOnMapElements(elements);

    return new KmlPlace(place, styleUrl, description, address, snippet, extendedDataMap);
  }

  private ExtendedDataMap readXData(KXmlParser parser) {
      ExtendedDataMap out = new ExtendedDataMap(); 
      String key = null;
      String value = null;
      int eventType = -1;
      String tagName=null;
      
      try {
        search:
        while (!PLACEMARK_XDATA_TAG.equals(tagName)){ // run over all key-value pairs
            // find key
            tagName = null;
            
            while (!DATA_TAG.equals(tagName)) {
                    eventType = parser.next();
                    tagName = parser.getName();
                    if (PLACEMARK_XDATA_TAG.equals(tagName)) {
                        break search;
                    }
                }
                // key found from attribute
                key = parser.getAttributeValue(0);
                //System.out.println(key);
                tagName = null;
                // find value
                while (!DATA_TAG.equals(tagName)) {
                    if (eventType == XmlPullParser.START_TAG
                            && VALUE_TAG.equals(tagName)) {
                        value = parser.nextText();
                    }
                    eventType = parser.next();
                    tagName = parser.getName();
                }

                // value found, write to output
                
                //System.out.println(value);
                out.addPair(key, value);
                
            }
    } catch (Exception e) {
        Log.printStackTrace(e);
    }
      
    return out;
}

  /*
  private String readXDataKey(final KXmlParser parser) throws IOException {
        String result = null;

        try {
            int eventType = parser.next();
            String name = parser.getName();
            while (!DATA_TAG.equals(name)) {
                if (eventType == XmlPullParser.START_TAG
                        && VALUE_TAG.equals(name)) {

                    result = parser.getAttributeValue(0);
                }
                eventType = parser.next();
                name = parser.getName();
            }
        } catch (final XmlPullParserException e) {
            Log.printStackTrace(e);
        } catch (final ArrayIndexOutOfBoundsException e) {
            Log.printStackTrace(e);
            // result will stay null
        }
        result = parser.getAttributeValue(0);
        return result;
    }

    private String readXDataValue(final KXmlParser parser) throws IOException {
        String result = null;

        try {
            int eventType = parser.next();
            String name = parser.getName();
            while (!DATA_TAG.equals(name)) {
                if (eventType == XmlPullParser.START_TAG
                        && VALUE_TAG.equals(name)) {
                    result = parser.nextText();
                }
                eventType = parser.next();
                name = parser.getName();
            }
        } catch (final XmlPullParserException e) {
            Log.printStackTrace(e);
        } catch (final ArrayIndexOutOfBoundsException e) {
            Log.printStackTrace(e);
            // result will stay null
        }

        return result;
    }
    */
  
protected Line readLine(final KXmlParser parser) throws IOException {
    WgsPoint[] coordinates = null;
    try {
      int eventType = parser.next();
      while (!LINE_STRING_TAG.equals(parser.getName())) {
        if (eventType == XmlPullParser.START_TAG
            && PLACEMARK_POINT_COORDINATES_TAG.equals(parser.getName())) {
          coordinates = parseWgsCoordinates(parser.nextText());
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    }

    return new Line(coordinates);
  }

  private LineStyle readLineStyle(final KXmlParser parser) throws IOException {
    int color = LineStyle.DEFAULT_COLOR;
    int width = LineStyle.DEFAULT_WIDTH;
    try {
      int eventType = parser.next();
      while (!LINE_STYLE_TAG.equals(parser.getName())) {
        if (eventType == XmlPullParser.START_TAG) {
          final String tagName = parser.getName();
          if (COLOR_TAG.equals(tagName)) {
            color = parseKmlColor(parser.nextText());
          } else if (WIDTH_TAG.equals(tagName)) {
            width = Integer.parseInt(parser.nextText());
          }
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    } catch (final NumberFormatException ignore) {
      //values set at the beginning
      Log.printStackTrace(ignore);
    }

    return new LineStyle(color, width);
  }

  private PolyStyle readPolyStyle(final KXmlParser parser) throws IOException {
    int color = PolyStyle.DEFAULT_COLOR;
    try {
      int eventType = parser.next();
      while (!POLY_STYLE_TAG.equals(parser.getName())) {
        if (eventType == XmlPullParser.START_TAG) {
          final String tagName = parser.getName();
          if (COLOR_TAG.equals(tagName)) {
            color = parseKmlColor(parser.nextText());
          }
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    } catch (final NumberFormatException ignore) {
      //values set at the beginning
      Log.printStackTrace(ignore);
    }

    return new PolyStyle(color);
  }

  protected int parseKmlColor(final String color) {
    if (color.length() != 8) {
      return LineStyle.DEFAULT_COLOR;
    }
    //kml is using aabbggrr instead of aarrggbb
    final int alpha = Integer.parseInt(color.substring(0, 2), 16) & 0xFF;
    final int blue = Integer.parseInt(color.substring(2, 4), 16) & 0xFF;
    final int green = Integer.parseInt(color.substring(4, 6), 16) & 0xFF;
    final int red = Integer.parseInt(color.substring(6), 16) & 0xFF;

    return alpha << 24 | red << 16 | green << 8 | blue;
  }

  private OnMapElement readPolygon(final KXmlParser parser) throws IOException {
    WgsPoint[] coordinates = null;
    try {
      int eventType = parser.next();
      while (!POLYGON_TAG.equals(parser.getName())) {
        if (eventType == XmlPullParser.START_TAG
            && PLACEMARK_POINT_COORDINATES_TAG.equals(parser.getName())) {
          coordinates = parseWgsCoordinates(parser.nextText());
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    }
    return new Polygon(coordinates);
  }

  private WgsPoint readPoint(final KXmlParser parser) throws IOException {
    WgsPoint result = null;
    try {
      int eventType = parser.next();
      while (!PLACEMARK_POINT_TAG.equals(parser.getName())) {
        if (eventType == XmlPullParser.START_TAG
            && PLACEMARK_POINT_COORDINATES_TAG.equals(parser.getName())) {
          result = parseWgsCoordinatesForPoint(parser.nextText())[0];
        }
        eventType = parser.next();
      }
    } catch (final XmlPullParserException e) {
      Log.printStackTrace(e);
    } catch (final ArrayIndexOutOfBoundsException e) {
      Log.printStackTrace(e);
      //result will stay null
    }

    return result;
  }

  
  
  protected WgsPoint[] parseWgsCoordinates(final String kmlCoordinates) {
    final Vector result = new Vector();
    if (kmlCoordinates == null) {
      return null;
    }

    //TODO jaanus : maybe can handle it better
    final String[] breakSplit = Utils.split(kmlCoordinates, "\n");
    for (int i = 0; i < breakSplit.length; i++) {
      final String[] spaceSplit = Utils.split(breakSplit[i].trim(), " ");
      for (int j = 0; j < spaceSplit.length; j++) {
        try {
          final String line = spaceSplit[j];
          if (line.indexOf(",") < 0) {
            continue;
          }

          final String[] split = Utils.split(line, ",");

          if (split.length < 2) {
            continue;
          }

          result.addElement(new WgsPoint(Double.parseDouble(split[0].trim()), Double
              .parseDouble(split[1].trim())));
        } catch (final NumberFormatException e) {
          Log.printStackTrace(e);
          //TODO jaanus :  is just ignore this point OK?
          continue;
        }
      }
    }

    final WgsPoint[] out = new WgsPoint[result.size()];
    result.copyInto(out);

    return out;
  }

  protected WgsPoint[] parseWgsCoordinatesForPoint(final String kmlCoordinates) {
    final Vector result = new Vector();
    if (kmlCoordinates == null) {
      return null;
    }
    try {
      final String[] split = Utils.split(kmlCoordinates, ",");

      result.addElement(new WgsPoint(Double.parseDouble(split[0].trim()), Double
          .parseDouble(split[1].trim())));
    } catch (final NumberFormatException e) {
      Log.printStackTrace(e);
      //TODO jaanus :  is just ignore this point OK?
    }

    final WgsPoint[] out = new WgsPoint[result.size()];
    result.copyInto(out);

    return out;
  }

  public int getCachingLevel() {
    return Cache.CACHE_LEVEL_NONE;
  }
}
