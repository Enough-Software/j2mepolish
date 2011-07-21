//#condition polish.usePolishGui
/*
 * Created on May 20, 2006 at 7:45:30 PM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ui.texteffects;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

//#if polish.api.nokia-ui
	import com.nokia.mid.ui.DirectGraphics;
	import com.nokia.mid.ui.DirectUtils;
//#endif


import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.DrawUtil;

/**
 * Paints a gradient font where the color changes from top to bottom.
 * <p>Activate the shadow text effect by specifying <code>text-effect: vertical-gradient;</code> in your polish.css file.
 *    You can finetune the effect with following attributes:
 * </p>
 * <ul>
 * 	 <li><b>text-vertical-gradient-start-color</b>: the top color of the gradient, defaults to white.</li>
 * 	 <li><b>text-vertical-gradient-end-color</b>: the bottom color of the gradient, defaults to black.</li>
 * 	 <li><b>text-vertical-gradient-steps</b>: the number of steps of the gradient after which the gradient is repeated, defaults to the font-height.</li>
 * </ul>
 * @author robertvirkus
 *
 */
public class VerticalGradientTextEffect extends TextEffect {
	
	private int[] colors;
	private String lastText;
	//#if polish.api.nokia-ui && !polish.Bugs.TransparencyNotWorkingInNokiaUiApi
		//#define tmp.useNokiaUiApi
		private Image nokiaImageBuffer;
	//#elif polish.midp2
		//#if polish.GradientText.ClearColor:defined
			//#= private final static int CLEAR_COLOR = ${polish.GradientText.ClearColor};
		//#else
			private final static int CLEAR_COLOR = 0xFF000123;
		//#endif
		private int[] rgbBuffer;
	//#endif
	private boolean useTransparency;
	
	/**
	 * Creates a new gradient text effect.
	 */
	public VerticalGradientTextEffect() {
		this.isTextSensitive = true;
	}

