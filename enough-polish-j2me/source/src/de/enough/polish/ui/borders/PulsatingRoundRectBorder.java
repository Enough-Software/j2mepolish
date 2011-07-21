//#condition polish.usePolishGui

/*
 * Created on Aug 3, 2007 at 5:33:07 PM.
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
package de.enough.polish.ui.borders;

import de.enough.polish.util.DrawUtil;

/**
 * <p>A color-changing border with round corners</p>
 * <p>Usage:
 * <pre>
 *  .myStyle {
 *  	border {
 *  		type: pulsating-round-rect;
 *  		width: 2;
 *  		start-color: black;
 *  		end-color: red;
 *  		steps: 5;
 *  		repeat: true;
 *  		back-and-forth: true;
 *  		arc-width: 10;
 *  		arc-height: 10;
 *  	}
 *  }
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Aug 3, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PulsatingRoundRectBorder extends RoundRectBorder {
	
	private final int startColor;
	private final int endColor;
	private final int steps;
	private final boolean repeat;
	private final boolean backAndForth;
	private boolean animationStopped;
	private boolean directionUp;
	private int currentStep;

	/**
	 * @param borderWidth 
	 * @param startColor
	 * @param endColor
	 * @param steps
	 * @param repeat
	 * @param backAndForth
	 * @param arcWidth 
	 * @param arcHeight 
	 */
	public PulsatingRoundRectBorder( int borderWidth, int startColor, int endColor, int steps, boolean repeat,  boolean backAndForth, int arcWidth, int arcHeight) 
	{
		super( startColor, borderWidth, arcWidth, arcHeight );
		this.startColor = startColor;
		this.endColor = endColor;
		this.steps = steps;
		this.repeat = repeat;
		this.backAndForth = backAndForth;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#animate()
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
		this.color = DrawUtil.getGradientColor(this.startColor, this.endColor, this.currentStep, this.steps );
		return true;
	}
	
	

}
