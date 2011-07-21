/*
 * Created on 22.08.2005 at 10:48:44.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.preprocess.backgrounds;

import java.util.Map;

import de.enough.polish.BuildException;
import de.enough.polish.preprocess.css.BackgroundConverter;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.preprocess.css.StyleSheet;
/**
 * 
 * @author Tim Muders
 *
 */
public class SmoothColorBackgroundConverter extends BackgroundConverter {

	protected String createNewStatement(Map map, Style stlye,
			StyleSheet styleSheet) throws BuildException {
		String result = "new de.enough.polish.ui.backgrounds.SmoothColorBackground(" 
			+ this.color + ", ";
		String gradientColor = "0";
		String gradientColorStr = (String) map.get("gradient-color");
		if ( gradientColorStr != null ) {
			gradientColor = parseColor( gradientColorStr );			
		}
		int stroke = 0 ;
		String strokeStr =  (String)map.get("stroke");
		if ( strokeStr != null ) {
			if ("dotted".equals(strokeStr)) {
				stroke = 1;
			}
		}
		
		result += gradientColor +","+stroke +")";
		
		return result;
	}


}
