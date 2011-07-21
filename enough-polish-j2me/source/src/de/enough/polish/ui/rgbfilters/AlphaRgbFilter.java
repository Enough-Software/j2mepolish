//#condition polish.usePolishGui
/*
 * Created on Jul 8, 2008 at 5:10:39 PM.
 *
 * Copyright (c) 2010 Robert Virkus / Enough Software
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

package de.enough.polish.ui.rgbfilters;

import java.io.IOException;

import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Graphics;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.RgbFilter;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.util.ImageUtil;
import de.enough.polish.util.RgbImage;

/**
 * This filter applies the alpha values
 * of a specified image to the input image
 *
 * @author Andre Schmidt
 */
public class AlphaRgbFilter extends RgbFilter
{
	/**
	 * the RgbImage alpha map
	 */
	transient RgbImage alphaMap;
	
	/**
	 * flag indicating if the input in process should be processed directly
	 */
	boolean direct = false;
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#process(de.enough.polish.util.RgbImage)
	 */
	public RgbImage process(RgbImage input) {
		RgbImage result;
		
		if(this.direct) {
			result = input;
		} else {
			result = new RgbImage(input);
		}

		// apply the alpha map onto the copied input
		//#if tmp.supportImageOperations
        ImageUtil.applyAlphaOntoRgbImage(result, this.alphaMap);
        //#endif
        
		return result;
    }
    
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#isActive()
	 */
	public boolean isActive() {
		return true;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		//#if polish.css.filter-alpha-image
		String alphaMapUrl = style.getProperty( "filter-alpha-image" );
		try {
			Image img = StyleSheet.getImage(alphaMapUrl, this, true);
			this.alphaMap = new RgbImage(img, true);
		} catch (IOException e) {
			//#debug error
			System.out.println("unable to load image [" + alphaMapUrl + "]" + e);
		}
		//#endif
		//#if polish.css.filter-direct
		Boolean filterDirectBoolean = style.getBooleanProperty( "filter-direct" );
		if(filterDirectBoolean != null) {
			this.direct = filterDirectBoolean.booleanValue();
		}
		//#endif
	}	
}
