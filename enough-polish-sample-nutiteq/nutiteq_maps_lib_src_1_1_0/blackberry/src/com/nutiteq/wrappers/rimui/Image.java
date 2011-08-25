package com.nutiteq.wrappers.rimui;

import java.io.IOException;
import java.io.InputStream;

import com.nutiteq.utils.IOUtils;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;

/**
 * Wrapper around Bitmap class to have similar interface to J2ME Image class. 
 */
public class Image {
  private final Bitmap image;
  private final Graphics graphics;

  private Image(final Bitmap bitmap) {
    image = bitmap;
    graphics = new Graphics(new net.rim.device.api.ui.Graphics(image));
  }

  public static Image createImage(final String image) throws IOException {
    return createImage(image.getClass().getResourceAsStream(image));
  }

  public static Image createImage(final byte[] imageData, final int offset, final int length) {
    final EncodedImage encoded = EncodedImage.createEncodedImage(imageData, offset, length);
    return new Image(encoded.getBitmap());
  }

  public static Image createImage(final int imageWidth, final int imageHeight) {
    return new Image(new Bitmap(imageWidth, imageHeight));
  }

  public static Image createImage(final InputStream is) throws IOException {
    final byte[] data = IOUtils.readFully(is);
    return createImage(data, 0, data.length);
  }

  public static Image createImage(final Image source, final int x, final int y, final int width,
      final int height, final int transform) {
    final Bitmap subimage = new Bitmap(width, height);
    subimage.createAlpha(Bitmap.ALPHA_BITDEPTH_8BPP);
    int [] data = new int[width * height];       
    for(int i=0; i<data.length;i++){
      data[i] = 0x00000000;  
    }
    subimage.setARGB(data, 0, width, 0, 0, width, height);
    final net.rim.device.api.ui.Graphics graphics = new net.rim.device.api.ui.Graphics(subimage);
    graphics.drawBitmap(0, 0, width, height, source.getNativeImage(), x, y);
    return new Image(subimage);
  }
  
  public static Image createRGBImage(final int[] imageData, final int width, final int height, final boolean processAlpha){
    Bitmap bitmap = new Bitmap(width,height);
    bitmap.setARGB(imageData,0,width,0,0,width,height);
    if(processAlpha){
      bitmap.createAlpha(Bitmap.ALPHA_BITDEPTH_8BPP);
    }
    Image newImage = new Image(bitmap);
    /*
      final Bitmap tmp = new Bitmap(width,height);
      if(processAlpha){
          tmp.createAlpha(Bitmap.ALPHA_BITDEPTH_8BPP);
      }
      tmp.setARGB(imageData, 0, width, 0, 0, width, height);
      return new Image(tmp);
      */
    return newImage;
  }

  public int getWidth() {
    return image.getWidth();
  }

  public int getHeight() {
    return image.getHeight();
  }

  public Graphics getGraphics() {
    return graphics;
  }

  public void getRGB(final int[] rgbData, final int offset, final int scanlength, final int x,
      final int y, final int width, final int height) {
    image.getARGB(rgbData, offset, scanlength, x, y, width, height);
  }

  public Bitmap getNativeImage() {
    return image;
  }
}
