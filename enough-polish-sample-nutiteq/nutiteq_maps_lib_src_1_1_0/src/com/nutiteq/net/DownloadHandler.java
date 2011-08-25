package com.nutiteq.net;

import com.nutiteq.io.ResourceRequestor;

public interface DownloadHandler {
  void enqueueDownload(final ResourceRequestor downloadable, final int cacheLevel);
}
