//#condition polish.usePolishGui

/*
 * Created on 09.06.2006 at 15:41:12.
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
package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;
import de.enough.polish.util.DrawUtil;
/**
 * DoubleGradientVerticalBackground generates an nice Backgroundscreen,
 * with two gradients, each taking half of the available height.
 * 
 * <p>Copyright Enough Software 2007 - 2010</p>
 * @author Andre Schmidt
 */
public class DoubleGradientVerticalBackground  extends Background {
	private final int firstTopColor;
	private final int firstBottomColor;
	
	private final int secondTopColor;
	private final int secondBottomColor;
	
	
	private final int stroke;
	private final int start;
	private final int end;
	private final boolean isPercent;

	private int[] firstGradient;
	private int[] secondGradient;
	private int lastHeight;
	private int startLine;
	private int endLine;

	/**
	 * Creates a new double gradient background
	 * 
	 * @param firstTopColor the color at the top of the first gradient
	 * @param firstBottomColor the color at the bottom of the first gradient
	 * @param secondTopColor the color at the top of the second gradient
	 * @param secondBottomColor the color at the bottom of the second gradient
	 * @param stroke the line stroke style
	 */
	public DoubleGradientVerticalBackground(int firstTopColor, int firstBottomColor, int secondTopColor, int secondBottomColor, int stroke) {
		this( firstTopColor, firstBottomColor, secondTopColor, secondBottomColor, stroke, 0, 0, false );
		
	}
	
	/**
	 * Creates a new double gradient background
	 * 
	 * @param firstTopColor the color at the top of the first gradient
	 * @param firstBottomColor the color at the bottom of the first gradient
	 * @param secondTopColor the color at the top of the second gradient
	 * @param secondBottomColor the color at the bottom of the second gradient
	 * @param stroke the line stroke style
	 * @param start the line counted from the top at which the gradient starts, either in pixels or in percent
	 * @param end the line counted from the top at which the gradient ends, either in pixels or in percent
	 * @param isPercent true when the start and end settings should be counted in percent
	 */
	public DoubleGradientVerticalBackground( int firstTopColor, int firstBottomColor, int secondTopColor, int secondBottomColor, int stroke, int start, int end, boolean isPercent ) {
		this.firstTopColor = firstTopColor;
		this.firstBottomColor = firstBottomColor;
		this.secondTopColor = secondTopColor;
		this.secondBottomColor = secondBottomColor;
		
		this.stroke = stroke;
		this.start = start;
		this.end = end;
		this.isPercent = isPercent;
	}
	
	/*
	 * Paints the screen 
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setStrokeStyle(this.stroke);
		
		paintGradient(this.firstGradient,x,y,width,height / 2, this.firstTopColor, this.firstBottomColor, g);
		paintGradient(this.secondGradient,x,y + height / 2,width,height / 2, this.secondTopColor, this.secondBottomColor, g);
		
		g.setStrokeStyle( Graphics.SOLID );
	}
	
	private void paintGradient(int[] gradient, int x, int y, int width, int height, int topColor, int bottomColor, Graphics g)
	{
		int startOffset = this.startLine;
		int endOffset = this.endLine;
		
		if (gradient == null || this.lastHeight != height ) {
			int steps = height;
			if (this.start != this.end) {
				steps = this.end - this.start;
				if (this.isPercent) {
					this.startLine = (this.start * height) / 100;
					this.endLine = (this.end * height) / 100;
					steps = this.endLine - this.startLine;
				} else {
					this.startLine = this.start;
					this.endLine = this.end;
				}
				startOffset = this.startLine;
				endOffset = this.endLine;				
			} else {
				this.endLine = height;
				endOffset = height;
			}
			gradient = DrawUtil.getGradient( topColor, bottomColor, steps );
			this.lastHeight = height;
		}
		
		g.setColor( topColor );
		for (int i = 0; i < height; i++) {
			if (i >= startOffset  && i < endOffset ) {
				int color = gradient[i - startOffset ];
				g.setColor( color );
				
			}
			g.drawLine( x, y, x + width, y);
			y++;
		}
	}
}
