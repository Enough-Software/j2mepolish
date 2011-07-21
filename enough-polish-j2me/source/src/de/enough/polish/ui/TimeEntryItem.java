//#condition polish.usePolishGui
/*
 * Created on Mar 16, 2009 at 10:23:33 AM.
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
package de.enough.polish.ui;

import java.util.Calendar;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.backgrounds.TriangleBackground;

/**
 * <p>An item allowing to adjust the time using pointer events.</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TimeEntryItem extends StringItem
{
	
	private int hour;
	private int minute;
	private Background bgArrowUp;
	private Background bgArrowDown;
	private int arrowLength;
	private int xAdjust;
	//#if polish.css.time-arrow-width
		private Dimension arrowWidth;
	//#endif

	/**
	 * 
	 * @param label 
	 * @param hour 
	 * @param minute 
	 */
	public TimeEntryItem(String label, int hour, int minute)
	{
		this(label, hour, minute, null );
	}

	
	/**
	 * @param label
	 * @param style
	 */
	public TimeEntryItem(String label, int hour, int minute, Style style)
	{
		super(label, constructText( hour, minute), INTERACTIVE, style);
		this.hour = hour;
		this.minute = minute;
	}

	/**
	 * @param hour
	 * @param minute
	 * @return
	 */
	private static String constructText(int hour, int minute)
	{
		StringBuffer buffer = new StringBuffer(5);
		if (hour < 10) {
			buffer.append(' ');
		}
		buffer.append( hour );
		buffer.append(':');
		if (minute < 10) {
			buffer.append( '0' );
		}
		buffer.append( minute );
		return buffer.toString();
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.StringItem#initContent(int, int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight)
	{
		super.initContent(firstLineWidth, availWidth, availHeight);
		int w = stringWidth("99");
		//#if polish.css.time-arrow-width
			if (this.arrowWidth != null) {
				w = this.arrowWidth.getValue( availWidth );
			}
		//#endif
		this.contentHeight += w + w;
		this.arrowLength = w;
		if (this.bgArrowUp == null) {
			this.bgArrowUp = new TriangleBackground(this.textColor, TriangleBackground.TOP);
		}
		if (this.bgArrowDown == null) {
			this.bgArrowDown = new TriangleBackground(this.textColor, TriangleBackground.BOTTOM);
		}
		int minWidth = w + w + 2;
		if (this.contentWidth < minWidth) {
			this.xAdjust = minWidth - this.contentWidth;
			this.contentWidth = minWidth;
		} else {
			this.xAdjust = 0;
		}
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.StringItem#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g)
	{
		int w = this.arrowLength;
		this.bgArrowUp.paint( x, y, w, w, g );
		this.bgArrowUp.paint( x + this.contentWidth - w, y, w, w, g );
		this.bgArrowDown.paint( x, y + this.contentHeight - w, w, w, g );
		this.bgArrowDown.paint( x + this.contentWidth - w, y + this.contentHeight - w, w, w, g );
		y += w;
		super.paintContent(x + this.xAdjust, y, leftBorder, rightBorder, g);
	}

	
	
	//#if polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TableItem#handlePointerReleased(int, int)
	 */
	protected boolean handlePointerReleased(int relX, int relY)
	{
		boolean processed = false;
		if (isInItemArea(relX, relY)) {
			int w = this.arrowLength;
			if (relY <= w) {
				if (relX <= w) {
					increaseHour();
					processed = true;
				} else if (relX >= this.contentWidth - w) {
					increaseMinute();
					processed = true;
				}
			} else if (relY >= this.contentHeight - w) {
				if (relX <= w) {
					decreaseHour();
					processed = true;
				} else if (relX >= this.contentWidth - w) {
					decreaseMinute();
					processed = true;
				}				
			}
		}
		return processed || super.handlePointerReleased(relX, relY);
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyReleased(int, int)
	 */
	protected boolean handleKeyReleased(int keyCode, int gameAction)
	{
		boolean handled = super.handleKeyReleased(keyCode, gameAction);
		if (!handled) {
			if (gameAction == Canvas.UP) {
				increaseMinute();
				handled = true;
			} else if (gameAction == Canvas.DOWN) {
				decreaseMinute();
				handled = true;
			}
		}
		return handled;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyRepeated(int, int)
	 */
	protected boolean handleKeyRepeated(int keyCode, int gameAction)
	{
		boolean handled = super.handleKeyRepeated(keyCode, gameAction);
		if (!handled) {
			if (gameAction == Canvas.UP) {
				increaseMinute();
				handled = true;
			} else if (gameAction == Canvas.DOWN) {
				decreaseMinute();
				handled = true;
			}
		}
		return handled;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction)
	{
		boolean handled = super.handleKeyPressed(keyCode, gameAction);
		if (!handled) {
			if (gameAction == Canvas.UP) {
				handled = true;
			} else if (gameAction == Canvas.DOWN) {
				handled = true;
			}
		}
		return handled;
	}
	
	private void increaseMinute()
	{
		int m = this.minute + 1;
		if (m >= 60) {
			m = 0;
			increaseHour();
		}
		this.minute = m;
		setText( constructText(this.hour, m));
	}

	
	/**
	 * 
	 */
	private void decreaseMinute()
	{
		int m = this.minute - 1;
		if (m < 0) {
			m = 59;
			decreaseHour();
		}
		this.minute = m;
		setText( constructText(this.hour, m));
	}
	

	/**
	 * 
	 */
	private void increaseHour()
	{
		int h = this.hour + 1;
		if (h >= 24) {
			h = 0;
		}
		this.hour = h;
		setText( constructText(h, this.minute));
		
	}


	/**
	 * 
	 */
	private void decreaseHour()
	{
		int h = this.hour - 1;
		if (h < 0) {
			h = 23;
		}
		this.hour = h;
		setText( constructText(h, this.minute));
	}


	public int getHour() {
		return this.hour;
	}
	
	public int getMinute() {
		return this.minute;
	}


	/**
	 * @param cal
	 */
	public void updateCalendar(Calendar cal)
	{
		//#debug
		System.out.println("setting time " + this.hour + ":" + this.minute);
		cal.set( Calendar.HOUR_OF_DAY, this.hour );
		cal.set( Calendar.MINUTE, this.minute );
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.StringItem#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle)
	{
		super.setStyle(style, resetStyle);
		if (this.bgArrowUp != null) {
			this.bgArrowUp.setStyle( style );
		}
		if (this.bgArrowDown != null) {
			this.bgArrowDown.setStyle( style );
		}
		//#if polish.css.time-arrow-width
			Dimension w = (Dimension) style.getObjectProperty("time-arrow-width");
			if (w != null) {
				this.arrowWidth = w;
			}
		//#endif
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.StringItem#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		super.setStyle(style);
		//#if polish.css.time-arrow-up
			Background bgUp = (Background) style.getObjectProperty("time-arrow-up");
			if (bgUp != null) {
				this.bgArrowUp = bgUp;
			}
		//#endif
		//#if polish.css.time-arrow-down
			Background bgDown = (Background) style.getObjectProperty("time-arrow-down");
			if (bgDown != null) {
				this.bgArrowDown = bgDown;
			}
		//#endif
	}
	
	
}
