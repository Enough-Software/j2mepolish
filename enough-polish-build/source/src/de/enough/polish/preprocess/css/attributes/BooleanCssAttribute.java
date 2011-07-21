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
import de.enough.polish.preprocess.css.CssAttribute;

/**
 * <p>A simple boolean based attribute.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BooleanCssAttribute extends CssAttribute {
	
	/**
	 * Creates a new instance.
	 */
	public BooleanCssAttribute() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getValue(java.lang.String, de.enough.polish.Environment)
	 */
	public String getValue(String value, Environment environment ) {
		if ("true".equals( value ) || "yes".equals( value )) {
			if (this.isBaseAttribute) {
				return "true";
			}
			return "Style.TRUE";
		} else if ("false".equals( value ) || "no".equals( value )) {
			if (this.isBaseAttribute) {
				return "false";
			}
			return "Style.FALSE";
		} else {
			try {
				boolean result = environment.getBooleanEvaluator().evaluate(value, "polish.css", -1 );
				if (result) {
					if (this.isBaseAttribute) {
						return "true";
					}
					return "Style.TRUE";
				} else {
					if (this.isBaseAttribute) {
						return "false";
					}
					return "Style.FALSE";					
				}
			} catch (BuildException e) {
				throw new BuildException( "Invalid CSS: the attribute \"" + this.name + "\" needs to be eiter \"true\" or \"false\" or a valid boolean expression - the given value \"" + value + "\" is not supported."  );
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#instantiateValue(java.lang.String)
	 */
	public Object instantiateValue(String sourceCode) {
		//if (this.isBaseAttribute) {
			if ("true".equals(sourceCode) || "Style.TRUE".equals(sourceCode)) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
//		} else {
//			
//		}
//		return super.instantiateValue(sourceCode);
	}
}
