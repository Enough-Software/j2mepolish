//#condition polish.usePolishGui
/*
 * Copyright (c) 2010-2011 Robert Virkus / Enough Software
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

import de.enough.polish.util.ImageUtil;
import de.enough.polish.util.RgbImage;

//#if polish.api.nokia-ui
	import com.nokia.mid.ui.DirectGraphics;
	import com.nokia.mid.ui.DirectUtils;
//#endif

import de.enough.polish.util.DeviceInfo;

/**
 *
 * This class provides an abstraction for platform-independent Graphics objects.
 * <p>Drawing primitives are provided for text, images, lines, rectangles,
 * and arcs. Rectangles and arcs may also be filled with a solid color.
 * Rectangles may also be specified with rounded corners. </p>
 *
 * <p>A <code>24</code>-bit color model is provided, with
 * <code>8</code> bits for each of red, green, and
 * blue components of a color. Not all devices support a full
 * <code>24</code> bits' worth
 * of color and thus they will map colors requested by the application into
 * colors available on the device. Facilities are provided in the
 * {@link
 * Display Display} class for obtaining device characteristics, such
 * as
 * whether color is available and how many distinct gray levels are
 * available.
 * Applications may also use {@link Graphics#getDisplayColor(int)
 * getDisplayColor()} to obtain the actual color that would be displayed
 * for a requested color.
 * This enables applications to adapt their behavior to a device without
 * compromising device independence. </p>
 *
 * <p>For all rendering operations, source pixels are always combined with
 * destination pixels using the <em>Source Over Destination</em> rule
 * [Porter-Duff].  Other schemes for combining source pixels with destination
 * pixels, such as raster-ops, are not provided.</p>
 *
 * <p>For the text, line, rectangle, and arc drawing and filling primitives,
 * the source pixel is a pixel representing the current color of the graphics
 * object being used for rendering.  This pixel is always considered to be
 * fully opaque.  With source pixel that is always fully opaque, the Source
 * Over Destination rule has the effect of pixel replacement, where
 * destination pixels are simply replaced with the source pixel from the
 * graphics object.</p>
 *
 * <p>The {@link #drawImage drawImage()} and {@link #drawRegion drawRegion()}
 * methods use an image as the source for rendering operations instead of the
 * current color of the graphics object.  In this context, the Source Over
 * Destination rule has the following properties: a fully opaque pixel in the
 * source must replace the destination pixel, a fully transparent pixel in the
 * source must leave the destination pixel unchanged, and a semitransparent
 * pixel in the source must be alpha blended with the destination pixel.
 * Alpha blending of semitransparent pixels is required.  If an implementation
 * does not support alpha blending, it must remove all semitransparency from
 * image source data at the time the image is created.  See <a
 * href="Image.html#alpha">Alpha Processing</a> for further discussion.
 *
 * <p>The destinations of all graphics rendering are considered to consist
 * entirely of fully opaque pixels.  A property of the Source Over Destination
 * rule is that compositing any pixel with a fully opaque destination pixel
 * always results in a fully opaque destination pixel.  This has the effect of
 * confining full and partial transparency to immutable images, which may only
 * be used as the source for rendering operations.</p>
 *
 * <p>
 * Graphics may be rendered directly to the display or to an off-screen
 * image buffer. The destination of rendered graphics depends on the
 * provenance of the graphics object. A graphics object for rendering
 * to the display is passed to the <code>Canvas</code> object's
 * {@link Canvas#paint(Graphics) paint()}
 * method. This is the only means by which a graphics object may
 * be obtained whose destination is the display. Furthermore, applications
 * may draw using this graphics object only for the duration of the
 * <code>paint()</code> method. </p>
 * <p>
 * A graphics object for rendering to an off-screen image buffer may
 * be obtained by calling the
 * {@link Image#getGraphics() getGraphics()}
 * method on the desired image.
 * A graphics object so obtained may be held indefinitely
 * by the application, and requests may be issued on this graphics
 * object at any time. </p>
 *<p>
 * The default coordinate system's origin is at the
 * upper left-hand corner of the destination. The X-axis direction is
 * positive towards the right, and the Y-axis direction is positive
 * downwards. Applications may assume that horizontal and vertical
 * distances in the coordinate system represent equal distances on the
 * actual device display, that is, pixels are square. A facility is provided
 * for translating the origin of the coordinate system.
 * All coordinates are specified as integers. </p>
 * <p>
 * The coordinate system represents locations between pixels, not the
 * pixels themselves. Therefore, the first pixel in the upper left corner
 * of the display lies in the square bounded by coordinates
 * <code>(0,0) , (1,0) , (0,1) , (1,1)</code>. </p>
 * <p>
 * Under this definition, the semantics for fill operations are clear.
 * Since coordinate grid lines lie between pixels, fill operations
 * affect pixels that lie entirely within the region bounded by the
 * coordinates of the operation. For example, the operation </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    g.fillRect(0, 0, 3, 2)    </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * <P>
 * paints exactly six pixels.  (In this example, and in all subsequent
 * examples, the variable <code>g</code> is assumed to contain a
 * reference to a
 * <code>Graphics</code> object.) </p>
 * <p>
 * Each character of a font contains a set of pixels that forms the shape of
 * the character.  When a character is painted, the pixels forming the
 * character's shape are filled with the <code>Graphics</code>
 * object's current color, and
 * the pixels not part of the character's shape are left untouched.
 * The text drawing calls
 * {@link #drawChar drawChar()},
 * {@link #drawChars drawChars()},
 * {@link #drawString drawString()}, and
 * {@link #drawSubstring drawSubstring()}
 * all draw text in this manner. </p>
 * <p>
 * Lines, arcs, rectangles, and rounded rectangles may be drawn with either a
 * <code>SOLID</code> or a <code>DOTTED</code> stroke style, as set by
 * the {@link #setStrokeStyle
 * setStrokeStyle()} method.  The stroke style does not affect fill, text, and
 * image operations. </p>
 * <p>
 * For the <code>SOLID</code> stroke style,
 * drawing operations are performed with a one-pixel wide pen that fills
 * the pixel immediately
 * below and to the right of the specified coordinate. Drawn lines
 * touch pixels at both endpoints. Thus, the operation </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    g.drawLine(0, 0, 0, 0);    </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * <p>
 * paints exactly one pixel, the first pixel in the upper left corner
 * of the display. </p>
 * <p>
 * Drawing operations under the <code>DOTTED</code> stroke style will
 * touch a subset of
 * pixels that would have been touched under the <code>SOLID</code>
 * stroke style.  The
 * frequency and length of dots is implementation-dependent.  The endpoints of
 * lines and arcs are not guaranteed to be drawn, nor are the corner points of
 * rectangles guaranteed to be drawn.  Dots are drawn by painting with the
 * current color; spaces between dots are left untouched. </p>
 * <p>
 * An artifact of the coordinate system is that the area affected by a fill
 * operation differs slightly from the area affected by a draw operation given
 * the same coordinates. For example, consider the operations </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    g.fillRect(x, y, w, h); // 1
 *    g.drawRect(x, y, w, h); // 2    </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * <P>
 * Statement (1) fills a rectangle <code>w</code> pixels wide and
 * <code>h</code> pixels high.
 * Statement (2) draws a rectangle whose left and top
 * edges are within the area filled by statement (1). However, the
 * bottom and right edges lie one pixel outside the filled area.
 * This is counterintuitive, but it preserves the invariant that </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    g.drawLine(x, y, x+w, y);
 *    g.drawLine(x+w, y, x+w, y+h);
 *    g.drawLine(x+w, y+h, x, y+h);
 *    g.drawLine(x, y+h, x, y);     </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * <P>
 * has an effect identical to statement (2) above. </p>
 * <p>
 * The exact pixels painted by <code>drawLine()</code> and
 * <code>drawArc()</code> are not
 * specified. Pixels touched by a fill operation must either
 * exactly overlap or directly abut pixels touched by the
 * corresponding draw operation. A fill operation must never leave
 * a gap between the filled area and the pixels touched by the
 * corresponding draw operation, nor may the fill operation touch
 * pixels outside the area bounded by the corresponding draw operation. </p>
 *
 * <p>
 * <a name="clip"></a>
 * <h3>Clipping</h3> <p>
 *
 * <p>
 * The clip is the set of pixels in the destination of the
 * <code>Graphics</code> object that may be modified by graphics rendering
 * operations.
 *
 * <p>
 * There is a single clip per <code>Graphics</code> object.
 * The only pixels modified by graphics operations are those that lie within the
 * clip. Pixels outside the clip are not modified by any graphics operations.
 *
 * <p>
 * Operations are provided for intersecting the current clip with
 * a given rectangle and for setting the current clip outright.
 * The application may specify the clip by supplying a clip rectangle
 * using coordinates relative to the current coordinate system.
 *
 * <p>
 * It is legal to specify a clip rectangle whose width or height is zero
 * or negative. In this case the clip is considered to be empty,
 * that is, no pixels are contained within it.
 * Therefore, if any graphics operations are issued under such a clip,
 * no pixels will be modified.
 *
 * <p>
 * It is legal to specify a clip rectangle that extends beyond or resides
 * entirely beyond the bounds of the destination.  No pixels exist outside
 * the bounds of the destination, and the area of the clip rectangle
 * that is outside the destination is ignored.  Only the pixels that lie
 * both within the destination and within the specified clip rectangle
 * are considered to be part of the clip.
 *
 * <p>
 * Operations on the coordinate system,
 * such as {@link Graphics#translate(int, int) translate()},
 * do not modify the clip.
 * The methods
 * {@link Graphics#getClipX() getClipX()},
 * {@link Graphics#getClipY() getClipY()},
 * {@link Graphics#getClipWidth() getClipWidth()} and
 * {@link Graphics#getClipHeight() getClipHeight()}
 * must return a rectangle that,
 * if passed to <code>setClip</code> without an intervening change to
 * the <code>Graphics</code> object's coordinate system, must result in
 * the identical set of pixels in the clip.
 * The rectangle returned from the <code>getClip</code> family of methods
 * may differ from the clip rectangle that was requested in
 * {@link Graphics#setClip(int, int, int, int) setClip()}.
 * This can occur if the coordinate system has been changed or if
 * the implementation has chosen to intersect the clip rectangle
 * with the bounds of the destination of the <code>Graphics</code> object.
 *
 * <p>
 * If a graphics operation is affected by the clip, the pixels
 * touched by that operation must be the same ones that would be touched
 * as if the clip did not affect the operation. For example,
 * consider a clip represented by the rectangle <code>(cx, cy, cw, ch)</code>
 * and a point <code>(x1, y1)</code> that
 * lies outside this rectangle and a point <code>(x2, y2)</code>
 * that lies within this
 * rectangle. In the following code fragment, </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    g.setClip(0, 0, canvas.getWidth(),
 *                    canvas.getHeight());
 *    g.drawLine(x1, y1, x2, y2); // 3
 *    g.setClip(cx, cy, cw, ch);
 *    g.drawLine(x1, y1, x2, y2); // 4     </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * <P>
 * The pixels touched by statement (4) must be identical to the pixels
 * within <code>(cx, cy, cw, ch)</code> touched by statement (3). </p>
 * <p>
 * <a name="anchor"></a>
 * <h3>Anchor Points</h3> <p>
 *
 * The drawing of text is based on &quot;anchor points&quot;.
 * Anchor points are used to minimize the amount of
 * computation required when placing text.
 * For example, in order to center a piece of text,
 * an application needs to call <code>stringWidth()</code> or
 * <code>charWidth()</code> to get the width and then perform a
 * combination of subtraction and division to
 * compute the proper location.
 * The method to draw text is defined as follows:
 * <pre><code>
 * public void drawString(String text, int x, int y, int anchor);
 * </code></pre>
 * This method draws text in the current color,
 * using the current font
 * with its anchor point at <code>(x,y)</code>. The definition
 * of the anchor point must be one of the
 * horizontal constants <code>(LEFT, HCENTER, RIGHT)</code>
 * combined with one of the vertical constants
 * <code>(TOP, BASELINE, BOTTOM)</code> using the bit-wise
 * <code>OR</code> operator.
 * Zero may also be used as the value of an anchor point.
 * Using zero for the anchor point value gives results
 * identical to using <code>TOP | LEFT</code>.</p>
 *
 * <p>
 * Vertical centering of the text is not specified since it is not considered
 * useful, it is hard to specify, and it is burdensome to implement. Thus,
 * the <code>VCENTER</code> value is not allowed in the anchor point
 * parameter of text
 * drawing calls. </p>
 * <p>
 * The actual position of the bounding box
 * of the text relative to the <code>(x, y)</code> location is
 * determined by the anchor point. These anchor
 * points occur at named locations along the
 * outer edge of the bounding box. Thus, if <code>f</code>
 * is <code>g</code>'s current font (as returned by
 * <code>g.getFont()</code>, the following calls will all have
 * identical results: </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    g.drawString(str, x, y, TOP|LEFT);
 *    g.drawString(str, x + f.stringWidth(str)/2, y, TOP|HCENTER);
 *    g.drawString(str, x + f.stringWidth(str), y, TOP|RIGHT);
 *
 *    g.drawString(str, x,
 *        y + f.getBaselinePosition(), BASELINE|LEFT);
 *    g.drawString(str, x + f.stringWidth(str)/2,
 *        y + f.getBaselinePosition(), BASELINE|HCENTER);
 *    g.drawString(str, x + f.stringWidth(str),
 *        y + f.getBaselinePosition(), BASELINE|RIGHT);
 *
 *    drawString(str, x,
 *        y + f.getHeight(), BOTTOM|LEFT);
 *    drawString(str, x + f.stringWidth(str)/2,
 *        y + f.getHeight(), BOTTOM|HCENTER);
 *    drawString(str, x + f.stringWidth(str),
 *        y + f.getHeight(), BOTTOM|RIGHT);      </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * <p>
 * For text drawing, the inter-character and inter-line spacing (leading)
 * specified by the font designer are included as part of the values returned
 * in the {@link Font#stringWidth(java.lang.String) stringWidth()}
 * and {@link Font#getHeight() getHeight()}
 * calls of class {@link Font Font}.
 * For example, given the following code: </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    // (5)
 *    g.drawString(string1+string2, x, y, TOP|LEFT);
 *
 *    // (6)
 *    g.drawString(string1, x, y, TOP|LEFT);
 *    g.drawString(string2, x + f.stringWidth(string1), y, TOP|LEFT);
 *         </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * </P>
 * <P>
 * Code fragments (5) and (6) behave similarly if not identically. This
 * occurs because <code>f.stringWidth()</code>
 * includes the inter-character spacing.  The exact spacing of may differ
 * between these calls if the system supports font kerning.</p>
 *
 * <p>Similarly, reasonable vertical spacing may be
 * achieved simply by adding the font height
 * to the Y-position of subsequent lines. For example: </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    g.drawString(string1, x, y, TOP|LEFT);
 *    g.drawString(string2, x, y + f.fontHeight(), TOP|LEFT);    </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * <P>
 * draws <code>string1</code> and <code>string2</code> on separate lines with
 * an appropriate amount of inter-line spacing. </p>
 * <p>
 * The <code>stringWidth()</code> of the string and the
 * <code>fontHeight()</code> of the font in which
 * it is drawn define the size of the bounding box of a piece of text. As
 * described above, this box includes inter-line and inter-character spacing.
 * The implementation is required to put this space below and to right of the
 * pixels actually belonging to the characters drawn. Applications that wish
 * to position graphics closely with respect to text (for example, to paint a
 * rectangle around a string of text) may assume that there is space below and
 * to the right of a string and that there is <em>no</em> space above
 * and to the
 * left of the string. </p>
 * <p>
 * Anchor points are also used for positioning of images. Similar to text
 * drawing, the anchor point for an image specifies the point on the bounding
 * rectangle of the destination that is to positioned at the
 * <code>(x,y)</code> location
 * given in the graphics request. Unlike text, vertical centering of images
 * is well-defined, and thus the <code>VCENTER</code> value may be
 * used within the anchor
 * point parameter of image drawing requests. Because images have no notion
 * of a baseline, the <code>BASELINE</code> value may not be used
 * within the anchor point
 * parameter of image drawing requests. </p>
 *
 * <h3>Reference</h3>
 *
 * <dl>
 * <dt>Porter-Duff
 * <dd>Porter, T., and T. Duff.  &quot;Compositing Digital Images.&quot;
 * <em>Computer Graphics V18 N3 (SIGGRAPH 1984)</em>, p. 253-259.
 * </dl>
 * 
 * @author Ovidiu Iliescu
 */
