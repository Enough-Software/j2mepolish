package com.nutiteq.log;

import android.util.Log;

/**
 * Logger implementation to be used on Android platform
 */
public class AndroidLogger implements Logger {
  private final String loggingTag;

  /**
   * Create new logger for Android platform.
   * 
   * @param loggingTag
   *          logging tag used in android log lines
   */
  public AndroidLogger(final String loggingTag) {
    this.loggingTag = loggingTag;
  }

  public void debug(final String message) {
    Log.d(loggingTag, message);
  }

  public void error(final String message) {
    Log.e(loggingTag, message);
  }

  public String getLog() {
    throw new UnsupportedOperationException("getLog not implemented");
  }

  public void info(final String message) {
    Log.i(loggingTag, message);
  }

  public void printStackTrace(final Throwable t) {
    Log.w(loggingTag, Log.getStackTraceString(t));
  }
}
