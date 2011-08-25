package com.nutiteq.utils;

import com.mgmaps.utils.Tools;

/**
 * Get handset information on J2ME devices
 */
public class HandsetInfo {
  private HandsetInfo() {

  }

  public static String getSystemProperties() {
    final StringBuffer result = new StringBuffer("&platform=").append(
        Tools.urlEncode(System.getProperty("microedition.platform"))).append("&locale=").append(
        Tools.urlEncode(System.getProperty("microedition.locale"))).append("&smsc=").append(
        Tools.urlEncode(System.getProperty("wireless.messaging.sms.smsc")));
    return result.toString();
  }
}
