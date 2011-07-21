//#condition polish.usePolishGui && polish.TextField.usePredictiveInput && polish.TextField.useDirectInput && !(polish.blackberry || polish.android)

package de.enough.polish.predictive;

import javax.microedition.rms.RecordStoreException;

import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.WrappedText;

public abstract class TextBuilder {

	/**
	 * Indicates that the caret should be shown to the left of current element
	 */
	public static final int ALIGN_LEFT = 0;

	/**
	 * Indicates that the caret should be shown to the right of current element
	 * and that results should be shown. This value only applies to instances of
	 * TextElement carrying a TrieReader
	 */
	public static final int ALIGN_FOCUS = 1;

	/**
	 * Indicates that the caret should be shown to the right of current element.
	 */
	public static final int ALIGN_RIGHT = 2;

	/**
	 * Indicates that the user pressed the DOWN button and wishes to jump to the
	 * next line.
	 */
	public static final int JUMP_PREV = 0;

	/**
	 * Indicates that the user pressed the UP button and wishes to jump to the
	 * previous line.
	 */
	public static final int JUMP_NEXT = 1;

	protected ArrayList textElements = null;

	protected int element;

	protected int align;

	protected int mode;

	protected int caret;

	protected StringBuffer text = null;

	/**
	 * Initializes the <code>TextElement</code> array, set the current element
	 * to -1, the align for the current element to <code>ALIGN_LEFT</code> and
	 * the current input mode to <code>MODE_FIRST_UPPERCASE</code>.
	 */
	public TextBuilder(int textSize) {
		this.textElements = new ArrayList();
		this.element = -1;
		this.align = ALIGN_LEFT;
		this.mode = TextField.MODE_FIRST_UPPERCASE;
		this.caret = 0;

		this.text = new StringBuffer();
	}

	public void keyNum(int keyCode,PredictiveReader reader) throws RecordStoreException {
		if (isString(0) || this.align == ALIGN_LEFT
				|| this.align == ALIGN_RIGHT) {
			addReader(reader);
		}

		getReader().keyNum(keyCode);
		

		if (getTextElement().isWordFound())
		{
			if (this.mode == TextField.MODE_FIRST_UPPERCASE)
				this.mode = TextField.MODE_LOWERCASE;
			
			getTextElement().keyNum(keyCode, this.mode);
			getTextElement().setResults();
		}
	}
	
	public void keySpace() {
		if (!isString(0))
			getTextElement().convertReader();

		addString(" ");
	}
	
	public boolean hasText()
	{
		return (this.textElements.size() > 0 && this.element != -1);
	}
	
	public abstract boolean keyClear() throws RecordStoreException;

	public void addWord(String string) {}

	/**
	 * Returns the <code>TrieReader</code> carried in the current
	 * <code>TextElement</code>. It must be checked previously via
	 * <code>isChar()</code> if the current element is a
	 * <code>TrieReader</code>
	 * 
	 * @return the instance of <code>TrieReader</code> carried in the current
	 *         <code>TextElement</code>
	 */
	public PredictiveReader getReader() {
		return (PredictiveReader) getTextElement().getElement();
	}

	public String getString() {
		return (String) getTextElement().getElement();
	}

	/**
	 * Returns the current <code>TextElement</code>
	 * 
	 * @return the current <code>TextElement</code>
	 */
	public TextElement getTextElement() {
		return getTextElement(this.element);
	}

	/**
	 * Returns the align of the current <code>TextElement</code>.
	 * 
	 * @return the align of the current <code>TextElement</code>
	 */
	public int getAlign() {
		return this.align;
	}

	/**
	 * Sets the align of the current <code>TextElement</code>
	 * 
	 * @param currentAlign
	 *            the align of the current <code>TextElement</code>
	 */
	public void setAlign(int currentAlign) {
		this.align = currentAlign;
	}

	/**
	 * Returns the current caret position
	 * 
	 * @return the current caret position
	 */
	public int getCaret() {
		return this.caret;
	}

