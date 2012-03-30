/*
 * Created on 20-March-2012 at 19:20:28.
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
package de.enough.polish.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.HttpConnection;

import de.enough.polish.util.ArrayList;

/**
 * Manages cookie for a HttpConnection
 * @author Robert Virkus, j2mepolish@enough.de
 *
 */
public class CookieManager
implements Externalizable
{

	private static final int VERSION = 100;
	
	private final ArrayList cookiesList;
	
	/**
	 * Creates a new cookie manager
	 */
	public CookieManager() {
		this.cookiesList = new ArrayList();
	}
	
	/**
	 * Extracts all cookies from the given connection
	 * @param connection the HttpConnection
	 * @return the number of cookies that have been transmitted
	 * @throws IOException when there was a network error
	 * @see #setCookies(String,HttpConnection) for the reverse process
	 */
	public int extractCookies( HttpConnection connection ) throws IOException {
		int foundCookies = 0;
		int index = 0;
		while (true) {
			String key = connection.getHeaderFieldKey(index);
			if (key == null && index > 0) {
				break;
			}
			if ("set-cookie".equalsIgnoreCase(key)) {
				String definition = connection.getHeaderField(index);
				addCookie(definition);
				foundCookies++;
			}
			index++;
		}
		return foundCookies;
	}
	
	/**
	 * 
	 * @param url
	 * @param connection
	 * @throws IOException 
	 * @see #extractCookies(HttpConnection)
	 */
	public void setCookie( String url, HttpConnection connection) throws IOException {
		String cookie = getCookiesForUrl(url);
		connection.setRequestProperty("cookie", cookie);
	}

	/**
	 * Adds a single cookie with the specified definition
	 * @param setCookieDefinition the definition such as "name=value; expires=Tue, 20 Mar 2012 08:49:37 GMT; domain=.mydomain.com"
	 */
	public void addCookie( String setCookieDefinition) {
		//#debug
		System.out.println("adding cookie " + setCookieDefinition);
		addCookie( new Cookie(setCookieDefinition) );
	}
	
	/**
	 * Adds a single cookie.
	 * When the cookie is expired, a previously set cookie will actually be deleted from this mananager.
	 * If a cookie with the same name and domain exists, it will be replaced
	 * @param cookie the cookie
	 */
	public void addCookie( Cookie cookie) {
		int index = this.cookiesList.indexOf(cookie);
		if (index != -1) {
			if (cookie.isExpired()) {
				this.cookiesList.remove(index);
				//#debug
				System.out.println("Cookie expired: " + cookie);
			} else {
				this.cookiesList.set(index, cookie);
				//#debug
				System.out.println("Replacing cookie " + cookie);
			}
		} else if (!cookie.isExpired()) {
			//#debug
			System.out.println("Adding new cookie " + cookie);
			this.cookiesList.add(cookie);
		}
	}
	
	/**
	 * Retrieves the value for the "cookie" request header for the specified URL
	 * @param url the url such as "http://www.mydomain.com/path"
	 * @return the cookie for the specified URL
	 */
	public String getCookiesForUrl(String url) {
		Object[] cookies = this.cookiesList.getInternalArray();
		StringBuffer buffer = null;
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = (Cookie) cookies[i];
			if (cookie == null) {
				break;
			}
			if (cookie.matchesUrl(url)) {
				if (buffer == null) {
					buffer = new StringBuffer(cookie.getNameValuePair());
				} else {
					buffer.append("; ").append(cookie.getNameValuePair());
				}
			}
		}
		if (buffer == null) {
			return null;
		}
		//#debug
		System.out.println("Combined cookie: " + buffer);
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(VERSION);
		int size = this.cookiesList.size();
		out.writeInt(size);
		Object[] cookies = this.cookiesList.getInternalArray();
		for (int i=0; i<size; i++) {
			Cookie cookie = (Cookie) cookies[i];
			cookie.write(out);
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
		int size = in.readInt();
		for (int i=0; i<size; i++) {
			Cookie cookie = new Cookie(in);
			addCookie(cookie);
		}
	}
}
