package com.smaato.soma.impl;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;

import com.smaato.soma.BannerItem;
import com.smaato.soma.SomaActionCallback;
import com.smaato.soma.SomaLibrary;

import de.enough.polish.ui.MasterCanvas;
import de.enough.polish.ui.StyleSheet;

/**
 * <p>Displays a banner in a Canvas - this is a convenience class that can be used by implementations for easily displaying banners.</p>
 *
 * <p>Copyright Smaato Inc. 2007, 2008</p>
 */
public abstract class SomaPolishCanvas extends Canvas {
	private static SomaActionCallback POLISH_CALLBACK = null;
	private static final long APPLICATION_ID =
	//#if soma.applicationId:defined
		//#= ${soma.applicationId};
	//#elif smaato.application.id:defined
		//#= ${smaato.application.id};
	//#else
		0;
	//#endif
	private static int AD_SPACE_ID = 
	//#if soma.adspaceId:defined
		//#=  ${soma.adspaceId};
	//#elif smaato.adspace.id:defined
		//#=  ${smaato.adspace.id};
	//#else
		0;
	//#endif
	private static int PUBLISHER_ID = 
	//#if soma.publisherId:defined
		//#= ${soma.publisherId};
	//#elif smaato.publisher.id:defined
		//#=  ${smaato.adspace.id};
	//#else
		0;
	//#endif
	
		
	
	private BannerItem banner;
	private long lastShowNotifyTime;
	

	/**
	 * Creates a new canvas.
	 */
	public SomaPolishCanvas() {
		super();
	}
	
	/**
	 * Sets a new banner for this canvas.
	 * @param banner
	 */
	public final void setBanner( BannerItem banner ) {
		if (this.banner != null && this.banner.getCommand() != null) {
			removeCommand( this.banner.getCommand() );
		}
		this.banner = banner;
		if (banner != null) {
			Command cmd = banner.getCommand();
			if (cmd != null) {
				addCommand( cmd );
			}
		}
		int h = super.getHeight();
		//#if polish.FullCanvasHeight:defined
			//#= h = ${polish.FullCanvasHeight};
		//#endif
		if (banner != null) {
			h -= banner.getHeight();
		}
		sizeChanged( getWidth(), h );
		repaint();
	}
	
	
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#hideNotify()
	 */
	protected void hideNotify()
	{
		if (this.banner != null) {
			getSomaLibrary().releaseBanner( this.banner );
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#showNotify()
	 */
	protected void showNotify()
	{
		long current = System.currentTimeMillis();
		if (current - this.lastShowNotifyTime > 10 * 1000 ) {
			this.lastShowNotifyTime = current;
			(new BannerThread()).start();
		}
	}

	private SomaLibrary getSomaLibrary() {
		if (POLISH_CALLBACK == null) {
			POLISH_CALLBACK = new PolishSomaActionCallback();
		}
		SomaLibrary somaLibrary = SomaLibrary.getInstance(PUBLISHER_ID , APPLICATION_ID, new long[]{ AD_SPACE_ID }, StyleSheet.display, POLISH_CALLBACK );
		//#if soma.banner.backgroundcolor:defined
			int color = 0xffffff;
			//#= color = ${soma.banner.backgroundcolor};
			somaLibrary.setBannerBackgroundColor( color );
		//#endif
		return somaLibrary;
	}

	public void paint( Graphics g ) {
		if (this.banner != null) {
			int height = this.banner.getHeight();
			int width = getWidth();
			g.setColor(0);
			g.fillRect(0, 0, width, height );
			this.banner.paint(0,0,width, height, g);
			g.translate( 0, height );
		}
	}
	


	/**
	 * Handles key events.
	 * When the implementation does not handle the event, the event is forwarded to the banner.
	 * @param keyCode the code of the key
	 * @see #handleKeyPressed(int, int)
	 */
	protected void keyPressed( int keyCode ) {
	}

	/**
	 * Handles the pointer press event.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @see #handlePointerPressed(int, int)
	 */
	protected void pointerPressed(int x, int y) {
	}
	
	/**
	 * Tries to handle the specified command.
	 * The default implementation forwards the call to the container. When the container is unable to process the command,
	 * it will be forwarded to an external command listener that has been set using setCommandListener(..)
	 * 
	 * @param cmd the command
	 * @return true when the command has been handled by this screen
	 */	
	protected boolean handleCommand( Command cmd ) {
		if (this.banner == null) {
			return false;
		}
		return this.banner.handleCommand( cmd );
	}

	
	
//	/* (non-Javadoc)
//	 * @see javax.microedition.lcdui.Displayable#getWidth()
//	 */
//	public int getWidth() {
//		int w = super.getWidth();
//		if (this.banner != null) {
//			w -= this.banner.getWidth();
//		}
//		return w;
//	}

	/**
	 * Retrieves the available height
	 * @return the available height
	 */
	public int getCanvasHeight()
	{
		int h; 
		//#if polish.Bugs.displaySetCurrentFlickers || polish.MasterCanvas.enable
			h = MasterCanvas.getScreenHeight();
			if (h == 0) {
				h = super.getHeight();
			}
		//#elif polish.FullCanvasHeight:defined
			//#= h = ${polish.FullCanvasHeight};
		//#else
			h = super.getHeight();
		//#endif
		//
		if (this.banner != null) {
			h -= this.banner.getHeight();
		}
		return h;
	}


	/**
	 * Forwards a repaint request.
	 * 
	 * @param x the x coordinate of the area that needs to be refreshed
	 * @param y the y coordinate of the area that needs to be refreshed
	 * @param width the width of the area that needs to be refreshed
	 * @param height the height of the area that needs to be refreshed
	 */
	protected void requestRepaint( int x, int y, int width, int height ) {
		if (this.banner != null) {
			y += this.banner.getHeight();
		}
		repaint( x, y, width, height );
	}

	private class BannerThread extends Thread {
		public BannerThread() {
			// nothing to init
		}
		public void run() {
			try {
				BannerItem ad = getSomaLibrary().getBanner( AD_SPACE_ID );
				setBanner( ad );
			} catch (Exception e) {
				//#debug error
				System.out.println("Unable to load banner " + e );
			}
		}
	}
}
