//#condition polish.usePolishGui
/*
 * Created on 29-Jan-2006 at 18:32:09.
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

import javax.microedition.lcdui.Image;

/**
 * Allows to use an image along with text within one icon.
 * <p><b>Warning:</b> the item will be transformed into a J2ME Polish internal item in the preprocessing
 *      phase. This does not limit any of the features, but the construct <code>if (item instanceof CustomItem)</code> 
 *      will always return false if it is an IconCustomItem. Use <code>if (item instanceof IconCustomItem)</code> instead.
 *      <br />
 *      This is why this class is final, no subclassing is suported.
 * </p>
 * @author robertvirkus
 *
 */
public final class IconCustomItem 
//#if polish.LibraryBuild
	extends FakeIconCustomItem 
//#else
	//# extends IconItem 
//#endif
{

	/**
	 * Creates a new icon.
	 * 
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 */
	public IconCustomItem( String text, Image image ) {
		super( text, image );
	}
	
	/**
	 * Creates a new icon.
	 * 
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 * @param style the style of this item
	 */
	public IconCustomItem( String text, Image image, Style style) {
		this( null, text, image, style );
	}
	
	/**
	 * Creates a new icon.
	 * 
	 * @param label the label of this item
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 */
	public IconCustomItem( String label, String text, Image image) {
		super( label, text, image, null );
	}

	/**
	 * Creates a new icon.
	 * 
	 * @param label the label of this item
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 * @param style the style of this item
	 */
	public IconCustomItem( String label, String text, Image image, Style style) {
		super(label, text, image, style);
	}
}
