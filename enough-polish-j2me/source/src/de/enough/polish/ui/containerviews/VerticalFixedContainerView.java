//#condition polish.usePolishGui
/*
 * Created on Jul 10, 2007 at 2:12:11 PM.
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

import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;

/**
 * <p>Keeps the currently focused item in a fixed position (either top, center or bottom) within a list of elements.</p>
 * <p>
 * Use this view with the "verticalfixed" view-type:
 * <pre>
 * .myList {
 * 		layout: center;
 * 		view-type: verticalfixed;
 * 		verticalfixedview-align: center;  (allowed values: top, center, bottom )
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Jul 10, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class VerticalFixedContainerView extends ContainerView {
	
	/**
	 * Creates a vertical container view with the focused item always centered
	 * in the middle of the screen. 
	 */
	public VerticalFixedContainerView() {
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Item, int, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth, int availWidth, int availHeight)
	{
		if (this.focusedItem != null) {
			Container container = (Container) parentContainerItem;
			int availableVerticalSpace = container.getScrollHeight();
			int focusedY = this.focusedItem.relativeY + container.getScrollYOffset();
			int focusedHeight = this.focusedItem.itemHeight;
			int targetY = (availableVerticalSpace - focusedHeight) >> 1;

			if (availableVerticalSpace != -1 && this.focusedItem.relativeY > targetY 
				&& (this.focusedItem.relativeY + ( focusedHeight >> 1)) < (this.contentHeight - targetY) ) 
			{
				container.setScrollYOffset(container.getScrollYOffset() + targetY - focusedY);
			}
		}

		super.initContent(parentContainerItem, firstLineWidth, availWidth, availHeight);
	}
}
