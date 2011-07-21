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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Variable;
import de.enough.polish.util.OutputFilter;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Packages files using the 7zip packager.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        28-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SevenZipPackager 
extends Packager
implements OutputFilter
{

	private Variable[] parameters;

	/**
	 * Creates a new packager 
	 */
	public SevenZipPackager() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.jar.Packager#createPackage(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void createPackage(File sourceDir, File targetFile, Device device,
			Locale locale, Environment env) 
	throws IOException, BuildException 
	{
		File executable;
		if ( File.separatorChar == '\\' ) {
			// this is a window environment
			executable = env.resolveFile("${polish.home}/bin/7za.exe");
			if (!executable.exists()) {
				executable = env.resolveFile("${7zip.home}/7za.exe");
			}
		} else {
			executable = env.resolveFile("${polish.home}/bin/7za");
			if (!executable.exists()) {
				executable = env.resolveFile("${7zip.home}/7za");
			}
		}
		ArrayList arguments = new ArrayList();
		if (executable.exists()) {
			arguments.add( executable.getAbsolutePath() );
		} else if ( File.separatorChar == '\\' ) {
			arguments.add( "7za.exe" );
		} else {
			arguments.add( "7za" );
		}
		arguments.add("a"); // a for add files
		arguments.add("-tzip"); // use zip compression
		arguments.add( targetFile.getAbsolutePath() ); // target JAR file
		// add optional parameters:
		if (this.parameters != null) {
			for (int i = 0; i < this.parameters.length; i++) {
				Variable parameter = this.parameters[i];
				if ( parameter.isConditionFulfilled(env) ) {
					String name = parameter.getName();
					String value = parameter.getValue();
					if (name.charAt(0) == '-') {
						if (value != null && value.length() > 0) {
							arguments.add( name + "=" +  value); // any parameter
						} else {
							arguments.add( name ); // any switch
						}
					} else if ("compression".equals(name)) {
						if ("high".equals(value) || "maximum".equals(value)) {
							arguments.add("-mx=9"); // highest compression
						} else if ("none".equals(value) || "minimum".equals(value)) {
							arguments.add("-mx=0" ); // no compression
						} else if ("normal".equals(value) || ("default".equals(value))){
							arguments.add("-mx=5"); // normal compression
						} else {
							throw new BuildException("7zip: Unsupported compression value [" + value + "]. Allowed values are \"high\", \"none\" or \"default\".");
						}
					} else if ("passes".equals(name)) {
						try {
							int passes = Integer.parseInt(value);
							if (passes < 1 || passes > 4) {
								System.out.println("7zip: Warning: the passes value [" + value + "] is not in the allowed range (1 to 4). Trying to call 7zip nevertheless...");
							}
						} catch (NumberFormatException e) {
							throw new BuildException("7zip: Unsupported passes value [" + value + "]. Use a number between 1 and 4.");
						}
						arguments.add("-mpass=" + value );
					} else if ("fastbytes".equals(name)) {
						try {
							int fastbytes = Integer.parseInt(value);
							if (fastbytes < 3 || fastbytes > 255) {
								System.out.println("7zip: Warning: the fastbytes value [" + value + "] is not in the allowed range (3 to 255). Trying to call 7zip nevertheless...");
							}
						} catch (NumberFormatException e) {
							throw new BuildException("7zip: Unsupported fastbytes value [" + value + "]. Use a number between 3 and 255.");
						}
						arguments.add("-mfb=" + value );
					} else {
						throw new BuildException("7zip: Unsupported parameter [" + name + "]. You can use non-supported parameters by starting them with \"-\".");
					}
				}
			}
		}
		arguments.add("-r"); // recursive
		arguments.add( sourceDir.getAbsolutePath() + "/*" ); // source directory
		//System.out.println(arguments);
		int result = ProcessUtil.exec( arguments, "7zip: ", true, this );
		if (result != 0) {
			throw new BuildException("7zip: Unable to create [" + targetFile.getAbsolutePath() + "]: 7zip returned [" + result + "].");
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.util.OutputFilter#filter(java.lang.String, java.io.PrintStream)
	 */
	public void filter(String message, PrintStream output) {
		if (message.indexOf("Compressing") == -1) {
			output.println( message );
		}
	}
	
	public void setParameters( Variable[] parameters, File baseDir ) {
		this.parameters = parameters;
	}

}
