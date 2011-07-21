/*
 * Created on 09-Mar-2004 at 21:39:34.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.preprocess.backgrounds;

import java.util.HashMap;
import java.util.Map;

import de.enough.polish.BuildException;
import de.enough.polish.preprocess.css.BackgroundConverter;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.preprocess.css.StyleSheet;

/**
 * <p>Creates the Image- or the BorderedImageBackground.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        09-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ImageBackgroundConverter extends BackgroundConverter {
	
	private static final HashMap REPEAT_TYPES = new HashMap();
	static {
		REPEAT_TYPES.put("repeat", BACKGROUNDS_PACKAGE + "TiledImageBackground.REPEAT");
		REPEAT_TYPES.put("no-repeat", "");
		REPEAT_TYPES.put("none", "");
		REPEAT_TYPES.put("repeat-x", BACKGROUNDS_PACKAGE + "TiledImageBackground.REPEAT_X");
		REPEAT_TYPES.put("repeat-y", BACKGROUNDS_PACKAGE + "TiledImageBackground.REPEAT_Y");
		REPEAT_TYPES.put("repeat-horizontal", BACKGROUNDS_PACKAGE + "TiledImageBackground.REPEAT_X");
		REPEAT_TYPES.put("repeat-vertical", BACKGROUNDS_PACKAGE + "TiledImageBackground.REPEAT_Y");
	}
	
	/**
	 * Creates a new creator
	 */
	public ImageBackgroundConverter() {
		super();
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BackgroundConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(Map background, Style style, StyleSheet styleSheet) throws BuildException {
		//TODO rob also allow other CSS settings:
		// background-attachment,
		// background-position ???,
		String imageUrl = (String) background.get("image");
		if (imageUrl== null) {
			imageUrl = (String) background.get("background-image");
			if (imageUrl== null) {
				if (style != null) {
					throw new BuildException("Invalid CSS: the every image background needs to define the \"image\" CSS attribute - check the " + style.getSelector() + " style.");
				} else {
					throw new BuildException("Invalid CSS: the every image background needs to define the \"image\" CSS attribute.");
				}
			}
		}
		imageUrl = getUrl( imageUrl );
		String repeat = (String) background.get("repeat");
		int paddingVertical = 0;
		int paddingHorizontal = 0;
		boolean overlap = false;
		if (repeat != null) {
			String rep = (String) REPEAT_TYPES.get( repeat );
			if (rep == null) {
				throw new BuildException("Invalid CSS: the repeat-type [" + repeat +"] is not supported by the image background.");
			}
			if (rep.length() > 1) {
				repeat = rep;
			} else {
				repeat = null;
			}
			int padding = 0;
			String paddingStr = (String) background.get("padding");
			if (paddingStr != null) {
				padding = parseInt( "padding", paddingStr );
			}
			paddingVertical = padding;
			paddingStr = (String) background.get("padding-vertical");
			if (paddingStr != null) {
				paddingVertical = parseInt( "padding-vertical", paddingStr );
			}
			paddingHorizontal = padding;
			paddingStr = (String) background.get("padding-horizontal");
			if (paddingStr != null) {
				paddingHorizontal = parseInt( "padding-horizontal", paddingStr );
			}
			String overlapStr = (String) background.get("overlap");
			if (overlapStr != null) {
				overlap = parseBoolean( "overlap", overlapStr );
			}
		}
		String anchor = (String) background.get("anchor");
		if (anchor != null) {
			anchor = parseAnchor( "anchor", anchor );
		} else {
			anchor = "Graphics.HCENTER | Graphics.VCENTER";
		}
		String xOffsetStr = (String) background.get("x-offset");
		if (xOffsetStr == null) {
			xOffsetStr = "0";
		} else {
			parseInt("x-offset", xOffsetStr);
		}
		String yOffsetStr = (String) background.get("y-offset");
		if (yOffsetStr == null) {
			yOffsetStr = "0";
		} else {
			parseInt("y-offset", yOffsetStr);
		}
		if (repeat == null) {
			// return default image background:
			return "new " + BACKGROUNDS_PACKAGE + "ImageBackground( " 
			+ this.color + ", \"" + imageUrl + "\", " + anchor + ", " + xOffsetStr + ", " + yOffsetStr + " )";
		} else {
			return "new " + BACKGROUNDS_PACKAGE + "TiledImageBackground( " 
			+ this.color + ", \"" + imageUrl + "\", " + repeat + ", " + anchor + ", " + paddingHorizontal + ", " + paddingVertical + ", " + overlap  + ", " + xOffsetStr + ", " + yOffsetStr + " )";
		}
	}

}
