//#condition polish.usePolishGui
package de.enough.polish.util;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.UiAccess;

public class ItemPreinit {
	public static void preinit(Item item) throws IllegalArgumentException{
		Screen screen = item.getScreen();
		if(screen == null) {
			throw new IllegalArgumentException("item has no screen");
		}
		
		Preinit.preinit(screen);
	}
	
	public static void preinit(Item item, int width, int height) throws IllegalArgumentException{
		UiAccess.init(item, width, width, height);
	}
}
