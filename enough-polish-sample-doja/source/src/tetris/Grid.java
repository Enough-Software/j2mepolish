/*
 * @(#)Grid.java	1.8 00/09/05 @(#)
 *
 * Copyright 2000 Sun Microsystems, Inc. All rights reserved.
 */
package tetris;



import com.nttdocomo.ui.Canvas;
import com.nttdocomo.ui.Display;
import com.nttdocomo.ui.Font;
import com.nttdocomo.ui.Frame;
import com.nttdocomo.ui.Graphics;


/**
 * This class represents the grid box that catches
 * the pieces dropping from above. If a dropped piece
 * fills up one or more horizontal lines in the grid,
 * these lines will be removed from it.
 */
public class Grid extends Canvas {

    private int[][]  points;
    private Score    score;
    private Piece    current;
    private int      width;
    private int      height;
    private int      count;
    private boolean  init;
    private Tetris   listener;
    private String   pad = "0000";
    private boolean  game_over;
    private Font     score_font;
    private Font     game_over_font;
    static final int SOFT_LEFT  = Frame.SOFT_KEY_1;
    static final int SOFT_RIGHT = Frame.SOFT_KEY_2;

    /**
     * Create a new instance of this class.
     * @param witdth the width (in tiles) of this grid
     * @param height the height (in tiles) of this grid
     * @param score the score object to keep track of the score
     * @param listener a reference to the owning tetris object
     */
    public Grid(int width, int height, Score score, Tetris listener) {

        this.width    = width;
        this.height   = height;
        this.score    = score;
        this.listener = listener;
        points        = new int[height][width];
        init          = false;

        reset();
        setSoftLabel(SOFT_LEFT, "New");
        setSoftLabel(SOFT_RIGHT, "Quit");

        score_font     = Font.getFont(Font.FACE_MONOSPACE | Font.SIZE_SMALL);
        game_over_font = Font.getFont(Font.FACE_PROPORTIONAL
                                      | Font.SIZE_LARGE | Font.STYLE_BOLD);
    }

    public void setGameOver() {
        game_over = true;
    }

    /** Remove all pieces from this instance */
    public void reset() {

        game_over = false;

        synchronized (this) {
            current = null;
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                points[i][j] = Graphics.BLACK;
            }
        }
    }

    /** Get the width (in tiles) of this instance. */
    public int getGridWidth() {
        return width;
    }

    /** Get the height (in tiles) of this instance. */
    public int getGridHeight() {
        return height;
    }

    /**
     * Returns <code>true</code> if the specified
     * line is completely full, <code>false</code>
     * otherwise.
     */
    public boolean isFull(int line) {

        int[] top = points[line];

        for (int i = 0; i < top.length; i++) {
            if (top[i] == Graphics.BLACK) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns <code>true</code> if the specified
     * line is completely empty, <code>false</code>
     * otherwise.
     */
    public boolean isEmpty(int line) {

        int[] top = points[line];

        for (int i = 0; i < top.length; i++) {
            if (top[i] != Graphics.BLACK) {
                return false;
            }
        }

        return true;
    }

    /** Add the specified piece to this instance. */
    public int addPiece(Piece piece) {

        int n = 0;

        synchronized (this) {
            if (current != null) {
                int     x       = current.getX();
                int     y       = current.getY();
                Point[] ppoints = current.getPoints();
                int     color   = current.getColor();

                // current = null;
                for (int i = 0; i < ppoints.length; i++) {
                    Point p = ppoints[i];

                    points[p.y + y][p.x + x] = color;
                }
            }
        }

        n       = pack();
        current = piece;

        return n;
    }

    private int pack() {

        int n = 0;

        for (int l = getGridHeight() - 1; l > 0; l--) {
            while (isFull(l)) {
                for (int i = l; i > 0; i--) {
                    int[] src = points[i - 1];
                    int[] dst = points[i];

                    for (int j = 0; j < src.length; j++) {
                        dst[j] = src[j];
                    }
                }

                int[] dst = points[0];

                for (int j = 0; j < dst.length; j++) {
                    dst[j] = Graphics.BLACK;
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {}

                n++;
            }
        }

        return n;
    }

    /**
     * Returns <code>true</code> if the specified
     * piece overlaps with any tile in this instance,
     * <code>false</code> otherwise.
     */
    public boolean overlapsWith(Piece piece) {

        Point[] pts = piece.getPoints();

        for (int i = 0; i < pts.length; i++) {
            int x = pts[i].x + piece.getX();
            int y = pts[i].y + piece.getY();

            if ((x < 0) || (x >= getGridWidth()) || (y < 0)
                    || (y >= getGridHeight())) {
                throw new IllegalArgumentException();
            }

            if (points[y][x] != Graphics.BLACK) {
                return true;
            }
        }

        return false;
    }

    /**
     * Render a pseudo-3D rectangle.
     * @param g the graphics context
     * @param x the x coordinate of the upper left corner
     * @param y the y coordinate of the upper left corner
     * @param color the basic color in which to draw the rectangle
     */
    public static void draw3DRect(Graphics g, int x, int y, int color) {

        int ts = Tetris.TILE_SIZE - 1;

        g.setColor(color);
        g.fillRect(x, y, ts, ts);
        g.setColor(ThreeDColor.brighter(color));
        g.drawLine(x, y, x, y + ts);
        g.drawLine(x, y, x + ts, y);
        g.setColor(ThreeDColor.darker(color));
        g.drawLine(x + ts, y + 1, x + ts, y + ts);
        g.drawLine(x, y + ts, x + ts, y + ts);
    }

    private String format(int n) {

        String raw = "" + n;

        return pad.substring(0, 4 - raw.length()) + raw;
    }

    /** Paint this instance onto the specified graphics context. */
    public synchronized void paint(Graphics g) {

        g.lock();
        g.setColor(g.getColorOfName(g.BLACK));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(g.getColorOfName(g.WHITE));
        g.drawLine(0, 0, 0, getHeight());
        g.drawLine(46, 0, 46, getHeight());
        g.drawLine(0, getHeight(), 46, getHeight());

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int c = points[i][j];

                if (c == Graphics.BLACK) {
                    continue;
                }

                draw3DRect(g, 1 + j * 5, i * 5, c);
            }
        }

        if (current != null) {
            current.paint(g);
        }

        Piece next = listener.getNext();

        if (next != null) {
            next.paint(g);
        }

        g.setColor(g.getColorOfName(g.SILVER));
        g.setFont(score_font);
        g.drawString("Level: " + listener.getLevel(), 50, 65);
        g.drawString("S: " + format(score.getScore()), 50, 80);
        g.drawString("H: " + format(score.getHighScore()), 50, 95);

        if (game_over) {
            g.setColor(g.getColorOfName(g.GRAY));
            g.setFont(game_over_font);
            g.drawString("GAME", 6, 51);
            g.drawString("OVER", 9, 66);
            g.setColor(g.getColorOfName(g.RED));
            g.setFont(game_over_font);
            g.drawString("GAME", 5, 50);
            g.drawString("OVER", 8, 65);
        }

        g.unlock(true);
    }

    /** Process key events. */
    public void processEvent(int type, int param) {

        if (type == Display.KEY_PRESSED_EVENT) {
            listener.keyPressed(param);
        }
    }
}

