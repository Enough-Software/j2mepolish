//#condition polish.usePolishGui && polish.android
package de.enough.polish.android.lcdui;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import de.enough.polish.android.midlet.MidletBridge;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;

public class AndroidTextField extends EditText
implements AndroidItemView, View.OnFocusChangeListener, View.OnTouchListener
{
	
	 //private static final int MODE_SHIFT = 30;
     //private static final int MODE_MASK  = 0x3 << MODE_SHIFT;


	private final TextField textField;
	private int cursorPosition;
	private boolean isNumericPassword;
	private float yPosAtPointerPress;
	private int scrollOffsetAtPointerPress;

	public AndroidTextField(TextField textField) {
		super(MidletBridge.getInstance());
		this.textField = textField;
		setSingleLine( false ); // please wrap text
		setHorizontallyScrolling(false); // please wrap text
		setBackgroundDrawable(null); // no background
		setPadding(0, 0, 0, 0); // no padding for me
		setCompoundDrawables(null, null, null, null); // remove borders
		setOnFocusChangeListener(this);
		applyTextField();
		setOnTouchListener(this);
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
		setFilters( new InputFilter[] { new InputFilter.LengthFilter(field.getMaxSize()) } );
		//TODO apply help text (setHint(), setHintColor() when help texts are being used
		//#if polish.TextField.showHelpText
			if (field.getHelpText() != null) {
				setHint(field.getHelpText());
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
			//TODO allow password fields
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
	
	
	
	@Override
	public InputConnection onCreateInputConnection(EditorInfo info) {
		if (this.isNumericPassword) {
			info.inputType = InputType.TYPE_CLASS_NUMBER;
			if (focusSearch(FOCUS_DOWN) != null) {
				// An action has not been set, but the enter key will move to
				// the next focus, so set the action to that.
				info.imeOptions |= EditorInfo.IME_ACTION_NEXT;
			} else {
				// An action has not been set, and there is no focus to move
				// to, so let's just supply a "done" action.
				info.imeOptions |= EditorInfo.IME_ACTION_DONE;
			}
			Editable editable = getText();
			InputConnection ic = new NumericPasswordInputConnection(editable, this);
			info.initialSelStart = Selection.getSelectionStart(editable);
			info.initialSelEnd = Selection.getSelectionEnd(editable);
			return ic;

		}
		return super.onCreateInputConnection(info);
	}

	public TextField getTextField() {
		return this.textField;
	}
	
//	@Override
//	public void draw(android.graphics.Canvas arg0) {
//		//System.out.println("DRAWING ANDROID TEXT FIELD, text=" + getText() + ", color=" + Integer.toHexString(getCurrentTextColor()) + ", pos=" + getLeft() + ", " + getTop() + " - " + getRight() + ", " + getBottom() + ", focus=" + AndroidDisplay.getInstance().findFocus() + "/" +  AndroidDisplay.getInstance().getFocusedChild() + ", childCount=" + AndroidDisplay.getInstance().getChildCount());
//		//System.out.println("Drawing textfield " + this.textField + " (" + System.currentTimeMillis() + ")");
//		System.out.println( "draw edit " + getText() );
//		super.draw(arg0);
//	}
//
//	@Override
//	public void bringToFront() {
//		System.out.println("ANDROID TEXT FIELD: bringToFront()");
//		super.bringToFront();
//		
//	}

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
		//#debug
		System.out.println("Focus changed for " + view + ": hasFocus=" + hasFocus);
		if (hasFocus && !this.textField.isFocused()) {
			this.textField.getScreen().focus(this.textField);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.TextView#onTextChanged(java.lang.CharSequence, int, int, int)
	 */
	protected void onTextChanged(CharSequence text, int start, int before, int after) {
		//#debug
		System.out.println("onTextChanged: text=[" + text + "]");
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
	

//	@Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		System.out.println("AndroidTextField.onMeasure: lineCount=" + getLineCount() + ", lineHeight=" + getLineHeight() + ", layout=" + getLayout());
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		//System.out.println("onMasure: width: avail=" + width + ", measured=" + getMeasuredWidth());
//		System.out.println("onMeasure: height: avail=" + height + ", measured=" + getMeasuredHeight() );
//		
//	}
//
//	@Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		System.out.println("on layout: " + l+  ", " + t + ", " + r + ", " + b + ") for " + getPolishItem());
//		super.onLayout(changed, l, t, r, b );
//	}
	
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

	
	
	private static class NumericPasswordInputConnection 
	extends BaseInputConnection 
	{
		
		private final Editable editable;

		public NumericPasswordInputConnection( Editable editable, View targetView) {
			super( targetView, true );
			this.editable = editable;
		}
		
		/*
		 * (non-Javadoc)
		 * @see android.view.inputmethod.BaseInputConnection#getEditable()
		 */
		public Editable getEditable() {
			return this.editable;
		}
	}



}


