//#condition polish.usePolishGui && polish.hasFloatingPoint
/*
 * Created on Jan 23, 2007 at 3:42:30 PM.
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



import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.Style;

/**
 * <p>Shows a tachometer visualization of a Gauge.</p>
 * <p>
 *    The tachometer has 3 different sections: too low, normal, too high which can be customized by setting colors and the areas.
 *    It's also possible to specify a start value and to simulate floating point support by specifying a divide factor.
 *    <br />
 *    Supported attributes:
 * <p>
 * <ul>
 * 	<li><b>gauge-tachometer-startvalue</b>: the lowest possible value, default is 0</li>
 * 	<li><b>gauge-tachometer-section1-start</b>: the value at which the first section starts, default is the startvalue (0).</li>
 * 	<li><b>gauge-tachometer-section2-start</b>: the value at which the second section starts, default is the value range divided by 3.</li>
 * 	<li><b>gauge-tachometer-section3-start</b>: the value at which the third section starts, default is the value range multiplied by 2/3.</li>
 * 	<li><b>gauge-tachometer-section1-color</b>: the color of the first section</li>
 * 	<li><b>gauge-tachometer-section2-color</b>: the color of the second section</li>
 * 	<li><b>gauge-tachometer-section3-color</b>: the color of the third section</li>
 * 	<li><b>gauge-tachometer-clockface-color</b>: the outer color of the tachometer, defaults to black</li>
 * 	<li><b>gauge-tachometer-needle-color</b>: the color of the tachometer needle/pointer/indicator, defaults to red.</li>
 * 	<li><b>gauge-tachometer-factor</b>: an integer factor by which all given values are divided for simulating floating point. A factor of 10 and a value of 14 will result in a shown value of 1.4, for example.</li>
 * </ul>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Jan 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @author Tim Muders
 */
public class TachometerGaugeView extends ItemView {

	private int startValue;
	private int maxValue; // the value range is between 0..maxValue
	private int factor = 1; // the factor by which the values should be divided for simulating floating point
	
	private int section1Start;
	private int section2Start;
	private int section3Start;
	private int section1Color = -1; // -1 means this section is not visible
	private int section2Color = -1; // -1 means this section is not visible
	private int section3Color = -1; // -1 means this section is not visible
	
