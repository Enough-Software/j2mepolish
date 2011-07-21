/*
 * Created on Apr 15, 2007 at 10:12:36 PM.
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

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.BuildException;
import de.enough.polish.Environment;
import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.CssMapping;
import de.enough.polish.util.StringUtil;

/**
 * <p>An attribute that consists of a mapped value or a combination.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MapCssAttribute extends CssAttribute {
	
	private char separator = '|';

	/**
	 * Creates a new instance.
	 */
	public MapCssAttribute() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#setDefinition(org.jdom.Element)
	 */
	public void setDefinition(Element definition)
	{
		super.setDefinition(definition);
		String separatorStr = definition.getAttributeValue("combinationsSeparator");
		if (separatorStr != null) {
			this.separator  = separatorStr.charAt(0);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getValue(java.lang.String, de.enough.polish.Environment)
	 */
	public String getValue(String value, Environment environment ) {
		if (this.requiresMapping && this.mappingsByName == null) {
			throw new BuildException("Invalid CSS attribute definition of " + this.name + ": no mappings found.");
		}
		if ("none".equals(value)) {
			return "null";
		}
		if (!this.allowsCombinations) {
			CssMapping mapping = getMapping(value);
			if (mapping != null) {
				mapping.checkCondition( this.name, value, environment.getBooleanEvaluator() );
				return mapping.getTo();
			} else if (this.requiresMapping) {
				throw new BuildException("Invalid CSS: the attribute \"" + this.name + "\" does not support the value \"" + value + "\".");
			} else {
				// System.out.println("returning unmapped value " + value );
				return value;
			}
		} else {
			// combinations are allowed
			if (!this.isCaseSensitive) {
				value = value.toLowerCase();
			}
			BooleanEvaluator evaluator =  environment.getBooleanEvaluator();
			value = StringUtil.replace(value, " or ", " | ");
			value = StringUtil.replace(value, " and ", " | ");
			value = StringUtil.replace(value, " || ", " | ");
			value = StringUtil.replace(value, " && ", " | ");
			value = value.replace('&', this.separator);
			if (this.separator != ',') {
				value = value.replace(',', this.separator);
			}
			if (this.separator != '|') {
				value = value.replace('|', this.separator);
			}
			String[] values = StringUtil.splitAndTrim(value, this.separator);
			StringBuffer convertedValueBuffer = new StringBuffer();
			for (int i = 0; i < values.length; i++) {
				String singleValue = values[i];
				CssMapping mapping = getMapping(singleValue);
				if (mapping != null) {
					mapping.checkCondition( this.name, value, evaluator );
					convertedValueBuffer.append( mapping.getTo() );
				} else if (this.requiresMapping) {
					throw new BuildException("Invalid CSS: the attribute \"" + this.name + "\" does not support the value \"" + singleValue + "\".");
				} else {
					convertedValueBuffer.append( singleValue );
				}
				if (i != values.length - 1) {
					convertedValueBuffer.append(this.separator);
				}
			}
			if (this.shell != null) {
				int startIndex = this.shell.indexOf(')');
				if (startIndex == -1) {
					startIndex = this.shell.indexOf('}');
					if (startIndex == -1) {
						throw new BuildException("Invalid css definition - the shell value " + this.shell + " is  invalid, neither ')' nor '}' found. Check the CSS attribute definition of " + this.name + " in your custom-css-attributes.xml.");
					}
				}
				convertedValueBuffer.insert(0, this.shell.substring(0, startIndex) );
				convertedValueBuffer.append( this.shell.substring(startIndex));
			}
			return convertedValueBuffer.toString();
//				if (this.isBaseAttribute) {
//					return convertedValueBuffer.toString();
//				} else if ( isInteger() ){
//					return "new Integer( " + convertedValueBuffer.toString() + ")";
//				}
		}
	}

//	/* (non-Javadoc)
//	 * @see de.enough.polish.preprocess.css.CssAttribute#getAllowedValues()
//	 */
//	public String[] getAllowedValues()
//	{
//		if (this.mappingsByName == null) {
//			return null;
//		} else {
//			return (String[]) this.mappingsByName.keySet().toArray( new String[ this.mappingsByName.size() ] );
//		}
//	}

}
