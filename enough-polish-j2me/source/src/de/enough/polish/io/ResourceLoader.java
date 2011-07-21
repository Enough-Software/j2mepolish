//#condition polish.usePolishGui
/*
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

package de.enough.polish.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads resources.
 * 
 * @author Robert Virkus
 */
public interface ResourceLoader {
	
	/**
	 * Retrieves a resource as an InputStream
	 * @param url the URL of the resource
	 * @return the input stream
	 * @throws IOException when the resource could not be retrieved
	 */
	InputStream getResourceAsStream( String url )
	throws IOException;
	
	/**
	 * Closes an input stream belonging to an URL
	 * @param url the URL
	 * @param in the corresponding input stream
	 * @throws IOException when the connection could not be closed
	 */
	void close( String url, InputStream in )
	throws IOException;

}
