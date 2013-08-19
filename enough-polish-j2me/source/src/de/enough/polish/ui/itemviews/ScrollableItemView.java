//#condition polish.usePolishGui
/*
 * Copyright (c) 2012 Robert Virkus / Enough Software
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
package de.enough.polish.ui.itemviews;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Animatable;
import de.enough.polish.ui.AnimationThread;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemScrollBar;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.Scrollable;
import de.enough.polish.ui.Scroller;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;

/**
 * Allows to set the height of any item so that it will scroll if necessary. 
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ScrollableItemView 
extends ItemView
implements Scrollable, Animatable
{
	
	private transient ItemScrollBar scrollBar;
	private Dimension scrollHeight;
	private boolean isShowScrollBar;
	private int	originalContentHeight;
	private int yOffset;
	private transient Scroller scroller;
	

	public ScrollableItemView()
	{
		// TODO Auto-generated constructor stub
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle)
	{
		super.setStyle(style, resetStyle);
		Dimension scrollHeightDim = (Dimension) style.getObjectProperty("scroll-height");
		if (scrollHeightDim != null)
		{
			this.scrollHeight = scrollHeightDim;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int, int)
	 */
	protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight)
	{
		if (this.scrollHeight == null)
		{
			throw new IllegalStateException("no scroll-height specified for " + parent);
		}
		int sHeight = this.scrollHeight.getValue(availHeight);
		int itemContentHeight = parent.getContentHeight();
		ItemScrollBar sb = this.scrollBar;
		if (sb == null)
		{
			//#style itemscrollbar?
			sb = new ItemScrollBar();
			this.scrollBar = sb;
			sb.setParent(parent);			
			sb.initScrollBar(availWidth, sHeight, itemContentHeight, this.yOffset);
			sb.relativeY = 0;
			sb.relativeX = 0;
			if (sb.isLayoutRight())
			{
				sb.relativeX = availWidth - sb.itemWidth;
			}
			System.out.println("scrollbar.itemWidth=" + sb.itemWidth);
		}
		int sbLayoutWidth = sb.getSliderLayoutWidth();
		this.isShowScrollBar = false;
		if (itemContentHeight > sHeight)
		{
			// assume that the item is still larger than the scrollHeight, use less width:
			super.initContentByParent(parent, firstLineWidth - sbLayoutWidth, availWidth - sbLayoutWidth, availHeight);
			if (this.contentHeight < sHeight)
			{
				if (sbLayoutWidth > 0)
				{
					super.initContentByParent(parent, firstLineWidth, availWidth, availHeight);
				}
			}
			else
			{
				this.isShowScrollBar = true;
			}
		}
		else
		{
			super.initContentByParent(parent, firstLineWidth, availWidth, availHeight);
			if (this.contentHeight > sHeight)
			{
				if (sbLayoutWidth > 0)
				{
					super.initContentByParent(parent, firstLineWidth - sbLayoutWidth, availWidth - sbLayoutWidth, availHeight);
				}
				this.isShowScrollBar = true;
			}
		}
		if (this.isShowScrollBar)
		{
			sb.initScrollBar(availWidth, sHeight, this.contentHeight, this.yOffset);
			if (this.scroller == null)
			{
				this.scroller = new Scroller( Scroller.ORIENTATION_VERTICAL, sHeight, this.contentHeight, this);
			}
		}
		this.originalContentHeight = this.contentHeight;
		this.contentHeight = sHeight;
		this.contentWidth = availWidth;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintContent(de.enough.polish.ui.Item, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g)
	{
		if (!this.isShowScrollBar)
		{
			super.paintContentByParent(parent, x, y, leftBorder, rightBorder, g);
		}
		else
		{
			ItemScrollBar sb = this.scrollBar;
			rightBorder -= sb.getSliderLayoutWidth();
			int clipX = g.getClipX();
			int clipY = g.getClipY();
			int clipWidth = g.getClipWidth();
			int clipHeight = g.getClipHeight();
			g.clipRect(x, y, this.contentWidth, this.contentHeight);
			super.paintContentByParent(parent, x, y + this.yOffset, leftBorder, rightBorder, g);
			g.setClip(clipX, clipY, clipWidth, clipHeight);
			sb.paint( x + sb.relativeX, y + sb.relativeY, leftBorder, rightBorder, g);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerPressed(int, int)
	 */
	public boolean handlePointerPressed(int x, int y)
	{
		boolean handled = false;
		if (this.isShowScrollBar)
		{
			handled = this.scroller.handlePointerPressed(x, y);
		}
		return handled || super.handlePointerPressed(x, y);
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerReleased(int, int)
	 */
	public boolean handlePointerReleased(int x, int y)
	{
		boolean handled = false;
		if (this.isShowScrollBar)
		{
			handled = this.scroller.handlePointerReleased(x, y);
		}
		return handled || super.handlePointerReleased(x, y);
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerDragged(int, int, de.enough.polish.ui.ClippingRegion)
	 */
	public boolean handlePointerDragged(int x, int y,
			ClippingRegion repaintRegion)
	{
		boolean handled = false;
		if (this.isShowScrollBar)
		{
			handled = this.scroller.handlePointerDragged(x, y, repaintRegion);
		}
		return handled || super.handlePointerDragged(x, y, repaintRegion);
	}

	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion)
	{
		//System.out.println("ScrollableItemView:animate: showScrollBar=" + this.isShowScrollBar);
		if (this.isShowScrollBar)
		{
			this.scroller.animate(currentTime, repaintRegion);
			this.scrollBar.animate(currentTime, repaintRegion);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Scrollable#setScrollOffset(de.enough.polish.ui.Scroller, int)
	 */
	public void setScrollOffset(Scroller scroller, int offset)
	{
		this.yOffset = offset;
		this.scrollBar.onScrollYOffsetChanged(offset);
		UiAccess.repaint(this.parentItem);
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.UiElement#getStyle()
	 */
	public Style getStyle()
	{
		return this.parentItem.getStyle();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle( Style style ) 
	{
		super.setStyle(style);
	}
	
	

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.UiElement#addRepaintArea(de.enough.polish.ui.ClippingRegion)
	 */
	public void addRepaintArea(ClippingRegion repaintArea)
	{
		this.parentItem.addRepaintArea(repaintArea);
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.UiElement#addRelativeToContentRegion(de.enough.polish.ui.ClippingRegion, int, int, int, int)
	 */
	public void addRelativeToContentRegion(ClippingRegion repaintRegion, int x,
			int y, int width, int height)
	{
		this.parentItem.addRelativeToContentRegion(repaintRegion, x, y, width, height);
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#focus(de.enough.polish.ui.Style, int)
	 */
	public void focus(Style focusstyle, int direction)
	{
		System.out.println("FOCUS");
		super.focus(focusstyle, direction);
		AnimationThread.removeAnimationItem(this);
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle)
	{
		System.out.println("DEFOCUS");
		super.defocus(originalStyle);
		AnimationThread.addAnimationItem(this);
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#showNotify()
	 */
	public void showNotify()
	{
		System.out.println("SHOW NOTIFY");
		super.showNotify();
		AnimationThread.addAnimationItem(this);
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#hideNotify()
	 */
	public void hideNotify()
	{
		System.out.println("HIDE NOTIFY");
		super.hideNotify();
		AnimationThread.removeAnimationItem(this);
	}

	
}
