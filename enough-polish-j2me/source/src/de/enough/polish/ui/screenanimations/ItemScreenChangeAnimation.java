//#condition polish.usePolishGui
/*
 * Created on Jan 4, 2009 at 7:20:13 AM.
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
package de.enough.polish.ui.screenanimations;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.ItemTransition;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;

/**
 * <p>Just animates content parts of two different screens rather than the complete screen.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ItemScreenChangeAnimation extends ScreenChangeAnimation
{
	
	/**
	 * Creates a new item screen transition.
	 */
	public ItemScreenChangeAnimation()
	{
		// use show for initialization
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, de.enough.polish.ui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Canvas, de.enough.polish.ui.Displayable, boolean)
	 */
	protected void onShow(Style style, Display dsplay, int width, int height,
			Displayable lstDisplayable, Displayable nxtDisplayable, boolean isForward)
	{
		if (lstDisplayable instanceof Screen && nxtDisplayable instanceof Screen) {
			//#if polish.css.item-screen-change-animation-container-transition
				ItemTransition containerTransition = (ItemTransition) style.getObjectProperty("item-screen-change-animation-container-transition");
				if (containerTransition != null) {
					containerTransition.init( UiAccess.getScreenContainer((Screen)lstDisplayable), UiAccess.getScreenContainer((Screen)nxtDisplayable) );
					UiAccess.getScreenContainer((Screen)nxtDisplayable).setItemTransition( containerTransition );
				}
			//#endif
			//TODO do the same for title and menubar
		}
		super.onShow(style, dsplay, width, height, lstDisplayable, nxtDisplayable,
				isForward);
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate()
	 */
	protected boolean animate()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#paintAnimation(javax.microedition.lcdui.Graphics)
	 */
	protected void paintAnimation(Graphics g)
	{
		// the item transitions paint themselves

	}

}
