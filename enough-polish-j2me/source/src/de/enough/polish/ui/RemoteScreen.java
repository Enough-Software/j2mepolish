/*
 * Created on Oct 30, 2008 at 8:50:57 AM.
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
 * <p>An interface for external screens like the bluetooth screener application of J2ME Polish.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface RemoteScreen
{
	/**
	 * Initializes the remote screen, this may be called several times, e.g. when the screen orientation is changed.
	 * 
	 * @param width the width of the physical display
	 * @param height the height of the physical display
	 * @param degrees the degrees by which the screen is rotated - 0, 90, 180 or 270.
	 */
	void init( int width, int height, int degrees );
	
	/**
	 * Refreshes parts of the screen.
	 * This method needs to do it's work asynchronously and return as fast as possible, as it is called from
	 * within the paint thread of the device.
	 * 
	 * @param x the horizontal start position
	 * @param y the vertical start position
	 * @param width the width of the refreshed part
	 * @param height the height of the refreshed part
	 * @param rgb the RGB data
	 */
	void updateScreen( int x, int y, int width, int height, int[] rgb );
}
