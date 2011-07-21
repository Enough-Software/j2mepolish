//#condition polish.usePolishGui
/*
 * Copyright (c) 2010 Robert Virkus / Enough Software
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

package de.enough.polish.browser.css;

import java.io.IOException;
import java.util.Hashtable;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;

import junit.framework.TestCase;

/**
 * Tests the CssReader
 * 
 * @author robertvirkus
 */
public class CssInterpreterTest extends TestCase {

	public CssInterpreterTest(String name) {
		super(name);
	}

	public void testNextToken() throws IOException {
		CssInterpreter reader = new CssInterpreter("/****** here are some more comments; }{;;;;} .mystyle{} ******  */\n"
				 + ".myName { color: #fff; background-color: #333\n }\n"
				 + "focused { background        {\ncolor: green;/**** another comment / another test */ image: url(/test.png); type: image              }\nfont-color: blue\n; }"
				 + "title { text-effect: shadow;\n \ntext-shadow-color: green;      }"
		);
		int i = 0;
		String[] tokens = new String[]{
			".myName",
			"color: #fff",
			"background-color: #333",
			"",
			"focused",
			"background",
			"color: green",
			"image: url(/test.png)",
			"type: image",
			"",
			"font-color: blue",
			"",
			"title",
			"text-effect: shadow",
			"text-shadow-color: green",
			""
		};
		StringBuffer strBuffer = new StringBuffer();
		while (reader.hasNextToken()) {
			String token = reader.nextToken( strBuffer );
			assertEquals( tokens[i], token );
			i++;
			if (i > 50) {
				fail("Unable to parse page");
			}
		}
		
		assertNull( reader.nextToken(strBuffer));
	}
	
	public void testNextStyle() throws IOException {
		CssInterpreter reader = new CssInterpreter("/****** here are some more comments; }{;;;;} .mystyle{} ******  */\n"
				 + ".myName { color: #fff; background-color: #333\n }\n"
				 + "focused { background        {\ncolor: rgb( 10, 100 % , 255 );/**** another comment / another test */ image: url(/test.png); type: image              }\nfont-color: rgb( 10, 100 % , 255 )\n; }"
				 + "title { text-effect: shadow;\n \ntext-shadow-color: #0f0;   \n\n font-color:    #33ee22   }"
				 + "test { text-effect: shadow;\n \ntext-shadow-color: #0f0;   \n\n font-color:    green   }"
				 + "test2 { text-effect: shadow;\n \ntext-shadow-color: #0f0;   \n\n font-color:    transparent   }"
		);
		Style style = reader.nextStyle();
		assertEquals( "myname", style.name);
		Color color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( 0xffffff, color.getColor() );
		
		style = reader.nextStyle();
		assertEquals( "focused", style.name);
		color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( (10 << 16) | (255 << 8) | 255, color.getColor() );
		
		style = reader.nextStyle();
		assertEquals( "title", style.name);
		color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( 0x33ee22, color.getColor() );

		style = reader.nextStyle();
		assertEquals( "test", style.name);
		color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( 0x008000, color.getColor() );

		style = reader.nextStyle();
		assertEquals( "test2", style.name);
		color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( Color.TRANSPARENT, color.getColor() );

		style = reader.nextStyle();
		assertNull( style );
	}
	
	
	public void testFocusedStyle() throws IOException {
		CssInterpreter reader = new CssInterpreter("/****** here are some more comments; }{;;;;} .mystyle{} ******  */\n"
				 + ".myName { color: #fff; background-color: #333\n }\n"
				 + ".myName:hover { background        {\ncolor: rgb( 10, 100 % , 255 );/**** another comment / another test */ image: url(/test.png); type: image              }\nfont-color: rgb( 10, 100 % , 255 )\n; }"
				 + ".myName:pressed { text-effect: shadow;\n \ntext-shadow-color: #0f0;   \n\n font-color:    #33ee22   }"
		);
		Hashtable styles = reader.getAllStyles();
		Style style = (Style) styles.get("myname");
		Style origStyle = style;
		assertNotNull(style);
		assertEquals( "myname", style.name);
		Color color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( 0xffffff, color.getColor() );
		
		style = (Style) origStyle.getObjectProperty("focused-style");
		assertNotNull(style);
		assertEquals( "mynamefocused", style.name);
		color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( (10 << 16) | (255 << 8) | 255, color.getColor() );
		
		style = (Style) origStyle.getObjectProperty("pressed-style");
		assertNotNull(style);
		assertEquals( "mynamepressed", style.name);
		color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( 0x33ee22, color.getColor() );

	}
}
