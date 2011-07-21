/*
 * Created on 28-Oct-2005 at 10:32:59.
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

import junit.framework.TestCase;

public class TextFieldTest extends TestCase {

	public TextFieldTest(String name) {
		super(name);
	}
	
	public void testNumericAnyTrick() {
		TextField field = new TextField( null, "23", 50, TextField.NUMERIC );
		field.setConstraints( TextField.ANY );
		assertEquals( TextField.MODE_NUMBERS, field.inputMode );
	}

}
