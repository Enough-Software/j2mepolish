package com.smaato.soma.sample;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.smaato.soma.BannerCanvas;
import com.smaato.soma.BannerCustomItem;
import com.smaato.soma.BannerItem;
import com.smaato.soma.SomaActionCallback;
import com.smaato.soma.SomaLibrary;
import com.smaato.soma.SynchronizationException;
/**
 * <p>Sample for using the SOMA library manually</p>
 */
public class SomaSampleMidlet 
extends MIDlet
implements SomaActionCallback, Runnable, CommandListener
{
	
	private static final long MY_PUBLISHER_ID = 12345;
	private static final long MY_APPLICATION_ID = 0;
	private static final long MY_SPACE_ID_STANDARD = 124;
	
	private SomaLibrary somaLibrary;
	private Command cmdExit;

	public SomaSampleMidlet() {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		if (this.somaLibrary == null) {
			Display display = Display.getDisplay( this );
			SomaLibrary lib = SomaLibrary.getInstance(MY_PUBLISHER_ID, MY_APPLICATION_ID, new long[]{MY_SPACE_ID_STANDARD}, display, this );
			// you can limit the number of cached banners for speeding up the very first retrieval of banners from the net:
			//lib.setNumberOfCachedBanners(1);
			this.somaLibrary = lib;
		}
		(new Thread( this )).start();
	}
	
	public void run() {
		BannerItem banner = null;
		try {
			banner = this.somaLibrary.getBanner( MY_SPACE_ID_STANDARD );
		} catch (SynchronizationException e) {
			e.printStackTrace();
		}
		SampleCanvas canvas = new SampleCanvas( banner );		
		Display display = Display.getDisplay( this );
		display.setCurrent( canvas );
		this.cmdExit = new Command("Exit", Command.EXIT, 5);
		canvas.addCommand( this.cmdExit );
		canvas.setCommandListener( this );
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	public void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		if (this.somaLibrary != null) {
			this.somaLibrary.exit();
		}

	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		// ignore

	}

	/* (non-Javadoc)
	 * @see com.smaato.soma.SomaActionCallback#getDisplay()
	 */
	public Display getDisplay()
	{
		return Display.getDisplay(this);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable disp)
	{
		if (cmd == this.cmdExit) {
			notifyDestroyed();
		}
	}

}

class SampleCanvas extends BannerCanvas {

	/**
	 * @param banner
	 */
	public SampleCanvas(BannerItem banner) {
		super(banner);
	}

	/* (non-Javadoc)
	 * @see com.smaato.soma.BannerCanvas#handleKeyPressed(int, int)
	 */
	public boolean handleKeyPressed(int keyCode, int gameAction) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.smaato.soma.BannerCanvas#handlePointerPressed(int, int)
	 */
	public boolean handlePointerPressed(int x, int y) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.smaato.soma.BannerCanvas#paintScreen(int, javax.microedition.lcdui.Graphics)
	 */
	public void paintScreen(int y, Graphics g) {
		g.setColor(0xffffff);
		g.fillRect(0,0, getWidth(), getHeight() );
		g.setColor( 0xff0000);
		g.fillArc( 10, y + 10, getWidth() - 10, getHeight() - 10 - y, 0, 360 );
	}
	
}

class MyCanvas2 extends Canvas implements CommandListener {
	
	private BannerItem banner;

	public MyCanvas2() {
		long adspaceId = 0;
		this.banner = SomaLibrary.getInstance().getBanner( adspaceId );
		Command cmd = this.banner.getCommand();
		addCommand( cmd );
		setCommandListener( this );
	}

	protected void paint(Graphics g)
	{
		this.banner.paint(0, 0, getWidth(), getHeight(), g);
		// now paint content...
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable disp)
	{
		if (this.banner.handleCommand(cmd)) {
			// banner handled the command:
			return;
		}
		// handle the command yourself...
		
	}

}


class MyCanvas extends Canvas {
	
	private BannerItem banner;
	private boolean bannerIsSelected = true;

	public MyCanvas() {
		long adspaceId = 0;
		this.banner = SomaLibrary.getInstance().getBanner( adspaceId );
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyPressed(int)
	 */
	protected void keyPressed(int keyCode)
	{
		int gameAction = getGameAction( keyCode );
		if (this.bannerIsSelected && this.banner.handleKeyPressed( keyCode, gameAction )) {
			return;
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyReleased(int)
	 */
	protected void keyReleased(int keyCode)
	{
		int gameAction = getGameAction( keyCode );
		if (this.bannerIsSelected && this.banner.handleKeyReleased( keyCode, gameAction )) {
			return;
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyRepeated(int)
	 */
	protected void keyRepeated(int keyCode)
	{
		int gameAction = getGameAction( keyCode );
		if (this.bannerIsSelected && this.banner.handleKeyRepeated( keyCode, gameAction )) {
			return;
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#pointerPressed(int, int)
	 */
	protected void pointerPressed(int x, int y)
	{
		if (this.banner.handlePointerPressed(x,y)) {
			return;
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#pointerReleased(int, int)
	 */
	protected void pointerReleased(int x, int y)
	{
		if (this.banner.handlePointerReleased(x,y)) {
			return;
		}
	}



	protected void paint(Graphics g)
	{
		this.banner.paint(0, 0, getWidth(), getHeight(), g);
		// now paint content...
	}

}

class MyForm extends Form {
	private BannerCustomItem banner;

	public MyForm( String title ) {
		super(title);
		long adspaceId = 0;
		this.banner = SomaLibrary.getInstance().getBannerAsCustomItem( adspaceId );
		append( this.banner );
		// append other form items...
	}
}