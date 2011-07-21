/*
 * Created on Nov 27, 2007 at 5:41:52 PM.
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
package de.enough.polish.io;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>A data input stream that logs all read data and provides it later as a byte array.</p>
 * <p>This stream is useful for getting the accessed data for later storage.</p>
 * <p>Example usage:
 * 	private void loadAndStoreData( InputStream in) {
 * 		RecordingDataInputStream dataIn = new RecordingDataInputStream( in );
 * 		load( dataIn );
 * 		byte[] readData = dataIn.getRecordedData();
 * 		store( readData );
 *  }
 * </p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Nov 27, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RecordingDataInputStream extends InputStream implements DataInput
{
	private ByteArrayOutputStream byteOut;
	private DataOutputStream dataOut;
	private final DataInputStream dataIn;

	/**
	 * 
	 * @param in the input stream
	 */
	public RecordingDataInputStream(InputStream in)
	{
		this.dataIn = new DataInputStream( in );
		this.byteOut = new ByteArrayOutputStream();
		this.dataOut = new DataOutputStream( this.byteOut );
	}
	
	/**
	 * Retrieves the data that has been read since the beginning or since the last clearRecordedData() call.
	 * @return the read data as an byte array
	 * @see #clearRecordedData()
	 */
	public byte[] getRecordedData() {
		try
		{
			this.dataOut.flush();
		} catch (IOException e)
		{
			//#debug warn
			System.out.println("Unable to flush DataOutputStream" + e);
		}
		return this.byteOut.toByteArray();
	}
	
	/**
	 * Resets the data recording and deletes all previously recorded data
	 */
	public void clearRecordedData() {
		this.byteOut = new ByteArrayOutputStream();
		this.dataOut = new DataOutputStream( this.byteOut );
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException
	{
		int result = this.dataIn.read();
		this.dataOut.writeByte(result);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readBoolean()
	 */
	public boolean readBoolean() throws IOException
	{
		boolean result = this.dataIn.readBoolean();
		this.dataOut.writeBoolean(result);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readByte()
	 */
	public byte readByte() throws IOException
	{
		byte result = this.dataIn.readByte();
		this.dataOut.writeByte(result);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readChar()
	 */
	public char readChar() throws IOException
	{
		char result = this.dataIn.readChar();
		this.dataOut.writeChar(result);
		return result;
	}

	//#if polish.hasFloatingPoint
	/* (non-Javadoc)
	 * @see java.io.DataInput#readDouble()
	 */
	public double readDouble() throws IOException
	{
		double result = this.dataIn.readDouble();
		this.dataOut.writeDouble(result);
		return result;
	}
	//#endif

	//#if polish.hasFloatingPoint
	/* (non-Javadoc)
	 * @see java.io.DataInput#readFloat()
	 */
	public float readFloat() throws IOException
	{
		float result = this.dataIn.readFloat();
		this.dataOut.writeFloat(result);
		return result;
	}
	//#endif

	/* (non-Javadoc)
	 * @see java.io.DataInput#readFully(byte[])
	 */
	public void readFully(byte[] buffer) throws IOException
	{
		readFully( buffer, 0, buffer.length );
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readFully(byte[], int, int)
	 */
	public void readFully(byte[] buffer, int offset, int len) throws IOException
	{
		this.dataIn.readFully(buffer, offset, len);
		this.dataOut.write(buffer, offset, len);
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readInt()
	 */
	public int readInt() throws IOException
	{
		int result = this.dataIn.readInt();
		this.dataOut.writeInt(result);
		return result;
	}

	//#if polish.JavaSE || polish.android
	/* (non-Javadoc)
	 * @see java.io.DataInput#readLine()
	 */
	public String readLine() throws IOException
	{
		return readUTF();
	}
	//#endif

	/* (non-Javadoc)
	 * @see java.io.DataInput#readLong()
	 */
	public long readLong() throws IOException
	{
		long result = this.dataIn.readLong();
		this.dataOut.writeLong(result);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readShort()
	 */
	public short readShort() throws IOException
	{
		short result = this.dataIn.readShort();
		this.dataOut.writeShort(result);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readUTF()
	 */
	public String readUTF() throws IOException
	{
		String result = this.dataIn.readUTF();
		this.dataOut.writeUTF(result);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readUnsignedByte()
	 */
	public int readUnsignedByte() throws IOException
	{
		int result = this.dataIn.readUnsignedByte();
		this.dataOut.writeByte( result );
		return result;
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readUnsignedShort()
	 */
	public int readUnsignedShort() throws IOException
	{
		int result = this.dataIn.readUnsignedShort();
		this.dataOut.writeShort(result);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#skipBytes(int)
	 */
	public int skipBytes(int len) throws IOException
	{
		int result = this.dataIn.skipBytes(len);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	public int available() throws IOException
	{
		return this.dataIn.available();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	public void close() throws IOException
	{
		this.dataIn.close();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#mark(int)
	 */
	public synchronized void mark(int readlimit)
	{
		this.dataIn.mark(readlimit);
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#markSupported()
	 */
	public boolean markSupported()
	{
		return this.dataIn.markSupported();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte[] b, int off, int len) throws IOException
	{
		int read = this.dataIn.read(b, off, len);
		this.dataOut.write( b, off, read );
		return read;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte[] b) throws IOException
	{
		return read(b, 0, b.length);
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#reset()
	 */
	public synchronized void reset() throws IOException
	{
		this.dataIn.reset();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long n) throws IOException
	{
		return this.dataIn.skip(n);
	}
	
}
