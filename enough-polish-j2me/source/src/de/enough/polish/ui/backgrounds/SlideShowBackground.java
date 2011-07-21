//#condition polish.usePolishGui
/*
 * Created on 25-April-2008 at 11:31:51.
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
package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.util.TextUtil;

/**
 * <p>Paints and exchanges several images in the background.</p>
 * <p>Following CSS parameters are supported:
 * <ul>
 * 		<li><b>images</b>: comma separated list of image urls, e.g. bg1.png,bg2.png,bg3.png</li>
 * 		<li><b>color</b>: the background color, should the image
 * 							be smaller than the actual background-area. Or "transparent".</li>
 * 		<li><b>anchor</b>: The anchor of the image, either  "left", "right", 
 * 			"center" (="horizontal-center"), "vertical-center", "top" or "bottom" 
 * 			or any combinationof these values. Defaults to "horizontal-center | vertical-center".
 * 		</li>
 * 		<li><b>x-offset</b>: The number of pixels to move the image horizontally, negative values move it to the left.</li>
 * 		<li><b>y-offset</b>: The number of pixels to move the image vertically, negative values move it to the top.</li>
 * 		<li><b></b>: </li>
 * </ul>
 * </p>
 *
 * <p>Copyright Enough Software 2008</p>

 * @author Robert Virkus, robert@enough.de
 */
public class SlideShowBackground 
extends Background
{
	
	private Image currentImage;
	private final int color;
	private final int anchor;
	private final boolean doCenter;
	private final int xOffset;
	private final int yOffset;
	private final String[] imageUrls;
	private int currentImageIndex;
	private long lastImageSwitch;
	private final long interval;

	/**
	 * Creates a new image background.
	 * 
	 * @param color the background color or Item.TRANSPARENT
	 * @param imageUrls comma separated list of image urls, e.g. "bg1.png,bg2.png,bg3.png"
	 * @param anchor the anchor of the image, either  "left", "right", 
	 * 			"center" (="horizontal-center"), "vertical-center", "top" or "bottom" 
	 * 			or any combinationof these values. Defaults to "horizontal-center | vertical-center"
	 * @param interval the interval in milliseconds before the next image is loaded
	 */
	public SlideShowBackground( int color, String imageUrls, int anchor, long interval ) {
		this(color, imageUrls, anchor, interval, 0, 0 );
	}

	/**
	 * Creates a new image background.
	 * 
	 * @param color the background color or Item.TRANSPARENT
	 * @param imageUrls comma separated list of image urls, e.g. "bg1.png,bg2.png,bg3.png"
	 * @param anchor the anchor of the image, either  "left", "right", 
	 * 			"center" (="horizontal-center"), "vertical-center", "top" or "bottom" 
	 * 			or any combinationof these values. Defaults to "horizontal-center | vertical-center"
	 * @param interval the interval in milliseconds before the next image is loaded
	 * @param xOffset The number of pixels to move the image horizontally, negative values move it to the left.
	 * @param yOffset The number of pixels to move the image vertically, negative values move it to the top.
	 */
	public SlideShowBackground( int color, String imageUrls, int anchor, long interval, int xOffset, int yOffset ) {
		this.color = color;
		this.imageUrls = TextUtil.split( imageUrls, ',' );
		this.anchor = anchor;
		this.interval = interval;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.doCenter = ( anchor == (Graphics.VCENTER | Graphics.HCENTER) );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		Image image = this.currentImage;
		if (image == null) {
			String url = null;
			try {
				this.lastImageSwitch = System.currentTimeMillis();
				url = this.imageUrls[ this.currentImageIndex ];
				image = StyleSheet.getImage(url, this, false);
				this.currentImage = image;
			} catch (Exception e) {
				//#debug error
				System.out.println( "unable to load image [" + url + "]" + e );
			}
		}
		if (this.color != Item.TRANSPARENT) {
			g.setColor( this.color );
			g.fillRect( x, y, width, height );
		}
		x += this.xOffset;
		y += this.yOffset;
		if (image != null) {
			if (this.doCenter) {
				int centerX = x + (width / 2);
				int centerY = y + (height / 2);
				g.drawImage(image, centerX, centerY, Graphics.HCENTER | Graphics.VCENTER );
			} else {
				if ( (this.anchor & Graphics.HCENTER) == Graphics.HCENTER) {
					x += (width / 2);
				} else if ( (this.anchor & Graphics.RIGHT) == Graphics.RIGHT) {
					x += width;
				}
				if ( (this.anchor & Graphics.VCENTER) == Graphics.VCENTER) {
					y += (height / 2);
				} else if ( (this.anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
					y += height;
				}
				//System.out.println("Drawing image at " + x + ", " + y);
				g.drawImage(image, x, y, this.anchor );
			}
		}
	}
	
	/**
	 * Sets the current image for this background.
	 * 
	 * @param image the image
	 */
	public void setImage( Image image ) {
		this.currentImage = image;
	}
	
	/**
	 * Retrieves the current image from this background.
	 * 
	 * @return the image
	 */
	public Image getImage() {
		return this.currentImage;
	}
	
	/**
	 * Releases all (memory intensive) resources such as images or RGB arrays of this background.
	 */
	public void releaseResources() {
		this.currentImage = null;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#animate(de.enough.polish.ui.Screen, de.enough.polish.ui.Item, long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(Screen screen, Item parent, long currentTime,
			ClippingRegion repaintRegion)
	{
		if (currentTime - this.lastImageSwitch > this.interval) {
			int index = this.currentImageIndex + 1;
			if (index >= this.imageUrls.length ) {
				index = 0;
			}
			String url = null;
			try {
				this.lastImageSwitch = currentTime;
				this.currentImageIndex = index;
				url = this.imageUrls[index];
				this.currentImage = StyleSheet.getImage(url, this, false);
			} catch (Exception e) {
				//#debug error
				System.out.println( "unable to load image [" + url + "]" + e );
			}
			addRelativeToBackgroundRegion(repaintRegion, screen, parent, 0, 0, 0, 0 );
		}
		
	}
	


}
