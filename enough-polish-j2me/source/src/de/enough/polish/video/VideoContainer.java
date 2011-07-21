//#condition polish.usePolishGui && polish.api.mmapi
package de.enough.polish.video;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;

//#if polish.blackberry
import net.rim.device.api.ui.Field;
//#endif

import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ArrayList;
import de.enough.polish.video.util.VideoUtil;

/**
 * A Container to play a video in its content area.
 * 
 * Usually a VideoContainer is filled with items and appended to a Screen or Container
 * and a controlling instance is set as the callback for the VideoContainer.
 * 
 * The VideoSource is set via setSource(). setRatio(), setRepeat() etc. are optional.
 * 
 * To initialize the player, the dimensions etc. for the video, prepare() is called. When the
 * Container is fully initialized (displayed on the screen) and the player and dimensions are set, the listener is
 * notified via onVideoReady() and the video can be played. 
 * 
 * @author Andre Schmidt
 */
public class VideoContainer extends Container implements Runnable, PlayerListener, VideoCallback {

	/**
	 * The video is stopped
	 */
	public static final int STATE_CLOSED = -1;
	
	/**
	 * The video is not yet prepared
	 */
	public static final int STATE_NOT_PREPARED = 0;
	
	/**
	 * The video is fully prepared
	 */
	public static final int STATE_READY = 1;
	
	/**
	 * The video is playing
	 */
	public static final int STATE_PLAYING = 2;
	
	/**
	 * The video is paused
	 */
	public static final int STATE_PAUSED = 3;
	
	/**
	 * The video is stopped
	 */
	public static final int STATE_STOPPED = 4;
	
	static final String tag = "video";
	
	/**
	 * A form to display the video in fullscreen mode
	 * @author Andre Schmidt
	 */
	private class Fullscreen extends Form {
		/**
		 * the parent to return to
		 */
		VideoContainer parent;
		
		/**
		 * Constructs a new Fullscreen with the given parent to return to
		 * @param parent
		 */
		public Fullscreen(VideoContainer parent) {
			//#style videoFull?
			super(null);
			this.parent = parent;
		}

		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Form#createCssSelector()
		 */
		protected String createCssSelector() {
			return null;
		}

		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Screen#handleKeyPressed(int, int)
		 */
		protected boolean handleKeyPressed(int keyCode, int gameAction) {
			// Redirects any key handling to the parent
			return UiAccess.handleKeyPressed(this.parent.screen, keyCode, gameAction);
		}
		
		public void showNotify() {
			super.showNotify();
			
			this.parent.showVideo();
		}

		public void hideNotify() {
			super.hideNotify();
			
			this.parent.hideVideo();
		}
	}
	
	
	/**
	 * The ratio to use (i.e. 16:9) when playing a video in the VideoContainer
	 * @author Andre Schmidt
	 */
	public static class Ratio {
		/**
		 * The horizontal dimension
		 */
		int horizontal;

		/**
		 * The vertical dimension
		 */
		int vertical;

		/**
		 * Constructs a new Ratio instance
		 * @param horizontal the horizontal dimension
		 * @param vertical the vertical dimension
		 */
		public Ratio(int horizontal, int vertical){
			this.horizontal = horizontal;
			this.vertical = vertical;
		}

		/**
		 * Returns the horizontal dimension
		 * @return the horizontal dimension
		 */
		public int getHorizontal() {
			return this.horizontal;
		}

		/**
		 * Returns the vertical dimension
		 * @return the vertical dimension
		 */
		public int getVertical() {
			return this.vertical;
		}
	}

	/**
	 * Static methods to test videoplayback
	 * @author Andre Schmidt
	 *
	 */
	public static class Test
	{
		/**
		 * Is supported ?
		 */
		Boolean seekSupported = null;
		
		/**
		 * Creates a player using the given resource
		 * @param resource the url of a resource file
		 * @return the resulting player
		 * @throws Exception
		 */
		public static Player createPlayer(String resource) throws Exception
		{
			InputStream stream = resource.getClass().getResourceAsStream(resource);
			try {
				Player player = Manager.createPlayer(stream, "video/3gpp");
				player.realize();
				player.prefetch();
				return player;
			} catch (IOException e) {
				//#debug error
				System.out.println("Could not create test player");
				return null;
			} catch (MediaException e) {
				//#debug error
				System.out.println("Could not create test player");
				return null;
			}
		}
		
