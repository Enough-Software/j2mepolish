//#condition polish.api.btapi

/*
 * Created on Jul 29, 2008 at 11:02:14 PM.
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

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Arrays;

/**
 * <p>Provides easy access for finding devices and services</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DiscoveryHelper implements DiscoveryListener
{
	/** General search mode - use this mode if in doubt. */
	public static final int SEARCH_MODE_GIAC = DiscoveryAgent.GIAC;
	/** Search mode for finding devices that are only visible for a short amount of time. */
	public static final int SEARCH_MODE_LIAC = DiscoveryAgent.LIAC;
	/**  major device class of computers (desktop,notebook, PDA, organizers, .... ) */
	public static final int DEVICE_CLASS_MAJOR_PC = 0x100;
	/**  major device class of phones (cellular, cordless, payphone, modem, ...) */
	public static final int DEVICE_CLASS_MAJOR_MOBILE = 0x200;
	/**  major device class of  LAN / network access point */
	public static final int DEVICE_CLASS_MAJOR_LAN = 0x300;
	/**  major device class of audio/video (headset,speaker,stereo, video display, vcr, ...) */
	public static final int DEVICE_CLASS_MAJOR_AUDIO = 0x400;
	/**  major device class of peripherals (mouse, joystick, keyboards, ..... ) */
	public static final int DEVICE_CLASS_MAJOR_PERIPHAL = 0x500;
	/**  major device class of  imaging devices (printing, scanner, camera, display, ...)  */
	public static final int DEVICE_CLASS_MAJOR_IMAGING = 0x600;
