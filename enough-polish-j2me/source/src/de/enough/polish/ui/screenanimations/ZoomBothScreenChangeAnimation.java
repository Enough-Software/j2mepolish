//#condition polish.usePolishGui && polish.midp2

/*
 * Created on 30-May-2005 at 01:14:36.
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
 * <p>Magnifies both the last screen and the next screen.</p>
 * <p>Activate this animation by specifying it in the corresponding screen's style:
 * <pre>
 * .myAlert {
 * 		//#if polish.midp2
 * 			screen-change-animation: zoomBoth;
 * 		//#endif
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ZoomBothScreenChangeAnimation extends ScreenChangeAnimation {
	private int outerScaleFactor = 260;
	private int innerScaleFactor = 460;
	private int[] nextCanvasScaledRgb;
	private int[] lastCanvasScaledRgb;
	private int currentMagnifyFactor;

	/**
	 * Creates a new animation 
	 */
	public ZoomBothScreenChangeAnimation() {
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
		int size = width * height;
		this.lastCanvasScaledRgb = new int[ size ];
		this.nextCanvasScaledRgb = new int[ size ];					
		super.onShow(style, dsplay, width, height, lstDisplayable,
				nxtDisplayable, isForward );
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate(long, long)
	 */
	protected boolean animate(long passedTime, long duration) {
		if (passedTime > duration) {
			this.nextCanvasScaledRgb = null;
			this.lastCanvasScaledRgb = null;
			return false;
		}
		int startFactor1, startFactor2, endFactor1, endFactor2;
		int startOpacity, endOpacity;
		int[] rgbFirstOriginal;
		int[] rgbSecondOriginal;
		int[] rgbFirstScaled;
		int[] rgbSecondScaled;
		if (this.isForwardAnimation) {
			startFactor1 = 100;
			endFactor1 = this.innerScaleFactor;
			startFactor2 = 30;
			endFactor2 = 100; //this.outerScaleFactor;
			startOpacity = 10;
			endOpacity = 255;
			rgbFirstOriginal = this.lastCanvasRgb;
			rgbFirstScaled = this.lastCanvasScaledRgb;
			rgbSecondOriginal = this.nextCanvasRgb;
			rgbSecondScaled = this.nextCanvasScaledRgb;
		} else {
			startFactor1 = this.innerScaleFactor;
			endFactor1 = 100;
			startFactor2 = this.outerScaleFactor;
			endFactor2 = 100;
			startOpacity = 255;
			endOpacity = 10;
			rgbFirstOriginal = this.nextCanvasRgb;
			rgbFirstScaled = this.nextCanvasScaledRgb;
			rgbSecondOriginal = this.lastCanvasRgb;
			rgbSecondScaled = this.lastCanvasScaledRgb;	
		}
		int magnifyFactor = calculateAnimationPoint(startFactor1, endFactor1, passedTime, duration);
		ImageUtil.scale(magnifyFactor, this.screenWidth, this.screenHeight, rgbFirstOriginal, rgbFirstScaled);
		magnifyFactor = calculateAnimationPoint(startFactor2, endFactor2, passedTime, duration);
		int opacity = calculateAnimationPoint(startOpacity, endOpacity, passedTime, duration);
		this.currentMagnifyFactor = magnifyFactor;
		ImageUtil.scale(opacity, magnifyFactor, this.screenWidth, this.screenHeight, rgbSecondOriginal, rgbSecondScaled);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g) {
		int[] first;
		int[] second;
		if (this.isForwardAnimation) {
			first = this.lastCanvasScaledRgb;
			second = this.nextCanvasScaledRgb;
		} else {
			first = this.nextCanvasScaledRgb;
			second = this.lastCanvasScaledRgb;	
		}
		g.drawRGB(first, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight, true );
		if (!this.isForwardAnimation) {
			int w = (this.screenWidth * this.currentMagnifyFactor) / 100;
			int h = (this.screenHeight * this.currentMagnifyFactor) / 100;
			g.setClip( (this.screenWidth >> 1) - (w >> 1), (this.screenHeight >> 1) - (h >> 1), w, h  );
		}
		g.drawRGB(second, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight, true );
	}

}
