//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:43:23.
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
package de.enough.polish.ui.borders;

import de.enough.polish.ui.Border;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a border with round corners.</p>
 *
 * <p>Copyright Enough Software 2004 - 2009</p>
 * @author Robert Virkus, robert@enough.de
 */
public class RoundRectBorder extends Border {

	protected int color;
	private final int arcWidth;
	private final int arcHeight;

	/**
	 * Creates a new round rectangle border.
	 * 
	 * @param color the color of the background
	 * @param borderWidth the width of the border
	 * @param arcWidth the horizontal diameter of the arc at the four corners
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 */
	public RoundRectBorder( int color, int borderWidth, int arcWidth, int arcHeight ) {
		super( borderWidth, borderWidth, borderWidth, borderWidth );
		this.color = color;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		// weird thing is that Graphics.drawRoundRect() surrounds more area than Graphics.drawRect(). Strange thing that.
		width--;
		height--;
		g.drawRoundRect( x, y, width, height, this.arcWidth, this.arcHeight );
		int border = this.borderWidthLeft - 1;
		while ( border > 0) {
			g.drawRoundRect( x+border, y+border, width - 2*border, height - 2*border, this.arcWidth, this.arcHeight );
			border--;
		}
	}


}
