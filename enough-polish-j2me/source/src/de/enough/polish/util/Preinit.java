//#condition polish.usePolishGui
package de.enough.polish.util;

import de.enough.polish.ui.Screen;
import de.enough.polish.ui.UiAccess;

public class Preinit implements Runnable {
	boolean threaded;
	
	Screen screen;
	
	public static void preinit(Screen screen) throws IllegalArgumentException{
		if(screen.isShown()) {
			synchronized(screen.getPaintLock()) {
				UiAccess.init(screen);
			}
		} else {
			UiAccess.init(screen);
		}
	}
	
	public Preinit(Screen screen) {
		this.screen = screen;
	}

	public void run() {
		preinit(this.screen);
	}
}
