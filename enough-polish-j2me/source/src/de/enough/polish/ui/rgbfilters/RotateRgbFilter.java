//#condition polish.usePolishGui && polish.hasFloatingPoint
/*
 * Created on Jul 8, 2008 at 5:10:39 PM.
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
package de.enough.polish.ui.rgbfilters;

import de.enough.polish.ui.RgbFilter;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ImageUtil;
import de.enough.polish.util.RgbImage;

/**
 * <p>Rotates an RGB image.</p>
 *
 * @author Ovidiu Iliescu
 */
public class RotateRgbFilter extends RgbFilter
{
	protected int angle = 0;

	/**
	 * Creates a new RotateRgb filter
	 */
	public RotateRgbFilter()
	{
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#isActive()
	 */
	public boolean isActive()
	{
		boolean isActive = false;
                
		if (this.angle != 0) {
			isActive = true ;
		}
		return isActive;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#process(de.enough.polish.util.RgbImage)
	 */
	public RgbImage process(RgbImage input)
	{
		if (!isActive()) {
			return input;
		}

		return ImageUtil.rotate(new RgbImage(input), this.angle);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle)
	{
		super.setStyle(style, resetStyle);

		//#if polish.css.filter-rotation-angle
			Integer rotationAngle = (Integer) style.getObjectProperty("filter-rotation-angle");
			if (rotationAngle != null) {
				this.angle = rotationAngle.intValue() % 360;
	        	}
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#releaseResources()
	 */
	public void releaseResources()
	{
	}

}