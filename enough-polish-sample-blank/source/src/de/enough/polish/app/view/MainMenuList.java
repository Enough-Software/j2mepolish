/*
 * Created on Dec 15, 2010 at 6:49:51 PM.
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
package de.enough.polish.app.view;

import de.enough.polish.ui.List;
import de.enough.polish.util.Locale;

/**
 * <p>The main menu of the application.</p>
 * <p>Often you can use List directly instead of using a specialized class.</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MainMenuList extends List {

	/**
	 * Creates a new main menu screen.
	 */
	public MainMenuList() {
		//#style screenMainMenu
		super( Locale.get("main.title"), List.IMPLICIT);
	}
	
	/**
	 * Adds a main menu entry to this screen.
	 * @param name the name of the entry
	 */
	public void addEntry( String name ) {
		//#style itemMainMenuEntry
		append(name, null);
	}
}
