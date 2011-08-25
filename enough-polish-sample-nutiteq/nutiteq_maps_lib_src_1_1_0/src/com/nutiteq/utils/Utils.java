package com.nutiteq.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import com.mgmaps.utils.Tools;
import com.nutiteq.components.Rectangle;
import com.nutiteq.components.Sortable;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.log.Log;

public class Utils {
  private static final String HTTP_PREFIX = "http://";
  private static final String HTTPS_PREFIX = "https://";
  private static final String FILE_PREFIX = "file://";
  private static final String JAR_RESOURCE_PREFIX = "/";

  public static final int RESOURCE_TYPE_NETWORK = 1;
  public static final int RESOURCE_TYPE_FILE = 2;
  public static final int RESOURCE_TYPE_JAR = 4;

  private static final int[] POW2_TABLE = { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512 };

  private Utils() {
  }

  public static boolean rectanglesIntersect(final int rOneX, final int rOneY, final int rOneWidth, final int rOneHeight, final int rTwoX,
      final int rTwoY, final int rTwoWidth, final int rTwoHeight) {
    //TODO jaanus : check this. maybe can add some optimization
    final int rectOneX = rOneWidth > 0 ? rOneX : rOneX + rOneWidth;
    final int rectOneY = rOneHeight > 0 ? rOneY : rOneY + rOneHeight;
    final int rectOneWidth = Math.abs(rOneWidth);
    final int rectOneHeight = Math.abs(rOneHeight);

    final int rectTwoX = rTwoWidth > 0 ? rTwoX : rTwoX + rTwoWidth;
    final int rectTwoY = rTwoHeight > 0 ? rTwoY : rTwoY + rTwoHeight;
    final int rectTwoWidth = Math.abs(rTwoWidth);
    final int rectTwoHeight = Math.abs(rTwoHeight);

    if (rectTwoY + rectTwoHeight < rectOneY || // is the bottom of two above the top of one?
        rectTwoY > rectOneY + rectOneHeight || // is the top of two below bottom of one?
        rectTwoX + rectTwoWidth < rectOneX || // is the right of two to the left of one?
        rectTwoX > rectOneX + rectOneWidth) { // is the left of two to the right of one?
      return false;
    }

    return true;
  }

  public static int binarySearch(final int[] array, final int target) {
    int high = array.length;
    int low = -1;
    int probe;
    while (high - low > 1) {
      probe = (high + low) / 2;
      if (array[probe] > target) {
        high = probe;
      } else {
        low = probe;
      }
    }
    if (low == -1 || array[low] != target) {
      return -1;
    } else {
      return low;
    }
  }

  public static int binarySearch(final String[] array, final String target) {
    int high = array.length;
    int low = -1;
    int probe;
    while (high - low > 1) {
      probe = (high + low) / 2;
      if (array[probe].compareTo(target) > 0) {
        high = probe;
      } else {
        low = probe;
      }
    }
    if (low == -1 || !array[low].equals(target)) {
      return -1;
    } else {
      return low;
    }
  }

  public static void bubbleSort(final Sortable[] array) {
    final int elements = array.length;
    int i;
    int j;
    Sortable t = null;
    for (i = 0; i < elements; i++) {
      for (j = 1; j < (elements - i); j++) {
        if (array[j - 1].compareTo(array[j]) > 0) {
          t = array[j - 1];
          array[j - 1] = array[j];
          array[j] = t;
        }
      }
    }
  }

  public static void bubbleSort(final int array[]) {
    final int elements = array.length;
    int i;
    int j;
    int t = 0;
    for (i = 0; i < elements; i++) {
      for (j = 1; j < (elements - i); j++) {
        if (array[j - 1] > array[j]) {
          t = array[j - 1];
          array[j - 1] = array[j];
          array[j] = t;
        }
      }
    }
  }

  public static void doubleBubbleSort(final int mainArray[], final int[] secondArray) {
    final int elements = mainArray.length;
    int i;
    int j;
    int t = 0;
    int t2 = 0;
    for (i = 0; i < elements; i++) {
      for (j = 1; j < (elements - i); j++) {
        if (mainArray[j - 1] > mainArray[j]) {
          t = mainArray[j - 1];
          t2 = secondArray[j - 1];

          mainArray[j - 1] = mainArray[j];
          secondArray[j - 1] = secondArray[j];

          mainArray[j] = t;
          secondArray[j] = t2;
        }
      }
    }
  }

