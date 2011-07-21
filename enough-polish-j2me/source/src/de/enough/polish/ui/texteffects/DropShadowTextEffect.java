//#condition polish.usePolishGui && polish.midp2 && polish.cldc1.1
/*
 * Created on 10.07.2006 at 12:22:13.
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

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.DrawUtil;

/**
 * <p>Paints a dropshadow behind a text, whereas you are able to specify
 *  the shadows inner and outer color.</p>
 * <p>Activate the shadow text effect by specifying <code>text-effect: drop-shadow;</code> in your polish.css file.
 *    You can finetune the effect with following attributes:
 * </p>
 * <ul>
 * 	 <li><b>text-drop-shadow-inner-color</b>: the inner color of the shadow, which should be less opaque than the text. </li>
 * 	 <li><b>text-drop-shadow-outer-color</b>: the outer color of the shadow, which should be less than opaque the inner color. </li>
 * 	 <li><b>text-drop-shadow-offsetx:</b>: use this for finetuning the shadow's horizontal position. Negative values move the shadow to the left.</li>
 * 	 <li><b>text-drop-shadow-offsety:</b>: use this for finetuning the shadow's vertical position. Negative values move the shadow to the top.</li>
 *   <li><b>text-drop-shadow-size:</b>: use this for finetuning the shadows radius.</li>
 * </ul>
 * <p>Choosing the same inner and outer color and varying the transparency is recommended. Dropshadow just works, if the Text is opaque.</p>
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        11-Jul-2006
 * </pre>
 * @author Simon Schmitt
 * 
 */
public class DropShadowTextEffect extends TextEffect {
			
	private String lastText;
	private int lastTextColor;
	int[] localRgbBuffer;
	
	private int innerColor = 0xA0909090;
	private int outerColor = 0x20909090;
	private int size=6;
	private int xOffset=1, yOffset=2;
	
	
	
	/**
	 * Creates a new drop shadow effect 
	 */
	public DropShadowTextEffect()
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
		// calculate imagesize
		Font font = g.getFont();
		int fHeight = font.getHeight();
		int fWidth = font.stringWidth( text );
		int newWidth=fWidth + this.size*2;
		int newHeight=fHeight+ this.size*2;
		
		// additional Margin for the image because of the shadow
		int iLeft = this.size-this.xOffset<0 ? 0 : this.size-this.xOffset;
		int iTop = this.size-this.yOffset<0 ? 0 : this.size-this.yOffset;
		
		
		// check whether the string has to be rerendered
		if (this.lastText!=text || this.lastTextColor != textColor) {
			this.lastText=text;
			this.lastTextColor=textColor;
			
			this.localRgbBuffer = getRgbData(text, textColor, font, iLeft, iTop, newWidth, newHeight );
			
			DrawUtil.dropShadow(this.localRgbBuffer,newWidth,newHeight,this.xOffset, this.yOffset, this.size,this.innerColor, this.outerColor);
			
		}
		
		int startX = getLeftX( x, orientation, fWidth );
		int startY = getTopY( y, orientation, fHeight, font.getBaselinePosition() );
		DrawUtil.drawRgb(this.localRgbBuffer, startX-iLeft, startY-iTop, newWidth, newHeight, true, g);
//		// offset of an invisble area caused by negative (x,y)
//		int invX=Math.max(0, -(startX-iLeft));
//		int invY=Math.max(0, -(startY-iTop));
//		// draw RGB-Data
//		if (newHeight-invY<=0 || newWidth-invX<=0){
//			// bugfix: exit if there is no part of text visible
//			return;
//		}
//		g.drawRGB(this.localRgbBuffer,invY*(newWidth)+invX,newWidth, ( startX-iLeft+invX<=0 ? 0 :startX-iLeft+invX), ( startY-iTop+invY<=0 ? 0 :startY-iTop+invY) , newWidth-invX, newHeight-invY, true);
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		boolean hasChanged = false;
		//#if polish.css.text-drop-shadow-inner-color
			Color sShadowColorObj = style.getColorProperty( "text-drop-shadow-inner-color" );
			if (sShadowColorObj != null) {
				this.innerColor = sShadowColorObj.getColor();
				hasChanged = true;
			}
		//#endif
		//#if polish.css.text-drop-shadow-outer-color
			Color eShadowColorObj = style.getColorProperty( "text-drop-shadow-outer-color" );
			if (eShadowColorObj != null) {
				this.outerColor = eShadowColorObj.getColor();
				hasChanged = true;
			}
		//#endif

		//#if polish.css.text-drop-shadow-size
			Integer sizeInt = style.getIntProperty( "text-drop-shadow-size" );
			if (sizeInt != null) {
				this.size = sizeInt.intValue();
				hasChanged = true;
			}
		//#endif
		//#if polish.css.text-drop-shadow-offsetx
			Integer oXInt = style.getIntProperty( "text-drop-shadow-offsetx" );
			if (oXInt != null) {
				this.xOffset = oXInt.intValue();
				hasChanged = true;
			}
		//#endif
		//#if polish.css.text-drop-shadow-offsety
			Integer oYInt = style.getIntProperty( "text-drop-shadow-offsety" );
			if (oYInt != null) {
				this.yOffset = oYInt.intValue();
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
		this.localRgbBuffer = null;
	}
	

}
