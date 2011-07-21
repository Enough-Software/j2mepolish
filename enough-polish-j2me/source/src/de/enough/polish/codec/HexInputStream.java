//#condition polish.cldc
/*
 * Created on 12-Oct-2005 at 09:45:32.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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

package de.enough.polish.codec;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Decodes hex encoded data on the fly.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class HexInputStream extends InputStream {

	private InputStream in;
	private byte[] buffer;
	private int positionInBuffer;

	/**
	 * Creates a new HexInputStream that wraps another stream.
	 * 
	 * @param in the stream that provides hex encoded data.
	 */
	public HexInputStream( InputStream in) {
		super();
		this.in = in;
	}

	/**
	 * Creates a new HexInputStream that uses a byte array internally.
	 * Compared to wrapping a byte array into a ByteArrayInputStream
	 * and then read it with the HexInputStream( InputStream in) constructor,
	 * this constructor results in a much faster processing.
	 * 
	 * @param buffer the hex encoded data that is used as a source.
	 */
	public HexInputStream( byte[] buffer ) {
		super();
		this.buffer = buffer;
	}

	/**
	 * Reads the next byte of data.
   * 
   * @return the read byte
   * @throws IOException if an error occurs
	 */
	public int read() throws IOException {
		// we assume that the characters are encoded as ASCII,
		// so there is one byte for each character
		char firstChar;
		char secondChar;
		if (this.buffer != null) {
			firstChar = (char) this.buffer[ this.positionInBuffer++ ];
			secondChar = (char) this.buffer[ this.positionInBuffer++ ];
		} else {
			firstChar = (char) this.in.read(); // ((this.in.read() << 8) | this.in.read() );
			secondChar = (char) this.in.read(); // ((this.in.read() << 8) | this.in.read() );
		}
		//System.out.print( firstChar + secondChar );
		int f = Character.digit( firstChar, 16 ) << 4;
        f = f | Character.digit( secondChar, 16 );
        return (f & 0xFF);
	}

}
