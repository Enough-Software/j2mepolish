//#condition polish.usePolishGui && polish.android
package de.enough.polish.android.lcdui;

import de.enough.polish.android.midlet.MidletBridge;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class AndroidTextField extends EditText
implements AndroidItemView, View.OnFocusChangeListener
{
	
	 private static final int MODE_SHIFT = 30;
     private static final int MODE_MASK  = 0x3 << MODE_SHIFT;


	private final TextField textField;
	private int cursorPosition;

	public AndroidTextField(TextField textField) {
		super(MidletBridge.getInstance());
		this.textField = textField;
		applyTextField();
		setSingleLine( false ); // please wrap text
		setHorizontallyScrolling(false); // please wrap text
		setBackgroundDrawable(null); // no background
		setPadding(0, 0, 0, 0); // no padding for me
		setCompoundDrawables(null, null, null, null); // remove borders
		setOnFocusChangeListener(this);
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean processed = super.onKeyDown(keyCode, event);
		//#debug
		System.out.println("EditText.onKeyDown processed=" + processed+ ", keyCode=" + keyCode);
		if ( !processed 
				&& (keyCode == KeyEvent.KEYCODE_DPAD_UP 
						|| keyCode == KeyEvent.KEYCODE_DPAD_DOWN 
						|| keyCode == KeyEvent.KEYCODE_MENU 
						|| keyCode == KeyEvent.KEYCODE_BACK
				)
		) {
			//AndroidDisplay.getInstance().toggleTextInput(this.textField);
			return CanvasBridge.current().onKey( this, keyCode, event );
		}
		return processed;
	}

//	@Override
//	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
//		boolean processed = super.onKeyMultiple(keyCode, repeatCount, event);
//		System.out.println("EditText.onKeyMultiple processed=" + processed);
//		return processed;
//	}
//
//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		boolean processed = super.onKeyUp(keyCode, event);
//		System.out.println("EditText.onKeyUp processed=" + processed + ", keyCode=" + keyCode);
//		return processed;
//	}
	
	public void applyTextField() {
		TextField field = this.textField;
		Style style = field.getStyle();
		if (style != null) {
			Font font = (Font)(Object)style.getFont();
			if (font == null) {
				font = Font.getDefaultFont();
			}
			setTypeface( font.getTypeface() );
			setTextSize( TypedValue.COMPLEX_UNIT_PX, font.getTextSize() - 0.352F ); 
			setTextColor( 0xff000000 | style.getFontColor() );
			//setLineSpacing( (float)style.getPaddingVertical(100), 1F);
			setLineSpacing( 0F, 1F);
		}
		int type;
		if (field.isConstraintsPhoneNumber()) {
			type = InputType.TYPE_CLASS_PHONE;
		} else if (field.isConstraintsNumeric() || field.isConstraintsDecimal()) {
			type = InputType.TYPE_CLASS_NUMBER;
		} else {
			type = InputType.TYPE_CLASS_TEXT;
		}
		if (field.isConstraintsPassword()) {
			type |= InputType.TYPE_TEXT_VARIATION_PASSWORD;
		}
		setInputType( type );
	}
	
	
	public TextField getTextField() {
		return this.textField;
	}
	
	@Override
	public void draw(Canvas arg0) {
		//System.out.println("DRAWING ANDROID TEXT FIELD, text=" + getText() + ", color=" + Integer.toHexString(getCurrentTextColor()) + ", pos=" + getLeft() + ", " + getTop() + " - " + getRight() + ", " + getBottom() + ", focus=" + AndroidDisplay.getInstance().findFocus() + "/" +  AndroidDisplay.getInstance().getFocusedChild() + ", childCount=" + AndroidDisplay.getInstance().getChildCount());
		//System.out.println("Drawing textfield " + this.textField + " (" + System.currentTimeMillis() + ")");
		//System.out.println( "draw edit at " + getLeft() + ", " + getRight() + ", " + getTop() + ", " + getBottom() + ": " + getText() );
		super.draw(arg0);
	}

	@Override
	public void bringToFront() {
		System.out.println("ANDROID TEXT FIELD: bringToFront()");
		super.bringToFront();
		
	}

	public Item getPolishItem() {
		return this.textField;
	}

	public View getAndroidView() {
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
	 */
	public void onFocusChange(View view, boolean hasFocus) {
		System.out.println("Focus changed for " + view + ": hasFocus=" + hasFocus);
		if (hasFocus) {
			this.textField.getScreen().focus(this.textField);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.TextView#onTextChanged(java.lang.CharSequence, int, int, int)
	 */
	protected void onTextChanged(CharSequence text, int start, int before, int after) {
		super.onTextChanged(text, start, before, after);
		//System.out.println("onTextChanged: text=[" + text + "]");
		if (text != null && this.textField != null) {
			this.textField.setString(text.toString());
			this.textField.notifyStateChanged();
			requestLayout();
		}
	}
	
	protected void onSelectionChanged(int selStart, int selEnd) {
		super.onSelectionChanged(selStart, selEnd);
		if (selStart == selEnd) {
			this.cursorPosition = selStart;
		}
	}

	public void moveCursor(int cursorAdjustment) {
		System.out.println("moving by " + cursorAdjustment + ", nextCursorPos=" + (this.cursorPosition + cursorAdjustment));
		setCursorPosition( this.cursorPosition + cursorAdjustment );
	}
	
	public int getCursorPosition() {
		return this.cursorPosition;
	}

	public void setCursorPosition(int pos) {
		try {
			setSelection(pos);
		} catch (IndexOutOfBoundsException e) {
			//#debug warn
			System.out.println("unable to set cursor position" + e);
		}
	}
	
	

}
