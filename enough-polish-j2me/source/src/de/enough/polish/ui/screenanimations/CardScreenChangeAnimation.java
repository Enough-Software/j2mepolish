//#condition polish.usePolishGui && polish.midp2

/*
 * Created on 14.09.2005 at 15:30:15.
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
package de.enough.polish.ui.screenanimations;

import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;

public class CardScreenChangeAnimation extends ScreenChangeAnimation {
	private boolean stillRun = true;
	//the start degrees of the images
	private int degree = 1,lstdegree = 89;
	//the nxtImage to start in screen
	private int xSplitPosition = 0;
	//the rgb - images
	private int[] rgbData ;
	//the height of the columns
	private int[] scaleableHeights;
	//the scale from the row
	private int scaleableWidth,wayForScale,heightScale;
	//#if polish.css.cube-screen-change-animation-background-color
	private int backGroundColor;
	//#endif
	
	public CardScreenChangeAnimation() {
		super();
		this.useLastCanvasRgb = true;
		this.useNextCanvasRgb = true;
	}
	
	protected void onShow(Style style, Display dsplay, int width, int height,
			Displayable lstDisplayable, Displayable nxtDisplayable, boolean isForward  ) 
	{
		super.onShow(style, dsplay, width, height, lstDisplayable, nxtDisplayable, isForward );
		//#if polish.css.cube-screen-change-animation-background-color
		Integer colorInt = style.getIntProperty( "cube-screen-change-animation-background-color" ); 
		if (colorInt != null ) { 
			this.backGroundColor = colorInt.intValue(); 
		}
		//#endif
		//System.out.print("width:"+width+":height:"+height);
		this.xSplitPosition = 0;
//			this.row = width;
		this.degree = 1;
		this.lstdegree = 89;
		this.stillRun = true;
		this.wayForScale = (width *100)/ 90;
		this.heightScale = ((height-((height * 12)/100))*100)/90;
		int size = width * height;
		this.scaleableWidth = width;
		this.scaleableHeights = new int [width];
		for(int i = 0;i < this.scaleableHeights.length;i++){
			this.scaleableHeights[i] = height;
		}
		this.rgbData = new int [size];
		System.arraycopy( this.lastCanvasRgb, 0, this.rgbData, 0, size);
		//lstScreenImage.getRGB(this.rgbData, 0, width, 0, 0, width, height );
	}
	

	
	
	protected boolean animate() {
		int currentX = 0,currentY = 0;
		int length = this.rgbData.length;
		int currentScalableHeight,targetY,verticalShrinkFactorPercent,horizontalScaleFactorPercent,sourceX,targetArrayIndex;
		for(int i = 0; i < length;i++){
			currentX = (currentX + 1) % this.screenWidth;
			if(currentX == 0){
				currentY++;	
			}
			currentScalableHeight = this.scaleableHeights[currentX];
			if(currentScalableHeight < currentY || (this.screenHeight - currentScalableHeight) > currentY )// || row > (this.screenWidth-this.row) || row < this.row)
			{
				//#if polish.css.cube-screen-change-animation-background-color
					this.rgbData[i] = this.backGroundColor;
				//#else
					this.rgbData[i] = 0;
				//#endif
			}
			else
			{
				targetY = currentY - (this.screenHeight - currentScalableHeight);
//				if(c <= 0)c = 1;
				verticalShrinkFactorPercent = (((this.screenHeight-((this.screenHeight-currentScalableHeight)*2))*100)/this.screenHeight);
//				if(u <= 0)u=1;
				if(this.xSplitPosition <= currentX){
					sourceX = currentX - this.xSplitPosition;
//					if(r <= 0)r = 1;
					horizontalScaleFactorPercent = ((this.scaleableWidth*100) / this.screenWidth);
				}else{
					sourceX = currentX;
					horizontalScaleFactorPercent = (((this.xSplitPosition)*100) / this.screenWidth);
				}
//				if(o <= 0)o++;
				targetArrayIndex = ((sourceX*100)/horizontalScaleFactorPercent)+(this.screenWidth * ((targetY*100)/verticalShrinkFactorPercent));
				if(targetArrayIndex >= length)targetArrayIndex = length-1;
				if(targetArrayIndex < 0)targetArrayIndex = 0;
//				this.rgbData[i] = this.rgbbuffer[newI];
				if( this.xSplitPosition > currentX){
					this.rgbData[i] = this.nextCanvasRgb[targetArrayIndex];
				}else{
					this.rgbData[i] = this.lastCanvasRgb[targetArrayIndex];
				}
			}
//			else if( this.row > row){
//				this.rgbData[i] = getColorRGB(true,row,column);
//			}else{
//				this.rgbData[i] = getColorRGB(false,row,column);
//			}
		}
		cubeEffect();
		if(this.lstdegree <= 1) {
			this.stillRun = false;
		}
		return this.stillRun;
	}
	
	
	private void cubeEffect(){		
//		the way to go by degrees in percent
//		the new scalableWidth for the front scaling of the cube
//		if(this.row < this.screenWidth-2)this.row+=2;
		
		this.xSplitPosition = (this.screenWidth -((this.wayForScale * this.lstdegree)/100));
		//System.out.print("row"+this.xSplitPosition+"\n");
		int endOfHeight; int difference; int scale; int sumScale;
		if(this.degree < 90){		
//			if(this.scaleableWidth <= 0)this.scaleableWidth++;
//			this.lstScale = this.scaleableWidth;
//			this.scaleableWidth-=this.row;
			this.degree++;
			this.scaleableWidth = this.screenWidth - this.xSplitPosition;
			endOfHeight =  (this.screenHeight -(this.heightScale * this.degree));
			difference = this.screenHeight + (endOfHeight/100);
			scale = ((this.screenHeight - difference)*100)/this.screenWidth;
			sumScale = scale;	
			int start = this.xSplitPosition+1;
			int finishAnimationSide = this.screenWidth-5;
			int newScale;
				for(int i = start; i < this.screenWidth;i++){
//			this.scaleableHeight[i] = this.screenHeight + (endOfHeight/100);
					if(this.xSplitPosition > finishAnimationSide){
						newScale =0 ;
					}else{
						newScale = this.screenHeight - (scale/100);
						scale = scale + sumScale;
					}
					this.scaleableHeights[i] = newScale;
//					if(newScale <= 0)this.scaleableWidth--;
				}
		}	
			if(this.lstdegree > 1)this.lstdegree--;
			endOfHeight =  (this.screenHeight -(this.heightScale * this.lstdegree));
			difference = this.screenHeight + (endOfHeight/100);
			scale = ((this.screenHeight - difference)*100)/this.screenWidth;
			sumScale = scale;
//			if(this.lstScale <= this.row)this.lstScale = this.row-10;
			int start = this.xSplitPosition+1;
			int newScale;
			for(int i = start; i > 0;i--){			
				if(this.xSplitPosition < 9){
					newScale =0 ;
				}else{
					newScale = this.screenHeight - (scale/100);
					scale = scale + sumScale;
				}
				this.scaleableHeights[i] = newScale;			
			}
	}
	
	public void paintAnimation(Graphics g) {
		g.drawRGB(this.rgbData,0,this.screenWidth,0,0,this.screenWidth,this.screenHeight,false);
	}

}
