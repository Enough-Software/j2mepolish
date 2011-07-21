//#condition polish.usePolishGui

/*
 * Created on Dec 18, 2008 at 12:59:40 PM.
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
package de.enough.polish.log.display;

import de.enough.polish.log.LogEntry;
import de.enough.polish.log.LogHandler;

/**
 * <p>Displays log entries on top of the screen - this can only be used when the J2ME Polish UI is active.</p>
 * <p>Example:</p>
 * <pre>
 * &lt;debug ...&gt;
 *    &lt;handler name=&quot;display&quot; />
 *      &lt;!-- optional parameters --&gt;
 *      &lt;!-- hide entries after 10 seconds: --&gt;
 *      &lt;parameter name=&quot;timeout&quot; value=&quot;10s&quot; /&gt;
 *      &lt;!-- show maximum 30 log entries: --&gt;
 *      &lt;parameter name=&quot;max-entries&quot; value=&quot;30&quot; /&gt;
 *      &lt;!-- specify used colors: --&gt;
 *      &lt;parameter name=&quot;background-color&quot; value=&quot;#a333&quot; /&gt;
 *      &lt;parameter name=&quot;font-color&quot; value=&quot;yellow&quot; /&gt;
 *      &lt;parameter name=&quot;shadow-color&quot; value=&quot;rgb(0,0,0)&quot; /&gt;
 *    &lt;/handler&gt;
 * &lt;/debug&gt;
 * </pre>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DisplayLogHandler extends LogHandler
{
	
	protected LogCanvas logCanvas;

	/**
	 * Creates a new log entry
	 */
	public DisplayLogHandler()
	{
		// nothing to initialize
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.log.LogHandler#handleLogEntry(de.enough.polish.log.LogEntry)
	 */
	public void handleLogEntry(LogEntry logEntry) throws Exception
	{
		if (this.logCanvas == null) {
			createLogCanvas();
		}
		this.logCanvas.addLogEntry( logEntry );
	}

	/**
	 * Creates the canvas that displays log entries. 
	 */
	protected void createLogCanvas()
	{
		this.logCanvas = new LogCanvas();
	}

}
