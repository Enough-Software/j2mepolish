/*
 * Created on 06.12.2005 at 12:38:34.
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

public class XmasSnowBackgroundConverter extends BackgroundConverter {
	
	public XmasSnowBackgroundConverter() 
	{
		super();
	}
	
	protected String createNewStatement(Map map, Style stlye,
			StyleSheet styleSheet) throws BuildException {
		String result = "new de.enough.polish.ui.backgrounds.XmasSnowBackground(" 
			+ this.color + ", ";
		String image;
		String imageStr = (String) map.get("image");
		if (imageStr == null) {
			throw new BuildException("Invalid CSS: You need to specify the \"image\" attribute for the background.");
		}
		image =  '"' + getUrl( imageStr ) + '"';
		int width ;
		String widthStr = (String)map.get("width");
		if ( widthStr == null ) {
			throw new BuildException("Invalid CSS: You need to specify the \"width\" attribute for the background.");			
		}
		width =  parseInt(widthStr, widthStr);
		int height ;
		String heightStr =  (String)map.get("height");
		if ( heightStr == null ) {
			throw new BuildException("Invalid CSS: You need to specify the \"height\" attribute for the background.");			
		}
		height =  parseInt(heightStr, heightStr);
		int far ;
		String farStr =  (String)map.get("far");
		if ( farStr == null ) {
			throw new BuildException("Invalid CSS: You need to specify the \"far\" attribute for the background.");			
		}
		far =  parseInt(farStr, farStr);
		int number = 1;
		String numberStr =  (String)map.get("number");
		if ( numberStr != null ) {
			number =  parseInt(numberStr, numberStr);			
		}
		result +=  image +","+width+","+height+ ","+far+ ","+number+")";
		
		return result;
	}
}
