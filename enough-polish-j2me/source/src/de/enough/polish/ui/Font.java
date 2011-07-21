//#condition polish.usePolishGui || polish.midp
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

package de.enough.polish.ui;



/**
 *
 * This class provides an abstraction for platform-independent Font objects.
 * 
 * The <code>Font</code> class represents fonts and font
 * metrics. <code>Fonts</code> cannot be
 * created by applications. Instead, applications query for fonts
 * based on
 * font attributes and the system will attempt to provide a font that
 * matches
 * the requested attributes as closely as possible.
 *
 * <p> A <code>Font's</code> attributes are style, size, and face. Values for
 * attributes must be specified in terms of symbolic constants. Values for
 * the style attribute may be combined using the bit-wise
 * <code>OR</code> operator,
 * whereas values for the other attributes may not be combined. For example,
 * the value </p>
 *
 * <p> <code>
 * STYLE_BOLD | STYLE_ITALIC
 * </code> </p>
 *
 * <p> may be used to specify a bold-italic font; however </p>
 *
 * <p> <code>
 * SIZE_LARGE | SIZE_SMALL
 * </code> </p>
 *
 * <p> is illegal. </p>
 *
 * <p> The values of these constants are arranged so that zero is valid for
 * each attribute and can be used to specify a reasonable default font
 * for the system. For clarity of programming, the following symbolic
 * constants are provided and are defined to have values of zero: </p>
 *
 * <p> <ul>
 * <li> <code> STYLE_PLAIN </code> </li>
 * <li> <code> SIZE_MEDIUM </code> </li>
 * <li> <code> FACE_SYSTEM </code> </li>
 * </ul> </p>
 *
 * <p> Values for other attributes are arranged to have disjoint bit patterns
 * in order to raise errors if they are inadvertently misused (for example,
 * using <code>FACE_PROPORTIONAL</code> where a style is
 * required). However, the values
 * for the different attributes are not intended to be combined with each
 * other. </p>
 *
 * @author Ovidiu Iliescu
 */

public class Font {

    protected
    	//#if polish.build.classes.NativeFont:defined
    		//#= ${polish.build.classes.NativeFont}
    	//#else
    		javax.microedition.lcdui.Font
    	//#endif
    	font = null ;
    //#if polish.usePolishGui
		private NativeFont nativeFont;
	//#endif
	/**
	 * The plain style constant. This may be combined with the
	 * other style constants for mixed styles.
	 * 
	 * <P>Value <code>0</code> is assigned to <code>STYLE_PLAIN</code>.</P>
	 * 
	 */
	public static final int STYLE_PLAIN = 0;

	/**
	 * The bold style constant. This may be combined with the
	 * other style constants for mixed styles.
	 * 
	 * <P>Value <code>1</code> is assigned to <code>STYLE_BOLD</code>.</P>
	 * 
	 */
	public static final int STYLE_BOLD = 1;

	/**
	 * The italicized style constant. This may be combined with
	 * the other style constants for mixed styles.
	 * 
	 * <P>Value <code>2</code> is assigned to <code>STYLE_ITALIC</code>.</P>
	 * 
	 */
	public static final int STYLE_ITALIC = 2;

	/**
	 * The underlined style constant. This may be combined with
	 * the other style constants for mixed styles.
	 * 
	 * <P>Value <code>4</code> is assigned to <code>STYLE_UNDERLINED</code>.</P>
	 * 
	 */
	public static final int STYLE_UNDERLINED = 4;

	/**
	 * The &quot;small&quot; system-dependent font size.
	 * 
	 * <P>Value <code>8</code> is assigned to <code>STYLE_SMALL</code>.</P>
	 * 
	 */
	public static final int SIZE_SMALL = 8;

	/**
	 * The &quot;medium&quot; system-dependent font size.
	 * 
	 * <P>Value <code>0</code> is assigned to <code>STYLE_MEDIUM</code>.</P>
	 */
	public static final int SIZE_MEDIUM = 0;

	/**
	 * The &quot;large&quot; system-dependent font size.
	 * 
	 * <P>Value <code>16</code> is assigned to <code>SIZE_LARGE</code>.</P>
	 */
	public static final int SIZE_LARGE = 16;

	/**
	 * The &quot;system&quot; font face.
	 * 
	 * <P>Value <code>0</code> is assigned to <code>FACE_SYSTEM</code>.</P>
	 * 
	 */
	public static final int FACE_SYSTEM = 0;

	/**
	 * The &quot;monospace&quot; font face.
	 * 
	 * <P>Value <code>32</code> is assigned to <code>FACE_MONOSPACE</code>.</P>
	 */
	public static final int FACE_MONOSPACE = 32;

	/**
	 * The &quot;proportional&quot; font face.
	 * 
	 * <P>Value <code>64</code> is assigned to
	 * <code>FACE_PROPORTIONAL</code>.</P>
	 */
	public static final int FACE_PROPORTIONAL = 64;

