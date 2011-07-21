/*
 * Created on 28-Apr-2005 at 16:33:34.
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
package de.enough.polish.jar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Packages files using the kzip packager.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        28-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class JarPackager 
extends Packager
//implements OutputFilter
{

	private boolean verbose;

	/**
	 * Creates a new packager 
	 */
	public JarPackager() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.jar.Packager#createPackage(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void createPackage(File sourceDir, File targetFile, Device device,
			Locale locale, Environment env) 
	throws IOException, BuildException 
	{
		ArrayList arguments = new ArrayList();
		//File manifestSource = new File( sourceDir, "META-INF" + File.separatorChar + "MANIFEST.MF" );
		//File manifestFile = new File( device.getBaseDir(), "MANIFEST.MF" );
		//boolean moved = manifestSource.renameTo( manifestFile );
		//System.out.println("Manifest moved to " + manifestFile.getAbsolutePath() + ": " + moved );
		//File manifestDir = new File( sourceDir, "META-INF");
		//manifestDir.delete();
		String javaHome = System.getProperty("java.home");
		if (javaHome != null) {
			if (javaHome.endsWith("jre")) {
				javaHome = javaHome.substring( 0, javaHome.length() - "/jre".length() );
			} else if (javaHome.charAt( javaHome.length() - 1) == File.separatorChar) {
				javaHome = javaHome.substring( 0, javaHome.length() - 1);
			}
			File jarFile;
			if (File.separatorChar == '\\') {
				// windows:
				jarFile = new File( javaHome + "\\bin\\jar.exe" );				
			} else {
				jarFile = new File( javaHome + "/bin/jar" );				
			}
			if (jarFile.exists()) {
				arguments.add( jarFile.getAbsolutePath() );
			} else {
				arguments.add( "jar" );
			}
		} else {
			arguments.add( "jar" );
		}
		if (this.verbose) {
			arguments.add( "-cvfM" );			
		} else {
			arguments.add( "-cfM" );
		}
		arguments.add( targetFile.getAbsolutePath() );
		//arguments.add( manifestFile.getAbsolutePath() );
		arguments.add( "-C" );
		arguments.add( sourceDir.getAbsolutePath() );
		arguments.add( "." );
		//System.out.println(arguments);
		//int result = ProcessUtil.exec( arguments, "jar: ", true, this, sourceDir );
		int result = 0;
		System.out.println("creating JAR file ["+ targetFile.getAbsolutePath() + "].");
		try {
			result = ProcessUtil.exec( arguments, "jar: ", true, null, sourceDir );
		} catch (IOException e) {
			if (File.separatorChar == '\\') {
				// windows:
				System.err.println("Unable to execute the jar packager. Check if your PATH includes %JAVA_HOME%\\bin.");
			} else {
				System.err.println("Unable to execute the jar packager. Check if your PATH includes $JAVA_HOME/bin.");
			}
			throw e;
		}
		if (result != 0) {
			throw new BuildException("jar: Unable to create [" + targetFile.getAbsolutePath() + "]: jar returned [" + result + "].");
		}
	}

	
	public void setVerbose( boolean enable ) {
		this.verbose = enable;
	}

	public String toString() {
		return "Jar Packager";
	}
	
}
