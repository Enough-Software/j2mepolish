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
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.ImageUtil;

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
 *
 * <pre>
 * history
 *        14-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ScalingImageBackground 
extends Background
{
	
	/** use this mode for not scaling the image at all */
	public static final int MODE_NO_SCALE = 0;
	/** use this mode for scaling the image according to the width and height of the background */
	public static final int MODE_SCALE = 1;
	/** use this mode for scaling the image proportional according to the minimum of width and height of the background */
	public static final int MODE_SCALE_PROPORTIONAL = 2;
	/** use this mode for scaling the image proportional according to the maximum of width and height of the background */
	public static final int MODE_SCALE_PROPORTIONAL_EXPAND = 3;

	
	private Image image;
	private final int color;
	private boolean isLoaded;
	private final String imageUrl;
	private final int anchor;
	private final boolean doCenter;
	private final int xOffset;
	private final int yOffset;
	private final int scalingMode;
	//#if polish.midp2
		private int[] rgbData;
		private int[] rgbDataScaled;
		int lastWidth;
		int lastHeight;
		private int scaledWidth;
		private int scaledHeight;
	//#endif


	/**
	 * Creates a new image background.
	 * 
	 * @param color the background color or Item.TRANSPARENT
	 * @param imageUrl the url of the image, e.g. "/bg.png", must not be null!
	 * @param scalingMode the scaling mode to be used
	 * @param anchor the anchor of the image, either  "left", "right", 
	 * 			"center" (="horizontal-center"), "vertical-center", "top" or "bottom" 
	 * 			or any combinationof these values. Defaults to "horizontal-center | vertical-center"
	 * @param xOffset The number of pixels to move the image horizontally, negative values move it to the left.
	 * @param yOffset The number of pixels to move the image vertically, negative values move it to the top.
	 * @see #MODE_NO_SCALE
	 * @see #MODE_SCALE
	 * @see #MODE_SCALE_PROPORTIONAL
	 * @see #MODE_SCALE_PROPORTIONAL_EXPAND
	 */
	public ScalingImageBackground( int color, String imageUrl, int scalingMode, int anchor, int xOffset, int yOffset ) {
		this.color = color;
		this.imageUrl = imageUrl;
		this.scalingMode = scalingMode;
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
	 * @param scalingMode the scaling mode to be used
	 * @param anchor the anchor of the image, either  "left", "right", 
	 * 			"center" (="horizontal-center"), "vertical-center", "top" or "bottom" 
	 * 			or any combinationof these values. Defaults to "horizontal-center | vertical-center"
	 * @param xOffset The number of pixels to move the image horizontally, negative values move it to the left.
	 * @param yOffset The number of pixels to move the image vertically, negative values move it to the top.
	 * @see #MODE_NO_SCALE
	 * @see #MODE_SCALE
	 * @see #MODE_SCALE_PROPORTIONAL
	 * @see #MODE_SCALE_PROPORTIONAL_EXPAND
	 */
	public ScalingImageBackground( int color, Image image, int scalingMode, int anchor, int xOffset, int yOffset ) {
		this.color = color;
		this.image = image;
		this.imageUrl = null;
		this.isLoaded = true;
		this.scalingMode = scalingMode;
		this.anchor = anchor;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.doCenter = ( anchor == (Graphics.VCENTER | Graphics.HCENTER) );
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (!this.isLoaded) {
			try {
				this.image = StyleSheet.getImage(this.imageUrl, this, false);
				//#if polish.midp2
					this.rgbData = new int[ this.image.getWidth() * this.image.getHeight() ];
					this.image.getRGB(this.rgbData, 0,this.image.getWidth(), 0, 0, this.image.getWidth(), this.image.getHeight() );
				//#endif
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
		x += this.xOffset;
		y += this.yOffset;
		if (this.image != null) {
			//#if polish.midp2
				if (width != this.lastWidth || height != this.lastHeight) {
					int newWidth, newHeight;
					switch (this.scalingMode) {
					case MODE_SCALE:
						newWidth = width;
						newHeight = height;
						break;
					case MODE_SCALE_PROPORTIONAL:
						int factor = Math.min( (width << 8) / this.image.getWidth(), (height << 8) / this.image.getHeight() );
						newWidth = (this.image.getWidth() * factor) >> 8;
						newHeight = (this.image.getHeight() * factor) >> 8;
						break;
					case MODE_SCALE_PROPORTIONAL_EXPAND:
						factor = Math.max( (width << 8) / this.image.getWidth(), (height << 8) / this.image.getHeight() );
						newWidth = (this.image.getWidth() * factor) >> 8;
						newHeight = (this.image.getHeight() * factor) >> 8;
						break;
					default:
						newWidth = this.image.getWidth();
						newHeight = this.image.getHeight();
						break;
					}
					this.rgbDataScaled = ImageUtil.scale( this.rgbData, newWidth, newHeight, this.image.getWidth(), this.image.getHeight() );
					this.lastWidth = width;
					this.lastHeight = height;
					this.scaledWidth = newWidth;
					this.scaledHeight = newHeight;
				}
				if (this.doCenter) {
					x += (width >> 1) - (this.scaledWidth >> 1);
					y += (height >> 1) - (this.scaledHeight >> 1);
				} else {
					if ( (this.anchor & Graphics.HCENTER) == Graphics.HCENTER) {
						x += (width >> 1) - (this.scaledWidth >> 1);
					} else if ( (this.anchor & Graphics.RIGHT) == Graphics.RIGHT) {
						x += width - this.scaledWidth;
					}
					if ( (this.anchor & Graphics.VCENTER) == Graphics.VCENTER) {
						y += (height >> 1) - (this.scaledHeight >> 1);
					} else if ( (this.anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
						y += height - this.scaledHeight;
					}
				}
				DrawUtil.drawRgb( this.rgbDataScaled, x, y, this.scaledWidth, this.scaledHeight, true, g );
			//#else
				// fallback to plain images on MIDP 1.0 phones:
				if (this.doCenter) {
					int centerX = x + (width >> 1);
					int centerY = y + (height >> 1);
					g.drawImage(this.image, centerX, centerY, Graphics.HCENTER | Graphics.VCENTER );
				} else {
					if ( (this.anchor & Graphics.HCENTER) == Graphics.HCENTER) {
						x += (width >> 1);
					} else if ( (this.anchor & Graphics.RIGHT) == Graphics.RIGHT) {
						x += width;
					}
					if ( (this.anchor & Graphics.VCENTER) == Graphics.VCENTER) {
						y += (height >> 1);
					} else if ( (this.anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
						y += height;
					}
					//System.out.println("Drawing image at " + x + ", " + y);
					g.drawImage(this.image, x, y, this.anchor );
				}
			//#endif
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
		//#if polish.midp2
			this.rgbData = null;
			this.rgbDataScaled = null;
			this.lastWidth = 0;
			this.lastHeight = 0;
		//#endif
	}


}
