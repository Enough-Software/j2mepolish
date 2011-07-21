//#condition polish.api.btapi

/*
 * Created on Jul 31, 2008 at 10:53:38 PM.
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

import java.io.IOException;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.RemoteDevice;

/**
 * <p>Combines a remote devive with it's class.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BluetoothDevice
{
	private RemoteDevice device;
	private DeviceClass deviceClass;
	private int majorClass;
	private int minorClass;
	private int serviceClasses;
	/**
	 * @param device
	 * @param deviceClass
	 */
	public BluetoothDevice(RemoteDevice device, DeviceClass deviceClass)
	{
		this.device = device;
		this.deviceClass = deviceClass;
		this.majorClass = deviceClass.getMajorDeviceClass();
		this.minorClass = deviceClass.getMinorDeviceClass();
		this.serviceClasses = deviceClass.getServiceClasses();
	}
	/**
	 * @return the device
	 */
	public RemoteDevice getDevice() {
		return this.device;
	}
	
	/**
	 * @param device the device to set
	 */
	public void setDevice(RemoteDevice device)
	{
		this.device = device;
	}
	/**
	 * @return the deviceClass
	 */
	public DeviceClass getDeviceClass() {
	return this.deviceClass;}
	
	/**
	 * @param deviceClass the deviceClass to set
	 */
	public void setDeviceClass(DeviceClass deviceClass)
	{
		this.deviceClass = deviceClass;
		this.majorClass = deviceClass.getMajorDeviceClass();
		this.minorClass = deviceClass.getMinorDeviceClass();
		this.serviceClasses = deviceClass.getServiceClasses();
	}

	public int getMajorDeviceClass() {
		return this.majorClass;
	}
	public int getMinorDeviceClass() {
		return this.minorClass;
	}
	public int getServiceClasses() {
		return this.serviceClasses;
	}
	
	public String getBluetoothAddress() {
		return this.device.getBluetoothAddress();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if (o instanceof BluetoothDevice) {
			o = ((BluetoothDevice)o).device;
		}
		return this.device.equals(o);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return this.device.hashCode();
	}
	/**
	 * @return the friendly name of the device
	 * @throws IOException 
	 */
	public String getFriendlyName() throws IOException
	{
		return this.device.getFriendlyName(false);
	}
	
	
	
}
