/*
 * @(#)Tetris.java	1.11 00/09/05 @(#)
 *
 * Copyright 2000 Sun Microsystems, Inc. All rights reserved.
 */
package tetris;



import com.nttdocomo.ui.IApplication;
import com.nttdocomo.ui.Display;


/** The game of Tetris. */
public class Tetris extends IApplication implements Runnable {

    public static final int GRID_WIDTH  = 9;
    public static final int GRID_HEIGHT = 20;
    public static final int TILE_SIZE   = 5;
    private Grid            grid;
    private Score           score = new Score();
    private Piece           piece;
    private Piece           next;
    private boolean         gameOver;
    private int             dropped;
    private int             delay;
    private int             count;
    private int             grace;
    private int             level;

    /** Set up game. */
    public void start() {

        reset();

        grid = new Grid(GRID_WIDTH, GRID_HEIGHT, score, this);

        Display.setCurrent(grid);

        Thread runner = new Thread(this);

        runner.start();
    }

    private void reset() {

        score.reset();

        gameOver = false;
        dropped  = 0;
        delay    = 6;
        count    = 0;
        grace    = 0;
        level    = 0;
    }

    /** Runnable interface. */
    public void run() {

        piece = Piece.createRandomPiece(grid);
        next  = Piece.createRandomPiece(grid);

        next.setLocation(13, 5);
        grid.addPiece(piece);

        for (;;) {
            try {
                Thread.sleep(50);
            } catch (Exception e) {}

            if (gameOver) {
                continue;
            }

            grid.repaint();

            if ((++count % delay == 0) &&!piece.down() && (grace == 0)) {
                grace = 6;

                if (piece.getY() <= 0) {
                    gameOver = true;

                    grid.setGameOver();
                    score.save();

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException x) {}

                    newGame();

                    continue;
                }
            }

            if ((grace > 0) && (--grace == 0)) {
                score.add(piece.drop());

                piece = next;

                piece.resetPosition();

                int n = grid.addPiece(piece);

                next = Piece.createRandomPiece(grid);

                next.setLocation(13, 5);

                int bonus = 2;

                for (int i = 0; i < n; i++) {
                    score.add(bonus += 2);
                }

                if ((++dropped % 30 == 0) && (delay > 1)) {
                    --delay;
                    level++;
                }
            }
        }
    }

    /** Get the current level of the game. */
    public int getLevel() {
        return level + 1;
    }

    /** Get the next piece that will be dropped. */
    public Piece getNext() {
        return next;
    }

    private void newGame() {

        grid.reset();

        piece = Piece.createRandomPiece(grid);

        grid.addPiece(piece);
        score.save();
        reset();
    }

    /** Key event handler. */
    public void keyPressed(int key) {

        if (key == Display.KEY_SOFT2) {
            terminate();
        }

        if (key == Display.KEY_SOFT1) {
            newGame();
        }

        if (gameOver) {
            return;
        }

        if (key == Display.KEY_LEFT) {
            piece.left();
        }

        if (key == Display.KEY_RIGHT) {
            piece.right();
        }

        if (key == Display.KEY_DOWN) {
            score.add(piece.drop());
        }

        if (key == Display.KEY_SELECT) {
            piece.rotateRight();
        }
    }
}

