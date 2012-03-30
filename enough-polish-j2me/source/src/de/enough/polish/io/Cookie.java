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

import de.enough.polish.util.TextUtil;
import de.enough.polish.util.TimePoint;

/**
 * <p>Manages a single cookie.</p>
 * 
 * <p>Copyright Enough Software 2012</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Cookie 
implements Externalizable
{
	private static final int VERSION = 100;
	private String nameValuePair;
	private String name;
	private String value;
	private String domain;
	private String path;
	private TimePoint expires;
	
	/**
	 * Creates a new cookie from the given data input stream
	 * @param in the stream
	 * @throws IOException when reading fails
	 */
	public Cookie(DataInputStream in) 
	throws IOException 
	{
		read(in);
	}
	
	/**
	 * Creates a new cookie
	 * @param setCookie the cookie, e.g.  "name=value; expires=Thu, 19-Apr-2012 20:47:22 GMT; path=/; domain=.enough.de; httponly"
	 */
	public Cookie(String setCookie) {
		String[] values = TextUtil.splitAndTrim(setCookie, ';');
		String nameValuePair = values[0];
		int equalsPos = nameValuePair.indexOf('=');
		this.name = nameValuePair.substring(0, equalsPos);
		this.value = nameValuePair.substring(equalsPos+1);
		this.nameValuePair = nameValuePair;
		for (int i = 1; i < values.length; i++) {
			String string = values[i];
			if (string.startsWith("expires=") || string.startsWith("Expires=")) {
				String expiresString = string.substring("expires=".length());
				this.expires = TimePoint.parseCookieExpires(expiresString);
			} else if (string.startsWith("domain=") || string.startsWith("Domain=")) {
				this.domain = string.substring("domain=".length());
			} else if (string.startsWith("path=") || string.startsWith("Path=")) {
				this.path = string.substring("path=".length());				
			}
		}
	}
	
	/**
	 * Checks if this cookie is expired
	 * @return true when it is expired
	 */
	public boolean isExpired() {
		return ( (this.expires != null) && (System.currentTimeMillis() > this.expires.getTimeInMillis()) );
	}
	
	/**
	 * Checks if this cookie should be deleted after the session
	 * @return true when the cookie is for the session only
	 */
	public boolean isSessionOnly() {
		return (this.expires == null);
	}

	/**
	 * Retrieves the name and value separated by an equals sign
	 * @return name and value
	 * @see #getName()
	 * @see #getValue()
	 */
	public String getNameValuePair() {
		return this.nameValuePair;
	}

	/**
	 * Retrieves the name of this cookie
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retrieves the value of this cookie
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Retrieves the domain of this cookie
	 * @return the domain
	 */
	public String getDomain() {
		return this.domain;
	}

	/**
	 * Retrieves the path of this cookie
	 * @return the path
	 */ 
	public String getPath() {
		return this.path;
	}
	
	/**
	 * Retrieves the expires setting of this cookie
	 * @return the timepoint when this cookie expires
	 */
	public TimePoint getExpires() {
		return this.expires;
	}
	
	/**
	 * Cookies are deemed equals when they have the same name and domain
	 * @return true when the given object is a cookie and the names and domains match
	 */
	public boolean equals( Object o) {
		if (!(o instanceof Cookie)) {
			return false;
		}
		Cookie other = (Cookie)o;
		return (this.name.equals(other.name))
				&& ((this.domain == null && other.domain == null) 
					|| this.domain.equals(other.domain));
	}
	
	/**
	 * The hashcode is calculated by or-ing the hascodes of the name and domain of this cookie
	 * @return the hashcode of this cookie
	 */
	public int hashCode() {
		int hashCode = this.name.hashCode();
		if (this.domain != null) {
			hashCode |= this.domain.hashCode();
		}
		return hashCode;
	}

	/**
	 * Checks if this cookie matches the given url
	 * @param url the URL
	 * @return true when this cookie is applicable for the given URL
	 */
	public boolean matchesUrl(String url) {
		if (this.domain == null) {
			return true;
		}
		return (url.indexOf(this.domain) != -1);
	}

	/**
	 * Gives out useful information about this cookie
	 * @return name and decoded value, domain, path and expires settings along with the identity
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString())
			.append(' ').append(this.name).append('=').append(TextUtil.decodeUrl(this.value));
		if (this.domain != null) {
			buffer.append("; domain=").append(this.domain);
		}
		if (this.path != null) {
			buffer.append("; path=").append(this.path);
		}
		if (this.expires != null) {
			buffer.append("; expires=").append(this.expires.toRfc3339());
		}
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		out.writeUTF(this.name);
		out.writeUTF(this.value);
		boolean isNotNull = (this.domain != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.domain);
		}
		isNotNull = (this.path != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.path);
		}
		isNotNull = (this.expires != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			this.expires.write(out);
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
		this.name = in.readUTF();
		this.value = in.readUTF();
		this.nameValuePair = this.name + "=" + this.value;
		boolean isNotNull = in.readBoolean();
		if (isNotNull) {
			this.domain = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.path = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.expires = new TimePoint(in);
		}
	}
}
