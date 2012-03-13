//#condition polish.usePolishGui && polish.android
package de.enough.polish.android.lcdui;

import android.graphics.Rect;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.TextView;
import de.enough.polish.android.midlet.MidletBridge;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;

public class AndroidTextFieldImpl extends EditText
implements View.OnTouchListener, AndroidTextField, TextView.OnEditorActionListener
{
	private final TextField textField;
	private int cursorPosition;
	private boolean isNumericPassword;
	private float yPosAtPointerPress;
	private int scrollOffsetAtPointerPress;
	private boolean isSingleLine;

	public AndroidTextFieldImpl(TextField textField) {
		super(MidletBridge.getInstance());
		this.textField = textField;
		setSingleLine( false ); // please wrap text
		setHorizontallyScrolling(false); // please wrap text
		setBackgroundDrawable(null); // no background
		setPadding(0, 0, 0, 0); // no padding for me
		setCompoundDrawables(null, null, null, null); // remove borders
		applyTextField();
		setOnTouchListener(this);
		setLineSpacing( 0F, 1F);
		setOnEditorActionListener(this);
	}
	
	
	private void applyTextField() {
		TextField field = this.textField;
		Style style = field.getStyle();
		if (style != null) {
			setStyle( style );
		}
		setFilters( new InputFilter[] { new InputFilter.LengthFilter(field.getMaxSize()) } );
		//TODO apply setHintColor() when help texts are being used
		//#if polish.TextField.showHelpText
			if (field.getHelpText() != null) {
				setHint(field.getHelpText());
			}
		//#endif
		//#if polish.css.text-wrap
			if (style != null) {
				Boolean isWrapBool = style.getBooleanProperty("text-wrap");
				if ((isWrapBool != null) && (!isWrapBool.booleanValue())) {
					setSingleLine();
					this.isSingleLine = true;
				}
			} 
		//#endif
		this.isNumericPassword = false;
		int type =  InputType.TYPE_TEXT_FLAG_MULTI_LINE;
		if (field.isConstraintsPhoneNumber()) {
			type |= InputType.TYPE_CLASS_PHONE;
		} else if (field.isConstraintsNumeric()) {
			if (field.isConstraintsPassword()) {
				this.isNumericPassword = true;
				type |= InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
			} else {
				// normal numeric field:
				type |= InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED;
			}
		} else if (field.isConstraintsDecimal()) {
			type |= InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL;
		} else if (field.isConstraintsEmail()) {
			type |= InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
		} else {
			type |= InputType.TYPE_CLASS_TEXT;
			if (field.isConstraintsPassword()) {
				type |= InputType.TYPE_TEXT_VARIATION_PASSWORD;
			}
		}
		setInputType( type );
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.android.lcdui.AndroidEditView#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle( Style style ) {
		Font font = (Font)(Object)style.getFont();
		if (font == null) {
			font = Font.getDefaultFont();
		}
		setTypeface( font.getTypeface() );
		setTextSize( TypedValue.COMPLEX_UNIT_PX, font.getTextSize() - 0.352F ); 
		setTextColor( 0xff000000 | style.getFontColor() );
		//setLineSpacing( (float)style.getPaddingVertical(100), 1F);
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
			boolean onKeyDownHandled = CanvasBridge.current().onKey( this, keyCode, event );
			return onKeyDownHandled;
		}
		return processed;
	}

	
	
	
	@Override
	public InputConnection onCreateInputConnection(EditorInfo info) {
		InputConnection inputConnection = super.onCreateInputConnection(info);
		if (this.isNumericPassword) {
			info.inputType = InputType.TYPE_CLASS_NUMBER;
		}
		return inputConnection;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.android.lcdui.AndroidEditView#getTextField()
	 */
	public TextField getTextField() {
		return this.textField;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.android.lcdui.AndroidEditView#getPolishItem()
	 */
	public Item getPolishItem() {
		return this.textField;
	}


	/*
	 * (non-Javadoc)
	 * @see android.widget.TextView#onFocusChanged(boolean, int, android.graphics.Rect)
	 */
	protected void onFocusChanged (boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		//#debug
		System.out.println("Focus changed for " + this.textField + ": hasFocus=" + gainFocus);
		if (gainFocus && !this.textField.isFocused()) {
			this.textField.getScreen().focus(this.textField);
		}
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.TextView#onTextChanged(java.lang.CharSequence, int, int, int)
	 */
	protected void onTextChanged(CharSequence text, int start, int before, int after) {
		//#debug
		System.out.println("onTextChanged: text=[" + text + "], start=" + start + ", before=" + before + ", after=" + after);
		this.cursorPosition = start + after;
		if (this.isNumericPassword && text != null) {
			StringBuffer buffer = new StringBuffer(text.length());
			boolean foundInvalidCharacter = false;
			for (int charIndex=0; charIndex<text.length(); charIndex++) {
				char c = text.charAt(charIndex);
				if (Character.isDigit(c)) {
					buffer.append(c);
				} else {
					foundInvalidCharacter = true;
				}
			}
			if (foundInvalidCharacter) {
				text = buffer.toString();
				setTextKeepState(text);
			}
		}
		if (text != null && this.textField != null) {
			this.textField.setString(text.toString());
			this.textField.notifyStateChanged();
		}
		super.onTextChanged(text, start, before, after);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.TextView#onSelectionChanged(int, int)
	 */
	protected void onSelectionChanged(int selStart, int selEnd) {
		super.onSelectionChanged(selStart, selEnd);
		if (selStart == selEnd) {
			this.cursorPosition = selStart;
		}
	}

	public void moveCursor(int cursorAdjustment) {
		//System.out.println("moving by " + cursorAdjustment + ", nextCursorPos=" + (this.cursorPosition + cursorAdjustment));
		setCursorPosition( this.cursorPosition + cursorAdjustment );
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.android.lcdui.AndroidEditView#getCursorPosition()
	 */
	public int getCursorPosition() {
		return this.cursorPosition;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.android.lcdui.AndroidEditView#setCursorPosition(int)
	 */
	public void setCursorPosition(int pos) {
		if (pos <= getText().length()) {
			try {
				setSelection(pos);
			} catch (IndexOutOfBoundsException e) {
				//#debug warn
				System.out.println("unable to set cursor position" + e);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	public boolean onTouch(View view, MotionEvent event) {
		// we want to allow scrolling while the user touches the native EditField,
		// at the same time we don't want to interfere with the MotionEvent processing,
		// so we always return false (=we have not handled the event)
		//System.out.println("AndroidTextField.onTouch " + event);
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			Screen screen = AndroidDisplay.getInstance().getCurrentPolishScreen();
			if (screen != null) {
				this.yPosAtPointerPress = event.getRawY();
				this.scrollOffsetAtPointerPress = screen.getScrollYOffset();
			}
		}
		if (action == MotionEvent.ACTION_MOVE) {
			Screen screen = AndroidDisplay.getInstance().getCurrentPolishScreen();
			if (screen != null) {
				int diff = (int) (this.yPosAtPointerPress - event.getRawY());
				int newOffset = this.scrollOffsetAtPointerPress - diff;
				screen.setScrollYOffset(newOffset, false);
				AndroidDisplay.getInstance().invalidate();
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.TextView.OnEditorActionListener#onEditorAction(android.widget.TextView, int, android.view.KeyEvent)
	 */
	public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
		if ((actionId == EditorInfo.IME_ACTION_UNSPECIFIED) || (actionId == EditorInfo.IME_ACTION_NONE)) {
			if (this.textField.getDefaultCommand() != null) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					this.textField.getDefaultCommand().commandAction(this.textField, this.textField.getScreen());
				}
				return true;
			}
			if (this.isSingleLine) {
				return true;
			}
		}
		return false;
	}

}


