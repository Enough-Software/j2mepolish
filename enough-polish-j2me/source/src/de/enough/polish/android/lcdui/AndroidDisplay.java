//#condition polish.usePolishGui && polish.android
package de.enough.polish.android.lcdui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import de.enough.polish.android.midlet.MIDlet;
import de.enough.polish.android.midlet.MidletBridge;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.AlertType;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.NativeDisplay;
import de.enough.polish.ui.Screen;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.IdentityArrayList;

/**
 * <code>Display</code> represents the manager of the display and input
 * devices of the system. It includes methods for retrieving properties of the
 * device and for requesting that objects be displayed on the device. Other
 * methods that deal with device attributes are primarily used with <A
 * HREF="Canvas.html"><CODE>Canvas</CODE></A>
 * objects and are thus defined there instead of here.
 * <p>
 * 
 * There is exactly one instance of Display per <A
 * HREF="../midlet/MIDlet.html"><CODE>MIDlet</CODE></A>
 * and the application can get a reference to that instance by calling the <A
 * HREF="Display.html#getDisplay(javax.microedition.midlet.MIDlet)"><CODE>getDisplay()</CODE></A>
 * method. The application may call the <code>getDisplay()</code> method at
 * any time during course of its execution. The <code>Display</code> object
 * returned by all calls to <code>getDisplay()</code> will remain the same
 * during this time.
 * <p>
 * 
 * A typical application will perform the following actions in response to calls
 * to its <code>MIDlet</code> methods:
 * <UL>
 * <LI><STRONG>startApp</STRONG> - the application is moving from the paused
 * state to the active state. Initialization of objects needed while the
 * application is active should be done. The application may call <A
 * HREF="Display.html#setCurrent(javax.microedition.lcdui.Displayable)"><CODE>setCurrent()</CODE></A>
 * for the first screen if that has not already been done. Note that
 * <code>startApp()</code> can be called several times if
 * <code>pauseApp()</code> has been called in between. This means that
 * one-time initialization should not take place here but instead should occur
 * within the <code>MIDlet's</code> constructor. </LI>
 * <LI><STRONG>pauseApp</STRONG> - the application may pause its threads.
 * Also, if it is desirable to start with another screen when the application is
 * re-activated, the new screen should be set with <code>setCurrent()</code>.</LI>
 * <LI><STRONG>destroyApp</STRONG> - the application should free resources,
 * terminate threads, etc. The behavior of method calls on user interface
 * objects after <code>destroyApp()</code> has returned is undefined. </li>
 * </UL>
 * <p>
 * 
 * <P>
 * The user interface objects that are shown on the display device are contained
 * within a <A HREF="Displayable.html"><CODE>Displayable</CODE></A>
 * object. At any time the application may have at most one
 * <code>Displayable</code> object that it intends to be shown on the display
 * device and through which user interaction occurs. This
 * <code>Displayable</code> is referred to as the <em>current</em>
 * <code>Displayable</code>.
 * </p>
 * 
 * <P>
 * The <code>Display</code> class has a <A
 * HREF="Display.html#setCurrent(javax.microedition.lcdui.Displayable)"><CODE>setCurrent()</CODE></A>
 * method for setting the current <code>Displayable</code> and a <A
 * HREF="Display.html#getCurrent()"><CODE>getCurrent()</CODE></A>
 * method for retrieving the current <code>Displayable</code>. The
 * application has control over its current <code>Displayable</code> and may
 * call <code>setCurrent()</code> at any time. Typically, the application will
 * change the current <code>Displayable</code> in response to some user
 * action. This is not always the case, however. Another thread may change the
 * current <code>Displayable</code> in response to some other stimulus. The
 * current <code>Displayable</code> will also be changed when the timer for an
 * <A HREF="Alert.html"><CODE>Alert</CODE></A>
 * elapses.
 * </P>
 * 
 * <p>
 * The application's current <code>Displayable</code> may not physically be
 * drawn on the screen, nor will user events (such as keystrokes) that occur
 * necessarily be directed to the current <code>Displayable</code>. This may
 * occur because of the presence of other <code>MIDlet</code> applications
 * running simultaneously on the same device.
 * </p>
 * 
 * <P>
 * An application is said to be in the <em>foreground</em> if its current
 * <code>Displayable</code> is actually visible on the display device and if
 * user input device events will be delivered to it. If the application is not
 * in the foreground, it lacks access to both the display and input devices, and
 * it is said to be in the <em>background</em>. The policy for allocation of
 * these devices to different <code>MIDlet</code> applications is outside the
 * scope of this specification and is under the control of an external agent
 * referred to as the <em>application management software</em>.
 * </p>
 * 
 * <P>
 * As mentioned above, the application still has a notion of its current
 * <code>Displayable</code> even if it is in the background. The current
 * <code>Displayable</code> is significant, even for background applications,
 * because the current <code>Displayable</code> is always the one that will be
 * shown the next time the application is brought into the foreground. The
 * application can determine whether a <code>Displayable</code> is actually
 * visible on the display by calling <A
 * HREF="Displayable.html#isShown()"><CODE>isShown()</CODE></A>.
 * In the case of <code>Canvas</code>, the <A
 * HREF="Canvas.html#showNotify()"><CODE>showNotify()</CODE></A>
 * and <A HREF="Canvas.html#hideNotify()"><CODE>hideNotify()</CODE></A>
 * methods are called when the <code>Canvas</code> is made visible and is
 * hidden, respectively.
 * </P>
 * 
 * <P>
 * Each <code>MIDlet</code> application has its own current
 * <code>Displayable</code>. This means that the <A
 * HREF="Display.html#getCurrent()"><CODE>getCurrent()</CODE></A>
 * method returns the <code>MIDlet's</code> current <code>Displayable</code>,
 * regardless of the <code>MIDlet's</code> foreground/background state. For
 * example, suppose a <code>MIDlet</code> running in the foreground has
 * current <code>Displayable</code> <em>F</em>, and a <code>MIDlet</code>
 * running in the background has current <code>Displayable</code> <em>B</em>.
 * When the foreground <code>MIDlet</code> calls <code>getCurrent()</code>,
 * it will return <em>F</em>, and when the background <code>MIDlet</code>
 * calls <code>getCurrent()</code>, it will return <em>B</em>.
 * Furthermore, if either <code>MIDlet</code> changes its current
 * <code>Displayable</code> by calling <code>setCurrent()</code>, this will
 * not affect the any other <code>MIDlet's</code> current
 * <code>Displayable</code>.
 * </p>
 * 
 * <P>
 * It is possible for <code>getCurrent()</code> to return <code>null</code>.
 * This may occur at startup time, before the <code>MIDlet</code> application
 * has called <code>setCurrent()</code> on its first screen. The
 * <code>getCurrent(</code>) method will never return a reference to a
 * <code>Displayable</code> object that was not passed in a prior call to
 * <code>setCurrent()</code> call by this <code>MIDlet</code>.
 * </p>
 * 
 * <a name="systemscreens"></a>
 * <h3>System Screens</h3>
 * 
 * <P>
 * Typically, the current screen of the foreground <code>MIDlet</code> will be
 * visible on the display. However, under certain circumstances, the system may
 * create a screen that temporarily obscures the application's current screen.
 * These screens are referred to as <em>system screens.</em> This may occur if
 * the system needs to show a menu of commands or if the system requires the
 * user to edit text on a separate screen instead of within a text field inside
 * a <code>Form</code>. Even though the system screen obscures the
 * application's screen, the notion of the current screen does not change. In
 * particular, while a system screen is visible, a call to
 * <code>getCurrent()</code> will return the application's current screen, not
 * the system screen. The value returned by <code>isShown()</code> is
 * <code>false</code> while the current <code>Displayable</code> is obscured
 * by a system screen.
 * </p>
 * 
 * <p>
 * If system screen obscures a canvas, its <code>hideNotify()</code> method is
 * called. When the system screen is removed, restoring the canvas, its
 * <code>showNotify()</code> method and then its <code>paint()</code> method
 * are called. If the system screen was used by the user to issue a command, the
 * <code>commandAction()</code> method is called after
 * <code>showNotify()</code> is called.
 * </p>
 * 
 * <p>
 * This class contains methods to retrieve the prevailing foreground and
 * background colors of the high-level user interface. These methods are useful
 * for creating <CODE>CustomItem</CODE> objects that match the user interface
 * of other items and for creating user interfaces within <CODE>Canvas</CODE>
 * that match the user interface of the rest of the system. Implementations are
 * not restricted to using foreground and background colors in their user
 * interfaces (for example, they might use highlight and shadow colors for a
 * beveling effect) but the colors returned are those that match reasonably well
 * with the implementation's color scheme. An application implementing a custom
 * item should use the background color to clear its region and then paint text
 * and geometric graphics (lines, arcs, rectangles) in the foreground color.
 * </p>
 * <HR>
 * 
 * 
 * @since MIDP 1.0
 */
