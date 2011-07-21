//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2007 at 22:01:54.
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
import de.enough.polish.ui.Style;
import de.enough.polish.util.DrawUtil;

/**
 * <p>Paints a filled rectangle as a background in a specific color.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * @author Robert Virkus, robert@enough.de
 */
public class TriangleBackground 
extends Background 
{
	/**
	 * Orientation for pointing the triangle upwards
	 */
	public final static int TOP = 0; 
	/**
	 * Orientation for pointing the triangle downwards
	 */
	public final static int BOTTOM = 1;
	/**
	 * Orientation for pointing the triangle to the left
	 */
	public final static int LEFT = 2;
	/**
	 * Orientation for pointing the triangle to the right
	 */
	public final static int RIGHT = 3;
	
	private int color;
	private final int orientation;
	

	/**
	 * Creates a new triangle background.
	 * 
	 * @param color the color of the background in RGB, e.g. 0xFFDD11
	 * @param orientation the orientation, either TOP, BOTTOM, LEFT, RIGHT
	 */
	public TriangleBackground( int color, int orientation ) {
		this.color = color;
		this.orientation = orientation;
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		int x1, y1, x2, y2, x3, y3;
		switch (this.orientation) {
		case TOP:
			x1 = x + (width >>> 1);
			y1 = y;
			x2 = x;
			y2 = y + height;
			x3 = x + width;
			y3 = y2;
			break;
		case BOTTOM:
			x1 = x + (width >>> 1);
			y1 = y + height;
			x2 = x;
			y2 = y;
			x3 = x + width;
			y3 = y2;
			break;
		case LEFT:
			x1 = x;
			y1 = y + (height >>> 1);
			x2 = x + width;
			y2 = y;
			x3 = x2;
			y3 = y + height;
			break;
		default: // == RIGHT
			x1 = x + width;
			y1 = y + (height >>> 1);
			x2 = x;
			y2 = y;
			x3 = x;
			y3 = y + height;
			break;
		}
		//#if polish.midp2
			g.fillTriangle( x1, y1, x2, y2, x3, y3 );
		//#else
			DrawUtil.fillTriangle(x1, y1, x2, y2, x3, y3, g);
		//#endif
	}

	
	//#if polish.css.animations
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
		 */
		public void setStyle(Style style)
		{
			//#if polish.css.background-triangle-color
				Color col = style.getColorProperty("background-triangle-color");
				if (col != null) {
					this.color = col.getColor();
				}
			//#endif
		}
	//#endif


}
