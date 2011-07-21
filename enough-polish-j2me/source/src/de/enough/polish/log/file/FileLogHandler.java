//#condition polish.api.pdaapi || polish.api.fileconnection

/*
 * Created on 05-Jul-2005 at 23:45:29.
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
package de.enough.polish.log.file;

import java.io.PrintStream;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import de.enough.polish.log.LogEntry;
import de.enough.polish.log.LogHandler;
import de.enough.polish.util.ArrayList;

/**
 * <p>Writes log entries into the filesystem.</p>
 * <p>
 * Use the loghandler by specifying the following handler in your build.xml script:
 * <pre>
 * &lt;debug ...&gt;
 *    &lt;handler name=&quot;file&quot; />
 * &lt;/debug&gt;
 * </pre>
 * </p>
 * <p>
 * You can configure this log handler with following parameters:
 * </p>
 * <ul>
 *   <li><b>preferredRoot</b>: The file root into which the log should be written, e.g. E:</li>
 *   <li><b>useUnqiueName</b>: Set to true when eacb log should be carry a unique name, this ensures that former logs are not overwritten. 
 *          J2ME Polish adds the current time in milliseonds to each log-file when this option is activated.</li>
 *   <li><b>fileName</b>: The name of the log-file, defaults to "j2melog".</li>
 *   <li><b>flushEachEntry</b>: Set to true for writing each log entry. This allows to read the log even when the application crashes horribly in between. 
 *          Since the permission is re-checked after each flushing, it is advisable to sign the application for testing.</li>
 * </ul>
 * <p>Example:
 * <pre>
 * &lt;debug ...&gt;
 *    &lt;handler name=&quot;file&quot; />
 *      &lt;-- optional parameters --&gt;
 *      &lt;parameter name=&quot;preferredRoot&quot; value=&quot;E:&quot; /&gt;
 *      &lt;parameter name=&quot;useUnqiueName&quot; value=&quot;true&quot; /&gt;
 *      &lt;parameter name=&quot;fileName&quot; value=&quot;applog&quot; /&gt;
 *      &lt;parameter name=&quot;flushEachEntry&quot; value=&quot;false&quot; /&gt;
 *    &lt;/handler&gt;
 * &lt;/debug&gt;
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Jul 11, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FileLogHandler 
extends LogHandler
implements Runnable
{
	
	private PrintStream out;
	private boolean isShuttingDown;
	private ArrayList scheduledLogEntries;
	private boolean isPermanentLogError;

	/**
	 * Creates a new file log handler.
	 *
	 */
	public FileLogHandler() {
		super();
	}

	public void handleLogEntry(LogEntry entry) throws Exception {
		if (this.isPermanentLogError) {
			return;
		}
		if (this.scheduledLogEntries == null) {
			this.scheduledLogEntries = new ArrayList( 7 );
			Thread thread = new Thread( this );
			thread.start();
		}
		synchronized ( this.scheduledLogEntries ) {
			this.scheduledLogEntries.add(entry);
			this.scheduledLogEntries.notify();
		}
	}

	public void exit() {
		super.exit();
		if (this.out != null) {
			this.out.flush();
			this.out.close();
			this.out = null;
		}
	}

	public void run() {
		FileConnection connection = null;
		// create the logfile:
		synchronized ( this ) {
			String url = null;
			String root = null;
			Enumeration enumeration = FileSystemRegistry.listRoots();
			String roots = "";
			//#if polish.log.file.preferredRoot:defined
				while (enumeration.hasMoreElements()) {
					root = (String) enumeration.nextElement();
					roots += root + "; ";
					//#= if  ( root.startsWith( "${polish.log.file.preferredRoot}" )) {
						break;
					//#= }
				}
				
			//#else				
				//root = (String) enumeration.nextElement();
				while (enumeration.hasMoreElements()) {
					try{
						root = (String) enumeration.nextElement();
						//#if polish.log.file.useUnqiueName == true
							url = "file:///" + root + "j2melog" + System.currentTimeMillis() + ".txt";
						//#elif polish.log.file.fileName:defined
							//#= url = "file:///" + root + "${polish.log.file.fileName}";
						//#else
							url = "file:///" + root + "j2melog.txt";
						//#endif
						FileConnection c = null;
						if( url != null ){
							c = (FileConnection) Connector.open( url, Connector.READ_WRITE );
						}
						if( c != null ){
							if( ( !c.canRead() ) || ( !c.canWrite() ) ){
								continue;
							}
							this.out = new PrintStream( c.openOutputStream() );
							break;
						}
					}catch (Exception e) {
						//ignore
					}finally{
						if( this.out != null ){
							try{
								this.out.close();
							}catch (Exception e) {
								//ignore
							}
						}
					}
				}
			//#endif
			
			//#if polish.log.file.useUnqiueName == true
				url = "file:///" + root + "j2melog" + System.currentTimeMillis() + ".txt";
			//#elif polish.log.file.fileName:defined
				//#= url = "file:///" + root + "${polish.log.file.fileName}";
			//#else
				url = "file:///" + root + "j2melog.txt";
			//#endif
			try {
				connection = (FileConnection) Connector.open( url, Connector.READ_WRITE );
				if (!connection.exists()) {
					//System.out.println("Creating file...");
					connection.create();
				}
				//System.out.println("opening data output stream...");
				this.out = new PrintStream( connection.openOutputStream() );
				this.out.println("time\tlevel\tclass\tline\tmessage\terror");
				//#if polish.log.file.flushEachEntry == true
					this.out.close();
					this.out = null;
				//#endif
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Unable to open file log: " + e );
				this.isPermanentLogError = true;
				return;
			}
			//this.out.println( roots );
		}
		
		while (!this.isShuttingDown) {
			while ( this.scheduledLogEntries.size() != 0 ) {
				LogEntry entry;
				synchronized ( this.scheduledLogEntries ) {
					entry = (LogEntry) this.scheduledLogEntries.remove(0);
				}
				StringBuffer buffer = new StringBuffer();					
				buffer.append( entry.time ).append('\t')
					.append( entry.level ).append('\t')
					.append( entry.className ).append('\t')
					.append( entry.lineNumber ).append('\t')
					.append( entry.message ).append('\t')
					.append( entry.exception );
				try {
					//#if polish.log.file.flushEachEntry == true
						this.out = new PrintStream( connection.openOutputStream(connection.fileSize()) );
					//#endif
					this.out.println( buffer.toString() );
					//#if polish.log.file.flushEachEntry == true
						this.out.close();
						this.out = null;
					//#endif
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Unable to write log entry: " + e );
				}
			}
			// wait for next log entry:
			try {
				synchronized ( this.scheduledLogEntries ) {
					this.scheduledLogEntries.wait();
				}
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

}
