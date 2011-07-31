//#condition polish.usePolishGui && polish.midp2

/*
 * Created on 28-July-2007 at 03:58:36.
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

import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ImageUtil;

/**
 * <p>Magnifies the last screen into single pixels that remain in their original size. Similar to Tempest 2000 messages.</p>
 * <p>Activate this animation by specifying it in the corresponding screen's style:
 * <pre>
 * .myAlert {
 * 		//#if polish.midp2
 * 			screen-change-animation: particle;
 * 		//#endif
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright (c) 2009 - 2011 Enough Software</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ParticleScreenChangeAnimation extends ScreenChangeAnimation {
	private int scaleFactor = 460;
	private int[] scaledScreenRgb;

	/**
	 * Creates a new animation 
	 */
	public ParticleScreenChangeAnimation() {
		super();
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, javax.microedition.lcdui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Screen)
	 */
	protected void onShow(Style style, Display dsplay, int width, int height,
			Displayable lstDisplayable, Displayable nxtDisplayable, boolean isForward  ) 
	{
		if (isForward) {
			this.useLastCanvasRgb = true;
			this.useNextCanvasRgb = false;
		} else {
			this.useLastCanvasRgb = false;
			this.useNextCanvasRgb = true;
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
		int[] originalRgb;
		int startFactor, endFactor;
		if (this.isForwardAnimation) {
			startFactor = 100;
			endFactor = this.scaleFactor;
			originalRgb = this.lastCanvasRgb;
		} else  {
			startFactor = this.scaleFactor;
			endFactor = 100;
			originalRgb = this.nextCanvasRgb;
		}
		
		// increase factor similar to exponential:
		int factor = calculateAnimationPoint(startFactor, endFactor, passedTime, duration);
		// linear scale:
		//int factor = 100 + (this.scaleFactor - 100) * this.currentStep / (this.steps - 1);
		ImageUtil.particleScale(factor, this.screenWidth, this.screenHeight, originalRgb, this.scaledScreenRgb);
		return true;
	}
	

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g) {
		if (this.isForwardAnimation) {
			g.drawImage( this.nextCanvasImage, 0, 0, Graphics.TOP | Graphics.LEFT );
		} else {
			g.drawImage( this.lastCanvasImage, 0, 0, Graphics.TOP | Graphics.LEFT );			
		}
		g.drawRGB(this.scaledScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight, true );
	}

}
