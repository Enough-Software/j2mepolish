//#condition polish.usePolishGui
/*
 * Created on Jun 14, 2008 at 1:26:40 AM.
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
package de.enough.polish.ui.cssanimations;

import de.enough.polish.ui.CssAnimation;
import de.enough.polish.ui.Style;

/**
 * <p>Combines several animations for a single CSS attribute.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CombinedCssAnimation extends CssAnimation
{
	
	

	private final CssAnimation[] animations;

	/**
	 * @param cssAttributeId
	 * @param triggerEvent
	 * @param duration
	 * @param delay
	 * @param function
	 * @param repeat 
	 * @param fireEvent 
	 * @param animations all embedded animations
	 */
	public CombinedCssAnimation(int cssAttributeId, String triggerEvent,
			long duration, long delay, int function, int repeat, String fireEvent, CssAnimation[] animations )
	{
		super(cssAttributeId, triggerEvent, duration, delay,
				function, repeat, fireEvent,
				animations[0].getStartValue(), animations[ animations.length - 1].getEndValue()  );
		this.animations = animations;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.CssAnimation#animate(de.enough.polish.ui.Style, java.lang.Object)
	 */
	public Object animate(Style style, Object styleValue, long passedTime)
	{
		for (int i=0; i<this.animations.length; i++) {
			CssAnimation animation = this.animations[i];
			long animationDelay = animation.getDelay();
			long animationDuration = animation.getDuration();
			if (passedTime < animationDelay) {
				// sub animation is not yet ready:
				return styleValue;
			} else {
				if (passedTime <= animationDelay + animationDuration) {
					passedTime -= animationDelay;
					// found the correct animation:
					Object value = animation.animate(style, styleValue, passedTime);
					if (value == ANIMATION_FINISHED && i < this.animations.length -1) {
						return styleValue;
					}
					return value;
				}
			}
			passedTime -= animationDelay + animationDuration;
		}
		return ANIMATION_FINISHED;		
	}


}
