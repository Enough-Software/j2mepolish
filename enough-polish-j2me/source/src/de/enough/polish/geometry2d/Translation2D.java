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
 * Performs translations on 2D geometrical entities (lines, polygons)
 *
 * @author Ovidiu Iliescu
 */
public class Translation2D
{

    /**
     * Translate a Point2D object on the X and Y axes.
     * 
     * @param point the point to translate
     * @param dx the X-axis offset
     * @param dy the Y-axis offset
     */
    public static void translate(Point2D point, double dx, double dy)
    {
        point.x += dx;
        point.y += dy;
    }

    /**
     * Translate a Line2D object on the X and Y axes.
     * 
     * @param line the line to translate
     * @param dx the X-axis offset
     * @param dy the Y-axis offset
     */
    public static void translate(Line2D line, double dx, double dy)
    {
        line.p1.x += dx;
        line.p1.y += dy;
        line.p2.x += dx;
        line.p2.y += dy;
    }

    /**
     * Translate a Polygon2D object on the X and Y axes.
     *
     * @param polygon the polygon to translate
     * @param dx the X-axis offset
     * @param dy the Y-axis offset
     */
    public static void translate(Polygon2D polygon, double dx, double dy)
    {
        Enumeration points = polygon.points.elements() ;

        while ( points.hasMoreElements() )
        {
            translate ( (Point2D) points.nextElement(), dx, dy );
        }
    }

    /**
     * Moves a Line2D object (with respect to it's origin) to specified point.
     * 
     * @param line the line to move
     * @param point the point to move to
     */
    public static void moveTo(Line2D line, Point2D point)
    {
        translate(line, point.x, point.y );
    }

    /**
     * Moves a Polygon2D object (with respect to it's origin) to specified point.
     *
     * @param polygon the polygon to move
     * @param point the point to move to
     */
    public static void moveTo(Polygon2D polygon, Point2D point)
    {
        translate(polygon, point.x, point.y );
    }
}
