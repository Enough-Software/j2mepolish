//#condition polish.usePolishGui && polish.blackberry
/*
 * Copyright (c) 2011 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.blackberry.ui;

import javax.microedition.lcdui.Graphics;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.XYRect;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;

/**
 * Allows to embed arbitrary native BlackBerry fields within a J2ME Polish based UI.
 * @author Robert Virkus, j2mepolish@enough.de
 *
 */
public class FieldItem extends Item {

	/**
	 * Creates a new field item
	 * @param field the native BlackBerry field
	 */
	public FieldItem(Field field) {
		this( field, null);
	}

	/**
	 * Creates a new field item
	 * @param field the native BlackBerry field
	 * @param style the J2ME Polish style
	 */
	public FieldItem(Field field, Style style) {
		super(style);
		this._bbField = field;
		
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		BaseScreenManager.getInstance().layoutAndPosition(this, availWidth, availHeight);
		XYRect extent = this._bbField.getExtent();
		this.contentWidth = extent.width;
		this.contentHeight = extent.height;
		if (this._bbField.isFocusable()) {
			this.appearanceMode = INTERACTIVE;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#showNotify()
	 */
	protected void showNotify() {
		super.showNotify();
		BaseScreenManager.getInstance().addPermanentNativeItem(this);
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#hideNotify()
	 */
	protected void hideNotify() {
		super.hideNotify();
		BaseScreenManager.getInstance().removePermanentNativeItem(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style newStyle, int direction) {
		Style plainStyle = super.focus(newStyle, direction);
		//#if polish.JavaPlatform < BlackBerry/5.0
			this._bbField.setFocus();
		//#else
			BaseScreenManager.getInstance().setFieldWithFocus(this._bbField);
		//#endif
		return plainStyle;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		// TODO Auto-generated method stub
		super.defocus(originalStyle);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		// no need to paint anything here, this is done natively
		BaseScreenManager.getInstance().position(this, x, y);

	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector() {
		return this._bbField.getClass().getName();
	}

}
