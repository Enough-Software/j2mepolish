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


import junit.framework.TestCase;

public class CalendarEntryTest extends TestCase {

	public static void main(String[] args) throws IOException {
		CalendarEntryTest test = new CalendarEntryTest();
		test.testSerialization();
	}

	public void testSerialization() throws IOException {
		CalendarCategory nationHolidayCategory = new CalendarCategory("National Holidays", "0;2");
		CalendarEntry entry = new CalendarEntry("St. Patricks Day", nationHolidayCategory,  2009, Calendar.MARCH, 17);
		
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOut);
		entry.write(out);
		
		ByteArrayInputStream byteIn = new ByteArrayInputStream( byteOut.toByteArray() );
		DataInputStream in = new DataInputStream(byteIn);
		
		CalendarEntry read = new CalendarEntry();
		read.read(in);
		
		assertTrue( read.getCategory() != null );
		assertEquals("0;2", read.getCategory().getId() );
		
		System.out.println("everything's a-ok");
				
	}

	public void testEventRepeatRule() {
		CalendarEntry entry = new CalendarEntry();
		entry.setSummary("test");
		entry.setReoccurence(CalendarEntry.REOCCURENCE_YEARLY);
		EventRepeatRule rule = entry.getRepeat();
		assertNotNull( rule );
		assertEquals( rule.getInterval(), EventRepeatRule.INTERVAL_YEARLY);
	}
}
