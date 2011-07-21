/*
 * Created on 02-Nov-2004 at 15:38:29.
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
package de.enough.polish.jar;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.build.PackageSetting;
import de.enough.polish.util.LoggerThread;
import de.enough.polish.util.StringUtil;

/**
 * <p>Calls an external packager-tool.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        02-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ExternalPackager extends Packager {


	/**
	 * Creates a new external packager
	 */
	public ExternalPackager() {
		// default setting
	}
	
	/**
	 * Creates a new external packager
	 * 
	 * @param setting the settings
	 */
	public ExternalPackager( PackageSetting setting ) {
		super();
		this.extensionSetting = setting;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.jar.Packager#doPackage(java.io.File, java.io.File, de.enough.polish.Device, de.enough.polish.preprocess.BooleanEvaluator, java.util.Map, org.apache.tools.ant.Project)
	 */
	public void createPackage(File sourceDir, File targetFile, Device device,
			Locale locale, Environment env )
	throws IOException, BuildException 
	{
		PackageSetting setting = getSetting();
		env.addVariable("polish.packageDir", sourceDir.getAbsolutePath() );
		
		String executable = env.writeProperties( setting.getExecutable());
		String argumentsStr = env.writeProperties( setting.getArguments() );
		String[] arguments = StringUtil.splitAndTrim( argumentsStr, ";;");
		String[] parameters = new String[ arguments.length + 1 ];
		parameters[0] = executable;
		System.arraycopy( arguments, 0, parameters, 1, arguments.length );
		
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(parameters);
		
		try {
			String info = executable;
			if (info.indexOf( File.separatorChar ) != -1) {
				info = info.substring( info.indexOf( File.separatorChar ));
			}
			info += ": ";
			LoggerThread.log(process, info);
			int result = process.waitFor();
			if (result != 0) {
				System.err.println( "Call to external packager was: ");
				for (int i = 0; i < parameters.length; i++) {
					System.err.print( parameters[i] + " " );
				}
				System.err.println();
				throw new BuildException("External packager failed with result [" + result + "].");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new BuildException("External packager was interrupted.");
		}
		
	}

	public String toString() {
		return "External Packager";
	}
}
