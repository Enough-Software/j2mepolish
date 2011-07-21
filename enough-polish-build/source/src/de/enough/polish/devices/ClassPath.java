/*
 * Created on 09-Dec-2005 at 16:28:15.
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
package de.enough.polish.devices;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.util.StringUtil;

/**
 * <p>Manages the classpath for a device.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        09-Dec-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ClassPath {

	private final Device device;
	private final LibraryManager libraryManager;
	private boolean isInitalised;
	
	private String bootClassPath;
	private String classPath;
	private String[] classPaths;
	private String[] bootClassPaths;

	/**
	 * The device.
	 * 
	 * @param device the device 
	 * @param libraryManager the manager of optional and other device specific APIs.
	 */
	public ClassPath( Device device, LibraryManager libraryManager ) {
		this.device = device;
		this.libraryManager = libraryManager;
	}

	private void initialize() {
		Map duplicates = new HashMap();
		initBootClassPath(duplicates);
		initClassPath(duplicates);
//		String[] paths = this.bootClassPaths;
//		for (int i = 0; i < paths.length; i++)
//		{
//			String path = paths[i];
//			System.out.println("bootcp=" + path);
//		}
//		paths = this.classPaths;
//		for (int i = 0; i < paths.length; i++)
//		{
//			String path = paths[i];
//			System.out.println("cp=" + path);
//		}
		this.isInitalised = true;
	}
	
	private void initClassPath(Map duplicates) {
		String[] paths = this.libraryManager.getClassPaths(this.device);
		ArrayList pathList = new ArrayList();
		StringBuffer buffer = new StringBuffer();
		if (paths != null) {
			for (int i = 0; i < paths.length; i++) {
				String path = paths[i].replace('\\', '/');
				if (!duplicates.containsKey(path)) {
					duplicates.put(path, Boolean.TRUE);
					buffer.append( path );
					pathList.add( path );
					if ( i != paths.length -1 ) {
						buffer.append( File.pathSeparatorChar );
					}
				}
			}
		}
		this.classPaths = (String[]) pathList.toArray( new String[ pathList.size() ] );
		this.classPath = buffer.toString();
	}

	private void initBootClassPath(Map duplicates) {
		String bootPathStr = this.device.getCapability( "polish.build.bootclasspath" );
		if (bootPathStr == null) {
			throw new BuildException("IllegalState: device [" + this.device.identifier + "] has no build.BootClassPath defined!");
		}
		Environment env = this.device.getEnvironment(); 
		if (env == null) {
			throw new BuildException("IllegalState: device [" + this.device.identifier + "] has no environment!");
		}
		StringBuffer buffer = new StringBuffer();
		String[] paths = StringUtil.splitAndTrim( bootPathStr, ',' );
		File polishHome = (File) env.get("polish.home");
		polishHome = new File( polishHome, "import" );
		File importFolder = (File) env.get("polish.apidir");
		ArrayList cleanedPaths = new ArrayList( paths.length );
		for (int i = 0; i < paths.length; i++) {
			String pathElement = env.writeProperties( paths[i] );
			
			File lib = new File( pathElement ); 
			if ( ! lib.exists() ) {
				lib = new File( polishHome, pathElement );
				if ( ! lib.exists() ) {
					lib = new File( importFolder, pathElement );
					if ( ! lib.exists() ) {
						lib = new File( pathElement );
						if ( ! lib.exists() ) {
                            
						    String polishHomeString = "no polish home";
                            if(polishHome != null) {
                                polishHomeString = polishHome.getAbsolutePath();
                            }
                            String importFolderString = "no import folder";
                            if(importFolder != null) {
                                importFolderString = importFolder.getAbsolutePath();
                            }
							System.err.println("WARNING: unable to resolve boot classpath library [" + pathElement + "] of device [" + this.device.identifier +"]: file not found! default-dir=[" + 
                                                     polishHomeString + "], api-dir=[" + importFolderString + "]. If this leads to problems, please add those libraries to ${polish.home}/import.");
							lib = null;
						}
					}
				}
			}
			if (lib != null) {
				String path = lib.getAbsolutePath().replace('\\', '/');
				if (! duplicates.containsKey(path)) {
					duplicates.put(path, Boolean.TRUE);
					cleanedPaths.add( path );
					paths[i] = path;
					buffer.append( path );
					if ( i < paths.length - 1 ) {
						buffer.append( File.pathSeparatorChar );
					}
				}
			}
		}
		paths = (String[]) cleanedPaths.toArray( new String[ cleanedPaths.size() ] );
		
		// now add ccertain optional APIs to the bootclasspath if they require it (like the MMAPI on MIDP 2.0 systems):
		Library[] libraries = this.libraryManager.getLibraries( this.device );
		ArrayList additionalPaths = null;
		for (int i = 0; i < libraries.length; i++) {
			Library library = libraries[i];
			if (library.getPosition() == Library.POSITION_BOOTCP_PREPPEND) {
				String path = library.getPath().replace('\\', '/');
				if (! duplicates.containsKey(path)) {
					duplicates.put(path, Boolean.TRUE);
					if (additionalPaths == null) {
						additionalPaths = createList( paths );
					}
					additionalPaths.add( 0, path );
					buffer.insert(0, path + File.pathSeparatorChar );
				}
			} else if (library.getPosition() == Library.POSITION_BOOTCP_APPEND) {
				String path = library.getPath().replace('\\', '/');
				if (! duplicates.containsKey(path)) {
					duplicates.put(path, Boolean.TRUE);
					if (additionalPaths == null) {
						additionalPaths = createList( paths );
					}
					additionalPaths.add(  path );
					buffer.append( File.pathSeparatorChar ).append( path );
				}			
			}
		}
		if (additionalPaths != null) {
			paths = (String[]) additionalPaths.toArray( new String[ additionalPaths.size() ]);
		}
		this.bootClassPath = buffer.toString();
		this.bootClassPaths = paths;
	}


	private ArrayList createList(String[] paths) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			list.add( path );
		}
		return list;
	}

	public String getBootClassPath() {
		if (!this.isInitalised) {
			initialize();
		}
		return this.bootClassPath;
	}
	
	public String[] getBootClassPaths() {
		if (!this.isInitalised) {
			initialize();
		}
		return this.bootClassPaths;
	}

	public String getClassPath() {
		if (!this.isInitalised) {
			initialize();
		}
		return this.classPath;
	}


	public String[] getClassPaths() {
		if (!this.isInitalised) {
			initialize();
		}
		return this.classPaths;
	}

}
