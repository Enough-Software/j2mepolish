/*
 * Created on Nov 15, 2007 at 4:00:50 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.types.Path;

import de.enough.polish.ant.PolishTask;

/**
 * <p>Manages configurations.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Nov 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ConfigurationManager
{
	
	private final ArrayList configurations;

	public ConfigurationManager() {
		this.configurations = new ArrayList();
	}
	
	public void addConfiguration( Configuration config ) {
		this.configurations.add(config);
	}
	
	public void addConfiguration( String className, String path, PolishTask task, Environment env ) {
		AntClassLoader classLoader = new AntClassLoader( getClass().getClassLoader(), task.getProject(), new Path(task.getProject(), path), false );
		try
		{
			Configuration cfg = (Configuration) classLoader.loadClass(className).newInstance();
			cfg.configure(task, env);
			addConfiguration( cfg );
		} catch (Exception e)
		{
			String message = "Unable to load class \"" + className + "\" from " + path + e.toString() ;
			System.err.println(message);
			e.printStackTrace();
			throw new BuildException( message );
		}
	}
	
	/**
	 * Allows all configurations to add settings at the start of the initialization of a new build.
	 * 
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @param env the environment/configuration
	 */
	public void preInitialize(Device device, Locale locale, Environment env) {
		for (Iterator iter = this.configurations.iterator(); iter.hasNext();)
		{
			Configuration configuration = (Configuration) iter.next();
			configuration.preInitialize(device, locale, env);
		}
	}
	

	/**
	 * Allows all configurations to add settings at the end of the initialization of a new build.
	 * 
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @param env the environment/configuration
	 */
	public void postInitialize( Device device, Locale locale, Environment env ) {
		for (Iterator iter = this.configurations.iterator(); iter.hasNext();)
		{
			Configuration configuration = (Configuration) iter.next();
			configuration.postInitialize(device, locale, env);
		}
	}

	
}
