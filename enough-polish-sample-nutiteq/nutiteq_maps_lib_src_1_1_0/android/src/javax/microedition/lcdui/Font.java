package javax.microedition.lcdui;

import android.graphics.Paint;
import android.graphics.Typeface;

public class Font {
  public static final int FACE_SYSTEM = 0;
  public static final int FACE_MONOSPACE = 32;
  public static final int FACE_PROPORTIONAL = 64;

  public static final int FONT_STATIC_TEXT = 0;
  public static final int FONT_INPUT_TEXT = 1;

  public static final int SIZE_MEDIUM = 0;
  public static final int SIZE_SMALL = 8;
  public static final int SIZE_LARGE = 16;

  private static final int TEXT_SIZE_SMALL = 10;
  private static final int TEXT_SIZE_MEDIUM = 16;
  private static final int TEXT_SIZE_LARGE = 22;

  public static final int STYLE_PLAIN = 0;
  public static final int STYLE_BOLD = 1;
  public static final int STYLE_ITALIC = 2;
  public static final int STYLE_UNDERLINED = 4;

  private final int size;
  private final Paint typefacePaint;
  private final float scale = 1.0f;

  private Font(final int size, final Paint paint) {
    this.size = size;
    typefacePaint = paint;
    typefacePaint.setTextSize(size);
  }

  public static Font getDefaultFont() {
    return getFont(FACE_SYSTEM, STYLE_PLAIN, SIZE_MEDIUM);
  }

  public static Font getFont(final int face, final int style, final int size) {
    int textSize;
    int paintFlags = 0;
    int typefaceStyle = Typeface.NORMAL;
    Typeface base;
    switch (face) {
    case FACE_MONOSPACE:
      base = Typeface.MONOSPACE;
      break;
    case FACE_SYSTEM:
      base = Typeface.DEFAULT;
      break;
    case FACE_PROPORTIONAL:
      base = Typeface.SANS_SERIF;
      break;
    default:
      throw new IllegalArgumentException("unknown face " + face);
    }
    switch (size) {
    case SIZE_LARGE:
      textSize = TEXT_SIZE_LARGE;
      break;
    case SIZE_SMALL:
      textSize = TEXT_SIZE_SMALL;
      break;
    default:
      textSize = TEXT_SIZE_MEDIUM;
      break;
    }
    if ((style & STYLE_BOLD) != 0) {
      typefaceStyle |= Typeface.BOLD;
    }
    if ((style & STYLE_ITALIC) != 0) {
      typefaceStyle |= Typeface.ITALIC;
    }
    if ((style & STYLE_UNDERLINED) != 0) {
      paintFlags |= Paint.UNDERLINE_TEXT_FLAG;
    }

    final Typeface typeface = Typeface.create(base, typefaceStyle);
    final Paint paint = new Paint(paintFlags);

    paint.setTypeface(typeface);

    return new Font(textSize, paint);
  }

  public int getHeight() {
    return Math.round(size + typefacePaint.descent());
  }

  public int stringWidth(final String str) {
    return Math.round(typefacePaint.measureText(str));
  }

  public int substringWidth(final String text, final int offset, final int len) {
    return Math.round(typefacePaint.measureText(text, offset, offset + len));
  }

  public Paint getTypefacePaint() {
    return typefacePaint;
  }

  public int charWidth(final char ch) {
    return stringWidth(Character.toString(ch));
  }

  protected int getSize() {
    return size;
  }

  public int getDescent() {
    return Math.round(typefacePaint.descent());
  }
}
