//#condition polish.blackberry && polish.useNativeGui
/*
 * Created on Jan 25, 2010 at 8:38:32 PM.
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

import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.NativeItem;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * <p>Maps exclusive and multiple choicegroups</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ChoiceGroupField 
extends VerticalFieldManager
implements NativeItem, FieldChangeListener
{
	private ChoiceGroup choiceGroup;
	private boolean isIgnoreValueChanged;
	private RadioButtonGroup radioChoiceGroup;
	private boolean isIgnoreValueChange;

	public ChoiceGroupField( ChoiceGroup parent) {
		this.choiceGroup = parent;
		init(parent);
		this.setChangeListener(this);
	}
	
	private void init(ChoiceGroup parent) {
		Object[] items = parent.getInternalArray();
		boolean isRadio = (parent.getType() == ChoiceGroup.EXCLUSIVE);
		RadioButtonGroup group = null;
		if (isRadio) {
			group = new RadioButtonGroup(); 
			this.radioChoiceGroup = group;
		}
		for (int i = 0; i < items.length; i++) {
			ChoiceItem choice = (ChoiceItem) items[i];
			if (choice == null) {
				break;
			}
			if (isRadio) {
				RadioButtonField radio = new RadioButtonField( choice.getText(), group, choice.isSelected(), Field.EDITABLE );
				add(radio);
			} else {
				CheckboxField check = new CheckboxField( choice.getText(), choice.isSelected(), Field.EDITABLE);
				add(check);
			}
		}
	}

	public void notifyValueChanged(Item parent, Object value) {
		if (!this.isIgnoreValueChanged) {
			if (value instanceof ChoiceItem) {
				// an item has been added or removed:
				super.deleteAll();
				init( this.choiceGroup );
			} else if (this.radioChoiceGroup != null) {
				int index = this.choiceGroup.getSelectedIndex();
				this.radioChoiceGroup.setSelectedIndex(index);
			} else {
				int size = this.choiceGroup.size();
				for (int i = 0; i < size; i++) {
					CheckboxField field = (CheckboxField) getField(i);
					boolean isSelected = this.choiceGroup.isSelected(i);
					field.setChecked(isSelected);
				}
			}
		}
		
	}


	public void animate(long currentTime, ClippingRegion repaintRegion) {
		// nothing to animate
	}

	public void fieldChanged(Field field, int context) {
		if (context != FieldChangeListener.PROGRAMMATIC) {
			this.isIgnoreValueChange = true;
			try {
				if (this.radioChoiceGroup != null) {
					int selectedIndex = this.radioChoiceGroup.getSelectedIndex();
					this.choiceGroup.setSelectedIndex(selectedIndex, true);
				} else {
					int size = this.choiceGroup.size();
					for (int i = 0; i < size; i++) {
						CheckboxField check = (CheckboxField) getField(i);
						boolean isSelected = check.getChecked();
						this.choiceGroup.setSelectedIndex(i, isSelected);
					}
				}
			} finally {
				this.isIgnoreValueChange = false;
			}
		}		
	}

	public Item getPolishItem() {
		return this.choiceGroup;
	}

}
