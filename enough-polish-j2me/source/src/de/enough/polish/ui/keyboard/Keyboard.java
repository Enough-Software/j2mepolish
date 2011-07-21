//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.keyboard.keys.BlankItem;
import de.enough.polish.ui.keyboard.keys.ClearKeyItem;
import de.enough.polish.ui.keyboard.keys.DeleteKeyItem;
import de.enough.polish.ui.keyboard.keys.ModeKeyItem;
import de.enough.polish.ui.keyboard.keys.ShiftKeyItem;
import de.enough.polish.ui.keyboard.view.KeyboardView;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Arrays;
import de.enough.polish.util.IntHashMap;
import de.enough.polish.util.IntList;
import de.enough.polish.util.Properties;

/**
 * A Keyboard implementation for pointer devices.
 * This class shouldn't be used on its own but rather
 * through the implementation in TextField which can
 * be activated with polish.TextField.useVirtualKeyboard 
 * @author Andre
 */
public class Keyboard
	extends Container
{
	/**
	 * default mappings for alpha characters for TextField.ANY
	 */
	public static String KEYS_ANY_ALPHA = "0,0=a\n0,1=b\n0,2=c\n0,3=d\n0,4=e\n1,0=f\n1,1=g\n1,2=h\n1,3=i\n1,4=j\n2,0=k\n2,1=l\n2,2=m\n2,3=n\n2,4=o\n3,0=p\n3,1=q\n3,2=r\n3,3=s\n3,4=t\n4,0=u\n4,1=v\n4,2=x\n4,3=y\n4,4=z\n5,0=[SHIFT]\n5,1=[SPACE]\n5,2=[DELETE]\n5,3=[123]\n";

	/**
	 * default mappings for numeric characters for TextField.ANY
	 */
	public static String KEYS_ANY_NUMERIC = "0,0=1\n0,1=2\n0,2=3\n0,3=4\n0,4=5\n0,5=6\n1,0=7\n1,1=8\n1,2=9\n1,3=0\n1,4=!\n1,5=\"\n2,0=%\n2,1=&\n2,2=/\n2,3=(\n2,4=)\n2,5==\n3,0=?\n3,1=\'\n3,2=*\n3,3=+\n3,4=-\n3,5=#\n4,0=,\n4,1=.\n4,2=:\n4,3=;\n4,4=[\n4,5=]\n5,0=[]\n5,1=[SPACE]\n5,2=[CLEAR]\n5,3=[ABC]\n";
	
	/**
	 * default mappings for numeric characters for TextField.NUMBERS
	 */
	public static String KEYS_NUMERIC = "0,0=1\n0,1=2\n0,2=3\n1,0=4\n1,1=5\n1,2=6\n2,0=7\n2,1=8\n2,2=9\n3,0=[]\n3,1=0\n3,2=[]";
	
	/**
	 * default mappings for numeric characters for TextField.PHONENUMBER
	 */
	public static String KEYS_PHONENUMBER = "0,0=1\n0,1=2\n0,2=3\n1,0=4\n1,1=5\n1,2=6\n2,0=7\n2,1=8\n2,2=9\n3,0=[]\n3,1=0\n3,2=+";
	
	/**
	 * the mode for alpha characters for TextField.ANY
	 */
	public static final int MODE_ANY_ALPHA = 0x00;
	
	/**
	 * the mode for numeric characters for TextField.ANY
	 */
	public static final int MODE_ANY_NUMERIC = 0x01;
	
	/**
	 * the mode for numeric characters for TextField.NUMBERS
	 */
	public static final int MODE_NUMERIC = 0x02;
	
	/**
	 * the mode for numeric characters for TextField.PHONENUMBER
	 */
	public static final int MODE_PHONENUMBER = 0x03;
	
	/**
	 * the key mappings placeholder for the shift key
	 */
	public static final String ID_SHIFT = "[SHIFT]";
	
	/**
	 * the key mappings placeholder for the clear key
	 */
	public static final String ID_CLEAR = "[CLEAR]";
	
	/**
	 * the key mappings placeholder for the delete key
	 */
	public static final String ID_DELETE = "[DELETE]";
	
	/**
	 * the key mappings placeholder for the space key
	 */
	public static final String ID_SPACE = "[SPACE]";
	
	/**
	 * the key mappings placeholder for the mode key
	 */
	public static final String ID_MODE = "[MODE]";
	
	/**
	 * the key mappings placeholder for a blank space in a keyboard layout
	 */
	public static final String ID_BLANK = "[]";
	
	/**
	 * the layouts by modes
	 */
	static IntHashMap layoutByMode = new IntHashMap();
	
	/**
	 * the current mode of the keyboard
	 */
	int currentMode;
	
	
	/**
	 * the available modes
	 */
	IntList modes;
	
	/**
	 * the index of the current mode within modes 
	 */
	int currentModeIndex = 0;
	
	/**
	 * the view which uses this keyboard instance
	 */
	KeyboardView view;
	
	/**
	 * indicates if shift is activated
	 */
	boolean shift;
	
	/**
	 * the last key that was used
	 */
	public KeyItem lastKey;
	
	/**
	 * Creates a new Keyboard instance
	 */
	public Keyboard() {
		//#style keyboard?
		super(false);
		
		this.modes = new IntList(); 
	}
	
	/**
	 * Adds a mode to this Keyboard instance with
	 * the specified key mappings which are in
	 * the format of a property file
	 * @param mode the mode
	 * @param mapping the mapping string
	 */
	public void addMode(int mode, String mapping) {
		//return if the mode is already added
		if(hasMode(mode)) {
			//#debug info
			System.out.println("mode is already added");
			return;
		}
		
		try {
			// load a Properties instance with the mappings
			Properties keys = new Properties();
			InputStream stream = new ByteArrayInputStream(mapping.getBytes());
			keys.load(stream);
			// add the mode
			addMode(mode,keys);
		} catch (IOException e) {
			//#debug error
			System.out.println("unable to load key definitions");
		}
	}

	/**
	 * Adds a mode to this Keyboard instance with
	 * the specified key mappings as a Properties 
	 * instance
	 * @param mode the mode
	 * @param properties the Properties instance
	 */
	public void addMode(int mode, Properties properties) {
		//return if the mode has already been added
		if(hasMode(mode)) {
			//#debug info
			System.out.println("mode is already added");
			return;
		}
		
		// build the layout
		ArrayList rows = buildLayout(mode, properties);
		
		// add the mode to available ones of this instance
		this.modes.add(mode);
		
		// add the layout with the mode
		layoutByMode.put(mode, rows);
	}
	
	/**
	 * Returns the index of the given mode in the available modes list. If
	 * the mode is not available -1 is returned.
	 * @param mode the mode
	 * @return the index of the given mode in the available modes list if available otherwise false 
	 */
	int getModeIndex(int mode) {
		for (int index = 0; index < this.modes.size(); index++) {
			int entry = this.modes.get(index);
			if(entry == mode) {
				return index;
			}
		}
		
		return -1;
	}
	
	/**
	 * Returns true if the given mode is an available mode
	 * @param mode the mode
	 * @return true  if the given mode is an available mode otherwise false
	 */
	boolean hasMode(int mode) {
		return getModeIndex(mode) != -1;
	}
	
	/**
	 * Returns an array of KeyItems by reading a key mapping from
	 * a Properties instance and ordering the keys by the given positions
	 * @param mapping the key mapping  
	 * @return the array of ordered key items
	 */
	KeyItem[] getKeyItems(Properties mapping) {
		Object[] positions = mapping.keys();
		KeyItem[] items = new KeyItem[positions.length];
		
		for (int i = 0; i < positions.length; i++) {
			String position = (String)positions[i];
			String keys = mapping.getProperty(position);
			
			KeyItem keyItem = getSpecialKeyItem(keys, position);
			
			if(keyItem == null) {
				keyItem = getKeyItem(keys,position);
			}
			
			items[i] = keyItem;
		}
		
		Arrays.sort(items, new KeyComparator());
		
		return items;
	}
	
	/**
	 * Builds a keyboard layout by reading the given key mapping
	 * and converting it into rows of key items as containers
	 * @param mode the mode
	 * @param mapping the key mapping
	 * @return an arraylist of rows of key items
	 */
	ArrayList buildLayout(int mode, Properties mapping) {
		// get an ordered list of keys
		KeyItem[] keyItems = getKeyItems(mapping);
		
		ArrayList rows = new ArrayList();
		
		int currentRow = 0;
		
		//#style keyrow
		Container row = new Container(false);
		
		// create rows from the ordered list by reading
		// the specified rows of the individual keys
		for (int i = 0; i < keyItems.length; i++) {
			KeyItem keyItem = keyItems[i];
			
			if(currentRow < keyItem.getRow()) {
				currentRow = keyItem.getRow();
				rows.add(row);
				
				//#style keyrow
				row = new Container(false);
			}
			
			row.add(keyItem);
		}
		
		rows.add(row);
		
		return rows;
	}
	
	/**
	 * Returns a special KeyItem if
	 * the key equals on of the specified special key ids
	 * @param key the key
	 * @param position the position of the key
	 * @return a new instance of a special key item
	 */
	KeyItem getSpecialKeyItem(String key, String position) {
		if(key.equals(ID_SHIFT)) {
			//#style keyShift
			return new ShiftKeyItem(this, position);
		} else if(key.equals(ID_MODE)) {
			//#style keyMode
			return new ModeKeyItem(this, position);
		} else if(key.equals(ID_CLEAR)) {
			//#style keyClear
			return new ClearKeyItem(this, position);
		} else if (key.equals(ID_DELETE)) {
			//#style keyDelete
			return new DeleteKeyItem(this, position);
		} else if(key.equals(ID_BLANK)) {
			//#style keyBlank
			return new BlankItem(this, position);
		} else if(key.equals(ID_SPACE)) {
			//#style keySpace
			return new KeyItem(this, position, " ");
		} else {
			return null;
		}
	}
	
	/**
	 * Returns a KeyItem specified by the given key and position
	 * @param key the key
	 * @param position the position
	 * @return a KeyItem specified by the given key and position
	 */
	KeyItem getKeyItem(String key,String position) {
		//#style key
		return new KeyItem(this,position,key);
	}
	
	/**
	 * Returns the KeyboardView instance of this keyboard
	 * @return the KeyboardView instance of this keyboard
	 */
	public KeyboardView getKeyboardView() {
		return this.view;
	}
	
	/**
	 * Sets the KeyboardView of this keyboard
	 * @param view the KeyboardView to set
	 */
	public void setKeyboardView(KeyboardView view) {
		this.view = view;
	}

	/**
	 * Sets the mode of this keyboard
	 * @param mode the mode
	 */
	public void setMode(int mode) {
		//return if the mode is not available
		if(!hasMode(mode)) {
			//#debug info
			System.out.println("the mode is not available");
			return;
		}
		
		// clear the keyboard from the previous layout
		clear();
		
		// get the rows for the previous mode ...
		ArrayList rows = (ArrayList)layoutByMode.get(getMode());
		
		// and remove them
		if(rows != null) {
			for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
				remove((Container)rows.get(rowIndex));
			}
		}
		
		this.currentModeIndex = getModeIndex(mode);
		this.currentMode = mode;
		
		// get the rows for the mode to set ...
		rows = (ArrayList)layoutByMode.get(mode);
		
		// and add them
		for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
			add((Container)rows.get(rowIndex));
		}
	}
	
	/**
	 * Sets the next mode in the mode list as the current
	 */
	public void setNextMode() {
		this.currentModeIndex = (this.currentModeIndex + 1) % this.modes.size();
		int mode = this.modes.get(this.currentModeIndex);
		setMode(mode);
	}
	
	/**
	 * Applies a key value to the associated KeyboardView
	 * @param key the key value
	 */
	public void apply(char key) {
		apply(key,false,false);
	}
	
	/**
	 * Applies a key value to the associated KeyboardView
	 * @param key the key value
	 * @param isDoubleclick 
	 * @param isMultiple
	 */
	public void apply(char key, boolean isDoubleclick, boolean isMultiple) {
		// get the current text in the KeyboardView
		String source = this.view.getText();
		
		String text;
		
		if(isDoubleclick && isMultiple) {
			text = source.substring(0,source.length() - 1) + key;
		}
		else {
			// add the key value
			text = source + key;
		}
		
		// set the new text
		this.view.setText(text);
	}
	
	/**
	 * Clears the text in the associated KeyboardView
	 */
	public void clearText() {
		this.view.setText("");
	}
	
	/**
	 * Delete the last character in the associated KeyboardView
	 */
	public void deleteText() {
		String text = this.view.getText();
				
		if(text.length() > 0)
		{
			this.view.setText(text.substring(0, text.length() - 1));
		}
	}
	
	/**
	 * Returns the current mode of this keyboard
	 * @return the current mode of this keyboard
	 */
	public int getMode() {
		return this.currentMode;
	}
	
	/**
	 * Sets the shift state
	 * @param shift true if shift should be activated otherwise false
	 */
	public void shift(boolean shift) {
		this.shift = shift;
	}
	
	/**
	 * Returns true if shift is activated
	 * @return true if shift is activated otherwise false
	 */
	public boolean isShift() {
		return this.shift;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#createCssSelector()
	 */
	protected String createCssSelector() {
		return null;
	}
	
}
