//#condition polish.hasFloatingPoint
/*
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

package de.enough.polish.geometry2d;

/**
 * Defines a geometrical 2D point. IMPORTANT: Carthesian coordinates are used.
 *
 * @author Ovidiu Iliescu
 */
public class Point2D {

    /**
     * The X coordinate of the point.
     */
    public double x;

    /**
     * The X coordinate of the point.
     */
    public double y;

    public Point2D()
    {
        x=0.0;
        y=0.0;
    }

    /**
     * Creates a new point with the specified coordinates.
     * 
     * @param x coordinate of the point
     * @param y coordinate of the point
     */
    public Point2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a deep-clone of this object.
     *
     * @return the cloned object.
     */
    public Point2D clonePoint()
    {
        return new Point2D(this.x,this.y);
    }

}
