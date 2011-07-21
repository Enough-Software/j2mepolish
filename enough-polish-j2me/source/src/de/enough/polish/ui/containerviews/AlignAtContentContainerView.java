//#condition polish.usePolishGui
/*
 * Created on Dec 8, 2008 at 7:38:29 AM.
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
package de.enough.polish.ui.containerviews;

import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Style;

/**
 * <p>Aligns elements so that their contents start at the same position.</p>
 * <p>Usage:
 * </p>
 * <pre>
 * .myList {
 * 		view-type: align-at-content;
 * 		align-content-x: 30%; (optional)
 * }
 * </pre>
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AlignAtContentContainerView extends ContainerView
{
	
	Dimension contentX;

	/**
	 * Creates a new view type
	 */
	public AlignAtContentContainerView()
	{
		// use style settings for configuration
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Item, int, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth, int availWidth, int availHeight)
	{
		super.initContent(parentContainerItem, firstLineWidth, availWidth, availHeight);
		// now just adjust relativeX positions, so that the contents start at the same horizontal position:
		Item[] items = this.parentContainer.getItems();
		int startX = 0;
		if (this.contentX != null) {
			startX = this.contentX.getValue(availWidth);
		} else {
			// content should be adjusted dynamically, so we first need to find
			// out which label is the largest:
			int maxLabelWidth = 0;
			for (int i = 0; i < items.length; i++)
			{
				Item item = items[i];
				Item label = item.getLabelItem();
				if (label != null && label.itemWidth > maxLabelWidth) {
					maxLabelWidth = label.itemWidth;
				}
			}
			startX = maxLabelWidth;
		}
		for (int i = 0; i < items.length; i++)
		{
			Item item = items[i];
			Item label = item.getLabelItem();
			if (label == null) {
				item.relativeX = startX;
			} else {
				item.relativeX = startX - label.itemWidth;
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style)
	{
		super.setStyle(style);
		//#if polish.css.align-content-x
			this.contentX = (Dimension) style.getObjectProperty("align-content-x");
		//#endif
	}
	
	

}
