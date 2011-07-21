//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:29:46.
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

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a rectangle with round corners and a border.</p>
 * <p>This background-type can save some memory and processing time,
 *       when used instead of the RoundRectBackground and a RoundRectBorder together.
 * </p>
 *
 * <p>Copyright Enough Software 2005 - 2009</p>
 * @author Robert Virkus, robert@enough.de
 */
public class BorderedRoundRectBackground extends Background {

	private int color;
	private final int arcWidth;
	private final int arcHeight;
	private int borderColor; 

	/**
	 * Creates a new round rectangle background with a border.
	 * 
	 * @param color the color of the background
	 * @param arcWidth the horizontal diameter of the arc at the four corners
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 * @param borderColor the color of the border
	 * @param borderWidth the width of the border
	 */
	public BorderedRoundRectBackground( int color,  int arcWidth, int arcHeight, int borderColor, int borderWidth) {
		this.color = color;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		g.fillRoundRect( x, y, width, height, this.arcWidth, this.arcHeight );
		width--;
		height--;
		g.setColor( this.borderColor );
		int border = this.borderWidth;
		while ( border > 0) {
			g.drawRoundRect( x+border, y+border, width - 2*border, height - 2*border, this.arcWidth, this.arcHeight );
			border--;
		}
	}

	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		//#if polish.css.background-round-rect-bordered-color
			Color col = style.getColorProperty("background-round-rect-bordered-color");
			if (col != null) {
				this.color = col.getColor();
			}
		//#endif
		//#if polish.css.background-round-rect-bordered-border-color
			Color bcol = style.getColorProperty("background-round-rect-bordered-border-color");
			if (bcol != null) {
				this.borderColor = bcol.getColor();
			}
		//#endif
			//#if polish.css.background-round-rect-bordered-border-width
			Integer brdWidth = style.getIntProperty("background-round-rect-bordered-border-width");
			if (brdWidth != null) {
				this.borderWidth = brdWidth.intValue();
			}
		//#endif
	}
	//#endif
	
	
	
}
