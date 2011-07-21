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

import java.util.Enumeration;

/**
 * Enlarges various 2D geometrical entities (lines, polygons) by a given factor,
 * with respect to (0,0). If the entity to be enlarge is not centered on (0,0), 
 * the offset between (0,0) and the various points of the entity will also increase.
 * The factor can be <1, in which case the entity will be shrunk (not enlarged).
 * Negative factors will produce mirror images.
 * 
 * @author Ovidiu Iliescu
 */
public class Enlarge2D
{

    /**
     * Enlarges the specified line.
     *
     * @param line to enlarge
     * @param factor the factor
     */
    public static void enlarge(Line2D line, double factor)
    {

        line.p1.x *= factor ;
        line.p1.y *= factor;
        line.p2.x *= factor;
        line.p2.y *= factor ;
    }

    /**
     * Enlarges the specified polygon.
     *
     * @param polygon to enlarge
     * @param factor the factor
     */
    public static void enlarge(Polygon2D polygon, double factor)
    {
        Enumeration points = polygon.points.elements() ;

        Point2D temp ;
        while ( points.hasMoreElements() )
        {
            temp = (Point2D) points.nextElement() ;
            temp.x *= factor;
            temp.y *= factor;
        }
    }

}
