//#condition polish.midp2 && polish.usePolishGui
/*
 * Created on 09.01.2006 at 16:56:54.
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


import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.ImageUtil;

/**
 * The Picure Browser paints 5 in one row by scaling down the four outer ones and having a suitable large image in the middle.
 * 
 * @author Tim Muders
 *
 */
public class PictureBrowserItem
//#if polish.LibraryBuild
	extends FakeCustomItem
//#else
	//# extends Item 
//#endif
{
	private Image[] thumbnails;
	private int[][] thumbnailsRGBData;
	private int selectedThumbIndex = 0;
	private int thumbnailWidth, thumbnailHeight, selectedWidth, selectedHeight;
	private String[] urls;
	
	
	/**
	 * Creates a new, <code>PictureBrowserItem</code>.
	 * 
	 * @param label the label
	 * @param thumbnails the images for the menu
	 * @param urls the urls for the target image
	 * @param scaleFactor the skale factor for the thumbs that are displayed on the side of the item. 
	 *        Note that the first given thumb is used for the calculation of the sizes for all images.
	 *        The complete width off the item are 4*firstimage.getWidth()*scaleFactor/100 + 2*padding-horizontal
	 */
	public PictureBrowserItem( String label, Image[] thumbnails, String[] urls, int scaleFactor ){
		this( label, thumbnails, urls, scaleFactor, null );
	}	
	
	/**
	 * Creates a new, <code>PictureBrowserItem</code>.
	 * 
	 * @param label the label
	 * @param thumbnails the images for the menu
	 * @param urls the urls for the target image
	 * @param scaleFactor the skale factor for the thumbs that are displayed on the side of the item. 
	 *        Note that the first given thumb is used for the calculation of the sizes for all images.
	 *        The complete width off the item are 4*firstimage.getWidth()*scaleFactor/100 + 2*padding-horizontal
	 * @param style the design settings
	 */
	public PictureBrowserItem( String label, Image[] thumbnails, String[] urls, int scaleFactor, Style style ){
		super( label, 0, INTERACTIVE, style );
		this.thumbnails = thumbnails;
		this.urls = urls;
		Image thumbnail = thumbnails[0];
		this.thumbnailWidth = (thumbnail.getWidth() * scaleFactor) / 100;
		this.thumbnailHeight = (thumbnail.getHeight() * scaleFactor) / 100;
		this.selectedWidth = this.thumbnailWidth*2 + this.paddingVertical;
		this.selectedHeight = this.thumbnailHeight*2 + this.paddingVertical;
		int size = this.thumbnailWidth * this.thumbnailHeight;
		this.thumbnailsRGBData = new int [thumbnails.length][];
		for(int i = 0; i < thumbnails.length; i++){
			int w = thumbnails[i].getWidth();
			int h = thumbnails[i].getHeight();
			int[] rgbData = new int[w*h];
			this.thumbnails[i].getRGB(rgbData, 0, w, 0, 0, w, h );
			if (i == this.selectedThumbIndex){
				this.thumbnailsRGBData[i] = new int[this.selectedHeight*this.selectedWidth];
				ImageUtil.scale(rgbData, this.selectedWidth, this.selectedHeight,w,h,this.thumbnailsRGBData[i]);
			} else {
				this.thumbnailsRGBData[i] = new int[size];
				ImageUtil.scale(rgbData, this.thumbnailWidth, this.thumbnailHeight,w,h,this.thumbnailsRGBData[i]);
			}
		}
	}

	/**
	 * Retrieves the number of stored thumbnails
	 * 
	 * @return the number of stored thumbnails
	 */
	public int getNumberOfThumbnails(){
		return this.thumbnailsRGBData.length;
	}
	
	
	/**
	 * Changes the Selected Thumb with the given int i,
	 * and scale the old Selected back to a normal Thumb.
	 * 
	 * @param index  next Selected Thumb
	 */
	public void setSelectedThumbIndex(int index){
		int[] newSelectedRbgData = this.thumbnailsRGBData[index];
		if ( (index == this.selectedThumbIndex) && (newSelectedRbgData != null) ) {
			// ignore
			return;
		}
		// scale up selected thumb:
		Image thumbnail = this.thumbnails[index];
		int width = thumbnail.getWidth();
		int height = thumbnail.getHeight();
		int[] rgbData = new int[width * height];
		thumbnail.getRGB(rgbData, 0, width, 0, 0, width, height );
		newSelectedRbgData = new int[this.selectedHeight*this.selectedWidth];
		ImageUtil.scale(rgbData, this.selectedWidth, this.selectedHeight,width,height,newSelectedRbgData);
		//	scale down previous thumb:
		if (this.thumbnailsRGBData[this.selectedThumbIndex] != null) {
			Image selectedThumb = this.thumbnails[this.selectedThumbIndex];
			width = selectedThumb.getWidth();
			height = selectedThumb.getHeight();
			rgbData = new int[width * height];
			selectedThumb.getRGB(rgbData, 0, width, 0, 0, width, height );
			this.thumbnailsRGBData[this.selectedThumbIndex] = new int[this.thumbnailWidth*this.thumbnailHeight];
			ImageUtil.scale(rgbData, this.thumbnailWidth, this.thumbnailHeight,width,height,this.thumbnailsRGBData[this.selectedThumbIndex]);	
		}
		this.thumbnailsRGBData[index] = newSelectedRbgData;
		this.selectedThumbIndex = index;
		this.repaint();
	}
	
	/* 
	 * Paints the PictureBrowserItem.
	 *
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
//		int translateX = g.getTranslateX(); 
//		int translateY = g.getTranslateY();
//		g.translate(-translateX, -translateY );

		int index = this.selectedThumbIndex - Math.min( 2,  this.thumbnails.length - 1 );
		if (index < 0) {
			index = this.thumbnails.length + index;
		}
		int number = Math.min( 5, this.thumbnails.length );
//		int x = translateX;
//		int y = translateY;
		int originalY = y;
		for (int i=0; i<number; i++ ) {
			if (index == this.selectedThumbIndex) {
				y = originalY;
				x += this.thumbnailWidth + this.paddingHorizontal;
				DrawUtil.drawRgb( this.thumbnailsRGBData[index], x, y, this.selectedWidth, this.selectedHeight, false, g );
				
				x += this.selectedWidth + this.paddingHorizontal;
			} else {
				DrawUtil.drawRgb( this.thumbnailsRGBData[index], x, y, this.thumbnailWidth, this.thumbnailHeight, false, g );
				y += this.thumbnailHeight + this.paddingVertical;
			}
			index++;
			if (index == this.thumbnails.length) {
				index = 0;
			}
		}
//		g.translate(translateX, translateY);  

	}

	
	/**
	 * Sets the Font.
	 * 
	 * @param style -  the Font to be painted
	 */
	public void setStyle( Style style ) {
		super.setStyle( style );
		int newSelectedHeight = this.thumbnailHeight * 2 + this.paddingVertical;
		int newSelectedWidth = this.thumbnailWidth * 2 + this.paddingVertical;
		if (newSelectedHeight != this.selectedHeight || newSelectedWidth != this.selectedWidth ) {
			this.selectedWidth = newSelectedWidth;
			this.selectedHeight = newSelectedHeight;
			this.thumbnailsRGBData[ this.selectedThumbIndex ] = null;
			setSelectedThumbIndex( this.selectedThumbIndex );
		}
	}
	
	/**
	 * Changes the Selected Image if interaction:
	 * Canvas.RIGHT
	 * Canvas.LEFT.
	 *
	 * @param keyCode
	 * @param gameAction
	 * @return <code>true</code> if handled, <code>false</code> otherwise 
	 */
//	protected boolean traverse(int dir,int viewportWidth,int viewportHeight,int[] visRect_inout) {
	protected boolean handleKeyPressed( int keyCode, int gameAction ) {
		if(this.thumbnails.length > 1){
		   if (gameAction == Canvas.LEFT) {
			   //			   Left
			   if(this.selectedThumbIndex == 0) {
				   this.setSelectedThumbIndex(this.thumbnails.length-1);
			   } else {
				   this.setSelectedThumbIndex(this.selectedThumbIndex-1);
			   }
			   return true;
		   } else if (gameAction == Canvas.RIGHT) {
			   //			   Right
			   if(this.selectedThumbIndex < this.thumbnails.length-1) {
				   this.setSelectedThumbIndex(this.selectedThumbIndex+1);
			   } else {
				   this.setSelectedThumbIndex(0);
			   }
			   
			   return true;
		   }
		}
		return false;
	}

	/**
	 * Returns the Array Position of the Selected Thumb.
	 * 
	 * @return int the Selected Thumb number
	 */
	public int getSelectedThumbIndex() {
		return this.selectedThumbIndex;
	}

	/**
	 * returns the url as an String.
	 * 
	 * @param index - the index where the url is Placed in the String Array urls
	 * @return the url of that image or null when no URLs have been defined
	 */
	public String getUrl(int index) {
		if (this.urls == null) {
			return null;
		}
		return this.urls[index];
	}

	protected String createCssSelector() {
		return "picturebrowser";
	}

	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		this.contentWidth = this.thumbnailWidth * 2 + 2 * this.paddingHorizontal + this.selectedWidth;
		this.contentHeight = this.thumbnailHeight * 2 + this.paddingVertical;
	}

}