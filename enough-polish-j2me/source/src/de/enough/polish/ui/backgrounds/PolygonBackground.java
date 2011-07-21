//#condition polish.usePolishGui
/*
 * Created on 12-Oct-2007 at 21:31:54.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Point;
import de.enough.polish.ui.Style;
import de.enough.polish.util.DrawUtil;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a filled polygon as a background in a specific color.</p>
 * <p>usage:
 * <pre>
 * .myItem {
 * 		background {	
 * 			type: polygon;
 * 			color: green;
 * 			points: 0,50 50,0 100,50 50,100;
 * 			max-x: 100; 
 *			max-y: 100;
 *			scale-mode: proportional; // none, scale, proportional, expand 
 *			expand: false;
 * 		}
 * }
 * </pre>
 * </p>
 * 
 * <p>Copyright Enough Software 2007 - 2010</p>
 * @author Robert Virkus, robert@enough.de
 */
public class PolygonBackground 
extends Background 
{
	/** use this mode for not scaling the polygon at all */
	public static final int MODE_NO_SCALE = 0;
	/** use this mode for scaling the polygon according to the width and height of the background - the given reference width and height are used for calculating the actual positions */
	public static final int MODE_SCALE = 1;
	/** use this mode for scaling the polygon proportional according to the minimum of width and height of the background - the given reference width and height are used for calculating the actual positions */
	public static final int MODE_SCALE_PROPORTIONAL = 2;
	/** use this mode for scaling the polygon proportional according to the maximum of width and height of the background - the given reference width and height are used for calculating the actual positions */
	public static final int MODE_SCALE_PROPORTIONAL_EXPAND = 3;
	
	
	private int color;
	private Color colorObj;
	private Point[] points;
	private int scalingMode;
	
	private int[] xPoints;
	private int[] yPoints;
	private int referenceWidth;
	private int referenceHeight;
	private int lastY;
	private int lastX;
	private int lastWidth;
	private int lastHeight;
	private final int anchor;

	/**
	 * Creates a new simple background.
	 * 
	 * @param color the color of the background in RGB, e.g. 0xFFDD11
	 * @param points the points of the polygon
	 * @param referenceWidth the maximum horizontal position, 0 when the positions should not be scaled
	 * @param referenceHeight the maximum horizontal position, 0 when the positions should not be scaled
	 * @param scalingMode the scaling mode to be used
	 * @param anchor the anchor like Graphics.TOP | Graphics.RIGHT etc - only applicable in no-expanded proportional or non-scaling mode
	 * @see #MODE_NO_SCALE
	 * @see #MODE_SCALE
	 * @see #MODE_SCALE_PROPORTIONAL
	 * @see #MODE_SCALE_PROPORTIONAL_EXPAND
	 */
	public PolygonBackground( int color, Point[] points, int referenceWidth, int referenceHeight,  int scalingMode, int anchor ) {
		this( null, points, referenceWidth, referenceHeight, scalingMode, anchor );
		this.color = color;
	}
	
	/**
	 * Creates a new simple background.
	 * 
	 * @param color the color of the background in RGB, e.g. 0xFFDD11
	 * @param points the points of the polygon
	 * @param referenceWidth the maximum horizontal position, 0 when the positions should not be scaled
	 * @param referenceHeight the maximum horizontal position, 0 when the positions should not be scaled
	 * @param scalingMode the scaling mode to be used
	 * @param anchor the anchor like Graphics.TOP | Graphics.RIGHT etc - only applicable in no-expanded proportional or non-scaling mode
	 * @see #MODE_NO_SCALE
	 * @see #MODE_SCALE
	 * @see #MODE_SCALE_PROPORTIONAL
	 * @see #MODE_SCALE_PROPORTIONAL_EXPAND
	 */
	public PolygonBackground( Color color, Point[] points, int referenceWidth, int referenceHeight, int scalingMode, int anchor ) {
		this.colorObj = color;
		this.points = points;
		this.referenceWidth = referenceWidth;
		this.referenceHeight = referenceHeight;
		this.scalingMode = scalingMode;
		this.anchor = anchor;
	}

	private int[] getPoints(int start, Point[] myPoints, int[] scalarPoints, int available, int max, boolean isX ) {
		int reference = isX ? this.referenceWidth : this.referenceHeight;
		if( scalarPoints == null) {
			scalarPoints = new int[ myPoints.length ];
		}
		for (int i = 0; i < scalarPoints.length; i++) {
			int value;
			if (isX) {
				value = myPoints[i].x;
			} else {
				value = myPoints[i].y;
			}
			if (max == 0) {
				scalarPoints[i] = start + value;
			} else {
				scalarPoints[i] = start + (value * max) / reference;
			}
		}
		return scalarPoints;
	}


	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (this.colorObj != null) {
			this.color = this.colorObj.getColor();
			this.colorObj = null;
		}
		
		if (x != this.lastX || y != this.lastY || width != this.lastWidth || height != this.lastHeight || this.xPoints == null) {
			int maxWidth  = width;
			int maxHeight = height;
			switch (this.scalingMode) {
			case MODE_NO_SCALE:
				maxWidth = 0;
				maxHeight = 0;
				break;
			case MODE_SCALE:
				break;
			case MODE_SCALE_PROPORTIONAL:
				maxWidth = Math.min( width, height);
				maxHeight = maxWidth;
				break;
			case MODE_SCALE_PROPORTIONAL_EXPAND:
				maxWidth = Math.max( width, height);
				maxHeight = maxWidth;
				break;
			}
			int startX = x;
			if ( (this.anchor & Graphics.HCENTER) == Graphics.HCENTER) {
				if (maxWidth == 0) {
					startX += (width - this.referenceWidth) >> 1;
				} else {
					startX += (width - maxWidth) >> 1;
				}
			} else if ( (this.anchor & Graphics.RIGHT) == Graphics.RIGHT) {
				if (maxWidth == 0) {
					startX += (width - this.referenceWidth);
				} else {
					startX += (width - maxWidth);
				}
			}
			int startY = y;
			if ( (this.anchor & Graphics.VCENTER) == Graphics.VCENTER) {
				if (maxHeight == 0) {
					startY += (height - this.referenceHeight) >> 1;
				} else {
					startY += (height - maxHeight) >> 1; //((this.referenceHeight * max) / height)) >> 1;
				}
			} else if ( (this.anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
				if (maxHeight == 0) {
					startY += (height - this.referenceHeight);
				} else {
					startY += (height - maxHeight);
				}
			}
			
			this.xPoints = getPoints(startX, this.points, this.xPoints,  width, maxWidth,  true );
			this.yPoints = getPoints(startY, this.points, this.yPoints, height, maxHeight, false);
			this.lastX = x;
			this.lastY = y;
			this.lastWidth = width;
			this.lastHeight = height;
		}
		DrawUtil.fillPolygon(this.xPoints, this.yPoints, this.color, g);
	}

	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		//#if polish.css.background-polygon-color
			Color tbgColor = style.getColorProperty("background-polygon-color");
			if (tbgColor != null) {
				this.color = tbgColor.getColor();
			}
		//#endif
		//#if polish.css.background-polygon-reference-width
			Integer refWidthInt = style.getIntProperty("background-polygon-reference-width");
			if (refWidthInt != null) {
				this.referenceWidth = refWidthInt.intValue();
				this.lastWidth = 0;
			}
		//#endif
		//#if polish.css.background-polygon-reference-height
			Integer refHeightInt = style.getIntProperty("background-polygon-reference-height");
			if (refHeightInt != null) {
				this.referenceHeight = refHeightInt.intValue();
				this.lastWidth = 0;
			}
		//#endif

	}
	//#endif
}
