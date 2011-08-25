package com.nutiteq.utils;

import net.rim.device.api.system.DeviceInfo;

import com.mgmaps.utils.Tools;

//TODO jaanus : handle this better. in IDE gives errors
public class HandsetInfo {
  private HandsetInfo() {

  }

  public static String getSystemProperties() {
    final StringBuffer result = new StringBuffer();
    result.append("&platform=")
        .append(Tools.urlEncode(System.getProperty("microedition.platform")));
    result.append("&locale=").append(Tools.urlEncode(System.getProperty("microedition.locale")));
    result.append("&smsc=").append(
        Tools.urlEncode(System.getProperty("wireless.messaging.sms.smsc")));
    result.append("&bb_device=").append(Tools.urlEncode(DeviceInfo.getDeviceName()));
    result.append("&bb_platform=").append(Tools.urlEncode(DeviceInfo.getPlatformVersion()));
    result.append("&bb_simulator=").append(DeviceInfo.isSimulator());
    result.append("&bb_manufacturer=").append(Tools.urlEncode(DeviceInfo.getManufacturerName()));
    result.append("&bb_pin=").append(DeviceInfo.getDeviceId());
    return result.toString();
  }
}