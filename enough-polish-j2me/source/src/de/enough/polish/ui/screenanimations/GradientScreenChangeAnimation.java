//#condition polish.usePolishGui and polish.midp2
/*
 * Created on 24.08.2005 at 15:37:25.
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
import de.enough.polish.util.DrawUtil;

/**
 * <p>Fades the current screen's colors the the ones of the target screen. Quite similar to the fade animation.</p>
 * <p>Activate this animation by specifying it in the corresponding screen's style:
 * <pre>
 * .myAlert {
 * 		//#if polish.midp2
 * 			screen-change-animation: gradient;
 * 		//#endif
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Apr 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class GradientScreenChangeAnimation 
extends ScreenChangeAnimation 
{
	
	private int[] currentScreenRgb;
	private int steps = 5;
	private int currentStep;

	/**
	 * Creates a new animation.
	 */
	public GradientScreenChangeAnimation() {
		super();
		this.useLastCanvasRgb = true;
		this.useNextCanvasRgb = true;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, javax.microedition.lcdui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Screen)
	 */
	protected void onShow(Style style, Display dsplay, int width, int height,
			Displayable lstDisplayable, Displayable nxtDisplayable, boolean isForward  ) 
	{
		super.onShow(style, dsplay, width, height, lstDisplayable, nxtDisplayable, isForward );
		this.currentScreenRgb = new int[ width * height ];
		System.arraycopy( this.lastCanvasRgb, 0, this.currentScreenRgb, 0, width * height);
		this.currentStep = 0;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate()
	 */
	protected boolean animate() {
		if (this.currentStep < this.steps) {
			int lastColor;
			int targetColor;
			int permille = 1000 * this.currentStep / this.steps;
			for (int i = 0; i < this.currentScreenRgb.length; i++) {
				lastColor = this.lastCanvasRgb[i];
				targetColor = this.nextCanvasRgb[i];
				if (lastColor != targetColor) {
					this.currentScreenRgb[i] = DrawUtil.getGradientColor( lastColor, targetColor, permille );
				}
			}
			this.currentStep++;
			return true;
		} else {
			this.lastCanvasRgb = null;
			this.currentScreenRgb = null;
			this.nextCanvasRgb = null;
			this.currentStep = 0;
			return false;
		}
	
	}

	

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g) {
		g.drawRGB(this.currentScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight, false );		
	}

}