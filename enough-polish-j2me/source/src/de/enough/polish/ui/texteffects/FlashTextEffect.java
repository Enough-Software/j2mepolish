//#condition polish.usePolishGui

/*
 * Created on Oct 28, 2007 at 8:55:24 PM.
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
 * <p>Flashes a text constantly. Please use sparingly as this might be quite distracting.</p>
 * <p>Usage sample:
 * <pre>
 * title {
 *    font-color: red;
 *    text-effect: flash;
 *    text-flash-interval: 800; // interval in ms
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Oct 28, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FlashTextEffect extends TextEffect
{
	
	private int interval = 700;
	private boolean isTextVisible;
	private long lastAnimationSwitch;
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#animate()
	 */
	public boolean animate()
	{
		boolean animated = super.animate();
		long current = System.currentTimeMillis();
		if (current - this.lastAnimationSwitch > this.interval) {
			this.lastAnimationSwitch = current;
			this.isTextVisible = !this.isTextVisible;
			animated = true;
		}
		return animated;
	}

	/**
	 * Creates a new text effect.
	 */
	public FlashTextEffect()
	{
		// use style settings to influence
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g)
	{
		if (this.isTextVisible) {
			g.drawString(text, x, y, orientation);
		}

	}

	//#if polish.css.text-flash-interval
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style, boolean resetStyle)
	{
		super.setStyle(style, resetStyle);
		Integer intervalInt = style.getIntProperty("text-flash-interval");
		if (intervalInt != null) {
			this.interval = intervalInt.intValue();
		}
	}
	//#endif
	
	

}
