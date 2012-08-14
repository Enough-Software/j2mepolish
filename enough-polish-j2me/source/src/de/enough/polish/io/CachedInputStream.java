/*
 * Created on 10-August-2012 at 19:20:28.
 * 
 * Copyright (c) 2012 Robert Virkus / Enough Software
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
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Wraps another input stream to read data along.</p>
 * 
 * <p>Copyright Enough Software 2012</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CachedInputStream extends InputStream {
	
	private final ByteArrayOutputStream outStream;
	private final InputStream inStream;
	
	/**
	 * Creates a new cached input stream.
	 * @param wrappedStream the stream that is going to be cached
	 */
	public CachedInputStream( InputStream wrappedStream ) {
		this.inStream = wrappedStream;
		this.outStream = new ByteArrayOutputStream();
	}
	
	/**
	 * Retrieves the read data.
	 * @return all data that has been read from the underlying InputStream
	 */
	public byte[] getBufferedData() {
		return this.outStream.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		int value = this.inStream.read();
		this.outStream.write(value);
		return value;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	public int available() throws IOException {
		return this.inStream.available();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	public void close() throws IOException {
		this.inStream.close();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#mark(int)
	 */
	public void mark(int pos) {
		this.inStream.mark(pos);
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#markSupported()
	 */
	public boolean markSupported() {
		return this.inStream.markSupported();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte[] buffer, int off, int len) throws IOException {
		int read = this.inStream.read(buffer, off, len);
		if (read > 0) {
			this.outStream.write(buffer, off, read);
		}
		return read;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#reset()
	 */
	public void reset() throws IOException {
		this.inStream.reset();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long len) throws IOException {
		return this.inStream.skip(len);
	}
	

}
