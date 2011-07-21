/*
 * Created on Dec 16, 2009 at 7:36:20 PM.
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
package de.enough.polish.event;

/**
 * <p>Represents a PointerEvent</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class KeyEvent extends UiEvent {	
	private int keyCode;
	private int gameAction;

	/**
	 * Resets this key event for re-using it.
	 * @param key the last pressed or released key code
	 * @param action the last associated game action
	 */
	public void reset( int key, int action ) {
		this.keyCode = key;
		this.gameAction = action;
		super.reset();
	}

	/**
	 * Retrieves the last pressed or released key code
	 * @return the keyCode value
	 */
	public int getKeyCode() {
		return this.keyCode;
	}

	/**
	 * Retrieves the last associated game action
	 * @return the gameAction value
	 */
	public int getGameAction() {
		return this.gameAction;
	}
	
	
}
