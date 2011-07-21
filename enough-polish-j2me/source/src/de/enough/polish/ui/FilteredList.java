//#condition polish.usePolishGui
/*
 * Created on Jun 21, 2007 at 11:28:35 PM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ui;


import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.util.ArrayList;

/**
 * <p>Displays a list of choices that can be limited by the user by entering some input.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Jun 21, 2007 - michael creation
 * </pre>
 * @author Michael Koch
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FilteredList 
extends List
implements ItemStateListener //, CommandListener
{
	private static final int FIELD_POSITION_TOP = 0;
	private static final int FIELD_POSITION_BOTTOM = 1;
	private static final int FILTER_STARTS_WITH = 0;
	private static final int FILTER_INDEX_OF = 1;
	protected int filterPosition = FIELD_POSITION_BOTTOM;
	protected int filterMode = FILTER_STARTS_WITH;
	protected final TextField filterTextField;
	private final ArrayList itemsList;
	//private CommandListener originalCommandListener;
	private String lastFilterText;
	private int filterHeight;

	/**
	 * Creates a new FilteredList 
	 * @param title the title
	 * @param listType the type, either Choice.MULTIPLE, Choice.IMPLICIT or Choice.EXCLUSIVE
	 */
	public FilteredList(String title, int listType) {
		this( title, listType, (ChoiceItem[])null, (Style) null );
	}
	
	/**
	 * Creates a new FilteredList 
	 * @param title the title
	 * @param listType the type, either Choice.MULTIPLE, Choice.IMPLICIT or Choice.EXCLUSIVE
	 * @param style style for the list
	 */
	public FilteredList(String title, int listType, Style style) {
		this( title, listType, (ChoiceItem[])null, style );
	}

	/**
	 * Creates a new FilteredList 
	 * @param title the title
	 * @param listType the type, either Choice.MULTIPLE, Choice.IMPLICIT or Choice.EXCLUSIVE
	 * @param stringElements list item texts
	 * @param imageElements list item images
	 */
	public FilteredList(String title, int listType, String[] stringElements, Image[] imageElements) {
		this( title, listType, stringElements, imageElements, null );
	}


	/**
	 * Creates a new FilteredList 
	 * @param title the title
	 * @param listType the type, either Choice.MULTIPLE, Choice.IMPLICIT or Choice.EXCLUSIVE
	 * @param stringElements list item texts
	 * @param imageElements list item images
	 * @param style style for the list
	 */
	public FilteredList(String title, int listType, String[] stringElements, Image[] imageElements, Style style) {
		this( title, listType, ChoiceGroup.buildChoiceItems(stringElements, imageElements, listType, style), style );
	}


	/**
	 * Creates a new FilteredList 
	 * @param title the title
	 * @param listType the type, either Choice.MULTIPLE, Choice.IMPLICIT or Choice.EXCLUSIVE
	 * @param items items of the list
	 */
	public FilteredList(String title, int listType, ChoiceItem[] items) {
		this(title, listType, items, null);
	}


	
	/**
	 * Creates a new FilteredList 
	 * @param title the title
	 * @param listType the type, either Choice.MULTIPLE, Choice.IMPLICIT or Choice.EXCLUSIVE
	 * @param items items of the list
	 * @param style style for the list
	 */
	public FilteredList(String title, int listType, ChoiceItem[] items, Style style) {
		super(title, listType, items, style);
		//#style filterTextField?
		this.filterTextField = new TextField( null, "", 30, TextField.ANY );
		this.filterTextField.screen = this;
		setItemStateListener( this );
		this.itemsList = new ArrayList();
		//super.setCommandListener( this );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		//#debug
		System.out.println("handleKeyPressed( " + keyCode + ", " + gameAction + ")");
		boolean handled = false;
		if (! isGameActionFire( keyCode, gameAction )) {
			handled = this.filterTextField.handleKeyPressed(keyCode, gameAction);
			if (handled && this.filterTextField.getItemHeight( this.contentWidth, this.contentWidth, this.contentHeight + this.filterHeight) != this.filterHeight) {
				calculateContentArea(0, 0, this.screenWidth, this.screenHeight );
			}
		}
		if (!handled) {
			handled = super.handleKeyPressed(keyCode, gameAction);
		}
		return handled;
	}
	


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#handleKeyReleased(int, int)
	 */
	protected boolean handleKeyReleased(int keyCode, int gameAction) {
		//#debug
		System.out.println("handleKeyReleased( " + keyCode + ", " + gameAction + ")");
		boolean handled = false; 
		if (!(isGameActionFire(keyCode, gameAction)) ) {
			handled = this.filterTextField.handleKeyReleased(keyCode, gameAction);
		}
		if (!handled) {
			handled = super.handleKeyReleased(keyCode, gameAction);
		}
		return handled;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#handleKeyRepeated(int, int)
	 */
	protected boolean handleKeyRepeated(int keyCode, int gameAction) {
		//#debug
		System.out.println("handleKeyRepeated( " + keyCode + ", " + gameAction + ")");
		boolean handled = false;
		if (!(isGameActionFire(keyCode, gameAction))) {
			handled = this.filterTextField.handleKeyRepeated(keyCode, gameAction);
		}
		if (!handled) {
			handled = super.handleKeyRepeated(keyCode, gameAction);
		}
		return handled;
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#animate(long,ClippingRegion)
	 */
	public void animate( long currentTime, ClippingRegion repaintRegion ) {
		super.animate(currentTime,  repaintRegion);
		this.filterTextField.animate(currentTime,  repaintRegion);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#setItemCommands( ArrayList,Item)
	 */
	protected void setItemCommands( ArrayList commandsList, Item item ) {
		if (item != this.filterTextField) {
			this.filterTextField.addCommands(commandsList);
		} else {
			this.container.addCommands(commandsList);
		}
		super.setItemCommands(commandsList, item);
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#adjustContentArea(int, int, int, int, de.enough.polish.ui.Container)
	 */
	protected void adjustContentArea(int x, int y, int width, int height, Container cont) {
		this.filterHeight = this.filterTextField.getItemHeight( this.contentWidth, this.contentWidth, this.contentHeight / 2 );
		this.contentHeight -= this.filterHeight;
		this.container.setScrollHeight( this.contentHeight );
		if (this.filterPosition == FIELD_POSITION_TOP) {
			this.filterTextField.relativeY = this.contentY;
			this.contentY += this.filterHeight;
		} else {
			this.filterTextField.relativeY = this.contentY + this.contentHeight;
		}
		super.adjustContentArea(x, y, width, height, cont);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#showNotify()
	 */
	public void showNotify() {
		//#debug
		System.out.println("showNotify of FilteredList" + this );
		if (!this.filterTextField.isFocused) {
			this.filterTextField.setShowInputInfo( false );
			this.filterTextField.focus( this.filterTextField.getFocusedStyle(), 0 );
		}
		//#if polish.blackberry
			Display.getInstance().notifyFocusSet(this.filterTextField);
		//#endif
		itemStateChanged( this.filterTextField );
		super.showNotify();
	}
	
	//#if polish.blackberry
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#notifyFocusSet(de.enough.polish.ui.Item)
	 */
	protected void notifyFocusSet(Item item) {
		if (this.isMenuOpened() || item == this.filterTextField) {
			super.notifyFocusSet(item);
		}
	}
	//#endif

	//#if !polish.blackberry
    private boolean forwardEventToNativeField(Screen screen, int keyCode) {
    	//# return false;
    //#else
    	//# protected boolean forwardEventToNativeField(Screen screen, int keyCode) {
    	boolean forward = Display.getInstance().forwardEventToNativeField( screen, keyCode );
    	return forward && (getGameAction( keyCode ) != FIRE);
	//#endif
    }


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#getCurrentItem()
	 */
	public Item getCurrentItem() {
		return this.filterTextField;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#paintScreen(javax.microedition.lcdui.Graphics)
	 */
	protected void paintScreen(Graphics g) {
		this.filterTextField.paint( this.contentX, this.filterTextField.relativeY, this.contentX, this.contentX + this.contentWidth, g );
		super.paintScreen(g);
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#append(de.enough.polish.ui.ChoiceItem)
	 */
	public int append(ChoiceItem item) {
		if (this.listType == Choice.MULTIPLE) {
			this.choiceGroup.selectChoiceItem(item, item.isSelected);
			//#if !( polish.ChoiceGroup.suppressSelectCommand || polish.ChoiceGroup.suppressMarkCommands)  
				item.setItemCommandListener( this.choiceGroup );
			//#endif
		}
		this.itemsList.add( item );
		this.lastFilterText = null;
		if (isShown()) {
			itemStateChanged( this.filterTextField );
		}
		return this.itemsList.size() - 1;
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#delete(int)
	 */
	public void delete(int elementNum) {
		this.itemsList.remove(elementNum);
		this.lastFilterText = null;
		if (isShown()) {
			itemStateChanged( this.filterTextField );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#deleteAll()
	 */
	public void deleteAll() {
		this.itemsList.clear();
		this.lastFilterText = null;
		if (isShown()) {
			itemStateChanged( this.filterTextField );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#getItem(int)
	 */
	public ChoiceItem getItem(int elementNum) {
		return (ChoiceItem) this.itemsList.get(elementNum);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#getSelectedFlags(boolean[])
	 */
	public int getSelectedFlags(boolean[] selectedArray_return) {
		int count = 0;
		for (int i = 0; i < selectedArray_return.length; i++) {
			boolean selected = ((ChoiceItem) this.itemsList.get(i)).isSelected;
			selectedArray_return[i] = selected;
			if (selected) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Determines whether there are any changes compared to the specified boolean array.
	 * 
	 * @param flags an array indicating the expected state of this list - true array elements indicate "selected" items of this list
	 * @return true when there are changes in this list
	 */
	public boolean containsChangesTo( boolean[] flags ) {
		if (this.itemsList.size() != flags.length ) {
			return true;
		}
		for (int i = 0; i < flags.length; i++) {
			boolean flag = flags[i];
			if (flag !=  ((ChoiceItem)this.itemsList.get(i)).isSelected ) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#getSelectedIndex()
	 */
	public int getSelectedIndex() {
		if (this.listType == Choice.IMPLICIT && isShown()) {
			Item focItem = this.container.getFocusedItem();
			if (focItem != null) {
				return this.itemsList.indexOf(focItem);
			}
		}
		Object[] items = this.itemsList.getInternalArray();
		for (int i = 0; i < items.length; i++) {
			Object object = items[i];
			if (object == null) {
				return -1;
			}
			ChoiceItem item = (ChoiceItem) object;
			if ( item.isSelected ) {
				return i;
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#insert(int, de.enough.polish.ui.ChoiceItem)
	 */
	public void insert(int elementNum, ChoiceItem item) {
		this.itemsList.add(elementNum, item);
		this.lastFilterText = null;
		if (isShown()) {
			itemStateChanged( this.filterTextField );
		}
	}
	
	/**
	 * Sets the <code>String</code> and <code>Image</code> parts of the
	 * element referenced by <code>elementNum</code>,
	 * replacing the previous contents of the element.
	 * 
	 * @param elementNum the index of the element to be set
	 * @param stringPart the string part of the new element
	 * @param imagePart the image part of the element, or null if there is no image part
	 * @param elementStyle the style for the new list element.
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @throws NullPointerException if stringPart is null
	 * @see Choice#set(int, String, Image) in interface Choice
	 */
	public void set(int elementNum, String stringPart, Image imagePart, Style elementStyle )
	{
		ChoiceItem item = getItem(elementNum );
		item.setText( stringPart );
		if (imagePart != null) {
			item.setImage(imagePart);
		}
		if (elementStyle != null) {
			item.setStyle(elementStyle);
		}
		this.lastFilterText = null;
		if (isShown()) {
			itemStateChanged( this.filterTextField );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#set(int, de.enough.polish.ui.ChoiceItem)
	 */
	public void set(int elementNum, ChoiceItem item) {
		this.itemsList.set(elementNum, item);
		this.lastFilterText = null;
		if (isShown()) {
			itemStateChanged( this.filterTextField );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#setSelectedFlags(boolean[])
	 */
	public void setSelectedFlags(boolean[] selectedArray) {
		for (int i = 0; i < selectedArray.length; i++) {
			boolean isSelected = selectedArray[i];
			ChoiceItem item = ((ChoiceItem) this.itemsList.get(i));
			item.select( isSelected );
			
			//#if !polish.ChoiceGroup.suppressMarkCommands
				if ( this.listType == Choice.MULTIPLE ) {
					if (isSelected) {
						item.removeCommand(ChoiceGroup.MARK_COMMAND);
						item.setDefaultCommand(ChoiceGroup.UNMARK_COMMAND);
					} else {
						item.removeCommand(ChoiceGroup.UNMARK_COMMAND);
						item.setDefaultCommand(ChoiceGroup.MARK_COMMAND);
					}
				}
			//#endif
		}
		if (isShown()) {
			itemStateChanged( this.filterTextField );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#setSelectedIndex(int, boolean)
	 */
	public void setSelectedIndex(int elementNum, boolean isSelected) {
		if ( this.listType == Choice.MULTIPLE ) {
			ChoiceItem item = (ChoiceItem) this.itemsList.get( elementNum );
			item.select( isSelected );
			//#if !polish.ChoiceGroup.suppressMarkCommands
				if (isSelected) {
					item.removeCommand(ChoiceGroup.MARK_COMMAND);
					item.setDefaultCommand(ChoiceGroup.UNMARK_COMMAND);
				} else {
					item.removeCommand(ChoiceGroup.UNMARK_COMMAND);
					item.setDefaultCommand(ChoiceGroup.MARK_COMMAND);
				}
			//#endif
		} else {
			if (!isSelected) {
				return; // ignore this call
			}
			int oldIndex = getSelectedIndex(); 
			if ( oldIndex != -1) {
				ChoiceItem oldSelected = (ChoiceItem) this.itemsList.get( oldIndex );
				oldSelected.select( false );
			}
			ChoiceItem newSelected = (ChoiceItem) this.itemsList.get( elementNum );
			newSelected.select( true );
			
		}
		if (isShown()) {
			if (this.listType == Choice.IMPLICIT) {
				focus( elementNum );
			}
			itemStateChanged( this.filterTextField );
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#focus(int, boolean)
	 */
	public void focus(int index, boolean force)
	{
		Item item = null;
		if (index != -1) {
			item = (Item) this.itemsList.get( index );
		}
		focus( index, item, force );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#focus(de.enough.polish.ui.Item, boolean)
	 */
	public void focus(Item item, boolean force)
	{
		int index = -1;
		if (item != null) {
			index = this.itemsList.indexOf( item );
			if (index == -1) {
				super.focus( item, force );
				return;
			}
		}
		focus( index, item, force );
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#focus(int, de.enough.polish.ui.Item, boolean)
	 */
	public void focus(int index, Item item, boolean force)
	{
		if (isMenuOpened()) {
			super.focus( index, item, force );
			return;
		}
		if (index != -1 && item == null) {
			item = (Item) this.itemsList.get( index );
		}
		if (item != null) {
			index = this.container.indexOf(item);
			if (index == -1) {
				super.focus( index, item, force );
			} else {
				this.container.focusChild(index);
			}
		} else {
			super.focus( index, item, force );
			//this.container.focusChild(-1);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#isSelected(int)
	 */
	public boolean isSelected(int elementNum) {
		ChoiceItem item = (ChoiceItem) this.itemsList.get( elementNum );
		return item.isSelected;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#size()
	 */
	public int size() {
		return this.itemsList.size();
	}
	
//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.Screen#getCommandListener()
//	 */
//	public CommandListener getCommandListener() {
//		return this.originalCommandListener;
//	}
//
//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.Screen#setCommandListener(javax.microedition.lcdui.CommandListener)
//	 */
//	public void setCommandListener(CommandListener listener) {
//		this.originalCommandListener = listener;
//	}
//	
//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.Screen#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
//	 */
//	public void commandAction(Command command, Displayable screen) {
//		System.out.println("commandAction: " + command.getLabel() );
//		this.filterTextField.commandAction(command, this.filterTextField);
//		if (this.container.itemCommandListener != null && this.container.commands != null && this.container.commands.contains(command)) {
//			this.container.itemCommandListener.commandAction(command, this.container);
//		} else if (this.originalCommandListener != null) {
//			this.originalCommandListener.commandAction(command, this);
//		}
//	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handleCommand(javax.microedition.lcdui.Command)
	 */
	protected boolean handleCommand(Command cmd) {
		if (this.filterTextField.handleCommand(cmd)) {
			return true;
		}
		return super.handleCommand(cmd);
	}

	public void setFilterLabel( String label ) {
		this.filterTextField.setLabel(label);
	}
	
	/**
	 * Sets the text of the filter element.
	 * 
	 * @param text the text that is should be entered into the filter field.
	 */
	public void setFilterText( String text ) {
		this.filterTextField.setString( text );
		if (this.contentWidth != 0 && isShown()) {
			int height = this.filterTextField.getItemHeight( this.contentWidth, this.contentWidth, this.contentHeight + this.filterHeight );
			if (height != this.filterHeight) {
				calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
			}
			itemStateChanged( this.filterTextField );
		}
	}
	
	/**
	 * Retrieves the text from the filter element.
	 * 
	 * @return the text that is currently entered into the filter field.
	 */
	public String getFilterText() {
		return this.filterTextField.getString();
	}
	
	/**
	 * @param filterStyle
	 */
	public void setFilterStyle(Style filterStyle) {
		this.filterTextField.focusedStyle = filterStyle;
		if (isShown()) {
			this.filterTextField.focus(filterStyle, 0);
		}
	}
	
	/**
	 * Checks if the given item matches the current input text.
	 * Subclasses can override this method for implementing specific
	 * filter strategies.
	 * 
	 * @param filterText the current filter text
	 * @param cItem the ChoiceItem
	 * @param checkForSelectedRadioItem true when this is an exclusive list
	 * @return true for choice items that should be appended to the shown list.
	 * @see #FILTER_STARTS_WITH
	 * @see #FILTER_INDEX_OF
	 * @see #setFitPolicy(int)
	 */
	protected boolean matches( String filterText, ChoiceItem cItem, boolean checkForSelectedRadioItem ) {
		if (checkForSelectedRadioItem && cItem.isSelected) {
			return true;
		} else if (this.filterMode == FILTER_STARTS_WITH) {
			return ( cItem.getText().toLowerCase().startsWith(filterText));
		} else {
			return ( cItem.getText().toLowerCase().indexOf( filterText ) != -1);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemStateListener#itemStateChanged(de.enough.polish.ui.Item)
	 */
	public void itemStateChanged(Item item) {
		if (item == this.filterTextField) {
			String text = this.filterTextField.getString();
			if (text == this.lastFilterText || (text != null && text.equals(this.lastFilterText))) {
				return;
			}
			this.lastFilterText = text;
			Object[] itemObjects = this.itemsList.getInternalArray();
			ArrayList matchingItems = new ArrayList( itemObjects.length );
			int focIndex = -1;
			Item focItem = this.container.focusedItem;
			boolean checkForSelectedRadioItem = (this.listType == Choice.EXCLUSIVE);
			if (text.length() == 0) {
				matchingItems.addAll( this.itemsList );
				focIndex = getSelectedIndex();
				if (focIndex != -1 && focItem == null) {
					focItem = (Item) this.itemsList.get( focIndex );
				}
			} else {
				String filterText = text.toLowerCase();
				//System.out.println("caretPos=" + this.filterTextField.getCaretPosition() + ", tex.length=" + text.length());
				//System.out.println("filter=[" + filterText  + "] - number of elements=" + this.itemsList.size());
				for (int i = 0; i < itemObjects.length; i++) {
					Object object = itemObjects[i];
					if (object == null) {
						break;
					}
					ChoiceItem cItem = (ChoiceItem) object;
					boolean isMatch = matches( filterText, cItem, checkForSelectedRadioItem );
					if (isMatch) {
						matchingItems.add(cItem);
						if (cItem == focItem) {
							focIndex = i;
						}
					}
				}
			}
			this.container.setItemsList( matchingItems );
			if (focIndex != -1) {
				super.focus( focIndex, focItem, false );
			} else if (matchingItems.size() > 0) {
				super.focus( 0, (Item) matchingItems.get(0), false );
			}
			if (checkForSelectedRadioItem && this.getSelectedIndex() != -1) {
				( (ChoiceGroup)this.container ).setSelectedIndex( matchingItems.indexOf( this.itemsList.get( getSelectedIndex() ) ) , true);
			}
			this.filterTextField.showCommands();
		}
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.filter-position
			Integer filterPositionInt = style.getIntProperty("filter-position");
			if (filterPositionInt != null) {
				this.filterPosition = filterPositionInt.intValue();
			}
		//#endif
		//#if polish.css.filter-style
			Style filterStyle = (Style) style.getObjectProperty("filter-style");
			if (filterStyle != null) {
				this.filterTextField.focusedStyle = filterStyle;
				this.filterTextField.focus(filterStyle, 0);
			}
		//#endif
	}
	
	

	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#getCssSelector()
	 */
	protected String createCssSelector() {
		return "filteredlist";
	}
	//#endif	

	/**
	 * Concats all strings from the selected elements together.
	 * 
	 * @param delimiter the delimiter between elements
	 * @return the String including all selected elements or null when none is selected
	 */
	public String toSelectionString( String delimiter ) {
		Object[] elements = this.itemsList.getInternalArray();
		StringBuffer buffer = null;
		for (int i = 0; i < elements.length; i++) {
			ChoiceItem item  = (ChoiceItem) elements[i];
			if (item == null) {
				break;
			}
			if (item.isSelected) {
				if (buffer == null) {
					buffer = new StringBuffer();
				} else {
					buffer.append( delimiter );
				}
				buffer.append( item.text );
			}
		}
		if (buffer == null) {
			return null;
		} else {
			return buffer.toString();
		}
	}

}
