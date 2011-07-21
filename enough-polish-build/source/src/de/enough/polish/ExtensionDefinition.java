/*
 * Created on 26-Apr-2005 at 14:27:16.
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

import org.apache.tools.ant.Project;
import org.jdom.Element;

/**
 * <p>Contains the data taken from extensions.xml and custom-extension.xml for each extension point.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        26-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ExtensionDefinition {

	private final Element element;
	private final Project antProject;
	private final ExtensionManager manager;
	private final String type;
	private final String name;
	private final String className;
	private final String classPath;
	private String autoStartCondition;

	/**
	 * Creates a new extension.
	 * 
	 * @param element the XML definition
	 * @param antProject the ant project, can be used to resolve classpaths
	 * @param manager the parent manager
	 */
	public ExtensionDefinition( Element element, Project antProject, ExtensionManager manager ) {
		super();
		this.element = element;
		this.antProject = antProject;
		this.manager = manager;
		this.type = element.getChildTextTrim( "type" );
		if (this.type == null) {
			throw new IllegalArgumentException("The extension has no type.");
		}
		this.name = element.getChildTextTrim( "name" );
		if (this.name == null) {
			throw new IllegalArgumentException("The extension has no name.");
		}
		this.className = element.getChildTextTrim( "class" );
		this.classPath = element.getChildTextTrim( "classpath" );
		this.autoStartCondition = element.getChildTextTrim( "autostart" );
		if (this.autoStartCondition != null) {
			this.autoStartCondition = this.autoStartCondition.replace( '\n', ' ' );
		}
	}

	public Project getAntProject() {
		return this.antProject;
	}
	
	public Element getElement() {
		return this.element;
	}
	
	public ExtensionManager getManager() {
		return this.manager;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return this.type;
	}
	
	
	public String getClassName() {
		return this.className;
	}
	
	public String getClassPath() {
		return this.classPath;
	}

	/**
	 * Retrieves the value of the specified parameter name, if defined at all.
	 * 
	 * @param parameterName the name of the parameter 
	 * @return the value or null of this parameter has not been defined
	 */
	public String getParameterValue(String parameterName ) {
		return this.element.getChildTextTrim(parameterName );
	}

	/**
	 * @return the condition for starting this extension automatically
	 */
	public String getAutoStartCondition() {
		return this.autoStartCondition;
	}
	
	/**
	 * Checks whether the autostart condition for this extension is fulfilled.
	 * 
	 * @param env the environment
	 * @return true when this condition is fulfilled
	 */
	public boolean isConditionFulfilled(Environment env) {
		if (this.autoStartCondition == null) {
			return true;
		}
		return env.isConditionFulfilled( this.autoStartCondition );
	}

	
}
