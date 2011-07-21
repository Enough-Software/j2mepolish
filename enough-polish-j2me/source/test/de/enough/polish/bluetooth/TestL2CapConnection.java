/*
 * Created on Jul 25, 2008 at 2:44:25 PM.
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
import java.util.ArrayList;
import java.util.Random;

import javax.bluetooth.L2CAPConnection;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TestL2CapConnection implements L2CAPConnection
{
	
	private final boolean isSynchrone;
	private final int receiveMtu;
	private final int transmitMtu;
	private final Object lock = new Object();
	private final ArrayList chunks;
	private final Random random;
	private TestL2CapConnection otherSide;
	private boolean isClosed;

	public TestL2CapConnection( boolean isSynchrone, int receiveMtu, int transmitMtu) {
		this.isSynchrone = isSynchrone;
		if (isSynchrone) {
			this.receiveMtu = Math.min(receiveMtu, transmitMtu) - 5;
			this.transmitMtu = this.receiveMtu;			
		} else {
			this.receiveMtu = receiveMtu;
			this.transmitMtu = transmitMtu;
		}
		
		this.chunks = new ArrayList();
		this.random = new Random( System.currentTimeMillis() );
	}
	
	public TestL2CapConnection( TestL2CapConnection otherSide ) {
		otherSide.otherSide = this;
		this.otherSide = otherSide;
		this.isSynchrone = otherSide.isSynchrone;
		this.receiveMtu = otherSide.receiveMtu;
		this.transmitMtu = otherSide.transmitMtu;
		
		this.chunks = new ArrayList();
		this.random = new Random( System.currentTimeMillis() );
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.L2CAPConnection#getReceiveMTU()
	 */
	public int getReceiveMTU() throws IOException
	{
		return this.receiveMtu;
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.L2CAPConnection#getTransmitMTU()
	 */
	public int getTransmitMTU() throws IOException
	{
		return this.transmitMtu;
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.L2CAPConnection#ready()
	 */
	public boolean ready() throws IOException
	{
		return this.chunks.size() != 0;
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.L2CAPConnection#receive(byte[])
	 */
	public int receive(byte[] data) throws IOException
	{
		int written = data.length;
		while (this.chunks.size() == 0) {
			if (this.isClosed) {
				throw new IOException("closed");
			}
			try
			{
				Thread.sleep(200);
			} catch (InterruptedException e)
			{
				// ignore
			}
		}
		synchronized (this.lock)
		{
			int index = 0;
			if (!this.isSynchrone && this.chunks.size() > 1) {
				index = this.random.nextInt( this.chunks.size() );
				System.out.println("receiving chunk " + index);
			}
			byte[] buffer = (byte[]) this.chunks.remove(index);
			if (written > buffer.length) {
				written = buffer.length;
			}
			System.arraycopy( buffer, 0, data, 0, written );
			//System.out.println("receive " + written + ", with first byte " + data[0] + " chunks.size=" + chunks.size());
		}
		return written;
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.L2CAPConnection#send(byte[])
	 */
	public void send(byte[] data) throws IOException
	{
		synchronized(this.otherSide.lock) {
			//System.out.println("adding " + data.length + " with first byte " + data[0]);
			this.otherSide.chunks.add(data);
			return;
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.Connection#close()
	 */
	public void close() throws IOException
	{
		this.isClosed = true;
	}

}
