//#condition polish.usePolishGui
/*
 * Created on 09-Nov-2004 at 11:04:10.
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

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;

/**
 * <p>Draws an expanding backround which will show a border when it is fully expanded.</p>
 *
 * <p>Copyright Enough Software 2004 - 2009</p>

 * <pre>
 * history
 *        09-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BorderedRoundRectOpeningBackground extends Background {
	private boolean isAnimationRunning;
	private int currentHeight;
	private final int color;
	private final int startHeight;
	private final int speed;
	private int maxHeight;
	private final int arcWidth;
	private final int arcHeight;
	private final int borderColor; 

	/**
	 * Creates a new opening background.
	 * 
	 * @param color the color of the background.
	 * @param startHeight the start height, default is 1
	 * @param speed the constant speed in of pixels by which the background-height should be increased 
	 * 			at each animation-step, use -1 to accelerate
	 * @param arcWidth the horizontal diameter of the arc at the four corners
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 * @param borderColor the color of the border
	 * @param borderWidth the width of the border
	 * 
	 */
	public BorderedRoundRectOpeningBackground( int color, int startHeight, int speed, int arcWidth, int arcHeight, int borderColor, int borderWidth ) {
		super();
		this.color = color;
		this.startHeight = startHeight;
		this.speed = speed;
		this.arcHeight = arcHeight;
		this.arcWidth = arcWidth;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		this.maxHeight = height;
		if (this.isAnimationRunning) {
			int difference = height - this.currentHeight;
			height = this.currentHeight;
			y += difference >> 1;	
		}
		g.setColor( this.color );
		g.fillRoundRect( x, y, width, height, this.arcWidth, this.arcHeight );
		if (!this.isAnimationRunning && this.borderWidth > 0) {
			// draw the border:
			width--;
			height--;
			g.setColor( this.borderColor );
			g.drawRoundRect( x, y, width, height, this.arcWidth, this.arcHeight );
			int border = this.borderWidth - 1;
			while ( border > 0) {
				g.drawRoundRect( x+border, y+border, width - (border<<1), height - (border<<1), this.arcWidth, this.arcHeight );
				border--;
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#animate()
	 */
	public boolean animate() {
		if (this.isAnimationRunning) {
			int adjust = this.speed;
			if (adjust == -1) {
				adjust = (this.maxHeight - this.currentHeight) / 3;
				if (adjust < 2) {
					adjust = 2;
				}
			}
			this.currentHeight += adjust;
			if (this.currentHeight >= this.maxHeight) {
				this.isAnimationRunning = false;
			}
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#showNotify()
	 */
	public void showNotify()
	{
		super.showNotify();
		this.currentHeight = this.startHeight;
		this.isAnimationRunning = true;
	}
}
