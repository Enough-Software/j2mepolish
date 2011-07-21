/*
 * Created on Feb 16, 2007 at 9:18:29 AM.
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
package de.enough.polish.util;

import java.util.Vector;

/**
 * <p>Processes tasks asynchronously</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see Task
 */
public class TaskThread extends Thread {
	
	private static TaskThread INSTANCE;
	private final Vector queue;
	private boolean stopRequested;
	
	private TaskThread() {
		this.queue = new Vector();
	}

	
	/**
	 * Retrieves the running instance of this thread.
	 * 
	 * @return the running instance (singleton pattern)
	 */
	public static TaskThread getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TaskThread();
			INSTANCE.start();
		}
		return INSTANCE;
	}
	
	/**
	 * Adds a task that should be executed in a background thread.
	 * 
	 * @param task the task
	 */
	public void addTask( Task task ) {
		this.queue.addElement( task );
		synchronized (this.queue) {
			this.queue.notify();
		}
	}
	
	public void run() {
		while (!this.stopRequested) {
			while (this.queue.size() != 0) {
				Task task = (Task) this.queue.elementAt(0);
				this.queue.removeElementAt(0);
				try {
					task.execute();
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to execute task " + task + e );
				}
			}
			synchronized (this.queue) {
				try {
					this.queue.wait();
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
	}
	

}
