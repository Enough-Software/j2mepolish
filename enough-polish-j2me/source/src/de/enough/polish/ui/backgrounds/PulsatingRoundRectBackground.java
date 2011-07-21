//#condition polish.usePolishGui
/*
 * Created on 15-Mar-2004 at 10:57:00.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.ui.backgrounds;

import de.enough.polish.ui.Background;
import de.enough.polish.util.DrawUtil;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints an animated background, which colors change.</p>
  * <p>Following CSS parameters are supported:
 * <ul>
 * 		<li><b>start-color</b>: The color with which the animation is started.</li>
 * 		<li><b>end-color</b>: The color with which the animation is stopped.</li>
 * 		<li><b>repeat</b>: true when the animation should run without stopping. 
 * 			Is enabled by default.</li>
 * 		<li><b>back-and-forth</b>: true when the animation should run backward when 
 * 			the end-color is reached. Is enabled by default</li>
 * </ul>
*
 * <p>Copyright Enough Software 2004 - 2009</p>

 * <pre>
 * history
 *        15-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PulsatingRoundRectBackground extends Background {
	private int currentColor;
	private int currentStep;
	private final boolean repeat;
	private final boolean backAndForth;
	private boolean directionUp = true;
	private boolean animationStopped;
	private final int startColor;
	private final int endColor;
	private final int steps;
	private final int arcWidth;
	private final int arcHeight;
	

	/**
	 * @param startColor
	 * @param endColor
	 * @param steps
	 * @param repeat
	 * @param backAndForth
	 * @param arcWidth the horizontal diameter of the arc at the four corners
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 */
	public PulsatingRoundRectBackground(int startColor, int endColor, int steps, boolean repeat,  boolean backAndForth, int arcWidth, int arcHeight) 
	{
		this.startColor = startColor;
		this.endColor = endColor;
		this.steps = steps;
		this.repeat = repeat;
		this.backAndForth = backAndForth;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
		this.currentColor = startColor;
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.currentColor );
		g.fillRoundRect( x, y, width - 1, height, this.arcWidth, this.arcHeight );
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#animate()
	 */
	public boolean animate() {
		if (this.animationStopped) {
			return false;
		}
		if (this.backAndForth) {
			if (this.directionUp) {
				this.currentStep++;
				if (this.currentStep > this.steps ) {
					this.currentStep--;
					this.directionUp = false;
				}
			} else {
				this.currentStep--;
				if (this.currentStep == -1) {
					this.currentStep = 0;
					if ( this.repeat ) {
						this.directionUp = true;
					} else {
						this.animationStopped = true;
					}
				}
			}
		} else {
			this.currentStep++;
			if (this.currentStep > this.steps ) {
				if (this.repeat) {
					this.currentStep = 0;
				} else {
					this.currentStep--;
					this.animationStopped = true;
				}
			}
			
		}
		this.currentColor = DrawUtil.getGradientColor(this.startColor, this.endColor, this.currentStep, this.steps );
		return true;
	}

}
