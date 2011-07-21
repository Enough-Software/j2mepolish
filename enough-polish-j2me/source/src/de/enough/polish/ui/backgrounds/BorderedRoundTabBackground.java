//#condition polish.usePolishGui
/*
 * Created on 24-Jan-2005 at 01:42:46.
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
 * <p>Paints a rectangle with top round corners and a border.</p>
 *
 * <p>Copyright Enough Software 2005 - 2009</p>
 * @author Robert Virkus, robert@enough.de
 */
public class BorderedRoundTabBackground extends Background {

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
	public BorderedRoundTabBackground( int color,  int arcWidth, int arcHeight, int borderColor, int borderWidth) {
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
		g.drawRoundRect( x, y, width, height, this.arcWidth, this.arcHeight );
		int border = this.borderWidth - 1;
		while ( border > 0) {
			g.drawRoundRect( x+border, y+border, width - (border<<1), height - (border<<1), this.arcWidth, this.arcHeight );
			border--;
		}
		g.setColor( this.color );
		y += height / 2;
		if ( (height & 1 ) == 1 ) {
			y += 1;
		}
		height /= 2;
		g.fillRect( x + this.borderWidth, y, width - 2 * this.borderWidth + 1, height + 1 );
		g.setColor( this.borderColor );
		g.drawLine( x, y, x, y + height );
		g.drawLine( x + width, y, x + width, y + height );
		g.drawLine( x, y + height, x + width, y + height );
		border = this.borderWidth - 1;
		while ( border > 0) {
			g.drawLine( x + border, y, x + border, y + height );
			g.drawLine( x + width - border, y, x + width - border, y + height );
			g.drawLine( x, y + height - border, x + width, y + height - border );
			border--;
		}
		
	}

	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		//#if polish.css.background-round-tab-bordered-color
			Color bgColor = style.getColorProperty("background-round-tab-bordered-color");
			if (bgColor != null) {
				this.color = bgColor.getColor();
			}
		//#endif
		//#if polish.css.background-round-tab-bordered-border-color
			Color brdColor = style.getColorProperty("background-round-tab-bordered-border-color");
			if (brdColor != null) {
				this.borderColor = brdColor.getColor();
			}
		//#endif
		//#if polish.css.background-round-tab-bordered-border-width
			Integer brdWidth = style.getIntProperty("background-round-tab-bordered-border-width");
			if (brdWidth != null) {
				this.borderWidth = brdWidth.intValue();
			}
		//#endif
	}
	//#endif
	
}
