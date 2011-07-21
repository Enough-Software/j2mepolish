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
 * <p>Packages files using the kzip packager.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        28-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class KZipPackager 
extends Packager
implements OutputFilter
{

	private Variable[] parameters;

	/**
	 * Creates a new packager 
	 */
	public KZipPackager() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.jar.Packager#createPackage(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void createPackage(File sourceDir, File targetFile, Device device,
			Locale locale, Environment env) 
	throws IOException, BuildException 
	{
		File executable = env.resolveFile("${polish.home}/bin/kzip.exe");
		if (!executable.exists()) {
			executable = env.resolveFile("${kzip.home}/kzip.exe");
		}
		ArrayList arguments = new ArrayList();
		if ( File.separatorChar != '\\' ) {
			// use wine on Unix environments:
			arguments.add("wine");
			arguments.add("--");
		}
		if (executable.exists()) {
			arguments.add( executable.getAbsolutePath() );
		} else  {
			arguments.add( "kzip.exe" );
		}
		File tempFile = new File( sourceDir, "kziptemp.ZIP" );
		arguments.add( "kziptemp.ZIP" ); // target JAR file
		arguments.add("/y"); // overwrite existing files
		// add optional parameters:
		if (this.parameters != null) {
			for (int i = 0; i < this.parameters.length; i++) {
				Variable parameter = this.parameters[i];
				if ( parameter.isConditionFulfilled(env) ) {
					String name = parameter.getName();
					String value = parameter.getValue();
					if (name.charAt(0) == '/') {
						if (value == null || value.length() == 0) {
							arguments.add( name );
						} else {
							arguments.add( name + value );
						}
					} else if ("blocks".equals(name) || ("blocksplit".equals(name))) {
						try {
							int split = Integer.parseInt( value );
							if (split < 0 || split > 2048) {
								System.out.println("kzplit: Warning: the blocksplit value [" + value + "] is not supported - trying to call kzip anyhow...");
							}
						} catch (NumberFormatException e) {
							throw new BuildException("kzip: Unable to set blocksplit value [" + value + "]: Use a number between 0 and 2048." );
						}
					} else {
						throw new BuildException("kzip: Unable to set parameter [" + name + "]: start parameter with \"/\" to set any parameter." );
					}
				}
			}
		}
		arguments.add("/r"); // recursive
		arguments.add( "*.*" ); // source directory
		//System.out.println(arguments);
		int result = ProcessUtil.exec( arguments, "kzip: ", true, this, sourceDir );
		if (result != 0) {
			throw new BuildException("kzip: Unable to create [" + targetFile.getAbsolutePath() + "]: kzip returned [" + result + "].");
		}
		// now rename temporary file:
		if (targetFile.exists()) {
			boolean deleted = targetFile.delete();
			if (!deleted) {
				throw new BuildException("kzip: Unable to delete previous JAR file [" + targetFile.getAbsolutePath() + " - check the permissions of that file or delete it manually.");
			}
		}
		boolean renamed = tempFile.renameTo(targetFile);
		if (!renamed) {
			throw new BuildException("kzip: Unable to rename [" + tempFile.getAbsolutePath() + " to " + targetFile.getAbsolutePath() + "].");
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.util.OutputFilter#filter(java.lang.String, java.io.PrintStream)
	 */
	public void filter(String message, PrintStream output) {
		if (message.indexOf("Adding") == -1) {
			output.println( message );
		}
	}
	
	public void setParameters( Variable[] parameters, File baseDir ) {
		this.parameters = parameters;
	}

	
}
