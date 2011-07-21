//#condition polish.usePolishGui && polish.api.mmapi
package de.enough.polish.video.control;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.AnimationThread;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.video.VideoCallback;
import de.enough.polish.video.VideoContainer;
import de.enough.polish.video.VideoSource;
import de.enough.polish.video.util.VideoUtil;

/**
 * A Gauge to display and control the current playback position of a video 
 * in a VideoItem.
 * @author Andre Schmidt
 *
 */
public class SeekControlItem extends Gauge implements VideoCallback
{
	/**
	 * The display to show the progress
	 */
	StringItem display;
	
	/**
	 * The VideoItem
	 */
	VideoContainer video;
	
	/**
	 * Is currently seeking ?
	 */
	boolean seek = false;
	
	/**
	 * the current time in microseconds
	 */
	long current;
	
	/**
	 * the total time in microseconds
	 */
	long total;
	
	int currentSeconds;
	
	int totalSecond;
	
	/**
	 * Constructs a new SeekControlItem
	 * @param item the VideoItem to control
	 */
	public SeekControlItem(VideoContainer item) {
		this(item,null);
	}
	
	/**
	 * Constructs a new SeekControlItem with a style
	 * @param item the VideoItem to control
	 * @param style the style
	 */
	public SeekControlItem(VideoContainer item, Style style) {
		super(null, true, 100, 0, style);
		
		this.video = item;
		
		this.video.addCallback(this);
		
		//#style seekControlDisplay?
		this.display = new StringItem(null,"0:00/0:00");
		this.display.setParent(this);
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.video.VideoCallback#onVideoReady()
	 */
	public void onVideoReady() 
	{
		// Set the dimensions of the gauge for the video
		this.current = 0;
		this.currentSeconds = 0;
		
		this.total = this.video.getLength();
		this.totalSecond = VideoUtil.getSeconds(total); 
		
		setValue(this.currentSeconds);
		setMaxValue(this.totalSecond);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.video.VideoCallback#onError(java.lang.String, java.lang.Exception)
	 */
	public void onVideoPartReady(VideoSource source) {}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.video.VideoCallback#onError(java.lang.String, java.lang.Exception)
	 */
	public void onVideoError( Exception e) {}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.video.VideoCallback#onVideoPause()
	 */
	public void onVideoPause() {}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.video.VideoCallback#onVideoPlay()
	 */
	public void onVideoPlay() {}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.video.VideoCallback#onVideoStop()
	 */
	public void onVideoStop() 
	{
		setValue(0);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.video.VideoCallback#onVideoDestroy()
	 */
	public void onVideoClose() {}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Gauge#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		boolean handled = super.handleKeyPressed(keyCode, gameAction);
		
		if(this.video.getState() >= VideoContainer.STATE_READY)
		{
			if(gameAction == Canvas.LEFT || gameAction == Canvas.RIGHT)
			{	
				// if the current gauge value is changed, get the according time
				long current = getValue() * 1000000;
				long length = getMaxValue() * 1000000;
				
				// set the display
				setDisplay(current, length);
				
				this.seek = true;
				
				return true;
			}
			
			if(this.seek && gameAction == Canvas.FIRE && keyCode != Canvas.KEY_NUM5)
			{
				int value = getValue();
				
				long time = value * 1000000;
				
				// seek to the given position
				this.video.seek(time);
				
				this.seek = false;
				
				return true;
			}
		}
		
		return handled;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Gauge#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		
		if(this.video.isFullscreen())
		{
			return animated;
		}
		
		if(!this.seek)
		{
			if(this.video.getState() >= VideoContainer.STATE_READY)
			{
				this.current = this.video.getTime();
				
				int seconds = VideoUtil.getSeconds(this.current);
				
				if(this.currentSeconds != seconds)
				{
					this.currentSeconds = seconds;
					
					// set the display and the gauge
					setDisplay(this.current, this.total);
					
					setValue(this.currentSeconds);
					
					return true;
				}
			}
		}
		
		return animated;
	}
	
	/**
	 * Sets the display
	 * @param current the current time
	 * @param length the total length 
	 */
	void setDisplay(long current, long length)
	{
		String currentStr = VideoUtil.getTime(current);
		String lengthStr = VideoUtil.getTime(length);
		
		this.display.setText(currentStr + "/" + lengthStr);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Gauge#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {
		// get the display height
		int displayHeight = this.display.getItemHeight(this.contentWidth, this.contentWidth, this.availableHeight);
		
		// paint the display
		this.display.paint(x, y - displayHeight, leftBorder, rightBorder, g);
		
		// paint the gauge
		super.paintContent(x, y, leftBorder, rightBorder, g);
	}

	protected void showNotify() {
		super.showNotify();
		
		AnimationThread.addAnimationItem(this);
	}
	
	protected void hideNotify() {
		super.hideNotify();
		
		AnimationThread.removeAnimationItem(this);
	}

	public void onSnapshot(byte[] data, String encoding) {
		// TODO Auto-generated method stub
		
	}
}
