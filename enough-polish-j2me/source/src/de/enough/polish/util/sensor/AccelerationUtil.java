//#condition polish.api.sensor
/*
 * Created on Mar 15, 2008 at 9:02:47 AM.
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
package de.enough.polish.util.sensor;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.sensor.ChannelInfo;
import javax.microedition.sensor.Data;
import javax.microedition.sensor.DataListener;
import javax.microedition.sensor.MeasurementRange;
import javax.microedition.sensor.SensorConnection;
import javax.microedition.sensor.SensorInfo;
import javax.microedition.sensor.SensorManager;

import de.enough.polish.util.ArrayList;

/**
 * <p>Helps to deal with an acceleration sensor when polish.api.sensor is supported by the device.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AccelerationUtil implements Runnable, DataListener
{
	private static AccelerationUtil instance;
	private final boolean isAccelerationSupported;
	private final ArrayList listeners;
	private final SensorInfo accelerationSensorInfo;
	private SensorConnection accelerationSensorConnection;
	private int minimumX;
	private int maximumX;
	private int minimumY;
	private int maximumY;
	private int minimumZ;
	private int maximumZ;

	/**
	 * Disallow direct instantiation
	 */
	private AccelerationUtil()
	{
		SensorInfo[] sensors = SensorManager.findSensors("acceleration", SensorInfo.CONTEXT_TYPE_USER);
		SensorInfo accelerationInfo = null;
		for (int i = 0; i < sensors.length; i++)
		{
			SensorInfo sensor = sensors[i];
			ChannelInfo[] channels = sensor.getChannelInfos();
			if (channels.length == 3) {
				boolean channelsValid = true;
				for (int j = 0; j < channels.length; j++)
				{
					ChannelInfo info = channels[j];
					if (info.getDataType() != ChannelInfo.TYPE_INT) {
						channelsValid = false;
					} else {
						try {
							MeasurementRange range = info.getMeasurementRanges()[0];
							switch (j) {
							case 0:
								this.minimumX = (int)range.getSmallestValue();
								this.maximumX = (int)range.getLargestValue();
								break;
							case 1:
								this.minimumY = (int)range.getSmallestValue();
								this.maximumY = (int)range.getLargestValue();
								break;
							case 2:
								this.minimumZ = (int)range.getSmallestValue();
								this.maximumZ = (int)range.getLargestValue();
								break;
							}
						} catch (Exception e) {
							// e.g. IndexOutOfBounds
							channelsValid = false;
						}
					}
				}
				if (channelsValid) {
					accelerationInfo = sensor;
					break;
				}
			}
		}
		this.isAccelerationSupported = (accelerationInfo != null);
		this.listeners = this.isAccelerationSupported ? new ArrayList() : null;
		this.accelerationSensorInfo = accelerationInfo;
	}

	/**
	 * Determines if the acceleration sensor is supported on this device.
	 * @return true when the acceleration sensor is supported. The user might still need to allow the usage on some systems.
	 */
	public static boolean isAccelerationSensorSupported() {
		if (instance == null) {
			instance = new AccelerationUtil();
		}
		return instance.isAccelerationSupported;
	}

	/**
	 * Adds an acceleration listener
	 * @param listener the listener
	 * @return true when the acceleration sensor is supported on this device 
	 */
	public static boolean addAccelerationListener( AccelerationListener listener ) 
	{
		if (instance == null) {
			instance = new AccelerationUtil();
		}
		if (!instance.isAccelerationSupported) {
			return false;
		}
		instance.addAccelerationListenerImpl(listener);
		return true;
	}
	
	/**
	 * Removes a previously registered acceleration listener
	 * @param listener the listener
	 */
	public static void removeAccelerationListener( AccelerationListener listener ) 
	{
		if (instance != null) {
			instance.removeAccelerationListenerImpl(listener);
		}
	}

	/**
	 * Adds a listener
	 * @param listener the listener to be added
	 */
	private void addAccelerationListenerImpl( AccelerationListener listener ) {
		this.listeners.add(listener);
		if (this.accelerationSensorConnection == null) {
			(new Thread(this)).start();
		}
	}
	

	/**
	 * Removes a listener
	 * @param listener the listener to be removed
	 */
	private void removeAccelerationListenerImpl(AccelerationListener listener)
	{
		this.listeners.remove(listener);
		if (this.listeners.size() == 0 && this.accelerationSensorConnection != null) {
			try
			{
				this.accelerationSensorConnection.close();
			} catch (IOException e)
			{
				// ignore
			}
			this.accelerationSensorConnection = null;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		// is used to create a connection to the sensor:
		try
		{
			this.accelerationSensorConnection = (SensorConnection) Connector.open( this.accelerationSensorInfo.getUrl() );
			this.accelerationSensorConnection.setDataListener(this, 1);
		} 
		catch (IOException e)
		{
			//#debug error
			System.out.println("Unable to establish connection to " + this.accelerationSensorInfo.getUrl() + e );
		}
		
	}

	/* (non-Javadoc)
	 * @see javax.microedition.sensor.DataListener#dataReceived(javax.microedition.sensor.SensorConnection, javax.microedition.sensor.Data[], boolean)
	 */
	public void dataReceived(SensorConnection con, Data[] datas, boolean isDataLost)
	{
		if (datas.length == 3) {
			Data data = datas[0];
			// this should be x, y and z values:
			int x = data.getIntValues()[0];
			int y = datas[1].getIntValues()[0];
			int z = datas[2].getIntValues()[0];
			Object[] listenersArray = this.listeners.getInternalArray();
			for (int i = 0; i < listenersArray.length; i++)
			{
				AccelerationListener listener = (AccelerationListener) listenersArray[i];
				if (listener == null) {
					break;
				}
				listener.notifyAcceleration( x, this.minimumX, this.maximumX, y, this.minimumY, this.maximumY, z, this.minimumZ, this.maximumZ );
			}
		}
//		for (int i = 0; i < datas.length; i++)
//		{
//			Data data = datas[i];
//			
//		}
		// TODO robertvirkus implement dataReceived
		
	}
}
