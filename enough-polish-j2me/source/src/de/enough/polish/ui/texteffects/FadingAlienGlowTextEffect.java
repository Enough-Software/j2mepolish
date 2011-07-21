//#condition polish.usePolishGui && polish.midp2 && polish.cldc1.1
/**
 * 
 */
package de.enough.polish.ui.texteffects;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.DrawUtil;

/**
 * <p>Paints an alien glow text effect, whereas you are able to specify
 *  the inner and outer color as well as the font-color. This particular 
 *  version supports also an anminated outer color (look at FadeTextEffect for details).</p>
 * <p>Activate the alien glow text effect by specifying <code>text-effect: fading-alien-glow;</code> in your polish.css file.
 *    You can finetune the effect with following attributes:
 * </p>
 * <ul>
 * 	 <li><b>alien-glow-inner-color</b>: the inner color of the alien glow, which is usually and by default white. </li>
 * 	 <li><b>alien-glow-outer-color</b>: the outer color of the alien glow, which is green by default. </li>
 *   <li><b>font-color:</b>: You should set the font-color black in order to get an nice alien glow effect. </li>
 * </ul>
 * <p>Choosing the same inner and outer color and varying the transparency is recommended. Dropshadow just works, if the Text is opaque.</p>
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        14-Jul-2006
 * </pre>
 * @author Simon Schmitt
 * 
 */
public class FadingAlienGlowTextEffect extends TextEffect {
	
	private transient final DrawUtil.FadeUtil outerFader;
	
	private final static int CLEAR_COLOR = 0xFF000123;
	private int clearColor;

	private String lastText;
	int[] argbBuffer;

	private int innerColor = 0xFFFFFFFF;
	private int outerColor; // is determined by outerFader
	
