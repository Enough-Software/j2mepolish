//#condition polish.usePolishGui

/*
 * Created on 27-May-2005 at 18:54:36.
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
package de.enough.polish.ui.screenanimations;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.CssAnimation;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;

/**
 * <p>Moves the new screen like two snapping doors from top and bottom that meet in the middle.</p>
 *
 * <p>Copyright (c) Enough Software 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class VerticalDoorsScreenChangeAnimation extends ScreenChangeAnimation
{	
	private static final int DIRECTION_DEFAULT = 0;
	private static final int DIRECTION_CLOSE = 1;
	private static final int DIRECTION_OPEN = 2;
	private static final int DOOR_BOTH = 0;
	private static final int DOOR_TOP = 1;
	private static final int DOOR_BOTTOM = 2;
	
	private int currentY;
	private long duration = 500;
	private int direction = DIRECTION_DEFAULT;
	private long startTime;
	private int door = DOOR_BOTH;

	/**
	 * Creates a new animation 
	 */
	public VerticalDoorsScreenChangeAnimation()
	{
		// Do nothing here.
	}

	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style)
	{
		super.setStyle(style);
		this.currentY = this.screenHeight / 2;

		//#if polish.css.vertical-doors-screen-change-animation-duration
			Integer durationInt = style.getIntProperty("vertical-doors-screen-change-animation-duration");
			if (durationInt != null)
			{
				this.duration = durationInt.longValue();
			}
		//#endif
		//#if polish.css.vertical-doors-screen-change-animation-direction
			Integer directionInt = style.getIntProperty("vertical-doors-screen-change-animation-direction");
			if (durationInt != null)
			{
				this.direction = directionInt.intValue();
			} else {
				this.direction = DIRECTION_DEFAULT;
			}
		//#endif
		//#if polish.css.vertical-doors-screen-change-animation-door
			Integer doorInt = style.getIntProperty("vertical-doors-screen-change-animation-door");
			if (doorInt != null)
			{
				this.door = doorInt.intValue();
			} else {
				this.door = DOOR_BOTH;
			}
		//#endif
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#onShow(de.enough.polish.ui.Style, de.enough.polish.ui.Display, int, int, de.enough.polish.ui.Displayable, de.enough.polish.ui.Displayable, boolean)
	 */
	protected void onShow(Style style, Display dsplay, int width, int height,
			Displayable lstDisplayable, Displayable nxtDisplayable,
			boolean isForward) 
	{
		super.onShow(style, dsplay, width, height, lstDisplayable, nxtDisplayable,
				isForward);
		this.startTime = System.currentTimeMillis();
		//#if polish.css.vertical-doors-screen-change-animation-direction
			if (this.direction == DIRECTION_CLOSE) {
				this.isForwardAnimation = false;
			} else if (this.direction == DIRECTION_OPEN) {
				this.isForwardAnimation = true;
			}
		//#endif
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate()
	 */
	protected boolean animate()
	{
		long passedTime = System.currentTimeMillis() - this.startTime;
		int nextY = CssAnimation.calculatePointInRange(this.screenHeight/2, 0, passedTime, this.duration, CssAnimation.FUNCTION_EASE_OUT);

		if (nextY > 0)
		{
			this.currentY = nextY;
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#paintAnimation(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g)
	{
		Image first;
		Image second;
		int height;
		if (this.isForwardAnimation) {
			first = this.lastCanvasImage;
			second = this.nextCanvasImage;
			height = (this.screenHeight/2) - this.currentY;
		} else {
			first = this.nextCanvasImage;
			second = this.lastCanvasImage;
			height = this.currentY;
		}
		// draw last (direction==DIRECTION_CLOSING/isForwardAnimation) or next screen:
		g.drawImage(first, 0, 0, Graphics.TOP | Graphics.LEFT);
		// draw top door:
		//#if polish.css.vertical-doors-screen-change-animation-door
			if (this.door == DOOR_BOTH || this.door == DOOR_TOP) {
		//#endif
				g.setClip(0, 0, this.screenWidth, height );
				g.drawImage(second, 0, height - (this.screenHeight/2), Graphics.TOP | Graphics.LEFT);
		//#if polish.css.vertical-doors-screen-change-animation-door
			} else {
				g.setClip(0, 0, this.screenWidth, this.screenHeight/2 );
				g.drawImage(second, 0, 0, Graphics.TOP | Graphics.LEFT);				
			}
		//#endif
			
		// draw bottom door:
			//#if polish.css.vertical-doors-screen-change-animation-door
			if (this.door == DOOR_BOTH || this.door == DOOR_BOTTOM) {
		//#endif
				g.setClip(0, this.screenHeight-height, this.screenWidth, height );
				g.drawImage(second, 0, (this.screenHeight/2) - height , Graphics.TOP | Graphics.LEFT);
		//#if polish.css.vertical-doors-screen-change-animation-door
			} else {
				g.setClip(0, this.screenHeight/2, this.screenWidth, this.screenHeight/2 + 1 );
				g.drawImage(second, 0, 0, Graphics.TOP | Graphics.LEFT);				
			}
		//#endif
	}
}
