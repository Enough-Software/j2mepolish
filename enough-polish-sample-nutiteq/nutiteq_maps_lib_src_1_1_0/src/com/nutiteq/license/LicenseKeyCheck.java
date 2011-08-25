package com.nutiteq.license;

import com.mgmaps.utils.Tools;
import com.nutiteq.BasicMapComponent;
import com.nutiteq.cache.Cache;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.log.Log;
import com.nutiteq.utils.HandsetInfo;
import com.nutiteq.utils.Utils;

public class LicenseKeyCheck implements ResourceRequestor, ResourceDataWaiter {
  public static final String MIDLET_NAME_ATTRIBUTE = "MIDlet-Name";
  public static final String MIDLET_VENDOR_ATTRIBUTE = "MIDlet-Vendor";

  private static final String LIBRARY_VERSION = "@{app.version}";

  private static final String LICENSE_SERVER_BASEURL = "http://lbs.nutiteq.com/license.php?";

  private final String appName;
  private final String appVendor;
  private final BasicMapComponent mapComponent;
  private final String licenseKey;
  private static final int MAX_RETRY_LIMIT = 1;
  private int failureCount = 0;

  public LicenseKeyCheck(final BasicMapComponent mapComponent, final String licenseKey,
      final String appName, final String appVendor) {
    if (mapComponent == null) {
      throw new IllegalArgumentException("Map component needed!");
    }
    if (licenseKey == null || appName == null || appVendor == null) {
      mapComponent.setLicense(License.LICENSE_INVALID_DATA);
    }

    this.mapComponent = mapComponent;
    this.licenseKey = licenseKey;
    this.appName = appName;
    this.appVendor = appVendor;

    if ("".equals(appName.trim()) || "".equals(appVendor.trim()) || "".equals(licenseKey)) {
      mapComponent.setLicense(License.LICENSE_INVALID_DATA);
    }
  }

  public String resourcePath() {
    return new StringBuffer(LICENSE_SERVER_BASEURL).append("mykey=").append(
        Tools.urlEncode(licenseKey)).append("&app=").append(Tools.urlEncode(appName)).append(
        "&vendor=").append(Tools.urlEncode(appVendor)).append("&ver=").append(
        Tools.urlEncode(LIBRARY_VERSION)).append(HandsetInfo.getSystemProperties()).toString();
  }

  public void dataRetrieved(final byte[] data) {
    if (data == null || data.length == 0) {
      Log.error("License: Invalid data received!");
      mapComponent.setLicense(License.LICENSE_NETWORK_ERROR);
    } else {
      try {
        final String response = new String(data);
        final String[] split = Utils.split(response, ":");
        mapComponent.setLicense(new License(Integer.parseInt(split[0]), split[1]));
      } catch (final Exception e) {
        Log.error("Check license: " + new String(data));
        Log.printStackTrace(e);
        mapComponent.setLicense(License.LICENSE_NETWORK_ERROR);
      }
    }
  }

  public void notifyError() {
    failureCount++;
    if (failureCount <= MAX_RETRY_LIMIT) {
      mapComponent.enqueueDownload(this, Cache.CACHE_LEVEL_NONE);
    } else {
      mapComponent.setLicense(License.LICENSE_NETWORK_ERROR);
    }
  }

  public int getCachingLevel() {
    return Cache.CACHE_LEVEL_NONE;
  }
}
