/*
 * Created on 08-Apr-2006 at 20:38:42.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>Feeds an output stream with a given input stream in a background thread.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        08-Apr-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class StreamWrapper extends Thread {

	private final InputStream in;
	private final OutputStream out;

	/**
	 * Cerates a new wrapper
	 * @param out the output stream for writing data
	 * @param in the input stream for reading data
	 */
	public StreamWrapper( InputStream in, OutputStream out ) {
		super("StreamWrapper");
		this.in = in;
		this.out = out;
	}

	public void run() {
		byte[] buffer = new byte[ 8 * 1024 ];
		int read;
		try {
			while ( (read = this.in.read( buffer, 0, buffer.length )) != -1 ) {
				this.out.write( buffer, 0, read );
			}
		} catch (IOException e) {
			System.err.println("Unable to read from inputstream: " + e.toString() );
			e.printStackTrace();
		} finally {
			try {
				this.in.close();
			} catch (IOException e) {
				System.err.println("Unable to close inputstream: " + e.toString() );
				e.printStackTrace();
			}
			try {
				this.out.close();
			} catch (IOException e) {
				System.err.println("Unable to close outputstream: " + e.toString() );
				e.printStackTrace();
			}
		}
	}

}
