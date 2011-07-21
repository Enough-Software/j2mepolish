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

import de.enough.polish.ui.*;
import de.enough.polish.blackberry.ui.Graphics;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.XYRect;


/**
 * <p>Paints a J2ME Polish field as it behaves normally instead of using a native BlackBerry representation.</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ItemField 
extends Field
implements NativeItem
{
	
	
	private static Graphics graphics = new Graphics();
	protected Item polishItem;
	private int layoutHeight;
	private int layoutWidth;

	/**
	 * Creates a new custom field with a FIELD_LEFT style
	 * @param parent the parent item
	 */
	public ItemField(Item parent) {
		this( parent, FieldHelper.getStyle(parent) );
	}


	/**
	 * Creates a new custom field
	 * @param parent the parent item
	 * @param style the BlackBerry native style 
	 */
	public ItemField(Item parent, long style) {
		super(style);
		this.polishItem = parent;
	}

	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.Field#layout(int, int)
	 */
	protected void layout(int width, int height) {
		this.layoutWidth = width;
		this.layoutHeight = height;
		// layout the item, if necessary:
		this.polishItem.getItemWidth(width, width, height);
		setExtent( this.polishItem.getContentWidth(), this.polishItem.getContentHeight() );
	}

	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.Field#paint(net.rim.device.api.ui.Graphics)
	 */
	protected void paint(net.rim.device.api.ui.Graphics g) {
		graphics.setGraphics(g);
		XYRect clip = g.getClippingRect();
		UiAccess.paintContent( this.polishItem, clip.x, clip.y, clip.x, clip.x + clip.width, graphics.getMidpGraphics() );
	}


	public Item getPolishItem() {
		return this.polishItem;
	}


	public void notifyValueChanged(Item parent, Object value) {
		if (this.layoutWidth != 0) {
			XYRect rect = getExtent();	
			int currentWidth = rect.width;
			int currentHeight = rect.height;
			int itemWidth = this.polishItem.getItemWidth(this.layoutWidth, this.layoutWidth, this.layoutHeight);
			int itemHeight = this.polishItem.itemHeight;
			if (itemWidth != currentWidth || itemHeight != currentHeight) {
				updateLayout();
			} else {
				invalidate();
			}
		}
	}


	public void animate(long currentTime, ClippingRegion repaintRegion) {
		// typically can be ignored?
	}

}
