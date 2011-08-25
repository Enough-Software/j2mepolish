package com.nutiteq.net;

import javax.microedition.lcdui.Image;

import com.nutiteq.cache.Cache;
import com.nutiteq.cache.ImageWaiter;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;

public class ImageWaitingDownloadable implements ResourceRequestor, ResourceDataWaiter {
  private final String iconUrl;
  private final ImageWaiter waiter;

  public ImageWaitingDownloadable(final ImageWaiter waiter, final String iconUrl) {
    this.waiter = waiter;
    this.iconUrl = iconUrl;
  }

  public String resourcePath() {
    return iconUrl;
  }

  public void notifyError() {
    //ignore
  }

  public void dataRetrieved(final byte[] data) {
    try {
      final Image image = Image.createImage(data, 0, data.length);
      waiter.imageDownloaded(iconUrl, image);
    } catch (final Exception e) {
      //ignore
    }
  }

  public int getCachingLevel() {
    return Cache.CACHE_LEVEL_MEMORY | Cache.CACHE_LEVEL_PERSISTENT;
  }
}
