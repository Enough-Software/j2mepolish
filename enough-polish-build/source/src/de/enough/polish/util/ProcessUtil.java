/*
 * Created on 28-Apr-2005 at 13:15:59.
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
package de.enough.polish.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;


/**
 * <p>Can be used to start other processes.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        28-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class ProcessUtil {

	/**
	 * This class cannot be instantiated
	 */
	private ProcessUtil() {
		// This class cannot be instantiated
	}
	
	/**
	 * Executes an external process and logs the output of it.
	 * This method just forwards the call to exec( String[], String, boolean ).
	 * 
	 * @param arguments the arguments in an array list, e.g. { "java", "-jar", "home/lib/Blubb.jar" }
	 * @param info the information that should be printed before each log output, e.g. "process: "
	 * @param wait true when this method should wait for the return of the process.
	 * @return always 0, when wait == false; -1 when the waiting was interrupted; in all other cases the return value
	 *         of the process is returned, 0 usally indicates success.
	 * @throws IOException when the process could not be started
	 * @see #exec(String[], String, boolean)
	 */
	public static int exec( ArrayList arguments, String info, boolean wait ) 
	throws IOException 
	{
		return exec( arguments, info, wait, null, null );
	}

	/**
	 * Executes an external process and logs the output of it.
	 * This method just forwards the call to exec( String[], String, boolean ).
	 * 
	 * @param arguments the arguments in an array list, e.g. { "java", "-jar", "home/lib/Blubb.jar" }
	 * @param info the information that should be printed before each log output, e.g. "process: "
	 * @param wait true when this method should wait for the return of the process.
	 * @param filter the filter for messages of the processs
	 * @return always 0, when wait == false; -1 when the waiting was interrupted; in all other cases the return value
	 *         of the process is returned, 0 usally indicates success.
	 * @throws IOException when the process could not be started
	 * @see #exec(String[], String, boolean)
	 */
	public static int exec( ArrayList arguments, String info, boolean wait, OutputFilter filter ) 
	throws IOException 
	{
		return exec( arguments, info, wait, filter, null );
	}
	
	/**
	 * Executes an external process and logs the output of it.
	 * This method just forwards the call to exec( String[], String, boolean ).
	 * 
	 * @param arguments the arguments in an array list, e.g. { "java", "-jar", "home/lib/Blubb.jar" }
	 * @param info the information that should be printed before each log output, e.g. "process: "
	 * @param wait true when this method should wait for the return of the process.
	 * @param filter the filter for messages of the processs
	 * @param dir the current directory for the started process
	 * @return always 0, when wait == false; -1 when the waiting was interrupted; in all other cases the return value
	 *         of the process is returned, 0 usally indicates success.
	 * @throws IOException when the process could not be started
	 * @see #exec(String[], String, boolean)
	 */
	public static int exec( ArrayList arguments, String info, boolean wait, OutputFilter filter, File dir ) 
	throws IOException 
	{
		String[] parameters = (String[]) arguments.toArray( new String[ arguments.size() ] );
		return exec( parameters, info, wait, filter, dir );
	}

	
	/**
	 * Executes an external process and logs the output of it.
	 * 
	 * @param arguments the arguments, e.g. { "java", "-jar", "home/lib/Blubb.jar" }
	 * @param info the information that should be printed before each log output, e.g. "process: "
	 * @param wait true when this method should wait for the return of the process.
	 * @return always 0, when wait == false; -1 when the waiting was interrupted; in all other cases the return value
	 *         of the process is returned, 0 usally indicates success.
	 * @throws IOException when the process could not be started
	 */
	public static int exec( String[] arguments, String info, boolean wait ) 
	throws IOException 
	{
		return exec( arguments, info, wait, null, null );
	}
	
	/**
	 * Executes an external process and logs the output of it.
	 * 
	 * @param arguments the arguments, e.g. { "java", "-jar", "home/lib/Blubb.jar" }
	 * @param info the information that should be printed before each log output, e.g. "process: "
	 * @param wait true when this method should wait for the return of the process.
	 * @param filter the filter for messages of the processs
	 * @return always 0, when wait == false; -1 when the waiting was interrupted; in all other cases the return value
	 *         of the process is returned, 0 usally indicates success.
	 * @throws IOException when the process could not be started
	 */
	public static int exec( String[] arguments, String info, boolean wait, OutputFilter filter ) 
	throws IOException
	{
		return exec( arguments, info, wait, filter, null );
	}
	
	/**
	 * Executes an external process and logs the output of it.
	 * 
	 * @param arguments the arguments, e.g. { "java", "-jar", "home/lib/Blubb.jar" }
	 * @param info the information that should be printed before each log output, e.g. "process: "
	 * @param wait true when this method should wait for the return of the process.
	 * @param filter the filter for messages of the processs
	 * @param dir the current directory for the started process
	 * @return always 0, when wait == false; -1 when the waiting was interrupted; in all other cases the return value
	 *         of the process is returned, 0 usally indicates success.
	 * @throws IOException when the process could not be started
	 */
	public static int exec( String[] arguments, String info, boolean wait, OutputFilter filter, File dir ) 
	throws IOException 
	{
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec( arguments, null, dir );
		LoggerThread errorLog = new LoggerThread( process.getErrorStream(), System.err, info, true, filter );
		errorLog.start();
		LoggerThread outputLog = new LoggerThread( process.getInputStream(), System.out, info, true, filter );
		outputLog.start();
		int result = 0;
		if (wait) {
			try {
				result = process.waitFor();
			} catch (InterruptedException e) {
				result = -1;
				e.printStackTrace();
				System.err.println("Unable to wait for process [" + arguments[0] + "]: " + e.toString() );
			}
		}
		return result;
	}


	/**
	 * Records the output of the specified process and returns the output as a string array.
	 * @param arguments the process arguments
	 * @return the output as a string array
	 * @throws IOException 
	 * @throws IOException when the process could not be started
	 */
	public static String[] toStringArray(String[] arguments) 
	throws IOException 
	{
		return toStringArray(arguments, null);
	}
	
	/**
	 * Records the output of the specified process and returns the output as a string array.
	 * @param arguments the process arguments
	 * @param dir the current directory for the process
	 * @return the output as a string array
	 * @throws IOException when the process could not be started
	 */
	public static String[] toStringArray(String[] arguments, File dir) 
	throws IOException 
	{
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec( arguments, null, dir );
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		PrintStream output = new PrintStream(byteOut);
		LoggerThread errorLog = new LoggerThread( process.getErrorStream(), output, null, false, null );
		errorLog.start();
		LoggerThread outputLog = new LoggerThread( process.getInputStream(), output, null, false, null );
		outputLog.start();
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.err.println("Unable to wait for process [" + arguments[0] + "]: " + e.toString() );
		}
		String text = new String( byteOut.toByteArray() );
		return StringUtil.split(text, '\n');
	}
	
	
	/**
	 * Provides a debug helper for printing out all arguments of a process for error tracking.
	 * @param arguments the arguments of the process
	 * @return a single string with spaces between single arguments and quotation marks around arguments that contain spaces themselves.
	 */
	public static String toString( String[] arguments ) {
		StringBuffer call = new StringBuffer();
		for (int j = 0; j < arguments.length; j++) {
			String arg = arguments[j];
			if (arg.indexOf(' ') != -1) {
				call.append('"').append(arg).append('"');
			} else {
				call.append(arg);
			}
			call.append(' ');
		}
		return call.toString();
	}
	
	/**
	 * Provides a debug helper for printing out all arguments of a process for error tracking.
	 * @param arguments the arguments of the process
	 * @return a single string with spaces between single arguments and quotation marks around arguments that contain spaces themselves.
	 */
	public static String toString( ArrayList arguments ) {
		StringBuffer call = new StringBuffer();
		for (int i=0; i<arguments.size(); i++) {
			String arg = (String) arguments.get(i);
			if (arg.indexOf(' ') != -1) {
				call.append('"').append(arg).append('"');
			} else {
				call.append(arg);
			}
			call.append(' ');
		}
		return call.toString();
	}

}
