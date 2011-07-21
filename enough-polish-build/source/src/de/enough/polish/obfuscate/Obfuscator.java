/*
 * Created on 22-Feb-2004 at 12:16:08.
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
package de.enough.polish.obfuscate;

import java.io.File;
import java.util.Locale;

import de.enough.polish.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;
import de.enough.polish.ExtensionManager;
import de.enough.polish.ant.build.ObfuscatorSetting;
import de.enough.polish.devices.LibraryManager;

/**
 * <p>An obfuscator is used to obfuscate and shrink the code.</p>
 * <p>This class is used to handle any external obfuscator.</p>
 * 
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        22-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class Obfuscator extends Extension {
	/**
	 * The symbol that can be queried in Environment.getSymbol() when you're interested whether this project has been obfuscated already.
	 * @see Environment#hasSymbol(String)
	 */
	public static final String SYMBOL_ENVIRONMENT_HAS_BEEN_OBFUSCATED = "polish.build.obfuscated";
	protected Project project;
	protected LibraryManager libraryManager;
	protected File libDir;
	protected ObfuscatorSetting setting;

	/**
	 * Creates a new obfuscator.
	 */
	public Obfuscator() {
		super();
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#execute(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void execute(Device device, Locale locale, Environment env)
	throws BuildException 
	{
		
		// TODO enough implement execute

	}
	/**
	 * Obfuscates a jar-file for the given device.
	 * 
	 * @param device The J2ME device
	 * @param sourceFile The jar-file containing the projects classes
	 * @param targetFile The file to which the obfuscated classes should be copied to 
	 * @param preserve All names of classes which should be preserved,
	 *                 that means not renamed or removed.
	 * @param bootClassPath A path to the library containing either the MIDP1.0 or MIDP2.0 environment.
	 * @throws BuildException when the obfuscation failed
	 */
	public abstract void obfuscate( Device device, File sourceFile, File targetFile, String[] preserve, Path bootClassPath )
	throws BuildException;
	
	/**
	 * Instantiates an obfuscator.
	 * 
	 * @param setting The obfuscator setting 
	 * @param antProject
	 * @param manager
	 * @param environment
	 * @return The concrete obfuscator.
	 * @throws BuildException when the preferred obfuscator or none obfuscator at all
	 *         could be instantiated.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static final Obfuscator getInstance( ObfuscatorSetting setting, Project antProject, ExtensionManager manager, Environment environment )
	throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		Obfuscator obfuscator = (Obfuscator) manager.getExtension(ExtensionManager.TYPE_OBFUSCATOR, setting, environment);
		File libDir = environment.getBuildSetting().getApiDir();
		LibraryManager libraryManager = environment.getLibraryManager();
		obfuscator.init( setting, antProject, libDir, libraryManager );
		return obfuscator;
	}

	/**
	 * Initialises this obfuscator.
	 * The protected field project, libDir and libraryManager are set in the default implementation.
	 * 
	 * @param obfuscatorSetting the settings for this obfuscator
	 * @param proj the Ant-project to which the obfuscator belongs to
	 * @param lbDir the main library directory
	 * @param lbManager the api manager 
	 */
	public void init( ObfuscatorSetting obfuscatorSetting, Project proj, File lbDir, LibraryManager lbManager) {
		this.project = proj;
		this.libDir = lbDir;
		this.libraryManager = lbManager;
		this.setting = obfuscatorSetting;
	}
	
	/**
	 * Builds the classpath for the given device and its boot class path.
	 * 
	 * @param device the device
	 * @param bootClassPath the classpath for the base, e.g. midp1.jar 
	 * @return a OS-dependent string with the complete classpath
	 */
	protected static final String getClassPath(Device device, Path bootClassPath) {
		String bootPath = bootClassPath.toString();
		StringBuffer classPathBuffer = new StringBuffer();
		String[] classPaths = device.getClassPaths();
		for (int i = 0; i < classPaths.length; i++) {
			classPathBuffer.append( classPaths[i] );
			classPathBuffer.append( File.pathSeparatorChar );
		}
		classPathBuffer.append( bootPath );
		return classPathBuffer.toString();
	}
	
	public ObfuscatorSetting getSetting() {
		return this.setting;
	}

	
}
