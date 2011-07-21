//#condition polish.usePolishGui
/*
 * Created on 16-Nov-2005 at 22:46:18.
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

/**
 * <p>Colorizes each character and its neighbouring characters.</p>
 * <p>Activate the shadow text effect by specifying <code>text-effect: lighthouse;</code> in your polish.css file.
 *    You can finetune the effect with following attributes:
 * </p>
 * <ul>
 * 	 <li><b>text-lighthouse-mode</b>: Sets the mode of the lighthouse font-effect, default is &quot;back-and-forth&quot;, other possible modes are left-to-right, right-to-left, back-and-forth-once, left-to-right-once and right-to-left-once.</li>
 * 	 <li><b>text-lighthouse-color</b>: Sets the main color of the lighthouse font-effect.</li>
 * 	 <li><b>text-lighthouse-neighbor-color</b>: Sets the color for characters next to the currently main character in the lighthouse font-effect.</li>
 * </ul>
 * 
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        16-Nov-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LighthouseTextEffect extends TextEffect {
	
	//private static final int MODE_BACK_AND_FORTH = 0; // default mode
	private static final int MODE_LEFT_TO_RIGHT = 1;
	private static final int MODE_RIGHT_TO_LEFT = 2;
	private static final int MODE_BACK_AND_FORTH_ONCE = 3;
	private static final int MODE_LEFT_TO_RIGHT_ONCE = 4;
	private static final int MODE_RIGHT_TO_LEFT_ONCE = 5;
	
	private static final int DIRECTION_RIGHT = 0;
	private static final int DIRECTION_LEFT = 1;
	
	private String lastText;
	private boolean animationRunning = true;
	private int currentPos;
	private int direction;
	
	private int mainColor = 0x44b500;
	private int neighborColor = 0x379100;
	private int mode;

	/**
	 * Creates a new type writer effect. 
	 */
	public LighthouseTextEffect() {
		super();
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		String text = this.lastText;
		if (!this.animationRunning || text == null) {
			return animated; 
		}
		int pos = this.currentPos;
		if (this.direction == DIRECTION_RIGHT ) {
			pos++;
			if (pos >= text.length()) {
				if (this.mode == MODE_LEFT_TO_RIGHT_ONCE) {
					this.animationRunning = false;
				} else if (this.mode == MODE_LEFT_TO_RIGHT) {
					pos = -1;
				} else {
					this.direction = DIRECTION_LEFT;
				}
			}
		} else {
			pos--;
			if (pos < 0) {
				if (this.mode == MODE_BACK_AND_FORTH_ONCE || this.mode == MODE_RIGHT_TO_LEFT_ONCE) {
					this.animationRunning = false;
				} else if (this.mode == MODE_RIGHT_TO_LEFT) {
					pos = text.length();
				} else {
					this.direction = DIRECTION_RIGHT;
				}
			}
		}
		this.currentPos = pos;
		return true;
	}

	


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#hideNotify()
	 */
	public void hideNotify() {
		this.lastText = null;
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g) 
	{
		int length = text.length();
		if ( text != this.lastText ) {
			this.lastText = text;
			this.animationRunning = true;
			if (this.mode == MODE_RIGHT_TO_LEFT || this.mode == MODE_RIGHT_TO_LEFT_ONCE) {
				this.currentPos = length;
			} else {
				this.direction = DIRECTION_RIGHT;
				this.currentPos = -1;
			}
		}
		if (!this.animationRunning) {
			g.drawString(text, x, y, orientation);
		} else {
			int pos = this.currentPos;
			if (pos < -1) {
				pos = -1;
			}
			Font font = g.getFont();
			x = getLeftX(x, orientation, font.stringWidth( text ) );
			y = getTopY(y, orientation, font.getHeight(), font.getBaselinePosition() );
			int neighbor = pos - 1;
			if (neighbor >= 0) {
				if (neighbor != 0) {
					g.drawSubstring( text, 0, neighbor, x, y,  Graphics.TOP | Graphics.LEFT );
					x += font.substringWidth(text, 0, neighbor);
				}
				g.setColor( this.neighborColor );
				char c = text.charAt( neighbor );
				g.drawChar( c, x,  y, Graphics.TOP | Graphics.LEFT );
				x += font.charWidth( c );
			}
			if ( pos >= 0 && pos < length ) {
				g.setColor( this.mainColor );
				char c = text.charAt(pos);
				g.drawChar( c, x,  y, Graphics.TOP | Graphics.LEFT );
				x += font.charWidth( c );
			}
			neighbor = pos + 1;
			if (neighbor < length ) {
				g.setColor( this.neighborColor );
				char c = text.charAt(neighbor);
				g.drawChar( c, x,  y, Graphics.TOP | Graphics.LEFT );
				neighbor++;
				if ( neighbor < length ) {
					x += font.charWidth( c );
					g.setColor( textColor );
					g.drawSubstring(text, neighbor, length - neighbor, x, y, Graphics.TOP | Graphics.LEFT ); 
				}
			}
			
		}
	}



	//#if polish.css.text-lighthouse-mode || polish.css.text-lighthouse-color || polish.css.text-lighthouse-neighbor-color
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		this.animationRunning = true;
		//#if polish.css.text-lighthouse-mode
			Integer modeInt = style.getIntProperty("text-lighthouse-mode");
			if (modeInt != null) {
				int localMode = modeInt.intValue();
				if ( localMode == MODE_RIGHT_TO_LEFT || localMode == MODE_RIGHT_TO_LEFT_ONCE ) {
					this.direction = DIRECTION_LEFT;
				} else {
					this.direction = DIRECTION_RIGHT;
				}
				this.mode = localMode;
			}
		//#endif
		//#if polish.css.text-lighthouse-color
			Integer colorInt = style.getIntProperty("text-lighthouse-color");
			if (colorInt != null) {
				this.mainColor = colorInt.intValue();
			}
		//#endif
		//#if polish.css.text-lighthouse-neighbor-color
			Integer neighborColorInt = style.getIntProperty("text-lighthouse-neighbor-color");
			if (neighborColorInt != null) {
				this.neighborColor = neighborColorInt.intValue();
			}
		//#endif
	}
	//#endif

}
