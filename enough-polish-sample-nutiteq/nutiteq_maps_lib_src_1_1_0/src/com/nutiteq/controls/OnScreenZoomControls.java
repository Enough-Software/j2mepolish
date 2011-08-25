package com.nutiteq.controls;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nutiteq.utils.Utils;

/**
 * <p>
 * Zoom controls to be used on touch screen. Displays images on screen and also
 * handles pressing on them.
 * </p>
 * <p>
 * Used image needs to have zoom images in one image - zoom in is first frame
 * and zoom out second frame. Frames need to be the same size.
 * </p>
 */
public class OnScreenZoomControls {
  /**
   * Default zoom controls image path for library.
   */
  public static final String DEFAULT_ZOOM_IMAGE = "/images/m-l-controlls.png";

  public static final int CONTROL_ZOOM_IN = 0;
  public static final int CONTROL_ZOOM_OUT = 1;

  private final int[][] controlPositions;

  private final Image[] images;

  private final int imageWidth;
  private final int imageHeight;

  /**
   * Create zoom controls with given image.
   * 
   * @param controlsImage
   *          image to be used
   */
  public OnScreenZoomControls(final Image controlsImage) {
    imageWidth = controlsImage.getWidth() / 2;
    imageHeight = controlsImage.getHeight();

    controlPositions = new int[][] { { 5, 5 }, { 5, 5 + imageHeight + 1 } };

    images = new Image[2];
    for (int i = 0; i < images.length; i++) {
      images[i] = Image.createImage(controlsImage, i * imageWidth, 0, imageWidth, imageHeight, 0);
    }
  }

  /**
   * Not part of public API
   * 
   * @param g
   * @param displayWidth
   * @param displayHeight
   */
  public void paint(final Graphics g, final int displayWidth, final int displayHeight) {
    //TODO jaanus : this clip can be smaller
    g.setClip(0, 0, displayWidth, displayHeight);
    for (int i = 0; i < images.length; i++) {
      g.drawImage(images[i], controlPositions[i][0], controlPositions[i][1], Graphics.TOP
          | Graphics.LEFT);
    }
  }

  /**
   * Not part of public API
   * 
   * @param x
   * @param y
   * @return action code
   */
  public int getControlAction(final int x, final int y) {
    for (int i = 0; i < controlPositions.length; i++) {
      if (Utils.rectanglesIntersect(controlPositions[i][0], controlPositions[i][1], imageWidth,
          imageHeight, x, y, 1, 1)) {
        return i;
      }
    }

    return -1;
  }
}
