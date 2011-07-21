/*
 * Created on May 16, 2008 at 11:04:41 PM.
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
package de.enough.polish.preprocess.custom;

import java.util.regex.Matcher;

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TranslationPreprocessorTest extends TestCase
{

	/**
	 * @param name
	 */
	public TranslationPreprocessorTest(String name)
	{
		super(name);
	}
	
	public void testLocateGetPattern() {
		Matcher matcher;
		String input;
		
		input = "		UiAccess.setSubtitle(this, Locale.get(\"glk.title\"));";
		matcher = TranslationPreprocessor.LOCALE_GET_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals("Locale.get(\"glk.title\")", matcher.group() );

		input = "this.list.add(getIconItem( \"20zoll-7speichen\", Locale.get(\"zoll.speichen\"), GLKForm.ICON_RIM));";
		matcher = TranslationPreprocessor.LOCALE_GET_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals("Locale.get(\"zoll.speichen\")", matcher.group() );

		input = "this.list.add(getIconItem( \"20zoll-7speichen\", Locale.get(\"20zoll.7speichen\"), GLKForm.ICON_RIM));";
		matcher = TranslationPreprocessor.LOCALE_GET_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals("Locale.get(\"20zoll.7speichen\")", matcher.group() );

		
		input = "this.list.add(getIconItem( \"20zoll-7speichen\", Locale.get(\"20zoll-7speichen\"), GLKForm.ICON_RIM));";
		matcher = TranslationPreprocessor.LOCALE_GET_PATTERN.matcher( input );
		assertTrue( matcher.find() );
		assertEquals("Locale.get(\"20zoll-7speichen\")", matcher.group() );
	}

}
