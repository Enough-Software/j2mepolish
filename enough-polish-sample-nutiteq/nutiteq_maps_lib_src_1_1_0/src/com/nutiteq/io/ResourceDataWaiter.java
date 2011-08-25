package com.nutiteq.io;

/**
 * Interface for extending {@link com.nutiteq.io.ResourceRequestor} by declaring
 * that given objects waits full data for resource. Resource retrieval should be
 * handled by library implementation.
 */
public interface ResourceDataWaiter {
  /**
   * Data for resource retrieved
   * 
   * @param data
   *          retrieved data
   */
  void dataRetrieved(byte[] data);
}
