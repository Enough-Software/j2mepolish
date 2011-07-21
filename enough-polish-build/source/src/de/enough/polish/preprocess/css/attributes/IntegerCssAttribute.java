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

import de.enough.polish.BuildException;
import de.enough.polish.Environment;
import de.enough.polish.preprocess.css.CssAnimationSetting;
import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.Style;

/**
 * <p>A simple numerical attribute.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class IntegerCssAttribute extends CssAttribute {
	
	/**
	 * Creates a new instance.
	 */
	public IntegerCssAttribute() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getValue(java.lang.String, de.enough.polish.Environment)
	 */
	public String getValue(String value, Environment environment ) {
		if (this.allowedValues == null) {
			try {
				int intValue;
				try {
					intValue = parseInt(value);
				} catch (NumberFormatException e) {
					String processedValue = environment.getProperty( "calculate(" + value + ")", true);
					intValue = parseInt(processedValue);
				}
				if (this.isBaseAttribute ) {
					return Integer.toString( intValue );
				} else {
					return "new Integer(" + intValue + ")";
				}
			} catch (NumberFormatException e) {
				throw new BuildException("Invalid CSS: The attribute [" + this.name + "] needs an integer value. The value [" + value + "] cannot be accepted.");
			} catch (BuildException e) {
				throw new BuildException("Unable to parse integer value \"" + value + "\" of CSS attribute " + this.name + ": " + e.getMessage() );
			}
		} else {
			// there are fixed allowed values defined:
			for (int i = 0; i < this.allowedValues.length; i++) {
				if (!this.isCaseSensitive) {
					value = value.toLowerCase();
				}
				if (value.equals( this.allowedValues[i])) {
					if (this.isBaseAttribute) {
						return "" + i;
					} else {
						return "new Integer(" + i + ")";
					}
				}
			}
			StringBuffer sb = new StringBuffer();
			sb.append("Invalid CSS: the attribute [");
			sb.append(this.name);
			sb.append("] needs to be one of the following values: [");
			for (int i = 0; i < this.allowedValues.length; i++) {
				sb.append(this.allowedValues[i]);
				if (i < this.allowedValues.length - 1) {
					sb.append("], [");
				}
			}		
			sb.append("]. The value [");
			sb.append(value);
			sb.append("] is not supported.");
			throw new BuildException( sb.toString() );
		}
	}

	/**
	 * @param value
	 * @return the parsed value
	 */
	protected int parseInt(String value)
	{
		int l = value.length();
		if (l > 2 && value.endsWith("px")) {
			value = value.substring(0, l - 2);
		}
		return Integer.parseInt( value );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#instantiateValue(java.lang.String)
	 */
	public Object instantiateValue(String sourceCode) {
		if (this.isBaseAttribute) {
			return new Integer( parseInt(sourceCode));
		}
		return super.instantiateValue(sourceCode);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#generateAnimationSourceCode(de.enough.polish.preprocess.css.CssAnimationSetting, de.enough.polish.preprocess.css.Style, de.enough.polish.Environment)
	 */
	public String generateAnimationSourceCode(CssAnimationSetting cssAnimation, Style style, Environment environment)
	{
		return generateAnimationSourceCode("IntegerCssAnimation", cssAnimation, style, environment);
	}

}
