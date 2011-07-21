/*
 * Created on 07-Nov-2005 at 02:12:59.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.propertyfunctions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.enough.polish.Environment;

/**
 * <p>Encodes the given parameter for URLs in the UTF-8 encoding.</p>
 * <p>Sample usage:
 * <pre>
 * //#= String url = "http://myserver.com?vendor=${ encodeurl( MIDlet-Vendor )}"; 
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *         07-Nov-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class EncodeUrlFunction extends PropertyFunction {

	/**
	 * Creates a new URL encoding function
	 */
	public EncodeUrlFunction() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.propertyfunctions.PropertyFunction#process(java.lang.String, java.lang.String[], de.enough.polish.Environment)
	 */
	public String process(String input, String[] arguments, Environment env) {
		try {
			return URLEncoder.encode( input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// should never happen:
			//e.printStackTrace();
			throw new RuntimeException("Unable to urlencode String [" + input + "]: " + e.toString(), e );
		}
	}

}