	/**
	 * Default font specifier used to draw Item and Screen contents.
	 * 
	 * <code>FONT_STATIC_TEXT</code> has the value <code>0</code>.
	 * 
	 * @since MIDP 2.0
	 */
	public static final int FONT_STATIC_TEXT = 0;

	/**
	 * Font specifier used by the implementation to draw text input by
	 * a user.
	 * 
	 * <code>FONT_INPUT_TEXT</code> has the value <code>1</code>.
	 * 
	 * @since MIDP 2.0
	 */
	public static final int FONT_INPUT_TEXT = 1;

    protected Font ( 
        	//#if polish.build.classes.NativeFont:defined
    			//#= ${polish.build.classes.NativeFont}
	    	//#else
	    		javax.microedition.lcdui.Font
	    	//#endif
    		font)
    {
        this.font = font;
    }

    //#if polish.midp2
    /**
     * Gets the <code>Font</code> used by the high level user interface
     * for the <code>fontSpecifier</code> passed in. It should be used
     * by subclasses of
     * <code>CustomItem</code> and <code>Canvas</code> to match user
     * interface on the device.
     *
     * @param fontSpecifier one of <code>FONT_INPUT_TEXT</code>, or
     * <code>FONT_STATIC_TEXT</code>
     * @return font that corresponds to the passed in font specifier
     * @throws IllegalArgumentException if <code>fontSpecifier</code> is not
     * a valid fontSpecifier
     */
    public static Font getFont(int fontSpecifier) {
        return new Font ( 
            	//#if polish.build.classes.NativeFont:defined
        			//#= ${polish.build.classes.NativeFont}
	        	//#else
	        		javax.microedition.lcdui.Font
	        	//#endif
        		.getFont(fontSpecifier) );
    }
    //#endif

    /**
     * Gets the default font of the system.
     * @return the default font
     */
    public static Font getDefaultFont() {
        return new Font ( 
            	//#if polish.build.classes.NativeFont:defined
        			//#= ${polish.build.classes.NativeFont}
	        	//#else
	        		javax.microedition.lcdui.Font
	        	//#endif
        		.getDefaultFont() );
    }

    /**
     * Obtains an object representing a font having the specified face, style,
     * and size. If a matching font does not exist, the system will
     * attempt to provide the closest match. This method <em>always</em>
     * returns
     * a valid font object, even if it is not a close match to the request.
     *
     * @param inp_face one of <code>FACE_SYSTEM</code>,
     * <code>FACE_MONOSPACE</code>, or <code>FACE_PROPORTIONAL</code>
     * @param inp_style <code>STYLE_PLAIN</code>, or a combination of
     * <code>STYLE_BOLD</code>,
     * <code>STYLE_ITALIC</code>, and <code>STYLE_UNDERLINED</code>
     * @param inp_size one of <code>SIZE_SMALL</code>, <code>SIZE_MEDIUM</code>,
     * or <code>SIZE_LARGE</code>
     * @return instance the nearest font found
     * @throws IllegalArgumentException if <code>face</code>,
     * <code>style</code>, or <code>size</code> are not
     * legal values
     */
    public static Font getFont(int inp_face, int inp_style, int inp_size) {
        return new Font ( 
            	//#if polish.build.classes.NativeFont:defined
        			//#= ${polish.build.classes.NativeFont}
	        	//#else
	        		javax.microedition.lcdui.Font
	        	//#endif
        		.getFont(inp_face, inp_style, inp_size));
    }

    /**
     * Gets the style of the font. The value is an <code>OR'ed</code>
     * combination of
     * <code>STYLE_BOLD</code>, <code>STYLE_ITALIC</code>, and
     * <code>STYLE_UNDERLINED</code>; or the value is
     * zero (<code>STYLE_PLAIN</code>).
     * @return style of the current font
     *
     * @see #isPlain()
     * @see #isBold()
     * @see #isItalic()
     */
    public int getStyle() {
        return font.getStyle();
    };

    /**
     * Gets the size of the font.
     *
     * @return one of <code>SIZE_SMALL</code>, <code>SIZE_MEDIUM</code>,
     * <code>SIZE_LARGE</code>
     */
    public int getSize() {
        return font.getSize();
    }

    /**
     * Gets the face of the font.
     *
     * @return one of <code>FACE_SYSTEM</code>,
     * <code>FACE_PROPORTIONAL</code>, <code>FACE_MONOSPACE</code>
     */
    public int getFace() {
        return font.getFace();
    }

    /**
     * Returns <code>true</code> if the font is plain.
     * @see #getStyle()
     * @return <code>true</code> if font is plain
     */
    public boolean isPlain() {
        return font.isPlain();
    }

