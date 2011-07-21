/*
 * Created on Jun 9, 2010 at 4:02:44 PM.
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
package de.enough.polish.calendar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;

import de.enough.polish.util.TimePeriod;
import de.enough.polish.util.TimePoint;


import junit.framework.TestCase;

public class EventRepeatRuleTest extends TestCase {

	public static void main(String[] args) throws IOException {
		EventRepeatRuleTest test = new EventRepeatRuleTest();
		test.testNextDate();
	}

	public void testNextDate() throws IOException {
		CalendarCategory nationHolidayCategory = new CalendarCategory("National Holidays", "0;2");
		CalendarEntry entry = new CalendarEntry("St. Patricks Day", nationHolidayCategory,  2010, Calendar.MARCH, 17);
		
		TimePoint from = new TimePoint( 2010, Calendar.JANUARY, 1);
		TimePoint to = new TimePoint( 2020, Calendar.JANUARY, 1);
		TimePeriod period = new TimePeriod( from, false, to, true );
		assertTrue( from == period.getStart() );

		EventRepeatRule rule = new EventRepeatRule(EventRepeatRule.INTERVAL_YEARLY);
		TimePoint received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 17, received.getDay());
		assertEquals( Calendar.MARCH, received.getMonth());
		assertEquals( 2010, received.getYear());
		assertTrue( from == period.getStart() );
		
		rule = EventRepeatRule.RULE_YEARLY;
		assertNotNull( received );
		assertEquals( 17, received.getDay());
		assertEquals( Calendar.MARCH, received.getMonth());
		assertEquals( 2010, received.getYear());
		
		assertTrue( from == period.getStart() );
		from.setMonth( Calendar.APRIL);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 17, received.getDay());
		assertEquals( Calendar.MARCH, received.getMonth());
		assertEquals( 2011, received.getYear());

		assertTrue( from == period.getStart() );
		
		period.setStart( received );
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 17, received.getDay());
		assertEquals( Calendar.MARCH, received.getMonth());
		assertEquals( 2012, received.getYear());
		period.setStart( from );

		assertTrue( from == period.getStart() );
		entry = new CalendarEntry("Martin Luther King Jr. Day", nationHolidayCategory,  2010, Calendar.JANUARY, 18);
		rule = new EventRepeatRule(Calendar.MONDAY, 3);
		from.setDate( 2010, Calendar.JANUARY, 1 );
		assertTrue( from == period.getStart() );
		
		
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.JANUARY, received.getMonth());
		assertEquals( 18, received.getDay());
		
		from.setMonth(  Calendar.FEBRUARY );
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 17, received.getDay());
		assertEquals( Calendar.JANUARY, received.getMonth());
		assertEquals( 2011, received.getYear());

		from.setMonth(  Calendar.JANUARY );
		from.setDay(  18);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 17, received.getDay());
		assertEquals( Calendar.JANUARY, received.getMonth());
		assertEquals( 2011, received.getYear());
		
		period.setStart( received );
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 16, received.getDay());
		assertEquals( Calendar.JANUARY, received.getMonth());
		assertEquals( 2012, received.getYear());
		period.setStart(from);
		
		
		from.setYear(  2010);
		from.setMonth(  Calendar.JANUARY );
		from.setDay(  1);
		entry = new CalendarEntry("Memorial Day", nationHolidayCategory,  2010, Calendar.MAY, 31);
		rule = new EventRepeatRule(Calendar.MONDAY, -1);

		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 31, received.getDay());
		assertEquals( Calendar.MAY, received.getMonth());
		assertEquals( 2010, received.getYear());
		
		from.setMonth(  Calendar.JUNE );
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 30, received.getDay());
		assertEquals( Calendar.MAY, received.getMonth());
		assertEquals( 2011, received.getYear());
		
		period.setStart( received );
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 28, received.getDay());
		assertEquals( Calendar.MAY, received.getMonth());
		assertEquals( 2012, received.getYear());
		period.setStart(from);
		
		entry = new CalendarEntry("New Years Eve", nationHolidayCategory,  2010, Calendar.DECEMBER, 31);		
		entry.setRepeat( EventRepeatRule.RULE_YEARLY );
		
		from.setYear(  2011);
		from.setMonth(  Calendar.JANUARY );
		from.setDay(  1);
		from.setHour(  0 );
		
		received = entry.getRepeat().getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( Calendar.DECEMBER, received.getMonth());
		assertEquals( 31, received.getDay());
		assertEquals( 2011, received.getYear());
		
		from.setYear(  2010);
		received = entry.getRepeat().getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( Calendar.DECEMBER, received.getMonth());
		assertEquals( 31, received.getDay());
		assertEquals( 2010, received.getYear());
		
		entry = new CalendarEntry("New Years Day", nationHolidayCategory,  2010, Calendar.JANUARY, 1);		
		entry.setRepeat( EventRepeatRule.RULE_YEARLY );
		
		from.setYear(  2011);
		from.setMonth(  Calendar.JANUARY );
		from.setDay(  1);
		from.setHour(  0 );
		
		received = entry.getRepeat().getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( Calendar.JANUARY, received.getMonth());
		assertEquals( 1, received.getDay());
		assertEquals( 2012, received.getYear());
		
		from.setYear(  2010);
		received = entry.getRepeat().getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( Calendar.JANUARY, received.getMonth());
		assertEquals( 1, received.getDay());
		assertEquals( 2011, received.getYear());

		
		entry = new CalendarEntry("St. Patricks Day3", nationHolidayCategory,  2010, Calendar.JANUARY, 1);		
		entry.setRepeat( new EventRepeatRule( Calendar.MONDAY, 1));
		
		from.setYear(  2011);
		from.setMonth(  Calendar.JANUARY );
		from.setDay(  1);
		from.setHour(  0 );
		
		received = entry.getRepeat().getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( Calendar.JANUARY, received.getMonth());
		assertEquals( 2012, received.getYear());
		assertEquals( 2, received.getDay());
		
		entry = new CalendarEntry("New Years Eve", nationHolidayCategory,  2010, Calendar.DECEMBER, 31);		
		entry.setRepeat( EventRepeatRule.RULE_YEARLY );
		
		from.setYear(  2009);
		from.setMonth(  Calendar.JANUARY );
		from.setDay(  1);
		from.setHour(  0 );
		to.setDate(2010, Calendar.JANUARY, 1);
		
		
		received = entry.getRepeat().getNextDate(entry, period );
		assertNull( received );
		
		entry = new CalendarEntry("New Years Eve", nationHolidayCategory,  2011, Calendar.DECEMBER, 31);		
		entry.setRepeat( EventRepeatRule.RULE_YEARLY );
		received = entry.getRepeat().getNextDate(entry, period );
		assertNull( received );
	}
	
	public void testFirstMondayOfMonth() {
		CalendarCategory nationHolidayCategory = new CalendarCategory("Business", "0;2");
		CalendarEntry entry = new CalendarEntry("Meeting", nationHolidayCategory,  2009, Calendar.DECEMBER, 17);
		
		TimePoint from = new TimePoint( 2010, Calendar.JANUARY, 1);
		TimePoint to = new TimePoint( 2020, Calendar.JANUARY, 1);
		TimePeriod period = new TimePeriod( from, false, to, true );

		EventRepeatRule rule = new EventRepeatRule(EventRepeatRule.INTERVAL_MONTHLY, Calendar.MONDAY, 1);
		TimePoint received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.JANUARY, received.getMonth());
		assertEquals( 4, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.FEBRUARY, received.getMonth());
		assertEquals( 1, received.getDay());
		
		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.MARCH, received.getMonth());
		assertEquals( 1, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.APRIL, received.getMonth());
		assertEquals( 5, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.MAY, received.getMonth());
		assertEquals( 3, received.getDay());
		
		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.JUNE, received.getMonth());
		assertEquals( 7, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.JULY, received.getMonth());
		assertEquals( 5, received.getDay());
		
		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.AUGUST, received.getMonth());
		assertEquals( 2, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.SEPTEMBER, received.getMonth());
		assertEquals( 6, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.OCTOBER, received.getMonth());
		assertEquals( 4, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.NOVEMBER, received.getMonth());
		assertEquals( 1, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.DECEMBER, received.getMonth());
		assertEquals( 6, received.getDay());
		
		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2011, received.getYear());
		assertEquals( Calendar.JANUARY, received.getMonth());
		assertEquals( 3, received.getDay());


	}
	
	
	public void testLastMondayOfMonth() {
		CalendarCategory nationHolidayCategory = new CalendarCategory("Business", "0;2");
		CalendarEntry entry = new CalendarEntry("Meeting", nationHolidayCategory,  2009, Calendar.DECEMBER, 17);
		
		TimePoint from = new TimePoint( 2010, Calendar.JANUARY, 1);
		TimePoint to = new TimePoint( 2020, Calendar.JANUARY, 1);
		TimePeriod period = new TimePeriod( from, false, to, true );

		EventRepeatRule rule = new EventRepeatRule(EventRepeatRule.INTERVAL_MONTHLY, Calendar.MONDAY, -1);
		TimePoint received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.JANUARY, received.getMonth());
		assertEquals( 25, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.FEBRUARY, received.getMonth());
		assertEquals( 22, received.getDay());
		
		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.MARCH, received.getMonth());
		assertEquals( 29, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.APRIL, received.getMonth());
		assertEquals( 26, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.MAY, received.getMonth());
		assertEquals( 31, received.getDay());
		
		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.JUNE, received.getMonth());
		assertEquals( 28, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.JULY, received.getMonth());
		assertEquals( 26, received.getDay());
		
		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.AUGUST, received.getMonth());
		assertEquals( 30, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.SEPTEMBER, received.getMonth());
		assertEquals( 27, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.OCTOBER, received.getMonth());
		assertEquals( 25, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.NOVEMBER, received.getMonth());
		assertEquals( 29, received.getDay());

		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2010, received.getYear());
		assertEquals( Calendar.DECEMBER, received.getMonth());
		assertEquals( 27, received.getDay());
		
		period.setStart(received);
		received = rule.getNextDate(entry, period );
		assertNotNull( received );
		assertEquals( 2011, received.getYear());
		assertEquals( Calendar.JANUARY, received.getMonth());
		assertEquals( 31, received.getDay());


	}

}
