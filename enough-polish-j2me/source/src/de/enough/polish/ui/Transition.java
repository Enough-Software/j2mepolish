//#condition polish.usePolishGui
/*
 * Created on Sep 11, 2008 at 9:42:49 PM.
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
 * <p>Realizes a graphical transition from something 'old' to something 'new'.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class Transition implements UiElement, Animatable
{
	
	protected RgbImage oldState;
	protected RgbImage newState;

	/** set to true in subclasses for populating oldStateRgbImage */
	protected boolean useOldStateRgb;
	/** RGB image containing RGB data of the old state, this is initialized by the Transition base class when useOldStateRgb is set to true */
	protected RgbImage oldStateRgbImage;
	/** set to true in subclasses for populating newStateRgbImage */
	protected boolean useNewStateRgb;
	/** RGB image containing RGB data of the new state, this is initialized by the Transition base class when useNewStateRgb is set to true */
	protected RgbImage newStateRgbImage;
	
	protected int oldX;
	protected int oldY;
	protected int newX;
	protected int newY;


	
	protected Style style;
	protected UiElement parent;
	protected boolean isFinished;
	
	/**
	 * Creates a new transition
	 */
	public Transition() {
		// initialize within it's init method.
	}
	
	/**
	 * Initializes this transition shortly before it is shown.
	 * 
	 * @param oldStateRgbImage the old state of the parent
	 * @param newStateRgbImage the new state of the parent
	 * @param transitionParent the parent
	 */
	public void initTransition(RgbImage oldStateRgbImage, RgbImage newStateRgbImage, UiElement transitionParent) {
		this.oldState = oldStateRgbImage;
		this.newState = newStateRgbImage;
		this.parent = transitionParent;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.UiElement#addRepaintArea(de.enough.polish.ui.ClippingRegion)
	 */
	public void addRepaintArea(ClippingRegion repaintArea)
	{
		this.parent.addRepaintArea(repaintArea);
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.UiElement#addRelativeToContentRegion(de.enough.polish.ui.ClippingRegion, int, int, int, int)
	 */
	public void addRelativeToContentRegion(ClippingRegion repaintRegion, int x,
			int y, int width, int height)
	{
		this.parent.addRelativeToContentRegion(repaintRegion, x, y, width, height);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.UiElement#getStyle()
	 */
	public Style getStyle()
	{
		return this.style;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.UiElement#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		this.style = style;
		setStyle( style, true );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.UiElement#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle)
	{
		// nothing to implement
	}
	
	/**
	 * Paints the transition
	 * @param x horizontal start position
	 * @param y vertical start position
	 * @param g Graphics context
	 */
	public abstract void paint( int x, int y, Graphics g );
	
	/**
	 * Animates this transition
	 * 
	 * @return true when the animation continues, false otherwise
	 */
	public abstract boolean animate();
	
	public abstract void start(boolean isForward);
	
	public abstract void stop();

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Animatable#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion)
	{
		if (!animate()) {
			this.isFinished = true;
		}
	}
	

	/**
	 * Determines whether this transition is finished.
	 * @return true when this animation is stopped
	 */
	public boolean isFinished() {
		return this.isFinished;
	}

}
