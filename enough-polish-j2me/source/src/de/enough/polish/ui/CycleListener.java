//#condition polish.usePolishGui
/*
 * Created on Dec 31, 2010 at 7:28:12 PM.
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
package de.enough.polish.ui;

/**
 * <p>Allows to get notified and to stop the process when an item cycles through its list of children, either from bottom to top, top to bottom, left to right or right to left.</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface CycleListener {

	/**
	 * Direction is from top to the bottom, so currently the top item or element is focused and the bottom one should receive the focus.
	 */
	final static int DIRECTION_TOP_TO_BOTTOM = 1;

	/**
	 * Direction is from bottom to the top, so currently the bottom item or element is focused and the top one should receive the focus.
	 */
	final static int DIRECTION_BOTTOM_TO_TOP = 2;

	/**
	 * Direction is from left to the right, so currently the left item or element is focused and the right one should receive the focus.
	 */
	final static int DIRECTION_LEFT_TO_RIGHT = 2;

	/**
	 * Direction is from right to the left, so currently the right item or element is focused and the left one should receive the focus.
	 */
	final static int DIRECTION_RIGHT_TO_LEFT = 3;


	/**
	 * Notifies the listener about a cycle action.
	 * @param item the item that cycles
	 * @param direction the direction
	 * @return true when the cycling can continue, false when the cycling should be aborted.
	 */
	boolean onCycle( Item item, int direction );
}
