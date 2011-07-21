//#condition polish.usePolishGui && polish.midp2
/*
 * Created on 01-May-2007 at 11:34:15
 *
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
package de.enough.polish.ui.borders;

import de.enough.polish.ui.Border;
import de.enough.polish.ui.StyleSheet;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * <p>Paints an border with customizable images for each edge and each side.</p>
 *
 * <p>Copyright (c) Enough Software 2007 - 2009</p>
 * @author Michael Koch, michael.koch@enough.de
 */
public class ImageBorder extends Border
{
	private String imageUrl;
	private Image topLeftImage;
	private Image topCenterImage;
	private Image topRightImage;
	private Image middleLeftImage;
	private Image middleRightImage;
	private Image bottomLeftImage;
	private Image bottomCenterImage;
	private Image bottomRightImage;
	private boolean isLoaded;
	private final int borderWidth;
	
	public ImageBorder(int borderWidth, Image image)
	{
		super( borderWidth, borderWidth, borderWidth, borderWidth );
		this.borderWidth = borderWidth;
		this.topLeftImage = getImagePart(image, 0, borderWidth);
		this.topCenterImage = getImagePart(image, 1, borderWidth);
		this.topRightImage = getImagePart(image, 2, borderWidth);
		this.middleLeftImage = getImagePart(image, 3, borderWidth);
		this.middleRightImage = getImagePart(image, 4, borderWidth);
		this.bottomLeftImage = getImagePart(image, 5, borderWidth);
		this.bottomCenterImage = getImagePart(image, 6, borderWidth);
		this.bottomRightImage = getImagePart(image, 7, borderWidth);
		this.isLoaded = true;
	}

	public ImageBorder(int borderWidth, String imageUrl)
	{
		super( borderWidth, borderWidth, borderWidth, borderWidth );
		this.borderWidth = borderWidth;
		this.imageUrl = imageUrl;
	}
	
	private Image getImagePart(Image image, int index, int borderWidth)
	{
		return Image.createImage(image, 0, index * borderWidth, borderWidth, borderWidth, Sprite.TRANS_NONE);
	}

	public void paint(int x, int y, int width, int height, Graphics g)
	{
		if (!this.isLoaded) {
			try {
				Image image = StyleSheet.getImage(this.imageUrl, this, false);
				this.topLeftImage = getImagePart(image, 0, this.borderWidth);
				this.topCenterImage = getImagePart(image, 1, this.borderWidth);
				this.topRightImage = getImagePart(image, 2, this.borderWidth);
				this.middleLeftImage = getImagePart(image, 3, this.borderWidth);
				this.middleRightImage = getImagePart(image, 4, this.borderWidth);
				this.bottomLeftImage = getImagePart(image, 5, this.borderWidth);
				this.bottomCenterImage = getImagePart(image, 6, this.borderWidth);
				this.bottomRightImage = getImagePart(image, 7, this.borderWidth);
			} catch (IOException e) {
				//#debug error
				System.out.println( "unable to load image " + e );
			}
			this.isLoaded = true;
		}
		
		if (this.topLeftImage != null) {
			g.drawImage(this.topLeftImage, x, y, Graphics.TOP | Graphics.LEFT );
		}

		if (this.topCenterImage != null) {
			int num = (width - (2 * this.borderWidth)) / this.borderWidth + 1;
			for (int i = 1; i <= num; i++) {
				g.drawImage(this.topCenterImage, x + i * this.borderWidth, y, Graphics.TOP | Graphics.LEFT );
			}
		}
		
		if (this.topRightImage != null) {
			g.drawImage(this.topRightImage, x + width, y, Graphics.TOP | Graphics.RIGHT );
		}

		if (this.middleLeftImage != null) {
			int num = (height - (2 * this.borderWidth)) / this.borderWidth + 1;
			for (int i = 1; i <= num; i++) {
				g.drawImage(this.middleLeftImage, x, y + i * this.borderWidth, Graphics.TOP | Graphics.LEFT );
			}
		}
		
		if (this.middleRightImage != null) {
			int num = (height - (2 * this.borderWidth)) / this.borderWidth + 1;
			for (int i = 1; i <= num; i++) {
				g.drawImage(this.middleRightImage, x + width, y + i * this.borderWidth, Graphics.TOP | Graphics.RIGHT );
			}
		}
		
		if (this.bottomLeftImage != null) {
			g.drawImage(this.bottomLeftImage, x, y + height, Graphics.BOTTOM | Graphics.LEFT );
		}
		
		if (this.bottomCenterImage != null) {
			int num = (width - (2 * this.borderWidth)) / this.borderWidth + 1;
			for (int i = 1; i <= num; i++) {
				g.drawImage(this.bottomCenterImage, x + i * this.borderWidth, y + height, Graphics.BOTTOM | Graphics.LEFT );
			}
		}

		if (this.bottomRightImage != null) {
			g.drawImage(this.bottomRightImage, x + width, y + height, Graphics.BOTTOM | Graphics.RIGHT );
		}
	}
}
