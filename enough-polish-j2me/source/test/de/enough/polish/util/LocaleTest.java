/*
 * Created on Feb 6, 2010 at 6:12:14 PM.
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

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LocaleTest extends TestCase {

	/**
	 * 
	 */
	public LocaleTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public LocaleTest(String arg0) {
		super(arg0);
	}
	
	public void testFormatDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2010);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 6);
		calendar.set(Calendar.HOUR_OF_DAY, 10);
		calendar.set(Calendar.MINUTE, 59);
		String pattern;
		String date;
		
		pattern = "dd.MM.yyyy";
		date = Locale.formatDate(calendar, pattern);
		assertEquals("06.01.2010", date);
		
		pattern = "yyyy-MM-dd";
		date = Locale.formatDate(calendar, pattern);
		assertEquals("2010-01-06", date);

		Locale.setMonthNames(new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"} );
		pattern = "yyyy-MMMMM-dd";
		date = Locale.formatDate(calendar, pattern);
		assertEquals("2010-January-06", date);
		
		pattern =  "MMMMM dd, yyyy";
		date = Locale.formatDate(calendar, pattern);
		assertEquals("January 06, 2010", date);

		pattern = "day=dd, month=MM, year=yyyy";
		date = Locale.formatDate(calendar, pattern);
		assertEquals("day=06, month=01, year=2010", date);

	}
	
	public static void main(String[] args) {
		LocaleTest test = new LocaleTest();
		test.testFormatDate();
	}
}