public class AndroidDisplay 
extends ViewGroup 
implements NativeDisplay //, OnTouchListener
{
	
	//#if polish.useFullScreen
		//#define tmp.fullScreen
	//#endif

	private static final KeyEvent delKeyDownEvent = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DEL);
	private static final KeyEvent delKeyUpEvent = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_DEL);
	
	private static AndroidDisplay instance;

	// This is just a fallback
	Bitmap bitmap = Bitmap.createBitmap(320,455,Bitmap.Config.ARGB_8888);
	
	/** This instance may be null*/
	private de.enough.polish.android.lcdui.Canvas currentPolishCanvas;
	// following variables are implicitly defined by getter- or setter-methods:
	private ArrayList<Runnable> seriallyRunnables = new ArrayList<Runnable>();
	DisplayUtil util;

	private View mainView;

//	@Override
//	protected void onRestoreInstanceState(Parcelable state) {
//		//#debug
//		System.out.println("onRestoreInstanceState");
//		super.onRestoreInstanceState(state);
//	}
//
//	@Override
//	protected Parcelable onSaveInstanceState() {
//		//#debug
//		System.out.println("onSaveInstanceState");
//		return super.onSaveInstanceState();
//	}

	/**
	 * Creates a view with the given context
	 * @param context the context
	 */
	public AndroidDisplay(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		
	}
	
	
	
	/////////////////////

	/**
	 * Image type for <code>List</code> element image.
	 * 
	 * <P>
	 * The value of <code>LIST_ELEMENT</code> is <code>1</code>.
	 * </P>
	 * <DT><B>See Also: </B> <A
	 * HREF="Display.html#getBestImageHeight(int)"><CODE>getBestImageHeight(int
	 * imageType)</CODE></A>
	 * 
	 * @since MIDP 2.0
	 */
	public static final int LIST_ELEMENT = 1;

	/**
	 * Image type for <code>ChoiceGroup</code> element image.
	 * 
	 * <P>
	 * The value of <code>CHOICE_GROUP_ELEMENT</code> is <code>2</code>.
	 * </P>
	 * <DT><B>See Also: </B> <A
	 * HREF="Display.html#getBestImageHeight(int)"><CODE>getBestImageHeight(int
	 * imageType)</CODE></A>
	 * 
	 * @since MIDP 2.0
	 */
	public static final int CHOICE_GROUP_ELEMENT = 2;

	/**
	 * Image type for <code>Alert</code> image.
	 * 
	 * <P>
	 * The value of <code>ALERT</code> is <code>3</code>.
	 * </P>
	 * <DT><B>See Also: </B> <A
	 * HREF="Display.html#getBestImageHeight(int)"><CODE>getBestImageHeight(int
	 * imageType)</CODE></A>
	 * 
	 * @since MIDP 2.0
	 */
	public static final int ALERT = 3;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_BACKGROUND</code> specifies the background color of the
	 * screen. The background color will always contrast with the foreground
	 * color.
	 * 
	 * <p>
	 * <code>COLOR_BACKGROUND</code> has the value <code>0</code>.
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	public static final int COLOR_BACKGROUND = 0;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_FOREGROUND</code> specifies the foreground color, for text
	 * characters and simple graphics on the screen. Static text or
	 * user-editable text should be drawn with the foreground color. The
	 * foreground color will always constrast with background color.
	 * 
	 * <p>
	 * <code>COLOR_FOREGROUND</code> has the value <code>1</code>.
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	public static final int COLOR_FOREGROUND = 1;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_HIGHLIGHTED_BACKGROUND</code> identifies the color for the
	 * focus, or focus highlight, when it is drawn as a filled in rectangle. The
	 * highlighted background will always constrast with the highlighted
	 * foreground.
	 * 
	 * <p>
	 * <code>COLOR_HIGHLIGHTED_BACKGROUND</code> has the value <code>2</code>.
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	public static final int COLOR_HIGHLIGHTED_BACKGROUND = 2;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_HIGHLIGHTED_FOREGROUND</code> identifies the color for text
	 * characters and simple graphics when they are highlighted. Highlighted
	 * foreground is the color to be used to draw the highlighted text and
	 * graphics against the highlighted background. The highlighted foreground
	 * will always constrast with the highlighted background.
	 * 
	 * <p>
	 * <code>COLOR_HIGHLIGHTED_FOREGROUND</code> has the value <code>3</code>.
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	public static final int COLOR_HIGHLIGHTED_FOREGROUND = 3;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_BORDER</code> identifies the color for boxes and borders
	 * when the object is to be drawn in a non-highlighted state. The border
	 * color is intended to be used with the background color and will contrast
	 * with it. The application should draw its borders using the stroke style
	 * returned by <code>getBorderStyle()</code>.
	 * 
	 * <p>
	 * <code>COLOR_BORDER</code> has the value <code>4</code>.
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	public static final int COLOR_BORDER = 4;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_HIGHLIGHTED_BORDER</code> identifies the color for boxes
	 * and borders when the object is to be drawn in a highlighted state. The
	 * highlighted border color is intended to be used with the background color
	 * (not the highlighted background color) and will contrast with it. The
	 * application should draw its borders using the stroke style returned
	 * <code>by getBorderStyle()</code>.
	 * 
	 * <p>
	 * <code>COLOR_HIGHLIGHTED_BORDER</code> has the value <code>5</code>.
	 * 
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	public static final int COLOR_HIGHLIGHTED_BORDER = 5;

	// private Item currentItem;

	/**
	 * Gets the <code>Display</code> object that is unique to this
	 * <code>MIDlet</code>.
	 * 
	 * @param m -
	 *            MIDlet of the application
	 * @return the display object that application can use for its user
	 *         interface
	 * @throws NullPointerException -
	 *             if m is null
	 */
	public static AndroidDisplay getDisplay(MIDlet m) {
		if(instance != null) {
			return instance;
		}
		if(m == null) {
			//throw new NullPointerException("The display is requested without providing a MIDlet reference.");
			instance = new AndroidDisplay(null);
		} else {
			instance = new AndroidDisplay(m._getMidletBridge());
		}
		return instance;
	}
	
	public static AndroidDisplay getInstance() {
		return instance;
	}

	/**
	 * Returns one of the colors from the high level user interface color
	 * scheme, in the form <code>0x00RRGGBB</code> based on the
	 * <code>colorSpecifier</code> passed in.
	 * 
	 * @param colorSpecifier -
	 *            the predefined color specifier; must be one of
	 *            COLOR_BACKGROUND, COLOR_FOREGROUND,
	 *            COLOR_HIGHLIGHTED_BACKGROUND, COLOR_HIGHLIGHTED_FOREGROUND,
	 *            COLOR_BORDER, or COLOR_HIGHLIGHTED_BORDER
	 * @return color in the form of 0x00RRGGBB
	 * @throws IllegalArgumentException -
	 *             if colorSpecifier is not a valid color specifier
	 * @since MIDP 2.0
	 */
	public int getColor(int colorSpecifier) {
		return 0;
		// TODO implement getColor
	}

	/**
	 * Returns the stroke style used for border drawing depending on the state
	 * of the component (highlighted/non-highlighted). For example, on a
	 * monochrome system, the border around a non-highlighted item might be
	 * drawn with a <code>DOTTED</code> stroke style while the border around a
	 * highlighted item might be drawn with a <code>SOLID</code> stroke style.
	 * 
	 * @param highlighted -
	 *            true if the border style being requested is for the
	 *            highlighted state, false if the border style being requested
	 *            is for the non-highlighted state
	 * @return Graphics.DOTTED or Graphics.SOLID
	 * @since MIDP 2.0
	 */
	public int getBorderStyle(boolean highlighted) {
		return 0;
		// TODO implement getBorderStyle
	}

	/**
	 * Gets information about color support of the device.
	 * 
	 * @return true if the display supports color, false otherwise
	 */
	public boolean isColor() {
		return true;
	}

	/**
	 * Gets the number of colors (if <code>isColor()</code> is
	 * <code>true</code>) or graylevels (if <code>isColor()</code> is
	 * <code>false</code>) that can be represented on the device.
	 * <P>
	 * Note that the number of colors for a black and white display is
	 * <code>2</code>.
	 * 
	 * @return number of colors
	 */
	public int numColors() {
		return 65536;
	}

	/**
	 * Gets the number of alpha transparency levels supported by this
	 * implementation. The minimum legal return value is <code>2</code>,
	 * which indicates support for full transparency and full opacity and no
	 * blending. Return values greater than <code>2</code> indicate that alpha
	 * blending is supported. For further information, see <a
	 * href="Image.html#alpha">Alpha Processing</a>.
	 * 
	 * @return number of alpha levels supported
	 * @since MIDP 2.0
	 */
	public int numAlphaLevels() {
		return 256;
	}

	/**
	 * Gets the current <code>Displayable</code> object for this
	 * <code>MIDlet</code>. The <code>Displayable</code> object returned
	 * may not actually be visible on the display if the <code>MIDlet</code>
	 * is running in the background, or if the <code>Displayable</code> is
	 * obscured by a system screen. The <A
	 * HREF="Displayable.html#isShown()"><CODE>Displayable.isShown()</CODE></A>
	 * method may be called to determine whether the <code>Displayable</code>
	 * is actually visible on the display.
	 * 
	 * <p>
	 * The value returned by <code>getCurrent()</code> may be
	 * <code>null</code>. This occurs after the application has been
	 * initialized but before the first call to <code>setCurrent()</code>.
	 * </p>
	 * 
	 * @return the MIDlet's current Displayable object
	 * @see #setCurrent(de.enough.polish.android.lcdui.Canvas)
	 */
	public de.enough.polish.android.lcdui.Canvas getCurrent() {
		return this.currentPolishCanvas;
	}

	/**
	 * Requests that a different <code>Displayable</code> object be made
	 * visible on the display. The change will typically not take effect
	 * immediately. It may be delayed so that it occurs between event delivery
	 * method calls, although it is not guaranteed to occur before the next
	 * event delivery method is called. The <code>setCurrent()</code> method
	 * returns immediately, without waiting for the change to take place.
	 * Because of this delay, a call to <code>getCurrent()</code> shortly
	 * after a call to <code>setCurrent()</code> is unlikely to return the
	 * value passed to <code>setCurrent()</code>.
	 * 
	 * <p>
	 * Calls to <code>setCurrent()</code> are not queued. A delayed request
	 * made by a <code>setCurrent()</code> call may be superseded by a
	 * subsequent call to <code>setCurrent()</code>. For example, if screen
	 * <code>S1</code> is current, then
	 * </p>
	 * 
	 * <TABLE BORDER="2">
	 * <TR>
	 * <TD ROWSPAN="1" COLSPAN="1">
	 * 
	 * <pre><code>
	 * d.setCurrent(S2);
	 * d.setCurrent(S3);
	 * </code></pre>
	 * 
	 * </TD>
	 * </TR>
	 * </TABLE>
	 * 
	 * <p>
	 * may eventually result in <code>S3</code> being made current, bypassing
	 * <code>S2</code> entirely.
	 * </p>
	 * 
	 * <p>
	 * When a <code>MIDlet</code> application is first started, there is no
	 * current <code>Displayable</code> object. It is the responsibility of
	 * the application to ensure that a <code>Displayable</code> is visible
	 * and can interact with the user at all times. Therefore, the application
	 * should always call <code>setCurrent()</code> as part of its
	 * initialization.
	 * </p>
	 * 
	 * <p>
	 * The application may pass <code>null</code> as the argument to
	 * <code>setCurrent()</code>. This does not have the effect of setting
	 * the current <code>Displayable</code> to <code>null</code>; instead,
	 * the current <code>Displayable</code> remains unchanged. However, the
	 * application management software may interpret this call as a request from
	 * the application that it is requesting to be placed into the background.
	 * Similarly, if the application is in the background, passing a non-null
	 * reference to <code>setCurrent()</code> may be interpreted by the
	 * application management software as a request that the application is
	 * requesting to be brought to the foreground. The request should be
	 * considered to be made even if the current <code>Displayable</code> is
	 * passed to the <code>setCurrent()</code>. For example, the code
	 * </p>
	 * <TABLE BORDER="2">
	 * <TR>
	 * <TD ROWSPAN="1" COLSPAN="1">
	 * 
	 * <pre><code>
	 * d.setCurrent(d.getCurrent());
	 * </code></pre>
	 * 
	 * </TD>
	 * </TR>
	 * </TABLE>
	 * <p>
	 * generally will have no effect other than requesting that the application
	 * be brought to the foreground. These are only requests, and there is no
	 * requirement that the application management software comply with these
	 * requests in a timely fashion if at all.
	 * </p>
	 * 
	 * <p>
	 * If the <code>Displayable</code> passed to <code>setCurrent()</code>
	 * is an <A HREF="Alert.html"><CODE>Alert</CODE></A>,
	 * the previously current <code>Displayable</code>, if any, is restored
	 * after the <code>Alert</code> has been dismissed. If there is a current
	 * <code>Displayable</code>, the effect is as if
	 * <code>setCurrent(Alert, getCurrent())</code> had been called. Note that
	 * this will result in an exception being thrown if the current
	 * <code>Displayable</code> is already an alert. If there is no current
	 * <code>Displayable</code> (which may occur at startup time) the
	 * implementation's previous state will be restored after the
	 * <code>Alert</code> has been dismissed. The automatic restoration of the
	 * previous <code>Displayable</code> or the previous state occurs only
	 * when the <code>Alert's</code> default listener is present on the
	 * <code>Alert</code> when it is dismissed. See <a
	 * href="Alert.html#commands">Alert Commands and Listeners</a> for details.
	 * </p>
	 * 
	 * <p>
	 * To specify the <code>Displayable</code> to be shown after an
	 * <code>Alert</code> is dismissed, the application should use the <A
	 * HREF="Display.html#setCurrent(javax.microedition.lcdui.Alert,
	 * javax.microedition.lcdui.Displayable)"><CODE>setCurrent(Alert,
	 * Displayable)</CODE></A> method. If the application calls
	 * <code>setCurrent()</code> while an <code>Alert</code> is current, the
	 * <code>Alert</code> is removed from the display and any timer it may
	 * have set is cancelled.
	 * </p>
	 * 
	 * <p>
	 * If the application calls <code>setCurrent()</code> while a system
	 * screen is active, the effect may be delayed until after the system screen
	 * is dismissed. The implementation may choose to interpret
	 * <code>setCurrent()</code> in such a situation as a request to cancel
	 * the effect of the system screen, regardless of whether
	 * <code>setCurrent()</code> has been delayed.
	 * </p>
	 * 
	 * @param nextDisplayable -
	 *            the Displayable requested to be made current; null is allowed
	 * @see #getCurrent()
	 */
