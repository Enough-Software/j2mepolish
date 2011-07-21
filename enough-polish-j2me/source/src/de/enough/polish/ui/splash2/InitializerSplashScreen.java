//#condition polish.usePolishGui
/*
 * Created on 15-Dec-2010 at 09:50:45.
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
package de.enough.polish.ui.splash2;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Canvas;
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

	private final ApplicationInitializer initializer;
		
	private final Image image;
	private final int backgroundColor;
	private String message;
	private int messageColor;
	private boolean isStarted;
	
	/**
	 * Creates a new InitializerSplashScreen using a white background.
	 * 
	 * @param image the image that is shown in the center
	 * @param initializer the application initializer that will be called in a background thread
	 */
	public InitializerSplashScreen(Image image, ApplicationInitializer initializer)
	{
		this(image, 0xffffff, initializer );
	}
	
	
	/**
	 * Creates a new InitializerSplashScreen using the internal default view.
	 * The message will be shown in the default font.
	 * 
	 * @param image the image that is shown in the center
	 * @param backgroundColor the background color, e.g. white: 0xFFFFFF
	 * @param initializer the application initializer that will be called in a background thread
	 */
	public InitializerSplashScreen( Image image, int backgroundColor, ApplicationInitializer initializer )
	{	
		this.image = image;
		this.backgroundColor = backgroundColor;
		this.initializer = initializer;
	}
		

	/**
	 * Sets a message that is displayed immediately.
	 * This call is ignored when a SplashView is used.
	 * 
	 * @param message the new message, null when no message should be shown.
	 * @param color the color of the message
	 */
	public void setMessage( String message, int color ) {
		this.messageColor = color;
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
		int height = getHeight();
		int width = getWidth();
		g.setColor( this.backgroundColor );
		g.fillRect( 0, 0, width, height );
		if (this.image != null) {
			g.drawImage(this.image,  width/2, height/2, Graphics.HCENTER | Graphics.VCENTER );
		}
		if (this.message != null) {
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
			this.initializer.initApp();
		} catch (Exception e) {
			//#debug error
			System.out.println("Unable to call initApp()" + e );
			this.message = "Error: " + e.toString();
			repaint();
		}
	}
	
	//#if polish.midp2
	/*
	 * (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#sizeChanged(int, int)
	 */
	public void sizeChanged( int width, int height ) {
		repaint();
	}
	//#endif
	
	/*
	 * (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#showNotify()
	 */
	public void showNotify() {
		//#if polish.midp2
			super.setFullScreenMode( true );
		//#endif
		if (this.isStarted) {
			return;
		}
		this.isStarted = true;
		Thread thread = new Thread( this );
		thread.start();
	}
	
}
