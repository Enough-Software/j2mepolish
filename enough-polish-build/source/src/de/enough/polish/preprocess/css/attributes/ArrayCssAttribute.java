/*
 * Created on Oct 13, 2007 at 8:07:47 PM.
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
package de.enough.polish.preprocess.css.attributes;

import org.jdom.Element;

import de.enough.polish.BuildException;
import de.enough.polish.Environment;
import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.CssAttributesManager;
import de.enough.polish.util.StringUtil;

/**
 * <p>Allows to use any CssAttribute/type as an array</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Oct 13, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ArrayCssAttribute extends CssAttribute {

	private String separator;
	private String arrayType;
	private CssAttribute baseAttribute;

	/**
	 * Creates a new instance
	 */
	public ArrayCssAttribute() {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#setDefinition(org.jdom.Element)
	 */
	public void setDefinition(Element definition) {
		super.setDefinition(definition);
		String sep = definition.getAttributeValue("separator");
		if (sep == null) {
			sep = " ";
		}
		this.separator = sep;
		String arrType = definition.getAttributeValue("array-type");
		if (arrType == null) {
			throw new BuildException("Invalid CSS attribute: every array definition requires the attribute \"array-type\" - please check custom-css-attributes.xml" );
		}
		this.arrayType = arrType;
		String type = definition.getAttributeValue("type");
		if (type == null) {
			throw new BuildException("Invalid CSS attribute: every definition requires the attribute \"type\" - please check custom-css-attributes.xml" );
		}
		if (!type.endsWith("[]")) {
			throw new BuildException("Invalid CSS attribute: array definitions need to end the \"type\" attribute with \"[]\" - please check custom-css-attributes.xml" );
		}
		type = type.substring(0, type.length() - 2).trim();
		CssAttribute attribute = CssAttributesManager.getInstance().getAttribute(type);
		if (attribute == null) {
			attribute = CssAttributesManager.getInstance().getType(type);
		}
		if (attribute == null) {
			throw new BuildException("Invalid CSS attribute: unknown base type for array attribute: \"" + type + "\" - please check custom-css-attributes.xml" );
		}
		if (attribute.getName() == null) {
			attribute.setName( this.name );
		}
		this.baseAttribute = attribute;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getValue(java.lang.String, de.enough.polish.Environment)
	 */
	public String getValue(String value, Environment environment) {
		if ("null".equals(value) || "none".equals(value)) {
			return "null";
		}
		String[] values = StringUtil.splitAndTrim(value, this.separator);
		StringBuffer buffer = new StringBuffer();
		buffer.append("new " + this.arrayType + "[]{ ");
		for (int i = 0; i < values.length; i++) {
			value = values[i];
			buffer.append( this.baseAttribute.getValue(value, environment) );
			if (i != values.length - 1) {
				buffer.append(", ");
			}
		}
		buffer.append("}");
		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#instantiateValue(java.lang.String)
	 */
	public Object instantiateValue(String sourceCode) {
		// TODO robertvirkus implement instantiateValue
		return super.instantiateValue(sourceCode);
	}
	
	
	
	
	

}
