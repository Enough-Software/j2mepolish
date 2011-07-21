//#condition polish.usePolishGui
/*
 * Created on Mar 11, 2010 at 3:32:30 AM.
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
package de.enough.polish.ui;

/**
 * <p>Allows implementations to listen for initialization chamges of screens.</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface ScreenInitializerListener {

	/**
	 * Is called after the screen's content has been initialized.
	 * The screen will have set the content area by then (contentX, cotentY, contentWidth and contentHeight).
	 * @param screen the screen
	 */
	void notifyScreenInitialized( Screen screen );

	/**
	 * Adjusts the content area of a screen during initialization.
	 * The screen will have set the content area (contentX, cotentY, contentWidth and contentHeight).
	 * 
	 * @param screen the screen that is currently being initialized
	 */
	void adjustContentArea(Screen screen);
}
