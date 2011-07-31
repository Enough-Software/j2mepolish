//#condition polish.usePolishGui && polish.midp2

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

import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ImageUtil;

/**
 * <p>Magnifies the new screen.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ScaleScreenChangeAnimation extends ScreenChangeAnimation {
	private int scaleFactor = 10;
	private int[] scaledScreenRgb;
	private int currentScaleFactor;

	/**
	 * Creates a new animation 
	 */
	public ScaleScreenChangeAnimation() {
		super();
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, javax.microedition.lcdui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Screen)
	 */
	protected void onShow(Style style, Display dsplay, int width, int height,
			Displayable lstDisplayable, Displayable nxtDisplayable, boolean isForward  ) 
	{
		if (isForward) {
			this.useLastCanvasRgb = false;
			this.useNextCanvasRgb = true;			
		} else {
			this.useLastCanvasRgb = true;
			this.useNextCanvasRgb = false;
		}
		this.scaledScreenRgb = new int[ width * height ];
		super.onShow(style, dsplay, width, height, lstDisplayable,
				nxtDisplayable, isForward );
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate(long, long)
	 */
	protected boolean animate(long passedTime, long duration) {
		if (passedTime > duration) {
			this.scaledScreenRgb = null;
			return false;
		}
		int startValue, endValue;
		int[] rgb;
		if (this.isForwardAnimation) {
			startValue = this.scaleFactor;
			endValue = 100;
			rgb = this.nextCanvasRgb;
		} else {
			startValue = 100;
			endValue = this.scaleFactor;
			rgb = this.lastCanvasRgb;
		}
		
		int factor = calculateAnimationPoint(startValue, endValue, passedTime, duration); 
		ImageUtil.scale(factor, this.screenWidth, this.screenHeight, rgb, this.scaledScreenRgb );
		this.currentScaleFactor = factor;
		
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g) {
		Image img;
		if (this.isForwardAnimation) {
			img = this.lastCanvasImage;
		} else {
			img = this.nextCanvasImage;
		}
		g.drawImage(img, 0, 0, Graphics.TOP | Graphics.LEFT);
        final int targetWidth = (this.screenWidth * this.currentScaleFactor) / 100;
        final int targetHeight = (this.screenHeight * this.currentScaleFactor) / 100;
        g.clipRect( (this.screenWidth - targetWidth)/2, (this.screenHeight - targetHeight)/2, targetWidth, targetHeight );
		g.drawRGB(this.scaledScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight, false );
	}

}
