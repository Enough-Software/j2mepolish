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
import de.enough.polish.util.DrawUtil;

/**
 * Implements a simple pie-chart-like gauge view.
 * @author Ovidiu
 *
 */
public class PieGaugeView extends ItemView {
	private int selectedColor = 0xFF00FF;
	private int unSelectedColor = 0x111111;	

	
	protected void paintContent(Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g) {
		Gauge gauge = (Gauge)parent;
		int oldColor = g.getColor();
		
		// Draw BG.
		g.setColor(unSelectedColor);
		g.fillArc(x,y,this.contentWidth, this.contentHeight,0, 360);
		
		// Draw border
		g.setColor(selectedColor);
		g.drawArc(x,y,this.contentWidth, this.contentHeight,0, 360);
		
		// Draw Content
		g.setColor(selectedColor);
		int fillDistance = (int) ( 360 * ( (1.0 * gauge.getValue() ) / gauge.getMaxValue() ) );
		g.fillArc(x,y,this.contentWidth, this.contentHeight,90, -fillDistance);
		
		g.setColor(oldColor);
	}

	
	protected void setStyle(Style style) {
		super.setStyle(style);		
		//#if polish.css.gauge-background-color
			Color bgColor = style.getColorProperty("gauge-background-color");
			if (bgColor != null) {
				this.unSelectedColor = bgColor.getColor();
			}
		//#endif
		//#if polish.css.gauge-color
			Color fgColor = style.getColorProperty("gauge-color");
			if (fgColor != null) {
				this.selectedColor = fgColor.getColor();
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

	protected void initContent(Item parent, int firstLineWidth, int availWidth,
			int availHeight) {
				this.contentHeight = availHeight;
				this.contentWidth = availWidth;
	}
	
	
}
