package com.nutiteq.io;

/**
 * Interface for general resources retrieval. Resource retrieval will be handled
 * inside library.
 */
public interface ResourceRequestor {
  /**
   * Get path to resource. Currently library supports retrieval from network and
   * application jar
   * 
   * @return path to resource
   */
  String resourcePath();

  /**
   * Notify implementing object about retrieval error
   */
  void notifyError();

  /**
   * Define at which level should given resource be cached. Check
   * {@link com.nutiteq.cache.Cache}
   * 
   * @return cache level for resource
   */
  int getCachingLevel();
}
