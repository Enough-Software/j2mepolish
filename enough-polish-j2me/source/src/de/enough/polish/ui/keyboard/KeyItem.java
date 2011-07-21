//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.TextUtil;

/**
 * A key item to display keys in a keyboard
 * @author Andre
 *
 */
public class KeyItem extends Item 
{
	/**
	 * the index of the row of a key position in a keyboard
	 */
	public static final int POSITION_ROW = 0;
	
	/**
	 * the index of the index of a key position in a keyboard
	 */
	public static final int POSITION_INDEX = 1;
	
	/**
	 * a blank for undefined key values
	 */
	public static final char KEY_UNDEFINED = ' ';
	
	/**
	 * the double click interval
	 */
	static final int DOUBLECLICK_INTERVAL = 1000;
	
	/**
	 * the keyboard to which this key item belongs
	 */
	Keyboard keyboard;
	
	/**
	 * the key(s) of this key item
	 */
	String keys;
	
	/**
	 * the shifted (uppercase) key(s) of this key item 
	 */
	String keysUpper;
	
	/**
	 * the current key (for multiple characters in a key item)
	 */
	char currentKey; 
	
	/**
	 * the current index (for multiple characters in a key item)
	 */
	int currentIndex;
	
	/**
	 * the position in the keyboard of this key item
	 */
	int[] position;
	
	/**
	 * the width of the displayed key values
	 */
	int stringWidth;
	
	/**
	 * the height of the displayed key values
	 */
	int stringHeight;
	
	/**
	 * the font for the displayed key values
	 */
	Font font;
	
	/**
	 * the last time the key was used
	 */
	long lastTimeKeyReleased = -1;
	
	/**
	 * Creates a new KeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 * @param keys the keys
	 */
	public KeyItem(Keyboard keyboard, String position, String keys) {
		this(keyboard, position, keys, null);
	}

	/**
	 * Creates a new KeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 * @param keys the keys
	 * @param style the style
	 */
	public KeyItem(Keyboard keyboard, String position, String keys, Style style) {
		super(style);
		
		setAppearanceMode(Item.INTERACTIVE);
		
		this.keyboard = keyboard;
		
		this.keys = keys;
		
		this.currentIndex = 0;
		
		this.position = getPosition(position);
	}
	
	/**
	 * Parses the position of this key item from the position string (e.g. 0,1)
	 * @param positionString the position string
	 * @return an integer array of the row and index of this key item in its keyboard
	 */
	int[] getPosition(String positionString) {
		String[] splitted = TextUtil.split(positionString, ',');
		
		int[] position = new int[2];
		
		position[POSITION_ROW] = Integer.parseInt(splitted[POSITION_ROW]);
		position[POSITION_INDEX] = Integer.parseInt(splitted[POSITION_INDEX]);
		
		return position;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector() {
		return null;
	}

	/**
	 * Returns the font of the style. If the font of the style
	 * is null return the default font.
	 * @return the font 
	 */
	public Font getFont() {
		if (this.font == null) {
			if (this.style != null) {
				this.font = this.style.getFont();
			}
			if (this.font == null) {
				this.font = Font.getDefaultFont();
			}
		}
		return this.font;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth,
			int availHeight) {
		// get the string width
		this.stringWidth = getFont().stringWidth(getKeys(true));
		// set the content height to the used font height
		this.contentHeight = getFont().getHeight();
		// set the content width to the calculated string width
		this.contentWidth = this.stringWidth;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {
		// set the color according to the style
		if (this.style != null) {
			g.setColor(this.style.getFontColor());
		} else {
			g.setColor(0x000000);
		}

		// draw the keys
		g.drawString(getKeys(this.keyboard.isShift()), x, y, Graphics.LEFT | Graphics.TOP);
	}
	
	/**
	 * Returns the (shifted) keys for this key item
	 * @param shift true if the shifted keys should be returned
	 * @return the (shifted) keys for this key item
	 */
	String getKeys(boolean shift) {
		if(shift) {
			if(this.keysUpper == null) {
				this.keysUpper = this.keys.toUpperCase();
			}
			return this.keysUpper;
		}
		else {
			return this.keys;
		}
	}
	
	/**
	 * Returns the (shifted) current key
	 * @param shift true if the shifted current key should be returned
	 * @return the (shifted) current key
	 */
	public char getCurrentKey(boolean shift) {
		return getKeys(shift).charAt(this.currentIndex);
	}
	
	/**
	 * Returns the keyboard
	 * @return the keyboard
	 */
	protected Keyboard getKeyboard() {
		return this.keyboard;
	}
	
	/**
	 * Returns the row
	 * @return the row
	 */
	public int getRow() {
		return this.position[POSITION_ROW];
	}
	
	/**
	 * Returns the index
	 * @return the index
	 */
	public int getIndex() {
		return this.position[POSITION_INDEX];
	}
	
	/**
	 * Applies the current key to the keyboard
	 * @param doubleclick 
	 */
	protected void apply(boolean doubleclick) {
		char key = getCurrentKey(this.keyboard.isShift());
		
		this.keyboard.apply(key, doubleclick, isMultiple());
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#toString()
	 */
	public String toString() {
		return "KeyItem [" + this.keys + " : " + getRow() + "/" + getIndex() + "]";
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerReleased(int, int)
	 */
	protected boolean handlePointerReleased(int relX, int relY) {
		if(this.isInItemArea(relX, relY)) {
			super.handlePointerReleased(relX, relY);
		
			boolean multipleClick = isMultipleClick();
			
			if(multipleClick)
			{
				this.currentIndex = (this.currentIndex + 1) % this.keys.length();
			} else {
				this.currentIndex = 0;
			}
			
			apply(multipleClick);
			
			this.keyboard.lastKey = this;
			
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @return
	 */
	boolean isMultipleClick() {
		long time = System.currentTimeMillis();
		if(this.lastTimeKeyReleased != -1) {
			long diff = time - this.lastTimeKeyReleased;
			this.lastTimeKeyReleased = time;
			if(diff < DOUBLECLICK_INTERVAL && this.keyboard.lastKey == this) {
				return true;
			}
		} else {
			this.lastTimeKeyReleased = time;
		}

		return false;
	}
	
	/**
	 * @return
	 */
	boolean isMultiple() {
		return this.keys.length() > 1;
	}
}
