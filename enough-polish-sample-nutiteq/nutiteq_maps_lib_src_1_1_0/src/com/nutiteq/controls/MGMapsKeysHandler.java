package com.nutiteq.controls;

import javax.microedition.lcdui.Canvas;

/**
 * A wrapper around {@link UserDefinedKeysMapping} that defines MGMaps style
 * move/zoom key actions.
 */
public class MGMapsKeysHandler implements ControlKeysHandler {
  private final UserDefinedKeysMapping keysMapping;

  public MGMapsKeysHandler() {
    keysMapping = new UserDefinedKeysMapping();
    //default keys
    keysMapping.defineKey(ControlKeys.MOVE_UP_KEY, -1);
    keysMapping.defineKey(ControlKeys.MOVE_DOWN_KEY, -2);
    keysMapping.defineKey(ControlKeys.MOVE_LEFT_KEY, -3);
    keysMapping.defineKey(ControlKeys.MOVE_RIGHT_KEY, -4);
    keysMapping.defineKey(ControlKeys.SELECT_KEY, -5);
    //MPowerPlayer
    keysMapping.defineKey(ControlKeys.MOVE_UP_KEY, 38);
    keysMapping.defineKey(ControlKeys.MOVE_DOWN_KEY, 40);
    keysMapping.defineKey(ControlKeys.MOVE_LEFT_KEY, 37);
    keysMapping.defineKey(ControlKeys.MOVE_RIGHT_KEY, 39);
    keysMapping.defineKey(ControlKeys.SELECT_KEY, 10);
    //Blackberry
    keysMapping.defineKey(ControlKeys.MOVE_UP_KEY, Canvas.UP);
    keysMapping.defineKey(ControlKeys.MOVE_DOWN_KEY, Canvas.DOWN);
    keysMapping.defineKey(ControlKeys.MOVE_LEFT_KEY, Canvas.LEFT);
    keysMapping.defineKey(ControlKeys.MOVE_RIGHT_KEY, Canvas.RIGHT);
    keysMapping.defineKey(ControlKeys.SELECT_KEY, Canvas.FIRE);
    keysMapping.defineKey(ControlKeys.SELECT_KEY, -8);
    //Numeric keys
    keysMapping.defineKey(ControlKeys.MOVE_UP_KEY, Canvas.KEY_NUM2);
    keysMapping.defineKey(ControlKeys.MOVE_DOWN_KEY, Canvas.KEY_NUM8);
    keysMapping.defineKey(ControlKeys.MOVE_LEFT_KEY, Canvas.KEY_NUM4);
    keysMapping.defineKey(ControlKeys.MOVE_RIGHT_KEY, Canvas.KEY_NUM6);
    keysMapping.defineKey(ControlKeys.SELECT_KEY, Canvas.KEY_NUM5);

    //Zoom keys
    keysMapping.defineKey(ControlKeys.ZOOM_IN_KEY, Canvas.KEY_POUND);
    // 'i' and 'o' for zooming In and Out on phones with full keyboards
    keysMapping.defineKey(ControlKeys.ZOOM_IN_KEY, 'i');
    // 'q' and 'p' or 'q' and 'a' for zooming In and Out on phones with full keyboards
    keysMapping.defineKey(ControlKeys.ZOOM_IN_KEY, 'q');
    // '+' and '-' for zooming in and out
    keysMapping.defineKey(ControlKeys.ZOOM_IN_KEY, '+');
    // Vol'+' on Motorola KRZR K1 and other Motorolas
    keysMapping.defineKey(ControlKeys.ZOOM_IN_KEY, -100);
    // Vol'+' on Blackberry Pearl
    keysMapping.defineKey(ControlKeys.ZOOM_IN_KEY, -150);
    // Vol'+' on Sony-Ericssons
    keysMapping.defineKey(ControlKeys.ZOOM_IN_KEY, -36);
    // Vol'+' on Sony-Ericsson P990
    keysMapping.defineKey(ControlKeys.ZOOM_IN_KEY, -38);
    
    keysMapping.defineKey(ControlKeys.ZOOM_OUT_KEY, Canvas.KEY_STAR);
    // 'i' and 'o' for zooming In and Out on phones with full keyboards
    keysMapping.defineKey(ControlKeys.ZOOM_OUT_KEY, 'o');
    // 'q' and 'p' or 'q' and 'a' for zooming In and Out on phones with full keyboards
    keysMapping.defineKey(ControlKeys.ZOOM_OUT_KEY, 'p');
    keysMapping.defineKey(ControlKeys.ZOOM_OUT_KEY, 'a');
    // '+' and '-' for zooming in and out
    keysMapping.defineKey(ControlKeys.ZOOM_OUT_KEY, '-');
    // Vol'-' on Motorola KRZR K1 and other Motorolas
    keysMapping.defineKey(ControlKeys.ZOOM_OUT_KEY, -101);
    // Vol'-' on Blackberry Pearl
    keysMapping.defineKey(ControlKeys.ZOOM_OUT_KEY, -151);
    // Vol'-' on Sony-Ericssons
    keysMapping.defineKey(ControlKeys.ZOOM_OUT_KEY, -37);
    // Vol'-' on Sony-Ericsson P990
    keysMapping.defineKey(ControlKeys.ZOOM_OUT_KEY, -39);
  }

  public int getKeyActionCode(final int keyCode) {
    return keysMapping.getKeyActionCode(keyCode);
  }
}
