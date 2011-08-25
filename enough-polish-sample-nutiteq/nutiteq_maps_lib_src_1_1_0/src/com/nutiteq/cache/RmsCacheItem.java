package com.nutiteq.cache;

public class RmsCacheItem {
  public int dataLength;
  public int bucket;
  public int recordId;
  public RmsCacheItem next;
  public RmsCacheItem previous;
  public String key;
}
