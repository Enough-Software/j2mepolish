//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:55:32.
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
 * <p>Paints a border which is like shadow, which is seen on the bottom and on the right of the bordered Item.</p>
 *
 * <p>Copyright Enough Software 2004 - 2009</p>
 * @author Robert Virkus, robert@enough.de
 */
public class BottomRightShadowBorder extends Border {
	private final int color;
	private final int offset;

	/**
	 * Creates a new border which is like a shadow which is seen on the bottom and on the right of the Item.
	 * 
	 * @param color the color of this border in RGB, e.g. 0xFFDD12
	 * @param borderWidth the width of this border
	 * @param offset the offset of the shadow
	 */
	public BottomRightShadowBorder( int color, int borderWidth, int offset ) {
		super(0, borderWidth, 0, borderWidth);
		this.color = color;
		this.offset = offset;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		int bottom = y + height;
		int right = x + width;
		int xOffset = x + this.offset;
		int yOffset = y + this.offset;
		// draw buttom line:
		g.drawLine( xOffset, bottom, right, bottom );
		// draw right line:
		g.drawLine( right, yOffset, right, bottom );
		
		if (this.borderWidthLeft > 1) {
			int border = this.borderWidthLeft - 1;
			while ( border > 0) {
				g.drawLine( xOffset, bottom - border, right, bottom - border );
				g.drawLine( right - border, yOffset, right - border, bottom );
				border--;
			}
		}
	}

}
