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
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;

/**
 * <p>Processes commandAction events in (possibly several) separate threads.</p>
 * <p>Note that several long running operations are handled asynchronously, meaning they are handled
 *    at the same time in different threads.
 * </p>
 *
 * <p>Copyright Enough Software 2008</p>
 * <pre>
 * history
 *        March 12, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see ThreadedCommandListener
 */
public class AsynchronousMultipleCommandListener implements Runnable {
	
	
	private static AsynchronousMultipleCommandListener instance;
	
	private CommandListener commandListener;
	private Command command;
	private Displayable displayable;
	private ItemCommandListener itemCommandListener;
	private Item parentItem;
	private boolean isStopRequested;
	private boolean isWorking;

	/**
	 * Creates a new AsynchronousMultipleCommandListener
	 */
	private AsynchronousMultipleCommandListener() {
		Thread thread = new Thread( this );
		thread.start();
	}
	
	/**
	 * Retrieves the singleton instance of this asynchronous command listener
	 * @return the singleton instance
	 */
	public static AsynchronousMultipleCommandListener getInstance() {
		if (instance == null) {
			instance = new AsynchronousMultipleCommandListener();
		}
		return instance;
	}
	
	/**
	 * Stops the background thread of this command listener.
	 * Please note that no further events will be processed after requestStop() has been called.
	 */
	public void requestStop() {
		this.isStopRequested = true;
	}
	
	/**
	 * Executes the specified command event asynchronously
	 * @param listener the listener
	 * @param cmd the command
	 * @param disp the displayable
	 */
	public void commandAction(CommandListener listener, Command cmd, Displayable disp) {
		synchronized (this) {
			if (this.isWorking) {
				WorkerThread thread = new WorkerThread( listener, cmd, disp );
				thread.start();
			} else {
				this.commandListener = listener;
				this.command = cmd;
				this.displayable = disp;
				notify();
			}
		}

	}
	
	/**
	 * Executes the specified command event asynchronously
	 * @param listener the listener
	 * @param cmd the command
	 * @param item the parent item 
	 */
	public void commandAction(ItemCommandListener listener, Command cmd, Item item) {
		synchronized (this) {
			if (this.isWorking) {
				WorkerThread thread = new WorkerThread( listener, cmd, item );
				thread.start();
			} else {
				this.itemCommandListener = listener;
				this.command = cmd;
				this.parentItem = item;
				notify();
			}
		}

	}

	
	//#if false
		/**
		 * Executes the specified command event asynchronously
		 * @param listener the listener
		 * @param cmd the command
		 * @param disp the displayable
		 */
		public void commandAction( de.enough.polish.ui.CommandListener listener, de.enough.polish.ui.Command cmd, de.enough.polish.ui.Displayable disp) {
			// ignore
		}
	//#endif
		
	//#if false
		/**
		 * Executes the specified command event asynchronously
		 * @param listener the listener
		 * @param cmd the command
		 * @param item the parent item 
		 */
		public void commandAction( de.enough.polish.ui.ItemCommandListener listener, de.enough.polish.ui.Command cmd, de.enough.polish.ui.Item item) {
			// ignore
		}
	//#endif

		
	//#if polish.LibraryBuild && polish.usePolishGui
		//# public void commandAction( javax.microedition.lcdui.CommandListener listener, javax.microedition.lcdui.Command cmd, javax.microedition.lcdui.Displayable disp) {
		//# }
	//#endif

	//#if polish.LibraryBuild && polish.usePolishGui
		//# public void commandAction( javax.microedition.lcdui.ItemCommandListener listener, javax.microedition.lcdui.Command cmd, javax.microedition.lcdui.Item item) {
		//# }
	//#endif


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		CommandListener listener = null;
		ItemCommandListener itemListener = null;
		Command cmd = null;
		Displayable disp = null;
		Item itm = null;
		while (!this.isStopRequested) {
			synchronized(this) {
				if (this.command == null) {
					this.isWorking = false;
					try {
						wait();
					} catch (InterruptedException e) {
						// ignore
					}
				}
				this.isWorking = true;
				listener = this.commandListener;
				itemListener = this.itemCommandListener;
				cmd = this.command;
				disp = this.displayable;
				itm = this.parentItem;
				this.command = null;
				this.displayable = null;
				this.commandListener = null;
				this.itemCommandListener = null;
				this.parentItem = null;
			}
			
			try {
				if (listener != null) {
					listener.commandAction(cmd, disp);
				} else {
					itemListener.commandAction(cmd, itm); 
				}
			} catch (Throwable e) {
				//#debug error
				System.out.println("Unable to process cmd " + cmd.getLabel() + " for screen " + disp + e);
			}
		} // while (!this.isStopRequested) 

	}

	
	
	private static class WorkerThread extends Thread {
		private Command command;
		private Displayable displayable;
		private CommandListener commandListener;
		private ItemCommandListener itemCommandListener;
		private Item item;
		
		public WorkerThread( CommandListener commandListener, Command command, Displayable displayable) {
			this.commandListener = commandListener;
			this.command = command;
			this.displayable = displayable;
		}

		public WorkerThread( ItemCommandListener commandListener, Command command, Item item) {
			this.itemCommandListener = commandListener;
			this.command = command;
			this.item = item;
		}

		public void run() {
			try {
				if (this.commandListener != null) {
					this.commandListener.commandAction( this.command, this.displayable);
				} else {
					this.itemCommandListener.commandAction(this.command, this.item);
				}
			} catch (Throwable e) {
				//#debug error
				System.out.println("Unable to process cmd " + this.command.getLabel() + " for screen " + this.displayable + e);
			}
		}
	}

}
