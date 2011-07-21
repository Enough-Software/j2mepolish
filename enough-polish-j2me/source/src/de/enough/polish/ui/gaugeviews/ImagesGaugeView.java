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
import de.enough.polish.util.TextUtil;

/**
 * <p>Displays a list of images like an animated GIF</p>
 * <p>Takes a list of images and an interval as arguments.
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2010</p>
 * <pre>
 * history
 *        22-Oct-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class ImagesGaugeView extends ItemView{
	
	/**
	 * The parent gauge
	 */
	transient Gauge gauge;
	
	/**
	 * flag to indicate that the gauge is non-interactive
	 */
	boolean isContinuousRunning;
	
	/**
	 * the images
	 */
	transient Image[] images;
	
	/**
	 * the index of the current image
	 */
	int current = 0;
	
	/**
	 * the interval in ms, defaults to 500
	 */
	int interval = 500;
	
	/**
	 * the last animation time
	 */
	long lastAnimationTime = 0;
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int, int)
	 */
	protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) 
	{
		this.gauge = (Gauge)parent;
		this.isContinuousRunning = this.gauge.getMaxValue() == Gauge.INDEFINITE && this.gauge.getValue() == Gauge.CONTINUOUS_RUNNING;
		
		if (this.images == null) {
			//#debug error
			System.out.println("Unable to initialize ImageGaugeView with style " + (parent.getStyle() == null ? "<null>" : parent.getStyle().name ) + ": no gauge-images-sources defined.");
			return;
		}
		int maxHeight = 0;
		int maxWidth = 0;
		int width;
		int height;
		for (int i = 0; i < this.images.length; i++) {
			Image image = this.images[i];
			
			width = image.getWidth();
			height = image.getWidth();
			
			if(width > maxWidth)
			{
				maxWidth = width;
			}
			
			if(height > maxHeight)
			{
				maxHeight = height;
			}
		}
		
		this.contentHeight = maxHeight;
		this.contentWidth = maxWidth;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		super.animate(currentTime, repaintRegion);
		
		if (this.isContinuousRunning)
		{
			if(this.lastAnimationTime == -1)
			{
				this.lastAnimationTime = currentTime;
			}
				
			if( ((currentTime - this.lastAnimationTime) > this.interval) && this.images != null)
			{
				this.current = (this.current + 1) % this.images.length;
				this.lastAnimationTime = currentTime;
				addFullRepaintRegion( this.parentItem, repaintRegion );
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintContent(de.enough.polish.ui.Item, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g) 
	{
		g.drawImage(this.images[this.current], x, y, Graphics.TOP | Graphics.LEFT );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	protected void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		
		//#if polish.css.gauge-images-sources
			String sources = style.getProperty("gauge-images-sources");
			if (sources != null) {
				setImages(sources);
			}
		//#endif
		
		//#if polish.css.gauge-images-interval
			Integer intervalObj = style.getIntProperty("gauge-images-interval");
			if (intervalObj != null) {
				this.interval = intervalObj.intValue();
			}
		//#endif
		
		if(this.images == null)
		{
			//#debug error
			System.out.println("no images set for image gauge");
		}
	}
	
	/**
	 * Reads the comma-separated list set via gauge-images-sources
	 * and loads the images to <code>images</code>
	 * @param sources
	 */
	void setImages(String sources)
	{
		String[] urls = TextUtil.split(sources, ',');
		this.images = new Image[urls.length];
		String image = null;
		try {
			for (int i = 0; i < urls.length; i++) {
				image = urls[i].trim();
				
				if(image.charAt(0) != '/')
				{
					image = "/" + image; 
				}
				
				this.images[i] = Image.createImage(image);
			}
		} catch (IOException e) {
			//#debug error
			System.out.println("unable to read image " + image + " : " + e.toString());
			this.images = null;
		}
	}
}
