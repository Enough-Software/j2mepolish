/*
 * Copyright (c) 2011 Robert Virkus / Enough Software
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

package de.enough.polish.sample.processing.tree;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.processing.ProcessingContext;
import de.enough.polish.processing.ProcessingScreen;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Displayable;

/**
 * <p>Shows a demonstration of the possibilities of J2ME Polish.</p>
 *
 * <p>Copyright Enough Software 2011</p>
 * 
 * @author Ovidiu Iliescu
 */
public class ProcessingTreeMidlet 
extends MIDlet
implements CommandListener
{

	private ProcessingScreen screen ;
    private Display display;
    
    private Command cmdExit = new Command("Exit", Command.EXIT, 1 );
    private Command cmdHelp = new Command("Help", Command.HELP, 1 );
    

	public ProcessingTreeMidlet() {
		super();

        // Create a sample ProcessingContext
        ProcessingContext context = new ProcessingTreeContext();

        // And attach it to a ProcessingScreen.
		//#style mainScreen
		this.screen = new ProcessingScreen("Processing Tree", context);
		this.screen.addCommand( this.cmdExit );
		this.screen.addCommand(this.cmdHelp);
		this.screen.setCommandListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {		
		this.display = Display.getDisplay(this);
		this.display.setCurrent( this.screen );		
	}

	/*
	 * (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		// ignore
	}

	/*
	 * (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// just quit
	}

	private void quit() {
		notifyDestroyed();
	}

	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == this.cmdExit) {
			quit();
		} else {
			Alert alert = new Alert("A tree is being rendered everytime you click a button or touch the screen (on touch enabled phones).\nOriginal code by Michael Lange\n\nhttp://www.openprocessing.org/visuals/?visualID=4732");
			this.display.setCurrent(alert);
		}
		
	}


}
