//#condition polish.api.btapi
/*
 * Created on Jul 29, 2008 at 10:33:13 PM.
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.L2CAPConnection;
import javax.microedition.io.StreamConnection;

/**
 * <p>Provides a stream connection over the (packet oriented) L2CAP bluetooth protocol</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class L2CapStreamConnection implements StreamConnection
{

	private final L2CAPConnection connection;

	/**
	 * Creates a new L2CapStreamConnection
	 * @param connection the original L2CAPConnection
	 */
	public L2CapStreamConnection(L2CAPConnection connection)
	{
		this.connection = connection;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.InputConnection#openDataInputStream()
	 */
	public DataInputStream openDataInputStream() throws IOException
	{
		return new DataInputStream( openInputStream() );
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.InputConnection#openInputStream()
	 */
	public InputStream openInputStream() throws IOException
	{
		return new L2CapInputStream( this.connection );
	}


	/* (non-Javadoc)
	 * @see javax.microedition.io.OutputConnection#openDataOutputStream()
	 */
	public DataOutputStream openDataOutputStream() throws IOException
	{
		return new DataOutputStream( openOutputStream() );
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.OutputConnection#openOutputStream()
	 */
	public OutputStream openOutputStream() throws IOException
	{
		return new L2CapOutputStream( this.connection );
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.Connection#close()
	 */
	public void close() throws IOException
	{
		this.connection.close();
	}

}
