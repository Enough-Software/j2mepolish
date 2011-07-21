/*
 * Created on 22-Feb-2004 at 12:49:27.
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

import de.enough.polish.Device;
import de.enough.polish.util.StringUtil;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import proguard.*;
import proguard.Configuration;
import proguard.ProGuard;
import proguard.classfile.ClassConstants;

/**
 * <p>Is used to obfuscate code with the ProGuard obfuscator, version 2.x.</p>
 * <p>For details of ProGuard, please refer to http://proguard.sourceforge.net/.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        22-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ProGuard2Obfuscator extends Obfuscator {
	

	/**
	 * Creates a new pro guard obfuscator.
	 */
	public ProGuard2Obfuscator() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.Obfuscator#obfuscate(de.enough.polish.Device, java.io.File, java.lang.String[], org.apache.tools.ant.types.Path)
	 */
	public void obfuscate(Device device, File sourceFile, File targetFile, String[] preserve, Path bootClassPath) 
	throws BuildException 
	{
		System.out.println("Starting obfuscation with ProGuard 2.x.");
		// create the configuration for ProGuard:
		Configuration cfg = new Configuration();
		
		// set libraries:
		cfg.libraryJars  = getPath( bootClassPath.toString() );
		String[] apiPaths = device.getClassPaths();
		for (int i = 0; i < apiPaths.length; i++) {
			cfg.libraryJars.addAll( getPath( apiPaths[i] ) );
		}
		
        cfg.inJars = getPath( sourceFile );
        cfg.outJars = getPath( targetFile );

        // do not obfuscate the <keep> classes and the defined midlets:
        cfg.keepClassFileOptions = new ArrayList( preserve.length );
        for (int i = 0; i < preserve.length; i++) {
			String className = StringUtil.replace( preserve[i], '.', '/');
			//System.out.println("\npreservering: " + className );
	        cfg.keepClassFileOptions.add(
	        		new KeepClassFileOption(
        				ClassConstants.INTERNAL_ACC_PUBLIC,
	                    0,
	                    className,
	                    null,
	                    null,
	                    true,
	                    false,
	                    false));
		}
        // some devices do not support mixed-case class names:
        cfg.useMixedCaseClassNames = false;

        // overload names with different return types:
        cfg.overloadAggressively = true;

        // move all classes to the root package:
        cfg.defaultPackage = "";		
        
        ProGuard proGuard = new ProGuard( cfg );
		try {
			proGuard.execute();
			System.out.println("ProGuard 2.x has successfully finished obfuscation.");
		} catch (IOException e) {
			// check if all preserve-classes are found:
			for (int i = 0; i < preserve.length; i++) {
				String className = preserve[i];
				String fileName = StringUtil.replace( className, '.', File.separatorChar ) + ".java";
				File file = new File( device.getSourceDir() + File.separator + fileName );
				if (!file.exists()) {
					System.err.println("WARNING: the MIDlet or class [" + className + "] was not found: [" + file.getAbsolutePath() + "] does not exist.");
					System.err.println("Please check your <midlet>-setting in the file [build.xml].");
				}
			}
			throw new BuildException("ProGuard 2.x was unable to obfuscate: " + e.getMessage(), e );
		}
	}
	
	/**
	 * Converts the path of the given file to a proguard.ClassPath
	 * 
	 * @param file The file for which a class path should be retrieved.
	 * @return The classpath of the given file
	 */
	private ClassPath getPath(File file ) {
		return getPath( file.getAbsolutePath() );
	}

	/**
	 * Converts the given file path to a proguard.ClassPath
     * 
	 * @param path The path as a String
	 * @return The path as a proguard-ClassPath
     */
    private ClassPath getPath(String path)
    {
        ClassPath classPath = new ClassPath();
        String[] elements = StringUtil.split( path, File.pathSeparatorChar );
        for (int i = 0; i < elements.length; i++) {
        	ClassPathEntry entry =
                new ClassPathEntry( elements[i]);

            classPath.add(entry);
		}
        return classPath;
    }

}
