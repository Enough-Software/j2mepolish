package com.nutiteq.helpers;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.nutiteq.bluetooth.BluetoothAPIDevice;
import com.nutiteq.bluetooth.BluetoothDevice;
import com.nutiteq.bluetooth.BluetoothHandler;
import com.nutiteq.bluetooth.DiscoveredDevice;
import com.nutiteq.core.MappingCore;
import com.nutiteq.location.LocationSourceWaiter;
import com.nutiteq.location.providers.BluetoothProvider;
import com.nutiteq.location.providers.LocationDataConnectionProvider;
import com.nutiteq.task.LocalTask;

/**
 * Generic bluetooth GPS selector implemented using J2ME high level UI elements.
 */
public class BluetoothGpsBrowser extends List implements CommandListener, LocalTask,
    BluetoothHandler {
  private final LocationSourceWaiter sourceWaiter;

  private final Command select;
  private final Command back;

  private final String searchingMessage;

  private BluetoothDevice device;

  private DiscoveredDevice[] found;

  private final String noDevicesFoundMessage;

  /**
   * Create new browser that will be shown on screen.
   * 
   * @param title
   *          form title
   * @param sourceWaiter
   *          calling class waiting for location source
   * @param searchingMessage
   *          message shown while devices search is in progress
   * @param noDevicesFoundMessage
   *          message shown if no devices found
   * @param selectCommandLabel
   *          label on select command
   * @param backCommandLabel
   *          label on back command
   */
  public BluetoothGpsBrowser(final String title, final LocationSourceWaiter sourceWaiter,
      final String searchingMessage, final String noDevicesFoundMessage,
      final String selectCommandLabel, final String backCommandLabel) {
    super(title, List.IMPLICIT);
    this.sourceWaiter = sourceWaiter;
    this.searchingMessage = searchingMessage;
    this.noDevicesFoundMessage = noDevicesFoundMessage;

    append(searchingMessage, null);

    select = new Command(selectCommandLabel, Command.OK, 0);
    back = new Command(backCommandLabel, Command.BACK, 0);

    addCommand(back);

    setCommandListener(this);

    MappingCore.getInstance().runAsync(this);
  }

  public void execute() {
    device = new BluetoothAPIDevice(this);
    device.findRemoteDevices();
  }

  public void commandAction(final Command cmd, final Displayable d) {
    if (cmd == back) {
      sourceWaiter.browsingCanceled();
    } else if (cmd == select) {
      final int index = getSelectedIndex();
      final BluetoothProvider bluetoothProvider = new BluetoothProvider(
          (LocationDataConnectionProvider) device, found[index].getUrl());
      sourceWaiter.setLocationSource(bluetoothProvider);
    }
  }

  public void remoteDevicesFound(final DiscoveredDevice[] devicesFound) {
    final Vector appended = new Vector();
    deleteAll();
    boolean added = false;
    for (int i = 0; i < devicesFound.length; i++) {
      final DiscoveredDevice discoveredDevice = devicesFound[i];
      if (discoveredDevice.getUrl() == null) {
        continue;
      }
      added = true;
      append(discoveredDevice.getName(), null);
      appended.addElement(discoveredDevice);
    }

    found = new DiscoveredDevice[appended.size()];
    appended.copyInto(found);

    if (added) {
      addCommand(select);
      setSelectCommand(select);
    } else {
      append(noDevicesFoundMessage, null);
    }
  }
}
