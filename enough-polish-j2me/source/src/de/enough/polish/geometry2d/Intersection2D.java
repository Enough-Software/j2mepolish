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
import java.util.Vector;

/**
 * Calculates the intersection of various 2D geometrical entities (points, lines,
 * polygons). 
 *
 * @author Ovidiu Iliescu
 */
public class Intersection2D {


    /**
     * Checks if two points coincide.
     * 
     * @param p1 the first point.
     * @param p2 the second point
     * @return a reference to p1 if the points coincide, null otherwise.
     */
    public static Point2D intersection(Point2D p1, Point2D p2)
    {
        if ( ( Math.abs(p1.x - p2.x) < Util2D.VERY_SMALL_DISTANCE ) && ( Math.abs(p1.y - p2.y) < Util2D.VERY_SMALL_DISTANCE ) )
        {
            return p1;
        }
        else
        {
            return null;
        }
    }

    /**
     * Checks if a line and a point intersect (if the point is on the line).
     *
     * @param line the line to use
     * @param point the point to use
     * @return a reference to the point if the two entities intersect, null otherwise.
     */
    public static Point2D intersection(Line2D line, Point2D point)
    {
        boolean areIntersecting = false;
        
        if ( line.isSegment == true )
        {
            areIntersecting = ( Math.abs( Util2D.getDistance(line.p1, line.p2) - Util2D.getDistance(line.p1,point) - Util2D.getDistance(line.p2,point) ) < Util2D.VERY_SMALL_DISTANCE );
        }
        else
        {
            if ( Util2D.isVertical(line) )
            {
                // Vertical lines have an undefined slope, just compare the X.
                areIntersecting = ( Math.abs ( point.x - line.p1.x ) < Util2D.VERY_SMALL_DISTANCE );
            }
            else
            {
                areIntersecting = ( Math.abs( Util2D.getSlope(line) - Util2D.getSlope(line.p1,point) ) < Util2D.VERY_SMALL_DISTANCE);
            }
        }

        if ( areIntersecting )
        {
            return point;
        }
        else
        {
            return null;
        }
    }

    /**
     * Checks if two lines (or line segments) overlap.
     *
     * @param line1 the first line
     * @param line2 the second line
     * @return returns true if they overlap, false otherwise
     */
    public static boolean areOverlapping(Line2D line1, Line2D line2)
    {
        return ( Util2D.areParallel(line1, line2) &&
                ( ( intersection(line2, line1.p1) != null )
                  || ( intersection(line2, line1.p2) != null ) )
                &&
                ( ( intersection(line1, line2.p1) != null )
                  || ( intersection(line1, line2.p2) != null ) )
               );
    }


    /**
     * Calculates the intersection point (if any) of two lines (or line segments).
     * IMPORTANT: If the intersection is not a point but a line or a line segment,
     * the function will return null.
     *
     * @param line1 the first line
     * @param line2 the second line
     * @return the intersection point, or null if they are parallel
     */
    public static Point2D intersection(Line2D line1, Line2D line2)
    {

        Point2D result ;       
        
        // Check if the lines are parallel
        if ( Util2D.areParallel(line1, line2) )
        {
            return null;
        }

        // If line1 is vertical, solve for line1.
        if ( Util2D.isVertical(line1) )
        {
            double y = Util2D.getSlope(line2) * line1.p1.x + Util2D.getYIntercept(line2);
            result = new Point2D(line1.p1.x,y);  
        }
        else // If line2 is vertical, solve for line1.
        if ( Util2D.isVertical(line2))
        {
            double y = Util2D.getSlope(line1) * line2.p1.x + Util2D.getYIntercept(line1);
            result = new Point2D(line2.p1.x,y);
        }
        else // General case below
        {
            double yIntercept1 = Util2D.getYIntercept(line1);
            double yIntercept2 = Util2D.getYIntercept(line2);

            double slope1 = Util2D.getSlope(line1);
            double slope2 = Util2D.getSlope(line2);

            double xIntersection = - (yIntercept1 - yIntercept2) / ( slope1 - slope2 );
            double yIntersection = yIntercept1 + slope1 * xIntersection ;

            result = new Point2D ( xIntersection, yIntersection );
        }

        // Check if the intersection point lies on both lines (in case one of
        // them is a line segment and not an infinite line) .
        if ( ! ( ( intersection(line2, result) != null ) && ( intersection(line1, result) != null ) ) )
        {
            result = null;
        }

        return result;

    }

