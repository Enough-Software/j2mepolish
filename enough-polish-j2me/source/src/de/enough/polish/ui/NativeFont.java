//#condition polish.usePolishGui

/*
 * Created on Mar 9, 2010 at 5:59:39 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
 * <p>Encapsulates native font management</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface NativeFont {
	/**
	 * Gets the style of the font. The value is an <code>OR'ed</code>
	 * combination of
	 * <code>STYLE_BOLD</code>, <code>STYLE_ITALIC</code>, and
	 * <code>STYLE_UNDERLINED</code>; or the value is
	 * zero (<code>STYLE_PLAIN</code>).
	 * 
	 * @return style of the current font
	 * @see #isPlain()
	 * @see #isBold()
	 * @see #isItalic()
	 */
	int getStyle();

	/**
	 * Gets the size of the font.
	 * 
	 * @return one of SIZE_SMALL, SIZE_MEDIUM, SIZE_LARGE
	 */
	int getSize();

	/**
	 * Gets the face of the font.
	 * 
	 * @return one of FACE_SYSTEM, FACE_PROPORTIONAL, FACE_MONOSPACE
	 */
	int getFace();

	/**
	 * Returns <code>true</code> if the font is plain.
	 * 
	 * @return true if font is plain
	 * @see #getStyle()
	 */
	boolean isPlain();

	/**
	 * Returns <code>true</code> if the font is bold.
	 * 
	 * @return true if font is bold
	 * @see #getStyle()
	 */
	boolean isBold();

	/**
	 * Returns <code>true</code> if the font is italic.
	 * 
	 * @return true if font is italic
	 * @see #getStyle()
	 */
	boolean isItalic();

	/**
	 * Returns <code>true</code> if the font is underlined.
	 * 
	 * @return true if font is underlined
	 * @see #getStyle()
	 */
	boolean isUnderlined();

	/**
	 * Gets the standard height of a line of text in this font. This value
	 * includes sufficient spacing to ensure that lines of text painted this
	 * distance from anchor point to anchor point are spaced as intended by the
	 * font designer and the device. This extra space (leading) occurs below
	 * the text.
	 * 
	 * @return standard height of a line of text in this font (a  non-negative value)
	 */
	int getHeight();

	/**
	 * Gets the distance in pixels from the top of the text to the text's
	 * baseline.
	 * 
	 * @return the distance in pixels from the top of the text to the text's baseline
	 */
	int getBaselinePosition();

	/**
	 * Gets the advance width of the specified character in this Font.
	 * The advance width is the horizontal distance that would be occupied if
	 * <code>ch</code> were to be drawn using this <code>Font</code>,
	 * including inter-character spacing following
	 * <code>ch</code> necessary for proper positioning of subsequent text.
	 * 
	 * @param ch - the character to be measured
	 * @return the total advance width (a non-negative value)
	 */
	int charWidth(char ch);

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
	 * @param ch - the array of characters
	 * @param offset - the index of the first character to measure
	 * @param length - the number of characters to measure
	 * @return the width of the character range
	 * @throws ArrayIndexOutOfBoundsException - if offset and length specify an invalid range
	 * @throws NullPointerException - if ch is null
	 */
	int charsWidth(char[] ch, int offset, int length);

	/**
	 * Gets the total advance width for showing the specified
	 * <code>String</code>
	 * in this <code>Font</code>.
	 * The advance width is the horizontal distance that would be occupied if
	 * <code>str</code> were to be drawn using this <code>Font</code>,
	 * including inter-character spacing following
	 * <code>str</code> necessary for proper positioning of subsequent text.
	 * 
	 * @param str - the String to be measured
	 * @return the total advance width
	 * @throws NullPointerException - if str is null
	 */
	int stringWidth( String str);

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
	 * @param str - the String to be measured
	 * @param offset - zero-based index of first character in the substring
	 * @param len - length of the substring
	 * @return the total advance width
	 * @throws StringIndexOutOfBoundsException - if offset and length specify an invalid range
	 * @throws NullPointerException - if str is null
	 */
	int substringWidth( String str, int offset, int len);

}