//	public void setCurrent(de.enough.polish.android.lcdui.Canvas nextDisplayable) {
//		if (this.currentPolishCanvas != null) {
//			this.currentPolishCanvas._hideNotify();
//		}
//		//#debug
//		System.out.println("Setting currentCanvas to '"+nextDisplayable+"'");
//		this.currentPolishCanvas = nextDisplayable;
//		if(nextDisplayable != null) {
//			nextDisplayable.setBitmap(this.bitmap);
//			nextDisplayable._showNotify();
//		}
//		postInvalidate();
//	}
	
	public void setCurrent(de.enough.polish.ui.Display nextDisplayable) {
		setCurrent((Displayable)nextDisplayable);
	}
	

	public void setCurrent(Displayable nextDisplayable) {
		// #debug
		System.out.println("AndroidDisplay.setCurrent: "  + nextDisplayable);
		if (nextDisplayable instanceof Canvas) {
			Canvas androidCanvas = (Canvas)nextDisplayable;
			CanvasBridge bridge = androidCanvas._getBridge();
			if (bridge == null) {
				System.out.println("Creating a new bridge with context " + MidletBridge.getInstance());
				bridge = new CanvasBridge(MidletBridge.getInstance());
				bridge.setCanvas(androidCanvas);
			}
			if (this.mainView != null) {
				removeAllViews();
				if (this.mainView instanceof CanvasBridge) {
					((CanvasBridge)this.mainView).hideNotify();
				}
				removeView( this.mainView );
			}
			this.mainView = bridge;
			bridge.showNotify();
			addView( bridge, 0 );
			bridge.requestFocus();
			return;
		}		
	}

	/**
	 * Requests that this <code>Alert</code> be made current, and that
	 * <code>nextDisplayable</code> be made current after the
	 * <code>Alert</code> is dismissed. This call returns immediately
	 * regardless of the <code>Alert's</code> timeout value or whether it is a
	 * modal alert. The <code>nextDisplayable</code> must not be an
	 * <code>Alert</code>, and it must not be <code>null</code>.
	 * 
	 * <p>
	 * The automatic advance to <code>nextDisplayable</code> occurs only when
	 * the <code>Alert's</code> default listener is present on the
	 * <code>Alert</code> when it is dismissed. See <a
	 * href="Alert.html#commands">Alert Commands and Listeners</a> for details.
	 * </p>
	 * 
	 * <p>
	 * In other respects, this method behaves identically to <A
	 * HREF="Display.html#setCurrent(javax.microedition.lcdui.Displayable)"><CODE>setCurrent(Displayable)</CODE></A>.
	 * </p>
	 * 
	 * @param alert -
	 *            the alert to be shown
	 * @param nextDisplayable -
	 *            the Displayable to be shown after this alert is dismissed
	 * @throws NullPointerException -
	 *             if alert or nextDisplayable is null
	 * @throws IllegalArgumentException -
	 *             if nextDisplayable is an Alert
	 * @see Alert, getCurrent()
	 */
	/*
	 * public void setCurrent( Alert alert, Displayable nextDisplayable) {
	 * //TODO implement setCurrent }
	 */

	/**
	 * Requests that the <code>Displayable</code> that contains this
	 * <code>Item</code> be made current, scrolls the <code>Displayable</code>
	 * so that this <code>Item</code> is visible, and possibly assigns the
	 * focus to this <code>Item</code>. The containing
	 * <code>Displayable</code> is first made current as if <A
	 * HREF="Display.html#setCurrent(javax.microedition.lcdui.Displayable)"><CODE>setCurrent(Displayable)</CODE></A>
	 * had been called. When the containing <code>Displayable</code> becomes
	 * current, or if it is already current, it is scrolled if necessary so that
	 * the requested <code>Item</code> is made visible. Then, if the
	 * implementation supports the notion of input focus, and if the
	 * <code>Item</code> accepts the input focus, the input focus is assigned
	 * to the <code>Item</code>.
	 * 
	 * <p>
	 * This method always returns immediately, without waiting for the switching
	 * of the <code>Displayable</code>, the scrolling, and the assignment of
	 * input focus to take place.
	 * </p>
	 * 
	 * <p>
	 * It is an error for the <code>Item</code> not to be contained within a
	 * container. It is also an error if the <code>Item</code> is contained
	 * within an <code>Alert</code>.
	 * </p>
	 * 
	 * @param item -
	 *            the item that should be made visible
	 * @throws IllegalStateException -
	 *             if the item is not owned by a container
	 * @throws IllegalStateException -
	 *             if the item is owned by an Alert
	 * @throws NullPointerException -
	 *             if item is null
	 * @since MIDP 2.0
	 */
	/*
	 * public void setCurrentItem( Item item) { this.currentItem = item; }
	 */

	/**
	 * Causes the <code>Runnable</code> object <code>r</code> to have its
	 * <code>run()</code> method called later, serialized with the event
	 * stream, soon after completion of the repaint cycle. As noted in the <a
	 * href="./package-summary.html#events">Event Handling</a> section of the
	 * package summary, the methods that deliver event notifications to the
	 * application are all called serially. The call to <code>r.run()</code>
	 * will be serialized along with the event calls into the application. The
	 * <code>run()</code> method will be called exactly once for each call to
	 * <code>callSerially()</code>. Calls to <code>run()</code> will occur
	 * in the order in which they were requested by calls to
	 * <code>callSerially()</code>.
	 * 
	 * <p>
	 * If the current <code>Displayable</code> is a <code>Canvas</code> that
	 * has a repaint pending at the time of a call to
	 * <code>callSerially()</code>, the <code>paint()</code> method of the
	 * <code>Canvas</code> will be called and will return, and a buffer switch
	 * will occur (if double buffering is in effect), before the
	 * <code>run()</code> method of the <code>Runnable</code> is called. If
	 * the current <code>Displayable</code> contains one or more
	 * <code>CustomItems</code> that have repaints pending at the time of a
	 * call to <code>callSerially()</code>, the <code>paint()</code>
	 * methods of the <code>CustomItems</code> will be called and will return
	 * before the <code>run()</code> method of the <code>Runnable</code> is
	 * called. Calls to the <code>run()</code> method will occur in a timely
	 * fashion, but they are not guaranteed to occur immediately after the
	 * repaint cycle finishes, or even before the next event is delivered.
	 * </p>
	 * 
	 * <p>
	 * The <code>callSerially()</code> method may be called from any thread.
	 * The call to the <code>run()</code> method will occur independently of
	 * the call to <code>callSerially()</code>. In particular,
	 * <code>callSerially()</code> will <em>never</em> block waiting for
	 * <code>r.run()</code> to return.
	 * </p>
	 * 
	 * <p>
	 * As with other callbacks, the call to <code>r.run()</code> must return
	 * quickly. If it is necessary to perform a long-running operation, it may
	 * be initiated from within the <code>run()</code> method. The operation
	 * itself should be performed within another thread, allowing
	 * <code>run()</code> to return.
	 * </p>
	 * 
	 * <p>
	 * The <code>callSerially()</code> facility may be used by applications to
	 * run an animation that is properly synchronized with the repaint cycle. A
	 * typical application will set up a frame to be displayed and then call
	 * <code>repaint()</code>. The application must then wait until the frame
	 * is actually displayed, after which the setup for the next frame may
	 * occur. The call to <code>run()</code> notifies the application that the
	 * previous frame has finished painting. The example below shows
	 * <code>callSerially()</code> being used for this purpose.
	 * </p>
	 * <TABLE BORDER="2">
	 * <TR>
	 * <TD ROWSPAN="1" COLSPAN="1">
	 * 
	 * <pre><code>
	 *  class Animation extends Canvas
	 *  implements Runnable {
	 *  
	 *  // paint the current frame
	 *  void paint(Graphics g) { ... }
	 *  
	 *  Display display; // the display for the application
	 *  
	 *  void paint(Graphics g) { ... } // paint the current frame
	 *  
	 *  void startAnimation() {
	 *  // set up initial frame
	 *  repaint();
	 *  display.callSerially(this);
	 *  }
	 *  
	 *  // called after previous repaint is finished
	 *  void run() {
	 *  if (  /* there are more frames * / ) {
	 *  // set up the next frame
	 *  repaint();
	 *  display.callSerially(this);
	 *  }
	 *  }
	 *  }
	 * </code></pre>
	 * 
	 * </TD>
	 * </TR>
	 * </TABLE>
	 * 
	 * @param r -
	 *            instance of interface Runnable to be called
	 */
	public void callSerially(Runnable r) {
		this.seriallyRunnables.add(r);
	}
	

	public void callSeriallies() {
		if (this.seriallyRunnables.size() > 0) {
			Runnable[] runnables = this.seriallyRunnables.toArray(new Runnable[ this.seriallyRunnables.size()]);
			this.seriallyRunnables.clear();
			for (int i = 0; i < runnables.length; i++) {
				Runnable runnable = runnables[i];
				runnable.run();
			}
		}
	}
	

	/**
	 * Requests a flashing effect for the device's backlight. The flashing
	 * effect is intended to be used to attract the user's attention or as a
	 * special effect for games. Examples of flashing are cycling the backlight
	 * on and off or from dim to bright repeatedly. The return value indicates
	 * if the flashing of the backlight can be controlled by the application.
	 * 
	 * <p>
	 * The flashing effect occurs for the requested duration, or it is switched
	 * off if the requested duration is zero. This method returns immediately;
	 * that is, it must not block the caller while the flashing effect is
	 * running.
	 * </p>
	 * 
	 * <p>
	 * Calls to this method are honored only if the <code>Display</code> is in
	 * the foreground. This method MUST perform no action and return <CODE>false</CODE>
	 * if the <code>Display</code> is in the background.
	 * 
	 * <p>
	 * The device MAY limit or override the duration. For devices that do not
	 * include a controllable backlight, calls to this method return <CODE>false</CODE>.
	 * 
	 * @param duration -
	 *            the number of milliseconds the backlight should be flashed, or
	 *            zero if the flashing should be stopped
	 * @return true if the backlight can be controlled by the application and
	 *         this display is in the foreground, false otherwise
	 * @throws IllegalArgumentException -
	 *             if duration is negative
	 * @since MIDP 2.0
	 */
	public boolean flashBacklight(int duration) {
		return false;
		// TODO implement flashBacklight
	}

	/**
	 * Requests operation of the device's vibrator. The vibrator is intended to
	 * be used to attract the user's attention or as a special effect for games.
	 * The return value indicates if the vibrator can be controlled by the
	 * application.
	 * 
	 * <p>
	 * This method switches on the vibrator for the requested duration, or
	 * switches it off if the requested duration is zero. If this method is
	 * called while the vibrator is still activated from a previous call, the
	 * request is interpreted as setting a new duration. It is not interpreted
	 * as adding additional time to the original request. This method returns
	 * immediately; that is, it must not block the caller while the vibrator is
	 * running.
	 * </p>
	 * 
	 * <p>
	 * Calls to this method are honored only if the <code>Display</code> is in
	 * the foreground. This method MUST perform no action and return <CODE>false</CODE>
	 * if the <code>Display</code> is in the background.
	 * </p>
	 * 
	 * <p>
	 * The device MAY limit or override the duration. For devices that do not
	 * include a controllable vibrator, calls to this method return <CODE>false</CODE>.
	 * </p>
	 * 
	 * @param duration -
	 *            the number of milliseconds the vibrator should be run, or zero
	 *            if the vibrator should be turned off
	 * @return true if the vibrator can be controlled by the application and
	 *         this display is in the foreground, false otherwise
	 * @throws IllegalArgumentException -
	 *             if duration is negative
	 * @since MIDP 2.0
	 */
	public boolean vibrate(int duration) {
		return false;
		// TODO implement vibrate
	}

	/**
	 * Returns the best image width for a given image type. The image type must
	 * be one of <A
	 * HREF="Display.html#LIST_ELEMENT"><CODE>LIST_ELEMENT</CODE></A>,
	 * <A
	 * HREF="Display.html#CHOICE_GROUP_ELEMENT"><CODE>CHOICE_GROUP_ELEMENT</CODE></A>,
	 * or <A HREF="Display.html#ALERT"><CODE>ALERT</CODE></A>.
	 * 
	 * @param imageType -
	 *            the image type
	 * @return the best image width for the image type, may be zero if there is
	 *         no best size; must not be negative
	 * @throws IllegalArgumentException -
	 *             if imageType is illegal
	 * @since MIDP 2.0
	 */
	public int getBestImageWidth(int imageType) {
		return 0;
		// TODO implement getBestImageWidth
	}

	/**
	 * Returns the best image height for a given image type. The image type must
	 * be one of <A
	 * HREF="Display.html#LIST_ELEMENT"><CODE>LIST_ELEMENT</CODE></A>,
	 * <A
	 * HREF="Display.html#CHOICE_GROUP_ELEMENT"><CODE>CHOICE_GROUP_ELEMENT</CODE></A>,
	 * or <A HREF="Display.html#ALERT"><CODE>ALERT</CODE></A>.
	 * 
	 * @param imageType -
	 *            the image type
	 * @return the best image height for the image type, may be zero if there is
	 *         no best size; must not be negative
	 * @throws IllegalArgumentException -
	 *             if imageType is illegal
	 * @since MIDP 2.0
	 */
	public int getBestImageHeight(int imageType) {
		return 0;
		// TODO implement getBestImageHeight
	}

	public boolean notifyDisplayableChange(
			de.enough.polish.ui.Displayable currentDisp,
			de.enough.polish.ui.Displayable nextDisp) 
	{
		//#if polish.useNativeAlerts
			if (nextDisp instanceof Alert) {
				final Alert alert = (Alert) nextDisp;
				alert.setNextDisplayable(currentDisp); 
				final AlertDialog.Builder builder = new AlertDialog.Builder(MidletBridge.getInstance());
				builder.setTitle(alert.getTitle());
				if (alert.getTitle() != alert.getString()) {
					builder.setMessage(alert.getString());
				}
				if (alert.getType() == AlertType.INFO || alert.getType() == AlertType.CONFIRMATION) {
					builder.setIcon(android.R.drawable.ic_dialog_info);
				} else {
					builder.setIcon(android.R.drawable.ic_dialog_alert);					
				}
				IdentityArrayList commandsList = alert._commands;
				if (commandsList != null) {
					int lastIndex = Math.min(3, commandsList.size()) - 1;
					boolean positiveHandled = false;
					boolean negativeHandled = false;
					boolean neutralHandled = false;
					for (int i=0; i <= lastIndex; i++) {
						Command command = (Command) commandsList.get(i);
						int type = command.getCommandType();
						if (type == Command.OK || type == Command.SCREEN || type == Command.ITEM) {
							if (!positiveHandled) {
								positiveHandled = true;
								builder.setPositiveButton(command.getLabel(), new CommandClickHandler(alert, command));
							} else if (!neutralHandled) {
								neutralHandled = true;
								builder.setNeutralButton(command.getLabel(), new CommandClickHandler(alert, command));
							}
						} else {
							if (!negativeHandled) {
								negativeHandled = true;
								builder.setNegativeButton(command.getLabel(), new CommandClickHandler(alert, command));
							} else if (!neutralHandled) {
								neutralHandled = true;
								builder.setNeutralButton(command.getLabel(), new CommandClickHandler(alert, command));
							}
						}
					}
					//TODO handle cases when there are more than 3 commands or more than 2 positive/negative commands
				}

//				public void onClick(DialogInterface dialog, int which) {
//				System.out.println("Neutral Button Clicked");
//				AlertDialog ad=builder.create();
//				ad.cancel();
//				}

//				builder.setOnCancelListener(new OnCancelListener() {
//					public void onCancel(DialogInterface dialog) {
//						System.out.println(" the cancel listner invoked");
//					}
//				});
				MidletBridge.getInstance().runOnUiThread( new Runnable() {
					public void run() {
						builder.show();
					}
				});
				return true;
			}
		//#endif
		return false;
	}

