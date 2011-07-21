//#condition polish.usePolishGui && polish.api.mmapi
package de.enough.polish.video.control;

import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemStateListener;
import de.enough.polish.ui.Style;
import de.enough.polish.video.VideoCallback;
import de.enough.polish.video.VideoContainer;
import de.enough.polish.video.VideoSource;
/**
 * A gauge to set the volume of a video in a VideoItem
 * @author Andre Schmidt
 *
 */
public class VolumeControlItem extends Gauge implements VideoCallback, ItemStateListener{

	/**
	 * The maximum volume to set
	 */
	public static final int MAX_VOLUME = 100;
	
	/**
	 * The steps to set the volume
	 */
	public static final int STEPS = 10;
	
	/**
	 * The VideoItem to control
	 */
	VideoContainer video;
	
	/**
	 * Constructs a new VolumeControlItem
	 * @param item the VideoItem to control
	 */
	public VolumeControlItem(VideoContainer item) {
		this(item, null);
	}
	
	/**
	 * Constructs a new VolumeControlItem with a style
	 * @param item the VideoItem to control
	 * @param style the style
	 */
	public VolumeControlItem(VideoContainer item,Style style) {
		super(null, true, STEPS, 0,style);
		
		this.video = item;
		
		this.video.addCallback(this);
		
		setValue(STEPS);

		setItemStateListener(this);
	}
	
	/**
	 * Sets the volume to display, must be in percent
	 * @param percent the volume in percent
	 */
	public void setVolume(int percent)
	{
		int value = percent / 100 * STEPS;
		setValue(value);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.video.VideoCallback#onVideoReady()
	 */
	public void onVideoReady() {
		this.video.setVolume(getValue() * STEPS);
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
	public void onVideoStop() {}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.video.VideoCallback#onVideoDestroy()
	 */
	public void onVideoClose() {}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemStateListener#itemStateChanged(de.enough.polish.ui.Item)
	 */
	public void itemStateChanged(Item item) {
		if(this.video.getState() != VideoContainer.STATE_NOT_PREPARED)
		{
			int volume = (MAX_VOLUME / STEPS) * getValue();
			this.video.setVolume(volume);
		}
	}

	public void onSnapshot(byte[] data, String encoding) {
		// TODO Auto-generated method stub
		
	}
	
}
