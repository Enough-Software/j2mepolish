//#condition polish.usePolishGui && polish.android
package de.enough.polish.android.lcdui;

import java.util.ArrayList;

import android.database.DataSetObserver;
import android.graphics.Rect;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;
import de.enough.polish.android.midlet.MidletBridge;
import de.enough.polish.ui.ChoiceTextField;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;

public class AndroidAutoCompleteTextFieldImpl extends AutoCompleteTextView
implements View.OnTouchListener, AndroidTextField
{
	
	private final ChoiceTextField textField;
	private int cursorPosition;
	private boolean isNumericPassword;
	private float yPosAtPointerPress;
	private int scrollOffsetAtPointerPress;

	public AndroidAutoCompleteTextFieldImpl(ChoiceTextField textField) {
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
	}
	
	
	private void applyTextField() {
		ChoiceTextField field = this.textField;
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


	public void setChoices(String[] availableChoices) {
		ChoiceAdapter adapter = new ChoiceAdapter(availableChoices, this);
		setAdapter(adapter);
	}

	private static class ChoiceAdapter extends BaseAdapter implements Filterable, ListAdapter {
		
		private final ArrayList<String> originalChoices;
		private ArrayList<String> selectedChoices;
		private Filter filter;

		public ChoiceAdapter(String[] availableChoices, AndroidAutoCompleteTextFieldImpl parent) {
			this.originalChoices = new ArrayList<String>(availableChoices.length);
			for (int i = 0; i < availableChoices.length; i++) {
				String choice = availableChoices[i];
				this.originalChoices.add(choice);
			}
			this.selectedChoices = new ArrayList<String>(availableChoices.length);
		}

		public int getCount() {
			return this.selectedChoices.size();
		}

		public Object getItem(int i) {
			return this.selectedChoices.get(i);
		}

		public long getItemId(int i) {
			return i;
		}

		public int getItemViewType(int i) {
			return 0;
		}

		public View getView(int i, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new TextView(MidletBridge.getInstance());
			}
			((TextView)convertView).setText(this.selectedChoices.get(i));
			return convertView;
		}

		public int getViewTypeCount() {
			return 1;
		}

		public boolean hasStableIds() {
			return false;
		}

		public boolean isEmpty() {
			return (this.selectedChoices.size() == 0);
		}

//		public void registerDataSetObserver(DataSetObserver observer) {
//			// TODO Auto-generated method stub
//			System.out.println("registering " + observer);
//			
//			
//		}
//
//		public void unregisterDataSetObserver(DataSetObserver observer) {
//			// TODO Auto-generated method stub
//			System.out.println("unregistering " + observer);
//			
//		}

		public boolean areAllItemsEnabled() {
			return true;
		}

		public boolean isEnabled(int i) {
			return true;
		}

		public Filter getFilter() {
			if (this.filter == null) {
				this.filter = new ChoicesFilter();
			}
			return this.filter;
		}
		
		private class ChoicesFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence prefix) {
				FilterResults results = new FilterResults();
				if (prefix == null || prefix.length() == 0) {
	                ArrayList<String> list = new ArrayList<String>(ChoiceAdapter.this.originalChoices);
	                results.values = list;
	                results.count = list.size();
	            } else {
	                String prefixString = prefix.toString().toLowerCase();

	                final ArrayList<String> newValues = new ArrayList<String>();

	                final int count = ChoiceAdapter.this.originalChoices.size();
	                for (int i = 0; i < count; i++) {
	                    final String value = ChoiceAdapter.this.originalChoices.get(i);
	                    //final String valueText = value.toString().toLowerCase();

	                    // First match against the whole, non-splitted value
	                    if (value.startsWith(prefixString)) {
	                        newValues.add(value);
	                    } 
//	                    else {
//	                        final String[] words = valueText.split(" ");
//	                        final int wordCount = words.length;
//
//	                        // Start at index 0, in case valueText starts with space(s)
//	                        for (int k = 0; k < wordCount; k++) {
//	                            if (words[k].startsWith(prefixString)) {
//	                                newValues.add(value);
//	                                break;
//	                            }
//	                        }
//	                    }
	                }

	                results.values = newValues;
	                results.count = newValues.size();
	            }

	            return results;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				ChoiceAdapter.this.selectedChoices = (ArrayList<String>) results.values;
				if (results.count > 0) {
				    notifyDataSetChanged();
				} else {
				    notifyDataSetInvalidated();
				}
			}
			
		}
	
	}

}

