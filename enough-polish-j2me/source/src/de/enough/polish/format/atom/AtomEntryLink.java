/*
 * Created on Sep 29, 2010 at 7:40:55 PM.
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
package de.enough.polish.format.atom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;
import de.enough.polish.xml.XmlDomNode;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AtomEntryLink
implements Externalizable
{
	
	private static int VERSION = 100;
	private String href;
	private String rel;
	private String type;

	/**
	 * Creates a new empty link
	 */
	public AtomEntryLink() {
		// use setters to initialize
	}
	
	/**
	 * Creates a new link initialized from XML
	 * @param node the XML node
	 */
	public AtomEntryLink(XmlDomNode node) {
		this.href = node.getAttribute("href");
		if (this.href == null) {
			node.print();
			throw new IllegalArgumentException( "for node " + node );
		}
		this.rel = node.getAttribute("rel");
		this.type = node.getAttribute("type");
	}
	
	


	/**
	 * Retrieves the link URL
	 * @return the href URL, e.g. http://www.server.com/myImage.jpg
	 */
	public String getHref() {
		return this.href;
	}

	/**
	 * Sets the URL of this link
	 * @param href the href URL to set, e.g. http://www.server.com/myImage.jpg
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * Retrieves the relation of this link.
	 * A relation can be used for subtyping content
	 * @return the relation
	 */
	public String getRel() {
		return this.rel;
	}

	/**
	 * Sets the relation of this link
	 * @param rel the relation to set
	 */
	public void setRel(String rel) {
		this.rel = rel;
	}

	/**
	 * Retrieves the type of this link
	 * @return the type, e.g. image/jpeg
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the type of this link
	 * @param type the type, e.g. image/jpeg
	 */
	public void setType(String type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		if (this.href == null) {
			throw new IOException("cannot serialize link without href");
		}
		out.writeUTF( this.href );
		boolean notNull = (this.rel != null);
		out.writeBoolean( notNull );
		if (notNull) {
			out.writeUTF( this.rel );
		}
		notNull = (this.type != null);
		out.writeBoolean( notNull );
		if (notNull) {
			out.writeUTF( this.type );
		}
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version > VERSION) {
			throw new IOException("for version " + version);
		}
		this.href = in.readUTF();
		boolean notNull = in.readBoolean();
		if (notNull) {
			this.rel = in.readUTF();
		}
		notNull = in.readBoolean();
		if (notNull) {
			this.type = in.readUTF();
		}
	}
}