	/**
	 * Creates a new FadingAlienGlowTextEffect 
	 */
	public FadingAlienGlowTextEffect()
	{
		this.outerFader =new DrawUtil.FadeUtil();
		this.isTextSensitive = true;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g) {
		
		final int radius=3+1;
		
		// the faders part:
		// intialisation
		if (this.lastText!=text || text==null){ // TODO: check for other changes
			this.outerFader.changed=true;
			this.lastText=text;
		}
		// do not draw if you do not know the color
		if (this.outerFader.changed){
			this.outerFader.step();
		}
		
		if (text==null) {
			return;
		}
		
		this.outerColor=this.outerFader.cColor;

		// the alien-glows part:
		//calculate imagesize
		Font font = g.getFont();
		int fHeight = font.getHeight();
		int fWidth = font.stringWidth( text );
		int newWidth=fWidth + radius*2;
		int newHeight=fHeight+ radius*2;
				
		// create Image, Graphics, ARGB-buffer
		Graphics bufferG;
		Image midp2ImageBuffer = Image.createImage( newWidth, newHeight);
		bufferG = midp2ImageBuffer.getGraphics();
		this.argbBuffer = new int[ (newWidth) * (newHeight) ];
		
		// draw pseudo transparent Background
		bufferG.setColor( CLEAR_COLOR );
		bufferG.fillRect(0,0,newWidth, newHeight);
		
		// draw String on Graphics
		bufferG.setFont(font);
		
		// draw outlineText
		bufferG.setColor( this.outerColor );
		bufferG.drawString(text,radius-1,radius-1, Graphics.LEFT | Graphics.TOP);
		bufferG.drawString(text,radius-1,radius+1, Graphics.LEFT | Graphics.TOP);
		bufferG.drawString(text,radius+1,radius-1, Graphics.LEFT | Graphics.TOP);
		bufferG.drawString(text,radius+1,radius+1, Graphics.LEFT | Graphics.TOP);
		
		bufferG.setColor( this.innerColor );
		bufferG.drawString(text,radius,radius, Graphics.LEFT | Graphics.TOP);
		
		// get RGB-Data from Image
		midp2ImageBuffer.getRGB(this.argbBuffer,0,newWidth, 0, 0, newWidth, newHeight);
		
		// check clearColor
		int[] clearColorArray = new int[1]; 
		midp2ImageBuffer.getRGB(clearColorArray, 0, 1, 0, 0, 1, 1 );
		this.clearColor = clearColorArray[0];
		
		// transform RGB-Data
		for (int i=0; i<this.argbBuffer.length;i++){
			//	 perform Transparency
			if  (this.argbBuffer[i] == this.clearColor){
				this.argbBuffer[i] = 0x00000000;
			}
		}
		
		// perform a gaussain convolution, with a 5x5 matrix
		DrawUtil.applyFilter(
				DrawUtil.FILTER_GAUSSIAN_3
				,150,this.argbBuffer,newWidth,newHeight);
		
		// draw RGB-Data
		int startX = getLeftX( x, orientation, fWidth );
		int startY = getTopY( y, orientation, fHeight, font.getBaselinePosition() );
		DrawUtil.drawRgb(this.argbBuffer, startX-radius, startY-radius, newWidth, newHeight, true,  g );
//		// offset of an invisble area caused by negative (x,y)
//		int invX=Math.max(0, -(startX-radius));
//		int invY=Math.max(0, -(startY-radius));
//		if (newHeight-invY<=0 || newWidth-invX<=0){
//			// bugfix: exit if there is no part of text visible
//			return;
//		}
//		
//		g.drawRGB(this.argbBuffer, invY * ( newWidth)+invX ,newWidth, ( startX-radius+invX<=0 ? 0 :startX-radius+invX), ( startY-radius+invY<=0 ? 0 :startY-radius+invY) , newWidth-invX, newHeight-invY, true);
		
		g.setColor( textColor );
		g.drawString(text,startX,startY, Graphics.LEFT | Graphics.TOP);
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		
		if (this.lastText == null) {
			return animated;
		}
		
		// compute the next outer color
		return this.outerFader.step() | animated;
		
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		boolean hasChanged = false;
		
		 //#if polish.css.text-fading-alien-glow-inner-color
			Color innerColorObj = style.getColorProperty( "text-fading-alien-glow-inner-color" );
			if (innerColorObj != null) {
				this.innerColor = innerColorObj.getColor();
				hasChanged = true;
			}
		//#endif
		//#if polish.css.text-fading-alien-glow-outer-out-color
			Color outerOutColorObj = style.getColorProperty( "text-fading-alien-glow-outer-out-color" );
			if (outerOutColorObj != null) {
				this.outerFader.startColor = outerOutColorObj.getColor();
				hasChanged = true;
			}
		//#endif
		//#if polish.css.text-fading-alien-glow-outer-in-color
			Color outerInColorObj = style.getColorProperty( "text-fading-alien-glow-outer-in-color" );
			if (outerInColorObj != null) {
				this.outerFader.endColor = outerInColorObj.getColor();
				hasChanged = true;
			}
		//#endif
			
		//#if polish.css.text-fading-alien-glow-delay
			Integer delayInt = style.getIntProperty("text-fading-alien-glow-delay");
			if (delayInt != null ) {
				this.outerFader.delay = delayInt.intValue();
				hasChanged = true;
			}
		//#endif
		//#if polish.css.text-fading-alien-glow-steps
			Integer fadeTimeInt = style.getIntProperty("text-fading-alien-glow-steps");
			if (fadeTimeInt != null ) {
				this.outerFader.stepsIn = fadeTimeInt.intValue();
				this.outerFader.stepsOut=this.outerFader.stepsIn;
				hasChanged = true;
			}
		//#endif
		//#if polish.css.text-fading-alien-glow-duration-in
			Integer diInt = style.getIntProperty("text-fading-alien-glow-duration-in");
			if (diInt != null ) {
				this.outerFader.sWaitTimeIn = diInt.intValue();
				hasChanged = true;
			}
		//#endif
		//#if polish.css.text-fading-alien-glow-duration-out
			Integer doInt = style.getIntProperty("text-fading-alien-glow-duration-out");
			if (doInt != null ) {
				this.outerFader.sWaitTimeOut = doInt.intValue();
				hasChanged = true;
			}
		//#endif
		//#if polish.css.text-fading-alien-glow-mode
			Integer glowModeInt = style.getIntProperty("text-fading-alien-glow-mode");
			if (glowModeInt!=null){
				this.outerFader.mode = glowModeInt.intValue();
				hasChanged = true;
			}
		//#endif	
		if (resetStyle || hasChanged ) {
			this.lastText = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#releaseResources()
	 */
	public void releaseResources() {
		super.releaseResources();
		this.lastText = null;
		this.argbBuffer = null;
	}
	
}
