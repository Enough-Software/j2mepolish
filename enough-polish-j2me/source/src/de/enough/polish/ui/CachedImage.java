//#condition polish.usePolishGui
/*
 * Created on 10-August-2012 at 19:20:28.
 * 
 * Copyright (c) 2012 Robert Virkus / Enough Software
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
package de.enough.polish.ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.enough.polish.io.CachedInputStream;
import de.enough.polish.io.Externalizable;

/**
 * <p>Caches the image data from images loaded from the web.</p>
 * 
 * <p>Copyright Enough Software 2012</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CachedImage 
implements Externalizable
{

	private static final int VERSION = 100;
	private byte[] cachedData;
	//#if polish.build.classes.NativeImage:defined
		//#= ${polish.build.classes.NativeImage} image;
	//#else
		javax.microedition.lcdui.Image image;
	//#endif
	
	/**
	 * Creates a new empty cached image
	 */
	public CachedImage() {
		// de-serialization is required for fields
	}

	/**
	 * Creates a new cached image
	 * 
	 * @throws IOException 
	 */
	public CachedImage(InputStream in) throws IOException {
		CachedInputStream bufferStream = new CachedInputStream(in);
		this.image =
		//#if polish.build.classes.NativeImage:defined
			//#= ${polish.build.classes.NativeImage}
		//#else
			javax.microedition.lcdui.Image
		//#endif
			.createImage(bufferStream);
		this.cachedData = bufferStream.getBufferedData();
	}

	/**
	 * Retrieves the underlying image
	 * @return the image
	 */
	public
	//#if polish.build.classes.NativeImage:defined
		//#= ${polish.build.classes.NativeImage} 
	//#else
		javax.microedition.lcdui.Image 
	//#endif
	getImage() {
		if (this.image == null && this.cachedData != null) {
			this.image =
					//#if polish.build.classes.NativeImage:defined
						//#= ${polish.build.classes.NativeImage}
					//#else
						javax.microedition.lcdui.Image
					//#endif
						.createImage(this.cachedData, 0, this.cachedData.length);
		}
		return this.image;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		if (this.cachedData != null) {
			out.writeBoolean(true);
			out.writeInt( this.cachedData.length );
			out.write(this.cachedData);
		} else {
			out.writeBoolean(false);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version > VERSION) {
			throw new IOException("invalid version " + version);
		}
		boolean notNull = in.readBoolean();
		if (notNull) {
			int size = in.readInt();
			this.cachedData = new byte[ size ];
			in.read( this.cachedData, 0, size );
		}
	}

}
