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

import de.enough.polish.ui.Color;
import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;

/**
 * <p>Expands the new screen from the horizontal center to left and right.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2011</p>
 * @author Michael Koch, michael@enough.de
 */
public class FlashScreenChangeAnimation extends ScreenChangeAnimation
{	
	private int currentX;
	private int currentSize;
	//#if polish.css.flash-screen-change-animation-color
		private int color = 0;
	//#endif

	/**
	 * Creates a new animation 
	 */
	public FlashScreenChangeAnimation()
	{
		// Do nothing here.
	}

	//#if polish.css.flash-screen-change-animation-color
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style)
	{
		super.setStyle(style);
		//#if polish.css.flash-screen-change-animation-color
			Color colorInt = (Color) style.getObjectProperty("flash-screen-change-animation-color");
			if (colorInt != null)
			{
				this.color = colorInt.getColor();
			}
		//#endif
	}
	//#endif

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate(long, long)
	 */
	protected boolean animate(long passedTime, long duration) {
		if (passedTime > duration) {
			return false;
		}
		int startValue, endValue;
	
		if (this.isForwardAnimation) {
			startValue = 0;
			endValue = this.screenWidth / 2;
		} else {
			startValue = this.screenWidth / 2;
			endValue = 0;
		}
		int value = calculateAnimationPoint(startValue, endValue, passedTime, duration);
		this.currentX = this.screenWidth / 2 - value;
		this.currentSize = value * 2;
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g)
	{
		Image first;
		Image second;
		if (this.isForwardAnimation) {
			first = this.lastCanvasImage;
			second = this.nextCanvasImage;
		} else {
			first = this.nextCanvasImage;
			second = this.lastCanvasImage;
		}

		g.drawImage(first, 0, 0, Graphics.TOP | Graphics.LEFT);
		//#if polish.css.flash-screen-change-animation-color
			g.setColor(this.color);
		//#else
			g.setColor(0);
		//#endif
		g.drawLine( this.currentX - 1, 0, this.currentX - 1, this.screenHeight );
		g.drawLine( this.currentX + this.currentSize, 0,  this.currentX + this.currentSize, this.screenHeight );
		g.setClip( this.currentX, 0, this.currentSize, this.screenHeight );
		g.drawImage(second, 0, 0, Graphics.TOP | Graphics.LEFT);
	}
}