		/**
		 * Tests on a player if seek (FF/RW) is supported
		 * @param player the player to test with
		 * @return true if seek is supported, otherwise false
		 */
		public static boolean isSeekSupported(Player player)
		{
			//Is FramePositioningControl supported ?
			if(player.getControl("FramePositioningControl") != null)
			{
				return true;
			}
			
			try {
				//Try to set to the video to the beginning
				player.setMediaTime(0);
			} catch (MediaException e) {
				return false;
			}
			
			return true;
		}
	}
	
	public class Seek extends Thread implements Runnable
	{
		VideoSource source;
		long position;
		
		public Seek(VideoSource source, long position)
		{
			this.source = source;
			this.position = position;
		}
		
		public void run()
		{
			try {
				Player player = this.source.getPlayer();
				
				//#if !polish.video.forceSetMediaTime
				if(this.source.getFramePositioningControl() != null)
				{
					int frame = VideoUtil.getFrame(this.source.getFramePositioningControl(), this.position, player.getDuration());
					this.source.getFramePositioningControl().seek(frame);
				}
				else
				//#endif
				{
					player.setMediaTime(this.position);
				}
			} catch (Exception e) {
				onVideoError(e);
			}
		}
	}
	
	/**
	 * The VideoSource to use
	 */
	VideoSource source;
	
	/**
	 * The VideoSource to clear
	 */
	VideoSource sourceToClear;
	
	/**
	 * The VideoMultipart to use
	 */
	VideoMultipart multipart;
	
	/**
	 * The VideoMultipart to clear
	 */
	VideoMultipart multipartToClear;

	/**
	 * The callbacks
	 */
	ArrayList callbacks;

	/**
	 * flag to indicate that the video should be repeated
	 */
	boolean repeat;

	/**
	 * flag to indicate that the video should played in landscape
	 */
	boolean landscape;
	
	/**
	 * flag to indicate if the video is currently in fullscreen mode 
	 */
	boolean fullscreen;
	private boolean restoreFullscreenInPlay;

	
	/**
	 * flag to indicate if the video is muted
	 */
	boolean mute;
	
	/** 
	 * the volume
	 */
	int volume = 100;

	/**
	 * the ratio to use
	 */
	Ratio ratio;
	
	/**
	 * the current state of the VideoContainer
	 */
	int state;

	/**
	 * The current x position
	 */
	int videoX;

	/**
	 * The current y position
	 */
	int videoY;

	/**
	 * The current width
	 */
	int videoWidth;

	/**
	 * The current height
	 */
	int videoHeight;

	/**
	 * The length of the video in microseconds
	 */
	long videoLength;
	
	/**
	 * The Fullscreen instance
	 */
	Fullscreen fullScreen;

	/**
	 * The parent of the FullScreen to return to
	 */
	Displayable fullScreenParent;
	
	/**
	 * The current prepare thread
	 */
	Thread currentThread;
	
	boolean resume;

	private final boolean adjustSizeAutomatically;

	private int gameActionEnterFullscreen;

	private boolean startPlayAfterPrepare;

	
	/**
	 * Constructs a new VideoContainer instance
	 */
	public VideoContainer() {
		this(false, null);
	}
	
	/**
	 * Constructs a new VideoContainer instance with the given
	 * screen as parent
	 */
	public VideoContainer(Style style) {
		this( false, style );
	}
	
	/**
	 * Constructs a new VideoContainer instance
	 * @param adjustSizeAutomatically true when the size of this container should be adjusted according to the video source 
	 */
	public VideoContainer(boolean adjustSizeAutomatically) {
		this(adjustSizeAutomatically, null);
	}
	
