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
public class KeysInfoCollector extends InfoCollector
implements DynamicTestView
{
	
	private static String[] keys = new String[] {
				"key.LeftSoftKey:left softkey",
				"key.RightSoftKey:right softkey",
				"key.MiddleSoftKey:middle softkey",
				"key.ReturnKey:back/return key",
				"key.ClearKey:clear/delete key",
				"key.SelectKey:select key",
				"key.left:left key",
				"key.right:right key",
				"key.up:up key",
				"key.down:down key",
				"key.fire:fire key"
	};

	/* nokia full keyboard */
	/*
	private static final int STEP_Q_KEY = 5;
	private static final int STEP_W_KEY = 6;
	private static final int STEP_E_KEY = 7;
	private static final int STEP_R_KEY = 8;
	private static final int STEP_T_KEY = 9;
	private static final int STEP_A_KEY = 10;
	private static final int STEP_S_KEY = 11;
	private static final int STEP_D_KEY = 12;
	private static final int STEP_F_KEY = 13;
	private static final int STEP_G_KEY = 14;
	private static final int STEP_Y_KEY = 15;
	private static final int STEP_X_KEY = 16;
	private static final int STEP_C_KEY = 17;
	private static final int STEP_V_KEY = 18;
	private static final int STEP_B_KEY = 19;
	private static final int STEP_Z_KEY = 20;
	private static final int STEP_U_KEY = 21;
	private static final int STEP_I_KEY = 22;
	private static final int STEP_O_KEY = 23;
	private static final int STEP_P_KEY = 24;
	private static final int STEP_UE_KEY = 25;
	private static final int STEP_H_KEY = 26;
	private static final int STEP_J_KEY = 27;
	private static final int STEP_K_KEY = 28;
	private static final int STEP_L_KEY = 29;
	private static final int STEP_OE_KEY = 30;
	private static final int STEP_AE_KEY = 31;
	private static final int STEP_N_KEY = 32;
	private static final int STEP_M_KEY = 33;
	private static final int STEP_KOMMA_KEY = 34;
	private static final int STEP_DOT_KEY = 35;
	private static final int STEP_USCORE_KEY = 36;
	private static final int STEP_ENTER_KEY = 37;
	private static final int STEP_BACKSPACE_KEY = 38;
	private static final int STEP_TILDE_KEY = 39;
	private static final int STEP_LSHIFT_KEY = 40;
	private static final int STEP_RSHIFT_KEY = 41;
	private static final int STEP_POUND_KEY = 42;
	private static final int STEP_CTRL_KEY = 43;
	private static final int STEP_SCHAR_KEY = 44;
	private static final int STEP_1_KEY = 45;
	private static final int STEP_2_KEY = 46;
	private static final int STEP_3_KEY = 47;
	private static final int STEP_4_KEY = 48;
	private static final int STEP_5_KEY = 49;
	private static final int STEP_6_KEY = 50;
	private static final int STEP_7_KEY = 51;
	private static final int STEP_8_KEY = 52;
	private static final int STEP_9_KEY = 53;
	private static final int STEP_0_KEY = 54;
	*/
	
    private int step;
    private DynamicTest test;
    private Canvas canvas;
	private int canvasWidth;
	private int canvasHeight;
	private final Font font;
	private Display display;
	private boolean isFinished;
    //private MIDPSysInfoMIDlet midlet = null;
    /** 
     * Creates a new instance of GameControlsInfoCollector
     */
    public KeysInfoCollector() {
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
        	this.canvas = (Canvas) this.test;
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
		//g.fillRect( 0, 0, this.canvas.getWidth(), this.canvas.getHeight() );
		g.setColor( 0 );
		g.setFont( this.font );
		String key = keys[this.step];
		int pos = key.indexOf(':');
		String message = key.substring( pos + 1);
		g.drawString(message, 1, 1, Graphics.TOP | Graphics.LEFT );
		int fontHeight = this.font.getHeight() + 5; 
		g.drawString("Or press 0 to skip.", 1, fontHeight, Graphics.TOP | Graphics.LEFT );
		//g.drawString( this.canvas.getWidth() + "x" + this.canvas.getHeight(), 1, fontHeight << 1, Graphics.TOP | Graphics.LEFT );
		
	}

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTestView#keyPressed(int)
	 */
	public void keyPressed(int keyCode) {
		if (keyCode == Canvas.KEY_NUM0) {
			this.step++;
			
			if (this.step > keys.length - 1) {
				this.isFinished = true;
				
				if (this.view != null) {
					this.display.setCurrent( this.view );
				}
			} else {
				this.test.repaint();
			}
			return;
		}
		String key = keys[this.step];
		int pos = key.indexOf(':');
		String info = key.substring(0, pos);
		addInfo( info, "" + keyCode );
		this.step++;
		if (this.step > keys.length - 1) {
			this.isFinished = true;
			Canvas canvas = (Canvas) this.test;
	        addInfo( "Canvas.UP: ", "" + canvas.getKeyCode(Canvas.UP));
	        addInfo( "Canvas.LEFT: ", "" + canvas.getKeyCode(Canvas.LEFT));
	        addInfo( "Canvas.RIGHT: ","" + canvas.getKeyCode(Canvas.RIGHT));
	        addInfo( "Canvas.DOWN: ", "" + canvas.getKeyCode(Canvas.DOWN));
	        addInfo( "Canvas.FIRE: ", "" + canvas.getKeyCode(Canvas.FIRE));
	        addInfo( "Canvas.GAME_A: ", "" + canvas.getKeyCode(Canvas.GAME_A));
	        addInfo( "Canvas.GAME_B: ", "" + canvas.getKeyCode(Canvas.GAME_B));
	        addInfo( "Canvas.GAME_C: ", "" + canvas.getKeyCode(Canvas.GAME_C));
	        addInfo( "Canvas.GAME_D: ", "" + canvas.getKeyCode(Canvas.GAME_D));

	        /* send */
	        /*
	        try{
	        	SysInfoServer remoteServer = (SysInfoServer) RemoteClient.open("de.enough.sysinfo.SysInfoServer","http://sysinfo.j2mepolish.org/sysinfoserver");
	        	Hashtable infos = new Hashtable();
	        	Info[] infoArray = getInfos();
	        	int infoSize = getInfos().length;
	        	for(int i=0; i<infoSize; i++){
	        		infos.put(infoArray[i].name, infoArray[i].value);
	        	}
	        	remoteServer.sendKeys(infos);
	        }catch(Exception e){
	        	
	        }
	        */
	        
	        if (this.view != null) {
				this.display.setCurrent( this.view );
			}
		} else {
			this.test.repaint();
		}
	}

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTestView#keyReleased(int)
	 */
	public void keyReleased(int keyCode) {
		// TODO enough implement keyReleased
		
	}

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTestView#keyRepeated(int)
	 */
	public void keyRepeated(int keyCode) {
		// TODO enough implement keyRepeated
		
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