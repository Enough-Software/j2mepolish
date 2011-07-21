//#condition polish.usePolishGui
/*
 * Created on 02.02.2007 at 11:22:39.
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
import de.enough.polish.ui.Style;

public class RecLineGaugeView extends ItemView{
	private int width = 5;
	private int height = 5;
	private int number = 4;
	private int selectedColor = 0xFF00FF;
	private int unSelectedColor = 0xFFFFFF;	
	private int valuePosition = 0;
	private long animationInterval = 300;
	private long lastAnimationTime;
	
	public boolean animate() {
		boolean animated = super.animate();
		Gauge gauge = (Gauge)this.parentItem;
		if ( gauge.getMaxValue() == Gauge.INDEFINITE && gauge.getValue() == Gauge.CONTINUOUS_RUNNING ) {
			long time = System.currentTimeMillis();
			if (time - this.lastAnimationTime >= this.animationInterval) {
				this.lastAnimationTime = time;
				int position = this.valuePosition + 1;
				if (position >= this.number) {
					position = 0;
				}
				this.valuePosition = position; 
				animated = true;				
			}
		}
		return animated;
	}
	
	protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) {
		this.contentWidth = (this.width + this.paddingHorizontal) * this.number - (this.paddingHorizontal);
		this.contentHeight = this.height ;
	}

	
	protected void paintContent(Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g) {
		Gauge gauge = (Gauge)parent;
//		int centerX = x + parent.itemWidth / 2;
//		int centerY = y + parent.itemHeight / 2;
		int position = this.valuePosition;
		if (gauge.getMaxValue() != Gauge.INDEFINITE) {
			int valuePercent = (gauge.getValue() * 100 ) / gauge.getMaxValue();
			position = ((valuePercent*this.number)/100);
		}
		for(int i = 0; i < this.number; i++){
			if(i == position ){
				g.setColor(this.selectedColor);
			}else{
				g.setColor(this.unSelectedColor);
			}
			int newX = x + (this.paddingHorizontal*i);
			g.fillRect(newX + (this.width*i) , y, this.width, this.height);
		}
//		g.setColor(0xFF0000);
//		g.drawString(""+gauge.getValue(), x + this.width * this.number  ,  y + this.height, 0);
//		g.drawString(""+position, x + this.width * this.number +20 ,  y + this.height * 2, 0);
	}

	
	protected void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.gauge-recline-number
			Integer number = style.getIntProperty("gauge-recline-number");
			if (number != null) {
				this.number = number.intValue();
			}
		//#endif
		//#if polish.css.gauge-recline-width
			Integer width = style.getIntProperty("gauge-recline-width");
			if (width != null) {
				this.width = width.intValue();
			}
		//#endif
		//#if polish.css.gauge-recline-height
			Integer height = style.getIntProperty("gauge-recline-height");
			if (height != null) {
				this.height = height.intValue();
			}
		//#endif
		//#if polish.css.gauge-recline-selectedcolor
			Color selectedColor = style.getColorProperty("gauge-recline-selectedcolor");
			if (selectedColor != null) {
				this.selectedColor = selectedColor.getColor();
			}
		//#endif
		//#if polish.css.gauge-recline-unselectedcolor
			Color unSelectedColor = style.getColorProperty("gauge-recline-unselectedcolor");
			if (unSelectedColor != null) {
				this.unSelectedColor = unSelectedColor.getColor();
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
