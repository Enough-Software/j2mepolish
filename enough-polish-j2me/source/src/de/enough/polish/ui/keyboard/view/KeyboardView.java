//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.view;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Command;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TextField;
import de.enough.polish.ui.keyboard.Keyboard;
import de.enough.polish.util.Properties;

/**
 * A class to display a Keyboard instance and a header to show the
 * current text that was written. This form is shown in 
 * TextField.handlePointerReleased() and set the text of the specified
 * TextField with the written result of the KeyboardView
 * <pre>
 * history
 *        02-March-2010 - David Added support for password fields.
 * </pre>
 * @author Andre
 * @author David
 *
 */
public class KeyboardView extends FramedForm implements ItemCommandListener 
{
	/**
	 * the submit command to close the keyboard view and pass its value
	 * to the specified textfield
	 */
	Command cmdSubmit = new Command("",Command.ITEM,0);
	
	/**
	 * the header item
	 */
	Container headerItem;
	
	/**
	 * the submit item
	 */
	StringItem submitItem;
	
	/**
	 * the display item
	 */
	StringItem displayItem;
	
	/**
	 * the TextField
	 */
	TextField field;
	
	/**
	 * the screen
	 */
	Displayable screen;
	
	/**
	 * the keyboard
	 */
	Keyboard keyboard;
	
	/**
	 * the text
	 */
	String text;
	
	/**
	 * the static instance of KeyboardView
	 */
	static KeyboardView instance;
	
	/**
	 * Returns an instance of a KeyboardView for the given title, textfield and screen
	 * @param title the title to display
	 * @param field the field to pass the value to
	 * @param screen the screen to return to
	 * @return an instance of a KeyboardView
	 */
	public static KeyboardView getInstance(String title, TextField field, Displayable screen) {
		if(instance == null) {
			instance = new KeyboardView();
		} 
		
		instance.init(title, field, screen);
		
		return instance;
	}
	
	/**
	 * Creates a new KeyboardView instance
	 */
	KeyboardView() {
		//#style keyboardView
		super(null);
	}
	
	/**
	 * Initializes the KeyboardView
	 * @param title the title
	 * @param field the textfield
	 * @param screen the screen
	 */
	void init(String title, TextField field, Displayable screen) {
		setTitle(title);
		
		this.field = field;
		
		this.screen = screen;
		
		buildHeader();
		
		build();
		
		String value = field.getString();
		setText(value);
	}
	
	/**
	 * Builds the header
	 */
	void buildHeader() {
		deleteAll(Graphics.TOP);
		
		//#style keyboardViewSubmit
		this.submitItem = new StringItem(null,"OK");
		this.submitItem.setDefaultCommand(cmdSubmit);
		this.submitItem.setItemCommandListener(this);
		
		//#style keyboardViewDisplay
		this.displayItem = new StringItem(null,"");
		
		//#style keyboardViewHeader
		this.headerItem = new Container(false);
			
		this.headerItem.add(this.displayItem);
		this.headerItem.add(this.submitItem);
		
		append(Graphics.TOP,this.headerItem);
	}
	
	/**
	 * Builds the keyboard
	 */
	void build() {
		// removed previous items
		deleteAll(-1);
		
		// create the keyboard
		this.keyboard = new Keyboard();
		this.keyboard.setKeyboardView(this);
		
		// get the field type of the associated textfield
		int fieldType = this.field.getConstraints() & 0xffff;
		
		String modeAnyAlphaUrl = null;
		String modeAnyNumericUrl = null;
		String modeNumericUrl = null;
		String modePhonenumberUrl = null;
		
		// read the file mapping urls if they are defined in the
		// build.xml variables section
		
		//#if virtualKeyboard.KeyMap.Any.Alpha:defined
			//#= modeAnyAlphaUrl = "${virtualKeyboard.KeyMap.Any.Alpha}";
		//#endif
		
		//#if virtualKeyboard.KeyMap.Any.Numeric:defined
			//#= modeAnyNumericUrl = "${virtualKeyboard.KeyMap.Any.Numeric}";
		//#endif
		
		//#if virtualKeyboard.KeyMap.Numeric:defined
			//#= modeNumericUrl = "${virtualKeyboard.KeyMap.Numeric}";
		//#endif
		
		//#if virtualKeyboard.KeyMap.Phonenumber:defined
			//#= modePhonenumberUrl = "${virtualKeyboard.KeyMap.Phonenumber}";
		//#endif
		
		// add the modes if the conditions are met
		addMode(Keyboard.MODE_ANY_ALPHA, 
				Keyboard.KEYS_ANY_ALPHA, 
				modeAnyAlphaUrl, 
				fieldType == TextField.ANY);
		
		addMode(Keyboard.MODE_ANY_NUMERIC, 
				Keyboard.KEYS_ANY_NUMERIC, 
				modeAnyNumericUrl, 
				fieldType == TextField.ANY);
		
		addMode(Keyboard.MODE_NUMERIC, 
				Keyboard.KEYS_NUMERIC, 
				modeNumericUrl, 
				fieldType == TextField.NUMERIC);
		
		addMode(Keyboard.MODE_PHONENUMBER, 
				Keyboard.KEYS_PHONENUMBER, 
				modePhonenumberUrl, 
				fieldType == TextField.PHONENUMBER);
		
		// set the initial modes depending on the textfield type
		
		if(fieldType == TextField.ANY) {
			this.keyboard.setMode(Keyboard.MODE_ANY_ALPHA);
		} else if(fieldType == TextField.NUMERIC) {
			this.keyboard.setMode(Keyboard.MODE_NUMERIC);
		} else if(fieldType == TextField.PHONENUMBER) {
			this.keyboard.setMode(Keyboard.MODE_PHONENUMBER);
		}
		
		// append the keyboard to this 
		
		append(this.keyboard);
	}
	
	/**
	 * Adds a mode to the associated keyboard by using the default mapping or
	 * the mapping file if any
	 * @param mode the mode
	 * @param mappingDefault the default mapping
	 * @param mappingUrl the mapping file url
	 * @param condition the condition to add the mode
	 */
	void addMode(int mode, String mappingDefault, String mappingUrl, boolean condition) {
		if(condition) {
			try {
				if(mappingUrl != null) {
					this.keyboard.addMode(mode, new Properties(mappingUrl,"UTF8"));
				} else {
					this.keyboard.addMode(mode, mappingDefault);
				}
			} catch (IOException e) {
				//#debug error
				System.out.println("unable to read properties file " + mappingUrl);
			}
		}
	}
	
	/**
	 * Returns the text
	 * @return the text
	 */
	public String getText() {
		return this.text;
	}
	
	/**
	 * Sets the text in the display item.
         * This method also takes into account the type of the textfield
	 * @param text the text
	 */
	public void setText(String text) {
		this.text = text;
                if ((this.field.getConstraints() & TextField.PASSWORD) == TextField.PASSWORD){
                    StringBuffer buff= new StringBuffer(text.length());
                    for (int i=0;i<text.length();i++){
                        buff.append("*");
                    }
                    this.displayItem.setText(buff.toString());
                }else{
                    this.displayItem.setText(text);
                }
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemCommandListener#commandAction(de.enough.polish.ui.Command, de.enough.polish.ui.Item)
	 */
	public void commandAction(Command c, Item item) {
		if(c == cmdSubmit) {
			// set the text in the field
			this.field.setString(text);
			// return to the screen
			Display.getInstance().setCurrent(this.screen);
		}
	}
}
