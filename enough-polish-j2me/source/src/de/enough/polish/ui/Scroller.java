//#condition polish.usePolishGui
package de.enough.polish.ui;

import javax.microedition.lcdui.Canvas;

/**
 * Helps to scroll elements  
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Scroller
implements Animatable
{
	private static final long SCROLL_DURATION = 300L; // milliseconds
	public static final int ORIENTATION_VERTICAL = 0;
	public static final int ORIENTATION_HORIZONTAL = 1;
	final int orientation;
	int offsetCurrent;
	int offsetTarget;
	int scrollSpeed;
	long scrollStartTime;
	int scrollStartOffset;
	private int scrollDamping;
	int scrollDirection;
	private long lastAnimationTime;
	//#if polish.css.bounce && !(polish.Container.ScrollBounce:defined && polish.Container.ScrollBounce == false)
		//#define tmp.checkBouncing
		private boolean allowBouncing = true;
	//#endif
	private int	scrollDimension;
	Scrollable parent;
	private int	maxDimension;
	private int	lastPointerPressX;
	private int	lastPointerPressY;
	private int	lastPointerPressOffset;
	private long	lastPointerPressTime;
	
	public Scroller(int scrollOrientation, int scrollDimension, int maxDimension, Scrollable parent )
	{
		this.orientation = scrollOrientation;
		this.scrollDimension = scrollDimension;
		this.maxDimension = maxDimension;
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Animatable#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion)
	{
		boolean addFullRepaintRegion = false;
		// scroll the container:
		int target = this.offsetTarget;
		int current = this.offsetCurrent;
		if (target != current) {
			long passedTime = (currentTime - this.scrollStartTime);
			int nextOffset = CssAnimation.calculatePointInRange(this.scrollStartOffset, target, passedTime, SCROLL_DURATION , CssAnimation.FUNCTION_EXPONENTIAL_OUT );
			onScrollOffsetChanged(nextOffset, false);
			addFullRepaintRegion = true;
		}
		int speed = this.scrollSpeed;
		if (speed != 0) {
			speed = (speed * (100 - this.scrollDamping)) / 100;
			if (speed <= 0) {
				speed = 0;
			}
			this.scrollSpeed = speed;
			long timeDelta = currentTime - this.lastAnimationTime;
			if (timeDelta > 1000) {
				timeDelta = AnimationThread.ANIMATION_INTERVAL;
			}
			speed = (int) ((speed * timeDelta) / 1000);
			if (speed == 0) {
				this.scrollSpeed = 0;
			}
			int offset = this.offsetCurrent;
			if (this.scrollDirection == Canvas.UP) {
				offset += speed;
				target = offset;
				if (offset > 0) {
					this.scrollSpeed = 0;
					target = 0;
					//#if tmp.checkBouncing
						if (!this.allowBouncing) {
							offset = 0;
						}
					//#elif polish.Container.ScrollBounce:defined && polish.Container.ScrollBounce == false
						offset = 0;
					//#endif
				}
			} else {
				offset -= speed;
				target = offset;
				int maxItemHeight = this.maxDimension;
				if (offset + maxItemHeight < this.scrollDimension) { 
					this.scrollSpeed = 0;
					target = this.scrollDimension - maxItemHeight;
					//#if tmp.checkBouncing
						if (!this.allowBouncing) {
							offset = target;
						}
					//#elif polish.Container.ScrollBounce:defined && polish.Container.ScrollBounce == false
						offset = target;
					//#endif
				}
			}
			onScrollOffsetChanged(offset, true);
			this.offsetTarget = target;
			addFullRepaintRegion = true;
		}
		this.lastAnimationTime = currentTime;

		// add repaint region:
		if (addFullRepaintRegion) 
		{
			this.parent.addRepaintArea(repaintRegion);
		}

	}
	
	/**
	 * Starts to scroll in the specified direction
	 * @param direction either Canvas.UP or Canvas.DOWN
	 * @param speed the speed in pixels per second
	 * @param damping the damping in percent; 0 means no damping at all; 100 means the scrolling will be stopped immediately
	 */
	public void startScroll( int direction,int speed, int damping) {
		//#debug
		System.out.println("startScrolling " + (direction == Canvas.UP ? "up" : "down") + " with speed=" + speed + ", damping=" + damping + " for " + this.parent);
		this.scrollDirection = direction;
		this.scrollDamping = damping;
		this.scrollSpeed = speed;
	}


	protected void onScrollOffsetChanged(int offset, boolean adjustTarget)
	{
		this.offsetCurrent = offset;
		if (adjustTarget)
		{
			this.offsetTarget = offset;
		}
		this.parent.setScrollOffset(this, offset);
	}
	
	public void setScrollOffset( int offset, boolean smooth )
	{
		if (smooth)
		{
			this.offsetTarget = offset;
		}
		else
		{
			this.offsetCurrent = offset;
		}
	}
	
	public boolean handlePointerPressed(int x, int y)
	{
		System.out.println("p pressed");
		this.lastPointerPressX = x;
		this.lastPointerPressY = y;
		this.lastPointerPressOffset = this.offsetTarget;
		this.lastPointerPressTime = System.currentTimeMillis();
		return false;
	}


	public boolean handlePointerReleased(int x, int y)
	{
		System.out.println("p released");
		int scrollDiff = Math.abs(this.offsetTarget - this.lastPointerPressOffset);
		if ( scrollDiff > Display.getScreenHeight()/10) 
		{
			// check if we should continue the scrolling:
			long dragTime = System.currentTimeMillis() - this.lastPointerPressTime;
			if (dragTime < 1000 && dragTime > 1) 
			{
				int direction = Canvas.DOWN;
				if (this.offsetCurrent > this.lastPointerPressOffset) 
				{
					direction = Canvas.UP;
				}
				startScroll( direction,  (int) ((scrollDiff * 1000 ) / dragTime), 20 );
			} 
			else if (this.offsetCurrent > 0) 
			{
				setScrollOffset(0, true);
			} 
			else if (this.offsetCurrent + this.maxDimension < this.scrollDimension) 
			{
				int maxItemHeight = this.maxDimension;
				if (this.offsetCurrent + maxItemHeight < this.scrollDimension) { 
					int target = this.scrollDimension - maxItemHeight;
					setScrollOffset( target, true );
				}
	
			}
			return true;
		}
		return false;
	}
	
