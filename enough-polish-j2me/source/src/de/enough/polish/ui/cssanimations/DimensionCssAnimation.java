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
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Style;

/**
 * <p>Animates numerical dimension CSS attributes.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DimensionCssAnimation extends CssAnimation
{
	
	protected final int startValue;
	protected final int endValue;
	private final boolean isPercent;
	

	/**
	 * @param cssAttributeId
	 * @param triggerEvent
	 * @param duration
	 * @param delay
	 * @param function
	 * @param repeat 
	 * @param fireEvent 
	 * @param startValue the integer value at the beginning
	 * @param endValue the integer value at the end of this animation
	 */
	public DimensionCssAnimation(int cssAttributeId, String triggerEvent,
			long duration, long delay, int function, int repeat, String fireEvent,
			Dimension startValue, Dimension endValue)
	{
		super(cssAttributeId, triggerEvent, duration, delay,
				function, repeat, fireEvent, startValue, endValue );
		this.startValue = startValue.getValue(100);
		this.endValue = endValue.getValue(100);
		this.isPercent = startValue.isPercent();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.CssAnimation#animate(de.enough.polish.ui.Style, java.lang.Object)
	 */
	public Object animate(Style style, Object styleValue, long passedTime)
	{
		
		int currentValue = calculatePointInRange(this.startValue, this.endValue, passedTime, this.duration, this.function);
		//System.out.println("running percent or absolute animation: current value=" + currentValue);
		if (currentValue == this.endValue) {
			return ANIMATION_FINISHED;
		}
		

		
		
		//((PercentOrAbsoluteInteger)styleValue).setValue(currentValue);
		Dimension x = new Dimension(currentValue, this.isPercent ); 

		
		return x;
		
	}

}
