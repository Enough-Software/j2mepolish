/*
 * SoundInfoForm.java
 *
 * Created on 29 de junio de 2004, 19:34
 */

package com.grimo.me.product.midpsysinfo;

/**
 * Collects information about the game controls.
 * 
 * @author  Waldemar Baraldi <waldemar.baraldi@grimo-software.com>
 * @author  Robert Virkus <j2mepolish@enough.de> (architectural changes)
 */

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import de.enough.sysinfo.MIDPSysInfoMIDlet;

/**
 * Collects information about the game controls.
 * 
 * @author  Waldemar Baraldi <waldemar.baraldi@grimo-software.com>
 * @author  Robert Virkus <j2mepolish@enough.de> (architectural changes)
 */
public class KeysMapCollector extends InfoCollector
implements DynamicTestView
{
	private static String charactersMapStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ@!$%&/()=?����',;.: -_";
    private int step;
    private DynamicTest test;
	private int canvasWidth;
	private int canvasHeight;
	private final Font font;
	private Display display;
	private boolean isFinished;
    
    /** 
     * Creates a new instance of GameControlsInfoCollector
     */
    public KeysMapCollector() {
        super();
        this.font = Font.getDefaultFont();
    }

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.InfoCollector#collectInfos(com.grimo.me.product.midpsysinfo.MIDPSysInfoMIDlet, javax.microedition.lcdui.Display)
	 */
	public void collectInfos(MIDPSysInfoMIDlet midlet, Display disp) {
		this.display = disp;                
        System.out.println("testing for keys...");
        try {
        	Class.forName("javax.microedition.pki.Certificate");
        	//#if polish.useDefaultPackage
        		String className = "Midp2FullCanvasTest";
        	//#else
        		//# String className = "com.grimo.me.product.midpsysinfo.Midp2FullCanvasTest";
        	//#endif
        	Class testClass = Class.forName( className );
        	this.test = (DynamicTest) testClass.newInstance();
        } catch (Exception e) {
        	//#debug error
        	System.out.println("Unable to load Midp2FullCanvasTest" + e);
        } catch (Error e) {
        	//#debug error
        	System.out.println("Unable to load Midp2FullCanvasTest" + e);
        }
        if (this.test == null) {
	        try {
	        	Class.forName( "com.nokia.mid.ui.FullCanvas" );
	        	//#if polish.useDefaultPackage
	        		String className = "NokiaFullCanvasTest";
	        	//#else
	        		//# String className = "com.grimo.me.product.midpsysinfo.NokiaFullCanvasTest";
	        	//#endif
	        	Class testClass = Class.forName( className );
	        	this.test = (DynamicTest) testClass.newInstance();
	        } catch (Exception e) {
	        	//#debug error
	        	System.out.println("Unable to load NokiaFullCanvasTest" + e);
	        } catch (Error e) {
	        	//#debug error
	        	System.out.println("Unable to load NokiaFullCanvasTest" + e);
	        }
	        if (this.test == null) {
	        	this.test = new NormalCanvasTest();
	        }
        }
        this.test.setView( this );
        this.canvasWidth = this.test.getWidth();
        this.canvasHeight = this.test.getHeight();
        disp.setCurrent( this.test.getDisplayable() );
        

    }

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTestView#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paint(Graphics g) {
		g.setColor( 0xFFFFFF );
		g.fillRect( 0, 0, this.canvasWidth, this.canvasHeight );
		g.setColor( 0 );
		g.setFont( this.font );
		String message = null;
		char c = charactersMapStr.charAt( this.step );
		message = "Please press [" + c  + "]";
		g.drawString(message, 1, 1, Graphics.TOP | Graphics.LEFT );
		int fontHeight = this.font.getHeight() + 5; 
		g.drawString("Or press 0 to skip.", 1, fontHeight, Graphics.TOP | Graphics.LEFT );
		
	}

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTestView#keyPressed(int)
	 */
	public void keyPressed(int keyCode) {
		this.step++;
		if (this.step > charactersMapStr.length()) {
			this.isFinished = true;
			if (this.view != null) {
				this.display.setCurrent( this.view );
			}
		}
		if (keyCode != Canvas.KEY_NUM0) {
				addInfo( "Key_" + charactersMapStr.charAt( this.step - 1), "" + keyCode );
		}
		this.test.repaint();
	}

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTestView#keyReleased(int)
	 */
	public void keyReleased(int keyCode) {
		// ignore
		
	}

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTestView#keyRepeated(int)
	 */
	public void keyRepeated(int keyCode) {
		// ignore
		
	}
	
	
    
	public void show(Display disp, InfoForm infoForm) {
		this.view = infoForm;
		if (this.isFinished) {
			disp.setCurrent( infoForm );
		}
	}
	
	public boolean isFinished() {
		return this.isFinished;
	}

}