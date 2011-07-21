/*
 * Created on 02-Nov-2004 at 15:15:54.
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
import de.enough.polish.Extension;
import de.enough.polish.ExtensionManager;
import de.enough.polish.ant.build.PackageSetting;

/**
 * <p>Is responsible for packaging files.</p>
 *
 * <p>Copyright Enough Software 2004 - 2009</p>

 * <pre>
 * history
 *        02-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class Packager extends Extension {
	

	/**
	 * The key which can be used to retrieves the used packager
	 * from the environment AFTER the packaging build phase:
	 * @see Environment#get(String)
	 */
	public static final String KEY_ENVIRONMENT = "key.Packager";

	/**
	 * Creates a new packager
	 */
	protected Packager() {
		super();
	}
	
	public final static Packager getInstance( PackageSetting setting, ExtensionManager manager, Environment environment ) {
		if (setting == null) {
			return new DefaultPackager();
		} else if ( setting.getClassName() != null) {
			try {
				Packager packager = (Packager) manager.getExtension( ExtensionManager.TYPE_PACKAGER, setting, environment );
				return packager;
			} catch (Exception e) {
				e.printStackTrace();
				throw new BuildException("Unable to initialise the packager: " + e.toString() );
			}
		} else if ( setting.getExecutable() != null) {
			if ( setting.getArguments() == null) {
				throw new BuildException("Please set the \"arguments\"-attribute of the <package>-element.");
			}
			Packager packager = new ExternalPackager( setting );
			manager.registerExtension(ExtensionManager.TYPE_PACKAGER, packager );
			return packager;
		} else {
			Packager packager = new DefaultPackager();
			manager.registerExtension(ExtensionManager.TYPE_PACKAGER, packager );
			return packager;
		}
	}
	
	public PackageSetting getSetting() {
		return (PackageSetting) this.extensionSetting;
	}

	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#execute(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void execute(Device device, Locale locale, Environment env)
	throws BuildException
	{
		try {
			createPackage( new File( device.getClassesDir() ), device.getJarFile(), device, locale, env );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to package for device [" + device.getIdentifier() + "] into jar [" + device.getJarFile() + "]: " + e.toString() );
		}
	}
	
	/**
	 * Creates a jar file from all the contents in the given directory.
	 * 
	 * @param sourceDir the directory which contents should be jarred
	 * @param targetFile the target jar-file
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @param env the environment settings
	 * @throws IOException when the packaging fails
	 * @throws BuildException when the packaging fails for another reason
	 */
	public abstract void createPackage( File sourceDir, File targetFile, Device device, Locale locale, Environment env)
	throws IOException, BuildException;

}
