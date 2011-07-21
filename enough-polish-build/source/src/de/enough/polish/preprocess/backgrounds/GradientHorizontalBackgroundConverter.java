/*
 * Created on 28.02.2008 at 15:46:23.
 * 
 * Copyright (c) 2008 Robert Virkus / Enough Software
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
 * Converter for de.enough.polish.ui.backgrounds.GradientHorizontalBackground.
 * @author Robert Virkus
 *
 */
public class GradientHorizontalBackgroundConverter extends BackgroundConverter {

	protected String createNewStatement(Map map, Style stlye,
			StyleSheet styleSheet) throws BuildException {
		String topColor = "0xFFFFFF";
		String topColorStr = (String) map.get("left-color");
		if ( topColorStr != null ) {
			topColor = parseColor( topColorStr );			
		}
		String bottomColor = "0x0000FF";
		String bottomColorStr = (String) map.get("right-color");
		if ( bottomColorStr != null ) {
			bottomColor = parseColor( bottomColorStr );			
		}
		String stroke = "Graphics.SOLID";
		String strokeStr =  (String)map.get("stroke");
		if ( strokeStr != null ) {
			stroke =  parseStroke( "stroke", strokeStr);			
		}
		int start = 0;
		int end = 0;
		boolean isPercent = false;
		String startStr = (String) map.get("start"); 
		if (startStr != null) {
			if (startStr.charAt(startStr.length()-1) == '%') {
				isPercent = true;
				start = parseInt( "start", startStr.substring(0, startStr.length() - 1));
			} else {
				start = parseInt( "start", startStr);
			}			
		}
		String endStr = (String) map.get("end"); 
		if (endStr != null) {
			if (endStr.charAt(endStr.length()-1) == '%') {
				isPercent = true;
				end = parseInt( "end", endStr.substring(0, endStr.length() - 1));
			} else {
				end = parseInt( "end", endStr);
			}	
		}
		String isPercentStr = (String) map.get("is-percent");
		if (!isPercent && isPercentStr != null) {
			isPercent = parseBoolean("is-percent", isPercentStr);
		}

		String result = "new de.enough.polish.ui.backgrounds.GradientHorizontalBackground(" 
			+ topColor + ", " + bottomColor + ","+ stroke 
			+ "," + start + "," + end + "," + isPercent + ")";
		
		return result;
	}

}
