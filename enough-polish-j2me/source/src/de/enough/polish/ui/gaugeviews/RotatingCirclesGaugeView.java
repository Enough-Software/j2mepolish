//#condition polish.usePolishGui
/*
 * Created on Aug 25, 2008 at 9:15:33 PM.
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
package de.enough.polish.ui.gaugeviews;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Style;
import de.enough.polish.util.DrawUtil;

/**
 * <p>Rotates circles</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 * @author Achint Chaudhary
 */
public class RotatingCirclesGaugeView extends ItemView {

	private final int[] xCord;	// Array to hold X coordinates of the circle
	private final int[] yCord;	// Array to hold Y coordinates of the circle
	int arrayIndex = 0;		// Counter for the values stored in the xCord and yCord arrays

		
	// This array stores Sine values, which are used in program to calculate the circular path

	private static int[] sinetable =    {   
			0,  286,  572,  857, 1143, 1428, 1713, 1997, 2280, 2563,
	        2845, 3126, 3406, 3686, 3964, 4240, 4516, 4790, 5063, 5334,
	        5604, 5872, 6138, 6402, 6664, 6924, 7182, 7438, 7692, 7943,
	        8192, 8438, 8682, 8923, 9162, 9397, 9630, 9860,10087,10311,
			10531,10749,10963,11174,11381,11585,11786,11982,12176,12365,
			12551,12733,12911,13085,13255,13421,13583,13741,13894,14044,
			14189,14330,14466,14598,14726,14849,14968,15082,15191,15296,
			15396,15491,15582,15668,15749,15826,15897,15964,16026,16083,
			16135,16182,16225,16262,16294,16322,16344,16362,16374,16382,16384
	};

	private int sineval = 0;
	private int cosval  = 0;
	private int colorStart;
	private Dimension maxWidth;
	private int colorEnd = 0x666666;
	private int[] colors;
	
	/**
	 * Creates a new gauge view
	 */
	public RotatingCirclesGaugeView() {
		this.xCord = new int[12];
		this.yCord = new int[12];
	}

	/*
	* This method sets Sin and Cosin values for the angle given
	*/
	private void setSinCosValues(int currentAngle)
	{
		if (currentAngle > 360)
		{
			currentAngle-=360;
			if (currentAngle >=360)
			{
				this.sineval = 0;
				this.cosval  = 0;
				return;
			}
		}

		if (currentAngle >= 270)
		{
			currentAngle-=360;
			currentAngle = 0 - currentAngle;
			this.sineval = 0 - sinetable[currentAngle];
			currentAngle-=90;
			currentAngle = 0 - currentAngle;
			this.cosval = sinetable[currentAngle];
			return;
		}
		
		if (currentAngle >= 180)
		{
			currentAngle-=180;
			this.sineval = 0 - sinetable[currentAngle];
			currentAngle-=90;
			currentAngle = 0 - currentAngle;
			this.cosval = 0 - sinetable[currentAngle];
			return;         
		}

		if (currentAngle >= 90)
		{
			currentAngle-=180;
			currentAngle = 0 - currentAngle;
			this.sineval = sinetable[currentAngle];
			currentAngle-=90;
			currentAngle = 0 - currentAngle;
			this.cosval = 0 - sinetable[currentAngle];
			return;         
		}

		if (currentAngle < 90)
		{
			this.sineval = sinetable[currentAngle];
			currentAngle-=90;
			currentAngle = 0 - currentAngle;
			this.cosval = sinetable[currentAngle];
			return;         
		}

	}

	

	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	protected void setStyle(Style style, boolean resetStyle)
	{
		super.setStyle(style, resetStyle);
		boolean changed = false;
		//#if polish.css.gauge-rotating-circles-start-color
			Color colStart = style.getColorProperty("gauge-rotating-circles-start-color");
			if (colStart != null) {
				this.colorStart = colStart.getColor();
				changed = true;
			}
		//#endif
		//#if polish.css.gauge-rotating-circles-end-color
			Color colEnd = style.getColorProperty("gauge-rotating-circles-end-color");
			if (colEnd != null) {
				this.colorEnd = colEnd.getColor();
				changed = true;
			}
		//#endif
		if (changed) {
			this.colors = DrawUtil.getGradient(this.colorStart, this.colorEnd, 6);
		}
		//#if polish.css.max-width
			Dimension widthInt = (Dimension) style.getObjectProperty("max-width");
			if (widthInt != null) {
				this.maxWidth = widthInt;
			}
		//#endif
	}

	protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) {
		if (this.maxWidth != null) {
			this.contentWidth = this.maxWidth.getValue(availWidth);
			this.contentHeight = this.maxWidth.getValue(availWidth);
		} else {
			this.contentWidth=availWidth / 2;
			this.contentHeight=availWidth / 2;
		}
		int max = 0;
		int angle = 0;
		int x = 100; // Initial x position of the centre of the circle
		int y = 100; // Initial y position of the centre of the circle
		int xOffset=0;	// This variable stores new X co-ordinate for the circle
		int yOffset=0;		// This variable stores new Y co-ordinate for the circle
		for(int i=0; i<12; i++)
		{
			if(angle>=360)
			{
				angle = 0;
				x = 98;
				y = 98;
			}

			setSinCosValues(angle);

			xOffset=this.sineval*20;
			yOffset=this.cosval*20;

			x += (xOffset>>14);
			y += (yOffset>>14);

			this.xCord[i] = x;
			this.yCord[i] = y;
			
			//System.out.println("x=" + x + ", y=" + y);
			if ( x > max ) {
				max = x;
			}
			if ( y > max ) {
				max = y;
			}

			angle += 60;
		}
		for(int i=0; i<12; i++)
		{
			this.xCord[i] = ((this.xCord[i] * this.contentWidth) / max) - 50; 
			this.yCord[i] = ((this.yCord[i] * this.contentWidth) / max) - 50; 
		}
		if (this.colors == null) {
			this.colors = DrawUtil.getGradient(this.colorStart, this.colorEnd, 6);
		}
	}

	

	protected void paintContent(Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g) {
		g.setColor( this.colors[0] );
		g.fillArc(x + xCord[arrayIndex],   y + yCord[arrayIndex],16,16,0,360);
		g.drawArc(x + xCord[arrayIndex],   y + yCord[arrayIndex],16,16,0,360);
		
		g.setColor( this.colors[1] );
		g.fillArc(x + xCord[arrayIndex+1], y + yCord[arrayIndex+1],13,13,0,360);
		g.drawArc(x + xCord[arrayIndex+1], y + yCord[arrayIndex+1],13,13,0,360);
		
		g.setColor( this.colors[2] );
		g.fillArc(x + xCord[arrayIndex+2], y + yCord[arrayIndex+2],10,10,0,360);
		g.drawArc(x + xCord[arrayIndex+2], y + yCord[arrayIndex+2],10,10,0,360);
		
		g.setColor( this.colors[3] );
		g.fillArc(x + xCord[arrayIndex+3], y + yCord[arrayIndex+3],7,7,0,360);
		g.drawArc(x + xCord[arrayIndex+3], y + yCord[arrayIndex+3],7,7,0,360);
		
		g.setColor( this.colors[4] );
		g.fillArc(x + xCord[arrayIndex+4], y + yCord[arrayIndex+4],5,5,0,360);
		g.drawArc(x + xCord[arrayIndex+4], y + yCord[arrayIndex+4],5,5,0,360);
		
		g.setColor( this.colors[5] );
		g.fillArc(x + xCord[arrayIndex+5], y + yCord[arrayIndex+5],2,2,0,360);
		g.drawArc(x + xCord[arrayIndex+5], y + yCord[arrayIndex+5],2,2,0,360);
	}
	
	

	public boolean animate() {
		this.arrayIndex++;
		if(this.arrayIndex>5) {
			this.arrayIndex=0;
		}
		return true;
	}
	
	/**
	 * Determines whether this view is valid for the given item.
	 * @return true when this view can be applied
	 */
	protected boolean isValid(Item parent, Style style) {
		return parent instanceof Gauge;
	}
	

}

