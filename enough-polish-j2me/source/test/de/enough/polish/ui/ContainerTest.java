/*
 * Created on 25-Oct-2005 at 12:59:53.
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
package de.enough.polish.ui;

import de.enough.polish.ui.containerviews.MIDP2LayoutView;
import junit.framework.TestCase;

public class ContainerTest extends TestCase {

	public ContainerTest(String name) {
		super(name);
	}
	
	public void testParseIndexUrl() {
		Container container = new Container( false );
		StringItem item1 = new StringItem( null, "1");
		StringItem item2 = new StringItem( null, "2");
		StringItem item3 = new StringItem( null, "3");
		StringItem item4 = new StringItem( null, "4");
		StringItem item5 = new StringItem( null, "5");
		container.add(item1);
		container.add(item2);
		container.add(item3);
		container.add(item4);
		container.add(item5);
		
		String result;
		result = container.parseIndexUrl("%INDEX%", item1);
		assertEquals( "0", result );
		result = container.parseIndexUrl("icon%INDEX%", item1);
		assertEquals( "icon0", result );
		result = container.parseIndexUrl("%INDEX%icon", item1);
		assertEquals( "0icon", result );
		
		result = container.parseIndexUrl("%INDEX%", item4);
		assertEquals( "3", result );
		result = container.parseIndexUrl("icon%INDEX%", item4 );
		assertEquals( "icon3", result );
		result = container.parseIndexUrl("%INDEX%icon", item4);
		assertEquals( "3icon", result );
	}
	
	public void testMIDP2LayoutContainerView() {
		Container container = new Container(true);
		Container nested = new Container( false );
		int availHeight = 200;
		container.add(nested);
		nested.containerView = new MIDP2LayoutView();
		StringItem button = new StringItem(null, "button", StringItem.BUTTON );
		nested.add( button );
		container.getItemHeight( 200, 200, availHeight );
		assertEquals( nested, container.getFocusedItem() );
		assertEquals( button, nested.getFocusedItem() );
		
		container = new Container(true);
		nested = new Container( false );
		nested.containerView = new MIDP2LayoutView();
		button = new StringItem(null, "button", StringItem.BUTTON );
		nested.add( button );
		container.add(nested);		
		container.getItemHeight( 200, 200, availHeight );
		assertEquals( nested, container.getFocusedItem() );
		assertEquals( button, nested.getFocusedItem() );
		
		
		container = new Container(true);
		nested = new Container( false );
		container.add(nested);
		nested.containerView = new MIDP2LayoutView();
		StringItem item = new StringItem(null, "One");
		nested.add( item );
		item = new StringItem(null, "Two");
		nested.add( item );
		button = new StringItem(null, "button", StringItem.BUTTON );
		nested.add( button );
		item = new StringItem(null, "Three");
		nested.add( item );
		container.getItemHeight( 200, 200, availHeight );
		assertEquals( nested, container.getFocusedItem() );
		assertEquals( button, nested.getFocusedItem() );

		
	}
	
	public void testSetAbsoluteY() {
		Container container = new Container( false );
		StringItem item1 = new StringItem( null, "1");
		StringItem item2 = new StringItem( null, "2");
		StringItem item3 = new StringItem( null, "3");
		StringItem item4 = new StringItem( null, "4");
		StringItem item5 = new StringItem( null, "5");
		container.add(item1);
		container.add(item2);
		container.add(item3);
		container.add(item4);
		container.add(item5);
		
		container.init(100, 100, 200);
		
		int absoluteY = item4.getAbsoluteY();
		System.out.println("before insert: absY=" + absoluteY);
		container.add(0, new StringItem(null, "x"));
		container.add(0, new StringItem(null, "y"));
		container.add(0, new StringItem(null, "z"));
		
		container.init(100, 100, 200);
		System.out.println("after insert: absY=" + item4.getAbsoluteY());
		item4.setAbsoluteY(absoluteY);
		System.out.println("after set: absY=" + item4.getAbsoluteY());
		
	}

}
