/*
 * Created on 26-Apr-2005 at 18:35:58.
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

import org.jdom.Element;

/**
 * <p>Defines default behavior of extensions. Use the &lt;typedefinition&gt;-elements of the extensions.xml for configuring them.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        26-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ExtensionTypeDefinition {

	private final Element element;
	private final String defaultClassName;
	private final String defaultClassPath;
	private final String name;

	/**
	 * Creates a new extension type.
	 * 
	 * @param element the XML definition
	 */
	public ExtensionTypeDefinition(Element element) {
		super();
		this.element = element;
		this.name = element.getChildTextTrim("name");
		if (this.name == null) {
			throw new IllegalArgumentException("Each <typedefinition> requires the nested <name> element.");
		}
		this.defaultClassName = element.getChildTextTrim("defaultclass");
		if (this.defaultClassName == null) {
			throw new IllegalArgumentException("Each <typedefinition> requires the nested <defaultclass> element, please check the definition of the type [" + this.name + "] in custom-extensions.xml or extensions.xml.");
		}
		this.defaultClassPath = element.getChildTextTrim("defaultclasspath");
	}

	public String getDefaultClassName() {
		return this.defaultClassName;
	}
	public String getDefaultClassPath() {
		return this.defaultClassPath;
	}
	public Element getElement() {
		return this.element;
	}

	/**
   * Retrieves the name of the extension type.
   * 
	 * @return the name of the the extension type
	 */
	public String getName() {
		return this.name;
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
}
