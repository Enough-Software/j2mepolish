/*
 * Created on Jul 8, 2010 at 10:45:31 PM.
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

import java.util.Calendar;

import de.enough.polish.util.TimePoint;

import junit.framework.TestCase;

public class CalendarEntryListTest extends TestCase {

	public CalendarEntryListTest() {
		this(null);
	}

	public CalendarEntryListTest(String name) {
		super(name);
	}
	
	public void testSortEntries() {
		System.out.println("++ sort entries ++");
		CalendarEntryList list = new CalendarEntryList();
		assertEquals(0, list.getEntries().length);
		
		CalendarCategory root = new CalendarCategory("test");
		CalendarEntry entry11 = new CalendarEntry("St. Patricks Day", root,  2011, Calendar.MARCH, 1);
		list.add(entry11);
		CalendarEntry entry12 = new CalendarEntry("St. Patricks Day", root,  2011, Calendar.JANUARY, 17);
		list.add(entry12);
		CalendarEntry entry13 = new CalendarEntry("St. Patricks Day", root,  2012, Calendar.JANUARY, 2);
		list.add(entry13);
		CalendarEntry entry1 = new CalendarEntry("St. Patricks Day", root,  2010, Calendar.MARCH, 17);
		list.add(entry1);
		CalendarEntry entry2 = new CalendarEntry("Before", root,  2010, Calendar.MARCH, 17);
		list.add(entry2);
		CalendarEntry entry3 = new CalendarEntry("Aaa3", root,  2010, Calendar.MARCH, 30);
		list.add(entry3);
		assertEquals(6, list.getEntries().length);
		
		CalendarEntry entry4 = new CalendarEntry("Aaa1", root,  2010, Calendar.MARCH, 30);
		list.add(entry4);
		CalendarEntry entry6 = new CalendarEntry("Aaa", root,  new TimePoint(2010, Calendar.MARCH, 30, 10, 1) );
		list.add(entry6);
		CalendarEntry entry7 = new CalendarEntry("Zzz", root,  new TimePoint(2010, Calendar.MARCH, 30, 10, 0) );
		list.add(entry7);
		CalendarEntry entry5 = new CalendarEntry("Aaa2", root,  2010, Calendar.MARCH, 30);
		list.add(entry5);
		System.out.println("START SORTING...");
		CalendarEntry[] entries = list.getEntries();
		System.out.println("END SORTING...");
		System.out.println("============== ENTRIES START =========================");
		for (int i = 0; i < entries.length; i++) {
			CalendarEntry entry = entries[i];
			System.out.println(entry.getStartDate() + ": " + entry.getSummary());
		}
		System.out.println("============== ENTRIES END =========================");

		assertEquals(10, list.getEntries().length);
		assertEquals( entry2, list.getEntries()[0]);
		assertEquals( entry1, list.getEntries()[1]);
		assertEquals( entry4, list.getEntries()[2]);
		assertEquals( entry5, list.getEntries()[3]);
		assertEquals( entry3, list.getEntries()[4]);
		assertEquals( entry7, list.getEntries()[5]);
		assertEquals( entry6, list.getEntries()[6]);
		assertEquals( entry12, list.getEntries()[7]);
		assertEquals( entry11, list.getEntries()[8]);
		assertEquals( entry13, list.getEntries()[9]);
		
		System.out.println("++ end sort entries ++");

	}
	
	public void testGetEntriesForDay() {
		CalendarEntryList list = new CalendarEntryList();
		assertEquals(0, list.getEntries().length);
		
		CalendarCategory root = new CalendarCategory("test");
		CalendarEntry entry1 = new CalendarEntry("St. Patricks Day", root,  2010, Calendar.MARCH, 17);
		list.add(entry1);
		assertEquals(1, list.getEntries().length);
		CalendarEntry entry2 = new CalendarEntry("Before", root,  2010, Calendar.MARCH, 17);
		list.add(entry2);
		CalendarEntry entry3 = new CalendarEntry("Aaa3", root,  2010, Calendar.MARCH, 30);
		list.add(entry3);
		assertEquals(3, list.getEntries().length);
		
		CalendarEntry entry4 = new CalendarEntry("Aaa1", root,  2010, Calendar.MARCH, 30);
		list.add(entry4);
		CalendarEntry entry6 = new CalendarEntry("Aaa", root,  new TimePoint(2010, Calendar.MARCH, 30, 10, 1) );
		list.add(entry6);
		CalendarEntry entry7 = new CalendarEntry("Zzz", root,  new TimePoint(2010, Calendar.MARCH, 30, 10, 0) );
		list.add(entry7);
		CalendarEntry entry5 = new CalendarEntry("Aaa2", root,  2010, Calendar.MARCH, 30);
		list.add(entry5);
		
		TimePoint day = new TimePoint( 2010, Calendar.MARCH, 30 );
		CalendarEntry[] entries = list.getEntriesForDay(day);
		assertEquals( 5, entries.length );
		assertEquals( entry4, entries[0] );
		assertEquals( entry5, entries[1] );
		assertEquals( entry3, entries[2] );
		assertEquals( entry7, entries[3] );
		assertEquals( entry6, entries[4] );
		
		day.setDay(17);
		entries = list.getEntriesForDay(day);
		assertEquals( 2, entries.length );
		assertEquals( entry2, entries[0] );
		assertEquals( entry1, entries[1] );

		day.setDay(18);
		entries = list.getEntriesForDay(day);
		assertEquals( 0, entries.length );

		day.setDay(16);
		entries = list.getEntriesForDay(day);
		assertEquals( 0, entries.length );

		day.setDay(31);
		entries = list.getEntriesForDay(day);
		assertEquals( 0, entries.length );

		day.setDay(29);
		entries = list.getEntriesForDay(day);
		assertEquals( 0, entries.length );
	}

}
