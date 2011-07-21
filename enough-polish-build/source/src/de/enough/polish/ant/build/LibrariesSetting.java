/*
 * Created on 14-Apr-2005 at 22:11:22.
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
package de.enough.polish.ant.build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import de.enough.polish.BuildException;
import de.enough.polish.Environment;

import de.enough.polish.ant.Setting;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.IntegerIdGenerator;

/**
 * <p>A container for several &lt;library&gt; tags.</p>
 *
 * <p>Copyright Enough Software 2005-2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LibrariesSetting extends Setting {
	/**
	 * Key for the environment.
	 * @see Environment#get(String)
	 */
	public static final String KEY_ENVIRONMENT = "key.LibrariesSetting";
	private final ArrayList libraries;
	private final IntegerIdGenerator integerIdGenerator;
	private Environment environment;

	/**
	 * Creates a new libraries setting
	 * @param environment the environment
	 */
	public LibrariesSetting( Environment environment ) {
		super();
		this.environment = environment;
		this.libraries = new ArrayList();
		this.integerIdGenerator = new IntegerIdGenerator();
	}
	
	public void addConfiguredLibrary( LibrarySetting setting ) {
		if (!setting.isValid() ) {
			throw new BuildException("Invalid <library>-element: you need to define either the \"file\", \"files\" or the \"dir\" attribute of the <library>-element in your build.xml file.");
		}
		this.libraries.add( setting );
	}
	
	/**
	 * Adds a library to this setting.
	 * @param path the path to the library
	 */
	public void addLibrary(File path)
	{
		if (!path.exists()) {
			throw new BuildException("Library path \"" + path.getAbsolutePath() + "\" does not exist.");
		}
		LibrarySetting setting = new LibrarySetting();
		if (path.isDirectory()) { 
			setting.setDir( path );
		} else {
			setting.setFile( path );
		}
		addConfiguredLibrary(setting);
	}
	
	public LibrarySetting[] getLibraries() {
		LibrarySetting[] libs = new LibrarySetting[ this.libraries.size() ];
		this.libraries.toArray( libs );
		return libs;
	}

	/**
	 * @param setting
	 */
	public void add(LibrariesSetting setting) {
		this.libraries.addAll( setting.libraries );
	}
	

	/**
	 * Copies all third party binary libraries to the cache.
	 * 
	 * @param binaryBaseDir the base dir of the cache
	 * @return true when at least one library had to be written again
	 * @throws IOException when a file could not be read or written
	 * @throws FileNotFoundException when a file was not found
 	 */
	public boolean copyToCache( File binaryBaseDir ) 
	throws FileNotFoundException, IOException 
	{
		File idsFile = new File( binaryBaseDir, "library-ids.txt" );
		if (idsFile.exists()) {
			Map idsMap = FileUtil.readPropertiesFile( idsFile );
			this.integerIdGenerator.setIdsMap(idsMap);
		}
		LibrarySetting[] libs = getLibraries();
		boolean updated = false;
		for (int i = 0; i < libs.length; i++) {
			LibrarySetting lib = libs[i];
			int id = this.integerIdGenerator.getId( lib.getPath(this.environment), true );
			updated |= lib.copyToCache(binaryBaseDir, Integer.toString(id),  this.environment);
		}
		if ( this.integerIdGenerator.hasChanged() ) {
			Map idsMap = this.integerIdGenerator.getIdsMap();
			FileUtil.writePropertiesFile( idsFile, idsMap );
		}
		return updated;
	}



}
