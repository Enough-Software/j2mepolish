//BattleTac code
package com.nutiteq.ui;

import javax.microedition.lcdui.Image;

/**
 * Interface for Image processing, can be used to process map tiles
 * before displaying them
 */
public interface ImageProcessor {
  /**
   * Processes the input image and returns a new one with the same dimensions
   */
  Image processImage(final Image input);
}
