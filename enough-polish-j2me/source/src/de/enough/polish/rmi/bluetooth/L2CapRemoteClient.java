//#condition polish.api.btapi && (polish.usePolishGui || polish.midp)
/*
 * Created on Nov 8, 2008 at 12:15:46 AM.
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
package de.enough.polish.rmi.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.L2CAPConnection;
import javax.microedition.io.Connection;

import de.enough.polish.bluetooth.DiscoveryHelper;
import de.enough.polish.bluetooth.L2CapStreamConnection;
import de.enough.polish.rmi.RemoteClient;
import de.enough.polish.rmi.RemoteException;

/**
 * <p>Allows to access L2CAP bluetooth RMI servers. 
 *     Set the preprocessing <code>polish.rmi.l2cap</code> to <code>true</code> to enable the usage of L2CAP for RMI.
 * </p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class L2CapRemoteClient 
extends RemoteClient
implements BluetoothRemoteClient
{
	
	protected L2CapStreamConnection connection;
	private boolean isConnecting;

	/**
	 * Creates a new L2CAP client.
	 * 
	 * @param uuid the UUID of the service
	 */
	public L2CapRemoteClient(String uuid)
	{
		super(uuid);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.rmi.RemoteClient#callMethod(java.lang.String, long, java.lang.Object[])
	 */
	protected Object callMethod(String name, long primitivesFlag,
			Object[] parameters) throws RemoteException
	{
		if (this.connection == null) {
			try
			{
				connect();
			} catch (Exception e)
			{
				//#debug error
				System.out.println("Unable to establish bluetooth connection" + e);
				throw new RemoteException( e );
			}
		}
		DataOutputStream out = null;
		DataInputStream in = null; 
		try
		{
			out = this.connection.openDataOutputStream();
			writeMethodParameters(name, primitivesFlag, parameters, out);
			out.flush();
			in = this.connection.openDataInputStream();
			return readResponse(in);
		} catch (IOException e)
		{
			//#debug error
			System.out.println("Unable to write/read method call" + e);
			try {
				this.connection.close();
			} catch (Exception e2) {
				// ignore
			}
			this.connection = null;
			throw new RemoteException( e );
		} finally 
		{
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					// ignore
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.rmi.bluetooth.BluetoothRemoteClient#connect()
	 */
	public void connect() throws IOException
	{
		if (this.isConnecting) {
			return;
		}
		this.isConnecting = true;
		try {
			L2CAPConnection con = (L2CAPConnection) DiscoveryHelper.findAndConnectService(this.url, DiscoveryHelper.SEARCH_MODE_GIAC, DiscoveryHelper.DEVICE_CLASS_MAJOR_PC, true);
			if (con == null) {
				throw new IOException(this.url + " not found");
			}
			this.connection = new L2CapStreamConnection( con );
		} finally {
			this.isConnecting = false;
		}		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.rmi.bluetooth.BluetoothRemoteClient#disconnect()
	 */
	public void disconnect() throws IOException
	{
		if (this.connection != null) {
			this.connection.close();
			this.connection = null;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.rmi.bluetooth.BluetoothRemoteClient#getConnection()
	 */
	public Connection getConnection()
	{
		return this.connection;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.rmi.bluetooth.BluetoothRemoteClient#setConnection(javax.microedition.io.Connection)
	 */
	public void setConnection(Connection connection)
	{
		if (connection instanceof L2CAPConnection) {
			this.connection = new L2CapStreamConnection( (L2CAPConnection)connection);
		} else if (connection instanceof L2CapStreamConnection) {
			this.connection = (L2CapStreamConnection)connection;
		} else {
			throw new IllegalArgumentException();
		}
	}

}
