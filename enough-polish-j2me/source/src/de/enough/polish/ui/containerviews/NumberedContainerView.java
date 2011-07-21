//#condition polish.usePolishGui
/*
 * Created on Jun 27, 2007 at 12:32:08 AM.
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
 * <p>Sets a sequential number for each embedded item-label.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Jun 27, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class NumberedContainerView extends ContainerView {

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth, int availWidth, int availHeight) {
		Container container = (Container) parentContainerItem;
		int maxNumber = Math.min( container.size(), 9 );
		for (int i = 0; i < maxNumber; i++) {
			Item item = container.get(i);
			item.setLabel( Integer.toString( i + 1 ) );
		}
		super.initContent(parentContainerItem, availWidth, availWidth, availHeight);
	}

}

