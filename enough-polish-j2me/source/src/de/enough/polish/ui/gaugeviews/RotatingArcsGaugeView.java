//#condition polish.usePolishGui
/*
 * Created on Jan 31, 2007 at 3:01:30 PM.
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
 * <p>Shows an animation of rotating arcs for visualizing an CONTINUOUS_RUNNING indefinite gauge.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Jan 31, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RotatingArcsGaugeView extends ItemView {
	
	private int startColor = 0x222222; 
	private int endColor   = 0xeeeeee; 
	private int numberOfArcs = 6;
	private int rotationSpeed = 12;
	
	private int startArc;
	private int[] arcColors;
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate()
	 */
	public boolean animate() {
		super.animate();
		//this.startColor = (this.startColor + 360/this.numberOfArcs) % 360;
		this.startArc += this.rotationSpeed;
		return true;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) {
		this.contentWidth = Math.max( availWidth/4, 24 );
		this.contentHeight = this.contentWidth;
		this.arcColors = DrawUtil.getGradient(this.startColor, this.endColor, this.numberOfArcs );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintContent(de.enough.polish.ui.Item, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder,
			int rightBorder, Graphics g) 
	{
//		Gauge gauge = (Gauge) parent;
//		if (gauge.getValue() != Gauge.CONTINUOUS_RUNNING) {
//			gauge.paintContent(x, y, leftBorder, rightBorder, g);
//			return;
//		}
		int arcStep = 360 / this.numberOfArcs;
		int arc = this.startArc;
		int width = this.contentWidth;
		for (int i = 0; i < this.arcColors.length; i++) {
			int color = this.arcColors[i];
			g.setColor( color );
			g.fillArc( x, y, width, width, arc, arcStep >> 1 );
			arc += arcStep;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle );
		//#if polish.css.gauge-rotating-arc-start-color
			Color startColorObj = style.getColorProperty("gauge-rotating-arc-start-color");
			if (startColorObj != null) {
				this.startColor = startColorObj.getColor();
			}
		//#endif
		//#if polish.css.gauge-rotating-arc-end-color
			Color endColorObj = style.getColorProperty("gauge-rotating-arc-end-color");
			if (endColorObj != null) {
				this.endColor = endColorObj.getColor();
			}
		//#endif
		//#if polish.css.gauge-rotating-arc-number
			Integer numberInt = style.getIntProperty("gauge-rotating-arc-number");
			if (numberInt != null) {
				this.numberOfArcs = numberInt.intValue();
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
