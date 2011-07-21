//#condition polish.usePolishGui
/*
 * Created on Jun 27, 2007 at 11:33:57 PM.
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

import javax.microedition.lcdui.Canvas;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.util.ArrayList;

/**
 * <p>Provides several lists in tabs.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Jun 27, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TabbedList extends Screen {
	private final static int TAB_POSITION_TOP = 0;
	private final static int TAB_POSITION_BOTTOM = 1;
	/**
	 * The default select command for <code>IMPLICIT</code> <code>Lists</code>.
	 * Applications using an <code>IMPLICIT</code> <code>List</code>
	 * should set their own select command
	 * using
	 * <A HREF="../../../javax/microedition/lcdui/List.html#setSelectCommand(javax.microedition.lcdui.Command)"><CODE>setSelectCommand</CODE></A>.
	 * 
	 * <p><code>SELECT_COMMAND</code> is treated as an ordinary
	 * <code>Command</code> if it is used with other <code>Displayable</code>
	 * types.</p>
	 */
	public static Command SELECT_COMMAND = List.SELECT_COMMAND;
	
	private final Container tabTitles;
	private final ArrayList tabContainers;
	private int defaultListType;
	private int currentTabIndex;
	private int tabTitlePosition = TAB_POSITION_BOTTOM;
	private Command selectCommand = SELECT_COMMAND;

	/**
	 * @param title
	 */
	public TabbedList(String title, int defaultListType ) {
		this( title, defaultListType, null);
	}
	/**
	 * @param title
	 */
	public TabbedList(String title, int defaultListType, Style style) {
		super(title, false, style);
		this.defaultListType = defaultListType;
		this.tabTitles = new Container( true );
		this.tabTitles.isFocused = true;
		this.tabTitles.screen = this;
		this.tabContainers = new ArrayList();
		if (SELECT_COMMAND != null) {
			addCommand( SELECT_COMMAND );
		}
	}
	
	//#if polish.midp
	public int appendTab( javax.microedition.lcdui.Item item ) {
		return -1;
	}
	//#endif

	public int appendTab( String text, Image icon ) {
		return appendTab( new IconItem( text, icon ), this.defaultListType, null );
	}
	public int appendTab( String text, Image icon, Style style ) {
		return appendTab( new IconItem( text, icon, style ), this.defaultListType, null );
	}
	public int appendTab( Item item ) {
		return appendTab( item, this.defaultListType, null );
	}
	public int appendTab( Item item, Style style ) {
		return appendTab( item, this.defaultListType, style );
	}

	public int appendTab( Item item, int tabListType, Style style ) {
		this.tabTitles.add(item);
		if (style != null) {
			item.setStyle(style);
		}
		Container tabContainer = new ChoiceGroup( null, tabListType, null, null, true );
		tabContainer.screen = this;
		this.tabContainers.add(tabContainer);
		if (this.container == null) {
			this.container = tabContainer;
		}
		
		return this.tabTitles.size() - 1;
	}
	
	public void deleteTab( int tabIndex ) {
		this.tabContainers.remove(tabIndex);
		this.tabTitles.remove(tabIndex);
		if (tabIndex == this.currentTabIndex && this.tabContainers.size() != 0) {
			if (tabIndex == this.tabContainers.size()) {
				tabIndex--;
			}
			setCurrentTab( tabIndex );
		}
	}
	
	public void setCurrentTab( int tabIndex ) {
		if (this.container != null) {
			//this.container.defocus( this.style );
			this.container.hideNotify();
		}
		this.tabTitles.focusChild(tabIndex);
		ChoiceGroup group = getTab(tabIndex);
		this.container = group;
		this.currentTabIndex = tabIndex;
		group.setScrollHeight( this.contentHeight );
		if (group.style == null) {
			group.setStyleWithBackground( this.style, true );			
		}
		group.focus( group.style, 0 );
		group.isFocused = true;
		group.showNotify();
		repaint();
	}
	
	public void setTabStyle( int tabIndex ) {
		// ignore
	}

	public void setTabStyle( int tabIndex, Style style ) {
		((Item) this.tabContainers.get(tabIndex)).setStyle(style);
	}
	
	public ChoiceGroup getTab( int tabIndex ) {
		return (ChoiceGroup) this.tabContainers.get( tabIndex );
	}

	public int getTabNumber() {
		return this.tabTitles.size();
	}
	
	public void setSelectCommand( Command selectCommand ) {
		if (this.selectCommand != null) {
			removeCommand( this.selectCommand );
		}
		if (selectCommand != null) {
			addCommand( selectCommand );
		}
		this.selectCommand = selectCommand;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#calculateContentArea(int, int, int, int)
	 */
	protected void calculateContentArea(int x, int y, int width, int height) {
		super.calculateContentArea(x, y, width, height);
		if (this.tabTitlePosition == TAB_POSITION_TOP) {
			this.tabTitles.relativeY = this.contentY;
			this.contentY += this.tabTitles.getItemHeight(this.contentWidth, this.contentWidth, this.contentHeight);
		} else {
			this.tabTitles.relativeY = this.contentY + this.contentHeight - this.tabTitles.getItemHeight(this.contentWidth, this.contentWidth, this.contentHeight);
		}
		this.contentHeight -= this.tabTitles.itemHeight;
		this.tabTitles.relativeX = this.contentX;
		if (this.container != null) {
			this.container.setScrollHeight( this.contentHeight );
		}
		if (!this.isInitialized && this.tabTitles.size() > 0) {
			this.isInitialized = true;
			setCurrentTab( this.currentTabIndex );
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		boolean handled;
		if (gameAction == Canvas.LEFT || gameAction == Canvas.RIGHT) {
			handled = this.tabTitles.handleKeyPressed(keyCode, gameAction);
			if (handled) {
				setCurrentTab( this.tabTitles.getFocusedIndex() );
				// TODO notify TabListener
				return true;
			}
		}
		handled = super.handleKeyPressed(keyCode, gameAction);
		if (!handled && isGameActionFire(keyCode, gameAction) && ((ChoiceGroup)this.container).choiceType == Choice.IMPLICIT) {
			Command command = this.selectCommand;
			if (command == null) {
				command = SELECT_COMMAND;
				if (command == null) {
					command = List.SELECT_COMMAND;
				}
			}
			callCommandListener( command );
			handled  = true;
		}
		return handled;
	}
//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.Screen#handleKeyReleased(int, int)
//	 */
//	protected boolean handleKeyReleased(int keyCode, int gameAction) {
//		// TODO robertvirkus implement handleKeyReleased
//		return super.handleKeyReleased(keyCode, gameAction);
//	}
//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.Screen#handleKeyRepeated(int, int)
//	 */
//	protected boolean handleKeyRepeated(int keyCode, int gameAction) {
//		// TODO robertvirkus implement handleKeyRepeated
//		return super.handleKeyRepeated(keyCode, gameAction);
//	}
//	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerPressed(int x, int y) {
		return this.tabTitles.handlePointerPressed(x-this.tabTitles.relativeX, y-this.tabTitles.relativeY) 
				|| super.handlePointerPressed(x, y);
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handlePointerReleased(int, int, ClippingRegion)
	 */
	protected boolean handlePointerDragged(int x, int y, ClippingRegion repaintRegion) {
		Container cont = this.tabTitles;
		return cont.handlePointerDragged(x-cont.relativeX, y-cont.relativeY, repaintRegion) 
				|| super.handlePointerDragged(x, y, repaintRegion);
	}
	//#endif

	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handlePointerReleased(int, int)
	 */
	protected boolean handlePointerReleased(int x, int y) {
		boolean releaseHandled = this.tabTitles.handlePointerReleased(x-this.tabTitles.relativeX, y-this.tabTitles.relativeY);
		if (releaseHandled) {
			int index = this.tabTitles.getFocusedIndex();
			setCurrentTab( index );
		}
		return  releaseHandled || super.handlePointerReleased(x, y);
	}
	//#endif

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#paintScreen(javax.microedition.lcdui.Graphics)
	 */
	protected void paintScreen(Graphics g) {
		this.tabTitles.paint( this.tabTitles.relativeX, this.tabTitles.relativeY, this.contentX, this.contentX + this.contentWidth, g );
		super.paintScreen(g);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		//#if polish.css.tabbar-style
			Style tabbarStyle = (Style) style.getObjectProperty("tabbar-style");
			if (tabbarStyle != null) {
				this.tabTitles.setStyle(tabbarStyle);
			}
		//#endif
		//#if polish.css.tabbar-position
			Integer tabbarPos = style.getIntProperty("tabbar-position");
			if (tabbarPos != null) {
				this.tabTitlePosition = tabbarPos.intValue();
			}
		//#endif
		super.setStyle(style);
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#animate(long,ClippingRegion)
	 */
	public void animate( long currentTime, ClippingRegion repaintRegion ) {
		super.animate(currentTime,  repaintRegion);
		this.tabTitles.animate(currentTime,  repaintRegion);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#getRootItems()
	 */
	protected Item[] getRootItems() {
		return new Item[]{ this.tabTitles };
	}
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#hideNotify()
	 */
	public void hideNotify() {
		super.hideNotify();
		this.tabTitles.hideNotify();
	}
	
	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#createCssSelector()
	 */
	protected String createCssSelector() {
		return "tabbedlist";
	}
	//#endif

	/**
	 * Gets the number of elements present.
	 * 
	 * @param tabIndex the index of the tab
	 * @return the number of elements in the Choice
	 */
	public int size(int tabIndex) {
		return getTab(tabIndex).size();
	}

	/**
	 * Gets the <code>String</code> part of the element referenced by
	 * <code>elementNum</code>.
	 * The <code>elementNum</code> parameter must be within the range
	 * <code>[0..size()-1]</code>, inclusive.
	 * 
	 * @param tabIndex the index of the tab
	 * @param elementNum the index of the element to be queried
	 * @return the string part of the element
	 * @throws IndexOutOfBoundsException - if elementNum is invalid
	 * @see #getImage(int, int)
	 */
	public String getString(int tabIndex, int elementNum) {
		return getTab(tabIndex).getString(elementNum);
	}

	/**
	 * Gets the <code>Image</code> part of the element referenced by
	 * <code>elementNum</code>.
	 * The <code>elementNum</code> parameter must be within the range
	 * <code>[0..size()-1]</code>, inclusive.
	 * 
	 * @param tabIndex the index of the tab
	 * @param elementNum the index of the element to be queried
	 * @return the image part of the element, or null if there is no image
	 * @throws IndexOutOfBoundsException - if elementNum is invalid
	 * @see #getString(int, int)
	 */
	public Image getImage(int tabIndex, int elementNum) {
		return getTab(tabIndex).getImage(elementNum);
	}

	/**
	 * Appends an element to the <code>Choice</code>. The added
	 * element will be the last
	 * element of the <code>Choice</code>. The size of the
	 * <code>Choice</code> grows by one.
	 * 
	 * @param tabIndex the index of the tab
	 * @param stringPart the string part of the element to be added
	 * @param imagePart the image part of the element to be added,  or null if there is no image part
	 * @return the assigned index of the element
	 * @throws NullPointerException - if stringPart is null
	 */
	public int append( int tabIndex, String stringPart, Image imagePart) {
		return append( tabIndex, new ChoiceItem( stringPart, imagePart, this.defaultListType, null), null );
	}

	/**
	 * Appends an element to the <code>Choice</code>. The added
	 * element will be the last
	 * element of the <code>Choice</code>. The size of the
	 * <code>Choice</code> grows by one.
	 * 
	 * @param tabIndex the index of the tab
	 * @param stringPart the string part of the element to be added
	 * @param imagePart the image part of the element to be added,  or null if there is no image part
	 * @return the assigned index of the element
	 * @throws NullPointerException - if stringPart is null
	 */
	public int append( int tabIndex, String stringPart, Image imagePart, Style style) {
		return append( tabIndex, new ChoiceItem( stringPart, imagePart, this.defaultListType, style), null );
	}
	
	/**
	 * @param tabIndex the index of the tab
	 * @param item
	 * @param style
	 * @return
	 */
	private int append(int tabIndex, ChoiceItem item, Style style) {
		ChoiceGroup group = getTab(tabIndex);
		item.setChoiceType( group.choiceType );
		return group.append(item, style);
	}
	
	/**
	 * Inserts an element into the <code>Choice</code> just prior to the element specified.
	 * The size of the <code>Choice</code> grows by one.
	 * The <code>elementNum</code> parameter must be within the range
	 * <code>[0..size()]</code>, inclusive.  The index of the last
	 * element is <code>size()-1</code>, and
	 * so there is actually no element whose index is
	 * <code>size()</code>. If this value
	 * is used for <code>elementNum</code>, the new element is
	 * inserted immediately after
	 * the last element. In this case, the effect is identical to
	 * <A HREF="../../../javax/microedition/lcdui/Choice.html#append(java.lang.String, javax.microedition.lcdui.Image)"><CODE>append()</CODE></A>.
	 * 
	 * @param tabIndex the index of the tab
	 * @param elementNum the index of the element where insertion is to occur
	 * @param stringPart the string part of the element to be inserted
	 * @param imagePart the image part of the element to be inserted, or null if there is no image part
	 * @throws IndexOutOfBoundsException - if elementNum is invalid
	 * @throws NullPointerException - if stringPart is null
	 */
	public void insert(int tabIndex, int elementNum, String stringPart, Image imagePart) {
		insert( tabIndex, elementNum, new ChoiceItem( stringPart, imagePart, this.defaultListType, null ), null );
	}
	
	/**
	 * Inserts an element into the <code>Choice</code> just prior to the element specified.
	 * The size of the <code>Choice</code> grows by one.
	 * The <code>elementNum</code> parameter must be within the range
	 * <code>[0..size()]</code>, inclusive.  The index of the last
	 * element is <code>size()-1</code>, and
	 * so there is actually no element whose index is
	 * <code>size()</code>. If this value
	 * is used for <code>elementNum</code>, the new element is
	 * inserted immediately after
	 * the last element. In this case, the effect is identical to
	 * <A HREF="../../../javax/microedition/lcdui/Choice.html#append(java.lang.String, javax.microedition.lcdui.Image)"><CODE>append()</CODE></A>.
	 * 
	 * @param tabIndex the index of the tab
	 * @param elementNum the index of the element where insertion is to occur
	 * @param stringPart the string part of the element to be inserted
	 * @param imagePart the image part of the element to be inserted, or null if there is no image part
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @throws NullPointerException if stringPart is null
	 */
	public void insert(int tabIndex, int elementNum, String stringPart, Image imagePart, Style style) {
		insert( tabIndex, elementNum, new ChoiceItem( stringPart, imagePart, this.defaultListType, style ), null );		
	}
	
	/**
	 * Inserts an element into the <code>Choice</code> just prior to the element specified.
	 * The size of the <code>Choice</code> grows by one.
	 * The <code>elementNum</code> parameter must be within the range
	 * <code>[0..size()]</code>, inclusive.  The index of the last
	 * element is <code>size()-1</code>, and
	 * so there is actually no element whose index is
	 * <code>size()</code>. If this value
	 * is used for <code>elementNum</code>, the new element is
	 * inserted immediately after
	 * the last element. In this case, the effect is identical to
	 * <A HREF="../../../javax/microedition/lcdui/Choice.html#append(java.lang.String, javax.microedition.lcdui.Image)"><CODE>append()</CODE></A>.
	 * 
	 * @param tabIndex the index of the tab
	 * @param elementNum the index of the element where insertion is to occur
	 * @param item the item
	 * @param style the style oft the item
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @throws NullPointerException if stringPart is null
	 */
	public void insert(int tabIndex, int elementNum, ChoiceItem item, Style style) {
		getTab(tabIndex).insert(elementNum, item, style);		
	}

	/**
	 * Deletes the element referenced by <code>elementNum</code>.
	 * The size of the <code>Choice</code> shrinks by
	 * one. It is legal to delete all elements from a <code>Choice</code>.
	 * The <code>elementNum</code> parameter must be within the range
	 * <code>[0..size()-1]</code>, inclusive.
	 * 
	 * @param tabIndex the index of the tab
	 * @param elementNum the index of the element to be deleted
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 */
	public void delete(int tabIndex, int elementNum) {
		getTab( tabIndex ).delete(elementNum);
	}

	/**
	 * Deletes all elements from this <code>Choice</code>, leaving it
	 * with zero elements.
	 * This method does nothing if the <code>Choice</code> is already empty.
	 * 
	 * @param tabIndex the index of the tab
	 * @since  MIDP 2.0
	 */
	public void deleteAll(int tabIndex) {
		getTab( tabIndex ).deleteAll();
	}

	/**
	 * Sets the <code>String</code> and <code>Image</code> parts of the element referenced by <code>elementNum</code>,
	 * replacing the previous contents of the element.
	 * The <code>elementNum</code> parameter must be within the range
	 * <code>[0..size()-1]</code>, inclusive.  The font attribute of
	 * the element is left unchanged.
	 * 
	 * @param tabIndex the index of the tab
	 * @param elementNum the index of the element to be set
	 * @param stringPart the string part of the new element
	 * @param imagePart the image part of the element, or  null if there is no image part
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @throws NullPointerException if stringPart is null
	 */
	public void set(int tabIndex, int elementNum, String stringPart, Image imagePart) {
		set( tabIndex, elementNum, new ChoiceItem( stringPart, imagePart, this.defaultListType, null), null);
	}
	
	/**
	 * Sets the <code>String</code> and <code>Image</code> parts of the element referenced by <code>elementNum</code>,
	 * replacing the previous contents of the element.
	 * The <code>elementNum</code> parameter must be within the range
	 * <code>[0..size()-1]</code>, inclusive.  The font attribute of
	 * the element is left unchanged.
	 * 
	 * @param tabIndex the index of the tab
	 * @param elementNum the index of the element to be set
	 * @param stringPart the string part of the new element
	 * @param imagePart the image part of the element, or  null if there is no image part
	 * @param elementStyle the style of the new element
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @throws NullPointerException if stringPart is null
	 */
	public void set(int tabIndex, int elementNum, String stringPart, Image imagePart, Style elementStyle ) {
		set( tabIndex, elementNum, new ChoiceItem( stringPart, imagePart, this.defaultListType, elementStyle), null);
	}
	
	/**
	 * Sets the <code>String</code> and <code>Image</code> parts of the element referenced by <code>elementNum</code>,
	 * replacing the previous contents of the element.
	 * The <code>elementNum</code> parameter must be within the range
	 * <code>[0..size()-1]</code>, inclusive.  The font attribute of
	 * the element is left unchanged.
	 * 
	 * @param tabIndex the index of the tab
	 * @param elementNum the index of the element to be set
	 * @param item the element
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @throws NullPointerException if stringPart is null
	 */
	public void set(int tabIndex, int elementNum, ChoiceItem item ) {
		set( tabIndex, elementNum, item, null );
	}
	
	/**
	 * Sets the <code>String</code> and <code>Image</code> parts of the element referenced by <code>elementNum</code>,
	 * replacing the previous contents of the element.
	 * The <code>elementNum</code> parameter must be within the range
	 * <code>[0..size()-1]</code>, inclusive.  The font attribute of
	 * the element is left unchanged.
	 * 
	 * @param tabIndex the index of the tab
	 * @param elementNum the index of the element to be set
	 * @param item the element
	 * @param elementStyle the style of the item
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @throws NullPointerException if stringPart is null
	 */
	public void set(int tabIndex, int elementNum, ChoiceItem item, Style elementStyle ) {
		ChoiceGroup group = getTab(tabIndex);
		item.setChoiceType( group.choiceType );
		group.set(elementNum, item, elementStyle );
	}


	/**
	 * Gets a boolean value indicating whether this element is selected.
	 * The <code>elementNum</code> parameter must be within the range
	 * <code>[0..size()-1]</code>, inclusive.
	 * 
	 * @param tabIndex the index of the tab
	 * @param elementNum the index of the element to be queried
	 * @return selection state of the element
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 */
	public boolean isSelected(int tabIndex, int elementNum) {
		return getTab(tabIndex).isSelected(elementNum);
	}

	/**
	 * Returns the index number of an element in the <code>Choice</code> that is
	 * selected. For
	 * <code>Choice</code> types <code>EXCLUSIVE</code>,
	 * <code>POPUP</code>, and <code>IMPLICIT</code>
	 * there is at most one element selected, so
	 * this method is useful for determining the user's
	 * choice. Returns <code>-1</code> if
	 * the <code>Choice</code> has no elements (and therefore has no
	 * selected elements).
	 * 
	 * <p>For <code>MULTIPLE</code>, this always returns
	 * <code>-1</code> because no single
	 * value can in general represent the state of such a <code>Choice</code>.
	 * To get the complete state of a <code>MULTIPLE</code> <code>Choice</code>,
	 * see <A HREF="../../../javax/microedition/lcdui/Choice.html#getSelectedFlags(boolean[])"><CODE>getSelectedFlags</CODE></A>.</p>
	 * 
	 * @param tabIndex the index of the tab
	 * @return index of selected element, or -1 if none
	 * @see #setSelectedIndex(int, int, boolean)
	 */
	public int getSelectedIndex(int tabIndex) {
		return getTab(tabIndex).getSelectedIndex();
	}

	/**
	 * Queries the state of a <code>Choice</code> and returns the state of all elements in the boolean array
	 * <code>selectedArray_return</code>. <strong>Note:</strong> this
	 * is a result parameter.
	 * It must be at least as long as the size
	 * of the <code>Choice</code> as returned by <code>size()</code>.
	 * If the array is longer, the extra
	 * elements are set to <code>false</code>.
	 * 
	 * <p>This call is valid for all types of
	 * <code>Choices</code>. For <code>MULTIPLE</code>, any
	 * number of elements may be selected and set to <code>true</code>
	 * in the result
	 * array. For <code>EXCLUSIVE</code>, <code>POPUP</code>, and
	 * <code>IMPLICIT</code>
	 * exactly one element will be selected (unless there are
	 * zero elements in the <code>Choice</code>). </p>
	 * 
	 * @param tabIndex the index of the tab
	 * @param selectedArray_return - array to contain the results
	 * @return the number of selected elements in the Choice
	 * @throws IllegalArgumentException - if selectedArray_return is shorter than the size of the Choice.
	 * @throws NullPointerException - if selectedArray_return is null
	 * @see #setSelectedFlags(int, boolean[])
	 */
	public int getSelectedFlags(int tabIndex, boolean[] selectedArray_return) {
		return getTab(tabIndex).getSelectedFlags(selectedArray_return);
	}

	/**
	 * For <code>MULTIPLE</code>, this simply sets an individual element's selected state.
	 * 
	 * <P>For <code>EXCLUSIVE</code> and <code>POPUP</code>,
	 * this can be used only to select any
	 * element, that is, the <code> selected </code> parameter must be <code>
	 * true </code>. When an element is selected, the previously
	 * selected element
	 * is deselected. If <code> selected </code> is <code> false </code>, this
	 * call is ignored. If element was already selected, the call has
	 * no effect.</P>
	 * 
	 * <P>For <code>IMPLICIT</code>,
	 * this can be used only to select any
	 * element, that is, the <code> selected </code> parameter must be <code>
	 * true </code>. When an element is selected, the previously
	 * selected element
	 * is deselected. If <code> selected </code> is <code> false </code>, this
	 * call is ignored. If element was already selected, the call has
	 * no effect.</P>
	 * 
	 * <P>The call to <code>setSelectedIndex</code> does not cause
	 * implicit activation of
	 * any <code>Command</code>.
	 * </P>
	 * 
	 * <p>For all list types, the <code>elementNum</code> parameter
	 * must be within the range
	 * <code>[0..size()-1]</code>, inclusive. </p>
	 * 
	 * @param tabIndex the index of the tab
	 * @param elementNum the index of the element, starting from zero
	 * @param selected the state of the element, where true means selected and false means not selected
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @see #getSelectedIndex(int)
	 */
	public void setSelectedIndex(int tabIndex, int elementNum, boolean selected) {
		getTab(tabIndex).setSelectedIndex(elementNum, selected);
	}

	/**
	 * Attempts to set the selected state of every element in the <code>Choice</code>.
	 * The array
	 * must be at least as long as the size of the
	 * <code>Choice</code>. If the array is
	 * longer, the additional values are ignored.
	 * 
	 * <p>For <code>Choice</code> objects of type
	 * <code>MULTIPLE</code>, this sets the selected
	 * state of every
	 * element in the <code>Choice</code>. An arbitrary number of
	 * elements may be selected.
	 * </p>
	 * 
	 * <p>For <code>Choice</code> objects of type
	 * <code>EXCLUSIVE</code>, <code>POPUP</code>,
	 * and <code>IMPLICIT</code>, exactly one array
	 * element must have the value <code>true</code>. If no element is
	 * <code>true</code>, the
	 * first element
	 * in the <code>Choice</code> will be selected. If two or more
	 * elements are <code>true</code>, the
	 * implementation will choose the first <code>true</code> element
	 * and select it. </p>
	 * 
	 * @param tabIndex the index of the tab
	 * @param selectedArray an array in which the method collect the selection status
	 * @throws IllegalArgumentException if selectedArray is shorter than the size of the Choice
	 * @throws NullPointerException if selectedArray is null
	 * @see #getSelectedFlags(int, boolean[])
	 */
	public void setSelectedFlags(int tabIndex, boolean[] selectedArray) {
		getTab(tabIndex).setSelectedFlags(selectedArray);
	}
	/**
	 * Retrieves the index of the currently selected tab.
	 * 
	 * @return the index of the current tab
	 */
	public int getCurrentTabIndex() {
		return this.currentTabIndex;
	}
}
