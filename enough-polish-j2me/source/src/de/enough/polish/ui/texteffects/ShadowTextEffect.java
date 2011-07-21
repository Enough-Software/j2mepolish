//#condition polish.usePolishGui
/*
 * Created on 16-Nov-2005 at 12:22:20.
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

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;

/**
 * <p>Paints a shadow behind a text.</p>
 * <p>Activate the shadow text effect by specifying <code>text-effect: shadow;</code> in your polish.css file.
 *    You can finetune the effect with following attributes:
 * </p>
 * <ul>
 * 	 <li><b>text-shadow-color</b>: the color of the shadow, defaults to black.</li>
 * 	 <li><b>text-shadow-orientation</b>: the orientation of the shadow, either bottom-right, bottom-left, top-right, top-left, bottom, top, right or left. Defaults to bottom-right.</li>
 * 	 <li><b>text-shadow-x</b>: use this for finetuning the shadow's horizontal position. Negative values move the shadow to the left.</li>
 * 	 <li><b>text-shadow-y</b>: use this for finetuning the shadow's vertical position. Negative values move the shadow to the top.</li>
 * </ul>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        16-Nov-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ShadowTextEffect extends TextEffect {
	
	public static final int ORIENTATION_BOTTOM_RIGHT = 0;
	public static final int ORIENTATION_BOTTOM_LEFT = 1;
	public static final int ORIENTATION_TOP_RIGHT = 2;
	public static final int ORIENTATION_TOP_LEFT = 3;
	public static final int ORIENTATION_BOTTOM = 4;
	public static final int ORIENTATION_TOP = 5;
	public static final int ORIENTATION_RIGHT = 6;
	public static final int ORIENTATION_LEFT = 7;
	
	public int shadowColor;
	public int xOffset = 1;
	public int yOffset = 1;

	/**
	 * Creates a shadow effect.
	 */
	public ShadowTextEffect() {
		super();
	}
	
	/**
	 * Creates a shadow effect.
	 * @param shadowColor the color 
	 * @param xOffset the xOffset
	 * @param yOffset the yOffset
	 */
	public ShadowTextEffect(int shadowColor, int xOffset, int yOffset) {
		super();
		this.shadowColor = shadowColor;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	
	//#if polish.css.text-shadow-color || polish.css.text-shadow-x || polish.css.text-shadow-y || polish.css.text-shadow-orientation
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		//#ifdef polish.css.text-shadow-color
			Integer colorInt = style.getIntProperty("text-shadow-color");
			if (colorInt != null) {
				this.shadowColor = colorInt.intValue();
			}
		//#endif
		//#ifdef polish.css.text-shadow-orientation
			Integer orientationInt = style.getIntProperty("text-shadow-orientation");
			if (orientationInt != null) {
				switch ( orientationInt.intValue() ) {
				case ORIENTATION_BOTTOM_RIGHT: 
					this.xOffset = 1;
					this.yOffset = 1;
					break;
				case ORIENTATION_BOTTOM_LEFT: 
					this.xOffset = -1;
					this.yOffset = 1;
					break;
				case ORIENTATION_TOP_RIGHT: 
					this.xOffset = 1;
					this.yOffset = -1;
					break;
				case ORIENTATION_TOP_LEFT: 
					this.xOffset = -1;
					this.yOffset = -1;
					break;
				case ORIENTATION_BOTTOM: 
					this.xOffset = 0;
					this.yOffset = 1;
					break;
				case ORIENTATION_TOP: 
					this.xOffset = 0;
					this.yOffset = -1;
					break;
				case ORIENTATION_RIGHT: 
					this.xOffset = 1;
					this.yOffset = 0;
					break;
				case ORIENTATION_LEFT: 
					this.xOffset = -1;
					this.yOffset = 0;
				}
			}
		//#endif
		//#ifdef polish.css.text-shadow-x
			Integer xInt = style.getIntProperty("text-shadow-x");
			if (xInt != null) {
				this.xOffset = xInt.intValue();
			}
		//#endif
		//#ifdef polish.css.text-shadow-y
			Integer yInt = style.getIntProperty("text-shadow-y");
			if (yInt != null) {
				this.yOffset = yInt.intValue();
			}
		//#endif
		//#debug
		System.out.println("ShadowTextEffect.setStyle(): color=" + Integer.toHexString(this.shadowColor) + ", x=" + this.xOffset + ", y=" + this.yOffset );
	}
	//#endif



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y, int orientation,
			Graphics g) 
	{
		g.setColor( this.shadowColor );
		g.drawString(text, x + this.xOffset, y + this.yOffset, orientation);
		g.setColor( textColor );
		g.drawString(text, x, y, orientation);
	}

}
