package com.mgmaps.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class Tools {
  private static final byte[] BOM = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

  public static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  // skip & read buffer size
  public static final int BUFSIZE = 4096;

  public static final int COORDS_D = 0;
  public static final int COORDS_DM = 1;
  public static final int COORDS_DMS = 2;

  private Tools() {
  }

  /**
   * Read a line from the input stream.
   * 
   * @param is
   *          input stream to read from
   * @throws IOException
   *           if an error exception occurs
   */
  public static String readLine2(final InputStream is) throws IOException {
    final StringBuffer sb = new StringBuffer();
    while (true) {
      final int c = is.read();
      if (c == -1) {
        break;
      }
      if (c == '\n' || c == '\r') {
        break;
      } else {
        sb.append((char) c);
      }
    }
    return sb.toString();
  }

  /**
   * Pad a string with leading zeroes.
   * 
   * @param s
   *          the string to pad
   * @param chars
   *          minimum number of characters
   * @return the padded string
   */
  public static String padZero(final String s, final int chars) {
    final StringBuffer result = new StringBuffer();
    final int len = s.length();
    for (int i = len; i < chars; i++) {
      result.append('0');
    }
    result.append(s);
    return result.toString();
  }

  /**
   * Pad a string with trailing zeroes.
   * 
   * @param s
   *          the string to pad
   * @param chars
   *          minimum number of characters
   * @return the padded string
   */
  public static String padZeroEnd(final String s, final int chars) {
    final StringBuffer result = new StringBuffer();
    final int len = s.length();
    result.append(s);
    for (int i = len; i < chars; i++) {
      result.append('0');
    }
    return result.toString();
  }

  /**
   * Format a time (milliseconds) to a string.
   * 
   * @param time
   *          time in milliseconds
   * @return the time as a string
   */
  public static String timeFormat(final long time) {
    int millis = (int) (time % 86400000L);
    final StringBuffer result = new StringBuffer();
    result.append(padZero(Integer.toString(millis / 3600000), 2));
    result.append(':');
    millis %= 3600000;
    result.append(padZero(Integer.toString(millis / 60000), 2));
    result.append(':');
    millis %= 60000;
    result.append(padZero(Integer.toString(millis / 1000), 2));
    result.append('.');
    millis %= 1000;
    result.append(padZero(Integer.toString(millis), 3));
    return result.toString();
  }

  /**
   * Format a floating point number represented as integer.
   * 
   * @param number
   *          number to format
   * @param decimals
   *          number of decimals after the point
   * @return the formatted number
   */
  public static String floatFormat(final long number, final int decimals) {
    // compute the divisor
    int pow10 = 1;
    for (int i = 0; i < decimals; i++) {
      pow10 *= 10;
    }
    final long trunc = Math.abs(number / pow10);
    long frac = Math.abs(number % pow10);

    // build the result
    final StringBuffer result = new StringBuffer();
    if (number < 0) {
      result.append('-');
    }
    result.append(trunc);
    result.append('.');
    // tail removing
    int i;
    for (i = 1; (frac % 10 == 0) && (i < decimals); i++) {
      frac /= 10;
    }
    // head padding
    result.append(padZero(Long.toString(frac), decimals - i + 1));
    return result.toString();
  }

  /**
   * Word-wrap text.
   * 
   * @param text
   *          the text to wrap
   * @param font
   *          the font used
   * @param fwidth
   *          text field width
   * @param currentX
   *          start of first line (heading)
   * @return a string array of lines containing the wrapped text
   */
  public static String[] wrapText(final String text, final Font font, final int fwidth, final int currentX) {
    // wrapped text lines
    final Vector wt = new Vector();

    final int len = text.length();
    int first = 0;
    int last = 0;
    boolean emptylineflag = false;

    for (int i = 0; i < len; i++) {
      // split at '\n'
      if (text.charAt(i) == '\n') {
        wt.addElement(text.substring(first, i));
        first = i + 1;
        last = i;
        emptylineflag = false;
        continue;
      }

      // update last if necessary
      if (text.charAt(i) == ' ') {
        last = i;
        // don't check to split - we would have splitted at the previous char
        continue;
      }

      // check length, see if we should split
      final int w = font.substringWidth(text, first, i - first + 1);
      // we should not split if we printed nothing on the previous line
      if ((first == 0 && w + currentX > fwidth) || (first != 0 && w > fwidth)) {
        // yep, we should split, first to last, then the rest
        // if last is equal to first, the first word doesn't fit, so add an
        // empty string
        // but set a flag to avoid adding it again
        if (first < last || (first == last && !emptylineflag)) {
          final String s = text.substring(first, last);
          wt.addElement(s);
        }
        if (first < last) {
          // start with last+1
          emptylineflag = false;
          first = last + 1;
        } else {
          emptylineflag = true;
        }
      }
    }

    // add the last string
    wt.addElement(text.substring(first));

    return toStringArray(wt);
  }

  /**
   * Split a string.
   * 
   * @param s
   *          string to split
   * @param c
   *          character used for splitting
   * @param max
   *          max number of fields to be returned; 0 for unlimited, non-zero to
   *          return all the remaining string in the last split
   * @param dblquotes
   *          whether to ignore delimiters between double quotes
   * @return an array with the tokens
   */
  public static String[] split(final String s, final char c, final boolean dblquotes, final int max) {
    int j = 0;
    final Vector vector = new Vector();

    // add first max-1 components
    int num = 0;
    int i = 0;
    String ss = null;
    int k1;
    int k2;
    for (i = 0; num != max - 1; i = j + 1) {
      k1 = -1;
      k2 = -1;
      j = s.indexOf(c, i);
      if (dblquotes) {
        // should have k1=0
        k1 = s.indexOf('"', i);
        // quote found and before delimiter
        if (k1 >= 0 && k1 < j) {
          // next quote
          k2 = s.indexOf('"', k1 + 1);
          if (k2 >= 0) {
            // recompute next delimiter - should have j=k2+1
            j = s.indexOf(c, k2 + 1);
          }
        }
      }
      if (j >= 0) {
        if (dblquotes && k1 >= 0 && k2 >= 0) {
          ss = s.substring(k1 + 1, k2);
        } else {
          ss = s.substring(i, j);
        }
        vector.addElement(ss);
        num++;
      } else {
        if (dblquotes && k1 >= 0 && k2 >= 0) {
          ss = s.substring(k1 + 1, k2);
        } else {
          ss = s.substring(i);
        }
        vector.addElement(ss);
        num++;
        break;
      }
    }

    // add the max-th component
    k1 = -1;
    k2 = -1;
    if (max != 0 && j >= 0) {
      if (dblquotes) {
        k1 = s.indexOf('"', i);
        // quote found and before delimiter
        if (k1 >= 0) {
          // next quote
          k2 = s.indexOf('"', k1 + 1);
        }
      }
      if (dblquotes && k1 >= 0 && k2 >= 0) {
        ss = s.substring(k1 + 1, k2);
      } else {
        ss = s.substring(i);
      }
      vector.addElement(ss);
      num++;
    }

    // convert to array
    final String as[] = new String[num];
    vector.copyInto(as);

    // return the array
    return as;
  }

  /**
   * Convert a vector to a string array.
   * 
   * @param v
   *          vector to convert
   * @return the string array
   */
  public static String[] toStringArray(final Vector v) {
    final String[] res = new String[v.size()];
    v.copyInto(res);
    return res;
  }

  /**
   * Format coordinate.
   * 
   * @param number
   *          coordinate as an int
   * @param isLat
   *          whether it's a latitude or not
   * @param disp
   *          whether to format for display or not
   * @return the formatted coord
   */
  public static String formatCoord(final int number, final boolean isLat, final boolean disp) {
    int ax = Math.abs(number);
    final String ch = "" + (isLat ? ((number >= 0) ? 'N' : 'S') : ((number >= 0) ? 'E' : 'W'));
    if (disp) {
      final StringBuffer result = new StringBuffer();
      switch (0) {
      case Tools.COORDS_DMS:
        result.append(ax / 1000000);
        result.append((char) 0x00B0);
        ax %= 1000000;
        ax *= 60;
        result.append(ax / 1000000);
        result.append('\'');
        ax %= 1000000;
        ax *= 60;
        result.append(ax / 1000000);
        result.append('"');
        break;

      case Tools.COORDS_DM:
        result.append(ax / 1000000);
        result.append((char) 0x00B0);
        ax %= 1000000;
        ax *= 60;
        result.append(Tools.floatFormat(ax / 1000, 3));
        result.append('\'');
        break;

      case Tools.COORDS_D:
        result.append(Tools.floatFormat(ax / 10, 5));
        break;
      }
      result.append(ch);
      return result.toString();
    } else {
      // do not translate when formatting coords for non-display
      return Tools.floatFormat(ax, 6) + ch;
    }
  }

  /**
   * Scale an image down 2^n times.
   * 
   * @param src
   *          image to scale
   * @param dif
   *          logarithmic scaling level (1 = scale by 2, 2 = scale by 4, etc.)
   * @return the scaled image
   */
  public static Image scaleImage05(final Image src, final int dif) {
    final int srcWidth = src.getWidth();
    final int srcHeight = src.getHeight();
    final int dstWidth = srcWidth >> dif;
    final int dstHeight = srcHeight >> dif;

    //TODO jaanus : this should actually be handled somewhere outside
    if (dstWidth == 0 || dstHeight == 0) {
      return Image.createImage(1, 1);
    }

    final Image dst = Image.createImage(dstWidth, dstHeight);
    final Graphics g = dst.getGraphics();
    final int[] lineRGB = new int[srcWidth];
    final int[] srcPos = new int[dstWidth]; // cache for x positions

    /*
     * Pre-calculate x positions with modified bresenham algorithm
     * http://www.cs.helsinki.fi/group/goa/mallinnus/lines/bresenh.html
     */
    for (int x = 1; x < dstWidth; x++) {
      srcPos[x] = x << dif;
    }

    for (int y = 0; y < dstHeight; y++) {
      src.getRGB(lineRGB, 0, srcWidth, 0, y << dif, srcWidth, 1);
      for (int x = 1; x < dstWidth; x++) {
        // skip pixel 0
        lineRGB[x] = lineRGB[srcPos[x]];
      }
      g.drawRGB(lineRGB, 0, dstWidth, 0, y, dstWidth, 1, false);
    }

    return dst;
  }

  /**
   * Scale an image up 2^n times.
   * 
   * @param image
   *          image to scale
   * @param dif
   *          logarithmic scaling level (1 = scale by 2, 2 = scale by 4, etc.)
   * @return the scaled image
   */
  public static Image scaleImage20(final Image src, final int dif) {
    return scaleImage20(src, -1, -1, dif);
  }

  public static Image scaleImage20(final Image src, final int sourceX, final int sourceY, final int dif) {
    final int dstWidth = src.getWidth();
    final int dstHeight = src.getHeight();
    int srcWidth = dstWidth >> dif;
    int srcHeight = dstHeight >> dif;
    if (srcWidth < 1) {
      srcWidth = 1;
    }
    if (srcHeight < 1) {
      srcHeight = 1;
    }
    final int srcX = sourceX == -1 ? (dstWidth - srcWidth) >> 1 : sourceX;
    final int srcY = sourceY == -1 ? (dstHeight - srcHeight) >> 1 : sourceY;
    final Image dst = Image.createImage(dstWidth, dstHeight);
    final Graphics g = dst.getGraphics();
    final int[] lineRGB = new int[dstWidth];
    final int[] srcPos = new int[dstWidth]; // cache for x positions
    final int[] lineRGB2 = new int[dstWidth];

    /*
     * Pre-calculate x positions with modified bresenham algorithm
     * http://www.cs.helsinki.fi/group/goa/mallinnus/lines/bresenh.html
     */
    for (int x = 0; x < dstWidth; x++) {
      srcPos[x] = srcX + (x >> dif);
    }

    for (int y = 0; y < dstHeight; y++) {
      src.getRGB(lineRGB, 0, dstWidth, 0, srcY + (y >> dif), dstWidth, 1);
      for (int x = 0; x < dstWidth; x++) {
        lineRGB2[x] = lineRGB[srcPos[x]];
      }
      g.drawRGB(lineRGB2, 0, dstWidth, 0, y, dstWidth, 1, false);
    }

    return dst;
  }

  /**
   * URLEncode method.
   * 
   * @param s
   *          string to urlencode
   * @return urlencoded string
   */
  public static String urlEncode(final String s) {
    if (s == null) {
      return null;
    }

    byte[] b = null;

    try {
      b = s.getBytes("utf-8");
    } catch (final Exception ex) {
      try {
        b = s.getBytes("UTF8");
      } catch (final Exception ex2) {
        b = s.getBytes();
      }
    }

    final StringBuffer result = new StringBuffer();

    for (int i = 0; i < b.length; i++) {
      if ((b[i] >= 'A' && b[i] <= 'Z') || (b[i] >= 'a' && b[i] <= 'z') || (b[i] >= '0' && b[i] <= '9')) {
        result.append((char) b[i]);
      } else {
        result.append('%');
        result.append(HEX_ARRAY[((b[i] + 256) >> 4) & 0x0F]);
        result.append(HEX_ARRAY[b[i] & 0x0F]);
      }
    }

    return result.toString();
  }

  /**
   * Turn a signed byte into an unsigned byte (stored as int).
   * 
   * @param b
   *          byte to convert
   * @return the converted byte/int
   */
  public static int unsigned(final byte b) {
    return (b < 0) ? (b + 256) : b;
  }

  /**
   * Convert a byte array to a string, with UTF-8 as encoding.
   * 
   * @param data
   *          byte array to convert
   * @return the resulting string
   */
  public static String byteArrayToString(final byte[] data) {
    String sdata = null;
    try {
      if (startsWithBOM(data)) {
        sdata = new String(data, 3, data.length - 3, "utf-8");
      } else {
        sdata = new String(data, "utf-8");
      }
    } catch (final Exception ex) {
      sdata = new String(data);
    }
    return sdata;
  }

  private static boolean startsWithBOM(final byte[] data) {
    return data.length >= 3 && data[0] == BOM[0] && data[1] == BOM[1] && data[2] == BOM[2];
  }
}
