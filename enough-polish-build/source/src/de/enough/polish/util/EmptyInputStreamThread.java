/*
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
package de.enough.polish.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Reads and immediately discards an input stream.</p>
 * <p>This is needed for executing processes on windows platforms,
 * since the standard error pipes might run fully and then block
 * the executed process completely. Weird, but that's how it is.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        09.01.2005 - rob creation
 * </pre>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class EmptyInputStreamThread extends Thread {

	private final InputStream input;
	
	public EmptyInputStreamThread( InputStream input ) {
		this.input = input;
	}
	
	public void run() {
		try {
			while ( this.input.read() != -1 ) {
				// do nothing
			}
		} catch (IOException e) {
			// ignore
		}
	}
		
}
