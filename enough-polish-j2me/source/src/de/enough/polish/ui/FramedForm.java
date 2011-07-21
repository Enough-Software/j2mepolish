//#condition polish.usePolishGui
/*
 * Created on 12-Apr-2005 at 13:31:53.
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
package de.enough.polish.ui;

import javax.microedition.lcdui.Canvas;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.util.ArrayList;

/**
 * <p>Allows to split up a form into several frames.</p>
 * <p>The main frame is used for the normal content. Additional frames
 *    can be used for keeping GUI elements always in the same position,
 *    regardless whether the form is scrolled.
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FramedForm 
extends Form
implements CycleListener
{
	
	/** the frame positioned at the top */
	public static final int FRAME_TOP = Graphics.TOP;
	/** the frame positioned at the bottom */
	public static final int FRAME_BOTTOM = Graphics.BOTTOM;
	/** the frame positioned at the left */
	public static final int FRAME_LEFT = Graphics.LEFT;
	/** the frame positioned at the right */
	public static final int FRAME_RIGHT = Graphics.RIGHT;
	/** the frame positioned at the center (the scrollable view) */
	public static final int FRAME_CENTER = -1;
	
	
	protected Container leftFrame;
	protected Container rightFrame;
	protected Container topFrame;
	protected Container bottomFrame;
	private int originalContentHeight;
	private int originalContentWidth;
	private boolean expandRightFrame;
	private boolean expandLeftFrame;
	//#if polish.FramedForm.allowCycling
		protected boolean allowCycling;
	//#endif
	
	protected Container currentlyActiveContainer;
	//#if polish.css.leftframe-style
		private Style leftFrameStyle;
	//#endif
	//#if polish.css.rightframe-style
		private Style rightFrameStyle;
	//#endif
	//#if polish.css.topframe-style
		private Style topFrameStyle;
	//#endif
	//#if polish.css.bottomframe-style
		private Style bottomFrameStyle;
	//#endif
	//#if polish.css.bottomframe-height
		private Dimension bottomFrameHeight;
	//#endif
	//#if polish.css.topframe-height
		private Dimension topFrameHeight;
	//#endif
		
	private int originalContentY;
	private int originalContentX;
	private boolean keepContentFocused;
	private boolean isCycled;

	/**
	 * Creates a new FramedForm
	 * 
	 * @param title the title of this form
	 */
	public FramedForm(String title ) {
		this( title, null );
	}

	/**
	 * Creates a new FramedForm
	 * 
	 * @param title the title of this form
	 * @param style the style of this form, usually set with a #style directive
	 */
	public FramedForm(String title, Style style) {
		super( title, style );
		this.currentlyActiveContainer = this.container;
		this.container.setCycleListener(this);
	}

	private Container getFrame( int frameOrientation ) {
		switch (frameOrientation) {
		case  Graphics.TOP:
			return this.topFrame;
		case  Graphics.BOTTOM:
			return this.bottomFrame;
		case  Graphics.LEFT:
			return this.leftFrame;
		case  Graphics.RIGHT:
			return this.rightFrame;
		}		
		return this.container;
	}

	/**
	 * Looks up the frame around the given position
	 * @param x the absolute x position
	 * @param y the absolute y position
	 * @return the frame of the specified position, might be null
	 */
	public  Container getFrame(int x, int y) {
		if (this.topFrame != null && y < this.topFrame.relativeY + this.topFrame.itemHeight) {
			return this.topFrame;
		}
		if (this.bottomFrame != null && y > this.bottomFrame.relativeY) {
			return this.bottomFrame;
		}
		if (this.leftFrame != null && x < this.leftFrame.relativeX + this.leftFrame.itemWidth) {
			return this.leftFrame;
		}
		if (this.rightFrame != null && x > this.rightFrame.relativeX) {
			return this.rightFrame;
		}
		if (this.container.isInItemArea( x - this.container.relativeX, y - this.container.relativeY)) {
			return this.container;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#getItemAt(int, int)
	 */
	public Item getItemAt( int x, int y ) {
        Container cont = this.container;
        Item superItemAt = null;
		if (isMenuOpened()) {
            superItemAt = super.getItemAt(x, y);
            if (superItemAt instanceof CommandItem) {
                return superItemAt;
            }
        }
		Container frame = this.topFrame;
		if (frame != null && y >= frame.relativeY && y <= frame.relativeY + frame.itemHeight) {
			return frame.getItemAt(x - frame.relativeX, y - frame.relativeY);
		}
		frame = this.bottomFrame;
		if (frame != null && y >= frame.relativeY && y <= frame.relativeY + frame.itemHeight) {
			return frame.getItemAt(x - frame.relativeX, y - frame.relativeY);
		}
		frame = this.leftFrame;
		if (frame != null && x >= frame.relativeX && x <= frame.relativeX + frame.itemWidth &&  y >= frame.relativeY && y <= frame.relativeY + frame.itemHeight) {
			return frame.getItemAt(x - frame.relativeX, y - frame.relativeY);
		}
		frame = this.rightFrame;
		if (frame != null && x >= frame.relativeX && x <= frame.relativeX + frame.itemWidth &&  y >= frame.relativeY && y <= frame.relativeY + frame.itemHeight) {
			return frame.getItemAt(x - frame.relativeX, y - frame.relativeY);
		}
		frame = this.currentlyActiveContainer;
        if (frame != cont) {
            Item item = frame.getItemAt(x - frame.relativeX, y - frame.relativeY);
            if (item != null && item != frame) {
                return item;
            }
        }
        if (superItemAt != null) {
        	return superItemAt;
        } else {
        	return super.getItemAt(x, y);
        }
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#focus(de.enough.polish.ui.Item, boolean)
	 */
	public void focus(Item item, boolean force) {
		focus( item, force, false );
	}
	
	/**
	 * Focuses the specified item and optionally keeps the current frame focused
	 * @param item the item
	 * @param force when the item should be focused even when it is not interactive
	 * @param keepCurrentFrame true when the currently active container should keep its focus
	 */
	public void focus(Item item, boolean force, boolean keepCurrentFrame) {
		if (item.isFocused) {
			//#debug
			System.out.println("Ignoring focus for already focused item " + item);
			return;
		}
		if (item.parent != null && item.parent.label == item) {
            item = item.parent;
        }
		ArrayList children = new ArrayList();
		while (item.parent != null) {
			if (force || item.isInteractive()) {
				children.add(item);
			} 
			item = item.parent;
		}
		Container frame = null;
		int offset = 0;
		if (!keepCurrentFrame && item != this.currentlyActiveContainer && item instanceof Container) {
			frame = (Container)item;
			offset = frame.getScrollYOffset();
			setActiveFrame( frame, this.keepContentFocused, 0  );
		}
		for (int i=children.size(); --i >= 0; ) {
			Item child = (Item) children.get(i);
			if (!child.isFocused) {
				if (item instanceof Container) {
					Container cont = (Container) item;
					cont.focusChild( cont.indexOf(child), child, 0, force );
				}
			}
			item = child;
		}
		if (frame != null) {
			frame.setScrollYOffset(offset, false);
		}
	}
	

	/**
	 * Deletes all the items from all frames of this <code>FramedForm</code>, leaving  it with zero items.
	 * This method does nothing if the <code>FramedForm</code> is already empty.
	 * 
	 * @since  MIDP 2.0
	 */
	public void deleteAll()
    {
        super.deleteAll();
        if (this.leftFrame != null)
        {
            this.leftFrame.clear();
            this.leftFrame = null;
        }
        if (this.rightFrame != null)
        {
            this.rightFrame.clear();
            this.rightFrame = null;
        }
        if (this.topFrame != null)
        {
            this.topFrame.clear();
            this.topFrame = null;
        }
        if (this.bottomFrame != null)
        {
            this.bottomFrame.clear();
            this.bottomFrame = null;
        }
        requestInit();
    }
	

	
	/**
	 * Removes all items from the specified frame.
	 * 
	 * @param frameOrientation either FRAME_TOP, FRAME_BOTTOM, FRAME_LEFT, FRAME_RIGHT or FRAME_CENTER for the content frame.
	 * @see #FRAME_TOP
	 * @see #FRAME_BOTTOM
	 * @see #FRAME_LEFT
	 * @see #FRAME_RIGHT
	 * @see #FRAME_CENTER
	 */
	public void deleteAll( int frameOrientation ) {
		Container frame = getFrame( frameOrientation );
		if (frame != null) {
			frame.clear();
			deleteFrame(frameOrientation);
		}
	}


	//#if polish.LibraryBuild
	public int append( javax.microedition.lcdui.Item item ) {
		// just a convenience method, in reality the append( Item item ) method is called
		return -1;
	}
	//#endif

	//#if polish.LibraryBuild
	/**
	 * Updates an existing item in the specified frame
	 * @param frameOrientation either FRAME_TOP, FRAME_BOTTOM, FRAME_LEFT, FRAME_RIGHT or FRAME_CENTER for the content frame.
	 * @param itemNumber the index of the previous item
	 * @param item the new item
	 * @see #FRAME_TOP
	 * @see #FRAME_BOTTOM
	 * @see #FRAME_LEFT
	 * @see #FRAME_RIGHT
	 * @see #FRAME_CENTER
	 */
	public void set( int frameOrientation, int itemNumber, javax.microedition.lcdui.Item item ) {
		// just a convenience method, in reality the append( Item item ) method is called
	}
	//#endif

	//#if polish.LibraryBuild
	/**
	 * Updates an existing item in the content frame
	 * @param itemNumber the index of the previous item
	 * @param item the new item
	 */
	public void set( int itemNumber, javax.microedition.lcdui.Item item ) {
		// just a convenience method, in reality the append( Item item ) method is called
	}
	//#endif

	//#if polish.LibraryBuild
	/**
	 * Adds the given item to the specified frame.
	 * 
	 * @param frameOrientation either FRAME_TOP, FRAME_BOTTOM, FRAME_LEFT, FRAME_RIGHT or FRAME_CENTER for the content frame.
	 * @param item the item
	 * @see #FRAME_TOP
	 * @see #FRAME_BOTTOM
	 * @see #FRAME_LEFT
	 * @see #FRAME_RIGHT
	 * @see #FRAME_CENTER
	 */
	public void append( int frameOrientation, javax.microedition.lcdui.Item item ) {
		// just a convenience method, in reality the addItem( Item item, int frameOrientation ) method is called
	}
	//#endif
	
	//#if polish.LibraryBuild
	/**
	 * Sets the <code>ItemStateListener</code> for the <code>Screen</code>, 
	 * replacing any previous <code>ItemStateListener</code>. 
	 * If
	 * <code>iListener</code> is <code>null</code>, simply
	 * removes the previous <code>ItemStateListener</code>.
	 * 
	 * @param iListener the new listener, or null to remove it
	 */
	public void setItemStateListener( javax.microedition.lcdui.ItemStateListener iListener ) {
		throw new RuntimeException("Unable to use standard ItemStateListener in a framed form.");
	}
	//#endif


	/**
	 * Adds the given item to the specified frame.
	 * 
	 * @param frameOrientation either FRAME_TOP, FRAME_BOTTOM, FRAME_LEFT, FRAME_RIGHT or FRAME_CENTER for the content frame.
	 * @param item the item
	 * @see #FRAME_TOP
	 * @see #FRAME_BOTTOM
	 * @see #FRAME_LEFT
	 * @see #FRAME_RIGHT
	 * @see #FRAME_CENTER
	 */
	public void append( int frameOrientation, Item item ) {
		append( frameOrientation, item, null );
	}
	
	
	/**
	 * Updates an existing item in the specified frame
	 * @param frameOrientation either FRAME_TOP, FRAME_BOTTOM, FRAME_LEFT, FRAME_RIGHT or FRAME_CENTER for the content frame.
	 * @param itemNum the index of the previous item
	 * @param item the new item
	 * @see #FRAME_TOP
	 * @see #FRAME_BOTTOM
	 * @see #FRAME_LEFT
	 * @see #FRAME_RIGHT
	 * @see #FRAME_CENTER
	 */
	public void set( int frameOrientation, int itemNum, Item item ) {
		Container frame = getFrame( frameOrientation );
		if (frame != null) {
			frame.set(itemNum, item);
		}
	}
	
	/**
	 * Removes the given item from the specified frame.
	 * The <code>itemNum</code> parameter must be
	 * within the range <code>[0..size()-1]</code>, inclusive.
	 * 
	 * @param frameOrientation either FRAME_TOP, FRAME_BOTTOM, FRAME_LEFT, FRAME_RIGHT or FRAME_CENTER for the content frame.
	 * @param itemNum the index of the item
	 * @see #FRAME_TOP
	 * @see #FRAME_BOTTOM
	 * @see #FRAME_LEFT
	 * @see #FRAME_RIGHT
	 * @see #FRAME_CENTER
	 */
	public void delete( int frameOrientation, int itemNum ) {
		Container frame = getFrame( frameOrientation );
		if (frame != null) {
			frame.remove(itemNum);
			if (frame.size() == 0) {
				deleteFrame( frameOrientation );
			} else {
				requestInit();
			}
		}
	}
	
	/**
	 * Retrieves the size of the specified frame.
	 * 
	 * @param frameOrientation either FRAME_TOP, FRAME_BOTTOM, FRAME_LEFT, FRAME_RIGHT or FRAME_CENTER for the content frame.
	 * @return the size of the frame, -1 when the frame does not exist 
	 * @see #FRAME_TOP
	 * @see #FRAME_BOTTOM
	 * @see #FRAME_LEFT
	 * @see #FRAME_RIGHT
	 * @see #FRAME_CENTER
	 */
	public int size( int frameOrientation ) {
		Container frame = getFrame( frameOrientation );
		if (frame == null) {
			return -1;
		} else {
			return frame.size();
		}
	}

	/**
	 * Deletes a complete frame.
	 * 
	 * @param frameOrientation either FRAME_TOP, FRAME_BOTTOM, FRAME_LEFT, FRAME_RIGHT or FRAME_CENTER for the content frame.
	 * @return true when the frame could be deleted
	 * @see #FRAME_TOP
	 * @see #FRAME_BOTTOM
	 * @see #FRAME_LEFT
	 * @see #FRAME_RIGHT
	 * @see #FRAME_CENTER
	 */
	public boolean deleteFrame(int frameOrientation) {
		Container frame;
		switch (frameOrientation) {
		case  Graphics.TOP:
			frame = this.topFrame;
			this.topFrame = null;
			break;
		case  Graphics.BOTTOM:
			frame = this.bottomFrame;
			this.bottomFrame = null;
			break;
		case  Graphics.LEFT:
			frame = this.leftFrame;
			this.leftFrame = null;
			break;
		case  Graphics.RIGHT:
			frame = this.rightFrame;
			this.rightFrame = null;
			break;
		default: return false;
		}		
		if (frame == null) {
			return false;
		}
		if (frame == this.currentlyActiveContainer) {
			setActiveFrame( this.container );
		}
		frame.hideNotify();
		requestInit();
		return true;
	}

	/**
	 * Adds the given item to the specified frame.
	 * 
	 * @param frameOrientation either FRAME_TOP, FRAME_BOTTOM, FRAME_LEFT, FRAME_RIGHT or FRAME_CENTER for the content frame.
	 * @param item the item
	 * @param itemStyle the style for that item, is ignored when null
	 * @see #FRAME_TOP
	 * @see #FRAME_BOTTOM
	 * @see #FRAME_LEFT
	 * @see #FRAME_RIGHT
	 * @see #FRAME_CENTER
	 */
	public void append( int frameOrientation, Item item, Style itemStyle ) {
		if (itemStyle != null) {
			item.setStyle( itemStyle );
		}
		Container frame;
		switch (frameOrientation) {
			case  Graphics.TOP:
				if (this.topFrame == null) {
					//#style topframe, frame, default
					this.topFrame = new Container( false );
					this.topFrame.setCycleListener(this);
					//#if polish.css.topframe-style
						if (this.topFrameStyle != null) {
							this.topFrame.setStyle(this.topFrameStyle);
						}
					//#endif
				}
				frame = this.topFrame;
				break;
			case  Graphics.BOTTOM:
				if (this.bottomFrame == null) {
					//#style bottomframe, frame, default
					this.bottomFrame = new Container( false );
					this.bottomFrame.setCycleListener(this);
					//#if polish.css.bottomframe-style
						if (this.bottomFrameStyle != null) {
							this.bottomFrame.setStyle(this.bottomFrameStyle);
						}
					//#endif
				}
				frame = this.bottomFrame;
				break;
			case  Graphics.LEFT:
				if (this.leftFrame == null) {
					//#style leftframe, frame, default
					this.leftFrame = new Container( false );
					this.leftFrame.setCycleListener(this);
					//#if polish.css.leftframe-style
						if (this.leftFrameStyle != null) {
							this.leftFrame.setStyle(this.leftFrameStyle);
						}
					//#endif
				}
				frame = this.leftFrame;
				break;
			case Graphics.RIGHT:
				if (this.rightFrame == null) {
					//#style rightframe, frame, default
					this.rightFrame = new Container( false );
					this.rightFrame.setCycleListener(this);
					//#if polish.css.rightframe-style
						if (this.rightFrameStyle != null) {
							this.rightFrame.setStyle(this.rightFrameStyle);
						}
					//#endif
				}
				frame = this.rightFrame;
				break;
			default:
				super.append(item, itemStyle);
				return;
		}
		frame.screen = this;
		frame.add( item );
		if (isShown()) {
			requestInit();
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#adjustContentArea(int, int, int, int, de.enough.polish.ui.Container)
	 */
	protected void adjustContentArea(int x, int y, int width, int height, Container cont) {
		this.originalContentX = x;
		this.originalContentY = y;
		this.originalContentWidth = width;
		this.originalContentHeight = height;
		if (this.leftFrame != null) {
			this.expandLeftFrame = (this.leftFrame.style.layout & Item.LAYOUT_VEXPAND) == Item.LAYOUT_VEXPAND;
			if (this.expandLeftFrame) {
				this.leftFrame.setStyleWithBackground( this.leftFrame.style, true );
			}
			int frameWidth = this.leftFrame.getItemWidth(width/2, width/2, height);
			x += frameWidth;
			width -= frameWidth;
		}
		if (this.rightFrame != null) {
			this.expandRightFrame = (this.rightFrame.style.layout & Item.LAYOUT_VEXPAND) == Item.LAYOUT_VEXPAND; 
			if (this.expandRightFrame) {
				this.rightFrame.setStyleWithBackground( this.rightFrame.style, true );
			}
			width -= this.rightFrame.getItemWidth(this.originalContentWidth/2, this.originalContentWidth/2, this.originalContentHeight);
		}
		if (this.topFrame != null ) {
			int frameHeight = this.topFrame.getItemHeight(this.originalContentWidth, this.originalContentWidth, this.originalContentHeight);
			//#if polish.css.topframe-height
				if (this.topFrameHeight != null) {
					frameHeight = this.topFrameHeight.getValue( frameHeight );
				}
			//#endif
			y += frameHeight;
			height -= frameHeight;
		}
		if (this.bottomFrame != null ) {
			int frameHeight =  this.bottomFrame.getItemHeight(this.originalContentWidth, this.originalContentWidth, this.originalContentHeight);
			//#if polish.css.bottomframe-height
				if (this.bottomFrameHeight != null) {
					frameHeight = this.bottomFrameHeight.getValue( frameHeight );
				}
			//#endif
			height -= frameHeight;
		}
		this.contentX = x;
		this.contentY = y;
		this.contentWidth = width;
		this.contentHeight = height;
		
		int yOffset = getScrollYOffset();
		// adjust scroll offset for bottom frame animation
		if(cont != null && yOffset < 0 && (yOffset + cont.getItemAreaHeight() < height) )
		{
			cont.setScrollYOffset( -(cont.getItemAreaHeight() - height));
		}
		super.adjustContentArea(x, yOffset, width, height, cont);
	}	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#checkForRequestInit(Item)
	 */
	protected boolean checkForRequestInit(Item source) {
		return super.checkForRequestInit(source)
			|| source == this.topFrame
			|| source == this.bottomFrame
			|| source == this.leftFrame
			|| source == this.rightFrame
		;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#paintScreen(javax.microedition.lcdui.Graphics)
	 */
	protected void paintScreen(Graphics g) {
		boolean paintContentContainerLast = this.currentlyActiveContainer == this.container;
		if (!paintContentContainerLast) {
			super.paintScreen(g);
		}
		if (this.leftFrame != null) {
		 	Style frameStyle = this.leftFrame.style;
			if (this.expandLeftFrame) {
			 	if ( frameStyle.background != null ) {
			 		frameStyle.background.paint( frameStyle.getMarginLeft(this.screenWidth), frameStyle.getMarginTop(this.screenHeight), this.leftFrame.backgroundWidth, this.originalContentHeight - frameStyle.getMarginTop(this.screenHeight) - frameStyle.getMarginBottom(this.screenHeight), g);
			 	}
			 	if ( frameStyle.border != null ) {
			 		frameStyle.border.paint( frameStyle.getMarginLeft(this.screenWidth), frameStyle.getMarginTop(this.screenHeight), this.leftFrame.backgroundWidth, this.originalContentHeight - frameStyle.getMarginTop(this.screenHeight) - frameStyle.getMarginBottom(this.screenHeight), g);
			 	}
			}
			int y = this.originalContentY;
			if ( (frameStyle.layout & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER ) {
				y += (this.originalContentHeight - frameStyle.getMarginBottom(this.screenHeight) - this.leftFrame.itemHeight) / 2;
			} else if ( (frameStyle.layout & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM ) {
				y += this.originalContentHeight - frameStyle.getMarginBottom(this.screenHeight) - this.leftFrame.itemHeight;
			}
			this.leftFrame.relativeX = 0;
			this.leftFrame.relativeY = y;
			this.leftFrame.paint( 0, y, 0, this.contentWidth, g );
		}
		if (this.rightFrame != null) {
		 	Style frameStyle = this.rightFrame.style;
			if (this.expandRightFrame) {
		 		int leftFrameBackgroundWidth = 0;  
		 		if(this.leftFrame != null) {
		 			leftFrameBackgroundWidth = this.leftFrame.getBackgroundWidth();
		 		}
			 	if ( frameStyle.background != null ) {
			 		frameStyle.background.paint( this.contentX + this.contentWidth + frameStyle.getMarginLeft(this.screenWidth), frameStyle.getMarginTop(this.screenHeight), leftFrameBackgroundWidth, this.originalContentHeight - frameStyle.getMarginTop(this.screenHeight) - frameStyle.getMarginBottom(this.screenHeight), g);
			 	}
			 	if ( frameStyle.border != null ) {
			 		frameStyle.border.paint( this.contentX + this.contentWidth + frameStyle.getMarginLeft(this.screenWidth), frameStyle.getMarginTop(this.screenHeight), leftFrameBackgroundWidth, this.originalContentHeight - frameStyle.getMarginTop(this.screenHeight) - frameStyle.getMarginBottom(this.screenHeight), g);
			 	}
			}
			int y = this.originalContentY;
			if ( (frameStyle.layout & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER ) {
				y += (this.originalContentHeight - frameStyle.getMarginBottom(this.screenHeight) - this.rightFrame.itemHeight) / 2;
			} else if ( (frameStyle.layout & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM ) {
				y += this.originalContentHeight - frameStyle.getMarginBottom(this.screenHeight) - this.rightFrame.itemHeight;
			}
			this.rightFrame.relativeX = this.contentWidth;
			this.rightFrame.relativeY = y;
			this.rightFrame.paint( this.contentWidth, y, this.contentWidth, this.screenWidth, g );
		}
		if (this.topFrame != null ) {
			int clipY = g.getClipY();
			this.topFrame.relativeX = this.originalContentX;
			this.topFrame.relativeY = this.originalContentY;
			int frameY = this.originalContentY;
			//#if polish.css.topframe-height
				int clipX = 0;
				int clipWidth = 0;
				int clipHeight = 0;
				if(this.topFrameHeight != null)
				{
					if (paintContentContainerLast) {
						super.paintScreen(g);
						paintContentContainerLast = false;
					}
					clipX = g.getClipX();
					clipWidth = g.getClipWidth();
					clipHeight = g.getClipHeight();
					int itemHeight = this.topFrame.itemHeight;
					int height = this.topFrameHeight.getValue( itemHeight );
					g.clipRect( clipX, frameY, clipWidth, height );
				}
			//#endif
			if (clipY < frameY + this.topFrame.itemHeight) {
				this.topFrame.paint( this.originalContentX, frameY, this.originalContentX, this.originalContentX + this.originalContentWidth, g );
			}
			//#if polish.css.topframe-height
				if (this.topFrameHeight != null) {
					g.setClip( clipX, clipY, clipWidth, clipHeight );
				}
			//#endif
		}
		if (this.bottomFrame != null ) {
			this.bottomFrame.relativeX = this.originalContentX;
			this.bottomFrame.relativeY = this.contentY + this.contentHeight;
			int frameY = this.contentY + this.contentHeight;
			//#if polish.css.bottomframe-height
				int clipX = 0;
				int clipY = 0;
				int clipWidth = 0;
				int clipHeight = 0;
				if(this.bottomFrameHeight != null)
				{
					if (paintContentContainerLast) {
						super.paintScreen(g);
						paintContentContainerLast = false;
					}
					clipX = g.getClipX();
					clipY = g.getClipY();
					clipWidth = g.getClipWidth();
					clipHeight = g.getClipHeight();
					int itemHeight = this.bottomFrame.itemHeight;
					int height = this.bottomFrameHeight.getValue( itemHeight );
					g.clipRect( clipX, frameY, clipWidth, height );
				}
			//#endif
			this.bottomFrame.paint( this.originalContentX, frameY, this.originalContentX, this.originalContentX + this.originalContentWidth, g );
			//#if polish.css.bottomframe-height
				if (this.bottomFrameHeight != null) {
					g.setClip( clipX, clipY, clipWidth, clipHeight );
				}
			//#endif
		}
		
		if (paintContentContainerLast) {
			super.paintScreen(g);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		this.isCycled = false;
		boolean handled = this.currentlyActiveContainer.handleKeyPressed(keyCode, gameAction);
		if (!handled && !this.keepContentFocused) {
			if (this.isCycled) {
				handled = true;
				this.isCycled = false;
			} else {
				Container nextFrame = getNextFrame(gameAction);
				if (nextFrame != null) {
					setActiveFrame(nextFrame, false, gameAction);
					handled = true;
				} else {
					return false;
				}
			}
		}
		return handled || super.handleKeyPressed(keyCode, gameAction);
	}
	
	/**
	 * Focuses the first resp. last element of the container 
	 * depending on the direction of the frame change
	 * @param gameAction the game action
	 * @param cont the container
	 */
	public void focusByAction(int gameAction, Container cont)
	{
		switch(gameAction)
		{
			case LEFT:
			case RIGHT:
			case DOWN :
				for (int i=0; i<cont.size(); i++) {
					Item item = cont.get(i);
					if (item.appearanceMode != Item.PLAIN) {
						cont.focusChild(i);
						break;
					}
				}
				break;
			case UP : 
				for (int i=cont.size(); --i >= 0; ) {
					Item item = cont.get(i);
					if (item.appearanceMode != Item.PLAIN) {
						cont.focusChild(i);
						break;
					}
				}
				break;
		}
	}
	
	/**
	 * Returns the next frame depending on the currently active frame and the specified gameAction
	 * @param gameAction the gameAction
	 * @return the next frame
	 */
	Container getNextFrame(int gameAction)
	{
		Container newFrame = null;
		if (this.currentlyActiveContainer == this.container ) {
			newFrame = getFrameByGameAction(gameAction, new Container[]{ this.bottomFrame, this.leftFrame, this.rightFrame, this.topFrame }, 
													new Container[]{ this.topFrame, this.leftFrame, this.rightFrame, this.bottomFrame }, 
													new Container[]{ this.leftFrame, this.topFrame, this.bottomFrame, this.rightFrame },
													new Container[]{ this.rightFrame, this.bottomFrame, this.topFrame, this.leftFrame });
		} 
		//#if polish.FramedForm.allowCycling
		else if(this.allowCycling)
		{
			if(this.currentlyActiveContainer == this.bottomFrame)
			{
				newFrame = getFrameByGameAction(gameAction, new Container[]{ this.topFrame, this.container }, 
														new Container[]{ this.container, this.topFrame }, 
														new Container[]{ this.leftFrame, this.container },
														new Container[]{ this.rightFrame, this.container });
			}
			else if(this.currentlyActiveContainer == this.topFrame)
			{
				newFrame = getFrameByGameAction(gameAction, new Container[]{ this.container, this.bottomFrame }, 
														new Container[]{ this.bottomFrame, this.container }, 
														new Container[]{ this.leftFrame, this.container },
														new Container[]{ this.rightFrame, this.container });
			}
			else if(this.currentlyActiveContainer == this.leftFrame)
			{
				newFrame = getFrameByGameAction(gameAction, new Container[]{ this.bottomFrame, this.container }, 
														new Container[]{ this.topFrame, this.container }, 
														new Container[]{ this.rightFrame, this.container },
														new Container[]{ this.container, this.rightFrame });
			}
			else if(this.currentlyActiveContainer == this.rightFrame)
			{
				newFrame = getFrameByGameAction(gameAction, new Container[]{ this.bottomFrame, this.container }, 
														new Container[]{ this.topFrame, this.container }, 
														new Container[]{ this.container, this.leftFrame },
														new Container[]{ this.leftFrame, this.container });
			}
			
		}
		else
		{
		//#endif
			if (this.container.appearanceMode != Item.PLAIN &&
					(
					(gameAction == UP && this.currentlyActiveContainer == this.bottomFrame)
					|| (gameAction == DOWN && this.currentlyActiveContainer == this.topFrame)
					|| (gameAction == LEFT && this.currentlyActiveContainer == this.rightFrame)
					|| (gameAction == RIGHT && this.currentlyActiveContainer == this.leftFrame)
					)
			){
				//System.out.println("Changing back to default container");
				newFrame = this.container;
			}
		//#if polish.FramedForm.allowCycling
		}
		//#endif
		
		return newFrame;
	}
	
	/**
	 * Returns the next frame by getting the next relevant container form the specified container arrays
	 * @param gameAction the game action
	 * @param down the containers for DOWN  
	 * @param up the containers for UP
	 * @param left the containers for LEFT
	 * @param right the containers for RIGHT
	 * @return the next frame
	 */
	Container getFrameByGameAction(int gameAction, Container[] down, Container[] up, Container[] left, Container[] right)
	{
		Container[] nextFrames;
		switch (gameAction) {
			case DOWN:
				nextFrames = down;
				break;
			case UP:
				nextFrames = up;
				break;
			case LEFT:
				nextFrames = left;
				break;
			case RIGHT:
				nextFrames = right;
				break;
			default:
				return null;
		}
		
		Container newFrame = null;
		//#if polish.FramedForm.allowCycling
		if(this.allowCycling)
		{
			newFrame = this.currentlyActiveContainer;
		}
		//#endif
		
		for (int i = 0; i < nextFrames.length; i++) {
			Container frame = nextFrames[i];
			if (frame != null && frame.appearanceMode != Item.PLAIN) {
				newFrame = frame;
				break;
			}
		}
		return newFrame;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handleKeyReleased(int, int)
	 */
	protected boolean handleKeyReleased(int keyCode, int gameAction) {
		return this.currentlyActiveContainer.handleKeyReleased(keyCode, gameAction);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handleKeyRepeated(int, int)
	 */
	protected boolean handleKeyRepeated(int keyCode, int gameAction) {
		return this.currentlyActiveContainer.handleKeyRepeated(keyCode, gameAction);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handleCommand(javax.microedition.lcdui.Command)
	 */
	protected boolean handleCommand(Command cmd) {
		if (this.currentlyActiveContainer != this.container 
			&& this.currentlyActiveContainer != null
			&& this.currentlyActiveContainer.handleCommand(cmd) ) 
		{
			return true;
		}
		return super.handleCommand(cmd);
	}

	/**
	 * Retrieves the currently focused item.
	 * 
	 * @return the currently focused item, null when none is focused.
	 */
	public Item getCurrentItem() {
		if (!this.keepContentFocused && this.currentlyActiveContainer != null) {
			return this.currentlyActiveContainer.focusedItem;
		}
		return this.container.focusedItem;
	}
	
	/**
	 * Focuses the specified frame.
	 * @param frameOrientation either Graphics.TOP, Graphics.BOTTOM, Graphics.LEFT, Graphics.RIGHT or -1 for the main/scrollable frame
	 */
	public void setActiveFrame( int frameOrientation ) {
		setActiveFrame( frameOrientation, false );
	}
	
	/**
	 * Focuses the specified frame while optionally keeping the focus on the main content frame.
	 * 
	 * @param frameOrientation either Graphics.TOP, Graphics.BOTTOM, Graphics.LEFT, Graphics.RIGHT or -1 for the main/scrollable frame
	 * @param keepMainFocus true when the focus should be kept on the main container at the same time.
	 */
	public void setActiveFrame( int frameOrientation, boolean keepMainFocus ) {
		setActiveFrame( getFrame(frameOrientation), keepMainFocus );
	}
	
	/**
	 * Activates another frame.
	 * 
	 * @param newFrame the next frame
	 * @param keepMainFocus true when the focus should be kept on the main container at the same time.
	 */
	private void  setActiveFrame(Container newFrame) {
		setActiveFrame(newFrame, false);
	}

	/**
	 * Activates another frame.
	 * 
	 * @param newFrame the next frame
	 * @param keepMainFocus true when the focus should be kept on the main container at the same time.
	 */
	protected void  setActiveFrame(Container newFrame, boolean keepMainFocus ) {
		int direction = 0;
		if (newFrame.appearanceMode != Item.PLAIN) {
			if (this.currentlyActiveContainer == this.bottomFrame) {
				direction = Canvas.UP;
			} else if (this.currentlyActiveContainer == this.topFrame) {
				direction = Canvas.DOWN;
			} else if (this.currentlyActiveContainer == this.leftFrame) {
				direction = Canvas.RIGHT;
			} else if (this.currentlyActiveContainer == this.rightFrame) {
				direction = Canvas.LEFT;
			} else {
				if (newFrame == this.bottomFrame) {
					direction = Canvas.DOWN;
				} else if (newFrame == this.topFrame) {
					direction = Canvas.UP;
				} else if (newFrame == this.leftFrame) {
					direction = Canvas.LEFT;
				} else {
					direction = Canvas.RIGHT;
				}
			}
		}
		setActiveFrame( newFrame, keepMainFocus, direction );
	}
	
	/**
	 * Activates another frame.
	 * 
	 * @param newFrame the next frame
	 * @param keepMainFocus true when the focus should be kept on the main container at the same time.
	 * @param direction the direction that should be used. e.g. Canvas.UP, DOWN
	 */
	protected void setActiveFrame(Container newFrame, boolean keepMainFocus, int direction) {
		if (newFrame == null || newFrame == this.currentlyActiveContainer) {
			return;
		}
		if (!keepMainFocus) {
			this.currentlyActiveContainer.defocus( this.currentlyActiveContainer.style );
		}
		if (newFrame.appearanceMode != Item.PLAIN) {
			newFrame.focus( StyleSheet.focusedStyle, direction );
		}
		this.currentlyActiveContainer = newFrame;
		if (newFrame == this.container) {
			int w = this.contentWidth;
			int h = this.contentHeight;
			if (newFrame.itemHeight > h) {
				w -= getScrollBarWidth();
				int ch = newFrame.getItemHeight( w, w, h );
				if (ch <= h) {
					w += getScrollBarWidth();
					newFrame.init( w, w, h );
				}
			}
			initContent(newFrame);
		}
		if (keepMainFocus) {
			if (newFrame != this.container) {
				this.keepContentFocused = true;
			}
		} else {
			this.keepContentFocused = false;
		}
		
		if (this.screenStateListener != null) {
			this.screenStateListener.screenStateChanged( this );
		}
		
	}
	
	

	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerPressed(int x, int y) {
		Container areaFrame = getFrame( x, y );
		Container activeFrame = this.currentlyActiveContainer;
		if (areaFrame != null && areaFrame != activeFrame && areaFrame.isInteractive()) {
			int yOffset = areaFrame.getScrollYOffset();
			setActiveFrame(areaFrame);
			areaFrame.setScrollYOffset(yOffset, false);
			if (areaFrame.handlePointerPressed(x - areaFrame.relativeX, y - areaFrame.relativeY)) {
				return true;
			}
		}
		Container newFrame = null;
		if ( activeFrame != null 
				&& activeFrame.handlePointerPressed(x - activeFrame.relativeX, y - activeFrame.relativeY)) 
		{ 
			newFrame = activeFrame;
		} else if ( this.container != activeFrame 
				&& this.container.handlePointerPressed(x - this.container.relativeX, y - this.container.relativeY)) 
		{
			newFrame = this.container;
		} else if ( this.topFrame != null && this.topFrame != activeFrame 
				&& this.topFrame.handlePointerPressed(x - this.topFrame.relativeX, y - this.topFrame.relativeY) ) 
		{
			newFrame = this.topFrame;
		} else if ( this.bottomFrame != null && this.bottomFrame != activeFrame
				&& this.bottomFrame.handlePointerPressed(x - this.bottomFrame.relativeX, y - this.bottomFrame.relativeY) ) 
		{
			newFrame = this.bottomFrame;
		} else if ( this.leftFrame != null && this.leftFrame != activeFrame 
				&& this.leftFrame.handlePointerPressed(x - this.leftFrame.relativeX, y - this.leftFrame.relativeY) ) 
		{
			newFrame = this.leftFrame;
		} else if ( this.rightFrame != null && this.rightFrame != activeFrame 
				&& this.rightFrame.handlePointerPressed(x - this.rightFrame.relativeX, y - this.rightFrame.relativeY) ) 
		{
			newFrame = this.rightFrame;
		}
		if (!this.keepContentFocused && newFrame != null && newFrame != activeFrame ) {
			setActiveFrame(newFrame);
		}
		return (newFrame != null);
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handlePointerPressed(int, int, ClippingRegion)
	 */
	protected boolean handlePointerDragged(int x, int y, ClippingRegion repaintRegion ) {
		Container activeFrame = this.currentlyActiveContainer;
		if ( activeFrame != null ) {
			return activeFrame.handlePointerDragged(x - activeFrame.relativeX, y - activeFrame.relativeY, repaintRegion);
		}
		return false;
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerReleased(int x, int y) {
		Container newFrame = null;
		Container activeFrame = this.currentlyActiveContainer;
		if ( activeFrame != null 
				&& activeFrame.handlePointerReleased(x - activeFrame.relativeX, y - activeFrame.relativeY)) 
		{ 
			newFrame = activeFrame;
		} else if ( this.container != activeFrame 
				&& this.container.handlePointerReleased(x - this.container.relativeX, y - this.container.relativeY)) 
		{
			newFrame = this.container;
		} else if ( this.topFrame != null && this.topFrame != activeFrame 
				&& this.topFrame.handlePointerReleased(x - this.topFrame.relativeX, y - this.topFrame.relativeY) ) 
		{
			newFrame = this.topFrame;
		} else if ( this.bottomFrame != null && this.bottomFrame != activeFrame
				&& this.bottomFrame.handlePointerReleased(x - this.bottomFrame.relativeX, y - this.bottomFrame.relativeY) ) 
		{
			newFrame = this.bottomFrame;
		} else if ( this.leftFrame != null && this.leftFrame != activeFrame 
				&& this.leftFrame.handlePointerReleased(x - this.leftFrame.relativeX, y - this.leftFrame.relativeY) ) 
		{
			newFrame = this.leftFrame;
		} else if ( this.rightFrame != null && this.rightFrame != activeFrame 
				&& this.rightFrame.handlePointerReleased(x - this.rightFrame.relativeX, y - this.rightFrame.relativeY) ) 
		{
			newFrame = this.rightFrame;
		}
		if (!this.keepContentFocused && newFrame != null && newFrame != activeFrame ) {
			setActiveFrame(newFrame);
		}
		return (newFrame != null);
	}
	//#endif
	

	//#ifdef polish.hasTouchEvents
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handlePointerTouchDown(int, int)
	 */
	public boolean handlePointerTouchDown(int x, int y) {
		Container frame = this.topFrame;
		if (frame != null && frame.handlePointerTouchDown(x - frame.relativeX, y - frame.relativeY)) {
			return true;
		}
		frame = this.bottomFrame;
		if (frame != null && frame.handlePointerTouchDown(x - frame.relativeX, y - frame.relativeY)) {
			return true;
		}
		frame = this.leftFrame;
		if (frame != null && frame.handlePointerTouchDown(x - frame.relativeX, y - frame.relativeY)) {
			return true;
		}
		frame = this.rightFrame;
		if (frame != null && frame.handlePointerTouchDown(x - frame.relativeX, y - frame.relativeY)) {
			return true;
		}
		return super.handlePointerTouchDown(x, y);
	}
	//#endif

	//#if polish.hasTouchEvents
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handlePointerTouchUp(int, int)
	 */
	public boolean handlePointerTouchUp(int x, int y) {
		Container frame = this.topFrame;
		if (frame != null && frame.handlePointerTouchUp(x - frame.relativeX, y - frame.relativeY)) {
			return true;
		}
		frame = this.bottomFrame;
		if (frame != null && frame.handlePointerTouchUp(x - frame.relativeX, y - frame.relativeY)) {
			return true;
		}
		frame = this.leftFrame;
		if (frame != null && frame.handlePointerTouchUp(x - frame.relativeX, y - frame.relativeY)) {
			return true;
		}
		frame = this.rightFrame;
		if (frame != null && frame.handlePointerTouchUp(x - frame.relativeX, y - frame.relativeY)) {
			return true;
		}
		return super.handlePointerTouchUp(x, y);
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#animate(long,ClippingRegion)
	 */
	public void animate( long currentTime, ClippingRegion repaintRegion ) {
		super.animate(currentTime,  repaintRegion);
		if ( this.leftFrame != null ) {
			this.leftFrame.animate(currentTime,  repaintRegion);
		}
		if ( this.rightFrame != null ) {
			this.rightFrame.animate(currentTime,  repaintRegion);
		}
		if ( this.topFrame != null ) {
			this.topFrame.animate(currentTime,  repaintRegion);
		}
		if ( this.bottomFrame != null ) {
			this.bottomFrame.animate(currentTime,  repaintRegion);
		}
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#getRootItems()
	 */
	protected Item[] getRootItems() {
		Container[] frames = new Container[4];
		int index = 0;
		if (this.topFrame != null) {
			frames[index] = this.topFrame;
			index++;
		}
		if (this.bottomFrame != null) {
			frames[index] = this.bottomFrame;
			index++;
		}
		if (this.leftFrame != null) {
			frames[index] = this.leftFrame;
			index++;
		}
		if (this.rightFrame != null) {
			frames[index] = this.rightFrame;
			index++;
		}
		if (index < 4) {
			Container[] activeFrames = new Container[ index ];
			System.arraycopy( frames, 0, activeFrames, 0, index );
			frames = activeFrames;
		}
		return frames;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		super.setStyle(style);
		
		//#if polish.css.leftframe-style
			this.leftFrameStyle = (Style) style.getObjectProperty("leftframe-style");
			if (this.leftFrame != null && this.leftFrameStyle != null) {
				this.leftFrame.setStyle(this.leftFrameStyle);
			}
		//#endif
		//#if polish.css.rightframe-style
			this.rightFrameStyle = (Style) style.getObjectProperty("rightframe-style");
			if (this.rightFrame != null && this.rightFrameStyle != null) {
				this.rightFrame.setStyle(this.rightFrameStyle);
			}
		//#endif
		//#if polish.css.topframe-style
			this.topFrameStyle = (Style) style.getObjectProperty("topframe-style");
			if (this.topFrame != null && this.topFrameStyle != null) {
				this.topFrame.setStyle(this.topFrameStyle);
			}
		//#endif
		//#if polish.css.bottomframe-style
			this.bottomFrameStyle = (Style) style.getObjectProperty("bottomframe-style");
			if (this.bottomFrame != null && this.bottomFrameStyle != null) {
				this.bottomFrame.setStyle(this.bottomFrameStyle);
			}
		//#endif
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		
		//#if polish.css.bottomframe-height || polish.css.topframe-height
			boolean recalculateContentArea = false;
		//#endif
		//#if polish.css.bottomframe-height
			Dimension bottomFrameHeightInt = (Dimension) style.getObjectProperty("bottomframe-height");
			if (bottomFrameHeightInt != null) {
				this.bottomFrameHeight = bottomFrameHeightInt;
				recalculateContentArea = true;
			}
		//#endif
		
		//#if polish.css.topframe-height
			Dimension topFrameHeightInt = (Dimension) style.getObjectProperty("topframe-height");
			if (topFrameHeightInt != null) {
				this.topFrameHeight = topFrameHeightInt;
				recalculateContentArea = true;
			}
		//#endif
		
		//#if polish.css.bottomframe-height || polish.css.topframe-height
			if(recalculateContentArea) {
				calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
			}
		//#endif
	}

	//#if polish.LibraryBuild
	/**
	 * Sets a style for the specified frame programmatically.
	 * The style has to be defined using the //#style preprocessing directive, e.g.
	 * <pre>
	 * //#style dynamicTopFrame
	 * framedForm.setFrameOrientation( Graphics.TOP );
	 * </pre>
	 * @param frameOrientation the frame, e.g. Graphics.TOP
	 */
	public void setFrameStyle( int frameOrientation) {
		// nothing to do
	}
	//#endif
	
	/**
	 * Sets the style for the specified frame programmatically
	 * @param frameOrientation the frame, e.g. Graphics.TOP
	 * @param style the style for the frame
	 */
	public void setFrameStyle( int frameOrientation, Style style) {
		Container frame = null;
		switch (frameOrientation) {
		case  Graphics.TOP:
			//#if polish.css.topframe-style
				this.topFrameStyle = style;
			//#endif
			frame = this.topFrame;
			break;
		case  Graphics.BOTTOM:
			//#if polish.css.bottomframe-style
				this.bottomFrameStyle = style;
			//#endif
			frame = this.bottomFrame;
			break;
		case  Graphics.LEFT:
			//#if polish.css.leftframe-style
				this.leftFrameStyle = style;
			//#endif
			frame = this.leftFrame;
			break;
		case  Graphics.RIGHT:
			//#if polish.css.rightframe-style
				this.rightFrameStyle = style;
			//#endif
			frame = this.rightFrame;
			break;
		}		
		if (style != null && frame != null) {
			frame.setStyle( style );
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#setItemCommands( ArrayList,Item)
	 */
	protected void setItemCommands( ArrayList commandsList, Item item ) {
		if (true/*this.keepContentFocused*/) {
			while (item.parent != null) {
				item = item.parent;
			}
			if (item != this.container) {
				addItemCommands( this.container, commandsList );
			} else {
				if (this.topFrame != null && this.topFrame.isFocused) {
					addItemCommands( this.topFrame, commandsList );
				}
				if (this.bottomFrame != null && this.bottomFrame.isFocused) {
					addItemCommands( this.bottomFrame, commandsList );
				}
				if (this.leftFrame != null && this.leftFrame.isFocused) {
					addItemCommands( this.leftFrame, commandsList );
				}
				if (this.rightFrame != null && this.rightFrame.isFocused) {
					addItemCommands( this.rightFrame, commandsList );
				}
			}
		}
		super.setItemCommands(commandsList, item);
	}

	private void addItemCommands(Container cont, ArrayList commandsList )
	{
		Item item = cont.getFocusedItem();
		if (item != null) {
			item.addCommands(commandsList);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#sizeChanged(int, int)
	 */
	public void sizeChanged(int width, int height) {
		if (this.leftFrame != null) {
			this.leftFrame.onScreenSizeChanged(width, height);
		}
		if (this.rightFrame != null) {
			this.rightFrame.onScreenSizeChanged(width, height);
		}
		if (this.topFrame != null) {
			this.topFrame.onScreenSizeChanged(width, height);
		}
		if (this.bottomFrame != null) {
			this.bottomFrame.onScreenSizeChanged(width, height);
		}
		super.sizeChanged(width, height);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#releaseResources()
	 */
	public void releaseResources() {
		super.releaseResources();
		
		if (this.leftFrame != null) {
			this.leftFrame.releaseResources();
		}
		if (this.rightFrame != null) {
			this.rightFrame.releaseResources();
		}
		if (this.topFrame != null) {
			this.topFrame.releaseResources();
		}

		if (this.bottomFrame != null) {
			this.bottomFrame.releaseResources();
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#destroy()
	 */
	public void destroy() {
		super.destroy();
		
		if (this.leftFrame != null) {
			this.leftFrame.destroy();
		}
		if (this.rightFrame != null) {
			this.rightFrame.destroy();
		}
		if (this.topFrame != null) {
			this.topFrame.destroy();
		}

		if (this.bottomFrame != null) {
			this.bottomFrame.destroy();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.CycleListener#onCycle(de.enough.polish.ui.Item, int)
	 */
	public boolean onCycle(Item item, int direction) {
		if (!this.keepContentFocused) {
			int gameAction;
			if (direction == CycleListener.DIRECTION_TOP_TO_BOTTOM) {
				gameAction = UP;
			} else if (direction == CycleListener.DIRECTION_BOTTOM_TO_TOP) {
				gameAction = DOWN;
			} else if (direction == CycleListener.DIRECTION_LEFT_TO_RIGHT) {
				gameAction = LEFT;
			} else {
				gameAction = RIGHT;
			}
			Container newFrame = getNextFrame(gameAction);
			if (newFrame != null && newFrame != this.currentlyActiveContainer) {
				this.isCycled = true;
				setActiveFrame(newFrame, false, gameAction );
				return false;
			}
		}
		return true;
	}

	
	
	
	

//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.Screen#focus(int, de.enough.polish.ui.Item, boolean)
//	 */
//	public void focus(int index, Item item, boolean force)
//	{
//		// TODO robertvirkus implement focus
//		super.focus(index, item, force);
//		
//		/**
//		 * Focuses the specified item.
//		 * 
//		 * @param index the index of the item which is already shown on this screen.
//		 * @param item the item which is already shown on this screen.
//		 * @param force true when the item should be focused even when it is inactive (like a label for example)
//		 */
//		public void focus(int index, Item item, boolean force) {
//			if (index != -1 && item != null && (item.appearanceMode != Item.PLAIN || force ) ) {
//				//#debug
//				System.out.println("Screen: focusing item " + index );
//				this.container.focus( index, item, 0 );
//				if (index == 0) {
//					this.container.setScrollYOffset( 0, false );
//				}
//			} else if (index == -1) {
//				this.container.focus( -1 );
//			} else {
//				//#debug warn
//				System.out.println("Screen: unable to focus item (did not find it in the container or is not activatable) " + index);
//			}
//		}
//	}
	
	
	
	
	
	
	
	
	
}
