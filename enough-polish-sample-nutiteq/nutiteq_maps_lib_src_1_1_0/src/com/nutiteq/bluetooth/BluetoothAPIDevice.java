package com.nutiteq.bluetooth;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;

import com.nutiteq.location.providers.LocationDataConnection;
import com.nutiteq.location.providers.LocationDataConnectionProvider;
import com.nutiteq.log.Log;

/**
 * Bluetooth devices implemented on top of Bluetooth API (JSR-82)
 */
public class BluetoothAPIDevice implements BluetoothDevice, DiscoveryListener,
    LocationDataConnectionProvider {
  public static final int BLUETOOTH_TIMEOUT = 30000;

  private final Vector btDevicesFound = new Vector();
  public Vector btServicesFound = new Vector();
  private boolean isBTSearchComplete;

  private final BluetoothHandler handler;

  /**
   * Create new browser with caller, that could handle found devices.
   * 
   * @param handler
   *          handler for receiving found devices
   */
  public BluetoothAPIDevice(final BluetoothHandler handler) {
    this.handler = handler;
  }

  /**
   * Find all remote devices supporting serial port connection and report them
   * to {@link BluetoothHandler}.
   */
  public void findRemoteDevices() {
    findDevices();
    findServices(new UUID[] { new UUID(UUID_SERIALPORT) });
  }

  private void findDevices() {
    try {
      // cleans previous elements
      btDevicesFound.removeAllElements();
      isBTSearchComplete = false;
      final LocalDevice local = LocalDevice.getLocalDevice();
      final DiscoveryAgent discoveryAgent = local.getDiscoveryAgent();
      // discover new devices
      discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
      while (!isBTSearchComplete) {
        // waits for a fixed time, to avoid long search
        synchronized (this) {
          this.wait(BLUETOOTH_TIMEOUT);
        }
        // check if search is completed
        if (!isBTSearchComplete) {
          // search no yet completed so let's cancel it
          discoveryAgent.cancelInquiry(this);
        }
      }
    } catch (final Exception e) {
      Log.error("BT: find " + e.getMessage());
      Log.printStackTrace(e);
    }
  }

  private void findServices(final UUID[] aServices) {
    btServicesFound.removeAllElements();
    try {
      final LocalDevice local = LocalDevice.getLocalDevice();
      final DiscoveryAgent discoveryAgent = local.getDiscoveryAgent();
      // discover services
      if (btDevicesFound.size() > 0) {
        for (int i = 0; i < btDevicesFound.size(); i++) {
          isBTSearchComplete = false;
          // adds a null element in case we don't find service
          btServicesFound.addElement(null);
          final int transID = discoveryAgent.searchServices(null, aServices,
              (RemoteDevice) (btDevicesFound.elementAt(i)), this);
          // wait for service discovery ends
          synchronized (this) {
            this.wait(BLUETOOTH_TIMEOUT);
          }
          if (!isBTSearchComplete) {
            discoveryAgent.cancelServiceSearch(transID);
          }
        }
      }

      final DiscoveredDevice[] devices = new DiscoveredDevice[btDevicesFound.size()];
      for (int i = 0; i < devices.length; i++) {
        String name = null;
        String url = null;
        try {
          name = ((RemoteDevice) btDevicesFound.elementAt(i)).getFriendlyName(false);
          final ServiceRecord elementAt = (ServiceRecord) btServicesFound.elementAt(i);
          if (elementAt != null) {
            url = elementAt.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
          }
        } catch (final IOException e) {
          Log.printStackTrace(e);
        }
        devices[i] = new DiscoveredDevice(name, url);
      }

      handler.remoteDevicesFound(devices);
    } catch (final Exception e) {
      Log.printStackTrace(e);
      Log.error("findServices: " + e.getMessage());
    }
  }

  public void deviceDiscovered(final RemoteDevice remoteDevice, final DeviceClass dClass) {
    try {
      Log.debug(remoteDevice.getFriendlyName(false));
    } catch (final IOException e) {
    }
    btDevicesFound.addElement(remoteDevice);
  }

  public void inquiryCompleted(final int discType) {
    isBTSearchComplete = true;
    // notifies and wake main thread that device search is completed
    synchronized (this) {
      this.notify();
    }
  }

  public void serviceSearchCompleted(final int transId, final int respCode) {
    isBTSearchComplete = true;
    // notifies and wake mains thread that service search is completed
    synchronized (this) {
      this.notify();
    }
  }

  public void servicesDiscovered(final int param, final ServiceRecord[] serviceRecord) {
    final int index = btServicesFound.size() - 1;
    for (int i = 0; i < serviceRecord.length; i++) {
      btServicesFound.setElementAt(serviceRecord[i], index);
    }
  }

  public LocationDataConnection openConnection(final String url) throws IOException {
    return new BluetoothAPIConnection(Connector.open(url));
  }
}
