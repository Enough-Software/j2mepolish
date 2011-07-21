//#condition polish.midp || polish.usePolishGui

/*
 * Created on 23-Mar-2005 at 18:00:01.
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

import javax.microedition.lcdui.Canvas;
//#if polish.usePolishGui
	//# import de.enough.polish.ui.Display;
	//# import de.enough.polish.ui.Displayable;
//#else
	import javax.microedition.lcdui.Display;
	import javax.microedition.lcdui.Displayable;
//#endif
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


//#if polish.usePolishGui
	import de.enough.polish.ui.ScreenInfo;
	import de.enough.polish.ui.Style;
	import de.enough.polish.ui.Background;
	import de.enough.polish.ui.UiAccess;
//#endif
import de.enough.polish.util.TextUtil;

/**
 * <p>Provides a SplashScreen that initializes the real application in a background thread.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * @see ApplicationInitializer#initApp()
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class InitializerSplashScreen
//#if polish.midp2
	//# extends Canvas
//#elifdef polish.classes.fullscreen:defined
	//#= extends ${polish.classes.fullscreen}
//#else
	extends Canvas
//#endif
implements Runnable
{

	//#if polish.classes.ApplicationInitializer:defined
		//#= private final ${ polish.classes.ApplicationInitializer } initializer;
	//#else
		private final ApplicationInitializer initializer;
	//#endif
		
	private boolean isInitialized;
	private Displayable nextScreen;
	private final Display display;
	//#ifdef polish.classes.SplashView:defined
		//#= private final ${ classname( polish.classes.SplashView) } view;
	//#else
		private final Image image;
		private String readyMessage;
		private final int messageColor;
		private final int backgroundColor;
	//#endif
	//#if polish.LibraryBuild
		private final SplashView view;
	//#endif
	private String message;
	//#if polish.usePolishGui
		private Background background;
	//#endif
	private boolean isStarted;
	
	//#if !polish.classes.SplashView:defined
	/**
	 * Creates a new InitializerSplashScreen using the internal default view and a white background.
	 * 
	 * @param display the display responsible for switching screens
	 * @param image the image that is shown in the center
	 * @param initializer the application initializer that will be called in a background thread
	 */
	//#if polish.classes.ApplicationInitializer:defined
		//#= public InitializerSplashScreen( Display display, Image image, ${ polish.classes.ApplicationInitializer } initializer )
	//#else
	public InitializerSplashScreen(Display display, Image image, ApplicationInitializer initializer)
	//#endif
	{
		this(display, image, 0xffffff, null, 0, initializer );
	}
	//#endif
	
	//#if !polish.classes.SplashView:defined && polish.usePolishGui
	/**
	 * Creates a new InitializerSplashScreen using the internal default view and a white background.
	 * 
	 * @param display the display responsible for switching screens
	 * @param image the image that is shown in the center
	 * @param initializer the application initializer that will be called in a background thread
	 * @param style the style for this screen - only the background is currently applied
	 */
	//#if polish.classes.ApplicationInitializer:defined
		//#= public InitializerSplashScreen( Display display, Image image, ${ polish.classes.ApplicationInitializer } initializer, Style style )
	//#else
	public InitializerSplashScreen(Display display, Image image, ApplicationInitializer initializer, Style style)
	//#endif
	{
		this(display, image, 0xffffff, null, 0, initializer, style );
	}
	//#endif


	//#if !polish.classes.SplashView:defined
	/**
	 * Creates a new InitializerSplashScreen using the internal default view.
	 * The message will be shown in the default font.
	 * 
	 * @param display the display responsible for switching screens
	 * @param image the image that is shown in the center
	 * @param backgroundColor the background color, e.g. white: 0xFFFFFF
	 * @param readyMessage the message that is displayed when the application has been initialized, set to null if the screen created by ApplicationInitializer.initApp() should be shown directly after that method returns.
	 * @param messageColor the color for the message, e.g. black: 0
	 * @param initializer the application initializer that will be called in a background thread
	 */
	//#if polish.classes.ApplicationInitializer:defined
		//#= public InitializerSplashScreen( Display display, Image image, int backgroundColor, String readyMessage, int messageColor, ${ polish.classes.ApplicationInitializer } initializer )
	//#else
		public InitializerSplashScreen( Display display, Image image, int backgroundColor, String readyMessage, int messageColor, ApplicationInitializer initializer )
	//#endif
	{	
		this.display = display;
		this.image = image;
		this.backgroundColor = backgroundColor;
		this.readyMessage = readyMessage;
		this.messageColor = messageColor;
		this.initializer = initializer;
		//#if polish.LibraryBuild
			this.view = null;
		//#endif
	}
	//#endif
		
	//#if !polish.classes.SplashView:defined && polish.usePolishGui
		/**
		 * Creates a new InitializerSplashScreen using the internal default view.
		 * The message will be shown in the default font.
		 * 
		 * @param display the display responsible for switching screens
		 * @param image the image that is shown in the center
		 * @param backgroundColor the background color, e.g. white: 0xFFFFFF
		 * @param readyMessage the message that is displayed when the application has been initialized, set to null if the screen created by ApplicationInitializer.initApp() should be shown directly after that method returns.
		 * @param messageColor the color for the message, e.g. black: 0
		 * @param initializer the application initializer that will be called in a background thread
		 * @param style the style for this screen - only the background is currently applied
		 */
		//#if polish.classes.ApplicationInitializer:defined
			//#= public InitializerSplashScreen( Display display, Image image, int backgroundColor, String readyMessage, int messageColor, ${ polish.classes.ApplicationInitializer } initializer, Style style )
		//#else
			public InitializerSplashScreen( Display display, Image image, int backgroundColor, String readyMessage, int messageColor, ApplicationInitializer initializer, Style style )
		//#endif
		{
			this.display = display;
			this.image = image;
			this.backgroundColor = backgroundColor;
			this.readyMessage = readyMessage;
			this.messageColor = messageColor;
			this.initializer = initializer;
			//#if polish.LibraryBuild
				this.view = null;
			//#endif
			this.background = style.background;
		}
	//#endif		
		
	//#ifdef polish.classes.SplashView:defined
		//#if polish.classes.ApplicationInitializer:defined
			//#= public InitializerSplashScreen( Display display, ${ classname( polish.classes.SplashView) } view, ${ classname( polish.classes.ApplicationInitializer) } initializer )
		//#else
			//#= public InitializerSplashScreen( Display display, ${ classname( polish.classes.SplashView) } view, ApplicationInitializer initializer )
		//#endif
		//#if false
			public InitializerSplashScreen( Display display, SplashView view, ApplicationInitializer initializer )
		//#endif
		{
			this.view = view;
			this.display = display;
			this.initializer = initializer;
			//#if false
				this.image = null;
				this.backgroundColor = 0;
				this.readyMessage = null;
				this.messageColor = 0;
			//#endif			
			//#if polish.midp2 && !(polish.Bugs.fullScreenInShowNotify || polish.Bugs.fullScreenInPaint)
				super.setFullScreenMode( true );
			//#endif
		}
	//#endif


	/**
	 * Sets a message that is displayed immediately.
	 * This call is ignored when a SplashView is used.
	 * 
	 * @param message the new message, null when no message should be shown.
	 */
	public void setMessage( String message ) {
		this.message = message;
		repaint();
	}
	
	
	
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
		//#ifdef polish.classes.SplashView:defined
			this.view.paint( width, height, this.isInitialized, g);
		//#else
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
					
			g.drawImage(this.image,  width/2, height/2, Graphics.HCENTER | Graphics.VCENTER );
			if (this.isInitialized) {
				g.setColor( this.messageColor );
				Font font = Font.getDefaultFont();
				String[] lines = TextUtil.wrap( this.readyMessage, font, width - 10, width - 10 );
				int y = height - ( lines.length * ( font.getHeight() + 1 ) );
				for (int i = 0; i < lines.length; i++) {
					String line = lines[i];
					g.drawString( line, width/2, y, Graphics.TOP | Graphics.HCENTER );
					y += font.getHeight() +  1;
				}
			} else if (this.message != null) {
				g.setColor( this.messageColor );
				Font font = Font.getDefaultFont();
				String[] lines = TextUtil.wrap( this.message, font, width - 10, width - 10 );
				int y = height - ( lines.length * ( font.getHeight() + 1 ) );
				for (int i = 0; i < lines.length; i++) {
					String line = lines[i];
					g.drawString( line, width/2, y, Graphics.TOP | Graphics.HCENTER );
					y += font.getHeight() +  1;
				}
			}
		//#endif
		//#if polish.ScreenInfo.enable && polish.usePolishGui
			ScreenInfo.paint( g, 0, width );
		//#endif

	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// sleep just a little bit so that the main thread can show this splash screen.
		try {
			Thread.sleep( 10 );
		} catch (Exception e) {
			// ignore
		}
		try {
			this.nextScreen = this.initializer.initApp();
			//#if !polish.classes.SplashView:defined
				if (this.readyMessage == null && this.nextScreen != null) {
					this.display.setCurrent( this.nextScreen );
					return;
				}
			//#endif
			this.isInitialized = true;
			repaint();
		} catch (Exception e) {
			//#debug error
			System.out.println("Unable to call initApp()" + e );
			//#if !polish.classes.SplashView:defined
				this.message = "Error: " + e.toString();
				repaint();
			//#endif
		}
	}
	
	

	public void keyPressed(int keyCode) {
		if (this.isInitialized && this.nextScreen != null ) {
			this.display.setCurrent( this.nextScreen );
		}
	}
	
	public void keyReleased(int keyCode) {
		// ignore
	}

	public void keyRepeated(int keyCode) {
		// ignore
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

	//#if polish.hasPointerEvents
	/**
	 * Processes pointer dragged events.
	 * 
	 * @param x the horizontal coordinate of the clicked pixel
	 * @param y the vertical coordinate of the clicked pixel
	 */
	public void pointerDragged( int x, int y ) {
		// ignore
	}
	//#endif
	
	//#if polish.midp2
	public void sizeChanged( int width, int height ) {
		repaint();
	}
	//#endif
	
	public void showNotify() {
		//#if polish.midp2
			super.setFullScreenMode( true );
		//#endif
		if (this.isStarted) {
			return;
		}
		//#if polish.ScreenInfo.enable && polish.usePolishGui
			UiAccess.showNotify( ScreenInfo.item );
		//#endif
		this.isStarted = true;
		Thread thread = new Thread( this );
		thread.start();
	}
	
	public void hideNotify() {
		//this.isStarted = false;
		// when setting isStarted to false and there are security prompts during startup, 
		// the application will never be able to start.
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
	
}