  public static void doubleBubbleSort(final String mainArray[], final String[] secondArray) {
    final int elements = mainArray.length;
    int i;
    int j;
    String t;
    String t2;
    for (i = 0; i < elements; i++) {
      for (j = 1; j < (elements - i); j++) {
        if (mainArray[j - 1].compareTo(mainArray[j]) > 0) {
          t = mainArray[j - 1];
          t2 = secondArray[j - 1];

          mainArray[j - 1] = mainArray[j];
          secondArray[j - 1] = secondArray[j];

          mainArray[j] = t;
          secondArray[j] = t2;
        }
      }
    }
  }

  /**
   * Avoid FP operations.
   */
  public static int log2(final int x) {
    for (int i = POW2_TABLE.length - 1; i >= 0; i--) {
      if (x == POW2_TABLE[i]) {
        return i;
      }
    }
    throw new IllegalArgumentException("Do not know the log2 from " + x);
  }

  public static Image resizeImageAndCopyPrevious(final int newWidth, final int newHeight, final Image resized) {
    // TODO jaanus : if new is smaller can optimize with
    // createImage(Image image, int x, int y, int width, int height, int
    // transform)
    final Image result = Image.createImage(newWidth, newHeight);
    final Graphics g = result.getGraphics();
    g.drawImage(resized, (newWidth - resized.getWidth()) / 2, (newHeight - resized.getHeight()) / 2, Graphics.TOP | Graphics.LEFT);
    return result;
  }

  public static Rectangle mergeAreas(final Rectangle areaOne, final Rectangle areaTwo) {
    final int resultX = Math.min(areaOne.getX(), areaTwo.getX());
    final int resultY = Math.min(areaOne.getY(), areaTwo.getY());

    final int areaOneRightX = areaOne.getX() + areaOne.getWidth();
    final int areaTwoRightX = areaTwo.getX() + areaTwo.getWidth();

    final int areaOneBottomY = areaOne.getY() + areaOne.getHeight();
    final int areaTwoBottomY = areaTwo.getY() + areaTwo.getHeight();

    final int resultWidth = Math.max(areaOneRightX, areaTwoRightX) - resultX;
    final int resultHeight = Math.max(areaOneBottomY, areaTwoBottomY) - resultY;

    return new Rectangle(resultX, resultY, resultWidth, resultHeight);
  }

  public static String[] split(final String string, final String splitBy) {
    final Vector tokens = new Vector();
    final int tokenLength = splitBy.length();

    int tokenStart = 0;
    int splitIndex;
    while ((splitIndex = string.indexOf(splitBy, tokenStart)) != -1) {
      tokens.addElement(string.substring(tokenStart, splitIndex));
      tokenStart = splitIndex + tokenLength;
    }

    tokens.addElement(string.substring(tokenStart));

    final String[] result = new String[tokens.size()];
    tokens.copyInto(result);
    return result;
  }

  public static Rectangle areaToScreen(final Rectangle area, final int viewX, final int viewY, final int viewWidth, final int viewHeight) {
    final int areaX = area.getX() < viewX ? viewX : area.getX();
    final int areaY = area.getY() < viewY ? viewY : area.getY();
    final int areaW = area.getX() + area.getWidth() > viewX + viewWidth ? viewX + viewWidth - areaX : area.getX() + area.getWidth() - areaX;
    final int areaH = area.getY() + area.getHeight() > viewY + viewHeight ? viewY + viewHeight - areaY : area.getY() + area.getHeight()
        - areaY;

    return new Rectangle(areaX - viewX, areaY - viewY, areaW, areaH);
  }

  public static String[] wrapText(final String text, final Font font, final int maxWidth) {
    return Tools.wrapText(text, font, maxWidth, 0);
  }

  public static Image createImage(final String image) {
    try {
      return Image.createImage(image);
    } catch (final IOException e) {
      Log.debug("createImage '" + image + "': " + e.getMessage());
      Log.printStackTrace(e);
      return null;
    }
  }

  public static double round(final double num) {
    final double floor = Math.floor(num);
    if (num - floor >= 0.5) {
      return Math.ceil(num);
    } else {
      return floor;
    }
  }

  public static double parseDecimalDegree(final String hoursMinutesFractions, final String whereOnGlobe) {
    final int dotPos = hoursMinutesFractions.indexOf('.');
    final String degrees = hoursMinutesFractions.substring(0, dotPos - 2);
    final String minutes = hoursMinutesFractions.substring(dotPos - 2);
    try {
      final double result = Double.parseDouble(degrees) + Double.parseDouble(minutes) / 60;
      return "W".equals(whereOnGlobe) || "S".equals(whereOnGlobe) ? -result : result;
    } catch (final NumberFormatException e) {
      return 0.0;
    }

  }

