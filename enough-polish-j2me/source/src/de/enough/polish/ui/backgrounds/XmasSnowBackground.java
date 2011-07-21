//#condition polish.usePolishGui
/*
 * Created on 06.12.2005 at 11:38:02.
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

import java.util.Random;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;

public class XmasSnowBackground extends Background {
	private boolean isRunning = true;
	private int[] x,y,z;
	private int numberOfFlakes,color,maxFlakeSize;
	private transient final Random rand = new Random();
	private int flakeColor = 0xffffff;
	private int lastWidth;
	
	public XmasSnowBackground() {
		// just have rand completed
	}
	
	public XmasSnowBackground(int color, int snowFlakesColor, int maxSnowFlakeSize, int numberOfFlakes) {
		this( color, null, 100, 100, maxSnowFlakeSize, numberOfFlakes );
		this.flakeColor = snowFlakesColor;
	}
	public XmasSnowBackground(int color, String url, int width, int height, int maxSnowFlakeSize, int numberOfFlakes) {
		super();
		this.color = color;
		this.maxFlakeSize = maxSnowFlakeSize;
		this.numberOfFlakes = numberOfFlakes;
		this.x = new int[numberOfFlakes];
		this.y = new int[numberOfFlakes];
		this.z = new int[numberOfFlakes];
		int i = 0;
		while(i < numberOfFlakes){
			this.x[i] = nextRandInt(width);
			this.y[i] = nextRandInt(height);
			this.z[i] = nextRandInt(maxSnowFlakeSize);
			i++;
		}
	}
	
	
	/**
	 * @param width
	 * @return
	 */
	private int nextRandInt(int max)
	{
		int result;
		//#if polish.hasFloatingPoint || polish.cldc1.1
			result =  this.rand.nextInt(max);
		//#else
			result =  this.rand.nextInt();
			result = result < 0 ? (-result % max) : (result % max);
		//#endif
		return result;
	}

	public boolean animate() {
		return this.isRunning;
	}
	
	public void paint(int x, int y, int width, int height, Graphics g) {
		this.lastWidth = width;
		if (this.color != Item.TRANSPARENT) {
			g.setColor(this.color);
			g.fillRect(x,y,width,height);
		}
		g.setColor(this.flakeColor );
		int i = 0;
		while(i < this.numberOfFlakes){
//			this.x[i]++;
			if(this.y[i] < height && this.x[i] < width){
				int z1 = this.z[i];
				this.y[i]+= z1;	
				this.x[i]+=1;
			}
			else{
				this.y[i] = 0;
				this.x[i] = nextRandInt(width); //Math.abs( this.rand.nextInt() % width );
				this.z[i] = nextRandInt(this.maxFlakeSize); //Math.abs( this.rand.nextInt() % this.maxFlakeSize );
			}
//			System.out.print("X:"+this.x[i]+";Y:"+this.y[i]+";Z:"+this.z[i]+";width"+width+";height"+height+"\n");
			int size = this.z[i];
//			if (i==0) {
//				System.out.println("x=" + this.x[i] + ", y=" + this.y[i]);
//			}
			g.fillRoundRect( x + this.x[i], y + this.y[i],size,size,size,size);
			i++;
		}
	}
	
	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		//#if polish.css.background-snowflakes-color
			Color bgColor = style.getColorProperty("background-snowflakes-color");
			if (bgColor != null) {
				this.color = bgColor.getColor();
			}
		//#endif
		//#if polish.css.background-snowflakes-flake-color
			Color flakeColorObj = style.getColorProperty("background-snowflakes-flake-color");
			if (flakeColorObj != null) {
				this.flakeColor = flakeColorObj.getColor();
			}
		//#endif
		//#if polish.css.background-snowflakes-max-flake-size
			Integer maxSizeInt = style.getIntProperty("background-snowflakes-max-flake-size");
			if (maxSizeInt != null) {
				this.maxFlakeSize = maxSizeInt.intValue();
			}
		//#endif
		//#if polish.css.background-snowflakes-number-of-flakes
			Integer numberOfFlakesInt = style.getIntProperty("background-snowflakes-number-of-flakes");
			if (numberOfFlakesInt != null) {
				int number = numberOfFlakesInt.intValue();
				if (number != this.numberOfFlakes) {
					int min;
					boolean increase = (number > this.numberOfFlakes);
					if (increase) {
						min = this.numberOfFlakes;
					} else {
						min = number;
					}
					int[] xCopy = new int[number];
					System.arraycopy(this.x, 0, xCopy, 0, min );
					int[] yCopy = new int[number];
					System.arraycopy(this.y, 0, yCopy, 0, min );
					int[] zCopy = new int[number];
					System.arraycopy(this.z, 0, zCopy, 0, min );
					if (increase) {
						for (int i=min; i<number; i++) {
							yCopy[i] = 0;
							xCopy[i] = nextRandInt( this.lastWidth );
							zCopy[i] = nextRandInt( this.maxFlakeSize );
						}
					}
					this.x = xCopy;
					this.y = yCopy;
					this.z = zCopy;
					this.numberOfFlakes = number;
				}
				
			}
		//#endif
	}
//#endif
}
