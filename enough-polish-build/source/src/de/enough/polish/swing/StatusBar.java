/*
 * Created on 12-Feb-2005 at 18:25:43.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.swing;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * <p>Provides a simple way for showing status messages.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        12-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class StatusBar extends JLabel {

	private static final long serialVersionUID = 2748506651711151711L;

	private Color messageBackground;
	private Color messageForeground;
	private Color warnBackground;
	private Color warnForeground;
	private Icon messageIcon;
	private Icon warnIcon;

	/**
	 * Creates a new empty status bar.
	 */
	public StatusBar() {
		super();
		this.messageBackground = getBackground();
		this.messageForeground = Color.BLACK;
		this.warnBackground = Color.RED;
		this.warnForeground = Color.BLACK;
		setOpaque(true);
	}

	/**
	 * Shows a simple message.
	 *  
	 * @param text the message
	 */
	public void message( String text ) {
		setBackground( this.messageBackground );
		setForeground(this.messageForeground);
		setIcon( this.messageIcon );
		setText( text );
	}
	
	/**
	 * Shows warning message.
	 *  
	 * @param text the message
	 */
	public void warn( String text ) {
		setBackground( this.warnBackground );
		setForeground( this.warnForeground );
		setIcon( this.warnIcon );
		setText( text );
	}


	
	public Color getMessageBackground() {
		return this.messageBackground;
	}
	public void setMessageBackground(Color messageBackground) {
		this.messageBackground = messageBackground;
	}
	public Color getMessageForeground() {
		return this.messageForeground;
	}
	public void setMessageForeground(Color messageForeground) {
		this.messageForeground = messageForeground;
	}
	public Icon getMessageIcon() {
		return this.messageIcon;
	}
	public void setMessageIcon(Icon messageIcon) {
		this.messageIcon = messageIcon;
	}
	public Color getWarnBackground() {
		return this.warnBackground;
	}
	public void setWarnBackground(Color warnBackground) {
		this.warnBackground = warnBackground;
	}
	public Color getWarnForeground() {
		return this.warnForeground;
	}
	public void setWarnForeground(Color warnForeground) {
		this.warnForeground = warnForeground;
	}
	public Icon getWarnIcon() {
		return this.warnIcon;
	}
	public void setWarnIcon(Icon warnIcon) {
		this.warnIcon = warnIcon;
	}
}
