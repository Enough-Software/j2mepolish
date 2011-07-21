//#condition polish.usePolishGui
/*
 * Created on Dec 3, 2008 at 6:04:22 PM.
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
package de.enough.polish.ui;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.util.RgbImage;

/**
 * <p>Displays RGB images within J2ME Polish screens and containers.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RgbImageItem extends Item
{

	private RgbImage image;

	/**
	 * Creates a new RGB image item
	 * 
	 * @param image the base RGB image
	 */
	public RgbImageItem(RgbImage image)
	{
		this( null, image, null);
	}

	/**
	 * Creates a new RGB image item
	 * 
	 * @param image the base RGB image
	 * @param style the element's style
	 */
	public RgbImageItem(RgbImage image, Style style)
	{
		this( null, image, style );
	}
	/**
	 * Creates a new RGB image item
	 * 
	 * @param label the element's label
	 * @param image the base RGB image
	 * @param style the element's style
	 */
	public RgbImageItem(String label, RgbImage image, Style style)
	{
		super(label, 0, PLAIN, style);
		this.image = image;
	}
	
	

	/**
	 * Retrieves the RGB image
	 * @return the image
	 */
	public RgbImage getImage() {
		return this.image;
	}
	

	/**
	 * Sets the RGB image
	 * @param image the image to set
	 */
	public void setImage(RgbImage image)
	{
		this.image = image;
		if (this.isInitialized) {
			if ((image == null && this.contentWidth != 0)
				|| (image != null && (image.getWidth() != this.contentWidth ||  image.getHeight() != this.contentHeight))
			) {
				requestInit();
			}
			repaint();
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight)
	{
		RgbImage img = this.image;
		if (img == null) {
			this.contentWidth = 0;
			this.contentHeight = 0;
		} else {
			this.contentWidth = img.getWidth();
			this.contentHeight = img.getHeight();
		}

	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g)
	{
		RgbImage img = this.image;
		if (img != null) {
			img.paint(x, y, g);
		}
	}
	
	
	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector()
	{
		return "rgbimage";
	}
	//#endif


}
