//#condition polish.usePolishGui && polish.android && (!polish.android.MidletBridge.skip)
package de.enough.polish.android.midlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import de.enough.polish.android.helper.ResourcesHelper;
import de.enough.polish.android.io.ConnectionNotFoundException;
import de.enough.polish.android.lcdui.AndroidDisplay;
import de.enough.polish.android.lcdui.CanvasBridge;
import de.enough.polish.ui.AnimationThread;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.util.IdentityArrayList;


/**
 * A MIDlet is a MID Profile application.
 * 
 * A <code>MIDlet</code> is a MID Profile application. The application must
 * extend this class to allow the application management software to control the
 * MIDlet and to be able to retrieve properties from the application descriptor
 * and notify and request state changes. The methods of this class allow the
 * application management software to create, start, pause, and destroy a
 * MIDlet. A <code>MIDlet</code> is a set of classes designed to be run and
 * controlled by the application management software via this interface. The
 * states allow the application management software to manage the activities of
 * multiple <CODE>MIDlets</CODE> within a runtime environment. It can select
 * which <code>MIDlet</code>s are active at a given time by starting and
 * pausing them individually. The application management software maintains the
 * state of the <code>MIDlet</code> and invokes methods on the
 * <code>MIDlet</code> to notify the MIDlet of change states. The
 * <code>MIDlet</code> implements these methods to update its internal
 * activities and resource usage as directed by the application management
 * software. The <code>MIDlet</code> can initiate some state changes itself
 * and notifies the application management software of those state changes by
 * invoking the appropriate methods.
 * <p>
 * 
 * <strong>Note:</strong> The methods on this interface signal state changes.
 * The state change is not considered complete until the state change method has
 * returned. It is intended that these methods return quickly.
 * <p>
 * 
 * To trap the home button and prevent its action, you can use the variable
 * polish.android.traphomebutton in the build.xml.
 * <b>Warning: If your application is the default launcher application, you may have problems to switch back. Try to remove your application with adb remove
 * 
 */
public class MidletBridge extends Activity {
	
	//#if polish.useFullScreen
		//#define tmp.fullScreen
	//#else
		private final IdentityArrayList addedCommands = new IdentityArrayList();
		private final IdentityArrayList addedCommandMenuItemBridges = new IdentityArrayList();
		private boolean isMenuOpened;
	//#endif


	//The one and only MIDlet
	public static MidletBridge instance;
	
	//Tag for logging
	public static final String TAG = "Polish";
	
	// The view of the application
//	private AndroidDisplay display;

	private final HashMap<String,String> appProperties;
	private boolean isAppPropertiesLoaded;

	private ContentResolver contentResolver;

	private boolean shuttingDown;
	
//	private boolean isSoftkeyboardOpen;

	private int currentScreenYOffset;

	private boolean suicideOnExit =
		//#if polish.android.killProcessOnExit:defined
			//#= ${polish.android.killProcessOnExit}
		//#else
			true
		//#endif
	;
	private boolean isSoftKeyboardShown;
	private static MIDlet midlet;
	

//	private PowerManager.WakeLock wakeLock;

	/**
	 * Creates a new MIDlet Bridge
	 */
	public MidletBridge() {
		if (instance == null) {
			instance = this;
		}
		this.appProperties = new HashMap<String,String>();
		//#= this.appProperties.put("MIDlet-Name", "${MIDlet-Name}");
		//#= this.appProperties.put("MIDlet-Vendor", "${MIDlet-Vendor}");
		//#= this.appProperties.put("MIDlet-Version", "${MIDlet-Version}");
		//#= this.appProperties.put("microedition.pim.version", "1.0");
	}
	
	public void openOptionsMenu() {
		super.openOptionsMenu();
	}
	
