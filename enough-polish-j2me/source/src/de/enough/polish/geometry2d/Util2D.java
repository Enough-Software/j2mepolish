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
 * Various utility functions pertaining to 2D geometrical operations
 *
 * @author Ovidiu Iliescu
 */
public class Util2D {

    /**
     * Defines the distance below which two points are considered to coincide.
     */
    public static final double VERY_SMALL_DISTANCE = 0.000001 ;

    /**
     * Defines the origin as a point
     */
     public static final Point2D origin = new Point2D(0,0);

    /**
     * Returns the slope of a line defined by two points
     *
     * @param point1 the first point
     * @param point2 the second point
     * @return the slope of the line
     */
    public static double getSlope(Point2D point1, Point2D point2)
    {
        return ( ( ( point1.y ) - point2.y ) / ( ( point1.x ) - point2.x   ) ) ;
    }

    /**
     * Return the slope of a line.
     *
     * @param line the line
     * @return the slope of the line
     */
    public static double getSlope(Line2D line)
    {
        return getSlope(line.p1, line.p2);
    }

    /**
     * Returns the distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the distance between the two points
     */
    public static double getDistance(Point2D p1, Point2D p2)
    {
        return Math.sqrt( (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y) );
    }

    /**
     * Returns the Y-intercept of a line.
     *
     * @param line the line
     * @return the Y-intercept
     */
    public static double getYIntercept(Line2D line)
    {
        double m = getSlope(line);
        return line.p1.y - m * line.p1.x;
    }

    /**
     * Returns the X-intercept of a line.
     *
     * @param line the line
     * @return the X-intercept
     */
    public static double getXIntercept(Line2D line)
    {
        return  (- getYIntercept(line))/getSlope(line);
    }

    /**
     * Returns a Line2D object for a line, based on the line's general equation (Ax + By = C).
     *
     * @param A value of coefficient A
     * @param B value of coefficient B
     * @param C value of coefficient C
     * @return the Line2D object representing the line with the specified coefficients
     */
    public static Line2D line2DFromGeneralEquation(double A, double B, double C)
    {
        Point2D p1 = new Point2D ( C/A, 0) ; // X intercept
        Point2D p2 = new Point2D ( 0, C/B) ; // Y intercept
        Line2D result = new Line2D(p1,p2,false);
        return result;
    }

    /**
     * Checks if a line is vertical.
     *
     * @param line the line to check
     * @return returns true if the line is vertical, false otherwise.
     */
    public static boolean isVertical (Line2D line)
    {
        return ( (Math.abs(line.p1.x - line.p2.x) < Util2D.VERY_SMALL_DISTANCE ) ) ;
    }

    /**
     * Checks if two lines are parallel
     *
     * @param line1 he first line
     * @param line2 the second line
     * @return returns true if the lines are parallel, false otherwise
     */
    public static boolean areParallel(Line2D line1, Line2D line2)
    {
        // If both lines are vertical, they are parallel
        if ( isVertical(line1) && isVertical(line2) )
        {
            return true;
        }
        else // If one of them is vertical, they are not parallel
        if ( isVertical(line1) || isVertical(line2) )
        {
            return false;
        }
        else // General case. If their slopes are the same, they are parallel
        {
            return ( Math.abs ( getSlope(line1) - getSlope(line2) ) < VERY_SMALL_DISTANCE ) ;
        }
    }

    
    

}
