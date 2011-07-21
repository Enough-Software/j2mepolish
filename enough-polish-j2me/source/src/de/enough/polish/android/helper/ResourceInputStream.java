//#condition polish.android
/*
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

package de.enough.polish.android.helper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps input streams opened with getClass().getResourceAsStream(..).
 * 
 * @author Robert Virkus
 */
public class ResourceInputStream extends InputStream {

	private final InputStream in;
	private final String url;
	private final int id;

	/**
	 * Creates a new resource input stream
	 * 
	 * @param url the URL of the resource
	 * @param id the ID of the resource (R.java)
	 * @param in the actual input stream
	 */
	public ResourceInputStream(String url, int id, InputStream in) {
		this.url = url;
		this.id = id;
		this.in = in;
	}
	
	/**
	 * Retrieves the url or the original resource
	 * @return the resource URL, e.g. /image.png
	 */
	public String getResourceUrl() {
		return this.url;
	}
	
	/**
	 * Retrieves the ID of the resource within R.java
	 * @return the ID of the resource
	 */
	public int getResourceId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		return this.in.read();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	public int available() throws IOException {
		return this.in.available();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	public void close() throws IOException {
		this.in.close();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#mark(int)
	 */
	public void mark(int pos) {
		this.in.mark(pos);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#markSupported()
	 */
	public boolean markSupported() {
		return this.in.markSupported();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte[] buf, int offset, int len) throws IOException {
		return this.in.read(buf, offset, len);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte[] buf) throws IOException {
		return this.in.read(buf);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#reset()
	 */
	public void reset() throws IOException {
		this.in.reset();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long len) throws IOException {
		return this.in.skip(len);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return this.in.equals(o);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.in.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.in.toString();
	}
	
	

}
