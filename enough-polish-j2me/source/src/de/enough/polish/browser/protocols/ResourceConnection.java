/*
 * Created on 11-Jan-2006 at 19:20:28.
 * 
 * Copyright (c) 2009 - 2009 Michael Koch / Enough Software
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;

/**
 * A StreamConnection for resources from inside the application jar.
 * The urls of resources always start with <code>resource://</code>. 
 */
public class ResourceConnection
implements StreamConnection
{
	private String path;
	private InputStream inputStream;

	/**
	 * Creates a new resource connection for the specified URL
	 * @param url the URL, e.g. /image.png.
	 */
	public ResourceConnection(String url)
	{
		// Resource paths are always absolute.
		if (url.length() > 0 && url.charAt(0) != '/')
		{
			url = '/' + url;
		}
		this.path = url;
	}
	
	/**
	 * Creates a new resource connection for the specified input stream.
	 * 
	 * @param in the input stream
	 */
	public ResourceConnection(InputStream in)
	{
		this.path = "";
		this.inputStream = in;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.Connection#close()
	 */
	public void close() throws IOException
	{
		if (this.inputStream != null)
		{
			try {
				this.inputStream.close();
			} catch (Exception e) {
				// ignore
			} finally{
				this.inputStream = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.InputConnection#openDataInputStream()
	 */
	public DataInputStream openDataInputStream() throws IOException
	{
		return new DataInputStream(openInputStream());
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.OutputConnection#openDataOutputStream()
	 */
	public DataOutputStream openDataOutputStream() throws IOException
	{
		// Resource connections don't support output streams.
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.InputConnection#openInputStream()
	 */
	public synchronized InputStream openInputStream() throws IOException
	{
		if (this.inputStream == null)
		{
			this.inputStream = getClass().getResourceAsStream(this.path);

			if (this.inputStream == null)
			{
				throw new IOException("resource not found: " + this.path );
			}
		}

		return this.inputStream;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.OutputConnection#openOutputStream()
	 */
	public OutputStream openOutputStream() throws IOException
	{
		// Resource connections don't support output streams.
		return null;
	}
}
