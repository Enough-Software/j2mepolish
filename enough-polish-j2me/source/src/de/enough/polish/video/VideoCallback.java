package de.enough.polish.video;

/**
 * Provides an interface to nofify listeners
 * of video states and actions
 * @author Andre Schmidt
 *
 */
public interface VideoCallback {
	/**
	 * Called when a video is fully prepared
	 */
	void onVideoReady();
	
	/**
	 * Called when a video is destroyed
	 */
	void onVideoClose();
	
	/**
	 * Called when an error occures
	 */
	void onVideoError(Exception e);
	
	/**
	 * Called when the video is paused
	 */
	void onVideoPause();
	
	/**
	 * Called when the video is played
	 */
	void onVideoPlay();
	
	/**
	 * Called when the video is stopped
	 */
	void onVideoStop();
	
	/**
	 * Called when a capture is done
	 * @param data the resulting data of the capture
	 */
	void onSnapshot(byte[] data, String encoding);
}
