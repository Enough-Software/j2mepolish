/*
 * Created on Jan 9, 2007 at 2:23:02 PM.
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
package de.enough.polish.sample.rmi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

/**
 * <p>Stores a highscore</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jan 9, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class GameHighscore implements Externalizable {
	
	private String name;
	private int[] points;
	
	public GameHighscore() {
		// used for reading a highscore from an input stream
	}
	
	public GameHighscore( String name, int[] points ) {
		this.name = name;
		this.points = points;
	}
	
	public void read(DataInputStream in) throws IOException {
		boolean isNotNull = in.readBoolean();
		if (isNotNull) {
			this.name = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			int length = in.readInt();
			this.points = new int[ length ];
			for (int i = 0; i < length; i++) {
				this.points[i] = in.readInt();
			}

		}
		
	}
	public void write(DataOutputStream out) throws IOException {
		boolean isNotNull = (this.name != null);
		out.writeBoolean( isNotNull );
		if (isNotNull) {
			out.writeUTF( this.name );
		}
		isNotNull = (this.points != null);
		out.writeBoolean( isNotNull );
		if (isNotNull) {
			out.writeInt( this.points.length );
			for (int i = 0; i < this.points.length; i++) {
				out.writeInt( this.points[i] );
			}
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public int[] getPoints() {
		return this.points;
	}

}
