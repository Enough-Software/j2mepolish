/*
 * DisplayInfoForm.java
 *
 * Created on 29 de junio de 2004, 18:56
 */

package com.grimo.me.product.midpsysinfo;


import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.sysinfo.MIDPSysInfoMIDlet;

/**
 * Tests display capabilities of the current device.
 * 
 * @author  Waldemar Baraldi <waldemar.baraldi@grimo-software.com>
 * @author  Robert Virkus <robert.virkus@enough.de> (added detection of detailed FullCanvas settings)
 * @author  Mark Schrijver <mark.schrijver@mobillion.nl> (font sizes)
 */
public class DisplayInfoCollector extends InfoCollector {
    
    
    public DisplayInfoCollector(){
    	super();
    }
    

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.InfoCollector#collectInfos(com.grimo.me.product.midpsysinfo.MIDPSysInfoMIDlet, javax.microedition.lcdui.Display)
	 */
	public void collectInfos(MIDPSysInfoMIDlet midlet, Display display) {
	       Canvas canvas = new Canvas() {
            public void paint(Graphics g){
            	// do nothing
            }
        };
        canvas.addCommand( new Command("DummyCommand", Command.SCREEN, 1 ));
        
        boolean isColor = display.isColor();
        int numColors = display.numColors();
                
        if ( ! isColor ){
            addInfo( "Display type: ", "Grayscale");
            addInfo( "Levels: ", Integer.toString(numColors) );
        } else {
        	addInfo( "Display type: ", "Color" );
        	addInfo( "Colors: ", Integer.toString(numColors) );
        }
        
        //addInfo( "Canvas width: ", Integer.toString(canvas.getWidth()) );
        //addInfo( "Canvas height: ", Integer.toString(canvas.getHeight()) );
        addInfo( "CanvasSize: ", Integer.toString(canvas.getWidth()) + "x" + Integer.toString(canvas.getHeight()) );
        try {
        	Class fullCanvasClass = Class.forName( "com.nokia.mid.ui.FullCanvas" );
        	if (fullCanvasClass != null) {
        		addNokiaFullCanvasTest();
        	}
        } catch (Exception e) {
        	//#debug error
        	System.out.println("Unable to load NokiaFullCanvasTest" + e);
        } catch (Error e) {
        	//#debug error
        	System.out.println("Unable to load NokiaFullCanvasTest" + e);
        }
        try {
        	Class.forName("javax.microedition.pki.Certificate");
        	//#if polish.useDefaultPackage
        		String className = "Midp2FullCanvasTest";
        	//#else
        		//# String className = "com.grimo.me.product.midpsysinfo.Midp2FullCanvasTest";
        	//#endif
        	Class testClass = Class.forName( className );
        	DynamicTest test = (DynamicTest) testClass.newInstance();
        	Displayable disp = (Displayable) test;
        	display.setCurrent(disp);
        	(new WaitThread(display, test )).start();
        	//test.addTestResults( this );
        } catch (Exception e) {
        	//#debug error
        	System.out.println("Unable to load Midp2FullCanvasTest" + e);
        } catch (Error e) {
        	//#debug error
        	System.out.println("Unable to load Midp2FullCanvasTest" + e);
        }
        addInfo( "Canvas.isDoubleBuffered: ", "" + canvas.isDoubleBuffered() );
        
        Font font = Font.getDefaultFont();
        addInfo("Default-Font-Height: ", "" + font.getHeight() );
        font = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL );
        addInfo("Small-Font-Height: ", "" + font.getHeight() );
        font = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM );
        addInfo("Medium-Font-Height: ", "" + font.getHeight() );
        font = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE );
        addInfo("Large-Font-Height: ", "" + font.getHeight() );
        
        try{
        	if(canvas.hasRepeatEvents()){
        		addInfo("hasRepeatEvents", "yes");
        	}else{
        		addInfo("hasRepeatEvents", "no");
        	}
        }catch (Exception e) {
        	addInfo("hasRepeatEvents", "no");
        }
        
        try{
        	//Display.numAlphaLevels()  Display.numColors()
        	addInfo("alphaLevels", String.valueOf(display.numAlphaLevels()));
        }catch (Exception e) {
        	//nothing to do
        }
	}


	private void addNokiaFullCanvasTest() {
		try {
        	Class.forName( "com.nokia.mid.ui.FullCanvas" );
        	//#if polish.useDefaultPackage
        		String className = "NokiaFullCanvasTest";
        	//#else
        		//# String className = "com.grimo.me.product.midpsysinfo.NokiaFullCanvasTest";
        	//#endif
        	Class testClass = Class.forName( className );
        	DynamicTest test = (DynamicTest) testClass.newInstance();
        	test.addTestResults( this );
        } catch (Exception e) {
        	//#debug error
        	System.out.println("Unable to load NokiaFullCanvasTest" + e);
        } catch (Error e) {
        	//#debug error
        	System.out.println("Unable to load NokiaFullCanvasTest" + e);
        }
	}
	
	private class WaitThread extends Thread {
		private final Display display;
		private final DynamicTest test;

		/**
		 * @param display
		 * @param disp
		 * @param test 
		 */
		public WaitThread( Display display, DynamicTest test ) {
			this.display = display;
			this.test = test;
		}
		
		public void run() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				//ignore
			}
			this.test.addTestResults(DisplayInfoCollector.this);
			if (getView() != null) {
				this.display.setCurrent( getView() );
			}
		}
	}
    
}

