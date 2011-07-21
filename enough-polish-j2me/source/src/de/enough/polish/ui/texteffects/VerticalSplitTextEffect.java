//#condition polish.usePolishGui
/*
 * Created on Apr 24, 2008 at 2:10:00 AM.
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

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;

/**
 * <p>Paints the text in two colors that are split vertically.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class VerticalSplitTextEffect extends TextEffect
{

	private int bottomColor = 0xcccccc;
	private int splitPos = 50;



	/**
	 * Creates a new split text effect
	 */
	public VerticalSplitTextEffect()
	{
		// initialization is done within setStyle
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style,boolean)
	 */
	public void setStyle(Style style, boolean resetStyle)
	{
		super.setStyle(style, resetStyle);
		//#if polish.css.text-split-bottom-color
			Color bottomColorObj = style.getColorProperty("text-split-bottom-color");
			if (bottomColorObj != null) {
				this.bottomColor = bottomColorObj.getColor();
			}
		//#endif
		//#if polish.css.text-split-split-pos
			Integer splitPosObj = style.getIntProperty("text-split-split-pos");
			if (splitPosObj != null) {
				this.splitPos = splitPosObj.intValue();
			}
		//#endif
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g)
	{
		//System.out.println("Drawing split with splitPos=" + this.splitPos + ", color=" + Integer.toHexString(this.bottomColor) + ", text=" + text);
		g.setColor(textColor);
		g.drawString(text, x, y, orientation);
		
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipW = g.getClipWidth();
		int clipH = g.getClipHeight();
		int split = (g.getFont().getHeight() * this.splitPos) / 100;
		if (orientation == 0) {
			g.clipRect(clipX, y + split , clipW, clipH );
		} else {
			int topY = getTopY(y, orientation, g.getFont());
			g.clipRect(clipX, topY + split , clipW, clipH );
		}
		g.setColor(this.bottomColor);
		g.drawString(text, x, y, orientation);
		g.setClip(clipX, clipY, clipW, clipH);
	}

}
