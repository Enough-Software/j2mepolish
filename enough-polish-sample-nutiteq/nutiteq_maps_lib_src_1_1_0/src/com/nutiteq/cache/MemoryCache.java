package com.nutiteq.cache;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * <p>
 * Memory cache implementing LRU (least recently used) strategy. If cache is
 * full, least recently used items will be pushed out.
 * </p>
 * 
 * <p>
 * Current implementation uses only actual data size. Objects/keys overhead is
 * not calculated in cache size.
 * </p>
 */
public class MemoryCache implements Cache {
  private final Hashtable cache;
  private final int maxSize;
  private CacheItem mru;
  private CacheItem lru;
  private int size;

  /**
   * Create a new MemoryCache instance.
   * 
   * @param cacheSize
   *          cache size in bytes.
   */
  public MemoryCache(final int cacheSize) {
    maxSize = cacheSize;
    cache = new Hashtable();
  }

  public void initialize() {

  }

  public void deinitialize() {

  }

  public byte[] get(final String cacheId) {
    final CacheItem result = (CacheItem) cache.get(cacheId);

    if (result == null) {
      return null;
    }

    //make it the most recently used entry
    if (mru != result) { //not already the MRU
      if (lru == result) { // I'm the least recently used
        lru = result.previous;
      }

      // Remove myself from the LRU list.
      if (result.next != null) {
        result.next.previous = result.previous;
      }

      result.previous.next = result.next;

      // Add myself back in to the front.
      mru.previous = result;
      result.previous = null;
      result.next = mru;
      mru = result;
    }

    return result.data;
  }

  public void cache(final String cacheId, final byte[] data, final int cacheLevel) {
    if ((cacheLevel & CACHE_LEVEL_MEMORY) != CACHE_LEVEL_MEMORY || data == null || data.length == 0) {
      return;
    }

    final byte[] existing = get(cacheId);
    if (existing != null) {
      // The key has already been used.  By calling get() we already promoted
      // it to the MRU spot.  However, if the data has changed, we need to
      // update it in the hash table.
      //TODO jaanus : check also data content?
      if (existing.length != data.length) {
        final CacheItem i = (CacheItem) cache.get(cacheId);
        i.data = data;
      }
    } else {
      // cache miss
      final CacheItem item = new CacheItem();
      item.key = cacheId;
      item.data = data;
      item.next = mru;
      item.previous = null;

      if (cache.size() == 0) {
        // then cache is empty
        lru = item;
      } else {
        mru.previous = item;
      }

      mru = item;
      cache.put(cacheId, item);

      size += data.length;
    }

    while (size > maxSize) {
      // Kick out the least recently used element.
      cache.remove(lru.key);
      size -= lru.data.length;

      if (lru.previous != null) {
        lru.previous.next = null;
      }

      lru = lru.previous;
    }
  }

  public boolean contains(final String cacheKey) {
    return cache.containsKey(cacheKey);
  }

  public boolean contains(final String cacheKey, final int cacheLevel) {
    if ((cacheLevel & CACHE_LEVEL_MEMORY) != CACHE_LEVEL_MEMORY) {
      return false;
    }

    return contains(cacheKey);
  }

  //TEST METHODS
  protected int getCalculatedSize() {
    return size;
  }

  protected int getActualElementsSize() {
    final Enumeration e = cache.elements();
    int result = 0;
    while (e.hasMoreElements()) {
      final CacheItem item = (CacheItem) e.nextElement();
      result += item.data.length;
    }

    return result;
  }

  protected CacheItem getMRU() {
    return mru;
  }
}
