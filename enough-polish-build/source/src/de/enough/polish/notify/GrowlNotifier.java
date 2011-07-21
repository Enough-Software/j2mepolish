package de.enough.polish.notify;

import de.enough.polish.Notify;
import de.enough.polish.util.StringUtil;

/**
 * Helper for Growl notifications - these notifications are popular for OS X.
 *
 * copyright 2009 Enough Software
 * @author Robert Virkus
 *
 */
public class GrowlNotifier extends Notify {
	
	private final static String APPLE_SCRIPT = "tell application \"GrowlHelperApp\"\n"
		+ "-- Make a list of all the notification types\n" 
		+ "-- that this script will ever send:\n"
		+ "set the allNotificationsList to {\"Message\"}\n"
		+ "-- Make a list of the notifications\n" 
		+ "-- that will be enabled by default.\n"
		+ "-- Those not enabled by default can be enabled later\n" 
		+ "-- in the 'Applications' tab of the growl prefpane.\n"
		+ "set the enabledNotificationsList to {\"Message\"}\n"
	
		+ "-- Register our script with growl.\n"
		+ "-- You can optionally (as here) set a default icon\n" 
		+ "-- for this script's notifications.\n"
		+ "register as application 	\"J2ME Polish\" all notifications allNotificationsList 	default notifications enabledNotificationsList	icon of application \"Script Editor\"\n"
	
		+ "--	Send a Notification...\n"
		+ "notify with name \"Message\" title \"${title}\" description 	\"${description}\" application name \"J2ME Polish\"\n"
	
		+ "end tell"
		;

	/**
	 * Publishes a growl notification
	 * @param title the title
	 * @param text the text
	 * @return true when the publishing succeeded
	 */
	protected boolean publishInternal( String title, String text ) {
		String script = StringUtil.replace( APPLE_SCRIPT, "${title}", title );
		script = StringUtil.replace( script, "${description}", text );
		String[] cmd = new String[]{"/usr/bin/osascript", "-e",  script };
		// run the script
		try
		{
			Runtime.getRuntime().exec(cmd);
//			ProcessUtil.exec(cmd, "apple: ", true);
//			Process process = Runtime.getRuntime().exec(cmd);
//			int result = process.waitFor();
//			System.out.println("result=" + result);
			return true;
		} catch (Exception e)
		{
			// TODO robertvirkus handle IOException
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Checks if Growl is available
	 * @return true when growl notifications can be used
	 */
	public static boolean isGrowlAvailable() {
		String oSName = System.getProperty("os.name").toLowerCase();
		boolean isMacOsX = oSName.startsWith("mac os x");
		//TODO check if growl helper is really running... 
		return isMacOsX;
	}
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("usage:");
			System.out.println("java de.enough.polish.notify.GrowlNotifier [title] [description]");
			System.exit(1);
		}
		if (isGrowlAvailable()) {
                    GrowlNotifier in = new GrowlNotifier();
			in.publishInternal( args[0], args[1]);
		}
		System.exit(0);
	}
}
