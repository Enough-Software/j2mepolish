/*
 * Created on Feb 28, 2007 at 4:03:51 AM.
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
package de.enough.polish.propertyfunctions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * <p>Tests the calculate property function</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Feb 28, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CalculateFunctionTest extends TestCase {


	public CalculateFunctionTest(String name) {
		super(name);
	}
	
	
	public void testCalculate() {
		PropertyFunction calculate = new CalculateFunction();
		String output = calculate.process("16 + 1", null, null);
		assertEquals("17", output );
		
		output = calculate.process("16+1", null, null);
		assertEquals("17", output );
		
		output = calculate.process("16 + 1 + 1", null, null);
		assertEquals("18", output );

		output = calculate.process("-16 + 1 + 1", null, null);
		assertEquals("-14", output );
		
		output = calculate.process("16 - 1", null, null);
		assertEquals("15", output );

		output = calculate.process("16-1", null, null);
		assertEquals("15", output );

		output = calculate.process("16+-1", null, null);
		assertEquals("15", output );

		System.out.println("Term part pattern=" + CalculateFunction.TERM_PART_STR );
		String input = "19 - 10 * 4";
		Matcher matcher = Pattern.compile( CalculateFunction.TERM_PART_STR ).matcher(input);
		while (matcher.find()) {
			System.out.println("group=" + matcher.group() );
		}

		
		System.out.println("Term pattern=" + CalculateFunction.TERM_STR );
		input = "(19 - 10 * 4) + (19 - 8 / 2 * 4) + 4";
		matcher = CalculateFunction.TERM_PATTERN.matcher(input);
		while (matcher.find()) {
			System.out.println("group=" + matcher.group() );
		}
		
		output = calculate.process("(19 - 10 * 4) + (19 - 8 / 2 * 4) + 4", null, null );
		assertEquals( "60", output );

	}

}
