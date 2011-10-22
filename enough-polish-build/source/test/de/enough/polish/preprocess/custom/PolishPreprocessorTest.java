/*
 * Created on 11-Jan-2005 at 18:59:03.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.preprocess.custom;

import java.util.regex.Matcher;

import de.enough.polish.util.StringList;
import junit.framework.TestCase;

/**
 * <p>Tests the polish preprocessor.</p>
 *
 * <p>copyright Enough Software 2005</p>
 * <pre>
 * history
 *        11-Jan-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PolishPreprocessorTest extends TestCase {

	
	public PolishPreprocessorTest(String name) {
		super(name);
	}
		
	
	
	public void testSetCurrentAlertDisplayable() {
		
		String input = null; // "		display.setCurrent( alert, screen );";
		Matcher matcher = null; ///PolishPreprocessor.SET_CURRENT_ALERT_DISPLAYABLE_PATTERN.matcher( input ); 
//		assertTrue( matcher.find() );
//		assertEquals( "display.setCurrent( alert, screen )", matcher.group() );
		
//		input = "		this.display.setCurrent( this.alert, this.screen );";
//		matcher = PolishPreprocessor.SET_CURRENT_ALERT_DISPLAYABLE_PATTERN.matcher( input ); 
//		assertTrue( matcher.find() );
//		assertEquals( "this.display.setCurrent( this.alert, this.screen )", matcher.group() );
//
//		input = "		display . setCurrent ( this.alert , this.screen );";
//		matcher = PolishPreprocessor.SET_CURRENT_ALERT_DISPLAYABLE_PATTERN.matcher( input ); 
//		assertTrue( matcher.find() );
//		assertEquals( "display . setCurrent ( this.alert , this.screen )", matcher.group() );
	
		PolishPreprocessor preprocessor = new PolishPreprocessor();
		preprocessor.isUsingPolishGui = true;
		String[] lines = new String[] {
				"	public void testMethod() {",
				"		this.display.setCurrent( this.alert, this.screen );",
				"		display.setCurrent( alert, item );",
				"		display.setCurrent(alert,screen);",
				"	}"
		};
		StringList list = new StringList( lines );
		
		preprocessor.processClass(list, "TestClass");
		
		lines = list.getArray();
		
//		assertEquals(  "		this.item.show( this.display );", lines[1] );
//		assertEquals(  "		item.show( display );", lines[2] );
//		assertEquals(  "		getMyItem().show( display );", lines[3] );
	}
	
	
	public void testIsPrimitiveArray() {
		String test;
		
		test = "int[]";
		assertTrue( PolishPreprocessor.isPrimitiveArray(test));
		test = "int []";
		assertTrue( PolishPreprocessor.isPrimitiveArray(test));
		test = "long[]";
		assertTrue( PolishPreprocessor.isPrimitiveArray(test));
		test = "long []";
		assertTrue( PolishPreprocessor.isPrimitiveArray(test));
		test = "short[]";
		assertTrue( PolishPreprocessor.isPrimitiveArray(test));
		test = "short [ ]";
		assertTrue( PolishPreprocessor.isPrimitiveArray(test));
		test = "byte    [ ]";
		assertTrue( PolishPreprocessor.isPrimitiveArray(test));
		test = "byte []";
		assertTrue( PolishPreprocessor.isPrimitiveArray(test));
		test = "boolean[]";
		assertTrue( PolishPreprocessor.isPrimitiveArray(test));
		test = "boolean []";
		assertTrue( PolishPreprocessor.isPrimitiveArray(test));
		test = "double[]";
		assertTrue( PolishPreprocessor.isPrimitiveArray(test));
		test = "double []";
		assertTrue( PolishPreprocessor.isPrimitiveArray(test));
		test = "float[]";
		assertTrue( PolishPreprocessor.isPrimitiveArray(test));
		test = "float []";
		assertTrue( PolishPreprocessor.isPrimitiveArray(test));
	}
	
}
