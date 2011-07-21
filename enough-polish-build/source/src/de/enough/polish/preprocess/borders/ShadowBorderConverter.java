/*
 * Created on 10-Mar-2004 at 16:18:39.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.preprocess.borders;

import de.enough.polish.preprocess.css.BorderConverter;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.preprocess.css.StyleSheet;

import de.enough.polish.BuildException;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Creates a shadow-like border.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        10-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ShadowBorderConverter extends BorderConverter {
	
	private static final Map TYPES = new HashMap();
	static {
		TYPES.put("bottom-right-shadow", BORDERS_PACKAGE + "BottomRightShadowBorder");
		TYPES.put("right-bottom-shadow", BORDERS_PACKAGE + "BottomRightShadowBorder");
		TYPES.put("shadow", BORDERS_PACKAGE + "BottomRightShadowBorder");
	}
	
	/**
	 * Creates a new instance
	 */
	public ShadowBorderConverter() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BorderConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(Map border, Style style, StyleSheet styleSheet) throws BuildException {
		String typeName = (String) border.get("type");
		String type = (String) TYPES.get( typeName );
		if (type == null) {
			throw new BuildException("Invalid CSS: the shadow border [" + typeName + "] is not supported. Please define another shadow-border in the [type] argument, e.g. \"type: bottom-right-shadow\".");
		}
		String offset = (String) border.get("offset");
		if (offset == null) {
			offset = "1"; // default ofset
		} else {
			parseInt( "offset", offset );
		}
		return "new " + type + "( " + this.color + ", " + this.width 
				+ ", " + offset + ")";
	}
}
