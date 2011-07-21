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

/**
 * <p>Provides a partial background - this background will be usually used within a <code>combined</code> background.</p>
 * <pre>
 * backgrounds {
 * 	partialTop {
 * 		type: partial;
 * 		start: 0%;
 * 		end: 20%;
 * 		color: white;
 *  }
 * 	partialBottom {
 * 		type: partial;
 * 		start: 20%;
 * 		end: 100%;
 * 		color: blue;
 *  }
 * }
 * .myForm {
 * 	background {
 *    type: combined;
 *    foreground: partialTop;
 *    background: partialBottom;
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
public class PartialSimpleBackground extends Background
{
	private int color;
	private Dimension start;
	private Dimension end;
	
	/**
	 * Creates a new gradient background
	 * 
	 * @param color the color
	 * @param start the line counted from the top at which the gradient starts in percent
	 * @param end the line counted from the top at which the gradient ends in percent
	 */
	public PartialSimpleBackground( int color, int start, int end ) {
		this( color, new Dimension(start, true), new Dimension(end, true) );
	}
	
	/**
	 * Creates a new gradient background
	 * 
	 * @param color the color
	 * @param start the line counted from the top at which the gradient starts in percent
	 * @param end the line counted from the top at which the gradient ends in percent
	 */
	public PartialSimpleBackground( int color, Dimension start, Dimension end ) {
		this.color = color;
		this.start = start;
		this.end = end;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g)
	{
		
		int yStart = y + this.start.getValue(height);
		int h = this.end.getValue(height) - this.start.getValue(height);
		g.setColor( this.color );
		g.fillRect(x, yStart, width, h );
	}
	
	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		//#if polish.css.background-partial-color
			Color tbgColor = style.getColorProperty("background-partial-color");
			if (tbgColor != null) {
				this.color = tbgColor.getColor();
			}
		//#endif
		//#if polish.css.background-partial-start
			Dimension startObj = (Dimension) style.getObjectProperty("background-partial-start");
			if (startObj != null) {
				this.start = startObj;
			}
		//#endif
		//#if polish.css.background-partial-end
			Dimension endObj = (Dimension) style.getObjectProperty("background-partial-end");
			if (endObj != null) {
				this.end = endObj;
			}
		//#endif
	}
	//#endif

}
