package com.nutiteq.controls;

/**
 * Interface for implementations mapping action codes defined in
 * {@link com.nutiteq.controls.ControlKeys} to key presses on phone.
 */
public interface ControlKeysHandler {
  /**
   * Get action code for the key 
   * @param keyCode phone key code
   * @return action code from {@link com.nutiteq.controls.ControlKeys}
   */
  int getKeyActionCode(final int keyCode);
}