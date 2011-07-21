//#condition polish.usePolishGui && polish.api.mmapi

/*
 * Created on Feb 14, 2010 at 11:49:48 PM.
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
package de.enough.polish.video;

import java.util.Date;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;

//#if polish.api.advancedmultimedia
	import de.enough.polish.multimedia.AdvancedMultimediaManager;
//#endif
import de.enough.polish.snapshot.SnapshotUtil;

/**
 * <p>A specialist video source for capturing snapshots.</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CaptureSource extends VideoSource {
	
	private boolean isSnapshotInProgress;
	private boolean isAutofocusEnabled;
	private boolean isInitializing;
	private String protocol;


	/**
	 * Creates a new CaptureSource with the default protocol retrieved by SnapshotUtil.getProtocol()
	 * @throws MediaException when the protocol could not be retrieved or when snapshots are not supported 
	 */
	public CaptureSource() throws MediaException {
		this( SnapshotUtil.getProtocol());
	}

	
	/**
	 * Creates a new CaptureSource
	 * @param protocol the protocol, e.g. "capture://video" or SnapshotUtil.getProtocol()
	 */
	public CaptureSource(String protocol) {
		super("capture", protocol, null);
		this.protocol = protocol;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.video.VideoSource#open()
	 */
	protected void open() throws Exception {
		this.player = Manager.createPlayer(this.protocol);
		this.player.realize();
		this.player.prefetch();
		this.videoControl = (VideoControl) this.player.getControl("VideoControl");
	}

	/**
	 * Captures a snapshot.
	 * 
	 * @param encoding the encoding of the image
	 * @return the corresponding byte[] data
	 * @throws MediaException when there was a MMAPI level error
	 */
	public byte[] capture(String encoding)
	throws MediaException
	{
		encoding = null;
		if (this.isSnapshotInProgress) {
			throw new MediaException("Snapshot in progress");
		}
		this.isSnapshotInProgress = true;
		int step = 0;
		try {
            //#debug info
            System.out.println("getSnapshot(" + encoding + "), protocol=" + this.protocol + " at " + (new Date()).toString());
			VideoControl vc = this.videoControl;
			Player pl = this.player;
			if (vc == null || pl.getState() == Player.CLOSED) {
				throw new MediaException("Unable to init snapshot");
			}
			step = 1;
//			if (tries == 0) {
//				try {
//					step = 2;
//		        	vc.setVisible(false);
//		        	vc.setVisible(true);
//				} catch (Exception e) {
//					throw new MediaException("While setting visibility: " + e);
//				}
//			}
			//#if polish.api.advancedmultimedia
				if (this.isAutofocusEnabled) {
					step = 3;
					//#debug
					System.out.println("Setting focus for player");
					AdvancedMultimediaManager.setFocus(pl);
				}
			//#endif
            //#debug info
            System.out.println("Start to capture data at " + (new Date()).toString());
            byte[] data = null;
            String message = null;
            try {
            	step = 4;
            	if (encoding != null) {
            		String supported = System.getProperty("video.snapshot.encodings");
	            	if (supported != null && supported.indexOf(' ') == -1) {
	            		encoding = supported; 
	            	}
            	}
            	data = vc.getSnapshot(encoding);
            } catch (MediaException e) {
            	//#debug info
            	System.out.println("did not get data for encoding " + encoding + e);
            	if (encoding == null) {
            		throw e;
            	}
            	message = e.getMessage();
            }
            if (data == null && encoding != null) {
            	try {
            		step = 5;
            		//#debug
            		System.out.println("retrying snapshot with <null> encoding.");
            		data = vc.getSnapshot(null);
            		//#debug
            		System.out.println("got data for <null> encoding.");
            	} catch (MediaException e) {
            		throw new MediaException( "(1): " + message + " enc=[" + encoding + "], (2):" + e.getMessage() + ", supported:[" + System.getProperty("video.snapshot.encodings") + "]" );
            	}
            }
            //#debug info
            System.out.println("End of capturing data at " + (new Date()).toString());
            //#debug
            System.out.println("data.length=" + data.length);
			this.isSnapshotInProgress = false;
			stopSnapshot();
            return data;
		} catch (MediaException e) {
			//#debug error
			System.out.println("Unable to take snapshot " + e);
			throw e;
		} catch (Throwable e) {
			//#debug error
			System.out.println("Unable to take snapshot at step " + step + e);
			if (e instanceof SecurityException) {
				throw (SecurityException) e;
			}
			throw new MediaException( e.toString() + " at step " + step ); 
		} finally {
			//#debug
			System.out.println("stopping snapshot, last step=" + step);
			this.isSnapshotInProgress = false;
		}
		}


	private void stopSnapshot() {
		// TODO Besitzer implement stopSnapshot
		
	}
//
//
//	private void initPlayer() {
//		//#debug
//		System.out.println("initPlayer()");
//		if (this.player != null) {
//			stopSnapshot();
//		}
//		this.isInitializing = true;
//    	try {
//			String protocol = SnapshotUtil.getProtocol();
//			
//            //#debug info
//            System.out.println("The capture protocol is " + protocol);
//            try {
//            	this.player = Manager.createPlayer(protocol);
//            } catch (MediaException e) {
//            	if (!"capture://video".equals(protocol)) {
//            		protocol = "capture://video";
//            		this.player = Manager.createPlayer(protocol);
//            	}
//            }
//            //this.player.addPlayerListener( this );
//			this.player.realize();
//			VideoControl vc = (VideoControl) this.player.getControl("VideoControl");
//			this.videoControl = vc;
//			if (vc != null) {
//				try {
//					//#if polish.blackberry
//					if (this._bbField != null) {
//						getScreen().removePermanentNativeItem(this);
//					}
//					this._bbField = (Field) videoControl.initDisplayMode(VideoControl.USE_GUI_PRIMITIVE, "net.rim.device.api.ui.Field");
//					if (this.adjustSizeAutomatically) {
//						this.contentWidth = videoControl.getSourceWidth();
//						this.contentHeight = videoControl.getSourceHeight();
//					}
//					/*
//					 * todo: add BB specific code:
//					 * 			if (this._bbField != null) {
//				getScreen().removePermanentNativeItem(this);
//			}
//			this._bbField = (Field) videoControl.initDisplayMode(VideoControl.USE_GUI_PRIMITIVE, "net.rim.device.api.ui.Field");
//			if (this.adjustSizeAutomatically) {
//				this.contentWidth = videoControl.getSourceWidth();
//				this.contentHeight = videoControl.getSourceHeight();
//			}
//					 */
//					vc.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, Display.getInstance());
//					vc.setVisible(true);
//					this.player.prefetch();
//					// it's a bit weird to first start the player before setting the size and location,
//					// but in this way it works fine on Nokia Series 60 as well:
//					//#if polish.Bugs.videoControlBeforePlayer
//						this.player.start();
//					//#endif
//					int width = this.contentWidth;
//					int height = this.contentHeight;
//					int locX = this.contentX;
//					int locY = this.contentY;
////					Point resolution = getDefaultResolution();
////					if (resolution != null) {
//						int resW = vc.getDisplayWidth();
//						int resH = vc.getDisplayHeight();
////						int resW = resolution.x;
////						int resH = resolution.y;
//						//#debug
//						System.out.println("current resolution: " + resW + "x" + resH + ", target res=" + width + "x" + height);
////						if (( (resW > resH && getWidth() <= getHeight()) || (resW < resH && getWidth() > getHeight()) ) ) {
////							//#debug
////							System.out.println("resolution is switched to horizontal mode");
////							int tmp = resW;
////							resW = resH;
////							resH = tmp;
////						}
//						// 1: assume height stays the same:
//						int adjustedWidth = (resW * height) / resH;
//						if (adjustedWidth <= width) {
//							// height can stay the same, but the width is shrinked:
//							locX += (width - adjustedWidth)/2;
//							width = adjustedWidth;
//						} else {
//							// 2. width can stay the same, but the height is shrinked:
//							int adjustedHeight = (width * resH) / resW;
//							locY += (height - adjustedHeight)/2;
//							height = adjustedHeight;
//						}
//						//#debug
//						System.out.println("Switched res to " + width + "x" + height);
////					}
//
//					vc.setDisplaySize( width, height );
//					vc.setDisplayLocation( locX, locY );
//					//#if !polish.Bugs.videoControlBeforePlayer
//						this.player.start();
//					//#endif
//				} catch (MediaException e) {
//	                //#debug error
//	                System.out.println("Cannot start video player. The error is: " + e);
//	                throw e;
//				}
//			}
//    	} catch (MediaException e) {
//    		throw e;
//    	} catch (Throwable e) {
//			//#debug error
//			System.out.println("unable to initialize capture player" + e);
//			throw new MediaException(e.toString());
//		} finally {
//			//#if polish.Bugs.SnapshotRequiresScreenChange
//				//#debug
//				System.out.println("Screen toggle for preview window.");
//				this.isSnapshotInProgress = true;
//				Display.getInstance().toggleScreen();
//				this.isSnapshotInProgress = false;
//			//#endif
//			this.isInitializing = false;
//			repaint();
//		}  
//		}


	/**
	 * Enables or disables the autofocus mode.
	 * Note that autofocusing is only supported for devices with the camera supplements of the Advanced Media API (JSR 234).
	 * 
	 * @param autofocus true when the autofocus mode should be enabled
	 * @see #isAutofocus()
	 */
	public void setAutofocus(boolean autofocus) {
		this.isAutofocusEnabled = autofocus;
	}


	/**
	 * Determines whether this capture source is currently busy and should not be interrupted.
	 * This is case when it is currently taking a snapshot or when it is currently initializing.
	 * @return true when being busy.
	 */
	public boolean isBusy() {
		return this.isSnapshotInProgress || this.isInitializing ;
	}


	/**
	 * Determines whether a snapshot is currently made
	 * @return true when a this screen tries to create a snapshot at the moment.
	 */
	public boolean isSnapshotInProgress() {
		return this.isSnapshotInProgress;
	}

	/**
	 * Checks if the autofocus mode is enabled.
	 * Note that autofocusing is only supported for devices with the camera supplements of the Advanced Media API (JSR 234).
	 * 
	 * @return true when the autofocus mode should be enabled and when JSR 234 is supported
	 */
	public boolean isAutofocus() {
		boolean result;
		//#if polish.api.advancedmultimedia
			result = this.isAutofocusEnabled;
		//#else
			result = false;
		//#endif
		return result;
	}


}