	/**
	 * Constructs a new VideoContainer
	 * @param adjustSizeAutomatically true when the size of this container should be adjusted according to the video source 
	 * @param style the style
	 */
	public VideoContainer(boolean adjustSizeAutomatically, Style style) {
		super(true,style);
		this.adjustSizeAutomatically = adjustSizeAutomatically;
		this.callbacks = new ArrayList();
		setState(STATE_NOT_PREPARED);
	}
	
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		super.animate(currentTime, repaintRegion);
	}

	/**
	 * Convenience method to set the screen
	 * if the VideoContainer is not added to
	 * a Screen or a Container. Should only
	 * be used if you know what you're doing.
	 * @param screen the screen
	 */
	public void setScreen(Screen screen)
	{
		this.screen = screen;
	}
	
	/**
	 * Sets the VideoSource of this VideoContainer. If the source
	 * has already been set before, close() is called on the current, 
	 * the state is reset and the new one set as the source.
	 * @param source the VideoSource to use
	 */
	public synchronized void setSource(VideoSource source)
	{
		setState(STATE_NOT_PREPARED);
		this.sourceToClear = this.source;
		this.source = source;
		this.source.setParent(this);
	}
	
	/**
	 * Returns the current VideoSource
	 * @return the current VideoSource
	 */
	public VideoSource getSource()
	{
		return this.source;
	}
	
	/**]
	 * Sets a VideoMultipart as the source. Closes
	 * all previously used sources and sets the first
	 * part as the VideoSource
	 * @param multipart the VideoMultipart instance
	 */
	public void setMultipart(VideoMultipart multipart)
	{
		setState(STATE_NOT_PREPARED);
		this.multipartToClear = this.multipart;
		this.multipart = multipart;
		if(this.multipart.hasNext())
		{
			this.source = multipart.next();
		}
	}
	
	void close(VideoSource videoSource) {
		if(videoSource != null) {
			if(getState() == STATE_PLAYING)
			{
				try
				{
					videoSource.getVideoControl().setVisible(false);
					videoSource.getPlayer().stop();
				}catch(MediaException e)
				{
					onVideoError(e);
				}
			}
			
			onVideoClose();
			
			videoSource.close();
		}
	}
	
	/**
	 * Closes this VideoContainer.
	 */
	public void close()
	{
		close(this.source);
		setState(STATE_CLOSED);
	}

	/**
	 * Adds a callback
	 * @param callback the callback
	 */
	public void addCallback(VideoCallback callback) {
		this.callbacks.add(callback);
	}

	/**
	 * Removes a callback
	 * @param callback the callback
	 */
	public void removeCallback(VideoCallback callback) {
		this.callbacks.remove(callback);
	}
	
	/**
	 * Sets  a shortcut 
	 * @param gameAction
	 */
	public void setEnterFullscreenGameAction( int gameAction ) {
		this.gameActionEnterFullscreen = gameAction;
	}
	
	/**
	 * Returns the callback at the given index
	 * @param index the index
	 * @return the callback at the given index
	 */
	private VideoCallback callback(int index) {
		return (VideoCallback) this.callbacks.get(index);
	}

	/**
	 * Initializes the source by calling open() on it
	 * @param src the VideoSource to use
	 * @throws Exception if an error occurs
	 */
	protected void initSource(VideoSource src) throws Exception {
		src.open();
	}

	/**
	 * Initializes the display by using the content area of the
	 * Container.
	 * @param src the VideoSource to use
	 * @throws Exception if an error occurs
	 */
	protected void initDisplay(VideoSource src) throws Exception {
		this.videoX = getAbsoluteX() + getContentX();
		this.videoY = getAbsoluteY() + getContentY();
		this.videoHeight = getContentHeight();
		this.videoWidth = getContentWidth();

		VideoControl videoControl = src.getVideoControl();
		if (videoControl == null) 
		{
			throw new IllegalStateException("no videocontrol");
		}
		//#if polish.blackberry
			if (this._bbField != null) {
				getScreen().removePermanentNativeItem(this);
			}
			this._bbField = (Field) videoControl.initDisplayMode(VideoControl.USE_GUI_PRIMITIVE, "net.rim.device.api.ui.Field");
			videoControl.setDisplaySize(this.videoWidth, this.videoHeight);
			if (this.adjustSizeAutomatically) {
				int resultWidth = videoControl.getDisplayWidth();
				int resultHeight = videoControl.getDisplayHeight();
				this.contentWidth = resultWidth;
				this.contentHeight = resultHeight;
			} 
		//#elif polish.video.rotate
			if(isLandscape())
			{
				videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO | (Sprite.TRANS_ROT90 << 4), Display.getInstance());
			}
			else
			{
				videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, Display.getInstance());
			}
		//#else
			videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO , Display.getInstance());
		//#endif

		if(!(this.fullscreen || this.restoreFullscreenInPlay))
		{
			//#if polish.blackberry
				videoControl.setDisplayFullScreen(false);
				//videoControl.setDisplaySize( this.videoWidth/2, this.videoHeight/2);
			//#else
				setDisplay(videoControl, this.videoX, this.videoY, this.videoWidth,
					this.videoHeight, this.ratio);
			//#endif
		}
		else
		{
			if (this.restoreFullscreenInPlay) {
				this.restoreFullscreenInPlay = false;
				this.fullscreen = true;
			}
			//#if polish.blackberry
				videoControl.setDisplayFullScreen(true);
			//#else
				setDisplay(	this.source.getVideoControl(), 0, 0,
						Display.getScreenWidth(),
						Display.getScreenHeight(), 
						this.ratio);
			//#endif
		}
		
		videoControl.setVisible(true);
	}

	/**
	 * Set the display by using the given VideoControl instance and the
	 * dimensions given
	 * @param control the VideoControl instance
	 * @param x the x position
	 * @param y the y position
	 * @param width the width
	 * @param height the height
	 * @param ratio the ratio to use
	 * @throws Exception if an error occurs
	 */
	protected void setDisplay(VideoControl control, int x, int y, int width,
			int height, Ratio ratio) 
	throws Exception 
	{
		//#debug
		System.out.println("setDisplay for control=" + control + ", x=" + x + ", y=" + y + ", width=" + width + ", height=" + height);
		
		//TODO implements ratio handling
		
		control.setVisible(false);
		
		// Asssumed rotation of 90 CCW for landscape
		if(isLandscape())
		{	
			int orginalX = x;
			x = Display.getScreenHeight() - (y + height);
			y = orginalX;
			
			int orginalWidth = width;
			width = height;
			height = orginalWidth;
		}
		
		control.setDisplayLocation(x, y);
		
		control.setDisplaySize(width, height);
		
		control.setVisible(true);
	}

	/**
	 * Initializes the volume
	 * @param source the VideoSource to use
	 * @throws Exception if an error occurs
	 */
	protected void initVolume(VideoSource source) throws Exception {
		setVolume(this.volume);		
		VolumeControl volumeControl = this.source.getVolumeControl();
		if (volumeControl != null) {
			if(this.mute)
			{
				volumeControl.setLevel(0);
			}
			else
			{
				volumeControl.setLevel(this.volume);
			}
		}
	}

	/**
	 * Starts the thread to prepare the video if the state is zero
	 */
	public void prepare()
	{
		if(getState() == STATE_NOT_PREPARED)
		{
			//#debug 
			System.out.println("preparing VideoContainer in background thread.");
			this.currentThread = new Thread(this);
			this.currentThread.start();
		}
		//#if polish.debug.warn
		else
		{
			//#debug warn
			System.out.println("prepare() failed: state is not zero");
		}
		//#endif
	}
	
	/**
	 * Plays the video or shows the snapshot preview window.
	 * If the video has not been prepared yet, this will be done in a background thread.
	 */
	public void play() {
		//#debug
		System.out.println("play() requested.");
		if(getState() < STATE_READY)
		{
			this.startPlayAfterPrepare = true;
			prepare();
		}
		else	
		{
			//#debug 
			System.out.println("playing VideoContainer");
			try {
				getScreen().addPermanentNativeItem(this);
				this.source.getVideoControl().setVisible(true);
				this.source.getPlayer().start();
				setState(STATE_PLAYING);
			} catch (Exception e) {
				onVideoError(e);
			}
			onVideoPlay();
		}
		
	}
	
	/**
	 * Pauses the video
	 */
	public void pause() {
		if(getState() == STATE_PLAYING)
		{
			try {
				this.source.getVideoControl().setVisible(false);
				this.source.getPlayer().stop();
				setState(STATE_PAUSED);
			} catch (MediaException e) {
				onVideoError(e);
			}
			
			onVideoPause();
		}
	}

	/**
	 * Stops the video
	 */
	public void stop() {
		//#debug 
		System.out.println("stopping VideoContainer");
		if (this.fullscreen) {
			this.fullscreen = false;
			this.restoreFullscreenInPlay = true;
		}
		if(getState() == STATE_PLAYING)
		{
			try {
				this.source.getVideoControl().setVisible(false);
				this.source.getPlayer().stop();
				seek(0);
				setState(STATE_STOPPED);
				
				onVideoStop();
			} catch (MediaException e) {
				onVideoError(e);
			}
		}
		//#if polish.blackberry
			if (this._bbField != null) {
				getScreen().removePermanentNativeItem(this);
			}
		//#endif
	}
	
	/**
	 * Set the player to the given position. The position must be in microseconds. 
	 * @param position the position in microseconds
	 */
	public void seek(long position) {
		new Seek(this.source,position).start();
	}
	
	/**
	 * Returns the current time of the playback
	 * @return the current time
	 */
	public long getTime()
	{
		return this.source.getPlayer().getMediaTime();
	}
	
	/**
	 * Returns the total length of the video
	 * @return the total length
	 */
	public long getLength()
	{
		return this.source.getPlayer().getDuration();
	}
	
	/**
	 * Sets the volume
	 * @param volume the volume to set
	 */
	public void setVolume(int volume)
	{
		if (volume < 0 || volume > 100) {
			onVideoError(new IllegalArgumentException("volume must be in percent"));
		}
		
		try {
			this.volume = volume;
			
			if(!isMute())
			{
				VolumeControl volumeControl = this.source.getVolumeControl();
				if (volumeControl != null) {
					volumeControl.setLevel(volume);
				}
			}
		} catch (Exception e) {
			onVideoError(e);
		}
	}
	
	/**
	 * Returns the volume
	 * @return the volume
	 */
	public int getVolume()
	{
		return this.volume;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate()
	 */
	public boolean animate() {
		// don't animate
		return false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if(getState() == STATE_PLAYING)
		{
			g.setColor(0x000000);
			g.fillRect(getAbsoluteX(), getAbsoluteY(), this.itemWidth, this.itemHeight);
		}
		else
		{
			super.paint(x, y, leftBorder, rightBorder, g);
		}
		
		/*if(!isLandscape())
		{
			//scrolling in landscape mode is not supported for now
			if (getState() > State.ready
				&& (this.videoX != x || this.videoY != y)) {
				try {
					this.videoX = getAbsoluteX();
					this.videoY = getAbsoluteY();
					this.videoHeight = this.itemHeight;
					this.videoWidth = this.itemWidth;
	
					setDisplay(this.videoControl, this.videoX, this.videoY,
							this.videoWidth, this.videoHeight, this.ratio);
				} catch (Exception e) {
					onVideoError(e);
				}
			}
		}*/
	}
	
	/**
	 * Enters or leaves the fullscreen mode.
	 * 
	 * @param fullscreen true if fullscreen mode should be entered, otherwise false
	 * @throws IllegalStateException when no video source has been set
	 */
	public void setFullscreen(boolean fullscreen) {
		if (this.source == null) {
			throw new IllegalStateException();
		}
		setFullscreen( this.source, fullscreen );
	}

	/**
	 * Enters or leaves the fullscreen mode.
	 * 
	 * @param src the video source
	 * @param fullscreen true if fullscreen mode should be entered, otherwise false
	 * @throws IllegalArgumentException when src is null
	 * @throws IllegalStateException when the source cannot retrieve a VideoControl
	 */
	public void setFullscreen(VideoSource src, boolean fullscreen) {
		if (src == null) {
			throw new IllegalArgumentException();
		}
		VideoControl videoControl = src.getVideoControl();
		if (videoControl == null) {
			throw new IllegalStateException();
		}
		try
		{
			videoControl.setDisplayFullScreen( fullscreen );
			this.fullscreen = fullscreen;
			this.restoreFullscreenInPlay = false;
		} catch (MediaException e)
		{
			//#debug error
			System.out.println("Unable to enter fullscreen mode" + e );
			onVideoError(e);
		}
		
	}
	
	/**
	 * Enters or leaves the fullscreen mode by using another styleable form
	 * 
	 * @param fullscreen true if fullscreen mode should be entered, otherwise false
	 * @throws IllegalArgumentException when no video source has been set
	 */
	public void setPseudoFullscreen(boolean fullscreen) {
		setPseudoFullscreen( this.source, fullscreen );
	}

	/**
	 * Enters or leaves the fullscreen mode by using another styleable form
	 * 
	 * @param src the video source
	 * @param fullscreen true if fullscreen mode should be entered, otherwise false
	 * @throws IllegalArgumentException when src is null
	 */
	public void setPseudoFullscreen(VideoSource src, boolean fullscreen) {
		if (src == null) {
			throw new IllegalArgumentException();
		}
		try{
			this.changingViewMode = true;
			
			if (fullscreen) {
				this.fullscreen = fullscreen;
				
				if(this.fullScreen == null)
				{
					this.fullScreen = new Fullscreen(this);
				}
				
				this.fullScreenParent = Display.getInstance().getCurrent();
				Display.getInstance().setCurrent(this.fullScreen);
				
				if(getState() >= STATE_READY)
				{
					setDisplay(	this.source.getVideoControl(), 0, 0,
								Display.getScreenWidth(),
								Display.getScreenHeight(), 
								this.ratio);
				}
			} else {
				Display.getInstance().setCurrent(this.fullScreenParent);

				if(getState() >= STATE_READY)
				{
					setDisplay(	this.source.getVideoControl(), this.videoX, this.videoY,
								this.videoWidth, this.videoHeight, this.ratio);
				}
				
				this.fullscreen = fullscreen;
			}
			
			this.changingViewMode = false;
		} catch (Exception e) {
			//#debug error
			System.out.println("exception : " + e.getMessage());
			onVideoError(e);
		}
	}
	
	/**
	 * Prepares the video by creating the player, initializing the display and controls
	 * and notifying the listeners when its done.
	 */
	public synchronized void run() {
		
		try {
				if (getState() == STATE_NOT_PREPARED) {
					//#debug
					System.out.println("de.enough.polish.video.VideoContainer.run(): preparing video source");	
					
					//Wait till the Container is initialized
					while(!isInitialized())
					{
						try {
							Thread.sleep(500);
						} catch (Exception e) {
							// ignore
						}
						//#debug
						System.out.println("Waiting for initialization...");
					}
					//#debug
					System.out.println("starting actual initialization...");
					
					close(this.sourceToClear);
					
					if(this.source != null)
					{
						init(this.source);

						setState(STATE_READY);
						
						onVideoReady();
					
						// if this has a multipart source,prepare the next VideoSource, wait for END_OF_MEDIA
						// of the current, start the next VideoSource and close the current
						while(this.multipart != null && this.multipart.hasNext())
						{
							VideoSource oldSource = this.source;
							VideoSource nextSource = this.multipart.next();
							init(nextSource);
							wait();
							this.source = nextSource;
							play();
							oldSource.close();
						}
						if (this.startPlayAfterPrepare) {
							this.startPlayAfterPrepare = false;
							play();
						}
					}
					else {
						//#debug error
						System.out.println("source is not set");
					}
				}
			
			}
			catch (Exception e) {
				onVideoError(e);
			} 
			//#debug
			System.out.println("exit prepare thread");
	}
	
	public synchronized void next()
	{
		notify();
	}
	
	/**
	 * Initializes the source, the display and the volume
	 * 
	 * @param src the VideoSource to use
	 * @throws Exception if an error happens
	 */
	private void init(VideoSource src) throws Exception
	{
		//#debug
		System.out.println("init() for VideoSource " + src);
		initSource(src);
		initDisplay(src);
		initVolume(src);
		src.getPlayer().addPlayerListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Container#initContent(int, int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		super.initContent(firstLineWidth, availWidth, availHeight);
		this.contentWidth = availWidth;
		this.contentHeight = availHeight;
		if (this.adjustSizeAutomatically && this.source != null) {
			VideoControl videoControl = this.source.getVideoControl();
			if (videoControl != null) {
				this.contentWidth = videoControl.getDisplayWidth();
				this.contentHeight = videoControl.getDisplayHeight();
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.media.PlayerListener#playerUpdate(javax.microedition.media.Player, java.lang.String, java.lang.Object)
	 */
	public void playerUpdate(Player player, String event, Object data) {
		if (event == null) {
			return;
		}
		if (event.equals(PlayerListener.END_OF_MEDIA)) {
			if(this.multipart != null)
			{
				if(!this.multipart.hasNext())
				{
					if(isRepeat())
					{
						this.multipart.reset();
						
						this.source = this.multipart.next();
					}
					else
					{
						return;
					}
				}
				
				next();
			}
			else
			{
				if (this.repeat) {
					try {
						player.start();
					} catch (MediaException e) {
						onVideoError(e);
					}
				}
				else
				{
					stop();
				}
			}
		}
		
		if (event.equals(PlayerListener.ERROR)) {
			onVideoError(new Exception("player error"));
		}
	}

	/**
	 * Returns true if the video should be played in landscape mode
	 * @return true if landscape mode is set, otherwise false
	 */
	public boolean isLandscape() {
		return this.landscape;
	}

	/**
	 * Sets the landscape mode
	 * @param landscape true if the video should be played in landscape mode, otherwise false
	 */
	public void setLandscape(boolean landscape) {
		this.landscape = landscape;
	}

	/**
	 * Returns true if the video should be repeated
	 * @return true if the video should be repeated, otherwise false
	 */
	public boolean isRepeat() {
		return this.repeat;
	}

	/**
	 * Sets the repeat mode
	 * @param repeat true if the video should be repeated, otherwise false
	 */
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	/**
	 * Sets the ratio to playback the video
	 * @param ratio the ratio
	 */
	public void setRatio(Ratio ratio) {
		this.ratio = ratio;
	}

	/**
	 * Returns the current state of the VideoContainer
	 * @return the current state 
	 */
	public int getState() {
		return this.state;
	}

	/**
	 * Sets the state of the VideoContainer
	 * @param state the state
	 */
	private synchronized void setState(int state) {
		this.state = state;
	}

	/**
	 * Returns true if the video is played in fullscreen mode
	 * @return true if the video is played in fullscreen mode, otherwise false
	 */
	public boolean isFullscreen() {
		return this.fullscreen;
	}
	
	public void mute(boolean mute)
	{
		this.mute = mute;
		
		if(getState() >= STATE_READY)
		{
			if(this.mute)
			{
				this.source.getVolumeControl().setLevel(0);
			}
			else
			{
				this.source.getVolumeControl().setLevel(this.volume);
			}
		}
	}
	
	public boolean isMute()
	{
		return this.mute;
	}
	
	boolean changingViewMode;

	private Exception lastException;

	void showVideo()
	{
		if(this.resume && !this.changingViewMode)
		{
			play();
		}
	}
	
	void hideVideo()
	{
		if(!this.changingViewMode && getState() > STATE_CLOSED)
		{
			this.resume = (getState() == STATE_PLAYING);
			pause();
		}
	}

	protected void showNotify() {
		super.showNotify();
		
		showVideo();
	}
	
	/**
	 * Captures a snapshot when a CaptureSource is used.
	 * A registered callback listener is notified about the actions as well.
	 * In case the internal player is not yet playing, it will started automatically.
	 * 
	 * @param encoding the encoding of the image
	 * @return the corresponding byte[] data, null when an error occurs or when a VideoSource is used that is not a CaptureSource.
	 */
	public byte[] capture(String encoding) {
		byte[] data = null;
		if(this.source instanceof CaptureSource)
		{
			if (getState() < STATE_READY) {
				play();
			}
			try {
				data = ((CaptureSource)this.source).capture(encoding);
				onSnapshot(data,encoding);
			} catch (MediaException e) {
				onVideoError(e);
			}
		}
		return data;
	}
	
	protected void hideNotify() {
		super.hideNotify();
		
		hideVideo();
	}

	public void onSnapshot(byte[] data, String encoding) {
		for (int i = 0; i < this.callbacks.size(); i++) {
			callback(i).onSnapshot(data, encoding);
		}
	}

	public void onVideoClose() {
		for (int i = 0; i < this.callbacks.size(); i++) {
			callback(i).onVideoClose();
		}
	}

	public void onVideoError(Exception e) {
		//#debug error
		System.out.println("onVideoError: error=" + e );
		this.lastException = e;
		for (int i = 0; i < this.callbacks.size(); i++) {
			callback(i).onVideoError(e);
		}
	}
	
	/**
	 * Retrieves the last exception that occurred, if any
	 */
	public Exception getLastException() {
		return this.lastException;
	}

	public void onVideoPause() {
		for (int i = 0; i < this.callbacks.size(); i++) {
			callback(i).onVideoPause();
		}
	}

	public void onVideoPlay() {
		for (int i = 0; i < this.callbacks.size(); i++) {
			callback(i).onVideoPlay();
		}
	}

	public void onVideoReady() {
		for (int i = 0; i < this.callbacks.size(); i++) {
			callback(i).onVideoReady();
		}
	}

	public void onVideoStop() {
		for (int i = 0; i < this.callbacks.size(); i++) {
			callback(i).onVideoStop();
		}
	}	
}
