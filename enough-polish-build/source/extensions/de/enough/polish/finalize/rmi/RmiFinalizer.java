/*
 * Created on Dec 28, 2006 at 6:53:44 PM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.finalize.rmi;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.finalize.Finalizer;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.JarUtil;
import de.enough.polish.util.TextFile;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Dec 28, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RmiFinalizer extends Finalizer {

	/* (non-Javadoc)
	 * @see de.enough.polish.finalize.Finalizer#finalize(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void finalize(File jadFile, File jarFile, Device device,
			Locale locale, Environment env) 
	{
		boolean useXmlRpc = "true".equals(env.getVariable("polish.rmi.xmlrpc"));
		if (useXmlRpc) {
			return;
		}
		File sourceDir = (File) this.environment.get("rmi-classes-dir" );
	    List rmiClasses = (List) this.environment.get("rmi-classes" );
	    if (rmiClasses != null) {
	    	File targetJar = new File( device.getJarFile().getParentFile(), "client-rmi-classes.jar" );
	    	try {
				File obfuscationMapFile = new File( this.environment.getProjectHome(), ".polishSettings/obfuscation-map.txt" );
				if (obfuscationMapFile.exists()) {
					// when the default package option is used, we need to translate the class names to the full classnames with
					// package names before embeddeding the obfuscation map:
					if (env.hasSymbol("polish.useDefaultPackage")) {
						File  fullyQualifiedObfuscationMapFile = new File( device.getBaseDir() + File.separatorChar + "obfuscation-map.txt" );
						if ( (! fullyQualifiedObfuscationMapFile.exists()) || ( fullyQualifiedObfuscationMapFile.lastModified() < obfuscationMapFile.lastModified())) {
							Map sourceFilesByNameMap = (Map) this.environment.get("polish.sourcefiles.byClassName");
							if (sourceFilesByNameMap == null) {
								sourceFilesByNameMap = buildSourceFilesByNameMap();
								this.environment.set("polish.sourcefiles.byClassName", sourceFilesByNameMap );
							}
							Map obfuscationMap = FileUtil.readProperties( obfuscationMapFile );
							Object[] keys = obfuscationMap.keySet().toArray();
							for (int i = 0; i < keys.length; i++) {
								String className = (String) keys[i];
								String fullyQualifiedClassName = (String) sourceFilesByNameMap.get( className );
								obfuscationMap.put( fullyQualifiedClassName, obfuscationMap.get(className) );
							}
							FileUtil.writePropertiesFile(  fullyQualifiedObfuscationMapFile, obfuscationMap);
						}
						obfuscationMapFile = fullyQualifiedObfuscationMapFile;
					}
				}
		    	System.out.println("packaging rmi classes to " + targetJar.getAbsolutePath() );
				File[] files = (File[]) rmiClasses.toArray( new File[ rmiClasses.size() ] );
				JarUtil.jar( files, sourceDir, targetJar, false );
				if (obfuscationMapFile.exists()) {
					JarUtil.addToJar( obfuscationMapFile, targetJar, null, false );
				}
			} catch (IOException e) {
				BuildException be =  new BuildException("Unable to create " + targetJar.getAbsolutePath() + ": " + e.toString() );
				be.initCause( e );
				throw be;
			}
	    }
	}

	/**
	 * Build a map that links classnames to their original fully qualified name.
	 * 
	 * @return the map
	 */
	private Map buildSourceFilesByNameMap() {
		TextFile[][] sourceFilesArray = (TextFile[][]) this.environment.get("polish.sourcefiles");
		Map sourceFilesByNameMap = new HashMap();
		if (sourceFilesArray != null) {
			for (int i = 0; i < sourceFilesArray.length; i++) {
				TextFile[] sourceFiles = sourceFilesArray[i];
				for (int j = 0; j < sourceFiles.length; j++) {
					TextFile file = sourceFiles[j];
					String fullClassName = file.getClassName();
					String className = fullClassName;
					int lastDotPos = fullClassName.lastIndexOf('.');
					if (lastDotPos !=  -1) {
						className = fullClassName.substring( lastDotPos + 1 );
					}
					sourceFilesByNameMap.put(className ,fullClassName );
				}
			}
		}
		return sourceFilesByNameMap;
	}

}
