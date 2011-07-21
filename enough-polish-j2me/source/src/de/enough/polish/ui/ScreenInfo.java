//#condition polish.usePolishGui

/*
 * Created on 07-Jun-2005 at 16:20:01.
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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * <p>Can be used for displaying information on each screen. 
 * This needs to be enabled specifically by setting the 
 * "polish.ScreenInfo.enable" preprocessing variable to "true":
 * <pre>
 * 	&lt;variable name=&quot;polish.ScreenInfo.enable&quot; value=&quot;true&quot; /&gt;
 * </pre>
 * </p>
 * <p>
 *  If you want to display text you can use the "screeninfo" style
 *  for designing this element.
 *  Alternatively you can use the setImage( Image, Style ) or setText( String, Style )
 *  methods for changing the style during runtime like this:
 * <pre>
 * if (this.isOnline) {
 *    //#style online
 *    ScreenInfo.setText("on");
 * } else {
 *    //#style offline
 *    ScreenInfo.setText("off");
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2010</p>
 * <pre>
 * history
 *        07-Jun-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ScreenInfo {
	
	public static Item item;
	private static boolean visible = true;
	private static int itemY;
	private static int itemX;
	private static boolean positionSet;
	
	static {
		//#style screeninfo, default
		item = new IconItem( null, null );
	}

	/**
	 * No instantiation allowed 
	 */
	private ScreenInfo() {
		super();
	}
	
	/**
	 * Paddles the visibility of the ScreenInfo object.
	 * 
	 * @param isVisible true when this element should be shown. 
	 */
	public static void setVisible( boolean isVisible ) {
		if (isVisible) {
			item.showNotify();
		} else {
			item.hideNotify();
		}
		visible = isVisible;
		repaint();
	}
	
	/**
	 * Determines whether the ScreenInfo object should be painted.
	 * 
	 * @return true when the ScreenInfo item should be painted
	 */
	public static boolean isVisible() {
		return visible;
	}
	
	private static void repaint() {
		Display display = Display.getInstance();
		if (display != null) {
			display.repaint();
		}
	}
	
	/**
	 * Sets the text contents of the ScreenInfo. 
	 * 
	 * @param text the new text
	 */
	public static void setText( String text ) {
		//#debug
		System.out.println("ScreenInfo.setText " + text );
		((IconItem)item).setText( text );
		repaint();
	}
	
	/**
	 * Retrieves the text contents of the ScreenInfo element.
	 * 
	 * @return the current text of this screen info
	 */
	public static String getText()
	{
		return ((IconItem)item).getText();
	}
	

	/**
	 * Sets the text contents of the ScreenInfo along with a style. 
	 * 
	 * @param text the new text
	 * @param style the new style, is ignored when null
	 */
	public static void setText( String text, Style style ) {
		//#debug
		System.out.println("ScreenInfo.setText " + text );
		((IconItem)item).setText( text, style );
		repaint();
	}

	/**
	 * Sets the image for the screen info object.
	 * 
	 * @param image the image, when null is given, no image is painted.
	 */
	public static void setImage( Image image ) {
		((IconItem)item).setImage( image );
	}

	/**
	 * Sets the image for the screen info object.
	 * 
	 * @param image the image, when null is given, no image is painted.
	 * @param style the new style of this item, is ignored when null
	 */
	public static void setImage( Image image, Style style ) {
		((IconItem)item).setImage( image, style );
	}
	
	//#if polish.LibraryBuild
	/**
	 * Sets the item that is painted on the screen. Warning: read doc!
	 * You can replace the standard item (it's an IconItem) with this method, you can even set your own
	 * custom item. You need to rememember that afterexchanging the item with a non IconItem, you cannot
	 * call setText and setImage anymore.
	 * (This is a dummy signature that is not used, since it accepts the javax.microedition.lcdui classses, the 
	 * J2ME Polish build process will automatically convert this call to the correct one).
	 * 
	 * 
	 * @param newItem the item that is painted on the screen
	 */
	public static void setItem( javax.microedition.lcdui.Item newItem ) {
		// ignore
	}
	//#endif

	/**
	 * Sets the item that is painted on the screen. Warning: read doc!
	 * You can replace the standard item (it's an IconItem) with this method, you can even set your own
	 * custom item. You need to rememember that afterexchanging the item with a non IconItem, you cannot
	 * call setText and setImage anymore.
	 * 
	 * @param newItem the item that is painted on the screen
	 */
	public static void setItem( Item newItem ) {
		item = newItem;
		if (visible) {
			newItem.showNotify();
		}
		repaint();
	}

	/**
	 * Sets the item that is painted on the screen. Warning: read doc!
	 * You can replace the standard item (it's an IconItem) with this method, you can even set your own
	 * custom item. You need to rememember that afterexchanging the item with a non IconItem, you cannot
	 * call setText and setImage anymore.
	 * 
	 * @param newItem the item that is painted on the screen
	 * @param style the new style of this item, is ignored when null
	 */
	public static void setItem( Item newItem, Style style ) {
		if (style != null) {
			item.setStyle( style );
		}
		item = newItem;
		if (visible) {
			newItem.showNotify();
		}
		repaint();
	}

	/**
	 * Sets the position of this ScreenInfo - if not set it will be painted on the left side below the title.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public static void setPosition( int x, int y ) {
		itemX = x;
		itemY = y;
		item.relativeX = x;
		item.relativeY = y;
		positionSet = true;
		repaint();
	}
	
	/**
	 * Sets the screen which now uses the ScreenInfo element.
	 * 
	 * @param screen the new screen or null when an old screen deregisters itself (hideNotify())
	 */
	public static void setScreen( Screen screen ) {
		item.screen = screen;
	}
	
	/**
	 * Sets a specific background.
	 * Usually not necessary, when this element is designed using the "screeninfo" style
	 * or the setImage( Image, Style ) method or the setText( String, Style ) method.
	 * 
	 * @param background the new background.
	 */
	public static void setBackground( Background background ) {
		item.background = background;
		repaint();
	}
	
	/**
	 * Sets a specific text color.
	 * Usually not necessary, when this element is designed using the "screeninfo" style
	 * or the setImage( Image, Style ) method or the setText( String, Style ) method.
	 * 
	 * @param color the new font color.
	 */
	public static void setFontColor( int color ) {
		((IconItem)item).textColor = color;
		repaint();
	}

	/**
	 * Paints this element. This method is usually only called by the Screen implementation.
	 * 
	 * @param g the graphics object
	 * @param titleHeight the height of the screen's title
	 * @param screenWidth the width of the screen
	 */
	public static void paint( Graphics g, int titleHeight, int screenWidth ) {
		if (!visible) {
			return;
		}
		if ( positionSet ) {
			//#debug
			System.out.println("painting screeninfo " + item + " with set position at " + itemX + ", " + itemY );
			item.paint( itemX, itemY, itemX, screenWidth, g );
		} else {
			//#debug
			System.out.println("painting screeninfo " + item + " without set position at 0, 0" );
			item.paint( 0, 0, 0, screenWidth, g );
		}
	}


}
