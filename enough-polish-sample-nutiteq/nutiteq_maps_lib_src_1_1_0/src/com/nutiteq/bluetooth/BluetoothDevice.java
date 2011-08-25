package com.nutiteq.bluetooth;

/**
 * Bluetooth device used for communication with other devices.
 */
public interface BluetoothDevice {
  /**
   * UUID for serial port
   */
  long UUID_SERIALPORT = 0x1101;

  /**
   * Find devices supporting
   */
  void findRemoteDevices();
}
