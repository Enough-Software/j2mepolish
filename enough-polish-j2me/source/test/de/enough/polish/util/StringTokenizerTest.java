/*
 * Created on Mar 1, 2006 at 12:56:16 PM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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

import java.util.NoSuchElementException;

import junit.framework.TestCase;

public class StringTokenizerTest extends TestCase {


	public StringTokenizerTest(String name) {
		super(name);
	}
	
	public void testCountTokens() {
		String input = "one;two;three";
		StringTokenizer tokenizer = new StringTokenizer( input, ';' );
		java.util.StringTokenizer j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.countTokens(), tokenizer.countTokens() );
		
		input = "one;two;three;four";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.countTokens(), tokenizer.countTokens() );

		input = "one";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.countTokens(), tokenizer.countTokens() );
		
		input = "one;two;three;four;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.countTokens(), tokenizer.countTokens() );


		input = ";";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.countTokens(), tokenizer.countTokens() );

		input = ";;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.countTokens(), tokenizer.countTokens() );

		input = ";;;;;;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.countTokens(), tokenizer.countTokens() );

		input = ";a";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.countTokens(), tokenizer.countTokens() );

		input = ";a;;;b;;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.countTokens(), tokenizer.countTokens() );

	
		input = "a;;;;;;b";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.countTokens(), tokenizer.countTokens() );

		input = "a;;;;;;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.countTokens(), tokenizer.countTokens() );

		input = ";;;;;;b";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.countTokens(), tokenizer.countTokens() );

		input = ";;;sdasdas;;;b";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.countTokens(), tokenizer.countTokens() );

		input = "asdsdasds;;;sdasdas;;;basdasds";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.countTokens(), tokenizer.countTokens() );
}

	public void testHasMoreTokens() {
		String input = "one;two;three";
		StringTokenizer tokenizer = new StringTokenizer( input, ';' );
		java.util.StringTokenizer j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertFalse( j2seTokenizer.hasMoreTokens() );
		assertFalse( tokenizer.hasMoreTokens() );
		
		input = "one;two;three;four";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		j2seTokenizer.nextToken();
		tokenizer.nextToken();
		assertFalse( j2seTokenizer.hasMoreTokens() );
		assertFalse( tokenizer.hasMoreTokens() );

		input = "one;two;three;four;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertFalse( j2seTokenizer.hasMoreTokens() );
		assertFalse( tokenizer.hasMoreTokens() );

		input = "one;two;three;four;;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertFalse( j2seTokenizer.hasMoreTokens() );
		assertFalse( tokenizer.hasMoreTokens() );

		input = ";;one;two;three;four;;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertFalse( j2seTokenizer.hasMoreTokens() );
		assertFalse( tokenizer.hasMoreTokens() );

		input = "one;two;three;four;;;;;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertFalse( j2seTokenizer.hasMoreTokens() );
		assertFalse( tokenizer.hasMoreTokens() );

		input = "one;;two;;three;;four;;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		tokenizer.nextToken();
		j2seTokenizer.nextToken();
		assertFalse( j2seTokenizer.hasMoreTokens() );
		assertFalse( tokenizer.hasMoreTokens() );

		input = "one";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		j2seTokenizer.nextToken();
		tokenizer.nextToken();
		assertFalse( j2seTokenizer.hasMoreTokens() );
		assertFalse( tokenizer.hasMoreTokens() );

		input = ";one";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		j2seTokenizer.nextToken();
		tokenizer.nextToken();
		assertFalse( j2seTokenizer.hasMoreTokens() );
		assertFalse( tokenizer.hasMoreTokens() );

		input = "one;;;;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertTrue( j2seTokenizer.hasMoreTokens() );
		assertTrue( tokenizer.hasMoreTokens() );
		j2seTokenizer.nextToken();
		tokenizer.nextToken();
		assertFalse( j2seTokenizer.hasMoreTokens() );
		assertFalse( tokenizer.hasMoreTokens() );

		input = ";";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertFalse( j2seTokenizer.hasMoreTokens() );
		assertFalse( tokenizer.hasMoreTokens() );

	
		input = ";;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertFalse( j2seTokenizer.hasMoreTokens() );
		assertFalse( tokenizer.hasMoreTokens() );

		input = ";;;;;;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertFalse( j2seTokenizer.hasMoreTokens() );
		assertFalse( tokenizer.hasMoreTokens() );
	}

	public void testNextToken() {
		String input = "one;two;three";
		StringTokenizer tokenizer = new StringTokenizer( input, ';' );
		java.util.StringTokenizer j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		try {
			j2seTokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}
		try {
			tokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}
		
		input = "one;two;three;four";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		try {
			j2seTokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}
		try {
			tokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}

		input = "one;two;three;four;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		try {
			j2seTokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}
		try {
			tokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}
		
		input = ";;one;;;;two;three;four;;;;;;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		try {
			j2seTokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}
		try {
			tokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}

		input = "one";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		try {
			j2seTokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}
		try {
			tokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}

		input = ";one";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		try {
			j2seTokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}
		try {
			tokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}

		input = ";one;;;;";
		tokenizer = new StringTokenizer( input, ';' );
		j2seTokenizer = new java.util.StringTokenizer( input, ";", false ); 
		assertEquals( j2seTokenizer.nextToken(), tokenizer.nextToken() );
		try {
			j2seTokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}
		try {
			tokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}

		input = ";";
		tokenizer = new StringTokenizer( input, ';' );
		try {
			j2seTokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}
		try {
			tokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}

		input = ";;;;;;";
		tokenizer = new StringTokenizer( input, ';' );
		try {
			j2seTokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}
		try {
			tokenizer.nextToken();
			fail("NoSuchElementException expected.");
		} catch (NoSuchElementException e) {
			// expected
		}
}
}
