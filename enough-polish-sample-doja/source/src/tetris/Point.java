/*
 * @(#)Point.java	1.4 00/09/05 @(#)
 *
 * Copyright 2000 Sun Microsystems, Inc. All rights reserved.
 */
package tetris;



/** Utility class representing a 2D point. */
public class Point {

    /** Create a new instance of this class. */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** The x coordinate of this point.*/
    public int x;

    /** The y coordinate of this point.*/
    public int y;
}

