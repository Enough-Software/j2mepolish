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

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;
import de.enough.polish.util.DrawUtil;

/**
 * <p>Creates a black and white picture out of the next screen and fades this into the actual target color.</p>
 * <p>Activate this animation by specifying it in the corresponding screen's style:
 * <pre>
 * .myAlert {
 * 		//#if polish.midp2
 * 			screen-change-animation: bwToColor;
 * 		//#endif
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software 2007 - 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BwToColorScreenChangeAnimation 
extends ScreenChangeAnimation 
{
	
	private int[] currentScreenRgb;

	/**
	 * Creates a new animation.
	 */
	public BwToColorScreenChangeAnimation() {
		this.useNextCanvasRgb = true;
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style)
	{
		super.setStyle(style);
		this.currentScreenRgb = new int[ this.screenWidth * this.screenHeight ];
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate(long, long)
	 */
	protected boolean animate(long passedTime, long duration) {
		if (passedTime > duration) {
			this.currentScreenRgb = null;
			this.nextCanvasRgb = null;
			return false;
		}
		int startColor;
		int targetColor;
		int permille = calculateAnimationPoint(0, 1000, passedTime, duration);
		int red,green,blue;
		for (int i = 0; i < this.currentScreenRgb.length; i++) {
			targetColor = this.nextCanvasRgb[i];
			red = (0x00FF & (targetColor >>> 16));	
			green = (0x0000FF & (targetColor >>> 8));
			blue = targetColor & (0x000000FF );
			int brightness = (red + green + blue) / 3;
			if (brightness > 127) {
				startColor = 0xffffff;
			} else {
				startColor = 0x000000;
			}
			if (startColor != targetColor) {
				this.currentScreenRgb[i] = DrawUtil.getGradientColor(startColor, targetColor, permille );
			}
		}
		return true;
	}

	

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g) {
		g.drawRGB(this.currentScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight, false );		
	}

}