//#condition polish.api.btapi
/*
 * Created on Jul 24, 2008 at 10:59:32 PM.
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
import java.io.OutputStream;

import javax.bluetooth.L2CAPConnection;

/**
 * <p>An OutputStream over an L2CAP bluetooth connection</p>
 * <p>
 *   Note: the stream is not thread safe and should be used by only a single thread
 *         or by externally synchronized threads.
 * </p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class L2CapOutputStream extends OutputStream implements L2CapStream
{

	private final L2CAPConnection connection;
	private int mtu;
	private byte[] buffer;
	private int bufferIndex;
	private boolean isFlushing;
	
	private final byte[] confirmation = new byte[ MINIMUM_MTU ];

	/**
	 * Creates a new L2CAP output stream.
	 * 
	 * @param connection the L2CAP connection
	 * @throws IOException when the connection was not properly configured
	 * @throws IllegalArgumentException when connection is null
	 */
	public L2CapOutputStream(L2CAPConnection connection) throws IOException
	{
		if (connection == null) {
			throw new IllegalArgumentException();
		}
		this.connection = connection;
		this.mtu = this.connection.getTransmitMTU();
		this.buffer = new byte[ this.mtu ];
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int data) throws IOException
	{
		this.buffer[this.bufferIndex] = (byte)data;
		this.bufferIndex++;
		if (this.bufferIndex >= this.mtu) {
			flush();
		}
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#close()
	 */
	public void close() throws IOException
	{
		flush();
		byte[] eofBuffer = new byte[ MINIMUM_MTU];
		System.arraycopy( EOF_SEQUENCE, 0, eofBuffer, 0, EOF_SEQUENCE.length );
		send(eofBuffer);
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#flush()
	 */
	public void flush() throws IOException
	{
		if (this.bufferIndex > 0) {
			try {
				this.isFlushing = true;
				int index = this.bufferIndex;
				this.bufferIndex = 0;
				write( this.buffer, 0, index );
			} finally {
				this.isFlushing = false;
			}
		}		
	}
		

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write(byte[] data) throws IOException
	{
		write(data, 0, data.length);
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public void write(byte[] data, int offset, int len) throws IOException
	{
		if (this.bufferIndex + len < MINIMUM_MTU && !this.isFlushing) {
			System.arraycopy( data, offset, this.buffer, this.bufferIndex, len );
			this.bufferIndex += len;
			return;
		}
		if (this.bufferIndex > 0) {
			byte[] copy = new byte[ len + this.bufferIndex ];
			System.arraycopy( this.buffer, 0, copy, 0, this.bufferIndex );
			System.arraycopy( data, offset, copy, this.bufferIndex, len );
			len += this.bufferIndex;
			offset = 0;
			data = copy;
			this.bufferIndex = 0;
		}
		if (len <= this.mtu && offset == 0 && len == data.length) {
			send(data);
		} else {
			while (len > 0) {
				int currentLength = Math.min(len, this.mtu);
				byte[] transfer = new byte[currentLength ];
				System.arraycopy( data, offset, transfer, 0, currentLength );
				len -= currentLength;
				offset += currentLength;
				send(transfer);
			}
		}
	}

	/**
	 * Sends data and waits for a receiving confirmation
	 * @param data the data to be send
	 * @throws IOException when the sending or reading fails
	 */
	private void send(byte[] data) throws IOException {
		this.connection.send(data);
		// wait for confirmation:
		this.connection.receive(this.confirmation);		
	}

}
