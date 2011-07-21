package de.enough.polish.notify;

import de.enough.polish.Notify;

/**
 * Helper for notifications on Linux.
 *
 * copyright 2009 Enough Software
 * @author Michael Koch
 */
public class LinuxNotifier extends Notify {

    /**
     * Publishes a notification
     * @param title the title
     * @param text the text
     * @return true when the publishing succeeded
     */
    protected boolean publishInternal(String title, String text) {
        try {
            String[] cmd = new String[]{"notify-send", title, text};
            Runtime.getRuntime().exec(cmd);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send notification.");
            return false;
        }
    }

    /**
     * Checks if notification framework is available
     * @return true when notifications can be used
     */
    public static boolean isNotifyAvailable() {
        String oSName = System.getProperty("os.name").toLowerCase();
        boolean isLinux = oSName.startsWith("linux");
        //TODO check if 'notify-send' command is really available.
        return isLinux;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("usage:");
            System.out.println("java de.enough.polish.notify.LinuxNotifier [title] [description]");
            System.exit(1);
        }
        if (isNotifyAvailable()) {
            LinuxNotifier in = new LinuxNotifier();
            in.publishInternal(args[0], args[1]);
        }
        System.exit(0);
    }
}