    /**
     * Returns <code>true</code> if the font is bold.
     * @see #getStyle()
     * @return <code>true</code> if font is bold
     */
    public boolean isBold() {
        return font.isBold() ;
    }

    /**
     * Returns <code>true</code> if the font is italic.
     * @see #getStyle()
     * @return <code>true</code> if font is italic
     */
    public boolean isItalic() {
        return font.isItalic();
    }

    /**
     * Returns <code>true</code> if the font is underlined.
     * @see #getStyle()
     * @return <code>true</code> if font is underlined
     */
    public boolean isUnderlined() {
        return font.isUnderlined();
    }

    /**
     * Gets the standard height of a line of text in this font. This value
     * includes sufficient spacing to ensure that lines of text painted this
     * distance from anchor point to anchor point are spaced as intended by the
     * font designer and the device. This extra space (leading) occurs below
     * the text.
     * @return standard height of a line of text in this font (a
     * non-negative value)
     */
    public int getHeight() {
        return font.getHeight();
    }

    /**
     * Gets the distance in pixels from the top of the text to the text's
     * baseline.
     * @return the distance in pixels from the top of the text to the text's
     * baseline
     */
    public int getBaselinePosition() {
        return font.getBaselinePosition();
    }

    /**
     * Gets the advance width of the specified character in this Font.
     * The advance width is the horizontal distance that would be occupied if
     * <code>ch</code> were to be drawn using this <code>Font</code>,
     * including inter-character spacing following
     * <code>ch</code> necessary for proper positioning of subsequent text.
     *
     * @param ch the character to be measured
     * @return the total advance width (a non-negative value)
     */
    public int charWidth(char ch) {
        return font.charWidth(ch);
    }

    /**
     * Returns the advance width of the characters in <code>ch</code>,
     * starting at the specified offset and for the specified number of
     * characters (length).
     * The advance width is the horizontal distance that would be occupied if
     * the characters were to be drawn using this <code>Font</code>,
     * including inter-character spacing following
     * the characters necessary for proper positioning of subsequent text.
     *
     * <p>The <code>offset</code> and <code>length</code> parameters must
     * specify a valid range of characters
     * within the character array <code>ch</code>. The <code>offset</code>
     * parameter must be within the
     * range <code>[0..(ch.length)]</code>, inclusive.
     * The <code>length</code> parameter must be a non-negative
     * integer such that <code>(offset + length) &lt;= ch.length</code>.</p>
     *
     * @param ch the array of characters
     * @param offset the index of the first character to measure
     * @param length the number of characters to measure
     * @return the width of the character range
     * @throws ArrayIndexOutOfBoundsException if <code>offset</code> and
     * <code>length</code> specify an
     * invalid range
     * @throws NullPointerException if <code>ch</code> is <code>null</code>
     */
    public int charsWidth(char[] ch, int offset, int length) {
        return font.charsWidth(ch, offset, length);
    }
    
    /**
     * Gets the total advance width for showing the specified
     * <code>String</code>
     * in this <code>Font</code>.
     * The advance width is the horizontal distance that would be occupied if
     * <code>str</code> were to be drawn using this <code>Font</code>, 
     * including inter-character spacing following
     * <code>str</code> necessary for proper positioning of subsequent text.
     * 
     * @param str the <code>String</code> to be measured
     * @return the total advance width
     * @throws NullPointerException if <code>str</code> is <code>null</code>
     */
    public int stringWidth(String str) {
        return font.stringWidth(str);
    }

    /**
     * Gets the total advance width for showing the specified substring in this
     * <code>Font</code>.
     * The advance width is the horizontal distance that would be occupied if
     * the substring were to be drawn using this <code>Font</code>,
     * including inter-character spacing following
     * the substring necessary for proper positioning of subsequent text.
     *
     * <p>
     * The <code>offset</code> and <code>len</code> parameters must
     * specify a valid range of characters
     * within <code>str</code>. The <code>offset</code> parameter must
     * be within the
     * range <code>[0..(str.length())]</code>, inclusive.
     * The <code>len</code> parameter must be a non-negative
     * integer such that <code>(offset + len) &lt;= str.length()</code>.
     * </p>
     *
     * @param str the <code>String</code> to be measured
     * @param offset zero-based index of first character in the substring
     * @param len length of the substring
     * @return the total advance width
     * @throws StringIndexOutOfBoundsException if <code>offset</code> and
     * <code>length</code> specify an
     * invalid range
     * @throws NullPointerException if <code>str</code> is <code>null</code>
     */
    public int substringWidth(String str, int offset, int len) {
        return font.substringWidth(str, offset, len);
    }

    /**
     * Retrieves access to the native font implementation.
     * @return the native font
     */
	public 
	//#if polish.build.classes.NativeFont:defined
		//#= ${polish.build.classes.NativeFont}
	//#else
		javax.microedition.lcdui.Font
	//#endif
	getNativeFont() {
		return this.font;
	} 



}