	public void closeOptionsMenu() {
		super.closeOptionsMenu();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle icicle) {
		//#debug
		System.out.println("MidletBridge.onCreate(...) for " + this +  ", Process.pid: " + Process.myPid() + ", isTaskRoot=" + isTaskRoot());
		super.onCreate(icicle);
		if (!isTaskRoot()) {
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//#if polish.android.hideStatusBar
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//#endif
		// shrink the application on softkeyboard display:
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		CanvasBridge.DISPLAY_HEIGHT_PIXEL = metrics.heightPixels;
		CanvasBridge.DISPLAY_WIDTH_PIXEL = metrics.widthPixels;
		//System.out.println("METRICS: DESIRED=" + desiredWindowWidth + "x" + desiredWindowHeight + ", defaultDisplay=" + CanvasBridge.DISPLAY_WIDTH_PIXEL + "x" + CanvasBridge.DISPLAY_HEIGHT_PIXEL);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		setSystemProperty("Cell-Id","-1");
		setSystemProperty("Cell-lac","-1");
		setSystemProperty("SignalStrength","0");
		
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		PhoneStateListener listener = new PhoneStateListener() {
			@Override
			public void onCellLocationChanged(CellLocation location) {
				//#debug
				System.out.println("onCellLocationChanged");
				if(location instanceof GsmCellLocation) {
					GsmCellLocation gsmCellLocation = (GsmCellLocation)location;
					//#debug
					System.out.println("gsmCellLocation:"+gsmCellLocation);
					int cellId = gsmCellLocation.getCid();
					String cellIdString = Integer.toString(cellId);
					setSystemProperty("Cell-Id",cellIdString);

					int lac = gsmCellLocation.getLac();
					String lacString = Integer.toString(lac);
					setSystemProperty("Cell-lac",lacString);
				}
			}

			@Override
			public void onSignalStrengthChanged(int asu) {
				String asuString = Integer.toString(asu);
				//#debug
				System.out.println("SignalStrength (asu) is '"+asu+"'");
				setSystemProperty("SignalStrength",asuString);
			}
		};
		int events = PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTH;
		telephonyManager.listen(listener, events);
		String subscriberId = telephonyManager.getSubscriberId();
		if(subscriberId == null) {
			subscriberId = "";
		}
		setSystemProperty("IMSI", subscriberId);
		String deviceId = telephonyManager.getDeviceId();
		if(deviceId == null) {
			deviceId = "";
		}
		setSystemProperty("IMEI", deviceId);
		
		Locale locale = getBaseContext().getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		setSystemProperty("microedition.locale", language);
		if(this.contentResolver == null) {
			this.contentResolver = getContentResolver();
		}
		
		// read files directory and save it as a system property
		String appDirectory = getApplicationContext().getFilesDir().getAbsolutePath();
		setSystemProperty("fileconn.dir.private", appDirectory);
		
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
//		registerReceiver(BluetoothEventReceiver.getInstance(), intentFilter);
		
		// rickyn: This initialization fixes the bootstrap problem for GameCanvas.
		new AndroidDisplay(this);
		// This variable is needed to set the midlet to the display. Do not remove!
		Display display = null; //Display.getDisplay(null);
		
		// now create MIDlet:
		try {
			//#if false
				midlet = (MIDlet) Class.forName("${polish.classes.midlet-1}").newInstance();
			//#else
				//#= midlet = (MIDlet) Class.forName("${polish.classes.midlet-1}").newInstance();		
			//#endif
			midlet._setMidletBridge(this);
			//#if true
				//# display = Display.getDisplay(midlet);
			//#endif
		} catch (Exception e) {
			System.err.println("While loading MIDlet: " + e );
			e.printStackTrace();
			notifyDestroyed();
			return;
		}
		
		//#if polish.android.trapHomeButton
			//#debug
			System.out.println("trap Home");
			PackageManager packageManager = getPackageManager();
	        IntentFilter filter = new IntentFilter();
	        filter.addAction("android.intent.action.MAIN");
	        filter.addCategory("android.intent.category.HOME");
	        filter.addCategory("android.intent.category.DEFAULT");
	
	        ComponentName newHomeComponent = new ComponentName(getPackageName(), MidletBridge.class.getName());
	
	        ComponentName[] systemComponents = new ComponentName[] {new ComponentName("com.android.launcher", "com.android.launcher.Launcher"), newHomeComponent};
	
	        packageManager.clearPackagePreferredActivities("com.android.launcher");
	        packageManager.addPreferredActivity(filter, IntentFilter.MATCH_CATEGORY_EMPTY, systemComponents, newHomeComponent);
		
        //#endif
		
		//#= display.setMidlet(midlet);
	}

	public void backlightOn() {
//		if(this.wakeLock != null && this.wakeLock.isHeld()) {
//			//#debug
//			System.out.println("The backlight was requested to go on but the wakeLock is already held. I return without doing anything.");
//			return;
//		}
//		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		this.wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP, "Wake Lock");
//		this.wakeLock.acquire();
//		//#debug
//		System.out.println("WakeLock acquired?"+this.wakeLock.isHeld());
		AndroidDisplay.getDisplay(midlet).setKeepScreenOn(true);
	}
	
	/**
	 * You need to call this method when you switched on the light with {@link #backlightOn()}.
	 */
	public void backlightRelease() {
//		if(this.wakeLock != null && this.wakeLock.isHeld()) {
//			this.wakeLock.release();
//		}
		AndroidDisplay.getDisplay(midlet).setKeepScreenOn(false);
	}
	
	protected void setSystemProperty(String name, String value) {
		if(value == null) {
			value = "";
		}
		// Hidden because midp does not like this method
		//#= System.setProperty(name,value);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//#debug
		System.out.println("Config changed:"+newConfig);
		
		Locale locale = newConfig.locale;
		String language = locale.getLanguage();
		String previousLanguage = System.getProperty("microedition.locale");
		if (!language.equals(previousLanguage)) {
			setSystemProperty("microedition.locale", language);
			//TODO reload resources when dynamic translations are used
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean handled = AndroidDisplay.getDisplay(midlet).onKeyDown(keyCode, event);
		return handled || super.onKeyDown(keyCode, event);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
	 */
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean handled = AndroidDisplay.getDisplay(midlet).onKeyUp(keyCode, event);
		return handled || super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event)
	{
		boolean handled = AndroidDisplay.getDisplay(midlet).onKeyMultiple(keyCode, repeatCount, event);
		return handled || super.onKeyMultiple(keyCode, repeatCount, event);
	}
	
	protected void onPause() {
		//#debug
		System.out.println("MidletBridge.onPause() for " + this + ", isTaskRoot=" + isTaskRoot());
		super.onPause();
		if (!isTaskRoot()) {
			return;
		}
		try {
			hideSoftKeyboard();
			midlet.pauseApp();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestart()
	 */
	
	protected void onRestart() {
		//#debug
		System.out.println("MidletBridge.onRestart() for " + this);
		super.onRestart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	
	protected void onResume() {
		//#debug
		System.out.println("MidletBridge.onResume() for " + this);
		super.onResume();
		if (!isTaskRoot()) {
			finish();
			return;
		}
		
		AndroidDisplay display = AndroidDisplay.getDisplay(midlet);
		if(display.getParent() == null) {
			setContentView(display);
		}
		// This should allow to control the audio volume with the volume keys on the handset when the application has focus.
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.shuttingDown = false;
		// Needed to redraw any previous screens of a previous run. So the application need not call setCurrent in the case of a rerun.
		display.refresh();
		try {
			midlet.startApp();
		} catch (Exception e) {
			//#debug fatal
			System.out.println("startApp() failed: " + e);
			//TODO: add fatal error handling here, e.g. by displaying system error message
		}
	}

	protected void onStart() {
		//#debug
		System.out.println("MidletBridge.onStart() for " + this);
		super.onStart();
		//Debug.startMethodTracing("skobbler");
		// on resume will be called directly afterwards...
//		try {
//			midlet.startApp();
//		} catch (Exception e) {
//			//#debug fatal
//			System.out.println("starApp() failed: " + e);
//			//TODO: add fatal error handling here, e.g. by displaying system error message
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	
	protected void onStop() {
		//#debug
		System.out.println("MidletBridge.onStop() for " + this);
		super.onStop();
		if (!isTaskRoot()) {
			return;
		}
		//#if polish.javaplatform >= Android/1.5
			hideSoftKeyboard();
		//#endif
		//Debug.stopMethodTracing();
		midlet.pauseApp();
		// Release the wake lock if it was acquired.
		backlightRelease();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	
	protected void onDestroy() {
		//#debug
		System.out.println("onDestroy() for " + this + ", isTaskRoot=" + isTaskRoot());
		//Debug.stopMethodTracing();
		super.onDestroy();
		if (!isTaskRoot()) {
			return;
		}
		//#if polish.javaplatform >= Android/1.5
			hideSoftKeyboard();
		//#endif
		//TODO: Use listener pattern to register and unregister Lifecycle listeners.
//		deregisterSqlDao();
		AndroidDisplay display = AndroidDisplay.getDisplay(midlet);
		display.shutdown();
		if ( ! this.shuttingDown) {
			this.shuttingDown = true;
			try {
				midlet.destroyApp(true);
			} catch (MIDletStateChangeException e) {
				//
			}
		}
		if (this.suicideOnExit) {
			int myPid = Process.myPid();
			Process.killProcess(myPid);
		}
	}

	// TODO:
//	private void deregisterSqlDao() {
//		SqlDao.getInstance().destroy();
//	}

	

	/**
	 * Used by an <code>MIDlet</code> to notify the application management
	 * software that it has entered into the <em>Destroyed</em> state. The
	 * application management software will not call the MIDlet's
	 * <code>destroyApp</code> method, and all resources held by the
	 * <code>MIDlet</code> will be considered eligible for reclamation. The
	 * <code>MIDlet</code> must have performed the same operations (clean up,
	 * releasing of resources etc.) it would have if the
	 * <code>MIDlet.destroyApp()</code> had been called.
	 * </DL>
	 * 
	 */
	public final void notifyDestroyed() {
		if( ! this.shuttingDown) {
			this.shuttingDown = true;
			AnimationThread thread = StyleSheet.animationThread;
			if (thread != null) {
				StyleSheet.animationThread = null;
				thread.requestStop();
			}
			super.finish();
		}
		
	}

	
//	
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		// TODO Auto-generated method stub
//		return super.dispatchTouchEvent(ev);
//	}
//


	/**
	 * Notifies the application management software that the MIDlet does not
	 * want to be active and has entered the <em>Paused</em> state. Invoking
	 * this method will have no effect if the <code>MIDlet</code> is
	 * destroyed, or if it has not yet been started.
	 * <p>
	 * It may be invoked by the <code>MIDlet</code> when it is in the
	 * <em>Active</em> state.
	 * <p>
	 * 
	 * If a <code>MIDlet</code> calls <code>notifyPaused()</code>, in the
	 * future its <code>startApp()</code> method may be called make it active
	 * again, or its <code>destroyApp()</code> method may be called to request
	 * it to destroy itself.
	 * <p>
	 * 
	 * If the application pauses itself it will need to call
	 * <code>resumeRequest</code> to request to reenter the
	 * <code>active</code> state.
	 * </DL>
	 * 
	 */
	public final void notifyPaused() {
		//TODO: Trigger the lifecycle but do not call the lifecycle notification methods directly.
//		onPause();
	}

	
	/**
	 * Provides a <code>MIDlet</code> with a mechanism to indicate that it is
	 * interested in entering the <em>Active</em> state. Calls to this method
	 * can be used by the application management software to determine which
	 * applications to move to the <em>Active</em> state.
	 * <p>
	 * When the application management software decides to activate this
	 * application it will call the <code>startApp</code> method.
	 * <p>
	 * The application is generally in the <em>Paused</em> state when this is
	 * called. Even in the paused state the application may handle asynchronous
	 * events such as timers or callbacks.
	 * </DL>
	 * 
	 */
	public final void resumeRequest() {
		// TODO implement resumeRequest
	}

	/**
	 * <p>
	 * Requests that the device handle (for example, display or install) the
	 * indicated URL.
	 * </p>
	 * 
	 * <p>
	 * If the platform has the appropriate capabilities and resources available,
	 * it SHOULD bring the appropriate application to the foreground and let the
	 * user interact with the content, while keeping the MIDlet suite running in
	 * the background. If the platform does not have appropriate capabilities or
	 * resources available, it MAY wait to handle the URL request until after
	 * the MIDlet suite exits. In this case, when the requesting MIDlet suite
	 * exits, the platform MUST then bring the appropriate application (if one
	 * exists) to the foreground to let the user interact with the content.
	 * </p>
	 * 
	 * <p>
	 * This is a non-blocking method. In addition, this method does NOT queue
	 * multiple requests. On platforms where the MIDlet suite must exit before
	 * the request is handled, the platform MUST handle only the last request
	 * made. On platforms where the MIDlet suite and the request can be handled
	 * concurrently, each request that the MIDlet suite makes MUST be passed to
	 * the platform software for handling in a timely fashion.
	 * </p>
	 * 
	 * <p>
	 * If the URL specified refers to a MIDlet suite (either an Application
	 * Descriptor or a JAR file), the application handling the request MUST
	 * interpret it as a request to install the named package. In this case, the
	 * platform's normal MIDlet suite installation process SHOULD be used, and
	 * the user MUST be allowed to control the process (including cancelling the
	 * download and/or installation). If the MIDlet suite being installed is an
	 * <em>update</em> of the currently running MIDlet suite, the platform
	 * MUST first stop the currently running MIDlet suite before performing the
	 * update. On some platforms, the currently running MIDlet suite MAY need to
	 * be stopped before any installations can occur.
	 * </p>
	 * 
	 * <p>
	 * If the URL specified is of the form <code>tel:&lt;number&gt;</code>,
	 * as specified in <a href="http://www.ietf.org/rfc/rfc2806.txt">RFC2806</a>,
	 * then the platform MUST interpret this as a request to initiate a voice
	 * call. The request MUST be passed to the &quot;phone&quot; application to
	 * handle if one is present in the platform. The &quot;phone&quot;
	 * application, if present, MUST be able to set up local and global phone
	 * calls and also perform DTMF post dialing. Not all elements of RFC2806
	 * need be implemented, especially the area-specifier or any other
	 * requirement on the terminal to know its context. The isdn-subaddress,
	 * service-provider and future-extension may also be ignored. Pauses during
	 * dialing are not relevant in some telephony services.
	 * </p>
	 * 
	 * <p>
	 * Devices MAY choose to support additional URL schemes beyond the
	 * requirements listed above.
	 * </p>
	 * 
	 * <p>
	 * Many of the ways this method will be used could have a financial impact
	 * to the user (e.g. transferring data through a wireless network, or
	 * initiating a voice call). Therefore the platform MUST ask the user to
	 * explicitly acknowlege each request before the action is taken.
	 * Implementation freedoms are possible so that a pleasant user experience
	 * is retained. For example, some platforms may put up a dialog for each
	 * request asking the user for permission, while other platforms may launch
	 * the appropriate application and populate the URL or phone number fields,
	 * but not take the action until the user explicitly clicks the load or dial
	 * buttons.
	 * </p>
	 * 
	 * @param urlString the URL for the platform to load. An empty string (not null)
	 *            cancels any pending requests.
	 * @return true if the MIDlet suite MUST first exit before the content can
	 *         be fetched.
	 * @throws ConnectionNotFoundException -
	 *             if the platform cannot handle the URL requested.
	 * @since MIDP 2.0
	 */
	public final boolean platformRequest(String urlString) throws ConnectionNotFoundException{
		if(urlString == null) {
			throw new IllegalArgumentException("Parameter 'url' must not be null.");
		}
		if("".equals(urlString)) {
			// TODO: Cancel pending requests.
			return false;
		}
		if(urlString.startsWith("tel:")) {
			String number = urlString.substring(4);
			// The line is hidden from the IDE as eclipse uses the MIDP String which does not implement CharSequence.
			boolean matches = java.util.regex.Pattern.compile("\\+?\\d+").matcher(number).matches();
			if(!matches) {
				throw new ConnectionNotFoundException("The telephone number '"+number+"' is malformed. It must be described by the regular expression '\\+?\\d+'");
			}
			Intent i = new Intent();
			i.setAction(Intent.ACTION_DIAL);
			Uri numberUri = Uri.parse("tel:"+number);
			//#debug
			System.out.println("Uri for phone number:"+numberUri);
			i.setData(numberUri);
			startActivity(i);
			return false;
		}
		if(urlString.startsWith("http:") || urlString.startsWith("https:")) {
			Uri uri = Uri.parse(urlString);
			Intent intent = new Intent(Intent.ACTION_VIEW,uri);
			startActivity(intent);
			return false;
		}
		if(urlString.startsWith("device://show/settings/gps")) {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
			return false;
		}
		throw new ConnectionNotFoundException("The url '"+urlString+"' can not behandled. The url scheme is not supported");
	}

	/**
	 * Get the status of the specified permission. If no API on the device
	 * defines the specific permission requested then it must be reported as
	 * denied. If the status of the permission is not known because it might
	 * require a user interaction then it should be reported as unknown.
	 * 
	 * @param permission -
	 *            to check if denied, allowed, or unknown.
	 * @return 0 if the permission is denied; 1 if the permission is allowed; -1
	 *         if the status is unknown
	 * @since MIDP 2.0
	 */
	public final int checkPermission(String permission) {
		return -1;
		// TODO implement checkPermission
	}

	public void showSoftKeyboard() {
		//#debug
		System.out.println("MidletBridge.showSoftKeyboard");
		//#if polish.javaplatform >= Android/1.5
			InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
			View focusedView = AndroidDisplay.getInstance().findFocus();
			//System.out.println("focused view=" + focusedView + ", softkeyboard.active=" + inputMethodManager.isActive());
			if (focusedView != null) {
				inputMethodManager.showSoftInput(focusedView, InputMethodManager.SHOW_FORCED);
			}
		//#endif
	}

	public void hideSoftKeyboard() {
		//#debug
		System.out.println("MidletBridge.hideSoftKeyboard");
		//#if polish.javaplatform >= Android/1.5
			AndroidDisplay display = AndroidDisplay.getInstance();
			if (display != null) {
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
				View focusedView = display.findFocus();
				if (focusedView != null && inputMethodManager != null) {
					//System.out.println("focused view=" + focusedView + ", softkeyboard.active=" + inputMethodManager.isActive());
					IBinder windowToken = focusedView.getWindowToken();
					inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
				}
			}
		//#endif
	}
	
	/**
	 * Shows the virtual keyboard when it is hidden and hides it when it is shown.
	 */
	public void toggleSoftKeyboard() {
		//#debug
		System.out.println("Handling toggleSoftKeyboard");

		//#if polish.javaplatform >= Android/1.5
			InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
			View focusedView = AndroidDisplay.getInstance().findFocus();
			if (focusedView != null && inputMethodManager != null) {
				IBinder windowToken = focusedView.getWindowToken();
				inputMethodManager.toggleSoftInputFromWindow(windowToken,  InputMethodManager.SHOW_FORCED, 0);
			}
		//#endif
	}
	
	
	/**
	 * Determines whether the softkeyboard is currently shown
	 * @return true when the virtual keyboard is shown
	 */
	public boolean isSoftKeyboardShown() {
		// this works only for the first time, when a softkeyboard has not been shown yet:
//		InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
//		View focusedView = AndroidDisplay.getInstance().findFocus();
//		boolean isActive = inputMethodManager.isActive(focusedView);
//		return isActive;
		return this.isSoftKeyboardShown;
	}

	
	private Screen getCurrentScreen() {
		Display display = Display.getInstance();
		if (display == null) {
			return null;
		}
		Displayable disp = display.getNextOrCurrent();
		if (disp == null || (!(disp instanceof Screen))) {
			return null;
		}
		return (Screen) disp;
	}
	
//	private void onSoftKeyboardOpened() {
//		Screen screen = getCurrentScreen();
//		if (screen != null) {
//			Container rootContainer = screen.getRootContainer();
//			if (rootContainer != null) {
//				Item item = rootContainer.getFocusedChild();
//				if (item != null) {
//					int absY = item.getAbsoluteY();
//					int screenHeight = screen.getScreenHeight();
//					if (absY > screenHeight / 3) {
//						int newYOffset = - item.relativeY;
//						int contHeight = rootContainer.getItemAreaHeight();
//						if (contHeight < screen.getScreenContentHeight()) {
//							newYOffset -= rootContainer.relativeY - screen.getScreenContentY();
//	}
//
//						screen.setScrollYOffset( newYOffset, true);
//						rootContainer.resetLastPointerPressYOffset();
//					}
//				}
//			}
//		}
//	}
//
//	private void onSoftKeyboardClosed() {
//		Screen screen = getCurrentScreen();
//		if (screen != null) {
//			Container rootContainer = screen.getRootContainer();
//			if (rootContainer != null) {
//				int contHeight = rootContainer.getItemAreaHeight();
//				if (contHeight < screen.getScreenContentHeight()) {
//					// only reset the scroll y offset for screens that use less space than is available:
//					rootContainer.setScrollYOffset(0, true);
//					rootContainer.resetLastPointerPressYOffset();
//	}
//			}
//		}
//	}

	/**
	 * 
	 * @param suicideOnExit true if the process should be killed after the destroy event is received.
	 */
	public void setSuicideOnExit(boolean suicideOnExit) {
		this.suicideOnExit = suicideOnExit;
	}

	public void switchInputMethod() {
		InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showInputMethodPicker();
	}

	public void onSizeChanged( int w, int h, int oldW, int oldH) {
		if (h < oldH) {
			this.isSoftKeyboardShown = true;
		} else {
			this.isSoftKeyboardShown = false;	
		}
	}
	
	
	
	public synchronized void addCommand( de.enough.polish.ui.Command cmd ) {
		//#if !tmp.fullscreen
			if (!this.addedCommands.contains(cmd)) {
				this.addedCommands.add( cmd );
			}
		//#endif
	}
	
	public synchronized void removeCommand( de.enough.polish.ui.Command cmd ) {
		//#if !tmp.fullscreen
			this.addedCommands.remove( cmd );
		//#endif
	}
	
	
	//#if !tmp.fullScreen
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (menu != null) {
			this.isMenuOpened = true;
			menu.clear();
			this.addedCommandMenuItemBridges.clear();
			Object[] commands = this.addedCommands.getInternalArray();
			for (int i = 0; i < commands.length; i++) {
				Command cmd = (Command) commands[i];
				if (cmd == null) {
					break;
				}
				int groupId = 0;
				int itemId = this.addedCommandMenuItemBridges.size();
				MenuItem item = menu.add( groupId, itemId, cmd.getPriority(), cmd.getLabel() );
				//#if polish.css.icon-image
					Style style = cmd.getStyle();
					if (style != null) {
						String url = style.getProperty("icon-image");
						if (url != null) {
							try {
								int id = ResourcesHelper.getResourceID(url);
								item.setIcon(id);
							} catch (IOException e) {
								//#debug error
								System.err.println("Unable to retrieve ID for " + url + e);
							}
						}
					}
				//#endif
				CommandMenuItemBridge bridge = new CommandMenuItemBridge( cmd, item );
				this.addedCommandMenuItemBridges.add(bridge);
			}
		}
		super.onPrepareOptionsMenu(menu);
		return true;
	}
	//#endif

	//#if !tmp.fullScreen
	public boolean onOptionsItemSelected(MenuItem item) {
		Object[] items = this.addedCommandMenuItemBridges.getInternalArray();
		for (int i = 0; i < items.length; i++) {
			CommandMenuItemBridge bridge = (CommandMenuItemBridge) items[i];
			if (bridge == null) {
				break;
			}
			if (bridge.menuItem == item) {
				final Command cmd = bridge.cmd;
				if (cmd.hasSubCommands()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(MidletBridge.getInstance());
					builder.setTitle(cmd.getLabel());
					String[] labels = new String[ cmd.getSubCommandsCount() ];
					Object[] subCommands = cmd.getSubCommmandsArray();
					for (int j = 0; j < labels.length; j++) {
						labels[j] = ((Command)subCommands[j]).getLabel();
					}
					builder.setItems(labels, new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int itemIndex) {
					    	Display.getInstance().commandAction(cmd.getSubCommands()[itemIndex], (de.enough.polish.ui.Displayable)null );
					    }
					});
					builder.show();
				} else {
					Display.getInstance().commandAction(cmd, (de.enough.polish.ui.Displayable)null );
				}
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	//#endif

	//#if !tmp.fullScreen
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		this.isMenuOpened = false;
	}
	//#endif
	
	
	//#if !tmp.fullScreen
	public boolean onBack() {
		if (this.isMenuOpened) {
			return false;
		}
		//#if polish.javaplatform >= Android/1.5
			if (isSoftKeyboardShown()) {
				hideSoftKeyboard();
				return true;
			}
		//#endif
/* Disabled due to back handled twice in case of native command handling.
 * Untested for polish command handling.
		Command cmdBack = null;
		Object[] commands = this.addedCommands.getInternalArray();
		for (int i = 0; i < commands.length; i++) {
			Command cmd = (Command) commands[i];
			if (cmd == null) {
				break;
			}
			int type = cmd.getCommandType();
			if (type == Command.BACK || type == Command.CANCEL || type == Command.EXIT) {
				if (cmdBack == null || cmdBack.getPriority() > cmd.getPriority()) {
					cmdBack = cmd;
				}
			}
		}
		if (cmdBack == null && this.addedCommands.size() == 1 && ((Command)commands[0]).getCommandType() == Command.OK) {
			// this seems to be an alert with only the OK command present:
			cmdBack = (Command)commands[0];
		}
		if (cmdBack != null) {
			Display.getInstance().commandAction(cmdBack, (de.enough.polish.ui.Displayable)null );
			return true;
		}
		return false;
*/
		return true;
	}
	//#endif
	
	//#if !tmp.fullScreen
	public boolean onOK() {
		Command cmdOK = null;
		Object[] commands = this.addedCommands.getInternalArray();
		for (int i = 0; i < commands.length; i++) {
			Command cmd = (Command) commands[i];
			if (cmd == null) {
				break;
			}
			int type = cmd.getCommandType();
			if (type == Command.OK) {
				if (cmdOK == null || cmdOK.getPriority() > cmd.getPriority()) {
					cmdOK = cmd;
				}
			}
		}
		if (cmdOK != null) {
			Display.getInstance().commandAction(cmdOK, (de.enough.polish.ui.Displayable)null );
			return true;
		}
		return false;
	}
	//#endif


	static class CommandMenuItemBridge {
		final Command cmd;
		final MenuItem menuItem;

		public CommandMenuItemBridge(de.enough.polish.ui.Command cmd, MenuItem menuItem) {
			this.cmd = cmd;
			this.menuItem = menuItem;
		}
		
	}


	public static MidletBridge getInstance() {
		return instance;
	}


}
