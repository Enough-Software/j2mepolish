/*
 * Created on 23-Apr-2005 at 14:18:59.
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
package de.enough.polish.finalize;

import java.io.File;
import java.util.Locale;

import org.apache.tools.ant.Project;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;
import de.enough.polish.ExtensionManager;
import de.enough.polish.ant.build.FinalizerSetting;

/**
 * <p>Is used to to work on the final JAR and JAD file and is called after the packaging step.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        23-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class Finalizer extends Extension {

	/**
	 * Creates a new finalizer.
	 */
	public Finalizer() {
		super();
	}
	
	public static Finalizer getInstance( FinalizerSetting setting, Project antProject, ExtensionManager manager, Environment environment ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		Finalizer finalizer = (Finalizer) manager.getExtension( ExtensionManager.TYPE_FINALIZER, setting, environment );
		return finalizer;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#execute(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void execute( Device device, Locale locale, Environment env ) {
		String jadPath = env.getVariable( "polish.jadPath" );
		File jadFile =  this.antProject.resolveFile( jadPath );
		String jarPath = env.getVariable( "polish.jarPath" );
		File jarFile =  this.antProject.resolveFile( jarPath );
		finalize( jadFile, jarFile, device, locale, env );
	}

	/**
	 * Finalizes the building of the given JAD and JAR combination.
	 * 
	 * @param jadFile the JAD file
	 * @param jarFile the JAR file
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @param env environment variables and settings
	 */
	public abstract void finalize(File jadFile, File jarFile, Device device, Locale locale, Environment env);

}
