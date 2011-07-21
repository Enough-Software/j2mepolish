/*
 * Created on 15-Apr-2004 at 11:01:05.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.webprocessor.util;

import java.io.*;
import java.util.ArrayList;

/**
 * <p>Loads resources either from the disk or from the jar file.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        15-Apr-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class ResourceUtil {
	
	private ClassLoader classLoader;
	
	public ResourceUtil( ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	
	/**
	 * Opens the specified resource.
	 * The caller needs to ensure that the resource is closed.
	 * 
	 * @param url the url to the resource, a '/'-separated path
	 * @return the InputStream for the specified resource.
	 * @throws FileNotFoundException when the specified resource could not be found
	 */
	public final InputStream open( String url ) 
	throws FileNotFoundException 
	{
		// check if url points to an existing file:
		File file = new File( url );
		if (file.exists()) {
			try {
				return new FileInputStream( file );
			} catch (FileNotFoundException e) {
				// should not be thrown, since we checked file.exists()
				// now try to get the specified resource from the class loader...
			}
		}
		InputStream in = this.classLoader.getResourceAsStream(url);
		if (in == null) {
			throw new FileNotFoundException("unable to open resource [" + url + "]: resource not found.");
		}
		return in;
	}


	/**
	 * Reads the specified text file and returns its content.
	 * 
	 * @param url the URL to the text file.
	 * @return a String array with the content of the specified file.
	 * @throws FileNotFoundException when the specified resource could not be found
	 * @throws IOException when the resource could not be read
	 */
	public String[] readTextFile(String url ) 
	throws FileNotFoundException, IOException 
	{
		InputStream is = open( url );
		ArrayList lines = new ArrayList();
		BufferedReader in = new BufferedReader( new InputStreamReader(is));
		String line;
		while ((line = in.readLine()) != null) {
			lines.add( line );
		}
		in.close();
		is.close();
		return (String[]) lines.toArray( new String[ lines.size() ]);
	}
}
