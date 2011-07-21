/*
 * Created on 04-Apr-2005 at 14:48:49.
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
package de.enough.polish;

import org.apache.tools.ant.types.Path;

import de.enough.polish.ant.Setting;

/**
 * <p>Extends the possibilities of the Setting by allowing the setting of a class name and a classpath.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        04-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ExtensionSetting extends Setting {

	protected String name;
	protected String className;
	protected Path classPath;
	protected String target;

	/**
	 * Creates a new setting.
	 */
	public ExtensionSetting() {
		super();
	}
	
	/**
	 * Sets the classname of the extension.
	 * 
	 * @param className the class name
	 */
	public void setClass( String className ) {
		this.className = className;
	}
	
	public String getClassName() {
		return this.className;
	}
	
	public void setClassPath( Path classPath ) {
		this.classPath = classPath;
	}

	public Path getClassPath() {
		return this.classPath;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTarget(){
		return this.target;
	}
	/**
	 * Sets the &lt;antcall&gt; target of this extension.
	 *  
	 * @param target the Ant target that should be called
	 */
	public void setTarget( String target ) {
		this.target = target;
	}

	/**
	 * Retrieves the defined parameter.
	 * 
	 * @param parameterName the name of the parameter
	 * @param environment the environment, is used for finding out whether the parameter is active. 
	 *        When the environment is null, the first match is returned.
	 * @return the parameter or null when none is found.
	 */
	public Variable getParameter(String parameterName, Environment environment) {
		Variable[] parameters = getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Variable variable = parameters[i];
			String varName = variable.getName();
			if ( parameterName.equals( varName )) {
				if (environment == null || variable.isConditionFulfilled( environment )) {
					return variable;
				}
			}
		}
		return null;
	}
}
