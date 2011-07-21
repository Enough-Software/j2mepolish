/*
 * Created on Nov 08, 2008 at 3:20:53 AM.
 * 
 * Copyright (c) 2008 Robert Virkus / Enough Software
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

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.L2CAPConnection;

import de.enough.polish.bluetooth.L2CapInputStream;
import de.enough.polish.bluetooth.L2CapOutputStream;
import de.enough.polish.rmi.Remote;
import de.enough.polish.rmi.RmiResolver;

/**
 * <p>A bluetooth service that allows the easy creation of desktop L2CAP based bluetooth services.</p>
 * <p>This service requires initialization by calling init( Remote remote, String uuid ). 
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class L2CapRemoteService implements BluetoothRemoteService {
	
	
	
	private String uuid;

	private L2CapServer server;
	private RmiResolver resolver;

	/** Creates a new Remote Service */
	public L2CapRemoteService() {
		super();
	}
	
	
	/**
	 * Initializes this service
	 * @param serviceImplementation the service implementation
	 * @param uuid the UUID, for generating a UUID you can visit http://www.famkruithof.net/uuid/uuidgen
	 */
	public void init( Remote serviceImplementation, String uuid )  {
		this.resolver = new RmiResolver( serviceImplementation );
		this.uuid = uuid;
	}


	/**
	 * Starts the service
	 * @throws BluetoothStateException 
	 */
	public void start() throws BluetoothStateException {
		if (this.server == null) {
			this.server = new L2CapServer(this, L2CapServer.generateLocalUrl(this.uuid));
		}
		this.server.start();
	}
	
	/**
	 * Stops the service
	 */
	public void stop() {
		this.server.requestStop();
	}
	


	/**
	 * @param connection
	 */
	public void process(L2CAPConnection connection)
	{
		L2CapOutputStream l2CapOut = null;
		L2CapInputStream l2CapIn = null;
		try
		{
			while (true) {
				l2CapOut = new L2CapOutputStream( connection );
				l2CapIn = new L2CapInputStream( connection );
				DataOutputStream out = new DataOutputStream( l2CapOut );
				DataInputStream in = new DataInputStream( l2CapIn );
				this.resolver.process(in, out);
			}
		} catch (IOException e)
		{
			// TODO robertvirkus handle IOException
			e.printStackTrace();
		} finally {
			if (l2CapOut != null) {
				try
				{
					l2CapOut.close();
				} catch (Exception e)
				{
					// ignore
				}
			}
			if (l2CapIn != null) {
				try
				{
					l2CapIn.close();
				} catch (Exception e)
				{
					// ignore
				}
			}
		}
		
	}



	/**
	 * @param capServer
	 * @param e
	 */
	public void handleError(L2CapServer capServer, IOException e)
	{
		e.printStackTrace();
		System.err.println("Error while offering bluetooth service: " + e.toString() );
		
	}
	

}