	private int clockfaceColor = 0xFF0000; // black
	private int innerColor = 0xFFFFFF; // white
	private int needleColor = 0x000000; // red

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) {
		Gauge gauge = (Gauge) parent;
		this.maxValue = gauge.getMaxValue(); 
		
		int range = this.maxValue - this.startValue;
		if (this.section2Start == 0) {
			this.section2Start = range / 3;
		}
		if (this.section3Start == 0) {
			this.section3Start = (range * 2) / 3;
		}
//		this.contentWidth = ( lineWidth * 2 ) / 3;
//		System.out.println("firstline:"+firstLineWidth+";lineWidth:"+lineWidth+";contentWidth:"+this.contentWidth);
		this.contentWidth = 120 ;
		this.contentHeight = this.contentWidth;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintContent(de.enough.polish.ui.Item, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder,
			int rightBorder, Graphics g) 
	{
		int itemWidth = this.contentWidth;
		int itemHeight = this.contentHeight;
		int widthLine = x + itemWidth;
		int heightLine = y + itemHeight;
		Gauge gauge = (Gauge)parent;
		int centerX = x + itemWidth / 2;
		int centerY = y + itemWidth / 2;
		int innerCircleRadius = this.contentWidth / 10;
		g.setColor( this.clockfaceColor );
//		g.drawRect(x, y, itemWidth, itemHeight);
//		g.drawArc(centerX - (innerCircleRadius ), centerY- (innerCircleRadius ), innerCircleRadius * 2, innerCircleRadius * 2, 0, 360 );
		g.drawArc(x , y, itemWidth, itemHeight, 0, 360 );
		int innerStartX = x + innerCircleRadius;
		int innerStartY = y + innerCircleRadius;
		int innerWidth = itemWidth - innerCircleRadius *2;
		int innerHeight = itemHeight - innerCircleRadius *2;
		int pointerLength = (innerWidth /2)-(innerWidth /8);
//		g.drawArc(innerStartX, innerStartY, innerWidth , innerHeight , 315, 270 );
//		for (int i = 1; i < 6; i++) {
//			g.setColor(0xFF00CC);
//			g.drawArc(innerStartX + 1*i, innerStartY +1*i, innerWidth-2*i , innerHeight-2*i , 315, 90 );
//			g.setColor(0xFFFFCC);
//			g.drawArc(innerStartX + 1*i, innerStartY +1*i, innerWidth-2*i , innerHeight-2*i , 405, 90 );
//			g.setColor(0xFF0000);
//			g.drawArc(innerStartX + 1*i, innerStartY +1*i, innerWidth-2*i , innerHeight-2*i , 135, 90 );
//		}
		g.setColor( this.clockfaceColor );
		Font font = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN,Font.SIZE_SMALL);
		int startValueStringWidth = font.stringWidth(""+this.startValue);
		int maxValueStringWidth = font.stringWidth(""+this.maxValue);
		int gaugeValueStringWidth = font.stringWidth(""+gauge.getValue());
		g.setFont(font);
		g.drawString(""+this.startValue, centerX / 2 + startValueStringWidth,  innerStartY + innerHeight - font.getHeight(), 0);
		g.drawString(""+this.maxValue, centerX + maxValueStringWidth,  innerStartY + innerHeight - font.getHeight(), 0);
		g.drawString(""+gauge.getValue(), centerX - gaugeValueStringWidth/2 ,  innerStartY + innerHeight - font.getHeight()*2, 0);
		g.setColor( this.needleColor );
		int value = gauge.getValue();
		double valuePercent;
		int degree ;
		double degreeCos,degreeSin;
		int angleCos,angleSin,newX,newY;
		valuePercent = ((double)value / (double)this.maxValue)*100 ;
		degree = (int) (225 - (valuePercent*2.7));
		degreeCos = Math.cos(Math.PI*degree/180);
		degreeSin = Math.sin(Math.PI*degree/180);
		angleCos = (int)( degreeCos * pointerLength);
		angleSin = (int)( degreeSin * pointerLength);
//		newX = (angleCos / pointerLength)*100;
//		newY = (angleSin / pointerLength)*100;
		newX = centerX + (angleCos);
		newY = centerY + (-angleSin);
		g.drawLine( centerX, centerY, newX, newY);
//		for (int i = -1; i < 3; i++) {
//			double pi= Math.PI;
//			double xAbstand=120*(Math.cos(pi /(double)8 - (double)1 *pi/6));
//			double yAbstand=120*(Math.sin(pi /(double)8 - (double)1 *pi/6)); 
//			int xc = centerX + (int)xAbstand;
//			int yc = centerY - (int)yAbstand;
//			int xe = newX + (int)xAbstand;
//			int ye = newY - (int)yAbstand;
////			g.drawLine( centerX, centerY, newX, newY);
//			g.drawLine( centerX+i, centerY+i, newX+i, newY+i);
//			g.drawLine( xc, yc, xc, yc);
//			g.drawLine( xe, ye, xe, ye);
////			g.drawLine( centerX, centerY, xe, ye);
//		}
//		System.out.println("--------------------");
		//draws ticks
		int startX, startY, endX, endY, tickLength = pointerLength + 10;
//		for (int i = 225; i >= -45; i-=45) {
		for (int i = 225; i >= -45; i-=15) {
			degree = i;
			degreeCos = Math.cos(Math.PI*degree/180);
			degreeSin = Math.sin(Math.PI*degree/180);
			angleCos = (int)( degreeCos * tickLength);
			angleSin = (int)( degreeSin * tickLength);
			startX = centerX + (angleCos);
			startY = centerY + (-angleSin);
			if(i != 90 && i != 225 && i != -45 && i != 0 && i != 180 && i != 45 && i != 135){
				angleCos = (int)( degreeCos * (tickLength -4));
				angleSin = (int)( degreeSin * (tickLength -4));
			}else{
				angleCos = (int)( degreeCos * (tickLength -10));
				angleSin = (int)( degreeSin * (tickLength -10));
			}
			endX = centerX + (angleCos);
			endY = centerY + (-angleSin);
			g.drawLine( startX , startY, endX , endY);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		
		//#if polish.css.gauge-tachometer-startvalue
			Integer startValueInt = style.getIntProperty("gauge-tachometer-startvalue");
			if (startValueInt != null) {
				this.startValue = startValueInt.intValue();
			}
		//#endif
		//#if polish.css.gauge-tachometer-section1-start
			Integer section1StartInt = style.getIntProperty("gauge-tachometer-section1-start");
			if (section1StartInt != null) {
				this.section1Start = section1StartInt.intValue();
			}
		//#endif
		//#if polish.css.gauge-tachometer-section2-start
			Integer section2StartInt = style.getIntProperty("gauge-tachometer-section2-start");
			if (section2StartInt != null) {
				this.section2Start = section2StartInt.intValue();
			}
		//#endif
		//#if polish.css.gauge-tachometer-section3-start
			Integer section3StartInt = style.getIntProperty("gauge-tachometer-section3-start");
			if (section3StartInt != null) {
				this.section3Start = section3StartInt.intValue();
			}
		//#endif
		//#if polish.css.gauge-tachometer-section1-color
			Color section1ColorObj = style.getColorProperty("gauge-tachometer-section1-color");
			if (section1ColorObj != null) {
				this.section1Color = section1ColorObj.getColor();
			}
		//#endif
		//#if polish.css.gauge-tachometer-section2-color
			Color section2ColorObj = style.getColorProperty("gauge-tachometer-section2-color");
			if (section2ColorObj != null) {
				this.section2Color = section2ColorObj.getColor();
			}
		//#endif
		//#if polish.css.gauge-tachometer-section3-color
			Color section3ColorObj = style.getColorProperty("gauge-tachometer-section3-color");
			if (section3ColorObj != null) {
				this.section3Color = section3ColorObj.getColor();
			}
		//#endif
		//#if polish.css.gauge-tachometer-inner-color
			Color colorInnerObj = style.getColorProperty("gauge-tachometer-inner-color");
			if (colorInnerObj != null) {
				this.innerColor = colorInnerObj.getColor();
			}
		//#endif
		//#if polish.css.gauge-tachometer-clockface-color
			Color colorOuterObj = style.getColorProperty("gauge-tachometer-clockface-color");
			if (colorOuterObj != null) {
				this.clockfaceColor = colorOuterObj.getColor();
			}
		//#endif
		//#if polish.css.gauge-tachometer-needle-color
			Color colorNeedleObj = style.getColorProperty("gauge-tachometer-needle-color");
			if (colorNeedleObj != null) {
				this.needleColor = colorNeedleObj.getColor();
			}
		//#endif
		//#if polish.css.gauge-tachometer-factor
			Integer factorInt = style.getIntProperty("gauge-tachometer-factor");
			if (factorInt != null) {
				this.factor = factorInt.intValue();
			}
		//#endif
	}
	
	/**
	 * Determines whether this view is valid for the given item.
	 * @return true when this view can be applied
	 */
	protected boolean isValid(Item parent, Style style) {
		return parent instanceof Gauge;
	}
	
	

}