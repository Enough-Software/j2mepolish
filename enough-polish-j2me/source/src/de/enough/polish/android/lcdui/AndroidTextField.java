//#condition polish.usePolishGui && polish.android
package de.enough.polish.android.lcdui;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;

public interface AndroidTextField {

	void setStyle(Style style);

	TextField getTextField();

	Item getPolishItem();

	int getCursorPosition();

	void setCursorPosition(int pos);
	
	void moveCursor(int cursorAdjustment);
	
	void setTextKeepState(CharSequence text);
	
	CharSequence getText();
	
	void applyTextField();

}