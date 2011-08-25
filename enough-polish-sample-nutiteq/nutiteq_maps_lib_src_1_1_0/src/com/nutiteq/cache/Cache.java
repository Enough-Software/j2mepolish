package com.nutiteq.cache;

/**
 * Interface for library caching implementation. At the moment three caching
 * levels are supported:
 * <ul>
 * <li>no caching</li>
 * <li>memory caching</li>
 * <li>rms caching</li>
 * </ul>
 * Caching definition needs to be revised in the future.
 */
public interface Cache {
  /**
   * Don't cache items
   */
  int CACHE_LEVEL_NONE = 0;
  /**
   * Cache items at memory level
   */
  int CACHE_LEVEL_MEMORY = 1;
  /**
   * Cache item to persistent storage. Based on implementation can be RMS or
   * file system.
   */
  int CACHE_LEVEL_PERSISTENT = 2;

  /**
   * Initialize needed resources for cache. For example read cache index.
   */
  void initialize();

  /**
   * Clean up cache resources and, if needed, write cache definition/index
   * somewhere.
   */
  void deinitialize();

  /**
   * Get cached data.
   * 
   * @param cacheKey
   *          key that was used for data caching
   * @return cached data
   */
  byte[] get(final String cacheKey);

  /**
   * Cache given data
   * 
   * @param cacheKey
   *          key for the cached data
   * @param data
   *          data to be cached
   * @param cacheLevel
   *          at which level this data needs to be cached (memory, rms, etc).
   */
  void cache(final String cacheKey, final byte[] data, final int cacheLevel);

  /**
   * Does this cache contain data for given cache key
   * 
   * @param cacheKey
   *          cache key checked
   * @return if data for key present
   */
  boolean contains(final String cacheKey);

  /**
   * Does the specified cache level contain given cache key
   * 
   * @param cacheKey
   *          cache key to be checked
   * @param cacheLevel
   *          which cache levels to check
   * @return if data for key present
   */
  boolean contains(final String cacheKey, final int cacheLevel);
}