    /**
     * Determines if a given point intersects with a polygon
     *
     * @param polygon the polygon to use
     * @param point the point to use
     * @return a reference to the point if the two shapes intersect, null otherwise
     */
    public static Point2D intersection(Polygon2D polygon, Point2D point)
    {
        Enumeration points = polygon.points.elements() ;
        Line2D tempLine = new Line2D();
        Point2D p1,p2,firstPoint,intersectionPoint;

        // Get the first point if there are more than 2 points in the polygon,
        // otherwise return;
        if ( polygon.points.size() > 2 )
        {
            p1 = (Point2D) points.nextElement();
            firstPoint = p1;
        }
        else
        {
            return null;
        }


        // Calculate intersection for the first n-1 lines of the polygon
        while ( points.hasMoreElements() )
        {
            p2 = (Point2D) points.nextElement() ;
            tempLine.p1 = p1;
            tempLine.p2 = p2;
            intersectionPoint = intersection(tempLine,point);
            if ( intersectionPoint != null )
            {
               return point;
            }
            p1 = p2;
        }

        // Calculate the intersection for the last line
        tempLine.p1 = p1;
        tempLine.p2 = firstPoint;
        intersectionPoint = intersection(tempLine,point);
        if ( intersectionPoint != null )
        {
           return point;
        }

        return null;

    }

    /**
     * Determines if a given point is inside the specified polygon.
     *
     * @param polygon the polygon to use
     * @param point the point to use
     * @return a reference to the point if it sits inside, null otherwise
     */
    public static Point2D isInside(Polygon2D polygon, Point2D point)
    {
        Enumeration points = polygon.points.elements() ;
        Line2D tempLine = new Line2D();
        Point2D p1,p2,firstPoint,intersectionPoint;
        int intersectionCount = 0;

        Line2D line = new Line2D();
        line.p1 = point;
        line.p2 = new Point2D(9999999D, point.y);

        // Get the first point if there are more than 2 points in the polygon,
        // otherwise return;
        if ( polygon.points.size() > 2 )
        {
            p1 = (Point2D) points.nextElement();
            firstPoint = p1;
        }
        else
        {
            return null;
        }    

        // Calculate intersection for the first n-1 lines of the polygon
        while ( points.hasMoreElements() )
        {
            p2 = (Point2D) points.nextElement() ;
            tempLine.p1 = p1;
            tempLine.p2 = p2;
            intersectionPoint = intersection(tempLine,line);
            if ( intersectionPoint != null )
            {
                // Special case : if the intersection point is a vertex of a
                // previously processed segment, only increment the counter
                // if the second vertex of the current segment is below
                // the cast ray.
                if ( intersection(p1, intersectionPoint) != null )
                {
                   if ( p2.y < point.y)
                   {
                    intersectionCount++;
                   }
                }
                else
                {
                    intersectionCount++;
                }
            }
            p1 = p2;
        }

        // Calculate the intersection for the last line
        tempLine.p1 = p1;
        tempLine.p2 = firstPoint;
        intersectionPoint = intersection(tempLine,line);
        if ( intersectionPoint != null )
        {
            // Special case : if the intersection point is a vertex of a
            // previously processed segment, only increment the counter
            // if the second vertex of the current segment is below
            // the cast ray.
            if ( intersection(p1, intersectionPoint) != null )
            {
               if ( firstPoint.y < point.y)
               {
                intersectionCount++;
               }
            }
            else
            {
                intersectionCount++;
            }
        }

        if ( intersectionCount % 2 == 1 )
        {
            return point;
        }
        else
        {
            return null;
        }
    }

