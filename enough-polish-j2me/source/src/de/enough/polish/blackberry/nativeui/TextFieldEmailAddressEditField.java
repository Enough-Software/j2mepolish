//#condition polish.blackberry && polish.useNativeGui
/*
 * Created on Jan 22, 2010 at 9:24:41 PM.
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

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.EmailAddressEditField;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.NativeItem;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.TextField;


/**
 * <p>Uses a native TextFieldEmailAddressEditField for editing EMAILADDR TextFields.</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TextFieldEmailAddressEditField 
extends EmailAddressEditField 
implements NativeItem, FieldChangeListener
{
	
	
	protected TextField textField;
	private long lastFieldChangedEvent;
	private boolean isIgnoreValueChange;

	/**
	 * Creates a new TextFieldEmailAddressEditField
	 * @param parent the parent item
	 */
	public TextFieldEmailAddressEditField(TextField parent) {
		this( parent, FieldHelper.getStyle(parent) );
	}

	/**
	 * Creates a new TextFieldEmailAddressEditField
	 * @param parent the parent item
	 * @param style the BlackBerry native style 
	 */
	public TextFieldEmailAddressEditField(TextField parent, long style) {
		super(null, parent.getString(), parent.getMaxSize(), TextFieldEditField.getEditStyle(parent, style));
		this.textField = parent;
	}
	
	//#if polish.JavaPlatform < BlackBerry/6.0
	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.Field#makeContextMenu(net.rim.device.api.ui.ContextMenu, int)
	 */
	protected void makeContextMenu(ContextMenu menu, int index) {
		//# super.makeContextMenu(menu, index);
		FieldHelper.makeContextMenu(menu, this.textField);
	}
	//#else
	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.component.BasicEditField#makeContextMenu(net.rim.device.api.ui.ContextMenu)
	 */
	protected void makeContextMenu(ContextMenu menu) {
		super.makeContextMenu(menu);
		FieldHelper.makeContextMenu(menu, this.textField);
	}
	//#endif

	
	/*
	 * (non-Javadoc)
	 * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(net.rim.device.api.ui.Field, int)
	 */
	public void fieldChanged(Field field, int context) {
		if (context != FieldChangeListener.PROGRAMMATIC) {
			this.isIgnoreValueChange = true;
				try {
				TextField tf = this.textField;
				//#if polish.Bugs.ItemStateListenerCalledTooEarly
					int fieldType = tf.getConstraints ()& 0xffff;
					if (fieldType == TextField.NUMERIC || fieldType == TextField.DECIMAL || fieldType == TextField.FIXED_POINT_DECIMAL) {
						tf.setString( getText() );				
						tf.notifyStateChanged();					
					} else {
						long currentTime = System.currentTimeMillis();
						this.lastFieldChangedEvent = currentTime;
						Screen scr = this.textField.getScreen();
						if (scr != null) {
							scr.setLastInteractionTime(currentTime);
						}
					}
				//#else
					tf.setString( getText() );				
					tf.notifyStateChanged();
				//#endif
			} finally {
				this.isIgnoreValueChange = false;
			}
		}
	}
	

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.NativeItem#onValueChanged(de.enough.polish.ui.Item, java.lang.Object)
	 */
	public void notifyValueChanged(Item parent, Object value) {
		if (!this.isIgnoreValueChange) {
			setText( (String) value );
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Animatable#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		//#if polish.Bugs.ItemStateListenerCalledTooEarly
			if (this.lastFieldChangedEvent != 0 && currentTime - this.lastFieldChangedEvent > 500) {
				this.lastFieldChangedEvent = 0;
				this.isIgnoreValueChange = true;
				try {
					this.textField.setString( getText() );
					this.textField.notifyStateChanged();
				} finally {
					this.isIgnoreValueChange = false;
				}
			}
		//#endif
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.NativeItem#getPolishItem()
	 */
	public Item getPolishItem() {
		return this.textField;
	}


}
