//#condition polish.usePolishGui
/*
 * Created on 26-Jul-2004 at 14:13:59.
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
package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;

/**
 * <p>Paints a circular or elliptical background.</p>
 * <p>Following CSS-attributes are supported:</p>
 * <ul>
 * 	<li><b>type</b>: the type of the background, needs to be "circle".</li>
 * 	<li><b>color</b>: the color of the background, defaults to "white".</li>
 * 	<li><b>diameter</b>: the diameter of the background, when defined
 * 	always a circle will be painted.</li>
 * </ul>
 *
 * <p>Copyright Enough Software 2004 - 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CircleBackground extends Background {

	private int color;
	private int diameter;
	private final int anchor;
	private int xOffset;
	private int yOffset;
	private final boolean isPercent;
	

	/**
	 * Creates a new circle background.
	 * 
	 * @param color the color of the background.
	 * @param diameter the diameter of the background in pixels (positive numbers) or percent (negative numbers), when -1 this will
	 *      be ignored, otherwise this will result in a centered circle
	 *      (instead of an ellipse).
	 * @param anchor the anchor of the background, e.g. Graphics.RIGHT | Graphics.VCENTER
	 * @param xOffset The number of pixels to move the image horizontally, negative values move it to the left.
	 * @param yOffset The number of pixels to move the image vertically, negative values move it to the top.
	 */
	public CircleBackground( int color, int diameter, int anchor, int xOffset, int yOffset ) {
		this.color = color;
		this.isPercent = diameter < -1;
		this.diameter = this.isPercent ? -diameter : diameter;
		this.anchor = anchor;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		x += this.xOffset;
		y += this.yOffset;
		if (this.diameter != -1) {
			int diam = this.diameter;
			if (this.isPercent) {
				diam = (diam * Math.min( width, height)) / 100;
			}
			if ( (this.anchor & Graphics.HCENTER) == Graphics.HCENTER) {
				x += (width - diam) / 2;
			} else if ( (this.anchor & Graphics.RIGHT) == Graphics.RIGHT) {
				x += width - diam;
			}
			if ( (this.anchor & Graphics.VCENTER) == Graphics.VCENTER) {
				y += (height - diam)/ 2;
			} else if ( (this.anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
				y += height - diam;
			}
			width = diam;
			height = diam;
		}
		g.setColor( this.color );
		g.fillArc(x, y, width, height, 0, 360 );	
	}
	

	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		//#if polish.css.background-circle-color
			Color bgColor = style.getColorProperty("background-circle-color");
			if (bgColor != null) {
				this.color = bgColor.getColor();
			}
		//#endif
		//#if polish.css.background-circle-diameter
			Integer diameterInt = style.getIntProperty("background-circle-diameter");
			if (diameterInt != null) {
				this.diameter = diameterInt.intValue();
			}
		//#endif
		//#if polish.css.background-circle-x-offset
			Integer xOffsetInt = style.getIntProperty("background-circle-x-offset");
			if (xOffsetInt != null) {
				this.xOffset = xOffsetInt.intValue();
			}
		//#endif
		//#if polish.css.background-circle-y-offset
			Integer yOffsetInt = style.getIntProperty("background-circle-y-offset");
			if (yOffsetInt != null) {
				this.yOffset = yOffsetInt.intValue();
			}
		//#endif
			
	}
	//#endif

}
