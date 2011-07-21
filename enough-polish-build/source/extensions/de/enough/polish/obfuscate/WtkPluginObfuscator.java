/*
 * Created on 03-Nov-2004 at 16:49:10.
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
import java.io.IOException;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;


import de.enough.polish.Device;
import de.enough.polish.ant.build.ObfuscatorSetting;
import de.enough.polish.devices.LibraryManager;
import de.enough.polish.util.FileUtil;

/**
 * <p>Obfuscates the classes with any obfuscator which provides a WTK plugin.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        03-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class WtkPluginObfuscator extends Obfuscator {
	
	protected com.sun.kvem.environment.Obfuscator plugin;
	/** 
	 * Indicates if a JAD file is really needed. 
	 * Defaults to true
	 */
	protected boolean needsJadFile;
	protected String wtkBinPath;
	protected String wtkLibPath;
	protected String emptyApiPath;
	

	/**
	 * Creates a new WTK Plugin Obfuscator
	 */
	public WtkPluginObfuscator() {
		super();
		this.plugin = getObfuscatorPlugin();
		this.needsJadFile = true;
	}

	/**
	 * Retrieves an instance of the actual plugin.
	 * 
	 * @return the class which implements com.sun.kvem.environment.Obfuscator 
	 */
	protected abstract com.sun.kvem.environment.Obfuscator getObfuscatorPlugin();
	
	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.Obfuscator#init(org.apache.tools.ant.Project, java.io.File, de.enough.polish.LibraryManager)
	 */
	public void init(  ObfuscatorSetting obfuscatorSetting, Project proj, File lbDir, LibraryManager lbManager) {
		super.init(obfuscatorSetting, proj, lbDir, lbManager);
		String wtkHome = proj.getProperty("wtk.home");
		this.wtkBinPath = wtkHome + File.separator + "bin";
		this.wtkLibPath = wtkHome + File.separator + "lib";
		this.emptyApiPath = wtkHome + File.separator + "wtklib" + File.separator + "emptyapi.zip";
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.Obfuscator#obfuscate(de.enough.polish.Device, java.io.File, java.io.File, java.lang.String[], org.apache.tools.ant.types.Path)
	 */
	public void obfuscate(Device device, File sourceFile, File targetFile,
			String[] preserve, Path bootClassPath) 
	throws BuildException 
	{
		try {
			String projectPath = device.getBaseDir();
			File projectDir = new File( projectPath );
			File jadFile = null;
			if (this.needsJadFile) {
				jadFile = createTemporaryJadFile( device, sourceFile, preserve, projectDir );
			}
			// create classpath-string
			String classPath = getClassPath(device, bootClassPath);
			// create a script file:
			createScriptFile( device, jadFile, projectDir, classPath, preserve );
			// run the obfuscator:
			runObfuscator(targetFile, this.wtkBinPath, this.wtkLibPath, sourceFile.getAbsolutePath(), projectPath, classPath, bootClassPath.toString() );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to obfuscate: " + e.toString(), e );
		}
	}
	


	/**
	 * Creates a script file if necessary.
	 * The default implementation just calls the same method on the obfuscator plugin.
	 * @param device
	 * 
	 * @param jadFile the file containing the JAD
	 * @param projectDir the current base directory
	 * @param preserve array with class-names which should be spared from obfuscation
	 * @param classPath the complete class path
	 */
	protected void createScriptFile( Device device, File jadFile, File projectDir, String classPath, String[] preserve )
	throws IOException
	{
		this.plugin.createScriptFile( jadFile, projectDir );
	}
	
	/**
	 * Runs the actual obfuscator.
	 * 
	 * @param jarFileObfuscated the target file
	 * @param wtkBinDir path to the bin directory of the WTK
	 * @param wtkLibDir path to the lib directory of the WTK
	 * @param jarFilename the source file
	 * @param projectDir the current base directory
	 * @param classPath 
	 * @param emptyApi
	 * @throws IOException
	 */
	protected void runObfuscator( File jarFileObfuscated, String wtkBinDir,
			String wtkLibDir, String jarFilename,
			String projectDir, String classPath,
			String emptyApi) 
	throws IOException
	{
		this.plugin.run(jarFileObfuscated, wtkBinDir, wtkLibDir, jarFilename, projectDir, classPath, emptyApi);
	}


	/**
	 * Creates a minimum JAD file for informing the obfuscator.
	 * 
	 * @param device the current device
	 * @param sourceFile the current jar file
	 * @param preserve array with classes which should be preserved
	 * @param baseDir the current base directory
	 * @throws IOException
	 */
	protected File createTemporaryJadFile(Device device, File sourceFile, String[] preserve, File baseDir ) 
	throws IOException 
	{
		ArrayList lines = new ArrayList();
		lines.add("MIDlet-Name: J2ME-Polish" );
		lines.add("MIDlet-Version: 1.2.3" );
		lines.add("MIDlet-Vendor: Enough Software" );
		lines.add("MIDlet-Jar-URL: " + sourceFile.getName() );
		lines.add("MIDlet-Jar-Size: " + sourceFile.length() );
		if (device.isMidp1()) {
			lines.add("MicroEditon-Profile: MIDP/1.0" );	
		} else {
			lines.add("MicroEditon-Profile: MIDP/2.0" );
		}
		if (device.isCldc10()) {
			lines.add("MicroEditon-Configuration: CLDC/1.0" );	
		} else {
			lines.add("MicroEditon-Configuration: CLDC/1.1" );	
		}
		for (int i = 0; i < preserve.length; i++) {
			String className = preserve[i];
			lines.add("MIDlet-" + (i+1) + ": " + className );
		}
		File jadFile = new File( baseDir, "temp.jad" );
		FileUtil.writeTextFile( jadFile, (String[]) lines.toArray( new String[ lines.size() ] ) );
		return jadFile;
	}

}
