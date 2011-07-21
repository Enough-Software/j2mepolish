//#condition polish.usePolishGui
/*
 * Created on Jun 13, 2008 at 11:51:51 PM.
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
package de.enough.polish.ui;

import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.MathUtil;


/**
 * <p>Animates a CSS attribute for a style.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class CssAnimation 
{
	
	public static final int FUNCTION_EASE = 1;
	public static final int FUNCTION_LINEAR = 2;
	public static final int FUNCTION_EASE_IN = 3;
	public static final int FUNCTION_EASE_OUT = 4;
	public static final Object ANIMATION_FINISHED = new Object();
	
	protected final int cssAttributeId;
	protected String triggerEventId;
	protected final long duration;
	protected final long delay;
	protected final int function;
	protected final int repeat;
	protected final Object startValue;
	protected final Object endValue;
	protected final String fireEvent;
	
	protected CssAnimation( int cssAttributeId, String triggerEvent, long duration, long initialDelay, int function, int repeat, String fireEvent, Object startValue, Object endValue ) {
		this.cssAttributeId = cssAttributeId;
		this.triggerEventId = triggerEvent;
		this.duration = duration;
		this.delay = initialDelay;
		this.function = function;
		this.repeat = repeat;
		this.fireEvent = fireEvent;
		this.startValue = startValue;
		this.endValue = endValue;
	}
	
	/**
	 * @param style
	 */
	public void setStartValue(Style style)
	{
		style.addAttribute( this.cssAttributeId, this.startValue );
	}
	
	/**
	 * @param style
	 */
	public void setEndValue(Style style)
	{
		style.addAttribute( this.cssAttributeId, this.endValue );
	}

	
	public Object getStartValue() {
		return this.startValue;
	}
	
	public Object getEndValue() {
		return this.endValue;
	}


	
	/**
	 * Animates a UI element
	 * @param style the style of the element
	 * @param styleValue the current value
	 * @param passedTime the time passed since the start of this animation in milliseconds
	 * @return the next value, ANIMATION_FINISHED when the animation is finished
	 * @see #ANIMATION_FINISHED
	 */
	public abstract Object animate(Style style, Object styleValue, long passedTime);

		
	public static int calculatePointInRange( int startValue, int endValue, long passedTime, long duration, int function ) {

				
		if (passedTime >= duration) {
			//System.out.println("too long, returning " + endValue + ", passedTime=" + passedTime + ", duration=" + duration);
			return endValue;
		}

		int valueRange = endValue - startValue;
		switch (function) {
		case FUNCTION_EASE:
			int time250Promille = (int)((passedTime * 250) / duration);
			int apxSin = MathUtil.apxSin(time250Promille);
			return startValue + ((apxSin * valueRange) / 1000);
		case FUNCTION_LINEAR:
			int linearValue = (int) (startValue + (valueRange * passedTime)/duration);
			return linearValue;
		case FUNCTION_EASE_IN:
		case FUNCTION_EASE_OUT:
			long max = duration^2;
			long current;
			if (function == FUNCTION_EASE_OUT) {
				current = (duration-passedTime)^2;
				return (int) (endValue - ((current * valueRange)/max));
			} else {
				current = passedTime^2;
				return (int) (startValue + ((current * valueRange)/max));
			}
		default:
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * @param startColor
	 * @param endColor
	 * @param passedTime time since the start of this animation
	 * @param duration
	 * @param function
	 * @return the resulting color
	 */
	public static int calculateColorInRange(int startColor, int endColor,
			long passedTime, long duration,
			int function)
	{
		if (passedTime >= duration) {
			//System.out.println("too long, returning " + endValue);
			return endColor;
		}
		switch (function) {
		case FUNCTION_EASE:
			int time250Promille = (int)((passedTime * 250) / duration);
			int apxSin = MathUtil.apxSin(time250Promille);
			return DrawUtil.getGradientColor(startColor, endColor, apxSin);
		case FUNCTION_LINEAR:
			return DrawUtil.getGradientColor(startColor, endColor, (int) ((passedTime*1000) / duration) );
		case FUNCTION_EASE_IN:
		case FUNCTION_EASE_OUT:
			long max = duration^2;
			long current;
			if (function == FUNCTION_EASE_OUT) {
				current = (duration-passedTime)^2;
				return DrawUtil.getGradientColor(endColor, startColor, (int) ((current * 1000)/max) );
			} else {
				current = passedTime^2;
				return DrawUtil.getGradientColor(startColor, endColor, (int) ((current * 1000)/max) );
			}
		default:
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Retrieves the waiting time before this animation starts
	 * 
	 * @return the delay in milliseconds
	 */
	public long getDelay()
	{
		return this.delay;
	}
	
	/**
	 * Retrieves the duration of this animation.
	 * 
	 * @return the duration in milliseconds
	 */
	public long getDuration() {
		return this.duration;
	}

	/**
	 * Retrieves the number of times this animation should be repeated
	 * 
	 * @return 0 when it should not be repeated, 'n' for repeating it 'n' times, -1 when it should be repeated until a new style has been set
	 */
	public int getRepeat() {
		return this.repeat;
	}
	
	/**
	 * Retrieves the event that is fired when this animations is finished.
	 * 
	 * @return the event
	 */
	public String getFireEvent() {
		return this.fireEvent;
	}
	
}
