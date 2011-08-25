package com.nutiteq.wrappers.rimui;

/**
 * This is a wrapper around J2ME font class, to enable usages of RIM UI font.
 * This implementation does not provide full implementation wrapping. Only
 * methods used by library have been implemented.
 */
public class Font {
  public static final int FACE_SYSTEM = 0;
  public static final int FACE_PROPORTIONAL = 64;
  public static final int FACE_MONOSPACE = 32;

  public static final int STYLE_BOLD = 1;
  public static final int STYLE_PLAIN = 1;
  public static final int STYLE_ITALIC = 2;
  public static final int STYLE_UNDERLINED = 4;
  
  public static final int SIZE_SMALL = 1;
  public static final int SIZE_MEDIUM = 2;
  public static final int SIZE_LARGE = 4;

  public static final int FONT_STATIC_TEXT = 0;
  public static final int FONT_INPUT_TEXT = 1;
  
  private final net.rim.device.api.ui.Font wrapped;

  /**
   * Wrap native Font
   * 
   * @param wrapped
   *          RIM UI from to be wrapped
   */
  private Font(final net.rim.device.api.ui.Font wrapped) {
    this.wrapped = wrapped;
  }

  public int getHeight() {
    return wrapped.getHeight();
  }

  public int stringWidth(final String string) {
    return wrapped.getAdvance(string);
  }

  public static Font getDefaultFont() {
    return new Font(net.rim.device.api.ui.Font.getDefault());
  }

  public static Font getFont(final int face, final int style, final int size) {
    //TODO jaanus
    return getDefaultFont();
  }

  public int substringWidth(final String str, final int offset, final int len) {
    return wrapped.getAdvance(str, offset, len);
  }

  /**
   * Retrieve wrapped RIM UI font
   * 
   * @return wrapped 'native' font
   */
  public net.rim.device.api.ui.Font getNativeFont() {
    return wrapped;
  }
}
