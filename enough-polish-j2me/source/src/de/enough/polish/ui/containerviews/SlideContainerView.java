//#condition polish.usePolishGui
/*
 * Created on Aug 21, 2006 at 12:48:38 PM.
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
package de.enough.polish.ui.containerviews;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Border;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;

/**
 * <p>Slides the parent container in any direction, this animation can used for menus for example.</p>
 * <pre>
 * menu {
 * 	view-type: slide;
 * }
 * </pre>
 * <pre>
 * menu {
 * 	view-type: slide;
 * 	slideview-direction: leftop; // moving from bottom-right to the left-top corner
 * }
 * </pre>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Aug 21, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SlideContainerView extends ContainerView {
	
	//private static final int DIRECTION_UP = 0;
	private static final int DIRECTION_DOWN = 1;
	private static final int DIRECTION_LEFT = 2;
	private static final int DIRECTION_RIGHT = 3;
	private static final int DIRECTION_DIAGONAL_LEFTUP = 4;
	private static final int DIRECTION_DIAGONAL_RIGHTUP = 5;
	private static final int DIRECTION_DIAGONAL_LEFTDOWN = 6;
	private static final int DIRECTION_DIAGONAL_RIGHTDOWN = 7;

	//#if polish.css.slideview-direction
		private int direction;
	//#endif
	private boolean isAnimationFinished;
	private int yOffset;
	private int xOffset;
	private int minSpeed = 2;
	private int maxSpeed = -1;
	private int duration = 1600;
	private long animationStartTime;
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion)
	{
		super.animate(currentTime, repaintRegion);
		if (!this.isAnimationFinished ) {
			int y = 0;
			//#if polish.css.slideview-direction
				int x = 0;
				if (this.yOffset != 0) {
					y = calculateNext( this.yOffset, 0, currentTime, this.contentHeight );
				}
				if (this.xOffset != 0) {
					x = calculateNext( this.xOffset, 0, currentTime, this.contentWidth );
				}
				this.parentContainer.addRelativeToBackgroundRegion(repaintRegion, -2,  - 2, this.parentContainer.getBackgroundWidth() + 4, this.parentContainer.getBackgroundHeight() + 4 );
				this.xOffset = x;
				this.yOffset = y;
				if (x == 0 && y == 0) {
					this.isAnimationFinished = true;
				}
			//#else
				y = calculateNext( this.yOffset, 0, currentTime, this.contentHeight );
				this.parentContainer.addRelativeToBackgroundRegion(repaintRegion, -2, y - 2, this.parentContainer.getBackgroundWidth() + 4, this.parentContainer.getBackgroundHeight() - y + 4 );
				this.yOffset = y;
				if (y == 0) {
					this.isAnimationFinished = true;
				}
			//#endif
		}
	}
	
	/**
	 * Calculates the next value
	 * @param current the current value
	 * @param target the target value
	 * @param currentTime the current time
	 * @param completeDistance the complete distance
	 * @return the next value
	 */
	protected int calculateNext( int current, int target, long currentTime, int completeDistance ) {
		boolean isPositive = (current > 0 ||  target > 0);
		int speed;
		if (isPositive) {
			speed = Math.max( this.minSpeed, current / 3);
		} else {
			speed = Math.max( this.minSpeed, -current / 3);
		}
		if (this.maxSpeed != -1 && speed > this.maxSpeed) {
			speed = this.maxSpeed;
		}
		boolean isDecrease = (target < current);
		int next;
		if (isDecrease) {
			next = current - speed;
			if (next < target) {
				next = target;
			}
		} else {
			next = current + speed;
			if (next > target) {
				next = target;
			}
		}
			
		if (this.duration != -1) {
			long timePassed = currentTime - this.animationStartTime;
			if (timePassed > this.duration) {
				return target;
			}
			int timeDistance =  (int) ((timePassed * completeDistance) / this.duration);
			if (isDecrease) {
				if ((completeDistance - timeDistance) < next) {
					next = completeDistance - timeDistance;
				}
			} else if (timeDistance - completeDistance > next){
				next = timeDistance - completeDistance;
			}
		}
		return next;
	}
	
	/**
	 * Initializes the animation.
	 */
	protected void initAnimation()
	{
		//#if polish.css.slideview-direction
			switch (this.direction) {
			case DIRECTION_DOWN:
				this.xOffset = 0;
				this.yOffset = - this.contentHeight;
				break;
			case DIRECTION_LEFT:
				this.xOffset = this.contentWidth;
				this.yOffset = 0;
				break;
			case DIRECTION_RIGHT:
				this.xOffset = - this.contentWidth;
				this.yOffset = 0;
				break;
			case DIRECTION_DIAGONAL_LEFTUP:
				this.xOffset = this.contentWidth;
				this.yOffset = this.contentHeight;			
				break;
			case DIRECTION_DIAGONAL_RIGHTUP:
				this.xOffset = - this.contentWidth;
				this.yOffset = this.contentHeight;			
				break;
			case DIRECTION_DIAGONAL_LEFTDOWN:
				this.xOffset = this.contentWidth;
				this.yOffset = - this.contentHeight;
				break;
			case DIRECTION_DIAGONAL_RIGHTDOWN:
				this.xOffset = - this.contentWidth;
				this.yOffset = - this.contentHeight;
				break;
			default:
				this.xOffset = 0;
		//#endif
				this.yOffset = this.contentHeight;			
		//#if polish.css.slideview-direction
			}
		//#endif
		this.restartAnimation = false;
		this.isAnimationFinished = false;
		this.animationStartTime = System.currentTimeMillis();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#showNotify()
	 */
	public void showNotify() {
		super.showNotify();
		if (this.contentHeight != 0) {
			initAnimation();
		}
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth, int availWidth, int availHeight) {
		super.initContent(parentContainerItem, firstLineWidth, availWidth, availHeight);
		// not sufficient to check this only in the showNotify method,
		// since the container dimensions might not yet be known:
		if (this.restartAnimation) {
			initAnimation();
		}
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, Item[] myItems, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g)
	{
		boolean setClip = (this.yOffset != 0);
		//#if polish.css.slideview-direction
			setClip |= (this.xOffset != 0);
		//#endif
		if (setClip) {
			g.clipRect( x - 1, y - 1, rightBorder - leftBorder + 2, this.contentHeight + 2);
			int yOff = this.yOffset;
			//#if polish.css.slideview-direction
				int xOff = this.xOffset;
				rightBorder += xOff;
				leftBorder += xOff;
				x += xOff;
				y += yOff;
			//#else
				y += this.yOffset;
				g.clipRect( clipX, y - 2, clipWidth, this.contentHeight - this.yOffset + 4);
			//#endif
		}
		super.paintContent(container, myItems, x, y, leftBorder, rightBorder, 
				clipX, clipY, clipWidth, clipHeight, g);
		if (setClip) {
			g.setClip( clipX, clipY, clipWidth, clipHeight );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintBackground(de.enough.polish.ui.Background, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintBackground(Background background, int x, int y, int width, int height, Graphics g) {
		int yOff = this.yOffset;
		//#if polish.css.slideview-direction
			int xOff = this.xOffset;
			if (xOff >= 0) {
				x += xOff;
				width -= xOff;				
			} else {
				width += xOff;
			}
			if (yOff < 0) {
				height += yOff;
				yOff = 0;
			}
		//#endif
		y += yOff;
		height -= yOff;
		super.paintBackground(background, x, y, width, height, g);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintBorder(de.enough.polish.ui.Border, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintBorder(Border border, int x, int y, int width, int height, Graphics g) {
		int yOff = this.yOffset;
		//#if polish.css.slideview-direction
			int xOff = this.xOffset;
			if (xOff >= 0) {
				x += xOff;
				width -= xOff;				
			} else {
				width += xOff;
			}
			if (yOff < 0) {
				height += yOff;
				yOff = 0;
			}
		//#endif
		y += yOff;
		height -= yOff;
		super.paintBorder(border, x, y, width, height, g);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style)
	{
		super.setStyle(style);
		//#if polish.css.slideview-duration
			Integer durationInt = style.getIntProperty("slideview-duration");
			if (durationInt != null) {
				this.duration  = durationInt.intValue();
			}
		//#endif
		//#if polish.css.slideview-direction
			Integer directionInt = style.getIntProperty("slideview-direction");
			if (directionInt != null) {
				this.direction  = directionInt.intValue();
			}
		//#endif

	}
	
	
	

}
