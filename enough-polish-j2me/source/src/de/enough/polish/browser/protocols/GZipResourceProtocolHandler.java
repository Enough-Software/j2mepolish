/*
 * Created on 29-Okt-2008 at 17:34:28.
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
package de.enough.polish.browser.protocols;

import de.enough.polish.browser.ProtocolHandler;
import de.enough.polish.util.zip.GZipInputStream;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.StreamConnection;

/**
 * Protocol handler for handling resources compressed with gzip.
 */
public class GZipResourceProtocolHandler extends ProtocolHandler
{
	/**
	 * Creates a new gzip resource protocol handler using gzip as the protocol name.
	 */
	public GZipResourceProtocolHandler()
	{
		this("gzip");
	}

	/**
	 * Creates a new gzip resource protocol handler using the specified protocol name.
	 * @param protocolName the name of the protocol - <code>gzip</code> by default.
	 */
	public GZipResourceProtocolHandler( String protocolName )
	{
		super(protocolName);
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.browser.ProtocolHandler#getConnection(java.lang.String)
	 */
	public StreamConnection getConnection(String url)
	throws IOException
	{
		url = url.substring( this.protocolName.length() + ":/".length() );
		if (url.length() > 0 && url.charAt(0) != '/') {
			url = '/' + url;
		}
		InputStream in = getClass().getResourceAsStream(url);
		if (in == null) {
			throw new IOException("unknown resource: " + url);
		}
		return new ResourceConnection( new GZipInputStream(in, GZipInputStream.TYPE_GZIP, true) );
	}
}
