/*
 * Created on Mar 4, 2010 at high noon.
 * 
 * Copyright (c) 2010 Andre Schmidt / Enough Software
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

/**
 * <p>
 * Some useful URL methods
 * </p>
 * 
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Mar 4, 2010 - asc creation
 * </pre>
 * @author Andre Schmidt, andre@enough.de
 */
public class UrlUtil {
	/**
	 * Returns the path of a url 
	 * @param url the url
	 * @return the path
	 */
	public static String getPath(String url) {
		int index = url.lastIndexOf('/');
		String host = url.substring(0, index);
		return host;
	}
}
