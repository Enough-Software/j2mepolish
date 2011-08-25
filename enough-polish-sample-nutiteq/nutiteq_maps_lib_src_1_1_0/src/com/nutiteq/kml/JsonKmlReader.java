package com.nutiteq.kml;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Image;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import com.java4ever.apime.io.GZIP;
import com.nutiteq.cache.Cache;
import com.nutiteq.components.ExtendedDataMap;
import com.nutiteq.components.KmlPlace;
import com.nutiteq.components.Line;
import com.nutiteq.components.OnMapElement;
import com.nutiteq.components.Place;
import com.nutiteq.components.Polygon;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.log.Log;

/**
 * Creates KML Elements from JSON data. Specific for CloudMade geocoder
 * @author Jaak Laineste
 *
 */
public class JsonKmlReader implements ResourceRequestor, ResourceDataWaiter {

  private static Image defaultKmlIcon = Image.createImage(18, 18);

  private final KmlService service;
  private final KmlElementsWaiter servicesHandler;
  private final String serviceUrl;
  private int placeCount;
  private static final String DEFAULT_KML_ICON = "/images/def_kml.png";

  public JsonKmlReader(final KmlElementsWaiter servicesHandler, final KmlService service,
          final String serviceUrl){
      this(servicesHandler, service, serviceUrl, DEFAULT_KML_ICON);
  }
  
  public JsonKmlReader(final KmlElementsWaiter servicesHandler, final KmlService service,
      final String serviceUrl, final String defaultIcon) {
    this.servicesHandler = servicesHandler;
    this.service = service;
    this.serviceUrl = serviceUrl;

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
        
      
//      Log.debug("KmlReader.read() response packed ? " + responsePacked + ". Bytes:"+data.length);
    final byte[] finalData = guessPacked ? GZIP.inflate(data) : data;
    // remove BOM (ie the first 3 bytes)
    if(finalData[0]==0xef && finalData[1]==0xbb && finalData[2]==0xbf){
        System.arraycopy(finalData, 3, finalData, 0, finalData.length-3);
    }
        
    try {
      servicesHandler.addKmlPlaces(service, read(finalData, service.maxResults()));
    } catch (final IOException e) {
      Log.printStackTrace(e);
      notifyError();
    } finally {
    }
    Log.debug("JsonKmlReader done, read places: "+placeCount);
    
  }

  public void notifyError() {
    //TODO jaanus : ignore?
  }

  protected KmlPlace[] read(final byte[] finalData, final int maxResults) throws IOException {

    final Vector places = new Vector();
// read JSON and create KmlPlace from it.
    
    String jsonString = new String(finalData,"UTF8");
    ExtendedDataMap xdata = new ExtendedDataMap();
    
    try {
        JSONObject root = new JSONObject(jsonString);
        int found = root.getInt("found");
//        Log.debug(root.toString(2));
        Log.debug("found "+found+" objects");
        JSONArray features = root.getJSONArray("features");

        int size = features.length();
        
        for (int i=0;i<size;i++){
            final Vector geomElements = new Vector();

            JSONObject feature = (JSONObject) features.get(i);

            JSONObject location = feature.optJSONObject("location");
            String country= new String();
            String county = new String();
            String city = new String();
            if (location != null){
             country = location.optString("country");
             county = location.optString("county");
             city = location.optString("city");
            }
            JSONObject properties = feature.optJSONObject("properties");
            String name = properties.optString("name");
            
            JSONObject centroid = feature.optJSONObject("centroid");
            JSONArray coordinates = centroid.optJSONArray("coordinates");

            String latS = coordinates.getString(0);
            String lngS = coordinates.getString(1);

            double lat = Double.parseDouble(latS);
            double lng = Double.parseDouble(lngS);

            // read geometry
            JSONObject geometry = feature.optJSONObject("geometry");
            String geomType = geometry.getString("type");
            
            if (geomType.equals("POLYGON")){
                JSONArray geomCoords = geometry.getJSONArray("coordinates");
                Polygon polygon = new Polygon(getCoords(geomCoords));
                geomElements.addElement(polygon);
            }
            if (geomType.equals("LINESTRING")){
                JSONArray geomCoords = geometry.getJSONArray("coordinates");
                Line polyline = new Line(getCoords(geomCoords));
                geomElements.addElement(polyline);
            }
            if (geomType.equals("MULTIPOLYGON")){
                JSONArray geomCoords = geometry.getJSONArray("coordinates");
                int nPolygons = geomCoords.length();
                for(int j=0;j<nPolygons;j++){
                    Polygon polygon = new Polygon(getCoords(geomCoords.getJSONArray(j)));
                    geomElements.addElement(polygon);
                }
            }
            if (geomType.equals("MULTILINESTRING")){
                JSONArray geomCoords = geometry.getJSONArray("coordinates");
                int nLinestrings = geomCoords.length();
                for(int j=0;j<nLinestrings;j++){
                    Line line = new Line(getCoords(geomCoords.getJSONArray(j)));
                    geomElements.addElement(line);
                }
            }

            
            for (Enumeration e = properties.keys() ; e.hasMoreElements() ;) {
                String key = (String) e.nextElement();
                String val = properties.getString(key);
                xdata.addPair(key, val);
            }

            xdata.addPair("country", country);
            xdata.addPair("county", county);
            xdata.addPair("city", city);

        // create KML object
            Place kmlPlace = new Place(0, name, defaultKmlIcon, new WgsPoint(lng,lat));
            final OnMapElement[] elements = new OnMapElement[geomElements.size()];
            geomElements.copyInto(elements);
            String desc = new String("");
            if (location != null){
            desc  = "in ";
            if(!city.equals("")){
                desc += city+", ";
            }
            if(!county.equals("")){
                desc += county+", ";
            }
            if(!country.equals("")){
              desc += country;
            }
            }
            kmlPlace.setOnMapElements(elements);
            KmlPlace place = new KmlPlace(kmlPlace, null, desc, null, null, xdata);
            places.addElement(place);
            
        
        } // for each feature found
        
    } catch (Exception e) {
        e.printStackTrace();
        notifyError();
    }

    
    // TODO now some dummy place for testing

    
    final KmlPlace[] result = new KmlPlace[places.size()];
    places.copyInto(result);
    placeCount = places.size();
    return result;
  }

  private WgsPoint[] getCoords(JSONArray geomCoords) {
      Vector coordsV = new Vector();
      JSONArray geomCoords1=geomCoords;
    try {
      if (geomCoords.getJSONArray(0).optJSONArray(0) != null) {
        geomCoords1 = geomCoords.getJSONArray(0); // there is extra level

        int geomCoordsSize = geomCoords1.length();
        for (int j = 0; j < geomCoordsSize; j++) {
          JSONArray geomCoordPair = geomCoords1.getJSONArray(j);
          String gLatS = geomCoordPair.getString(0);
          String gLngS = geomCoordPair.getString(1);
          double gLat = Double.parseDouble(gLatS);
          double gLng = Double.parseDouble(gLngS);
          WgsPoint point = new WgsPoint(gLng, gLat);
          coordsV.addElement(point);
        }
      }
    } catch (JSONException e) {
      Log.error("JSON parsing exception");
      e.printStackTrace();
    }

      
      WgsPoint[] coords = new WgsPoint[coordsV.size()];
      coordsV.copyInto(coords);
      return coords;
}

public int getCachingLevel() {
    return Cache.CACHE_LEVEL_NONE;
  }
}
