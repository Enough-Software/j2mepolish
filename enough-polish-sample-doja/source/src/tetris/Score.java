/*
 * @(#)Score.java	1.5 00/09/05 @(#)
 *
 * Copyright 2000 Sun Microsystems, Inc. All rights reserved.
 */
package tetris;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;


/**
 * A class that keeps track of the current
 * score, and a high score. Persistance is done
 * through the use of the scratchpad.
 */
public class Score {

    private int score;
    private int hi_score;

    /** Create a new instance of this class. */
    public Score() {

        score    = 0;
        hi_score = 0;

        load();
    }

    /** Set current score to 0. */
    public void reset() {
        score = 0;
    }

    /** Add one to current score. */
    public void add() {
        add(1);
    }

    /** Add <code>x</code> to current score. */
    public void add(int x) {

        score += x;

        if (score > hi_score) {
            hi_score = score;

            save();
        }
    }

    /** Get the current score. */
    public int getScore() {
        return score;
    }

    /** Get the high score. */
    public int getHighScore() {
        return hi_score;
    }

    /** Load the high score from persistant store. */
    public void load() {

        try {
            InputStream in = Connector.openDataInputStream("scratchpad:///0");

            hi_score = in.read() << 24;
            hi_score |= (in.read() << 16);
            hi_score |= (in.read() << 8);
            hi_score |= in.read();

            in.close();
        } catch (IOException e) {
            System.out.println("Score.load() failed: " + e.getMessage());
        }
    }

    /** Save the high score to persistant store. */
    public void save() {

        try {
            OutputStream out =
                Connector.openDataOutputStream("scratchpad:///0");

            out.write((hi_score >>> 24) & 0xff);
            out.write((hi_score >>> 16) & 0xff);
            out.write((hi_score >>> 8) & 0xff);
            out.write(hi_score & 0xff);
            out.close();
        } catch (IOException e) {
            System.out.println("Score.save() failed: " + e.getMessage());
        }
    }
}

