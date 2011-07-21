//#condition polish.usePolishGui
/*
 * Created on Nov 21, 2007 at 12:23:44 PM.
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
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;

/**
 * <p>Provides a background consisting of two other backgrounds.</p>
 * <p>You can combine more by two backgrounds by using nested further combined backgrounds.</p>
 * <p>Usage:
 * <pre>
backgrounds {
	titleTop {
		type: polygon;
		points: 10,50 50,10 90,50 50,90;
		color: #d0f;
		scale-mode: proportional;
		anchor: right | vcenter;
	}
	titleBottom {
		type: combined;
		foreground: titlePolygonLeft;
		background: titleGradient;
	}
	titlePolygonLeft {
		type: polygon;
		points: 10,50 50,10 90,50 50,90;
		color: #d0f;
		scale-mode: proportional;
		anchor: left | vcenter;
	}
	titleGradient {
		type: vertical-gradient;
		top-color: white;
		bottom-color: blue;
	}
}

title {
	padding: 2;
	margin-top: 0;
	margin-bottom: 5;
	margin-left: 0;
	margin-right: 0;
	font-face: proportional;
	font-size: large;
	font-style: bold;
	font-color: brightFontColor;
	border: none;
	layout: horizontal-center | horizontal-expand;
	
	<b>background {
		type: combined;
		foreground: titleTop;
		background: titleBottom;
	}</b>
}
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Nov 21, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CombinedBackground extends Background
{

	private final Background foreground;
	private final Background background;

	/**
	 * Creates a new combined background.
	 * 
	 * @param foreground the background painted last
	 * @param background  the background painted in the background
	 * 
	 */
	public CombinedBackground( Background foreground, Background background )
	{
		this.foreground = foreground;
		this.background = background;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g)
	{
		this.background.paint(x, y, width, height, g);
		this.foreground.paint(x, y, width, height, g);
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#animate(de.enough.polish.ui.Screen, de.enough.polish.ui.Item, long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(Screen screen, Item parent, long currentTime,
			ClippingRegion repaintRegion)
	{
		this.foreground.animate(screen, parent, currentTime, repaintRegion);
		this.background.animate(screen, parent, currentTime, repaintRegion);
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#showNotify()
	 */
	public void showNotify()
	{
		this.foreground.showNotify();
		this.background.showNotify();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#hideNotify()
	 */
	public void hideNotify()
	{
		this.foreground.hideNotify();
		this.background.hideNotify();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#releaseResources()
	 */
	public void releaseResources()
	{
		this.foreground.releaseResources();
		this.background.releaseResources();
	}

	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		this.background.setStyle(style);
		this.foreground.setStyle(style);
	}
	//#endif
	
}
