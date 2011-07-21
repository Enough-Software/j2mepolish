//#condition polish.usePolishGui
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

/**
 * <p>Paints an border with customizable images for each edge and each side.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * @author Michael Koch, michael.koch@enough.de
 */
public class MultiImageBorder extends Border
{
	private Image topLeftImage;
	private String topLeftUrl;
	private Image topCenterImage;
	private String topCenterUrl;
	private Image topRightImage;
	private String topRightUrl;
	private Image middleLeftImage;
	private String middleLeftUrl;
	private Image middleRightImage;
	private String middleRightUrl;
	private Image bottomLeftImage;
	private String bottomLeftUrl;
	private Image bottomCenterImage;
	private String bottomCenterUrl;
	private Image bottomRightImage;
	private String bottomRightUrl;
	private boolean isLoaded;
	private int borderWidth;
	
	public MultiImageBorder(int borderWidth, Image topLeft, Image topCenter, Image topRight,
	                    Image middleLeft, Image middleRight,
	                    Image bottomLeft, Image bottomCenter, Image bottomRight)
	{
		super( borderWidth, borderWidth, borderWidth, borderWidth );
		this.borderWidth = borderWidth;
		this.topLeftImage = topLeft;
		this.topCenterImage = topCenter;
		this.topRightImage = topRight;
		this.middleLeftImage = middleLeft;
		this.middleRightImage = middleRight;
		this.bottomLeftImage = bottomLeft;
		this.bottomCenterImage = bottomCenter;
		this.bottomRightImage = bottomRight;
		this.isLoaded = true;
	}

	public MultiImageBorder(int borderWidth, String topLeft, String topCenter, String topRight,
	                    String middleLeft, String middleRight,
	                    String bottomLeft, String bottomCenter, String bottomRight)
	{
		super( borderWidth, borderWidth, borderWidth, borderWidth );
		this.borderWidth = borderWidth;
		this.topLeftUrl = topLeft;
		this.topCenterUrl = topCenter;
		this.topRightUrl = topRight;
		this.middleLeftUrl = middleLeft;
		this.middleRightUrl = middleRight;
		this.bottomLeftUrl = bottomLeft;
		this.bottomCenterUrl = bottomCenter;
		this.bottomRightUrl = bottomRight;
	}

	public void paint(int x, int y, int width, int height, Graphics g)
	{
		if (!this.isLoaded) {
			try {
				this.topLeftImage = StyleSheet.getImage(this.topLeftUrl, this, false);
				this.topCenterImage = StyleSheet.getImage(this.topCenterUrl, this, false);
				this.topRightImage = StyleSheet.getImage(this.topRightUrl, this, false);
				this.middleLeftImage = StyleSheet.getImage(this.middleLeftUrl, this, false);
				this.middleRightImage = StyleSheet.getImage(this.middleRightUrl, this, false);
				this.bottomLeftImage = StyleSheet.getImage(this.bottomLeftUrl, this, false);
				this.bottomCenterImage = StyleSheet.getImage(this.bottomCenterUrl, this, false);
				this.bottomRightImage = StyleSheet.getImage(this.bottomRightUrl, this, false);
			} catch (IOException e) {
				//#debug error
				System.out.println( "unable to load image " + e );
			}
			this.isLoaded = true;
		}
		
		if (this.topLeftImage != null) {
			g.drawImage(this.topLeftImage, x, y, Graphics.BOTTOM | Graphics.RIGHT );
		}

		if (this.topCenterImage != null) {
			int w = this.topCenterImage.getWidth();			
			int num = (width - w) / w + 1;
			for (int i = 0; i <= num; i++) {
				g.drawImage(this.topCenterImage, x + i * w, y, Graphics.BOTTOM | Graphics.LEFT );
			}
		}
		
		if (this.topRightImage != null) {
			g.drawImage(this.topRightImage, x + width, y, Graphics.BOTTOM | Graphics.LEFT );
		}

		if (this.middleLeftImage != null) {
			int w = this.middleLeftImage.getHeight();			
			int num = (height -  w) / w + 1;
			for (int i = 0; i <= num; i++) {
				g.drawImage(this.middleLeftImage, x, y + i * w, Graphics.TOP | Graphics.RIGHT );
			}
		}
		
		if (this.middleRightImage != null) {
			int w = this.middleRightImage.getHeight();			
			int num = (height - w) / w + 1;
			for (int i = 0; i <= num; i++) {
				g.drawImage(this.middleRightImage, x + width, y + i * w, Graphics.TOP | Graphics.LEFT );
			}
		}
		
		if (this.bottomLeftImage != null) {
			g.drawImage(this.bottomLeftImage, x, y + height, Graphics.TOP | Graphics.RIGHT );
		}
		
		if (this.bottomCenterImage != null) {
			int w = this.bottomCenterImage.getWidth();			
			int num = (width - w) / w + 1;
			for (int i = 0; i <= num; i++) {
				g.drawImage(this.bottomCenterImage, x + i * w, y + height, Graphics.TOP | Graphics.LEFT );
			}
		}

		if (this.bottomRightImage != null) {
			g.drawImage(this.bottomRightImage, x + width, y + height, Graphics.TOP | Graphics.LEFT );
		}
	}
}
