//#condition polish.usePolishGui
/*
 * Created on 14-Mar-2004 at 21:31:51.
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
package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.HashMap;

/**
 * <p>Paints text in a background.</p>
 * <p>Following CSS parameters are supported:
 * <ul>
 * 		<li><b>text</b>: the text, e.g. test</li>
 * 		<li><b>text-style</b>: the optional style text that is used for designing the text</li>
 * 		<li><b>color</b>: the background coloror "transparent".</li>
 * 		<li><b>anchor</b>: The anchor of the image, either  "left", "right", 
 * 			"center" (="horizontal-center"), "vertical-center", "top" or "bottom" 
 * 			or any combinationof these values. Defaults to "horizontal-center | vertical-center".
 * 		</li>
 * 		<li><b>x-offset</b>: The number of pixels to move the image horizontally, negative values move it to the left.</li>
 * 		<li><b>y-offset</b>: The number of pixels to move the image vertically, negative values move it to the top.</li>
 * 		<li><b></b>: </li>
 * </ul>
 * </p>
 *
 * <p>Copyright Enough Software 2004 - 2009</p>

 * <pre>
 * history
 *        14-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class TextBackground 
extends Background
{
	
	private static HashMap localizations;
	
	private int color;
	private final int anchor;
	private int xOffset;
	private int yOffset;
	private final transient StringItem item;
	private final String textStyleName;


	/**
	 * Creates a new image background.
	 * @param text the text 
	 * @param textStyleName  name of style of the text
	 * @param color the background color or Item.TRANSPARENT
	 * @param anchor the anchor of the image, either  "left", "right", 
	 * 			"center" (="horizontal-center"), "vertical-center", "top" or "bottom" 
	 * 			or any combinationof these values. Defaults to "horizontal-center | vertical-center"
	 * @param xOffset The number of pixels to move the image horizontally, negative values move it to the left.
	 * @param yOffset The number of pixels to move the image vertically, negative values move it to the top.
	 */
	public TextBackground( String text, String textStyleName, int color, int anchor, int xOffset, int yOffset ) {
		this.textStyleName = textStyleName;
		this.color = color;
		this.anchor = anchor;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.item = new StringItem( null, text );
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (this.color != Item.TRANSPARENT) {
			g.setColor( this.color );
			g.fillRect( x, y, width, height );
		}
		x += this.xOffset;
		y += this.yOffset;
		if ( (this.anchor & Graphics.VCENTER) == Graphics.VCENTER) {
			y += (height - this.item.getItemHeight(width, width, height)/ 2);
		} else if ( (this.anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
			y += height - this.item.getItemHeight(width, width, height);
		}
		this.item.paint( x, y, x, x + width, g );
	}
	

	
	/**
	 * Releases all (memory intensive) resources such as images or RGB arrays of this background.
	 */
	public void releaseResources() {
		this.item.releaseResources();
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#hideNotify()
	 */
	public void hideNotify()
	{
		UiAccess.hideNotify(this.item);
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#showNotify()
	 */
	public void showNotify()
	{
		UiAccess.showNotify(this.item);
		if (this.textStyleName != null) {
			Style style = StyleSheet.getStyle( this.textStyleName );
			if (style != null) {
				this.item.setStyle(style);
			}
		}
		if (localizations != null) {
			String originalText = (String) this.item.getAttribute("original");
			if (originalText == null) {
				originalText = this.item.getText();
				this.item.setAttribute("original", originalText);
			}
			if (originalText != null) {
				String translation = (String) localizations.get( originalText );
				if (translation != null && !translation.equals(originalText)) {
					this.item.setText(translation);
				}
			}
		}
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#animate(de.enough.polish.ui.Screen, de.enough.polish.ui.Item, long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(Screen screen, Item parent, long currentTime,
			ClippingRegion repaintRegion)
	{
		
		this.item.animate(currentTime, repaintRegion);
	}
	
	//#if polish.css.animations
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
		 */
		public void setStyle(Style style)
		{
			//#if polish.css.background-text-color
				Color col = style.getColorProperty("background-text-color");
				if (col != null) {
					this.color = col.getColor();
				}
			//#endif
			//#if polish.css.background-text-x-offset
				Integer xOffsetInt = style.getIntProperty("background-text-x-offset");
				if (xOffsetInt != null) {
					this.xOffset = xOffsetInt.intValue();
				}
			//#endif
			//#if polish.css.background-text-y-offset
				Integer yOffsetInt = style.getIntProperty("background-text-y-offset");
				if (yOffsetInt != null) {
					this.yOffset = yOffsetInt.intValue();
				}
			//#endif
				
		}
	//#endif


	/**
	 * Specifies a translations. Use this for localizing a TextBackground.
	 * @param original the original text
	 * @param translation the translation
	 */
	public static void setLocalization( String original, String translation ) {
		if (localizations == null) {
			localizations = new HashMap();
		}
		localizations.put(original, translation);
	}

}
