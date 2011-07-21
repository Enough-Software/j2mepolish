//#condition polish.usePolishGui
/*
 * Created on Oct 27, 2010 at 4:52:27 PM.
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

/**
 * <p>Allows to embed arbitrary items within an ChoiceItem</p>
 * <p>Use <code>UiAccess.append(ChoiceGroup, ChoiceItem)</code> or <code>ChoiceGroup.append( ChoiceItem)</code> for adding an ChoiceItem
 *    to a ChoiceGroup.
 * </p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ItemChoiceItem extends ChoiceItem {

	private Item item;
	private Style itemOriginalStyle;

	/**
	 * Creates a new ChoiceItem that includes a different item.
	 * 
	 * @param item the item that should be embedded into the ChoiceItem
	 * @param choiceType the type of the parent ChoiceGroup, e.g. Choice.EXCLUSIVE
	 */
	public ItemChoiceItem(Item item, int choiceType) {
		this( item, choiceType, null);
	}

	/**
	 * Creates a new ChoiceItem that includes a different item.
	 * 
	 * @param item the item that should be embedded into the ChoiceItem
	 * @param choiceType the type of the parent ChoiceGroup, e.g. Choice.EXCLUSIVE
	 * @param style the style for this item
	 */
	public ItemChoiceItem(Item item, int choiceType, Style style) {
		super(null, null, choiceType, style);
		this.item = item;
		item.parent = this;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ChoiceItem#initContentImpl(int, int, int)
	 */
	protected void initContentImpl(int firstLineWidth, int availWidth, int availHeight) {
		if (!this.item.isStyleInitialised && this.item.style != null) {
			this.item.setStyle( this.item.style );
			this.item.isStyleInitialised = true;
		}
		this.item.initContent(firstLineWidth, availWidth, availHeight);
		this.contentWidth = this.item.getContentWidth();
		this.contentHeight = this.item.getContentHeight();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ChoiceItem#paintContentImpl(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContentImpl(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		this.item.paintContent(x, y, leftBorder, rightBorder, g);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ChoiceItem#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		super.defocus(originalStyle);
		this.item.defocus(this.itemOriginalStyle);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ChoiceItem#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style newStyle, int direction) {
		this.itemOriginalStyle = this.item.focus(null, direction);
		return super.focus(newStyle, direction);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ChoiceItem#notifyItemPressedEnd()
	 */
	public void notifyItemPressedEnd() {
		this.item.notifyItemPressedEnd();
		super.notifyItemPressedEnd();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#notifyItemPressedStart()
	 */
	public boolean notifyItemPressedStart() {
		this.item.notifyItemPressedStart();
		return super.notifyItemPressedStart();
	}

//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.Item#setStyle()
//	 */
//	public void setStyle(Style style, boolean resetStyle) {
//		this.item.setStyle(style);
//		super.setStyle(style, resetStyle);
//	}

	
	
	
}
