//#condition polish.usePolishGui

/*
 * Created on 02-Jul-2006 at 15:19:49.
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
import java.util.Date;

/**
 * <p>Display the current time digitally.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        02-Jul-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ClockItem
//#if polish.LibraryBuild
	extends FakeStringCustomItem
//#else
	//# extends StringItem
//#endif
{

	private final Date date;
	private final Calendar calendar;
	private boolean includeSeconds = true;
	private boolean includeAmPm;
	private String formatStart;
	private String formatAfterHours;
	private String formatAfterMinutes;
	private String formatAfterSeconds;
	private String formatEnd;
	private long lastTimeUpdate;

	/**
	 * Creates a new clock.
	 * 
	 * @param label the label for the clock
	 */
	public ClockItem(String label) {
		this(label, null );
	}

	/**
	 * Creates a new clock.
	 * 
	 * @param label the label for the clock
	 * @param style the style for the clock string etc
	 */
	public ClockItem(String label, Style style) {
		super(label, null, style);
		this.date = new Date();
		this.calendar = Calendar.getInstance();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeStringCustomItem#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		long time = System.currentTimeMillis();
		if ( (this.includeSeconds 
				&& time/1000 > this.lastTimeUpdate/1000)
			|| (!this.includeSeconds 
					&& time/(1000*60) > this.lastTimeUpdate/(1000*60)) ) 
		{
			updateTime( time );
			animated = true;
		}
		return animated;
	}
	
	/**
	 * Determines whether seconds should be appended to this clock view.
	 * 
	 * @return true when seconds should be visualized
	 */
	public boolean includeSeconds() {
		return this.includeSeconds;
	}

	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeStringCustomItem#createCssSelector()
	 */
	protected String createCssSelector() {
		return "clockitem";
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeStringCustomItem#initContent(int, int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		updateTime( System.currentTimeMillis() );
		super.initContent(firstLineWidth, availWidth, availHeight );
	}

	private void updateTime( long time ) {
		this.lastTimeUpdate = time;
		if (this.formatStart == null) {
			// setStyle has not been called, so use default:
			this.formatStart = "";
			this.formatAfterHours = ":";
			this.formatAfterMinutes = ":";
			this.formatAfterSeconds = "";
			this.formatEnd = "";
		}
		StringBuffer buffer = new StringBuffer();
		this.date.setTime(time);
		this.calendar.setTime(this.date);
		buffer.append( this.formatStart );
		int hour = this.calendar.get( Calendar.HOUR_OF_DAY );
		boolean isPm = false;
		if (this.includeAmPm && hour > 12) {
			hour -= 12;
			isPm = true;
		}
		buffer.append( hour );
		buffer.append( this.formatAfterHours );
		int minute = this.calendar.get( Calendar.MINUTE );
		if (minute < 10) {
			buffer.append( '0' );
		} 
		buffer.append( minute  );
		buffer.append( this.formatAfterMinutes );
		if (this.includeSeconds) {
			int seconds = this.calendar.get( Calendar.SECOND );
			if (seconds < 10) {
				buffer.append('0');
			}
			buffer.append( seconds );
			
			buffer.append( this.formatAfterSeconds );
		}
		if (this.includeAmPm) {
			if (isPm) {
				buffer.append( "PM");
			} else {
				buffer.append( "AM");
			}
		}
		buffer.append( this.formatEnd );
		
		setText( buffer.toString() ); 
	}
	
	/**
	 * Fills in specific formats for time - this can be adjusted using the clock-format CSS attribute.
	 * 
	 * @param hours the hours
	 * @param minutes the minutes of the time
	 * @param seconds the seconds of the time
	 * @return the time adjusted by additional formats - e.g. "hh:mm:ss"
	 */
	public String updateTime( String hours, String minutes, String seconds ) {
		if (this.formatStart == null) {
			// setStyle has not been called, so use default:
			this.formatStart = "";
			this.formatAfterHours = ":";
			this.formatAfterMinutes = ":";
			this.formatAfterSeconds = "";
			this.formatEnd = "";
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append( this.formatStart )
			.append( hours )
			.append( this.formatAfterHours )
			.append( minutes );
		if (this.includeSeconds) {
			buffer.append( this.formatAfterMinutes )
				.append( seconds );
		}
		buffer.append( this.formatEnd );
		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeStringCustomItem#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.clock-format
			String format = style.getProperty("clock-format");
			//#debug
			System.out.println("Using clock-format: " + format);
			if (format != null) {
				this.includeAmPm = false;
				this.includeSeconds = false;
				this.formatStart = "";
				this.formatEnd = "";
				this.formatAfterHours = "";
				this.formatAfterMinutes = "";
				this.formatAfterSeconds = "";
				int hourPos = format.indexOf("hh");
				if (hourPos != -1) {
					this.formatStart = format.substring( 0, hourPos );
				} else {
					throw new IllegalArgumentException("invalid format: [" + format  + "]: no hh found.");
				}
				int minutePos = format.indexOf("mm");
				if (minutePos != -1) {
					this.formatAfterHours = format.substring( hourPos + 2, minutePos );
				} else {
					throw new IllegalArgumentException("invalid format: [" + format  + "]: no mm found.");
				}
				int amPmPos = format.indexOf("am");
				if (amPmPos == -1) {
					amPmPos = format.indexOf("pm");
				}
				int secondPos = format.indexOf("ss");
				if (secondPos != -1) {
					this.includeSeconds = true;
					this.formatAfterMinutes = format.substring( minutePos + 2, secondPos );
					if (amPmPos != -1 ) {
						this.includeAmPm = true;
						this.formatAfterSeconds = format.substring( secondPos + 2, amPmPos );
						this.formatEnd = format.substring(amPmPos + 2);
					} else {
						this.formatEnd = format.substring(secondPos + 2);
					}
				} else {					
					if (amPmPos != -1 ) {
						this.includeAmPm = true;
						this.formatAfterMinutes = format.substring( minutePos + 2, amPmPos );
						this.formatEnd = format.substring(amPmPos + 2);
					} else {
						this.formatEnd = format.substring(minutePos + 2);						
					}
				}
				
			}
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeStringCustomItem#hideNotify()
	 */
	protected void hideNotify() {
		super.hideNotify();
		AnimationThread.removeAnimationItem( this );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeStringCustomItem#showNotify()
	 */
	protected void showNotify() {
		super.showNotify();
		updateTime(System.currentTimeMillis());
		AnimationThread.addAnimationItem( this );
	}


}
 