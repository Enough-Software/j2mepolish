//#condition polish.usePolishGui
/*
 * Created on Jun 26, 2007 at 3:06:59 PM.
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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

/**
 * <p>Displays the currently selected item(s) and opens up a new FilteredList for selecting an element.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Jun 26, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see FilteredList
 */
public class FilteredChoiceGroup
//#if polish.LibraryBuild
	extends FakeStringCustomItem
	implements javax.microedition.lcdui.Choice, CommandListener
//#else
	//# extends StringItem
	//# implements Choice, CommandListener
//#endif
{
	/** Only a single option can be selected */
	public final static int EXCLUSIVE = Choice.EXCLUSIVE;
	/** Several values can be selected */
	public final static int MULTIPLE = Choice.MULTIPLE;
	/** Only a single option can be selected - it is selected automatically when pressing FIRE. */
	public final static int IMPLICIT = Choice.IMPLICIT;
	
	private final FilteredList filteredList;
	private boolean[] lastChoices;
	private String lastFilterText;
	private String nullText;
	private String delimiter = ", ";
	
	/**
	 * Creates a new FilteredChoiceGroup.
	 * 
	 * @param label the label of the group
	 * @param nullText the text that should be displayed when no item has been selected
	 * @param listType the type of the list, e.g. Choice.MULTIPLE, Choice.EXCLUSIVE or Choice.IMPLICIT
	 */
	public FilteredChoiceGroup(String label, String nullText, int listType ) {
		this(label, nullText, listType, null);
	}

	/**
	 * Creates a new FilteredChoiceGroup.
	 * 
	 * @param label the label of the group
	 * @param nullText the text that should be displayed when no item has been selected
	 * @param listType the type of the list, e.g. Choice.MULTIPLE, Choice.EXCLUSIVE or Choice.IMPLICIT
	 * @param style the style of this group
	 */
	public FilteredChoiceGroup(String label, String nullText, int listType, Style style) {
		super(label, nullText, Item.INTERACTIVE, style);
		//#style filteredlist?
		this.filteredList = new FilteredList( label, listType );
		if (listType == Choice.IMPLICIT) {
			this.filteredList.setSelectCommand( StyleSheet.OK_CMD );
		} else {
			this.filteredList.addCommand( StyleSheet.OK_CMD );
		}
		this.filteredList.addCommand( StyleSheet.CANCEL_CMD );
		this.filteredList.setCommandListener( this );
		this.nullText = nullText;
	}
	

	/**
	 * Sets the text of the filter element.
	 * 
	 * @param text the text that is should be entered into the filter field.
	 */
	public void setFilterText( String text ) {
		this.filteredList.setFilterText(text);
	}
	
	/**
	 * Retrieves the text from the filter element.
	 * 
	 * @return the text that is currently entered into the filter field.
	 */
	public String getFilterText() {
		return this.filteredList.getFilterText();
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#append(java.lang.String, javax.microedition.lcdui.Image)
	 */
	public int append(String stringPart, Image imagePart) {
		return this.filteredList.append( stringPart, imagePart );
	}

	/**
	 * Appends an item to this group.
	 * 
	 * @param stringPart the string part of the element to be added
	 * @param imagePart the image part of the element to be added, or null if there is no image part
	 * @param itemStyle the style for the new list element.
	 * @return the assigned index of the element
	 * @throws NullPointerException if stringPart is null
	 */
	public int append(String stringPart, Image imagePart, Style itemStyle ) {
		return this.filteredList.append( stringPart, imagePart, itemStyle );
	}
	
	/**
	 * Appends an item to this group.
	 * 
	 * @param item the choice item to be added
	 * @return the assigned index of the element
	 * @throws NullPointerException if item is null
	 */
	public int append(ChoiceItem item) {
		return this.filteredList.append(item);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#delete(int)
	 */
	public void delete(int elementNum) {
		this.filteredList.delete(elementNum);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#deleteAll()
	 */
	public void deleteAll() {
		this.filteredList.deleteAll();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#getFitPolicy()
	 */
	public int getFitPolicy() {
		return this.filteredList.getFitPolicy();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#getFont(int)
	 */
	public Font getFont(int elementNum) {
		return this.filteredList.getFont(elementNum);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#getImage(int)
	 */
	public Image getImage(int elementNum) {
		return this.filteredList.getImage(elementNum);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#getSelectedFlags(boolean[])
	 */
	public int getSelectedFlags(boolean[] selectedArray_return) {
		return this.filteredList.getSelectedFlags(selectedArray_return);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#getSelectedIndex()
	 */
	public int getSelectedIndex() {
		return this.filteredList.getSelectedIndex();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#getString(int)
	 */
	public String getString(int elementNum) {
		return this.filteredList.getString(elementNum);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#insert(int, java.lang.String, javax.microedition.lcdui.Image)
	 */
	public void insert(int elementNum, String stringPart, Image imagePart) {
		this.filteredList.insert(elementNum, stringPart, imagePart );
	}

	/**
	 * Inserts an element with an attached style definition
	 * @param elementNum the index at which the element is added
	 * @param stringPart the text
	 * @param imagePart the optional image
	 * @param elementStyle the associated style
	 */
	public void insert(int elementNum, String stringPart, Image imagePart, Style elementStyle) {
		this.filteredList.insert(elementNum, stringPart, imagePart, elementStyle );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#isSelected(int)
	 */
	public boolean isSelected(int elementNum) {
		return this.filteredList.isSelected(elementNum);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#set(int, java.lang.String, javax.microedition.lcdui.Image)
	 */
	public void set(int elementNum, String stringPart, Image imagePart) {
		this.filteredList.set(elementNum, stringPart, imagePart);
	}

	/**
	 * Sets an item at the specified index
	 * @param elementNum the element index
	 * @param stringPart the text
	 * @param imagePart the image
	 * @param elementStyle the associated style
	 */
	public void set(int elementNum, String stringPart, Image imagePart, Style elementStyle) {
		this.filteredList.set(elementNum, stringPart, imagePart, elementStyle);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#setFitPolicy(int)
	 */
	public void setFitPolicy(int fitPolicy) {
		this.filteredList.setFitPolicy(fitPolicy);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#setFont(int, javax.microedition.lcdui.Font)
	 */
	public void setFont(int elementNum, Font font) {
		this.filteredList.setFont(elementNum, font);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#setSelectedFlags(boolean[])
	 */
	public void setSelectedFlags(boolean[] selectedArray) {
		this.filteredList.setSelectedFlags(selectedArray);
		this.lastChoices = selectedArray;
		updateText();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#setSelectedIndex(int, boolean)
	 */
	public void setSelectedIndex(int elementNum, boolean selected) {
		this.filteredList.setSelectedIndex(elementNum, selected);
		updateText();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Choice#size()
	 */
	public int size() {
		return this.filteredList.size();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeCustomItem#handleKeyReleased(int, int)
	 */
	protected boolean handleKeyReleased(int keyCode, int gameAction) {
		if (getScreen().isGameActionFire(keyCode, gameAction)
				&& StyleSheet.display != null) 
		{
			showFilteredList( StyleSheet.display );
			return true;
		}
		return super.handleKeyReleased(keyCode, gameAction);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable disp) {
		try {
		if (disp == this.filteredList) {
			
			if (cmd == StyleSheet.OK_CMD || cmd == List.SELECT_COMMAND) {
				if (this.filteredList.containsChangesTo(this.lastChoices)) {
					  if (this.filteredList.listType != MULTIPLE) {
						  // the FilteredList will return multiple selections
						  // we need to make sure that only the focussed item is retained
						  this.filteredList.setSelectedIndex(getSelectedIndex(), true);
					  }
			         updateText();
			         this.notifyStateChanged();					
				}
			} else if (cmd == StyleSheet.CANCEL_CMD) {
				this.filteredList.setFilterText(this.lastFilterText);
				this.filteredList.setSelectedFlags(this.lastChoices);
			}
			this.lastChoices = null;
			this.lastFilterText = null;
			Screen scr = getScreen();
			if (scr != null) {
				StyleSheet.display.setCurrent(scr);
			}
		}
		} catch (Exception e) {
			//#debug error
			System.out.println("Unable to handle command " + cmd.getLabel() + e );
		}
	}

	/**
	 * 
	 */
	private void updateText() {
		if (this.filteredList.listType == Choice.MULTIPLE) {
			String selectionText = this.filteredList.toSelectionString( this.delimiter );
			if (selectionText == null) {
				setText( this.nullText );				
			} else {
				setText( selectionText );
			}
		} else {
			// exclusive or implicit list
			int index = this.filteredList.getSelectedIndex();
			if (index == -1) {
				setText( this.nullText );
			} else {
				setText( this.filteredList.getString( index ) );
			}
		}

	}
	
	/**
	 * Resets the filter text to null.
	 */
	public void resetFilter() {
		this.filteredList.setFilterText(null);
	}

	/**
	 * Shows the filtered list.
	 * @param display the display
	 */
	public void showFilteredList( Display display ) {
		if (this.filteredList.listType == Choice.IMPLICIT) {
			int selectedIdx = this.filteredList.getSelectedIndex();
			if (selectedIdx != -1) {
				ChoiceItem selectedItem = this.filteredList.getItem(selectedIdx);
				this.filteredList.focus(selectedItem);
			}
		}
		this.filteredList.setTitle( getLabel() );
		this.lastChoices = new boolean[ this.filteredList.size() ];
		this.filteredList.getSelectedFlags(this.lastChoices);
		this.lastFilterText = this.filteredList.getFilterText();
		display.setCurrent(this.filteredList);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeStringCustomItem#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.popup-style
			Style popupStyle = (Style) style.getObjectProperty( "popup-style" );
			if (popupStyle != null) {
				this.filteredList.setStyle( popupStyle );
			}
		//#endif
		//#if polish.css.filter-style
			Style filterStyle = (Style) style.getObjectProperty( "filter-style" );
			if (filterStyle != null) {
				this.filteredList.setFilterStyle( filterStyle );
			}
		//#endif
	}

	/**
	 * Retrieves the delimiter for separating text entries of a MULTIPLE FilteredChoiceGroup
	 * 
	 * @return the delimiter
	 */
	public String getDelimiter() {
	return this.delimiter;
	}
	

	/**
	 * Sets the delimiter for separating text entries of a MULTIPLE FilteredChoiceGroup - the default is ", "
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	
	

}
