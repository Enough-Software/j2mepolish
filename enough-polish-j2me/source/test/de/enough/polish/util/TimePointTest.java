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
import java.util.Date;
import java.util.TimeZone;

import de.enough.polish.util.TimePoint;

import junit.framework.TestCase;


public class TimePointTest extends TestCase {

	public void testIsBeforeAfter() {
		TimePoint date1 = new TimePoint( 2010, Calendar.JANUARY, 1 );
		TimePoint date2 = new TimePoint( 2010, Calendar.JANUARY, 2 );
		
		assertTrue( date1.isBefore(date2));
		assertFalse( date1.isAfter(date2));
		
		assertFalse( date2.isBefore(date1));
		assertTrue( date2.isAfter(date1));

		date1 = new TimePoint( 2010, Calendar.JANUARY, 1 );
		date2 = new TimePoint( 2010, Calendar.JANUARY, 1 );
		assertFalse( date2.isBefore(date1));
		assertFalse( date2.isAfter(date1));
		
		date1 = new TimePoint( 2010, Calendar.JANUARY, 1, 22, 9, 59, 998 );
		date2 = new TimePoint( 2010, Calendar.JANUARY, 1, 22, 9, 59, 999 );
		assertTrue( date1.isBefore(date2));
		assertFalse( date1.isAfter(date2));
		assertTrue( date1.equalsDay(date2));
		
		date1 = new TimePoint( 2010, Calendar.JANUARY, 1, 22, 9, 58, 999 );
		assertTrue( date1.isBefore(date2));
		assertFalse( date1.isAfter(date2));
		assertTrue( date1.equalsDay(date2));

		date1 = new TimePoint( 2010, Calendar.JANUARY, 1, 22, 8, 59, 999 );
		assertTrue( date1.isBefore(date2));
		assertFalse( date1.isAfter(date2));
		assertTrue( date1.equalsDay(date2));

		date1 = new TimePoint( 2010, Calendar.JANUARY, 1, 21, 9, 59, 999 );
		assertTrue( date1.isBefore(date2));
		assertFalse( date1.isAfter(date2));
		assertTrue( date1.equalsDay(date2));
		
		date1 = new TimePoint( 2010, Calendar.JANUARY, 1, 22, 9, 59, 999 );
		date2 = new TimePoint( 2010, Calendar.JANUARY, 2, 22, 9, 59, 999 );
		assertTrue( date1.isBefore(date2));
		assertFalse( date1.isAfter(date2));

		date1 = new TimePoint( 2010, Calendar.JANUARY, 2, 22, 9, 59, 999 );
		date2 = new TimePoint( 2010, Calendar.FEBRUARY, 2, 22, 9, 59, 999 );
		assertTrue( date1.isBefore(date2));
		assertFalse( date1.isAfter(date2));
		
		date1 = new TimePoint( 2009, Calendar.JANUARY, 2, 22, 9, 59, 999 );
		date2 = new TimePoint( 2010, Calendar.JANUARY, 2, 22, 9, 59, 999 );
		assertTrue( date1.isBefore(date2));
		assertFalse( date1.isAfter(date2));

		long time = System.currentTimeMillis();
		date1 = new TimePoint( time );
		date2 = new TimePoint( time + 1 );
		assertTrue( date1.isBefore(date2));
		assertFalse( date1.isAfter(date2));

	}
	
	public void testSameDay(){
		TimePoint tp = new TimePoint( 2010, Calendar.JANUARY, 30);
		TimePoint tp2 = new TimePoint( 2010, Calendar.JANUARY, 30);
	
		assertTrue( tp.equalsDay(tp2));
		
		tp2 = new TimePoint( 2010, Calendar.JANUARY, 31);
		assertFalse( tp.equalsDay(tp2));

		tp2 = new TimePoint( 2011, Calendar.JANUARY, 30);
		assertFalse( tp.equalsDay(tp2));
		
		tp2 = new TimePoint( 2010, Calendar.FEBRUARY, 30);
		assertFalse( tp.equalsDay(tp2));
		
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.YEAR, 2010 );
		cal.set( Calendar.MONTH, Calendar.JANUARY);
		cal.set( Calendar.DAY_OF_MONTH, 1 );
		cal.set( Calendar.HOUR_OF_DAY, 0 );
		cal.set( Calendar.MINUTE, 0 );
		cal.set( Calendar.SECOND, 0 );
		cal.set( Calendar.MILLISECOND, 0 );
		
