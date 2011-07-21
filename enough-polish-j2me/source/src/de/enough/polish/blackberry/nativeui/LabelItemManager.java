//#condition polish.blackberry && polish.useNativeGui
/*
 * Created on Jan 25, 2010 at 8:00:48 PM.
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

import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.NativeItem;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.HorizontalFieldManager;

/**
 * <p>Stores the label and item horizontally.</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LabelItemManager 
extends HorizontalFieldManager
implements NativeItem
{
	private NativeItem nativeItem;
	
	public LabelItemManager( Field label, Field item) {
		super( item.getStyle() );
		add( label );
		add( item );
		if (item instanceof NativeItem) {
			this.nativeItem = (NativeItem)item;
		}
	}

	public void notifyValueChanged(Item parent, Object value) {
		if (this.nativeItem != null) {
			this.nativeItem.notifyValueChanged(parent, value);
		}
		
	}

	public void animate(long currentTime, ClippingRegion repaintRegion) {
		if (this.nativeItem != null) {
			this.nativeItem.animate(currentTime, repaintRegion);
		}
	}

	public Item getPolishItem() {
		if (this.nativeItem != null) {
			return this.nativeItem.getPolishItem();
		}
		return null;
	}

}