//	/**  major device class of  other devices  */
//	public static final int DEVICE_CLASS_MAJOR_UNCATEGORIZED = 31;
	
	private static DiscoveryHelper INSTANCE;
	
	private ArrayList discoveredDevices;
	private ArrayList discoveredServices;

	private int transctionId;
	private int searchedClassMajor;
	private int searchedClassMinor;
	
	private DiscoveryHelper() {
		this.discoveredDevices = new ArrayList();
		this.discoveredServices = new ArrayList();
	}
	
	/**
	 * Finds all nearby devices using a GIAC search
	 * @return an array of all found devices, can be empty but not null
	 * @throws BluetoothStateException when there was an error during search
	 */
	public static BluetoothDevice[] findDevices() 
	throws BluetoothStateException 
	{
		return findDevices( DiscoveryAgent.GIAC, -1, -1 );
	}
	
	/**
	 * Finds all nearby devices in the specified search mode
	 * @param searchMode the search mode, either DiscoveryAgent.GIAC/DiscoveryHelper.SEARCH_MODE_GIAC or DiscoveryAgent.LIAC/DiscoveryHelper.SEARCH_MODE_LIAC. If in doubt use GIAC.
	 * @return an array of all found devices, can be empty but not null
	 * @throws BluetoothStateException when there was an error during search
	 * @see #SEARCH_MODE_GIAC
	 * @see #SEARCH_MODE_LIAC
	 */
	public static BluetoothDevice[] findDevices(int searchMode) 
	throws BluetoothStateException
	{
		return findDevices( searchMode, -1, -1 );
	}
	

	/**
	 * Finds all nearby devices with the specified major version in the specified search mode.
	 * 
	 * @param searchMode the search mode, either DiscoveryAgent.GIAC/DiscoveryHelper.SEARCH_MODE_GIAC or DiscoveryAgent.LIAC/DiscoveryHelper.SEARCH_MODE_LIAC. If in doubt use GIAC.
	 * @param deviceClassMajor the major version of device classes that should be returned, -1 if all devices should be retrieved.
	 *        To find all PC devices call findDevices( DiscoveryAgent.GIAC, DiscoveryHelper.DEVICE_CLASS_MAJOR_PC )
	 * @return an array of all found devices, can be empty but not null
	 * @throws BluetoothStateException when there was an error during search
	 * @see #SEARCH_MODE_GIAC
	 * @see #SEARCH_MODE_LIAC
	 * @see #DEVICE_CLASS_MAJOR_PC
	 */
	public static BluetoothDevice[] findDevices(int searchMode, int deviceClassMajor) 
	throws BluetoothStateException
	{
		return findDevices( searchMode, deviceClassMajor, -1 );
	}
	
	/**
	 * Finds all nearby devices of the specified major and minor types in the specified search mode.
	 * 
	 * @param searchMode the search mode, either DiscoveryAgent.GIAC/DiscoveryHelper.SEARCH_MODE_GIAC or DiscoveryAgent.LIAC/DiscoveryHelper.SEARCH_MODE_LIAC. If in doubt use GIAC.
	 * @param deviceClassMajor the major version of device classes that should be returned, -1 if all devices should be retrieved
	 * @param deviceClassMinor the minor version of device classes that should be returned, -1 if all devices should be retrieved
	 * @return an array of all found devices, can be empty but not null
	 * @throws BluetoothStateException when there was an error during search
	 * @see #SEARCH_MODE_GIAC
	 * @see #SEARCH_MODE_LIAC
	 * @see #DEVICE_CLASS_MAJOR_PC
	 */
	public static BluetoothDevice[] findDevices(int searchMode, int deviceClassMajor, int deviceClassMinor) 
	throws BluetoothStateException
	{
		checkInstance();
		return INSTANCE.findDevicesImpl( searchMode, deviceClassMajor, deviceClassMinor );
	}


	/**
	 * Finds all nearby devices in the specified search mode
	 * @param searchMode the search mode, either DiscoveryAgent.GIAC or DiscoveryAgent.LIAC. If in doubt use GIAC.
	 * @param deviceClassMajor the major version of device classes that should be returned, -1 if all devices should be retrieved
	 * @param deviceClassMinor the minor version of device classes that should be returned, -1 if all devices should be retrieved
	 * @return an array of all found devices, can be empty but not null
	 * @throws BluetoothStateException when there was an error during search
	 */
	private BluetoothDevice[] findDevicesImpl(int searchMode, int deviceClassMajor, int deviceClassMinor ) throws BluetoothStateException
	{
		this.searchedClassMajor = deviceClassMajor;
		this.searchedClassMinor = deviceClassMinor;
		LocalDevice device = LocalDevice.getLocalDevice();
		if (device == null) {
			return new BluetoothDevice[0];
		}
		DiscoveryAgent discAgent = device.getDiscoveryAgent();
		device.setDiscoverable(searchMode);
        synchronized(INSTANCE)	{
        	discAgent.startInquiry(searchMode, INSTANCE);
			// wait for results:
			try {
				INSTANCE.wait(); 
			} catch(InterruptedException e){
				// ignore
			}
        }
        BluetoothDevice[] devices = new BluetoothDevice[ INSTANCE.discoveredDevices.size() ];
        Arrays.cast( INSTANCE.discoveredDevices.getInternalArray(), devices );
        INSTANCE.discoveredDevices.clear();
        return devices;
	}

	/**
	 * Searches all nearby devices (found with GIAC) for a service with the specified UUID
	 * @param uuid the unique bluetooth identifier of the service
	 * @return the first found service or null if none is found
	 * @throws BluetoothStateException when the service search fails unexpectetly
	 */
	public static ServiceRecord findService( String uuid ) 
	throws BluetoothStateException 
	{
		return findService( uuid, DiscoveryAgent.GIAC );
	}
	
	/**
	 * Searches all nearby devices for a service with the specified UUID
	 * @param uuid the unique bluetooth identifier of the service
	 * @param searchMode the search mode for device (either DiscoveryAgent.GIAC or DiscoveryAgent.LIAC)
	 * @return the first found service or null if none is found
	 * @throws BluetoothStateException when the service search fails unexpectetly
	 */
	public static ServiceRecord findService( String uuid, int searchMode ) 
	throws BluetoothStateException 
	{
		UUID[] uuids = new UUID[]{new UUID( uuid, false )};
		int[] attributes = null;
		return findService(uuids, attributes, searchMode);
	}

	/**
	 * Searches all specified devices for a service with the specified UUID
	 * @param uuid the unique bluetooth identifier of the service
	 * @param devices the devices that should be searched for the service
	 * @return the first found service or null if none is found
	 * @throws BluetoothStateException when the service search fails unexpectetly
	 */
	public static ServiceRecord findService( String uuid, RemoteDevice[] devices ) 
	throws BluetoothStateException 
	{
		return findService( uuid,  -1, devices );
	}
	

	/**
	 * Searches first for devices and then for a service with the specified UUID
	 * @param uuid the unique bluetooth identifier of the service
	 * @param searchMode the search mode, either DiscoveryAgent.GIAC/DiscoveryHelper.SEARCH_MODE_GIAC or DiscoveryAgent.LIAC/DiscoveryHelper.SEARCH_MODE_LIAC. If in doubt use GIAC.
	 * @param deviceClassMajor the major version of device classes that should be returned, -1 if all devices should be retrieved.
	 *        To find all PC devices call findDevices( DiscoveryAgent.GIAC, DiscoveryHelper.DEVICE_CLASS_MAJOR_PC )
	 * @return the first found service or null if none is found
	 * @throws BluetoothStateException when the service search fails unexpectetly
	 */
	public static ServiceRecord findService( String uuid, int searchMode, int deviceClassMajor ) 
	throws BluetoothStateException 
	{
		BluetoothDevice[] devices = findDevices(searchMode, deviceClassMajor);
		return findService( uuid, devices );
	}
	
	/**
	 * Searches first for devices, then for a service with the specified UUID. When a service is found, a connection is made.
	 * @param uuid the unique bluetooth identifier of the service
	 * @param searchMode the search mode, either DiscoveryAgent.GIAC/DiscoveryHelper.SEARCH_MODE_GIAC or DiscoveryAgent.LIAC/DiscoveryHelper.SEARCH_MODE_LIAC. If in doubt use GIAC.
	 * @param deviceClassMajor the major version of device classes that should be returned, -1 if all devices should be retrieved.
	 *        To find all PC devices call findDevices( DiscoveryAgent.GIAC, DiscoveryHelper.DEVICE_CLASS_MAJOR_PC )
	 * @return the first found connection service or null if no service is found; depending on the protocol this can be a L2CAPConnection or a StreamConnection (for SPP/RFComm)
	 * @throws BluetoothStateException when the service search fails unexpectetly
	 */
	public static Connection findAndConnectService( String uuid, int searchMode, int deviceClassMajor) 
	throws BluetoothStateException 
	{
		ServiceRecord record = findService(uuid, searchMode, deviceClassMajor);
		if (record == null) {
			return null;
		}
		try
		{
			String url = record.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
			Connection con = Connector.open( url, Connector.READ_WRITE );
			return con;
		} catch (IOException e)
		{
			throw new BluetoothStateException( e.toString() );
		}
	}
	
	/**
	 * Searches first for devices, then for a L2CAP service with the specified UUID. When a service is found, a connection is made.
	 * @param uuid the unique bluetooth identifier of the service
	 * @param searchMode the search mode, either DiscoveryAgent.GIAC/DiscoveryHelper.SEARCH_MODE_GIAC or DiscoveryAgent.LIAC/DiscoveryHelper.SEARCH_MODE_LIAC. If in doubt use GIAC.
	 * @param deviceClassMajor the major version of device classes that should be returned, -1 if all devices should be retrieved.
	 *        To find all PC devices call findDevices( DiscoveryAgent.GIAC, DiscoveryHelper.DEVICE_CLASS_MAJOR_PC )
	 * @param useAndStoreConnectionUrl true when the last successfull connection URL should be stored - this will then be used for trying to connect the next time before repeating the search.
	 * @return the first found connection service or null if no service is found; depending on the protocol this can be a L2CAPConnection or a StreamConnection (for SPP/RFComm)
	 * @throws BluetoothStateException when the service search fails unexpectetly
	 */
	public static Connection findAndConnectService( String uuid, int searchMode, int deviceClassMajor, boolean useAndStoreConnectionUrl) 
	throws BluetoothStateException 
	{
		if (useAndStoreConnectionUrl) {
			RecordStore store = null;
			try {
				store = RecordStore.openRecordStore(uuid, false);
				RecordEnumeration recEnum = store.enumerateRecords(null, null, false);
				String url = new String( recEnum.nextRecord() );
				Connection con = Connector.open( url, Connector.READ_WRITE );
				return con;
			} catch (Exception e) {
				// ignore and continue to search for service and connect
			} finally {
				if (store != null) {
					try {
						store.closeRecordStore();
					} catch (Exception e) {
						// ignore
					}
				}
			}
		}
		ServiceRecord record = findService(uuid, searchMode, deviceClassMajor);
		if (record == null) {
			//#debug
			System.out.println("No service found for " + uuid);
			return null;
		}
		try
		{
			String url = record.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
			Connection con = Connector.open( url, Connector.READ_WRITE );
			if (useAndStoreConnectionUrl) {
				RecordStore store = null;
				try {
					store = RecordStore.openRecordStore(uuid, true);
					byte[] data = url.getBytes();
					if (store.getNumRecords() == 0) {
						store.addRecord( data, 0, data.length );
					} else {
						RecordEnumeration recEnum = store.enumerateRecords(null, null, false);
						int id = recEnum.nextRecordId();
						store.setRecord(id, data, 0, data.length);
					}
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to store last connection URL." + e);
				} finally {
					if (store != null) {
						try {
							store.closeRecordStore();
						} catch (Exception e) {
							// ignore
						}
					}
				}
			}
			//#debug 
			System.out.println("Connection established for " + uuid);
			return con;
		} catch (IOException e)
		{
			throw new BluetoothStateException( e.toString() );
		}
	}
	

	/**
	 * Searches all specified devices for a service with the specified UUID
	 * @param uuid the unique bluetooth identifier of the service
	 * @param devices the devices that should be searched for the service
	 * @return the first found service or null if none is found
	 * @throws BluetoothStateException when the service search fails unexpectetly
	 */
	public static ServiceRecord findService( String uuid, BluetoothDevice[] devices ) 
	throws BluetoothStateException 
	{
		return findService( uuid,  -1, devices );
	}

	
	/**
	 * Searches all nearby devices for a service with the specified UUID
	 * @param uuid the unique bluetooth identifier of the service
	 * @param attribute the attribute that should be queried, use -1 for not searching for any attributes
	 * @param devices the devices that should be searched for the service
	 * @return the first found service or null if none is found
	 * @throws BluetoothStateException when the service search fails unexpectetly
	 */
	public static ServiceRecord findService( String uuid, int attribute, RemoteDevice[] devices ) 
	throws BluetoothStateException 
	{
		UUID[] uuids = new UUID[]{new UUID( uuid, false )};
		int[] attributes = null;
		if (attribute != -1) {
			attributes = new int[]{ attribute };
		}
		return findService(uuids, attributes, devices );
	}
	
	/**
	 * Searches all nearby devices for a service with the specified UUID
	 * @param uuid the unique bluetooth identifier of the service
	 * @param attribute the attribute that should be queried, use -1 for not searching for any attributes
	 * @param devices the devices that should be searched for the service
	 * @return the first found service or null if none is found
	 * @throws BluetoothStateException when the service search fails unexpectetly
	 */
	public static ServiceRecord findService( String uuid, int attribute, BluetoothDevice[] devices ) 
	throws BluetoothStateException 
	{
		UUID[] uuids = new UUID[]{new UUID( uuid, false )};
		int[] attributes = null;
		if (attribute != -1) {
			attributes = new int[]{ attribute };
		}
		return findService(uuids, attributes, devices );
	}
	
	
	/**
	 * Searches all nearby devices for a service with the specified UUID
	 * @param ids the unique bluetooth identifiers of the service
	 * @param attributes the attributes that should be queried, use null for not searching for any attributes
	 * @param searchMode the search mode for device (either DiscoveryAgent.GIAC or DiscoveryAgent.LIAC)
	 * @return the first found service or null if none is found
	 * @throws BluetoothStateException when the service search fails unexpectetly
	 */
	public static ServiceRecord findService( UUID[] ids, int[] attributes, int searchMode ) 
	throws BluetoothStateException 
	{
		return findService(ids, attributes, findDevices( searchMode ) );
	}
	
	/**
	 * Searches all nearby devices for a service with the specified UUID
	 * @param ids the unique bluetooth identifiers of the service
	 * @param attributes the attributes that should be queried, use null for not searching for any attributes
	 * @param devices the devices that should be searched for the service
	 * @return the first found service or null if none is found
	 * @throws BluetoothStateException when the service search fails unexpectetly
	 */
	public static ServiceRecord findService( UUID[] ids, int[] attributes, RemoteDevice[] devices ) 
	throws BluetoothStateException 
	{
		checkInstance();
		return INSTANCE.findServicesImpl( ids, attributes, devices );
	}

	/**
	 * Searches all nearby devices for a service with the specified UUID
	 * @param ids the unique bluetooth identifiers of the service
	 * @param attributes the attributes that should be queried, use null for not searching for any attributes
	 * @param devices the devices that should be searched for the service
	 * @return the first found service or null if none is found
	 * @throws BluetoothStateException when the service search fails unexpectetly
	 */
	public static ServiceRecord findService( UUID[] ids, int[] attributes, BluetoothDevice[] devices ) 
	throws BluetoothStateException 
	{
		checkInstance();
		return INSTANCE.findServicesImpl( ids, attributes, devices );
	}


	/**
	 * Searches all nearby devices for a service with the specified UUID
	 * @param ids the unique bluetooth identifiers of the service
	 * @param attributes the attributes that should be queried, use null for not searching for any attributes
	 * @param devices the devices that should be searched for the service
	 * @return the first found service or null if none is found
	 * @throws BluetoothStateException when the service search fails unexpectetly
	 */
	private ServiceRecord findServicesImpl(UUID[] ids, int[] attributes, Object[] devices) throws BluetoothStateException
	{
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		if (localDevice == null) {
			return null;
		}
		DiscoveryAgent discAgent = localDevice.getDiscoveryAgent();
		for (int i = 0; i < devices.length; i++)
		{
			Object obj = devices[i];
			RemoteDevice remoteDevice = (obj instanceof RemoteDevice ? (RemoteDevice)obj : ((BluetoothDevice)obj).getDevice());
			this.transctionId = discAgent.searchServices(attributes, ids, remoteDevice, this);
			synchronized (this) {
				try {
					wait();
				} catch(InterruptedException e){
					// ignore
				}
			}
			if (this.discoveredServices.size() > 0) {
				break;
			}
		}
		if (this.discoveredServices.size() > 0) {
			ServiceRecord serviceRecord = (ServiceRecord) this.discoveredServices.get(0);
			this.discoveredServices.clear();
			return serviceRecord;
		}
		return null;

	}

	/**
	 * 
	 */
	private static void checkInstance()
	{
		if (INSTANCE == null) {
			INSTANCE = new DiscoveryHelper();
		}
		
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#deviceDiscovered(javax.bluetooth.RemoteDevice, javax.bluetooth.DeviceClass)
	 */
	public void deviceDiscovered(RemoteDevice device, DeviceClass deviceClass)
	{
		if (this.searchedClassMajor != -1 && deviceClass.getMajorDeviceClass() != this.searchedClassMajor) {
			return;
		}
		if (this.searchedClassMinor != -1 && deviceClass.getMinorDeviceClass() != this.searchedClassMinor) {
			return;
		}
		this.discoveredDevices.add( new BluetoothDevice( device, deviceClass) );
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#inquiryCompleted(int)
	 */
	public void inquiryCompleted(int discType)
	{
		synchronized (this) {
			this.notify();
		}

	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#serviceSearchCompleted(int, int)
	 */
	public void serviceSearchCompleted(int transID, int respCode)
	{
		synchronized (this) {
			this.notify();
		}
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#servicesDiscovered(int, javax.bluetooth.ServiceRecord[])
	 */
	public void servicesDiscovered(int transId, ServiceRecord[] services)
	{
		for (int i = 0; i < services.length; i++)
		{
			ServiceRecord serviceRecord = services[i];
			this.discoveredServices.add(serviceRecord);
		}

	}


}
