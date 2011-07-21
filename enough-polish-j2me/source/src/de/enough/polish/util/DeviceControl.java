package de.enough.polish.util;

//#if polish.usePolishGui
	import de.enough.polish.ui.Display;
	import de.enough.polish.ui.StyleSheet;
//#endif
//#if polish.android
	import de.enough.polish.android.midlet.MidletBridge;
//#endif
//#if polish.blackberry && polish.hasPointerEvents
	//#if polish.usePolishGui
		import de.enough.polish.blackberry.ui.BaseScreen;
	//#endif
	import net.rim.device.api.ui.VirtualKeyboard;
//#endif
	
/**
 * 
 * <p>Controls backlight and vibration in an device-independent manner</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * @author Andre Schmidt
 * @author Robert Virkus
 */
public class DeviceControl
//#if polish.api.nokia-ui
	//#define tmp.useNokiaUi
//#elif polish.blackberry && (polish.BlackBerry.enableBackLight || blackberry.certificate.dir:defined)
	//#define tmp.useBlackBerry
//#endif
//#if (!(tmp.useNokiaUi || tmp.useBlackBerry) && polish.usePolishGui && polish.midp2) || polish.api.samsung-api
	//#define tmp.useThread
	extends Thread 
//#endif
{
	
	private static DeviceControl thread;
	private static Object lightsLock = new Object();
	private static Object vibrateLock = new Object();
	private boolean lightOff = false;
	private static boolean fallbackOnGpsDisabled = true; 
	
	private DeviceControl() {
		// disallow instantiation
	}
	
	public void run()
	{
		int displaytime = 10000;
		long sleeptime = (displaytime * 90) / 100;
		boolean increaseAfterFirstLoop = true;
		while(!this.lightOff)
		{
			switchLightOnFor( displaytime );
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				// ignore
			}
			if (increaseAfterFirstLoop) {
				increaseAfterFirstLoop = false;
				displaytime = 20000;
				sleeptime = 18000;
			}
		}
	}
	
	private void switchLightOnFor( int durationInMs ) {
		synchronized(lightsLock) {
			//#if polish.api.samsung
				com.samsung.util.LCDLight.on(durationInMs);
			//#elif polish.api.nokia-ui
				//#if polish.Bugs.BacklightRequiresLightOff
					com.nokia.mid.ui.DeviceControl.setLights(0,0);
				//#endif
				com.nokia.mid.ui.DeviceControl.setLights(0,100);
			//#elif polish.midp2  && polish.usePolishGui
				StyleSheet.display.flashBacklight(durationInMs);
			//#endif
		}
	}
	
	private void switchLightOff()
	{
		synchronized(lightsLock) {
			this.lightOff = true;
			//#if polish.api.samsung
				com.samsung.util.LCDLight.off();
			//#elif polish.midp2 && polish.usePolishGui
				StyleSheet.display.flashBacklight(0);
			//#endif
		}
	}
	
	/**
	 * Turns the backlight on on a device until lightOff is called
	 * 
	 * @return true when backlight is supported on this device.
	 */
	public static boolean lightOn()
	{
		
		//#if polish.android
			MidletBridge.instance.backlightOn();
			//# return true;
		//#else
		synchronized(lightsLock) {
			boolean success = false;
			//#if tmp.useNokiaUi 
				com.nokia.mid.ui.DeviceControl.setLights(0,100);
				success = true;
			//#elif tmp.useBlackBerry
				net.rim.device.api.system.Backlight.enable(true);
				success = true;
			//#elif tmp.useThread
				if (thread == null) {
					if (isLightSupported()) {
						DeviceControl dc = new DeviceControl();
						dc.start();
						success = true;
					}
				}
			//#endif
			return success;
		}
		//#endif
	}
	
	/**
	 * Turns the backlight off
	 *
	 */
	public static void lightOff()
	{
		//#if polish.android
			MidletBridge.instance.backlightRelease();
		//#else
		synchronized(lightsLock) {
			//#if tmp.useNokiaUi
				com.nokia.mid.ui.DeviceControl.setLights(0,0);
			//#elif tmp.useBlackBerry
				net.rim.device.api.system.Backlight.enable(false);
			//#elif tmp.useThread
				DeviceControl dc = thread;
				if (dc != null) {
					dc.switchLightOff();
					thread = null;
				}
			//#endif
		}
		//#endif
	}
	
	/**
	 * Checks if backlight can be controlled by the application
	 * 
	 * @return true when the backlight can be controlled by the application
	 */
	public static boolean isLightSupported()
	{
		boolean isSupported = false;
		//#if polish.android
			isSupported = true;
		//#elif polish.api.nokia-ui && !polish.Bugs.NoBacklight
			isSupported = true;
		//#elif tmp.useBlackBerry
			isSupported = true;
		//#elif polish.api.samsung
			isSupported = com.samsung.util.LCDLight.isSupported();
//		//#elif polish.midp2 && polish.usePolishGui
//			isSupported = StyleSheet.display.flashBacklight(0);
		//#endif
		return isSupported;
	}

	
	

	/**
	 * Vibrates the device for the given duration
	 * 
	 * @param duration the duration in milliseconds
	 * @return true when the vibration was activated successfully
	 */
	public static boolean vibrate(int duration)
	{
		synchronized(vibrateLock) {
			boolean success = false;
			//#if polish.midp2 && polish.usePolishGui
				success = StyleSheet.display.vibrate(duration);
			//#elif polish.api.nokia-ui
				try {
					com.nokia.mid.ui.DeviceControl.startVibra(80, duration);
					success = true;
				} catch (IllegalStateException e) {
					// no vibration support
				}
			//#elif polish.api.samsung
				com.samsung.util.Vibration.start(duration, 100);
				success = com.samsung.util.Vibration.isSupported();
			//#endif
			return success;
		}
	}
	
	/**
	 * Checks if vibration can be controlled by the application
	 * 
	 * @return true when the vibration can be controlled by the application
	 */
	public static boolean isVibrateSupported()
	{
		synchronized(vibrateLock) {
			boolean isSupported = false;
			//#if polish.api.nokia-ui && !polish.api.midp2
				try {
					com.nokia.mid.ui.DeviceControl.startVibra(0, 1);
					isSupported = true;
				} catch (IllegalStateException e) {
					// no vibration support
				}
			//#elif polish.api.samsung
				isSupported = com.samsung.util.Vibration.isSupported();
			//#elif polish.midp2 && polish.usePolishGui
				isSupported = StyleSheet.display.vibrate(0);
			//#endif
			return isSupported;
		}
	}

	/**
	 * Shows the softkeyboard if the device supports it. This method is only supported on the Android platform at the moment.
	 * @see #hideSoftKeyboard()
	 * @see #isSoftKeyboardShown()
	 */
	public static void showSoftKeyboard() {
		//#if polish.javaplatform >= Android/1.5
			MidletBridge.instance.showSoftKeyboard();
		//#elif polish.blackberry && polish.hasPointerEvents && polish.usePolishGui
			Display disp = Display.getInstance();
			if (disp != null) {
				BaseScreen baseScreen = (BaseScreen)(Object)disp;
				VirtualKeyboard keyboard = baseScreen.getVirtualKeyboard();
				if (keyboard != null) {
					baseScreen.setIgnoreObscureEvent(true);
					keyboard.setVisibility( VirtualKeyboard.SHOW );					
				}
			}
		//#endif
	}
	
	/**
	 * Hides the softkeyboard if the device supports it. This method is only supported on the Android platform at the moment.
	 * @see #showSoftKeyboard()
	 * @see #isSoftKeyboardShown()
	 */
	public static void hideSoftKeyboard() {
		//#if polish.javaplatform >= Android/1.5
			MidletBridge.instance.hideSoftKeyboard();
		//#elif polish.blackberry && polish.hasPointerEvents && polish.usePolishGui
			Display disp = Display.getInstance();
			if (disp != null) {
				BaseScreen baseScreen = (BaseScreen)(Object)disp;
				VirtualKeyboard keyboard = baseScreen.getVirtualKeyboard();
				if (keyboard != null) {
					baseScreen.setIgnoreObscureEvent(false);
					keyboard.setVisibility( VirtualKeyboard.HIDE );					
				}
			}
		//#endif
	}
	
	/**
	 * Checks is a virtual keyboard is currently shown.
	 * @return true when a virtual keyboard is supported and visible at the moment
	 * @see #showSoftKeyboard()
	 * @see #hideSoftKeyboard()
	 */
	public static boolean isSoftKeyboardShown() {
		boolean result = false;
		//#if polish.javaplatform >= Android/1.5
			result = MidletBridge.instance.isSoftKeyboadShown();
		//#elif polish.blackberry && polish.hasPointerEvents && polish.usePolishGui
			Display disp = Display.getInstance();
			if (disp != null) {
				VirtualKeyboard keyboard = ((BaseScreen)(Object)disp).getVirtualKeyboard();
				if (keyboard != null) {
					int visibility = keyboard.getVisibility();
					result = (visibility ==  VirtualKeyboard.SHOW) || (visibility ==  VirtualKeyboard.SHOW_FORCE);
				}
			}
		//#endif
		return result;
	}
	
	//#if polish.android
	
	public static void setSuicideOnExit(boolean suicideOnExit) {
		MidletBridge.instance.setSuicideOnExit(suicideOnExit);
	}
	
	/**
	 * This method allows the caller to disable the fallback to the network location provider when the GPS location provider is not available.
	 * Normally the network location provider is used in case the GPS location provider goes offline for some reason. With this method you can
	 * turn of this fallback. This is useful if you want to be sure that you always have full acurracy for your location or non at all.
	 * @param setFallbackOnGpsDisabled <code>true</code> if you want to disable the fallback mechanism, <code>false</code> otherwise.
	 * @deprecated
	 */
	public static void shouldFallbackToNetworkLocationOnGpsDisabled(boolean setFallbackOnGpsDisabled) {
		fallbackOnGpsDisabled = setFallbackOnGpsDisabled;
	}

	/**
	 * This methods tells the caller if the fallback mechanism for the GPS location provider is enabled. See {@link #shouldFallbackToNetworkLocationOnGpsDisabled(boolean)} for details.
	 * @return <code>true</code> if the fallback is disabled, <code>false</code> if the fallback is enabled (the default).
	 * @deprecated
	 */
	public static boolean isFallbackToNetworkLocationOnGpsDisabled() {
		return fallbackOnGpsDisabled;
	}

	/**
	 * 
	 * @return <code>true</code> if GPS is enabled, <code>false</code> otherwise.
	 * @deprecated Use {@link de.enough.polish.location.LocationService#isGpsEnabled()}
	 */
	public static boolean isGpsEnabled() {
		return de.enough.polish.location.LocationService.isGpsEnabled();
	}
	
	//#endif
}
