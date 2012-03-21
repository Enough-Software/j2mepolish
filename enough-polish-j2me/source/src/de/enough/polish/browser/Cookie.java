//#condition polish.usePolishGui

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
package de.enough.polish.browser;

import de.enough.polish.util.TextUtil;
import de.enough.polish.util.TimePoint;

/**
 * <p>Manages a single cookie.</p>
 * 
 * <p>Copyright Enough Software 2012</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Cookie {
	String nameValuePair;
	String name;
	String value;
	String domain;
	String path;
	TimePoint expires;
	
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

	public String getNameValuePair() {
		return this.nameValuePair;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	public String getDomain() {
		return this.domain;
	}

	public String getPath() {
		return this.path;
	}

	public TimePoint getExpires() {
		return this.expires;
	}
	
	public boolean equals( Object o) {
		if (!(o instanceof Cookie)) {
			return false;
		}
		Cookie other = (Cookie)o;
		return (this.name.equals(other.name))
				&& (this.domain == null || this.domain.equals(other.domain));
	}
	
	public int hashCode() {
		int hashCode = this.name.hashCode();
		if (this.domain != null) {
			hashCode |= this.domain.hashCode();
		}
		return hashCode;
	}

	public boolean matchesDomain(String targetDomain) {
		if (this.domain == null) {
			return true;
		}
		return (targetDomain.indexOf(this.domain) != -1);
	}

}
