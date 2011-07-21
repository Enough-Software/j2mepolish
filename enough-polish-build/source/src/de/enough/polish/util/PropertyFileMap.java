/*
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PropertyFileMap {
	
	private final Map arrayIndecesByKeys;
	private final ArrayList linesList;
	private int firstSpaceSplitIndex = -1;

	public PropertyFileMap() {
		super();
		this.linesList = new ArrayList();
		this.arrayIndecesByKeys = new HashMap();
	}
	
	public void readFile( File file ) 
	throws FileNotFoundException, IOException 
	{
		System.out.println("emulator.properties:  loading file [" + file.getAbsolutePath() + "]");
		String[] lines = FileUtil.readTextFile( file );
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			this.linesList.add( line );
			int splitPos = line.indexOf(':');
			if ( splitPos == -1 ) {
				splitPos = line.indexOf('=');
				if ( splitPos != -1 && this.firstSpaceSplitIndex == -1) {
					this.firstSpaceSplitIndex = i;
				}
			}
			if ( splitPos != -1 ) {
				String key = line.substring( 0, splitPos ).trim();
				this.arrayIndecesByKeys.put( key, new Integer( i ) );
			}
		}
	}
	
	public void writeFile( File file ) 
	throws IOException 
	{
		FileUtil.writeTextFile( file, this.linesList );
	}
	
	public void put( String key, String value ) {
		Integer index = (Integer) this.arrayIndecesByKeys.get( key );
		if ( index != null ) {
			this.linesList.set(index.intValue(), key + ": " + value );
		} else if (this.firstSpaceSplitIndex != -1) {
			this.linesList.add( this.firstSpaceSplitIndex,  key + ": " + value );
		} else {
			this.linesList.add( key + ": " + value );
		}
	}
	
	public int size() {
		return this.linesList.size();
	}

}
