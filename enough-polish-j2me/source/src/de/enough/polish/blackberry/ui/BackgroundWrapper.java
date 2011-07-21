//#condition polish.blackberry && polish.usePolishGui && polish.JavaPlatform >= BlackBerry/4.6
/**
 * 
 */
package de.enough.polish.blackberry.ui;

import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.decor.Background;

/**
 * @author Besitzer
 *
 */
public class BackgroundWrapper extends Background {

	public static final BackgroundWrapper INSTANCE = new BackgroundWrapper();
	
	private de.enough.polish.ui.Background background;
	private final Graphics graphics;
	
	public BackgroundWrapper() {
		this( null );
	}
	
	/**
	 * Creates a new wrapper
	 */
	public BackgroundWrapper( de.enough.polish.ui.Background background) {
		this.background = background;
		this.graphics = new Graphics();
	}
	
	public void setBackground( de.enough.polish.ui.Background background ) {
		this.background = background;
	}

	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.decor.Background#draw(net.rim.device.api.ui.Graphics, net.rim.device.api.ui.XYRect)
	 */
	public void draw(net.rim.device.api.ui.Graphics g, XYRect rect) {
//		if (this.background != null) {
//			this.graphics.setGraphics(g);
//			//#if true
//				//# this.background.paint(rect.x, rect.y, rect.width, rect.height, this.graphics );
//			//#endif
//		}
	}

	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.decor.Background#isTransparent()
	 */
	public boolean isTransparent() {
		return true;
	}

}
