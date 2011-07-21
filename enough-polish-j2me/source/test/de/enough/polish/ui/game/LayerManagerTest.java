/*
 * Created on 01-Sep-2004 at 10:55:54.
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
package de.enough.polish.ui.game;

import javax.microedition.lcdui.Graphics;

import junit.framework.TestCase;

/**
 * <p>Tests the LayerManager implementation</p>
 *
 * <p>Copyright Enough Software 2004 - 2008</p>

 * <pre>
 * history
 *        01-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LayerManagerTest extends TestCase {
	
	Layer l1 = new MockLayer(1);
	Layer l2 = new MockLayer(2);
	Layer l3 = new MockLayer(3);
	Layer l4 = new MockLayer(4);
	Layer l5 = new MockLayer(5);
	Layer l6 = new MockLayer(6);
	Layer l7 = new MockLayer(7);
	Layer l8 = new MockLayer(8);
	Layer l9 = new MockLayer(9);
	Layer l10 = new MockLayer(10);
	Layer l11 = new MockLayer(11);
	Layer l12 = new MockLayer(12);
	Layer l13 = new MockLayer(13);
	Layer l14 = new MockLayer(14);

	public LayerManagerTest(String name) {
		super(name);
	}
	
	
	public void testAppendLayer() {
		LayerManager manager = new LayerManager();
		assertEquals( 0, manager.getSize() );
		manager.append( this.l1 );
		assertEquals( 1, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		
		manager.append( this.l2 );
		assertEquals( 2, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		
		manager.append( this.l3 );
		assertEquals( 3, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		
		manager.append( this.l4 );
		assertEquals( 4, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		
		manager.append( this.l5 );
		assertEquals( 5, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		assertEquals( this.l5, manager.getLayerAt( 4 ));

		manager.append( this.l6 );
		assertEquals( 6, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		assertEquals( this.l5, manager.getLayerAt( 4 ));
		assertEquals( this.l6, manager.getLayerAt( 5 ));

		manager.append( this.l7 );
		assertEquals( 7, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		assertEquals( this.l5, manager.getLayerAt( 4 ));
		assertEquals( this.l6, manager.getLayerAt( 5 ));
		assertEquals( this.l7, manager.getLayerAt( 6 ));

		manager.append( this.l8 );
		assertEquals( 8, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		assertEquals( this.l5, manager.getLayerAt( 4 ));
		assertEquals( this.l6, manager.getLayerAt( 5 ));
		assertEquals( this.l7, manager.getLayerAt( 6 ));
		assertEquals( this.l8, manager.getLayerAt( 7 ));

		manager.append( this.l9 );
		assertEquals( 9, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		assertEquals( this.l5, manager.getLayerAt( 4 ));
		assertEquals( this.l6, manager.getLayerAt( 5 ));
		assertEquals( this.l7, manager.getLayerAt( 6 ));
		assertEquals( this.l8, manager.getLayerAt( 7 ));
		assertEquals( this.l9, manager.getLayerAt( 8 ));

		manager.append( this.l10 );
		assertEquals( 10, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		assertEquals( this.l5, manager.getLayerAt( 4 ));
		assertEquals( this.l6, manager.getLayerAt( 5 ));
		assertEquals( this.l7, manager.getLayerAt( 6 ));
		assertEquals( this.l8, manager.getLayerAt( 7 ));
		assertEquals( this.l9, manager.getLayerAt( 8 ));
		assertEquals( this.l10, manager.getLayerAt( 9 ));

		manager.append( this.l11 );
		assertEquals( 11, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		assertEquals( this.l5, manager.getLayerAt( 4 ));
		assertEquals( this.l6, manager.getLayerAt( 5 ));
		assertEquals( this.l7, manager.getLayerAt( 6 ));
		assertEquals( this.l8, manager.getLayerAt( 7 ));
		assertEquals( this.l9, manager.getLayerAt( 8 ));
		assertEquals( this.l10, manager.getLayerAt( 9 ));
		assertEquals( this.l11, manager.getLayerAt( 10 ));

		manager.append( this.l12 );
		assertEquals( 12, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		assertEquals( this.l5, manager.getLayerAt( 4 ));
		assertEquals( this.l6, manager.getLayerAt( 5 ));
		assertEquals( this.l7, manager.getLayerAt( 6 ));
		assertEquals( this.l8, manager.getLayerAt( 7 ));
		assertEquals( this.l9, manager.getLayerAt( 8 ));
		assertEquals( this.l10, manager.getLayerAt( 9 ));
		assertEquals( this.l11, manager.getLayerAt( 10 ));
		assertEquals( this.l12, manager.getLayerAt( 11 ));
		
		try {
			manager.getLayerAt( 12 );
			fail("getLayerAt( size ) should throw IndexOutOfBoundsException");
		} catch (IndexOutOfBoundsException e) {
			// expected behaviour
		}
	}
	
	public void testRemoveLayer() {
		LayerManager manager = new LayerManager();
		assertEquals( 0, manager.getSize() );
		manager.append( this.l1 );
		manager.append( this.l2 );
		manager.append( this.l3 );
		manager.append( this.l4 );
		assertEquals( 4, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		
		manager.remove( this.l2 );
		assertEquals( 3, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l3, manager.getLayerAt( 1 ));
		assertEquals( this.l4, manager.getLayerAt( 2 ));
		
		manager.remove( this.l1 );
		assertEquals( 2, manager.getSize() );
		assertEquals( this.l3, manager.getLayerAt( 0 ));
		assertEquals( this.l4, manager.getLayerAt( 1 ));
		
		manager.remove( this.l4 );
		assertEquals( 1, manager.getSize() );
		assertEquals( this.l3, manager.getLayerAt( 0 ));
		
		manager.remove( this.l3 );
		assertEquals( 0, manager.getSize() );
		
		try {
			manager.getLayerAt( 0 );
			fail("getLayerAt( size ) should throw IndexOutOfBoundsException");
		} catch (IndexOutOfBoundsException e) {
			// expected behaviour
		}
		
		manager.append( this.l1 );
		manager.append( this.l2 );
		manager.append( this.l3 );
		manager.append( this.l4 );
		manager.append( this.l5 );
		manager.append( this.l6 );
		manager.append( this.l7 );
		assertEquals( 7, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		assertEquals( this.l5, manager.getLayerAt( 4 ));
		assertEquals( this.l6, manager.getLayerAt( 5 ));
		assertEquals( this.l7, manager.getLayerAt( 6 ));
		
		int size = manager.getSize();
		while( manager.getSize() > 0 ) {
			manager.remove( manager.getLayerAt(0) );
			size--;
			assertEquals( size, manager.getSize() );
		}
		
		manager = new LayerManager();
		assertEquals( 0, manager.getSize() );
		manager.append( this.l1 );
		manager.append( this.l2 );
		manager.append( this.l3 );
		manager.append( this.l4 );
		manager.append( this.l5 );
		assertEquals( 5, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		assertEquals( this.l5, manager.getLayerAt( 4 ));
		
		manager.remove( this.l5 );
		assertEquals( 4, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));

		manager.remove( this.l4 );
		assertEquals( 3, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));

		manager.remove( this.l3 );
		assertEquals( 2, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));

		manager.remove( this.l2 );
		assertEquals( 1, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));

		manager.remove( this.l1 );
		assertEquals( 0, manager.getSize() );
	}
	
	public void testInsertLayer() {
		LayerManager manager = new LayerManager();
		assertEquals( 0, manager.getSize() );
		manager.insert( this.l1, 0 );
		manager.insert( this.l2, 1 );
		manager.insert( this.l3, 2 );
		manager.insert( this.l4, 3 );
		assertEquals( 4, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		
		
		manager = new LayerManager();
		assertEquals( 0, manager.getSize() );
		manager.append( this.l1 );
		manager.append( this.l2 );
		manager.append( this.l3 );
		manager.append( this.l4 );
		assertEquals( 4, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		
		manager.insert( this.l5, 0 );
		assertEquals( 5, manager.getSize() );
		assertEquals( this.l5, manager.getLayerAt( 0 ));
		assertEquals( this.l1, manager.getLayerAt( 1 ));
		assertEquals( this.l2, manager.getLayerAt( 2 ));
		assertEquals( this.l3, manager.getLayerAt( 3 ));
		assertEquals( this.l4, manager.getLayerAt( 4 ));
				
		manager.insert( this.l5, 4 );
		assertEquals( 5, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		assertEquals( this.l5, manager.getLayerAt( 4 ));
		
		manager.insert( this.l5, 5 );
		assertEquals( 5, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l2, manager.getLayerAt( 1 ));
		assertEquals( this.l3, manager.getLayerAt( 2 ));
		assertEquals( this.l4, manager.getLayerAt( 3 ));
		assertEquals( this.l5, manager.getLayerAt( 4 ));
		
		manager.insert( this.l5, 1 );
		assertEquals( 5, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l5, manager.getLayerAt( 1 ));
		assertEquals( this.l2, manager.getLayerAt( 2 ));
		assertEquals( this.l3, manager.getLayerAt( 3 ));
		assertEquals( this.l4, manager.getLayerAt( 4 ));

		manager.insert( this.l6, 3 );
		assertEquals( 6, manager.getSize() );
		assertEquals( this.l1, manager.getLayerAt( 0 ));
		assertEquals( this.l5, manager.getLayerAt( 1 ));
		assertEquals( this.l2, manager.getLayerAt( 2 ));
		assertEquals( this.l6, manager.getLayerAt( 3 ));
		assertEquals( this.l3, manager.getLayerAt( 4 ));
		assertEquals( this.l4, manager.getLayerAt( 5 ));
	}
	
	class MockLayer extends Layer {
		
		private String stringValue;

		public MockLayer( int pos ) {
			this.stringValue = "" + pos;
		}

		/* (non-Javadoc)
		 * @see de.enough.polish.ui.game.Layer#paint(javax.microedition.lcdui.Graphics)
		 */
		public void paint(Graphics g) {
			// don't do anything
		}
		
		public String toString() {
			return this.stringValue;
		}
		
	}

}
