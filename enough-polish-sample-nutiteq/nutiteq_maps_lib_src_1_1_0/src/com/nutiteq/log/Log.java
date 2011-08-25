package com.nutiteq.log;

/**
 * Log handler used by library. Supports insertion of custom handlers. By
 * default all logging is disabled. All disabled log levels will be removed by
 * obfuscator and also the code required for log message will be gone.
 */
public class Log {
  private static Logger logger = new DefaultLogger();
  private static boolean showError;
  private static boolean showInfo;
  private static boolean showDebug;
  private static boolean printStackTrace;

  private Log() {
  }

  public static void error(final String message) {
    if (showError) {
      logger.error(message);
    }
  }

  public static void info(final String message) {
    if (showInfo) {
      logger.info(message);
    }
  }

  public static void debug(final String message) {
    if (showDebug) {
      logger.debug(message);
    }
  }

  public static String getLog() {
    return logger.getLog();
  }

  public static void printStackTrace(final Exception e) {
    if (printStackTrace) {
      logger.printStackTrace(e);
    }
  }

  public static void setLogger(final Logger logger) {
    Log.logger = logger;
  }

  public static void setShowError(final boolean showError) {
    Log.showError = showError;
  }

  public static void setShowInfo(final boolean showInfo) {
    Log.showInfo = showInfo;
  }

  public static void setShowDebug(final boolean showDebug) {
    Log.showDebug = showDebug;
  }

  public static void setPrintStackTrace(final boolean printStackTrace) {
    Log.printStackTrace = printStackTrace;
  }

  public static void enableAll() {
    showError = true;
    showInfo = true;
    showDebug = true;
    printStackTrace = true;
  }
}
