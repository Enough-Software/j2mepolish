//#condition polish.usePolishGui
/*
 * Created on 31-Jan-2006 at 00:04:45.
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

import de.enough.polish.event.EventManager;
import de.enough.polish.event.UiEventListener;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;

/**
 * <p>Allows to access J2ME Polish specific features in a standard compliant way.</p>
 * <p>When a ScreenStateListener is registered with a screen, it will get notified when
 *    the screen changes its focus or another internal state (like changing a tab in the TabbedForm).
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        31-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class UiAccess {
	
	/**
	 * The bit representing the UP key.  This constant has a value of
	 * <code>0x0002</code> (1 << Canvas.UP).
	 * @see #getKeyStates() 
	 */
	public static final int UP_PRESSED = 0x0002;

	/**
	 * The bit representing the DOWN key.  This constant has a value of
	 * <code>0x0040</code> (1 << Canvas.DOWN).
	 * @see #getKeyStates() 
	 */
	public static final int DOWN_PRESSED = 0x0040;

	/**
	 * The bit representing the LEFT key.  This constant has a value of
	 * <code>0x0004</code> (1 << Canvas.LEFT).
	 * @see #getKeyStates() 
	 */
	public static final int LEFT_PRESSED = 0x0004;

	/**
	 * The bit representing the RIGHT key.  This constant has a value of
	 * <code>0x0020</code> (1 << Canvas.RIGHT).
	 * @see #getKeyStates() 
	 */
	public static final int RIGHT_PRESSED = 0x0020;

	/**
	 * The bit representing the FIRE key.  This constant has a value of
	 * <code>0x0100</code> (1 << Canvas.FIRE).
	 * @see #getKeyStates() 
	 */
	public static final int FIRE_PRESSED = 0x0100;

	/**
	 * The bit representing the GAME_A key (may not be supported on all
	 * devices).  This constant has a value of
	 * <code>0x0200</code> (1 << Canvas.GAME_A).
	 * @see #getKeyStates() 
	 */
	public static final int GAME_A_PRESSED = 0x0200;

	/**
	 * The bit representing the GAME_B key (may not be supported on all
	 * devices).  This constant has a value of
	 * <code>0x0400</code> (1 << Canvas.GAME_B).
	 * @see #getKeyStates() 
	 */
	public static final int GAME_B_PRESSED = 0x0400;

	/**
	 * The bit representing the GAME_C key (may not be supported on all
	 * devices).  This constant has a value of
	 * <code>0x0800</code> (1 << Canvas.GAME_C).
	 * @see #getKeyStates() 
	 */
	public static final int GAME_C_PRESSED = 0x0800;

	/**
	 * The bit representing the GAME_D key (may not be supported on all
	 * devices).  This constant has a value of
	 * <code>0x1000</code> (1 << Canvas.GAME_D).
	 * @see #getKeyStates() 
	 */
	public static final int GAME_D_PRESSED = 0x1000;
	
	/**
	 * A constant for setting the input mode of an TextField to lowercase.
	 * @see #setInputMode(javax.microedition.lcdui.TextField, int)
	 */
	public static final int MODE_LOWERCASE = 0;
	/**
	 * A constant for setting the input mode of an TextField to uppercase for the first character, followed by lowercase characters.
	 * @see #setInputMode(javax.microedition.lcdui.TextField, int)
	 */
	public static final int MODE_FIRST_UPPERCASE = 1; // only the first character should be written in uppercase
	/**
	 * A constant for setting the input mode of an TextField to uppercase.
	 * @see #setInputMode(javax.microedition.lcdui.TextField, int)
	 */
	public static final int MODE_UPPERCASE = 2;
	/**
	 * A constant for setting the input mode of an TextField to numbers.
	 * @see #setInputMode(javax.microedition.lcdui.TextField, int)
	 */
	public static final int MODE_NUMBERS = 3;
	/**
	 * A constant for setting the input mode of an TextField to it't native input - that's useful for using T9 and similar input helpers.
	 * @see #setInputMode(javax.microedition.lcdui.TextField, int)
	 */
	public static final int MODE_NATIVE = 4;
	
	/**
	 * A constant for using the FIXED_POINT_DECIMAL constraint on TextFields.
	 * Sample usage:
	 * <pre>
	 * TextField cashRegister = new TextField("Price: ",  null, 5, UiAccess.CONSTRAINT_FIXED_POINT_DECIMAL );
	 * </pre>
	 * @see TextField#FIXED_POINT_DECIMAL
	 * @see #setNumberOfDecimalFractions(TextField, int)
	 * @see #getNumberOfDecimalFractions(TextField)
	 */
	public static final int CONSTRAINT_FIXED_POINT_DECIMAL =
		//#if polish.usePolishGui
			TextField.FIXED_POINT_DECIMAL
		//#else
			//# 20
		//#endif
	;
	
	private static HashMap attributes;

	/**
	 * No instantiation is allowd.
	 */
	private UiAccess() {
		super();
	}
	
	/**
	 * Gets the states of the physical game keys.  
	 * Each bit in the returned
	 * integer represents a specific key on the device.  A key's bit will be
	 * 1 if the key is currently down or has been pressed at least once since
	 * the last time this method was called.  The bit will be 0 if the key
	 * is currently up and has not been pressed at all since the last time
	 * this method was called.  This latching behavior ensures that a rapid
	 * key press and release will always be caught by an application loop,
	 * regardless of how slowly the loop runs.
	 * <p>
	 * For example:
	 * <code>
	 * <pre>
	 * 
	 * // Get the key state and store it
	 * int keyState = getKeyStates();
	 * if ((keyState & UiAccess.LEFT_KEY) != 0) {
	 * 		positionX--;
	 * }
	 * else if ((keyState & UiAccess.RIGHT_KEY) != 0) {
	 * 		positionX++;
	 * }
	 * 
	 * </pre>
	 * </code>
	 * <p>
	 * Calling this method has the side effect of clearing any latched state.
	 * Another call to getKeyStates immediately after a prior call will
	 * therefore report the system's best idea of the current state of the
	 * keys, the latched bits having been cleared by the first call.
	 * <p>
	 * On J2ME Polish this method is implemented by monitoring key press and
	 * release events.  Thus the state reported by getKeyStates might
	 * lag the actual state of the physical keys since the timeliness
	 * of the key information is be subject to the capabilities of each
	 * device.  Also, some devices may be incapable of detecting simultaneous
	 * presses of multiple keys.
	 * <p>
	 * This method returns 0 unless the GameCanvas is currently visible as
	 * reported by <A HREF="../../../../javax/microedition/lcdui/Displayable.html#isShown()"><CODE>Displayable.isShown()</CODE></A>.
	 * Upon becoming visible, a GameCanvas will initially indicate that
	 * all keys are unpressed (0); if a key is held down while the GameCanvas
	 * is being shown, the key must be first released and then pressed in
	 * order for the key press to be reported by the GameCanvas.
	 * <p>
	 * 
	 * @return An integer containing the key state information (one bit per  key), or 0 if the J2ME Polish is not used or there is no current screen
	 * @see UiAccess#UP_PRESSED
	 * @see UiAccess#DOWN_PRESSED
	 * @see UiAccess#LEFT_PRESSED
	 * @see UiAccess#RIGHT_PRESSED
	 * @see UiAccess#FIRE_PRESSED
	 * @see UiAccess#GAME_A_PRESSED
	 * @see UiAccess#GAME_B_PRESSED
	 * @see UiAccess#GAME_C_PRESSED
	 * @see UiAccess#GAME_D_PRESSED
	 */
	public static  int getKeyStates()
	{
		//#if polish.usePolishGui
		Screen screen = StyleSheet.currentScreen;
		if (screen != null) {
			return screen.getKeyStates();
		}
		//#endif
		return 0;
	}
	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Registers a ScreenStateListener to any J2ME Polish screen.
	 * 
	 * @param screen the screen
	 * @param listener the listener
	 */
	public static void setScreenStateListener( javax.microedition.lcdui.Screen screen, ScreenStateListener listener ) {
		// ignore, just for being able to use the ScreenStateListener along with a normal screen.
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Registers a ScreenStateListener to any J2ME Polish screen.
	 * 
	 * @param screen the screen
	 * @param listener the listener
	 */
	public static void setScreenStateListener( Screen screen, ScreenStateListener listener ) {
		screen.setScreenStateListener( listener );
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the focused index of the specified screen
	 * 
	 * @param screen the screen
	 * @return the focused index, -1 when it is not known
	 */
	public static int getFocusedIndex( javax.microedition.lcdui.Screen screen ) {
		return -1;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves the focused index of the specified screen
	 * 
	 * @param screen the screen
	 * @return the focused index, -1 when it is not known
	 */
	public static int getFocusedIndex( Screen screen ) {
		if (screen.container != null) {
			return screen.container.getFocusedIndex();
		}
		return -1;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the currently focused item of the specified ChoiceGroup
	 * @param group the group
	 * @return the index of the currently focused item, -1 if none is focused
	 */
	public static int getFocusedIndex(ChoiceGroup group)
	{
		return group.getFocusedIndex();
	}
	//#endif

	//#if polish.midp
	/**
	 * Retrieves the currently focused item of the specified ChoiceGroup
	 * @param group the group
	 * @return the index of the currently focused item, -1 if none is focused
	 */
	public static int getFocusedIndex(javax.microedition.lcdui.ChoiceGroup group)
	{
		return -1;
	}
	//#endif


	//#if polish.midp
	/**
	 * Retrieves the focused index of the specified screen
	 * 
	 * @param screen the screen
	 * @return the focused item, null when it is not known
	 */
	public static javax.microedition.lcdui.Item getFocusedItem( javax.microedition.lcdui.Screen screen ) {
		return null;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves the focused index of the specified screen
	 * 
	 * @param screen the screen
	 * @return the focused item, null when it is not known
	 */
	public static Item getFocusedItem( Screen screen ) {
		return screen.getCurrentItem();
	}
	//#endif

	//#if polish.midp
	/**
	 * Sets the focused index of the specified screen
	 * 
	 * @param screen the screen
	 * @param index the focused index, -1 when none should be focused
	 */
	public static void setFocusedIndex( javax.microedition.lcdui.Screen screen, int index ) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the focused index of the specified screen
	 * 
	 * @param screen the screen
	 * @param index the focused index, -1 when none should be focused
	 */
	public static void setFocusedIndex( Screen screen, int index ) {
		screen.focus( index );
	}
	//#endif

	//#if polish.midp
	/**
	 * Sets the focused item of the specified screen
	 * 
	 * @param screen the screen
	 * @param item the focused item, null when none should be focused
	 */
	public static void setFocusedItem( javax.microedition.lcdui.Screen screen, javax.microedition.lcdui.Item item ) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the focused item of the specified screen
	 * 
	 * @param screen the screen
	 * @param item the focused item, null when none should be focused
	 */
	public static void setFocusedItem( Screen screen, Item item ) {
		screen.focus(item);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets the title of the screen using an Item.
	 * <b>important</b>: you cannot call screen.setTitle(String) afterwards anymore!
	 * 
	 * @param screen the screen for which the title should be replaced 
	 * @param title the element responsible for painting the title.
	 * @see #getTitleItem(javax.microedition.lcdui.Screen)
	 */
	public static void setTitle( javax.microedition.lcdui.Screen screen, javax.microedition.lcdui.Item title ) {
		// this is ignored.
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the title of the screen using an Item.
	 * <b>important</b>: you cannot call screen.setTitle(String) afterwards anymore!
	 * 
	 * @param screen the screen for which the title should be replaced 
	 * @param title the element responsible for painting the title.
	 * @see #getTitleItem(Screen)
	 */
	public static void setTitle( Screen screen, Item title ) {
		screen.setTitle( title );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the title of the screen using an Item.
	 * <b>important</b>: you cannot call screen.setTitle(String) afterwards anymore!
	 * This method is meant to be used in conjunction with the //#style preprocessing directive.
	 * 
	 * @param screen the screen for which the title should be replaced 
	 * @param title the element responsible for painting the title.
	 * @param style the style for the title
	 * @see #getTitleItem(Screen)
	 */
	public static void setTitle( Screen screen, Item title, Style style ) {
		if (style != null) {
			title.setStyle(style);
		}
		screen.setTitle( title );
	}
	//#endif

	//#if polish.usePolishGui && polish.midp
	/**
	 * Sets the title of the screen using an Item.
	 * <b>important</b>: you cannot call screen.setTitle(String) afterwards anymore!
	 * 
	 * @param screen the screen for which the title should be replaced 
	 * @param title the element responsible for painting the title.
	 * @see #getTitleItem(javax.microedition.lcdui.Screen)
	 */
	public static void setTitle( javax.microedition.lcdui.Screen screen, Item title ) {
		// ignore
	}
	public static void setTitle(Screen screen, javax.microedition.lcdui.Item title ) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui && polish.midp
	/**
	 * Sets the title of the screen using an Item.
	 * <b>important</b>: you cannot call screen.setTitle(String) afterwards anymore!
	 * This method is meant to be used in conjunction with the //#style preprocessing directive.
	 * 
	 * @param screen the screen for which the title should be replaced 
	 * @param title the element responsible for painting the title.
	 * @param style the style for the title
	 * @see #getTitleItem(javax.microedition.lcdui.Screen)
	 */
	public static void setTitle( javax.microedition.lcdui.Screen screen, Item title, Style style ) {
		// ignore
	}
	//#endif


	//#if polish.midp
	/**
	 * Sets the fullscreen mode of the screen.
	 * The title and the menubar will be hidden by this call.
	 * 
	 * @param screen the screen
	 * @param fullScreen true when the fullscreen mode should be entered
	 */
	public static void setFullScreenMode( javax.microedition.lcdui.Screen screen, boolean fullScreen ) {
		// this is ignored.
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the fullscreen mode of the screen.
	 * The title and the menubar will be hidden by this call.
	 * 
	 * @param screen the screen
	 * @param fullScreen true when the fullscreen mode should be entered
	 */
	public static void setFullScreenMode( Screen screen, boolean fullScreen ) {
		screen.setFullScreenMode(fullScreen);
	}
	//#endif

	
	//#if polish.midp
	/**
	 * Adds a command to a list item.
	 * Warning: this method won't add any commands when the J2ME Polish GUI is not activated.
	 * 
	 * @param list the list
	 * @param index the index of the item
	 * @param command the item command
	 */
	public static void addItemCommand( javax.microedition.lcdui.List list, int index, Command command ) {
		// ignore on real lists
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Adds a command to a list item.
	 * Warning: this method won't add any commands when the J2ME Polish GUI is not activated.
	 * 
	 * @param list the list
	 * @param index the index of the item
	 * @param command the item command
	 */
	public static void addItemCommand( List list, int index, Command command ) {
		Item item = list.getItem(index);
		item.addCommand(command);
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets the parent for the given child item.
	 * 
	 * @param child the child
	 * @param parent the parent
	 */
	public static void setParent( Item child, Item parent ) {
		child.parent = parent;
	}
	//#endif
	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Sets the parent for the given child item.
	 * 
	 * @param child the child
	 * @param parent the parent
	 */
	public static void setParent( Item child, javax.microedition.lcdui.Item parent ) {
		// ignore
	}
	//#endif

	//#if polish.midp
	/**
	 * Sets the parent for the given child item.
	 * 
	 * @param child the child
	 * @param parent the parent
	 */
	public static void setParent( javax.microedition.lcdui.Item child, javax.microedition.lcdui.Item parent ) {
		// ignore
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the internal x position of the given item.
	 * When it is equal -9999 this item's internal position is not known.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal x position of this item's currently selected content, -9999 when it is unknown.
	 */
	public static int getInternalX( javax.microedition.lcdui.Item item ) {
		return -9999;
	}
	//#endif

	//#if polish.midp
	/**
	 * Retrieves the internal y position of the given item.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal y position of this item's currently selected content.
	 * @see #getInternalX(javax.microedition.lcdui.Item)
	 */
	public static int getInternalY( javax.microedition.lcdui.Item item ) {
		return -1;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the internal width of the given item.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal width of this item's currently selected content.
	 * @see #getInternalX(javax.microedition.lcdui.Item)
	 */
	public static int getInternalWidth( javax.microedition.lcdui.Item item ) {
		return -1;
	}
	//#endif

	//#if polish.midp
	/**
	 * Retrieves the internal height of the given item.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal height of this item's currently selected content.
	 * @see #getInternalX(javax.microedition.lcdui.Item)
	 */
	public static int getInternalHeight( javax.microedition.lcdui.Item item ) {
		return -1;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the internal x position of the given item.
	 * When it is equal Item.NO_POSITION_SET / -9999 this item's internal position is not known.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal x position of this item's currently selected content, -9999 / Item.NO_POSITION_SET when it is unknown.
	 */
	public static int getInternalX( Item item ) {
		return item.internalX;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves the internal y position of the given item.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal y position of this item's currently selected content.
	 * @see #getInternalX(Item)
	 */
	public static int getInternalY( Item item ) {
		return item.internalY;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the internal width of the given item.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal width of this item's currently selected content.
	 * @see #getInternalX(Item)
	 */
	public static int getInternalWidth( Item item ) {
		return item.internalWidth;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves the internal height of the given item.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal height of this item's currently selected content.
	 * @see #getInternalX(Item)
	 */
	public static int getInternalHeight( Item item ) {
		return item.internalHeight;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Focusses the given item with the specified style.
	 * 
	 * @param item the item that should be focussed
	 * @param direction the direction from which the focus comes, e.g. Canvas.DOWN, Canvas.UP or 0. 
	 * @param style the style - use null when the item's focussed style should be used
	 * @return the previously assigned style of that item
	 */
	public static Style focus( Item item, int direction, Style style ) {
		return item.focus(style, direction);
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Defocuses the given item and sets the style to the specified one.
	 * 
	 * @param item the item
	 * @param style the style 
	 */
	public static void defocus( Item item, Style style ) {
		item.defocus(style);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets the focus to the given index of the specified list.
	 * When the list is not shown, it will be shown in this call.
	 * When the J2ME Polish GUI is not used, only the list will be shown.
	 * 
	 * @param display the display 
	 * @param list the list
	 * @param index the index
	 */
	public static void setCurrentListIndex( Display display, javax.microedition.lcdui.List list, int index ) {
		//#if !polish.blackberry && polish.usePolishGui
			//display.setCurrent( list );
		//#endif
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the focus to the given index of the specified list.
	 * When the list is not shown, it will be shown in this call.
	 * When the J2ME Polish GUI is not used, only the list will be shown.
	 * 
	 * @param display the display 
	 * @param list the list
	 * @param index the index
	 */
	public static void setCurrentListIndex( Display display, List list, int index ) {
		Item item = list.getItem( index );
		item.show( display );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets a ChoiceItem or a subclass for the given list. 
	 * @param list the list
	 * @param index the index of the item that should be exchanged
	 * @param item the new item
	 */
	public static void setListItem( List list, int index, ChoiceItem item ) {
		list.set(index, item);
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves a ChoiceItem or a subclass for the given list. 
	 * @param list the list
	 * @param index the index of the item that should be returned
	 * @return the item at the given position
	 */
	public static ChoiceItem getListItem( List list, int index ) {
		return list.getItem(index);
	}
	//#endif

	
	//#if polish.usePolishGui
	/**
	 * Sets a ChoiceItem or a subclass for the given list. 
	 * @param list the list
	 * @param item the new item
	 */
	public static void appendListItem( List list, ChoiceItem item ) {
		list.append( item );
	}
	//#endif
	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Sets a ChoiceItem or a subclass for the given list. 
	 * @param list the list
	 * @param index the index of the item that should be exchanged
	 * @param item the new item
	 */
	public static void setListItem( javax.microedition.lcdui.List list, int index, ChoiceItem item ) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui && polish.midp
	/**
	 * Retrieves a ChoiceItem or a subclass for the given list. 
	 * @param list the list
	 * @param index the index of the item that should be returned
	 * @return the item at the given position
	 */
	public static ChoiceItem getListItem( javax.microedition.lcdui.List list, int index ) {
		return null;
	}
	//#endif

	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Sets a ChoiceItem or a subclass for the given list. 
	 * @param list the list
	 * @param item the new item
	 */
	public static void appendListItem( javax.microedition.lcdui.List list, ChoiceItem item ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets a ChoiceItem or a subclass for the given ChoiceGroup. 
	 * @param ChoiceGroup the ChoiceGroup
	 * @param index the index of the item that should be exchanged
	 * @param item the new item
	 */
	public static void setChoiceGroupItem( ChoiceGroup ChoiceGroup, int index, ChoiceItem item ) {
		ChoiceGroup.set(index, item);
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves a ChoiceItem or a subclass for the given ChoiceGroup. 
	 * @param ChoiceGroup the ChoiceGroup
	 * @param index the index of the item that should be returned
	 * @return the item at the given position
	 */
	public static ChoiceItem getChoiceGroupItem( ChoiceGroup ChoiceGroup, int index ) {
		return ChoiceGroup.getItem(index);
	}
	//#endif

	
	//#if polish.usePolishGui
	/**
	 * Appends a ChoiceItem or a subclass to the given ChoiceGroup. 
	 * @param ChoiceGroup the ChoiceGroup
	 * @param item the new item
	 */
	public static void appendChoiceGroupItem( ChoiceGroup ChoiceGroup, ChoiceItem item ) {
		ChoiceGroup.append( item );
	}
	//#endif
	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Sets a ChoiceItem or a subclass for the given ChoiceGroup. 
	 * @param ChoiceGroup the ChoiceGroup
	 * @param index the index of the item that should be exchanged
	 * @param item the new item
	 */
	public static void setChoiceGroupItem( javax.microedition.lcdui.ChoiceGroup ChoiceGroup, int index, ChoiceItem item ) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui && polish.midp
	/**
	 * Retrieves a ChoiceItem or a subclass for the given ChoiceGroup. 
	 * @param ChoiceGroup the ChoiceGroup
	 * @param index the index of the item that should be returned
	 * @return the item at the given position
	 */
	public static ChoiceItem getChoiceGroupItem( javax.microedition.lcdui.ChoiceGroup ChoiceGroup, int index ) {
		return null;
	}
	//#endif

	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Sets a ChoiceItem or a subclass for the given ChoiceGroup. 
	 * @param ChoiceGroup the ChoiceGroup
	 * @param item the new item
	 */
	public static void appendChoiceGroupItem( javax.microedition.lcdui.ChoiceGroup ChoiceGroup, ChoiceItem item ) {
		// ignore
	}
	//#endif
	
	
	
	

	//#if polish.midp
	/**
	 * Applies a style to the given item if used in conjunction with the //#style preprocessing directive.
	 * 
	 * Example:
	 * <pre>
	 * //#style myStyle
	 * UiAccess.setStyle( myItem );
	 * </pre>
	 * @param item the item which should get the new style
	 */
	public static void setStyle( javax.microedition.lcdui.Item item ) {
		// ignore
	}
	//#endif
	
	//#if polish.midp && polish.usePolishGui
	/**
	 * Applies a style to the given item if used in conjunction with the //#style preprocessing directive.
	 * 
	 * Example:
	 * <pre>
	 * //#style myStyle
	 * UiAccess.setStyle( myItem );
	 * </pre>
	 * @param item the item which should get the new style
	 * @param style the style for the item
	 */
	public static void setStyle( javax.microedition.lcdui.Item item, Style style ) {
		// ignore
	}
	//#endif


	//#if polish.midp
	/**
	 * Applies a style to the given screen if used in conjunction with the //#style preprocessing directive.
	 * 
	 * Example:
	 * <pre>
	 * //#style myStyle
	 * UiAccess.setStyle( myScreen );
	 * </pre>
	 * @param screen the screen which should get the new style
	 */
	public static void setStyle( javax.microedition.lcdui.Screen screen ) {
		// ignore
	}
	//#endif

	//#if polish.midp
	/**
	 * Applies a style to the given alert if used in conjunction with the //#style preprocessing directive.
	 * 
	 * Example:
	 * <pre>
	 * //#style myStyle
	 * UiAccess.setStyle( myAlert );
	 * </pre>
	 * @param screen the alert which should get the new style
	 */
	public static void setStyle( javax.microedition.lcdui.Alert screen ) {
		// ignore
	}
	//#endif

	//#if polish.midp && polish.usePolishGui
	/**
	 * Applies a style to the given alert if used in conjunction with the //#style preprocessing directive.
	 * 
	 * Example:
	 * <pre>
	 * //#style myStyle
	 * UiAccess.setStyle( myAlert );
	 * </pre>
	 * @param screen the alert which should get the new style
	 * @param style the style for the alert
	 */
	public static void setStyle( javax.microedition.lcdui.Alert screen, Style style ) {
		// ignore
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Resets the style of the specified item and all its children (if any).
	 * This is useful when you have applied changes to the Item's style or one of its elements.
	 * @param recursive true when all subelements of the Item should reset their style as well.
	 * @param item the item which should reset its style
	 * @see UiAccess#resetStyle(Screen, boolean)
	 */
	public static void resetStyle( javax.microedition.lcdui.Item item, boolean recursive ) {
		// ignore
	}
	//#endif

	//#if polish.midp
	/**
	 * Resets the style of the specified screen and all its elements.
	 * This is useful when you have applied changes to the screen's style or one of its elements.
	 * @param screen the screen which should reset its style
	 * @param recursive true when all subelements of the screen should reset their style as well.
	 * @see UiAccess#resetStyle(Item, boolean)
	 */
	public static void resetStyle( javax.microedition.lcdui.Screen screen, boolean recursive ) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Resets the style of the specified item and all its children (if any).
	 * This is useful when you have applied changes to the Item's style or one of its elements.
	 * @param recursive true when all subelements of the Item should reset their style as well.
	 * @param item the item which should reset its style
	 * @see UiAccess#resetStyle(Screen, boolean)
	 */
	public static void resetStyle( Item item, boolean recursive ) {
		item.resetStyle(recursive);
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Resets the style of the specified screen and all its elements.
	 * This is useful when you have applied changes to the screen's style or one of its elements.
	 * @param screen the screen which should reset its style
	 * @param recursive true when all subelements of the screen should reset their style as well.
	 * @see UiAccess#resetStyle(Item, boolean)
	 */
	public static void resetStyle( Screen screen, boolean recursive ) {
		screen.resetStyle(recursive);
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Applies a style to the given item if used in conjunction with the //#style preprocessing directive.
	 * 
	 * Example:
	 * <pre>
	 * //#style myStyle
	 * UiAccess.setStyle( myItem );
	 * </pre>
	 * @param item the item which should get the new style
	 */
	public static void setStyle( Item item ) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Applies a style to the given screen if used in conjunction with the //#style preprocessing directive.
	 * 
	 * Example:
	 * <pre>
	 * //#style myStyle
	 * UiAccess.setStyle( myScreen );
	 * </pre>
	 * @param screen the screen which should get the new style
	 */
	public static void setStyle( Screen screen ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Applies a style to the given item.
	 * 
	 * @param item the item which should get the new style
	 * @param style the style
	 */
	public static void setStyle( Item item, Style style ) {
		item.setStyle( style );
		item.repaint();
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Applies a style to the given screen.
	 * 
	 * @param screen the screen which should get the new style
	 * @param style the style
	 */
	public static void setStyle( Screen screen, Style style ) {
		screen.setStyle( style );
		screen.repaint();
	}
	//#endif	

	//#if polish.midp
	/**
	 * Applies a style to the specified list item.
	 * 
	 * @param choice the List screen or ChoiceGroup item. 
	 * @param itemIndex the index of the list
	 */
	public static void setStyle(javax.microedition.lcdui.Choice choice, int itemIndex) {
		// ignore
		
	}
	//#endif	

	//#if polish.usePolishGui
	/**
	 * Applies a style to the specified list item.
	 * 
	 * @param choice the List screen or ChoiceGroup item. 
	 * @param itemIndex the index of the list
	 */
	public static void setStyle(Choice choice, int itemIndex) {
		// ignore
	}
	//#endif	

	//#if polish.usePolishGui
	/**
	 * Applies a style to the specified list item.
	 * 
	 * @param choice the List screen or ChoiceGroup item. 
	 * @param itemIndex the index of the list
	 * @param style the new style of the item
	 */
	public static void setStyle(Choice choice, int itemIndex, Style style) {
		Item item = null;
		if (choice instanceof Screen) {
			item = ((Screen)choice).getRootContainer().get(itemIndex);
		} else if (choice instanceof Container){
			item = ((Container)choice).get(itemIndex);
		}
		if (item != null) {
			item.setStyle(style);
		}
	}
	//#endif	
	
	//#if polish.midp
	/**
	 * Applies a style to the specified command.
	 * 
	 * @param command the command 
	 */
	public static void setStyle(javax.microedition.lcdui.Command command) {
		// ignore
		
	}
	//#endif	


	//#if polish.usePolishGui && polish.midp
	/**
	 * Gets the current style of the given item.
	 * 
	 * Example:
	 * <pre>
	 * //#if polish.usePolishGui
	 * 	Style style = UiAccess.getStyle( myItem );
	 * 	if (style != null) }
	 * 		style.background = new SimpleBackground( 0x00FF00 );
	 *  }
	 * //#endif
	 * </pre>
	 * Note: this method is only available when the J2ME Polish GUI is used, so you better check for the polish.usePolishGui prepocessing symbol.
   * 
	 * @param item the item of which the style should be retrieved
   * @return the style of the item
	 */
	public static Style getStyle( javax.microedition.lcdui.Item item ) {
		return null;
	}
	//#endif	

	//#if polish.usePolishGui && polish.midp
	/**
	 * Gets the current style of the given screen.
	 * 
	 * Example:
	 * <pre>
	 * //#if polish.usePolishGui
	 * 	Style style = UiAccess.getStyle( myScreen );
	 * 	if (style != null) }
	 * 		style.background = new SimpleBackground( 0x00FF00 );
	 *  }
	 * //#endif
	 * </pre>
	 * Note: this method is only available when the J2ME Polish GUI is used, so you better check for the polish.usePolishGui prepocessing symbol.
   * 
	 * @param screen the screen of which the style should be retrieved
   * @return the style of the screen
	 */
	public static Style getStyle( javax.microedition.lcdui.Screen screen ) {
		return null;
	}
	//#endif	

	//#if polish.usePolishGui
	/**
	 * Gets the current style of the given item.
	 * 
	 * Example:
	 * <pre>
	 * //#if polish.usePolishGui
	 * 	Style style = UiAccess.getStyle( myItem );
	 * 	if (style != null) }
	 * 		style.background = new SimpleBackground( 0x00FF00 );
	 *  }
	 * //#endif
	 * </pre>
	 * Note: this method is only available when the J2ME Polish GUI is used, so you better check for the polish.usePolishGui prepocessing symbol.
   * 
	 * @param item the item of which the style should be retrieved
   * @return the style of the item
	 */
	public static Style getStyle( Item item ) {
		return item.style;
	}
	//#endif	

	//#if polish.usePolishGui
	/**
	 * Gets the current style of the given screen.
	 * 
	 * Example:
	 * <pre>
	 * //#if polish.usePolishGui
	 * 	Style style = UiAccess.getStyle( myScreen );
	 * 	if (style != null) }
	 * 		style.background = new SimpleBackground( 0x00FF00 );
	 *  }
	 * //#endif
	 * </pre>
	 * Note: this method is only available when the J2ME Polish GUI is used, so you better check for the polish.usePolishGui prepocessing symbol.
   * 
	 * @param screen the screen of which the style should be retrieved
   * @return the style of the screen
	 */
	public static Style getStyle( Screen screen ) {
		return screen.style;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Forwards a key event to the specified item.
	 * The handleKeyPressed call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handleKeyPressed( Item item, int keyCode, int gameAction ) {
		return item.handleKeyPressed(keyCode, gameAction);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Forwards a key event to the specified item.
	 * The handleKeyPressed call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handleKeyPressed( javax.microedition.lcdui.Item item, int keyCode, int gameAction ) {
		return false;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Forwards a key event to the specified item.
	 * The handleKeyReleased call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handleKeyReleased( Item item, int keyCode, int gameAction ) {
		return item.handleKeyReleased(keyCode, gameAction);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Forwards a key event to the specified item.
	 * The handleKeyPressed call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handleKeyReleased( javax.microedition.lcdui.Item item, int keyCode, int gameAction ) {
		return false;
	}
	//#endif
	
	
	//#if polish.usePolishGui
	/**
	 * Forwards a key event to the specified item.
	 * The handleKeyRepeated call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handleKeyRepeated( Item item, int keyCode, int gameAction ) {
		return item.handleKeyRepeated(keyCode, gameAction);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Forwards a key event to the specified item.
	 * The handleKeyPressed call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handleKeyRepeated( javax.microedition.lcdui.Item item, int keyCode, int gameAction ) {
		return false;
	}
	//#endif
	

	//#if polish.usePolishGui
	/**
	 * Forwards a pointer event to the specified item.
	 * The handlePointerPressed call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param x the x position of the pointer position relative to this item's left position
	 * @param y the y position of the pointer position relative to this item's top position
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handlePointerPressed( Item item, int x, int y ) {
		//#if polish.hasPointerEvents
			return item.handlePointerPressed(x, y);
		//#else
			//# return false;
		//#endif
	}
	//#endif

	//#if polish.midp
	/**
	 * Forwards a pointer event to the specified item.
	 * The handlePointerPressed call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param x the x position of the pointer position relative to this item's left position
	 * @param y the y position of the pointer position relative to this item's top position
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handlePointerPressed( javax.microedition.lcdui.Item item, int x, int y ) {
		return false;
	}
	//#endif
	

	//#if polish.usePolishGui
	/**
	 * Forwards a pointer event to the specified item.
	 * The handlePointerDragged call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param x the x position of the pointer position relative to this item's left position
	 * @param y the y position of the pointer position relative to this item's top position
	 * @return true when the event has been consumed by the item
	 * @deprecated use handlePointerDragged(Item,int,int,ClippingRegion)
	 * @see #handlePointerDragged(Item, int, int, ClippingRegion)
	 */
	public static boolean handlePointerDragged( Item item, int x, int y ) {
		//#if polish.hasPointerEvents
			return item.handlePointerDragged(x, y);
		//#else
			//# return false;
		//#endif
	}
	//#endif

	//#if polish.midp
	/**
	 * Forwards a pointer event to the specified item.
	 * The handlePointerDragged call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param x the x position of the pointer position relative to this item's left position
	 * @param y the y position of the pointer position relative to this item's top position
	 * @return true when the event has been consumed by the item
	 * @deprecated use handlePointerDragged(Item,int,int,ClippingRegion)
	 * @see #handlePointerDragged(javax.microedition.lcdui.Item, int, int, ClippingRegion)
	 */
	public static boolean handlePointerDragged( javax.microedition.lcdui.Item item, int x, int y ) {
		return false;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Forwards a pointer event to the specified item.
	 * The handlePointerDragged call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param x the x position of the pointer position relative to this item's left position
	 * @param y the y position of the pointer position relative to this item's top position
	 * @param repaintRegion the repaint region that should be refreshed after handling the drag event
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handlePointerDragged( Item item, int x, int y, ClippingRegion repaintRegion ) {
		//#if polish.hasPointerEvents
			return item.handlePointerDragged(x, y, repaintRegion);
		//#else
			//# return false;
		//#endif
	}
	//#endif

	//#if polish.midp
	/**
	 * Forwards a pointer event to the specified item.
	 * The handlePointerDragged call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param x the x position of the pointer position relative to this item's left position
	 * @param y the y position of the pointer position relative to this item's top position
	 * @param repaintRegion the repaint region that should be refreshed after handling the drag event
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handlePointerDragged( javax.microedition.lcdui.Item item, int x, int y, ClippingRegion repaintRegion ) {
		return false;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Forwards a pointer event to the specified item.
	 * The handlePointerReleased call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param x the x position of the pointer position relative to this item's left position
	 * @param y the y position of the pointer position relative to this item's top position
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handlePointerReleased( Item item, int x, int y ) {
		//#if polish.hasPointerEvents
			return item.handlePointerReleased(x, y);
		//#else
			//# return false;
		//#endif
	}
	//#endif

	//#if polish.midp
	/**
	 * Forwards a pointer event to the specified item.
	 * The handlePointerReleased call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param x the x position of the pointer position relative to this item's left position
	 * @param y the y position of the pointer position relative to this item's top position
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handlePointerReleased( javax.microedition.lcdui.Item item, int x, int y ) {
		return false;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Asks the specified item to handle the command.
	 * 
	 * @param item the item
	 * @param cmd the command that the item should handle
	 * @return true when the item has handled that command
	 */
	public static boolean handleCommand(Item item, Command cmd) {
		return item.handleCommand(cmd);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Asks the specified item to handle the command.
	 * 
	 * @param item the item
	 * @param cmd the command that the item should handle
	 * @return true when the item has handled that command
	 */
	public static boolean handleCommand(javax.microedition.lcdui.Item item, Command cmd) {
		return false;
	}
	//#endif
	

	//#if polish.usePolishGui
	/**
	 * Forwards a key event to the specified screen.
	 * The handleKeyPressed call is protected, this is an public accessor for any screen.
	 * 
	 * @param screen the screen 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the screen
	 */
	public static boolean handleKeyPressed( Screen screen, int keyCode, int gameAction ) {
		return screen.handleKeyPressed(keyCode, gameAction);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Forwards a key event to the specified screen.
	 * The handleKeyPressed call is protected, this is an public accessor for any screen.
	 * 
	 * @param screen the screen 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the screen
	 */
	public static boolean handleKeyPressed( javax.microedition.lcdui.Screen screen, int keyCode, int gameAction ) {
		return false;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Forwards a key event to the specified screen.
	 * The handleKeyReleased call is protected, this is an public accessor for any screen.
	 * 
	 * @param screen the screen 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the screen
	 */
	public static boolean handleKeyReleased( Screen screen, int keyCode, int gameAction ) {
		return screen.handleKeyReleased(keyCode, gameAction);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Forwards a key event to the specified screen.
	 * The handleKeyPressed call is protected, this is an public accessor for any screen.
	 * 
	 * @param screen the screen 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the screen
	 */
	public static boolean handleKeyReleased( javax.microedition.lcdui.Screen screen, int keyCode, int gameAction ) {
		return false;
	}
	//#endif
	
	
	//#if polish.usePolishGui
	/**
	 * Forwards a key event to the specified screen.
	 * The handleKeyRepeated call is protected, this is an public accessor for any screen.
	 * 
	 * @param screen the screen 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the screen
	 */
	public static boolean handleKeyRepeated( Screen screen, int keyCode, int gameAction ) {
		return screen.handleKeyRepeated(keyCode, gameAction);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Forwards a key event to the specified screen.
	 * The handleKeyPressed call is protected, this is an public accessor for any screen.
	 * 
	 * @param screen the screen 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the screen
	 */
	public static boolean handleKeyRepeated( javax.microedition.lcdui.Screen screen, int keyCode, int gameAction ) {
		return false;
	}
	//#endif
	
	
	//#if polish.usePolishGui
	/**
	 * Forwards a key event to the specified screen.
	 * The handlePointerPressed call is protected, this is an public accessor for any screen.
	 * 
	 * @param screen the screen 
	 * @param x the x position of the pointer pressing relative to this screen's left position
	 * @param y the y position of the pointer pressing relative to this screen's top position
	 * @return true when the event has been consumed by the screen
	 */
	public static boolean handlePointerPressed( Screen screen, int x, int y ) {
		//#if polish.hasPointerEvents
			return screen.handlePointerPressed(x, y);
		//#else
			//# return false;
		//#endif
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Forwards a key event to the specified screen.
	 * The handlePointerPressed call is protected, this is an public accessor for any screen.
	 * 
	 * @param screen the screen 
	 * @param x the x position of the pointer pressing relative to this screen's left position
	 * @param y the y position of the pointer pressing relative to this screen's top position
	 * @return true when the event has been consumed by the screen
	 */
	public static boolean handlePointerPressed( javax.microedition.lcdui.Screen screen, int x, int y ) {
		return false;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Forwards a pointer event to the specified screen.
	 * The handlePointerReleased call is protected, this is an public accessor for any screen.
	 * 
	 * @param screen the screen 
	 * @param x the x position of the pointer pressing relative to this screen's left position
	 * @param y the y position of the pointer pressing relative to this screen's top position
	 * @return true when the event has been consumed by the screen
	 */
	public static boolean handlePointerReleased( Screen screen, int x, int y ) {
		//#if polish.hasPointerEvents
			return screen.handlePointerReleased(x, y);
		//#else
			//# return false;
		//#endif
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Forwards a pointer event to the specified screen.
	 * The handlePointerReleased call is protected, this is an public accessor for any screen.
	 * 
	 * @param screen the screen 
	 * @param x the x position of the pointer pressing relative to this screen's left position
	 * @param y the y position of the pointer pressing relative to this screen's top position
	 * @return true when the event has been consumed by the screen
	 */
	public static boolean handlePointerReleased( javax.microedition.lcdui.Screen screen, int x, int y ) {
		return false;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Asks the specified screen to handle the command.
	 * 
	 * @param screen the screen
	 * @param cmd the command that the screen should handle
	 * @return true when the screen has handled that command
	 */
	public static boolean handleCommand(Screen screen, Command cmd) {
		return screen.handleCommand(cmd);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Asks the specified screen to handle the command.
	 * 
	 * @param screen the screen
	 * @param cmd the command that the screen should handle
	 * @return true when the screen has handled that command
	 */
	public static boolean handleCommand(javax.microedition.lcdui.Screen screen, Command cmd) {
		return false;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets the caret position in the given text field.
	 * Please note that this operation requires the direct input mode to work.
	 * 
	 * @param field the text field 
	 * @param position the new caret position,  0 puts the caret at the start of the line, getString().length moves the caret to the end of the input.
	 */
	public static void setCaretPosition( javax.microedition.lcdui.TextField field, int position ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets the caret position in the given text field.
	 * Please note that this operation requires the direct input mode to work.
	 * 
	 * @param field the text field 
	 * @param position the new caret position,  0 puts the caret at the start of the line, getString().length moves the caret to the end of the input.
	 */
	public static void setCaretPosition( TextField field, int position ) {
		field.setCaretPosition( position ); 
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets the number of decimal fractions that are allowed for FIXED_POINT_DECIMAL constrained TextFields
	 * @param field the textfield the textfield for which the fractions are set
	 * @param number the number (defaults to 2)
	 * @see UiAccess#CONSTRAINT_FIXED_POINT_DECIMAL
	 * @see TextField#FIXED_POINT_DECIMAL
	 */
	public static void setNumberOfDecimalFractions( javax.microedition.lcdui.TextField field, int number ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets the number of decimal fractions that are allowed for FIXED_POINT_DECIMAL constrained TextFields
	 * @param field the textfield the textfield for which the fractions are set
	 * @param number the number (defaults to 2)
	 * @see UiAccess#CONSTRAINT_FIXED_POINT_DECIMAL
	 * @see TextField#FIXED_POINT_DECIMAL
	 */
	public static void setNumberOfDecimalFractions( TextField field, int number ) {
		field.setNumberOfDecimalFractions( number ); 
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the number of decimal fractions that are allowed for FIXED_POINT_DECIMAL constrained TextFields
	 * @param field the textfield the textfield for which the fractions are set
	 * @return the number (defaults to 2)
	 * @see UiAccess#CONSTRAINT_FIXED_POINT_DECIMAL
	 * @see TextField#FIXED_POINT_DECIMAL
	 */
	public static int getNumberOfDecimalFractions( javax.microedition.lcdui.TextField field ) {
		return 2;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the number of decimal fractions that are allowed for FIXED_POINT_DECIMAL constrained TextFields
	 * @param field the textfield the textfield for which the fractions are set
	 * @return number the number (defaults to 2)
	 * @see UiAccess#CONSTRAINT_FIXED_POINT_DECIMAL
	 * @see TextField#FIXED_POINT_DECIMAL
	 */
	public static int getNumberOfDecimalFractions( TextField field ) {
		return field.getNumberOfDecimalFractions(); 
	}
	//#endif


	//#if polish.midp
	/**
	 * Sets the caret position in the given text box.
	 * Please note that this operation requires the direct input mode to work.
	 * 
	 * @param box the text box 
	 * @param position the new caret position,  0 puts the caret at the start of the line, getString().length moves the caret to the end of the input.
	 */
	public static void setCaretPosition( javax.microedition.lcdui.TextBox box, int position ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets the caret position in the given text box.
	 * Please note that this operation requires the direct input mode to work.
	 * 
	 * @param box the text box 
	 * @param position the new caret position,  0 puts the caret at the start of the line, getString().length moves the caret to the end of the input.
	 */
	public static void setCaretPosition( TextBox box, int position ) {
		box.textField.setCaretPosition( position ); 
	}
	//#endif

	//#if polish.midp
	/**
	 * Sets the input mode for the given textfield.
	 * Warning: you have to ensure that the input mode matches the contraints of
	 * the given TextField.
	 * 
	 * @param field the text field 
	 * @param inputMode the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASE or UiAccess.MODE_NATIVE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static void setInputMode( javax.microedition.lcdui.TextField field, int inputMode ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets the input mode for the given textfield.
	 * Warning: you have to ensure that the input mode matches the contraints of
	 * the given TextField.
	 * 
	 * @param field the text field 
	 * @param inputMode the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASE or UiAccess.MODE_NATIVE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static void setInputMode( TextField field, int inputMode ) {
		field.setInputMode( inputMode ); 
	}
	//#endif

	//#if polish.midp
	/**
	 * Sets the input mode for the given textbox.
	 * Warning: you have to ensure that the input mode matches the contraints of
	 * the given TextBox.
	 * 
	 * @param box the text box 
	 * @param inputMode the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASE or UiAccess.MODE_NATIVE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static void setInputMode( javax.microedition.lcdui.TextBox box, int inputMode ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets the input mode for the given textbox.
	 * Warning: you have to ensure that the input mode matches the contraints of
	 * the given TextBox.
	 * 
	 * @param box the text box 
	 * @param inputMode the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASEor UiAccess.MODE_NATIVE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static void setInputMode( TextBox box, int inputMode ) {
		box.textField.setInputMode( inputMode ); 
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the input mode for the given textfield.
	 * 
	 * @param field the text field 
	 * @return the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASEor UiAccess.MODE_NATIVE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static int getInputMode( javax.microedition.lcdui.TextField field ) {
		return -1;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the input mode for the given textfield.
	 * 
	 * @param field the text field 
	 * @return the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASE or UiAccess.MODE_NATIVE
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static int getInputMode( TextField field ) {
		return field.inputMode; 
	}
	//#endif

	//#if polish.midp
	/**
	 * Retrieves the input mode for the given textbox.
	 * 
	 * @param box the text box 
	 * @return the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASE or UiAccess.MODE_NATIVE
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static int getInputMode( javax.microedition.lcdui.TextBox box ) {
		return -1;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the input mode for the given textbox.
	 * 
	 * @param box the text box 
	 * @return the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASE or UiAccess.MODE_NATIVE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static int getInputMode( TextBox box ) {
		return box.textField.inputMode; 
	}
	//#endif
	
	
	//#if polish.midp
	/**
	 * Retrieves the decimal value entered with a dot as the decimal mark.
	 * <ul>
	 * <li>When the value has no decimal places it will be returned as it is: 12</li>
	 * <li>When the value is null, null will be returned: null</li>
	 * <li>When the value has decimal places, a dot will be used: 12.3</li>
	 * </ul>
	 * When the J2ME Polish GUI is not used, this method will only detect commas as possible
	 * alternative decimal marks.
	 * 
	 * @param field the text field with a DECIMAL constraint
	 * @return either the formatted value or null, when there was no input.
	 * @throws IllegalStateException when the TextField is not DECIMAL constrained
	 */
	public static String getDotSeparatedDecimalString( javax.microedition.lcdui.TextField field ) {
		//#if polish.midp2
		if (( field.getConstraints() & javax.microedition.lcdui.TextField.DECIMAL)!= javax.microedition.lcdui.TextField.DECIMAL) {
			throw new IllegalStateException();
		}
		//#endif
		String value = field.getString();
		if (value == null) {
			return null;
		}
		return value.replace(',', '.');
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the decimal value entered with a dot as the decimal mark.
	 * <ul>
	 * <li>When the value has no decimal places it will be returned as it is: 12</li>
	 * <li>When the value is null, null will be returned: null</li>
	 * <li>When the value has decimal places, a dot will be used: 12.3</li>
	 * </ul>
	 * @param field the text field with a DECIMAL constraint
	 * @return either the formatted value or null, when there was no input.
	 * @throws IllegalStateException when the TextField is not DECIMAL constrained
	 */
	public static String getDotSeparatedDecimalString( TextField field ) {
		return field.getDotSeparatedDecimalString(); 
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Adds the given command as a subcommand to the defined screen. When the J2ME Polish GUI is not used, this will just add the command to the screen like a normal command.
	 * 
	 * @param child the sub command
	 * @param parent the parent command
	 * @param screen the screen.
	 */
	public static void addSubCommand(  javax.microedition.lcdui.Command child, javax.microedition.lcdui.Command parent, javax.microedition.lcdui.Screen screen  ) {
		//#if !polish.blackberry
			//TODO implement addSubCommand
			screen.addCommand( child );
		//#endif
	}
	//#endif
	
	//#if polish.LibraryBuild
	/**
	 * Adds the given command as a subcommand to the defined screen. When the J2ME Polish GUI is not used, this will just add the command to the screen like a normal command.
	 * 
	 * @param child the sub command
	 * @param parent the parent command
	 * @param screen the screen.
	 */
	public static void addSubCommand(  javax.microedition.lcdui.Command child, javax.microedition.lcdui.Command parent, Screen screen  ) {
		//#if !polish.blackberry
			//TODO implement addSubCommand
			screen.addCommand( child );
		//#endif
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Adds the given command as a subcommand to the defined screen. When the J2ME Polish GUI is not used, this will just add the command to the screen like a normal command.
	 * 
	 * @param child the sub command
	 * @param parent the parent command
	 * @param screen the screen.
	 */
	public static void addSubCommand(  Command child, Command parent, Screen screen  ) {
		//parent.addSubCommand(child);
		screen.addSubCommand( child, parent );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Adds the given command as a subcommand to the defined screen. When the J2ME Polish GUI is not used, this will just add the command to the screen like a normal command.
	 * 
	 * @param child the sub command
	 * @param parent the parent command
	 * @param screen the screen.
	 * @param style the style of the command
	 */
	public static void addSubCommand(  Command child, Command parent, Screen screen, Style style  ) {
//		if (style != null) {
//			child.setStyle(style);
//		}
		//parent.addSubCommand(child);
		screen.addSubCommand( child, parent, style );
	}
	//#endif

	//#if polish.midp
	/**
	 * Removes the given command as subcommand from the defined screen
	 * 
	 * @param childCommand the command to remove.
	 * @param parentCommand the parent command of the command to remove.
	 * @param screen the screen.
	 */
	public static void removeSubCommand(javax.microedition.lcdui.Command childCommand, javax.microedition.lcdui.Command parentCommand, javax.microedition.lcdui.Screen screen)
	{
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Removes the given command as a subcommand from the defined screen
	 * 
	 * @param childCommand the command to remove.
	 * @param parentCommand the parent command of the command to remove.
	 * @param screen the screen.
	 */
	public static void removeSubCommand(Command childCommand, Command parentCommand, Screen screen)
	{
		//parentCommand.removeSubCommand( childCommand );
		screen.removeSubCommand(childCommand, parentCommand);
	}
	//#endif

	//#if polish.midp
	/**
	 * Removes all commands from the given screen
	 * This option is only available when the "menu" fullscreen mode is activated.
	 * 
	 * @param screen the screen.
	 */
	public static void removeAllCommands(  javax.microedition.lcdui.Screen screen ) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Removes all commands from the given screen
	 * This option is only available when the "menu" fullscreen mode is activated.
	 * 
	 * @param screen the screen.
	 */
	public static void removeAllCommands(  Screen screen ) {
		screen.removeAllCommands();
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Adds add separator with the given priority to the menu
	 * 
	 * @param priority the priority, same as Command priorities
	 * @param screen the screen.
	 */
	public static void addCommandSeparator(  int priority, Screen screen ) {
		screen.addCommandSeparator( priority );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Adds add separator with the given priority to the menu
	 * 
	 * @param priority the priority, same as Command priorities
	 * @param screen the screen.
	 * @param style the style of the separator
	 */
	public static void addCommandSeparator(  int priority, Screen screen, Style style  ) {
		screen.addCommandSeparator( priority, style );
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Adds add separator with the given priority to the menu
	 * 
	 * @param priority the priority, same as Command priorities
	 * @param screen the screen.
	 */
	public static void addCommandSeparator(  int priority, javax.microedition.lcdui.Screen screen ) {
		// ignore
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Adds add separator with the given priority to the menu
	 * 
	 * @param priority the priority, same as Command priorities
	 * @param screen the screen.
	 * @param style the style of the separator
	 */
	public static void addCommandSeparator(  int priority, javax.microedition.lcdui.Screen screen, Style style  ) {
		// ignore
	}
	//#endif


	//#if polish.midp
	/**
	 * Checks whether the commands menu of the screen is currently opened.
	 * Useful when overriding the keyPressed() method.
	 * 
	 * @param screen the screen
	 * @return true when the commands menu is opened.
	 */
	public static boolean isMenuOpened( javax.microedition.lcdui.Screen screen ) {
		return false;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Checks whether the commands menu of the screen is currently opened.
	 * Useful when overriding the keyPressed() method.
	 * 
	 * @param screen the screen
	 * @return true when the commands menu is opened.
	 */
	public static boolean isMenuOpened( Screen screen ) {
		return screen.isMenuOpened();
	}
	//#endif

	//#if polish.midp
    /**
     * Focuses the specified item on the given screen.
     * 
     * @param screen the screen
     * @param item the item that should be focused
     */
    public static void focus( javax.microedition.lcdui.Screen screen, javax.microedition.lcdui.Item item ) {
        // ignore
    }
    //#endif

	//#if polish.midp && polish.usePolishGui
    /**
     * Focuses the specified item on the given screen.
     * 
     * @param screen the screen
     * @param item the item that should be focused
     */
    public static void focus( Screen screen, javax.microedition.lcdui.Item item ) {
        // ignore
    }
    /**
     * Focuses the specified item on the given screen.
     * 
     * @param screen the screen
     * @param item the item that should be focused
     */
    public static void focus( javax.microedition.lcdui.Screen screen, Item item ) {
        // ignore
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Focuses the specified item on the given screen.
     * 
     * @param screen the screen
     * @param item the item that should be focused
     */
    public static void focus( Screen screen, Item item ) {
        screen.focus( item );
    }
    //#endif
    
	//#if polish.midp
    /**
     * Focuses the specified item on the given screen.
     * 
     * @param screen the screen
     * @param index the index of the item that should be focused, first item has the index 0
     */
    public static void focus( javax.microedition.lcdui.Screen screen, int index ) {
        // ignore
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Focuses the specified item on the given screen.
     * 
     * @param screen the screen
     * @param index the index of the item that should be focused, first item has the index 0
     */
    public static void focus( Screen screen, int index ) {
        screen.focus( index );
    }
    //#endif

	//#if polish.midp
    /**
     * Focuses the specified item on the given choice group.
     * 
     * @param choiceGroup the choice group
     * @param index the index of the item that should be focused, first item has the index 0
     */
    public static void focus( javax.microedition.lcdui.ChoiceGroup choiceGroup, int index ) {
        // ignore
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Focuses the specified item on the given choice group.
     * 
     * @param choiceGroup the choice group
     * @param index the index of the item that should be focused, first item has the index 0
     */
    public static void focus( ChoiceGroup choiceGroup, int index ) {
    	if (!choiceGroup.isFocused) {
    		Screen screen = choiceGroup.getScreen();
    		if (screen != null) {
    			screen.focus(choiceGroup);
    		}
    	}
        choiceGroup.focusChild( index );
    }
    //#endif

    /**
     * Releases all (memory) instensive resources that are currently hold by the J2ME Polish GUI.
     */
    public static void releaseResources() {
        //#if polish.usePolishGui
    		StyleSheet.releaseResources();
    		Displayable displayable = StyleSheet.display.getCurrent();
    		if ( displayable instanceof Screen ) {
    			((Screen)displayable).releaseResources();
    		}
        //#endif    	
    }
    
    /**
     * Releases all (memory) instensive resources that are currently hold by the J2ME Polish GUI when a non-J2ME Polish screen is shown.
     */
    public static void releaseResourcesOnScreenChange() {
        //#if polish.usePolishGui
    		AnimationThread.releaseResourcesOnScreenChange = true;
        //#endif    	
    }

    
    //#if polish.usePolishGui
    /**
     * Releases all (memory) instensive resources that are currently hold by the J2ME Polish GUI for the specified screen.
     * The release is synchronized with the paint cycle of the screen and must not be called from within a
     * paint method, as this will result in deadlocks.
     * 
     * @param screen the screen for which the resources should be released. 
     */
    public static void releaseResources( Screen screen ) {
    		screen.releaseResources();
    }
    //#endif

	//#if polish.midp
    /**
     * Releases all (memory) instensive resources that are currently hold by the J2ME Polish GUI for the specified screen.
     * The release is synchronized with the paint cycle of the screen and must not be called from within a
     * paint method, as this will result in deadlocks.
     * 
     * @param screen the screen for which the resources should be released. 
     */
    public static void releaseResources( javax.microedition.lcdui.Screen screen ) {
    		// ignore
    }
    //#endif
    
	//#if polish.midp
    /**
     * Sets the subtitle for the specified screen
     * 
     * @param screen the screen
     * @param subtitle the subtitle text
     */
    public static void setSubtitle( javax.microedition.lcdui.Screen screen, String subtitle ) {
    		// ignore
    }
    //#endif

	//#if polish.midp && polish.usePolishGui
    /**
     * Sets the subtitle for the specified screen
     * 
     * @param screen the screen
     * @param subtitle the subtitle text
     * @param style the style of the subtitle text
     */
    public static void setSubtitle( javax.microedition.lcdui.Screen screen, String subtitle, Style style ) {
    		// ignore
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Sets the subtitle for the specified screen
     * 
     * @param screen the screen
     * @param subtitle the subtitle text
     */
    public static void setSubtitle( Screen screen, String subtitle ) {
    		setSubtitle( screen, new StringItem(null, subtitle));
    }
    //#endif
    
    //#if polish.usePolishGui
    /**
     * Sets the subtitle for the specified screen
     * 
     * @param screen the screen
     * @param subtitle the subtitle text
     * @param style the style of the subtitle text
     */
    public static void setSubtitle( Screen screen, String subtitle, Style style ) {
    		setSubtitle( screen, new StringItem(null, subtitle, style));
    }
    //#endif


	//#if polish.midp
    /**
     * Sets the subtitle for the specified screen
     * 
     * @param screen the screen
     * @param subtitle the subtitle item
     */
    public static void setSubtitle( javax.microedition.lcdui.Screen screen, javax.microedition.lcdui.Item subtitle ) {
    		// ignore
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Sets the subtitle for the specified screen
     * 
     * @param screen the screen
     * @param subtitle the subtitle item
     */
    public static void setSubtitle( Screen screen, Item subtitle ) {
    		screen.setSubTitle(subtitle);
    }
    //#endif
    
	//#if polish.midp
    /**
     * Scrolls the screen to the given position.
     * 
     * @param screen the screen
     * @param yOffset the vertical offset: 0 is the very top, negative values scroll the screen towards the end.
     */
    public static void scroll( javax.microedition.lcdui.Screen screen, int yOffset ) {
    	// ignore
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Scrolls the screen to the given position.
     * 
     * @param screen the screen
     * @param yOffset the vertical offset: 0 is the very top, negative values scroll the screen towards the end.
     */
    public static void scroll( Screen screen, int yOffset ) {
    	Container container = screen.container;
    	if (container != null) {
    		container.setScrollYOffset(yOffset, false);
    	}
    }
    //#endif

    //#if polish.midp
    /**
     * Scrolls the screen so that the specified item becomes visible.
     * If the item is above the currently visible screen area, it will be placed at the top.
     * If the item is below the currently visible screen area, it will be placed at the bottom.
     * If the item is within the currently visible screen area, the call will be ignored. 
     * 
     * @param item the item that should become visible.
     * @see #scrollTo(javax.microedition.lcdui.Item, int)
     */
    public static void scrollTo( javax.microedition.lcdui.Item item ) {
    	// ignore
    }
    //#endif

    //#if polish.midp
    /**
     * Scrolls the screen so that the specified item will be position according to the specified position.
     *  
     * @param item the item that should become visible.
     * @param position the position, either Graphics.TOP, Graphics.BOTTOM or Graphics.VCENTER
     * @see #scrollTo(javax.microedition.lcdui.Item)
     */
    public static void scrollTo( javax.microedition.lcdui.Item item, int position ) {
    	// ignore
    }
    //#endif
    
    //#if polish.usePolishGui
    /**
     * Scrolls the screen so that the specified item becomes visible.
     * If the item is above the currently visible screen area, it will be placed at the top.
     * If the item is below the currently visible screen area, it will be placed at the bottom.
     * If the item is within the currently visible screen area, the call will be ignored. 
     * 
     * @param item the item that should become visible.
     * @see #scrollTo(Item, int)
     */
    public static void scrollTo( Item item ) {
    	if (item.parent instanceof Container) {
    		((Container)item.parent).scroll(0, item, true);
    		return;
    	}
    	Screen screen = item.getScreen();
    	if (screen == null || screen.container == null) {
    		return;
    	}
    	int itemY = item.getAbsoluteY();
    	int contentY = screen.container.getAbsoluteY();
    	int contentHeight = screen.container.getScrollHeight();
    	int scrollY = screen.container.getScrollYOffset();
    	if ( itemY + scrollY < contentY ) {
    		// item needs to be scrolled downwards:
    		int amount = contentY - (itemY + scrollY);
    		screen.setScrollYOffset(amount, false);
    	} else if ( itemY + item.itemHeight > contentY + contentHeight) {
    		// item needs to be scrolled upwards:
    		int amount = scrollY - ( (itemY + item.itemHeight) - (contentY + contentHeight) );
    		screen.setScrollYOffset(amount, false);
    	}
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Scrolls the screen so that the specified item will be position according to the specified position.
     *  
     * @param item the item that should become visible.
     * @param position the position, either Graphics.TOP, Graphics.BOTTOM or Graphics.VCENTER
     * @see #scrollTo(javax.microedition.lcdui.Item)
     */
    public static void scrollTo( Item item, int position ) {
    	Screen screen = item.getScreen();
    	if (screen == null || screen.container == null) {
    		return;
    	}
    	int itemY = item.getAbsoluteY();
    	int contentY = screen.container.getAbsoluteY();
    	int contentHeight = screen.container.getScrollHeight();
    	int scrollY = screen.container.getScrollYOffset();
    	int offset;
    	if (position == Graphics.TOP) {
    		offset = scrollY + ( contentY - itemY );     		
    	} else if (position == Graphics.BOTTOM) {
    		int bottom = contentY + contentHeight - item.itemHeight;
    		offset = scrollY + ( bottom - itemY ); 
    	} else {    		
    		int verticalCenter = contentY + (contentHeight >> 1) - (item.itemHeight >> 1);
    		offset = scrollY + ( verticalCenter - itemY ); 
    	}
		screen.setScrollYOffset(offset, false);
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Retrieves the background of the given screen.
     * This can be used to dynamically alter the background, e.g. by setting a different image:
     * <pre>
     * //#if polish.usePolishGui
     *   ImageBackground ib = (ImageBackground) UiAccess.getBackground( form );
     *   ib.setImage( newImage );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * 
     * @param screen the screen
     * @return the background for the screen
     */
    public static Background getBackground( Screen screen ) {
    	return screen.background;
    }
    //#endif

    //#if polish.usePolishGui && polish.midp
    /**
     * Retrieves the background of the given screen.
     * This can be used to dynamically alter the background, e.g. by setting a different image:
     * <pre>
     * //#if polish.usePolishGui
     *   ImageBackground ib = (ImageBackground) UiAccess.getBackground( form );
     *   ib.setImage( newImage );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * 
     * @param screen the screen
     * @return the background for the screen
     */
    public static Background getBackground( javax.microedition.lcdui.Screen screen ) {
    	return null;
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Sets the background for the given screen.
     * This can be used to dynamically alter the background:
     * <pre>
     * //#if polish.usePolishGui
     *   SimpleBackground bg = new SimpleBackground( 0x00FF00 );
     *   UiAccess.setBackground( item, screen );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * @param screen the screen
     * @param background - the new background
     */
    public static void setBackground( Screen screen, Background background ) {
    	screen.background = background;
    	screen.repaint();
    }
    //#endif

    //#if polish.usePolishGui && polish.midp
    /**
     * Sets the background for the given screen.
     * This can be used to dynamically alter the background:
     * <pre>
     * //#if polish.usePolishGui
     *   SimpleBackground bg = new SimpleBackground( 0x00FF00 );
     *   UiAccess.setBackground( item, screen );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * @param screen the screen
     * @param background - the new background
     */
    public static void setBackground( javax.microedition.lcdui.Screen screen, Background background ) {
    	// ignore
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Retrieves the background of the given item.
     * This can be used to dynamically alter the background, e.g. by setting a different image:
     * <pre>
     * //#if polish.usePolishGui
     *   ImageBackground ib = (ImageBackground) UiAccess.getBackground( item );
     *   ib.setImage( newImage );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * 
     * @param item the item
     * @return the background for the item
     */
    public static Background getBackground( Item item ) {
    	return item.background;
    }
    //#endif

    //#if polish.usePolishGui && polish.midp
    /**
     * Retrieves the background of the given item.
     * This can be used to dynamically alter the background, e.g. by setting a different image:
     * <pre>
     * //#if polish.usePolishGui
     *   ImageBackground ib = (ImageBackground) UiAccess.getBackground( item );
     *   ib.setImage( newImage );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * 
     * @param item the item
     * @return the background for the item
     */
    public static Background getBackground(  javax.microedition.lcdui.Item item ) {
    	return null;
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Sets the background for the given item.
     * This can be used to dynamically alter the background:
     * <pre>
     * //#if polish.usePolishGui
     *   SimpleBackground bg = new SimpleBackground( 0x00FF00 );
     *   UiAccess.setBackground( item, screen );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * @param item the item
     * @param background - the new background
     */
    public static void setBackground( Item item, Background background ) {
    	item.background = background;
    	item.repaint();
    }
    //#endif

    //#if polish.usePolishGui && polish.midp
    /**
     * Sets the background for the given item.
     * This can be used to dynamically alter the background:
     * <pre>
     * //#if polish.usePolishGui
     *   SimpleBackground bg = new SimpleBackground( 0x00FF00 );
     *   UiAccess.setBackground( item, screen );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * @param item the item
     * @param background - the new background
     */
    public static void setBackground( javax.microedition.lcdui.Item item, Background background ) {
    	// ignore
    }
    //#endif

	//#if polish.midp
    /**
     * Retrieves the command listener belonging to this screen.
     * 
     * @param screen the screen 
     * @return the associated command listener, always null for javax.microedition.lcdui.Screen objects that have not been converted to J2ME Polish components
     */
	public static CommandListener getCommandListener( javax.microedition.lcdui.Screen screen ) {
		return null;
	}
	//#endif


    //#if polish.usePolishGui
    /**
     * Retrieves the command listener belonging to this screen.
     * 
     * @param screen the screen 
     * @return the associated command listener, always null for javax.microedition.lcdui.Screen objects that have not been converted to J2ME Polish components
     */
	public static CommandListener getCommandListener( Screen screen ) {
		return screen.getCommandListener();
	}
    //#endif

	
    //#if polish.usePolishGui
	/**
	 * Sets an arbitrary attribute for the given item.
	 * 
	 * @param item the item to which the attribute should be added
	 * @param key the key for the attribute
	 * @param value the attribute value
	 */
	public static void setAttribute( Item item, Object key, Object value ) {
		item.setAttribute( key, value );
	}
    //#endif
	
    //#if polish.usePolishGui
	/**
	 * Gets an previously added attribute of the specified item.
	 * 
	 * @param item the item to which the attribute should be added
	 * @param key the key of the attribute
	 * @return the attribute value, null if none has been registered under the given key before
	 */
	public static Object getAttribute( Item item, Object key ) {
		return item.getAttribute( key );
	}
    //#endif
	
	//#if polish.usePolishGui
  /**
   * Returns a HashMap object with all registered attributes.
   * 
   * @param item the item from which the attributes should be retrieved
   * @return a HashMap object with all attribute key/value pairs, null if no attribute was stored before.
   */
  public static HashMap getAttributes( Item item ) {
	  return item.getAttributes();
  }
  //#endif

  //#if polish.usePolishGui
	/**
	 * Sets an arbitrary attribute for the specified list item.
	 * 
   * @param list a list of items
	 * @param index the index of the item to which the attribute should be added
	 * @param key the key for the attribute
	 * @param value the attribute value
	 */
	public static void setAttribute( List list, int index, Object key, Object value ) {
		Item item = list.getItem(index);
		item.setAttribute( key, value );
	}
  //#endif
	
  //#if polish.usePolishGui
	/**
	 * Gets an previously added attribute of the specified item.
	 * 
   * @param list a list of items
	 * @param index the index of item from which the attribute should be retrieved
	 * @param key the key of the attribute
	 * @return the attribute value, null if none has been registered under the given key before
	 */
	public static Object getAttribute( List list, int index, Object key ) {
		Item item = list.getItem( index );
		return item.getAttribute( key );
	}
  //#endif
	
	//#if polish.usePolishGui
	/**
	 * Returns a HashMap object with all registered attributes.
	 * 
   * @param list a list of items
	 * @param index the index of item from which the attributes should be retrieved
	 * @return a HashMap object with all attribute key/value pairs, null if no attribute was stored before.
	 */
	public static HashMap getAttributes( List list, int index ) {
		Item item = list.getItem(index);
		return item.getAttributes();
	}
	//#endif
	
	//#if polish.midp
  /**
	 * Sets an arbitrary attribute for the given item.
	 * 
	 * @param item the item to which the attribute should be added
	 * @param key the key for the attribute
	 * @param value the attribute value
	 */
	public static void setAttribute( javax.microedition.lcdui.Item item, Object key, Object value ) {
		if (attributes == null) {
			attributes = new HashMap();
		}
		HashMap itemAttributes = (HashMap) attributes.get( item );
		if (itemAttributes == null) {
			itemAttributes = new HashMap();
			attributes.put( item, itemAttributes );
		}
		itemAttributes.put( key, value );
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Gets an previously added attribute of the specified item.
	 * 
	 * @param item the item from which the attribute should be retrieved
	 * @param key the key of the attribute
	 * @return the attribute value, null if none has been registered under the given key before
	 */
	public static Object getAttribute( javax.microedition.lcdui.Item item, Object key ) {
		if ( attributes == null ) {
			return null;
		}
		HashMap itemAttributes = (HashMap) attributes.get( item );
		if (itemAttributes == null) {
			return null;
		}
		return itemAttributes.get( key );
	}
	//#endif
  
	//#if polish.midp
  /**
   * Returns a HashMap object with all registered attributes.
   * 
   * @param item the item from which the attributes should be retrieved
   * @return a HashMap object with all attribute key/value pairs, null if no attribute was stored before.
   */
  public static HashMap getAttributes( javax.microedition.lcdui.Item item ) {
    if ( attributes == null ) {
      return null;
    }
    return (HashMap) attributes.get( item );
  }
  //#endif
  
//#if polish.midp
	/**
	 * Sets an arbitrary attribute for the specified list item.
	 * 
   * @param list a list of items
	 * @param index the index of the item to which the attribute should be added
	 * @param key the key for the attribute
	 * @param value the attribute value
	 */
	public static void setAttribute( javax.microedition.lcdui.List list, int index, Object key, Object value ) {
		if (attributes == null) {
			attributes = new HashMap();
		}
		String item = list.toString() + index;
		HashMap itemAttributes = (HashMap) attributes.get( item );
		if (itemAttributes == null) {
			itemAttributes = new HashMap();
			attributes.put( item, itemAttributes );
		}
		itemAttributes.put( key, value );
	}
//#endif
	
//#if polish.midp
	/**
	 * Gets an previously added attribute of the specified item.
	 * 
   * @param list a list of items
   * @param index the index of item from which the attributes should be retrieved
	 * @param key the key of the attribute
	 * @return the attribute value, null if none has been registered under the given key before
	 */
	public static Object getAttribute( javax.microedition.lcdui.List list, int index, Object key ) {
		if ( attributes == null ) {
			return null;
		}
		String item = list.toString() + index;
		HashMap itemAttributes = (HashMap) attributes.get( item );
		if (itemAttributes == null) {
			return null;
		}
		return itemAttributes.get( key );
	}
//#endif
	
	//#if polish.midp
	/**
	 * Returns a HashMap object with all registered attributes.
	 * 
   * @param list a list of items
	 * @param index the index of the item from which the attributes should be retrieved
	 * @return a HashMap object with all attribute key/value pairs, null if no attribute was stored before.
	 */
	public static HashMap getAttributes( javax.microedition.lcdui.List list, int index ) {
	    if ( attributes == null ) {
	        return null;
	      }
	    String item = list.toString() + index;
	    return (HashMap) attributes.get( item );
	}
	//#endif

	//#if polish.midp
	/**
	 * Makes the item interactive (accessible) or non-interactive.
	 * This method is ignored when the J2ME Polish UI is not activated.
	 * <pre>
	 * //#style inactiveStyle
	 * UiAccess.setAccessible( myItem, false );
	 * </pre>
	 * 
	 * @param item the item that should be made accessible 
	 * @param isAccessible true when the item should be accessible/selectable
	 */
	public static void setAccessible( javax.microedition.lcdui.Item item, boolean isAccessible ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Makes the item interactive (accessible) or non-interactive.
	 * You can set a new style at the same time by adding a style directive:
	 * <pre>
	 * //#style inactiveStyle
	 * UiAccess.setAccessible( myItem, false );
	 * </pre>
	 * 
	 * @param item the item that should be made accessible 
	 * @param isAccessible true when the item should be accessible/selectable
	 */
	public static void setAccessible( Item item, boolean isAccessible ) {
		if (isAccessible) {
			item.setAppearanceMode( Item.INTERACTIVE );
		} else {
			item.setAppearanceMode( Item.PLAIN );
		}
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Makes the item interactive (accessible) or non-interactive.
	 * You can set a new style at the same time by adding a style directive:
	 * <pre>
	 * //#style inactiveStyle
	 * UiAccess.setAccessible( myItem, false );
	 * </pre>
	 * 
	 * @param item the item that should be made accessible 
	 * @param isAccessible true when the item should be accessible/selectable
	 * @param style the new style, is ignored when it is null
	 */
	public static void setAccessible( Item item, boolean isAccessible, Style style ) {
		if (!isAccessible && item.isFocused) {
			// first defocus item:
			Item parent = item.parent;
			if (parent instanceof Container) {
				((Container)parent).focusChild(-1);
			}
		}
		if (style != null) {
			item.setStyle(style);
		}
		if (isAccessible) {
			item.setAppearanceMode( Item.INTERACTIVE );
		} else {
			item.setAppearanceMode( Item.PLAIN );
		}
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Makes the specified List or ChoiceGroup item selectable or inaccessible - can be used in combination with a #style preprocessing directive.
	 * 
	 * @param choice the list 
	 * @param itemIndex the index of the list
	 * @param isAccessible true when the item should be accessible/selectable
	 */
	public static void setAccessible(javax.microedition.lcdui.Choice choice, int itemIndex, boolean isAccessible) {
		// ignore
	}
	//#endif	

	//#if polish.usePolishGui
	/**
	 * Makes the specified List item selectable or inaccessible - can be used in combination with a #style preprocessing directive.
	 * 
	 * @param choice the list 
	 * @param itemIndex the index of the list
	 * @param isAccessible true when the item should be accessible/selectable
	 * @throws IllegalArgumentException when choice is not a List/ChoiceGroup or a J2ME Polish Container
	 */
	public static void setAccessible(Choice choice, int itemIndex, boolean isAccessible) {
		setAccessible( choice, itemIndex, isAccessible, null);
	}
	//#endif	

	//#if polish.usePolishGui
	/**
	 * Makes the specified List or ChoiceGroup item selectable or inaccessible.
	 * 
	 * @param choice the List or ChoiceGroup
	 * @param itemIndex the index of the list
	 * @param isAccessible true when the item should be accessible/selectable
	 * @param style the new style of the item
	 * @throws IllegalArgumentException when choice is not a List/ChoiceGroup or a J2ME Polish Container
	 */
	public static void setAccessible(Choice choice, int itemIndex, boolean isAccessible, Style style) {
		Item item;
		if (choice instanceof List) {
			item = ((List)choice).getItem(itemIndex);
		} else if (choice instanceof Container) {
			item = ((Container)choice).get(itemIndex);
		} else {
			throw new IllegalArgumentException();
		}
		setAccessible( item, isAccessible, style );
	}
	//#endif	
	
	//#if polish.midp && polish.usePolishGui
	/**
	 * Makes the command interactive (accessible) or non-interactive.
	 * This method is ignored when the J2ME Polish UI is not activated.
	 * <pre>
	 * //#style inactiveStyle
	 * UiAccess.setAccessible( myScreen, myCommand, false );
	 * </pre>
	 * 
	 * @param screen the screen that contains the command
	 * @param command the item that should be made accessible 
	 * @param isAccessible true when the item should be accessible/selectable
	 */
	public static void setAccessible( javax.microedition.lcdui.Screen screen, Command command, boolean isAccessible ) {
		// ignore
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Makes the command interactive (accessible) or non-interactive.
	 * This method is ignored when the J2ME Polish UI is not activated.
	 * <pre>
	 * //#style inactiveStyle
	 * UiAccess.setAccessible( myScreen, myCommand, false );
	 * </pre>
	 * 
	 * @param screen the screen that contains the command
	 * @param command the item that should be made accessible 
	 * @param isAccessible true when the item should be accessible/selectable
	 */
	public static void setAccessible( javax.microedition.lcdui.Screen screen, javax.microedition.lcdui.Command command, boolean isAccessible ) {
		// ignore
	}
	//#endif

	
	//#if polish.usePolishGui
	/**
	 * Makes the item interactive (accessible) or non-interactive.
	 * <pre>
	 * //#style inactiveStyle
	 * UiAccess.setAccessible( myScreen, myCommand, false );
	 * </pre>
	 * 
	 * @param screen the screen that contains the command
	 * @param command the item that should be made accessible 
	 * @param isAccessible true when the item should be accessible/selectable
	 */
	public static void setAccessible( Screen screen, Command command, boolean isAccessible ) {
		setAccessible( screen.getCommandItem(command), isAccessible );
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Makes the item interactive (accessible) or non-interactive.
	 * <pre>
	 * //#style inactiveStyle
	 * UiAccess.setAccessible( myScreen, myCommand, false );
	 * </pre>
	 * 
	 * @param screen the screen that contains the command
	 * @param command the item that should be made accessible 
	 * @param isAccessible true when the item should be accessible/selectable
	 * @param style the new style for the command, is ignored when null
	 */
	public static void setAccessible( Screen screen, Command command, boolean isAccessible, Style style ) {
		setAccessible( screen.getCommandItem(command), isAccessible, style );

	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets an image for the specified ticker.
	 * This method is ignored when the J2ME Polish UI is not activated.
	 * 
	 * @param ticker the ticker item which will the image be set 
	 * @param image that image that will be set to the ticker
	 */
	public static void setTickerImage( javax.microedition.lcdui.Ticker ticker, Image image ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets an image for the specified ticker.
	 * This method is ignored when the J2ME Polish UI is not activated.
	 * 
	 * @param ticker the ticker item which will the image be set 
	 * @param image that image that will be set to the ticker
	 */
	public static void setTickerImage( Ticker ticker, Image image ) {
		ticker.setImage(image);
	}
	//#endif

	//#if polish.usePolishGui && polish.midp && !polish.android
	/**
	 * Sets an image for the specified ticker.
	 * This method is ignored when the J2ME Polish UI is not activated.
	 * 
	 * @param ticker the ticker item which will the image be set 
	 * @param image that image that will be set to the ticker
	 */
	public static void setTickerImage( Ticker ticker, de.enough.polish.ui.Image image ) {
		// ticker.setImage(image);
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Attaches data to the specified screen.
	 * This mechanism can be used to add business logic to screens.
	 * 
	 * @param screen the screen in which the data should be stored
	 * @param data the screen specific data
	 * @see #getData(Screen)
	 */
	public static void setData(Screen screen, Object data) {
		screen.setScreenData( data );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves screen specific data.
	 * This mechanism can be used to add business logic to screens.
	 * 
	 * @param screen the screen in which data has been previously stored using UiAccess.setData()
	 * @return any screen specific data or null when no data has been attached before
	 * @see #setData(Screen, Object)
	 */
	public static Object getData(Screen screen) {
		return screen.getScreenData();
	}
	//#endif

	//#if polish.midp
	/**
	 * Attaches data to the specified screen.
	 * This mechanism can be used to add business logic to screens.
	 * 
	 * @param screen the screen in which the data should be stored
	 * @param data the screen specific data
	 * @see #getData(Screen)
	 */
	public static void setData( javax.microedition.lcdui.Screen screen, Object data) {
		if (attributes == null) {
			attributes = new HashMap();
		}
		attributes.put(screen, data);
	}
	//#endif

	//#if polish.midp
	/**
	 * Retrieves screen specific data.
	 * This mechanism can be used to add business logic to screens.
	 * 
	 * @param screen the screen in which data has been previously stored using UiAccess.setData()
	 * @return any screen specific data or null when no data has been attached before
	 * @see #setData(Screen, Object)
	 */
	public static Object getData(javax.microedition.lcdui.Screen screen) {
		if (attributes == null) {
			return null;
		}
		return attributes.get( screen );
	}
	//#endif

	//#if polish.midp
	/**
	 * Changes the shown label of the specified command.
	 * Note that command.getLabel() will afterwards retrieve the same string as before, 
	 * only the shown label will be changed. You cannot change the labels of the
	 * commands that are shown on the left or right side of the menu, unless the extended menubar is activated 
	 * (polish.MenuBar.useExtendedMenuBar=true).
	 * This call is ignored when J2ME Polish does not render the menu.
	 *
	 * @param screen the screen that contains the command
	 * @param command the command
	 * @param label the new label that should be shown
	 */
	public static void setCommandLabel( javax.microedition.lcdui.Screen screen, javax.microedition.lcdui.Command command, String label) {
		//ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Changes the shown label of the specified command.
	 * Note that command.getLabel() will afterwards retrieve the same string as before, 
	 * only the shown label will be changed. You cannot change the labels of the
	 * commands that are shown on the left or right side of the menu, unless the extended menubar is activated 
	 * (polish.MenuBar.useExtendedMenuBar=true).
	 * This call is ignored when J2ME Polish does not render the menu.
	 *
	 * @param screen the screen that contains the command
	 * @param command the command
	 * @param label the new label that should be shown
	 */
	public static void setCommandLabel( Screen screen, Command command, String label) {
		screen.getCommandItem(command).setText(label);
	}
	//#endif
	
	//#if polish.LibraryBuild
	/**
	 * Changes the shown label of the specified command.
	 * Note that command.getLabel() will afterwards retrieve the same string as before, 
	 * only the shown label will be changed. You cannot change the labels of the
	 * commands that are shown on the left or right side of the menu, unless the extended menubar is activated 
	 * (polish.MenuBar.useExtendedMenuBar=true).
	 * This call is ignored when J2ME Polish does not render the menu.
	 *
	 * @param screen the screen that contains the command
	 * @param command the command
	 * @param label the new label that should be shown
	 */
	public static void setCommandLabel( Screen screen, javax.microedition.lcdui.Command command, String label) {
		// ignore
	}
	//#endif
	
	
	//#if polish.LibraryBuild
	/**
	 * Changes the shown label of the specified command.
	 * Note that command.getLabel() will afterwards retrieve the same string as before, 
	 * only the shown label will be changed. You cannot change the labels of the
	 * commands that are shown on the left or right side of the menu, unless the extended menubar is activated 
	 * (polish.MenuBar.useExtendedMenuBar=true).
	 * This call is ignored when J2ME Polish does not render the menu.
	 *
	 * @param command the command
	 * @param label the new label that should be shown
	 */
	public static void setCommandLabel( javax.microedition.lcdui.Command command, String label) {
		// ignore
	}
	//#endif

	//#if polish.midp
	/**
	 * Adds a ticker as a normal item to the specified form.
	 * 
	 * @param ticker the ticker 
	 * @param form  the form
	 * @return the index of the ticker within the form, -1 when the J2ME Polish UI is not used.
	 */
	public static int append( javax.microedition.lcdui.Ticker ticker, javax.microedition.lcdui.Form form ) {
		return -1;
	}
	//#endif


	//#if polish.usePolishGui and polish.midp
	/**
	 * Adds the specified (J2ME Polish) item to the given form.
	 * This can be used for example to add several tickers to a form.
	 * 
	 * @param item the item  
	 * @param form  the form
	 * @return the index of the item within the form, -1 when the J2ME Polish UI is not used.
	 */
	public static int append( Item item, javax.microedition.lcdui.Form form ) {
		return -1;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Adds the specified (J2ME Polish) item to the given form.
	 * 
	 * @param item the item  
	 * @param form  the form
	 * @return the index of the item within the form
	 */
	public static int append( Item item, Form form ) {
		return form.append(item);
	}
	//#endif


	//#if polish.usePolishGui
	/**
	 * Gets the visible status of the specified item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true.
	 * 
	 * @param item the item that might be invisible
	 * @return true when this item is visible.
	 */
	public static boolean isVisible( Item item ) {
		//#if polish.supportInvisibleItems
			return !item.isInvisible;
		//#else
			//# return true; 
		//#endif
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Gets the visible status of the specified list-item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true.
	 * 
	 * @param list the list 
	 * @param index the index of the item
	 * @return true when this item is visible.
	 */
	public static boolean isVisible( List list, int index ) {
		return isVisible( list.getItem( index ) );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the invisible status of the specified item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true.
	 * 
	 * @param item the item
	 * @param visible true when the item should become invisible.
	 */
	public static void setVisible( Item item, boolean visible ) {
		//#if polish.supportInvisibleItems
			item.setVisible( visible );
		//#endif
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the invisible status of the specified list-item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true.
	 * 
	 * @param list the list 
	 * @param index the index of the item
	 * @param visible true when the item should become invisible.
	 */
	public static void setVisible( List list, int index, boolean visible ) {
		setVisible( list.getItem(index), visible );
	}
	//#endif
	

	//#if polish.midp
	/**
	 * Gets the visible status of the specified item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true (and the J2ME Polish UI is used).
	 * 
	 * @param item the item that might be invisible
	 * @return true when this item is visible.
	 */
	public static boolean isVisible( javax.microedition.lcdui.Item item ) {
		return false;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Gets the visible status of the specified list-item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true.
	 * 
	 * @param list the list 
	 * @param index the index of the item
	 * @return true when this item is visible.
	 */
	public static boolean isVisible( javax.microedition.lcdui.List list, int index ) {
		return false;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets the visible status of the specified item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true (and the J2ME Polish UI is used).
	 * 
	 * @param item the item
	 * @param visible true when the item should be visible.
	 */
	public static void setVisible( javax.microedition.lcdui.Item item, boolean visible ) {
		// ignore
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets the invisible status of the specified list-item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true.
	 * 
	 * @param list the list 
	 * @param index the index of the item
	 * @param visible true when the item should become invisible.
	 */
	public static void setVisible( javax.microedition.lcdui.List list, int index, boolean visible ) {
		// ignore
	}
	//#endif

	
	//#if polish.usePolishGui
	/**
	 * Retrieves the index of the specified item in the screen.
	 * 
	 * @param item the item
	 * @param screen the screen
	 * @return the index of the item; -1 when the item is not part of the given screen
	 */
	public static int indexOf( Item item, Screen screen ) {
		if ( screen.container != null ) {
			return screen.container.indexOf(item);
		}
		return -1;
	}
	//#endif
	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Retrieves the index of the specified item in the screen.
	 * 
	 * @param item the item
	 * @param screen the screen
	 * @return the index of the item; -1 when the item is not part of the given screen
	 */
	public static int indexOf( Item item, javax.microedition.lcdui.Screen screen ) {
		return -1;
	}
	//#endif

	//#if polish.usePolishGui && polish.midp
	/**
	 * Retrieves the index of the specified item in the screen.
	 * 
	 * @param item the item
	 * @param screen the screen
	 * @return the index of the item; -1 when the item is not part of the given screen
	 */
	public static int indexOf( javax.microedition.lcdui.Item item, Screen screen ) {
		return -1;
	}
	//#endif

	//#if polish.midp
	/**
	 * Retrieves the index of the specified item in the screen.
	 * 
	 * @param item the item
	 * @param screen the screen
	 * @return the index of the item; -1 when the item is not part of the given screen
	 */
	public static int indexOf( javax.microedition.lcdui.Item item, javax.microedition.lcdui.Screen screen ) {
		if (screen instanceof javax.microedition.lcdui.Form) {
			javax.microedition.lcdui.Form form = (javax.microedition.lcdui.Form) screen;
			int size = form.size();
			for (int i = 0; i < size; i++) {
				if (form.get(i) == item) {
					return i;
				}
			}
		}
		return -1;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the RGB data of the specified item.
	 * This method only works on MIDP 2.0+ devices.
	 * 
	 * @param item the item
	 * @return the RGB data as an int array.
	 */
	public static int[] getRgbData( javax.microedition.lcdui.Item item ) {
		return null;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the RGB data of the specified item.
	 * This method only works on MIDP 2.0+ devices.
	 * 
	 * @param item the item
	 * @param opacity The opacity of the item between 0 (fully transparent) and 255 (fully opaque)
	 * @return the RGB data as an int array.
	 */
	public static int[] getRgbData( javax.microedition.lcdui.Item item, int opacity ) {
		return null;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves the RGB data of the specified item.
	 * This method only works on MIDP 2.0+ devices.
	 * 
	 * @param item the item
	 * @return the RGB data as an int array.
	 */
	public static int[] getRgbData( Item item ) {
		return getRgbData(item, 255);
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves the RGB data of the specified item.
	 * This method only works on MIDP 2.0+ devices.
	 * 
	 * @param item the item
	 * @param opacity The opacity of the item between 0 (fully transparent) and 255 (fully opaque)
	 * @return the RGB data as an int array.
	 */
	public static int[] getRgbData( Item item, int opacity ) {
		return item.getRgbData(true, opacity);

	}
	//#endif

	//#if polish.midp
	/**
	 * Retrieves the RGB data of the specified item's content without using a view-type/ItemView.
	 * This is often used by ItemView that want to change the original RGB data.
	 * In ItemView please ensure to call initContentByParent( item ) before using this function.
	 * 
	 * @param item the item
	 * @return an int array containing the RGB data of that item
	 */
	public static int[] getRgbDataOfContent( javax.microedition.lcdui.Item item ) {
		return null;
	}
	//#endif

	
 	//#if polish.usePolishGui
	/**
	 * Retrieves the RGB data of the specified item's content without using a view-type/ItemView.
	 * This is often used by ItemView that want to change the original RGB data.
	 * In ItemView please ensure to call initContentByParent( item ) before using this function.
	 * 
	 * @param item the item
	 * @return an int array containing the RGB data of that item (null on MIDP 1.0 devices)
	 */
	public static int[] getRgbDataOfContent( Item item ) {
		//#if polish.midp2
			Image image = Image.createImage( item.contentWidth, item.contentHeight );
			int transparentColor = 0x12345678;
			Graphics g = image.getGraphics();
			g.setColor(transparentColor);
			g.fillRect(0, 0, item.contentWidth, item.contentHeight );
			int[] transparentColorRgb = new int[1];
			image.getRGB(transparentColorRgb, 0, 1, 0, 0, 1, 1 );
			transparentColor = transparentColorRgb[0];
			item.paintContent( 0, 0, 0, item.contentWidth, g );
			int[] itemRgbData = new int[  item.contentWidth * item.contentHeight ];
			image.getRGB(itemRgbData, 0, item.contentWidth, 0, 0, item.contentWidth, item.contentHeight );
			// ensure transparent parts are indeed transparent
			for (int i = 0; i < itemRgbData.length; i++) {
				if( itemRgbData[i] == transparentColor ) {
					itemRgbData[i] = 0;
				}
			}
			return itemRgbData;
		//#else
			//# return null;
		//#endif
	}
	//#endif
	
 	//#if polish.usePolishGui
	/**
	 * Retrieves the RGB data of the specified item's content without using a view-type/ItemView.
	 * This is often used by ItemView that want to change the original RGB data.
	 * In ItemView please ensure to call initContentByParent( item ) before using this function.
	 * 
	 * @param item the item
	 * @param rgbData an int array in which the RGB data of the item's content is written
	 * @param x the horizontal start position for the RGB data
	 * @param y the vertical start position for the RGB data
	 * @param width the width of a single row in the rgbData
	 */
	public static void getRgbDataOfContent(Item item, int[] rgbData, int x, int y, int width) {
		//#if polish.midp2
			int contentWidth = item.contentWidth;
			int contentHeight = item.contentHeight;
			Image image = Image.createImage( contentWidth, contentHeight );
			int transparentColor = 0x12345678;
			Graphics g = image.getGraphics();
			g.setColor(transparentColor);
			g.fillRect(0, 0, contentWidth, contentHeight );
			int[] transparentColorRgb = new int[1];
			image.getRGB(transparentColorRgb, 0, 1, 0, 0, 1, 1 );
			transparentColor = transparentColorRgb[0];
			item.paintContent( 0, 0, 0, contentWidth, g );
			int[] itemRgbData = new int[  contentWidth * contentHeight ];
			image.getRGB(itemRgbData, 0, contentWidth, 0, 0, contentWidth, contentHeight );
			// ensure transparent parts are indeed transparent and copy to target array:
			for (int row = 0; row < contentHeight; row++ ) {
				for (int column = 0; column < contentWidth; column++ ) {
					int index = row * contentWidth + column;
					int pixel = itemRgbData[index];
					if ( pixel == transparentColor ) {
						pixel = 0;
					}
					int target = y * width + row * width + x + column;
					rgbData[ target ] = pixel;
				}
			}
		//#endif		
	}
	//#endif
	
 	//#if polish.midp
	/**
	 * Retrieves the RGB data of the specified item's content without using a view-type/ItemView.
	 * This is often used by ItemView that want to change the original RGB data.
	 * In ItemView please ensure to call initContentByParent( item ) before using this function.
	 * 
	 * @param item the item
	 * @param rgbData an int array in which the RGB data of the item's content is written
	 * @param x the horizontal start position for the RGB data
	 * @param y the vertical start position for the RGB data
	 * @param width the width of a single row in the rgbData
	 */
	public static void getRgbDataOfContent(javax.microedition.lcdui.Item item, int[] rgbData, int x, int y, int width) {
		// ignore
	}
	//#endif

 	//#if polish.midp
	/**
	 * Allows the given words for the specified textfield.
	 * Note that you need to enable the predictive input mode using the preprocessing variable
	 * <code>polish.TextField.usePredictiveInputMode</code>.
	 * 
	 * @param field the textfield
	 * @param words array of allowed words - use null to reset the allowed words to the default RMS dictionary
	 */
	public static void setPredictiveDictionary(javax.microedition.lcdui.TextField field, String[] words)
	{
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Allows the given words for the specified textfield.
	 * Note that you need to enable the predictive input mode using the preprocessing variable
	 * <code>polish.TextField.usePredictiveInputMode</code>.
	 * 
	 * @param field the textfield
	 * @param words array of allowed words - use null to reset the allowed words to the default RMS dictionary
	 */
	public static void setPredictiveDictionary(TextField field, String[] words)
	{
		field.setPredictiveDictionary( words );
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves matching words for the specified textfield.
	 * Note that you need to enable the predictive input mode using the preprocessing variable
	 * <code>polish.TextField.usePredictiveInputMode</code>.
	 * 
	 * @param field the textfield
	 * @return ArrayList&lt;String&gt; of allowed words - null when no preditive mode is used
	 */
	public static ArrayList getPredictiveMatchingWords(javax.microedition.lcdui.TextField field)
	{
		return null;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves matching words for the specified textfield.
	 * Note that you need to enable the predictive input mode using the preprocessing variable
	 * <code>polish.TextField.usePredictiveInputMode</code>.
	 * 
	 * @param field the textfield
	 * @return ArrayList&lt;String&gt; of allowed words - null when no predictive mode is used
	 */
	public static ArrayList getPredictiveMatchingWords(TextField field)
	{
		return field.getPredictiveMatchingWords();
	}
	//#endif
	
	//#if polish.midp
	//TODO andre: document
	public static void setTextfieldInfo(javax.microedition.lcdui.TextField field, String info)
	{
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	//TODO andre: document
	public static void setTextfieldInfo(TextField field, String info)
	{
		field.setPredictiveInfo( info );
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets the item responsible for displaying the current input mode like Abc, ABC, 123, and so on.
	 * This feature requires that the preprocessing variable
	 * "polish.TextField.useExternalInfo" is set to "true".
	 * 
	 * @param field the TextField
	 * @param infoItem the StringItem that should be used for rendering the current input mode. 
	 */
	public static void setTextfieldInfoItem(javax.microedition.lcdui.TextField field, javax.microedition.lcdui.StringItem infoItem)
	{
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the item responsible for displaying the current input mode like Abc, ABC, 123, and so on.
	 * This feature requires that the preprocessing variable
	 * "polish.TextField.useExternalInfo" is set to "true".
	 * 
	 * @param field the TextField
	 * @param infoItem the StringItem that should be used for rendering the current input mode. 
	 */
	public static void setTextfieldInfoItem(TextField field, StringItem infoItem)
	{
	 	//#if polish.TextField.useDirectInput && polish.TextField.useExternalInfo && !polish.blackberry
			field.setInfoItem(infoItem);
		//#endif
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets the help text for the specified TextField.
	 * This text will be shown when the TextField's content is null.
	 * This can only be called when the preprocessing variable
	 * "polish.TextField.showHelpText" is true.
	 * 
	 * @param field the TextField
	 * @param text the help text
	 */
	public static void setTextfieldHelp(javax.microedition.lcdui.TextField field, String text)
	{
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the help text for the specified TextField.
	 * This text will be shown when the TextField's content is null.
	 * This can only be called when the preprocessing variable
	 * "polish.TextField.showHelpText" is true.
	 * 
	 * @param field the TextField
	 * @param text the help text
	 */
	public static void setTextfieldHelp(TextField field, String text)
	{
		//#if polish.TextField.showHelpText
	 	field.setHelpText(text);
	 	//#endif
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Can be used to deactivate or activate commands for a specific TextField.
	 * This has no effect when commands are globally suppressed by settting
	 * the preprocessing variable "polish.TextField.suppressCommands" to "true".
	 * 
	 * @param field the TextField
	 * @param suppress true when commands should be surpressed
	 */
	public static void setSuppressCommands(javax.microedition.lcdui.TextField field, boolean suppress)
	{
		// ignore
	}
	//#endif

 	//#if polish.usePolishGui
	/**
	 * Can be used to deactivate or activate commands for a specific TextField.
	 * This has no effect when commands are globally suppressed by settting
	 * the preprocessing variable "polish.TextField.suppressCommands" to "true".
	 * 
	 * @param field the TextField
	 * @param suppress true when commands should be surpressed
	 */
	public static void setSuppressCommands(TextField field, boolean suppress)
	{
		field.setSuppressCommands(suppress);
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets an ItemStateListener specifically for this item.
	 * Change events are forwarded to both this listener as well as a possibly set listener of the
	 * corresponding screen.
	 * 
	 * @param item the item
	 * @param listener the listener
	 */
	public static void setItemStateListener(Item item, ItemStateListener listener) {
		item.setItemStateListener( listener );
		
	}
	//#endif

	//#if polish.midp2 && !polish.android
	/**
	 * Sets an ItemStateListener specifically for this item.
	 * Change events are forwarded to both this listener as well as a possibly set listener of the
	 * corresponding screen.
	 * 
	 * @param item the item
	 * @param listener the listener
	 */
	public static void setItemStateListener(javax.microedition.lcdui.Item item, javax.microedition.lcdui.ItemStateListener listener) {
		// ignore
		
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Calls show notify on the specified item.
	 * 
	 * @param item the item
	 */
	public static void showNotify(Item item) {
		item.showNotify();
	}
	//#endif

	//#if polish.midp
	/**
	 * Calls show notify on the specified item.
	 * 
	 * @param item the item
	 */
	public static void showNotify(javax.microedition.lcdui.Item item) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Calls show notify on the specified item.
	 * 
	 * @param screen the corresponding screen of that item
	 * @param item the item
	 */
	public static void showNotify(Screen screen, Item item) {
		item.screen = screen;
		item.showNotify();
	}
	//#endif

	//#if polish.midp
	/**
	 * Calls show notify on the specified item.
	 * 
	 * @param screen the corresponding screen of that item
	 * @param item the item
	 */
	public static void showNotify(javax.microedition.lcdui.Screen screen, javax.microedition.lcdui.Item item) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Calls hide notify on the specified item.
	 * 
	 * @param item the item
	 */
	public static void hideNotify(Item item) {
		item.hideNotify();
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Calls hide notify on the specified item.
	 * 
	 * @param item the item
	 */
	public static void hideNotify(javax.microedition.lcdui.Item item) {
		// ignore
	}
	//#endif

	
	//#if polish.midp
	/**
	 * Set the word-not-found box in the textfield
	 * 
	 * @param field the textfield
	 * @param alert the alert
	 */
	public static void setWordNotFound(javax.microedition.lcdui.TextField field, javax.microedition.lcdui.Alert alert)
	{
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Set the word-not-found box in the textfield
	 * 
	 * @param field the textfield
	 * @param alert the alert
	 */
	public static void setWordNotFound(TextField field, Alert alert)
	{
		field.setPredictiveWordNotFoundAlert( alert );
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Specifies if the given contain is allowed to cycle through its items.
	 * 
	 * @param container the container
	 * @param cycling true, if cycling should be allowed, otherwise false
	 */
	public static void setCycling(Container container, boolean cycling)
	{
		//#if polish.Container.allowCycling != false
	 		container.allowCycling = cycling;
	 	//#endif
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Specifies if the given FramedForm is allowed to cycle through its items.
	 * Needs polish.FramedForm.allowCycling to be set to true.
	 * 
	 * @param form the FramedForm
	 * @param cycling true, if cycling should be allowed, otherwise false
	 */
	public static void setCycling(FramedForm form, boolean cycling)
	{
		//#if polish.FramedForm.allowCycling
	 		form.allowCycling = cycling;
	 	//#endif
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the horizontal content start for the given screen.
	 * @param screen the screen
	 * @return the horizontal start position in pixels from the left, -1 when it is unknown 
	 */
	public static int getContentX( javax.microedition.lcdui.Screen screen ) {
		return -1;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the vertical content start for the given screen.
	 * @param screen the screen
	 * @return the vertical start position in pixels from the top, -1 when it is unknown 
	 */
	public static int getContentY( javax.microedition.lcdui.Screen screen ) {
		return -1;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the horizontal content width for the given screen.
	 * @param screen the screen
	 * @return the horizontal content width in pixels, -1 when it is unknown 
	 */
	public static int getContentWidth( javax.microedition.lcdui.Screen screen ) {
		return -1;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the vertical content width for the given screen.
	 * @param screen the screen
	 * @return the vertical content width in pixels, -1 when it is unknown 
	 */
	public static int getContentHeight( javax.microedition.lcdui.Screen screen ) {
		return -1;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the horizontal content start for the given screen.
	 * @param screen the screen
	 * @return the horizontal start position in pixels from the left, -1 when it is unknown 
	 */
	public static int getContentX( Screen screen ) {
		if (screen.contentWidth == 0) {
			screen.calculateContentArea(0, 0, Display.getScreenWidth(), Display.getScreenHeight() );
		}
		return screen.contentX;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the vertical content start for the given screen.
	 * @param screen the screen
	 * @return the vertical start position in pixels from the top, -1 when it is unknown 
	 */
	public static int getContentY( Screen screen ) {
		if (screen.contentWidth == 0) {
			screen.calculateContentArea(0, 0, Display.getScreenWidth(), Display.getScreenHeight() );
		}
		return screen.contentY;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the horizontal content width for the given screen.
	 * @param screen the screen
	 * @return the horizontal content width in pixels, -1 when it is unknown 
	 */
	public static int getContentWidth( Screen screen ) {
		if (screen.contentWidth == 0) {
			screen.calculateContentArea(0, 0, Display.getScreenWidth(), Display.getScreenHeight() );
		}
		return screen.contentWidth;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the vertical content width for the given screen.
	 * @param screen the screen
	 * @return the vertical content width in pixels, -1 when it is unknown 
	 */
	public static int getContentHeight( Screen screen ) {
		if (screen.contentHeight == 0) {
			screen.calculateContentArea(0, 0, Display.getScreenWidth(), Display.getScreenHeight() );
		}
		return screen.contentHeight;
	}
	//#endif
	
	//#if polish.midp2  && !polish.android
	/**
	 * Notifies the specified CustomItem that a key or pointer event has been processed and that it should not be processed by other components.
	 * This is useful when overriding keyPressed, keyReleased, keyRepeated or pointerPressed in CustomItems -
	 * these methods do not have a return value that indicates if the event has been handled by the CustomItem.
	 * In J2ME Polish there are <code>protected boolean handleKeyPressed(int keyCode, int gameAction )</code> etc methods
	 * for this purpose, however in a CustomItem these will call <code>protected void keyPressed(int keyCode)</code> etc.
	 * To abort processing of the event, you either have to call <code>invalidate()</code>, <code>repaint()</code> or 
	 * <code>UiAccess.setEventHandled( CustomItem item )</code>
	 * 
	 * @param item the CustomItem which just handled an user input event within keyPressed/keyReleased/pointerPressed/etc. 
	 */
	public void setEventHandled( javax.microedition.lcdui.CustomItem item ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui && polish.TextField.useDirectInput && polish.TextField.supportSymbolsEntry && !polish.blackberry
	/**
	 * Initializes the symbols list and returns it 
	 * @return the symbols list
	 */
	public static List getTextFieldSymbols() {
		TextField.initSymbolsList();
		return TextField.symbolsList;
	}
	//#endif
		
	//#if polish.usePolishGui
	/**
	 * Notifies the specified CustomItem that a key or pointer event has been processed and that it should not be processed by other components.
	 * This is useful when overriding keyPressed, keyReleased, keyRepeated or pointerPressed in CustomItems -
	 * these methods do not have a return value that indicates if the event has been handled by the CustomItem.
	 * In J2ME Polish there are <code>protected boolean handleKeyPressed(int keyCode, int gameAction )</code> etc methods
	 * for this purpose, however in a CustomItem these will call <code>protected void keyPressed(int keyCode)</code> etc.
	 * To abort processing of the event, you either have to call <code>invalidate()</code> or 
	 * <code>UiAccess.setEventHandled( CustomItem item )</code>
	 * 
	 * @param item the CustomItem which just handled an user input event within keyPressed/keyReleased/pointerPressed/etc. 
	 */
	public void setEventHandled( CustomItem item ) {
		item.isEventHandled = true;
	}
	//#endif
	
	/**
	 * Simulates a keyPressed event.
	 * The event will be forwarded to the current screen.
	 * 
	 * @param keyCode the keyCode 
	 */
	public static void emitKeyPress( int keyCode ) {
		//#if polish.usePolishGui
		Display display = Display.getInstance();
		if (display != null) {
			display.keyPressed(keyCode);
		}
		//#endif
	}

	/**
     * Simulates a keyRepeated event.
     * The event will be forwarded to the current screen.
     *
     * @param keyCode the keyCode
     */
    public static void emitKeyRepeated( int keyCode ) {
           //#if polish.usePolishGui
           Display display = Display.getInstance();
           if (display != null) {
                  display.keyRepeated(keyCode);
           }
           //#endif
    }
	
	/**
	 * Simulates a keyPressed event.
	 * The event will be forwarded to the current screen.
	 * 
	 * @param gameAction the game action that should be triggered, e.g. Canvas.DOWN or Canvas.FIRE 
	 */
	public static void emitGameActionPress( int gameAction ) {
		//#if polish.usePolishGui
		Display display = Display.getInstance();
		if (display != null) {
			int keyCode = display.getKeyCode(gameAction);
			display.keyPressed(keyCode);
		}
		//#endif
	}
	
	/**
	 * Simulates a keyReleased event.
	 * The event will be forwarded to the current screen.
	 * 
	 * @param keyCode the keyCode 
	 */
	public static void emitKeyRelease( int keyCode ) {
		//#if polish.usePolishGui
		Display display = Display.getInstance();
		if (display != null) {
			display.keyReleased(keyCode);
		}
		//#endif
	}
	
	/**
	 * Simulates a keyReleased event.
	 * The event will be forwarded to the current screen.
	 * 
	 * @param gameAction the game action that should be triggered, e.g. Canvas.DOWN or Canvas.FIRE 
	 */
	public static void emitGameActionRelease( int gameAction ) {
		//#if polish.usePolishGui
		Display display = Display.getInstance();
		if (display != null) {
			int keyCode = display.getKeyCode(gameAction);
			display.keyReleased(keyCode);
		}
		//#endif
	}
	

	//#if polish.usePolishGui && polish.midp
	/**
	 * Casts the given MIDP command into a J2ME Polish command
	 * @param cmd the command
	 * @return the casted command
	 */
	public static Command cast(javax.microedition.lcdui.Command cmd)
	{
		return null;
	}
	//#endif
	//#if polish.usePolishGui
	/**
	 * Casts the given J2ME Polish command into a MIDP command
	 * @param cmd the command
	 * @return the casted command
	 */
	public static 
	//#if polish.LibraryBuild
		javax.microedition.lcdui.Command
	//#else
		//# Command
	//#endif
	cast(Command cmd)
	{
		//#if polish.LibraryBuild
			return null;
		//#else
			//# return cmd;
		//#endif
	}
	//#endif
	

	//#if polish.usePolishGui && polish.midp
	/**
	 * Casts the given MIDP CommandListener into a J2ME Polish CommandListener
	 * @param cmd the CommandListener
	 * @return the casted CommandListener
	 */
	public static CommandListener cast(javax.microedition.lcdui.CommandListener cmd)
	{
		return null;
	}
	//#endif
	//#if polish.usePolishGui
	/**
	 * Casts the given J2ME Polish CommandListener into a MIDP CommandListener
	 * @param cmd the CommandListener
	 * @return the casted CommandListener
	 */
	public static 
	//#if polish.LibraryBuild
		javax.microedition.lcdui.CommandListener
	//#else
		//# CommandListener
	//#endif
	cast(CommandListener cmd)
	{
		//#if polish.LibraryBuild
			return null;
		//#else
			//# return cmd;
		//#endif
	}
	//#endif
	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Casts the given MIDP Displayable into a J2ME Polish Displayable
	 * @param disp the displayable
	 * @return the casted displayable
	 */
	public static Displayable cast(javax.microedition.lcdui.Displayable disp)
	{
		return null;
	}
	//#endif
	//#if polish.usePolishGui
	/**
	 * Casts the given J2ME Polish Displayable into a MIDP Displayable
	 * @param disp the displayable
	 * @return the casted displayable
	 */
	public static 
	//#if polish.LibraryBuild
		javax.microedition.lcdui.Displayable
	//#else
		//# Displayable
	//#endif
	cast(Displayable disp)
	{
		//#if polish.LibraryBuild
			return null;
		//#else
			//# return disp;
		//#endif
	}
	//#endif


	//#if polish.usePolishGui && polish.midp
	/**
	 * Casts the given LCDUI item into a J2ME Polish item
	 * @param item the LCDUI item
	 * @return the corresponding J2ME Polish item, null when no cast is possible
	 */
	public static Item cast( javax.microedition.lcdui.Item item ) {
		return null;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Casts the given J2ME Polish item into a LCDUI item
	 * @param item the J2ME Polish item
	 * @return the corresponding LCDUI item, null when no cast is possible
	 */
	public static
	//#if polish.LibraryBuild
		javax.microedition.lcdui.Item
	//#else
		//# Item 
	//#endif
	cast(Item item ) {
		//#if polish.LibraryBuild
			return null;
		//#else
			//# return item;
		//#endif
	}
	//#endif
	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Casts the given LCDUI items into J2ME Polish items
	 * @param items the LCDUI items
	 * @return the corresponding J2ME Polish item, null when no cast is possible
	 */
	public static Item[] cast( javax.microedition.lcdui.Item[] items ) {
		return null;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Casts the given J2ME Polish items into a LCDUI items
	 * @param items the J2ME Polish items
	 * @return the corresponding LCDUI item, null when no cast is possible
	 */
	public static
	//#if polish.LibraryBuild
		javax.microedition.lcdui.Item[]
	//#else
		//# Item[] 
	//#endif
	cast(Item[] items ) {
		//#if polish.LibraryBuild
			return null;
		//#else
			//# return items;
		//#endif
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets the screen orientation in 90 degrees steps.
	 * The preprocessing variable "polish.ScreenOrientationCanChangeManually" needs to be set to "true" for supporting this mode.
	 * 
	 * @param screen the screen
	 * @param degrees the screen orientation in degrees: 90, 180, 270 or 0
	 * @deprecated use setScreenOrientation(int degress) instead
	 * @see #setScreenOrientation(int)
	 */
	public static  void setScreenOrientation( Screen screen, int degrees ) {
		setScreenOrientation( degrees );
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets the screen orientation in 90 degrees steps.
	 * The preprocessing variable "polish.ScreenOrientationCanChangeManually" needs to be set to "true" for supporting this mode.
	 * 
	 * @param degrees the screen orientation in degrees: 90, 180, 270 or 0
	 */
	public static  void setScreenOrientation( int degrees ) {
		Display.setScreenOrientation(degrees);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets the screen orientation in 90 degrees steps.
	 * @param screen the screen
	 * @param degrees the screen orientation in degrees: 90, 180, 270 or 0
	 * @deprecated use setScreenOrientation(int degress) instead
	 * @see #setScreenOrientation(int)
	 */
	public static void setScreenOrientation( javax.microedition.lcdui.Screen screen, int degrees ) {
		// ignore
	}
	//#endif

	//#if polish.midp
	/**
	 * Determines whether the given key code is the left soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param screen the screen
	 * @return true when the given key is the right soft key
	 */
	public static boolean isSoftKeyLeft( int keyCode, javax.microedition.lcdui.Screen screen ) {
		return false;
	}
	//#endif

	//#if polish.midp
	/**
	 * Determines whether the given key code is the right soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param screen the screen
	 * @return true when the given key is the right soft key
	 */
	public static boolean isSoftKeyRight( int keyCode, javax.microedition.lcdui.Screen screen ) {
		return false;
	}
	//#endif

	//#if polish.midp
	/**
	 * Determines whether the given key code is the middle soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param screen the screen
	 * @return true when the given key is the middle soft key
	 */
	public static boolean isSoftKeyMiddle( int keyCode, javax.microedition.lcdui.Screen screen ) {
		return false;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Determines whether the given key code is a soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param screen the screen
	 * @return true when the given key is the middle soft key
	 */
	public static boolean isSoftKey( int keyCode, javax.microedition.lcdui.Screen screen ) {
		return false;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Determines whether the given key code is the left soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param gameAction the corresponding game action
	 * @param screen the screen
	 * @return true when the given key is the right soft key
	 */
	public static boolean isSoftKeyLeft( int keyCode, int gameAction, javax.microedition.lcdui.Screen screen ) {
		return false;
	}
	//#endif

	//#if polish.midp
	/**
	 * Determines whether the given key code is the right soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param gameAction the corresponding game action
	 * @param screen the screen
	 * @return true when the given key is the right soft key
	 */
	public static boolean isSoftKeyRight( int keyCode, int gameAction, javax.microedition.lcdui.Screen screen ) {
		return false;
	}
	//#endif

	//#if polish.midp
	/**
	 * Determines whether the given key code is the middle soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param gameAction the corresponding game action
	 * @param screen the screen
	 * @return true when the given key is the middle soft key
	 */
	public static boolean isSoftKeyMiddle( int keyCode, int gameAction, javax.microedition.lcdui.Screen screen ) {
		return false;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Determines whether the given key code is a soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param gameAction the corresponding game action
	 * @param screen the screen
	 * @return true when the given key is the middle soft key
	 */
	public static boolean isSoftKey( int keyCode, int gameAction,  javax.microedition.lcdui.Screen screen ) {
		return false;
	}
	//#endif
	
	

	//#if polish.usePolishGui
	/**
	 * Determines whether the given key code is the left soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param screen the screen
	 * @return true when the given key is the right soft key
	 */
	public static boolean isSoftKeyLeft( int keyCode, Screen screen ) {
		return screen.isSoftKeyLeft( keyCode, getGameAction(keyCode, screen) );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Determines whether the given key code is the right soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param screen the screen
	 * @return true when the given key is the right soft key
	 */
	public static boolean isSoftKeyRight( int keyCode, Screen screen ) {
		return screen.isSoftKeyRight( keyCode, getGameAction(keyCode, screen) );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Determines whether the given key code is the middle soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param screen the screen
	 * @return true when the given key is the middle soft key
	 */
	public static boolean isSoftKeyMiddle( int keyCode, Screen screen ) {
		return screen.isSoftKeyMiddle( keyCode, getGameAction(keyCode, screen) );
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Determines whether the given key code is the middle soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param screen the screen
	 * @return true when the given key is the middle soft key
	 */
	public static boolean isSoftKey( int keyCode, Screen screen ) {
		return screen.isSoftKeyMiddle( keyCode, getGameAction(keyCode, screen) );
	}
	//#endif
	
	//#if polish.usePolishGui
	private static int getGameAction(int keyCode, Screen screen) {
		int gameAction = 0;
		try {
			gameAction = screen.getGameAction(keyCode);
		} catch (Exception e) {
			// could be illegal key code for game action
		}
		return gameAction;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Determines whether the given key code is the left soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param gameAction the corresponding game action
	 * @param screen the screen
	 * @return true when the given key is the right soft key
	 */
	public static boolean isSoftKeyLeft( int keyCode, int gameAction, Screen screen ) {
		return screen.isSoftKeyLeft( keyCode, gameAction );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Determines whether the given key code is the right soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param gameAction the corresponding game action
	 * @param screen the screen
	 * @return true when the given key is the right soft key
	 */
	public static boolean isSoftKeyRight( int keyCode, int gameAction, Screen screen ) {
		return screen.isSoftKeyRight( keyCode, gameAction );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Determines whether the given key code is the middle soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param gameAction the corresponding game action
	 * @param screen the screen
	 * @return true when the given key is the middle soft key
	 */
	public static boolean isSoftKeyMiddle( int keyCode, int gameAction, Screen screen ) {
		return screen.isSoftKeyMiddle( keyCode, gameAction );
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Determines whether the given key code is a soft key for the given screen.
	 * If the screen is rotated, the key detection might also change.
	 * 
	 * @param keyCode the keyCode value
	 * @param gameAction the corresponding game action
	 * @param screen the screen
	 * @return true when the given key is the middle soft key
	 */
	public static boolean isSoftKey( int keyCode, int gameAction, Screen screen ) {
		return screen.isSoftKey( keyCode, gameAction );
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Determines whether the given key is really a Canvas.FIRE game action
	 * @param keyCode the key code
	 * @param gameAction the game action
	 * @param screen the screen
	 * @return true when the gameAction is Canvas.FIRE and the given key is not '5' or a soft key
	 */
	public static boolean isGameActionFire( int keyCode, int gameAction, javax.microedition.lcdui.Screen screen ) {
		return gameAction == javax.microedition.lcdui.Canvas.FIRE && keyCode != javax.microedition.lcdui.Canvas.KEY_NUM5; 
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Determines whether the given key is really a Canvas.FIRE game action
	 * @param keyCode the key code
	 * @param gameAction the game action
	 * @param screen the screen
	 * @return true when the gameAction is Canvas.FIRE and the given key is not '5' or a soft key
	 */
	public static boolean isGameActionFire( int keyCode, int gameAction, Screen screen ) {
		return screen.isGameActionFire( keyCode, gameAction );
	}
	//#endif

	
	//#if polish.usePolishGui
	/**
	 * Set the style of the specified screen's menubar.
	 * A full style is only applied when the external menubar is used, set the preprocessing variable
	 * polish.MenuBar.useExtendedMenuBar to true for this.
	 * Specify the style with a //#style preprocessing directive, e.g.
	 * <pre>
	 * //#style newMenuBarStyle
	 * UiAccess.setMenuBarStyle( myScreen );
	 * </pre>
	 * 
	 * @param screen the screen
	 */
	public static void setMenuBarStyle(Screen screen)
	{
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Set the style of the specified screen's menubar.
	 * A full style is only applied when the external menubar is used, set the preprocessing variable
	 * polish.MenuBar.useExtendedMenuBar to true for this.
	 * 
	 * @param screen the screen
	 * @param style the style
	 */
	public static void setMenuBarStyle(Screen screen, Style style)
	{
		screen.setMenuBarStyle( style );
	}
	//#endif
	
	
	//#if polish.midp
	/**
	 * Set the style of the specified screen's menubar.
	 * A full style is only applied when the external menubar is used, set the preprocessing variable
	 * polish.MenuBar.useExtendedMenuBar to true for this.
	 * Specify the style with a //#style preprocessing directive, e.g.
	 * <pre>
	 * //#style newMenuBarStyle
	 * UiAccess.setMenuBarStyle( myScreen );
	 * </pre>
	 * 
	 * @param screen the screen
	 */
	public static void setMenuBarStyle(javax.microedition.lcdui.Screen screen)
	{
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Set the style of the specified screen's menubar.
	 * A full style is only applied when the external menubar is used, set the preprocessing variable
	 * polish.MenuBar.useExtendedMenuBar to true for this.
	 * 
	 * @param screen the screen
	 * @param style the style
	 */
	public static void setMenuBarStyle(javax.microedition.lcdui.Screen screen, Style style)
	{
		// ignore
	}
	//#endif
	
	
	//#if polish.usePolishGui
	/**
	 * Retrieves text from a J2ME Polish item.
	 * @param item the item
	 * @return the text within the item, e.g. a StringItem or a TextField
	 */
	public static String getText( Item item) {
		if (item instanceof StringItem) {
			return ((StringItem)item).getText();
		}
		return null;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves text from a J2ME Polish item.
	 * @param item the item
	 * @return the text within the item, e.g. a StringItem or a TextField
	 */
	public static String getText( javax.microedition.lcdui.Item item) {
		if (item instanceof javax.microedition.lcdui.StringItem) {
			return ((javax.microedition.lcdui.StringItem)item).getText();
		} else if (item instanceof javax.microedition.lcdui.TextField) {
			return ((javax.microedition.lcdui.TextField)item).getString();
		}
		return null;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Determines the height of the title of the specified screen.
	 * This gives only a result after the screen has been shown.
	 * @param screen the screen
	 * @return the screen's title height or 0 when the the screen has not been initialized or J2ME Polish is not used in fullscreen mode
	 */
	public static int getTitleHeight(Screen screen)
	{
		return screen.titleHeight;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Determines the height of the title of the specified screen.
	 * This gives only a result after the screen has been shown.
	 * @param screen the screen
	 * @return the screen's title height or 0 when the the screen has not been initialized or J2ME Polish is not used in fullscreen mode
	 */
	public static int getTitleHeight(javax.microedition.lcdui.Screen screen)
	{
		return 0;
	}
	//#endif

	//#if polish.midp
	/**
	 * Sets the 'screen' property of the specified item to the given item
	 * @param item the item
	 * @param screen the screen
	 */
	public static void setItemScreen(javax.microedition.lcdui.Item item, javax.microedition.lcdui.Screen screen)
	{
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets the 'screen' property of the specified item to the given item
	 * @param item the item
	 * @param screen the screen
	 */
	public static void setItemScreen(Item item, Screen screen)
	{
		item.screen = screen;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Deletes the specified item from the form
	 * @param form the form
	 * @param item the item that should be deleted
	 * @return true when the delete was successful
	 */
	public static boolean delete(Form form, Item item)
	{
		Container container = form.container;
		if (container != null) {
			return container.remove(item);
		}
		return false;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Deletes the specified item from the form
	 * @param form the form
	 * @param item the item that should be deleted
	 * @return true when the delete was successful
	 */
	public static boolean delete(javax.microedition.lcdui.Form form, javax.microedition.lcdui.Item item)
	{
		return false;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves the title of the specified screen as an Item
	 * @param screen the screen
	 * @return the screen's title as an Item
	 * @see #setTitle(Screen, Item)
	 * @see #setTitle(Screen, Item, Style)
	 */
	public static Item getTitleItem(Screen screen) {
		return screen.getTitleItem();
	}
	//#endif


	//#if polish.midp
	/**
	 * Retrieves the title of the specified screen as an Item
	 * @param screen the screen
	 * @return the screen's title as an Item
	 * @see #setTitle(javax.microedition.lcdui.Screen, Item)
	 * @see #setTitle(javax.microedition.lcdui.Screen, javax.microedition.lcdui.Item)
	 */
	public static javax.microedition.lcdui.Item getTitleItem(javax.microedition.lcdui.Screen screen) {
		return null;
	}
	//#endif

	//#if polish.usePolishGui && polish.css.text-effect
	/**
	 * Specifies a new text effect for the specified string item.
	 * Note that this method is only accessible when you use the 'text-effect' CSS property in your polish.css.
	 * 
	 * @param item the string item
	 * @param effect the effect that should be applied
	 */
	public static void setTextEffect(StringItem item, TextEffect effect)
	{
		Style style = item.getStyle();
		if (style != null) {
			style = style.clone(true);
		} else {
			style = new Style();
		}
		style.addAttribute(88, effect);
		item.setStyle(style);
	}
	//#endif
	
	//#if polish.midp && polish.usePolishGui && (polish.css.text-effect || polish.LibraryBuild)
	/**
	 * Specifies a new text effect for the specified string item.
	 * Note that this method is only accessible when you use the 'text-effect' CSS property in your polish.css.
	 * 
	 * @param item the string item
	 * @param effect the effect that should be applied
	 */
	public static void setTextEffect(javax.microedition.lcdui.StringItem item, TextEffect effect) {
		// ignore
	}
	//#endif	
	
	//#if polish.usePolishGui
	/**
	 * Fires an event for the specified screen and all its components.
	 * This is typically used for triggering animations within screen components like its title or menubar.
	 * Since all screen components fire events, this method should be called within a background thread and never
	 * from within a de.enough.polish.event.EventListener.
	 * 
	 * @param name the name of the event
	 * @param screen the source of the event
	 * @param data the event's data, can be null
	 * @see UiAccess#fireEventForTitleAndMenubar(String, Screen, Object)
	 */
	public static void fireEvent( String name, Screen screen, Object data) {
		screen.fireEvent(name, data);
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Fires an event for the title and menubar of the specified screen.
	 * This is typically used for triggering animations for the title and/or menubar.
	 * Since all these screen components fire events, this method should be called within a background thread and never
	 * from within a de.enough.polish.event.EventListener.
	 * This method is not cycling through all screen components and its subcomponents, so it is a multitude faster
	 * than UiAccess.fireEvent( String name, Screen screen, Object data ).
	 * 
	 * @param name the name of the event
	 * @param screen the source of the event
	 * @param data the event's data, can be null
	 */
	public static void fireEventForTitleAndMenubar( String name, Screen screen, Object data) {
		screen.fireEventForTitleAndMenubar(name, data);
	}
	//#endif
	
	
	//#if polish.usePolishGui
	/**
	 * Fires an event for the specified item and all its subitems.
	 * This is typically used for triggering animations within item components like ChoiceItems within a ChoiceGroup, etc.
	 * Since all subitem fire events, this method should be called within a background thread and never
	 * from within a de.enough.polish.event.EventListener.
	 * 
	 * @param name the name of the event
	 * @param item the source of the event
	 * @param data the event's data, can be null
	 */
	public static void fireEvent( String name, Item item, Object data) {
		item.fireEvent(name, data);
	}
	//#endif
	
	
	//#if polish.midp
	/**
	 * Fires an event for the specified screen and all its components.
	 * This is typically used for triggering animations within screen components like its title or menubar.
	 * Since all screen components fire events, this method should be called within a background thread and never
	 * from within a de.enough.polish.event.EventListener.
	 * 
	 * @param name the name of the event
	 * @param screen the source of the event
	 * @param data the event's data, can be null
	 */
	public static void fireEvent( String name, javax.microedition.lcdui.Screen screen, Object data) {
		EventManager.fireEvent( name, screen, data );
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Fires an event for the title and menubar of the specified screen.
	 * This is typically used for triggering animations for the title and/or menubar.
	 * Since all these screen components fire events, this method should be called within a background thread and never
	 * from within a de.enough.polish.event.EventListener.
	 * 
	 * @param name the name of the event
	 * @param screen the source of the event
	 * @param data the event's data, can be null
	 * @see UiAccess#fireEventForTitleAndMenubar(String, javax.microedition.lcdui.Screen, Object)
	 */
	public static void fireEventForTitleAndMenubar( String name, javax.microedition.lcdui.Screen screen, Object data) {
		EventManager.fireEvent( name, screen, data );
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Fires an event for the specified item and all its subitems.
	 * This is typically used for triggering animations within item components like ChoiceItems within a ChoiceGroup, etc.
	 * Since all subitem fire events, this method should be called within a background thread and never
	 * from within a de.enough.polish.event.EventListener.
	 * This method is not cycling through all screen components and its subcomponents, so it is a multitude faster
	 * than UiAccess.fireEvent( String name, Screen screen, Object data ).
	 * 
	 * @param name the name of the event
	 * @param item the source of the event
	 * @param data the event's data, can be null
	 */
	public static void fireEvent( String name, javax.microedition.lcdui.Item item, Object data) {
		EventManager.fireEvent( name, item, data );
	}
	//#endif
	
	//#if polish.midp && polish.usePolishGui
	/**
	 * Retrieves the container of the screen.
	 * Note that this might be null on some screens.
	 * 
	 * @param screen the screen
	 * @return the container belonging to the given screen
	 */
	public static Container getScreenContainer( javax.microedition.lcdui.Screen screen ) {
		return null;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the container of the screen.
	 * Note that this might be null on some screens.
	 * 
	 * @param screen the screen
	 * @return the container belonging to the given screen
	 */
	public static Container getScreenContainer( Screen screen ) {
		return screen.getRootContainer();
	}
	//#endif
	
	
	//#if polish.midp
	/**
	 * Retrieves the lock object for the paint thread.
	 * You can use this paint lock to synchronize with the paint method of the specified screen.
	 * 
	 * @param screen the screen for which the paint lock should be retrieved.
	 * @return the paint lock object
	 */
	public static Object getPaintLock( javax.microedition.lcdui.Screen screen ) {
		return null;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the lock object for the paint thread.
	 * You can use this paint lock to synchronize with the paint method of the specified screen.
	 * 
	 * @param screen the screen for which the paint lock should be retrieved.
	 * @return the paint lock object
	 */
	public static Object getPaintLock( Screen screen ) {
		return screen.getPaintLock();
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Intializes the specified item with the specified dimensions
	 * @param item the item
	 * @param firstLineWidth the first line width
	 * @param availWidth the available width
	 * @param availHeight the available height
	 */
	public static void init(javax.microedition.lcdui.Item item, int firstLineWidth, int availWidth, int availHeight)
	{
		
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Intializes the specified item with the specified dimensions
	 * @param item the item
	 * @param firstLineWidth the first line width
	 * @param availWidth the available width
	 * @param availHeight the available height
	 */
	public static void init(Item item, int firstLineWidth, int availWidth, int availHeight)
	{
		item.init(firstLineWidth, availWidth, availHeight);
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Intializes the specified screen
	 * @param screen the screen
	 */
	public static void init(Screen screen)
	{
		screen.init(Display.getScreenWidth(),Display.getScreenHeight());
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Intializes the specified view
	 * @param view the view
	 * @param container the container
	 * @param firstLineWidth the first line width
	 * @param availWidth the available width
	 * @param availHeight the available height
	 */
	public static void initView(ContainerView view, Container container, int firstLineWidth, int availWidth,
			int availHeight)
	{
		view.parentItem = container;
		view.parentContainer = container;
		view.init(container, firstLineWidth,availWidth,availHeight);
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Makes the notifyItemPressedStart method publicly available.
	 * @param item the item which is pressed
	 */
	public static void notifyItemPressedStart(Item item) {
		item.notifyItemPressedStart();
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Makes the notifyItemPressedStart method publicly available.
	 * @param item the item which is pressed
	 */
	public static void notifyItemPressedStart(javax.microedition.lcdui.Item item) {
		// ignore
	}
	//#endif
	
	

	//#if polish.usePolishGui
	/**
	 * Makes the notifyItemPressedEnd method publicly available.
	 * @param item the item which is not pressed anymore
	 */
	public static void notifyItemPressedEnd(Item item) {
		item.notifyItemPressedEnd();
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Makes the notifyItemPressedEnd method publicly available.
	 * @param item the item which is not pressed anymore
	 */
	public static void notifyItemPressedEnd(javax.microedition.lcdui.Item item) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Set if the textfield should accept the enter key as an input which results in a new line.
	 * 
	 * @param textField the text field
	 * @param noNewLine set if new lines should be ignored
	 */
	public static void setNoNewLine(TextField textField, boolean noNewLine) {
		if(textField != null){
			textField.setNoNewLine(noNewLine);
		}
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Set if the textfield should accept the enter key as an input which results in a new line.
	 * 
	 * @param textField the text field
	 * @param noNewLine set if new lines should be ignored
	 */
	public static void setNoNewLine(javax.microedition.lcdui.TextField textField, boolean noNewLine) {
		// ignore
	}
	//#endif
	
	/**
	 * Enables or disables screen change animations
	 * @param enable true if screen change animations should be run otherwise false
	 */
	public static void enableScreenChangeAnimations(boolean enable) {
		//#if polish.usePolishGui && polish.css.screen-change-animation
			Display display = Display.getInstance();
			if(display != null) {
				display.enableScreenChangeAnimations = enable;
			}
			else if (StyleSheet.midlet != null){
				Display.getDisplay(StyleSheet.midlet).enableScreenChangeAnimations = enable;
			}
		//#endif
	}
	
	//#if polish.usePolishGui 
	/**
	 * Enables or disables screen change animations for the specified screen
	 * 
	 * @param screen the screen
	 * @param enable true if screen change animations should be run otherwise false
	 */
	public static void enableScreenChangeAnimation(Screen screen, boolean enable) {
		//#if polish.usePolishGui && polish.css.screen-change-animation
			screen.enableScreenChangeAnimation = enable;
		//#endif
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Enables or disables screen change animations for the specified screen
	 * 
	 * @param screen the screen
	 * @param enable true if screen change animations should be run otherwise false
	 */
	public static void enableScreenChangeAnimation(javax.microedition.lcdui.Screen screen, boolean enable) {
		// ignore
	}
	//#endif
	
	//#if polish.Display.useKeyValidator
	/**
	 * Sets the key validator for the current display. MUST be set after first Display.getDisplay().
	 * @param validator the key validator  
	 */
	public static void setUserInputValidator(de.enough.polish.ui.Display.UserInputValidator validator) {
		//#if polish.usePolishGui 
		Display display = Display.getInstance();
		if(display != null) {
			display.setKeyValidator(validator);
		}
		else if (StyleSheet.midlet != null){
			Display.getDisplay(StyleSheet.midlet).setKeyValidator(validator);
		}
		//#endif
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Repaints the specified item
	 * @param item the item
	 */
	public static void repaint(Item item) {
		item.repaint();
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Repaints the specified item
	 * @param item the item
	 */
	public static void repaint(javax.microedition.lcdui.Item item) {
		// ignore
	}
	//#endif

	//#if polish.midp
	/**
	 * Sets an UiEventListener for the specified screen and its items.
	 * @param screen the screen
	 * @param listener the listener, use null to remove a listener
	 */
	public static void setUiEventListener( javax.microedition.lcdui.Screen screen, UiEventListener listener) {
		// ignore
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets an UiEventListener for the specified item and its children.
	 * @param item the item for which the listener should be registered
	 * @param listener the listener, use null to remove a listener
	 */
	public static void setUiEventListener( javax.microedition.lcdui.Item item, UiEventListener listener) {
		// ignore
	}
	//#endif

	//#if polish.midp
	/**
	 * Retrieves an UiEventListener for the specified screen and its items.
	 * @param screen the screen
	 * @return the listener or null
	 */
	public static UiEventListener getUiEventListener( javax.microedition.lcdui.Screen screen) {
		return null;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves an UiEventListener for the specified item
	 * @param item the item
	 * @return the listener of the item or one of its parents or null
	 */
	public static UiEventListener getUiEventListener( javax.microedition.lcdui.Item item ) {
		return null;
	}
	//#endif

	
	//#if polish.usePolishGui
	/**
	 * Sets an UiEventListener for the specified screen and its items.
	 * @param screen the screen
	 * @param listener the listener, use null to remove a listener
	 */
	public static void setUiEventListener( Screen screen, UiEventListener listener) {
		screen.setUiEventListener( listener );
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets an UiEventListener for the specified item and its children.
	 * @param item the item for which the listener should be registered
	 * @param listener the listener, use null to remove a listener
	 */
	public static void setUiEventListener( Item item, UiEventListener listener) {
		item.setUiEventListener( listener );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves an UiEventListener for the specified screen and its items.
	 * @param screen the screen
	 * @return the listener or null
	 */
	public static UiEventListener getUiEventListener(Screen screen) {
		return screen.getUiEventListener();
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves an UiEventListener for the specified item
	 * @param item the item
	 * @return the listener of the item or one of its parents or null
	 */
	public static UiEventListener getUiEventListener(Item item ) {
		return item.getUiEventListener();
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Paints the content of the specified item.
	 * 
	 * @param item the item which content should be painted 
	 * @param x the left start position
	 * @param y the upper start position
	 * @param leftBorder the left border, nothing must be painted left of this position
	 * @param rightBorder the right border, nothing must be painted right of this position
	 * @param g the Graphics on which this item should be painted.
	 */
	public static void paintContent(Item item, int x, int y, int leftBorder, int rightBorder, Graphics g) {
		item.paintContent(x, y, leftBorder, rightBorder, g);
	}
	//#endif
	
	//#if polish.usePolishGui
	
	/**
	 * Initializes the content of the specified item.
	 * @param item the item which content should be initialized
	 * @param firstLineWidth the first line width
	 * @param availWidth the available width
	 * @param availHeight the available height
	 */
	public static void initContent(Item item,int firstLineWidth, int availWidth, int availHeight) {
		item.initContent(firstLineWidth, availWidth, availHeight);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Paints the content of the specified item.
	 * 
	 * @param item the item which content should be painted 
	 * @param x the left start position
	 * @param y the upper start position
	 * @param leftBorder the left border, nothing must be painted left of this position
	 * @param rightBorder the right border, nothing must be painted right of this position
	 * @param g the Graphics on which this item should be painted.
	 */
	public static void paintContent(javax.microedition.lcdui.Item item, int x, int y, int leftBorder, int rightBorder, Graphics g) {
		// ignore
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Paints the content of the specified Canvas.
	 * 
	 * @param canvas the canvas that should be painted
	 * @param g the Graphics on which the canvas should be painted.
	 */
	public static void paint(javax.microedition.lcdui.Canvas canvas, Graphics g) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Paints the content of the specified Canvas.
	 * 
	 * @param canvas the canvas that should be painted
	 * @param g the Graphics on which the canvas should be painted.
	 */
	public static void paint(Canvas canvas, Graphics g) {
		canvas.paint(g);
	}
	//#endif

	
	/**
	 * Returns the current left margin of the given item
	 * @param item the item
	 * @return the current left margin
	 */
	public static int getMarginLeft(Item item) {
		return item.marginLeft;
	}
	
	/**
	 * Returns the current right margin of the given item
	 * @param item the item
	 * @return the current right margin
	 */
	public static int getMarginRight(Item item) {
		return item.marginRight;
	}
	
	/**
	 * Returns the current left padding of the given item
	 * @param item the item
	 * @return the current left padding 
	 */
	public static int getPaddingLeft(Item item) {
		return item.paddingLeft;
	}
	
	/**
	 * Returns the current right padding of the given item
	 * @param item the item
	 * @return the current right padding 
	 */
	public static int getPaddingRight(Item item) {
		return item.paddingLeft;
	}

	/**
	 * Casts a J2ME Polish image into a MIDP one. 
	 * @param img the input image
	 * @return the output MIDP image
	 */
	public static Image cast(de.enough.polish.ui.Image img) {
		return (Image)img.getNativeImage();
	}
//
//	/**
//	 * Casts a J2ME Polish font into a MIDP font
//	 * @param font the input font
//	 * @return the output MIDP font
//	 */
//	public static javax.microedition.lcdui.Font cast(de.enough.polish.ui.Font font) {
//		return (javax.microedition.lcdui.Font) font.getNativeFont();
//	}
}
