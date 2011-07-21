/*
 * Created on Dec 15, 2010 at 4:15:44 PM.
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
package de.enough.polish.app.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

/**
 * <p>Contains the application configuration</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Configuration implements Externalizable {

	/**
	 * The key under which a configuration is typically stored.
	 */
	public final static String KEY = "_cfg";
	/**
	 * Version for serialization.
	 */
	private final static int VERSION = 100;
	
	/**
	 * Dirty flag, indicates if the configuration has been changed since the last reset or load.
	 */
	private boolean isDirty;
	
	/**
	 * Just an example for a field.
	 */
	private String userName;
	
	/**
	 * Creates a new standard configuration.
	 */
	public Configuration() {
		//TODO specify default values
	}
	
	/**
	 * Checks if the configuration has been changed since the creation or the last reset.
	 * @return true when the configuration has been changed.
	 * @see #resetDirtyFlag()
	 */
	public boolean isDirty() {
		return this.isDirty;
	}
	
	/**
	 * Resets the dirty flag.
	 * @see #isDirty()
	 */
	public void resetDirtyFlag() {
		this.isDirty = false;
	}
	
	/**
	 * Example: user name retrieval
	 * @return the user name
	 */
	public String getUserName() {
		return this.userName;
	}
	
	/**
	 * Example: sets the user name
	 * @param name the user name
	 */
	public void setUserName( String name ) {
		this.isDirty = true;
		this.userName = name;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		boolean notNull = (this.userName != null);
		out.writeBoolean( notNull );
		if (notNull) {
			out.writeUTF(this.userName);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version > VERSION) {
			throw new IOException("for invalid version " + version);
		}
		boolean notNull = in.readBoolean();
		if (notNull) {
			this.userName = in.readUTF();
		}
	}


}
