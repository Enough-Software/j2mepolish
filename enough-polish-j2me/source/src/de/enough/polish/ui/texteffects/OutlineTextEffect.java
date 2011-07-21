//#condition polish.usePolishGui
/*
 * Created on 16-Nov-2005 at 14:20:45.
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
 * <p>Paints the outline of a text.</p>
 * <p>Activate the shadow text effect by specifying <code>text-effect: outline;</code> in your polish.css file.
 *    You can finetune the effect with following attributes:
 * </p>
 * <ul>
 * 	 <li><b>text-outline-inner-color</b>: The color with which the outline is filled inside, defaults to white. The outline itself will be painted in the specified font-color.</li>
 * </ul>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        16-Nov-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class OutlineTextEffect extends TextEffect {
	
	private int innerColor = 0xFFFFFF;

	/**
	 * Creates a new outline text effect. 
	 */
	public OutlineTextEffect() {
		super();
	}
	
	
	//#ifdef polish.css.text-outline-inner-color
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		Integer colorInt = style.getIntProperty( "text-outline-inner-color" );
		if (colorInt != null) {
			this.innerColor = colorInt.intValue();
		}
	}
	//#endif



	public void drawString(String text, int textColor, int x, int y, int orientation, Graphics g) {
		g.drawString( text, x + 1, y + 1, orientation);
		g.drawString( text, x + 1, y - 1, orientation);
		g.drawString( text, x - 1, y - 1, orientation);
		g.drawString( text, x - 1, y + 1, orientation);
		g.setColor( this.innerColor );
		g.drawString( text, x, y, orientation);
		g.setColor( textColor );
	}

}
