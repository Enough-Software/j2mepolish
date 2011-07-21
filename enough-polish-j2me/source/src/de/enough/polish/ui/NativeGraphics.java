//#condition polish.usePolishGui

/*
 * Created on Mar 8, 2010 at 9:39:58 PM.
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
 * <p>Abstracts the natively used graphics class.</p>
 * <p>When the preprocessing variable polish.build.classes.NativeGraphics is defined, that class will be used directly. 
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface NativeGraphics {
	
	/**
	 * Translates the origin of the graphics context to the point
	 * <code>(x, y)</code> in the current coordinate system. All coordinates
	 * used in subsequent rendering operations on this graphics
	 * context will be relative to this new origin.<p>
	 * 
	 * The effect of calls to <code>translate()</code> are
	 * cumulative. For example, calling
	 * <code>translate(1, 2)</code> and then <code>translate(3,
	 * 4)</code> results in a translation of
	 * <code>(4, 6)</code>. <p>
	 * 
	 * The application can set an absolute origin <code>(ax,
	 * ay)</code> using the following
	 * technique:<p>
	 * <code>
	 * g.translate(ax - g.getTranslateX(), ay - g.getTranslateY())
	 * </code><p>
	 * 
	 * @param x - the x coordinate of the new translation origin
	 * @param y - the y coordinate of the new translation origin
	 * @see #getTranslateX()
	 * @see #getTranslateY()
	 */
	void translate(int x, int y);

	/**
	 * Gets the X coordinate of the translated origin of this graphics context.
	 * 
	 * @return X of current origin
	 */
	int getTranslateX();

	/**
	 * Gets the Y coordinate of the translated origin of this graphics context.
	 * 
	 * @return Y of current origin
	 */
	int getTranslateY();

	/**
	 * Gets the current color.
	 * 
	 * @return an integer in form 0x00RRGGBB
	 * @see #setColor(int, int, int)
	 */
	int getColor();

	/**
	 * Gets the red component of the current color.
	 * 
	 * @return integer value in range 0-255
	 * @see #setColor(int, int, int)
	 */
	int getRedComponent();

	/**
	 * Gets the green component of the current color.
	 * 
	 * @return integer value in range 0-255
	 * @see #setColor(int, int, int)
	 */
	int getGreenComponent();

	/**
	 * Gets the blue component of the current color.
	 * 
	 * @return integer value in range 0-255
	 * @see #setColor(int, int, int)
	 */
	int getBlueComponent();

	/**
	 * Gets the current grayscale value of the color being used for rendering
	 * operations. If the color was set by
	 * <code>setGrayScale()</code>, that value is simply
	 * returned. If the color was set by one of the methods that allows setting
	 * of the red, green, and blue components, the value returned is
	 * computed from
	 * the RGB color components (possibly in a device-specific fashion)
	 * that best
	 * approximates the brightness of that color.
	 * 
	 * @return integer value in range 0-255
	 * @see #setGrayScale(int)
	 */
	int getGrayScale();

	/**
	 * Sets the current color to the specified RGB values. All subsequent
	 * rendering operations will use this specified color.
	 * 
	 * @param red - the red component of the color being set in range 0-255
	 * @param green - the green component of the color being set in range 0-255
	 * @param blue - the blue component of the color being set in range 0-255
	 * @throws IllegalArgumentException - if any of the color components are outside of range 0-255
	 * @see #getColor()
	 */
	void setColor(int red, int green, int blue);

	/**
	 * Sets the current color to the specified RGB values. All subsequent
	 * rendering operations will use this specified color. The RGB value
	 * passed in is interpreted with the least significant eight bits
	 * giving the blue component, the next eight more significant bits
	 * giving the green component, and the next eight more significant
	 * bits giving the red component. That is to say, the color component
	 * is specified in the form of <code>0x00RRGGBB</code>. The high
	 * order byte of
	 * this value is ignored.
	 * 
	 * @param rgb - the color being set
	 * @see #getColor()
	 */
	void setColor(int rgb);

	/**
	 * Sets the current grayscale to be used for all subsequent
	 * rendering operations. For monochrome displays, the behavior
	 * is clear. For color displays, this sets the color for all
	 * subsequent drawing operations to be a gray color equivalent
	 * to the value passed in. The value must be in the range
	 * <code>0-255</code>.
	 * 
	 * @param value - the desired grayscale value
	 * @throws IllegalArgumentException - if the gray value is out of range
	 * @see #getGrayScale()
	 */
	void setGrayScale(int value);

	/**
	 * Gets the current font.
	 * 
	 * @return current font
	 * @see Font
	 * @see #setFont(Font)
	 */
	//#if polish.midp
	Font getFont();
	//#endif

	/**
	 * Sets the stroke style used for drawing lines, arcs, rectangles, and
	 * rounded rectangles.  This does not affect fill, text, and image
	 * operations.
	 * 
	 * @param style - can be SOLID or DOTTED
	 * @throws IllegalArgumentException - if the style is illegal
	 * @see #getStrokeStyle()
	 */
	void setStrokeStyle(int style);

	/**
	 * Gets the stroke style used for drawing operations.
	 * 
	 * @return stroke style, SOLID or DOTTED
	 * @see #setStrokeStyle(int)
	 */
	int getStrokeStyle();

	/**
	 * Sets the font for all subsequent text rendering operations.  If font is
	 * <code>null</code>, it is equivalent to
	 * <code>setFont(Font.getDefaultFont())</code>.
	 * 
	 * @param font - the specified font
	 * @see Font
	 * @see #getFont()
	 * @see #drawString(java.lang.String, int, int, int)
	 * @see #drawChars(char[], int, int, int, int, int)
	 */
	//#if polish.midp
	void setFont( Font font);
	//#endif

	/**
	 * Gets the X offset of the current clipping area, relative
	 * to the coordinate system origin of this graphics context.
	 * Separating the <code>getClip</code> operation into two methods returning
	 * integers is more performance and memory efficient than one
	 * <code>getClip()</code> call returning an object.
	 * 
	 * @return X offset of the current clipping area
	 * @see #clipRect(int, int, int, int)
	 * @see #setClip(int, int, int, int)
	 */
	int getClipX();

	/**
	 * Gets the Y offset of the current clipping area, relative
	 * to the coordinate system origin of this graphics context.
	 * Separating the <code>getClip</code> operation into two methods returning
	 * integers is more performance and memory efficient than one
	 * <code>getClip()</code> call returning an object.
	 * 
	 * @return Y offset of the current clipping area
	 * @see #clipRect(int, int, int, int)
	 * @see #setClip(int, int, int, int)
	 */
	int getClipY();

	/**
	 * Gets the width of the current clipping area.
	 * 
	 * @return width of the current clipping area.
	 * @see #clipRect(int, int, int, int)
	 * @see #setClip(int, int, int, int)
	 */
	int getClipWidth();

	/**
	 * Gets the height of the current clipping area.
	 * 
	 * @return height of the current clipping area.
	 * @see #clipRect(int, int, int, int)
	 * @see #setClip(int, int, int, int)
	 */
	int getClipHeight();

	/**
	 * Intersects the current clip with the specified rectangle.
	 * The resulting clipping area is the intersection of the current
	 * clipping area and the specified rectangle.
	 * This method can only be used to make the current clip smaller.
	 * To set the current clip larger, use the <code>setClip</code> method.
	 * Rendering operations have no effect outside of the clipping area.
	 * 
	 * @param x - the x coordinate of the rectangle to intersect the clip with
	 * @param y - the y coordinate of the rectangle to intersect the clip with
	 * @param width - the width of the rectangle to intersect the clip with
	 * @param height - the height of the rectangle to intersect the clip with
	 * @see #setClip(int, int, int, int)
	 */
	void clipRect(int x, int y, int width, int height);

	/**
	 * Sets the current clip to the rectangle specified by the
	 * given coordinates.
	 * Rendering operations have no effect outside of the clipping area.
	 * 
	 * @param x - the x coordinate of the new clip rectangle
	 * @param y - the y coordinate of the new clip rectangle
	 * @param width - the width of the new clip rectangle
	 * @param height - the height of the new clip rectangle
	 * @see #clipRect(int, int, int, int)
	 */
	void setClip(int x, int y, int width, int height);

	/**
	 * Draws a line between the coordinates <code>(x1,y1)</code> and
	 * <code>(x2,y2)</code> using
	 * the current color and stroke style.
	 * 
	 * @param x1 - the x coordinate of the start of the line
	 * @param y1 - the y coordinate of the start of the line
	 * @param x2 - the x coordinate of the end of the line
	 * @param y2 - the y coordinate of the end of the line
	 */
	void drawLine(int x1, int y1, int x2, int y2);
	
	/**
	 * Fills the specified rectangle with the current color.
	 * If either width or height is zero or less,
	 * nothing is drawn.
	 * 
	 * @param x - the x coordinate of the rectangle to be filled
	 * @param y - the y coordinate of the rectangle to be filled
	 * @param width - the width of the rectangle to be filled
	 * @param height - the height of the rectangle to be filled
	 * @see #drawRect(int, int, int, int)
	 */
	void fillRect(int x, int y, int width, int height);

	/**
	 * Draws the outline of the specified rectangle using the current
	 * color and stroke style.
	 * The resulting rectangle will cover an area <code>(width + 1)</code>
	 * pixels wide by <code>(height + 1)</code> pixels tall.
	 * If either width or height is less than
	 * zero, nothing is drawn.
	 * 
	 * @param x - the x coordinate of the rectangle to be drawn
	 * @param y - the y coordinate of the rectangle to be drawn
	 * @param width - the width of the rectangle to be drawn
	 * @param height - the height of the rectangle to be drawn
	 * @see #fillRect(int, int, int, int)
	 */
	void drawRect(int x, int y, int width, int height);

	/**
	 * Copies a region of the specified source image to a location within
	 * the destination, possibly transforming (rotating and reflecting)
	 * the image data using the chosen transform function.
	 * 
	 * <p>The destination, if it is an image, must not be the same image as
	 * the source image.  If it is, an exception is thrown.  This restriction
	 * is present in order to avoid ill-defined behaviors that might occur if
	 * overlapped, transformed copies were permitted.</p>
	 * 
	 * <p>The transform function used must be one of the following, as defined
	 * in the <A HREF="../../../javax/microedition/lcdui/game/Sprite.html"><CODE>Sprite</CODE></A> class:<br>
	 * 
	 * <code>Sprite.TRANS_NONE</code> - causes the specified image
	 * region to be copied unchanged<br>
	 * <code>Sprite.TRANS_ROT90</code> - causes the specified image
	 * region to be rotated clockwise by 90 degrees.<br>
	 * <code>Sprite.TRANS_ROT180</code> - causes the specified image
	 * region to be rotated clockwise by 180 degrees.<br>
	 * <code>Sprite.TRANS_ROT270</code> - causes the specified image
	 * region to be rotated clockwise by 270 degrees.<br>
	 * <code>Sprite.TRANS_MIRROR</code> - causes the specified image
	 * region to be reflected about its vertical center.<br>
	 * <code>Sprite.TRANS_MIRROR_ROT90</code> - causes the specified image
	 * region to be reflected about its vertical center and then rotated
	 * clockwise by 90 degrees.<br>
	 * <code>Sprite.TRANS_MIRROR_ROT180</code> - causes the specified image
	 * region to be reflected about its vertical center and then rotated
	 * clockwise by 180 degrees.<br>
	 * <code>Sprite.TRANS_MIRROR_ROT270</code> - causes the specified image
	 * region to be reflected about its vertical center and then rotated
	 * clockwise by 270 degrees.<br></p>
	 * 
	 * <p>If the source region contains transparent pixels, the corresponding
	 * pixels in the destination region must be left untouched.  If the source
	 * region contains partially transparent pixels, a compositing operation
	 * must be performed with the destination pixels, leaving all pixels of
	 * the destination region fully opaque.</p>
	 * 
	 * <p> The <code>(x_src, y_src)</code> coordinates are relative to
	 * the upper left
	 * corner of the source image.  The <code>x_src</code>,
	 * <code>y_src</code>, <code>width</code>, and <code>height</code>
	 * parameters specify a rectangular region of the source image.  It is
	 * illegal for this region to extend beyond the bounds of the source
	 * image.  This requires that: </P>
	 * <TABLE BORDER="2">
	 * <TR>
	 * <TD ROWSPAN="1" COLSPAN="1">
	 * <pre><code>
	 * x_src &gt;= 0
	 * y_src &gt;= 0
	 * x_src + width &lt;= source width
	 * y_src + height &lt;= source height    </code></pre>
	 * </TD>
	 * </TR>
	 * </TABLE>
	 * <P>
	 * The <code>(x_dest, y_dest)</code> coordinates are relative to
	 * the coordinate
	 * system of this Graphics object.  It is legal for the destination
	 * area to extend beyond the bounds of the <code>Graphics</code>
	 * object.  Pixels
	 * outside of the bounds of the <code>Graphics</code> object will
	 * not be drawn.</p>
	 * 
	 * <p>The transform is applied to the image data from the region of the
	 * source image, and the result is rendered with its anchor point
	 * positioned at location <code>(x_dest, y_dest)</code> in the
	 * destination.</p>
	 * 
	 * @param src - the source image to copy from
	 * @param x_src - the x coordinate of the upper left corner of the region within the source image to copy
	 * @param y_src - the y coordinate of the upper left corner of the region within the source image to copy
	 * @param width - the width of the region to copy
	 * @param height - the height of the region to copy
	 * @param transform - the desired transformation for the selected region being copied
	 * @param x_dest - the x coordinate of the anchor point in the destination drawing area
	 * @param y_dest - the y coordinate of the anchor point in the destination drawing area
	 * @param anchor - the anchor point for positioning the region within the destination image
	 * @throws IllegalArgumentException - if src is the same image as the destination of this Graphics object
	 * @throws NullPointerException - if src is null
	 * @throws IllegalArgumentException - if transform is invalid
	 * @throws IllegalArgumentException - if anchor is invalid
	 * @throws IllegalArgumentException - if the region to be copied exceeds the bounds of the source image
	 * @since  MIDP 2.0
	 */
	void drawRegion(Image src, int x_src, int y_src, int width, int height, int transform, int x_dest, int y_dest, int anchor);
	
	/**
	 * Draws the outline of the specified rounded corner rectangle
	 * using the current color and stroke style.
	 * The resulting rectangle will cover an area <code>(width +
	 * 1)</code> pixels wide
	 * by <code>(height + 1)</code> pixels tall.
	 * If either <code>width</code> or <code>height</code> is less than
	 * zero, nothing is drawn.
	 * 
	 * @param x - the x coordinate of the rectangle to be drawn
	 * @param y - the y coordinate of the rectangle to be drawn
	 * @param width - the width of the rectangle to be drawn
	 * @param height - the height of the rectangle to be drawn
	 * @param arcWidth - the horizontal diameter of the arc at the four corners
	 * @param arcHeight - the vertical diameter of the arc at the four corners
	 * @see #fillRoundRect(int, int, int, int, int, int)
	 */
	void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

	/**
	 * Fills the specified rounded corner rectangle with the current color.
	 * If either <code>width</code> or <code>height</code> is zero or less,
	 * nothing is drawn.
	 * 
	 * @param x - the x coordinate of the rectangle to be filled
	 * @param y - the y coordinate of the rectangle to be filled
	 * @param width - the width of the rectangle to be filled
	 * @param height - the height of the rectangle to be filled
	 * @param arcWidth - the horizontal diameter of the arc at the four corners
	 * @param arcHeight - the vertical diameter of the arc at the four corners
	 * @see #drawRoundRect(int, int, int, int, int, int)
	 */
	void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

	/**
	 * Fills a circular or elliptical arc covering the specified rectangle.
	 * <p>
	 * The resulting arc begins at <code>startAngle</code> and extends
	 * for <code>arcAngle</code> degrees.
	 * Angles are interpreted such that <code>0</code> degrees
	 * is at the <code>3</code> o'clock position.
	 * A positive value indicates a counter-clockwise rotation
	 * while a negative value indicates a clockwise rotation.
	 * <p>
	 * The center of the arc is the center of the rectangle whose origin
	 * is (<em>x</em>,&nbsp;<em>y</em>) and whose size is specified by the
	 * <code>width</code> and <code>height</code> arguments.
	 * <p>
	 * If either <code>width</code> or <code>height</code> is zero or less,
	 * nothing is drawn.
	 * 
	 * <p> The filled region consists of the &quot;pie wedge&quot;
	 * region bounded
	 * by the arc
	 * segment as if drawn by <code>drawArc()</code>, the radius extending from
	 * the center to
	 * this arc at <code>startAngle</code> degrees, and radius extending
	 * from the
	 * center to this arc at <code>startAngle + arcAngle</code> degrees. </p>
	 * 
	 * <p> The angles are specified relative to the non-square extents of
	 * the bounding rectangle such that <code>45</code> degrees always
	 * falls on the
	 * line from the center of the ellipse to the upper right corner of
	 * the bounding rectangle. As a result, if the bounding rectangle is
	 * noticeably longer in one axis than the other, the angles to the
	 * start and end of the arc segment will be skewed farther along the
	 * longer axis of the bounds. </p>
	 * 
	 * @param x - the x coordinate of the upper-left corner of the arc to be filled.
	 * @param y - the y coordinate of the upper-left corner of the arc to be filled.
	 * @param width - the width of the arc to be filled
	 * @param height - the height of the arc to be filled
	 * @param startAngle - the beginning angle.
	 * @param arcAngle - the angular extent of the arc, relative to the start angle.
	 * @see #drawArc(int, int, int, int, int, int)
	 */
	void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	/**
	 * Draws the outline of a circular or elliptical arc
	 * covering the specified rectangle,
	 * using the current color and stroke style.
	 * <p>
	 * The resulting arc begins at <code>startAngle</code> and extends
	 * for <code>arcAngle</code> degrees, using the current color.
	 * Angles are interpreted such that <code>0</code>&nbsp;degrees
	 * is at the <code>3</code>&nbsp;o'clock position.
	 * A positive value indicates a counter-clockwise rotation
	 * while a negative value indicates a clockwise rotation.
	 * <p>
	 * The center of the arc is the center of the rectangle whose origin
	 * is (<em>x</em>,&nbsp;<em>y</em>) and whose size is specified by the
	 * <code>width</code> and <code>height</code> arguments.
	 * <p>
	 * The resulting arc covers an area
	 * <code>width&nbsp;+&nbsp;1</code> pixels wide
	 * by <code>height&nbsp;+&nbsp;1</code> pixels tall.
	 * If either <code>width</code> or <code>height</code> is less than zero,
	 * nothing is drawn.
	 * 
	 * <p> The angles are specified relative to the non-square extents of
	 * the bounding rectangle such that <code>45</code> degrees always
	 * falls on the
	 * line from the center of the ellipse to the upper right corner of
	 * the bounding rectangle. As a result, if the bounding rectangle is
	 * noticeably longer in one axis than the other, the angles to the
	 * start and end of the arc segment will be skewed farther along the
	 * longer axis of the bounds. </p>
	 * 
	 * @param x - the x coordinate of the upper-left corner of the arc to be drawn
	 * @param y - the y coordinate of the upper-left corner of the arc to be drawn
	 * @param width - the width of the arc to be drawn
	 * @param height - the height of the arc to be drawn
	 * @param startAngle - the beginning angle
	 * @param arcAngle - the angular extent of the arc, relative to the start angle
	 * @see #fillArc(int, int, int, int, int, int)
	 */
	void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	/**
	 * Draws the specified <code>String</code> using the current font and color.
	 * The <code>x,y</code> position is the position of the anchor point.
	 * See <a href="#anchor">anchor points</a>.
	 * 
	 * @param str - the String to be drawn
	 * @param x - the x coordinate of the anchor point
	 * @param y - the y coordinate of the anchor point
	 * @param anchor - the anchor point for positioning the text
	 * @throws NullPointerException - if str is null
	 * @throws IllegalArgumentException - if anchor is not a legal value
	 * @see #drawChars(char[], int, int, int, int, int)
	 */
	void drawString( String str, int x, int y, int anchor);

	/**
	 * Draws the specified <code>String</code> using the current font and color.
	 * The <code>x,y</code> position is the position of the anchor point.
	 * See <a href="#anchor">anchor points</a>.
	 * 
	 * <p>The <code>offset</code> and <code>len</code> parameters must
	 * specify a valid range of characters within
	 * the string <code>str</code>.
	 * The <code>offset</code> parameter must be within the
	 * range <code>[0..(str.length())]</code>, inclusive.
	 * The <code>len</code> parameter
	 * must be a non-negative integer such that
	 * <code>(offset + len) &lt;= str.length()</code>.</p>
	 * 
	 * @param str - the String to be drawn
	 * @param offset - zero-based index of first character in the substring
	 * @param len - length of the substring
	 * @param x - the x coordinate of the anchor point
	 * @param y - the y coordinate of the anchor point
	 * @param anchor - the anchor point for positioning the text
	 * @throws StringIndexOutOfBoundsException - if offset and length do not specify a valid range within the String str
	 * @throws IllegalArgumentException - if anchor is not a legal value
	 * @throws NullPointerException - if str is null
	 * @see #drawString(String, int, int, int).
	 */
	void drawSubstring( String str, int offset, int len, int x, int y, int anchor);
	
	/**
	 * Draws the specified character using the current font and color.
	 * 
	 * @param character - the character to be drawn
	 * @param x - the x coordinate of the anchor point
	 * @param y - the y coordinate of the anchor point
	 * @param anchor - the anchor point for positioning the text; see anchor points
	 * @throws IllegalArgumentException - if anchor is not a legal value
	 * @see #drawString(java.lang.String, int, int, int)
	 * @see #drawChars(char[], int, int, int, int, int)
	 */
	void drawChar(char character, int x, int y, int anchor);

	/**
	 * Draws the specified characters using the current font and color.
	 * 
	 * <p>The <code>offset</code> and <code>length</code> parameters must
	 * specify a valid range of characters within
	 * the character array <code>data</code>.
	 * The <code>offset</code> parameter must be within the
	 * range <code>[0..(data.length)]</code>, inclusive.
	 * The <code>length</code> parameter
	 * must be a non-negative integer such that
	 * <code>(offset + length) &lt;= data.length</code>.</p>
	 * 
	 * @param data - the array of characters to be drawn
	 * @param offset - the start offset in the data
	 * @param length - the number of characters to be drawn
	 * @param x - the x coordinate of the anchor point
	 * @param y - the y coordinate of the anchor point
	 * @param anchor - the anchor point for positioning the text; see anchor points
	 * @throws ArrayIndexOutOfBoundsException - if offset and length do not specify a valid range within the data array
	 * @throws IllegalArgumentException - if anchor is not a legal value
	 * @throws NullPointerException - if data is null
	 * @see #drawString(java.lang.String, int, int, int)
	 */
	void drawChars(char[] data, int offset, int length, int x, int y, int anchor);

	/**
	 * Draws the specified image by using the anchor point.
	 * The image can be drawn in different positions relative to
	 * the anchor point by passing the appropriate position constants.
	 * See <a href="#anchor">anchor points</a>.
	 * 
	 * <p>If the source image contains transparent pixels, the corresponding
	 * pixels in the destination image must be left untouched.  If the source
	 * image contains partially transparent pixels, a compositing operation
	 * must be performed with the destination pixels, leaving all pixels of
	 * the destination image fully opaque.</p>
	 * 
	 * <p>If <code>img</code> is the same as the destination of this Graphics
	 * object, the result is undefined.  For copying areas within an
	 * <code>Image</code>, <A HREF="../../../javax/microedition/lcdui/Graphics.html#copyArea(int, int, int, int, int, int, int)"><CODE>copyArea</CODE></A> should be used instead.
	 * </p>
	 * 
	 * @param img - the specified image to be drawn
	 * @param x - the x coordinate of the anchor point
	 * @param y - the y coordinate of the anchor point
	 * @param anchor - the anchor point for positioning the image
	 * @throws IllegalArgumentException - if anchor is not a legal value
	 * @throws NullPointerException - if img is null
	 * @see Image
	 */
	void drawImage( Image img, int x, int y, int anchor);


	/**
	 * Copies the contents of a rectangular area
	 * <code>(x_src, y_src, width, height)</code> to a destination area,
	 * whose anchor point identified by anchor is located at
	 * <code>(x_dest, y_dest)</code>.  The effect must be that the
	 * destination area
	 * contains an exact copy of the contents of the source area
	 * immediately prior to the invocation of this method.  This result must
	 * occur even if the source and destination areas overlap.
	 * 
	 * <p>The points <code>(x_src, y_src)</code> and <code>(x_dest,
	 * y_dest)</code> are both specified
	 * relative to the coordinate system of the <code>Graphics</code>
	 * object.  It is
	 * illegal for the source region to extend beyond the bounds of the
	 * graphic object.  This requires that: </P>
	 * <TABLE BORDER="2">
	 * <TR>
	 * <TD ROWSPAN="1" COLSPAN="1">
	 * <pre><code>
	 * x_src + tx &gt;= 0
	 * y_src + ty &gt;= 0
	 * x_src + tx + width &lt;= width of Graphics object's destination
	 * y_src + ty + height &lt;= height of Graphics object's destination      </code></pre>
	 * </TD>
	 * </TR>
	 * </TABLE>
	 * 
	 * <p>where <code>tx</code> and <code>ty</code> represent the X and Y
	 * coordinates of the translated origin of this graphics object, as
	 * returned by <code>getTranslateX()</code> and
	 * <code>getTranslateY()</code>, respectively.</p>
	 * 
	 * <P>
	 * However, it is legal for the destination area to extend beyond
	 * the bounds of the <code>Graphics</code> object.  Pixels outside
	 * of the bounds of
	 * the <code>Graphics</code> object will not be drawn.</p>
	 * 
	 * <p>The <code>copyArea</code> method is allowed on all
	 * <code>Graphics</code> objects except those
	 * whose destination is the actual display device.  This restriction is
	 * necessary because allowing a <code>copyArea</code> method on
	 * the display would
	 * adversely impact certain techniques for implementing
	 * double-buffering.</p>
	 * 
	 * <p>Like other graphics operations, the <code>copyArea</code>
	 * method uses the Source
	 * Over Destination rule for combining pixels.  However, since it is
	 * defined only for mutable images, which can contain only fully opaque
	 * pixels, this is effectively the same as pixel replacement.</p>
	 * 
	 * @param x_src - the x coordinate of upper left corner of source area
	 * @param y_src - the y coordinate of upper left corner of source area
	 * @param width - the width of the source area
	 * @param height - the height of the source area
	 * @param x_dest - the x coordinate of the destination anchor point
	 * @param y_dest - the y coordinate of the destination anchor point
	 * @param anchor - the anchor point for positioning the region within the destination image
	 * @throws IllegalStateException - if the destination of this Graphics object is the display device
	 * @throws IllegalArgumentException - if the region to be copied exceeds the bounds of the source image
	 * @since  MIDP 2.0
	 */
	void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor);

	/**
	 * Fills the specified triangle will the current color.  The lines
	 * connecting each pair of points are included in the filled
	 * triangle.
	 * 
	 * @param x1 - the x coordinate of the first vertex of the triangle
	 * @param y1 - the y coordinate of the first vertex of the triangle
	 * @param x2 - the x coordinate of the second vertex of the triangle
	 * @param y2 - the y coordinate of the second vertex of the triangle
	 * @param x3 - the x coordinate of the third vertex of the triangle
	 * @param y3 - the y coordinate of the third vertex of the triangle
	 * @since  MIDP 2.0
	 */
	void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3);
	
	/**
	 * Renders a series of device-independent RGB+transparency values in a
	 * specified region.  The values are stored in
	 * <code>rgbData</code> in a format
	 * with <code>24</code> bits of RGB and an eight-bit alpha value
	 * (<code>0xAARRGGBB</code>),
	 * with the first value stored at the specified offset.  The
	 * <code>scanlength</code>
	 * specifies the relative offset within the array between the
	 * corresponding pixels of consecutive rows.  Any value for
	 * <code>scanlength</code> is acceptable (even negative values)
	 * provided that all resulting references are within the
	 * bounds of the <code>rgbData</code> array. The ARGB data is
	 * rasterized horizontally from left to right within each row.
	 * The ARGB values are
	 * rendered in the region specified by <code>x</code>,
	 * <code>y</code>, <code>width</code> and <code>height</code>, and
	 * the operation is subject to the current clip region
	 * and translation for this <code>Graphics</code> object.
	 * 
	 * <p>Consider <code>P(a,b)</code> to be the value of the pixel
	 * located at column <code>a</code> and row <code>b</code> of the
	 * Image, where rows and columns are numbered downward from the
	 * top starting at zero, and columns are numbered rightward from
	 * the left starting at zero. This operation can then be defined
	 * as:</p>
	 * 
	 * <TABLE BORDER="2">
	 * <TR>
	 * <TD ROWSPAN="1" COLSPAN="1">
	 * <pre><code>
	 * P(a, b) = rgbData[offset + (a - x) + (b - y) * scanlength]       </code></pre>
	 * </TD>
	 * </TR>
	 * </TABLE>
	 * 
	 * <p> for </p>
	 * 
	 * <TABLE BORDER="2">
	 * <TR>
	 * <TD ROWSPAN="1" COLSPAN="1">
	 * <pre><code>
	 * x &lt;= a &lt; x + width
	 * y &lt;= b &lt; y + height    </code></pre>
	 * </TD>
	 * </TR>
	 * </TABLE>
	 * <p> This capability is provided in the <code>Graphics</code>
	 * class so that it can be
	 * used to render both to the screen and to offscreen
	 * <code>Image</code> objects.  The
	 * ability to retrieve ARGB values is provided by the <A HREF="../../../javax/microedition/lcdui/Image.html#getRGB(int[], int, int, int, int, int, int)"><CODE>Image.getRGB(int[], int, int, int, int, int, int)</CODE></A>
	 * method. </p>
	 * 
	 * <p> If <code>processAlpha</code> is <code>true</code>, the
	 * high-order byte of the ARGB format
	 * specifies opacity; that is, <code>0x00RRGGBB</code> specifies a
	 * fully transparent
	 * pixel and <code>0xFFRRGGBB</code> specifies a fully opaque
	 * pixel.  Intermediate
	 * alpha values specify semitransparency.  If the implementation does not
	 * support alpha blending for image rendering operations, it must remove
	 * any semitransparency from the source data prior to performing any
	 * rendering.  (See <a href="Image.html#alpha">Alpha Processing</a> for
	 * further discussion.)
	 * If <code>processAlpha</code> is <code>false</code>, the alpha
	 * values are ignored and all pixels
	 * must be treated as completely opaque.</p>
	 * 
	 * <p> The mapping from ARGB values to the device-dependent
	 * pixels is platform-specific and may require significant
	 * computation.</p>
	 * 
	 * @param rgbData - an array of ARGB values in the format 0xAARRGGBB
	 * @param offset - the array index of the first ARGB value
	 * @param scanlength - the relative array offset between the corresponding pixels in consecutive rows in the rgbData array
	 * @param x - the horizontal location of the region to be rendered
	 * @param y - the vertical location of the region to be rendered
	 * @param width - the width of the region to be rendered
	 * @param height - the height of the region to be rendered
	 * @param processAlpha - true if rgbData has an alpha channel, false if all pixels are fully opaque
	 * @throws ArrayIndexOutOfBoundsException - if the requested operation will attempt to access an element of rgbData whose index is either negative or beyond its length
	 * @throws NullPointerException - if rgbData is null
	 * @since  MIDP 2.0
	 */
	void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha);
	/**
	 * Gets the color that will be displayed if the specified color
	 * is requested. This method enables the developer to check the
	 * manner in which RGB values are mapped to the set of distinct
	 * colors that the device can actually display. For example,
	 * with a monochrome device, this method will return either
	 * <code>0xFFFFFF</code> (white) or <code>0x000000</code> (black)
	 * depending on the brightness of the specified color.
	 * 
	 * @param color - the desired color (in 0x00RRGGBB format, the high-order byte is ignored)
	 * @return the corresponding color that will be displayed on the device's screen (in 0x00RRGGBB format)
	 * @since  MIDP 2.0
	 */
	int getDisplayColor(int color);
}
