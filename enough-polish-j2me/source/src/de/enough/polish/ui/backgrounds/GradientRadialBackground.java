//#condition polish.usePolishGui

/*
 * Created on 2008-03-02 at 4:37:12.
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
package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Style;
import de.enough.polish.util.DrawUtil;
/**
 * Generates a radial gradient from the inner-color to the outer-color.
 * @author Robert Virkus 
 */
public class GradientRadialBackground  extends Background {
	private int innerColor;
	private int outerColor;
	private Dimension start;
	private Dimension end;
	private Dimension centerX;
	private Dimension centerY;


	/**
	 * Creates a new radial gradient background
	 * 
	 * @param innerColor the color at the top of the gradient
	 * @param outerColor the color at the bottom of the gradient
	 * @param start the line counted from the top at which the gradient starts, either in pixels or in percent
	 * @param end the line counted from the top at which the gradient ends, either in pixels or in percent
	 */
	public GradientRadialBackground( int innerColor, int outerColor, int start, int end ) {
		this( innerColor, outerColor, start, end, 0, 0 );
	}
	
	/**
	 * Creates a new radial gradient background
	 * 
	 * @param innerColor the color at the top of the gradient
	 * @param outerColor the color at the bottom of the gradient
	 * @param start the line counted from the top at which the gradient starts, either in pixels or in percent
	 * @param end the line counted from the top at which the gradient ends, either in pixels or in percent
	 * @param centerX the horizontal center in percent. 0 is the center, -100 is the very left, +100 the very right
	 * @param centerY the vertical center in percent. 0 is the center, -100 is the very top, +100 the very bottom
	 */
	public GradientRadialBackground( int innerColor, int outerColor, int start, int end, int centerX, int centerY ) {
		this( innerColor, outerColor, new Dimension(start, true), new Dimension(end, true), new Dimension(centerX, true), new Dimension(centerY, true));
	}
	/**
	 * Creates a new radial gradient background
	 * 
	 * @param innerColor the color at the top of the gradient
	 * @param outerColor the color at the bottom of the gradient
	 * @param start the line counted from the top at which the gradient starts, either in pixels or in percent
	 * @param end the line counted from the top at which the gradient ends, either in pixels or in percent
	 * @param centerX the horizontal center in percent. 0 is the center, -100 is the very left, +100 the very right
	 * @param centerY the vertical center in percent. 0 is the center, -100 is the very top, +100 the very bottom
	 */
	public GradientRadialBackground( int innerColor, int outerColor, Dimension start, Dimension end, Dimension centerX, Dimension centerY ) {
		this.innerColor = innerColor;
		this.outerColor = outerColor;
		if (start != null && end != null && start.getValue(100) != end.getValue( 100)) {
			this.start = start;
			this.end = end;
		}
		this.centerX = centerX;
		this.centerY = centerY;
	}

	/*
	 * Paints the screen 
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		int steps = (Math.max( width, height) >> 1) - 1;
		int startOffset;
		int endOffset;
		if (this.start != null) {
			startOffset = this.start.getValue(steps);
			endOffset = this.end.getValue(steps);
		} else {
			startOffset = 0;
			endOffset = steps;
		}
		int targetX = (width >> 1) + (((width>>1)*this.centerX.getValue(width)) / 100) - startOffset;
		int targetY = (height >> 1) + (((height>>1)*this.centerY.getValue(height))) - startOffset;
		int originalX = x;
		int originalY = y;
		g.setColor( this.innerColor );
		for (int i = 0; i < steps; i++) {
			if (i >= startOffset  && i < endOffset ) {
				int color = DrawUtil.getGradientColor(this.outerColor, this.innerColor, i - startOffset, steps );
				g.setColor( color );
				
			}
			g.fillArc( x, y, width, height, 0, 360 );
			x = originalX + (targetX * i)/steps;
			y = originalY + (targetY * i)/steps;
			if (width > startOffset) {
				width -= 2;
			}
			if (height > startOffset) {
				height -= 2;
			}
		}
	}
	
	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		//#if polish.css.background-radial-gradient-inner-color
			Color tbgColor = style.getColorProperty("background-radial-gradient-inner-color");
			if (tbgColor != null) {
				this.innerColor = tbgColor.getColor();
			}
		//#endif
		//#if polish.css.background-radial-gradient-outer-color
			Color bbgColor = style.getColorProperty("background-radial-gradient-outer-color");
			if (bbgColor != null) {
				this.outerColor = bbgColor.getColor();
			}
		//#endif
		//#if polish.css.background-radial-gradient-start
			Dimension startObj = (Dimension) style.getObjectProperty("background-radial-gradient-start");
			if (startObj != null) {
				this.start = startObj;
			}
		//#endif
		//#if polish.css.background-radial-gradient-end
			Dimension endObj = (Dimension) style.getObjectProperty("background-radial-gradient-end");
			if (endObj != null) {
				this.end = endObj;
			}
		//#endif
		//#if polish.css.background-radial-gradient-center-x
			Dimension centerXObj = (Dimension) style.getObjectProperty("background-radial-gradient-center-x");
			if (centerXObj != null) {
				this.centerX = centerXObj;
			}
		//#endif
		//#if polish.css.background-radial-gradient-center-y
			Dimension centerYObj = (Dimension) style.getObjectProperty("background-radial-gradient-center-y");
			if (centerYObj != null) {
				this.centerY = centerYObj;
			}
		//#endif
	}
	//#endif
}
