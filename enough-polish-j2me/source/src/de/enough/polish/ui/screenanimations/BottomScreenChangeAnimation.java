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

import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;

/**
 * <p>Moves the new screen from the bottom to the top.</p>
 * <p>Activate this animation by specifying it in the corresponding screen's style:
 * <pre>
 * .myAlert {
 * 		screen-change-animation: bottom;
 * 		bottom-screen-change-animation-speed: 4; ( 2 is default )
 * 		bottom-screen-change-animation-move-previous: true; ( false is default )
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
public class BottomScreenChangeAnimation extends ScreenChangeAnimation {
	
	protected int currentY;
	//#if polish.css.bottom-screen-change-animation-speed
		private int speed = -1;
	//#endif
	//#if polish.css.bottom-screen-change-animation-move-previous
		private boolean movePrevious;
	//#endif
	

	/**
	 * Creates a new animation 
	 */
	public BottomScreenChangeAnimation() {
		super();
		this.supportsDifferentScreenSizes = true;
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style)
	{
		super.setStyle(style);
		if (this.isForwardAnimation) {
			this.currentY = 0;
		} else {
			this.currentY = this.screenHeight;
		}
		//#if polish.css.bottom-screen-change-animation-speed
			Integer speedInt = style.getIntProperty( "bottom-screen-change-animation-speed" );
			if (speedInt != null ) {
				this.speed = speedInt.intValue();
			} else {
				this.speed = -1;
			}
		//#endif
		//#if polish.css.bottom-screen-change-animation-move-previous
			Boolean movePreviousBool = style.getBooleanProperty("bottom-screen-change-animation-move-previous");
			if (movePreviousBool != null) {
				this.movePrevious = movePreviousBool.booleanValue();
			} else {
				this.movePrevious = false;
			}
		//#endif
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate()
	 */
	protected boolean animate() {
		int adjust;
		//#if polish.css.bottom-screen-change-animation-speed
			if (this.speed != -1) {
				adjust = this.speed;
			} else {
		//#endif
				adjust = (this.screenHeight - this.currentY) / 3;
				if (adjust < 2) {
					adjust = 2;
				}
		//#if polish.css.bottom-screen-change-animation-speed
			}
		//#endif
		
		if (this.isForwardAnimation) {
			if (this.currentY < this.screenHeight) {
				this.currentY += adjust;
				return true;
			}
		} else if (this.currentY > 0) {
			this.currentY -= adjust;
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g) {
		int y = 0;
		//#if polish.css.bottom-screen-change-animation-move-previous
			if (this.movePrevious) {
				y = -this.currentY;
			}
		//#endif
		Image first;
		Image second;
		int firstX = 0;
		int firstY = 0;
		int secondX = 0;
		int secondY = 0;
		if (this.isForwardAnimation) {
			first = this.lastCanvasImage;
			second = this.nextCanvasImage;
			firstX = this.lastContentX;
			firstY = this.lastContentY;
			secondX = this.nextContentX;
			secondY = this.nextContentY;
		} else {
			first = this.nextCanvasImage;
			second = this.lastCanvasImage;
			firstX = this.nextContentX;
			firstY = this.nextContentY;
			secondX = this.lastContentX;
			secondY = this.lastContentY;
		}
		g.drawImage( first, firstX, y + firstY, Graphics.TOP | Graphics.LEFT );
		g.drawImage( second, secondX, secondY + this.screenHeight - this.currentY, Graphics.TOP | Graphics.LEFT );

//		g.drawImage( first, 0, y, Graphics.TOP | Graphics.LEFT );
//		g.drawImage( second, 0, this.screenHeight -  this.currentY, Graphics.TOP | Graphics.LEFT );
	}

}
