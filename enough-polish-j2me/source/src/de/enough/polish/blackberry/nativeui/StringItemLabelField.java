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

import net.rim.device.api.ui.component.LabelField;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.NativeItem;
import de.enough.polish.ui.StringItem;


/**
 * <p>Paints a J2ME Polish field as it behaves normally instead of using a native BlackBerry representation.</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class StringItemLabelField 
extends LabelField
implements NativeItem
{
	
	
	protected StringItem stringItem;

	/**
	 * Creates a new custom field with a FIELD_LEFT style
	 * @param parent the parent item
	 */
	public StringItemLabelField(StringItem parent) {
		this( parent, FieldHelper.getStyle(parent) );
	}

	/**
	 * Creates a new custom field
	 * @param parent the parent item
	 * @param style the BlackBerry native style 
	 */
	public StringItemLabelField(StringItem parent, long style) {
		super(parent.getText(), style);
		this.stringItem = parent;
	}

	public Item getPolishItem() {
		return this.stringItem;
	}

	public void notifyValueChanged(Item parent, Object value) {
		super.setText(value);
	}

	public void animate(long currentTime, ClippingRegion repaintRegion) {
		// nothing to animate
	}

}
