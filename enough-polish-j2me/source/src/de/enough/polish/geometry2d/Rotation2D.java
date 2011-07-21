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
 * Routines to rotate 2D geometrical objects.
 *
 * @author Ovidiu Iliescu
 */
public class Rotation2D
{


    /**
     * Rotate a Point2D object around another Point2D object
     *
     * @param point the point to rotate
     * @param origin the point around which to rotate
     * @param angle the angle (in degrees) of rotation
     */
    public static void rotate(Point2D point, Point2D origin, double angle)
    {
        double cos = Math.cos( (Math.PI * angle) / 180);
        double sin = Math.cos( (Math.PI * angle) / 180);

        rotate(point, origin, cos, sin);
    }


   /**
     * Rotate a Point2D object around another Point2D object
     *
     * @param point the point to rotate
     * @param origin the point around which to rotate
     * @param cos the cosine of the rotation angle
     * @param sin the sine of the rotation angle
     */
    public static void rotate(Point2D point, Point2D origin, double cos, double sin)
    {
        double x = point.x - origin.x ;
        double y = point.y - origin.y ;
        point.x =  ( (x * cos) - (y * sin) );
        point.y =  ( (x * sin) + (y * cos) );
        point.x += origin.x;
        point.y += origin.y;
    }

    /**
     * Rotates a Line2D object around a Point2D object.
     * 
     * @param line the line to rotate
     * @param origin the point around which to rotate
     * @param angle the angle (in degrees) of rotation
     */
    public static void rotate(Line2D line, Point2D origin, double angle)
    {
        double cos = Math.cos( (Math.PI * angle) / 180);
        double sin = Math.sin( (Math.PI * angle) / 180);

        rotate(line, origin, cos, sin);
    }

    /**
     * Rotates a Line2D object around a Point2D object.
     *
     * @param line the line to rotate
     * @param origin the point around which to rotate
     * @param cos the cosine of the rotation angle
     * @param sin the sine of the rotation angle
     */
    private static void rotate(Line2D line, Point2D origin, double cos, double sin)
    {
        rotate(line.p1,origin,cos,sin);
        rotate(line.p2,origin,cos,sin);
    }

    /**
     * Rotates a Polygon2D object around a Point2D object.
     * 
     * @param polygon the polygon to rotate
     * @param origin the point around which to rotate
     * @param angle the rotation angle (in degrees)
     */
    public static void rotate(Polygon2D polygon, Point2D origin, double angle)
    {
        double cos = Math.cos( (Math.PI * angle) / 180);
        double sin = Math.sin( (Math.PI * angle) / 180);

        rotate(polygon, origin, cos, sin);
    }

    /**
     * Rotates a Polygon2D object around a Point2D object.
     *
     * @param polygon the polygon to rotate
     * @param origin the point around which to rotate
     * @param cos the cosine of the rotation angle
     * @param sin the sine of the rotation angle
     */
    public static void rotate(Polygon2D polygon, Point2D origin, double cos, double sin) {
        Enumeration points = polygon.points.elements() ;

        while ( points.hasMoreElements() )
        {
            rotate ( (Point2D) points.nextElement(), origin, cos, sin );
        }
    }

}
