//#condition polish.usePolishGui && polish.midp2 && polish.javapackage.jsr184

package de.enough.polish.ui.screenanimations;

import javax.microedition.lcdui.Graphics;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Material;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.World;

import de.enough.polish.graphics3d.m3g.nodes.TexturePlane;
import de.enough.polish.graphics3d.m3g.utils.UtilitiesM3G;

import de.enough.polish.ui.AnimationThread;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;

/**
 * A M3G powered 3D Y-axis rotating screen change animation
 * <p>
 * Renders in retained mode.
 * <p>
 * Note: steps are taken to accommodate the blocking delay that occours when Image2D are created
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class RotatingScreenM3GScreenChangeAnimation extends ScreenChangeAnimation
{
    protected Graphics3D g3d;
    
    protected static TexturePlane texMatrixOne, texMatrixTwo;
    
    protected static Group texPlaneRoot;
    
    protected Background sceneBG;
    
    protected static World scene;
    
    protected boolean rotateDirection = false; 
    protected float rotateYAngle = .0f;
    protected float degreesPerSec = 180; //rotation speed
    protected float degreesPerFrame = .0f; //rotation speed
    protected float scaleFactor = .0f;
    
    private boolean doneSwitch = false;
    private volatile boolean initializedNewTexplane = false;
    
	protected boolean animate()
	{
		Node child = texPlaneRoot.getChild(0);
		
		//candidate for new angle in next frame
		float newAngle = this.rotateYAngle - this.degreesPerFrame;
		
		if(newAngle < -89f && !this.doneSwitch)
		{
			//is state correct and has 2nd textureplane been initialized?
	    	if(child == texMatrixOne && this.initializedNewTexplane)
	    	{
	    		texPlaneRoot.removeChild(texMatrixOne);
	    		texPlaneRoot.addChild(child = texMatrixTwo);
	    		
	    		newAngle = 89f;
	    		
	    		this.doneSwitch = true;
	    	}
	    	else
	    		newAngle = -90f;
		}
		else
		//is animation complete?
		if(this.doneSwitch && newAngle < 0.001f)
		{
			this.rotateYAngle = .0f;
			if(null != texMatrixOne)
				texMatrixOne.setOrientation(this.rotateYAngle, 0f, 1f, 0f);
			if(null != texMatrixTwo)
				texMatrixTwo.setOrientation(this.rotateYAngle, 0f, 1f, 0f);
			return false;
		}
		
		this.rotateYAngle = newAngle;
		
		//#debug debug
		System.out.println("this.rotateYAngle: "+this.rotateYAngle);
		
		if(null != child)
			child.setOrientation(this.rotateYAngle, 0f, 1f, 0f);
		
		return true;
	}

	protected void paintAnimation(Graphics g) 
	{
    	try
    	{
    		// bind graphics
    		this.g3d.bindTarget(g);
    		
    		// render in retained mode
    		this.g3d.render(scene);
		} 
    	finally 
    	{
    		// flush
    		this.g3d.releaseTarget();
		}
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
    		System.out.println("Init start");
    		
    		System.out.println("Used mem: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) );
    	//#enddebug
    		
    	this.doneSwitch = false;
    	
    	this.rotateYAngle = .0f;
    	
    	this.degreesPerFrame = this.degreesPerSec * ((float)AnimationThread.ANIMATION_INTERVAL/1000f);
    	
    	if(null == this.g3d)
    		this.g3d = Graphics3D.getInstance();
        	
    	if(null == scene)
    	{
    		synchronized (this.g3d)
    		{
    			//build entire scene
    			
    			scene = new World();
    			
    			//setup camera
    			Camera cam = new Camera();
    			cam.setPerspective( 64f,  // field of view
    					(float)getWidth() / (float)getHeight(),  // aspectRatio
    					1.0f,      // near clipping plane
    					50.0f ); // far clipping plane
    			cam.setTranslation(0.0f, 0.0f, 10.0f);
    			
    			scene.addChild(cam);
    			
    			//setup lights
    			Light light = new Light();
    			
    			scene.addChild(light);
    			
    			//init bg
    			scene.setBackground(null); //transparent black
    			
    			// default surface specs
    			final Material planeMaterial = new Material();
    			
    			// Plane polygn mode, ensure precise rendering of texture surface
    			final PolygonMode polymode = new PolygonMode();
    			polymode.setShading(PolygonMode.SHADE_FLAT);
    			polymode.setPerspectiveCorrectionEnable(true);
    			
    			//create Plane apperance
    			final Appearance appearance = new Appearance();
    			appearance.setMaterial(planeMaterial);
    			appearance.setPolygonMode(polymode);
    			
    			// Create plane textures. Texture possibly broken down into subplane due to texture dimension limits
    			texMatrixOne = new TexturePlane(this.lastCanvasImage, UtilitiesM3G.M3G_MAX_TEXTURE_DIMENSION, false, appearance);
    			
    			texPlaneRoot = new Group();
    			texPlaneRoot.addChild(texMatrixOne);
    			
    			scene.addChild(texPlaneRoot);
    			
    			scene.setActiveCamera(cam);
    			
    			//load in seperate thread to avoid blocking
    			this.initializedNewTexplane = false;
    			new Thread()
    			{
    				public void run() 
    				{
    					texMatrixTwo = new TexturePlane(RotatingScreenM3GScreenChangeAnimation.this.nextCanvasImage, UtilitiesM3G.M3G_MAX_TEXTURE_DIMENSION, false, appearance);
    					if(RotatingScreenM3GScreenChangeAnimation.this.scaleFactor > 0.01f)
    						texMatrixTwo.setScale( RotatingScreenM3GScreenChangeAnimation.this.scaleFactor, RotatingScreenM3GScreenChangeAnimation.this.scaleFactor, 1f);
    					RotatingScreenM3GScreenChangeAnimation.this.initializedNewTexplane = true;
    				}
    			}.start();
			}
    	}
    	else
    	{
    		synchronized (this.g3d)
    		{
    			//only update planes
    			
    			long updateStart = System.currentTimeMillis();
    			
    			texPlaneRoot.removeChild(texMatrixOne);
    			texPlaneRoot.removeChild(texMatrixTwo);
    			texPlaneRoot.addChild(texMatrixOne);
    			
    			texMatrixOne.setTexture(this.lastCanvasImage);
    			
    			//load in seperate thread to avoid blocking
    			this.initializedNewTexplane = false;
    			new Thread()
    			{
    				public void run() 
    				{
    					texMatrixTwo.setTexture(RotatingScreenM3GScreenChangeAnimation.this.nextCanvasImage);
    					RotatingScreenM3GScreenChangeAnimation.this.initializedNewTexplane = true;
    				}
    			}.start();
    			
    			//#debug debug
    			System.out.println("Update total: "+(System.currentTimeMillis()-updateStart));
			}
    	}
    	
    	final int numTilePerRow = texMatrixOne.getNumSubQuadPerRow();
    	final int numTilePerColumn = texMatrixOne.getNumSubQuadPerColumn();
    	final int tileSetWidth = numTilePerRow * texMatrixOne.getTextureDimension();
    	final int tileSetHeight = numTilePerColumn * texMatrixOne.getTextureDimension();
        float cameraDistanceScaleFactor =  6.2486935190932750978051082794944f; //opposite = Tan(A) * adjacent, Math.tan(64/2) * 10;
    	
    	float baseScale = 1f / Math.max(numTilePerRow, numTilePerColumn);
    	float ratioScale = (float)Math.max(tileSetWidth, tileSetHeight) / (float)Math.max(Display.getScreenWidth(), Display.getScreenHeight());
   		this.scaleFactor = baseScale * ratioScale * cameraDistanceScaleFactor;
   		
    	texMatrixOne.setScale( this.scaleFactor, this.scaleFactor, 1f);
    	if(null != texMatrixTwo)
    		texMatrixTwo.setScale( this.scaleFactor, this.scaleFactor, 1f);
    	
   		//#mdebug debug
	    	
	    	long end = System.currentTimeMillis();
			System.out.println("Init end. Total init time: " + (float)(end - start)/1000f + " secs" );
		}
    	catch (Exception e)
    	{
    		System.out.println("Error initialising: " + e.toString());
    		e.printStackTrace();
		}
    	//#enddebug
	}
}
