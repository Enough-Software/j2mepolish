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

import de.enough.polish.Environment;
import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.util.StringUtil;

/**
 * <p>The columns-width CSS attribute is a specialized one.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ColumnsWidthCssAttribute extends CssAttribute {
	
	/**
	 * Creates a new instance
	 */
	public ColumnsWidthCssAttribute() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getValue(java.lang.String, de.enough.polish.Environment)
	 */
	public String getValue(String value, Environment environment ) {
		// remove any spaces, e.g. "columns-width: 50, 100, *":
		value = StringUtil.replace( value, " ", "" );
		if (value.equals("equal")) {
			return "\"equal\"";
		} else if (value.equals("normal")) {
			return "\"normal\"";
		}
		String[] values = StringUtil.split(value, ',');
		StringBuffer result = new StringBuffer();
		result.append('"');
		for (int i = 0; i < values.length; i++) {
			String part = values[i];
			if ("*".equals(part)) {
				result.append(part);
			} else {
				try {
					String parsedPart = part;
					if (part.charAt(part.length()-1) == '%') {
						parsedPart = part.substring(0, part.length() - 1);
					}
					Double.parseDouble(parsedPart);
					result.append(part);
				} catch (NumberFormatException e) {
					if (part.startsWith("(")) {
						part = "calculate" + part;
					} else {
						part = "calculate(" + part + ")";
					}
					part = environment.getProperty(part, true);
					result.append(part);
				}
			}
			if (i < values.length -1) {
				result.append(",");
			}
		}
		result.append('"');
		return result.toString();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#instantiateValue(java.lang.String)
	 */
	public Object instantiateValue(String sourceCode) {
		if (sourceCode.length() > 2 && sourceCode.charAt(0) == '"' && sourceCode.charAt( sourceCode.length() - 1) == '"') {
			return sourceCode.substring( 1, sourceCode.length() - 1 );
		}
		return sourceCode;
	}
	
	
}
