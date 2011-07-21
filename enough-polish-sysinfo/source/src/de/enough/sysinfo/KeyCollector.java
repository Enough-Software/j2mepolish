package de.enough.sysinfo;

import java.util.Hashtable;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.grimo.me.product.midpsysinfo.DynamicTest;
import com.grimo.me.product.midpsysinfo.DynamicTestView;
import com.grimo.me.product.midpsysinfo.InfoCollector;
import com.grimo.me.product.midpsysinfo.InfoForm;
import com.grimo.me.product.midpsysinfo.NormalCanvasTest;

public class KeyCollector extends InfoCollector implements DynamicTestView{

	private static final int STEP_LEFT_SOFT_KEY = 0; 
	private static final int STEP_RIGHT_SOFT_KEY = 1; 
	private static final int STEP_MIDDLE_SOFT_KEY = 2;
	private static final int STEP_CLEAR_KEY = 3; 
	private static final int STEP_RETURN_KEY = 4; 
	private static final int STEP_SELECT_KEY = 5; 
	private int step;
    private DynamicTest test;
	private int canvasWidth;
	private int canvasHeight;
	private final Font font;
	private Display display;
	private boolean isFinished;
    private MIDPSysInfoMIDlet sysinfoMIDlet = null;

    /** 
     * Creates a new instance of GameControlsInfoCollector
     */
    public KeyCollector() {
        super();
        this.font = Font.getDefaultFont();
    }
    
    public KeyCollector(MIDPSysInfoMIDlet midlet) {
        super();
        this.font = Font.getDefaultFont();
        this.sysinfoMIDlet = midlet;
        this.sysinfoMIDlet.keyInfos = new Hashtable();
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
		switch (this.step) {
			case STEP_LEFT_SOFT_KEY:
				message = "Press left Soft-Key";
				break;
			case STEP_RIGHT_SOFT_KEY:
				message = "Press right Soft-Key";
				break;
			case STEP_MIDDLE_SOFT_KEY:
				message = "Press middle Soft-Key";
				break;
			case STEP_CLEAR_KEY:
				message = "Press Clear/Delete-Key";
				break;
			case STEP_RETURN_KEY:
				message = "Press Return/Back-Key";
				break;
			case STEP_SELECT_KEY:
				message = "Press Select-Key";
				break;
			
			default:
				message = "Test finished - please wait.";
		}
		g.drawString(message, 1, 1, Graphics.TOP | Graphics.LEFT );
		int fontHeight = this.font.getHeight() + 5; 
		g.drawString("Or press 0 to skip.", 1, fontHeight, Graphics.TOP | Graphics.LEFT );
		
	}

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTestView#keyPressed(int)
	 */
	public void keyPressed(int keyCode) {
		if (keyCode == Canvas.KEY_NUM0) {
			this.step++;
			if (this.step > STEP_SELECT_KEY) {
				this.isFinished = true;
				if(this.sysinfoMIDlet != null){
					System.out.println("setting identform");
					this.display.setCurrent(this.sysinfoMIDlet.identForm);
					return;
				}
				if (this.view != null) {
					this.display.setCurrent( this.view );
				}
			} else {
				this.test.repaint();
			}
			return;
		}
		switch (this.step) {
			case STEP_LEFT_SOFT_KEY:
				addInfo( "Left Soft-Key: ", "" + keyCode );
				this.sysinfoMIDlet.keyInfos.put("key.LeftSoftKey", String.valueOf(keyCode));
				break;
			case STEP_RIGHT_SOFT_KEY:
				addInfo( "Right Soft-Key: ", "" + keyCode );
				this.sysinfoMIDlet.keyInfos.put("key.RightSoftKey", String.valueOf(keyCode));
				break;
			case STEP_MIDDLE_SOFT_KEY:
				addInfo( "Middle Soft-Key: ", "" + keyCode );
				this.sysinfoMIDlet.keyInfos.put("key.MiddleSoftKey", String.valueOf(keyCode));
				break;
			case STEP_CLEAR_KEY:
				addInfo( "Clear-Key: ", "" + keyCode );
				this.sysinfoMIDlet.keyInfos.put("key.ClearKey", String.valueOf(keyCode));
				break;
			case STEP_RETURN_KEY:
				addInfo( "Return-Key: ", "" + keyCode );
				this.sysinfoMIDlet.keyInfos.put("key.ReturnSoftKey", String.valueOf(keyCode));
				break;
			case STEP_SELECT_KEY:
				addInfo( "Select-Key: ", "" + String.valueOf(keyCode) );
				this.sysinfoMIDlet.keyInfos.put("key.SelectSoftKey", String.valueOf(keyCode));
				try {
					addInfo( "gameAction(Select-Key) == FIRE: ", "" + (((Canvas)this.test).getGameAction( keyCode ) == Canvas.FIRE) );
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to retrieve game action of Select Key " + keyCode + e );
				}
				break;
		}
		this.step++;
		if (this.step > STEP_SELECT_KEY) {
			this.isFinished = true;
			Canvas canvas = (Canvas) this.test;
	        addInfo( "Canvas.UP: ", canvas.getKeyName(canvas.getKeyCode(Canvas.UP)));
	        addInfo( "Canvas.LEFT: ", canvas.getKeyName(canvas.getKeyCode(Canvas.LEFT)));
	        addInfo( "Canvas.RIGHT: ", canvas.getKeyName(canvas.getKeyCode(Canvas.RIGHT)));
	        addInfo( "Canvas.DOWN: ", canvas.getKeyName(canvas.getKeyCode(Canvas.DOWN)));
	        addInfo( "Canvas.FIRE: ", canvas.getKeyName(canvas.getKeyCode(Canvas.FIRE)));
	        addInfo( "Canvas.GAME_A: ", canvas.getKeyName(canvas.getKeyCode(Canvas.GAME_A)));
	        addInfo( "Canvas.GAME_B: ", canvas.getKeyName(canvas.getKeyCode(Canvas.GAME_B)));
	        addInfo( "Canvas.GAME_C: ", canvas.getKeyName(canvas.getKeyCode(Canvas.GAME_C)));
	        addInfo( "Canvas.GAME_D: ", canvas.getKeyName(canvas.getKeyCode(Canvas.GAME_D)));
			
	        if(this.sysinfoMIDlet != null){
				System.out.println("setting identform");
				this.display.setCurrent(this.sysinfoMIDlet.identForm);
				return;
			}
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