//	//#ifdef polish.hasPointerEvents
//		/**
//		 * Allows subclasses to check if a pointer release event is used for scrolling the container.
//		 * This method can only be called when polish.hasPointerEvents is true.
//		 * 
//		 * @param relX the x position of the pointer pressing relative to this item's left position
//		 * @param relY the y position of the pointer pressing relative to this item's top position
//		 */
//		protected boolean handlePointerScrollReleased(int relX, int relY) {
//			if (Display.getInstance().hasPointerMotionEvents()) {
//				return false;
//			}
//			int yDiff = relY - this.lastPointerPressY;
//			int bottomY = Math.max( this.itemHeight, this.internalY + this.internalHeight );
//			if (this.focusedItem != null && this.focusedItem.relativeY + this.focusedItem.backgroundHeight > bottomY) {
//				bottomY = this.focusedItem.relativeY + this.focusedItem.backgroundHeight;
//			}
//			if ( this.enableScrolling 
//					&& (this.itemHeight > this.scrollHeight || this.yOffset != 0)
//					&& ((yDiff < -5 && this.yOffset + bottomY > this.scrollHeight) // scrolling downwards
//						|| (yDiff > 5 && this.yOffset != 0) ) // scrolling upwards
//				) 
//			{
//				int offset = this.yOffset + yDiff;
//				if (offset > 0) {
//					offset = 0;
//				}
//				//System.out.println("adjusting scrolloffset to " + offset);
//				setScrollYOffset(offset, true);
//				return true;
//			}
//			return false;
//		}
//	//#endif

	public boolean handlePointerDragged(int x, int y, ClippingRegion repaintRegion)
	{
		System.out.println("p dragged");
		
		int maxItemHeight = this.maxDimension;
		if (maxItemHeight > this.scrollDimension || this.offsetTarget != 0) {
			int lastOffset = this.offsetTarget;
			int nextOffset = this.lastPointerPressOffset + (y - this.lastPointerPressY);
			//#if tmp.checkBouncing
				if (!this.allowBouncing) {
			//#endif
				//#if tmp.checkBouncing || (polish.Container.ScrollBounce:defined && polish.Container.ScrollBounce == false)
					if (nextOffset > 0) {
						nextOffset = 0;
					} else {
						if (nextOffset + maxItemHeight < this.scrollDimension) { 
							nextOffset = this.scrollDimension - maxItemHeight;
						}
					}
				//#endif
			//#if tmp.checkBouncing
				} else {
			//#endif
				//#if tmp.checkBouncing || !(polish.Container.ScrollBounce:defined && polish.Container.ScrollBounce == false)
					if (nextOffset > this.scrollDimension/3) {
						nextOffset = this.scrollDimension/3;
					} else {
						maxItemHeight += this.scrollDimension/3;
						if (nextOffset + maxItemHeight < this.scrollDimension) { 
							nextOffset = this.scrollDimension - maxItemHeight;
						}
					}
				//#endif
			//#if tmp.checkBouncing
				}
			//#endif
			boolean isScrolling = (nextOffset != lastOffset);
			if (isScrolling) {
				onScrollOffsetChanged(nextOffset, true);
				this.parent.addRepaintArea(repaintRegion);
				return true;
			}
		}
		
		return false;
	}

	
	
	
}
