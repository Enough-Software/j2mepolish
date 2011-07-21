//#condition polish.usePolishGui
/*
 * Created on Jun 14, 2006 at 6:16:32 PM.
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
package de.enough.polish.ui.rgbeffects;

import de.enough.polish.ui.RgbEffect;
import de.enough.polish.util.ImageUtil;
import de.enough.polish.util.RgbImage;

/**
 * <p>Rotates RGB data by a specific degree.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Jun 14, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RotateRgbEffect extends RgbEffect {
	
	private int angle;
	private int referencePointXPercent;
	private int referencePointYPercent;

	/**
	 * Creates a new rotate effect
	 */
	public RotateRgbEffect() {
		this( -45, 50, 50 );
	}
		
	/**
	 * Creates a new rotate effect
	 * 
	 * @param angle the angle in degrees by which the RGB data should be rotated
	 */
	public RotateRgbEffect( int angle ) {
		this( angle, 50, 50 );
	}
	
	/**
	 * Creates a new rotate effect
	 * 
	 * @param angle the angle in degrees by which the RGB data should be rotated
   * @param referencePointXPercent X-Position of the reference point
   * @param referencePointYPercent Y-Position of the reference point
	 */
	public RotateRgbEffect( int angle, int referencePointXPercent, int referencePointYPercent ) {
		this.angle = angle;
		this.referencePointXPercent = referencePointXPercent;
		this.referencePointYPercent = referencePointYPercent;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbEffect#renderEffect(de.enough.polish.util.RgbImage)
	 */
	public void renderEffect(RgbImage image) {
		//#if polish.hasFloatingPoint
			int referenceX = (image.getWidth() * this.referencePointXPercent) / 100;
			int referenceY = (image.getHeight() * this.referencePointYPercent) / 100;
			ImageUtil.rotate( image, this.angle, referenceX, referenceY );
		//#endif
	}

}
