/*
 * Created on Apr 5, 2009 at 8:36:51 PM.
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
package de.enough.polish.util;

//#if polish.api.bluetooth
	import javax.bluetooth.LocalDevice;
//#endif
//#if polish.midp || polish.usePolishGui
	import javax.microedition.lcdui.Canvas;
	import javax.microedition.lcdui.Graphics;
	import javax.microedition.lcdui.Image;
//#endif

//#if polish.usePolishGui
	import de.enough.polish.ui.Display;
//#endif

/**
 * <p>Provides information about the current device</p>
 * <pre>
 *  History
 *   04/02/2010  - David Rubin Added support for RIM vendor results in vendor
 * </pre>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DeviceInfo
{

	/** The vendor of this device is not known */
	public static final int VENDOR_UNKNOWN = -1;
	/** The vendor of this device is Nokia */
	public static final int VENDOR_NOKIA = 1;
	/** The vendor of this device is Samsung */
	public static final int VENDOR_SAMSUNG = 2;
	/** The vendor of this device is LG */
	public static final int VENDOR_LG = 3;
	/** The vendor of this device is Sony Ericsson */
	public static final int VENDOR_SONY_ERICSSON = 4;
	/** The vendor of this device is Motorola */
	public static final int VENDOR_MOTOROLA = 5;
	/** The vendor of this device is ZTE */
	public static final int VENDOR_ZTE = 6;
	/** The vendor of this device is Acer */
	public static final int VENDOR_ACER = 7;
	/** The vendor of this device is Alcatel */
	public static final int VENDOR_ALCATEL = 8;
	/** The vendor of this device is BlackBerry */
	public static final int VENDOR_BLACKBERRY = 9;
	/** The vendor of this device is Sagem */
	public static final int VENDOR_SAGEM = 10;
	private static boolean requiresFullRgbArrayForDrawRgb;
	private static boolean requiresFullRgbArrayForDrawRgbResolved;
	/** The vendor of this device is  * /
	public static final int VENDOR_ = ;
	*/
	
	private static final int VENDOR_UNRESOLVED = -2;
	private static int resolvedVendor = VENDOR_UNRESOLVED;

	private DeviceInfo()
	{
		// disallow external instantiation
	}
	
	/**
	 * Tries to detect the vendor of this device.
	 * 
	 * @return the vendor, e.g. VENDOR_NOKIA or VENDOR_UNKNOWN
	 */
	public static int getVendor() {
		if (resolvedVendor == VENDOR_UNRESOLVED) {
			resolvedVendor = resolveVendor();
		}
		return resolvedVendor;
	}
	
	private static int resolveVendor() {
		String platform = System.getProperty( "microedition.platform" );
		if (platform == null) {
			platform = "";
		} else {
			platform = platform.toLowerCase();
		}
		if ("j2me".equals(platform)) {
			String model = System.getProperty( "device.model" );
			if (model != null) {
				return VENDOR_MOTOROLA;
			}
		}
		if (platform.startsWith("nokia")) {
			return VENDOR_NOKIA;
		}
		if (platform.startsWith("sonyericsson")) {
			return VENDOR_SONY_ERICSSON;
		}
		if (platform.startsWith("samsung")) {
			return VENDOR_SAMSUNG;
		}
		if (platform.startsWith("lg")) {
			return VENDOR_LG;
		}
		if (platform.startsWith("zte")) {
			return VENDOR_ZTE;
		}
		if (platform.startsWith("blackberry") || platform.startsWith("rim")) {
			return VENDOR_BLACKBERRY;
		}
		String model = System.getProperty( "device.model" );
		if (model != null) {
			//TODO: note this is a rather wild guess:
			return VENDOR_SAMSUNG;
		}
		return VENDOR_UNKNOWN;
	}
	
	/**
	 * Retrieves the name of the device's vendor or null when it cannot be detected.
	 * @return the vendor name or null
	 */
	public static String getVendorName() {
		int vendor = getVendor();
		switch (vendor) {
		case VENDOR_UNKNOWN: return null;
		case VENDOR_NOKIA: return "Nokia";
		case VENDOR_SAMSUNG: return "Samsung";
		case VENDOR_LG: return "LG";
		case VENDOR_SONY_ERICSSON: return "Sony Ericsson";
		case VENDOR_MOTOROLA: return "Motorola";
		case VENDOR_ZTE: return "ZTE";
		case VENDOR_ACER: return "Acter";
		case VENDOR_ALCATEL: return "Alcatel";
		case VENDOR_BLACKBERRY: return "BlackBerry";
		case VENDOR_SAGEM: return "Sagem";
		}
		return null;
	}
	
	/**
	 * Tries to determine the name of this device model, e.g. "N97" when having a Nokia/N97
	 * @return the name of this device model or null when it cannot be determined
	 */
	public static String getDeviceName() {
		//#if polish.blackberry
			//# return net.rim.device.api.system.DeviceInfo.getDeviceName();
		//#else
			String platform = System.getProperty( "microedition.platform" );
			if (platform == null || "j2me".equals(platform)) {
				platform = System.getProperty( "device.model" );
			}
			if (platform != null) {
				String vendor = getVendorName();
				if (vendor != null) {
					int index = platform.toLowerCase().indexOf(vendor.toLowerCase());
					if (index != -1) {
						platform = platform.substring( index + vendor.length() ).trim();
					}
					index = platform.indexOf(' ');
					if (index != -1) {
						platform = platform.substring(0, index );
					}
					index = platform.indexOf('-');
					if (index != -1) {
						platform = platform.substring(0, index );
					}
					index = platform.indexOf('/');
					if (index != -1) {
						platform = platform.substring(0, index );
					}
					return platform;
				}
			}
			return null;
		//#endif
	}
	
	/**
	 * Tries to guess the key for changing the input mode, e.g. from 123 to abc.
	 * 
	 * @return the key, by default Canvas.KEY_POUND 
	 */
	public static int getKeyInputModeSwitch() {
		int key = 35; // == Canvas.KEY_POUND
		//#if polish.midp
			int vendor = getVendor();
			if (vendor == VENDOR_SONY_ERICSSON || vendor == VENDOR_SAMSUNG) {
				key = Canvas.KEY_STAR;
			}
		//#endif
		return key;
	}
	
	/**
	 * Tries to guess the key for entering a space character.
	 * 
	 * @return the key, by default Canvas.KEY_NUM0; 
	 */
	public static int getKeySpace() {
		int key = 48; // == Canvas.KEY_NUM0
		//#if polish.midp
			int vendor = getVendor();
			if (vendor == VENDOR_SONY_ERICSSON || vendor == VENDOR_SAMSUNG) {
				key = Canvas.KEY_POUND;
			}
		//#endif
		return key;
	}

	/**
	 * Checks if the device requires a full RGB array consisting of the width x height of the covered area
	 * @return true when a full RGB array is required, false when the width of the covered area is sufficient
	 */
	public static boolean requiresFullRgbArrayForDrawRgb() {
		//#if polish.midp2
			if (!requiresFullRgbArrayForDrawRgbResolved) {
				int[] rgb = new int[10];
				for (int i = 0; i < rgb.length; i++) {
					rgb[i] = 0x99000000;
				}
				try {
					Image image = Image.createImage(10, 10);
					Graphics g = image.getGraphics();
					g.drawRGB(rgb, 0, 0, 0, 0, 10, 10, true );
					// retrieve the seventh pixel-row (anyone will do):
					image.getRGB(rgb, 0, 10, 0, 7, 10, 1);
					int pixel = rgb[4];
					//#debug
					System.out.println("pixel color is " + Integer.toHexString(pixel));
					if (pixel == 0xffffffff) {
						requiresFullRgbArrayForDrawRgb = true;
					}
				} catch (Exception e) {
					requiresFullRgbArrayForDrawRgb = true;
				}
				requiresFullRgbArrayForDrawRgbResolved = true;
			}
		//#endif
		return requiresFullRgbArrayForDrawRgb;
	}


	/**
	 * Tests if the current device has pointer events
	 * 
	 * @return true when this device has pointer events
	 */
	public static boolean hasPointerEvents() {
		//#if polish.usePolishGui
			Display disp = Display.getInstance();
			if (disp != null) {
				return disp.hasPointerEvents();
			}
		//#endif
		//#if polish.midp
			Canvas canvas = new Canvas() {
				public void paint(Graphics g) {
					// ignore
				}
			};
			if ( canvas.hasPointerEvents() ) {
				return true;
			}
		//#endif
		return false;
	}

	/**
	 * Retrieves a unique ID of this device.
	 * This could be the IMEI or the local Bluetooth address of this device.
	 * Note that some phones allow to access the IMEI but only when the application has been signed.
	 * @return the device id
	 */
	public static String getDeviceId() {
		//#if polish.blackberry
			int idInt = net.rim.device.api.system.DeviceInfo.getDeviceId();
			//# return Integer.toString(idInt);
		//#else
			String[] properties = new String[]{ "phone.imei", "phone.imei", "com.sonyericsson.imei", "IMEI", "com.motorola.IMEI", "com.samsung.imei", "com.lge.imei", "IMEI"};
			String id = null;
			for (int i = 0; i < properties.length; i++) {
				String name = properties[i];
				try {
					id = System.getProperty(name);
					if (id != null) {
						break;
					}
				} catch (Exception e) {
					//#debug info
					System.out.println("Unable to retrieve System property " + name + ": most likely need to be signed" + e);
				}
			}
			if (id != null) {
				return id;
			}
			//#if polish.api.bluetooth
				try {
					id = LocalDevice.getLocalDevice().getBluetoothAddress();
					if (id != null) {
						return id;
					}
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to retrieve local Bluetooth address");
				}
			//#endif
			return null;
		//#endif
	}

}
