package com.nutiteq.cache;

//TODO jaanus : add linked list
public class CacheItem {
  public String key;
  public byte[] data;
  public CacheItem next;
  public CacheItem previous;
}
