package com.nutiteq.components;

import java.util.Hashtable;

public class ExtendedDataMap {
  private final Hashtable pairs;

  public ExtendedDataMap() {
    pairs = new Hashtable();
  }

  public void addPair(final String key, final String value) {
    pairs.put(key, value);
  }
  
  public String getValue(final String key) {
      return (String) pairs.get(key);
    }

}