//	public void setCurrent(de.enough.polish.ui.Display nextDisplayable) {
//		//#= setCurrent((de.enough.polish.android.lcdui.Canvas)nextDisplayable);
//	}

//	public static AndroidDisplay getInstance(MIDlet dlet) {
//		if(instance == null) {
//			instance = new AndroidDisplay(dlet);
//		}
//		return instance;
//	}

//	public boolean onTouch(View arg0, MotionEvent event) {
//		int action = event.getAction();
//		float x = event.getX();
//		float y = event.getY();
//		//#debug
//		System.out.println("onTouchEvent: action="+action + ", x=" + x + ", y=" + y);
//		switch(action) {
//			case MotionEvent.ACTION_DOWN:
//				onPointerPressed(x,y);
//				return true;
//			case MotionEvent.ACTION_UP:
//				onPointerReleased(x,y);
//				return true;
//			case MotionEvent.ACTION_MOVE:
//				onPointerDragged(x,y);
//				return true;
//			default: return false;
//		}
//	}

	public void shutdown() {
		if(this.currentPolishCanvas != null) {
			this.currentPolishCanvas._hideNotify();
		}
		Display displayInstance = Display.getInstance();
		if(displayInstance != null) {
			displayInstance.shutdown();
		}
		instance = null;
	}

	public void refresh() {
		if(this.currentPolishCanvas != null) {
			setCurrent(this.currentPolishCanvas);
		}
	}
	
	public void onShow(AndroidItemView androidView) {
		addView( androidView.getAndroidView() );
	}
	
	public void onHide(AndroidItemView androidView) {
		removeView( androidView.getAndroidView() );
	}

	
