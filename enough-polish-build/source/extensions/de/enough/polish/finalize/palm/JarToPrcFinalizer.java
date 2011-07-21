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
package de.enough.polish.finalize.palm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.finalize.Finalizer;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Uses the jartoprc.exe for converting JAR files to Palm PRC files.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class JarToPrcFinalizer extends Finalizer {

	/**
	 * Creates a new instance
	 */
	public JarToPrcFinalizer() {
		super();
	}

	/**
	 * Try to convert the JAR to the PRC format.
	 */
	public void finalize(File jadFile, File jarFile, Device device,
			Locale locale, Environment env) 
	{
		String propertyName = "palm.weme.home";
		String palmHomeStr = env.getVariable( propertyName );
		if (palmHomeStr == null) {
			propertyName = "palm.home";
			palmHomeStr = env.getVariable( propertyName );
			if (palmHomeStr == null) {
				System.err.println("You need to specify the \"palm.home\" property, so that J2ME Polish can convert JARs automatically to PRCs for Palm platforms. This is required when you want to use the full screen size, for example. Note that you can install normal JAR files only via OTA.");
				return;
			}
		}
		File palmHome = new File( palmHomeStr );
		if ( !palmHome.exists() ) {
			palmHome = new File( this.antProject.getBaseDir(), palmHomeStr );
			if ( !palmHome.exists() ) {
				System.err.println("ERROR: The \"" + propertyName + "\" property points to the non-existing directory [" + palmHome.getAbsolutePath() + "]." );
				System.err.println("You need to specify the \"palm.home\" property, so that J2ME Polish can convert JARs automatically to PRCs for Palm platforms. This is required when you want to use the full screen size, for example. Note that you can install normal JAR files only via OTA.");
				return;
			}
		}
		File jarToPrcExecutable = new File( palmHome, "Tools" + File.separatorChar + "bin" + File.separatorChar + "jartoprc.exe" );
		if ( !jarToPrcExecutable.exists() ) {
			System.err.println("ERROR: The \"" + propertyName + "\" property points to the wrong directory [" + palmHome.getAbsolutePath() + "] - this directory does not contain [Tools/bin/jartoprc.exe]!" );
			System.err.println("You need to specify the \"palm.home\" property, so that J2ME Polish can convert JARs automatically to PRCs for Palm platforms. This is required when you want to use the full screen size, for example. Note that you can install normal JAR files only via OTA.");
			return;
		}
		ArrayList arguments = new ArrayList();
		if ( File.separatorChar == '/') {
			arguments.add( "wine" );
		}
		arguments.add( jarToPrcExecutable.getAbsolutePath() );
		arguments.add( "-jad:" + jadFile.getAbsolutePath() );
		arguments.add( "-name:" + env.getVariable("MIDlet-Name") );
		
		String vendorId = env.getVariable("palm.vendorId" );
		if (vendorId != null) {
			if (vendorId.length() != 4 ) {
				System.err.println("Warning: the palm.vendorId [" + vendorId + "] seems to be invalid, a valid ID needs to be 4 characters long. Trying this ID nevertherless.");
			}
			arguments.add( "-id:" + vendorId  );
		}
		String icon = env.getVariable("palm.smallIcon" );
		if (icon != null) {
			File iconFile = new File( icon );
			if (!iconFile.exists()) {
				iconFile = new File( this.antProject.getBaseDir(), icon );
			}
			if (!iconFile.exists()) {
				throw new BuildException("Unable to find palm.smallIcon [" + icon + "] - file not found.");
			}
			arguments.add( "-smicon:" + iconFile.getAbsolutePath()  );
		}
		icon = env.getVariable("palm.largeIcon" );
		if (icon != null) {
			File iconFile = new File( icon );
			if (!iconFile.exists()) {
				iconFile = new File( this.antProject.getBaseDir(), icon );
			}
			if (!iconFile.exists()) {
				throw new BuildException("Unable to find palm.largeIcon [" + icon + "] - file not found.");
			}
			arguments.add( "-lgicon:" + iconFile.getAbsolutePath()  );
		}
		icon = env.getVariable("palm.splash" );
		if (icon != null) {
			File iconFile = new File( icon );
			if (!iconFile.exists()) {
				iconFile = new File( this.antProject.getBaseDir(), icon );
			}
			if (!iconFile.exists()) {
				throw new BuildException("Unable to find palm.splash [" + icon + "] - file not found.");
			}
			arguments.add( "-splash:" + iconFile.getAbsolutePath()  );
		}
		if ( !"false".equals( env.getVariable("palm.enableHighRes" )) ) {
			arguments.add( "-highres" );
		}
		if ( "true".equals( env.getVariable("palm.enableDebug" )) ) {
			arguments.add( "-stdoutOnVFS" );
		}
		if ( "true".equals( env.getVariable("palm.enableInstall" )) ) {
			arguments.add( "-install" );
		}
		
		try {
			int result = ProcessUtil.exec( arguments, "JarToRpc: " , true );
			if ( result != 0 ) {
				throw new BuildException( "Execution of jartoprc.exe failed, process returned " + result );
			}
			File prcFile = new File( jadFile.getParent(), env.getVariable("MIDlet-Name") + ".prc" );
			if ( !prcFile.exists() ) {
				throw new BuildException( "Execution of jartoprc.exe failed, no PRC file has been created." );
			} else if ( prcFile.length() < 10) {
				throw new BuildException( "Execution of jartoprc.exe failed, empty PRC file has been created." );
			}
			
			env.setVariable( "palm.prcPath", prcFile.getAbsolutePath() );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException( "Execution of jartoprc.exe failed: " + e.toString() );
		}
	}

}
