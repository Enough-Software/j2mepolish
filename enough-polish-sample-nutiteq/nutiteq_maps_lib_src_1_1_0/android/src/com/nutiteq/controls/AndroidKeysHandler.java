package com.nutiteq.controls;

/**
 * Keys handler for Android G1 phone. 
 */
public class AndroidKeysHandler extends UserDefinedKeysMapping {
  public AndroidKeysHandler() {
    defineKey(ControlKeys.MOVE_UP_KEY, android.view.KeyEvent.KEYCODE_DPAD_UP);
    defineKey(ControlKeys.MOVE_DOWN_KEY, android.view.KeyEvent.KEYCODE_DPAD_DOWN);
    defineKey(ControlKeys.MOVE_LEFT_KEY, android.view.KeyEvent.KEYCODE_DPAD_LEFT);
    defineKey(ControlKeys.MOVE_RIGHT_KEY, android.view.KeyEvent.KEYCODE_DPAD_RIGHT);
    defineKey(ControlKeys.SELECT_KEY, android.view.KeyEvent.KEYCODE_DPAD_CENTER);
    defineKey(ControlKeys.ZOOM_IN_KEY,android.view.KeyEvent.KEYCODE_I);
    defineKey(ControlKeys.ZOOM_OUT_KEY, android.view.KeyEvent.KEYCODE_O);
  }
}