  public static String replaceAll(final String original, final String tokenToBeReplaced, final String value) {
    //TODO jaanus : optimize
    final StringBuffer result = new StringBuffer();
    final String[] originalSplit = split(original, tokenToBeReplaced);
    for (int i = 0; i < originalSplit.length; i++) {
      result.append(originalSplit[i]);
      if (i != originalSplit.length - 1) {
        result.append(value);
      }
    }
    return result.toString();
  }

  public static WgsPoint parseWgsFromString(final String lon, final String lat) {
    try {
      return new WgsPoint(Double.parseDouble(lon), Double.parseDouble(lat));
    } catch (final NumberFormatException e) {
      return null;
    }
  }

  public static Reader createInputStreamReader(final byte[] data) {
    InputStreamReader reader;
    final InputStream is = new ByteArrayInputStream(data);
    try {
      reader = new InputStreamReader(is, "utf-8");
    } catch (final Exception e) {
      reader = new InputStreamReader(is);
    }

    return reader;
  }

  public static int parseInt(final String intString) {
    return parseInt(intString, 0);
  }

  public static int parseInt(final String intString, final int defaultValue) {
    try {
      return Integer.parseInt(intString);
    } catch (final NumberFormatException e) {
      return defaultValue;
    }
  }

  public static String urlEncode(final String string) {
    return Tools.urlEncode(string);
  }

  public static String toStringWithLeadingZeroes(final int number, final int totalNumbers) {
    final StringBuffer result = new StringBuffer(Integer.toString(number));
    for (int i = result.length(); i < totalNumbers; i++) {
      result.insert(0, '0');
    }
    return result.toString();
  }

  public static void closeStream(final InputStream is) {
    IOUtils.closeStream(is);
  }

  public static void closeStream(final OutputStream os) {
    IOUtils.closeStream(os);
  }

  public static void closeReader(final Reader reader) {
    IOUtils.closeReader(reader);
  }

  public static void closeRecordStore(final RecordStore rs) {
    if (rs != null) {
      try {
        rs.closeRecordStore();
      } catch (final RecordStoreException ignore) {
      }
    }
  }

  public static String readLine(final InputStream is) {
    try {
      return Tools.readLine2(is);
    } catch (final IOException e) {
      return null;
    }
  }

  public static String prepareForParameters(final String url) {
    if (url.endsWith("?") || url.endsWith("&")) {
      return url;
    }

    if (url.indexOf("?") > 0) {
      return url + "&";
    } else {
      return url + "?";
    }
  }

  public static int getResourceType(final String resourcePath) {
    final String path = resourcePath.trim().toLowerCase();
    if (path.startsWith(HTTP_PREFIX) || path.startsWith(HTTPS_PREFIX)) {
      return RESOURCE_TYPE_NETWORK;
    } else if (path.startsWith(FILE_PREFIX)) {
      return RESOURCE_TYPE_FILE;
    } else if (path.startsWith(JAR_RESOURCE_PREFIX)) {
      return RESOURCE_TYPE_JAR;
    }

    return 0;
  }
  
  /**
   * generates round polygon (circle) based on center, radius and number of points
   * Bases on spheroid with WGS84 primary axis radius (6378137 m)
   * @param center defines lat, long of center  
   * @param radius in meters
   * @param points number of required points. 360/points should be integer. About 24 looks typically fine.
   * @return array of WgsPoints. Can be used to make a line or polygon from these
   */
public static WgsPoint[] circlePoints(WgsPoint center, double radius, int points) {

      double earthsRadius = 6378137; // in meters for Wgs84
      double d2r = Math.PI / 180; // degrees to radians
      double r2d = 180 / Math.PI;

      double rLatitude = r2d * (radius / earthsRadius);
      double rLongitude = rLatitude / Math.cos(d2r * center.getLat());

      WgsPoint[] out = new WgsPoint[points + 2];

      for (int i = 0; i <= points + 1; i++) {
          double theta = Math.PI * ((double) i / ((double) points / 2));
          double pLong = center.getLon() + (rLongitude * Math.cos(theta));
          double pLat = center.getLat() + (rLatitude * Math.sin(theta));
          out[i] = new WgsPoint(pLong, pLat);

      }

      return out;
  }

}
