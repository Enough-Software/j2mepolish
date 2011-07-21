//#condition polish.usePolishGui

/*
 * Created on 22-Feb-2006 at 19:09:37.
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
import javax.microedition.lcdui.Image;

/**
 * <p>Realizes a scrollbar for any J2ME Polish screens.</p>
 * <p>
 *    The scrollbar needs to be activated using the &quot;polish.useScrollBar&quot;
 *    variable:
 * </p>
 *    <pre>
 *    &lt;variable name=&quot;polish.useScrollBar&quot; value=&quot;true&quot; /&gt;
 *    </pre>
 * <p>
 *   If you don't want the scrollbar to handle pointer/drag events itself, you can specify 
 *   &quot;polish.ScrollBar.handlePointerEvents&quot;:
 *   <pre>
 *    &lt;variable name=&quot;polish.ScrollBar.handlePointerEvents&quot; value=&quot;false&quot; /&gt;
 *   </pre>
 * <p>Design the scrollbar using the predefined &quot;scrollbar&quot; style. You
 *    can also specify a screen specific style by defining the &quot;scrollbar-style&quot; CSS attribute.
 *    <br />
 *    Scrollbars support following additional attributes (apart from the background etc):   
 * </p>
 * <ul>
 * 	<li><b>scrollbar-slider-width</b>: The width of the slider in pixels, is ignored when a slider-image is defined.</li>
 * 	<li><b>scrollbar-slider-color</b>: The color of the slider, is ignored when a slider-image is defined.</li>
 * 	<li><b>scrollbar-slider-image</b>: The image used for the slider.</li>
 * 	<li><b>scrollbar-slider-image-repeat</b>: Either &quot;true&quot; or &quot;false&quot;. When &quot;true&quot; is 
 * 							given, the slider image is repeated vertically, if necessary.</li>
 * 	<li><b>scrollbar-slider-mode</b>: The mode of the slider, either &quot;area&quot;,
 * 							&quot;item&quot; or &quot;page&quot;. In the &quot;area&quot; mode the slider represents the
 *                          position of the current selection relativ to the complete width and height.
 *                          In the &quot;item&quot; mode the index of currently selected item
 *                          is taken into account relative to the number of items.
 *                          In the &quot;page&quot; mode the position and height of the currently visible area is 
 *                          shown relative to the complete height of that screen.
 *                          The default mode is &quot;page&quot;.
 * </li>
 * 	<li><b>scrollbar-position</b>: either &quot;left&quot; or &quot;right&quot; (not yet supported).</li>
 * 	<li><b></b>: </li>
 * </ul>
 * <p>You can exchange the default implementation with the &quot;polish.classes.ScrollBar&quot;
 *    variable.
 * </p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        22-Feb-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ScrollBar extends Item {
	
	private static final int MODE_AREA = 0;
	private static final int MODE_ITEM = 1;
	private static final int MODE_PAGE = 2;
	
	protected int sliderColor;
	protected Dimension sliderWidth;
	//#if polish.css.scrollbar-slider-image
		protected Image sliderImage;
		//#if polish.css.scrollbar-slider-image-repeat
			protected boolean repeatSliderImage;
			protected int repeatSliderNumber;
		//#endif
	//#endif
	//#if polish.css.scrollbar-slider-mode
		protected int sliderMode = MODE_PAGE;
	//#endif
	//#if polish.css.scrollbar-slider-background
		protected Background sliderBackground;
	//#endif
	protected boolean hideSlider = true;
	protected int sliderY;
	protected int sliderHeight;
	protected int sliderMinHeight = 1;
	protected int scrollBarHeight;
	//#if polish.css.opacity && polish.midp2 && polish.css.scrollbar-fadeout
		//#define tmp.fadeout
		private int startOpacity = 255;
	//#endif
	protected boolean isVisible;
	protected boolean overlap;
	private int screenAvailableContentHeight;
	private int screenActualContentHeight;
	private boolean isPointerDraggedHandled;
	protected boolean isPointerPressedHandled;

	/**
	 * Creates a new default scrollbar
	 */
	public ScrollBar() {
		super();
	}

	/**
	 * Creates a new styled scrollbar
	 * 
	 * @param style the style
	 */
	public ScrollBar(Style style) {
		super(style);
	}

	/**
	 * Initializes this scrollbar.
	 * @param screenWidth the width of the screen
	 * @param screenAvailableHeight the height available for the content within the screen
	 * @param screenContentHeight the height of the content area of the screen
	 * @param contentYOffset the y offset for the content in the range of [-(screenContentHeight-screenAvailableHeight)...0]
	 * @param selectionStart the start of the selection relative to the screenContentHeight
	 * @param selectionHeight the height of the current selection
	 * @param focusedIndex the index of currently focused item
	 * @param numberOfItems the number of available items 
	 * @return the item width of the scroll bar.
	 */
	public int initScrollBar( int screenWidth, int screenAvailableHeight, int screenContentHeight, int contentYOffset, int selectionStart, int selectionHeight, int focusedIndex, int numberOfItems ) {
		//#debug
		System.out.println("initScrollBar( screenWidth=" + screenWidth + ", screenAvailableHeight=" + screenAvailableHeight + ", screenContentHeight=" + screenContentHeight + ", contentYOffset=" + contentYOffset + ", selectionStart=" + selectionStart + ", selectionHeight=" + selectionHeight + ", focusedIndex=" + focusedIndex + ", numberOfItems=" + numberOfItems + ")");
		this.screenActualContentHeight = screenContentHeight;
		this.screenAvailableContentHeight = screenAvailableHeight;
		if ( screenAvailableHeight >= screenContentHeight || screenContentHeight == 0) {
			this.isVisible = false;
			return 0;
		}
		int lastSliderY = this.sliderY;
		int lastSliderHeight = this.sliderHeight;
		int nextSliderY;
		int nextSliderHeight;
		this.isVisible = true;
		this.scrollBarHeight = screenAvailableHeight;
		//#if polish.css.scrollbar-slider-mode
			if ( this.sliderMode == MODE_ITEM && focusedIndex != -1) {
				//System.out.println("using item");
				int chunkPerSlider = (screenAvailableHeight << 8) / numberOfItems; 
				nextSliderY = (chunkPerSlider * focusedIndex) >>> 8;
				nextSliderHeight = chunkPerSlider >>> 8;
			} else if ( this.sliderMode == MODE_AREA && selectionHeight != 0 ) {
				// take the currently selected area
				//System.out.println("using area");
				nextSliderY = ( (selectionStart - contentYOffset) * screenAvailableHeight) / screenContentHeight;
				nextSliderHeight = (selectionHeight * screenAvailableHeight) / screenContentHeight;					
			} else {
		//#endif
				// use the page dimensions:
				//System.out.println("using page");
				nextSliderY = (-contentYOffset  * screenAvailableHeight) / screenContentHeight;
				nextSliderHeight = (screenAvailableHeight * screenAvailableHeight) / screenContentHeight;
		//#if polish.css.scrollbar-slider-mode
			}
		//#endif
		//#if polish.css.scrollbar-slider-image && polish.css.scrollbar-slider-image-repeat
			if (this.repeatSliderImage && this.sliderImage != null ) {
				if (nextSliderHeight > this.sliderImage.getHeight()) {
					this.repeatSliderNumber = this.sliderHeight / this.sliderImage.getHeight(); 
				} else {
					this.repeatSliderNumber = 1;
				}
			}
		//#endif
		//#debug
		System.out.println("sliderY=" + nextSliderY + ", sliderHeight=" + this.sliderHeight);
		if (nextSliderY < 0) {
			nextSliderHeight += nextSliderY;
			nextSliderY = 0;
		}
		this.sliderY = nextSliderY;
		if (!this.isInitialized || (this.scrollBarHeight != this.itemHeight) ) {
			init( screenWidth, screenWidth, screenAvailableHeight );
		}
		this.itemHeight = this.scrollBarHeight;
		//#if tmp.fadeout
			if (lastSliderY != nextSliderY || lastSliderHeight != nextSliderHeight) {
				this.opacityRgbData = null;
				this.opacity = this.startOpacity;
			}
		//#endif
			
		// adjust slider height if it not visible
		if (nextSliderHeight < this.sliderMinHeight) {
			nextSliderHeight = this.sliderMinHeight;
		} else if (nextSliderHeight < 0) {
			nextSliderHeight = 0;
		}
		this.sliderHeight = nextSliderHeight;
			
		return this.itemWidth;
//		int w = this.itemWidth;
//		//#if tmp.fadeout
//			if (this.fadeOut) {
//				w = 0;
//			}
//		//#endif
//		return w;
	}
	
	
	
	/**
	 * Resets the animation status - when the opcaity is defined, it will be set to the start opacity again
	 *
	 */
	public void resetAnimation() {
		//#if tmp.fadeout
			this.opacity = this.startOpacity;
		//#endif
	}
	

	//#if tmp.fadeout
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate()
	 */
	public boolean animate()
	{
		boolean animated = false;
		int[] rgbData = this.opacityRgbData;
		if (this.isVisible && this.overlap && rgbData != null && this.opacity > 0)  {
//			System.out.println("actual=" + this.screenActualContentHeight + ", available=" + this.screenAvailableContentHeight );
			this.opacity -= 10;
			if (this.opacity <= 0) {
				this.opacity = 0;
			} else {
				int alpha = (this.opacity << 24) ;
				for (int i = 0; i < rgbData.length; i++)
				{
					rgbData[i] = rgbData[i] & 0x00ffffff | alpha;
				}
			}
			animated = true;
		}
		return animated | super.animate();
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
//		//#if polish.css.scrollbar-slider-hide
//			if (!this.hideSlider) {
//			} else {
//				this.contentHeight = 0;
//				this.contentWidth = 0;
//			}
//		//#endif
		if (this.sliderWidth != null) {
			this.contentWidth = this.sliderWidth.getValue(availWidth);
		} else {
			this.contentWidth = 2;
		}
		this.contentHeight = this.scrollBarHeight - ( this.paddingTop + this.paddingBottom + this.marginTop + this.marginBottom + getBorderWidthTop() + getBorderWidthBottom());

	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if (!this.isVisible) {
			//System.out.println("scrollbar is not visible - aborting paint");
			return;
		}
//		System.out.println("painting scrollbar at x=" + x + ", y=" + y + " width=" + this.itemWidth + ", height=" + this.itemHeight + ", screenWidth=" + this.screen.screenWidth); //+ " pixel=" + Integer.toHexString( this.opacityRgbData[ this.itemWidth * this.itemHeight / 2] ) ) ;
//		System.out.println("clipping: x=" + g.getClipX() + ", clipWidth=" + g.getClipWidth() );
//		System.out.println("opacity=" + this.opacity);
//		//#if polish.css.scrollbar-slider-hide
//			if (this.hideSlider) {
//		//#endif
//				//x -= this.itemWidth;
//		//#if polish.css.scrollbar-slider-hide
//			}
//		//#endif
		super.paint(x, y, leftBorder, rightBorder, g);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) 
	{
		boolean doClip = false;
		int clipX = 0;
		int clipY = 0;
		int clipWidth = 0;
		int clipHeight = 0;
		if (this.sliderY + this.sliderHeight > this.contentHeight) {
			doClip = true;
			clipX = g.getClipX();
			clipY = g.getClipY();
			clipWidth = g.getClipWidth();
			clipHeight = g.getClipHeight();
			g.clipRect( clipX, y, clipWidth, this.contentHeight );
		}
		//#if polish.css.scrollbar-slider-background
			if (this.sliderBackground != null) {
				this.sliderBackground.paint(x, y + this.sliderY, this.contentWidth, this.sliderHeight, g);
			} else {
		//#endif
			//#if polish.css.scrollbar-slider-image
				if (this.sliderImage != null) {
					//System.out.println("painting scrollbar image");
					//#if polish.css.scrollbar-slider-image-repeat
						int imageHeight = this.sliderImage.getHeight();
						y = this.sliderY;
						for (int i=this.repeatSliderNumber; --i >= 0; ) {
							g.drawImage(this.sliderImage, x, this.sliderY, Graphics.TOP | Graphics.LEFT );
							y += imageHeight;
						}
					//#else
						g.drawImage(this.sliderImage, x, this.sliderY, Graphics.TOP | Graphics.LEFT );
					//#endif
				} else {
			//#endif
					//System.out.println("Painting slider at " + x + "," + (y + this.sliderY) + ", width=" + this.sliderWidth + ", height=" + this.sliderHeight);
					g.setColor( this.sliderColor );
					g.fillRect(x, y + this.sliderY, this.contentWidth, this.sliderHeight);
			//#if polish.css.scrollbar-slider-image
				}
			//#endif
		//#if polish.css.scrollbar-slider-background
			}
		//#endif
		if (doClip) {
			g.setClip( clipX, clipY, clipWidth, clipHeight );
		}
	}

	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector() {
		return "scrollbar";
	}
	//#endif


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.scrollbar-slider-image
			String url = style.getProperty("scrollbar-slider-image");
			if (url != null) {
				try {
					this.sliderImage = StyleSheet.getImage(url, url, false);
					this.sliderWidth = new Dimension( this.sliderImage.getWidth() );
					this.sliderHeight = this.sliderImage.getHeight();
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to load scrollbar slider image " + url + e );
				}
			}
			//#if polish.css.scrollbar-slider-image-repeat
				Boolean repeatSliderImageBool = style.getBooleanProperty("scrollbar-slider-image-repeat");
				if (repeatSliderImageBool != null) {
					this.repeatSliderImage = repeatSliderImageBool.booleanValue();
				}
			//#endif
		//#endif
		//#if polish.css.scrollbar-slider-width
			Dimension sliderWidthDim = (Dimension)style.getObjectProperty("scrollbar-slider-width");
			if (sliderWidthDim != null) {
				this.sliderWidth = sliderWidthDim; 
			}
		//#endif
		//#if polish.css.scrollbar-slider-color
			Integer sliderColorInt = style.getIntProperty("scrollbar-slider-color");
			if (sliderColorInt != null) {
				this.sliderColor = sliderColorInt.intValue();
			}
		//#endif
			
		//#if polish.css.scrollbar-slider-mode
			Integer sliderModeInt = style.getIntProperty("scrollbar-slider-mode");
			if (sliderModeInt != null) {
				this.sliderMode = sliderModeInt.intValue();
			}
		//#endif
		//#if tmp.fadeout
			Dimension opacityInt = (Dimension) style.getObjectProperty("opacity");
			if (opacityInt != null) {
				this.startOpacity = opacityInt.getValue(255);
			}
			if (this.startOpacity == 255) {
				this.startOpacity = 254;
				this.opacity = 254;
			}
			Boolean fadeOutBool = style.getBooleanProperty("scrollbar-fadeout");
			if (fadeOutBool != null) {
				this.overlap = fadeOutBool.booleanValue();
			}
		//#endif
		//#if polish.css.scrollbar-slider-background
			Background bg = (Background) style.getObjectProperty("scrollbar-slider-background");
			if (bg != null) {
				this.sliderBackground = bg;
			}
		//#endif
		//#if polish.css.scrollbar-slider-minimum-height
			Dimension sliderMinHeightInt = (Dimension)style.getObjectProperty("scrollbar-slider-minimum-height");
			if (sliderMinHeightInt != null) {
				this.sliderMinHeight = sliderMinHeightInt.getValue(this.contentHeight);
			}
		//#endif
	}
	
	//#if polish.hasPointerEvents && (polish.ScrollBar.handlePointerEvents != false)
	/**
	 * Handles the event when a pointer has been pressed at the specified position.
	 * The default method discards this event when relX/relY is outside of the item's area.
	 * When the event took place inside of the content area, the pointer-event is translated into an artificial
	 * FIRE game-action keyPressed event, which is subsequently handled
	 * bu the handleKeyPressed(-1, Canvas.FIRE) method.
	 * This method needs should be overwritten only when the "polish.hasPointerEvents"
	 * preprocessing symbol is defined: "//#ifdef polish.hasPointerEvents".
	 *    
	 * @param relX the x position of the pointer pressing relative to this item's left position
	 * @param relY the y position of the pointer pressing relative to this item's top position
	 * @return true when the pressing of the pointer was actually handled by this item.
	 * @see #isInItemArea(int, int) this method is used for determining whether the event belongs to this item
	 * @see #isInContentArea(int, int) for a helper method for determining whether the event took place into the actual content area
	 * @see #handleKeyPressed(int, int) 
	 */
	protected boolean handlePointerPressed( int relX, int relY ) {
		this.isPointerPressedHandled = false;
		//System.out.println("relX=" + relX + ", itemWidth=" + this.itemWidth);
		if (this.screenActualContentHeight <= this.screenAvailableContentHeight || relY < 0 || relY > this.screenAvailableContentHeight) {
			return false;
		}
		if ( (relX >= 0) || ((this.hideSlider || !this.isVisible) && relX >= -this.itemWidth) ) 
		{
			this.isPointerPressedHandled = true;
			return true;
		}
		return false;
	}
	//#endif

	//#if polish.hasPointerEvents && (polish.ScrollBar.handlePointerEvents != false)
	/**
	 * Handles the event when a pointer has been released at the specified position.
	 * The default method discards this event when relX/relY is outside of the item's area.
	 * When the event took place inside of the content area, the pointer-event is translated into an artificial
	 * FIRE game-action keyPressed event, which is subsequently handled
	 * bu the handleKeyPressed(-1, Canvas.FIRE) method.
	 * This method needs should be overwritten only when the "polish.hasPointerEvents"
	 * preprocessing symbol is defined: "//#ifdef polish.hasPointerEvents".
	 *    
	 * @param relX the x position of the pointer pressing relative to this item's left position
	 * @param relY the y position of the pointer pressing relative to this item's top position
	 * @return true when the pressing of the pointer was actually handled by this item.
	 * @see #isInItemArea(int, int) this method is used for determining whether the event belongs to this item
	 * @see #isInContentArea(int, int) for a helper method for determining whether the event took place into the actual content area
	 * @see #handleKeyPressed(int, int) 
	 */
	protected boolean handlePointerReleased( int relX, int relY ) {
		if (this.screenActualContentHeight <= this.screenAvailableContentHeight) {
			return false;
		}
		if ( this.isPointerPressedHandled) {
			if (this.isPointerDraggedHandled ) {
				// consume event so that no other items will handle it:
				return true;
			}
			int diff = 0;
			//System.out.println("y=" + relY + ", sliderY=" + this.sliderY + ", bottom=" + (this.sliderY + this.sliderHeight ) );
			if (relY < this.sliderY) {
				// scroll up
				diff = this.screen.contentHeight / 2;
			} else if (relY > this.sliderY + this.sliderHeight){
				// scroll down
				diff = - (this.screen.contentHeight / 2);
			}
			if (diff != 0) {
				this.screen.scrollRelative( diff );
				return true;
			}
		}
		return false;
	}
	//#endif

	//#if polish.hasPointerEvents && (polish.ScrollBar.handlePointerEvents != false)
	/**
	 * Allows to drag the scroll handle.
	 * 
	 * @param x relative x offset
	 * @param y relative y offset
	 * @return true when the dragged event was handled
	 */
	protected boolean handlePointerDragged(int x, int y)
	{
		//System.out.println("pointer drag " + x + ", " + y);
		this.isPointerDraggedHandled = false;
		if (this.screenActualContentHeight <= this.screenAvailableContentHeight) {
			//System.out.println("no scroll");
			return false;
		}
		if (this.isPointerPressedHandled) {
			// use the page dimensions:
			int scrollOffset = (y * this.screenActualContentHeight) / this.screenAvailableContentHeight;
			if (scrollOffset < 0) {
				scrollOffset = 0;
			} else if (scrollOffset > this.screenActualContentHeight - this.screenAvailableContentHeight) {
				scrollOffset = this.screenActualContentHeight - this.screenAvailableContentHeight;
			}
			this.screen.setScrollYOffset(-scrollOffset, false);
			this.isPointerDraggedHandled = true;
			return true;
		}
		return false;
	}
	//#endif
	
}
