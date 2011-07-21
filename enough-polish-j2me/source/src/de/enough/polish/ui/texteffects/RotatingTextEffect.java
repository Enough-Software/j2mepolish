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
 * <p>Constantly rotates the focused text. Please note that this MIDP 2.0 only effect assumes that only one line is rendered - rendering several lines decreases performance drastically.</p>
 * <p>Activate this text effect by specifying <code>text-effect: ratating;</code> in your polish.css file.
 *    You can finetune the effect with following attributes:
 * </p>
 * <ul>
 * 	 <li><b>text-rotating-start-angle</b>: The angle at which the text should be rotated at the beginning of the animation, defaults to 0 degrees.</li>
 * 	 <li><b>text-rotating-end-angle</b>: The angle at which the text should be rotated at the end of the animation, defaults to 360 degrees.</li>
 * 	 <li><b>text-rotating-steps</b>: The number of degrees by which the rotation should be increased in each animation step, defaults to 2 degrees.</li>
 * </ul>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Nov 29, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RotatingTextEffect extends TextEffect {
	
	private String lastText;
	private int angle;
	private transient RgbImage rotatedImage;
	private int xPos;
	private int yPos;
	private int startAngle = 0;
	private int endAngle = 360;
	private int steps = 4;
	private int[] originalRgbData;
	private int rotatedWidthHeight;
	private int originalHeight;
	private int originalWidth;
	
	/**
	 * Creates a new rotating effect
	 */
	public RotatingTextEffect()
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
		synchronized( this ) {
			if ( text != this.lastText ) {
				Font font = g.getFont();
				rotate( text, textColor,font, this.startAngle );
				this.lastText = text;
				this.xPos = getLeftX(x, orientation, this.rotatedImage.getWidth());
				this.yPos = getTopY(y, orientation, this.rotatedImage.getHeight(), (font.getBaselinePosition() * this.rotatedImage.getHeight()) / font.getHeight() );
			}
			
			this.rotatedImage.paint(this.xPos, this.yPos, g);
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#animate()
	 */
	public boolean animate() {
		if (this.originalRgbData == null) {
			return false;
		}
		synchronized( this ) {
			// clear this image:
			this.angle += this.steps;
			if (this.angle > this.endAngle ) {
				this.angle = this.startAngle;
			}
			rotate();
			return true;
		}
	}



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
	 * @param currentAngle the angle in degrees between 0 and 360
	 */
	private void rotate(String text, int textColor, Font font, int currentAngle) {
		this.angle = currentAngle;
		this.originalRgbData = getRgbData(text, textColor, font);
		int[] rgbData = this.originalRgbData;		
		this.originalHeight = font.getHeight();
		this.originalWidth = rgbData.length / this.originalHeight;
		this.rotatedWidthHeight = (int) Math.sqrt( this.originalHeight*this.originalHeight + this.originalWidth*this.originalWidth );
		this.rotatedImage = new RgbImage( this.rotatedWidthHeight, this.rotatedWidthHeight, 0, true );
		
		rotate();
	}

	private void rotate() {
		int referenceX = this.originalWidth / 2;
		int referenceY = this.originalHeight / 2;
		int backgroundColor = 0;
		double degreeCos = Math.cos(Math.PI*this.angle/180);
		double degreeSin = Math.sin(Math.PI*this.angle/180);		
		ImageUtil.rotate(this.originalRgbData, this.originalWidth, this.originalHeight, referenceX, referenceY, backgroundColor, 
				degreeCos, degreeSin, this.rotatedImage.getRgbData(), this.rotatedImage.getWidth(), this.rotatedImage.getHeight() );
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#releaseResources()
	 */
	public void releaseResources() {
		super.releaseResources();
		this.lastText = null;
		this.rotatedImage = null;
		this.originalRgbData = null;
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
		this.lastText = null;
		//#if polish.css.text-rotating-start-angle
			Integer startAngleInt = style.getIntProperty("text-rotating-start-angle");
			if (startAngleInt != null) {
				this.startAngle = startAngleInt.intValue();
				this.angle = this.startAngle;
			}
		//#endif
		//#if polish.css.text-rotating-end-angle
			Integer endAngleInt = style.getIntProperty("text-rotating-end-angle");
			if (endAngleInt != null) {
				this.endAngle = endAngleInt.intValue();
			}
		//#endif
	
		//#if polish.css.text-rotating-steps
			Integer stepsInt = style.getIntProperty("text-rotating-steps");
			if (stepsInt != null) {
				this.steps = stepsInt.intValue();
			}
		//#endif

	}

}
