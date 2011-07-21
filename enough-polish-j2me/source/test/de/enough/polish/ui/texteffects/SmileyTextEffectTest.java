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

package de.enough.polish.ui.texteffects;

import junit.framework.TestCase;

/**
 * Tests the smiley text effect
 * @author robertvirkus
 *
 */
public class SmileyTextEffectTest extends TestCase {

	/**
	 * Creates a new test case
	 */
	public SmileyTextEffectTest() {
		super();
	}

	/**
	 * Creates a new test case
	 * @param name the name
	 */
	public SmileyTextEffectTest(String name) {
		super(name);
	}
	
	public void testPerformance() {
		SmileyTextEffect effect;
		SmileyTextEffect.Smiley smile = new SmileyTextEffect.Smiley( new String[]{":-)", ":)"}, "/smile.png"); 
		SmileyTextEffect.Smiley smirk = new SmileyTextEffect.Smiley( new String[]{";-)", ";)"}, "/smirk.png"); 
		SmileyTextEffect.Smiley sad = new SmileyTextEffect.Smiley( new String[]{":-(", ":("}, "/sad.png");
		
		
		SmileyTextEffect.smileyList = new SmileyTextEffect.Smiley[]{
				smile,
				sad,
				smirk
		};
		
		SmileyTextEffect.smileyWidth = 10;
		SmileyTextEffect.smileyHeight = 10;
		
		effect = new SmileyTextEffect();
	}

}
