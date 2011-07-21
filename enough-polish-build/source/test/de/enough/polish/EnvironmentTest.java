/*
 * Created on 25-Apr-2005 at 11:05:20.
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
package de.enough.polish;

import java.util.regex.Matcher;

import junit.framework.TestCase;

/**
 * <p>Tests the property repleacement mechanisms of the the Environment class.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        25-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class EnvironmentTest extends TestCase {


	public EnvironmentTest(String name) {
		super(name);
	}
	
	public void testPropertyPattern() {
		String input = "${ polish.BitsPerColor }";
		Matcher matcher = Environment.PROPERTY_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "${ polish.BitsPerColor }", matcher.group() );
		
		input = "somestuffahead that is not important at all ${polish.BitsPerColor}";
		matcher = Environment.PROPERTY_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "${polish.BitsPerColor}", matcher.group() );

		input = "somestuffahead that is not important at all ${ uppercase( ${polish.BitsPerColor} )}";
		matcher = Environment.PROPERTY_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "${polish.BitsPerColor}", matcher.group() );

		input = "somestuffahead that is not important at all ${ uppercase( polish.BitsPerColor ) }";
		matcher = Environment.PROPERTY_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "${ uppercase( polish.BitsPerColor ) }", matcher.group() );

		input = "somestuffahead that is not important at all ${ lowercase( uppercase( polish.BitsPerColor ) ) }";
		matcher = Environment.PROPERTY_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "${ lowercase( uppercase( polish.BitsPerColor ) ) }", matcher.group() );

		input = "somestuffahead that is not important at all ${lowercase(uppercase(polish.BitsPerColor))}";
		matcher = Environment.PROPERTY_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "${lowercase(uppercase(polish.BitsPerColor))}", matcher.group() );
		
		input = "somestuffahead that is not important at all ${ bytes( 100 kb ) }";
		matcher = Environment.PROPERTY_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "${ bytes( 100 kb ) }", matcher.group() );

		input = "]${ replace(polish.identifier,A,\\u0020) }";
		matcher = Environment.PROPERTY_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "${ replace(polish.identifier,A,\\u0020) }", matcher.group() );
	}
	
	public void testFunctionPattern() {
		String input = "${ polish.BitsPerColor }";
		Matcher matcher = Environment.FUNCTION_PATTERN.matcher( input );
		assertFalse( matcher.find() );
		
		input = "${polish.BitsPerColor}";
		matcher = Environment.FUNCTION_PATTERN.matcher( input );
		assertFalse( matcher.find() );

		input = "${ uppercase( polish.BitsPerColor ) }";
		matcher = Environment.FUNCTION_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "uppercase( polish.BitsPerColor )", matcher.group() );

		input = "${ lowercase( uppercase( polish.BitsPerColor ) )";
		matcher = Environment.FUNCTION_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "uppercase( polish.BitsPerColor )", matcher.group() );

		input = "${lowercase(uppercase(polish.BitsPerColor))}";
		matcher = Environment.FUNCTION_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "uppercase(polish.BitsPerColor)", matcher.group() );

		input = "${ bytes( 100 kb ) }";
		matcher = Environment.FUNCTION_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "bytes( 100 kb )", matcher.group() );
		
		input = "imagewidth(spk.png) + 3";
		matcher = Environment.FUNCTION_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "imagewidth(spk.png)", matcher.group() );
		
		
		input = "calculate( imagewidth(spk.png) + 3 )";
		matcher = Environment.FUNCTION_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "imagewidth(spk.png)", matcher.group() );

		input = "calculate( 16 + 3 )";
		matcher = Environment.FUNCTION_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "calculate( 16 + 3 )", matcher.group() );

		input = "calculate( 16 + calculate(3/5) )";
		matcher = Environment.FUNCTION_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "calculate(3/5)", matcher.group() );
		
		input = "]${ replace(polish.identifier,A,\\u0020) }[";
		matcher = Environment.FUNCTION_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals( "replace(polish.identifier,A,\\u0020)", matcher.group() );
	}

	

}
