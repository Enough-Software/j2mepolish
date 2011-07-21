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
package de.enough.polish.precompile;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;
import de.enough.polish.ExtensionManager;
import de.enough.polish.ant.build.PreCompilerSetting;
import de.enough.polish.util.FileUtil;

/**
 * <p>Is the base class for any custom pre compilers.</p>
 * <p>
 * Custom pre compilers can modify the bytecode of application classes - this is done for all classes of all binary libraries and all classes that have been build in the last build.
 * In most cases a LibraryProcessor should be extended instead.
 * </p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        24-Mar-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class PreCompiler extends Extension {

	/**
	 * Creates a new instance of a custom pre compiler.
	 */
	protected PreCompiler() {
		super();
	}
	
	
	
	/**
	 * Retrieves a new PreCompiler
	 *  
	 * @param preCompilerSetting the ant settings
	 * @param manager the extension manager 
	 * @param environment the environment settings
	 * @return a new instance of a post compiler
	 * @throws BuildException when the class could not be loaded or initialized
	 */
	public static PreCompiler getInstance( PreCompilerSetting preCompilerSetting, ExtensionManager manager, Environment environment )
	throws BuildException
	{
		try {
			PreCompiler preCompiler = (PreCompiler) manager.getExtension( ExtensionManager.TYPE_PRECOMPILER, preCompilerSetting, environment );
			return preCompiler;
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("Unable to load pre compiler class [" + preCompilerSetting.getClassName() + "/" + preCompilerSetting.getName() + "]: " + e.toString(), e );
		}
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#execute(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void execute(Device device, Locale locale, Environment env)
	throws BuildException 
	{
		System.out.println("Invoking precompiler [" + getClass().getName() + "]...");
		preCompile( new File( device.getClassesDir() ), device );
	}
	
	/**
	 * Postcompiles the project for the given target device.
	 * 
	 * @param classesDir the directory that contains all compiled classes
	 * @param device the current target device
	 * @throws BuildException when post-compiling fails
	 */
	public abstract void preCompile( File classesDir, Device device )
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
	public PreCompilerSetting getSetting() {
		return (PreCompilerSetting) this.extensionSetting;
	}

	 /**
	  * Helper method to find all files to post compile in a given	directory and all its subdirectories.
	  * 
	  * @param classesDir the directory that contains all compiled	classes
	  * @param filter the filter to apply on the file names, or null
	  * @return an array list of file names
	  */
	protected ArrayList findFiles( File classesDir, FilenameFilter filter )
	{
		ArrayList list = new ArrayList();
		String[] files = FileUtil.filterDirectory( classesDir,	".class", true);
		if (filter != null) {
			for (int i = 0; i < files.length; i++) {
				if (filter.accept(classesDir, files[i])) {
					list.add(files[i]);
				}
			}
		}
		else {
			for (int i = 0; i < files.length; i++) {
			list.add(files[i]);
			}
		}
		return list;
	}

}
