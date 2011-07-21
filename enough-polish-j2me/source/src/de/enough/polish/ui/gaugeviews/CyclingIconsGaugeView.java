//#condition polish.usePolishGui
/*
 * Created on Jan 31, 2007 at 3:01:30 PM.
 * 
 * Copyright (c) 2009 Andre Schmidt / Enough Software
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
package de.enough.polish.ui.gaugeviews;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.Style;

/**
 * <p>Shows an animation of cyclic aligned icons for visualizing an CONTINUOUS_RUNNING indefinite gauge.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Aug 30, 2007 - asc creation
 * </pre>
 * @author Andre Schmidt, andre@enough.de
 */
public class CyclingIconsGaugeView extends ItemView {
	private int iconCount = 8;
	private int iconHighlightCount = 3;
	
	private Image iconImage = null;
	private Image iconHighlightImage = null;
	private Image iconHighlightCenterImage = null;
	
	private int iconHighlightIndex = 0;
	
	private int iconHighlightCenterIndex = -1;
	private int iconHighlightCenterSpan = -1;
	
	private int iconWidth = 10;
	
	private boolean isContinuousRunning;
	private int maxIcons;
	
	private transient Gauge gauge;
	private long lastAnimationTime;
	private long interval = 0;
	
	private boolean nextHighlight = false;
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) {
		this.gauge = (Gauge)parent;
		this.isContinuousRunning = this.gauge.getMaxValue() == Gauge.INDEFINITE && this.gauge.getValue() == Gauge.CONTINUOUS_RUNNING;
		
		if(!this.isContinuousRunning){
			this.maxIcons = this.iconCount - this.iconHighlightCount;
		}
		
		this.contentWidth = Math.max( availWidth/4, 24 );
		this.contentHeight = this.contentWidth;
	}

	protected void setStyle(Style style) {
		super.setStyle(style);
		
		String image;
		Integer countObj;
		
		//#if polish.css.gauge-cycling-icons-image
		image = style.getProperty("gauge-cycling-icons-image");
		if (image != null) {
			try
			{
				this.iconImage = Image.createImage(image);
			}
			catch(IOException e)
			{
				//#debug error
				System.out.println("unable to load image " + e);
			}
			
			this.iconWidth = this.iconImage.getWidth();
		}
		//#endif
		
		//#if polish.css.gauge-cycling-icons-count
		countObj = style.getIntProperty("gauge-cycling-icons-count");
		if (countObj != null) {
			this.iconCount = countObj.intValue();
		}
		//#endif
		
		//#if polish.css.gauge-cycling-icons-highlight-image
		image = style.getProperty("gauge-cycling-icons-highlight-image");
		if (image != null) {
			try
			{
				this.iconHighlightImage = Image.createImage(image);
			}
			catch(IOException e)
			{
				//#debug error
				System.out.println("unable to load image " + e);
			}
		}
		//#endif
		
		//#if polish.css.gauge-cycling-icons-highlight-count
		countObj = style.getIntProperty("gauge-cycling-icons-highlight-count");
		if (countObj != null) {
			this.iconHighlightCount = countObj.intValue();
		}
		//#endif
		
		//#if polish.css.gauge-cycling-icons-highlight-center-image
		image = style.getProperty("gauge-cycling-icons-highlight-center-image");
		if (image != null) {
			try
			{
				this.iconHighlightCenterImage = Image.createImage(image);
			}
			catch(IOException e)
			{
				//#debug error
				System.out.println("unable to load image " + e);
			}
			
			if(this.iconHighlightCount > 2)
			{
				this.iconHighlightCenterIndex = 1;
				this.iconHighlightCenterSpan = this.iconHighlightCount - 2;
			}
		}
		//#endif
	}
	
	

	//#if polish.css.gauge-cycling-icons-interval
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	protected void setStyle(Style style, boolean resetStyle)
	{
		super.setStyle(style, resetStyle);
		Integer countObj = style.getIntProperty("gauge-cycling-icons-interval");
		if (countObj != null) {
			this.interval = countObj.intValue();
		}
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate()
	 */
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		if (this.isContinuousRunning && (currentTime - this.lastAnimationTime) > this.interval) {
			this.nextHighlight = true;
			this.iconHighlightIndex = this.iconHighlightIndex % this.iconCount;
			this.lastAnimationTime = currentTime;
			addFullRepaintRegion( this.parentItem, repaintRegion );
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintContent(de.enough.polish.ui.Item, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder,
			int rightBorder, Graphics g) 
	{
		//#if !polish.hasFloatingPoint
			super.paintContentByParent(parent, x, y, leftBorder, rightBorder, g);
		//#else
			Image img = null;
			
			if(this.nextHighlight)
			{
				this.iconHighlightIndex++;
				this.nextHighlight = false;
			}
			
			if(!this.isContinuousRunning)
			{
				this.iconHighlightIndex = ((this.gauge.getValue() * 100 / this.gauge.getMaxValue()) * this.maxIcons) / 100;
			}
			
			int centerX 	= (this.contentWidth - this.iconWidth) / 2  + x;
			int centerY 	= (this.contentWidth - this.iconWidth) / 2  + y;
			double alpha 	= 0;
			double radius 	= (this.contentWidth - this.iconWidth)/2d;
			int sphereX 	= 0;
			int sphereY 	= 0;
			
			for(int i=0; i < this.iconCount; i++)
			{
				alpha = (360d / this.iconCount) * i;
				alpha = (alpha / 180) * Math.PI;
				
				sphereX = (int)(centerX + radius * Math.cos(alpha));
				sphereY = (int)(centerY - radius * Math.sin(alpha));
				
				img = getImage(g, i);
				
				if(img != null)
				{
					g.drawImage( img , sphereX, sphereY, 0);
				}
			}
		//#endif
	}
	
	private Image getImage(Graphics g, int i)
	{
		int startIndex = this.iconHighlightIndex;
		int endIndex = ((this.iconHighlightIndex + this.iconHighlightCount - 1) % this.iconCount);
		Image img = null;		
		
		if(startIndex <= endIndex)
		{
			if(i >= startIndex && i <= endIndex)
			{
				if(this.iconHighlightCenterIndex != -1)
				{
					img = null;
					if((img = getCenterImage(startIndex, g, i)) != null)
						return img;
				}
				
				return this.iconHighlightImage;
			}
		}
		else
		{
			if(i >= startIndex || i <= endIndex)
			{
				if(this.iconHighlightCenterIndex != -1)
				{
					img = null;
					if((img = getCenterImage(startIndex, g, i)) != null)
						return img;
				}
				
				return this.iconHighlightImage;
			}			
		}
		
		return this.iconImage;
	}
	
	public Image getCenterImage(int startIndex, Graphics g, int i)
	{
		int centerStartIndex = (startIndex + this.iconHighlightCenterIndex) % this.iconCount;
		int centerEndIndex = (startIndex + this.iconHighlightCenterIndex + (this.iconHighlightCenterSpan - 1)) % this.iconCount;
				
		if(centerStartIndex <= centerEndIndex)
		{
			if(i >= centerStartIndex && i <= centerEndIndex)
			{
				return this.iconHighlightCenterImage;
			}
		}
		else
		{
			if(i >= centerStartIndex || i <= centerEndIndex)
			{
				return this.iconHighlightCenterImage;
			}
		}
		
		return null;
	}
	
	/**
	 * Determines whether this view is valid for the given item.
	 * @return true when this view can be applied
	 */
	protected boolean isValid(Item parent, Style style) {
		return parent instanceof Gauge;
	}
	
}