    /**
     * Calculates the intersection point(s) between a polygon and a line.
     *
     * @param polygon the polygon to use
     * @param line the line to use
     * @return a Vector of Point2D objects representing the intersection points, or null if the two entities do not intersect
     */
    public static Vector intersection(Polygon2D polygon, Line2D line)
    {
        Enumeration points = polygon.points.elements() ;
        Vector result = null;
        Line2D tempLine = new Line2D();
        Point2D p1,p2,firstPoint,intersectionPoint;

        // Define the initial last intersection point to be completely outside
        // any plausibe polygon
        Point2D lastIntersectionPoint = new Point2D ( Double.MAX_VALUE / 3, Double.MAX_VALUE / 3);

        // Get the first point if there are more than 2 points in the polygon,
        // otherwise return;
        if ( polygon.points.size() > 2 )
        {
            p1 = (Point2D) points.nextElement();
            firstPoint = p1;
        }
        else
        {
            return result;
        }

        result = new Vector();

        // Calculate intersection for the first n-1 lines of the polygon
        while ( points.hasMoreElements() )
        {
            p2 = (Point2D) points.nextElement() ;
            tempLine.p1 = p1;
            tempLine.p2 = p2;
            intersectionPoint = intersection(tempLine,line);
            if ( intersectionPoint != null )
            {
                // Only add the intersecton point if it has not been added before
                if ( intersection(lastIntersectionPoint, intersectionPoint) == null)
                {
                    result.addElement(intersectionPoint);
                    lastIntersectionPoint = intersectionPoint ;
                }
            }
            p1 = p2;
        }

        // Calculate the intersection for the last line
        tempLine.p1 = p1;
        tempLine.p2 = firstPoint;
        intersectionPoint = intersection(tempLine,line);
        if ( intersectionPoint != null )
        {
            // Only add the intersecton point if it has not been added before
            if ( intersection(lastIntersectionPoint, intersectionPoint) == null)
            {
                result.addElement(intersectionPoint);
                lastIntersectionPoint = intersectionPoint ;
            }
        }

        if ( result.size() == 0 )
        {
            result = null ;
        }
        
        return result;

    }

    /**
     * Calculates the intersection point(s) of two polygons.
     *
     * @param polygon1 the first polygon
     * @param polygon2 the second polygon
     * @return a Vector of Point2D objects representing the intersection points, or null if the two entities do not intersect
     */
    public static Vector intersection(Polygon2D polygon1, Polygon2D polygon2)
    {
        Enumeration points = polygon1.points.elements() ;
        Vector result = null;
        Vector intersection ;
        Line2D tempLine = new Line2D();
        Point2D p1,p2,firstPoint;
        Point2D temp;
        Enumeration tempPoints;

        // Get the first point if there are more than 2 points in the polygon,
        // otherwise return;
        if ( polygon1.points.size() > 2 )
        {
            p1 = (Point2D) points.nextElement();
            firstPoint = p1;
        }
        else
        {
            return result;
        }

        result = new Vector();

        // Calculate intersection for the first n-1 lines of the polygon
        while ( points.hasMoreElements() )
        {
            p2 = (Point2D) points.nextElement() ;
            tempLine.p1 = p1;
            tempLine.p2 = p2;
            intersection = intersection(polygon2,tempLine);
            if ( intersection != null )
            {
               tempPoints = intersection.elements();
               while (tempPoints.hasMoreElements())
               {
                   temp = (Point2D) tempPoints.nextElement();
                   if ( intersection(temp,tempLine.p1) == null )
                   {
                    result.addElement(temp);
                   }
               }
            }
            p1 = p2;
        }

        // Calculate the intersection for the last line
        tempLine.p1 = p1;
        tempLine.p2 = firstPoint;
        intersection = intersection(polygon2,tempLine);
        if ( intersection != null )
        {
            tempPoints = intersection.elements();
            while (tempPoints.hasMoreElements())
            {
                temp = (Point2D) tempPoints.nextElement();
                if ( intersection(temp,tempLine.p1) == null )
                {
                result.addElement(temp);
                }
            }
        }

        if ( result.size() == 0 )
        {
            result = null ;
        }

        return result;

    }

}
