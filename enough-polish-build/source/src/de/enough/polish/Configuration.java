/*
 * Created on Nov 15, 2007 at 3:48:59 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

import de.enough.polish.ant.PolishTask;

/**
 * <p>Allows to configure J2ME Polish programmatically.</p>
 * <p>You can start a configuration automatically by specifying the Ant property
 *    <i>polish.build.configuration.class</i> that contains the fully classified name of the configuration class and
 *    <i>polish.build.configuration.path</i> that contains classpath for the configuration class.
 * </p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Nov 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Configuration
{

	protected  PolishTask polishTask;
	private HashMap librariesByPath;


	/**
	 * Configures this configuration initially and allows the configuration to add J2ME Polish extensions etc.
	 * The default implementation stores the task under the instance variable "polishTask".
	 * 
	 * @param task the J2ME Polish task
	 * @param env the environment
	 */
	public void configure( PolishTask task, Environment env ) {
		this.polishTask = task;
	}
	
	/**
	 * Allows this configuration to add settings at the start of the initialization of a new build.
	 * The default implementation doesn't do anything.
	 * 
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @param env the environment
	 */
	public void preInitialize( Device device, Locale locale, Environment env ) {
		// default implementation does nothing
	}
	

	/**
	 * Allows this configuration to add settings at the end of the initialization of a new build.
	 * The default implementation doesn't do anything.
	 * 
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @param env the environment
	 */
	public void postInitialize( Device device, Locale locale, Environment env ) {
		// default implementation does nothing
	}
	
	/**
	 * Adds a binary library to J2ME Polish.
	 * @param path the path to the library
	 * @param env the environment
	 */
	public void addLibrary( String path, Environment env ) {
		addLibrary( env.resolveFile(path) );
	}
	
	/**
	 * Adds a binary library to J2ME Polish.
	 * @param path the path to the library
	 */
	public void addLibrary( File path ) {
		if (this.librariesByPath == null) {
			this.librariesByPath = new HashMap();
		} else {
			if (this.librariesByPath.get(path) != null) { // this library has beed added already, ignore:
				return;
			}
		}
		this.librariesByPath.put(path, Boolean.TRUE );
		this.polishTask.addBinaryLibrary( path );
	}
	
	/**
	 * Adds a source directory to the build path.
	 * 
	 * @param path the path to the source directory
	 * @param env the environment
	 */
	public void addSourceDir( String path, Environment env ) {
		addSourceDir( env.resolveFile(path));
	}
	
	/**
	 * Adds a source directory to the build path.
	 * 
	 * @param path the path to the source directory
	 */
	private void addSourceDir(File path)
	{
		if (this.librariesByPath == null) {
			this.librariesByPath = new HashMap();
		} else {
			if (this.librariesByPath.get(path) != null) { // this source dir has beed added already, ignore:
				return;
			}
		}
		this.polishTask.addSourceDir( path );
	}

	/**
	 * Aborts the build process by throwing a BuildException with the specified message.
	 * @param message the message.
	 */
	public void abortBuild( String message ) {
		throw new BuildException( message );
	}
}
