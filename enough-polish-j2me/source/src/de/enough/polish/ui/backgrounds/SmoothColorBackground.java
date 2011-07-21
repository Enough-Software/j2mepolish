//#condition polish.usePolishGui

/*
 * Created on 22.08.2005 at 10:34:28.
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

import de.enough.polish.ui.Background;

public class SmoothColorBackground extends Background {
	private int color,targetColor;
	private final int stroke;
	public SmoothColorBackground(int color, int targetColor,int stroke){
		this.color = color;
		this.targetColor = targetColor;
		this.stroke = stroke;
		
	}
	
	
	synchronized private int colorValue(int value1,int value2,int sumX,int sumY){	
		if(value1 != value2){
			int woh;
			if(sumX <= sumY){
				woh = sumX;
			}else{
				woh = sumY;
			}
			if(value1 < value2){
				int sum =   (value2-value1 ) / woh;
				value1+= sum;
			}
			else{
				int sum =   (value1 -value2) / woh;
				value1-= sum;
			}
		}		
		return value1;
	}
	
	
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setStrokeStyle(this.stroke);
		int startX = x;
		int startY = y;
		g.setColor(000000);
		g.drawRect(x+1,y+1,width,height);
		int red,green, blue,red2,green2,blue2;
		red = (0x00FF & (this.color >>> 16));	
		green = (0x0000FF & (this.color >>> 8));
		blue = this.color & (0x000000FF );
		red2 = (0x00FF & (this.targetColor >>> 16));
		green2 = (0x0000FF & (this.targetColor >>> 8));
		blue2 = this.targetColor & (0x000000FF);
		do{		
			int sumX = width-(x-startX);
			int sumY = height-(y-startY);
			g.setColor(red,green,blue);
			g.drawRect(x,y,sumX,sumY);
			x ++;
			y ++;		
			red = this.colorValue(red,red2,sumX,sumY);
			green = this.colorValue(green,green2,sumX,sumY);
			blue = this.colorValue(blue,blue2,sumX,sumY);
		}
		while((red != red2 || green != green2 || blue != blue2) && x <= (width+startX) && y <= (height+startY));
		
		g.setStrokeStyle( Graphics.SOLID );
	}
}
