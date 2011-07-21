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
 * Blocks until an started process is finished. While
 * the process is running, <code>filter()</code> matches
 * each line printed by the process with the <code>regex</code>  
 * </p>
 *
 * <p>Copyright Enough Software 2008-2009</p>

 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class ProcessCondition implements OutputFilter{
	
	String[] arguments;
	String[] regexes;
	boolean result;
	
	/**
	 * Constructs a new Condition instance.
	 * @param arguments the arguments to start the process
	 * @param regex the regex to match
	 */
	public ProcessCondition(String[] arguments,String regex)
	{
		this( arguments, new String[]{regex});
	}
	
	/**
	 * Constructs a new Condition instance.
	 * @param arguments the arguments to start the process
	 * @param regexes the regexes to match
	 */
	public ProcessCondition(String[] arguments, String[] regexes)
	{
		this.arguments = arguments;
		this.regexes = regexes;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.util.OutputFilter#filter(java.lang.String, java.io.PrintStream)
	 */
	public void filter(String message, PrintStream output) {
		// if a line printed by the process 
		// matches the regex ...
		if (matches(message))
		{
			// set <code>result</code> to true 
			this.result = true;
		}
	}
	
	protected boolean matches(String message) {
		for (int i=0; i<this.regexes.length; i++) {
			String regex = this.regexes[i];
			if(message.matches(regex)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calls the process, waits until it is finished and
	 * returns <code>result</code>. 
	 * @return true, if the regex was matched, otherwise false
	 * @throws IOException if an process error occurs
	 */
	public boolean isMet() throws IOException
	{
		ProcessUtil.exec(this.arguments, null, true, this, null);
		if (!this.result) {
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		return this.result;
	}
	
}
