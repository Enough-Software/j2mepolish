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
 * Defines a geometrical 2D line or line segment. IMPORTANT: Carthesian coordinates are used.
 *
 * @author Ovidiu Iliescu
 */
public class Line2D {

    /**
     * The first endpoint of the line.
     */
    public Point2D p1;

    /**
     * The second endpoint of the line
     */
    public Point2D p2;

    /**
     * Specifies if the line should be treated as a line segment or an infinite-length line. Default is line segment.
     * Change it to false if you want the entity to be treated as an infinite-length line.
     */
    public boolean isSegment;


    public Line2D()
    {
        this.p1 = null;
        this.p2 = null ;
        this.isSegment = true ;
    }

    /**
     * Creates a line or a line segment defined by the specified points.
     * 
     * @param p1 the first point
     * @param p2 the second point
     * @param isSegment should this be a line or a line segment ?
     */
    public Line2D(Point2D p1, Point2D p2, boolean isSegment)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.isSegment = isSegment;
    }


    /**
     * Performs a deep-clone of this object.
     *
     * @return the cloned object
     */
    public Line2D cloneLine()
    {
        Line2D result = new Line2D(this.p1.clonePoint(),this.p2.clonePoint(),this.isSegment);
        return result;
    }

}
