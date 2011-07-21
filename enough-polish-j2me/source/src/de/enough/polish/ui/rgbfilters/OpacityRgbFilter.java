//#condition polish.usePolishGui
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

import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.RgbFilter;
import de.enough.polish.ui.Style;
import de.enough.polish.util.RgbImage;

/**
 * <p>Transforms the opacity of a specified RGB image.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class OpacityRgbFilter extends RgbFilter
{
	protected Dimension opacity;
	protected transient RgbImage output;
	
	/**
	 * Creates a new opacity filter
	 */
	public OpacityRgbFilter()
	{
		// just create a new instance
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#isActive()
	 */
	public boolean isActive()
	{
		if (this.opacity == null) {
			return false;
		}
		return (this.opacity.getValue(255) != 255);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#process(de.enough.polish.util.RgbImage)
	 */
	public RgbImage process(RgbImage input)
	{
		if (!isActive()) {
			return input;
		}
		if (this.output == null || this.output.getWidth() != input.getWidth() || this.output.getHeight() != input.getHeight() ) {
			this.output = new RgbImage( input.getWidth(), input.getHeight() );
		}
		int[] rgbInput = input.getRgbData();
		int[] rgbOutput = this.output.getRgbData();
		int alpha = this.opacity.getValue(255);
		int alphaMask = (alpha << 24) | 0xffffff;
		int pixel = 0;
		for (int i = 0; i < rgbOutput.length; i++)
		{
			pixel = rgbInput[i];
			if ( ((pixel & 0xff000000)>>>24) > alpha) {
				rgbOutput[i] = (pixel & alphaMask);
			} else {
				rgbOutput[i] = pixel;
			}
		}
		return this.output;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle)
	{
		super.setStyle(style, resetStyle);
		//#if polish.css.filter-opacity
			Dimension opacityInt = (Dimension) style.getObjectProperty("filter-opacity");
			if (opacityInt != null) {
				this.opacity = opacityInt;
			}
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#releaseResources()
	 */
	public void releaseResources()
	{
		this.output = null;
	}
	
}
