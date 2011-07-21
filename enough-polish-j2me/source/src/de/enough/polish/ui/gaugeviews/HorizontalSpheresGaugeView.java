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

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.Style;

/**
 * <p>Shows an animation of horizontal aligned spheres for visualizing an CONTINUOUS_RUNNING indefinite gauge.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Aug 30, 2007 - asc creation
 * </pre>
 * @author Andre Schmidt, andre@enough.de
 */
public class HorizontalSpheresGaugeView extends ItemView {
	private int sphereCount = 8;
	private int sphereColor = 0xFFFFFF;
	
	private int sphereHighlightCount = 3;
	private int sphereHighlightColor = 0xAAAAAA;
	private int sphereHighlightCenterColor = 0x000000;
	
	private int sphereHighlightIndex = 0;
	
	private int sphereHighlightCenterIndex = -1;
	private int sphereHighlightCenterSpan = -1;
	
	private int sphereWidth = 0;
	
	private boolean isContinuousRunning;
	private int maxSpheres;
	
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
			this.maxSpheres = this.sphereCount - this.sphereHighlightCount;
		}
		
		this.contentWidth = availWidth;
		
		if(this.sphereWidth == 0)
		{
			this.sphereWidth = (this.contentWidth - this.paddingHorizontal) / this.sphereCount;
		}
		
		this.contentHeight = this.sphereWidth;
	}

	protected void setStyle(Style style) {
		super.setStyle(style);
		
		Color colorObj;
		Integer countObj;
		
		//#if polish.css.gauge-horizontal-spheres-color
		colorObj = style.getColorProperty("gauge-horizontal-spheres-color");
		if (colorObj != null) {
			this.sphereColor = colorObj.getColor();
		}
		//#endif
		
		//#if polish.css.gauge-horizontal-spheres-count
		countObj = style.getIntProperty("gauge-horizontal-spheres-count");
		if (countObj != null) {
			this.sphereCount = countObj.intValue();
		}
		//#endif
		
		//#if polish.css.gauge-horizontal-spheres-highlight-color
		colorObj = style.getColorProperty("gauge-horizontal-spheres-highlight-color");
		if (colorObj != null) {
			this.sphereHighlightColor = colorObj.getColor();
		}
		//#endif
		
		//#if polish.css.gauge-horizontal-spheres-highlight-count
		countObj = style.getIntProperty("gauge-horizontal-spheres-highlight-count");
		if (countObj != null) {
			this.sphereHighlightCount = countObj.intValue();
		}
		//#endif
		
		//#if polish.css.gauge-horizontal-spheres-highlight-center-color
		colorObj = style.getColorProperty("gauge-horizontal-spheres-highlight-center-color");
		if (colorObj != null) {
			this.sphereHighlightCenterColor = colorObj.getColor();
			
			if(this.sphereHighlightCount > 2)
			{
				this.sphereHighlightCenterIndex = 1;
				this.sphereHighlightCenterSpan = this.sphereHighlightCount - 2;
			}
		}
		//#endif
		
		//#if polish.css.gauge-horizontal-spheres-width
		countObj = style.getIntProperty("gauge-horizontal-spheres-width");
		if (countObj != null) {
			this.sphereWidth = countObj.intValue();
			this.contentHeight = this.sphereWidth;
		}
		//#endif
	}
	
	
	

	//#if polish.css.gauge-horizontal-spheres-interval
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	protected void setStyle(Style style, boolean resetStyle)
	{
		super.setStyle(style, resetStyle);
		Integer countObj = style.getIntProperty("gauge-horizontal-spheres-interval");
		if (countObj != null) {
			this.interval = countObj.intValue();
		}
	}
	//#endif

	public void animate(long currentTime, ClippingRegion repaintRegion) {
		if (this.isContinuousRunning && (currentTime - this.lastAnimationTime) > this.interval) {
			this.nextHighlight = true;
			this.sphereHighlightIndex = this.sphereHighlightIndex % this.sphereCount;
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
			
			if(this.nextHighlight)
			{
				this.sphereHighlightIndex++;
				this.nextHighlight = false;
			}
			
			if(!this.isContinuousRunning)
			{
				this.sphereHighlightIndex = ((this.gauge.getValue() * 100 / this.gauge.getMaxValue()) * this.maxSpheres) / 100;
			}
			
			int stepX = this.contentWidth / this.sphereCount; 
			int offsetX = 0; 
				
			for(int i=0; i < this.sphereCount; i++)
			{
				setSphereColor(g, i);
				
				offsetX = stepX * i;
				
				g.fillArc( x + offsetX, y, this.sphereWidth, this.sphereWidth, 0, 360);
			}
		//#endif
	}
	
	private void setSphereColor(Graphics g, int i)
	{
		int startIndex = this.sphereHighlightIndex;
		int endIndex = ((this.sphereHighlightIndex + this.sphereHighlightCount - 1) % this.sphereCount);
				
		if(startIndex <= endIndex)
		{
			if(i >= startIndex && i <= endIndex)
			{
				if(this.sphereHighlightCenterIndex != -1)
				{
					if(setCenterSphereColor(startIndex, g, i))
						return;
				}
				
				g.setColor( this.sphereHighlightColor );
				return;
			}
		}
		else
		{
			if(i >= startIndex || i <= endIndex)
			{
				if(this.sphereHighlightCenterIndex != -1)
				{
					if(setCenterSphereColor(startIndex, g, i))
						return;
				}
				
				g.setColor( this.sphereHighlightColor );
				return;
			}			
		}
		
		g.setColor( this.sphereColor );
	}
	
	public boolean setCenterSphereColor(int startIndex, Graphics g, int i)
	{
		int centerStartIndex = (startIndex + this.sphereHighlightCenterIndex) % this.sphereCount;
		int centerEndIndex = (startIndex + this.sphereHighlightCenterIndex + (this.sphereHighlightCenterSpan - 1)) % this.sphereCount;
				
		if(centerStartIndex <= centerEndIndex)
		{
			if(i >= centerStartIndex && i <= centerEndIndex)
			{
				g.setColor( this.sphereHighlightCenterColor  );
				return true;
			}
		}
		else
		{
			if(i >= centerStartIndex || i <= centerEndIndex)
			{
				g.setColor( this.sphereHighlightCenterColor );
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Determines whether this view is valid for the given item.
	 * @return true when this view can be applied
	 */
	protected boolean isValid(Item parent, Style style) {
		return parent instanceof Gauge;
	}
	
}
