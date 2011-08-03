//#condition polish.usePolishGui && polish.midp2

/*
 * Created on 15-April-2007 at 18:54:36.
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

/**
 * <p>Fades in the new screen.</p>
 * <p>Activate this animation by specifying it in the corresponding screen's style:
 * <pre>
 * .myAlert {
 * 		//#if polish.midp2
 * 			screen-change-animation: fade;
 * 		//#endif
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright (c) 2009 - 2011 Enough Software</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FadeScreenChangeAnimation extends ScreenChangeAnimation {
	
	int currentOpacity;

	/**
	 * Creates a new animation 
	 */
	public FadeScreenChangeAnimation() {
		super();
		//#if !polish.blackberry
			this.useNextCanvasRgb = true;
		//#endif
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate(long, long)
	 */
	protected boolean animate(long passedTime, long duration) {
		if (passedTime > duration) {
			return false;
		}
		this.currentOpacity = calculateAnimationPoint(0, 255, passedTime, duration);
		//#if !polish.blackberry
			addOpacity(this.currentOpacity, this.nextCanvasRgb);
		//#endif
		return true;
	}

	//#if !polish.blackberry
	/**
	 * Adds the specified opacity to the RGB data.
	 * 
	 * @param opacity the opacity between 0 (fully transparent) and 255 (fully opaque)
	 * @param data the RGB data
	 */
	private void addOpacity(int opacity, int[] data) {
		opacity = (opacity << 24) | 0xFFFFFF;
		for (int i = 0; i < data.length; i++) {
			data[i] = (data[i] | 0xff000000) & opacity;
		}
	}
	//#endif

	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g) {
		g.drawImage( this.lastCanvasImage, 0, 0, Graphics.TOP | Graphics.LEFT );
		//#if polish.blackberry
			net.rim.device.api.ui.Graphics bbGraphics = null;
			//# bbGraphics = g.g;
			bbGraphics.setGlobalAlpha( this.currentOpacity );
			net.rim.device.api.system.Bitmap bitmap = null;
			//# bitmap = this.nextCanvasImage.getBitmap(); 
			bbGraphics.drawBitmap(0, 0, this.screenWidth, this.screenHeight, bitmap, 0, 0 ); 
			bbGraphics.setGlobalAlpha( 0xff ); // reset to fully opaque
		//#else
			g.drawRGB(this.nextCanvasRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight, true );
		//#endif
	}

}
