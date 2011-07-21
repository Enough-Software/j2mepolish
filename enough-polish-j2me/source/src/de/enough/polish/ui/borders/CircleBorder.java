//#condition polish.usePolishGui
/*
 * Created on 17-Jul-2004 at 15:31:50.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.ui.borders;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Border;

/**
 * <p>The CircleBorder paints a circle or elliptical border.</p>
 * <p>Following CSS-attributes are supported:</p>
 * <ul>
 * 	<li><b>type</b>: the type of the border, needs to be "circle".</li>
 * 	<li><b>color</b>: the color of the border, defaults to "black".</li>
 * 	<li><b>width</b>: the width of the border, defaults to "1" pixel.</li>
 * 	<li><b>stroke-style</b>: the stroke-style, either "dotted" or "solid". Defaults to "solid". </li>
 * </ul>
 *
 * <p>Copyright Enough Software 2004 - 2009</p>

 * <pre>
 * history
 *        17-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CircleBorder extends Border {

	private final int strokeStyle;
	private final int color;

	public CircleBorder( int color, int width, int strokeStyle ) {
		super( width, width, width, width);
		this.color = color;
		this.strokeStyle = strokeStyle;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		boolean setStrokeStyle = (this.strokeStyle != Graphics.SOLID );
		if (setStrokeStyle) {
			g.setStrokeStyle( this.strokeStyle );
		}
		g.drawArc( x, y, width, height, 0, 360 );
		if (this.borderWidthLeft > 1) {
			int bw = this.borderWidthLeft;
			while (bw > 0) {
				g.drawArc( x + bw, y + bw, width - 2*bw, height - 2*bw, 0, 360 );
				bw--;
			}
		}
		if (setStrokeStyle) {
			g.setStrokeStyle( Graphics.SOLID );
		}
	}

}
