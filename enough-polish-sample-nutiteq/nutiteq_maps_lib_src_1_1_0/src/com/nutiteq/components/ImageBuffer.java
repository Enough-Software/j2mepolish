package com.nutiteq.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nutiteq.utils.Utils;

public class ImageBuffer {
  private Image[] bufferImages;
  private Graphics[] bufferGraphics;
  private int front;

  public ImageBuffer(final int numberOfImages, final int imageWidth, final int imageHeight) {
    bufferImages = new Image[numberOfImages];
    bufferGraphics = new Graphics[numberOfImages];
    for (int i = 0; i < bufferImages.length; i++) {
      bufferImages[i] = Image.createImage(imageWidth, imageHeight);
      bufferGraphics[i] = bufferImages[i].getGraphics();
    }
  }

  public Image getFrontImage() {
    return bufferImages[front];
  }

  public Graphics getBackGraphics() {
    return bufferGraphics[nextValue(front)];
  }

  public Graphics getFrontGraphics() {
    return bufferGraphics[front];
  }

  public void flip() {
    front = nextValue(front);
  }

  private int nextValue(final int bufferIndex) {
    final int result = bufferIndex + 1;
    return result < bufferImages.length ? result : 0;
  }

  public void resize(final int newWidth, final int newHeight) {
    for (int i = 0; i < bufferImages.length; i++) {
      bufferImages[i] = Utils.resizeImageAndCopyPrevious(newWidth, newHeight, bufferImages[i]);
      bufferGraphics[i] = bufferImages[i].getGraphics();
    }
  }

  public void clean() {
    bufferImages = null;
    bufferGraphics = null;
  }
}
