package com.nutiteq.log;

/**
 * Interface for defining custom logging handlers.
 */
public interface Logger {
  void error(final String message);

  void info(final String message);

  void debug(final String message);

  String getLog();

  void printStackTrace(final Throwable t);
}
