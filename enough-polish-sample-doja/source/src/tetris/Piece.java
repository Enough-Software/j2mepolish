/*
 * @(#)Piece.java	1.6 00/09/05 @(#)
 *
 * Copyright 2000 Sun Microsystems, Inc. All rights reserved.
 */
package tetris;



import java.util.Random;

import com.nttdocomo.ui.Graphics;


/*
 * Instances of this class represent individual pieces
 * of the Tetris game. Each piece has a certain color, shape,
 * orientation and position. Pieces may be moved to the
 * left, to the right, downward, or rotated clockwise.
 * Pieces are comprised of square tiles. All the tiles in
 * one piece share the same color. All the different shapes
 * of Tetris pieces can be enumerated by taking four tiles
 * and grouping them such that each tile shares at least
 * one side with another tile.
 */
public class Piece {

    /** An ID for a piece that resembles the letter I. */
    public static final int I = 0;

    /** An ID for a piece that resembles the letter L. */
    public static final int L = 1;

    /** An ID for a piece that resembles the letter Z. */
    public static final int Z = 2;

    /** An ID for a piece that resembles the letter O. */
    public static final int O = 3;

    /** An ID for a piece that resembles the letter T. */
    public static final int        T = 4;
    private Point[][]              points;
    private int                    index;
    private int                    x;
    private int                    y;
    private int                    width;
    private int                    height;
    private int                    color;
    private Grid                   grid;
    private static Piece           test     = new Piece();
    private static Random          rnd      = new Random();
    private static final Point[][] O_POINTS = {
        { new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1) }
    };
    private static final Point[][] I_POINTS = {
        { new Point(0, 0), new Point(1, 0), new Point(2, 0),
          new Point(3, 0) },
        { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(0, 3) }
    };
    private static final Point[][] Z_POINTS = {
        { new Point(0, 0), new Point(1, 0), new Point(1, 1),
          new Point(2, 1) },
        { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) }
    };
    private static final Point[][] L_POINTS = {
        { new Point(0, 0), new Point(0, 1), new Point(0, 2),
          new Point(1, 2) },
        { new Point(2, 0), new Point(0, 1), new Point(1, 1),
          new Point(2, 1) },
        { new Point(0, 0), new Point(1, 0), new Point(1, 1),
          new Point(1, 2) },
        { new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(0, 1) }
    };
    private static final Point[][] T_POINTS = {
        { new Point(0, 0), new Point(0, 1), new Point(0, 2),
          new Point(1, 1) },
        { new Point(1, 0), new Point(0, 1), new Point(1, 1),
          new Point(2, 1) },
        { new Point(1, 0), new Point(0, 1), new Point(1, 1),
          new Point(1, 2) },
        { new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(1, 1) }
    };

    /** Don't allow anyone to create instances of this class. */
    private Piece() {}

    /** Private internal constructor. */
    private Piece(Grid grid, Point[][] points, int color) {

        this.points = points;
        this.grid   = grid;
        this.color  = color;
        index       = getRandomInt(points.length);

        update();
        resetPosition();
    }

    /**
     * This method creates a random Tetris piece.
     * @param grid the grid that this piece will
     * be associated with.
     */
    public static Piece createRandomPiece(Grid grid) {
        return createPiece(grid, getRandomInt(5));
    }

    /**
     * Create a new Tetris piece.
     * @param grid the grid that this piece will
     * be associated with.
     * @param id piece identifier, must be one of
     * <ul>
     * <li><code>I</code>
     * <li><code>L</code>
     * <li><code>Z</code>
     * <li><code>O</code>
     * <li><code>T</code>
     * </ul>
     */
    public static Piece createPiece(Grid grid, int id) {

        if (id == I) {
            return new Piece(grid, I_POINTS, ThreeDColor.red);
        }

        if (id == L) {
            return new Piece(grid, L_POINTS, ThreeDColor.yellow);
        }

        if (id == Z) {
            return new Piece(grid, Z_POINTS, ThreeDColor.green);
        }

        if (id == O) {
            return new Piece(grid, O_POINTS, ThreeDColor.blue);
        }

        if (id == T) {
            return new Piece(grid, T_POINTS, ThreeDColor.lightGray);
        }

        throw new IllegalArgumentException();
    }

    private static int getRandomInt(int limit) {
        return Math.abs(rnd.nextInt() % limit);
    }

    /** Reset this piece to its original location. */
    public void resetPosition() {
        x = grid.getGridWidth() / 2 - getWidth() / 2 - 1;
        y = 0;
    }

    /** Copy the attributes of this instance into <code>p</code>*/
    public void copyInto(Piece p) {

        p.points = points;
        p.grid   = grid;
        p.index  = index;
        p.x      = x;
        p.y      = y;
    }

    /**
     * Returns an array of points that describes
     * shape and orientation of this instance. The
     * points designate the tiles of this instance.
     */
    public Point[] getPoints() {
        return points[index];
    }

    /**
     * Drop this piece until it hits other pieces,
     * or touches the bottom of the containing grid.
     */
    public int drop() {

        int n = 0;

        while (down()) {
            n++;
        }

        return n;
    }

    /**
     * Will attempt to rotate this instance by 90 degrees
     * in a clockwise direction. If some of the tiles in this
     * instance would overlap with tiles from other pieces, or if some
     * of the tiles of this instance would lie outside the containing
     * grid as a result of this operation, the rotation is not preformed.
     */
    public synchronized void rotateRight() {

        copyInto(test);
        test.setNextIndex();

        if ((test.getX() + test.getWidth() < grid.getGridWidth())
                && (test.getY() + test.getHeight() < grid.getGridHeight())
                &&!grid.overlapsWith(test)) {
            setNextIndex();

            return;
        }

        if ((test.getX() + test.getWidth() >= grid.getGridWidth())
                && (test.getY() + test.getHeight() < grid.getGridHeight())) {
            int n = test.getX() + test.getWidth() - grid.getGridWidth();

            test.setLocation(test.getX() - n, test.getY());

            for (int i = test.getWidth() - --n; i > 0; --i) {
                if (!test.left()) {
                    return;
                }

                if (!grid.overlapsWith(test)) {
                    setNextIndex();
                    setLocation(test.getX(), getY());

                    return;
                }
            }
        }
    }

    private void setNextIndex() {

        if (++index >= points.length) {
            index = 0;
        }

        update();
    }

    private void setPreviousIndex() {

        if (--index < 0) {
            index = points.length - 1;
        }

        update();
    }

    private void update() {
        updateWidth();
        updateHeight();
    }

    /** Set the base location of this instance. */
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x coordinate of the base
     * location of this instance.
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y coordinate of the base
     * location of this instance.
     */
    public int getY() {
        return y;
    }

    /**
     * Will attempt to move this instance one unit to the left. If
     * some of the tiles in this instance would overlap with tiles
     * from other pieces, or if some of the tiles of this instance
     * would lie outside the containing grid as a result of this
     * operation, the move is not preformed.
     * <p> Returns <code>true</code> if the operation succeeded, <code>
     * false</code> otherwise.
     */
    public synchronized boolean left() {

        if (getX() <= 0) {
            return false;
        }

        copyInto(test);
        test.setLocation(test.getX() - 1, test.getY());

        if (!grid.overlapsWith(test)) {
            setLocation(getX() - 1, getY());

            return true;
        }

        return false;
    }

    /**
     * Will attempt to move this instance one unit to the right. If
     * some of the tiles in this instance would overlap with tiles
     * from other pieces, or if some of the tiles of this instance
     * would lie outside the containing grid as a result of this
     * operation, the move is not preformed.
     * <p> Returns <code>true</code> if the operation succeeded, <code>
     * false</code> otherwise.
     */
    public synchronized boolean right() {

        if (getX() + getWidth() >= grid.getGridWidth() - 1) {
            return false;
        }

        copyInto(test);
        test.setLocation(test.getX() + 1, test.getY());

        if (!grid.overlapsWith(test)) {
            setLocation(getX() + 1, getY());

            return true;
        }

        return false;
    }

    /**
     * Will attempt to move this instance one unit downward. If
     * some of the tiles in this instance would overlap with tiles
     * from other pieces, or if some of the tiles of this instance
     * would lie outside the containing grid as a result of this
     * operation, the move is not preformed.
     * <p> Returns <code>true</code> if the operation succeeded, <code>
     * false</code> otherwise.
     */
    public synchronized boolean down() {

        if (getY() + getHeight() >= grid.getGridHeight() - 1) {
            return false;
        }

        copyInto(test);
        test.setLocation(test.getX(), test.getY() + 1);

        if (!grid.overlapsWith(test)) {
            setLocation(getX(), getY() + 1);

            return true;
        }

        return false;
    }

    private void updateWidth() {

        width = -1;

        for (int i = 0; i < points[index].length; i++) {
            Point p = points[index][i];

            if (p.x > width) {
                width = p.x;
            }
        }
    }

    /** Get the width (in tiles) of this instance. */
    public int getWidth() {
        return width;
    }

    private void updateHeight() {

        height = -1;

        for (int i = 0; i < points[index].length; i++) {
            Point p = points[index][i];

            if (p.y > height) {
                height = p.y;
            }
        }
    }

    /** Get the height (in tiles) of this instance. */
    public int getHeight() {
        return height;
    }

    /** Get the color of this instance. */
    public int getColor() {
        return color;
    }

    /** Paint this instance onto the specified Graphics context. */
    public synchronized void paint(Graphics g) {

        for (int i = 0; i < points[index].length; i++) {
            Point p = points[index][i];

            Grid.draw3DRect(g, 1 + (p.x + getX()) * Tetris.TILE_SIZE,
                            (p.y + getY()) * Tetris.TILE_SIZE, color);
        }
    }
}

