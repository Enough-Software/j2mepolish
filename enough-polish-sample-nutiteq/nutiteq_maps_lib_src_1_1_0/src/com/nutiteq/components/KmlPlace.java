package com.nutiteq.components;

import javax.microedition.lcdui.Image;

import com.nutiteq.kml.KmlStyle;
import com.nutiteq.kml.KmlStylesCache;


//TODO jaanus : changes Place/KmlPlace relation
public class KmlPlace {
  private final Place place;
  private final String description;
  private String styleUrl;
  private final String address;
  private final String snippet;
  private final ExtendedDataMap extendedDataMap;


  /**
   * 
   * @param place
   * @param styleUrl
   * @param description
   * @param address
   * @param snippet
   */
  public KmlPlace(final Place place, final String styleUrl, final String description,
      final String address, final String snippet, final ExtendedDataMap extendedData) {
    this.place = place;
    this.styleUrl = styleUrl;
    this.description = description;
    this.address = address;
    this.snippet = snippet;
    this.extendedDataMap = extendedData;

  }

  /**
   * 
   * @param name
   * @param icon
   * @param coordinates
   * @param styleUrl
   * @param description
   * @param address
   * @param snippet
   * @param extendedData
   */
  public KmlPlace(final String name, final Image icon, final WgsPoint coordinates,
      final String styleUrl, final String description, final String address, final String snippet, final ExtendedDataMap extendedData) {
    this(new Place(0, name, icon, coordinates), styleUrl, description, address, snippet, extendedData);
  }

  public WgsPoint getWgs() {
    return place.getWgs();
  }

  public String getName() {
    return place.getName();
  }

  public Place getPlace() {
    return place;
  }

  public String getDescription() {
    return description;
  }

  public String getStyleUrl() {
    return styleUrl;
  }

  public String getAddress() {
    return address;
  }

  public String getSnippet() {
    return snippet;
  }

  public ExtendedDataMap getExtendedData() {
      return extendedDataMap;
    }

  
  /**
   * Update icon for this place
   * 
   * @param image
   *          image for new icon
   */
  public void updateIcon(final Image image) {
    place.setIcon(image);
  }

  /**
   * Not par of public API
   * 
   * @param url
   */
  public void setStyleUrl(final String url) {
    styleUrl = url;
  }

  /**
   * Not part of public API
   * 
   * @param imageUrl
   * @param stylesCache
   * @return is image used by this place
   */
  public boolean usesIcon(final String imageUrl, final KmlStylesCache stylesCache) {
    if (styleUrl == null || imageUrl == null || stylesCache == null) {
      return false;
    }

    if (!styleUrl.startsWith("#")) {
      return imageUrl.equals(styleUrl);
    }

    final KmlStyle style = stylesCache.getStyle(styleUrl.substring(1));
    return imageUrl.equals(style.getIconUrl());
  }

  public boolean equals(final Object obj) {
    //TODO jaanus : looks like bad design
    if (obj instanceof Place && place != null) {
      final Place p = (Place) obj;
      return place.equals(p);
    }

    if (!(obj instanceof KmlPlace)) {
      return false;
    }

    final KmlPlace other = (KmlPlace) obj;

    return place.equals(other.place);
  }

  public int hashCode() {
    throw new RuntimeException("hashCode() has not been implemented");
  }

  /**
   * Not part of public API
   * 
   * @return place as info object
   */
  public PlaceInfo getInfoObject() {
    final PlaceInfo result = new PlaceInfo();

    result.setName(place.getName());
    result.setDescription(description);
    result.setAddress(address);
    result.setSnippet(snippet);
    result.setExtendedData(extendedDataMap);
    result.setCoordinates(place.getWgs());

    return result;
  }
}
