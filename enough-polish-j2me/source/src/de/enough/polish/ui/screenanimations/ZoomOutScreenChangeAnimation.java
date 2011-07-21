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
 * <p>Activate this animation by specifying it in the corresponding screen's style:
 * <pre>
 * .myAlert {
 * 		//#if polish.midp2
 * 			screen-change-animation: zoomOut;
 * 		//#endif
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        27-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ZoomOutScreenChangeAnimation extends ScreenChangeAnimation {
	private int scaleFactor = 200;
	private int steps = 6;
	private int currentStep;
	private int[] scaledScreenRgb;

	/**
	 * Creates a new animation 
	 */
	public ZoomOutScreenChangeAnimation() {
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
			this.currentStep = this.steps;
		} else {
			this.currentStep = 0;
			this.useLastCanvasRgb = true;
			this.useNextCanvasRgb = false;			
		}
		super.onShow(style, dsplay, width, height, lstDisplayable, nxtDisplayable, isForward );
		this.scaledScreenRgb = new int[ width * height ];
		animate();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate()
	 */
	protected boolean animate() {
		int[] rgb;
		if (this.isForwardAnimation) {
			this.currentStep--;
			if (this.currentStep <= 0) {
				this.scaledScreenRgb = null;
				return false;
			}	
			rgb = this.nextCanvasRgb;
		} else { 
			this.currentStep++;
			if (this.currentStep >= this.steps ) {
				this.scaledScreenRgb = null;
				return false;
			}
			rgb = this.lastCanvasRgb;
		}
		int factor = 100 + ( (this.scaleFactor - 100) * this.currentStep ) / this.steps;
		int opacity = 255 / this.steps * ( this.steps - this.currentStep );
		ImageUtil.scale( opacity, factor, this.screenWidth, this.screenHeight, rgb, this.scaledScreenRgb);
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g) {
		Image canvasImage;
		if (this.isForwardAnimation) {
			canvasImage = this.lastCanvasImage;
		} else {
			canvasImage = this.nextCanvasImage;
		}
		g.drawImage( canvasImage, 0, 0, Graphics.TOP | Graphics.LEFT );
		g.drawRGB(this.scaledScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight, true );
	}

}
