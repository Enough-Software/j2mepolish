//#condition polish.usePolishGui && polish.midp && !polish.blackberry
/*
 * Created on Aug 12, 2008 at 8:54:23 PM.
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

import javax.microedition.midlet.MIDlet;

import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.NativeDisplay;

/**
 * <p>Wraps a MIDP Display</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class NativeDisplayImpl implements NativeDisplay
{
	
	javax.microedition.lcdui.Display display;
	
	protected NativeDisplayImpl( MIDlet midlet ) {
		this.display = javax.microedition.lcdui.Display.getDisplay(midlet); 
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#callSerially(java.lang.Runnable)
	 */
	public void callSerially(Runnable r)
	{
		this.display.callSerially(r);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#flashBacklight(int)
	 */
	public boolean flashBacklight(int duration)
	{
		//#if polish.midp1
			//# return false;
		//#else
			return this.display.flashBacklight(duration);
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#getBestImageHeight(int)
	 */
	public int getBestImageHeight(int imageType)
	{
		//#if polish.midp1
			//# return 0;
		//#else
			return this.display.getBestImageHeight(imageType);
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#getBestImageWidth(int)
	 */
	public int getBestImageWidth(int imageType)
	{
		//#if polish.midp1
			//# return 0;
		//#else
			return this.display.getBestImageWidth(imageType);
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#getBorderStyle(boolean)
	 */
	public int getBorderStyle(boolean highlighted)
	{
		//#if polish.midp1
			//# return 0;
		//#else
			return this.display.getBorderStyle(highlighted);
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#getColor(int)
	 */
	public int getColor(int colorSpecifier)
	{
		//#if polish.midp1
			//# return 0;
		//#else
			return this.display.getColor(colorSpecifier);
		//#endif
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#isColor()
	 */
	public boolean isColor()
	{
		return this.display.isColor();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#numAlphaLevels()
	 */
	public int numAlphaLevels()
	{
		//#if polish.midp1
			//# return 0;
		//#else
			return this.display.numAlphaLevels();
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#numColors()
	 */
	public int numColors()
	{
		return this.display.numColors();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#setCurrent(javax.microedition.lcdui.Display)
	 */
	public void setCurrent(Display nextDisplayable)
	{
		this.display.setCurrent(nextDisplayable);
	}
	
	public void setCurrent( de.enough.polish.ui.Displayable displayable ) {
		javax.microedition.lcdui.Displayable dis = (javax.microedition.lcdui.Displayable) displayable;
		this.display.setCurrent(dis);
	}

	public void setCurrentNative( javax.microedition.lcdui.Displayable dis ) {
		this.display.setCurrent(dis);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#vibrate(int)
	 */
	public boolean vibrate(int duration)
	{
		//#if polish.midp1
			//# return false;
		//#else
			return this.display.vibrate(duration);
		//#endif
	}


	/**
	 * Creates a new native display
	 * @param m the midlet
	 * @return the new native displa
	 */
	public static NativeDisplayImpl getDisplay(MIDlet m)
	{
		return new NativeDisplayImpl(m);
	}
	
	/**
	 * Retrieves the original MIDP based display
	 * @return the original display
	 */
	public javax.microedition.lcdui.Display getDisplay() {
		return this.display;
	}


	public boolean notifyDisplayableChange(Displayable currentDisplayable, Displayable nextDisplayable) {
		//ignore
		return false;
	}

}
