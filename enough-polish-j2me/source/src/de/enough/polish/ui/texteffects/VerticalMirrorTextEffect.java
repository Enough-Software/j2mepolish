//#condition polish.usePolishGui && polish.midp2
/*
 * Created on Jun 20, 2006 at 9:52:50 PM.
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

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.DrawUtil;

/**
 * <p>Adds a vertically mirrored text as a shadow to the text.</p>
 * <p>Activate the shadow text effect by specifying <code>text-effect: mirror-shadow;</code> in your polish.css file.
 *    You can finetune the effect with following attributes:
 * </p>
 * <ul>
 * 	 <li><b>text-mirror-color</b>: the color of the shadow, defaults to the specified font-color.</li>
 * 	 <li><b>text-mirror-padding</b>: the gap between the text and the mirror-shadow, defaults to 0.</li>
 * 	 <li><b>text-mirror-steps</b>: The number of pixels until the mirror-shadow fades away, defaults to the given font's height.</li>
 * 	 <li><b>text-mirror-start-translucency</b>: The opaqueness of the first row of the shadow in percent: between 0 (fully transparent) and 100 (fully opaque)</li>
 * 	 <li><b>text-mirror-end-translucency</b>: The opaqueness of the last row of the shadow in percent: between 0 (fully transparent) and 100 (fully opaque)</li>
 * </ul>
 * <p>Note that this effect is optimized for displaying a single line of text. When your text contains line-breaks, many temporary int arrays will be created. 
 *    Also note that this effect requires MIDP 2.0 support.
 * </p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Jun 20, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class VerticalMirrorTextEffect extends TextEffect {

	//#if polish.MirrorShadowText.ClearColor:defined
		//#= private final static int CLEAR_COLOR = ${polish.MirrorShadowText.ClearColor};
	//#else
		private final static int CLEAR_COLOR = 0xFF000123;
	//#endif

	private int shadowColor;
	private int padding;
	private int steps;
	private int startTranslucency;
	private int endTranslucency;
	
	private int[] rgbData;
	private int rgbWidth;
	private int rgbHeight;
	private transient Graphics bufferGraphics;
	private transient Image bufferImage;
	private int clearColor;
	private String lastText;

	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawStrings(java.lang.String[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
//	public void drawStrings(String[] textLines, int textColor, int x, int y, int leftBorder, int rightBorder, int lineHeight, int maxWidth, int layout, Graphics g) {
//		if (textLines == this.lastLines) {
//			g.drawRGB( this.rgbData, 0, this.rgbWidth, x, y, this.rgbWidth, this.rgbHeight, true );
//			return;
//		}
//		// calculate dimensions:
//		this.lastLines = textLines;
//		int height = (textLines.length  * lineHeight) + this.padding + this.steps;
//		// prepare RGB data buffer:
//		prepareRgbBuffer( maxWidth, height );
//		this.rgbGraphics.setColor( textColor );
//		this.rgbGraphics.setFont( g.getFont() );
//		// write each text line and add mirror shadow:
//		super.drawStrings(textLines, textColor, 0, 0, 0, maxWidth,
//				lineHeight, maxWidth, layout, this.rgbGraphics);
//		this.rgbData = new int[ maxWidth * height ];
//		this.bufferImage.getRGB( this.rgbData, 0, maxWidth, 0, 0, maxWidth, height );
//		removeClearColor();
//		g.drawRGB( this.rgbData, 0, this.rgbWidth, x, y, this.rgbWidth, this.rgbHeight, true );
//
//	}
	
//	private void removeClearColor( int[] data ) {
//		for (int i = 0; i < data.length; i++) {
//			int color = data[i];
//			if ( color == this.clearColor ) {
//				data[i] = 0x00000000; // full transparent
//			} else if ( (color & 0xFF000000) == 0 ){
//				color |= 0xFF000000;
//			}
//		}
//	}

	/**
	 * Creates a new mirror effect
	 */
	public VerticalMirrorTextEffect()
	{
		super();
		this.isTextSensitive = true;
	}

	private void prepareRgbBuffer( int width, int height ) {
		this.rgbWidth = width;
		this.rgbHeight = height;
		Image image = Image.createImage( width, height );
		this.bufferImage = image;
		Graphics bufferG = image.getGraphics();
		bufferG.setColor( CLEAR_COLOR );
		bufferG.fillRect( 0, 0, width + 1, height + 1 );
		this.bufferGraphics = bufferG;
		int[] clearColorArray = new int[1]; 
		image.getRGB(clearColorArray, 0, 1, 0, 0, 1, 1 );
		this.clearColor = clearColorArray[0];
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g) 
	{
		g.drawString( text, x, y, orientation);
		Font font = g.getFont();
		int fontHeight = font.getHeight();
		if ( text == this.lastText ) {
			if ( (orientation & Graphics.RIGHT) == Graphics.RIGHT ) {
				x -= this.rgbWidth;
			} else if ( (orientation & Graphics.HCENTER) == Graphics.HCENTER ) {
				x -= this.rgbWidth/2;
			}
			// g.drawRGB( this.rgbData, 0, this.rgbWidth, x, y + fontHeight + this.padding, this.rgbWidth, this.rgbHeight, true );
			DrawUtil.drawRgb( this.rgbData, x, y + fontHeight + this.padding, this.rgbWidth, this.rgbHeight, true, g );
			return;
		}
		
		int width = font.stringWidth( text );
		int height = this.steps;
		
		prepareRgbBuffer(width, height);
		
		
		this.bufferGraphics.setFont( font );
		this.bufferGraphics.setColor( this.shadowColor );
		for (int i=0; i < this.steps; i++ ) {
			this.bufferGraphics.setClip( 0, i, width, 1 );
			this.bufferGraphics.drawString( text, 0, -fontHeight + i*2 + 1, Graphics.LEFT | Graphics.TOP );
			//System.out.println( "clip-y=" + i + ", font-y=" + (-fontHeight + i*2 + 1) + ", fontHeight=" + fontHeight);
		}
		int[] translucencies = DrawUtil.getGradient( this.startTranslucency, this.endTranslucency, height);
		int[] data = new int[ width * height ];
		this.bufferImage.getRGB( data, 0, width, 0, 0, width, height );
		for (int row = 0; row < height; row++) {
			int translucency =  ( translucencies[row] << 24) | 0xFFFFFF;
			for (int column = 0; column < width; column++) {
				int index = row*width + column;
				int color = data[ index ];
				if (color == this.clearColor) {
					data[ index ] = 0x00000000; // fully transparent
				} else {
					data[ index ] = color & translucency;
				}
			}
		}
		if ( (orientation & Graphics.RIGHT) == Graphics.RIGHT ) {
			x -= width;
		} else if ( (orientation & Graphics.HCENTER) == Graphics.HCENTER ) {
			x -= width/2;
		}
		//g.drawRGB( data, 0, width, x, y + fontHeight + this.padding, width, height, true );
		DrawUtil.drawRgb( data, x, y + fontHeight + this.padding, width, height, true, g );
		
		this.rgbData = data;
		this.lastText = text;
		this.bufferImage = null;
		this.bufferGraphics = null;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		boolean styleChanged = false;
		if (resetStyle) {
			this.shadowColor = style.getFontColor();
			Font font = style.getFont();
			if (font == null) {
				font = Font.getDefaultFont();
			}	
			this.steps = font.getHeight();
		}
		//#if polish.css.text-mirror-color
			Color shadowColorObj = style.getColorProperty( "text-mirror-color" );
			if (shadowColorObj != null) {
				this.shadowColor = shadowColorObj.getColor();
				styleChanged = true;
			}
		//#endif
		//#if polish.css.text-mirror-padding
			Integer paddingInt = style.getIntProperty( "text-mirror-padding" );
			if (paddingInt != null) {
				this.padding = paddingInt.intValue();
				styleChanged = true;
			}
		//#endif
		//#if polish.css.text-mirror-steps
			Integer stepsInt = style.getIntProperty( "text-mirror-steps" );
			if (stepsInt != null) {
				this.steps = stepsInt.intValue();
				styleChanged = true;
			}
		//#endif
		int startTranslucencyPercent = 90;
		//#if polish.css.text-mirror-start-translucency
			Integer startTranslucencyInt = style.getIntProperty("text-mirror-start-translucency");
			if (startTranslucencyInt != null ) {
				startTranslucencyPercent = startTranslucencyInt.intValue();
				this.startTranslucency = (startTranslucencyPercent * 255) / 100;	
				styleChanged = true;
			}
		//#endif
		if (resetStyle) {
			this.startTranslucency = (startTranslucencyPercent * 255) / 100;
		}

		int endTranslucencyPercent = 0;
		//#if polish.css.text-mirror-end-translucency
			Integer endTranslucencyInt = style.getIntProperty("text-mirror-end-translucency");
			if (endTranslucencyInt != null ) {
				endTranslucencyPercent = endTranslucencyInt.intValue();
				this.endTranslucency = (endTranslucencyPercent * 255) / 100;
				styleChanged = true;
			}
		//#endif
		if (resetStyle) {
			this.endTranslucency = (endTranslucencyPercent * 255) / 100;
		}
		if (styleChanged || resetStyle) {
			this.lastText = null;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#releaseResources()
	 */
	public void releaseResources() {
		super.releaseResources();
		this.lastText = null;
		this.rgbData = null;
		this.bufferGraphics = null;
		this.bufferImage = null;
	}
	
	
	
	

}
