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
import de.enough.polish.preprocess.css.CssMapping;
import de.enough.polish.preprocess.css.Style;

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
public class DimensionMapCssAttribute extends MapCssAttribute {
	
	/**
	 * Creates a new instance.
	 */
	public DimensionMapCssAttribute() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.attributes.MapCssAttribute#getValue(java.lang.String, de.enough.polish.Environment)
	 */
	public String getValue(String value, Environment environment ) {
		if (this.requiresMapping && this.mappingsByName == null) {
			throw new BuildException("Invalid CSS attribute definition of " + this.name + ": no mappings found.");
		}
		if ("none".equals(value)) {
			return "null";
		}
		CssMapping mapping = getMapping(value);
		if (mapping != null) {
			mapping.checkCondition( this.name, value, environment.getBooleanEvaluator() );
			return mapping.getTo();
		} else {
			return getDimensionValue(value, environment);
		}

	}

	/**
	 * Checks and transforms the given dimension CSS value for this attribute.
	 * 
	 * @param value the attribute value
	 * @param environment the environment
	 * @return the transformed value or the same value if no transformation is required.
	 * @throws BuildException when a condition is not met or when the value contains conflicting values
	 */
	public String getDimensionValue(String value, Environment environment ) {
		if (value.endsWith("%")) {
			try {
				String plainValue = value.substring( 0, value.length() - 1);
				int dotIndex = plainValue.indexOf('.');
				if (dotIndex == -1) {
					dotIndex = plainValue.indexOf(',');
				}
				int numberOfFloatingPointDigits = 0;
				if (dotIndex != -1) {
					numberOfFloatingPointDigits = plainValue.length() - (dotIndex + 1);
					plainValue = plainValue.substring(0, dotIndex) + plainValue.substring( dotIndex + 1);
				}
				int intValue = Integer.parseInt( plainValue );
				if (this.isBaseAttribute) {
					return "-" + intValue;
				} else {
					if (numberOfFloatingPointDigits == 0) {
						return "new Dimension( " + intValue + ", true )";
					} else {
						StringBuffer code = new StringBuffer();
						code.append( "new Dimension( " );
						code.append( intValue );
						code.append( ", 1");
						for (int i=0; i<numberOfFloatingPointDigits; i++) {
							code.append('0');
						}
						code.append( " )" );
						return  code.toString();
					}
				}
			} catch (NumberFormatException e) {
				throw new BuildException("Invalid CSS: The attribute [" + this.name + "] needs an percent value. The value [" + value + "] cannot be accepted.");			
			}
		}
		try {
			if (value.endsWith("pt")) {
				return "new Dimension(\"" + value + "\")";
			}
			if (value.endsWith("px")) {
				value = value.substring(0, value.length() - 2).trim();
			}
			
			int intValue;
			try {
				intValue = Integer.parseInt( value );
			} catch (NumberFormatException e) {
				try {
					String processedValue = environment.getProperty( "calculate(" + value + ")", true);
					intValue = Integer.parseInt( processedValue );
				} catch (Exception e2) {
					try { throw new RuntimeException(); } catch (Exception e3) { e3.printStackTrace(); }
					throw new BuildException("Invalid CSS: unable to parse percent/absolute value \"" + value + "\" for attribute \"" + this.name + "\". A numerical value or calculation is expected. Check your polish.css file.");
				}
			}
			if (this.isBaseAttribute) {
				return "" + intValue;
			} else {
				return "new Dimension(" + intValue + ", false)";
			}
		} catch (NumberFormatException e) {
			throw new BuildException("Invalid CSS: The attribute [" + this.name + "] needs an integer value. The value [" + value + "] cannot be accepted.");
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#generateAnimationSourceCode(de.enough.polish.preprocess.css.CssAnimationSetting, de.enough.polish.preprocess.css.Style, de.enough.polish.Environment)
	 */
	public String generateAnimationSourceCode(CssAnimationSetting cssAnimation, Style style, Environment environment)
	{
		return generateAnimationSourceCode("DimensionMapCssAnimation", cssAnimation, style, environment);
	}
}
