//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.keys;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.keyboard.KeyItem;
import de.enough.polish.ui.keyboard.Keyboard;
//#ifdef polish.i18n.useDynamicTranslations
import de.enough.polish.util.Locale;
//#endif
/**
 * A special key item implementation for a blank key item
 * which does nothing and can be used as a blank filling in 
 * a keyboard layout
 * <pre>
 * history
 *        21-Jan-2010 - David added translations
 * </pre>
 * @author Andre
 * @author David
 */
public class BlankItem extends KeyItem {
       //I am pretty sure this isn't needed but just encase we can add it.
        public static final String DISPLAY_NAME=
        //#ifdef polish.i18n.useDynamicTranslations
             //# Locale.get("polish.keyboard.blank");
        //#elifdef polish.keyboard.blank:defined
            //#= "${polish.keyboard.blank}";
        //#else
           " ";
        //#endif

	/**
	 * Creates a new BlankItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 */
	public BlankItem(Keyboard keyboard, String position) {
		this(keyboard, position, null);
	}
	
	/**
	 * Creates a new BlankItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 * @param style the style
	 */
	public BlankItem(Keyboard keyboard, String position, Style style) {
		super(keyboard, position, DISPLAY_NAME, style);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.keyboard.KeyItem#apply(boolean)
	 */
	protected void apply(boolean doubleclick) {
		// do nothing
	}
}
