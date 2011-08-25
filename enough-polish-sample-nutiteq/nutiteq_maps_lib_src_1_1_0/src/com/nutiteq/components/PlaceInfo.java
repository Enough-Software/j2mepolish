package com.nutiteq.components;

/**
 * Data object representing additional data for internally handled map object.
 * Not all data is guaranteed to be present.
 */
public class PlaceInfo {
  private String name;
  private String description;
  private String address;
  private String snippet;
  private WgsPoint wgs;
  private ExtendedDataMap extendedDataMap;

  /**
   * Not part of public API
   */
  public void setName(final String name) {

    this.name = name;
  }

  public String getName() {
    return name;
  }

  /**
   * Not part of public API
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Not part of public API
   */
  public void setAddress(final String address) {
    this.address = address;
  }

  public String getAddress() {
    return address;
  }

  /**
   * Not part of public API
   */
  public void setSnippet(final String snippet) {
    this.snippet = snippet;
  }

  public String getSnippet() {
    return snippet;
  }

  /**
   * Not part of public API
   */
  public void setExtendedData(final ExtendedDataMap extendedData) {
    this.extendedDataMap= extendedData;
  }

/**
 * Returns ExtendedData element as (ExtendedDataMap, a Hashtable)
 */
   public ExtendedDataMap getExtendedData() {
       return extendedDataMap;
   }
/**
 * Returns specific ExtendedData value by key 
 * @param key
 *      Key for the data
 */  
  public String getExtendedData(final String key) {
      return extendedDataMap.getValue(key);
    }

  
  /**
   * Not part of public API
   */
  public void setCoordinates(final WgsPoint wgs) {
    this.wgs = wgs;
  }

  public WgsPoint getCoordinates() {
    return wgs;
  }
    
}

