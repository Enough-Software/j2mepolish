/*
 * Created on 22-May-2005 at 23:50:41.
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
package de.enough.polish.preverify;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import de.enough.polish.BuildException;
import org.apache.tools.ant.types.Path;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;

/**
 * <p>Preverifies the project for a specific device.</p>
 *
 * <p>Copyright Enough Software 2005 - 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class Preverifier extends Extension {
	
	public static final String BUILDCONTROL_PREVERIFIER_ENABLED = "polish.buildcontrol.preverifier.enabled";

	/**
	 * Key for accessing the used preverifier in the Environment.
	 * @see Environment#get(String)
	 */
	public static final String KEY_ENVIRONMENT = "key.preverifier";

	/**
	 * Key for accessing the executable in the Environment.
	 * @see Environment#get(String)
	 */
	public static final String KEY_EXECUTABLE = "preverify.executable";


	/**
	 * Key for accessing the preverify target directory in the Environment.
	 * @see Environment#get(String)
	 */
	public static final String KEY_TARGET = "preverify.target";

	
	/**
	 * The executable of the WTK that preverifies the project.
	 * Beware: this can be null in the preverify( ... ) method!
	 */
	protected File preverifyExecutable;

	/**
	 * Creates a new preverifier
	 */
	public Preverifier() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#execute(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void execute(Device device, Locale locale, Environment env)
			throws BuildException 
	{
		File sourceDir = new File( device.getClassesDir() );
		if ( !sourceDir.exists() ) {
			sourceDir = new File( this.antProject.getBaseDir(), device.getClassesDir() );
		}
		File targetDir = (File) env.get("preverify.target");
		this.preverifyExecutable = (File) env.get( "preverify.executable" );
		
		Path bootClassPath = new Path( this.antProject, device.getBootClassPath() );
		Path classPath = null;
		String classPathStr = device.getClassPath();
		if ( classPathStr != null ) {
			classPath = new Path( this.antProject, classPathStr );
		}
		
		try {
			preverify( device, sourceDir, targetDir, bootClassPath, classPath );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to preverify for device [" + device.getIdentifier() + "]: " + e.toString() );
		}
	}
	
	/**
	 * Preverifies the classes for the target device.
	 *  
	 * @param device the target device 
	 * @param sourceDir the directory containing the class files
	 * @param targetDir the directory to which the preverfied class files should be written to
	 * @param bootClassPath the boot class path of the device
	 * @param classPath  the class path of the device, null when the device does not support additional APIs
	 * @throws IOException when the process could not be executed
	 */
	public abstract void preverify(Device device, File sourceDir, File targetDir,
			Path bootClassPath, Path classPath) 
	throws IOException; 

}
