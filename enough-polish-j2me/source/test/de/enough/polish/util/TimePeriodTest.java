/*
 * Created on Jul 1, 2010 at 5:48:51 AM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.util;

import java.util.Calendar;

import de.enough.polish.util.TimePeriod;
import de.enough.polish.util.TimePoint;

import junit.framework.TestCase;


public class TimePeriodTest extends TestCase {

	public void testMatches() {
		
		TimePeriod period = new TimePeriod( new TimePoint( 2010, Calendar.DECEMBER, 31, 23, 59), false, new TimePoint( 2012, Calendar.DECEMBER, 31), true );
		
		TimePoint date = new TimePoint( 2010, Calendar.MARCH, 17);
		date = new TimePoint( 2010, Calendar.JANUARY, 1);
		assertFalse( period.matches(date) );
		date = new TimePoint( 2010, Calendar.DECEMBER, 31);
		assertFalse( period.matches(date) );
		date = new TimePoint( 2011, Calendar.JANUARY, 1);
		assertTrue( period.matches(date) );
		
		period = new TimePeriod( new TimePoint( 2011, Calendar.JANUARY, 1), false, new TimePoint( 2012, Calendar.DECEMBER, 31), true );
		date = new TimePoint( 2010, Calendar.MARCH, 17);
		assertFalse( period.matches(date) );
		
		date = new TimePoint( 2010, Calendar.JANUARY, 1);
		assertFalse( period.matches(date) );
		date = new TimePoint( 2011, Calendar.JANUARY, 1);
		assertFalse( period.matches(date) );
		date = new TimePoint( 2011, Calendar.JANUARY, 1, 0, 1);
		assertTrue( period.matches(date) );
		date = new TimePoint( 2011, Calendar.JANUARY, 2);
		assertTrue( period.matches(date) );

	}
}