public class Graphics {

//	//#if polish.build.classes.NativeGraphics:defined
//		//#= private ${polish.build.classes.NativeGraphics} nativeGraphics;
//	//#else
//		private NativeGraphics nativeGraphics;
//	//#endif
		
    protected
	//#if polish.build.classes.NativeGraphics:defined
    	//#= ${polish.build.classes.NativeGraphics}
    //#else
    	javax.microedition.lcdui.Graphics
    //#endif
    	graphics = null;

    protected RgbImage bufferedImage = null ;

	private Font currentFont;

	/**
	 * Constant for centering text and images horizontally
	 * around the anchor point
	 * 
	 * <P>Value <code>1</code> is assigned to <code>HCENTER</code>.</P></DL>
	 * 
	 */
	public static final int HCENTER = 1;

	/**
	 * Constant for centering images vertically
	 * around the anchor point.
	 * 
	 * <P>Value <code>2</code> is assigned to <code>VCENTER</code>.</P></DL>
	 * 
	 */
	public static final int VCENTER = 2;

	/**
	 * Constant for positioning the anchor point of text and images
	 * to the left of the text or image.
	 * 
	 * <P>Value <code>4</code> is assigned to <code>LEFT</code>.</P></DL>
	 * 
	 */
	public static final int LEFT = 4;

	/**
	 * Constant for positioning the anchor point of text and images
	 * to the right of the text or image.
	 * 
	 * <P>Value <code>8</code> is assigned to <code>RIGHT</code>.</P></DL>
	 * 
	 */
	public static final int RIGHT = 8;

	/**
	 * Constant for positioning the anchor point of text and images
	 * above the text or image.
	 * 
	 * <P>Value <code>16</code> is assigned to <code>TOP</code>.</P></DL>
	 * 
	 */
	public static final int TOP = 16;

	/**
	 * Constant for positioning the anchor point of text and images
	 * below the text or image.
	 * 
	 * <P>Value <code>32</code> is assigned to <code>BOTTOM</code>.</P></DL>
	 * 
	 */
	public static final int BOTTOM = 32;

	/**
	 * Constant for positioning the anchor point at the baseline of text.
	 * 
	 * <P>Value <code>64</code> is assigned to <code>BASELINE</code>.</P></DL>
	 * 
	 */
	public static final int BASELINE = 64;

	/**
	 * Constant for the <code>SOLID</code> stroke style.
	 * 
	 * <P>Value <code>0</code> is assigned to <code>SOLID</code>.</P></DL>
	 * 
	 */
	public static final int SOLID = 0;

	/**
	 * Constant for the <code>DOTTED</code> stroke style.
	 * 
	 * <P>Value <code>1</code> is assigned to <code>DOTTED</code>.</P></DL>
	 * 
	 * 
	 */
	public static final int DOTTED = 1;

	/**
	 * Creates a new graphics wrapper
	 * 
	 * @param g the native graphics
	 */
    public Graphics( 
    		//#if polish.build.classes.NativeGraphics:defined
	        	//#= ${polish.build.classes.NativeGraphics}
	        //#else
	        	javax.microedition.lcdui.Graphics
	        //#endif
    		g)
    {
        this.graphics = g;
    }

    /**
     * Intersects the current clip with the specified rectangle.
     * The resulting clipping area is the intersection of the current
     * clipping area and the specified rectangle.
     * This method can only be used to make the current clip smaller.
     * To set the current clip larger, use the <code>setClip</code> method.
     * Rendering operations have no effect outside of the clipping area.
     * @param x the x coordinate of the rectangle to intersect the clip with
     * @param y the y coordinate of the rectangle to intersect the clip with
     * @param width the width of the rectangle to intersect the clip with
     * @param height the height of the rectangle to intersect the clip with
     * @see #setClip(int, int, int, int)
     */
    public void clipRect(int x, int y, int width, int height)
    {
        this.graphics.clipRect(x, y, width, height);
    }

