package com.nutiteq.kml;

import java.util.Hashtable;

public class KmlStyleMap {
  private static final String NORMAL = "normal";
  private final Hashtable pairs;
  private final String id;

  public KmlStyleMap(final String id) {
    pairs = new Hashtable();
    this.id = id;
  }

  public String getStyleMapId() {
    return id;
  }

  public void addPair(final String key, final String styleUrl) {
    pairs.put(key, styleUrl);
  }

  public String getNormal() {
    return (String) pairs.get(NORMAL);
  }
}
