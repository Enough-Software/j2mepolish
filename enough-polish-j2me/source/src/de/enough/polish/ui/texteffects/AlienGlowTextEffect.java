//#condition polish.usePolishGui && polish.midp2 && polish.hasFloatingPoint
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
 *  the inner and outer color as well as the font-color.</p>
 * <p>Activate the alien glow text effect by specifying <code>text-effect: alien-glow;</code> in your polish.css file.
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
public class AlienGlowTextEffect extends TextEffect {

	private final static int CLEAR_COLOR = 0xFF000123;
	private int clearColor;

	private String lastText;
	private int lastTextColor;
	int[] argbBuffer;

	private int innerColor = 0xFFFFFFFF;
	private int outerColor = 0xFF00FF00;
	
	
	
	/**
	 * Creates a new alien glow effect
	 */
	public AlienGlowTextEffect()
	{
		super();
		this.isTextSensitive = true;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g) 
	{
		
		final int radius=3+1;

		//calculate imagesize
		Font font = g.getFont();
		int fHeight = font.getHeight();
		int fWidth = font.stringWidth( text );
		int newWidth=fWidth + radius*2;
		int newHeight=fHeight+ radius*2;
		
		// check whether the string has to be rerendered
		if (this.lastText != text || this.lastTextColor != textColor) {
			this.lastText=text;
			this.lastTextColor=textColor;
			
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

		}
		
		// get draw position:
		int startX = getLeftX( x, orientation, fWidth );
		int startY = getTopY( y, orientation, fHeight, font.getBaselinePosition() );
		
		DrawUtil.drawRgb(this.argbBuffer, startX-radius, startY-radius, newWidth, newHeight, true,  g );
		g.setColor( textColor );
		g.drawString(text,startX,startY, Graphics.LEFT | Graphics.TOP);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		boolean hasChanged = false;
		//#if polish.css.text-alien-glow-inner-color
			Color sShadowColorObj = style.getColorProperty( "text-alien-glow-inner-color" );
			if (sShadowColorObj != null) {
				this.innerColor = sShadowColorObj.getColor();
				hasChanged  = true;
			}
		//#endif
		//#if polish.css.text-alien-glow-outer-color
			Color eShadowColorObj = style.getColorProperty( "text-alien-glow-outer-color" );
			if (eShadowColorObj != null) {
				this.outerColor = eShadowColorObj.getColor();
				hasChanged = true;
			}
		//#endif
		if (resetStyle || hasChanged) {
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