	/**
	 * Creates a new gradient text effect.
	 * 
	 * @param startColor the top color
	 * @param endColor the bottom color
	 * @param steps the number of steps after which the gradient is repeated
	 */
	public VerticalGradientTextEffect(int startColor, int endColor, int steps) {
		this.colors = DrawUtil.getGradient(startColor, endColor, steps);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g) 
	{
		if (this.colors == null) {
			g.drawString( text, x, y, orientation );
			return;
		}
		Font font = g.getFont();
		int height = font.getHeight();
		int width = font.stringWidth( text );
		int startX = getLeftX( x, orientation, width );
		int startY = getTopY( y, orientation, height, font.getBaselinePosition() );
		//#if tmp.useNokiaUiApi
			if ( this.nokiaImageBuffer == null || text != this.lastText  ){
		//#elif polish.midp2
			if ( this.rgbBuffer == null || text != this.lastText || this.rgbBuffer.length != width * height ){
		//#endif
			// create image buffer
			//#if tmp.useNokiaUiApi || polish.midp2
				Graphics bufferG;
			//#else
				int clipX = g.getClipX();
				int clipY = g.getClipY();
				int clipWidth = g.getClipWidth();
				int clipHeight = g.getClipHeight();
			//#endif
			//#if tmp.useNokiaUiApi
				this.nokiaImageBuffer = DirectUtils.createImage( width, height, 0x00000000 );
				bufferG = this.nokiaImageBuffer.getGraphics();
				DirectGraphics dg = DirectUtils.getDirectGraphics(g);
			//#elif polish.midp2
				Image midp2ImageBuffer = Image.createImage( width, height );
				bufferG = midp2ImageBuffer.getGraphics();
				bufferG.setColor( CLEAR_COLOR );
				bufferG.fillRect( 0, 0, width + 1, height + 1 );
				int[] clearColorArray = new int[1]; 
				midp2ImageBuffer.getRGB(clearColorArray, 0, 1, 0, 0, 1, 1 );
				int clearColor = clearColorArray[0];
			//#endif
			//#if tmp.useNokiaUiApi || polish.midp2
				bufferG.setFont(font);
			//#endif	
			int j = 0;
			boolean increase = true;
			int maxJ = this.colors.length - 1;
			for (int i = 0; i < height; i++) {
				int color = this.colors[j];
				//#if tmp.useNokiaUiApi || polish.midp2
					bufferG.setClip( 0, i, width, 1 );
					//#if tmp.useNokiaUiApi
						if (this.useTransparency) {
							dg.setARGBColor(color);
						} else {
							bufferG.setColor( color );
						}
					//#else
						bufferG.setColor( color );
					//#endif
					bufferG.drawString( text, 0, 0, Graphics.TOP | Graphics.LEFT );
				//#else
					g.setColor( color );
					g.setClip( startX, startY + i, width, 1 );
					g.drawString( text, startX, startY, Graphics.TOP | Graphics.LEFT );
				//#endif
				if (increase) {	
					j++;
					if (j >= maxJ) {
						increase = false;
					}
				} else {
					j--;
					if ( j <= 0 ) {
						increase = true;
					}
				}
			}
			//#if tmp.useNokiaUiApi
				//DirectGraphics dg = DirectUtils.getDirectGraphics( g );
				dg.drawImage( this.nokiaImageBuffer, startX, startY, Graphics.TOP | Graphics.LEFT, 0 );
			//#elif polish.midp2
				// clear RGB array:
				int[] localRgbBuffer = new int[ width * height ];
				midp2ImageBuffer.getRGB( localRgbBuffer, 0, width, 0, 0, width, height );
				//int number = 0;
				for (int i = 0; i < localRgbBuffer.length; i++) {
					int color = localRgbBuffer[i];
					if ( color == clearColor ) {
						//number++;
						localRgbBuffer[i] = 0x00000000; // full transparent
					}
				}
				//System.out.println("Set " + number + " pixels to transparent");
				this.rgbBuffer = localRgbBuffer;
				//g.drawRGB( localRgbBuffer, 0, width, startX, startY, width, height, true );
				DrawUtil.drawRgb(localRgbBuffer, startX, startY, width, height, true, g );
			//#else
				g.setClip( clipX, clipY, clipWidth, clipHeight );
			//#endif
			this.lastText = text;	
		//#if tmp.useNokiaUiApi || polish.midp2
			} else {
				// text has been buffered:
				//#if tmp.useNokiaUiApi
					DirectGraphics dg = DirectUtils.getDirectGraphics( g );
					dg.drawImage( this.nokiaImageBuffer, startX, startY, Graphics.TOP | Graphics.LEFT, 0 );
				//#elif polish.midp2
					//g.drawRGB( this.rgbBuffer, 0, width, startX, startY, width, height, true );
					DrawUtil.drawRgb(this.rgbBuffer, startX, startY, width, height, true, g );
				//#endif
			}
		//#endif
		//#if false
			}
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		boolean styleDefined = false; 
		int startColor = 0xFFFFFFFF;
		//#if polish.css.text-vertical-gradient-start-color
			Integer startColorInt = style.getIntProperty("text-vertical-gradient-start-color");
			if (startColorInt != null) {
				startColor = startColorInt.intValue();
				styleDefined = true;
			}
		//#endif
		int endColor = 0xFF000000;
		//#if polish.css.text-vertical-gradient-end-color
			Integer endColorInt = style.getIntProperty("text-vertical-gradient-end-color");
			if (endColorInt != null) {
				endColor = endColorInt.intValue();
				styleDefined = true;
			}
		//#endif
		int steps;
		Font font = style.getFont();
		if (font == null) {
			font = Font.getDefaultFont();
		}
		//#if polish.css.text-vertical-gradient-end-steps
			Integer stepsInt = style.getIntProperty("text-vertical-gradient-steps");
			if (stepsInt != null) {
				steps = stepsInt.intValue();
				if (steps <= 0) {
					steps = font.getHeight();
				}
				styleDefined = true;
			} else {
				steps = font.getHeight();
			}
		//#else
			steps = font.getHeight();
		//#endif
		if (styleDefined || this.colors == null) {
			this.colors = DrawUtil.getGradient(startColor, endColor, steps);
		}
		if (styleDefined || resetStyle) {
			this.useTransparency = ((startColor & 0xFF000000) != 0) || ((endColor & 0xFF000000) != 0);
			this.lastText = null;
		}
	}
	
	

}
