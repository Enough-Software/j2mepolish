package com.nutiteq.io;

import java.io.IOException;
import java.io.InputStream;

import com.nutiteq.log.Log;
import com.nutiteq.task.Task;
import com.nutiteq.utils.IOUtils;

public class RetrieveJarResourceTask implements Task {
  private final ResourceRequestor resourceRequestor;

  public RetrieveJarResourceTask(final ResourceRequestor resourceRequestor) {
    this.resourceRequestor = resourceRequestor;
  }

  public void execute() {
    Log.debug("Read " + resourceRequestor.resourcePath());
    InputStream is = null;

    try {
      String resourcePath = resourceRequestor.resourcePath();
      if (resourcePath.indexOf('?') > 0) {
        //some servis appending parameters (kml reader)
        resourcePath = resourcePath.substring(0, resourcePath.indexOf('?'));
      }

      is = getClass().getResourceAsStream(resourcePath);
      //TODO jaanus : check this
      if (is == null) {
        resourceRequestor.notifyError();
      } else if (resourceRequestor instanceof ResourceDataWaiter) {
        final byte[] data = IOUtils.readFully(is);
        ((ResourceDataWaiter) resourceRequestor).dataRetrieved(data);
      } else if (resourceRequestor instanceof ResourceStreamWaiter) {
        //TODO jaanus : check this
        ((ResourceStreamWaiter) resourceRequestor).streamOpened(is, null, null);
      }
    } catch (final IOException e) {
      Log.error("Retrieve jar resource '" + resourceRequestor.resourcePath() + "': "
          + e.getMessage());
      resourceRequestor.notifyError();
    } finally {
      IOUtils.closeStream(is);
    }
  }
}
