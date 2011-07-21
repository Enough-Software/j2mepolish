//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.keys;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.keyboard.KeyItem;
import de.enough.polish.ui.keyboard.Keyboard;

//#ifdef polish.i18n.useDynamicTranslations
	import de.enough.polish.util.Locale;
//#endif

/**
 * A special key item implementation to shift the keys of a keyboard
 * <pre>
 * history
 *        21-Jan-2010 - David added translations
 * </pre>
 * @author Andre
 * @author David
 */
public class ShiftKeyItem extends KeyItem {
        public static final String DISPLAY_NAME=
        //#ifdef polish.i18n.useDynamicTranslations
                //# Locale.get("polish.keyboard.shift");
        //#elifdef polish.keyboard.shift:defined
            //#= "${polish.keyboard.shift}";
        //#else
           "S";
        //#endif

	/**
	 * Creates a new DeleteKeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 */
	public ShiftKeyItem(Keyboard keyboard, String position) {
		this(keyboard, position, null);
	}
	
	/**
	 * Creates a new ShiftKeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 * @param style the style
	 */
	public ShiftKeyItem(Keyboard keyboard, String position, Style style) {
		super(keyboard, position, DISPLAY_NAME, style);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.keyboard.KeyItem#apply(boolean)
	 */
	protected void apply(boolean doubleclick) {
		getKeyboard().shift(!getKeyboard().isShift());
	}
}
