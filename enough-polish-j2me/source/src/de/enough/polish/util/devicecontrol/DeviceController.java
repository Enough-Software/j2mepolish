//#condition polish.HasOptionalApis
package de.enough.polish.util.devicecontrol;

/**
 * Controls a device
 * @author Robert Virkus
 */
public interface DeviceController {
	
	/**
	 * Turns the backlight on on a device until lightOff() is called
	 * 
	 * @return true when backlight is supported on this device.
	 * @see #lightOff()
	 */
	boolean lightOn();
	
	/**
	 * Turns the backlight off
	 * @see #lightOn()
	 */
	void lightOff();
	
	/**
	 * Checks if backlight can be controlled by the application
	 * 
	 * @return true when the backlight can be controlled by the application
	 */
	boolean isLightSupported();


}
