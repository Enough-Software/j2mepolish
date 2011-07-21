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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;


/**
 * <p>Separates the text into several horizontal segments.</p>
 * <p>Activate the shadow text effect by specifying <code>text-effect: segment;</code> in your polish.css file.
 *    You can finetune the effect with following attributes:
 * </p>
 * <ul>
 * 	 <li><b>text-segment-color</b>: The color of the lines used for separating the segments, defaults to white..</li>
 * 	 <li><b>text-segment-gap</b>: The gap between segment lines, defaults to 3.</li>
 * </ul>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        16-Nov-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SegmentTextEffect extends TextEffect {
	
	private int lineColor = 0xFFFFFF;
	private int gap = 3;

	/**
	 * Creates a new segment text effect. 
	 */
	public SegmentTextEffect() {
		super();
	}
	
	
	//#if polish.css.text-segment-color || polish.css.text-segment-gap 
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		//#ifdef polish.css.text-segment-color
			Integer colorInt = style.getIntProperty( "text-segment-color" );
			if (colorInt != null) {
				this.lineColor = colorInt.intValue();
			}
		//#endif
		//#ifdef polish.css.text-segment-gap
			Integer gapInt = style.getIntProperty( "text-segment-gap" );
			if (gapInt != null) {
				this.gap = gapInt.intValue();
			}
		//#endif
	}
	//#endif



	public void drawString(String text, int textColor, int x, int y, int orientation, Graphics g) {
		g.drawString( text, x, y, orientation);
		
		// calculate segment positions:
		Font font = g.getFont();
		int width = font.stringWidth( text );
		int height = font.getHeight();
		int yStart = getTopY(y, orientation, height, font.getBaselinePosition() );
		int xStart = getLeftX(x, orientation, width);
		g.setColor( this.lineColor );
		height -= 2;
		int i = yStart + this.gap;
		while ( i < yStart + height ) {
			//for (int i = yStart + this.gap; i < yStart + height; i += this.gap ) {
			g.drawLine( xStart, i, xStart + width, i );
			i += this.gap;
		}
	}
	
}
