//#condition polish.api.mmapi

/*
 * Created on 20-Oct-2008 at 22:36:01.
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
package de.enough.polish.ui.splash;

import java.io.InputStream;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VideoControl;

//#if polish.usePolishGui
	import de.enough.polish.ui.Background;
	import de.enough.polish.ui.Style;
	import de.enough.polish.ui.StyleSheet;
//#endif

/**
 * <p>Provides a SplashScreen that initializes the real application in a background thread while playing back a movie.</p>
 * <p>When the movie playback has finished and the application is initialized, the next screen provided by
 * ApplicationInitializer.initApp() will be shown automatically.
 * </p>
 * <p>Note that this screen is only available when the target device supports the MMAPI (<code>//#if polish.api.mmapi</code>).</p>
 *
 * <p>Copyright (c) Enough Software 2008</p>
 * @see ApplicationInitializer#initApp()
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class InitializerMovieScreen
//#if polish.midp2 || polish.usePolishGui
	//# extends Canvas
//#elifdef polish.classes.fullscreen:defined
	//#= extends ${polish.classes.fullscreen}
//#else
	extends Canvas
//#endif
implements Runnable, PlayerListener
{

	//#if polish.classes.ApplicationInitializer:defined
		//#= private final ${ polish.classes.ApplicationInitializer } initializer;
	//#else
		private final ApplicationInitializer initializer;
	//#endif
		
	private Displayable nextScreen;
	private final Display display;
	private final int backgroundColor;
	//#if polish.usePolishGui
		private Background background;
	//#endif
	private boolean isStarted;
	private final String movieUrl;
	private Player player;
	private boolean isVideoFinished;
	private VideoControl videoControl;
	private String mimeType;
	
	/**
	 * Creates a new InitializerMovieScreen using the provided movie URL and a white background.
	 * 
	 * @param display the display responsible for switching screens
	 * @param movieUrl the URL to the movie that should be played
	 * @param mimeType the mime type of the movie, e.g. image/gif
	 * @param initializer the application initializer that will be called in a background thread
	 */
	//#if polish.classes.ApplicationInitializer:defined
		//#= public InitializerMovieScreen( Display display, String movieUrl, String mimeType, ${ polish.classes.ApplicationInitializer } initializer )
	//#else
	public InitializerMovieScreen(Display display, String movieUrl, String mimeType, ApplicationInitializer initializer)
	//#endif
	{
		this(display, movieUrl, mimeType, 0xffffff, initializer );
	}
	
	//#if polish.usePolishGui
	/**
	 * Creates a new InitializerMovieScreen using the provided movie URL and the background specified by the style.
	 * 
	 * @param display the display responsible for switching screens
	 * @param movieUrl the URL to the movie that should be played
	 * @param mimeType the mime type of the movie, e.g. image/gif
	 * @param initializer the application initializer that will be called in a background thread
	 * @param style the style for this screen - only the background is currently applied
	 */
	//#if polish.classes.ApplicationInitializer:defined
		//#= public InitializerMovieScreen( Display display, String movieUrl, String mimeType, ${ polish.classes.ApplicationInitializer } initializer, Style style )
	//#else
	public InitializerMovieScreen(Display display, String movieUrl, String mimeType, ApplicationInitializer initializer, Style style)
	//#endif
	{
		this(display, movieUrl, mimeType, 0xffffff, initializer, style );
	}
	//#endif


	/**
	 * Creates a new InitializerMovieScreen using the internal default view.
	 * The message will be shown in the default font.
	 * 
	 * @param display the display responsible for switching screens
	 * @param movieUrl the URL to the movie that should be played
	 * @param mimeType the mime type of the movie, e.g. image/gif
	 * @param backgroundColor the background color, e.g. white: 0xFFFFFF
	 * @param initializer the application initializer that will be called in a background thread
	 */
	//#if polish.classes.ApplicationInitializer:defined
		//#= public InitializerMovieScreen( Display display, String movieUrl, String mimeType, int backgroundColor, ${ polish.classes.ApplicationInitializer } initializer )
	//#else
		public InitializerMovieScreen( Display display, String movieUrl, String mimeType, int backgroundColor,  ApplicationInitializer initializer )
	//#endif
	{	
		this.display = display;
		this.movieUrl = movieUrl;
		this.mimeType = mimeType;
		this.backgroundColor = backgroundColor;
		this.initializer = initializer;
	}
		
	//#if  polish.usePolishGui
		/**
		 * Creates a new InitializerMovieScreen using the internal default view.
		 * The message will be shown in the default font.
		 * 
		 * @param display the display responsible for switching screens
		 * @param movieUrl the URL to the movie that should be played
		 * @param mimeType the mime type of the movie, e.g. image/gif
		 * @param backgroundColor the background color, e.g. white: 0xFFFFFF
		 * @param initializer the application initializer that will be called in a background thread
		 * @param style the style for this screen - only the background is currently applied
		 */
		//#if polish.classes.ApplicationInitializer:defined
			//#= public InitializerMovieScreen( Display display, String movieUrl, String mimeType, int backgroundColor, ${ polish.classes.ApplicationInitializer } initializer, Style style )
		//#else
			public InitializerMovieScreen( Display display, String movieUrl, String mimeType, int backgroundColor,  ApplicationInitializer initializer, Style style )
		//#endif
		{
			this.display = display;
			this.movieUrl = movieUrl;
			this.mimeType = mimeType;
			this.backgroundColor = backgroundColor;
			this.initializer = initializer;
			this.background = style.background;
		}
	//#endif		
		

	
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paint(Graphics g) {
		//#if polish.Bugs.fullScreenInPaint
			super.setFullScreenMode(true);
		//#endif
		//#if polish.FullCanvasSize:defined && !polish.usePolishGui
			//#= int height = ${polish.FullCanvasHeight};
			//#= int width = ${polish.FullCanvasWidth};
		//#else
			int height = getHeight();
			int width = getWidth();
		//#endif
		//#if polish.usePolishGui
			if (this.background != null) {
				this.background.paint( 0, 0, width, height, g );
			} else {
		//#endif
				g.setColor( this.backgroundColor );
				g.fillRect( 0, 0, width, height );					
		//#if polish.usePolishGui
			}
		//#endif
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			startVideo();
			initApp();
		} catch (Exception e) {
			//#debug error
			System.out.println("Unable to call initApp()" + e );
		}
	}
	
	

	/**
	 * 
	 */
	private void startVideo()
	{
		if (this.player == null) {
			InputStream is = this.getClass().getResourceAsStream( this.movieUrl );
			if (is == null) {
				//#debug error
				System.out.println("did not find video resource " + this.movieUrl);
				this.isVideoFinished = true;
				return;
			}
	        try
	        {
	            this.player = Manager.createPlayer( is, this.mimeType );
	            this.player.realize();
	            VideoControl control = ( VideoControl ) this.player.getControl( "VideoControl" );
	            if (control == null) {
	            	//#debug error
	            	System.out.println("Unable to retrieve VideoControl");
					this.isVideoFinished = true;
	            	return;
	            }
	            //#if polish.usePolishGui
	            	control.initDisplayMode( VideoControl.USE_DIRECT_VIDEO, StyleSheet.display );
	            //#else
	            	control.initDisplayMode( VideoControl.USE_DIRECT_VIDEO, this );
	            //#endif
	            int sourceWidth = control.getSourceWidth();
	            int sourceHeight = control.getSourceHeight();
	            int width = getWidth();
	            int height = getHeight();
				int x = (width >> 1) - (sourceWidth >> 1);
				int y = (height >> 1) - (sourceHeight >> 1);
				control.setDisplayLocation(x, y);
	            control.setVisible( true );
	            this.videoControl = control;
	            this.player.addPlayerListener( this );
	            this.player.start();
	        }
	        catch (Exception e)
	        {
	        	//#debug error
	        	System.out.println("unable to start video " + e);
				this.isVideoFinished = true;
	        }
		}
	}

	/**
	 * 
	 */
	private void initApp()
	{
		this.nextScreen = this.initializer.initApp();
		if (this.isVideoFinished) {
			this.display.setCurrent( this.nextScreen );
		}
	}
	
	public void keyPressed(int keyCode) {
		if (this.isVideoFinished && this.nextScreen != null ) {
			this.display.setCurrent( this.nextScreen );
		}
	}
	
	
	//#ifdef polish.hasPointerEvents
	public void pointerPressed(int x, int y) {
		keyPressed( 0 );
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	public void pointerReleased(int x, int y) {
		keyReleased( 0 );
	}
	//#endif

	//#if polish.midp2
	public void sizeChanged( int width, int height ) {
		repaint();
	}
	//#endif
	
	public void showNotify() {
		//#if polish.midp2 && !polish.usePolishGui
			super.setFullScreenMode( true );
		//#endif
		if (this.isStarted) {
			return;
		}
		this.isStarted = true;
		Thread thread = new Thread( this );
		thread.start();
	}
	
	public void hideNotify() {
		if (this.isVideoFinished && this.player != null) {
			if (this.videoControl != null) {
				try {
					this.videoControl.setVisible(false);
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to invisible player" + e);
				}
				this.videoControl = null;
			}
			try {
				this.player.close();
			} catch (Exception e) {
				//#debug error
				System.out.println("Unable to close player" + e);
			}
			this.player = null;
		}
	}

	//#if polish.usePolishGui
	/**
	 * Retrieves the background.
	 * Warning: this method is only available when the J2ME Polish GUI is used, check for the polish.usePolishGui preprocessing symbol
	 * 
	 * @return the background
	 */
	public Background getBackground() {
		return this.background;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the background of this splash screen.
	 * Warning: this method is only available when the J2ME Polish GUI is used, check for the polish.usePolishGui preprocessing symbol
	 * 
	 * @param background the background to set
	 */
	public void setBackground(Background background) {
		this.background = background;
		repaint();
	}
	//#endif
	/* (non-Javadoc)
	 * @see javax.microedition.media.PlayerListener#playerUpdate(javax.microedition.media.Player, java.lang.String, java.lang.Object)
	 */
	public void playerUpdate(Player p, String event, Object data)
	{
		if (END_OF_MEDIA.equals(event)) {
			this.isVideoFinished = true;
			if (this.nextScreen != null) {
				this.display.setCurrent(this.nextScreen);
			}
		}
		
	}

}
