//#condition polish.usePolishGui
/*
 * Created on Jan 4, 2009 at 7:41:05 PM.
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
package de.enough.polish.ui.transitions;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.RgbFilter;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.Transition;
import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.RgbImage;

/**
 * <p>Uses customizable rgb filters for displaying a transition.</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RgbFilterTransition extends Transition
{
	
	//#if polish.css.transition-old-filter
		protected RgbFilter[] oldFilters;
		protected boolean isOldFiltersActive;
		protected RgbFilter[] oldOriginalFilters;
	//#endif
	//#if polish.css.transition-old-filter
		protected RgbFilter[] newFilters;
		protected boolean isNewFiltersActive;
		protected RgbFilter[] newOriginalFilters;
	//#endif


	/**
	 * 
	 */
	public RgbFilterTransition()
	{
		// TODO robertvirkus implement RgbFilterTransition
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Transition#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle)
	{
		//#if polish.css.transition-old-filter
			if (this.oldFilters != null) {
				boolean isActive = false;
				for (int i=0; i<this.oldFilters.length; i++) {
					RgbFilter filter = this.oldFilters[i];
					filter.setStyle(style, resetStyle);
					isActive |= filter.isActive();
				}
				this.isOldFiltersActive = isActive;
				this.oldStateRgbImage = null;
			}
		//#endif
		//#if polish.css.transition-new-filter
			if (this.newFilters != null) {
				boolean isActive = false;
				for (int i=0; i<this.newFilters.length; i++) {
					RgbFilter filter = this.newFilters[i];
					filter.setStyle(style, resetStyle);
					isActive |= filter.isActive();
				}
				this.isNewFiltersActive = isActive;
				this.newStateRgbImage = null;
			}
		//#endif
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Transition#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		//#if polish.css.transition-old-filter
			RgbFilter[] oldFilterObjects = (RgbFilter[]) style.getObjectProperty("transition-old-filter");
			if (oldFilterObjects != null) {
				if (oldFilterObjects != this.oldOriginalFilters) {
					this.oldFilters = new RgbFilter[ oldFilterObjects.length ];
					for (int i = 0; i < oldFilterObjects.length; i++)
					{
						RgbFilter rgbFilter = oldFilterObjects[i];
						try
						{
							this.oldFilters[i] = (RgbFilter) rgbFilter.getClass().newInstance();
						} catch (Exception e)
						{
							//#debug warn
							System.out.println("Unable to initialize filter class " + rgbFilter.getClass().getName() + e );
						}
					}
					this.oldOriginalFilters = oldFilterObjects;
				}
			}
		//#endif
		//#if polish.css.transition-new-filter
			RgbFilter[] newFilterObjects = (RgbFilter[]) style.getObjectProperty("transition-new-filter");
			if (newFilterObjects != null) {
				if (newFilterObjects != this.newOriginalFilters) {
					this.newFilters = new RgbFilter[ newFilterObjects.length ];
					for (int i = 0; i < newFilterObjects.length; i++)
					{
						RgbFilter rgbFilter = newFilterObjects[i];
						try
						{
							this.newFilters[i] = (RgbFilter) rgbFilter.getClass().newInstance();
						} catch (Exception e)
						{
							//#debug warn
							System.out.println("Unable to initialize filter class " + rgbFilter.getClass().getName() + e );
						}
					}
					this.newOriginalFilters = newFilterObjects;
				}
			}
		//#endif
		super.setStyle(style);
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Transition#animate()
	 */
	public boolean animate()
	{
		// TODO robertvirkus implement animate
		return false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Transition#start(boolean)
	 */
	public void start(boolean isForward)
	{
		// TODO robertvirkus implement start

	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Transition#stop()
	 */
	public void stop()
	{
		// TODO robertvirkus implement stop

	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Transition#paint(int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, Graphics g)
	{
		//#if polish.css.transition-old-filter && polish.midp2
			if (this.isOldFiltersActive && this.oldFilters != null) {
				RgbImage rgbImage = this.oldStateRgbImage;
				if ( rgbImage == null) {
					rgbImage = new RgbImage( this.oldState );
					this.oldStateRgbImage = rgbImage;
				} 
				//System.out.println("painting RGB data for " + this  + ", pixel=" + Integer.toHexString( rgbData[ rgbData.length / 2 ]));
				for (int i=0; i<this.oldFilters.length; i++) {
					RgbFilter filter = this.oldFilters[i];
					rgbImage = filter.process(rgbImage);
				}
				int width = rgbImage.getWidth();
				int height = rgbImage.getHeight();
				int[] rgbData = rgbImage.getRgbData();
				DrawUtil.drawRgb(rgbData, x + this.oldX, y + this.oldY, width, height, true, g );
			} else {
		//#endif
				DrawUtil.drawRgb(this.oldState, x + this.oldX, y + this.oldY, g );
		//#if polish.css.transition-old-filter && polish.midp2
			}
		//#endif
		
		//#if polish.css.transition-new-filter && polish.midp2
			if (this.isNewFiltersActive && this.newFilters != null) {
				RgbImage rgbImage = this.newStateRgbImage;
				if ( rgbImage == null) {
					rgbImage = new RgbImage( this.newState );
					this.newStateRgbImage = rgbImage;
				} 
				//System.out.println("painting RGB data for " + this  + ", pixel=" + Integer.toHexString( rgbData[ rgbData.length / 2 ]));
				for (int i=0; i<this.newFilters.length; i++) {
					RgbFilter filter = this.newFilters[i];
					rgbImage = filter.process(rgbImage);
				}
				int width = rgbImage.getWidth();
				int height = rgbImage.getHeight();
				int[] rgbData = rgbImage.getRgbData();
				DrawUtil.drawRgb(rgbData, x + this.newX, y + this.newY, width, height, true, g );
			} else {
		//#endif
				DrawUtil.drawRgb(this.oldState, x + this.newX, y + this.newY, g );
		//#if polish.css.transition-new-filter && polish.midp2
			}
		//#endif		
	}

}
