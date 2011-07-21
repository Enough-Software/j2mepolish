/*
 * @(#)ThreeDColor.java	1.4 00/09/05 @(#)
 *
 * Copyright 2000 Sun Microsystems, Inc. All rights reserved.
 */
package tetris;

import com.nttdocomo.ui.Graphics;

/*
 * Provide functionality required for pseudo-3D
 * color highlighting and shadowing.
 */
public class ThreeDColor {

    /** Light Gray */
    public static int lightGray = Graphics.getColorOfRGB(0xc0, 0xc0, 0xc0);

    /** Blue */
    public static int blue = Graphics.getColorOfRGB(0x00, 0x00, 0xff);

    /** Red */
    public static int red = Graphics.getColorOfRGB(0xff, 0x00, 0x00);

    /** Yellow */
    public static int yellow = Graphics.getColorOfRGB(0xff, 0xff, 0x00);

    /** Green */
    public static int green = Graphics.getColorOfRGB(0x00, 0xc0, 0x00);

    /**
     * For a given color, return a shade suitable
     * for highlighting.
     */
    public static int brighter(int color) {

        if (color == lightGray) {
            return Graphics.getColorOfRGB(0xff, 0xff, 0xff);
        }

        if (color == blue) {
            return Graphics.getColorOfRGB(0x00, 0xb2, 0xb2);
        }

        if (color == red) {
            return Graphics.getColorOfRGB(0xff, 0xaf, 0xaf);
        }

        if (color == yellow) {
            return Graphics.getColorOfRGB(0xff, 0xff, 0xaf);
        }

        if (color == green) {
            return Graphics.getColorOfRGB(0xaf, 0xff, 0x00);
        }

        return -1;
    }

    /**
     * For a given color, return a shade suitable
     * for shadowing.
     */
    public static int darker(int color) {

        if (color == lightGray) {
            return Graphics.getColorOfRGB(0x80, 0x80, 0x80);
        }

        if (color == blue) {
            return Graphics.getColorOfRGB(0x40, 0x40, 0x40);
        }

        if (color == red) {
            return Graphics.getColorOfRGB(0xb2, 0x00, 0x00);
        }

        if (color == yellow) {
            return Graphics.getColorOfRGB(0xc8, 0xc8, 0x00);
        }

        if (color == green) {
            return Graphics.getColorOfRGB(0x00, 0xb2, 0x00);
        }

        return -1;
    }
}
