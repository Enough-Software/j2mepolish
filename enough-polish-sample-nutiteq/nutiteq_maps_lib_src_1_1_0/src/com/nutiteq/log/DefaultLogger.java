package com.nutiteq.log;

/**
 * Default logger used in library (if logging will be enabled). Uses memory
 * buffer of 4096 characters and prints all messages also to console.
 */
public class DefaultLogger implements Logger {
  private static final String LOG_LEVEL_ERROR = "Error";
  private static final String LOG_LEVEL_INFO = "Info";
  private static final String LOG_LEVEL_DEBUG = "Debug";

  private static final int LOG_BUFFER_SIZE = 4096;
  private static final StringBuffer BUFFER = new StringBuffer();

  public void error(final String message) {
    logMessage(LOG_LEVEL_ERROR, message);
  }

  public void info(final String message) {
    logMessage(LOG_LEVEL_INFO, message);
  }

  public void debug(final String message) {
    logMessage(LOG_LEVEL_DEBUG, message);
  }

  public String getLog() {
    return BUFFER.toString();
  }

  public void printStackTrace(final Throwable t) {
    t.printStackTrace();
  }

  private void logMessage(final String level, final String message) {
    final String logMessage = level + " > " + message;
    System.out.println(logMessage);
    checkBufferSize(logMessage);
    BUFFER.insert(0, logMessage + "\n");
  }

  private void checkBufferSize(final String logMessage) {
    if (BUFFER.length() + logMessage.length() > LOG_BUFFER_SIZE) {
      BUFFER.delete(LOG_BUFFER_SIZE / 2, BUFFER.length());
    }
  }
}
