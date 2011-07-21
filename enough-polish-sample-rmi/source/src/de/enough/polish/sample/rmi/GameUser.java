/*
 * Created on Dec 28, 2006 at 6:06:19 PM.
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
 * <p>Represents a player of the game.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Dec 28, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class GameUser implements Externalizable {
	private long id;
	private String userName;
	private int accountPoints;
	
	public GameUser() {
		
	}
	
	/**
	 * Creates a new user.
	 * 
	 * @param id the internal ID of the user
	 * @param userName the name of the user
	 * @param accountPoints the number of account points available to that user (e.g. an artificial currency)
	 */
	public GameUser(long id, String userName, int accountPoints) {
		super();
		this.id = id;
		this.userName = userName;
		this.accountPoints = accountPoints;
	}

	/**
	 * @return the accountPoints
	 */
	public int getAccountPoints() {
		return this.accountPoints;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return this.userName;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( "user=" )
			.append( this.userName )
			.append(", account=")
			.append(this.accountPoints )
			.append(", id=")
			.append( this.id )
			.append( ", reference=")
			.append( super.toString() );
		return buffer.toString();
	}

	public void read(DataInputStream in) throws IOException {
		this.id = in.readLong();
		this.userName = in.readUTF();
		this.accountPoints = in.readInt();
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeLong( this.id );
		out.writeUTF( this.userName );
		out.writeInt( this.accountPoints );
	}
}
