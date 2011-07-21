//#condition polish.usePolishGui
/*
 * Created on 17-Jul-2004 at 11:00:24.
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
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;

/**
 * <p>Paints an animated circular background.</p>
 * <p>Following CSS-attributes are supported:</p>
 * <ul>
 * 	<li><b>type</b>: the type of the background, needs to be "pulsating-circle".</li>
 * 	<li><b>color</b>: the color of the background, defaults to "white".</li>
 * 	<li><b>min-diameter</b>: the minimum diameter of the circle.</li>
 * 	<li><b>max-diameter</b>: the minimum diameter of the circle.</li>
 * </ul>
 *
 * <p>Copyright Enough Software 2004 - 2009</p>

 * <pre>
 * history
 *        17-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PulsatingCircleBackground extends Background {
	
	private final int color;
	private int maxDiameter;
	private int minDiameter;
	private final boolean isFlexibleMaxDiameter;
	private int currentDiameter;
	private boolean isGrowing = true;
	private final int speed;
	
	/**
	 * Creates a new pulsating-circle background.
	 * 
	 * @param color the color of this background
	 * @param minDiameter the minimum diameter
	 * @param maxDiameter the maximum diameter, -1 when this should be dynamic
	 * @param speed the constant speed or -1
	 */
	public PulsatingCircleBackground( int color, int minDiameter, int maxDiameter, int speed ) {
		super();
		this.color = color;
		this.minDiameter = minDiameter;
		this.maxDiameter = maxDiameter;
		this.speed = speed;
		this.isFlexibleMaxDiameter = (maxDiameter == -1);
		this.currentDiameter = minDiameter;
	}

	/**
	 * Renders the background to the screen.
	 * 
	 * @param x the x position of the background
	 * @param y the y position of the background
	 * @param width the width of the background
	 * @param height the height of the background
	 * @param g the Graphics instance for rendering this background
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (this.isFlexibleMaxDiameter) {
			this.maxDiameter = Math.min(width, height);
		}
		g.setColor( this.color );
		int centerX = x + (width >> 1);
		int centerY = y + (height >> 1);
		int current = this.currentDiameter;
		int offset = current >> 1;
		x = centerX - offset;
		y = centerY - offset;
		g.fillArc( x, y, current, current, 0, 360 );
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#animate(de.enough.polish.ui.Screen, de.enough.polish.ui.Item, long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(Screen screen, Item parent, long currentTime, ClippingRegion repaintRegion)
	{
		int current = this.currentDiameter;
		int previous = current;
		int adjust = this.speed;
		if (adjust == -1) {
			adjust = (this.maxDiameter - current) / 3;
			if (adjust < 1) {
				adjust = 1;
			}
		}
		if (this.isGrowing) {
			current += adjust;
			if (current >= this.maxDiameter) {
				this.isGrowing = false;
			}
		} else {
			current -= adjust;
			if (current <= this.minDiameter) {
				this.isGrowing = true;
			}
		}
		this.currentDiameter = current;
		current = Math.max( current, previous );
		if (parent != null) {
			int w = parent.getBackgroundWidth();
			int h = parent.getBackgroundHeight();
			int x = (w - current) >> 1;
			int y = (h - current) >> 1;
			parent.addRelativeToBackgroundRegion( this, null, repaintRegion, x, y, current, current );
		} else {
			repaintRegion.addRegion(0, 0, screen.getWidth(), screen.getScreenHeight() );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#showNotify()
	 */
	public void showNotify()
	{
		super.showNotify();
		this.isGrowing = true;
		this.currentDiameter = this.minDiameter;
	}
	
	
}
