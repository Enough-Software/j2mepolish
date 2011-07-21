/*
 * Created on Jul 19, 2010 at 5:46:39 PM.
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

/**
 * <p>Provides access to an image of an AtomEntry</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AtomImage
implements Externalizable
{
	
	private static final int VERSION = 100;
	private static boolean serializeImageData = true;
	private String url;
	private byte[] data;
	private Object nativeRepresentation;

	/**
	 * Creates a new empty image
	 */
	public AtomImage() {
		// nothing to init
	}
	
	/**
	 * Creates a new image with the specified URL
	 * @param url the URL of the image
	 */
	public AtomImage( String url ) {
		this.url = url;
	}
	
	/**
	 * Retrieves a new image
	 * @param url the URL of the image
	 * @param data the data of this image
	 */
	public AtomImage( String url, byte[] data ) {
		this.url = url;
		this.data = data;
	}
	
	
	/**
	 * Determines whether image data should be serialized (true by default).
	 * 
	 * @return true when image data should also be serialized
	 * @see #setSerializeImageData(boolean)
	 */
	public static boolean isSerializeImageData() {
		return serializeImageData;
	}
	
	/**
	 * Specifies if image data of entry feeds should be serialized. By default image data is serialized.
	 * @param serializeData true when image data should be serialized
	 * @see #isSerializeImageData()
	 */
	public static void setSerializeImageData(boolean serializeData) {
		serializeImageData   = serializeData;
	}

	
	/**
	 * Retrieves the data of this image
	 * @return the data
	 * @see #setData(byte[])
	 */
	public byte[] getData() {
		return this.data;
	}
	
	/**
	 * Sets the data of this image
	 * @param data the raw data of this image
	 * @see #getData()
	 */
	public void setData( byte[] data) {
		this.data = data;
	}
	
	/**
	 * Retrieves the URL of this image
	 * @return the URL
	 */
	public String getUrl() {
		return this.url;
	}
	
	/**
	 * Retrieve a native representation as it has been set before.
	 * @return the native representation e.g. de.enough.polish.ui.Image
	 * @see #setNativeRepresentation(Object)
	 */
	public Object getNativeRepresentation() {
		return this.nativeRepresentation;
	}
	
	/**
	 * Sets the native representation as it has been set before.
	 * @param object the native representation e.g. de.enough.polish.ui.Image
	 * @see #getNativeRepresentation()
	 */
	public void setNativeRepresentation( Object object ) {
		this.nativeRepresentation = object;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		boolean notNull = (this.url != null);
		out.writeBoolean(notNull);
		if (notNull) {
			out.writeUTF(this.url);
		}
		notNull = (this.data != null && serializeImageData);
		out.writeBoolean(notNull);
		if (notNull) {
			out.writeInt(this.data.length);
			out.write(this.data, 0, this.data.length);
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
			this.url = in.readUTF();
		}
		notNull = in.readBoolean();
		if (notNull) {
			int size = in.readInt();
			byte[] buffer = new byte[size];
			in.read(buffer, 0, size);
			this.data = buffer;
		}
	}

}
