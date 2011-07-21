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
import de.enough.polish.util.DrawUtil;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a plain border in one color at the top of the item.</p>
 *
 * <p>Copyright Enough Software 2005 - 2009</p>
 * @author Robert Virkus, robert@enough.de
 */
public class TopBorder extends Border {

	private final int color;
	private final boolean isArgb;

	/**
	 * Creates a new top border.
	 * 
	 * @param color the color of this border in RGB, e.g. 0xFFDD12
	 * @param borderWidth the width of this border
	 */
	public TopBorder( int color, int borderWidth ) {
		super(0, 0, borderWidth, 0);
		this.color = color;
		this.isArgb = ((color & 0xff000000) != 0) && ((color & 0xff000000) != 0xff);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		int endX = x + width -1;
		int border = this.borderWidthTop - 1;
		int col = this.color;
		if (this.isArgb) {
			DrawUtil.drawLine(col, x, y, endX, y, g);
			while ( border > 0) {
				DrawUtil.drawLine(col, x, y + border, endX, y + border, g);
				border--;
			}
		} else {
			g.setColor( col );
			g.drawLine(x, y, endX, y );
			while ( border > 0) {
				g.drawLine(x, y + border, endX, y + border );
				border--;
			}
		}
	}

}
