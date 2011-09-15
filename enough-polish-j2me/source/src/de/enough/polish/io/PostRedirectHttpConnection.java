//#condition polish.usePolishGui || polish.midp

/*
 * Created on 13-Jan-2007 at 10:12:48.
 * 
 * Copyright (c) 2009 Michael Koch / Enough Software
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
package de.enough.polish.io;

import java.io.IOException;

import javax.microedition.io.HttpConnection;

import de.enough.polish.util.HashMap;
import de.enough.polish.util.TextUtil;

/**
 * Provides a <code>HttpConnection</code> that supports HTTP redirects and allows easy creation of HTTP POST requests. This
 * class is compatible to <code>javax.microedition.io.HttpConnection</code>.
 * 
 * <p>
 * When connecting to an URL and a HTTP redirect is return this class follows
 * the redirect and uses the following HTTP connection. This works over multiple
 * levels. By default five redirects are supported. The number of supported
 * redirects can be tuned by setting the preprocessing variable
 * <code>polish.Browser.MaxRedirects</code> to some integer value.
 * </p>
 * <p>
 * You can specify a timeout with the  <code>PostRedirectHttpConnection( String url, int timeout)</code> or <code>PostRedirectHttpConnection( String url, Hashmap requestProperties, int timeout)</code> constructor. On
 * MIDP and BlackBerry devices this will result in an additional thread being launched that waits for the specific time and then closes the connection
 * in case it has so far not succeeded.
 * </p>
 * 
 * @see HttpRedirectConnection
 */
public class PostRedirectHttpConnection extends RedirectHttpConnection
{
	
	private final StringBuffer body;

	/**
	 * Creates a new http connection that understands redirects.
	 * 
	 * @param url  the url to connect to
	 * @throws IOException when Connector.open() fails
	 */
	public PostRedirectHttpConnection(String url) throws IOException
	{
		this(url, null, 0);
	}

	/**
	 * Creates a new http connection that understands redirects.
	 * 
	 * @param url  the url to connect to
	 * @throws IOException when Connector.open() fails
	 */
	public PostRedirectHttpConnection(String url, int timeout) throws IOException
	{
		this(url, null, timeout);
	}

	/**
	 * Creates a new http connection that understands redirects.
	 * 
	 * @param url  the url to connect to
	 * @param requestProperties the request properties to be set for each http request
	 * @throws IOException when Connector.open() fails
	 */
	public PostRedirectHttpConnection(String url, HashMap requestProperties)
	throws IOException
	{
		this( url, requestProperties, 0 );
	}
	
	/**
	 * Creates a new http connection that understands redirects.
	 * 
	 * @param url  the url to connect to
	 * @param requestProperties the request properties to be set for each http request
	 * @throws IOException when Connector.open() fails
	 */
	public PostRedirectHttpConnection(String url, HashMap requestProperties, int timeout)
	throws IOException
	{
		super( url, requestProperties, timeout );
		setRequestMethod( HttpConnection.POST );
		setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		this.body = new StringBuffer();
	}
	
	/**
	 * Adds a parameter for this post request
	 * @param name the name of the parameter
	 * @param value the value
	 */
	public void addPostParameter( String name, String value) {
		if (this.body.length() > 0) {
			this.body.append('&');
		}
		this.body.append( TextUtil.encodeUrl(name) ).append( '=' ).append( TextUtil.encodeUrl(value) );
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.RedirectHttpConnection#ensureConnectionCreated()
	 */
	protected synchronized void ensureConnectionCreated() 
	throws IOException
	{
		if (this.httpConnection == null)
		{
			byte[] data = this.body.toString().getBytes();
			openOutputStream().write(data, 0, data.length);
		}
		super.ensureConnectionCreated();
	}

}