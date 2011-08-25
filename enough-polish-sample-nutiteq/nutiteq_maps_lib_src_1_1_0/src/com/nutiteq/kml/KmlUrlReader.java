package com.nutiteq.kml;

import com.nutiteq.components.WgsBoundingBox;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.utils.Utils;

/**
 * <p>
 * Basic KML URL reader, adds standard BBOX, zoom and max=10 parameters in
 * default way. Usage example with MapItem:<br/>
 * 
 * <code>mapItem.addKmlService(new KmlUrlReader("http://www.panoramio.com/panoramio.kml?LANG=en_US.utf8&",true));</code>
 * </p>
 */
public class KmlUrlReader implements KmlService {
  private static final String DEFAULT_KML_ICON = "/images/def_kml.png";
  private final String url;
  private final boolean needsUpdateAfterRead;
  private boolean hasBeenRead;
  private int lastReadZoom;
  private WgsPoint lastReadCenter;
  private WgsPoint screenSizeDegrees;
  private final int maxElements;
  private final boolean serverSideRender;
  private String defaultIcon;

  /**
   * 
   * Constructor for the built-in KML reader. Asks for 10 items.
   * 
   * @param kmlUrl
   *          URL where to read the KML
   * @param needsUpdateAfterRead
   *          true if KML service is dynamic (in time and regarding BBOX), false
   *          otherwise
   */
  public KmlUrlReader(final String kmlUrl, final boolean needsUpdateAfterRead) {
    this(kmlUrl, 10, needsUpdateAfterRead, false);
  }

  /**
   * Constructor for the built-in KML reader.
   * 
   * @param kmlUrl
   *          URL where to read the KML
   * @param maxElements
   *          how many elements to request from server.
   * @param needsUpdateAfterRead
   *          true if KML service is dynamic (in time and regarding BBOX), false
   *          otherwise
   */
  public KmlUrlReader(final String kmlUrl, final int maxElements, final boolean needsUpdateAfterRead) {
    this(kmlUrl, maxElements, needsUpdateAfterRead, false);
  }

  /**
   * Constructor for the built-in KML reader.
   * 
   * @param kmlUrl
   *          URL where to read the KML
   * @param maxElements
   *          how many elements to request from server.
   * @param needsUpdateAfterRead
   *          true if KML service is dynamic (in time and regarding BBOX), false
   *          otherwise
   * @param defaultIcon
   *          default marker image location for the KML places
   */
  public KmlUrlReader(final String kmlUrl, final int maxElements,
      final boolean needsUpdateAfterRead, final String defaultIcon) {
   this(kmlUrl, maxElements, needsUpdateAfterRead, false,defaultIcon);
  }

  /**
   * Constructor for the built-in KML reader.
   * 
   * @param kmlUrl
   *          URL where to read the KML
   * @param maxElements
   *          how many elements to request from server.
   * @param needsUpdateAfterRead
   *          true if KML service is dynamic (in time and regarding BBOX), false
   *          otherwise
   * @param serverSideRender
   *          are elements drawn on map server side
   */
  public KmlUrlReader(final String kmlUrl, final int maxElements,
      final boolean needsUpdateAfterRead, final boolean serverSideRender) {
   this(kmlUrl, maxElements, needsUpdateAfterRead, serverSideRender,DEFAULT_KML_ICON);
  }
  /**
   * Constructor for the built-in KML reader.
   * 
   * @param kmlUrl
   *          URL where to read the KML
   * @param maxElements
   *          how many elements to request from server.
   * @param needsUpdateAfterRead
   *          true if KML service is dynamic (in time and regarding BBOX), false
   *          otherwise
   * @param serverSideRender
   *          are elements drawn on map server side
   */
  public KmlUrlReader(final String kmlUrl, final int maxElements,
      final boolean needsUpdateAfterRead, final boolean serverSideRender, final String defaultIcon) {
    url = needsUpdateAfterRead ? Utils.prepareForParameters(kmlUrl) : kmlUrl;
    this.maxElements = maxElements;
    this.needsUpdateAfterRead = needsUpdateAfterRead;
    this.serverSideRender = serverSideRender;
    this.defaultIcon = defaultIcon;    
  }
  
  public String getDefaultIcon() {
    return defaultIcon;
}

public String getServiceUrl(final WgsBoundingBox boundingBox, final int zoom) {
    final StringBuffer serviceUrl = new StringBuffer(url);
    if (needsUpdateAfterRead) {
      serviceUrl.append("BBOX=").append(boundingBox.getWgsMin().getLon()).append(",");
      serviceUrl.append(boundingBox.getWgsMin().getLat()).append(",");
      serviceUrl.append(boundingBox.getWgsMax().getLon()).append(",");
      serviceUrl.append(boundingBox.getWgsMax().getLat());
      serviceUrl.append("&zoom=").append(zoom).append("&max=").append(maxElements);
    }
    lastReadZoom = zoom;
    hasBeenRead = true;

    final double screenWidth = Math.abs(boundingBox.getWgsMax().getLon()
        - boundingBox.getWgsMin().getLon());
    final double screenHeight = Math.abs(boundingBox.getWgsMax().getLat()
        - boundingBox.getWgsMin().getLat());

    screenSizeDegrees = new WgsPoint(screenWidth, screenHeight);
    lastReadCenter = boundingBox.getBoundingBoxCenter();

    return serviceUrl.toString();

  }

  public int maxResults() {
    return maxElements;
  }

  public boolean needsUpdate(final WgsBoundingBox boundingBox, final int zoom) {
    if (!hasBeenRead) {
      return true;
    }

    if (!needsUpdateAfterRead) {
      return false;
    }

    if (lastReadZoom != zoom) {
      return true;
    }

    final WgsPoint bBoxCenter = boundingBox.getBoundingBoxCenter();
    final double xChange = Math.abs(lastReadCenter.getLon() - bBoxCenter.getLon());
    final double yChange = Math.abs(lastReadCenter.getLat() - bBoxCenter.getLat());
    //if screen moved 1/5 of screen size, update
    return xChange >= (screenSizeDegrees.getLon() * 0.20)
        || yChange >= (screenSizeDegrees.getLat() * 0.20);
  }

  public void resetRead() {
    hasBeenRead = false;
  }

  public boolean serverSideRender() {
    return serverSideRender;
  }
}