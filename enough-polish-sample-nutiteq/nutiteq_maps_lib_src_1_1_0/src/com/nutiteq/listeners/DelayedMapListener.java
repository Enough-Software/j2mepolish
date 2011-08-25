/*
 * Created on Aug 19, 2008
 */
package com.nutiteq.listeners;

import com.nutiteq.components.WgsPoint;

/**
 * This class implements a delayed map listener that can be used as a wrapper for another
 * map listener. It waits a few seconds before triggering mapMoved in the wrapped listener.
 * This allows for quick map moves without calling mapMoved after each one.  
 *
 * @author CristiS
 */
public class DelayedMapListener extends Thread implements MapListener {
  
  private static final int DEFAULT_DELAY = 2500;
  private static final int DEFAULT_TIMEOUT_INTERVAL = 0;
  private int mapDelay;
  private MapListener listener;

  private boolean stopping;
  
  private long lastCall;
  private int timeoutInterval;
  
  /**
   * Constructor for DelayedMapListener.
   *
   * @param listener wrapped listener
   */
  public DelayedMapListener(final MapListener listener) {
    this(listener, DEFAULT_DELAY, DEFAULT_TIMEOUT_INTERVAL);
  }
  
  /**
   * Constructor for DelayedMapListener.
   *
   * @param listener wrapped listener
   * @param mapDelay delay introduced before calling mapMoved in the wrapped listener.
   * @param timeoutInterval timeout interval. If a map move does not occur within this interval, 
   *   mapMoved is triggered anyway. Useful for services that should dynamically update map content
   *   even if the user doesn't move. If set to zero (by default) this behavior is disabled. 
   */
  public DelayedMapListener(final MapListener listener, final int mapDelay, final int timeoutInterval) {
    this.mapDelay = mapDelay;
    this.timeoutInterval = timeoutInterval-mapDelay;
    if (this.timeoutInterval < 0) {
      this.timeoutInterval = 0;
    }
    this.listener = listener;
    start();
  }

  public void mapClicked(final WgsPoint point) {
    listener.mapClicked(point);
  }

  public void mapMoved() {
    if (lastCall == 0) {
      lastCall = System.currentTimeMillis();
    }
    notify();
  }

  public void needRepaint(final boolean mapIsComplete) {
    listener.needRepaint(mapIsComplete);
  }

  /**
   * Main thread method.
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    while (!stopping) {
      try {
        synchronized(this) {
          if (timeoutInterval > 0) {
            wait(timeoutInterval);
          } else {
            wait();
          }
        }
      } catch (InterruptedException inte) { }
      
      // break the loop if stopping 
      if (stopping) {
        break;
      }
        
      try {
        // sleep for a while if needed
        // TODO "lastCall" should be in synchronized blocks or volatile
        final long oldMillis = System.currentTimeMillis();
        synchronized(this) {
          wait(mapDelay);
        }

        // notified before delay? continue the loop without updating the map
        // but first check if the timeout interval has passed
        final long newMillis = System.currentTimeMillis();
        if ((newMillis - oldMillis) < mapDelay &&
            (timeoutInterval == 0 || (newMillis - lastCall) < timeoutInterval)) {
          continue;
        }
          
        // run the update
        listener.mapMoved();
        lastCall = newMillis;
      } catch (InterruptedException inte2) {
        // ignore and continue the loop
      }
    }
  }
  
  /**
   * Set the timeout interval.
   * 
   * @param timeoutInterval new timeout interval
   */
  public synchronized void setTimeoutInterval(int timeoutInterval) {
    this.timeoutInterval = timeoutInterval-mapDelay;
    if (this.timeoutInterval < 0) {
      this.timeoutInterval = 0;
    }
  }
}
