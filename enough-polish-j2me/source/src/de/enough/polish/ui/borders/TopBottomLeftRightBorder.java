//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:36:37.
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
 * <p>Paints a plain border in one color at the top, bottom, left, right or a combination of them.</p>
 * <p>
 * Following attributes can be used in the CSS definition:
 * </p>
 * <ul>
 *   <li><b>color</b>: the color of this border</li>
 *   <li><b>top-width</b>: the width of the top border</li>
 *   <li><b>bottom-width</b>: the width of the bottom border</li>
 *   <li><b>left-width</b>: the width of the left border</li>
 *   <li><b>right-width</b>: the width of the right border</li>
 * </ul>
 * <p>CSS Example:
 * <pre>
 * border {
 * 	color: red;
 *  top-width: 2;
 *  left-width: 3;
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software 2005 - 2009</p>
 * @author Robert Virkus, robert@enough.de
 */
public class TopBottomLeftRightBorder extends Border {

	private final int color;

	/**
	 * Creates a new simple border.
	 * 
	 * @param color the color of this border in RGB, e.g. 0xFFDD12
	 * @param topWidth the width of this border at the top
	 * @param bottomWidth the width of this border at the bottom
	 * @param leftWidth  the width of this border at the left
	 * @param rightWidth  the width of this border at the right
	 */
	public TopBottomLeftRightBorder( int color, int topWidth, int bottomWidth, int leftWidth, int rightWidth ) {
		super(leftWidth, rightWidth, topWidth, bottomWidth);
		this.color = color;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		int rightX = x + width - 1;
		int bottomY = y + height - 1;
		// paint top border:
		int border = this.borderWidthTop - 1;
		while ( border >= 0) {
			g.drawLine( x, y + border, rightX, y + border );
			border--;
		}
		// paint bottom border:
		border = this.borderWidthBottom - 1;
		while ( border >= 0) {
			g.drawLine( x, bottomY - border, rightX, bottomY - border );
			border--;
		}
		// paint left border:
		border = this.borderWidthLeft - 1;
		while ( border >= 0) {
			g.drawLine( x + border, y, x + border, bottomY );
			border--;
		}
		// paint right border:
		border = this.borderWidthRight - 1;
		while ( border >= 0) {
			g.drawLine( rightX - border, y, rightX - border, bottomY );
			border--;
		}

	}

}
