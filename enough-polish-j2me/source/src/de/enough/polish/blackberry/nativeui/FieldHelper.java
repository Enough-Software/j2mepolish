//#condition polish.blackberry && polish.useNativeGui
/*
 * Created on Jan 23, 2010 at 3:23:22 AM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.blackberry.nativeui;

import java.util.Hashtable;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import de.enough.polish.blackberry.ui.CommandMenuItem;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.NativeItem;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

/**
 * <p>Helper utility for native representations for J2ME Polish items.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Jan 23, 2010 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FieldHelper
implements FocusChangeListener
{
	/**
	 * The key for storing native fields into item attributes.
	 */
	public static final String KEY_FIELD = "_bbField";
	
	private static FieldHelper instance;
	
	private FieldHelper() {
		// nothing to init
	}
	
	/**
	 * Retrieves the blackberry style for the given item 
	 * @param item the item
	 * @return the corresponding blackberry style, e.g. FOCUSABLE
	 */
	public static long getStyle(Item item) {
		long style = 0;
		if (item.isInteractive()) {
			style = Field.FOCUSABLE;
		} else {
			style = Field.NON_FOCUSABLE;
		}
		return style;
	}

	public static Field createField(Item item) {
		Field field = null;
		if (item instanceof TextField) {
			TextField textField = (TextField) item;
			if ((textField.getConstraints() & TextField.PASSWORD) == TextField.PASSWORD) {
				field = new TextFieldPasswordEditField(textField);
			} else if ((textField.getConstraints() & TextField.EMAILADDR) == TextField.EMAILADDR) {
				field = new TextFieldEmailAddressEditField(textField);
			} else {
				field = new TextFieldEditField(textField);
			}
		} else if (item instanceof StringItem) {
			if (item.getDefaultCommand() != null) {
				field = new StringItemButtonField((StringItem) item, item.getDefaultCommand() );
			} else {
				field = new StringItemLabelField((StringItem) item);
			}
		} else if (item instanceof ChoiceGroup) {
			ChoiceGroup choiceGroup = (ChoiceGroup)item;
			Object[] items = choiceGroup.getInternalArray();
			if (choiceGroup.getType() == ChoiceGroup.POPUP) {
				String[] names = new String[ choiceGroup.size() ];
				for (int i = 0; i < items.length; i++) {
					ChoiceItem choice = (ChoiceItem) items[i];
					if (choice == null) {
						break;
					}
					names[i] = choice.getText();
				}
				field = new ObjectChoiceField(null, names, choiceGroup.getSelectedIndex(), Field.EDITABLE);
			} else {
				field = new ChoiceGroupField( choiceGroup );
			}
		} else {
			field = new ItemField( item );
		}
		return field;
	}
	
	public static void makeContextMenu( ContextMenu menu, Item item) {
		ArrayList commandsList = item.getItemCommands();
		if (commandsList != null) {
			Object[] commands = commandsList.getInternalArray();
			for (int i = 0; i < commands.length; i++) {
				Command command = (Command) commands[i];
				if (command == null) {
					break;
				}
				CommandMenuItem menuItem = new CommandMenuItem( command, item );
				menu.addItem(menuItem);
			}
		}		
	}

	public static Field getField(Item item) {
		Field field = (Field) item.getAttribute( FieldHelper.KEY_FIELD );
		if (field == null) {
			field = FieldHelper.createField( item );
			if (field instanceof NativeItem) {
				item.setNativeItem((NativeItem) field);
				field.setFocusListener(getInstance());
			}			
			if (item.getLabel() != null) {
				field = new LabelItemManager( createField( item.getLabelItem()), field );
			}
			item.setAttribute(FieldHelper.KEY_FIELD, field);
		}
		return field;
	}
	
	public static FieldHelper getInstance() {
		if (instance == null) {
			instance = new FieldHelper();
		}
		return instance;
	}

	public void focusChanged(Field field, int eventType) {
		if ((eventType == FOCUS_GAINED || eventType == FOCUS_CHANGED) && field instanceof NativeItem) {
			NativeItem nativeItem = (NativeItem)field;
			if (eventType == FOCUS_GAINED) {
				Item item = nativeItem.getPolishItem();
				Item parent = item.getParent();
				if (parent instanceof Container) {
					Container cont = (Container)parent;
					cont.focusChild( cont.indexOf(item), item, 0, false);
				}
			} else if (field instanceof Manager) {
				Manager manager = (Manager) field;
				int index = manager.getFieldWithFocusIndex();
				if (nativeItem.getPolishItem() instanceof Container) {
					((Container)nativeItem.getPolishItem()).focusChild(index);
				}
			}
		}
	}

}
