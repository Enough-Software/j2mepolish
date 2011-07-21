//#condition polish.usePolishGui && polish.midp2 && polish.hasFloatingPoint
/*
 * Created on Nov 29, 2006 at 9:33:31 PM.
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


import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.ImageUtil;
import de.enough.polish.util.RgbImage;

/**
 * <p>Rotates the given text. Please note that this MIDP 2.0 only effect assumes that only one line is rendered - rendering several lines decreases performance drastically.</p>
 * <p>Activate this text effect by specifying <code>text-effect: rotate;</code> in your polish.css file.
 *    You can finetune the effect with following attributes:
 * </p>
 * <ul>
 * 	 <li><b>text-rotate-angle</b>: The angle by which the text should be rotated.</li>
 * </ul>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Nov 29, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RotateTextEffect extends TextEffect {
	
	private String lastText;
	private int angle = 10;
	private transient RgbImage rotatedImage;
	private int xPos;
	private int yPos;

	
	/**
	 * Creates a new rotate effect
	 */
	public RotateTextEffect()
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
		if ( text != this.lastText ) {
			Font font = g.getFont();
			this.rotatedImage = rotate( text, textColor,font, this.angle );
			this.lastText = text;
			this.xPos = getLeftX(x, orientation, this.rotatedImage.getWidth());
			this.yPos = getTopY(y, orientation, this.rotatedImage.getHeight(), (font.getBaselinePosition() * this.rotatedImage.getHeight()) / font.getHeight() );
		}
		
		this.rotatedImage.paint(this.xPos, this.yPos, g);
	}
	
	//#if polish.hasFloatingPoint
	/**
	 * Rotates the given text.
	 * Note that this method is only available on MIDP 2.0 devices with floating point support (either using Floater or by supporting CLDC 1.1)
	 * You can use this preprocessing directive:
	 * <pre>
	 * //#if polish.midp2 && polish.hasFloatingPoint
	 * </pre>
	 * 
	 * @param text the text
	 * @param textColor the color of the text
	 * @param font the font
	 * @param angle the angle in degrees between 0 and 360
	 * @return the rotated RgbImage
	 */
	public static RgbImage rotate(String text, int textColor, Font font, int angle) {
		int[] rgbData = getRgbData(text, textColor, font);		
		int height = font.getHeight();
		int width = rgbData.length / height;
		int referenceX = width / 2;
		int referenceY = height / 2;
		RgbImage image = new RgbImage( rgbData, width, true  );

		ImageUtil.rotate( image, angle, referenceX, referenceY );
		return image;
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#releaseResources()
	 */
	public void releaseResources() {
		super.releaseResources();
		this.lastText = null;
		this.rotatedImage = null;
	}
	
	/**
	 * Sets the angle for this effect.
	 * 
	 * @param angle the angle between 0 and 360 degrees
	 */
	public void setAngle( int angle ) {
		releaseResources();
		this.angle = angle;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		//#if polish.css.text-rotate-angle
			Integer angleInt = style.getIntProperty("text-rotate-angle");
			if (angleInt != null) {
				setAngle( angleInt.intValue() );
			}
		//#endif
	}
	
	

}
