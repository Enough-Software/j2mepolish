//#condition polish.usePolishGui

package de.enough.polish.ui;

/**
 * The AlertType provides an indication of the nature of alerts. 
 * Alerts are used by an application to present various kinds of 
 * information to the user. An AlertType may be used to directly 
 * signal the user without changing the current Displayable. 
 * The playSound method can be used to spontaneously generate a sound 
 * to alert the user. For example, a game using a Canvas can use playSound 
 * to indicate success or progress. 
 * The predefined types are INFO, WARNING, ERROR, ALARM, and CONFIRMATION. 
 *
 * <b>Note that in J2ME Polish the alert type does not really matter, you can influence the design in the style for the alert.</b>
 */

public class AlertType{
	/**
	 * An ALARM AlertType is a hint to alert the user to an event for which the user has previously requested to be notified.
	 */
	public static final AlertType ALARM = new AlertType();
	/**
	 * A CONFIRMATION AlertType is a hint to confirm user actions. For example, "Saved!" might be shown to indicate that a Save operation has completed. 
	 */
	public static final AlertType CONFIRMATION = new AlertType();
	/**
	 * An ERROR AlertType is a hint to alert the user to an erroneous operation. For example, an error alert might show the message, "There is not enough room to install the application." 
	 */
	public static final AlertType ERROR = new AlertType();
	/**
	 * An INFO AlertType typically provides non-threatening information to the user. For example, a simple splash screen might be an INFO AlertType. 
	 */
	public static final AlertType INFO = new AlertType();
	/**
	 * A WARNING AlertType is a hint to warn the user of a potentially dangerous operation. For example, the warning message may contain the message, "Warning: this operation will erase your data." 
	 */
	public static final AlertType WARNING = new AlertType();
	
	
	/**
	 * Protected constructor for subclasses.
	 */
	protected AlertType(){}
	
	
	/**
	 * Alert the user by playing the sound for this AlertType. 
	 * The AlertType instance is used as a hint by the device 
	 * to generate an appropriate sound. Instances other than 
	 * those predefined above may be ignored. The actual sound 
	 * made by the device, if any, is determined by the device. 
	 * The device may ignore the request, use the same sound for 
	 * several AlertTypes or use any other means suitable to 
	 * alert the user. 
	 * Note that this is only implemented for pure J2ME/MIDP phone
	 * 
	 * @param display to which the AlertType's sound should be played. 
	 * @return true if the user was alerted, false otherwise.
	 * @throws NullPointerException if display is null
	 */
	public boolean playSound(Display display) throws NullPointerException{
		//#if polish.midp && !(polish.android || polish.blackberry)
			javax.microedition.lcdui.AlertType nativeType;
			if (this == ALARM) {
				nativeType = javax.microedition.lcdui.AlertType.ALARM; 
			} else if (this == CONFIRMATION) {
				nativeType = javax.microedition.lcdui.AlertType.CONFIRMATION;
			} else if (this == ERROR) {
				nativeType = javax.microedition.lcdui.AlertType.ERROR;
			} else if (this == INFO) {
				nativeType = javax.microedition.lcdui.AlertType.INFO;
			} else if (this == WARNING) {
				nativeType = javax.microedition.lcdui.AlertType.WARNING;
			} else {
				return false;
			}
			return nativeType.playSound( (javax.microedition.lcdui.Display) display.getNativeDisplay() );
		//#else
			//# return false;
		//#endif
	}
	 
}