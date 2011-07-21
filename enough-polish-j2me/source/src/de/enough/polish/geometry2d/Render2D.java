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
import javax.microedition.lcdui.Graphics;

/**
 * Routines to render 2D geometrical objects on screen. They should be used mainly
 * for visually debugging and analysis.
 *
 * @author Ovidiu Iliescu
 */
public class Render2D
{
    /**
     * Paints a Point2D object on a Graphics object.
     *
     * @param point the point to paint
     * @param g the Graphics object to paint on
     */
    public static void paint(Point2D point, Graphics g)
    {
            g.drawLine((int) point.x, (int)point.y, (int)point.x,(int) point.y);
    }

    /**
     * Paints a Point2D object on a Graphics object and with the specified thickness.
     *
     * @param point the point to paint
     * @param g the Graphics object to paint on
     * @param thickness the thickness to use
     */
    public static void paint(Point2D point, Graphics g, int thickness)
    {
            g.fillRect((int) point.x-thickness/2, (int)point.y-thickness/2, thickness,thickness);
    }

    /**
     * Paints a Line2D object on a Graphics object.
     *
     * @param line the line to paint
     * @param g the Graphics object to paint on
     */
    public static void paint(Line2D line, Graphics g)
    {
        g.drawLine((int)line.p1.x, (int)line.p1.y,(int) line.p2.x,(int) line.p2.y);
    }

    /**
     * Paints a Polygon2D object on a Graphics object.
     *
     * @param polygon the polygon to paint
     * @param g the Graphics object to paint on
     */
    public static void paint(Polygon2D polygon, Graphics g)
    {
        Enumeration points = polygon.points.elements() ;

        Point2D p1,p2,firstPoint;

        // Get the first point if there are more than 2 points in the polygon,
        // otherwise return;
        if ( polygon.points.size() > 2 )
        {
            p1 = (Point2D) points.nextElement();
            firstPoint = p1;
        }
        else
        {
            return;
        }

        // Draw the first n-1 lines of the polygon
        while ( points.hasMoreElements() )
        {
            p2 = (Point2D) points.nextElement() ;
            g.drawLine((int)p1.x,(int) p1.y,(int) p2.x,(int) p2.y);
            p1 = p2;
        }
        
        // Draw the last (closing) line of the polygon
        g.drawLine((int)p1.x, (int)p1.y, (int)firstPoint.x, (int) firstPoint.y);


    }

}
