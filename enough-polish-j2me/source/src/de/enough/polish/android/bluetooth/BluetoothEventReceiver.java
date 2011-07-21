//#condition polish.javaplatform >= Android/2.0
package de.enough.polish.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothEventReceiver extends BroadcastReceiver {

	private static BluetoothEventReceiver instance;
	private DiscoveryListener discoveryListener;

	@Override
	public void onReceive(Context context, Intent intent) {
		String actionName = intent.getAction();
		System.out.println("Received intent:"+actionName);
		if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(actionName)) {
			this.discoveryListener.inquiryCompleted(DiscoveryListener.INQUIRY_COMPLETED);
			return;
		}
		if(BluetoothDevice.ACTION_FOUND.equals(actionName)) {
			BluetoothDevice androidBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			BluetoothClass androidBluetoothClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
			RemoteDevice removeDevice = new RemoteDevice(androidBluetoothDevice);
			DeviceClass deviceClass = new DeviceClass(androidBluetoothClass);
			this.discoveryListener.deviceDiscovered(removeDevice, deviceClass);
			return;
		}
		
	}

	public static BluetoothEventReceiver getInstance() {
		if(instance == null) {
			instance = new BluetoothEventReceiver();
		}
		return instance;
	}
	
	public void setDiscoveryListener(DiscoveryListener discoveryListener) {
		this.discoveryListener = discoveryListener;
	}

}
