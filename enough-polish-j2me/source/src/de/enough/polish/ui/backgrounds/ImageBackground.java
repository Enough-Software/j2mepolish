//#condition polish.usePolishGui
/*
 * Created on 14-Mar-2004 at 21:31:51.
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

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.ImageConsumer;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;

/**
 * <p>Paints an image as a background.</p>
 * <p>Following CSS parameters are supported:
 * <ul>
 * 		<li><b>image</b>: the image url, e.g. url( bg.png ) or none</li>
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
 * <p>Copyright Enough Software 2004 - 2009</p>

 * <pre>
 * history
 *        14-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ImageBackground 
extends Background
//#ifdef polish.images.backgroundLoad
implements ImageConsumer
//#endif
{
	
	private Image image;
	private int color;
	private boolean isLoaded;
	private final String imageUrl;
	private final int anchor;
	private final boolean doCenter;
	private Dimension xOffset;
	private Dimension yOffset;

	/**
	 * Creates a new image background.
	 * 
	 * @param color the background color or Item.TRANSPARENT
	 * @param imageUrl the url of the image, e.g. "/bg.png", must not be null!
	 * @param anchor the anchor of the image, either  "left", "right", 
	 * 			"center" (="horizontal-center"), "vertical-center", "top" or "bottom" 
	 * 			or any combinationof these values. Defaults to "horizontal-center | vertical-center"
	 */
	public ImageBackground( int color, String imageUrl, int anchor ) {
		this(color, imageUrl, anchor, 0, 0 );
	}

	/**
	 * Creates a new image background.
	 * 
	 * @param color the background color or Item.TRANSPARENT
	 * @param imageUrl the url of the image, e.g. "/bg.png", must not be null!
	 * @param anchor the anchor of the image, either  "left", "right", 
	 * 			"center" (="horizontal-center"), "vertical-center", "top" or "bottom" 
	 * 			or any combination of these values. Defaults to "horizontal-center | vertical-center"
	 * @param xOffset The number of pixels to move the image horizontally, negative values move it to the left.
	 * @param yOffset The number of pixels to move the image vertically, negative values move it to the top.
	 */
	public ImageBackground( int color, String imageUrl, int anchor, int xOffset, int yOffset ) {
		this( color, imageUrl, anchor, new Dimension(xOffset), new Dimension(yOffset) );
	}
	
	/**
	 * Creates a new image background.
	 * 
	 * @param color the background color or Item.TRANSPARENT
	 * @param imageUrl the url of the image, e.g. "/bg.png", must not be null!
	 * @param anchor the anchor of the image, either  "left", "right", 
	 * 			"center" (="horizontal-center"), "vertical-center", "top" or "bottom" 
	 * 			or any combination of these values. Defaults to "horizontal-center | vertical-center"
	 * @param xOffset The number of pixels to move the image horizontally, negative values move it to the left.
	 * @param yOffset The number of pixels to move the image vertically, negative values move it to the top.
	 */
	public ImageBackground( int color, String imageUrl, int anchor, Dimension xOffset, Dimension yOffset ) {
		this.color = color;
		this.imageUrl = imageUrl;
		this.anchor = anchor;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.doCenter = ( anchor == (Graphics.VCENTER | Graphics.HCENTER) );
		this.isLoaded = (imageUrl == null);
	}
	
	/**
	 * Creates a new image background.
	 * 
	 * @param color the background color or Item.TRANSPARENT
	 * @param image the image, must not be null!
	 * @param anchor the anchor of the image, either  "left", "right", 
	 * 			"center" (="horizontal-center"), "vertical-center", "top" or "bottom" 
	 * 			or any combination of these values. Defaults to "horizontal-center | vertical-center"
	 */
	public ImageBackground( int color, Image image, int anchor ) {
		this(color, image, anchor, 0, 0 );
	}

	/**
	 * Creates a new image background.
	 * 
	 * @param color the background color or Item.TRANSPARENT
	 * @param image the image, must not be null!
	 * @param anchor the anchor of the image, either  "left", "right", 
	 * 			"center" (="horizontal-center"), "vertical-center", "top" or "bottom" 
	 * 			or any combinationof these values. Defaults to "horizontal-center | vertical-center"
	 * @param xOffset The number of pixels to move the image horizontally, negative values move it to the left.
	 * @param yOffset The number of pixels to move the image vertically, negative values move it to the top.
	 */
	public ImageBackground( int color, Image image, int anchor, int xOffset, int yOffset ) {
		this( color, image, anchor, new Dimension(xOffset), new Dimension(yOffset));
	}

	/**
	 * Creates a new image background.
	 * 
	 * @param color the background color or Item.TRANSPARENT
	 * @param image the image, must not be null!
	 * @param anchor the anchor of the image, either  "left", "right", 
	 * 			"center" (="horizontal-center"), "vertical-center", "top" or "bottom" 
	 * 			or any combinationof these values. Defaults to "horizontal-center | vertical-center"
	 * @param xOffset The number of pixels to move the image horizontally, negative values move it to the left.
	 * @param yOffset The number of pixels to move the image vertically, negative values move it to the top.
	 */
	public ImageBackground( int color, Image image, int anchor, Dimension xOffset, Dimension yOffset ) {
		this.color = color;
		this.image = image;
		this.imageUrl = null;
		this.isLoaded = true;
		this.anchor = anchor;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.doCenter = ( anchor == (Graphics.VCENTER | Graphics.HCENTER) );
	}

	//#ifdef polish.images.backgroundLoad
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ImageConsumer#setImage(java.lang.String, javax.microedition.lcdui.Image)
	 */
	public void setImage(String url, Image image) {
		this.image = image;
	}
	//#endif
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (!this.isLoaded) {
			try {
				this.image = StyleSheet.getImage(this.imageUrl, this, false);
			} catch (IOException e) {
				//#debug error
				System.out.println( "unable to load image [" + this.imageUrl + "]" + e );
			}
			this.isLoaded = true;
		}
		if (this.color != Item.TRANSPARENT) {
			g.setColor( this.color );
			g.fillRect( x, y, width, height );
		}
		x += this.xOffset.getValue(width);
		y += this.yOffset.getValue(height);
		if (this.image != null) {
			if (this.doCenter) {
				int centerX = x + (width / 2);
				int centerY = y + (height / 2);
				g.drawImage(this.image, centerX, centerY, Graphics.HCENTER | Graphics.VCENTER );
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
				g.drawImage(this.image, x, y, this.anchor );
			}
		}
	}
	
	/**
	 * Sets the image for this background.
	 * 
	 * @param image the image
	 */
	public void setImage( Image image ) {
		this.image = image;
		this.isLoaded = (image != null);
	}
	
	/**
	 * Retrieves the image from this background.
	 * 
	 * @return the image
	 */
	public Image getImage() {
		return this.image;
	}
	
	/**
	 * Releases all (memory intensive) resources such as images or RGB arrays of this background.
	 */
	public void releaseResources() {
		if (this.imageUrl != null) {
			this.isLoaded = false;
			this.image = null;
		}
	}


	//#if polish.css.animations
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
		 */
		public void setStyle(Style style)
		{
			//#if polish.css.background-image-color
				Color col = style.getColorProperty("background-image-color");
				if (col != null) {
					this.color = col.getColor();
				}
			//#endif
			//#if polish.css.background-image-x-offset
				Dimension xOffsetInt = (Dimension) style.getObjectProperty("background-image-x-offset");
				if (xOffsetInt != null) {
					this.xOffset = xOffsetInt;
				}
			//#endif
			//#if polish.css.background-image-y-offset
				Dimension yOffsetInt = (Dimension) style.getObjectProperty("background-image-y-offset");
				if (yOffsetInt != null) {
					this.yOffset = yOffsetInt;
				}
			//#endif
		}
	//#endif

}
