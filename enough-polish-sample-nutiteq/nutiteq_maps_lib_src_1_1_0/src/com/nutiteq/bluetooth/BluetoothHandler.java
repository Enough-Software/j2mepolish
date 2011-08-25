package com.nutiteq.bluetooth;

/**
 * Interface for classes dealing with bluetooth devices.
 */
public interface BluetoothHandler {
  /**
   * Give found devices to handler class
   * 
   * @param devicesFound
   *          devices found by bluetooth device
   */
  void remoteDevicesFound(final DiscoveredDevice[] devicesFound);
}
