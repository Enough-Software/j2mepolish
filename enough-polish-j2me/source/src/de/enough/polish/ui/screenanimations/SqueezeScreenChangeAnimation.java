//#condition polish.usePolishGui && polish.midp2

/*
 * Created on 14.09.2005 at 15:30:15.
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

import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ImageUtil;

/**
 * <p>Squeezes the last screen while showing the new screen in the background.</p>
 * <p>Usage:</p>
 * <pre>
 * .screenMain {
 * 	screen-change-animation: squeeze;
 * }
 * </pre>
 *
 * <p>Copyright Enough Software 2008 - 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SqueezeScreenChangeAnimation extends ScreenChangeAnimation {
	
	private int[] scaledRgb;
	private int currentHeight;
	
	/**
	 * Creates a new squeeze animation
	 */
	public SqueezeScreenChangeAnimation() {
		super();
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, javax.microedition.lcdui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Canvas, javax.microedition.lcdui.Displayable, boolean)
	 */
	protected void onShow(Style style, Display dsplay, int width, int height, Displayable lstDisplayable, Displayable nxtDisplayable, boolean isForward)
	{
		if (isForward) {
			this.useLastCanvasRgb = true;
			this.useNextCanvasRgb = false;
		} else {
			this.useLastCanvasRgb = false;
			this.useNextCanvasRgb = true;			
		}
		this.scaledRgb = new int [width * height];
		super.onShow(style, dsplay, width, height, lstDisplayable, nxtDisplayable, isForward );
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate(long, long)
	 */
	protected boolean animate(long passedTime, long duration) {
		if (passedTime > duration) {
			this.scaledRgb = null;
			return false;
		}
		int[] original;
		int startValue, endValue;
		if (this.isForwardAnimation) {
			startValue = this.screenHeight;
			endValue = 10;
			original = this.lastCanvasRgb;
		} else {
			startValue = 10;
			endValue = this.screenHeight;
			original = this.nextCanvasRgb;
		}
		int h = calculateAnimationPoint(startValue, endValue, passedTime, duration);
		this.currentHeight = h;
		ImageUtil.scale(original, this.screenWidth, h, this.screenWidth, this.screenHeight, this.scaledRgb);
		return true;
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#paintAnimation(javax.microedition.lcdui.Graphics)
	 */
	protected void paintAnimation(Graphics g)
	{
		Image canvasImage;
		if (this.isForwardAnimation) {
			canvasImage = this.nextCanvasImage;
		} else {
			canvasImage = this.lastCanvasImage;
		}
		g.drawImage(canvasImage, 0, 0, Graphics.TOP | Graphics.LEFT);
		g.drawRGB(this.scaledRgb, 0, this.screenWidth, 0, this.screenHeight - this.currentHeight, this.screenWidth, this.currentHeight, false);
	}

}
