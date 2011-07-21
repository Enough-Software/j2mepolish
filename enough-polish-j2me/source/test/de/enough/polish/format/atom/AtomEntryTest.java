/*
 * Created on Aug 17, 2010 at 12:09:04 AM.
 * 
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
package de.enough.polish.format.atom;

import junit.framework.TestCase;

public class AtomEntryTest extends TestCase {

	public AtomEntryTest(String name) {
		super(name);
	}
	
	public void testGetContentLinks() {
		AtomEntry entry = new AtomEntry();
		assertEquals(0, entry.getContentLinks().length);
		entry.setContent("");
		assertEquals(0, entry.getContentLinks().length);
		entry.setContent("hello <i>world</i> <a href=\"http://www.j2mepolish.org\">J2ME Polish</a> some <b>more text</b> goes here<p>check...");
		AtomContentLink[] links = entry.getContentLinks();
		assertEquals(1, links.length);
		assertEquals("http://www.j2mepolish.org", links[0].getHref() );
		assertEquals("J2ME Polish", links[0].getDescription() );
		
		entry.setContent("hello <i>world</i> <a href=\"http://www.j2mepolish.org\">J2ME Polish</a> some <b>more text</b> goes here<p>check...<a href = \"http://test.de\"  >Read more</a> now.<b>dsds</b>");
		links = entry.getContentLinks();
		assertEquals(2, links.length);
		assertEquals("http://www.j2mepolish.org", links[0].getHref() );
		assertEquals("J2ME Polish", links[0].getDescription() );
		assertEquals("http://test.de", links[1].getHref() );
		assertEquals("Read more", links[1].getDescription() );
	}

}
