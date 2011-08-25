package com.nutiteq.cache;

import javax.microedition.lcdui.Image;

public interface ImageWaiter {
  void imageDownloaded(final String url, final Image image);
}
