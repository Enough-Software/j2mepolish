package com.nutiteq.cache;

/**
 * Combines different caching levels to one cache.
 */
public class CachingChain implements Cache {
  private final Cache[] cacheLevels;

  /**
   * Define caching chain with given cache levels.
   * 
   * @param cacheLevels
   *          cache levels to be used in this chain.
   */
  public CachingChain(final Cache[] cacheLevels) {
    this.cacheLevels = cacheLevels;
  }

  public void initialize() {
    for (int i = 0; i < cacheLevels.length; i++) {
      cacheLevels[i].initialize();
    }
  }

  public void deinitialize() {
    for (int i = 0; i < cacheLevels.length; i++) {
      cacheLevels[i].deinitialize();
    }
  }

  public byte[] get(final String cacheKey) {
    byte[] result = null;
    for (int i = 0; i < cacheLevels.length; i++) {
      result = cacheLevels[i].get(cacheKey);
      if (result != null) {
        break;
      }
    }

    return result;
  }

  public void cache(final String cacheKey, final byte[] data, final int cacheLevel) {
    if (cacheLevel == CACHE_LEVEL_NONE || data == null || data.length == 0) {
      return;
    }

    for (int i = 0; i < cacheLevels.length; i++) {
      cacheLevels[i].cache(cacheKey, data, cacheLevel);
    }
  }

  public boolean contains(final String cacheKey) {
    for (int i = 0; i < cacheLevels.length; i++) {
      if (cacheLevels[i].contains(cacheKey)) {
        return true;
      }
    }

    return false;
  }

  public boolean contains(final String cacheKey, final int cacheLevel) {
    for (int i = 0; i < cacheLevels.length; i++) {
      if (cacheLevels[i].contains(cacheKey, cacheLevel)) {
        return true;
      }
    }

    return false;
  }
}
