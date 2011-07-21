/*
 * Created on Mar 13, 2008 at 12:58:57 PM.
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
package de.enough.polish.sample.animation;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;

import de.enough.polish.event.EventListener;
import de.enough.polish.event.EventManager;
import de.enough.polish.util.DeviceControl;

/**
 * 
 * <br>Copyright Enough Software 2008
 * @author Robert Virkus
 */
public class AnimationMidlet extends MIDlet implements CommandListener{

    private Display display;
    private Command cmdSkipIntro = new Command("", Command.OK, 1);
    private List mainMenu;

	protected void destroyApp(boolean unconditional) {
    	//#debug
        System.out.println("Destroying MIDlet.");
    }

    protected void pauseApp() {
    	//#debug
        System.out.println("Stopping MIDlet.");
    }

    protected void startApp(){
    	//#debug
        System.out.println("Starting MIDlet.");
        boolean showIntro = this.display == null;
        this.display = Display.getDisplay(this);
        if (showIntro) {
        	showIntro();
        } else {
        	showMainMenu();
        }
        
    }
    
    
    /**
	 * 
	 */
	private void showMainMenu()
	{
		if (this.mainMenu == null) {
			//#style mainMenuScreen
			List list = new List("Vampires", List.IMPLICIT);
			//#style mainMenuItem
			list.append("New Game", null);
			//#style mainMenuItem
			list.append("Help", null);
			//#style mainMenuItem
			list.append("About", null);
			//#style mainMenuItem
			list.append("Exit", null);
			list.setCommandListener(this);
			this.mainMenu = list;
		}
		this.display.setCurrent(this.mainMenu);
		
	}

	private void showIntro() {
        //#style introScreen
        Form form = new Form(null);
        //#style introText1
        StringItem introText1 = new StringItem( null, "When the night falls...");
        form.append(introText1);
        //#style introText2
        StringItem introText2 = new StringItem( null, "...vampires come out!");
        form.append(introText2);
        ImageItem imageItem = null;
        try {
        	Image image = Image.createImage("/vampire.png");
        	//#style introImage
        	imageItem = new ImageItem( null, image, ImageItem.PLAIN, null);
        	form.append(imageItem);
        } catch (Exception e) {
        	//#debug error
        	System.out.println("Unable to load image " + e );
        }
        //#style introText3
        StringItem introText3 = new StringItem( null, "Only you can stop them!");
        form.append(introText3);
        //#style pressKeyText
        StringItem pressKeyText = new StringItem( null, "Press Fire");
        pressKeyText.setDefaultCommand(this.cmdSkipIntro);
        form.append(pressKeyText);
        form.setCommandListener(this);
                
        DeviceControl.lightOn();
        this.display.setCurrent(form);
    }

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable dis)
	{
		if (cmd == this.cmdSkipIntro) {
			EventManager.getInstance().removeAllRemappings();
			showMainMenu();
		} else {
			if (this.mainMenu != null && this.mainMenu.getSelectedIndex() == 0) {
				showIntro();
			} else {
				notifyDestroyed();
			}
		}
		
	}


}
