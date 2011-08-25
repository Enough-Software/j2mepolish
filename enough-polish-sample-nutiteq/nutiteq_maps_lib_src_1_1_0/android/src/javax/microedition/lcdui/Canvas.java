package javax.microedition.lcdui;

import android.view.KeyEvent;

public abstract class Canvas extends Displayable {
  public static final int FIRE = KeyEvent.KEYCODE_DPAD_CENTER;
  public static final int GAME_A = KeyEvent.KEYCODE_POUND;
  public static final int GAME_B = KeyEvent.KEYCODE_STAR;
  public static final int GAME_C = KeyEvent.KEYCODE_1;
  public static final int GAME_D = KeyEvent.KEYCODE_3;

  public static final int LEFT = KeyEvent.KEYCODE_DPAD_LEFT;
  public static final int RIGHT = KeyEvent.KEYCODE_DPAD_RIGHT;
  public static final int UP = KeyEvent.KEYCODE_DPAD_UP;
  public static final int DOWN = KeyEvent.KEYCODE_DPAD_DOWN;

  public static final int KEY_NUM0 = KeyEvent.KEYCODE_0;
  public static final int KEY_NUM1 = KeyEvent.KEYCODE_1;
  public static final int KEY_NUM2 = KeyEvent.KEYCODE_2;
  public static final int KEY_NUM3 = KeyEvent.KEYCODE_3;
  public static final int KEY_NUM4 = KeyEvent.KEYCODE_4;
  public static final int KEY_NUM5 = KeyEvent.KEYCODE_5;
  public static final int KEY_NUM6 = KeyEvent.KEYCODE_6;
  public static final int KEY_NUM7 = KeyEvent.KEYCODE_7;
  public static final int KEY_NUM8 = KeyEvent.KEYCODE_8;
  public static final int KEY_NUM9 = KeyEvent.KEYCODE_9;
  public static final int KEY_POUND = KeyEvent.KEYCODE_POUND;
  public static final int KEY_STAR = KeyEvent.KEYCODE_STAR;

  protected Canvas() {

  }

  public final void repaint() {
    throw new RuntimeException("Never call me again!");
  }

  public void setFullScreenMode(final boolean mode) {
    throw new RuntimeException("Never call me again!");
  }

  public boolean isDoubleBuffered() {
    return false;
  }

  public boolean hasPointerEvents() {
    return false;
  }

  public boolean hasPointerMotionEvents() {
    return false;
  }

  public boolean hasRepeatEvents() {
    return false;
  }

  public int getKeyCode(final int gameAction) {
    return gameAction;
  }

  public String getKeyName(final int keyCode) {
    return null;
  }

  public int getGameAction(final int keyCode) {
    return keyCode;
  }

  protected void keyPressed(final int keyCode) {

  }

  protected void keyRepeated(final int keyCode) {

  }

  protected void keyReleased(final int keyCode) {

  }

  protected void pointerPressed(final int x, final int y) {

  }

  protected void pointerReleased(final int x, final int y) {

  }

  protected void pointerDragged(final int x, final int y) {

  }

  public final void repaint(final int x, final int y, final int width, final int height) {

  }

  public final void serviceRepaints() {

  }

  protected void showNotify() {

  }

  protected void hideNotify() {

  }

  protected abstract void paint(final Graphics g);

  protected void sizeChanged(final int w, final int h) {

  }
}
