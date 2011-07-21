/*
 * Created on 06-Apr-2006 at 04:10:44.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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

import java.io.File;
import java.io.IOException;

import de.enough.polish.Environment;

/**
 * <p>Checks whether a resource exists</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        06-Apr-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ExistsFunction extends ResourceFuntion {

	/**
	 * Creates a new exists function
	 */
	public ExistsFunction() {
		super( false );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.propertyfunctions.ResourceFuntion#process(java.io.File, java.lang.String, java.lang.String[], de.enough.polish.Environment)
	 */
	public String process(File resource, String resourceName,
			String[] arguments, Environment env) 
	throws IOException 
	{
		if (resource == null || !(resource.exists())) {
			return "false";
		} else {
			return "true";
		}
	}

}
