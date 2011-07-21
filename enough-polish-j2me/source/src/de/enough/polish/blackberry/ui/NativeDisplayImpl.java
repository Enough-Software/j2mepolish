//#condition polish.usePolishGui && polish.blackberry
/*
 * Created on Aug 12, 2008 at 10:10:29 PM.
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
package de.enough.polish.blackberry.ui;

import de.enough.polish.blackberry.midlet.MIDlet;
//#if polish.useNativeGui || polish.useNativeAlerts
	import de.enough.polish.blackberry.nativeui.AlertDialog;
//#endif
//#if polish.useNativeGui
	import de.enough.polish.blackberry.nativeui.FormScreen;
//#endif
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.AlertType;
import de.enough.polish.ui.AnimationThread;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.NativeDisplay;
import de.enough.polish.ui.StyleSheet;

//#if blackberry.certificate.dir:defined
	import net.rim.device.api.system.Backlight;
//#endif

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

/**
 * <p>Integrates native blackberry functions.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class NativeDisplayImpl implements NativeDisplay
{
	private final MIDlet midlet;

	protected NativeDisplayImpl( MIDlet midlet ) {
		this.midlet = midlet;
	}

	public static NativeDisplayImpl getDisplay( MIDlet midlet ) {
		return new NativeDisplayImpl( midlet );
	}
	
	//#if polish.LibraryBuild
		public static NativeDisplayImpl getDisplay( javax.microedition.midlet.MIDlet midlet ) {
			return null;
		}
	//#endif
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#callSerially(java.lang.Runnable)
	 */
	public void callSerially(Runnable r)
	{
		this.midlet.invokeLater( r );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#flashBacklight(int)
	 */
	public boolean flashBacklight(int duration)
	{
		//#if blackberry.certificate.dir:defined
			// the application is going to be signed, so we can use Backlight:
			Backlight.enable(true, duration/1000);
			return true;
		//#else
			//# return false;
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#getBestImageHeight(int)
	 */
	public int getBestImageHeight(int imageType)
	{
		// TODO robertvirkus implement getBestImageHeight
		return 0;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#getBestImageWidth(int)
	 */
	public int getBestImageWidth(int imageType)
	{
		// TODO robertvirkus implement getBestImageWidth
		return 0;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#getBorderStyle(boolean)
	 */
	public int getBorderStyle(boolean highlighted)
	{
		// TODO robertvirkus implement getBorderStyle
		return Graphics.SOLID;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#getColor(int)
	 */
	public int getColor(int colorSpecifier)
	{
		// TODO robertvirkus implement getColor
		return 0;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#isColor()
	 */
	public boolean isColor()
	{
		return net.rim.device.api.system.Display.isColor();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#numAlphaLevels()
	 */
	public int numAlphaLevels()
	{
		// TODO robertvirkus implement numAlphaLevels
		return 3;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#numColors()
	 */
	public int numColors()
	{
		return net.rim.device.api.system.Display.getNumColors();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#setCurrent(de.enough.polish.ui.Display)
	 */
	public void setCurrent(Display nextDisplayable)
	{
        Object lock = Application.getEventLock();
        synchronized (lock) {
        	BaseScreen baseScreen;
        	//#if polish.blackberry
        		//# baseScreen = nextDisplayable;
        	//#else
        		baseScreen = (BaseScreen)(Object)nextDisplayable;
        	//#endif
            this.midlet.pushScreen( baseScreen );
        }
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.NativeDisplay#vibrate(int)
	 */
	public boolean vibrate(int duration)
	{
		// TODO robertvirkus implement vibrate
		return false;
	}

	public boolean notifyDisplayableChange(Displayable currentDisplayable, Displayable nextDisplayable) {
    	// remove all fields but the dummy field:
		BaseScreen screen = (BaseScreen)(Object)Display.getInstance();
		screen.notifyDisplayableChange( currentDisplayable, nextDisplayable );
		//#if polish.useNativeAlerts || polish.useNativeGui
			if (nextDisplayable instanceof Alert) {
				AlertDialog dialog = new AlertDialog((Alert) nextDisplayable);
		        Object lock = Application.getEventLock();
		        synchronized (lock) {
		        	this.midlet.pushScreen(dialog);
		        }
				return true;
			} else 
		//#endif
		//#if polish.useNativeGui
			if (nextDisplayable instanceof Form) {
				Form polishForm = (Form)nextDisplayable;
				FormScreen bbForm = (FormScreen) polishForm.getNativeScreen();
				if (bbForm == null) {
					bbForm = new FormScreen( polishForm );
					polishForm.setNativeScreen(bbForm);
				}
		        Object lock = Application.getEventLock();
		        synchronized (lock) {
		        	if (screen.isDisplayed()) {
		        		this.midlet.popScreen(screen);
		        	}
		        	this.midlet.pushScreen(bbForm);
		        }
		        StyleSheet.currentScreen = polishForm;
				if (StyleSheet.animationThread == null) {
					StyleSheet.animationThread = new AnimationThread();
					StyleSheet.animationThread.start();
				}
				return true;
			}
		//#endif
		return false;
	}

	public void setCurrent(Displayable nextDisplayable) {
		// TODO Auto-generated method stub
		
	}

}
