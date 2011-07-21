/*
 * Created on Nov 21, 2007 at 2:01:53 AM.
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
package de.enough.polish.preprocess.css;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Nov 21, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssBlockTest extends TestCase
{

	
	public CssBlockTest(String name)
	{
		super(name);
	}
	
	public void testPattern() {
		String input = "test: url( test.png ); top { type: schnick; test2: schnuck; } schnaddel: babbel;";
		Pattern pattern = Pattern.compile( CssBlock.ATTRIBUTE_NAME_STR );
		Matcher matcher = pattern.matcher( input );
		assertEquals( true, matcher.find() );
		assertEquals("test", matcher.group());
		String patternStr = "\\:\\s*" + CssBlock.ATTRIBUTE_VALUE_STR + "\\s*\\;?";
		pattern = Pattern.compile( patternStr );
		matcher = pattern.matcher( input );
		assertEquals( true, matcher.find() );
		assertEquals(": url( test.png );", matcher.group());
		
		matcher = pattern.matcher( "test: true" );
		assertEquals( true, matcher.find() );
		assertEquals(": true", matcher.group());
		
		matcher = CssBlock.INNER_BLOCK_PATTERN.matcher(input);
		assertTrue( matcher.find() );
		assertEquals( "top { type: schnick; test2: schnuck; }", matcher.group()  );
		
		matcher = pattern.matcher( "test: dadel-du" );
		assertEquals( true, matcher.find() );
		assertEquals(": dadel-du", matcher.group());
		
		input = "test: url( test.png ); top { type: schnick-schnack; test2: schnuck; } schnaddel: babbel;";
		matcher = CssBlock.INNER_BLOCK_PATTERN.matcher(input);
		assertTrue( matcher.find() );
		assertEquals( "top { type: schnick-schnack; test2: schnuck; }", matcher.group()  );

		patternStr = CssBlock.ATTRIBUTE_NAME_STR + "\\s*\\:\\s*" + CssBlock.ATTRIBUTE_VALUE_STR + "\\s*\\;?";
		pattern = Pattern.compile(patternStr);
		matcher = pattern.matcher( input );
		assertEquals( true, matcher.find() );
		assertEquals( "test: url( test.png );", matcher.group() );

		patternStr = "\\{\\s*(\\s*" + CssBlock.ATTRIBUTE_NAME_STR + "\\s*\\:\\s*" + CssBlock.ATTRIBUTE_VALUE_STR + "\\s*\\;?\\s*)+\\s*\\}";
		pattern = Pattern.compile(patternStr);
		matcher = pattern.matcher( input );
		assertEquals( true, matcher.find() );
		assertEquals( "{ type: schnick-schnack; test2: schnuck; }", matcher.group() );
		
		input = ".browserInput extends .browserText {      inherit: false; padding: 2;     layout: expand | left;  focused-style: browserInputFocused;     border {                type: round-rect;               width: 1;color: browserFontColor; }}";
		matcher = CssBlock.INNER_BLOCK_PATTERN.matcher(input);
		assertTrue( matcher.find() );
		assertEquals("border {                type: round-rect;               width: 1;color: browserFontColor; }", matcher.group() );

		input = ".browserInput extends .browserText {   margin: 0;      padding-top: 3; padding: 2;     background {            type: vertical-gradient;                top-color: #ccc;                bottom-color: #fff;             start: 10%;               end: 90%;       }}";
		matcher = CssBlock.INNER_BLOCK_PATTERN.matcher(input);
		assertTrue( matcher.find() );
		assertEquals("background {            type: vertical-gradient;                top-color: #ccc;                bottom-color: #fff;             start: 10%;               end: 90%;       }", matcher.group() );
		
		input = ".itemMain:hover { font-color: white;      font-style: bold;       background {            type: round-rect;               color: #150020; }                       text-color: white;                                                      text-effect: split;        text-split-bottom-color: white; text-split-split-pos: 50%;      text-split-split-pos-animation {                on: focus-down;         duration: 600ms;                range: 0%..100%;        }       text-split-split-pos-animation {           on: focus-up;           duration: 600ms;                range: 100%..0%;        }               text-color-animation {          on: focus-down;         duration: 200ms;                range: white..red;              function: linear; }}";
		new CssBlock(input);
	}

}
