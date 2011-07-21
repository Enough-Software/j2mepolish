/*
 * Created on Jan 24, 2008 at 1:33:50 PM.
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
package de.enough.polish.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>A convenience class for accessing resources.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourceStreamUtil
{

	/**
	 * Disallow instantiation.
	 */
	private ResourceStreamUtil()
	{
		// nothing to init
	}
	
	/**
	 * Retrieves a resource as a byte array.
	 * @param url the URL of the resource within the JAR file, e.g. /resource.xml
	 * @return the resource as byte array
	 * @throws IOException when the resource could not be read
	 * TODO: This method should also handle file:// urls.
	 */
	public static byte[] getResourceAsByteArray( String url ) 
	throws IOException
	{
		return toByteArray( url.getClass().getResourceAsStream( url ) );
	}

	/**
	 * Retrieves a resource as a byte array.
	 * @param in the input stream of the resource, the input stream will be closed automatically
	 * @return the resource as byte array
	 * @throws IOException when the resource could not be read
	 */
	public static byte[] toByteArray(InputStream in)
	throws IOException
	{
		try {
			int bufferSize = in.available();
			if (bufferSize <= 0) {
				bufferSize = 8*1024;
			}
			byte[] buffer = new byte[ bufferSize ];
			ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize);
			int read;
			while ( (read = in.read(buffer, 0, bufferSize)) != -1) {
				out.write(buffer, 0, read);
			}
			return out.toByteArray();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException( e.toString() );
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

}
