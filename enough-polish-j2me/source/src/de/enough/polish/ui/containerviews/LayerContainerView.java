//#condition polish.usePolishGui
/*
 * Created on April 26, 2009 at 10:08:29 PM.
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
 */package de.enough.polish.ui.containerviews;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;

/**
 * <p>Aligns elements in different layers.</p>
 * <p>Usage:
 * </p>
 * <pre>
 * .myForm {
 * 		view-type: layer;
 * }
 * </pre>
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LayerContainerView extends ContainerView {

	/**
	 * Creates a new layer container view
	 */
	public LayerContainerView() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Item, int, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth,
			int availWidth, int availHeight) 
	{
		Container parent = (Container) parentContainerItem;		
		this.parentContainer = parent;
		Item[] myItems = parent.getItems();
		int maxWidth = 0;
		int maxHeight = 0;
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			int width = item.getItemWidth(firstLineWidth, availWidth, availHeight);
			int height = item.itemHeight;
			if (width > maxWidth) {
				maxWidth = width;
			}
			if (height > maxHeight) {
				maxHeight = height;
			}
			item.relativeX = 0;
			item.relativeY = 0;
		}
		this.contentWidth = maxWidth;
		this.contentHeight = maxHeight;
	}
	
	

}
