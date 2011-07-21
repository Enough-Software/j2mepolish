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

import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ImageUtil;

/**
 * <p>Magnifies the new screen.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        27-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ScaleScreenChangeAnimation extends ScreenChangeAnimation {
	private int scaleFactor = 60;
	private int steps = 6;
	private int currentStep;
	private int[] nextScreenRgb;
	private boolean scaleDown;
	private int[] scaledScreenRgb;

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
		super.onShow(style, dsplay, width, height, lstDisplayable,
				nxtDisplayable, isForward );
		this.nextScreenRgb = new int[ width * height ];
		this.nextCanvasImage.getRGB( this.nextScreenRgb, 0, width, 0, 0, width, height );
		this.scaledScreenRgb = new int[ width * height ];
		System.arraycopy(this.nextScreenRgb, 0, this.scaledScreenRgb, 0, width * height );
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate()
	 */
	protected boolean animate() {
		int step = this.currentStep;
		if (this.scaleDown) {
			step--;
			if (step <= 0) {
				// set default values:
				this.scaleFactor = 200;
				this.currentStep = 0;
				this.nextScreenRgb = null;
				this.scaledScreenRgb = null;
				this.scaleDown = false;
				return false;
			}
		} else {
			step++;
			if (step > this.steps) {
				this.scaleDown = true;
				return true;
			}
		}
		this.currentStep = step;
		int factor = 100 + ( this.scaleFactor * step ) / this.steps; 
		ImageUtil.scale(factor, this.screenWidth, this.screenHeight, this.nextScreenRgb, this.scaledScreenRgb );
		
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g) {
		g.drawRGB(this.scaledScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight, false );
	}

}
