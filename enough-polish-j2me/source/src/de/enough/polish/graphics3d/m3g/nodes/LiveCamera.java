//#condition polish.javapackage.jsr184

package de.enough.polish.graphics3d.m3g.nodes;

import javax.microedition.lcdui.Canvas;
import javax.microedition.m3g.Camera;

/**
 * A camera abstraction that allows user to navigate the camera
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class LiveCamera extends Camera
{
	protected float rotAngleY;
	
	public void handleInput( int keyCode, int gameAction )
	{
		switch (gameAction) 
		{
		case Canvas.LEFT:
			this.translate(-.25f, 0, 0);
			break;
		case Canvas.RIGHT:
			this.translate(.25f, 0, 0);
			break;
		case Canvas.UP:
			this.translate(0, .25f, 0);
			break;
		case Canvas.DOWN:
			this.translate(0, -.25f, 0);
			break;
		default:
			break;
		}
		
		switch (keyCode) 
		{
		//zoom in
		case Canvas.KEY_NUM1:
			this.translate(0, 0, -1f);
			break;
		//zoom out
		case Canvas.KEY_NUM3:
			this.translate(0, 0, 1f);
			break;
		//rotate Y axis cw before other transforms hence keeping focus on origo
		case Canvas.KEY_NUM7:
			this.rotAngleY -= 1.0f;
			this.setOrientation(this.rotAngleY, 0, 1f, 0);
			break;
		case Canvas.KEY_NUM9:
			this.rotAngleY += 1.0f;
			this.setOrientation(this.rotAngleY, 0, 1f, 0);
			break;		
		default:
			break;
		}
		
		//#mdebug debug
    	float[] trans = new float[3];
    	getTranslation(trans);
    	
    	System.out.println("Cam x: "+ trans[0]+ " y: "+ trans[1]+ " z: "+trans[2]);
    	
    	System.out.println("Rotation Y: "+rotAngleY);
    	//#enddebug
	}
}
