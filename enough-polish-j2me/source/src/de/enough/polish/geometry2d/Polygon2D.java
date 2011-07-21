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

import java.util.Vector;
import java.util.Enumeration;


/**
 * Defines a geometrical 2D polygon. IMPORTANT: Carthesian coordinates are used.
 *
 * @author Ovidiu Iliescu
 */
public class Polygon2D
{
    /**
     * A Vector of Point2D objects that define the points of the polygon.
     */
    public Vector points = new Vector(3);

    /**
     * Performs a deep-clone of this object.
     *
     * @return the cloned object
     */
    public Polygon2D clonePolygon()
    {

        Polygon2D result = new Polygon2D();
        Vector resultPoints = result.points ;

        Enumeration items = this.points.elements();

        while ( items.hasMoreElements() )
        {
            Point2D point = (Point2D) items.nextElement();
			resultPoints.addElement( point.clonePoint() );
        }
        
        return result;

    }
}
