/*
 * Created on 30-Jul-2005 at 20:44:23.
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
import java.io.PrintStream;
import java.util.ArrayList;

import org.apache.tools.ant.types.Path;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.ExtensionDefinition;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.JarUtil;
import de.enough.polish.util.OrderedMultipleEntriesMap;
import de.enough.polish.util.OutputFilter;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Uses the normal ProGuard obfuscator for verifying the compiled classes.</p>
 *
 * <p>Copyright Enough Software 2005 - 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ProGuardPreverifier 
extends Preverifier 
implements OutputFilter
{

	private File proGuardJarFile;
	private boolean isVerbose = false;

	/**
	 * Creates a new un-initialized preverifier
	 */
	public ProGuardPreverifier() {
		super();
	}

	public void preverify(Device device, File sourceDir, File targetDir, Path bootClassPath, Path classPath) 
	throws IOException 
	{
		if (this.proGuardJarFile == null) {
			String proguardPath;
			ExtensionDefinition definition = getExtensionDefinition();
			if (definition != null) {
				proguardPath = getEnvironment().writeProperties( definition.getClassPath().toString() );
			} else {
				proguardPath = getEnvironment().writeProperties( "${polish.home}/lib/proguard.jar" );
			}
			this.proGuardJarFile = new File( proguardPath );
			if ( !this.proGuardJarFile.exists() ) {
				this.proGuardJarFile = new File( getEnvironment().getBaseDir(), proguardPath );
			}
		}
		File sourceFile = new File( device.getBaseDir() + "/compiled.jar");
		try {
			JarUtil.jar( sourceDir, sourceFile, false );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to prepare the obfuscation-jar from [" + sourceFile.getAbsolutePath() + "] to [" + device.getClassesDir() + "]: " + e.getMessage(), e );
		}
		File targetFile  = new File( device.getBaseDir() + "/preverified.jar");
		
		OrderedMultipleEntriesMap params = new OrderedMultipleEntriesMap();
		// the input jar file:
		params.put( "-injars", quote( sourceFile.getAbsolutePath() ) );
		// the output jar file:
		params.put(  "-outjars", quote( targetFile.getAbsolutePath() ) );
		// the libraries:
		StringBuffer buffer = new StringBuffer();
		String[] apiPaths = device.getBootClassPaths();
		String path = apiPaths[0];
		buffer.append( quote( path ) );
		for (int i = 1; i < apiPaths.length; i++) {
			path = apiPaths[i];
			buffer.append( File.pathSeparatorChar );
			buffer.append( quote( path ) );
		}
		apiPaths = device.getClassPaths();
		for (int i = 0; i < apiPaths.length; i++) {
			path = apiPaths[i];
			buffer.append( File.pathSeparatorChar );
			buffer.append( quote( path ) );
		}
		params.put( "-libraryjars", buffer.toString() );
		// we only want to preverify, not obfuscate:
		params.put( "-dontshrink", "" );
		params.put( "-dontoptimize", "" );
		params.put( "-dontobfuscate", "" );
		// preverify the code:
		params.put( "-microedition", "" );
		
		ArrayList argsList = new ArrayList();
		// the executable:
		argsList.add( "java" );
		argsList.add( "-jar" );
		argsList.add( this.proGuardJarFile.getAbsolutePath() );
		
		for (int i = 0; i < params.size(); i++) {
			String key = (String) params.getKey(i);
			String value = (String) params.getValue(i);
			argsList.add( key );
			if (value.length() > 0) {
				argsList.add( value );
			}
		}

		//System.out.println( argsList );
		int result = 0;
		try {
			result = ProcessUtil.exec(argsList, "proguard: ", true, this );
			// invoking the main(String[]) method fails because
			// ProGuard calls System.exit(0) explicitly (for whatever reason)...
			//JarUtil.exec( this.proGuardJarFile, argsList, getClass().getClassLoader() );
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ProGuard preverify arguments: " + argsList.toString() );
			throw new BuildException("ProGuard is unable to preverify: " + e.toString(), e );
		}
		if (result != 0) {
			System.out.println("ProGuard preverify arguments: " + argsList.toString() );
			throw new BuildException("ProGuard was unable to preverify - got return value [" + result + "].");
		}
		FileUtil.delete( targetDir );
		JarUtil.unjar(targetFile, targetDir);
		System.out.println("successfully preverified classes in " + sourceDir.getAbsolutePath()  );
	}
	
	
	private String quote( String path ) {
		if ( path.indexOf( ' ' ) != -1 ) {
			// for some weird reason proguard expects
			// to be doubled quoted under Windows... sigh.
			if ( File.separatorChar == '\\' ) {
				path = "\"'" + path + "'\"";
			} else {
				path = '"' + path + '"';
			}
		}
		return path;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.util.OutputFilter#filter(java.lang.String, java.io.PrintStream)
	 */
	public void filter(String message, PrintStream output) {
		if (this.isVerbose 
			|| (message.indexOf("Note:") == -1
			&& message.indexOf("Reading program") == -1
			&& message.indexOf("Reading library") == -1			
			&& message.indexOf("You might consider") == -1
			&& message.indexOf("their implementations") == -1
			&& message.indexOf("Copying resources") == -1
			) )
		{
			output.println( message );
		}
		
	}

}
