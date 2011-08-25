//BattleTac code
package com.nutiteq.ui;

import javax.microedition.lcdui.Image;

import com.nutiteq.log.Log;

/**
 * This class processes tile images so that displaying them will
 * preserve the night vision of the user: The images will be
 * monochrome red with smaller contrast.
 *
 * The current implementation also inverts the image so that darker
 * original colors will be displayed using lighter reds. This fits
 * to most of the maps with darker routes and labels.
 *
 * @author Krisztian Schaffer
 */
public class NightModeImageProcessor implements ImageProcessor {

  public NightModeImageProcessor() {
  }

  public Image processImage(Image input) {
    Image retval = input;
    try {
      int width = input.getWidth();
      int height = input.getHeight();
      final int[] buf = new int[width * height];
      input.getRGB(buf, 0, width, 0, 0, width, height);
      for (int j = buf.length - 1; j >= 0; --j) {
        int oldValue = buf[j];
        buf[j] = 0xff000000 | // transparency off (needed for BB)
          ((820 - // the constant makes balance between 768 (3*256) and 1024 (4*256) because we divide by 4 instead of 3 so we lost some contrast
              ((oldValue & 0xff) + ((oldValue & 0xff00) >> 8) + ((oldValue & 0xff0000) >> 16)))//Sum of rgb: the brightness of the pixel, inverted.
              >> 2) // should be /3
              << 16; //shift up to the red component to get monochrome red result
      }
      retval = Image.createRGBImage(buf, width, height, false);
    } catch (Exception e) {
      Log.printStackTrace(e); 
    }
    return retval;
  }
}
