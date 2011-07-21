//#condition polish.usePolishGui
/*
 * Created on Dec 18, 2008 at 1:03:01 PM.
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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.log.LogEntry;
import de.enough.polish.ui.Canvas;
import de.enough.polish.ui.Display;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.TextUtil;

/**
 * <p>Displays log entries directly on the screen.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LogCanvas extends Canvas
{
	//#if polish.log.display.timeout:defined
		//#= private static final long TIMEOUT = ${ time(polish.log.display.timeout) };
	//#else
		private static final long TIMEOUT = 30 * 1000;
	//#endif
	//#if polish.log.display.max-entries:defined
		//#= private static final int MAX_ENTRIES = ${ polish.log.display.max-entries };
	//#else
		private static final int MAX_ENTRIES = 100;
	//#endif
	//#if polish.log.display.background-color:defined
		//#= private static final int COLOR_BACKGROUND = ${ color( polish.log.display.background-color) };
	//#else
		private static final int COLOR_BACKGROUND = 0xaaffffff;
	//#endif
	//#if polish.log.display.font-color:defined
		//#= private static final int COLOR_FONT = ${ color( polish.log.display.font-color) };
	//#else
		private static final int COLOR_FONT = 0;
	//#endif
	//#if polish.log.display.shadow-color:defined
		//#= private static final int COLOR_SHADOW = ${ color( polish.log.display.shadow-color) };
	//#else
		private static final int COLOR_SHADOW = 0xffffff;
	//#endif
		
	protected ArrayList logLines;
	protected boolean isRegistered;
	protected long lastAddTime;

	/**
	 * Creates a new log canvas
	 */
	public LogCanvas()
	{
		this.logLines = new ArrayList(MAX_ENTRIES);
	}
	
	/**
	 * Adds a log entry to this screen.
	 * The default implementation just adds the message of the log entry.
	 * @param logEntry the log entry that is added
	 */
	public void addLogEntry(LogEntry logEntry)
	{
		this.lastAddTime = logEntry.time;
		String message = logEntry.getMessage();
		Font font = Font.getDefaultFont();
		int screenWidth = Display.getScreenWidth() - 10;
		String[] lines = TextUtil.wrap(message, font, screenWidth, screenWidth );
		for (int i=lines.length; --i >= 0; ) {
			if (i == 0) {
				this.logLines.add( lines[i] );				
			} else {
				this.logLines.add( " " + lines[i] );
			}
		}
		while (this.logLines.size() > MAX_ENTRIES) {
			this.logLines.remove(0);
		}
		Display display = Display.getInstance();
		if (!this.isRegistered) {
			if (display != null) {
				//#if polish.log.display || polish.Display.supportLayers 
					display.addLayer( this );
				//#endif
				this.isRegistered = true;
			}
		} else if (display != null){
			display.repaint();
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	protected void paint(Graphics g)
	{
		if (System.currentTimeMillis() - this.lastAddTime > TIMEOUT) {
			return;
		}
		
		Font font = Font.getDefaultFont();
		g.setFont( font );
		Object[] lines = this.logLines.getInternalArray();
		int screenHeight = Display.getScreenHeight();
		int lineHeight = font.getHeight() + 4;
		int y = lineHeight;
		DrawUtil.fillRect(0, 0, getWidth(), Math.max( screenHeight, this.logLines.size()*lineHeight ), COLOR_BACKGROUND, g);
		for (int i = this.logLines.size(); --i >= 0; )
		{
			String line = (String) lines[i];
			g.setColor(COLOR_SHADOW);
			g.drawString(line, 3, y + 1, Graphics.LEFT | Graphics.BOTTOM);
			g.setColor(COLOR_FONT);
			g.drawString(line, 2, y, Graphics.LEFT | Graphics.BOTTOM);
			y += lineHeight;
			if (y > screenHeight) {
				break;
			}
		}
	}



}
