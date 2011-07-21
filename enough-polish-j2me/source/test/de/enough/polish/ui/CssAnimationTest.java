/*
 * Created on Jun 23, 2008 at 12:43:37 AM.
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

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssAnimationTest extends TestCase
{
	
	public void testFunctionEaseOut() {
//		int lastValue = 0;
//		int lastDifference = 0;
//		for (int i=0; i<100; i++) {
//			int currentValue = i*i*i;
//			int difference = currentValue - lastValue;
//			System.out.println(i + "=" + currentValue + " (+" + difference + ") /  (+" + (difference - lastDifference) + ")"  );
//			lastDifference = difference;
//			lastValue = currentValue;
//		}
		
		long duration = 1000;
		int startValue = 30;
		int endValue = 0;
		for (long time = 0; time < duration; time += 50) {
			System.out.println( time + "ms = " + CssAnimation.calculatePointInRange(startValue, endValue, time, duration, CssAnimation.FUNCTION_EASE_OUT));
		}
		
	}

}