//	//#if polish.javaplatform >= Android/1.5
//	@Override
//	public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
//		editorInfo.imeOptions |= EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_NONE;
//        //return new PolishInputConnection(this, false);
//		return new BaseInputConnection(this, false) {
//			private boolean isTextInputEnabled;
//
//			public boolean deleteSurroundingText(int leftLength, int rightLength) {
//				// Emulate the del key. This is needed for the Sony Ericsson Xperia X8 device which does not send the raw key event
//				// but calls this method to communicate a deletion. No other device behaves like this.
//				onKeyDown(KeyEvent.KEYCODE_DEL, delKeyDownEvent);
//				onKeyUp(KeyEvent.KEYCODE_DEL, delKeyUpEvent);
//				return super.deleteSurroundingText(leftLength, rightLength);
//			}
//		};
//	}
//	//#endif

	/*
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		System.out.println("AndroidDisplay.onLayout: " + left + ", " + top + ", " + right + ", " + bottom);
		this.positionLeft = left;
		this.positionRight = right;
		this.positionTop = top;
		this.positionBottom = bottom;
		int count = getChildCount();
		System.out.println("layout: child count=" + count);
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (i == 0) {
				child.layout(left, 0, right, bottom - top);
			} else if (child == this.editView){
				TextField textField = this.editView.getTextField();
				if (textField != null) {
					this.editView.measure( this.positionRight - this.positionLeft, this.positionBottom - this.positionTop );
					int absoluteX = textField.getAbsoluteX() + textField.getContentX();
					int absoluteY = textField.getAbsoluteY() + textField.getContentY();
					this.editView.layout( absoluteX, absoluteY, absoluteX + textField.getContentWidth(), absoluteY + textField.getContentHeight() );
				}
			} else {
				System.out.println("AndroidDisplay: layouting child " + child);
				int width = right-left;
				int height = bottom-top;
				LayoutParams params = child.getLayoutParams();
				params.width = 100;
				params.height = 100;
				child.setLayoutParams(params);
				child.measure(width, height);
				System.out.println("AndroidDisplay: After measure child: dimension=" + child.getMeasuredWidth() + "x" + child.getMeasuredHeight());
				child.layout(50, 50, 150, 150);
				//child.layout(0, 0, 150, 150);
				child.requestFocus();
				bringChildToFront(child);
			}
		}
	}
	*/

	
	
	//#if polish.useNativeAlerts
		private static class CommandClickHandler implements DialogInterface.OnClickListener {
			private Alert alert;
			private Command command;
			
			
			
			public CommandClickHandler(Alert alert, Command command) {
				this.alert = alert;
				this.command = command;
			}



			public void onClick(DialogInterface dialog, int which) {
				this.command.commandAction(null, this.alert);
			}
			
		}
	//#endif

	@Override
	public void dispatchDraw(android.graphics.Canvas canvas) {
        final int count = getChildCount();
        //int yOffset = 0;
        Rect clip = null;
        Screen screen = getCurrentPolishScreen();
		if (screen != null) {
			//yOffset = screen.getRootContainer().getScrollYOffset();
			int contX = screen.getScreenContentX();
			int contY = screen.getScreenContentY();
    		clip = new Rect( contX, contY, contX + screen.getScreenContentWidth(), contY + screen.getScreenContentHeight() );
		}
    	this.mainView.draw(canvas);
    	if (clip != null) {
        	canvas.save();
    		canvas.clipRect(clip, Op.REPLACE);
    	}
        for (int i = 1; i < count; i++) {
            View child = getChildAt(i);
            Item item = ((AndroidItemView) child).getPolishItem();
            int x = item.getAbsoluteX() + item.getContentX();
            int y = item.getAbsoluteY() + item.getContentY();
            if (y != child.getTop()) {
            	child.layout(x, y, x + child.getMeasuredWidth(), y + child.getMeasuredHeight());
            }
        	canvas.translate(x, y);
        	child.draw(canvas);
        	canvas.translate(-x, -y);
        }
		if (clip != null) {
			canvas.restore();
		}
	}

		
		
	 private Screen getCurrentPolishScreen() {
		Displayable displayable = Display.getInstance().getCurrent();
		if (displayable instanceof Screen) {
			return (Screen) displayable;
		}
		return null;
	}



		private int line_height;

		    public static class LayoutParams extends ViewGroup.LayoutParams {
		        public final int horizontal_spacing;
		        public final int vertical_spacing;

		        /**
		         * @param horizontal_spacing Pixels between items, horizontally
		         * @param vertical_spacing Pixels between items, vertically
		         */
		        public LayoutParams(int horizontal_spacing, int vertical_spacing) {
		            super(0, 0);
		            this.horizontal_spacing = horizontal_spacing;
		            this.vertical_spacing = vertical_spacing;     
		        }
		    }

		   
		    @Override
		    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		        int width = MeasureSpec.getSize(widthMeasureSpec);
		        int height = MeasureSpec.getSize(heightMeasureSpec);
		        this.mainView.measure(width, height);
		        // other views are measured by the J2ME Polish items
		        setMeasuredDimension(width, height);
