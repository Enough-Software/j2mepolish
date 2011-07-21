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

/**
 * <p>A attribute referring to a style.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class StyleCssAttribute extends CssAttribute {
	
	/**
	 * Creates a new instance.
	 */
	public StyleCssAttribute() {
		super();
	}


//	/* (non-Javadoc)
//	 * @see de.enough.polish.preprocess.css.CssAttribute#instantiateValue(java.lang.String)
//	 */
//	public Object instantiateValue(String sourceCode) {
//		try {
//			Class styleSheetClass = Class.forName("de.enough.polish.ui.StyleSheet");
//			Object style = null;
//			try {
//				style = ReflectionUtil.getStaticFieldValue(styleSheetClass, sourceCode);
//			} catch (Exception e) {
//				System.out.println("Warning: unable to resolve style field " + sourceCode + " - now trying with Style at the end.");
//			}
//		} catch (ClassNotFoundException e) {
//			System.out.println("Unable to load StyleSheet class: " + e );
//			e.printStackTrace();
//			return null;
//		}
//		
//		return super.instantiateValue(sourceCode);
//	}
	
	

}
