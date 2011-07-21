//#condition polish.midp || polish.usePolishGui
/*
 * Copyright (c) 2010 Robert Virkus / Enough Software
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

package de.enough.polish.processing;

import de.enough.polish.util.BitMapFont;
import de.enough.polish.util.BitMapFontViewer;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * Placeholder class that emulates the behavior of a Mobile Processing PFont, for compatibility with existing Mobile Processing scripts.
 * The class actually uses J2ME Polish bitmap fonts instead.
 *
 * @author Ovidiu
 */
public class PFont {

    protected Font platformFont = null ;
    protected BitMapFont bitmapFont = null ;
    protected int color = 0x000000;
    protected int bgColor;
    protected boolean useNativeFontColor = false ;

    public static int FACE_SYSTEM = Font.FACE_SYSTEM ;
    public static int FACE_MONOSPACE = Font.FACE_MONOSPACE ;
    public static int FACE_PROPORTIONAL = Font.FACE_PROPORTIONAL ;
    public static int SIZE_LARGE = Font.SIZE_LARGE ;
    public static int SIZE_MEDIUM = Font.SIZE_MEDIUM ;
    public static int SIZE_SMALL = Font.SIZE_SMALL ;
    public static int STYLE_PLAIN = Font.STYLE_PLAIN ;
    public static int STYLE_BOLD = Font.STYLE_BOLD ;
    public static int STYLE_ITALIC = Font.STYLE_ITALIC ;
    public static int STYLE_UNDERLINED = Font.STYLE_UNDERLINED ;

    /**
     * Creates a new PFont instance
     * @param font the font to use
     */
    public PFont(Font font)
    {
        platformFont = font ;
    }

    /**
     * Creates a new PFont instance from a J2MEPolish font
     * @param fontUrl the URL from where to load the font
     */
    public PFont(String fontUrl)
    {
        bitmapFont = BitMapFont.getInstance(fontUrl);
        useNativeFontColor = true ;
    }

    /**
     * Creates a new PFont instance from a J2MEPolish font
     * @param fontUrl the URL from where to load the font
     * @param textColor the text color
     * @param bgColor the background color
     */
    public PFont(String fontUrl, color textColor, color bgColor)
    {
        this.color = textColor.color;
        this.bgColor = bgColor.color;
        this.useNativeFontColor = false;
        bitmapFont = BitMapFont.getInstance(fontUrl);
    }

    /**
     * Gets the font height
     * @return the font height
     */
    public int getHeight()
    {
        if ( platformFont != null )
        {
            return platformFont.getHeight();
        }
        else
        {
            return bitmapFont.getFontHeight();
        }
    }

    /**
     * Returns the font's baseline position
     * @return the baseline position of the font
     */
    public int getBaseline()
    {
        if ( platformFont != null )
        {
            return platformFont.getBaselinePosition() ;
        }
        else
        {
            return getHeight() ;
        }
    }

    /**
     * Returns the width of the given char arary or of a sub-array of thereof when drawn with the given font
     * @param ch the char array in question
     * @param offset the offset at which to start the subset
     * @param length the length of the subset
     * @return the width in pixels
     */
    public int charsWidth(char[] ch, int offset, int length) {
        int result = 0;
        int temp;
        if (platformFont != null) {
            result = platformFont.charsWidth(ch, offset, length);
        } else {
            for (int i = offset, end = offset + length; i < end; i++) {
               temp = bitmapFont.charWidth(ch[i]);
               if ( temp == -1 )
               {
                   result += bitmapFont.charWidth('a');
               }
               else
               {
                   result += temp;
               }
            }
        }
        return result;
    }

    /**
     * Returns the width of the given char when drawn with the current font
     * @param ch the char in question
     * @return the width in pixels
     */
    public int charWidth(char ch) {
        int result = 0;
        int temp;
        if (platformFont != null) {
            result = platformFont.charWidth(ch);
        } else {
               temp = bitmapFont.charWidth(ch);
               if ( temp == -1 )
               {
                   result += bitmapFont.charWidth('a');
               }
               else
               {
                   result += temp;
               }
        }
        return result;
    }

    /**
     * Returns the width of the given string when drawn with the current font 
     * @param str the string in question
     * @return the width in pixels
     */
    public int stringWidth(String str) {
        int result;
        if (platformFont != null) {
            result = platformFont.stringWidth(str);
        } else {
            result = substringWidth(str, 0, str.length());
        }
        return result;
    }

    /**
     * Returns the width of a substring when drawn with the current font
     * @param str the whole string
     * @param offset the offset of the desired substring
     * @param length the substring length
     * @return the width
     */
    public int substringWidth(String str, int offset, int length) {
        int result = 0;
        int temp ;
        if (platformFont != null) {
            result = platformFont.substringWidth(str, offset, length);
        } else {
            int index;
            for (int i = offset, end = offset + length; i < end; i++) {
                temp = bitmapFont.charWidth(str.charAt(i));
               if ( temp == -1 )
               {
                   result += bitmapFont.charWidth('a');
               }
               else
               {
                   result += temp;
               }
            }
        }
        return result;
    }

    /**
     * Draws a string at the specified position
     * @param g the Graphics object to draw on 
     * @param str the string to draw
     * @param x the x-coordinate of the drawing position
     * @param y the y-coordinate of the drawing position
     * @param textAlign the text alignment to use
     */
    public void draw(Graphics g, String str, int x, int y, int textAlign) {
        if (platformFont != null) {
            //// system font
            g.setFont(platformFont);
            int align = Graphics.TOP;
            if (textAlign == ProcessingInterface.CENTER) {
                align |= Graphics.HCENTER;
            } else if (textAlign == ProcessingInterface.RIGHT) {
                align |= Graphics.RIGHT;
            } else {
                align |= Graphics.LEFT;
            }
            g.drawString(str, x, y - platformFont.getBaselinePosition(), align);
        } else {
            if (textAlign != ProcessingInterface.LEFT) {
                int width = stringWidth(str);
                if (textAlign == ProcessingInterface.CENTER) {
                    x -= width >> 1;
                } else if (textAlign == ProcessingInterface.RIGHT) {
                    x -= width;
                }
            }

            BitMapFontViewer bfv = null;
            if (useNativeFontColor)
            {
               bfv = bitmapFont.getViewer(str);
            }
            else
            {
                bfv = bitmapFont.getViewer(str, color);
            }

            bfv.paint(x, y - bitmapFont.getFontHeight() , g);

        }
    }

    /**
     * Returns if the current font is a bitmap one or not
     * @return true if the font is a bitmap font, false otherwise
     */
    public boolean isBitmapFont()
    {
        if ( bitmapFont != null )
        {
            return true;
        }
        else
        {
            return false;
        }

    }

}
