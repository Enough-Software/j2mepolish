/*
 * Created on May 23, 2008 at 4:52:47 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package com.grimo.me.product.midpsysinfo;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.sysinfo.MIDPSysInfoMIDlet;


/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class EventViewer extends Canvas
{
	
	
	private static final int MESSAGES_LENGTH = 30;
	private final String[] messages;
	private final MIDPSysInfoMIDlet sysInfoMIDlet;
	
	public EventViewer(MIDPSysInfoMIDlet sysInfoMIDlet) {
		this.sysInfoMIDlet = sysInfoMIDlet;
		this.messages = new String[ MESSAGES_LENGTH ];
		this.messages[0] = "Press 0 to return";
		try {
			FullScreenSetter setter = (FullScreenSetter) Class.forName("com.grimo.me.product.midpsysinfo.Midp2FullScreenSetter").newInstance();
			setter.setFullScreen( this );
		} catch (Exception e) {
			// ignore
		}
	}
 	

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyPressed(int)
	 */
	protected void keyPressed(int keyCode)
	{
		try {
			int gameAction = getGameAction( keyCode );
			if (gameAction != 0) {
				addMessage( "keyPressed(" + keyCode + "),ga=" + gameAction + "=" + getKeyCode( gameAction) + "=" + getKeyName(keyCode));	
			} else {
				addMessage( "keyPressed( " + keyCode + " ) =" + getKeyName(keyCode));	
			}
		} catch (IllegalArgumentException e) {			
			addMessage( "keyPressed( " + keyCode + " ) =" + getKeyName(keyCode));	
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyReleased(int)
	 */
	protected void keyReleased(int keyCode)
	{
		addMessage( "keyReleased( " + keyCode + " )");
		if (keyCode == KEY_NUM0) {
			this.sysInfoMIDlet.showMainMenu();
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyRepeated(int)
	 */
	protected void keyRepeated(int keyCode)
	{
		addMessage( "keyRepeated( " + keyCode + " )");
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#pointerDragged(int, int)
	 */
	protected void pointerDragged(int x, int y)
	{
		addMessage("pointerDragged( " + x + ", " + y + " )");
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#pointerPressed(int, int)
	 */
	protected void pointerPressed(int x, int y)
	{
		addMessage("pointerPressed( " + x + ", " + y + " )");
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#pointerReleased(int, int)
	 */
	protected void pointerReleased(int x, int y)
	{
		addMessage("pointerReleased( " + x + ", " + y + " )");
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#showNotify()
	 */
	protected void showNotify()
	{
		addMessage( "showNotify");
		try {
			FullScreenSetter setter = (FullScreenSetter) Class.forName("com.grimo.me.product.midpsysinfo.Midp2FullScreenSetter").newInstance();
			setter.setFullScreen( this );
		} catch (Exception e) {
			// ignore
		}
		addMessage("w=" + getWidth() + ", h=" + getHeight() + " in showNotify");
	}
	
	

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#hideNotify()
	 */
	protected void hideNotify()
	{
		addMessage( "hideNotify");
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#sizeChanged(int, int)
	 */
	protected void sizeChanged(int w, int h)
	{
		addMessage( "sizeChanged( " + w + ", " + h  + " )");
	}
	
	public void addMessage( String message ) {
		System.arraycopy( this.messages, 0, this.messages, 1, MESSAGES_LENGTH -1 );
		this.messages[0] = message;
		repaint();
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	protected void paint(Graphics g)
	{
		g.setColor( 0xffffff );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		g.setColor( 0 );
		Font font = Font.getDefaultFont();
		g.setFont(font);
		int lineHeight = font.getHeight() + 2;
		int y = lineHeight + 3;
		int x = 5;
		for (int i=0; i<MESSAGES_LENGTH; i++) {
			String message = this.messages[i];
			if (message == null) {
				break;
			}
			g.drawString( message, x, y, Graphics.LEFT | Graphics.BOTTOM );
			y += lineHeight;
		}
	}

}
