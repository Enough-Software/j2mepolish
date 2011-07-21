//#condition polish.api.btapi
/*
 * Created on Jul 24, 2008 at 10:58:06 PM.
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
import java.io.InputStream;

import javax.bluetooth.L2CAPConnection;

/**
 * <p>Provides an InputStream over an L2CAP Bluetooth Connection</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class L2CapInputStream extends InputStream implements L2CapStream
{

	private final L2CAPConnection connection;
	private int mtu;
	private final byte[] buffer;
	private int bufferOffset;
	private int bufferLen;
	private final byte[] confirmation = new byte[ MINIMUM_MTU ]; 

	/**
	 * Creates a new L2CAP input stream.
	 * 
	 * @param connection the L2CAP connection
	 * @throws IOException when the connection was not properly configured
	 * @throws IllegalArgumentException when connection is null
	 */
	public L2CapInputStream(L2CAPConnection connection) throws IOException
	{
		if (connection == null) {
			throw new IllegalArgumentException();
		}
		this.connection = connection;
		this.mtu = connection.getReceiveMTU();
		this.buffer = new byte[ this.mtu ];
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException
	{
		byte[] readBuffer = new byte[1];
		read( readBuffer, 0, 1 );
		return (readBuffer[0] & 0xff);
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	public int available() throws IOException
	{
		if (this.bufferLen == 0 && this.connection.ready()) {
			this.bufferLen = this.connection.receive(this.buffer);
		}
		return this.bufferLen;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	public void close() throws IOException
	{
		//this.connection.close();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte[] data) throws IOException
	{
		return read(data, 0, data.length );
	}
	
	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte[] data, int offset, int len) throws IOException
	{
		// first check if we already have some data available:
		if (this.bufferLen != 0) {
			len = Math.min( len, this.bufferLen );
			System.arraycopy(this.buffer, this.bufferOffset, data, offset, len );
			this.bufferOffset += len;
			this.bufferLen -= len;
			if (this.bufferLen == 0) {
				this.bufferOffset = 0;
			}
			return len;
		}
		
		// try to load data from connection:
		int read = this.connection.receive(this.buffer);
		// detect EOF condition:
		if (read == L2CAPConnection.MINIMUM_MTU) {
			boolean isEof = true;
			for (int i=0; i<read; i++) {
				byte is = this.buffer[i];
				byte should = 0;
				if (i < EOF_SEQUENCE.length) {
					should = EOF_SEQUENCE[i];
				}
				if (is != should) {
					isEof = false;
					break;
				}
			}
			if (isEof) {
				// send confirmation that the connection can be closed:
				this.connection.send( this.confirmation );
				return -1;
			}
		}
		// send confirmation that buffer has been received:
		this.connection.send(this.confirmation);
		len = Math.min( len, read );
		System.arraycopy( this.buffer, 0, data, offset, len );
		if (len < read) {
			this.bufferOffset = len;
			this.bufferLen = read - len;
		}
		return len;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long n) throws IOException
	{
		// TODO implement skip
		return super.skip(n);
	}
	

	/* (non-Javadoc)
	 * @see java.io.InputStream#mark(int)
	 */
	public void mark(int readlimit)
	{
		// ignore
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#markSupported()
	 */
	public boolean markSupported()
	{
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.io.InputStream#reset()
	 */
	public void reset() throws IOException
	{
		throw new IOException();
	}


}
