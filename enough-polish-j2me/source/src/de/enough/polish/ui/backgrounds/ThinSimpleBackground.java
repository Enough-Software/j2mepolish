//#condition polish.usePolishGui
/*
 * Created on Jul 29, 2006 at 6:34:22 PM.
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

/**
 * <p>Renders a simple background with a maxium width.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Jul 29, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ThinSimpleBackground extends Background {
	
	private final int color;
	private final int maxWidth;
	private final boolean isPercent;
	private int pixelWidth;
	private int lastWidth;
	private int xOffset;
	private final int borderColor;

	/**
	 * Creates a new ThinSimpleBackground.
	 * 
	 * @param color the color
	 * @param maxWidth the maxium width in either pixel or percent (0..100)
	 * @param isPercent true when the maxWidth parameter represents a percentage value
   * @param borderWidth the width of the border
   * @param borderColor the color of the border
	 */
	public ThinSimpleBackground( int color, int maxWidth, boolean isPercent, int borderWidth, int borderColor ) {
		this.color = color;
		this.maxWidth = maxWidth;
		this.isPercent = isPercent;
		this.borderWidth = borderWidth;
		this.borderColor = borderColor;
	}

	public void paint(int x, int y, int width, int height, Graphics g) {
		if (width != this.lastWidth) {
			int pw;
			if (this.isPercent) {
				pw = (width * this.maxWidth) / 100;
			} else {
				pw = Math.max( width, this.maxWidth);
			}
			this.pixelWidth = pw;
			this.xOffset = (width - pw) / 2;
			this.lastWidth = width;
		}
		g.setColor( this.color );
		x += this.xOffset;
		width = this.pixelWidth;
		g.fillRect( x + this.xOffset, y, this.pixelWidth, height );
		
		if (this.borderWidth > 0) {
			g.setColor( this.borderColor );
			int border = this.borderWidth;
			while ( border > 0) {
				g.drawRect( x+border, y+border, width - 2*border, height - 2*border );
				border--;
			}
		}

	}
	
	

}
