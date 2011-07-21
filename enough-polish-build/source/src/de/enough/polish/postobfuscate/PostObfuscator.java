/*
 * Created on 24-Mar-2005 at 15:44:24.
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
package de.enough.polish.postobfuscate;

import java.io.File;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;
import de.enough.polish.ExtensionManager;
import de.enough.polish.ant.build.PostObfuscatorSetting;

/**
 * <p>Is the base class for any custom post obfuscators.</p>
 * <p>
 * Custom post obfuscators can modify the obfuscated sources of an application before the 
 * application is preverified and packages.
 * </p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        24-Mar-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class PostObfuscator extends Extension {

	/**
	 * Creates a new instance of a custom post obfuscator.
	 */
	protected PostObfuscator() {
		super();
	}
	
	
	
	/**
	 * Retrieves a new PostObfuscator
	 *  
	 * @param postObfuscatorSetting the ant settings
	 * @param manager the extension manager 
	 * @param environment the environment settings
	 * @return a new instance of a post compiler
	 * @throws BuildException when the class could not be loaded or initialized
	 */
	public static PostObfuscator getInstance( PostObfuscatorSetting postObfuscatorSetting, ExtensionManager manager, Environment environment )
	throws BuildException
	{
		try {
			return (PostObfuscator) manager.getExtension( ExtensionManager.TYPE_POSTOBFUSCATOR, postObfuscatorSetting, environment );
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("Unable to load post obfuscator class [" + postObfuscatorSetting.getClassName() + "/" + postObfuscatorSetting.getName() + "]: " + e.toString(), e );
		}
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#execute(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void execute(Device device, Locale locale, Environment env)
	throws BuildException 
	{
		this.environment = env;
		postObfuscate( new File( device.getClassesDir() ), device );
	}
	
	/**
	 * Postobfuscates the project for the given target device.
	 * 
	 * @param classesDir the directory that contains all compiled classes
	 * @param device the current target device
	 * @throws BuildException when post-obfuscation fails
	 */
	public abstract void postObfuscate( File classesDir, Device device )
	throws BuildException;
	
	
	/**
	 * Subclasses can override this method for setting a different bootclasspath for the current device.
	 * This method is called before the application is compiled.
	 * The default implementation returns the specified bootclasspath.
	 * When several postcompilers try to change this path it can result in complications.
	 * 
	 * @param device the current device
	 * @param bootClassPath the current bootclasspath
	 * @return the appropriate bootclasspath for the current device, usually the same that has been given.
	 */
	public String verifyBootClassPath( Device device, String bootClassPath ) {
		return bootClassPath;
	}
	
	/**
	 * Subclasses can override this method for setting a different classpath for the current device.
	 * This method is called before the application is compiled.
	 * The default implementation returns the specified classpath.
	 * 
	 * @param device the current device
	 * @param classPath the current classpath
	 * @return the appropriate classpath for the current device, usually the same that has been given.
	 */
	public String verifyClassPath( Device device, String classPath ) {
		return classPath;
	}
	
	/**
	 * Retrieves the settings for this post compiler.
	 * 
	 * @return the settings
	 */
	public PostObfuscatorSetting getSetting() {
		return (PostObfuscatorSetting) this.extensionSetting;
	}
}
