//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:01:54.
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
 * <p>Paints a filled rectangle as a background in a specific color.</p>
 *
 * <p>Copyright Enough Software 2004 - 2009</p>
 * @author Robert Virkus, robert@enough.de
 */
public class SimpleBackground 
extends Background 
{
	
	private int color;
	private Color colorObj;
	private boolean isInitialized;

	/**
	 * Creates a new simple background.
	 * 
	 * @param color the color of the background in RGB, e.g. 0xFFDD11
	 */
	public SimpleBackground( int color ) {
		super();
		this.color = color;
		this.isInitialized = true;
	}

	/**
	 * Creates a new simple background.
	 * 
	 * @param color the color of the background in RGB, e.g. 0xFFDD11 or a dynamic color
	 */
	public SimpleBackground( Color color ) {
		super();
		this.colorObj = color;
		this.isInitialized = false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (!this.isInitialized) {
			this.color = this.colorObj.getColor();
			this.isInitialized = true;
			this.colorObj = null;
		}
		g.setColor( this.color );
		g.fillRect( x, y, width, height );
	}

	/**
	 * @return the associated color of this background
	 */
	public int getColor() {
		if (!this.isInitialized) {
			this.color = this.colorObj.getColor();
			this.isInitialized = true;
			this.colorObj = null;
		}
		return this.color;
	}
	
	//#if polish.css.animations
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
		 */
		public void setStyle(Style style)
		{
			//#if polish.css.background-simple-color
				Color col = style.getColorProperty("background-simple-color");
				if (col != null) {
					this.color = col.getColor();
				}
			//#endif
		}
	//#endif

}
