/*
 * Created on Jul 13, 2010 at 5:39:52 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.format.atom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

/**
 * <p>Encapsulates information about an Atom feed author</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AtomAuthor
implements Externalizable
{

	private static final int VERSION = 100;
	private String name;
	private String email;
	private String uri;
	
	/**
	 * Creates an empty author
	 */
	public AtomAuthor() {
		// nothing to init
	}

	
	/**
	 * Creates a new author
	 * @param name the name of the author
	 * @param email the email of the author
	 * @param uri the URI of the author
	 */
	public AtomAuthor(String name, String email, String uri) {
		this.name = name;
		this.email = email;
		this.uri = uri;
	}

	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return this.uri;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		boolean notNull = (this.name != null);
		out.writeBoolean(notNull);
		if (notNull) {
			out.writeUTF( this.name );
		}
		notNull = (this.email != null);
		out.writeBoolean(notNull);
		if (notNull) {
			out.writeUTF( this.email );
		}
		notNull = (this.uri != null);
		out.writeBoolean(notNull);
		if (notNull) {
			out.writeUTF( this.uri );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version != VERSION) {
			throw new IOException("unknown verion " + version);
		}
		boolean notNull = in.readBoolean();
		if (notNull) {
			this.name = in.readUTF();
		}
		notNull = in.readBoolean();
		if (notNull) {
			this.email = in.readUTF();
		}
		notNull = in.readBoolean();
		if (notNull) {
			this.uri = in.readUTF();
		}
	}
	

}
