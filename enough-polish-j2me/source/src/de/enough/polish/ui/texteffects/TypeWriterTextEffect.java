//#condition polish.usePolishGui
/*
 * Created on 16-Nov-2005 at 18:16:18.
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

import de.enough.polish.ui.TextEffect;

/**
 * <p>Writes one character after the other.</p> 
 * <p>Activate the shadow text effect by specifying <code>text-effect: typewriter;</code> in your polish.css file.
 * </p>
 * 
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        16-Nov-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TypeWriterTextEffect extends TextEffect {
	
	private String lastText;
	private String shownText;
	private boolean animationRunning;
	private int currentPos;

	/**
	 * Creates a new type writer effect. 
	 */
	public TypeWriterTextEffect() {
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
		this.currentPos++;
		if (this.currentPos > text.length()) {
			this.animationRunning = false;
			this.shownText = null;
		} else {
			this.shownText = text.substring(0, this.currentPos);
		}
		return true;
	}

	


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#hideNotify()
	 */
	public void hideNotify() {
		this.lastText = null;
		this.shownText = null;
		this.currentPos = 0;
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g) 
	{
		if ( text != this.lastText ) {
			this.lastText = text;
			this.animationRunning = true;
			this.currentPos = 0;
			this.shownText = "";
		}
		String shownTextPart = this.shownText;
		if (!this.animationRunning || shownTextPart == null) {
			//System.out.println("TypeWriter: animation not running: shownText=" + shownTextPart + ", animationRunning=" + this.animationRunning);
			g.drawString(text, x, y, orientation);
		} else {
			//System.out.println("TypeWriter: printing shownText=" + shownTextPart );
			Font font = g.getFont();
			x = getLeftX(x, orientation, font.stringWidth( text ) );
			y = getTopY(y, orientation, font.getHeight(), font.getBaselinePosition() );
			g.drawString(shownTextPart, x, y,  Graphics.TOP | Graphics.LEFT );
		}
	}

}
