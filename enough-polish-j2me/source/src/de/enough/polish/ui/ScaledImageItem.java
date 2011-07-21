//#condition polish.usePolishGui
/*
 * Copyright (c) 2009 Michael Koch / Enough Software
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
package de.enough.polish.ui;

import javax.microedition.lcdui.Image;

import de.enough.polish.util.ImageUtil;

/**
 * An item that can contain an image that is scaled to the expected size (on MIDP 2.0+ handsets).
 * 
 * @author Michael Koch, michael.koch@enough.de
 */
public class ScaledImageItem
	extends ImageItem
{
	private Image originalImage;
	private Dimension scaledImageWidth;
	private Dimension scaledImageHeight;

	/**
	 * Creates a scaled image item with the give size constraints.
	 * 
	 * @param label the label string
	 * @param image the image, can be mutable or immutable
	 * @param imageWidth the expected width of the image on the screen
	 * @param imageHeight the expected height of the image on the screen
	 * @param layout a combination of layout directives
	 * @param altText the text that may be used in place of the image
	 * 
	 * @throws IllegalArgumentException - if the layout value is not a legal combination of directives
	 * 
	 * @see ImageItem#ImageItem(String, Image, int, String)
	 * @see #ScaledImageItem(String, Image, Dimension, Dimension, int, String, Style)
	 */
	public ScaledImageItem(String label, Image image, Dimension imageWidth, Dimension imageHeight, int layout, String altText)
	{
		this(label, image, imageWidth, imageHeight, layout, altText, null);
	}

	/**
	 * Creates a scaled image item with the give size constraints.
	 * 
	 * @param label the label string
	 * @param image the image, can be mutable or immutable
	 * @param imageWidth the expected width of the image on the screen
	 * @param imageHeight the expected height of the image on the screen
	 * @param layout a combination of layout directives
	 * @param altText the text that may be used in place of the image
	 * @param style the style of this image item
	 * 
	 * @throws IllegalArgumentException - if the layout value is not a legal combination of directives
	 * 
	 * @see ImageItem#ImageItem(String, Image, int, String, Style)
	 * @see #ScaledImageItem(String, Image, Dimension, Dimension, int, String)
	 */
	public ScaledImageItem(String label, Image image, Dimension imageWidth, Dimension imageHeight, int layout, String altText, Style style)
	{
		super(label, image, layout, altText, style);
		this.originalImage = image;
		this.scaledImageWidth = imageWidth;
		this.scaledImageHeight = imageHeight;
	}

	//#if polish.midp2
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.ImageItem#initContent(int, int, int)
		 */
		protected void initContent(int firstLineWidth, int availWidth, int availHeight)
		{
			super.initContent(firstLineWidth, availWidth, availHeight);
			int sourceWidth = 0;
			int sourceHeight = 0;
	
			if (this.originalImage != null) {
				sourceWidth = this.originalImage.getWidth();
				sourceHeight = this.originalImage.getHeight();
			}
	
			int targetWidth = sourceWidth;
			int targetHeight = sourceHeight;
	
			if (this.scaledImageWidth != null) {
				targetWidth = this.scaledImageWidth.getValue(availWidth);
			}
	
			if (this.scaledImageHeight != null) {
				targetHeight = this.scaledImageHeight.getValue(availHeight);
			}
	
			// Overwrite content size.
			this.contentHeight = targetHeight;
			this.contentWidth = targetWidth;
	
			// Don't try to scale a non-existent image.
			if (this.originalImage == null) {
				return;
			}
	
			Image currentImage = this.image;
	
			// Scale only when current image size differs from expected image size. 
			if ((sourceWidth != targetWidth || sourceHeight != targetWidth)
				&& (targetWidth != currentImage.getWidth() || targetHeight != currentImage.getHeight())
				&& (targetWidth > 1 && targetHeight > 1)
			) {
				//#debug
				System.out.println("Scaling image from " + sourceWidth + "x" + sourceHeight + " to " + targetWidth + "x" + targetHeight);
	
				// Get RGB data of original image.
				int[] rgbData = new int[sourceWidth * sourceHeight];
				this.originalImage.getRGB(rgbData, 0, sourceWidth, 0, 0, sourceWidth, sourceHeight);
	
				// Scale image.
				int[] newRgbData = new int[targetWidth * targetHeight];
		        ImageUtil.scale(rgbData, targetWidth, targetHeight, sourceWidth, sourceHeight, newRgbData);
	
		        // Create and set new image.
		        Image img = Image.createRGBImage(newRgbData, targetWidth, targetHeight, true);
		        this.image = img;
			}
		}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ImageItem#setImage(javax.microedition.lcdui.Image)
	 */
	public void setImage(Image image)
	{
		this.originalImage = image;
		super.setImage(image);
	}
}
