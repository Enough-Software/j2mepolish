//#condition polish.usePolishGui
/*
 * Created on May 30, 2007 at 12:09:08 AM.
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
package de.enough.polish.ui.clockviews;

import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.ClockItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;

/**
 * <p>Visualizes the clock as a binary string, e.g. 101:111 instead of 4:11.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        May 30, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BinaryTextClockView extends ItemView {

	protected transient final Date date;
	protected transient final Calendar calendar;
	protected transient final StringItem clockText;
	protected transient long lastTimeUpdate;
	protected transient ClockItem clockItem;
	
	/**
	 * Creates a new item view.
	 */
	public BinaryTextClockView() {
		this.date = new Date();
		this.calendar = Calendar.getInstance();
		this.clockText = new StringItem( null, null );
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		long time = System.currentTimeMillis();
		if ( (this.clockItem.includeSeconds() 
				&& time/1000 > this.lastTimeUpdate/1000)
			|| (!this.clockItem.includeSeconds() 
					&& time/(1000*60) > this.lastTimeUpdate/(1000*60)) ) 
		{
			this.clockText.setText( updateTime( time ) );
			animated = true;
		}
		return animated;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) 
	{
		// cast to object is required since ClockItem officially extends javax.microedition.lcdui.CustomItem:
		this.clockItem = (ClockItem)(Object)parent; 
		this.clockText.setText( updateTime( System.currentTimeMillis() ) );
		this.contentWidth = this.clockText.getItemWidth(firstLineWidth, availWidth, availHeight);
		this.contentHeight = this.clockText.getItemHeight(firstLineWidth, availWidth, availHeight);
	}
	
	/**
	 * Updates the shown time.
	 * @param time the currently shown time
	 * @return the time as a binary string
	 */
	protected String updateTime( long time ) {
		this.lastTimeUpdate = time;
		this.date.setTime(time);
		this.calendar.setTime(this.date);
		String hour = Integer.toBinaryString( this.calendar.get( Calendar.HOUR_OF_DAY ) );
		String minute = Integer.toBinaryString( this.calendar.get( Calendar.MINUTE ) );
		String seconds = null;
		if (this.clockItem.includeSeconds()) {
			seconds = Integer.toBinaryString( this.calendar.get( Calendar.SECOND ) );
		}
		return this.clockItem.updateTime(hour, minute, seconds); 
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintContent(de.enough.polish.ui.Item, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder,
			int rightBorder, Graphics g) 
	{
		this.clockText.paint(x, y, leftBorder, rightBorder, g);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#isValid(de.enough.polish.ui.Item, de.enough.polish.ui.Style)
	 */
	protected boolean isValid(Item parent, Style style) {
		// cast to object is required since ClockItem officially extends javax.microedition.lcdui.CustomItem:
		return ((Object)parent instanceof ClockItem); 
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		this.clockText.setStyle(style);
		// remove parent background and border, so that only the clock text contains them:
		removeParentBackground();
		removeParentBorder();
	}
	
	

}
