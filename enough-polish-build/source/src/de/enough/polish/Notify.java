/*
 * Copyright (c) 2009 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish;

import de.enough.polish.notify.GrowlNotifier;
import de.enough.polish.notify.LinuxNotifier;

/**
 * A generic interface for notifications.
 * This will call the notification system that is installed.
 *
 * TODO: This should be able to be turned on and off by some config
 * TODO: config path location of bin files
 *
 * @author david
 */
public abstract class Notify {

    private static Notify INSTANCE;

    public synchronized static Notify getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        if (GrowlNotifier.isGrowlAvailable()) {
            INSTANCE = new GrowlNotifier();
        } else if (LinuxNotifier.isNotifyAvailable()) {
            INSTANCE = new LinuxNotifier();
        }

        return INSTANCE;
    }

    /**
     * Shows the message using the desired handlers
     * @param title the title
     * @param message the message
     * @return true if publish was successful, otherwise false
     */
    public static boolean publish(String title, String message) {
        Notify n = Notify.getInstance();
        if (n != null) {
            return n.publishInternal(title, message);
        }
        return false;
    }

    protected abstract boolean publishInternal(String title,String message);

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("usage:");
            System.out.println("java de.enough.polish.notify [title] [description]");
            System.exit(1);
        }
        publish(args[0], args[1]);
        System.exit(0);
    }
}