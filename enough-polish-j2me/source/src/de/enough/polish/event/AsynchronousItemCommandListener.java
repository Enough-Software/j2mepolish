//#condition polish.midp2 or polish.usePolishGui

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
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;

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
public class AsynchronousItemCommandListener implements Runnable, ItemCommandListener {
	
	private final ItemCommandListener parent;
	private final ArrayList commands;
	private final ArrayList items;
	private boolean isStopRequested;
	private boolean isWorking;

	/**
	 * Creates a new threaded command listener
	 * 
	 * @param parent the parent CommandListener that is used to process commands in a background thread.
	 * @throws IllegalArgumentException when parent is null
	 */
	public AsynchronousItemCommandListener( ItemCommandListener parent) {
		if (parent == null) {
			throw new IllegalArgumentException();
		}
		this.parent = parent;
		this.commands = new ArrayList();
		this.items = new ArrayList();
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
				Item item = null;
				synchronized(this) {
					cmd = (Command) this.commands.remove(0);
					item = (Item) this.items.remove(0);
				}
				try {
					this.parent.commandAction(cmd, item);
				} catch (Throwable e) {
					//#debug error
					System.out.println("Unable to process cmd " + cmd.getLabel() + " for item " + item + e);
				}
			} // while there are commands
		} // while (!this.isStopRequested) 

	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.ItemCommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Item)
	 */
	public void commandAction(Command cmd, Item item) {
		synchronized (this) {
			if (this.isWorking) {
				WorkerThread thread = new WorkerThread( cmd, item );
				thread.start();
			} else {
				this.commands.add( cmd );
				this.items.add( item );
				notify();
			}
		}

	}
	
	class WorkerThread extends Thread {
		private Command command;
		private Item item;
		
		public WorkerThread( Command command, Item item) {
			this.command = command;
			this.item = item;
		}
		
		public void run() {
			AsynchronousItemCommandListener.this.parent.commandAction( this.command, this.item);
		}
	}

}
