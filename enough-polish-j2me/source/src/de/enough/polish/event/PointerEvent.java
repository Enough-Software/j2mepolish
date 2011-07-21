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
public class PointerEvent extends UiEvent {	
	private int pointerX;
	private int pointerY;

	/**
	 * Resets this pointer event for re-using it.
	 * @param x the last pointer x position relative to the source of this event
	 * @param y the last pointer y position relative to the source of this event
	 */
	public void reset( int x, int y ) {
		this.pointerX = x;
		this.pointerY = y;
		super.reset();
	}

	/**
	 * Retrieves the horizontal pointer position relative to the source of this event
	 * @return the pointerX value
	 */
	public int getPointerX() {
		return this.pointerX;
	}

	/**
	 * Retrieves the vertical pointer position relative to the source of this event
	 * @return the pointerY value
	 */
	public int getPointerY() {
		return this.pointerY;
	}
	
	
}
