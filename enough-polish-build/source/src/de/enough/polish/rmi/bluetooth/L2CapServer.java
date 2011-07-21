/*
 * Created on Aug 4, 2008 at 10:00:59 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.rmi.bluetooth;

import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.L2CAPConnectionNotifier;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;

/**
 * <p>Waits for incoming L2CAP connections.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class L2CapServer extends Thread
{

	private boolean stopRequested;
	private final String url;
	private L2CapRemoteService service;
	
	/**
	 * Generates the URL that fits for the given UUID
	 * @param uuid the UUID
	 * @return the local connection URL for establishing an L2CAP based service
	 * @throws BluetoothStateException when the URL could not be generated
	 */
	public static String generateLocalUrl( String uuid ) throws BluetoothStateException {
		LocalDevice device = LocalDevice.getLocalDevice();
		device.setDiscoverable(DiscoveryAgent.GIAC);
		String url = "btl2cap://localhost:" + uuid;
		String max = LocalDevice.getProperty("bluetooth.l2cap.receiveMTU.max");
		if (max != null) {
			if (max.equals("0")) {
				max = "762";
			}
			 url += ";ReceiveMTU=" + max + ";TransmitMTU=" + max;
		}
		return url;
	}

	/**
	 * Creates a new L2CAP Server
	 * @param service the service
	 * @param url the connection URL
	 * 
	 */
	public L2CapServer( L2CapRemoteService service, String url )
	{
		this.service = service;
		this.url = url;
	}
	
	/** Waits for incoming connections */
	public void run() {
		while (!this.stopRequested) {
            try
			{
				L2CAPConnectionNotifier notifier = (L2CAPConnectionNotifier)Connector.open(this.url);
				L2CAPConnection connection = notifier.acceptAndOpen();
				this.service.process( connection );
			} catch (IOException e)
			{
				this.service.handleError( this, e );
			}

		}
	}

	
	/**
	 * Requests to stop this server at the next possible moment
	 */
	public void requestStop() {
		this.stopRequested = true;
	}
}
