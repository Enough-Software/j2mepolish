/*
 * Created on Jul 25, 2008 at 2:27:47 PM.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import de.enough.polish.util.Arrays;

import junit.framework.TestCase;

/**
 * <p>tests the l2cap streams</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class L2CapStreamTest extends TestCase
{
	
	public void testByteArrays() throws IOException {
		TestL2CapConnection sender;
		TestL2CapConnection receiver;
		boolean isSynchrone;
		int receiveMtu;
		int sendMtu;
		byte[] sendData;
		byte[] receivedData;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		Random random = new Random( System.currentTimeMillis() );
		
		isSynchrone = true;
		receiveMtu = 512;
		sendMtu = 756;
		sender = new TestL2CapConnection(isSynchrone, receiveMtu, sendMtu );
		receiver = new TestL2CapConnection( sender );
		
		L2CapOutputStream out = new L2CapOutputStream(sender);
		L2CapInputStream in = new L2CapInputStream(receiver);
		
		sendData = new byte[ sender.getTransmitMTU() * 5 + 17 ];
		for (int i = 0; i < sendData.length; i++)
		{
			sendData[i] = (byte) random.nextInt();
		}
		( new SendThread( out, sendData) ).start(); 
		
		receivedData = new byte[ sender.getReceiveMTU() / 2 + 13 ];
		int read;
		while ( (read = in.read(receivedData)) != -1) {
			byteOut.write( receivedData, 0, read );
		}
		receivedData = byteOut.toByteArray();
		assertEquals( sendData.length, receivedData.length );
		for (int i = 0; i < receivedData.length; i++)
		{
			if (receivedData[i] != sendData[i]) {
				System.out.println("Error at offset " + i + " (expected=" + sendData[i] + ", got=" + receivedData[i] + ")");
				break;
			}
		}
		assertTrue( Arrays.equals( sendData, receivedData ) );
		
		
	}
	
	public void testSingleBytesWrite() throws IOException {
		TestL2CapConnection sender;
		TestL2CapConnection receiver;
		boolean isSynchrone;
		int receiveMtu;
		int sendMtu;
		byte[] sendData;
		byte[] receivedData;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		Random random = new Random( System.currentTimeMillis() );
		
		isSynchrone = true;
		receiveMtu = 512;
		sendMtu = 756;
		sender = new TestL2CapConnection(isSynchrone, receiveMtu, sendMtu );
		receiver = new TestL2CapConnection( sender );
		
		L2CapOutputStream out = new L2CapOutputStream(sender);
		L2CapInputStream in = new L2CapInputStream(receiver);

		sendData = new byte[ sender.getTransmitMTU() * 5 + 17 ];
		for (int i = 0; i < sendData.length; i++)
		{
			sendData[i] = (byte) random.nextInt();
		}
		( new SendThread( out, sendData, true) ).start(); 
		
		receivedData = new byte[ sender.getReceiveMTU() / 2 + 13 ];
		byteOut = new ByteArrayOutputStream();
		int read;
		while ( (read = in.read(receivedData)) != -1) {
			byteOut.write( receivedData, 0, read );
		}
		receivedData = byteOut.toByteArray();
		assertEquals( sendData.length, receivedData.length );
		for (int i = 0; i < receivedData.length; i++)
		{
			if (receivedData[i] != sendData[i]) {
				System.out.println("Error at offset " + i + " (expected=" + sendData[i] + ", got=" + receivedData[i] + ")");
//				for (int j=i+1; j<receivedData.length; j++) {
//					if (sendData[j] == receivedData[i]) {
//						System.out.println("Found next match at offset " + j);
//						break;
//					}
//				}
				break;
			}
		}
		assertTrue( Arrays.equals( sendData, receivedData ) );
	}
	
	public void testSingleBytesWriteAndRead() throws IOException {
		TestL2CapConnection sender;
		TestL2CapConnection receiver;
		boolean isSynchrone;
		int receiveMtu;
		int sendMtu;
		byte[] sendData;
		byte[] receivedData;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		Random random = new Random( System.currentTimeMillis() );
		
		isSynchrone = true;
		receiveMtu = 512;
		sendMtu = 756;
		sender = new TestL2CapConnection(isSynchrone, receiveMtu, sendMtu );
		receiver = new TestL2CapConnection( sender );
		
		L2CapOutputStream out = new L2CapOutputStream(sender);
		L2CapInputStream in = new L2CapInputStream(receiver);

		sendData = new byte[ sender.getTransmitMTU() * 5 + 17 ];
		for (int i = 0; i < sendData.length; i++)
		{
			sendData[i] = (byte) random.nextInt();
		}
		( new SendThread( out, sendData, true) ).start(); 
		
		for (int i=0; i<sendData.length; i++) {
			assertEquals( (sendData[i] & 0xff), in.read() );
		}
	}
	
	public void testDataOutputDataInputWithByteStreams() throws IOException {
		TestL2CapConnection sender;
		TestL2CapConnection receiver;
		boolean isSynchrone;
		int receiveMtu;
		int sendMtu;
		final int[] sendData;
		Random random = new Random( System.currentTimeMillis() );
		
		isSynchrone = true;
		receiveMtu = 512;
		sendMtu = 756;
		sender = new TestL2CapConnection(isSynchrone, receiveMtu, sendMtu );
		receiver = new TestL2CapConnection( sender );
		
		final L2CapOutputStream out = new L2CapOutputStream(sender);
		L2CapInputStream in = new L2CapInputStream(receiver);
		
		sendData = new int[ sender.getTransmitMTU() * 5 + 17 ];
		for (int i = 0; i < sendData.length; i++)
		{
			sendData[i] = random.nextInt();
		}
		final DataOutputStream dataOut = new DataOutputStream( out );
		(new Thread() {
			public void run() {
				try {
					dataOut.writeUTF("test");
					dataOut.writeInt( sendData.length );
					for (int i = 0; i < sendData.length; i++)
					{
						dataOut.writeInt( sendData[i] );
					}
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		int read;
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		while ((read = in.read(buffer)) != -1) {
			byteOut.write(buffer, 0, read );
		}
		
		
		DataInputStream dataIn = new DataInputStream( new ByteArrayInputStream(byteOut.toByteArray()) );
		
		assertEquals("test", dataIn.readUTF());
		assertEquals( sendData.length, dataIn.readInt() );
		for (int i = 0; i < sendData.length; i++)
		{
			assertEquals( sendData[i], dataIn.readInt() );
		}

	}
	
	
	public void testDataOutputDataInput() throws IOException {
		TestL2CapConnection sender;
		TestL2CapConnection receiver;
		boolean isSynchrone;
		int receiveMtu;
		int sendMtu;
		final int[] sendData;
		Random random = new Random( System.currentTimeMillis() );
		
		isSynchrone = true;
		receiveMtu = 512;
		sendMtu = 756;
		sender = new TestL2CapConnection(isSynchrone, receiveMtu, sendMtu );
		receiver = new TestL2CapConnection( sender );
		
		final L2CapOutputStream out = new L2CapOutputStream(sender);
		L2CapInputStream in = new L2CapInputStream(receiver);
		
		sendData = new int[ sender.getTransmitMTU() * 5 + 17 ];
		for (int i = 0; i < sendData.length; i++)
		{
			sendData[i] = random.nextInt();
		}
		final DataOutputStream dataOut = new DataOutputStream( out );
		(new Thread() {
			public void run() {
				try {
					dataOut.writeUTF("test");
					dataOut.writeInt( sendData.length );
					for (int i = 0; i < sendData.length; i++)
					{
						dataOut.writeInt( sendData[i] );
					}
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		DataInputStream dataIn = new DataInputStream( in );
		
		assertEquals("test", dataIn.readUTF());
		assertEquals( sendData.length, dataIn.readInt() );
		for (int i = 0; i < sendData.length; i++)
		{
			assertEquals( sendData[i], dataIn.readInt() );
		}

	}

}

class SendThread extends Thread {
	private final byte[] data;
	private final L2CapOutputStream out;
	private final boolean writeSingely;

	public SendThread( L2CapOutputStream out, byte[] data ) {
		this( out, data, false);
	}
	
	public SendThread( L2CapOutputStream out, byte[] data, boolean writeSingely ) {
		this.out = out;
		this.data = data;
		this.writeSingely = writeSingely;
	}

	
	public void run() {
		try {
			if (this.writeSingely) {
				for (int i=0; i<data.length;i++) {
					out.write( data[i] );
				}
			} else {
				out.write(data);
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

