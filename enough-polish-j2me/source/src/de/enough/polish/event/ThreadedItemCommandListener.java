//#condition polish.midp2 or polish.usePolishGui
/*
 * Created on Feb 22, 2007 at 12:41:53 PM.
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
 * <p>Processes item commandAction events in a single separate thread.</p>
 * <p>Note that several long running operations are handled synchronously, meaning they are handled
 *    one after the other. For processing several long running operations in parallel you
 *    can use the AsynchronousCommandListener.
 * </p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Feb 22, 2008 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see AsynchronousCommandListener
 */
public class ThreadedItemCommandListener implements Runnable, ItemCommandListener {
	
	private final ItemCommandListener parent;
	private final ArrayList commands;
	private final ArrayList items;
	private boolean isStopRequested;

	/**
	 * Creates a new threaded item command listener
	 * 
	 * @param parent the parent ItemCommandListener that is used to process commands in a background thread.
	 * @throws IllegalArgumentException when parent is null
	 */
	public ThreadedItemCommandListener( ItemCommandListener parent) {
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
					try {
						wait();
					} catch (InterruptedException e) {
						// ignore
					}
				}
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
			this.commands.add( cmd );
			this.items.add( item );
			notify();
		}

	}

}
