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

import de.enough.polish.Environment;
import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.CssMapping;
import de.enough.polish.preprocess.css.ParameterizedCssMapping;

/**
 * <p>A CSS attribute that can contain several complex parameters, e.g. a background definition.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ParameterizedCssAttribute extends CssAttribute {
	
	/**
	 * Creates a new instance.
	 */
	public ParameterizedCssAttribute() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getValue(java.lang.String, de.enough.polish.Environment)
	 */
	public String getValue(String value, Environment environment ) {
		CssMapping mapping = getMapping(value);
		if (mapping != null) {
			mapping.checkCondition( this.name, value, environment.getBooleanEvaluator() );
			return mapping.getConverter();
		} else {
			// should be a reference to the corresponding polish.css section, e.g. backgrounds/borders,
			// so return the alleged reference name:
			return "StyleSheet." + value + Character.toUpperCase( this.name.charAt(0) ) + this.name.substring(1);
			//return null;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#createMapping(org.jdom.Element)
	 */
	protected CssMapping createMapping(Element element) {
		return new ParameterizedCssMapping( this, element );
	}

}