	/**
	 * Returns the TextElement at the given index
	 * 
	 * @return the TextElement at the given index
	 */
	private TextElement getTextElement(int index) {
		if (this.textElements.size() > 0)
		{
			return (TextElement) this.textElements.get(index);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Adds an instance of <code>TextElement</code> to
	 * <code>textElements</code> in compliance with the current align.
	 * <p>
	 * If the align is <code>ALIGN_LEFT</code>, the element is inserted at
	 * the current element position
	 * </p>
	 * <p>
	 * If the align is <code>ALIGN_FOCUS</code>, the element is inserted
	 * behind the current element position
	 * </p>
	 * <p>
	 * If the align is <code>ALIGN_RIGHT</code>, the element is inserted
	 * behind the current element position
	 * </p>
	 * The index of the current element is set eventually to the position the
	 * new element is inserted at.
	 * 
	 * @param textElement the <code>TextElement</code> to add
	 */
	protected void addElement(TextElement textElement) {
		if (this.element >= 0) {
			if (this.align == ALIGN_LEFT)
				this.textElements.add(this.element, textElement);

			if (this.align == ALIGN_FOCUS)
				this.textElements.add(this.element + 1, textElement);

			if (this.align == ALIGN_RIGHT)
				this.textElements.add(this.element + 1, textElement);

			if ((this.align == ALIGN_FOCUS || this.align == ALIGN_RIGHT)
					&& this.element < this.textElements.size())
				this.element++;
		} else {
			this.textElements.add(0, textElement);
			this.element = 0;
		}
	}
	
	/**
	 * Retrieves the line the caret is positioned.
	 * 
	 * @param textLines
	 *            the text lines of the field
	 * @return the index of the line
	 */
	public int getElementLine(WrappedText textLines) {
		int caretPosition = this.getCaret();
		int length = 0;
		int index = 0;

		for (index = 0; index < textLines.size(); index++) {
			length += textLines.getLine(index).length();

			if (length >= caretPosition - 1)
				return index;
		}

		return textLines.size() - 1;
	}
	
	/**
	 * Retrieves the caret position if a the caret should be positioned in the previous or the next line
	 * 
	 * @param jumpDirection
	 *            the direction of the caret jump
	 * @return the caret position
	 */
	public int getJumpPosition(int jumpDirection, WrappedText textLines) {
		int caretLine = getElementLine(textLines);
		int caretPosition = getCaretPosition();

		if (jumpDirection == JUMP_PREV && caretLine > 0)
			return caretPosition - textLines.getLine(caretLine - 1).length();

		if (jumpDirection == JUMP_NEXT && caretLine != (textLines.size() - 1))
			return caretPosition + textLines.getLine(caretLine).length();

		return -1;
	}


	/**
	 * Retrieves the line the caret is positioned.
	 * 
	 * @param textLines
	 *            the text lines of the field
	 * @return the index of the line
	 */
	public int getElementLine(String[] textLines) {
		int caretPosition = this.getCaret();
		int length = 0;
		int index = 0;

		for (index = 0; index < textLines.length; index++) {
			length += textLines[index].length();

			if (length >= caretPosition - 1)
				return index;
		}

		return textLines.length - 1;
	}

	/**
	 * Retrieves the caret position if a the caret should be positioned in the previous or the next line
	 * 
	 * @param jumpDirection
	 *            the direction of the caret jump
	 * @return the caret position
	 */
	public int getJumpPosition(int jumpDirection, String[] textLines) {
		int caretLine = getElementLine(textLines);
		int caretPosition = getCaretPosition();

		if (jumpDirection == JUMP_PREV && caretLine > 0)
			return caretPosition - textLines[caretLine - 1].length();

		if (jumpDirection == JUMP_NEXT && caretLine != (textLines.length - 1))
			return caretPosition + textLines[caretLine].length();

		return -1;
	}

	/**
	 * Set the element next to caret position <code>offset</code> as the
	 * current element. If the position is closer to the start of the element
	 * the align for the current element is set to <code>ALIGN_LEFT</code>,
	 * otherwise to <code>ALIGN_RIGHT</code>
	 * 
	 * @param position
	 *            the caret position
	 */
	public void setCurrentElementNear(int position) {
		int length = 0;
		int lengthOffset = 0;

		int left = 0;
		int right = 0;

		for (int i = 0; i < this.textElements.size(); i++) {
			length = getTextElement(i).getLength();
			lengthOffset += length;

			if (lengthOffset > position) {
				this.element = i;

				left = lengthOffset - length;
				right = lengthOffset;
								
				if ((position - left) > (right - position))
				{
					this.align = ALIGN_RIGHT;
				}
				else
				{
					this.align = ALIGN_LEFT;
				}				
				
				return;
			}
		}

		this.element = this.textElements.size() - 1;
		this.align = ALIGN_RIGHT;

		return;
	}

	/**
	 * Creates a new <code>TextElement</code> carrying <code>string</code>
	 * 
	 * @param string
	 *            the string the <code>TextElement</code> should carry
	 */
	public abstract void addString(String string);

	/**
	 * Creates a new <code>TextElement</code> carrying <code>reader</code>
	 * 
	 * @param reader
	 *            the instance of <code>TrieReader</code> the
	 *            <code>TextElement</code> should carry
	 */
	public abstract void addReader(PredictiveReader reader);

	/**
	 * Deletes the current element
	 * 
	 * @return true, if an element was deleted, otherwise false
	 */
	public abstract boolean deleteCurrent();

	public boolean decreaseString() {
		if (this.isString(0)) {
			String element = getString();
			if (element.length() > 0) {
				element = element.substring(0,element.length() - 1);
				getTextElement().setElement(element);
				setAlign(ALIGN_RIGHT);
				return (element.length() > 0);
			} else
				return false;
		} else
			return true;
	}

	/**
	 * Increases the caret position to set in <code>getCaretPosition()</code>
	 * by setting the corresponding align and element index
	 * <p>
	 * If the current align is <code>ALIGN_LEFT</code>, the align is set to
	 * <code>ALIGN_FOCUS</code> (for <code>TrieReader</code> elements) or
	 * <code>ALIGN_RIGHT</code> (for characters)
	 * </p>
	 * <p>
	 * If the current align is <code>ALIGN_FOCUS</code> or
	 * <code>ALIGN_RIGHT</code> and the element is not the last element in
	 * <code>textElements</code>, the align is set to <code>ALIGN_LEFT</code>
	 * and the current element index is incremented by 1.
	 * </p>
	 */
	public void increaseCaret() {
		if (this.textElements.size() != 0) {
			if (this.align == ALIGN_FOCUS) {
				getTextElement().convertReader();

				this.align = ALIGN_RIGHT;
			} else if (this.element != this.textElements.size() - 1) {
				this.align = ALIGN_LEFT;
				this.element++;
			} else
				this.align = ALIGN_RIGHT;

		}
	}

	/**
	 * Decreases the caret position to set in <code>getCaretPosition()</code>
	 * by setting the corresponding align and element index
	 * <p>
	 * If the current align is <code>ALIGN_LEFT</code> and the current element
	 * is not the first element in <code>textElements</code>, the align is
	 * set to <code>ALIGN_FOCUS</code> (for <code>TrieReader</code>
	 * elements) or <code>ALIGN_LEFT</code> (for characters) and the current
	 * index gets decremented by 1.
	 * </p>
	 * <p>
	 * If the current align is <code>ALIGN_FOCUS</code>, the align is set to
	 * <code>ALIGN_LEFT</code>.
	 * </p>
	 * <p>
	 * If the current align is <code>ALIGN_RIGHT</code> or
	 * <code>ALIGN_RIGHT</code> and the element is not the last element in
	 * <code>textElements</code>, the align is set to
	 * <code>ALIGN_FOCUS</code> (for <code>TrieReader</code> elements) or
	 * <code>ALIGN_LEFT</code> (for characters)
	 * </p>
	 */
	public void decreaseCaret() {
		if (this.textElements.size() != 0) {
			switch (this.align) {
			case ALIGN_LEFT:
				if (this.element > 0) {
					if (!isString(-1))
						this.align = ALIGN_FOCUS;
					else
						this.align = ALIGN_LEFT;

					this.element--;
				}
				break;

			case ALIGN_FOCUS:
				this.align = ALIGN_LEFT;
				break;

			case ALIGN_RIGHT:
				if (isString(0))
					this.align = ALIGN_LEFT;
				else
					this.align = ALIGN_FOCUS;
				break;
			}
		}
	}

	/**
	 * Returns true, if the element at <code>offset</code> from the current
	 * element index is a character or a <code>TrieReader</code>
	 * 
	 * @param offset
	 *            the offset for the element
	 * @return true, if the element is a character, otherwise false
	 */
	public boolean isString(int offset) {
		if (this.textElements.size() > 0 && (this.element - offset) >= 0)
		{
			return getTextElement(this.element + offset).isString();
		}
		else
		{	
			// old code by andre:
			//return true;
			//TODO andre: check following line:
			return true; // JUST A GUESS
		}
	}

	/**
	 * Retrieves the field caret position by adding the string length of the
	 * elements preceding the current element. If the current align is
	 * <code>ALIGN_FOCUS</code> or <code>ALIGN_RIGHT</code>, the caret
	 * position is incremented by the string length of the current element
	 * 
	 * @return the field caret position
	 */
	public int getCaretPosition() {
		int result = 0;

		for (int i = 0; i < this.element; i++)
			result += getTextElement(i).getLength();

		if (this.textElements.size() > 0)
			if ((this.align == ALIGN_FOCUS || this.align == ALIGN_RIGHT)
					&& this.element >= 0)
				result += getTextElement(this.element).getLength();

		this.caret = result;
		return result;
	}

	/**
	 * Constructs a string to display in the field by concating the string of
	 * the elements in <code>textElements</code>
	 * 
	 * @return the constructed string
	 */
	public StringBuffer getText() {
		this.text.setLength(0);

		for (int i = 0; i < this.textElements.size(); i++) {
			TextElement element = getTextElement(i);
			Object object = element.getElement();

			if (object instanceof String)
				this.text.append((String) object);
			else if (object instanceof PredictiveReader)
				this.text.append(element.getSelectedWord());
		}

		return this.text;
	}

	/**
	 * Returns the character at the given index
	 * 
	 * @param index
	 *            the index of the character to return
	 * @return the character at the given index
	 */
	public char getTextChar(int index) {
		return this.text.charAt(index);
	}

	/**
	 * Returns the current input mode
	 * 
	 * @return the current input mode
	 */
	public int getMode() {
		return this.mode;
	}

	/**
	 * Sets the current input mode
	 * 
	 * @param mode
	 *            the input mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}
}