//		        
//		        final int count = getChildCount();
//		        System.out.println("measuring " + count + " views");
//		        for (int i = 0; i < count; i++) {
//		            final View child = getChildAt(i);
//		            System.out.println("view " + i + "=" + child);
//		        }
//		        int line_height = 0;
//
//		        int xpos = getPaddingLeft();
//		        int ypos = getPaddingTop();
//		        // measure main view:
//		        getChildAt(0).measure(width, height);
//		        // measure native AndroidItemViews and the main view:
//		        for (int i = 1; 0 < count; i++) {
//		            final View child = getChildAt(i);
//		            AndroidItemView itemView = (AndroidItemView) child;
//		            Item item = itemView.getPolishItem();
//		            //if (child.getVisibility() != GONE) {
//		                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
//		                int availableChildWidth;
//		                if (item.getAvailableContentWidth() < item.itemWidth) {
//		                	availableChildWidth = item.getAvailableContentWidth();
//		                } else {
//		                	availableChildWidth = item.getContentWidth();
//		                }
//		                int availableChildHeight = item.getContentHeight();
//		                child.measure(
//		                        MeasureSpec.makeMeasureSpec(availableChildWidth, MeasureSpec.EXACTLY),
//		                        MeasureSpec.makeMeasureSpec(availableChildHeight, MeasureSpec.AT_MOST));
//
//		                final int childw = child.getMeasuredWidth();
//		                System.out.println("child-width=" + childw + " of " + child);
//		                line_height = Math.max(line_height, child.getMeasuredHeight() + lp.vertical_spacing);
//
//		                if (xpos + childw > width) {
//		                    xpos = getPaddingLeft();
//		                    ypos += line_height;
//		                }
//
//		                xpos += childw + lp.horizontal_spacing;
//		            //}
//		        }
//		        this.line_height = line_height;
//
//		        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED){
//		            height = ypos + line_height;
//
//		        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST){
//		            if (ypos + line_height < height){
//		                height = ypos + line_height;
//		            }
//		        }
		    }

		    @Override
		    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		        return new LayoutParams(1, 1); // default of 1px spacing
		    }

		    @Override
		    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		        if (p instanceof LayoutParams) {
		            return true;
		        }
		        return false;
		    }

		    @Override
		    protected void onLayout(boolean changed, int l, int t, int r, int b) {
		    	int xpos, ypos;
		        final int count = getChildCount();
		        for (int i = 0; i < count; i++) {
		            final View child = getChildAt(i);
		            if (child == this.mainView) {
		            	child.layout(0, 0, r-l, b-t);
		            } else {
			            AndroidItemView itemView = (AndroidItemView) child;
			            Item item = itemView.getPolishItem();
		                xpos = item.getAbsoluteX() + item.getContentX();
		                ypos = item.getAbsoluteY() + item.getContentY();
		                child.layout(xpos, ypos, xpos + child.getMeasuredWidth(), ypos + child.getMeasuredHeight());
		            }
		        }
		    }

		
//		    public void addView(View view) {
//		    	System.out.println("ADDING VIEW " + view);
//		    	super.addView(view);
//		    }
//		    
//		    
//		    public void addView(View view, int pos) {
//		    	System.out.println("ADDING VIEW " + view + " AT POS " + pos);
//		    	super.addView(view, pos);
//		    }
		
}


