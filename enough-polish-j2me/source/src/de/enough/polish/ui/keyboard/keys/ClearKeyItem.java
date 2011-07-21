//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.keys;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.keyboard.KeyItem;
import de.enough.polish.ui.keyboard.Keyboard;
import de.enough.polish.ui.keyboard.view.KeyboardView;

//#ifdef polish.i18n.useDynamicTranslations
import de.enough.polish.util.Locale;
//#endif
/**
 * A special key item implementation for a clear key
 * to clear the contents of a KeyboardView
 * <pre>
 * history
 *        21-Jan-2010 - David added translations
 * </pre>
 * @author Andre
 * @author David
 */
public class ClearKeyItem extends KeyItem {

        public static final String DISPLAY_NAME=
        //#ifdef polish.i18n.useDynamicTranslations
                //# Locale.get("polish.keyboard.clr");
        //#elifdef polish.keyboard.clr:defined
            //#= "${polish.keyboard.clr}";
        //#else
           "CLR";
        //#endif
	
	/**
	 * Creates a new ClearKeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 */
	public ClearKeyItem(Keyboard keyboard, String position) {
		this(keyboard, position, null);
	}
	
	/**
	 * Creates a new ClearKeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 * @param style the style
	 */
	public ClearKeyItem(Keyboard keyboard, String position, Style style) {
                super(keyboard, position, DISPLAY_NAME, style);
	}

	protected void apply(boolean doubleclick) {
		Keyboard keyboard = getKeyboard();
		KeyboardView view = keyboard.getKeyboardView();
		String text = view.getText();
		
		if(text.length() > 0)
		{
			view.setText("");
		}
	}
}
