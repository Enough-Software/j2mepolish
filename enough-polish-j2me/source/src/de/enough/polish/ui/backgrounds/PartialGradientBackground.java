//#condition polish.usePolishGui

/*
 * Created on Dec 28, 2007 at 3:19:28 PM.
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
 * <p>Provides a partial gradient - this background will be usually used within a <code>combined</code> background.</p>
 * <pre>
 * backgrounds {
 * 	partialTop {
 * 		type: partial-gradient;
 * 		start: 10%;
 * 		end: 20%;
 * 		top-color: white;
 * 		bottom-color: blue;
 *  }
 * 	partialBottom {
 * 		type: partial-gradient;
 * 		start: 20%;
 * 		end: 40%;
 * 		top-color: blue;
 * 		bottom-color: white;
 *  }
 *  formFg {
 *  	type: combined;
 *  	foreground: partialTop;
 *  	background: partialBottom;
 *  }
 *  formBg {
 *  	color: white;
 *  }
 * }
 * .myForm {
 * 	background {
 *    type: combined;
 *    foreground: formFg;
 *    background: formBg;
 *  }
 * }
 * </pre>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Dec 28, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PartialGradientBackground extends Background
{

	private int topColor;
	private int bottomColor;
	private int stroke;
	private Dimension start;
	private Dimension end;
	private int startLine;
	private int endLine;
	private int[] gradient;
	private int lastHeight;
	
	/**
	 * Creates a new partial gradient background
	 * 
	 * @param topColor the color at the top of the gradient
	 * @param bottomColor the color at the bottom of the gradient
	 * @param stroke the line stroke style
	 * @param start the line counted from the top at which the gradient starts in percent
	 * @param end the line counted from the top at which the gradient ends in percent
	 */
	public PartialGradientBackground( int topColor, int bottomColor, int stroke, int start, int end ) {
		this( topColor, bottomColor, stroke, new Dimension(start, true), new Dimension(end, true));
	}
	
	/**
	 * Creates a new partial gradient background
	 * 
	 * @param topColor the color at the top of the gradient
	 * @param bottomColor the color at the bottom of the gradient
	 * @param stroke the line stroke style
	 * @param start the line counted from the top at which the gradient starts in percent
	 * @param end the line counted from the top at which the gradient ends in percent
	 */
	public PartialGradientBackground( int topColor, int bottomColor, int stroke, Dimension start, Dimension end ) {
		this.topColor = topColor;
		this.bottomColor = bottomColor;
		this.stroke = stroke;
		this.start = start;
		this.end = end;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g)
	{
		g.setStrokeStyle(this.stroke);
		int startOffset = this.startLine;
		int endOffset = this.endLine;
		int[] grad = this.gradient;
		if (grad == null || this.lastHeight != height ) {
			int steps = height;
			int startValue = this.start.getValue(height);
			int endValue = this.end.getValue(height);
			if (startValue != endValue) {
				this.startLine = startValue;
				this.endLine = endValue;
				steps = endValue - startValue;
				startOffset = startValue;
				endOffset = endValue;				
			} else {
				this.endLine = height;
				endOffset = height;
			}
			grad = DrawUtil.getGradient( this.topColor, this.bottomColor, steps );
			this.gradient = grad;
			this.lastHeight = height;
		}
		g.setColor( this.topColor );
		for (int i = startOffset; i < endOffset; i++) {
			int color = grad[i - startOffset ];
			g.setColor( color );
			g.drawLine( x, y + i, x + width, y + i);
		}
		g.drawLine( x, y + endOffset, x + width, y + endOffset);
		g.setStrokeStyle( Graphics.SOLID );	
	}
	
	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		boolean hasChanged = false;
		//#if polish.css.background-partial-gradient-top-color
			Color tbgColor = style.getColorProperty("background-partial-gradient-top-color");
			if (tbgColor != null) {
				this.topColor = tbgColor.getColor();
				hasChanged = true;
			}
		//#endif
		//#if polish.css.background-partial-gradient-bottom-color
			Color bbgColor = style.getColorProperty("background-partial-gradient-bottom-color");
			if (bbgColor != null) {
				this.bottomColor = bbgColor.getColor();
				hasChanged = true;
			}
		//#endif
		//#if polish.css.background-partial-gradient-start
			Dimension startObj = (Dimension) style.getObjectProperty("background-partial-gradient-start");
			if (startObj != null) {
				this.start = startObj;
				hasChanged = true;
			}
		//#endif
		//#if polish.css.background-partial-gradient-end
			Dimension endObj = (Dimension) style.getObjectProperty("background-partial-gradient-end");
			if (endObj != null) {
				this.end = endObj;
				hasChanged = true;
			}
		//#endif
		if (hasChanged) {
			this.lastHeight = 0;
		}
	}
	//#endif

}
