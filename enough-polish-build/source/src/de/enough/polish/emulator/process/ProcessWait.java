/*
 * Created on 16-Oct-2008 from seven to eleven
 * 
 * Copyright (c) 2004-2008 Andre Schmidt / Enough Software
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
package de.enough.polish.emulator.process;

import java.io.IOException;
import java.io.PrintStream;

import de.enough.polish.util.OutputFilter;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>
 * Waits until an started process prints a matching
 * line and just returns. Used in emulators to
 * wait till a specific stage in a process is reached.
 * </p>
 *
 * <p>Copyright Enough Software 2008-2009</p>
 * @author Andre Schmidt
 */
public class ProcessWait extends ProcessCondition implements OutputFilter, Runnable{

	private long timeout;

	/**
	 * Constructs a new Wait instance. Starts
	 * the process described in <code>arguments</code>
	 * and wait for <code>proceed()</code> to be called
	 * in <code>filter()</code> to return.
	 *  
	 * @param arguments the arguments for the process to start
	 * @param regex the regex for a line to match 
	 * @throws IOException if an process error occurs
	 */
	public ProcessWait(String[] arguments,String regex) throws IOException
	{
		super(arguments,regex);
		
		synchronized(this)
		{
			ProcessUtil.exec(arguments,null,false,this,null);
			try {
					wait();
			} catch (InterruptedException e) {
				// ignore
			}
		} 
	}
	
	/**
	 * Constructs a new Wait instance. Starts
	 * the process described in <code>arguments</code>
	 * and wait for <code>proceed()</code> to be called
	 * in <code>filter()</code> to return.
	 *  
	 * @param arguments the arguments for the process to start
	 * @param regexes the regex conditions for a line to match (first match will abort the wait) 
	 * @throws IOException if an process error occurs
	 */
	public ProcessWait(String[] arguments,String[] regexes) throws IOException
	{
		this( arguments, regexes, -1);
	}
	
	public ProcessWait(String[] arguments,String[] regexes, long timeout)  throws IOException
	{
		super(arguments,regexes);
		this.timeout = timeout;
		if (timeout != -1) {
			new Thread(this).start();
		}
		synchronized(this)
		{
			ProcessUtil.exec(arguments,null,false,this,null);
			try {
					wait();
			} catch (InterruptedException e) {
				// ignore
			}
		} 
	}

	/**
	 * Used to resolve <code>wait()</code> in the constructor.
	 */
	public synchronized void proceed()
	{
		this.timeout = -1;
		notify();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.process.Condition#filter(java.lang.String, java.io.PrintStream)
	 */
	public void filter(String message, PrintStream output) {
		// if the regex is matched by the message printed
		// of the process ...
		//output.println(message);
		if (matches(message))
		{
			// call proceed()
			proceed();
		}
	}

	public void run() {
		try {
			Thread.sleep(this.timeout);
		} catch (InterruptedException e) {
			// ignore interrupt
		}
		if (this.timeout != -1) {
			System.out.println("Reached timeout...");
			proceed();
		}
	}
	
	
}
