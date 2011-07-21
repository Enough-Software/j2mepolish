//#condition polish.usePolishGui

/*
 * Created on 09.06.2006 at 15:41:12.
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
 * Generates a gradient from the left-color to the right-color.
 * Copyright 2008 Enough Software
 * @author Robert Virkus 
 */
public class GradientHorizontalBackground  extends Background {
	private int leftColor;
	private int rightColor;
	private final int stroke;
	private Dimension start;
	private Dimension end;

	private int[] gradient;
	private int lastWidth;
	private int startLine;
	private int endLine;

	/**
	 * Creates a new gradient background
	 * 
	 * @param leftColor the color at the left edge of the gradient
	 * @param rightColor the color at the right edge of the gradient
	 * @param stroke the line stroke style
	 */
	public GradientHorizontalBackground(int leftColor, int rightColor,int stroke) {
		this( leftColor, rightColor, stroke, null, null );
		
	}
	

	/**
	 * Creates a new gradient background
	 * 
	 * @param leftColor the color at the top of the gradient
	 * @param rightColor the color at the bottom of the gradient
	 * @param stroke the line stroke style
	 * @param start the line counted from the top at which the gradient starts, either in pixels or in percent
	 * @param end the line counted from the top at which the gradient ends, either in pixels or in percent
	 * @param isPercent true when the start and end settings should be counted in percent
	 */
	public GradientHorizontalBackground( int leftColor, int rightColor, int stroke, int start, int end, boolean isPercent ) {
		this( leftColor, rightColor, stroke, new Dimension(start, isPercent), new Dimension( end, isPercent ));
	}
	
	
	/**
	 * Creates a new gradient background
	 * 
	 * @param leftColor the color at the top of the gradient
	 * @param rightColor the color at the bottom of the gradient
	 * @param stroke the line stroke style
	 * @param start the line counted from the top at which the gradient starts, either in pixels or in percent
	 * @param end the line counted from the top at which the gradient ends, either in pixels or in percent
	 */
	public GradientHorizontalBackground( int leftColor, int rightColor, int stroke, Dimension start, Dimension end ) {
		this.leftColor = leftColor;
		this.rightColor = rightColor;
		this.stroke = stroke;
		if (start != null && end != null && start.getValue(100) != end.getValue( 100)) {
			this.start = start;
			this.end = end;
		}
	}
	
	/*
	 * Paints the screen 
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setStrokeStyle(this.stroke);
		int startOffset = this.startLine;
		int endOffset = this.endLine;
		if (this.gradient == null || this.lastWidth != width ) {
			int steps = width;
			if (this.start != null) {
				this.startLine = this.start.getValue( width );
				this.endLine = this.end.getValue( width );
				steps = this.endLine - this.startLine;
				startOffset = this.startLine;
				endOffset = this.endLine;				
			} else {
				this.endLine = width;
				endOffset = width;
			}
			this.gradient = DrawUtil.getGradient( this.leftColor, this.rightColor, steps );
			this.lastWidth = width;
		}
		g.setColor( this.leftColor );
		for (int i = 0; i < width; i++) {
			if (i >= startOffset  && i < endOffset ) {
				int color = this.gradient[i - startOffset ];
				g.setColor( color );
				
			}
			g.drawLine( x, y, x, y + height);
			x++;
		}
		g.setStrokeStyle( Graphics.SOLID );
	}
	
	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		boolean hasChanged = false;
		//#if polish.css.background-horizontal-gradient-left-color
			Color lbgColor = style.getColorProperty("background-horizontal-gradient-left-color");
			if (lbgColor != null) {
				this.leftColor = lbgColor.getColor();
				hasChanged = true;
			}
		//#endif
		//#if polish.css.background-horizontal-gradient-right-color
			Color rbgColor = style.getColorProperty("background-horizontal-gradient-right-color");
			if (rbgColor != null) {
				this.rightColor = rbgColor.getColor();
				hasChanged = true;
			}
		//#endif
		//#if polish.css.background-horizontal-gradient-start
			Dimension startObj = (Dimension) style.getObjectProperty("background-horizontal-gradient-start");
			if (startObj != null) {
				this.start = startObj;
				hasChanged = true;
			}
		//#endif
		//#if polish.css.background-horizontal-gradient-end
			Dimension endObj = (Dimension) style.getObjectProperty("background-horizontal-gradient-end");
			if (endObj != null) {
				this.end = endObj;
				hasChanged = true;
			}
		//#endif
		if (hasChanged) {
			this.lastWidth = 0;
		}
	}
	//#endif
}
