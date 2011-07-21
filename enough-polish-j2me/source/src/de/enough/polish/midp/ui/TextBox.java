//#condition polish.usePolishGui && polish.midp
/*
 * Created on Aug 12, 2008 at 12:32:55 AM.
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
package de.enough.polish.midp.ui;

import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Ticker;

/**
 * <p>Embeds a native javax.microedition.lcdui.TextBox </p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TextBox
extends javax.microedition.lcdui.TextBox
implements Displayable
{
	
	private Ticker polishTicker;

	/**
	 * Creates a new <code>TextBox</code> object with the given title
	 * string, initial
	 * contents, maximum size in characters, and constraints.
	 * If the text parameter is <code>null</code>, the
	 * <code>TextBox</code> is created empty.
	 * The <code>maxSize</code> parameter must be greater than zero.
	 * An <code>IllegalArgumentException</code> is thrown if the
	 * length of the initial contents string exceeds <code>maxSize</code>.
	 * However,
	 * the implementation may assign a maximum size smaller than the
	 * application had requested.  If this occurs, and if the length of the
	 * contents exceeds the newly assigned maximum size, the contents are
	 * truncated from the end in order to fit, and no exception is thrown.
	 * 
	 * @param title the title text to be shown with the display
	 * @param text the initial contents of the text editing area, null may be used to indicate no initial content
	 * @param maxSize the maximum capacity in characters. The implementation may limit boundary maximum capacity and the actually assigned capacity may me smaller than requested. A defensive application will test the actually given capacity with getMaxSize().
	 * @param constraints see input constraints
	 * @throws IllegalArgumentException if maxSize is zero or less
	 * 				or if the constraints parameter is invalid
	 * 				or if text is illegal for the specified constraints
	 * 				or if the length of the string exceeds the requested maximum capacity
	 */
	public TextBox(String title, String text, int maxSize, int constraints)
	{
		super(title, text, maxSize, constraints );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Displayable#addCommand(de.enough.polish.ui.Command)
	 */
	public void addCommand(Command cmd)
	{
		super.addCommand(cmd);
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Displayable#removeCommand(de.enough.polish.ui.Command)
	 */
	public void removeCommand(Command cmd)
	{
		super.removeCommand( cmd );
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Displayable#setCommandListener(de.enough.polish.ui.CommandListener)
	 */
	public void setCommandListener(CommandListener l)
	{
		super.setCommandListener( new NativeCommandListener(l, this) );

	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Displayable#getTicker()
	 */
	public Ticker getPolishTicker()
	{
		return this.polishTicker;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Displayable#setTicker(de.enough.polish.ui.Ticker)
	 */
	public void setTicker(Ticker ticker)
	{
		this.polishTicker = ticker;
		javax.microedition.lcdui.Ticker nativeTicker = new javax.microedition.lcdui.Ticker( ticker.getText() );
		super.setTicker(nativeTicker);
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Displayable#sizeChanged(int, int)
	 */
	public void sizeChanged(int w, int h)
	{
		// ignore

	}

	//#if polish.midp1
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#getHeight()
	 */
	public int getHeight()
	{
		return 0;
	}
	//#endif

	//#if polish.midp1
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#getWidth()
	 */
	public int getWidth()
	{
		return 0;
	}
	//#endif

}
