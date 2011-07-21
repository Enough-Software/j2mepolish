//#condition polish.usePolishGui
/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt. 
  * 
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 * 
 * This version has be adjusted for use in J2ME Polish.
 */ 

/*
 * SplashScreen.java
 *
 * Created on August 26, 2005, 10:19 AM
 */

package de.enough.polish.ui;


import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * This component represents a splash screen, which is usually being displayed
 * when the application starts. It waits for a specified amount of time (by default
 * 5000 milliseconds) and then calls specified command listener commandAction method
 * with DISMISS_COMMAND as command parameter.
 * <p/>
 * This version is using CommandListener and static Command pattern, but is still
 * compatible with older version. So if there is no command listener specified,
 * it still can use setNextDisplayable() method to specify the dismiss screen and
 * automatically switch to it.
 * @author breh
 */
public class SplashScreen extends Screen {

        
    /**
     * Command fired when the screen is about to be dismissed
     * //TODO add i18n
     */
    public static final Command DISMISS_COMMAND = new Command("Dismiss",Command.OK,0);
    
    /**
     * Timeout value which wait forever. Value is "0".
     */
    public static final int FOREVER = 0;
    
    private static final int DEFAULT_TIMEOUT = 5000;
    
    private int timeout = DEFAULT_TIMEOUT;
    private boolean allowTimeoutInterrupt = true;
    
    private long currentDisplayTimestamp;

	private Image image;
    
    /**
     * Creates a new instance of SplashScreen
     * @param display display - is ignored
     */
    public SplashScreen(Display display) throws IllegalArgumentException  {
        super( null, false );
    }

    /**
     * Creates a new instance of SplashScreen
     * 
     * @param display display - is ignored
     * @param style the style for the splash screen 
     */
    public SplashScreen(Display display, Style style) throws IllegalArgumentException  {
        super( null, false, style );
    }

    // properties
    
    
    /**
     * Sets the timeout of the splash screen - i.e. the time in milliseconds for
     * how long the splash screen is going to be shown on the display.
     * <p/>
     * If the supplied timeout is 0, then the splashscreen waits forever (it needs to
     * be dismissed by pressing a key)
     *
     * @param timeout in milliseconds
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    /**
     * Gets current timeout of the splash screen
     *
     * @return timeout value
     */
    public int getTimeout() {
        return this.timeout;
    }
    
    /**
     * When set to true, the splashscreen timeout can be interrupted 
     * (and thus dismissed) by pressing a key.
     *
     * @param allow true if the user can interrupt the screen, false if the user need to wait
     * until timeout.
     */
    public void setAllowTimeoutInterrupt(boolean allow) {
        this.allowTimeoutInterrupt = allow;
    }
    
    /**
     * Can be the splashscreen interrupted (dismissed) by the user pressing a key?
     * @return true if user can interrupt it, false otherwise
     */
    public boolean isAllowTimeoutInterrupt() {
        return this.allowTimeoutInterrupt;
    }
    
    
    // canvas methods
    
    /**
     * keyPressed callback
     * @param keyCode
     */
    public void keyPressed(int keyCode) {
        if (this.allowTimeoutInterrupt) {
            doDismiss();
        }
    }
    
    
    /**
     * pointerPressed callback
     * @param x
     * @param y
     */
    public void pointerPressed(int x, int y) {
        if (this.allowTimeoutInterrupt) {
            doDismiss();
        }
    }
    
    /**
     * starts the coundown of the timeout
     */
    public void showNotify() {
        super.showNotify();
        // start watchdog task - only when applicable
        this.currentDisplayTimestamp = System.currentTimeMillis();
        if (this.timeout > 0) {
            Watchdog w = new Watchdog(this.timeout, this.currentDisplayTimestamp);
            w.start();
        }
    }
    
    
    public void hideNotify() {
        super.hideNotify();
        this.currentDisplayTimestamp = System.currentTimeMillis();
    }
    
    
    
    // private stuff
    
    private void doDismiss() {
        CommandListener commandListener = getCommandListener();
        if (commandListener == null) {
        	//#debug
            System.out.println("no command listener set");
        } else {
            commandListener.commandAction(DISMISS_COMMAND,this);
        }
    }
    
    
    
    private class Watchdog extends Thread {
        
        private int wdTimeout;
        private long wdCurrentDisplayTimestamp;
        
        private Watchdog(int timeout, long currentDisplayTimestamp) {
            this.wdTimeout = timeout;
            this.wdCurrentDisplayTimestamp = currentDisplayTimestamp;
        }
        
        public void run() {
            try {
                Thread.sleep(this.wdTimeout);
            } catch (InterruptedException ie) {
            	// Ignored.
            }
            // doDismiss (only if current display timout matches) - this means this
            // splash screen is still being shown on the display
            if (this.wdCurrentDisplayTimestamp == SplashScreen.this.currentDisplayTimestamp) {
                doDismiss();
            }
        }
        
        
    }
    
    public void setImage( Image image ) {
    	this.image = image;
    }

    


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#paintScreen(javax.microedition.lcdui.Graphics)
	 */
	protected void paintScreen(Graphics g) {
		if (this.image != null) {
			g.drawImage( this.image, getWidth()/2, getHeight()/2, Graphics.HCENTER | Graphics.VCENTER );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#createCssSelector()
	 */
	protected String createCssSelector() {
		return "splashscreen";
	}


}
