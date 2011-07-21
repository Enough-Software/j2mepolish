//#condition polish.usePolishGui && polish.android
package de.enough.polish.android.lcdui;

import android.view.KeyCharacterMap;
import android.view.KeyEvent;

public class DisplayUtil {
	
	public static final int KEY_MENU = -111;
	public static final int KEY_CLEAR = -8;
	
	KeyCharacterMap characterMap;
	
	public DisplayUtil(int deviceId)
	{
		this.characterMap = KeyCharacterMap.load(deviceId);
	}
	
	public int handleKey(int keyCode, KeyEvent event, Canvas canvas) {
		int flags = event.getFlags();
		if (keyCode == KeyEvent.KEYCODE_MENU 
				//#if polish.javaplatform >= Android/1.5
				&& ((flags & KeyEvent.FLAG_FROM_SYSTEM) == KeyEvent.FLAG_FROM_SYSTEM) 
			//#endif
		){
			return KEY_MENU;
		}
		if (keyCode == KeyEvent.KEYCODE_CLEAR || keyCode == KeyEvent.KEYCODE_DEL) {// && ((flags & KeyEvent.FLAG_SOFT_KEYBOARD) == KeyEvent.FLAG_SOFT_KEYBOARD)) {
			return KEY_CLEAR;
		}
		int key = event.getKeyCode();
		int meta = event.getMetaState();
		
		int ascii = this.characterMap.get(key, meta);
		//#debug
		System.out.println("Mapped keycode '"+keyCode+"' to ascii char '"+ascii+"'");
		// TODO: Refactor this code and make it more readable. Create a keycode map which returns ME keycodes.
		boolean isClearKey = isClear(keyCode);
		boolean isReturnKey = isReturn(keyCode);
		boolean isMenuKey = isMenu(keyCode);
		int gameActionKey = canvas.getGameAction(keyCode);
		boolean isGameActionKey = gameActionKey != 0;
		boolean isVolumeUp = isVolumeUp(keyCode);
		boolean isVolumeDown = isVolumeDown(keyCode);
		boolean notAsciiKey = ascii == 0;
		if( notAsciiKey && (isGameActionKey || isClearKey || isReturnKey ||	isMenuKey || isVolumeUp || isVolumeDown)
		){
			if(isVolumeUp) {
				// TODO: Remove the magic numbers.
				return -13;
			}
			if(isVolumeDown) {
				return -14;
			}
			return keyCode;
		}
		else
		{
			return ascii;
		}
	}
	
	private boolean isVolumeUp(int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			return true;
		}
		return false;
	}
	private boolean isVolumeDown(int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			return true;
		}
		return false;
	}	

	public boolean isClear(int keyCode)
	{
		//#ifdef polish.key.ClearKey:defined
		//#= if (keyCode == ${polish.key.ClearKey})
		//# {
		//#		return true; 
		//# }
		//#endif
		
		return false;
	}
	
	public boolean isReturn(int keyCode)
	{
		//#if polish.key.ReturnKey:defined
		//#= if (keyCode == ${polish.key.ReturnKey})
		//# {
		//#		return true; 
		//# }
		//#endif
			
		return false;
	}
	
	public boolean isMenu(int keyCode)
	{
		//#if polish.key.Menu:defined
		//#= if (keyCode == ${polish.key.Menu})
		//# {
		//#		return true; 
		//# }
		//#endif
			
		return false;
	}
}
