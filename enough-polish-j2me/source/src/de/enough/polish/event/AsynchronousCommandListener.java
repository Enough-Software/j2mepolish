//#condition polish.midp or polish.usePolishGui

/*
 * Created on March 12, 2007 at 2:14:10 PM.
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
package de.enough.polish.event;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import de.enough.polish.util.ArrayList;

/**
 * <p>Processes commandAction events in (possibly several) separate threads.</p>
 * <p>Note that several long running operations are handled asynchronously, meaning they are handled
 *    at the same time in different threads. For processing several long running operations in a sequence you
 *    can use the ThreadedCommandListener.
 * </p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        March 12, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see ThreadedCommandListener
 */
public class AsynchronousCommandListener implements Runnable, CommandListener {
	
	private final CommandListener parent;
	private final ArrayList commands;
	private final ArrayList displays;
	private boolean isStopRequested;
	private boolean isWorking;

	/**
	 * Creates a new AsynchronousCommandListener.java
	 * 
	 * @param parent the parent CommandListener that is used to process commands in a background thread.
	 * @throws IllegalArgumentException when parent is null
	 */
	public AsynchronousCommandListener( CommandListener parent) {
		if (parent == null) {
			throw new IllegalArgumentException();
		}
		this.parent = parent;
		this.commands = new ArrayList();
		this.displays = new ArrayList();
		Thread thread = new Thread( this );
		thread.start();
	}
	
	/**
	 * Stops the background thread of this command listener.
	 * Please note that no further events will be processed after requestStop() has been called.
	 */
	public void requestStop() {
		this.isStopRequested = true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (!this.isStopRequested) {
			synchronized(this) {
				if (this.commands.size() == 0) {
					this.isWorking = false;
					try {
						wait();
					} catch (InterruptedException e) {
						// ignore
					}
				}
				this.isWorking = true;
			}
			while (this.commands.size() > 0) {
				Command cmd = null;
				Displayable disp = null;
				synchronized(this) {
					cmd = (Command) this.commands.remove(0);
					disp = (Displayable) this.displays.remove(0);
				}
				try {
					this.parent.commandAction(cmd, disp);
				} catch (Throwable e) {
					//#debug error
					System.out.println("Unable to process cmd " + cmd.getLabel() + " for screen " + disp + e);
				}
			} // while there are commands
		} // while (!this.isStopRequested) 

	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable disp) {
		synchronized (this) {
			if (this.isWorking) {
				WorkerThread thread = new WorkerThread( cmd, disp );
				thread.start();
			} else {
				this.commands.add( cmd );
				this.displays.add( disp );
				notify();
			}
		}

	}
	
	class WorkerThread extends Thread {
		private Command command;
		private Displayable displayable;
		
		public WorkerThread( Command command, Displayable displayable) {
			this.command = command;
			this.displayable = displayable;
		}
		
		public void run() {
			AsynchronousCommandListener.this.parent.commandAction( this.command, this.displayable);
		}
	}

}
