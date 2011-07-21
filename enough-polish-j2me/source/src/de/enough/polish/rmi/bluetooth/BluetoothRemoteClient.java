/*
 * Created on Dec 18, 2008 at 9:38:24 PM.
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

import java.io.IOException;

import javax.microedition.io.Connection;

/**
 * <p>Abstracts usage of the different bluetooth protocols.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface BluetoothRemoteClient
{
	
	/**
	 * Connects this remote client.
	 * 
	 * @throws IOException when the connection fails
	 */
	void connect() throws IOException;
	
	/**
	 * Disconnects the client, e.g. at the application end.
	 * @throws IOException when the connection cannot be closed 
	 */
	void disconnect() throws IOException;
	
	/**
	 * Sets the connection that should be used.
	 * @param connection the connection
	 */
	void setConnection( Connection connection );
	
	/**
	 * Retrieves the connection that is used
	 * @return the used connection, can be null
	 */
	Connection getConnection();

}
