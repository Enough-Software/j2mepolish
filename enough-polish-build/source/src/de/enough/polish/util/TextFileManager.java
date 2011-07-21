/*
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
package de.enough.polish.util;

import java.util.HashMap;

/**
 * <p>Manages a collection of textfiles.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        09.01.2005 - rob creation
 * </pre>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TextFileManager {
	
	private final HashMap filesByPath;
	private final HashMap filesByClassName;
	private final HashMap filesByName;
	private final HashMap packagesByName;

	/**
	 * Creates a new manager.
	 */
	public TextFileManager() {
		this.filesByPath = new HashMap();
		this.filesByClassName = new HashMap();
		this.filesByName = new HashMap();
		this.packagesByName = new HashMap();
	}
	
	/**
	 * Adds a file to this manager.
	 * 
	 * @param file the text-file
	 */
	public void addTextFile( TextFile file ) {
		this.filesByPath.put( file.getFilePath(), file );
		this.filesByName.put( file.getFileName(), file );
		String className = file.getClassName();
		if (className != null) {
//			System.out.println("adding class [" + className + "]");
			this.filesByClassName.put( className, file );
			int dotIndex = className.lastIndexOf('.');
			if (dotIndex != -1) {
				String packageName = className.substring( 0, dotIndex );
				//System.out.println("adding package [" + packageName + "]");
				this.packagesByName.put( packageName, Boolean.TRUE );
				this.packagesByName.put( packageName + ".*", Boolean.TRUE );
			}
		}
	}
	
	/**
	 * Determines whether the import is listed in this manager.
	 * 
	 * @param importName the name of the import, e.g. "de.enough.polish.*" or "com.company.MyClass".
	 * @return true when the import is listed here. 
	 */
	public boolean containsImport( String importName ) {
		int multipleIndex = importName.indexOf(';');
		if ( multipleIndex != -1 ) {
			importName = importName.substring( 0, multipleIndex );
		}
		boolean result = this.filesByClassName.containsKey(importName)
				         || this.packagesByName.containsKey(importName);
//		System.out.println("contains import [" + importName + "]: " + result + ", stored classes: " + this.filesByClassName.size() );		
		return result;
	}
	
	public boolean containsPackage( String packageName ) {
		return this.packagesByName.containsKey(packageName);
	}

}
