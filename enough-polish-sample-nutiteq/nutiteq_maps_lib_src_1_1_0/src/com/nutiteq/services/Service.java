package com.nutiteq.services;

/**
 * Interface for generic service
 */
public interface Service {
  /**
   * Performe service action
   */
  void execute();

  /**
   * Cancel service execution
   */
  void cancel();
}