    //#if polish.midp2
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
     *    <pre><code>
     *   x_src + tx &gt;= 0
     *   y_src + ty &gt;= 0
     *   x_src + tx + width &lt;= width of Graphics object's destination
     *   y_src + ty + height &lt;= height of Graphics object's destination
     *    </code></pre>
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
     * @param x_src  the x coordinate of upper left corner of source area
     * @param y_src  the y coordinate of upper left corner of source area
     * @param width  the width of the source area
     * @param height the height of the source area
     * @param x_dest the x coordinate of the destination anchor point
     * @param y_dest the y coordinate of the destination anchor point
     * @param anchor the anchor point for positioning the region within
     *        the destination image
     *
     * @throws IllegalStateException if the destination of this
     * <code>Graphics</code> object is the display device
     * @throws IllegalArgumentException if the region to be copied exceeds
     * the bounds of the source image
     *
     */
    public void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor)
    {
        this.graphics.copyArea( x_src,  y_src,  width,  height,  x_dest,  y_dest,  anchor);
    }
    //#endif

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
     * @param x the <em>x</em> coordinate of the upper-left corner
     * of the arc to be drawn
     * @param y the <em>y</em> coordinate of the upper-left corner
     * of the arc to be drawn
     * @param width the width of the arc to be drawn
     * @param height the height of the arc to be drawn
     * @param startAngle the beginning angle
     * @param arcAngle the angular extent of the arc, relative to
     * the start angle
     * @see #fillArc(int, int, int, int, int, int)
     */
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
    {
        this.graphics.drawArc( x,  y,  width,  height,  startAngle,  arcAngle) ;
    }

    /**
     * Draws the specified character using the current font and color.
     * @param character the character to be drawn
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the text; see
     * <a href="#anchor">anchor points</a>
     *
     * @throws IllegalArgumentException if <code>anchor</code>
     * is not a legal value
     *
     * @see #drawString(java.lang.String, int, int, int)
     * @see #drawChars(char[], int, int, int, int, int)
     */
    public void drawChar(char character, int x, int y, int anchor)
    {
        this.graphics.drawChar(character,  x,  y,  anchor) ;
    }

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
     * @param data the array of characters to be drawn
     * @param offset the start offset in the data
     * @param length the number of characters to be drawn
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the text; see
     * <a href="#anchor">anchor points</a>
     *
     * @throws ArrayIndexOutOfBoundsException if <code>offset</code>
     * and <code>length</code>
     * do not specify a valid range within the data array
     * @throws IllegalArgumentException if anchor is not a legal value
     * @throws NullPointerException if <code>data</code> is <code>null</code>
     *
     * @see #drawString(java.lang.String, int, int, int)
     */
    public void drawChars(char[] data, int offset, int length, int x, int y, int anchor)
    {
        this.graphics.drawChars(data,offset, length, x, y, anchor) ;
    }

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
     * <code>Image</code>, {@link #copyArea copyArea} should be used instead.
     * </p>
     *
     * @param img the specified image to be drawn
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the image
     * @throws IllegalArgumentException if <code>anchor</code>
     * is not a legal value
     * @throws NullPointerException if <code>img</code> is <code>null</code>
     * @see Image
     */
    public void drawImage(Image img, int x, int y, int anchor)
    {
        this.graphics.drawImage( img.image ,x, y, anchor) ;
    }

    /**
     * Draws a line between the coordinates <code>(x1,y1)</code> and
     * <code>(x2,y2)</code> using
     * the current color and stroke style.
     * @param x1 the x coordinate of the start of the line
     * @param y1 the y coordinate of the start of the line
     * @param x2 the x coordinate of the end of the line
     * @param y2 the y coordinate of the end of the line
     */
    public void drawLine(int x1, int y1, int x2, int y2)
    {
        this.graphics.drawLine(x1, y1, x2, y2) ;
    }

    /**
     * Draws the outline of the specified rectangle using the current
     * color and stroke style.
     * The resulting rectangle will cover an area <code>(width + 1)</code>
     * pixels wide by <code>(height + 1)</code> pixels tall.
     * If either width or height is less than
     * zero, nothing is drawn.
     * @param x the x coordinate of the rectangle to be drawn
     * @param y the y coordinate of the rectangle to be drawn
     * @param width the width of the rectangle to be drawn
     * @param height the height of the rectangle to be drawn
     * @see #fillRect(int, int, int, int)
     */
    public void drawRect(int x, int y, int width, int height)
    {
        this.graphics.drawRect( x,y, width, height);
    }

    //#if polish.midp2
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
     * in the {@link javax.microedition.lcdui.game.Sprite Sprite} class:<br>
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
     *    <pre><code>
     *   x_src &gt;= 0
     *   y_src &gt;= 0
     *   x_src + width &lt;= source width
     *   y_src + height &lt;= source height    </code></pre>
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
     * @param src the source image to copy from
     * @param x_src the x coordinate of the upper left corner of the region
     * within the source image to copy
     * @param y_src the y coordinate of the upper left corner of the region
     * within the source image to copy
     * @param width the width of the region to copy
     * @param height the height of the region to copy
     * @param transform the desired transformation for the selected region
     * being copied
     * @param x_dest the x coordinate of the anchor point in the
     * destination drawing area
     * @param y_dest the y coordinate of the anchor point in the
     * destination drawing area
     * @param anchor the anchor point for positioning the region within
     * the destination image
     *
     * @throws IllegalArgumentException if <code>src</code> is the
     * same image as the
     * destination of this <code>Graphics</code> object
     * @throws NullPointerException if <code>src</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>transform</code> is invalid
     * @throws IllegalArgumentException if <code>anchor</code> is invalid
     * @throws IllegalArgumentException if the region to be copied exceeds
     * the bounds of the source image
     */
    public void drawRegion(Image src, int x_src, int y_src, int width, int height, int transform, int x_dest, int y_dest, int anchor)
    {
        this.graphics.drawRegion( src.image,  x_src,  y_src,  width,  height,  transform,  x_dest,  y_dest,  anchor) ;
    }
    //#endif

    //#if polish.midp2
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
     *    <pre><code>
     *    P(a, b) = rgbData[offset + (a - x) + (b - y) * scanlength]
     *         </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     *
     * <p> for </p>
     *
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *     x &lt;= a &lt; x + width
     *     y &lt;= b &lt; y + height    </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     * <p> This capability is provided in the <code>Graphics</code>
     * class so that it can be
     * used to render both to the screen and to offscreen
     * <code>Image</code> objects.  The
     * ability to retrieve ARGB values is provided by the {@link Image#getRGB}
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
     * @param rgbData an array of ARGB values in the format
     * <code>0xAARRGGBB</code>
     * @param offset the array index of the first ARGB value
     * @param scanlength the relative array offset between the
     * corresponding pixels in consecutive rows in the
     * <code>rgbData</code> array
     * @param x the horizontal location of the region to be rendered
     * @param y the vertical location of the region to be rendered
     * @param width the width of the region to be rendered
     * @param height the height of the region to be rendered
     * @param processAlpha <code>true</code> if <code>rgbData</code>
     * has an alpha channel,
     * false if all pixels are fully opaque
     *
     * @throws ArrayIndexOutOfBoundsException if the requested operation
     * will attempt to access an element of <code>rgbData</code>
     * whose index is either negative or beyond its length
     * @throws NullPointerException if <code>rgbData</code> is <code>null</code>
     *
     */
    public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha)
    {
        this.graphics.drawRGB(rgbData, offset,scanlength, x, y,width, height, processAlpha) ;
    }
    //#endif

    /**
     * Draws the outline of the specified rounded corner rectangle
     * using the current color and stroke style.
     * The resulting rectangle will cover an area <code>(width +
     * 1)</code> pixels wide
     * by <code>(height + 1)</code> pixels tall.
     * If either <code>width</code> or <code>height</code> is less than
     * zero, nothing is drawn.
     * @param x the x coordinate of the rectangle to be drawn
     * @param y the y coordinate of the rectangle to be drawn
     * @param width the width of the rectangle to be drawn
     * @param height the height of the rectangle to be drawn
     * @param arcWidth the horizontal diameter of the arc at the four corners
     * @param arcHeight the vertical diameter of the arc at the four corners
     * @see #fillRoundRect(int, int, int, int, int, int)
     */
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
    {
        this.graphics.drawRoundRect( x,  y,  width,  height,  arcWidth,  arcHeight) ;
    }

    /**
     * Draws the specified <code>String</code> using the current font and color.
     * The <code>x,y</code> position is the position of the anchor point.
     * See <a href="#anchor">anchor points</a>.
     * @param str the <code>String</code> to be drawn
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the text
     * @throws NullPointerException if <code>str</code> is <code>null</code>
     * @throws IllegalArgumentException if anchor is not a legal value
     * @see #drawChars(char[], int, int, int, int, int)
     */
    public void drawString(String str, int x, int y, int anchor)
    {
        this.graphics.drawString( str,  x, y, anchor) ;
    }

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
     * @param str the <code>String</code> to be drawn
     * @param offset zero-based index of first character in the substring
     * @param len length of the substring
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the text
     * @see #drawString(String, int, int, int)
     * @throws StringIndexOutOfBoundsException if <code>offset</code>
     * and <code>length</code> do not specify
     * a valid range within the <code>String</code> <code>str</code>
     * @throws IllegalArgumentException if <code>anchor</code>
     * is not a legal value
     * @throws NullPointerException if <code>str</code> is <code>null</code>
     */
    public void drawSubstring(String str, int offset, int len, int x, int y, int anchor)
    {
        this.graphics.drawSubstring(str,  offset, len,  x,  y, anchor) ;
    }

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
     * @param x the <em>x</em> coordinate of the upper-left corner of
     * the arc to be filled.
     * @param y the <em>y</em> coordinate of the upper-left corner of the
     * arc to be filled.
     * @param width the width of the arc to be filled
     * @param height the height of the arc to be filled
     * @param startAngle the beginning angle.
     * @param arcAngle the angular extent of the arc,
     * relative to the start angle.
     * @see #drawArc(int, int, int, int, int, int)
     */
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
    {
        this.graphics.fillArc( x, y, width, height, startAngle, arcAngle);
    }

    /**
     * Fills the specified rectangle with the current color.
     * If either width or height is zero or less,
     * nothing is drawn.
     * @param x the x coordinate of the rectangle to be filled
     * @param y the y coordinate of the rectangle to be filled
     * @param width the width of the rectangle to be filled
     * @param height the height of the rectangle to be filled
     * @see #drawRect(int, int, int, int)
     */
    public void fillRect(int x, int y, int width, int height)
    {
        this.graphics.fillRect( x, y, width, height) ;
    }


    /**
     * Fills the specified rounded corner rectangle with the current color.
     * If either <code>width</code> or <code>height</code> is zero or less,
     * nothing is drawn.
     * @param x the x coordinate of the rectangle to be filled
     * @param y the y coordinate of the rectangle to be filled
     * @param width the width of the rectangle to be filled
     * @param height the height of the rectangle to be filled
     * @param arcWidth the horizontal diameter of the arc at the four
     * corners
     * @param arcHeight the vertical diameter of the arc at the four corners
     * @see #drawRoundRect(int, int, int, int, int, int)
     */
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
    {
        this.graphics.fillRoundRect(x, y, width, height, arcWidth, arcHeight) ;
    }
    
    /**
     * Gets the blue component of the current color.
     * @return integer value in range <code>0-255</code>
     * @see #setColor(int, int, int)
     */
    public int getBlueComponent()
    {
        return this.graphics.getBlueComponent() ;
    }

    /**
     * Gets the height of the current clipping area.
     * @return height of the current clipping area.
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public int getClipHeight()
    {
        return this.graphics.getClipHeight() ;
    }

    /**
     * Gets the width of the current clipping area.
     * @return width of the current clipping area.
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public int getClipWidth()
    {
       return this.graphics.getClipWidth() ;
    }

    /**
     * Gets the X offset of the current clipping area, relative
     * to the coordinate system origin of this graphics context.
     * Separating the <code>getClip</code> operation into two methods returning
     * integers is more performance and memory efficient than one
     * <code>getClip()</code> call returning an object.
     * @return X offset of the current clipping area
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public int getClipX()
    {
        return this.graphics.getClipX() ;
    }

    /**
     * Gets the Y offset of the current clipping area, relative
     * to the coordinate system origin of this graphics context.
     * Separating the <code>getClip</code> operation into two methods returning
     * integers is more performance and memory efficient than one
     * <code>getClip()</code> call returning an object.
     * @return Y offset of the current clipping area
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public int getClipY()
    {
        return this.graphics.getClipY() ;
    }

    /**
     * Gets the current color.
     * @return an integer in form <code>0x00RRGGBB</code>
     * @see #setColor(int, int, int)
     */
    public int getColor()
    {
        return this.graphics.getColor() ;
    }

    //#if polish.midp2
    /**
     * Gets the color that will be displayed if the specified color
     * is requested. This method enables the developer to check the
     * manner in which RGB values are mapped to the set of distinct
     * colors that the device can actually display. For example,
     * with a monochrome device, this method will return either
     * <code>0xFFFFFF</code> (white) or <code>0x000000</code> (black)
     * depending on the brightness of the specified color.
     *
     * @param color the desired color (in <code>0x00RRGGBB</code>
     * format, the high-order
     * byte is ignored)
     * @return the corresponding color that will be displayed on the device's
     * screen (in <code>0x00RRGGBB</code> format)
     *
     */
    public int getDisplayColor(int color)
    {
        return this.graphics.getDisplayColor(color);
    }
    //#endif

    /**
     * Gets the current font.
     * @return current font
     * @see javax.microedition.lcdui.Font
     * @see #setFont(Font)
     */
    public Font getFont()
    {
    	return this.currentFont;
        //return new Font ( this.graphics.getFont() );
    }

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
     * @return integer value in range <code>0-255</code>
     * @see #setGrayscale(int)
     */
    public int getGrayScale()
    {
        return this.graphics.getGrayScale() ;
    }

    /**
     * Gets the green component of the current color.
     * @return integer value in range <code>0-255</code>
     * @see #setColor(int, int, int)
     */
    public int getGreenComponent()
    {
        return this.graphics.getGreenComponent() ;
    }

    /**
     * Gets the red component of the current color.
     * @return integer value in range <code>0-255</code>
     * @see #setColor(int, int, int)
     */
    public int getRedComponent()
    {
        return this.graphics.getRedComponent();
    }

    /**
     * Gets the stroke style used for drawing operations.
     * @return stroke style, <code>SOLID</code> or <code>DOTTED</code>
     * @see #setStrokeStyle
     */
    public int getStrokeStyle()
    {
        return this.graphics.getStrokeStyle() ;
    }

    /**
     * Gets the X coordinate of the translated origin of this graphics context.
     * @return X of current origin
     */
    public int getTranslateX()
    {
        return this.graphics.getTranslateX() ;
    }

    /**
     * Gets the Y coordinate of the translated origin of this graphics context.
     * @return Y of current origin
     */
    public int getTranslateY()
    {
        return this.graphics.getTranslateY();
    }

    /**
     * Sets the current clip to the rectangle specified by the
     * given coordinates.
     * Rendering operations have no effect outside of the clipping area.
     * @param x the x coordinate of the new clip rectangle
     * @param y the y coordinate of the new clip rectangle
     * @param width the width of the new clip rectangle
     * @param height the height of the new clip rectangle
     * @see #clipRect(int, int, int, int)
     */
    public void setClip(int x, int y, int width, int height)
    {
        this.graphics.setClip(x,y,width,height);
    }

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
     * @param RGB the color being set
     * @see #getColor
     */
    public void setColor(int RGB)
    {
        this.graphics.setColor(RGB);
    }

    /**
     * Sets the current color to the specified RGB values. All subsequent
     * rendering operations will use this specified color.
     * @param red the red component of the color being set in range
     * <code>0-255</code>
     * @param green the green component of the color being set in range
     * <code>0-255</code>
     * @param blue the blue component of the color being set in range
     * <code>0-255</code>
     * @throws IllegalArgumentException if any of the color components
     * are outside of range <code>0-255</code>
     * @see #getColor
     */
    public void setColor(int red, int green, int blue)
    {
        this.graphics.setColor(red,green,blue);
    }

    /**
     * Sets the font for all subsequent text rendering operations.  If font is
     * <code>null</code>, it is equivalent to
     * <code>setFont(Font.getDefaultFont())</code>.
     *
     * @param font the specified font
     * @see javax.microedition.lcdui.Font
     * @see #getFont()
     * @see #drawString(java.lang.String, int, int, int)
     * @see #drawChars(char[], int, int, int, int, int)
     */
    public void setFont ( Font font)
    {
    	this.currentFont = font;
        this.graphics.setFont(font.font);
    }

    /**
     * Sets the current grayscale to be used for all subsequent
     * rendering operations. For monochrome displays, the behavior
     * is clear. For color displays, this sets the color for all
     * subsequent drawing operations to be a gray color equivalent
     * to the value passed in. The value must be in the range
     * <code>0-255</code>.
     * @param gray the desired grayscale value
     * @throws IllegalArgumentException if the gray value is out of range
     * @see #getGrayScale
     */
    public void setGrayscale(int gray)
    {
        this.graphics.setGrayScale(gray);
    }

    /**
     * Sets the stroke style used for drawing lines, arcs, rectangles, and
     * rounded rectangles.  This does not affect fill, text, and image
     * operations.
     * @param style can be <code>SOLID</code> or <code>DOTTED</code>
     * @throws IllegalArgumentException if the <code>style</code> is illegal
     * @see #getStrokeStyle
     */
    public void setStrokeStyle(int style)
    {
        this.graphics.setStrokeStyle(style);
    }

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
     * @param x the x coordinate of the new translation origin
     * @param y the y coordinate of the new translation origin
     * @see #getTranslateX()
     * @see #getTranslateY()
     */
    public void translate(int x, int y)
    {
        this.graphics.translate(x, y);
    }

    

    // METHODS FROM DrawUtil.java

    /**
     * Draws a translucent line on MIDP 2.0+ and Nokia-UI-API devices.
     * Note that on pure MIDP 1.0 devices without support for the Nokia-UI-API the translucency is ignored.
     *
     * @param color the ARGB color
     * @param x1 horizontal start position
     * @param y1 vertical start position
     * @param x2 horizontal end position
     * @param y2 vertical end position
     */
    public void drawTranslucentLine( int color, int x1, int y1, int x2, int y2) {
            //#if polish.blackberry && polish.usePolishGui
                    net.rim.device.api.ui.Graphics bbGraphics = null;
                    //# bbGraphics = this.graphics.g;
                    int alpha = color >>> 24;
                    bbGraphics.setGlobalAlpha( alpha );
                    bbGraphics.setColor( color );
                    bbGraphics.drawLine(x1, y1, x2, y2);
                    bbGraphics.setGlobalAlpha( 0xff ); // reset to fully opaque
            //#elif polish.api.nokia-ui && !polish.Bugs.TransparencyNotWorkingInNokiaUiApi && !polish.Bugs.TransparencyNotWorkingInDrawPolygon
                    int[] xPoints = new int[] { x1, x2 };
                    int[] yPoints = new int[] { y1, y2 };
                    DirectGraphics dg = DirectUtils.getDirectGraphics(this.graphics);
                    dg.drawPolygon(xPoints, 0, yPoints, 0, 2, color );
            //#elifdef polish.midp2
                    if (y2 < y1 ) {
                            int top = y2;
                            y2 = y1;
                            y1 = top;
                    }
                    if (x2 < x1) {
                            int left = x2;
                            x1 = x2;
                            x2 = x1;
                            x1 = left;
                    }
//			int[] rgb = new int[]{ color };
//			if (y1 == y2) {
//				int start = Math.max( x1, 0);
//				for (int i = start; i < x2; i++ ) {
//					g.drawRGB(rgb, 0, 0, start + i, y1, 1, 1, true );
//				}
//			} else if (x1 == x2) {
//				int start = Math.max( y1, 0);
//				for (int i = start; i < y2; i++ ) {
//					g.drawRGB(rgb, 0, 0, x1, start + i, 1, 1, true );
//				}
//			}

                    if (x1 == x2 || y1 == y2) {
//				int[] rgb = new int[]{ color };
//				g.drawRGB( rgb, 0, 0, x1, y1, x2 - x1, y2 - y1, true );
                            int width = x2 - x1;
                            if (width == 0) {
                                    width = 1;
                            }
                            int height = y2 - y1;
                            if (height == 0) {
                                    height = 1;
                            }
                            int[] rgb = new int[ Math.max( width, height )];
                            for (int i = 0; i < rgb.length; i++) {
                                    rgb[i] = color;
                            }
                            // the scanlength should really be 0, but we use width so that
                            // this works on Nokia Series 40 devices as well:
                            // drawRGB(		  int[] rgbData,
                            //                int offset,
                            //                int scanlength, <<< this _should_ allow any value, even 0 or negative ones
                            //                int x,
                            //                int y,
                            //                int width,
                            //                int height,
                            //                boolean processAlpha)
                            this.graphics.drawRGB( rgb, 0, width, x1, y1, width, height, true );
                    } else {
                            // TODO use alpha channel
                            this.graphics.setColor( color );
                            this.graphics.drawLine(x1, y1, x2, y2);
                    }
            //#else
                    this.graphics.setColor( color );
                    this.graphics.drawLine(x1, y1, x2, y2);
            //#endif
    }

	/**
	 * Draws an (A)RGB array and fits it into the clipping area.
	 *
	 * @param rgb the (A)RGB array
	 * @param x the horizontal start position
	 * @param y the vertical start position
	 * @param width the width of the RGB array
	 * @param height the heigt of the RGB array
	 * @param processAlpha true when the alpha values should be used so that pixels are blended with the background
	 */
	public void drawRgb( int[] rgb, int x, int y, int width, int height, boolean processAlpha) {

		drawRgb( rgb, x, y, width, height, processAlpha, this.graphics.getClipX(), this.graphics.getClipY(), this.graphics.getClipWidth(), this.graphics.getClipHeight());
	}


	/**
	 * Draws an (A)RGB array and fits it into the clipping area.
	 *
	 * @param rgb the (A)RGB array
	 * @param x the horizontal start position
	 * @param y the vertical start position
	 * @param width the width of the RGB array
	 * @param height the heigt of the RGB array
	 * @param processAlpha true when the alpha values should be used so that pixels are blended with the background
	 * @param clipX the horizontal start of the clipping area
	 * @param clipY the vertical start of the clipping area
	 * @param clipWidth the width of the clipping area
	 * @param clipHeight the height of the clipping area
	 */
	public void drawRgb(int[] rgb, int x, int y, int width, int height,
			boolean processAlpha, int clipX, int clipY, int clipWidth,
			int clipHeight)
	{
		if (x + width < clipX || x > clipX + clipWidth || y + height < clipY || y > clipY + clipHeight) {
			// this is not within the visible bounds:
			return;
		}
		// adjust x / y / width / height to draw RGB within visible bounds:
		int offset = 0;
		if (x < clipX) {
			offset = clipX - x;
			x = clipX;
		}
		int scanlength = width;
		width -= offset;
		if (x + width > clipX + clipWidth) {
			width = (clipX + clipWidth) - x;
		}
		if (width <= 0) {
			return;
		}
		if (y < clipY) {
			offset += (clipY - y) * scanlength;
			height -= (clipY - y);
			y = clipY;
		}
		if (y + height > clipY + clipHeight) {
			height = (clipY + clipHeight) - y;
		}
		if (height <= 0) {
			return;
		}

		//#if polish.midp2
			graphics.drawRGB(rgb, offset, scanlength, x, y, width, height,  processAlpha);
		//#endif
	}


	/**
	 * Draws an RGB Image
	 * @param image the image
	 * @param x the horizontal position
	 * @param y the vertical position
	 */
	public void drawRgb(RgbImage image, int x, int y)
	{
		drawRgb( image.getRgbData(), x, y, image.getWidth(), image.getHeight(), image.isProcessTransparency(), this.graphics.getClipX(), this.graphics.getClipY(), this.graphics.getClipWidth(), this.graphics.getClipHeight());
	}

        /**
	 * Draws a (translucent) filled out rectangle.
	 * Please note that this method has to create temporary arrays for pure MIDP 2.0 devices each time it is called, using a TranslucentSimpleBackground
	 * is probably less resource intensive.
	 *
	 * @param x the horizontal start position
	 * @param y the vertical start position
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param color the argb color of the rectangle, when there is no alpha value (color & 0xff000000 == 0), the traditional g.fillRect() method is called
	 * @see de.enough.polish.ui.backgrounds.TranslucentSimpleBackground
	 */
	public void fillRect( int x, int y, int width, int height, int color) {
		if ((color & 0xff000000) == 0) {
			this.graphics.setColor(color);
			this.graphics.fillRect(x, y, width, height);
			return;
		}
		//#if polish.blackberry && polish.usePolishGui
			net.rim.device.api.ui.Graphics bbGraphics = null;
			//# bbGraphics = this.graphics.g;
			int alpha = color >>> 24;
			bbGraphics.setGlobalAlpha( alpha );
			bbGraphics.setColor( color );
			bbGraphics.fillRect(x, y, width, height);
			bbGraphics.setGlobalAlpha( 0xff ); // reset to fully opaque
		//#elif tmp.useNokiaUi
			DirectGraphics dg = DirectUtils.getDirectGraphics(graphics);
			int[] xCoords = new int[4];
			xCoords[0] = x;
			xCoords[1] = x + width;
			xCoords[2] = x + width;
			xCoords[3] = x;
			int[] yCoords = new int[4];
			yCoords[0] = y;
			yCoords[1] = y;
			yCoords[2] = y + height;
			yCoords[3] = y + height;
			dg.fillPolygon( xCoords, 0, yCoords, 0, 4, color );
		//#elif polish.midp2
			//#ifdef polish.Bugs.drawRgbOrigin
				x += this.graphics.getTranslateX();
				y += this.graphics.getTranslateY();
			//#endif

			// check if the buffer needs to be created:
			int[] buffer = null;
			//#if polish.Bugs.drawRgbNeedsFullBuffer || polish.vendor == Generic
				//#if polish.vendor == Generic
					if (DeviceInfo.requiresFullRgbArrayForDrawRgb()) {
				//#endif
						buffer = new int[ width * height ];
						for (int i = buffer.length - 1; i >= 0 ; i--) {
							buffer[i] = color;
						}
				//#if polish.vendor == Generic
					}
				//#endif
			//#endif
			//#if !polish.Bugs.drawRgbNeedsFullBuffer
				//#if polish.vendor == Generic
					if (buffer == null) {
				//#endif
						buffer = new int[ width ];
						for (int i = buffer.length - 1; i >= 0 ; i--) {
							buffer[i] = color;
						}
				//#if polish.vendor == Generic
					}
				//#endif
			//#endif
			//#if polish.Bugs.drawRgbNeedsFullBuffer
				drawRgb(buffer, x, y, width, height, true);
			//#else
				//#if polish.vendor == Generic
					if (DeviceInfo.requiresFullRgbArrayForDrawRgb()) {
						drawRgb(buffer, x, y, width, height, true);
						return;
					}
				//#endif
				if (x < 0) {
					width += x;
					x = 0;
				}
				if (width <= 0) {
					return;
				}
				if (y < 0) {
					height += y;
					y = 0;
				}
				if (height <= 0) {
					return;
				}
				graphics.drawRGB(buffer, 0, 0, x, y, width, height, true);
			//#endif
		//#else
			graphics.setColor(color);
			graphics.fillRect(x, y, width, height);
		//#endif
	}


	/**
	 * Draws a polygon.
	 *
	 * @param xPoints the x coordinates of the polygon
	 * @param yPoints the y coordinates of the polygon
	 * @param color the color of the polygon
	 */
	public void drawPolygon( int[] xPoints, int[] yPoints, int color)
    {
		//#if polish.blackberry && polish.usePolishGui
			Object o = graphics; // this cast is needed, otherwise the compiler will complain
			              // that javax.microedition.lcdui.Graphics can never be casted
			              // to de.enough.polish.blackberry.ui.Graphics.

			net.rim.device.api.ui.Graphics graphicsBB = ((de.enough.polish.blackberry.ui.Graphics) o).g;
			if ((color & 0xff000000) == 0) {
				color = 0xff000000 | color;
			}
			graphicsBB.setColor( color );
			graphicsBB.setColor(color);
			graphicsBB.drawPathOutline( xPoints, yPoints, null, null, true);
		//#elif polish.api.nokia-ui
			DirectGraphics dg = DirectUtils.getDirectGraphics(graphics);
			if ((color & 0xFF000000) == 0) {
				color |= 0xFF000000;
			}
			dg.drawPolygon(xPoints, 0, yPoints, 0, xPoints.length, color );
        //#else
        	// use default mechanism
	        int length = xPoints.length - 1;
			graphics.setColor( color );
	        for(int i = 0; i < length; i++) {
	            graphics.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
	        }
	        graphics.drawLine(xPoints[length], yPoints[length], xPoints[0], yPoints[0]);
        //#endif
    }

	/**
	 * Draws a filled out polygon.
	 *
	 * @param xPoints the x coordinates of the polygon
	 * @param yPoints the y coordinates of the polygon
	 * @param color the color of the polygon
	 */
	public void fillPolygon( int[] xPoints, int[] yPoints, int color ) {
		//#if polish.blackberry && polish.usePolishGui
			net.rim.device.api.ui.Graphics bbGraphics = null;
			//# bbGraphics = this.graphics.g;
			if ((color & 0xff000000) == 0) {
				color = 0xff000000 | color;
			}
			bbGraphics.setColor( color );
			// rest of translation is handled in de.enough.polish.blackberry.ui.Graphics directly, so we have to adjust
			// this here manually
            int translateX = this.graphics.getTranslateX();
			int translateY = this.graphics.getTranslateY();
			bbGraphics.translate( translateX, translateY );
			bbGraphics.drawFilledPath( xPoints, yPoints, null, null);
			bbGraphics.translate( -translateX, -translateY );
		//#elif polish.api.nokia-ui
			DirectGraphics dg = DirectUtils.getDirectGraphics(this.graphics);
			if ((color & 0xFF000000) == 0) {
				color |= 0xFF000000;
			}
			dg.fillPolygon(xPoints, 0, yPoints, 0, xPoints.length, color );
		//#else
			// ... use default mechanism by simple triangulation of the polygon. Holes within the polygon are not supported.
			// This code is based on JMicroPolygon: http://sourceforge.net/projects/jmicropolygon
			while (xPoints.length > 2) {
				// a, b & c represents a candidate triangle to draw.
				// a is the left-most point of the polygon
				int a = indexOfLeast(xPoints);
				// b is the point after a
				int b = (a + 1) % xPoints.length;
				// c is the point before a
				int c = (a > 0) ? a - 1 : xPoints.length - 1;
				// The value leastInternalIndex holds the index of the left-most
				// polygon point found within the candidate triangle, if any.
				int leastInternalIndex = -1;
				boolean leastInternalSet = false;
				// If only 3 points in polygon, skip the tests
				if (xPoints.length > 3) {
					// Check if any of the other points are within the candidate triangle
					for (int i=0; i<xPoints.length; i++) {
						if (i != a && i != b && i != c) {
							if (withinBounds(xPoints[i], yPoints[i],
											xPoints[a], yPoints[a],
											xPoints[b], yPoints[b],
											xPoints[c], yPoints[c]))
							{
								// Is this point the left-most point within the candidate triangle?
								if (!leastInternalSet || xPoints[i] < xPoints[leastInternalIndex])
								{
									leastInternalIndex = i;
									leastInternalSet = true;
								}
							}
						}
					}
				}
				// No internal points found, fill the triangle, and reservoir-dog the polygon
				if (!leastInternalSet) {
					graphics.setColor( color );
					//#if polish.midp2
						graphics.fillTriangle(xPoints[a], yPoints[a], xPoints[b], yPoints[b], xPoints[c], yPoints[c]);
					//#else
						fillTriangle(xPoints[a], yPoints[a], xPoints[b], yPoints[b], xPoints[c], yPoints[c]);
					//#endif
					int[][] trimmed = trimEar(xPoints, yPoints, a);
					xPoints = trimmed[0];
					yPoints = trimmed[1];
					// Internal points found, split the polygon into two, using the line between
					// "a" (left-most point of the polygon) and leastInternalIndex (left-most
					// polygon-point within the candidate triangle) and recurse with each new polygon
				} else {
					int[][][] split = split(xPoints, yPoints, a, leastInternalIndex);
					int[][] poly1 = split[0];
					int[][] poly2 = split[1];
					fillPolygon( poly1[0], poly1[1], color );
					fillPolygon( poly2[0], poly2[1], color );
					break;
				}
			}
		//#endif
	}

	/**
	 * Fills the specified triangle with the current color.
	 *
	 * @param x1 the x coordinate of the first vertex of the triangle
	 * @param y1 the y coordinate of the first vertex of the triangle
	 * @param x2 the x coordinate of the second vertex of the triangle
	 * @param y2 the y coordinate of the second vertex of the triangle
	 * @param x3 the x coordinate of the third vertex of the triangle
	 * @param y3 the y coordinate of the third vertex of the triangle
	 */
	public void fillTriangle(int x1,
            int y1,
            int x2,
            int y2,
            int x3,
            int y3)
	{
		//#if polish.midp2
			graphics.fillTriangle(x1, y1, x2, y2, x3, y3);
		//#elif polish.hasFloatingPoint
			int tmp;

			if (y1 > y2) {
				tmp = x1; x1 = x2; x2 = tmp;
				tmp = y1; y1 = y2; y2 = tmp;
			}

			if (y1 > y3) {
				tmp = x1; x1 = x3; x3 = tmp;
				tmp = y1; y1 = y3; y3 = tmp;
			}

			if (y2 > y3) {
				tmp = x2; x2 = x3; x3 = tmp;
				tmp = y2; y2 = y3; y3 = tmp;
			}

			double dx1, dx2, dx3;

			if (y2 - y1 > 0)
				dx1 = (double) (x2 - x1) / (double) (y2 - y1);
			else
				dx1 = 0;

			if (y3 - y1 > 0)
				dx2 = (double) (x3 - x1) / (double) (y3 - y1);
			else
				dx2 = 0;

			if (y3 - y2 > 0)
				dx3 = (double) (x3 - x2) / (double) (y3 - y2);
			else
				dx3 = 0;

			int pos_x1 = 0, pos_x2 = 0, pos_y = 0;

			pos_y = y1;
			int index1 = 1, index2 = 1;

			if (dx1 > dx2) {
				for (; pos_y < y2; pos_y++, index1++, index2++) {
					pos_x1 = x1 + (int) Math.floor(dx2 * index1 + 0.5);
					pos_x2 = x1 + (int) Math.floor(dx1 * index2 + 0.5);
					graphics.drawLine(pos_x1, pos_y, pos_x2, pos_y);
				}

				pos_x2 = x2;
				index2 = 0;

				for (; pos_y <= y3; pos_y++, index1++, index2++) {
					pos_x1 = x1 + (int) Math.floor(dx2 * index1 + 0.5);
					pos_x2 = x2 + (int) Math.floor(dx3 * index2 + 0.5);
					graphics.drawLine(pos_x1, pos_y, pos_x2, pos_y);
				}
			}
			else {
				for (; pos_y < y2; pos_y++, index1++, index2++) {
					pos_x1 = x1 + (int) Math.floor(dx1 * index1 + 0.5);
					pos_x2 = x1 + (int) Math.floor(dx2 * index2 + 0.5);
					graphics.drawLine(pos_x1, pos_y, pos_x2, pos_y);
				}

				pos_x1 = x2;
				index1 = 0;

				for (; pos_y <= y3; pos_y++, index1++, index2++) {
					pos_x1 = x2 + (int) Math.floor(dx3 * index1 + 0.5);
					pos_x2 = x1 + (int) Math.floor(dx2 * index2 + 0.5);
					graphics.drawLine(pos_x1, pos_y, pos_x2, pos_y);
				}
			}
		//#else
			int centerX = getCenter( x1, x2, x3 );
			int centerY = getCenter( y1, y2, y3 );
			boolean isPositionMoved;
			do {
				// drawTriangle( x1, y1, x2, y2, x3, y3, g );
				graphics.drawLine( x1, y1, x2, y2 );
				graphics.drawLine( x2, y2, x3, y3 );
				graphics.drawLine( x3, y3, x1, y1 );

				isPositionMoved = false;
				if (x1 < centerX) {
					x1++;
					isPositionMoved = true;
				} else if (x1 > centerX) {
					x1--;
					isPositionMoved = true;
				}
				if (x2 < centerX) {
					x2++;
					isPositionMoved = true;
				} else if (x2 > centerX) {
					x2--;
					isPositionMoved = true;
				}
				if (x3 < centerX) {
					x3++;
					isPositionMoved = true;
				} else if (x3 > centerX) {
					x3--;
					isPositionMoved = true;
				}
				if (y1 < centerY) {
					y1++;
					isPositionMoved = true;
				} else if (y1 > centerY) {
					y1--;
					isPositionMoved = true;
				}
				if (y2 < centerY) {
					y2++;
					isPositionMoved = true;
				} else if (y2 > centerY) {
					y2--;
					isPositionMoved = true;
				}
				if (y3 < centerY) {
					y3++;
					isPositionMoved = true;
				} else if (y3 > centerY) {
					y3--;
					isPositionMoved = true;
				}
			} while (isPositionMoved);
		//#endif
	}

        /**
	 * Retrieves the center position of all numbers
	 * @param n1 first number
	 * @param n2 second number
	 * @param n3 third number
	 * @return the center of all numbers: min( n1, n2, n3 ) +  (max( n1, n2, n3 ) - min( n1, n2, n3 )) / 2
	 */
	public static int getCenter(int n1, int n2, int n3) {
		int max = Math.max( n1, Math.max( n2, n3) );
		int min = Math.min( n1, Math.min( n2, n3 ) );
		return min + ((max - min) / 2);
	}

	/**
	 * Draws the specified triangle.
	 *
	 * @param x1 the x coordinate of the first vertex of the triangle
	 * @param y1 the y coordinate of the first vertex of the triangle
	 * @param x2 the x coordinate of the second vertex of the triangle
	 * @param y2 the y coordinate of the second vertex of the triangle
	 * @param x3 the x coordinate of the third vertex of the triangle
	 * @param y3 the y coordinate of the third vertex of the triangle
	 */
	public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
		graphics.drawLine( x1, y1, x2, y2 );
		graphics.drawLine( x2, y2, x3, y3 );
		graphics.drawLine( x3, y3, x1, y1 );
	}

	/**
	 * Finds the index of the smallest element
	 *
	 * @param elements the elements
	 * @return the index of the smallest element
	 */
	static int indexOfLeast(int[] elements) {
		int index = 0;
		int least = elements[0];
		for (int i=1; i<elements.length; i++) {
			if (elements[i] < least) {
				index = i;
				least = elements[i];
			}
		}
		return index;
	}

	/**
	 * Checks whether the specified point px, py is within the triangle defined by ax, ay, bx, by and cx, cy.
	 *
	 * @param px The x of the point to test
	 * @param py The y of the point to test
	 * @param ax The x of the 1st point of the triangle
	 * @param ay The y of the 1st point of the triangle
	 * @param bx The x of the 2nd point of the triangle
	 * @param by The y of the 2nd point of the triangle
	 * @param cx The x of the 3rd point of the triangle
	 * @param cy The y of the 3rd point of the triangle
	 * @return true when the point is within the given triangle
	 */
	private static boolean withinBounds(int px, int py,
								int ax, int ay,
								int bx, int by,
								int cx, int cy)
	{
		if (   px < Math.min(ax, Math.min( bx, cx ) )
				|| px > Math.max(ax, Math.max( bx, cx ) )
				|| py < Math.min(ay, Math.min( by, cy ) )
				|| py > Math.max(ay, Math.max( by, cy ) ) )
		{
			return false;
		}
		boolean sameabc = sameSide(px, py, ax, ay, bx, by, cx, cy);
		boolean samebac = sameSide(px, py, bx, by, ax, ay, cx, cy);
		boolean samecab = sameSide(px, py, cx, cy, ax, ay, bx, by);
		return sameabc && samebac && samecab;
	}

	private static boolean sameSide (int p1x, int p1y, int p2x, int p2y,
							int l1x, int l1y, int l2x, int l2y)
	{
		long lhs = ((p1x - l1x) * (l2y - l1y) - (l2x - l1x) * (p1y - l1y));
		long rhs = ((p2x - l1x) * (l2y - l1y) - (l2x - l1x) * (p2y - l1y));
		long product = lhs * rhs;
		boolean result = product >= 0;
		return result;
	}

	private static int[][] trimEar(int[] xPoints, int[] yPoints, int earIndex) {
		int[] newXPoints = new int[xPoints.length - 1];
		int[] newYPoints = new int[yPoints.length - 1];
		int[][] newPoly = new int[2][];
		newPoly[0] = newXPoints;
		newPoly[1] = newYPoints;
		int p = 0;
		for (int i=0; i<xPoints.length; i++) {
			if (i != earIndex) {
				newXPoints[p] = xPoints[i];
				newYPoints[p] = yPoints[i];
				p++;
			}
		}
		return newPoly;
	}

	private static int[][][] split(int[] xPoints, int[] yPoints, int aIndex, int bIndex) {
		int firstLen, secondLen;
		if (bIndex < aIndex) {
			firstLen = (xPoints.length - aIndex) + bIndex + 1;
		} else {
			firstLen = (bIndex - aIndex) + 1;
		}
		secondLen = (xPoints.length - firstLen) + 2;
		int[][] first = new int[2][firstLen];
		int[][] second = new int[2][secondLen];
		for (int i=0; i<firstLen; i++) {
			int index = (aIndex + i) % xPoints.length;
			first[0][i] = xPoints[index];
			first[1][i] = yPoints[index];
		}
		for (int i=0; i<secondLen; i++) {
			int index = (bIndex + i) % xPoints.length;
			second[0][i] = xPoints[index];
			second[1][i] = yPoints[index];
		}
		int[][][] result = new int[2][][];
		result[0] = first;
		result[1] = second;
		return result;
	}

	/**
	 * Creates a gradient of colors.
	 * This method is highly optimized and only uses bit-shifting and additions (no multiplication nor devision), but
	 * it will create a new integer array in each call.
	 *
	 * @param startColor the first color
	 * @param endColor the last color
	 * @param steps the number of colors in the gradient,
	 *        when 2 is given, the first one will be the startColor and the second one will the endColor.
	 * @return an int array with the gradient.
	 * @see #getGradient(int, int, int[])
	 * @see #getGradientColor(int, int, int)
	 */
	public static final int[] getGradient( int startColor, int endColor, int steps ) {
		if (steps <= 0) {
			return new int[0];
		}
		int[] gradient = new int[ steps ];
		getGradient(startColor, endColor, gradient);
		return gradient;

	}

	/**
	 * Creates a gradient of colors.
	 * This method is highly optimized and only uses bit-shifting and additions (no multiplication nor devision).
	 *
	 * @param startColor the first color
	 * @param endColor the last color
	 * @param gradient the array in which the gradient colors are stored.
	 * @see #getGradientColor(int, int, int, int)
	 */
	public static final void getGradient(int startColor, int endColor, int[] gradient) {
		int steps = gradient.length;
		if (steps == 0) {
			return;
		} else if (steps == 1) {
			gradient[0] = startColor;
			return;
		}
		int startAlpha = startColor >>> 24;
		int startRed = (startColor >>> 16) & 0x00FF;
		int startGreen = (startColor >>> 8) & 0x0000FF;
		int startBlue = startColor  & 0x00000FF;

		int endAlpha = endColor >>> 24;
		int endRed = (endColor >>> 16) & 0x00FF;
		int endGreen = (endColor >>> 8) & 0x0000FF;
		int endBlue = endColor  & 0x00000FF;

		int stepAlpha = ((endAlpha - startAlpha) << 8) / (steps-1);
		int stepRed = ((endRed -startRed) << 8) / (steps-1);
		int stepGreen = ((endGreen - startGreen) << 8) / (steps-1);
		int stepBlue = ((endBlue - startBlue) << 8) / (steps-1);
//		System.out.println("step red=" + Integer.toHexString(stepRed));
//		System.out.println("step green=" + Integer.toHexString(stepGreen));
//		System.out.println("step blue=" + Integer.toHexString(stepBlue));

		startAlpha <<= 8;
		startRed <<= 8;
		startGreen <<= 8;
		startBlue <<= 8;

		gradient[0] = startColor;
		for (int i = 1; i < steps; i++) {
			startAlpha += stepAlpha;
			startRed += stepRed;
			startGreen += stepGreen;
			startBlue += stepBlue;

			gradient[i] = (( startAlpha << 16) & 0xFF000000)
				| (( startRed << 8) & 0x00FF0000)
				| ( startGreen & 0x0000FF00)
				| ( startBlue >>> 8);
				//| (( startBlue >>> 8) & 0x000000FF);
		}
	}

	/**
	 * Retrieves the gradient color between the given start and end colors.
	 *
	 * @param startColor the start color
	 * @param endColor the end color
	 * @param permille the permille between 0 and 1000 - 0 will return the startColor, 1000 the endColor,
	 * 			500 a gradient color directly in the middlet between start and endcolor.
	 * @return the gradient color
	 */
	public static final int getGradientColor( int startColor, int endColor, int permille ) {
		int alpha = startColor >>> 24;
		int red = (startColor >>> 16) & 0x00FF;
		int green = (startColor >>> 8) & 0x0000FF;
		int blue = startColor  & 0x000000FF;

		int diffAlpha = (endColor >>> 24) - alpha;
		int diffRed   = ( (endColor >>> 16) & 0x00FF ) - red;
		int diffGreen = ( (endColor >>> 8) & 0x0000FF ) - green;
		int diffBlue  = ( endColor  & 0x000000FF ) - blue;

		alpha += (diffAlpha * permille) / 1000;
		red   += (diffRed   * permille) / 1000;
		green += (diffGreen * permille) / 1000;
		blue  += (diffBlue  * permille) / 1000;

		return ( alpha << 24 )
		     | ( red   << 16 )
		     | ( green <<  8 )
		     | ( blue        );


//		return (( alpha << 24) & 0xFF000000)
//			| (( red << 16) & 0x00FF0000)
//			| ( (green << 8) & 0x0000FF00)
//			| ( blue );
	}

	/**
	 * Retrieves the gradient color between the given start and end colors.
	 * This method returns getGradientColor(startColor, endColor, (step * 1000)/numberOfSteps);
	 *
	 * @param startColor the start color
	 * @param endColor the end color
	 * @param step the step/position within the gradient
	 * @param numberOfSteps the maxium step (=100%)
	 * @return the gradient color
	 * @see #getGradientColor(int, int, int)
	 */
	public static final int getGradientColor( int startColor, int endColor, int step, int numberOfSteps ) {
		int permille = (step * 1000) / numberOfSteps;
		return getGradientColor(startColor, endColor, permille);
	}

	/**
	 * Retrieves the complementary color to the specified one.
	 *
	 * @param color the original argb color
	 * @return the complementary color with the same alpha value
	 */
	public static int getComplementaryColor( int color ) {
		return  ( 0xFF000000 & color )
			| ((255 - (( 0x00FF0000 & color ) >> 16)) << 16)
			| ((255 - (( 0x0000FF00 & color ) >> 8)) << 8)
			| (255 - ( 0x000000FF & color ) );
	}


	/**
	 * <p>Paints a dropshadow behind a given ARGB-Array, whereas you are able to specify
	 *  the shadows inner and outer color.</p>
	 * <p>Note that the dropshadow just works for fully opaque pixels and that it needs
	 * a transparent margin to draw the shadow.
	 * </p>
	 * <p>Choosing the same inner and outer color and varying the transparency is recommended.
	 *  Dropshadow just works for fully opaque pixels.</p>
	 *
	 * @param argbData the images ARGB-Array
	 * @param width the width of the ARGB-Array
	 * @param height the width of the ARGB-Array
	 * @param xOffset use this for finetuning the shadow's horizontal position. Negative values move the shadow to the left.
	 * @param yOffset use this for finetuning the shadow's vertical position. Negative values move the shadow to the top.
	 * @param size use this for finetuning the shadows radius.
	 * @param innerColor the inner color of the shadow, which should be less opaque than the text.
	 * @param outerColor the outer color of the shadow, which should be less than opaque the inner color.
	 *
	 */
	public final static void dropShadow(int[] argbData, int width, int height,int xOffset, int yOffset, int size, int innerColor, int outerColor){

		// additional Margin for the image because of the shadow
		int iLeft = size-xOffset<0 ? 0 : size-xOffset;
		int iRight = size+xOffset<0 ? 0 : size+xOffset;
		int iTop = size-yOffset<0 ? 0 : size-yOffset;
		int iBottom = size+yOffset<0 ? 0 : size+yOffset;

		// set colors
		int[] gradient = getGradient( innerColor, outerColor, size );

		// walk over the text and look for non-transparent Pixels
		for (int ix=-size+1; ix<size; ix++){
			for (int iy=-size+1; iy<size; iy++){
				//int gColor=gradient[ Math.max(Math.abs(ix),Math.abs(iy))];
				//int gColor=gradient[(Math.abs(ix)+Math.abs(iy))/2];

				// compute the color and draw all shadowPixels with offset (ix, iy)
				//#if polish.cldc1.1
					int r = (int) Math.sqrt(ix*ix+iy*iy); // TODO: this might be a bit slowly
				//#elif polish.cldc1.0
					//# int r = (Math.abs(ix)+Math.abs(iy))/2; // TODO: this looks a bit uncool
				//#endif
				if ( r<size) {
					int gColor = gradient[ r ];

					for (int col=iLeft,row; col<width/*+iLeft*/-iRight; col++) {
						for (row=iTop;row<height-iBottom/*+iTop*/-1;row++){

							// draw if an opaque pixel is found and the destination is less opaque then the shadow
							if (argbData[row*(width /*+ size*2*/) + col]>>>24==0xFF
									&& argbData[(row+yOffset+iy)*(width /* size*2*/) + col+xOffset+ix]>>>24 < gColor>>>24)
							{
								argbData[(row+yOffset+iy)*(width /*+ size*2*/) + col+xOffset+ix]=gColor;
							}
						}
					}
				}
			}
		}

	}

	static int COLOR_BIT_MASK	= 0x000000FF;
	public static byte[][] FILTER_GAUSSIAN_2 = // a small and fast gaussian filtermatrix
									 {{1,2,1},
									  {2,4,2},
									  {1,2,1}};
	public static byte[][] FILTER_GAUSSIAN_3 = // a gaussian filtermatrix
	       			        {{0,1,2,1,0},
	       					 {1,3,5,3,1},
	       					 {2,5,9,5,2},
	       					 {1,3,5,3,1},
	       					 {0,1,2,1,0}};

	/**
	 * Performs a convolution of an image with a given matrix.
	 * @param filterMatrix a matrix, which should have odd rows an colums (not neccessarily a square). The matrix is used for a 2-dimensional convolution. Negative values are possible.
	 * @param brightness you can vary the brightness of the image measured in percent. Note that the algorithm tries to keep the original brightness as far as is possible.
	 * @param argbData the image (RGB+transparency)
	 * @param width of the given Image
	 * @param height of the given Image
	 * Be aware that the computation time depends on the size of the matrix.
	 */
	public final static void applyFilter(byte[][] filterMatrix, int brightness, int[] argbData, int width, int height) {

		// check whether the matrix is ok
		if (filterMatrix.length % 2 !=1 || filterMatrix[0].length % 2 !=1 ){
			 throw new IllegalArgumentException();
		}

		int fhRadius=filterMatrix.length/2+1;
		int fwRadius=filterMatrix[0].length/2+1;
		int currentPixel=0;
		int newTran, newRed, newGreen, newBlue;

		// compute the bightness
		int divisor=0;
		for (int fCol, fRow=0; fRow < filterMatrix.length; fRow++){
			for (fCol=0; fCol < filterMatrix[0].length; fCol++){
				divisor+=filterMatrix[fRow][fCol];
			}
		}
		// TODO: if (divisor==0), because of negativ matrixvalues
		if (divisor==0) {
			return; // no brightness
		}

		// copy the neccessary imagedata into a small buffer
		int[] tmpRect=new int[width*(filterMatrix.length)];
		System.arraycopy(argbData,0, tmpRect,0, width*(filterMatrix.length));

		for (int fCol, fRow, col, row=fhRadius-1; row+fhRadius<height+1; row++){
			for (col=fwRadius-1; col+fwRadius<width+1; col++){

				// perform the convolution
				newTran=0; newRed=0; newGreen=0; newBlue=0;

				for (fRow=0; fRow<filterMatrix.length; fRow++){

					for (fCol=0; fCol<filterMatrix[0].length;fCol++){

						// take the Data from the little buffer and skale the color
						currentPixel = tmpRect[fRow*width+col+fCol-fwRadius+1];
						if (((currentPixel >>> 24) & COLOR_BIT_MASK) != 0) {
							newTran	+= filterMatrix[fRow][fCol] * ((currentPixel >>> 24) & COLOR_BIT_MASK);
							newRed	+= filterMatrix[fRow][fCol] * ((currentPixel >>> 16) & COLOR_BIT_MASK);
							newGreen+= filterMatrix[fRow][fCol] * ((currentPixel >>> 8) & COLOR_BIT_MASK);
							newBlue	+= filterMatrix[fRow][fCol] * (currentPixel & COLOR_BIT_MASK);
						}

					}
				}

				// calculate the color
				newTran = newTran * brightness/100/divisor;
				newRed  = newRed  * brightness/100/divisor;
				newGreen= newGreen* brightness/100/divisor;
				newBlue = newBlue * brightness/100/divisor;

				newTran =Math.max(0,Math.min(255,newTran));
				newRed  =Math.max(0,Math.min(255,newRed));
				newGreen=Math.max(0,Math.min(255,newGreen));
				newBlue =Math.max(0,Math.min(255,newBlue));
				argbData[(row)*width+col]=(newTran<<24 | newRed<<16 | newGreen <<8 | newBlue);

			}

			// shift the buffer if we are not near the end
			if (row+fhRadius!=height) {
				System.arraycopy(tmpRect,width, tmpRect,0, width*(filterMatrix.length-1));	// shift it back
				System.arraycopy(argbData,width*(row+fhRadius), tmpRect,width*(filterMatrix.length-1), width);	// add new data
			}
		}

	}
	/**
	 * This class is used for fadeEffects (FadeTextEffect and FadinAlienGlowEffect).
	 * The you can set a start and an end color as well as some durations.
	 *
	 * Note: stepsIn has to be the same as  stepsOut or 0!
	 *
	 * @author Simon Schmitt
	 */
	public static class FadeUtil{
		public final static int FADE_IN =1;
		public final static int FADE_OUT=2;
		public final static int FADE_LOOP=3;
		public final static int FADE_BREAK=0;

		public int[] gradient;
		public boolean changed;

		public int startColor	=0xFF0080FF;
		public int endColor	=0xFF80FF00;

		public int steps;
		public int delay=0; 				// time till the effect starts
		public int stepsIn=5,stepsOut=5;  	// fading duration
		public int sWaitTimeIn=10; 		// time to stay faded in
		public int sWaitTimeOut=0; 		// time to stay faded out
		public int mode=FADE_LOOP;

		public int cColor;
		public int cStep;

		private void initialize(){
			//System.out.println(" init");

			this.cStep=0;

			switch (this.mode){
			case FADE_OUT:
				this.stepsIn=0;
				this.sWaitTimeIn=0;
				this.cColor=this.endColor;
				break;
			case FADE_IN:
				this.stepsOut=0;
				this.sWaitTimeOut=0;
				this.cColor=this.startColor;
				break;
			default://loop
				this.cColor=this.startColor;
			}

			this.cStep-=this.delay;

			this.steps= this.stepsIn+this.stepsOut+this.sWaitTimeIn+this.sWaitTimeOut;

			this.gradient = getGradient(this.startColor,this.endColor,Math.max(this.stepsIn, this.stepsOut));


		}

		public boolean step(){
			this.cStep++;

			// (re)define everything, if something changed
			if (this.gradient==null | this.changed) {
				initialize();
			}
			this.changed=false;

			// exit, if no animation is neccessary
			if (this.mode == FADE_BREAK){
				return false;
			}
			// we have to ensure that a new picture is drawn
			if (this.cStep<0){
				return true;
			}

			// set counter to zero (in case of a loop) or stop the engine, when we reached the end
			if (this.cStep==this.steps){
				this.cStep=0;

				if (this.mode != FADE_LOOP) {
					this.mode = FADE_BREAK;
					return true;
				}
			}

			if (this.cStep<this.stepsIn){
				// fade in
				this.cColor=this.gradient[this.cStep];
				//System.out.println("  [in] color:"+this.cStep);
				return true;

			} else if (this.cStep<this.stepsIn+this.sWaitTimeIn){
				// have a break
				if (this.cColor!=this.endColor){
					this.cColor=this.endColor;
					return true;
				}

				//System.out.println("  color:end color");

			} else if( this.cStep<this.stepsIn+this.sWaitTimeIn+this.stepsOut){
				// fade out
				this.cColor=this.gradient[this.stepsIn+this.sWaitTimeIn+this.stepsOut-this.cStep-1];
				//System.out.println("  [out] color:"+(this.stepsIn+this.sWaitTimeIn+this.stepsOut-this.cStep-1));
				return true;

			} else {
				// have another break
				if (this.cColor!=this.startColor){
					this.cColor=this.startColor;
					return true;
				}
				//System.out.println("  color:start color");
			}

			// it sees as if we had no change...
			return false;
		}
	}


        // NEW METHODS


    //#if polish.hasFloatingPoint
    /**
     * Draws a thick line of the specified width, at the specified coordinates, having the specified angle.
     * @param width the width (or thickness) of the line
     * @param length the length of the line
     * @param startX the X start coordinate
     * @param startY the Y start coordinate
     * @param degrees the line angle (in degrees)
     */
    public void drawAngledLine( int width, int length, int startX, int startY, int degrees )
    {
        drawAngledLine ( width, length, 0, startX, startY, degrees );
    }
    //#endif

    //#if polish.hasFloatingPoint
    /**
     * Draws a thick line of the specified width, at the specified coordinates, having the specified angle and the specified overlap length. The point defined by the target coordinates, along with the line angle, define a vector. The overlapLength parameter can be used to "slide" the line on this vector. For example, an overlap length of zero means the left endpoint of the line will coincide with the target coordinates. Increasing the overlap length will "slide" the line in the opposite direction of that defined by vector. 
     * @param width the width (or thickness) of the line
     * @param length the length of the line
     * @param overlapLength the amount (in pixels) by which to slide the line along the vector defined by the target coordinates and the line angle
     * @param targetX the X target coordinate
     * @param targetY the Y target coordinate
     * @param degrees the line angle (in degrees)
     */
    public void drawAngledLine( int width, int length, int overlapLength, int targetX, int targetY, int degrees )
    {
        double lineCos = Math.cos ( (Math.PI * degrees) / 180 );
        double lineSin = Math.sin ( (Math.PI * degrees) / 180 );

        int mainLength = length - overlapLength ;

        int x1 = (int) ( targetX + mainLength * lineCos );
        int y1 = (int) ( targetY - mainLength * lineSin );
        int x2 = (int) ( targetX - overlapLength * lineCos );
        int y2 = (int) ( targetY + overlapLength * lineSin );
        drawLine ( width, x1,y1,x2,y2);
    }
    //#endif

    //#if polish.hasFloatingPoint
    /**
     * Draws a line of the specified width
     * @param width the line width
     * @param x1 the x coordinate of the starting point
     * @param y1 the y coordinate of the starting point
     * @param x2 the x coordinate of the end point
     * @param y2 the y coordinate of the end point
     */
    public void drawLine( int width, int x1, int y1, int x2, int y2 )
    {
        if ( width == 1)
        {
            graphics.drawLine(x1, y1, x2, y2);
            return;
        }

        // Calculate line width and other stuff
        double pointDistance = Math.sqrt ( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) ) ;
        int startWidth = width / 2;
        int remainingWidth = width - startWidth ;

        System.out.println ( startWidth + " " + remainingWidth );

        double lineCos = (x1-x2) / pointDistance;
        double lineSin = ( y1-y2 ) / pointDistance ;
        double cosPerpendicularLine =  - lineSin ;
        double sinPerpendicularLine = lineCos ;

        // Calculate the polygon defining the line
        int tx1 = (int)( x1 + cosPerpendicularLine * startWidth );
        int tx2 = (int)( x1 - cosPerpendicularLine * remainingWidth );
        int tx3 = (int)( x2 - cosPerpendicularLine * remainingWidth );
        int tx4 = (int)( x2 + cosPerpendicularLine * startWidth );
        int ty1 = (int)( y1 + sinPerpendicularLine * startWidth );
        int ty2 = (int)( y1 - sinPerpendicularLine * remainingWidth );
        int ty3 = (int)( y2 - sinPerpendicularLine * remainingWidth );
        int ty4 = (int)( y2 + sinPerpendicularLine * startWidth );

        // Draw the polygon
        fillTriangle(tx1, ty1, tx2, ty2, tx3, ty3);
        fillTriangle(tx1, ty1, tx4, ty4, tx3, ty3);

    }
    //#endif

    //#if polish.hasFloatingPoint
    /**
     * Draws a circular or elliptical arc covering the specified rectangle, with the specified thickness. The drawing is done by approximating the arc with a polygon having enough vertices so that the arc appears relatively smooth.
     * The resulting arc begins at startAngle and extends for arcAngle degrees. Angles are interpreted such that 0 degrees is at the 3 o'clock position. A positive value indicates a counter-clockwise rotation while a negative value indicates a clockwise rotation.
     * The center of the arc is the center of the rectangle whose origin is (x, y) and whose size is specified by the width and height arguments.
     * If either width or height is zero or less, nothing is drawn.
     * The angles are specified relative to the non-square extents of the bounding rectangle such that 45 degrees always falls on the line from the center of the ellipse to the upper right corner of the bounding rectangle. As a result, if the bounding rectangle is noticeably longer in one axis than the other, the angles to the start and end of the arc segment will be skewed farther along the longer axis of the bounds.
     * @param thickness
     * @param x the x coordinate of the rectangle
     * @param y the y coordinate of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param startAngle the starting angle of the arc
     * @param arcAngle the length of the arc
     */
    public void drawArc( int thickness, int x, int y, int width, int height, int startAngle, int arcAngle)
    {
        int perimeter = (int) ( Math.PI * ( 3 * ( width + height) - Math.sqrt ( (3*width+height)*(width+3*height) ) ) );

        int noOfPoints = (int) ( ( ( (perimeter)  ) * Math.abs ( arcAngle ) ) / 360   );
        System.out.println("PTS: " + noOfPoints);
        drawArc ( thickness, x, y, width, height, startAngle, arcAngle, noOfPoints);
    }
    //#endif
    
    //#if polish.hasFloatingPoint
    /**
     * Draws a circular or elliptical arc covering the specified rectangle, with the specified thickness. The drawing is done by approximating the arc with a polygon having the specified number of vertices.
     * The resulting arc begins at startAngle and extends for arcAngle degrees. Angles are interpreted such that 0 degrees is at the 3 o'clock position. A positive value indicates a counter-clockwise rotation while a negative value indicates a clockwise rotation.
     * The center of the arc is the center of the rectangle whose origin is (x, y) and whose size is specified by the width and height arguments.
     * If either width or height is zero or less, nothing is drawn.
     * The angles are specified relative to the non-square extents of the bounding rectangle such that 45 degrees always falls on the line from the center of the ellipse to the upper right corner of the bounding rectangle. As a result, if the bounding rectangle is noticeably longer in one axis than the other, the angles to the start and end of the arc segment will be skewed farther along the longer axis of the bounds.
     * @param thickness
     * @param x the x coordinate of the rectangle
     * @param y the y coordinate of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param startAngle the starting angle of the arc
     * @param arcAngle the length of the arc
     * @param noOfVertices the number of vertices of the polygon approximating the arc
     */
    public void drawArc( int thickness, int x, int y, int width, int height, int startAngle, int arcAngle, int noOfVertices)
    {
        // To draw a thick arc, we actually use two helper arcs, a big one ( corresponding to the "outer portion" of the thick arc -
        // the pixels that are furthest away from the center), and small one ( corresponding to the "inner portion" of the thick arc -
        // the pixels that are closest to the center).
        // The distance between the two arcs is equal to the thickness of our desired thick arc.
        // Thus, the pixels between the two arcs are the pixels of our desired thick arc. We can now approximate the thick arc with a polygon
        // whose vertices are the vertices of the two helper arcs, and we can then fill this polygon to obtain our desired arc.

        int endAngle = startAngle + arcAngle ;
        if ( endAngle < startAngle )
        {
            int temp;
            temp = endAngle ;
            endAngle = startAngle ;
            startAngle = temp;
        }
        
        double startAngleRadians = ( Math.PI * startAngle ) / 180;
        double endAngleRadians = ( Math.PI * endAngle ) / 180;
        double angleStep = ( Math.PI * ( Math.abs(arcAngle) / ( 1.0 * noOfVertices ) ) ) / 180;

        // Calculate the "outer" arc parameters
        double bigWidth =  ( width / 2.0);
        double bigHeight =  ( height / 2.0 );

        // Calculate the "inner" arc parameters
        double smallWidth = ( (width -  thickness ) / 2.0);
        double smallHeight = ( ( height - thickness ) / 2.0 );

        
        int centerX = x + width / 2;
        int centerY = y + height / 2;

        // Draw the polygon approximating the arc one arc segment at a time
        double theta = startAngleRadians;
        int x1,y1,x2,y2,x3,y3,x4,y4;
        x1 = (int) ( Math.cos ( theta ) * bigWidth + centerX );
        y1 = (int) ( centerY - Math.sin ( theta ) * bigHeight );
        x2 = (int) ( Math.cos ( theta ) * smallWidth + centerX );
        y2 = (int) ( centerY - Math.sin ( theta ) * smallHeight );
        do
        {
            theta += angleStep ;
            
            x3 = (int) ( Math.cos ( theta ) * smallWidth + centerX );
            y3 = (int) ( centerY - Math.sin ( theta ) * smallHeight  );
            x4 = (int) ( Math.cos ( theta ) * bigWidth + centerX );
            y4 = (int) ( centerY - Math.sin ( theta ) * bigHeight );

            fillTriangle(x1, y1, x2, y2, x3, y3);
            fillTriangle(x3, y3, x4, y4, x1, y1);

            x1 = x4;
            y1 = y4;
            x2 = x3;
            y2 = y3;
        }
        while ( theta <= endAngleRadians) ;
    }
    //#endif

    //#if polish.hasFloatingPoint && !polish.midp1
    /**
     * Draws a rotated Image at the specified coordinates.
     * @param image the image to draw
     * @param imageCenterX the image rotation center X coordinate
     * @param imageCenterY the image rotation center Y coordinate
     * @param targetCenterX the X coordinate of the rotation center on the Graphics object
     * @param targetCenterY the Y coordinate of the rotation center on the Graphics object
     * @param degrees the rotation angle (in degrees).
     */
    public void drawRotatedImage( Image image, int imageCenterX, int imageCenterY, int targetCenterX, int targetCenterY, int degrees )
    {
        RgbImage img = new RgbImage(image.getRgbData(), image.getWidth());
        drawRotatedImage(img, imageCenterX, imageCenterY, targetCenterX, targetCenterY, degrees);
    }
    //#endif

    //#if polish.hasFloatingPoint
    /**
     * Draws a rotated RgbImage at the specified coordinates.
     * @param img the image to draw
     * @param imageCenterX the image rotation center X coordinate
     * @param imageCenterY the image rotation center Y coordinate
     * @param targetCenterX the X coordinate of the rotation center on the Graphics object
     * @param targetCenterY the Y coordinate of the rotation center on the Graphics object
     * @param degrees the rotation angle (in degrees).
     */
    public void drawRotatedImage( RgbImage img, int imageCenterX, int imageCenterY, int targetCenterX, int targetCenterY, int degrees )
    {

        int originalWidth = img.getWidth();
        int originalHeight = img.getHeight() ;
        // Rotate the image around its center
        degrees = - ( degrees - 90 ); // We use the correct trigonometric sense, and we consider 0 degrees to be at 3 o'clock.
        ImageUtil.rotate(img, degrees);

        // Calculate the cos and sin of the rotation angle
        double degreeCos = Math.cos(Math.PI * degrees / 180);
        double degreeSin = Math.sin(Math.PI * degrees / 180);

        // Calculate the delta between the image center and the image rotation point
        int centerXDelta = imageCenterX - originalWidth/2;
        int centerYDelta = imageCenterY - originalHeight/2;

        // Calculate the coordinates of the rotation point after the image has been rotated,
        // with respect to the image's center
        int newCenterX = (int) (centerXDelta * degreeCos - centerYDelta * degreeSin );
        int newCenterY = (int) (centerXDelta * degreeSin + centerYDelta * degreeCos );

        // Calculate the coordinates of the top-left corner of the rotated image
        int posX = targetCenterX - img.getWidth()/2 ;
        int posY = targetCenterY - img.getHeight()/2 ;

        // Apply the necessary offset so that the rotated image's rotation point
        // falls at the specfied point on the Graphics object and draw the rotated image
        img.paint (posX - newCenterX, posY - newCenterY, graphics);

    }
    //#endif
}
