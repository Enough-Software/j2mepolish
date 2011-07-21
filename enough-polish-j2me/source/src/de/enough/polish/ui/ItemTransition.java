//#condition polish.usePolishGui
/*
 * Created on Mar 21, 2009 at 10:23:31 AM.
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
package de.enough.polish.ui;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.util.RgbImage;

/**
 * <p>Provides a transition from one item to another item</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class ItemTransition
implements Animatable
{
	

	protected Item oldItem;
	protected Item newItem;
	
	protected int oldX;
	protected int oldY;
	protected int oldWidth;
	protected int oldHeight;
	
	protected int newX;
	protected int newY;
	protected int newWidth;
	protected int newHeight;
	
	protected int largeX;
	protected int largeY;
	protected int largeWidth;
	protected int largeHeight;

	
	protected boolean usesOldRgbData;
	protected RgbImage oldRgbImage;
	protected boolean usesNewRgbData;
	protected RgbImage newRgbImage;
	
	/**
	 * Creates a new instance of this transition.
	 */
	public ItemTransition() {
		// use init for populating fields.
	}
	
	/**
	 * Initializes this transition
	 * 
	 * @param oldItm the old item
	 * @param newItm the new item
	 */
	public void init( Item oldItm, Item newItm ) {
		this.oldItem = oldItm;
		this.oldX = oldItm.getAbsoluteX();
		this.oldY = oldItm.getAbsoluteY();
		this.oldWidth = oldItm.itemWidth;
		this.oldHeight = oldItm.itemHeight;
		this.newItem = newItm;
		this.newX = newItm.getAbsoluteX();
		this.newY = newItm.getAbsoluteY();
		this.newWidth = newItm.itemWidth;
		this.newHeight = newItm.itemHeight;
		
		this.largeX = Math.min( this.oldX, this.newX );
		this.largeY = Math.min( this.oldY, this.newY );
		this.largeWidth = Math.max( this.oldX + this.oldWidth, this.newX + this.newWidth ) - this.largeX;
		this.largeHeight = Math.max( this.oldY + this.oldHeight, this.newY + this.newHeight ) - this.largeY;
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.UiElement#addRelativeToContentRegion(de.enough.polish.ui.ClippingRegion, int, int, int, int)
	 */
	public void addRelativeToLargerItemRegion(ClippingRegion repaintRegion, int x, int y, int width, int height)
	{
		repaintRegion.addRegion( this.largeX + x, this.largeY + y, width, height);
	}

	
	/**
	 * Sets the style for this transition
	 * Sub classes may use this mechanism to specify settings.
	 * @param style the new style, please note that settings should not be resetted.
	 */
	public void setStyle(Style style)
	{
		// subclasses may use this for configuration
		
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Animatable#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion)
	{
		// TODO robertvirkus implement animate
		
	}

	
	public abstract void paint(int x, int y, int leftBorder, int rightBorder, Graphics g);
	
	public abstract boolean isFinished();

}
