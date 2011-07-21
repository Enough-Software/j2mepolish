/*
 * Created on Oct 12, 2007 at 9:33:27 PM.
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

import de.enough.polish.io.Serializable;

/**
 * <p>Represents a point within a scale</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Oct 12, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Point
implements Serializable
{
	/** the the horizontal position */
	public int x;
	/** the the vertical position */
	public int y;

	/**
	 * Creates a new undefined point.
	 */
	public Point() {
		// do nothing
	}
	
	/**
	 * Creates a new defined point.
	 * 
	 * @param x the horizontal position
	 * @param y the vertical positin
	 */
	public Point( int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals( Object o) {
		if (o instanceof Point) {
			Point p = (Point) o;
			return p.x == this.x && p.y == this.y;
		}
		return false;
	}
	
	public int hashCode() {
		return (this.x ^ this.y);
	}

}
