//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.keys;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.keyboard.KeyItem;
import de.enough.polish.ui.keyboard.Keyboard;
//#ifdef polish.i18n.useDynamicTranslations
import de.enough.polish.util.Locale;
//#endif
/**
 * A special key item implementation to switch between the modes
 * <pre>
 * history
 *        21-Jan-2010 - David added translations
 * </pre>
 * of a keyboard
 * @author Andre
 *
 */
public class ModeKeyItem extends KeyItem {
	public static final String DISPLAY_NAME=
        //#ifdef polish.i18n.useDynamicTranslations
                //# Locale.get("polish.keyboard.mode");
        //#elifdef polish.keyboard.mode:defined
            //#= "${polish.keyboard.mode}";
        //#else
           "M";
        //#endif
	/**
	 * Creates a new ModeKeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 */
	public ModeKeyItem(Keyboard keyboard, String position) {
		this(keyboard,position, null);
	}
	
	/**
	 * Creates a new ModeKeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 * @param style the style
	 */
	public ModeKeyItem(Keyboard keyboard, String position, Style style) {
		super(keyboard, position, DISPLAY_NAME, style);
	}

	protected void apply(boolean doubleclick) {
		Keyboard keyboard = getKeyboard();
		
		keyboard.setNextMode();
	}
}
