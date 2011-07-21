/*
 * Created on Nov 20, 2007 at 9:22:28 PM.
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

import java.util.HashMap;

import org.jdom.Element;

import de.enough.polish.Environment;
import de.enough.polish.preprocess.css.CssAnimationSetting;
import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.CssMapping;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.util.StringUtil;

/**
 * <p>Wraps an existing CssAttribute under a new name.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Nov 20, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class WrappedCssAttribute extends CssAttribute
{

	protected CssAttribute parent;

	/**
	 * Creates an empty css attribute
	 */
	public WrappedCssAttribute()
	{
		// initialize later by calling setParent and setDefinition
	}

	/**
	 * @param parent 
	 * @param definition
	 */
	public WrappedCssAttribute(CssAttribute parent, Element definition)
	{
		this.parent = parent;
		this.name = definition.getAttributeValue("name");
		this.type = definition.getAttributeValue("type");
		this.description = definition.getAttributeValue("description");
		this.since = definition.getAttributeValue("since");
		String idStr = definition.getAttributeValue("id");
		if (idStr == null) {
			this.id = parent.getId();
		} else {
			this.id = Integer.parseInt(idStr);
		}
		this.shell = definition.getAttributeValue("shell");
		this.appliesTo = definition.getAttributeValue("appliesTo");
		if ( this.appliesTo != null ) {
			String[] appliesChunks = StringUtil.splitAndTrim( this.appliesTo, ',');
			this.appliesToMap = new HashMap();
			for (int i = 0; i < appliesChunks.length; i++) {
				String chunk = appliesChunks[i];
				this.appliesToMap.put( chunk, Boolean.TRUE );
			}
		} else {
			this.appliesToMap = null;
		}
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#add(de.enough.polish.preprocess.css.CssAttribute)
	 */
	public void add(CssAttribute extension)
	{
		this.parent.add(extension);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		return this.parent.compareTo(o);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getAllowedValues()
	 */
	public String[] getAllowedValues()
	{
		return this.parent.getAllowedValues();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getApplicableMappings(java.lang.Class)
	 */
	public CssMapping[] getApplicableMappings(Class targetClass)
	{
		return this.parent.getApplicableMappings(targetClass);
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getDefaultValue()
	 */
	public String getDefaultValue()
	{
		return this.parent.getDefaultValue();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getDescription()
	 */
	public String getDescription()
	{
		if (this.description != null) {
			return this.description;
		}
		return this.parent.getDescription();
	}

//	/* (non-Javadoc)
//	 * @see de.enough.polish.preprocess.css.CssAttribute#getGroup()
//	 */
//	public String getGroup()
//	{
//		return this.parent.getGroup();
//	}

//	/* (non-Javadoc)
//	 * @see de.enough.polish.preprocess.css.CssAttribute#getId()
//	 */
//	public int getId()
//	{
//		return this.parent.getId();
//	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getMapping(java.lang.String)
	 */
	public CssMapping getMapping(String fromName)
	{
		return this.parent.getMapping(fromName);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getMappingByTo(java.lang.String)
	 */
	public CssMapping getMappingByTo(String toName)
	{
		return this.parent.getMappingByTo(toName);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getMappings()
	 */
	public CssMapping[] getMappings()
	{
		return this.parent.getMappings();
	}


//	/* (non-Javadoc)
//	 * @see de.enough.polish.preprocess.css.CssAttribute#getType()
//	 */
//	public String getType()
//	{
//		return this.parent.getType();
//	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getValue(java.lang.String, de.enough.polish.Environment)
	 */
	public String getValue(String value, Environment environment)
	{
		String code = this.parent.getValue(value, environment);
		if (this.shell != null) {
			int strtInde = this.shell.indexOf(')');
			code = this.shell.substring(0, strtInde) + code + this.shell.substring(strtInde);
		}
		return code;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getValuePosition(java.lang.String)
	 */
	public int getValuePosition(String value)
	{
		return this.parent.getValuePosition(value);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#hasFixValues()
	 */
	public boolean hasFixValues()
	{
		return this.parent.hasFixValues();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#instantiateDefault(de.enough.polish.Environment)
	 */
	public Object instantiateDefault(Environment environment)
	{
		return this.parent.instantiateDefault(environment);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#instantiateValue(java.lang.String)
	 */
	public Object instantiateValue(String sourceCode)
	{
		return this.parent.instantiateValue(sourceCode);
	}

//	/* (non-Javadoc)
//	 * @see de.enough.polish.preprocess.css.CssAttribute#isBaseAttribute()
//	 */
//	public boolean isBaseAttribute()
//	{
//		return this.parent.isBaseAttribute();
//	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#isDefault(java.lang.String)
	 */
	public boolean isDefault(String value)
	{
		return this.parent.isDefault(value);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#parseAndInstantiateValue(java.lang.String, de.enough.polish.Environment)
	 */
	public Object parseAndInstantiateValue(String valueStr, Environment environment)
	{
		return this.parent.parseAndInstantiateValue(valueStr, environment);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#setDefinition(org.jdom.Element)
	 */
	public void setDefinition(Element definition)
	{
		this.parent.setDefinition(definition);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#generateAnimationSourceCode(de.enough.polish.preprocess.css.CssAnimationSetting, de.enough.polish.preprocess.css.Style, de.enough.polish.Environment)
	 */
	public String generateAnimationSourceCode(CssAnimationSetting cssAnimation,
			Style style, Environment environment)
	{
		return this.parent.generateAnimationSourceCode(cssAnimation, style, environment);
	}
	
	

//	/* (non-Javadoc)
//	 * @see de.enough.polish.preprocess.css.CssAttribute#setId(int)
//	 */
//	public void setId(int id)
//	{
//		this.parent.setId(id);
//	}
	
	

}