		tp = new TimePoint( cal );
		assertEquals( 2010, tp.getYear() );
		assertEquals( Calendar.JANUARY, tp.getMonth());
		assertEquals( 1, tp.getDay());
		assertEquals( 0, tp.getHour() );
		assertEquals( 0, tp.getMinute() );
		assertEquals( 0, tp.getSecond() );
		assertEquals( 0, tp.getMillisecond() );
		
		tp2 = new TimePoint( cal.getTime().getTime() + 1 );
		assertEquals( 2010, tp2.getYear() );
		assertEquals( Calendar.JANUARY, tp2.getMonth());
		assertEquals( 1, tp2.getDay());
		assertEquals( 0, tp2.getHour() );
		assertEquals( 0, tp2.getMinute() );
		assertEquals( 0, tp2.getSecond() );
		assertEquals( 1, tp2.getMillisecond() );
		
		assertTrue( tp.equalsDay(tp2));
	}
	
	public void testAddMillsecond() {
		TimePoint tp = new TimePoint( 2011, Calendar.DECEMBER, 31);
		
		tp.addMillisecond( 999 );
		assertEquals( 2011, tp.getYear() );
		assertEquals( Calendar.DECEMBER, tp.getMonth());
		assertEquals( 31, tp.getDay());
		assertEquals( 0, tp.getHour() );
		assertEquals( 0, tp.getMinute() );
		assertEquals( 0, tp.getSecond() );
		assertEquals( 999, tp.getMillisecond() );
		
		
		tp.addMillisecond( 2002 );
		assertEquals( 2011, tp.getYear() );
		assertEquals( Calendar.DECEMBER, tp.getMonth());
		assertEquals( 31, tp.getDay());
		assertEquals( 0, tp.getHour() );
		assertEquals( 0, tp.getMinute() );
		assertEquals( 3, tp.getSecond() );
		assertEquals( 1, tp.getMillisecond() );
		
		tp.addMillisecond( -3002 );
		assertEquals( 2011, tp.getYear() );
		assertEquals( Calendar.DECEMBER, tp.getMonth());
		assertEquals( 30, tp.getDay());
		assertEquals( 23, tp.getHour() );
		assertEquals( 59, tp.getMinute() );
		assertEquals( 59, tp.getSecond() );
		assertEquals( 999, tp.getMillisecond() );
		
	}
	
	public void testAddSecond() {
		TimePoint tp = new TimePoint( 2011, Calendar.DECEMBER, 31);
		
		tp.addSecond( 181 );
		assertEquals( 2011, tp.getYear() );
		assertEquals( Calendar.DECEMBER, tp.getMonth());
		assertEquals( 31, tp.getDay());
		assertEquals( 0, tp.getHour() );
		assertEquals( 3, tp.getMinute() );
		assertEquals( 1, tp.getSecond() );
		
		tp.addSecond( 59 );
		assertEquals( 2011, tp.getYear() );
		assertEquals( Calendar.DECEMBER, tp.getMonth());
		assertEquals( 31, tp.getDay());
		assertEquals( 0, tp.getHour() );
		assertEquals( 4, tp.getMinute() );
		assertEquals( 0, tp.getSecond() );

		tp.addSecond( -241 );
		assertEquals( 2011, tp.getYear() );
		assertEquals( Calendar.DECEMBER, tp.getMonth());
		assertEquals( 30, tp.getDay());
		assertEquals( 23, tp.getHour() );
		assertEquals( 59, tp.getMinute() );
		assertEquals( 59, tp.getSecond() );

	}
	
	public void testAddMinute() {
		TimePoint tp = new TimePoint( 2011, Calendar.DECEMBER, 31, 10, 0);
		
		tp.addHour( 5 );
		assertEquals( 2011, tp.getYear() );
		assertEquals( Calendar.DECEMBER, tp.getMonth());
		assertEquals( 31, tp.getDay());
		assertEquals( 15, tp.getHour() );
		assertEquals( 0, tp.getMinute() );
		
		tp.addMinute( 59 );
		assertEquals( 2011, tp.getYear() );
		assertEquals( Calendar.DECEMBER, tp.getMonth());
		assertEquals( 31, tp.getDay());
		assertEquals( 15, tp.getHour() );
		assertEquals( 59, tp.getMinute() );

		tp.addMinute( 1 );
		assertEquals( 2011, tp.getYear() );
		assertEquals( Calendar.DECEMBER, tp.getMonth());
		assertEquals( 31, tp.getDay());
		assertEquals( 16, tp.getHour() );
		assertEquals( 0, tp.getMinute() );

		tp.addMinute( 200 );
		assertEquals( 2011, tp.getYear() );
		assertEquals( Calendar.DECEMBER, tp.getMonth());
		assertEquals( 31, tp.getDay());
		assertEquals( 19, tp.getHour() );
		assertEquals( 20, tp.getMinute() );
		
		tp.addMinute( 300 );
		assertEquals( 2012, tp.getYear() );
		assertEquals( Calendar.JANUARY, tp.getMonth());
		assertEquals( 1, tp.getDay());
		assertEquals( 0, tp.getHour() );
		assertEquals( 20, tp.getMinute() );
		
		tp.addMinute( -21 );
		assertEquals( 2011, tp.getYear() );
		assertEquals( Calendar.DECEMBER, tp.getMonth());
		assertEquals( 31, tp.getDay());
		assertEquals( 23, tp.getHour() );
		assertEquals( 59, tp.getMinute() );

		tp.addMinute( -359 );
		assertEquals( 2011, tp.getYear() );
		assertEquals( Calendar.DECEMBER, tp.getMonth());
		assertEquals( 31, tp.getDay());
		assertEquals( 18, tp.getHour() );
		assertEquals( 0, tp.getMinute() );
		
	}
	
	public void testAddHour() {
		TimePoint tp = new TimePoint( 2010, Calendar.DECEMBER, 31, 10, 0);
		
		tp.addHour( 5 );
		assertEquals( 2010, tp.getYear() );
		assertEquals( Calendar.DECEMBER, tp.getMonth());
		assertEquals( 31, tp.getDay());
		assertEquals( 15, tp.getHour() );
		
		tp.addHour( 9 );
		assertEquals( 2011, tp.getYear() );
		assertEquals( Calendar.JANUARY, tp.getMonth());
		assertEquals( 1, tp.getDay());
		assertEquals( 0, tp.getHour() );
		
		tp.addHour( 49 );
		assertEquals( 2011, tp.getYear() );
		assertEquals( Calendar.JANUARY, tp.getMonth());
		assertEquals( 3, tp.getDay());
		assertEquals( 1, tp.getHour() );
		
		tp.addHour(-2);
		assertEquals( Calendar.JANUARY, tp.getMonth());
		assertEquals( 2, tp.getDay());
		assertEquals( 23, tp.getHour() );
	
		tp.addHour(-56);
		assertEquals( 2010, tp.getYear() );
		assertEquals( Calendar.DECEMBER, tp.getMonth());
		assertEquals( 31, tp.getDay());
		assertEquals( 15, tp.getHour() );
	}

	
	public void testAddDay() {
		TimePoint tp = new TimePoint( 2010, Calendar.JANUARY, 30);
		tp.addDay(4);
		assertEquals( Calendar.FEBRUARY, tp.getMonth() );
		assertEquals( 2010, tp.getYear() );
		assertEquals( 3, tp.getDay() );

		tp.addDay(17);
		assertEquals( Calendar.FEBRUARY, tp.getMonth() );
		assertEquals( 2010, tp.getYear() );
		assertEquals( 20, tp.getDay() );

		tp.addDay(9);
		assertEquals( Calendar.MARCH, tp.getMonth() );
		assertEquals( 2010, tp.getYear() );
		assertEquals( 1, tp.getDay() );
		
		tp.addDay(-30);
		assertEquals( Calendar.JANUARY, tp.getMonth() );
		assertEquals( 2010, tp.getYear() );
		assertEquals( 30, tp.getDay() );

		tp = new TimePoint( 2010, Calendar.DECEMBER, 31);
		tp.addDay(1);
		assertEquals( Calendar.JANUARY, tp.getMonth() );
		assertEquals( 2011, tp.getYear() );
		assertEquals( 1, tp.getDay() );
		tp.addDay(-1);
		assertEquals( Calendar.DECEMBER, tp.getMonth() );
		assertEquals( 2010, tp.getYear() );
		assertEquals( 31, tp.getDay() );

		tp.addDay(36);
		assertEquals( Calendar.FEBRUARY, tp.getMonth() );
		assertEquals( 2011, tp.getYear() );
		assertEquals( 5, tp.getDay() );
		
		tp.addDay(-36);
		assertEquals( Calendar.DECEMBER, tp.getMonth() );
		assertEquals( 2010, tp.getYear() );
		assertEquals( 31, tp.getDay() );

	}
	
	
	public void testAddMonth() {
		TimePoint tp = new TimePoint( 2010, Calendar.JANUARY, 31);
		tp.addMonth(2);
		assertEquals( Calendar.MARCH, tp.getMonth() );
		assertEquals( 2010, tp.getYear() );
		assertEquals( 31, tp.getDay() );
		
		tp = new TimePoint( 2010, Calendar.DECEMBER, 31);
		tp.addMonth(1);
		assertEquals( Calendar.JANUARY, tp.getMonth() );
		assertEquals( 2011, tp.getYear() );
		assertEquals( 31, tp.getDay() );
		
		tp.addMonth(12);
		assertEquals( Calendar.JANUARY, tp.getMonth() );
		assertEquals( 2012, tp.getYear() );
		assertEquals( 31, tp.getDay() );
		
		tp.addMonth(25);
		assertEquals( Calendar.FEBRUARY, tp.getMonth() );
		assertEquals( 2014, tp.getYear() );
		assertEquals( 28, tp.getDay() );

		tp.addMonth(-1);
		assertEquals( Calendar.JANUARY, tp.getMonth() );
		assertEquals( 2014, tp.getYear() );
		assertEquals( 28, tp.getDay() );

		tp.addMonth(-12);
		assertEquals( Calendar.JANUARY, tp.getMonth() );
		assertEquals( 2013, tp.getYear() );
		assertEquals( 28, tp.getDay() );

		tp.addMonth(-13);
		assertEquals( Calendar.DECEMBER, tp.getMonth() );
		assertEquals( 2011, tp.getYear() );
		assertEquals( 28, tp.getDay() );

	}
	
	
	public void testAddYear() {
		TimePoint tp = new TimePoint( 2010, Calendar.JANUARY, 31);
		tp.addYear(2);
		assertEquals( Calendar.JANUARY, tp.getMonth() );
		assertEquals( 31, tp.getDay() );
		assertEquals( 2012, tp.getYear() );
		
		tp.addYear(-2);
		assertEquals( Calendar.JANUARY, tp.getMonth() );
		assertEquals( 31, tp.getDay() );
		assertEquals( 2010, tp.getYear() );

	}
	
	public void testAddDate() {
		TimePoint tp = new TimePoint( 2010, Calendar.JANUARY, 31);
		TimePoint date = new TimePoint( 1, 0, 0);
		
		tp.add( date );
		assertEquals( Calendar.JANUARY, tp.getMonth() );
		assertEquals( 31, tp.getDay() );
		assertEquals( 2011, tp.getYear() );
		
		date = new TimePoint( 0, 1, 0);
		tp.add( date );
		assertEquals( Calendar.FEBRUARY, tp.getMonth() );
		assertEquals( 28, tp.getDay() );
		assertEquals( 2011, tp.getYear() );
		
		date = new TimePoint( 0, 0, 1);
		tp.add( date );
		assertEquals( Calendar.MARCH, tp.getMonth() );
		assertEquals( 1, tp.getDay() );
		assertEquals( 2011, tp.getYear() );

		date = new TimePoint( 0, 0, 0, 22, 0);
		tp.add( date );
		assertEquals( Calendar.MARCH, tp.getMonth() );
		assertEquals( 1, tp.getDay() );
		assertEquals( 2011, tp.getYear() );
		assertEquals( 22, tp.getHour());

	}
	
	public void testSubtractDate() {
		TimePoint tp = new TimePoint( 2010, Calendar.JANUARY, 31);
		TimePoint date = new TimePoint( 1, 0, 0);
		
		tp.subtract( date );
		assertEquals( Calendar.JANUARY, tp.getMonth() );
		assertEquals( 31, tp.getDay() );
		assertEquals( 2009, tp.getYear() );
		
		date = new TimePoint( 0, 1, 0);
		tp.subtract( date );
		assertEquals( Calendar.DECEMBER, tp.getMonth() );
		assertEquals( 31, tp.getDay() );
		assertEquals( 2008, tp.getYear() );
		
		date = new TimePoint( 0, 0, 1);
		tp.subtract( date );
		assertEquals( Calendar.DECEMBER, tp.getMonth() );
		assertEquals( 30, tp.getDay() );
		assertEquals( 2008, tp.getYear() );

		date = new TimePoint( 0, 0, 0, 22, 0);
		tp.subtract( date );
		assertEquals( Calendar.DECEMBER, tp.getMonth() );
		assertEquals( 29, tp.getDay() );
		assertEquals( 2008, tp.getYear() );
		assertEquals( 2, tp.getHour());

	}
	
	public void testDifference() {
		TimePoint one;
		TimePoint two;
		TimePoint diff;
		
		one = new TimePoint( 2010, Calendar.JULY, 8 );
		two = new TimePoint( 2010, Calendar.JULY, 9 );
		diff = one.difference(two);
		assertEquals( 0, diff.getYear() );
		assertEquals( 0, diff.getMonth() );
		assertEquals( 1, diff.getDay() );
		diff = two.difference(one);
		assertEquals( 0, diff.getYear() );
		assertEquals( 0, diff.getMonth() );
		assertEquals( 1, diff.getDay() );
		
		one = new TimePoint( 2009, Calendar.JULY, 8 );
		two = new TimePoint( 2010, Calendar.JULY, 9 );
		diff = one.difference(two);
		assertEquals( 1, diff.getYear() );
		assertEquals( 0, diff.getMonth() );
		assertEquals( 1, diff.getDay() );
		diff = two.difference(one);
		assertEquals( 1, diff.getYear() );
		assertEquals( 0, diff.getMonth() );
		assertEquals( 1, diff.getDay() );
		
		one = new TimePoint( 2010, Calendar.JULY, 8 );
		two = new TimePoint( 2009, Calendar.JULY, 9 );
		diff = one.difference(two);
		assertEquals( 0, diff.getYear() );
		assertEquals( 11, diff.getMonth() );
		assertEquals( 29, diff.getDay() );
		diff = two.difference(one);
		assertEquals( 0, diff.getYear() );
		assertEquals( 11, diff.getMonth() );
		assertEquals( 29, diff.getDay() );
		
	}

	
	public void testTimeInMillis() {
		Date date = new Date();
		long time = date.getTime();
		TimePoint tp = new TimePoint( time );
		
		assertEquals( time, tp.getTimeInMillis() );
	}
	
	public void testGetWeekday() {
		TimePoint point = new TimePoint(2011, Calendar.MAY, 1);
		assertEquals( Calendar.SUNDAY, point.getDayOfWeek());
	}
	
	public void testCompare() {
		TimePoint tp1;
		TimePoint tp2;
		
		tp1 = new TimePoint( 2010, Calendar.MARCH, 17 );
		tp2 = new TimePoint( 2010, Calendar.MARCH, 18 );
		
		assertTrue( tp1.compareTo(tp2) < 0);
		assertTrue( tp2.compareTo(tp1) > 0);
		
		tp2 = new TimePoint( 2010, Calendar.MARCH, 17 );
		assertEquals( 0, tp1.compareTo(tp2));
		assertEquals( 0, tp2.compareTo(tp1));
		
		tp1 = new TimePoint( 2010, Calendar.MARCH, 17 );
		tp2 = new TimePoint( 2010, Calendar.MARCH, 17, 1, 0 ); // 1 AM
		
		assertTrue( tp1.compareTo(tp2) < 0);
		assertTrue( tp2.compareTo(tp1) > 0);
		
		tp1 = new TimePoint( 2010, Calendar.MARCH, 17 );
		tp2 = new TimePoint( 2011, Calendar.MARCH, 17, 1, 0 ); // 1 AM
		
		assertTrue( tp1.compareTo(tp2) < 0);
		assertTrue( tp2.compareTo(tp1) > 0);
		
		tp1 = new TimePoint( 2010, Calendar.MARCH, 18 );
		tp2 = new TimePoint( 2011, Calendar.MARCH, 17, 1, 0 ); // 1 AM
		
		assertTrue( tp1.compareTo(tp2) < 0);
		assertTrue( tp2.compareTo(tp1) > 0);
		
		tp1 = new TimePoint( 2010, Calendar.APRIL, 17 );
		tp2 = new TimePoint( 2011, Calendar.MARCH, 17, 1, 0 ); // 1 AM
		assertTrue( tp1.compareTo(tp2) < 0);
		assertTrue( tp2.compareTo(tp1) > 0);


	}
	
	public void testParseRfc3339() {
		String date;
		TimePoint tp;
		
		date = "1985-04-12T23:20:50Z";
		tp = TimePoint.parseRfc3339(date);
		assertEquals( 1985, tp.getYear() );
		assertEquals( Calendar.APRIL, tp.getMonth() );
		assertEquals( 12, tp.getDay() );
		assertEquals( 23, tp.getHour() );
		assertEquals( 20, tp.getMinute() );
		assertEquals(50, tp.getSecond() );
		assertEquals( TimeZone.getTimeZone("GMT"), tp.getTimeZone() );
		
		date = "1985-04-12T23:20:50.52Z";
		tp = TimePoint.parseRfc3339(date);
		assertEquals( 1985, tp.getYear() );
		assertEquals( Calendar.APRIL, tp.getMonth() );
		assertEquals( 12, tp.getDay() );
		assertEquals( 23, tp.getHour() );
		assertEquals( 20, tp.getMinute() );
		assertEquals(50, tp.getSecond() );
		assertEquals(520, tp.getMillisecond() );
		assertEquals( TimeZone.getTimeZone("GMT"), tp.getTimeZone() );

		
		date = "1985-04-12T23:20:50.521Z";
		tp = TimePoint.parseRfc3339(date);
		assertEquals( 1985, tp.getYear() );
		assertEquals( Calendar.APRIL, tp.getMonth() );
		assertEquals( 12, tp.getDay() );
		assertEquals( 23, tp.getHour() );
		assertEquals( 20, tp.getMinute() );
		assertEquals(50, tp.getSecond() );
		assertEquals(521, tp.getMillisecond() );
		assertEquals( TimeZone.getTimeZone("GMT"), tp.getTimeZone() );

		date = "1985-04-12T23:20:50-01:00";
		tp = TimePoint.parseRfc3339(date);
		assertEquals( 1985, tp.getYear() );
		assertEquals( Calendar.APRIL, tp.getMonth() );
		assertEquals( 12, tp.getDay() );
		assertEquals( 23, tp.getHour() );
		assertEquals( 20, tp.getMinute() );
		assertEquals(50, tp.getSecond() );
		assertEquals( -1 * 60 * 60 * 1000, tp.getTimeZone().getRawOffset() );

		date = "1985-04-12T23:20:50.52+01:00";
		tp = TimePoint.parseRfc3339(date);
		assertEquals( 1985, tp.getYear() );
		assertEquals( Calendar.APRIL, tp.getMonth() );
		assertEquals( 12, tp.getDay() );
		assertEquals( 23, tp.getHour() );
		assertEquals( 20, tp.getMinute() );
		assertEquals(50, tp.getSecond() );
		assertEquals(520, tp.getMillisecond() );
		assertEquals( 1 * 60 * 60 * 1000, tp.getTimeZone().getRawOffset() );

		date = "1985-04-12T23:20:50.52000000+01:00";
		tp = TimePoint.parseRfc3339(date);
		assertEquals( 1985, tp.getYear() );
		assertEquals( Calendar.APRIL, tp.getMonth() );
		assertEquals( 12, tp.getDay() );
		assertEquals( 23, tp.getHour() );
		assertEquals( 20, tp.getMinute() );
		assertEquals(50, tp.getSecond() );
		assertEquals(520, tp.getMillisecond() );
		assertEquals( 1 * 60 * 60 * 1000, tp.getTimeZone().getRawOffset() );

	}
}
