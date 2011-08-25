package com.nutiteq.net;

/**
 * Listener for activities from download counter.
 */
public interface NetworkListener {
  void downloadStarted();

  void dataMoved();

  void downloadCompleted();
}
