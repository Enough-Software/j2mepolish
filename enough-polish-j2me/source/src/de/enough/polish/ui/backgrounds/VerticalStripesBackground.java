//#condition polish.usePolishGui
/*
 * Created on 22-July-2011 at 10:25:51.
 *
 * Copyright (c) 2011 Robert Virkus / Enough Software
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
 */package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Dimension;

/**
 * <p>Paints an vertical stripes that are repeated horizontally.</p>
 * <p>usage:</p>
 * <pre>
 * .myStyle {
 * 		background {
 * 			type: vertical-stripes;
 * 			stripe-colors: #f00, #0f0, #00f;
 * 			stripe-widths: 10px, 5%, 10px;
 * 		}
 * }
 * </pre>
 *
 * <p>Copyright Enough Software 2004 - 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class VerticalStripesBackground extends Background {
	
	private final Color[] stripeColors;
	private final Dimension[] stripeWidhs;


	/**
	 * Creates a new vertical-stripes background
	 * @param stripeColors the colors
	 * @param stripeWidhs the associated widths
	 * @throws IllegalArgumentException when stripeColors or stripeWidths are null, or when stripeColors.length != stripeWidths.length
	 */
	public VerticalStripesBackground( Color[] stripeColors, Dimension[] stripeWidhs) {
		if (stripeColors == null) {
			throw new IllegalArgumentException("vertical-stripes background: no stripe=colors defined.");
		}
		if (stripeWidhs == null) {
			throw new IllegalArgumentException("vertical-stripes background: no stripe=widths defined.");
		}
		if (stripeColors.length != stripeWidhs.length) {
			throw new IllegalArgumentException("vertical-stripes background: stripe-colors and stripe=widths have different number of entries.");
		}
		this.stripeColors = stripeColors;
		this.stripeWidhs = stripeWidhs;
	 }

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		int endX = x + width;
		Color[] colors = this.stripeColors;
		Dimension[] widths = this.stripeWidhs;
		while (x < endX ) {
			for (int i = 0; i < colors.length; i++) {
				int stripeColor = colors[i].getColor();
				int stripeWidth = widths[i].getValue(width);
				if (x + stripeWidth > endX) {
					stripeWidth = endX - x;
				}
				g.setColor( stripeColor );
				g.fillRect(x, y, stripeWidth, height);
				x += stripeWidth;
			}			
		}

	}

}
