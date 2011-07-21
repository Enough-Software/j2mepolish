//#condition polish.midp2 && polish.usePolishGui && !polish.blackberry
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

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Graphics;
import de.enough.polish.ui.RgbFilter;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ImageUtil;
import de.enough.polish.util.RgbImage;

/**
 * This filter applies a drop shadow to an image
 *
 * @author Ovidiu Iliescu
 */
public class DropShadowRgbFilter extends RgbFilter
{

    private int innerColor = 0xA0909090;
    private int outerColor = 0x20909090;
    private int size=6;
    private int xOffset=1, yOffset=2;

    public int getSize()
    {
        return size;
    };

    public int getXOffset()
    {
        return xOffset;
    }

    public int getYOffset()
    {
        return yOffset;
    }

    public RgbImage process(RgbImage input) {

        // calculate imagesize
        int imgHeight = input.getHeight();
        int imgWidth = input.getWidth();
        int newWidth=imgWidth + this.size*2 + Math.abs ( xOffset);
        int newHeight=imgHeight+ this.size*2 + Math.abs ( yOffset );

        // additional Margin for the image because of the shadow
        int iLeft = this.size-this.xOffset<0 ? 0 : this.size-this.xOffset;
        int iTop = this.size-this.yOffset<0 ? 0 : this.size-this.yOffset;

        RgbImage newImage = new RgbImage(newWidth, newHeight);
        ImageUtil.drawRgbImageOntoOther(newImage,input,iLeft,iTop);
        Graphics.dropShadow(newImage.getRgbData(),newWidth,newHeight,this.xOffset, this.yOffset, this.size,this.innerColor, this.outerColor);
        return newImage;
    }

    public boolean isActive() {
        return ( (size != 0) );
    }

    public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		//#if polish.css.drop-shadow-inner-color
			Color sShadowColorObj = style.getColorProperty( "drop-shadow-inner-color" );
			if (sShadowColorObj != null) {
				this.innerColor = sShadowColorObj.getColor();
			}
		//#endif
		//#if polish.css.drop-shadow-outer-color
			Color eShadowColorObj = style.getColorProperty( "drop-shadow-outer-color" );
			if (eShadowColorObj != null) {
				this.outerColor = eShadowColorObj.getColor();
			}
		//#endif

		//#if polish.css.drop-shadow-size
			Integer sizeInt = style.getIntProperty( "drop-shadow-size" );
			if (sizeInt != null) {
				this.size = sizeInt.intValue();
			}
		//#endif
		//#if polish.css.drop-shadow-offsetx
			Integer oXInt = style.getIntProperty( "drop-shadow-offsetx" );
			if (oXInt != null) {
				this.xOffset = oXInt.intValue();
			}
		//#endif
		//#if polish.css.drop-shadow-offsety
			Integer oYInt = style.getIntProperty( "drop-shadow-offsety" );
			if (oYInt != null) {
				this.yOffset = oYInt.intValue();
			}
		//#endif
	}

}
