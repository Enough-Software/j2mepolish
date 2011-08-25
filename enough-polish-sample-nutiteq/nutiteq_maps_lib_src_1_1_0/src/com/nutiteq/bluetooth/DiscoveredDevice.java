package com.nutiteq.bluetooth;

/**
 * Device discovered by {@link BluetoothDevice}.
 */
public class DiscoveredDevice {
  private final String name;
  private final String url;

  /**
   * @param name
   *          devices friendly name
   * @param url
   *          URL for searched service
   */
  public DiscoveredDevice(final String name, final String url) {
    this.name = name;
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }
}
