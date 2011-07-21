//#condition polish.usePolishGui
/*
 * Created on Aug 12, 2008 at 8:34:34 PM.
 * 
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
 * <p>Allows to use native display functions in a portable fashion.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface NativeDisplay
{
	/**
	 * Image type for <code>List</code> element image.
	 * 
	 * <P>The value of <code>LIST_ELEMENT</code> is <code>1</code>.</P>
	 * <DT><B>See Also: </B>
	 * <A HREF="../../../javax/microedition/lcdui/Display.html#getBestImageHeight(int)"><CODE>getBestImageHeight(int imageType)</CODE></A>
	 * 
	 * @since MIDP 2.0
	 */
	int LIST_ELEMENT = 1;

	/**
	 * Image type for <code>ChoiceGroup</code> element image.
	 * 
	 * <P>The value of <code>CHOICE_GROUP_ELEMENT</code> is <code>2</code>.</P>
	 * <DT><B>See Also: </B>
	 * <A HREF="../../../javax/microedition/lcdui/Display.html#getBestImageHeight(int)"><CODE>getBestImageHeight(int imageType)</CODE></A>
	 * 
	 * @since MIDP 2.0
	 */
	int CHOICE_GROUP_ELEMENT = 2;

	/**
	 * Image type for <code>Alert</code> image.
	 * 
	 * <P>The value of <code>ALERT</code> is <code>3</code>.</P>
	 * <DT><B>See Also: </B>
	 * <A HREF="../../../javax/microedition/lcdui/Display.html#getBestImageHeight(int)"><CODE>getBestImageHeight(int imageType)</CODE></A>
	 * 
	 * @since MIDP 2.0
	 */
	int ALERT = 3;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_BACKGROUND</code> specifies the background color of
	 * the screen.
	 * The background color will always contrast with the foreground color.
	 * 
	 * <p>
	 * <code>COLOR_BACKGROUND</code> has the value <code>0</code>.
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	int COLOR_BACKGROUND = 0;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_FOREGROUND</code> specifies the foreground color,
	 * for text characters
	 * and simple graphics on the screen.  Static text or user-editable
	 * text should be drawn with the foreground color.  The foreground color
	 * will always constrast with background color.
	 * 
	 * <p> <code>COLOR_FOREGROUND</code> has the value <code>1</code>.
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	int COLOR_FOREGROUND = 1;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_HIGHLIGHTED_BACKGROUND</code> identifies the color for the
	 * focus, or focus highlight, when it is drawn as a
	 * filled in rectangle. The highlighted
	 * background will always constrast with the highlighted foreground.
	 * 
	 * <p>
	 * <code>COLOR_HIGHLIGHTED_BACKGROUND</code> has the value <code>2</code>.
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	int COLOR_HIGHLIGHTED_BACKGROUND = 2;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_HIGHLIGHTED_FOREGROUND</code> identifies the color for text
	 * characters and simple graphics when they are highlighted.
	 * Highlighted
	 * foreground is the color to be used to draw the highlighted text
	 * and graphics against the highlighted background.
	 * The highlighted foreground will always constrast with
	 * the highlighted background.
	 * 
	 * <p>
	 * <code>COLOR_HIGHLIGHTED_FOREGROUND</code> has the value <code>3</code>.
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	int COLOR_HIGHLIGHTED_FOREGROUND = 3;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_BORDER</code> identifies the color for boxes and borders
	 * when the object is to be drawn in a
	 * non-highlighted state.  The border color is intended to be used with
	 * the background color and will contrast with it.
	 * The application should draw its borders using the stroke style returned
	 * by <code>getBorderStyle()</code>.
	 * 
	 * <p> <code>COLOR_BORDER</code> has the value <code>4</code>.
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	int COLOR_BORDER = 4;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_HIGHLIGHTED_BORDER</code>
	 * identifies the color for boxes and borders when the object is to be
	 * drawn in a highlighted state.  The highlighted border color is intended
	 * to be used with the background color (not the highlighted background
	 * color) and will contrast with it.  The application should draw its
	 * borders using the stroke style returned <code>by getBorderStyle()</code>.
	 * 
	 * <p> <code>COLOR_HIGHLIGHTED_BORDER</code> has the value <code>5</code>.
	 * 
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	int COLOR_HIGHLIGHTED_BORDER = 5;

	/**
	 * Returns one of the colors from the high level user interface
	 * color scheme, in the form <code>0x00RRGGBB</code> based on the
	 * <code>colorSpecifier</code> passed in.
	 * 
	 * @param colorSpecifier - the predefined color specifier; must be one of COLOR_BACKGROUND, COLOR_FOREGROUND, COLOR_HIGHLIGHTED_BACKGROUND, COLOR_HIGHLIGHTED_FOREGROUND, COLOR_BORDER, or COLOR_HIGHLIGHTED_BORDER
	 * @return color in the form of 0x00RRGGBB
	 * @throws IllegalArgumentException - if colorSpecifier is not a valid color specifier
	 * @since  MIDP 2.0
	 */
	int getColor(int colorSpecifier);

	/**
	 * Returns the stroke style used for border drawing
	 * depending on the state of the component
	 * (highlighted/non-highlighted). For example, on a monochrome
	 * system, the border around a non-highlighted item might be
	 * drawn with a <code>DOTTED</code> stroke style while the border around a
	 * highlighted item might be drawn with a <code>SOLID</code> stroke style.
	 * 
	 * @param highlighted - true if the border style being requested is for the highlighted state, false if the border style being requested is for the non-highlighted state
	 * @return Graphics.DOTTED or Graphics.SOLID
	 * @since  MIDP 2.0
	 */
	int getBorderStyle(boolean highlighted);

	/**
	 * Gets information about color support of the device.
	 * 
	 * @return true if the display supports color,  false otherwise
	 */
	boolean isColor();

	/**
	 * Gets the number of colors (if <code>isColor()</code> is
	 * <code>true</code>)
	 * or graylevels (if <code>isColor()</code> is <code>false</code>)
	 * that can be
	 * represented on the device.<P>
	 * Note that the number of colors for a black and white display is
	 * <code>2</code>.
	 * 
	 * @return number of colors
	 */
	int numColors();

	/**
	 * Gets the number of alpha transparency levels supported by this
	 * implementation.  The minimum legal return value is
	 * <code>2</code>, which indicates
	 * support for full transparency and full opacity and no blending.  Return
	 * values greater than <code>2</code> indicate that alpha blending
	 * is supported.  For
	 * further information, see <a href="Image.html#alpha">Alpha
	 * Processing</a>.
	 * 
	 * @return number of alpha levels supported
	 * @since  MIDP 2.0
	 */
	int numAlphaLevels();

	/**
	 * Requests that a different <code>Displayable</code> object be
	 * made visible on the
	 * display.  The change will typically not take effect immediately.  It
	 * may be delayed so that it occurs between event delivery method
	 * calls, although it is not guaranteed to occur before the next event
	 * delivery method is called.  The <code>setCurrent()</code> method returns
	 * immediately, without waiting for the change to take place.  Because of
	 * this delay, a call to <code>getCurrent()</code> shortly after a
	 * call to <code>setCurrent()</code>
	 * is unlikely to return the value passed to <code>setCurrent()</code>.
	 * 
	 * <p> Calls to <code>setCurrent()</code> are not queued.  A
	 * delayed request made by a
	 * <code>setCurrent()</code> call may be superseded by a subsequent call to
	 * <code>setCurrent()</code>.  For example, if screen
	 * <code>S1</code> is current, then </p>
	 * 
	 * <TABLE BORDER="2">
	 * <TR>
	 * <TD ROWSPAN="1" COLSPAN="1">
	 * <pre><code>
	 * d.setCurrent(S2);
	 * d.setCurrent(S3);     </code></pre>
	 * </TD>
	 * </TR>
	 * </TABLE>
	 * 
	 * <p> may eventually result in <code>S3</code> being made
	 * current, bypassing <code>S2</code>
	 * entirely. </p>
	 * 
	 * <p> When a <code>MIDlet</code> application is first started,
	 * there is no current
	 * <code>Displayable</code> object.  It is the responsibility of
	 * the application to
	 * ensure that a <code>Displayable</code> is visible and can
	 * interact with the user at
	 * all times.  Therefore, the application should always call
	 * <code>setCurrent()</code>
	 * as part of its initialization. </p>
	 * 
	 * <p> The application may pass <code>null</code> as the argument to
	 * <code>setCurrent()</code>.  This does not have the effect of
	 * setting the current
	 * <code>Displayable</code> to <code>null</code>; instead, the
	 * current <code>Displayable</code>
	 * remains unchanged.  However, the application management software may
	 * interpret this call as a request from the application that it is
	 * requesting to be placed into the background.  Similarly, if the
	 * application is in the background, passing a non-null
	 * reference to <code>setCurrent()</code> may be interpreted by
	 * the application
	 * management software as a request that the application is
	 * requesting to be
	 * brought to the foreground.  The request should be considered to be made
	 * even if the current <code>Displayable</code> is passed to the
	 * <code>setCurrent()</code>.  For
	 * example, the code </p>
	 * <TABLE BORDER="2">
	 * <TR>
	 * <TD ROWSPAN="1" COLSPAN="1">
	 * <pre><code>
	 * d.setCurrent(d.getCurrent());    </code></pre>
	 * </TD>
	 * </TR>
	 * </TABLE>
	 * <p> generally will have no effect other than requesting that the
	 * application be brought to the foreground.  These are only requests,
	 * and there is no requirement that the application management
	 * software comply with these requests in a timely fashion if at all. </p>
	 * 
	 * <p> If the <code>Displayable</code> passed to
	 * <code>setCurrent()</code> is an <A HREF="../../../javax/microedition/lcdui/Alert.html"><CODE>Alert</CODE></A>, the previously current <code>Displayable</code>, if
	 * any, is restored after
	 * the <code>Alert</code> has been dismissed.  If there is a
	 * current <code>Displayable</code>, the
	 * effect is as if <code>setCurrent(Alert, getCurrent())</code>
	 * had been called.  Note
	 * that this will result in an exception being thrown if the current
	 * <code>Displayable</code> is already an alert.  If there is no
	 * current <code>Displayable</code>
	 * (which may occur at startup time) the implementation's previous state
	 * will be restored after the <code>Alert</code> has been
	 * dismissed.  The automatic
	 * restoration of the previous <code>Displayable</code> or the
	 * previous state occurs
	 * only when the <code>Alert's</code> default listener is present
	 * on the <code>Alert</code> when it
	 * is dismissed.  See <a href="Alert.html#commands">Alert Commands and
	 * Listeners</a> for details.</p>
	 * 
	 * <p>To specify the
	 * <code>Displayable</code> to be shown after an
	 * <code>Alert</code> is dismissed, the application
	 * should use the <A HREF="../../../javax/microedition/lcdui/Display.html#setCurrent(javax.microedition.lcdui.Alert, javax.microedition.lcdui.Displayable)"><CODE>setCurrent(Alert,
	 * Displayable)</CODE></A> method.  If the application calls
	 * <code>setCurrent()</code> while an
	 * <code>Alert</code> is current, the <code>Alert</code> is
	 * removed from the display and any timer
	 * it may have set is cancelled. </p>
	 * 
	 * <p> If the application calls <code>setCurrent()</code> while a
	 * system screen is
	 * active, the effect may be delayed until after the system screen is
	 * dismissed.  The implementation may choose to interpret
	 * <code>setCurrent()</code> in
	 * such a situation as a request to cancel the effect of the system
	 * screen, regardless of whether <code>setCurrent()</code> has
	 * been delayed. </p>
	 * 
	 * @param nextDisplayable the Display requested to be made current; null is allowed
	 */
	void setCurrent( Display nextDisplayable);
	
	/**
	 * Sets the next native displayable
	 * @param nextDisplayable the next displayable which is not a Canvas
	 */
	void setCurrent( Displayable nextDisplayable);
	
	/**
	 * Causes the <code>Runnable</code> object <code>r</code> to have
	 * its <code>run()</code> method
	 * called later, serialized with the event stream, soon after completion of
	 * the repaint cycle.  As noted in the
	 * <a href="./package-summary.html#events">Event Handling</a>
	 * section of the package summary,
	 * the methods that deliver event notifications to the application
	 * are all called serially. The call to <code>r.run()</code> will
	 * be serialized along with
	 * the event calls into the application. The <code>run()</code>
	 * method will be called exactly once for each call to
	 * <code>callSerially()</code>. Calls to <code>run()</code> will
	 * occur in the order in which they were requested by calls to
	 * <code>callSerially()</code>.
	 * 
	 * <p> If the current <code>Displayable</code> is a <code>Canvas</code>
	 * that has a repaint pending at the time of a call to
	 * <code>callSerially()</code>, the <code>paint()</code> method of the
	 * <code>Canvas</code> will be called and
	 * will return, and a buffer switch will occur (if double buffering is in
	 * effect), before the <code>run()</code> method of the
	 * <code>Runnable</code> is called.
	 * If the current <code>Displayable</code> contains one or more
	 * <code>CustomItems</code> that have repaints pending at the time
	 * of a call to <code>callSerially()</code>, the <code>paint()</code>
	 * methods of the <code>CustomItems</code> will be called and will
	 * return before the <code>run()</code> method of the
	 * <code>Runnable</code> is called.
	 * Calls to the
	 * <code>run()</code> method will occur in a timely fashion, but
	 * they are not guaranteed
	 * to occur immediately after the repaint cycle finishes, or even before
	 * the next event is delivered. </p>
	 * 
	 * <p> The <code>callSerially()</code> method may be called from
	 * any thread. The call to
	 * the <code>run()</code> method will occur independently of the
	 * call to <code>callSerially()</code>.
	 * In particular, <code>callSerially()</code> will <em>never</em>
	 * block waiting
	 * for <code>r.run()</code>
	 * to return. </p>
	 * 
	 * <p> As with other callbacks, the call to <code>r.run()</code>
	 * must return quickly. If
	 * it is necessary to perform a long-running operation, it may be initiated
	 * from within the <code>run()</code> method. The operation itself
	 * should be performed
	 * within another thread, allowing <code>run()</code> to return. </p>
	 * 
	 * <p> The <code>callSerially()</code> facility may be used by
	 * applications to run an
	 * animation that is properly synchronized with the repaint cycle. A
	 * typical application will set up a frame to be displayed and then call
	 * <code>repaint()</code>.  The application must then wait until
	 * the frame is actually
	 * displayed, after which the setup for the next frame may occur.  The call
	 * to <code>run()</code> notifies the application that the
	 * previous frame has finished
	 * painting.  The example below shows <code>callSerially()</code>
	 * being used for this
	 * purpose. </p>
	 * <TABLE BORDER="2">
	 * <TR>
	 * <TD ROWSPAN="1" COLSPAN="1">
	 * <pre><code>
	 * class Animation extends Canvas
	 * implements Runnable {
	 * 
	 * // paint the current frame
	 * void paint(Graphics g) { ... }
	 * 
	 * Display display; // the display for the application
	 * 
	 * void paint(Graphics g) { ... } // paint the current frame
	 * 
	 * void startAnimation() {
	 * // set up initial frame
	 * repaint();
	 * display.callSerially(this);
	 * }
	 * 
	 * // called after previous repaint is finished
	 * void run() {
	 * if ( &#47;* there are more frames *&#47; ) {
	 * // set up the next frame
	 * repaint();
	 * display.callSerially(this);
	 * }
	 * }
	 * }    </code></pre>
	 * </TD>
	 * </TR>
	 * </TABLE>
	 * 
	 * @param r - instance of interface Runnable to be called
	 */
	void callSerially( Runnable r);

	/**
	 * Requests a flashing effect for the device's backlight.  The flashing
	 * effect is intended to be used to attract the user's attention or as a
	 * special effect for games.  Examples of flashing are cycling the
	 * backlight on and off or from dim to bright repeatedly.
	 * The return value indicates if the flashing of the backlight
	 * can be controlled by the application.
	 * 
	 * <p>The flashing effect occurs for the requested duration, or it is
	 * switched off if the requested duration is zero.  This method returns
	 * immediately; that is, it must not block the caller while the flashing
	 * effect is running.</p>
	 * 
	 * <p>Calls to this method are honored only if the
	 * <code>Display</code> is in the
	 * foreground.  This method MUST perform no action
	 * and return <CODE>false</CODE> if the
	 * <code>Display</code> is in the background.
	 * 
	 * <p>The device MAY limit or override the duration. For devices
	 * that do not include a controllable backlight, calls to this
	 * method return <CODE>false</CODE>.
	 * 
	 * @param duration - the number of milliseconds the backlight should be flashed, or zero if the flashing should be stopped
	 * @return true if the backlight can be controlled by the application and this display is in the foreground, false otherwise
	 * @throws IllegalArgumentException - if duration is negative
	 * @since  MIDP 2.0
	 */
	boolean flashBacklight(int duration);

	/**
	 * Requests operation of the device's vibrator.  The vibrator is
	 * intended to be used to attract the user's attention or as a
	 * special effect for games.  The return value indicates if the
	 * vibrator can be controlled by the application.
	 * 
	 * <p>This method switches on the vibrator for the requested
	 * duration, or switches it off if the requested duration is zero.
	 * If this method is called while the vibrator is still activated
	 * from a previous call, the request is interpreted as setting a
	 * new duration. It is not interpreted as adding additional time
	 * to the original request. This method returns immediately; that
	 * is, it must not block the caller while the vibrator is
	 * running. </p>
	 * 
	 * <p>Calls to this method are honored only if the
	 * <code>Display</code> is in the foreground.  This method MUST
	 * perform no action and return <CODE>false</CODE> if the
	 * <code>Display</code> is in the background.</p>
	 * 
	 * <p>The device MAY limit or override the duration.  For devices
	 * that do not include a controllable vibrator, calls to this
	 * method return <CODE>false</CODE>.</p>
	 * 
	 * @param duration - the number of milliseconds the vibrator should be run, or zero if the vibrator should be turned off
	 * @return true if the vibrator can be controlled by the application and this display is in the foreground, false otherwise
	 * @throws IllegalArgumentException - if duration is negative
	 * @since  MIDP 2.0
	 */
	boolean vibrate(int duration);

	/**
	 * Returns the best image width for a given image type.
	 * The image type must be one of
	 * <A HREF="../../../javax/microedition/lcdui/Display.html#LIST_ELEMENT"><CODE>LIST_ELEMENT</CODE></A>,
	 * <A HREF="../../../javax/microedition/lcdui/Display.html#CHOICE_GROUP_ELEMENT"><CODE>CHOICE_GROUP_ELEMENT</CODE></A>, or
	 * <A HREF="../../../javax/microedition/lcdui/Display.html#ALERT"><CODE>ALERT</CODE></A>.
	 * 
	 * @param imageType - the image type
	 * @return the best image width for the image type, may be zero if there is no best size; must not be negative
	 * @throws IllegalArgumentException - if imageType is illegal
	 * @since  MIDP 2.0
	 */
	int getBestImageWidth(int imageType);

	/**
	 * Returns the best image height for a given image type.
	 * The image type must be one of
	 * <A HREF="../../../javax/microedition/lcdui/Display.html#LIST_ELEMENT"><CODE>LIST_ELEMENT</CODE></A>,
	 * <A HREF="../../../javax/microedition/lcdui/Display.html#CHOICE_GROUP_ELEMENT"><CODE>CHOICE_GROUP_ELEMENT</CODE></A>, or
	 * <A HREF="../../../javax/microedition/lcdui/Display.html#ALERT"><CODE>ALERT</CODE></A>.
	 * 
	 * @param imageType - the image type
	 * @return the best image height for the image type, may be zero if there is no best size; must not be negative
	 * @throws IllegalArgumentException - if imageType is illegal
	 * @since  MIDP 2.0
	 */
	int getBestImageHeight(int imageType);

	/**
	 * Informs the native implementation about a change of displays
	 * @param currentDisplayable the current displayed screen
	 * @param nextDisplayable the next displayed screen
	 * @return true when processing should be stopped
	 */
	boolean notifyDisplayableChange(Displayable currentDisplayable, Displayable nextDisplayable);

}
