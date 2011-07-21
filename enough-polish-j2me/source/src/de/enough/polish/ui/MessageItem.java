//#condition polish.usePolishGui

/*
 * Created on 18-Jul-2005 at 09:20:00.
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
import javax.microedition.lcdui.Image;

public class MessageItem 
//#if !polish.LibraryBuild
	//# extends CustomItem
//#else
	extends javax.microedition.lcdui.CustomItem
//#endif
{
	
	private final IconItem headlineItem;
	private final StringItem textItem;
	

	/**
	 * Creates a new message item
	 * 
	 * @param headline the headline text
	 * @param text the content text
	 */
	public MessageItem(String headline, String text ) {
		this( headline, text, null );
	}

	/**
	 * Creates a new message item
	 * 
	 * @param headline the headline text
	 * @param text the content text
	 * @param style the CSS style
	 */
	public MessageItem(String headline, String text, Style style ) {
		//#if polish.LibraryBuild
			super( null );
		//#else
			//# super( null, style );
		//#endif
		//#style messageheadline, default
		this.headlineItem = new IconItem( headline, null );
		//#style messagetext, default
		this.textItem = new StringItem( null, text);
	}


	protected int getMinContentWidth() {
		return 100;
	}

	protected int getMinContentHeight() {
		return 30;
	}

	protected int getPrefContentWidth(int height) {
		//#if polish.ScreenWidth:defined
			//#= int width = ${polish.ScreenWidth};
		//#else
			int width = 200;
		//#endif
		this.headlineItem.background = null;
		this.headlineItem.border = null;
		int headlineWidth = this.headlineItem.getItemWidth(width, width, height);
		this.textItem.background = null;
		this.textItem.border = null;
		int textWidth = this.textItem.getItemWidth(width, width, height);
		return Math.max(headlineWidth, textWidth);
	}

	protected int getPrefContentHeight(int width) {
		this.headlineItem.background = null;
		this.headlineItem.border = null;
		int availHeight = 200;
		int height = this.headlineItem.getItemHeight(width, width, availHeight);
		this.textItem.background = null;
		this.textItem.border = null;
		height += this.textItem.getItemHeight(width, width, availHeight);
		//#if !polish.LibraryBuild
			//# height += this.paddingVertical;
		//#endif
		return height;
	}
	
	/**
	 * Sets the headline text
	 * @param headline the new headline
	 */
	public void setHeadline( String headline ) {
		setHeadline( headline, null );
	}
	
	/**
	 * Sets the headline text
	 * @param headline the new headline
	 * @param style the new style for the headline
	 */
	public void setHeadline( String headline, Style style ) {
		this.headlineItem.setText( headline );
		if ( style != null ) {
			this.headlineItem.setStyle( style );
		}
	}
	
	/**
	 * Sets the text of the message.
	 * @param text the new text
	 */
	public void setText( String text ) {
		setText( text, null );
	}
	
	/**
	 * Sets the text of the message.
	 * @param text the new text
	 * @param style the new style of the text
	 */
	public void setText( String text, Style style ) {
		this.textItem.setText( text );
		if ( style != null ) {
			this.textItem.setStyle( style );
		}
	}
	
	/**
	 * Sets the image of the headline
	 * @param image the new image of the headline
	 */
	public void setImage( Image image ) {
		this.headlineItem.setImage(image);
	}


	protected void paint(Graphics g, int w, int h) {
		this.headlineItem.paint( 0, 0, 0, w,  g );
		//#if !polish.LibraryBuild
			//# int y = this.headlineItem.itemHeight + this.paddingVertical;
		//#else
			int y = this.headlineItem.itemHeight;
		//#endif
		this.textItem.paint( 0, y, 0, w, g );

	}

//	public void setStyle(Style style) {
//		super.setStyle(style);
//		this.headlineItem.setStyle( style );
//		this.headlineItem.background = null;
//		this.headlineItem.border = null;
//		this.textItem.setStyle( style );
//		this.textItem.background = null;
//		this.textItem.border = null;
//	}
	
	

}
