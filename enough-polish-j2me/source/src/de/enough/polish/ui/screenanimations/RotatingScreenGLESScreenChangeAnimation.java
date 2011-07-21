//#condition polish.usePolishGui && polish.midp2 && polish.javapackage.jsr239

package de.enough.polish.ui.screenanimations;

import javax.microedition.khronos.egl.EGL;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.math.FP;
import de.enough.polish.ui.AnimationThread;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;

import de.enough.polish.graphics3d.gles.nodes.TexturePlane;

/**
 * A GLES powered 3D Y-axis rotating screen change animation
 * <p>
 * Note: steps are taken to accommodate the blocking delay that occurs when binding textures
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class RotatingScreenGLESScreenChangeAnimation extends ScreenChangeAnimation
{
	private static EGL egl;
    private static EGL10 egl10;
    
    private static GL gl;
    private static GL10 gl10;
    
    private static Image offScreenBufffer;
    private static Graphics g2D;
    
    private static EGLDisplay eglDisplay;
    private static EGLSurface eglSurface;
    private static EGLContext eglContext;
    
    protected static TexturePlane texPlaneOne, texPlaneTwo, currentTexPlane;
    
    protected boolean rotateDirection = false; 
    protected int rotateYAnglex = 0;
    protected int degreesPerSecx = FP.intToFix(180);
    protected int degreesPerFramex = 0;
    protected int millisperSecx = FP.intToFix(1000);
    protected int eightyNinex = FP.intToFix(89);
    protected int scaleFactorx = 0;
    
    protected static int maxTextureSize;
    
    private boolean doneSwitch = false;
    private volatile boolean initializedNewTexplane = false;
    
	protected boolean animate()
	{
   		//#mdebug debug
    	try
    	{
		//#enddebug		
    		
		//candidate for new angle in next frame
		int newAnglex = this.rotateYAnglex - this.degreesPerFramex;
		
		if(newAnglex < - this.eightyNinex && !this.doneSwitch)
		{
			//is state correct and has 2nd textureplane been initialized?
	    	if(currentTexPlane == texPlaneOne && this.initializedNewTexplane)
	    	{
	    		currentTexPlane = texPlaneTwo;
	    		
	    		newAnglex = this.eightyNinex;
	    		
	    		this.doneSwitch = true;
	    	}
	    	else
	    		newAnglex = - (this.eightyNinex + FP.FIX_ONE);
		}
		else
		//is animation complete?
		if(this.doneSwitch && newAnglex < FP.FIX_EPS)
		{
			this.rotateYAnglex = 0;
			if(null != texPlaneOne)
				texPlaneOne.setRotateYx(0);
			if(null != texPlaneTwo)
				texPlaneTwo.setRotateYx(0);
			return false;
		}
		
		this.rotateYAnglex = newAnglex;
		
		currentTexPlane.setRotateYx(this.rotateYAnglex);
		
   		//#mdebug debug
		}
		catch (Exception e)
		{
			System.out.println("Error animating GLES scene: " + e.toString());
			e.printStackTrace();
		}
		//#enddebug		
		
		return true;
	}

	protected void paintAnimation(Graphics g) 
	{
	    // wait until OpenGL ES is available before starting to draw
	    egl10.eglWaitNative(EGL10.EGL_CORE_NATIVE_ENGINE, g2D);

	    // clear colour and depth buffers
	    gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

	    // set modeling and viewing transformations
	    gl10.glMatrixMode(GL10.GL_MODELVIEW);
	    gl10.glLoadIdentity();

	    // position the 'camera'
	    gl10.glTranslatex(0, 0, - FP.intToFix(10));
	    
	    // render plane
	    currentTexPlane.render();
	    
	    // wait until all Open GL ES tasks are finished
	    gl10.glFinish();  
	    egl10.eglWaitGL();	
	    
	    //draw off screen buffer
	    g.drawImage(offScreenBufffer, 0, 0, 0);
	}
	
	protected void onShow(Style style, Display dsplay, int width, int height,
			Displayable lstDisplayable, Displayable nxtDisplayable,
			boolean isForward) 
	{
		super.onShow(style, dsplay, width, height, lstDisplayable, nxtDisplayable,
				isForward);
		
    	//#mdebug debug
    	try 
    	{
    		long start = System.currentTimeMillis();
    		System.out.println("onShow() start");
    		
    		System.out.println("Used mem: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) );
    	//#enddebug
    		
    	this.doneSwitch = false;
    	
    	this.rotateYAnglex = 0;
    	
    	this.degreesPerFramex = FP.mul( this.degreesPerSecx, FP.div( FP.intToFix((int)AnimationThread.ANIMATION_INTERVAL), this.millisperSecx) );
    	
    	if(null == egl)
    	{
    		//build entire scene
    		
    		//bind GLES to native rendering system
    		bindGraphics();
    		
    		synchronized (egl)
    		{
    			//init projection matrix
    			setView( FP.intToFix(64), FP.FIX_ONE, FP.intToFix(50));
    			
    			//init black bg
    			gl10.glClearColor(.0f, .0f, .0f, 1.0f);
    			
    			//enable depth buffer test for hidden surface removal
    			gl10.glEnable(GL10.GL_DEPTH_TEST); 
    			gl10.glDepthFunc(GL10.GL_LEQUAL);
    			
    			//enable backface culling
    			gl10.glEnable(GL10.GL_CULL_FACE);
    			
    			gl10.glShadeModel(GL10.GL_SMOOTH);    // use smooth shading
    			
    			//enable perspective correction if possible for nice texture rendering
    			gl10.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    			
            	int[] params = new int[1];
            	gl10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, params, 0);
            	
            	maxTextureSize = params[0];
    			
    			// Create plane textures. Texture possibly broken down into subplane due to texture dimension limits
    			texPlaneOne = new TexturePlane(gl10, this.lastCanvasImage, maxTextureSize, false);
				
    			texPlaneTwo = new TexturePlane(gl10, this.nextCanvasImage, maxTextureSize, false);
    			if(this.scaleFactorx > FP.FIX_EPS)
    				texPlaneTwo.setScalex( this.scaleFactorx, this.scaleFactorx, FP.FIX_ONE);
    			this.initializedNewTexplane = true;
			}
    	}
    	else
    	{
    		synchronized (egl)
    		{
    			//only update planes
    			
    			texPlaneOne.setTexture(this.lastCanvasImage);
    			
				texPlaneTwo.setTexture(this.nextCanvasImage);
				this.initializedNewTexplane = true;
			}
    	}
    	
		final int cameraDistanceScaleFactorx = 409514; //FP.floatToFix( 6.2486935190932750978051082794944f ), opposite = Tan(A) * adjacent, Math.tan(64/2) * 10;
    	
    	final int numTilePerRow = texPlaneOne.getNumSubQuadPerRow();
    	final int numTilePerColumn = texPlaneOne.getNumSubQuadPerColumn();
    	final int tileSetWidth = numTilePerRow * texPlaneOne.getTextureDimension();
    	final int tileSetHeight = numTilePerColumn * texPlaneOne.getTextureDimension();
    	
    	int baseScalex = FP.div( FP.FIX_ONE, FP.intToFix( Math.max(numTilePerRow, numTilePerColumn) ) );
    	int ratioScalex = FP.div( FP.intToFix( Math.max(tileSetWidth, tileSetHeight) ) , FP.intToFix( Math.max(Display.getScreenWidth(), Display.getScreenHeight()) ) );
    	this.scaleFactorx = FP.mul( FP.mul(baseScalex , ratioScalex) , cameraDistanceScaleFactorx );
    	
		//#mdebug debug
//		System.out.println("numTilePerRow: "+numTilePerRow);
//		System.out.println("numTilePerColumn: "+numTilePerColumn);
//		System.out.println("tileSetWidth: "+tileSetWidth);
//		System.out.println("tileSetHeight: "+tileSetHeight);
//		System.out.println("baseScalex: "+FP.fixToFloat(baseScalex));
//		System.out.println("ratioScalex: "+FP.fixToFloat(ratioScalex));
//		System.out.println("scaleFactorx: "+FP.fixToFloat(this.scaleFactorx));
		//#enddebug
   		
   		texPlaneOne.setScalex( this.scaleFactorx, this.scaleFactorx, FP.FIX_ONE);
    	if(null != texPlaneTwo)
    		texPlaneTwo.setScalex( this.scaleFactorx, this.scaleFactorx, FP.FIX_ONE);
    	
    	currentTexPlane = texPlaneOne;
    	
   		//#mdebug debug
	    	
	    	long end = System.currentTimeMillis();
			System.out.println("onShow() end. Total time: " + (float)(end - start)/1000f + " secs" );
		}
    	catch (Exception e)
    	{
    		System.out.println("Error initialising GLES scene: " + e.toString());
    		e.printStackTrace();
		}
    	//#enddebug
	}
	
	private void bindGraphics()
	{
		//#mdebug debug
		try 
		{
			long bindStart = System.currentTimeMillis();
			
		//#enddebug
		
			egl = EGLContext.getEGL();
			//#mdebug debug
			if(null == egl)
			{
				System.out.println("Error: Failed fetching a EGL isntance");
			}
			//#enddebug
			egl10 = (EGL10) egl;

			//fetch default native
			eglDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
			//#mdebug debug
			if(null == eglDisplay)
			{
				System.out.println("Error: Failed getting connection to EGLDisplay");
			}
			//#enddebug

			//init display connection
			int[] majorMinor = new int[2];
			egl10.eglInitialize(eglDisplay, majorMinor);
			
			//define number of available configs
			int[] numConfigs = new int[1];
			egl10.eglGetConfigs(eglDisplay, null, 0, numConfigs);
			//#mdebug debug
		    if (numConfigs[0] < 1) 
		    {
		    	System.out.println("Error: No configurations identified");
			}
			//#enddebug
		    
			//settle desired configuration by attribute
			int configAttributes[] = {
					EGL10.EGL_RED_SIZE, 8,
					EGL10.EGL_GREEN_SIZE, 8,
					EGL10.EGL_BLUE_SIZE, 8,
					EGL10.EGL_ALPHA_SIZE, EGL10.EGL_DONT_CARE,
					EGL10.EGL_DEPTH_SIZE, EGL10.EGL_DONT_CARE,
					EGL10.EGL_STENCIL_SIZE, EGL10.EGL_DONT_CARE,
//					EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT,
					EGL10.EGL_NONE
			};

			//find config matching attributes
			int numTotalAvailConfigs = numConfigs[0];
			EGLConfig[] eglConfigs = new EGLConfig[numTotalAvailConfigs];
			boolean foundConfig = egl10.eglChooseConfig(eglDisplay, configAttributes, eglConfigs, eglConfigs.length, numConfigs);
			
			//no matching config found, using default
			if(!foundConfig || null == eglConfigs[0])
			{
				foundConfig = egl10.eglChooseConfig(eglDisplay, null, eglConfigs, eglConfigs.length, numConfigs);
			}
			//#mdebug debug
			if(!foundConfig || null == eglConfigs[0])
			{
				//fetch and output all configurations
				egl10.eglGetConfigs(eglDisplay, eglConfigs, eglConfigs.length, numConfigs);
				String newline = "\n";
				StringBuffer sb = new StringBuffer();
				for(int i = eglConfigs.length; --i>=0;)
				{
					System.out.println(eglConfigs[i].toString());
					sb.append(eglConfigs[i].toString()+newline);
				}
				System.out.println("Error: No cofigurations to choose. Complete list:"+newline+sb.toString());
			}
			//#enddebug
			
			EGLConfig eglConfig = eglConfigs[0];
			
			//#mdebug debug
//			System.out.println("Chosen config: "+eglConfig.toString());
//			int value[] = {0};
//			egl10.eglGetConfigAttrib(eglDisplay, eglConfig, EGL10.EGL_RED_SIZE, value);
//			System.out.println("EGL10.EGL_RED_SIZE: "+value[0]);
//			egl10.eglGetConfigAttrib(eglDisplay, eglConfig, EGL10.EGL_GREEN_SIZE, value);
//			System.out.println("EGL10.EGL_GREEN_SIZE: "+value[0]);
//			egl10.eglGetConfigAttrib(eglDisplay, eglConfig, EGL10.EGL_BLUE_SIZE, value);
//			System.out.println("EGL10.EGL_BLUE_SIZE: "+value[0]);
//			egl10.eglGetConfigAttrib(eglDisplay, eglConfig, EGL10.EGL_ALPHA_SIZE, value);
//			System.out.println("EGL10.EGL_ALPHA_SIZE: "+value[0]);
//			egl10.eglGetConfigAttrib(eglDisplay, eglConfig, EGL10.EGL_DEPTH_SIZE, value);
//			System.out.println("EGL10.EGL_DEPTH_SIZE: "+value[0]);
//			egl10.eglGetConfigAttrib(eglDisplay, eglConfig, EGL10.EGL_STENCIL_SIZE, value);
//			System.out.println("EGL10.EGL_STENCIL_SIZE: "+value[0]);
			//#enddebug
			
			//get rendering context
			eglContext =  egl10.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, null);
			//#mdebug debug
			if(null == eglContext)
			{
				System.out.println("Error: Unable to create a rendering state");
			}
			//#enddebug
			
			//get rendering handles
			if(null == offScreenBufffer || Display.getScreenWidth() != offScreenBufffer.getWidth() || Display.getScreenHeight() != offScreenBufffer.getHeight())
				offScreenBufffer = Image.createImage(Display.getScreenWidth(), Display.getScreenHeight());
			g2D = offScreenBufffer.getGraphics();   // 2D
			gl = eglContext.getGL();  // master handle
			//#mdebug debug
			if(null == gl)
			{
				System.out.println("Error: Unable to create a 3D graphics context");
			}
			//#enddebug
			gl10 = (GL10) gl; // 3D
			
			//define rendering surface
			eglSurface = egl10.eglCreatePixmapSurface(eglDisplay, eglConfig, g2D, null);
			
			//#mdebug debug
			if(null == gl)
			{
				System.out.println("Error: Unable to create a drawing windows");
			}
			//#enddebug
			
			//make context current
			egl10.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);			
			
		//#mdebug debug
			
			System.out.println("bindGraphics() time: "+(System.currentTimeMillis()-bindStart));
		}
		catch (Exception e)
		{
			int eglError = egl10.eglGetError();
			
			System.out.println("Error while performing OpenGL ES binding. EGL Error: "+eglError+"\n"+e.toString());
			e.printStackTrace();
		}
		//#enddebug
	}	
	
	/**
	 * Set up view frustum
	 * 
	 * @param fovyx camera field of view in degrees and as fixed point number
	 * @param nearx distance to near clipping plane in fixed point number
	 * @param farx distance to far clipping plane in fixed point number
	 */
	private void setView(int fovyx, int nearx, int farx)
	{
		int width = Display.getScreenWidth();
		int height = Display.getScreenHeight();
		int widthx = FP.intToFix(width);
		int heightx = FP.intToFix(height);
		
		gl10.glViewport(
				0,
				0,
				width,
				height
				);    

		int aspectRatiox = FP.div(widthx, heightx);

	    int topx = FP.mul( nearx , FP.tan( FP.div( FP.mul(fovyx, FP.FIX_PI), FP.FIX_360) ) );
	    
	    int bottomx = -topx;
	    int leftx = FP.mul(bottomx, aspectRatiox);
	    int rightx = FP.mul(topx, aspectRatiox);

	    gl10.glMatrixMode(GL10.GL_PROJECTION);
	    gl10.glLoadIdentity();
	    gl10.glFrustumx(leftx, rightx, bottomx, topx, nearx, farx); 
	}
}
