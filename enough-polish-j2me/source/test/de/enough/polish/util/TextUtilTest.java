/*
 * Created on 13-Dec-2005 at 16:20:01.
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

import junit.framework.TestCase;

public class TextUtilTest extends TestCase {

	public TextUtilTest(String name) {
		super(name);
	}
	
	public static void testReplaceFirst() {
		String input;
		String search;
		String replacement;
		
		input = "hello123world";
		search = "123";
		replacement = " ";
		assertEquals( "hello world", TextUtil.replaceFirst(input, search, replacement));
		
		input = "hello123world123";
		search = "123";
		replacement = " ";
		assertEquals( "hello world123", TextUtil.replaceFirst(input, search, replacement));
		
		input = "123hello world";
		search = "123";
		replacement = "";
		assertEquals( "hello world", TextUtil.replaceFirst(input, search, replacement));

		input = "123hello world";
		search = "123";
		replacement = "comonohbabycommon";
		assertEquals( "comonohbabycommonhello world", TextUtil.replaceFirst(input, search, replacement));
	}
	
	public static void testReplaceLast() {
		String input;
		String search;
		String replacement;
		
		input = "hello123world";
		search = "123";
		replacement = " ";
		assertEquals( "hello world", TextUtil.replaceLast(input, search, replacement));
		
		input = "hello123world123";
		search = "123";
		replacement = " ";
		assertEquals( "hello123world ", TextUtil.replaceLast(input, search, replacement));
		
		input = "123hello world";
		search = "123";
		replacement = "";
		assertEquals( "hello world", TextUtil.replaceLast(input, search, replacement));

		input = "123hello world123";
		search = "123";
		replacement = "";
		assertEquals( "123hello world", TextUtil.replaceLast(input, search, replacement));

		input = "123hello world";
		search = "123";
		replacement = "comonohbabycommon";
		assertEquals( "comonohbabycommonhello world", TextUtil.replaceLast(input, search, replacement));

		input = "123hello world123";
		search = "123";
		replacement = "comonohbabycommon";
		assertEquals( "123hello worldcomonohbabycommon", TextUtil.replaceLast(input, search, replacement));
	}

	
	public static void testReplace() {
		String input;
		String search;
		String replacement;
		
		input = "hello123world";
		search = "123";
		replacement = " ";
		assertEquals( "hello world", TextUtil.replace(input, search, replacement));
		
		input = "hello123world123";
		search = "123";
		replacement = " ";
		assertEquals( "hello world ", TextUtil.replace(input, search, replacement));
		
		input = "123hello world";
		search = "123";
		replacement = "";
		assertEquals( "hello world", TextUtil.replace(input, search, replacement));

		input = "123hello world123";
		search = "123";
		replacement = "";
		assertEquals( "hello world", TextUtil.replace(input, search, replacement));

		input = "123hello world";
		search = "123";
		replacement = "comonohbabycommon";
		assertEquals( "comonohbabycommonhello world", TextUtil.replace(input, search, replacement));

		input = "123hello world123";
		search = "123";
		replacement = "comonohbabycommon";
		assertEquals( "comonohbabycommonhello worldcomonohbabycommon", TextUtil.replace(input, search, replacement));
		
	}
	
	public void testSplitWithChunks(){
		String input = "one;two;three";
		int number = 3;
		String[] output = TextUtil.split( input, ';', number );
		assertEquals( number, output.length );
		assertEquals( "one", output[0] );
		assertEquals( "two", output[1] );
		assertEquals( "three", output[2] );
		
		
		input = "one;two;three";
		number = 2;
		output = TextUtil.split( input, ';', number );
		assertEquals( number, output.length );
		assertEquals( "one", output[0] );
		assertEquals( "two", output[1] );

		input = "one;two;three";
		number = 1;
		output = TextUtil.split( input, ';', number );
		assertEquals( number, output.length );
		assertEquals( "one", output[0] );

		input = "one;two;three";
		number = 4;
		output = TextUtil.split( input, ';', number );
		assertEquals( number, output.length );
		assertEquals( "one", output[0] );
		assertEquals( "two", output[1] );
		assertEquals( "three", output[2] );
		assertEquals( null, output[3] );
	} 
	
	public void testSplit() {
		String input;
		String[] output;

		input = "This was John's testimony when the Jewish leaders sent priests and Temple assistants from Jerusalem to ask  John, ";
		output = TextUtil.split( input, ' ' );
		assertEquals( 20, output.length );
		assertEquals( "This", output[0] );
		assertEquals( "was", output[1] );
		assertEquals( "John's", output[2] );
		assertEquals( "", output[17] );
		assertEquals( "John,", output[18] );
		assertEquals( "", output[19] );
		
		input = "This was John's testimony when the Jewish leaders sent priests and Temple assistants from Jerusalem to ask John,";
		output = TextUtil.split( input, ' ' );
		assertEquals( 18, output.length );
		assertEquals( "This", output[0] );
		assertEquals( "was", output[1] );
		assertEquals( "John's", output[2] );
		assertEquals( "John,", output[17] );
	}
	
	
		
	public void testReverseForRtlLanguage() {
		String input = "ŠšŸ 123 ŠšŸ";
		String reversed = TextUtil.reverseForRtlLanguage(input);
		System.out.println(input);
		System.out.println(reversed);
		
		input = "ŠšŸ (123 hallo) ŠšŸ, .šŸšŸ";
		reversed = TextUtil.reverseForRtlLanguage(input);
		System.out.println(input);
		System.out.println(reversed);
	}
	
	public void testResolveNamedHtmlEntity() {
		assertEquals( '&', TextUtil.resolveNamedHtmlEntity("amp"));
		assertEquals( '"', TextUtil.resolveNamedHtmlEntity("quot"));
		assertEquals( (char)34, TextUtil.resolveNamedHtmlEntity("quot"));
		assertEquals( (char)189, TextUtil.resolveNamedHtmlEntity("frac12"));
		assertEquals( 'ä', TextUtil.resolveNamedHtmlEntity("auml"));
		assertEquals( 'Ä', TextUtil.resolveNamedHtmlEntity("Auml"));
		assertEquals( '>', TextUtil.resolveNamedHtmlEntity("gt"));
		assertEquals( '<', TextUtil.resolveNamedHtmlEntity("lt"));
	}
	
	public void testUnescapeHtmlEntities() {
		assertEquals( "<variable name=\"test\" />", TextUtil.unescapeHtmlEntities("&lt;variable name=&quot;test&quot; /&gt;"));
		assertEquals( "<variable name=\"test\" />", TextUtil.unescapeHtmlEntities("&#60;variable name=&#34;test&#34; /&#62;"));
		assertEquals( "19 <= 20 && 19 > 0", TextUtil.unescapeHtmlEntities("19 &lt;= 20 &amp;&amp; 19 &gt; 0"));
		assertEquals( "19 <= 20 && 19 > 0", TextUtil.unescapeHtmlEntities("19 &#60;= 20 &#38;&#38; 19 &#62; 0"));
	}
}
