//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:08:13.
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
import de.enough.polish.ui.Style;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a rectangle with round corners as a background.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * @author Robert Virkus, robert@enough.de
 */
public class Web20RoundRectBackground 
extends Background 
{
	private int color;
	private Color colorObj;
	private int arcWidth;
	private int arcHeight;
	private int circleColor;
	private Color circleColorObj;
	private final int circleAnchor;
	private int paddingLeft;
	private int paddingRight;
	private int paddingTop;
	private int paddingBottom;
	private boolean isInitialized;


	/**
	 * Creates a new round rectangle background.
	 * 
	 * @param color the color of the background
	 * @param arcWidth the horizontal diameter of the arc at the four corners
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 * @param circleColor the color of the circle arc blended over the rounded background
	 * @param circleAnchor the anchor of the circle, either Graphics.TOP, Graphics.BOTTOM, Graphics.LEFT or Graphics.RIGHT
	 * @param paddingLeft the padding between the round edges and the background's left boundary
	 * @param paddingRight the padding between the round edges and the background's right boundary
	 * @param paddingTop the padding between the round edges and the background's top boundary
	 * @param paddingBottom the padding between the round edges and the background's bottom boundary
	 */
	public Web20RoundRectBackground( int color, int arcWidth, int arcHeight, int circleColor, int circleAnchor, int paddingLeft, int paddingRight, int paddingTop, int paddingBottom ) {
		super();
		this.color = color;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
		this.circleColor = circleColor;
		this.circleAnchor = circleAnchor;
		this.paddingLeft = paddingLeft;
		this.paddingRight = paddingRight;
		this.paddingTop = paddingTop;
		this.paddingBottom = paddingBottom;
		this.isInitialized = true;
	}
	
	/**
	 * Creates a new round rectangle background.
	 * 
	 * @param colorObj the color of the background
	 * @param arcWidth the horizontal diameter of the arc at the four corners
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 * @param circleColorObj the color of the circle arc blended over the rounded background
	 * @param circleAnchor the anchor of the circle, either Graphics.TOP, Graphics.BOTTOM, Graphics.LEFT or Graphics.RIGHT
	 * @param paddingLeft the padding between the round edges and the background's left boundary
	 * @param paddingRight the padding between the round edges and the background's right boundary
	 * @param paddingTop the padding between the round edges and the background's top boundary
	 * @param paddingBottom the padding between the round edges and the background's bottom boundary
	 */
	public Web20RoundRectBackground( Color colorObj, int arcWidth, int arcHeight, Color circleColorObj, int circleAnchor, int paddingLeft, int paddingRight, int paddingTop, int paddingBottom ) {
		super();
		this.colorObj = colorObj;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
		this.circleColorObj = circleColorObj;
		this.circleAnchor = circleAnchor;
		this.paddingLeft = paddingLeft;
		this.paddingRight = paddingRight;
		this.paddingTop = paddingTop;
		this.paddingBottom = paddingBottom;
		this.isInitialized = true;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (!this.isInitialized) {
			this.isInitialized = true;
			this.color = this.colorObj.getColor();
			this.circleColor = this.circleColorObj.getColor();
		}
		// paint background with rounded corners:
		g.setColor( this.color );
		g.fillRoundRect( x, y, width, height, this.arcWidth, this.arcHeight );
		// paint circular arc at either top, bottom, left or right:
		g.setColor( this.circleColor );
		switch (this.circleAnchor) {	
		case Graphics.LEFT:
			g.fillArc(x, y, width, height, 360 - 45, 180);
			break;
		case Graphics.RIGHT:
			g.fillArc(x + width, y, -width, height, 360 - 45, 180);
			break;
		case Graphics.TOP:
			int yNew = y + this.paddingTop - (height - (this.paddingBottom + this.paddingTop));
			//System.out.println("height=" + height + ", paddingTop=" + this.paddingTop + ", paddingBottom=" + this.paddingBottom + ", y=" + y + ", yNews=" + yNew);
			//g.fillArc(x + this.paddingLeft, y - (height - (this.paddingBottom + this.paddingTop))<<1, width - (this.paddingLeft + this.paddingRight), (height - (this.paddingTop + this.paddingBottom))<<1, 180, 180 );
			g.fillArc( x + this.paddingLeft, 
					   yNew, 
					   width - (this.paddingLeft + this.paddingRight), 
					   (height - (this.paddingTop + this.paddingBottom))<<1, 180, 180 );
			break;
		default:
			g.fillArc(	x + this.paddingLeft, 
						y + this.paddingTop, 
						width - (this.paddingLeft + this.paddingRight), 
						(height - (this.paddingTop + this.paddingBottom))<<1, 
						180, -180 );
			break;
		}
		
	}
	
	
	//#if polish.css.animations
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
		 */
		public void setStyle(Style style)
		{
			//#if polish.css.background-web20-color
				Color bgCol = style.getColorProperty("background-web20-color");
				if (bgCol != null) {
					this.color = bgCol.getColor();
				}
			//#endif
			//#if polish.css.background-web20-arc
				Integer arcInt = style.getIntProperty("background-web20-arc");
				if (arcInt != null) {
					this.arcWidth = arcInt.intValue();
					this.arcHeight = arcInt.intValue();
				}
			//#endif
			//#if polish.css.background-web20-arc-width
				Integer arcWidthInt = style.getIntProperty("background-web20-arc-width");
				if (arcWidthInt != null) {
					this.arcWidth = arcWidthInt.intValue();
				}
			//#endif
			//#if polish.css.background-web20-arc-height
				Integer arcHeightInt = style.getIntProperty("background-web20-arc-height");
				if (arcHeightInt != null) {
					this.arcHeight = arcHeightInt.intValue();
				}
			//#endif
			//#if polish.css.background-web20-circle-color
				Color circleCol = style.getColorProperty("background-web20-circle-color");
				if (circleCol != null) {
					this.circleColor = circleCol.getColor();
				}
			//#endif
			//#if polish.css.background-web20-padding-left
				Integer paddingLeftInt = style.getIntProperty("background-web20-padding-left");
				if (paddingLeftInt != null) {
					this.paddingLeft = paddingLeftInt.intValue();
				}
			//#endif
			//#if polish.css.background-web20-padding-right
				Integer paddingRightObj = style.getIntProperty("background-web20-padding-right");
				if (paddingRightObj != null) {
					this.paddingRight = paddingRightObj.intValue();
				}
			//#endif
			//#if polish.css.background-web20-padding-top
				Integer paddingTopObj = style.getIntProperty("background-web20-padding-top");
				if (paddingTopObj != null) {
					this.paddingTop = paddingTopObj.intValue();
				}
			//#endif
			//#if polish.css.background-web20-padding-bottom
				Integer paddingBottomObj = style.getIntProperty("background-web20-padding-bottom");
				if (paddingBottomObj != null) {
					this.paddingBottom = paddingBottomObj.intValue();
				}
			//#endif
		}
	//#endif

}
