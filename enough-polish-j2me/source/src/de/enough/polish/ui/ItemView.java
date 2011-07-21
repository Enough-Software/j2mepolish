//#condition polish.usePolishGui

/*
 * Created on Nov 27, 2006 at 1:06:40 PM.
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

import javax.microedition.lcdui.Graphics;

import de.enough.polish.io.Serializable;

/**
 * <p>An item view can take over the rendering of an item.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Nov 27, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class ItemView implements Serializable{
	
	protected int contentWidth;
	protected int contentHeight;
	protected int availableWidth;
	protected int availableHeight;
	protected int paddingVertical;
	protected int paddingHorizontal;
	protected int layout;
	protected boolean isLayoutCenter;
	protected boolean isLayoutRight;
	protected transient Item parentItem;
	protected boolean isFocused;
	
	/**
	 * Initializes the margin of the parent item
	 * Subclasses can override this (e.g. the container embedded in a screen)
	 * @param style the style
	 * @param availWidth the available width
	 */
	protected void initMargin(Style style, int availWidth) {
		this.parentItem.initMargin(style, availWidth);
	}
	
	/**
	 * Initializes the padding of the parent item
	 * Subclasses can override this (e.g. the container embedded in a screen)
	 * @param style the style
	 * @param availWidth the available width
	 */
	protected void initPadding(Style style, int availWidth) {
		this.parentItem.initPadding(style, availWidth);
	}
	
	/**
	 * Initializes this item view. 
	 * This method saves the available width and height and then calls initContent(int, int, int).
	 * Please always call the super.init(..) method when overriding this method.
	 * 
	 * @param parent the parent item
	 * @param firstLineWidth the maximum width of the first line 
	 * @param availWidth the maximum width of the view
	 * @param availHeight the maximum height of the view
	 * @see #initContent(Item, int, int, int)
	 * @see #availableWidth
	 * @see #availableHeight
	 */
	protected void init( Item parent, int firstLineWidth, int availWidth, int availHeight ) {
		this.parentItem = parent;
		this.availableWidth = availWidth;
		this.availableHeight = availHeight;
		this.paddingHorizontal = parent.paddingHorizontal;
		this.paddingVertical = parent.paddingVertical;
		initContent( parent, firstLineWidth, availWidth, availHeight );
	}
	
	/**
	 * Initialises this item view. 
	 * The implementation needs to calculate and set the contentWidth and 
	 * contentHeight fields. 
	 * 
	 * @param parent the parent item
	 * @param firstLineWidth the maximum width of the first line 
	 * @param availWidth the maximum width of the view
	 * @param availHeight the maximum height of the view
	 * @see #contentWidth
	 * @see #contentHeight
	 */
	protected abstract void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight);

	/**
	 * Paints this item view.
	 * 
	 * @param parent the parent item
	 * @param x the left start position
	 * @param y the upper start position
	 * @param leftBorder the left border, nothing must be painted left of this position
	 * @param rightBorder the right border, nothing must be painted right of this position
	 * @param g the Graphics on which this item should be painted.
	 */
	protected abstract void paintContent( Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g );
	
	/**
	 * Calls the original initContent method on the parent.
	 * This is only useful when the parent should later onward paint (parts) of the item's interface.
	 * contentWidth and contentHeight fields are set according to the parent results.
	 * 
	 * @param parent the parent item
	 * @param firstLineWidth the maximum width of the first line 
	 * @param availWidth the maximum width of any following lines
	 * @param availHeight TODO
	 */
	protected void initContentByParent( Item parent, int firstLineWidth, int availWidth, int availHeight) {
		parent.initContent(firstLineWidth, availWidth, availHeight);
		this.contentWidth = parent.contentWidth;
		this.contentHeight = parent.contentHeight;
	}

	/**
	 * Paints this item view by the parent.
	 * This could make sense if a specific view is only useful for a special case of the parent item.
	 * 
	 * @param parent the parent item
	 * @param x the left start position
	 * @param y the upper start position
	 * @param leftBorder the left border, nothing must be painted left of this position
	 * @param rightBorder the right border, nothing must be painted right of this position
	 * @param g the Graphics on which this item should be painted.
	 */
	protected void paintContentByParent( Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g ) {
		parent.paintContent(x, y, leftBorder, rightBorder, g);
	}


		
	/**
	 * Sets the focus to this container view.
	 * The default implementation sets the style and the field "isFocused" to true.
	 * 
	 * @param focusstyle the appropriate style.
	 * @param direction the direction from the which the focus is gained, 
	 *        either Canvas.UP, Canvas.DOWN, Canvas.LEFT, Canvas.RIGHT or 0.
	 *        When 0 is given, the direction is unknown.1
	 * 
	 */
	public void focus(Style focusstyle, int direction) {
		this.isFocused = true;
		if (focusstyle != null) {
			setStyle( focusstyle );
		}
	}

	
	/**
	 * Notifies this view that the parent container is not focused anymore.
	 * Please call super.defocus() when overriding this method.
	 * The default implementation calls setStyle( originalStyle )
	 * and sets the field "isFocused" to false.
	 * 
	 * @param originalStyle the previous used style, may be null.
	 */
	protected void defocus( Style originalStyle ) {
		this.isFocused = false;
		if (originalStyle != null) {
			setStyle( originalStyle );
		}
	}
	
	
	/**
	 * Sets the style for this view.
	 * The style can include additional parameters for the view.
	 * Subclasses should call super.setStyle(style) first.
	 * 
	 * @param style the style
	 */
	protected void setStyle( Style style ) {
		//#debug
		System.out.println("Setting style for " + this  );
		if (this.parentItem != null) {
			this.paddingHorizontal = this.parentItem.paddingHorizontal;
			this.paddingVertical = this.parentItem.paddingVertical;
		}
		this.layout = style.layout;
		// horizontal styles: center -> right -> left
		if ( ( this.layout & Item.LAYOUT_CENTER ) == Item.LAYOUT_CENTER ) {
			this.isLayoutCenter = true;
			this.isLayoutRight = false;
		} else {
			this.isLayoutCenter = false;
			if ( ( this.layout & Item.LAYOUT_RIGHT ) == Item.LAYOUT_RIGHT ) {
				this.isLayoutRight = true;
			} else {
				this.isLayoutRight = false;
				// meaning: layout == Item.LAYOUT_LEFT
			}
		}
		setStyle( style, true );
	}
	

	/**
	 * Sets the style for this view and is used to specify animatable CSS attribute.
	 * The style can include additional parameters for the view.
	 * Subclasses should call super.setStyle(style, resetStyle) first.
	 * 
	 * @param style the style
	 * @param resetStyle true when default style settings should be applied when nothing is set
	 */
	protected void setStyle( Style style, boolean resetStyle ) {
		// subclasses may implement this
		if (!resetStyle && this.parentItem != null) {
			this.paddingHorizontal = this.parentItem.paddingHorizontal;
			this.paddingVertical = this.parentItem.paddingVertical;
		}
	}
	

	/**
	 * Requests the re-initialization of this item view.
	 * This should be called when this view type changes its size.
	 */
	public void requestInit(){
		if (this.parentItem != null) {
			this.parentItem.requestInit();
		}
	}
	
	/**
	 * Removes the background from the parent container so that the view implementation can paint it itself.
	 * 
	 * @return the background of the parent, can be null
	 */
	public Background removeParentBackground() {
		if (this.parentItem == null) {
			//#debug warn
			System.out.println("Unable to remove parent background when parent field is not set.");
			return null;
		}
		Background bg = this.parentItem.background;
		this.parentItem.background = null;
		return bg;
	}
	
	/**
	 * Removes the border from the parent container so that the view implementation can paint it itself.
	 * 
	 * @return the border of the parent, can be null
	 */
	public Border removeParentBorder() {
		if (this.parentItem == null) {
			//#debug warn
			System.out.println("Unable to remove parent border when parentContainer field is not set.");
			return null;
		}
		Border border = this.parentItem.border;
		this.parentItem.border = null;
		return border;
	}
	
	/**
	 * Removes the set border from an item.
	 * 
	 * @param item the item to remove the border from
	 * @return the formerly set border
	 * @see #removeItemBackground(Item)
	 * @see #addItemBorder(Item, Border)
	 */
	protected Border removeItemBorder(Item item) {
		Border border = item.border;
		item.border = null;
		return border;
	}

	/**
	 * Removes the set background from an item.
	 * 
	 * @param item the item to remove the background from
	 * @return the formerly set background
	 * @see #removeItemBorder(Item)
	 * @see #addItemBackground(Item, Background)
	 */
	protected Background removeItemBackground(Item item) {
		Background background = item.background;
		item.background = null;
		return background;
	}
	
	/**
	 * Adds a background to an item again
	 * @param item to which the background should be added
	 * @param background the background
	 * @see #removeItemBackground(Item)
	 * @see #addItemBackgroundBorder(Item, Background, Border)
	 */
	protected void addItemBackground(Item item, Background background) {
		item.background = background;
	}
	
	/**
	 * Adds a border to an item again
	 * @param item to which the border should be added
	 * @param border the border
	 * @see #removeItemBorder(Item)
	 * @see #addItemBackgroundBorder(Item, Background, Border)
	 */
	protected void addItemBorder(Item item, Border border) {
		item.border = border;
	}
	
	/**
	 * Adds a background and a border to an item again
	 * @param item to which the border should be added
	 * @param background the background
	 * @param border the border
	 * @see #removeItemBackground(Item)
	 * @see #removeItemBorder(Item)
	 */
	protected void addItemBackgroundBorder( Item item, Background background, Border border ) {
		item.background = background;
		item.border = border;		
	}
	
	/**
	 * Animates this item.
	 * Subclasses can override this method to create animations.
	 * The default implementation animates the background and the item view if present.
	 * 
	 * @param currentTime the current time in milliseconds
	 * @param repaintRegion the repaint area that needs to be updated when this item is animated
	 * @see Item#getAbsoluteX()
	 * @see Item#getAbsoluteY()
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		if (animate()) {
			addFullRepaintRegion(this.parentItem, repaintRegion);
		}
	}
	
	/**
	 * Adds the complete item's dimensions to the repaint region.
	 * This is usually used within the animate method.
	 * 
	 * @param item the item
	 * @param repaintRegion the region to which the item's positions are added
	 */
	protected void addFullRepaintRegion( Item item, ClippingRegion repaintRegion ) {
		repaintRegion.addRegion( item.getAbsoluteX(), 
				item.getAbsoluteY(), 
				item.itemWidth,
				item.itemHeight 
		);
	}

	
	/**
	 * Animates this view - please use animate(long, ClippingRegion) instead, if possible
	 * 
	 * @return true when the view was actually animated.
	 * @see #animate(long, ClippingRegion)
	 */
	public boolean animate() {
		return false;
	}

	/**
	 * Notifies this view that it is about to be shown (again).
	 * The default implementation does nothing.
	 */
	public void showNotify() {
		// subclasses can override this
	}
	
	/**
	 * Called by the system to notify the item that it is now completely
	 * invisible, when it previously had been at least partially visible.  No
	 * further <code>paint()</code> calls will be made on this item
	 * until after a <code>showNotify()</code> has been called again.
	 */
	public void hideNotify() {
		// subclasses can override this
	}
	
	/**
	 * Retrieves the screen to which this view belongs to.
	 * This is necessary since the getScreen()-method of item has only protected
	 * access. The screen can be useful for setting the title for example. 
	 * 
	 * @return the screen in which this view is embedded.
	 */
	protected Screen getScreen() {
		return this.parentItem.getScreen();
	}
	
	/**
	 * Handles the given keyPressed event.
	 * The default implementation just calls getNextItem() and focuses the returned item.
	 * 
	 * @param keyCode the key code
	 * @param gameAction the game action like Canvas.UP etc
	 * @return true when the key was handled.
	 */
	public boolean handleKeyPressed( int keyCode, int gameAction) {
		return false;
	}
	
	/**
	 * Handles the given keyReleased event when the currently focused item was not able to handle it.
	 * The default implementation just calls getNextItem() and focuses the returned item.
	 * 
	 * @param keyCode the key code
	 * @param gameAction the game action like Canvas.UP etc
	 * @return true when the key was handled.
	 */
	public boolean handleKeyReleased( int keyCode, int gameAction) {
		return false;
	}

	/**
	 * Adjusts the given position to the content area of this view
	 * @param x the horizontal position relative to the parent item's outer left border
	 * @param y the vertical position relative to the parent item's outer top border
	 * @return the adjusted position relative to this view's content area
	 */
	protected Point adjustToContentArea( int x, int y ) {
		x -= this.parentItem.getContentX();
		y -= this.parentItem.getContentY();
		return new Point( x, y );
	}
	
	

	/**
	 * Handles pointer pressed events.
	 * This is an optional feature that doesn't need to be implemented by subclasses, since the parent container already forwards the event to the appropriate item (when this method returns false).
	 * The default implementation just returns false.
	 * You only need to implement this method when there are pointer events:
	 * <pre>
	 * //#if polish.hasPointerEvents
	 * </pre>
	 * 
	 * @param x the x position of the event relative to the item's horizontal left edge
	 * @param y the y position of the event relative to the item's vertical top edge
	 * @return true when the event has been handled. When false is returned the parent container
	 *         will forward the event to the affected item.
	 */
	public boolean handlePointerPressed(int x, int y) {
		return false;
	}
	
	/**
	 * Handles the event when a pointer has been released at the specified position.
	 * The default implementation just returns false.
	 * You only need to implement this method when there are pointer events:
	 * <pre>
	 * //#if polish.hasPointerEvents
	 * </pre>
	 *    
	 * @param x the x position of the event relative to the item's horizontal left edge
	 * @param y the y position of the event relative to the item's vertical top edge
	 * @return true when the pressing of the pointer was actually handled by this item.
	 */
	public boolean handlePointerReleased( int x, int y ) {
		return false;
	}
	
	/**
	 * Handles the event when a pointer has been dragged to the specified position.
	 * The default implementation adds a repaint region when handlePointerDragged(x,y) returned true.
	 * You only need to implement this method when there are pointer events:
	 * <pre>
	 * //#if polish.hasPointerEvents
	 * </pre>
	 *    
	 * @param x the x position of the event relative to the item's horizontal left edge
	 * @param y the y position of the event relative to the item's vertical top edge
	 * @param repaintRegion the repaint region into which the repaint area is marked when the event is handled
	 * @return true when the pressing of the pointer was actually handled by this item.
	 * @see #handlePointerDragged(int, int)
	 * @see Item#addRepaintArea(ClippingRegion)
	 */
	public boolean handlePointerDragged(int x, int y, ClippingRegion repaintRegion)
	{
		if (handlePointerDragged(x, y)) {
			this.parentItem.addRepaintArea(repaintRegion);
			return true;
		}
		return false;
	}
	
	/**
	 * Handles the event when a pointer has been dragged to the specified position.
	 * The default implementation just returns false.
	 * You only need to implement this method when there are pointer events:
	 * <pre>
	 * //#if polish.hasPointerEvents
	 * </pre>
	 *    
	 * @param x the x position of the event relative to the item's horizontal left edge
	 * @param y the y position of the event relative to the item's vertical top edge
	 * @return true when the pressing of the pointer was actually handled by this item.
	 */
	public boolean handlePointerDragged(int x, int y)
	{
		return false;
	}
	
	/**
	 * Handles a touch down/press event. 
	 * This is similar to a pointerPressed event, however it is only available on devices with screens that differentiate
	 * between press and touch events (read: BlackBerry Storm).
	 * 
	 * @param x the horizontal pixel position of the touch event relative to the parent item's left position
	 * @param y  the vertical pixel position of the touch event relative to the parent item's top position
	 * @return true when the event was handled
	 */
	public boolean handlePointerTouchDown( int x, int y ) {
		return false;
	}
	

	/**
	 * Handles a touch up/release event. 
	 * This is similar to a pointerReleased event, however it is only available on devices with screens that differentiate
	 * between press and touch events (read: BlackBerry Storm).
	 * 
	 * @param x the horizontal pixel position of the touch event relative to the parent item's left position
	 * @param y  the vertical pixel position of the touch event relative to the parent item's top position
	 * @return true when the event was handled
	 */
	public boolean handlePointerTouchUp( int x, int y ) {
		return false;
	}
	
	/**
	 * Implementation that are valid only for specific item classes can override this method so that they cannot be accidently attached to unsupported classes.
	 * This allows casting without checking the parent item with instanceof in each method, for example.
	 * The default implementation just returns true for any case.
	 * 
	 * @param parent the parent item
	 * @param style the style
	 * @return true when the view can be used for the parent item.
	 */
	protected boolean isValid( Item parent, Style style ) {
		return true;
	}
	
	/**
	 * Removes this view from the parent item.
	 */
	protected void removeViewFromParent() {
		//#ifdef polish.css.view-type
			this.parentItem.view = null;
		//#endif
	}
	
	/**
	 * Is called when an item is pressed using the FIRE game action.
	 * The default implementation fowards this to the parent item.
	 * 
	 * @return true when the item contains an action associated with FIRE
	 */
	protected boolean notifyItemPressedStart() {
		return this.parentItem.notifyItemPressedStart();
	}
	
	/**
	 * Is called when pressing an item is finished, usually when the FIRE key is released
	 * The default implementation fowards this to the parent item.
	 */
	protected void notifyItemPressedEnd() {
		this.parentItem.notifyItemPressedEnd();
	}

	/**
	 * @param background
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param g
	 */
	public void paintBackground(Background background, int x, int y, int width, int height, Graphics g) {
		background.paint(x, y, width, height, g);
	}

	/**
	 * @param border
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param g
	 */
	public void paintBorder(Border border, int x, int y, int width, int height, Graphics g) {
		border.paint(x, y, width, height, g);
	}

	/**
	 * Releases all resources that are not required to keep the state of this view.
	 * The default implementation does free nothing and only sets the "isInitialized" flag if the parent item.
	 */
	public void releaseResources()
	{
		if(this.parentItem != null)
		{
			this.parentItem.isInitialized = false;
		}
	}
	
	/**
	 * Destroys the containerview by removing references to the parent item
	 */
	public void destroy() {
		releaseResources();
		
		//make sure parent item is dereferenced, else possible mem leak
		this.parentItem = null;
	}
	
	/**
	 * Call this to notify an item that it is being pressed using a FIRE game action or similar
	 * 
	 * @param item the item that should be notified
	 * @return true when the item requests a repaint after this action
	 */
	protected boolean notifyItemPressedStart(Item item) {
		return item.notifyItemPressedStart();
	}
	
	/**
	 * Call this to notify an item that it is not being pressed anymore after a FIRE game action or similar
	 * @param item the item that should be notified
	 */
	protected void notifyItemPressedEnd(Item item) {
		item.notifyItemPressedEnd();
	}

	/**
	 * Sets the content width of this view.
	 * Subclasses can override this to react to content width changes
	 * @param width the new content width in pixel
	 */
	protected void setContentWidth(int width) {
		this.contentWidth = width;
	}

	/**
	 * Sets the content height of this item.
	 * Subclasses can override this to react to content height changes
	 * @param height the new content height in pixel
	 */
	protected void setContentHeight(int height) {
		this.contentHeight = height;
	}

	/**
	 * Notifies this item about a new screen size.
	 * The default implementation is empty.
	 * @param screenWidth the screen width
	 * @param screenHeight the screen height
	 */
	protected void onScreenSizeChanged(int screenWidth, int screenHeight) {
		// you can override this
	}

	
}
