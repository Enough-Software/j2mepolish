//#condition polish.api.btapi
/*
 * Created on Aug 4, 2008 at 7:23:42 AM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.bluetooth;

import de.enough.polish.bluetooth.BluetoothDevice;
import de.enough.polish.bluetooth.DiscoveryHelper;
import de.enough.polish.bluetooth.ScannerListener;

/**
 * <p>Regularly discovers new bluetooth devices.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DiscoveryScanner extends Thread
{
	
	private boolean isStopRequested;
	private long interval;
	private final ScannerListener listener;
	private final int majorDeviceClass;

	public DiscoveryScanner( ScannerListener listener, long interval, int majorDeviceClass) {
		this.listener = listener;
		this.interval = interval;
		this.majorDeviceClass = majorDeviceClass;
	}
	
	public void requestStop() {
		this.isStopRequested = true;
	}
	
	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	public void run() {
		while (!this.isStopRequested) {
			try {
				BluetoothDevice[] devices = DiscoveryHelper.findDevices(DiscoveryHelper.SEARCH_MODE_GIAC, this.majorDeviceClass);
				this.listener.notifyBluetoothDevices(devices);
				Thread.sleep(this.interval);
			} catch (Exception e) {
				// ignore
			}
		}
	}

}