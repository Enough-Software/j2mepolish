//#condition polish.usePolishGui
/*
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
import java.util.TimeZone;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.util.DateUtil;
import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TimePoint;

//#if polish.blackberry
	import net.rim.device.api.ui.Field;
	import net.rim.device.api.ui.FieldChangeListener;
	import net.rim.device.api.ui.UiApplication;
	import de.enough.polish.blackberry.ui.PolishDateField;
//#endif
	
//#if polish.android
	import de.enough.polish.android.midlet.MidletBridge;
//#endif
	
import de.enough.polish.calendar.CalendarItem;

/**
 * A <code>DateField</code> is an editable component for presenting date and time (calendar) information that may be placed into a <code>Form</code>. 
 * Value for this field can be initially set or left unset. If value is not set then the UI for the field
 * shows this clearly. The field value for &quot;not initialized state&quot; is not valid
 * value and <code>getDate()</code> for this state returns <code>null</code>.
 * <p>
 * Instance of a <code>DateField</code> can be configured to accept
 * date or time information
 * or both of them. This input mode configuration is done by
 * <code>DATE</code>, <code>TIME</code> or
 * <code>DATE_TIME</code> static fields of this
 * class. <code>DATE</code> input mode allows to set only
 * date information and <code>TIME</code> only time information
 * (hours, minutes). <code>DATE_TIME</code>
 * allows to set both clock time and date values.
 * <p>
 * In <code>TIME</code> input mode the date components of
 * <code>Date</code> object
 * must be set to the &quot;zero epoch&quot; value of January 1, 1970.
 * <p>
 * Calendar calculations in this field are based on default locale and defined
 * time zone. Because of the calculations and different input modes date object
 * may not contain same millisecond value when set to this field and get back
 * from this field.
 * <HR>
 * 
 * @author Robert Virkus, robert@enough.de
 * @since MIDP 1.0
 */
public class DateField extends StringItem
implements
//#if polish.DateField.useDirectInput == true || polish.Bugs.dateFieldBroken || polish.blackberry || polish.android
	//#define tmp.directInput
//#endif
//#if (!tmp.directInput || (polish.hasPointerEvents && !polish.DateField.useDirectInputForPointer)) && (polish.midp && !polish.blackberry)
	//#define tmp.useMidp
//#endif
//#if polish.DateField.useDirectInputForPointer && polish.hasPointerEvents
	//#define tmp.directInputPointer
//#endif
//#if tmp.directInput
	ItemCommandListener
//#endif
//#if tmp.directInput && (tmp.useMidp || tmp.directInputPointer) 
	,
//#endif
//#if tmp.useMidp || tmp.directInputPointer
	CommandListener
//#endif
//#if polish.blackberry
	, FieldChangeListener
//#endif
{
	/**
	 * Input mode for date information (day, month, year). With this mode this
	 * <code>DateField</code> presents and allows only to modify date
	 * value. The time
	 * information of date object is ignored.
	 * 
	 * <P>Value <code>1</code> is assigned to <code>DATE</code>.</P>
	 */
	public static final int DATE = 1;

	/**
	 * Input mode for time information (hours and minutes). With this mode this
	 * <code>DateField</code> presents and allows only to modify
	 * time. The date components
	 * should be set to the &quot;zero epoch&quot; value of January 1, 1970 and
	 * should not be accessed.
	 * 
	 * <P>Value <code>2</code> is assigned to <code>TIME</code>.</P>
	 */
	public static final int TIME = 2;

	/**
	 * Input mode for date (day, month, year) and time (minutes, hours)
	 * information. With this mode this <code>DateField</code>
	 * presents and allows to modify
	 * both time and date information.
	 * 
	 * <P>Value <code>3</code> is assigned to <code>DATE_TIME</code>.</P>
	 */
	public static final int DATE_TIME = 3;

	private Date date;
	private int inputMode;
	private TimeZone timeZone;
	private boolean showCaret;
	private int originalWidth;
	private int originalHeight;
	private long lastCaretSwitch;

	private Calendar calendar;
	
	//#if tmp.directInput
		private static final int[] DAYS_IN_MONTHS = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		private int editIndex;
		private int textComplementColor;
		private int currentField;
		private int currentFieldStartIndex;
		private ItemCommandListener additionalItemCommandListener;
	//#endif
	//#if tmp.useMidp
		private javax.microedition.lcdui.DateField midpDateField; 
		private de.enough.polish.midp.ui.Form form;
	//#endif
	//#if tmp.directInputPointer
		private CalendarItem calendarItem;
		private TimeEntryItem timeItem;
	//#endif
	//#if polish.blackberry
		private PolishDateField blackberryDateField;
		private int bbYAdjust;	
	//#endif
	//#if polish.javaplatform >= Android/1.5
		private long androidFocusedTime;
		private long androidLastInvalidCharacterTime;
	//#endif
		
	private String dateFormatPattern;
	
	//#if tmp.directInput
		private int startIndexYear;
		private int startIndexMonth;
		private int startIndexDay;
		private int startIndexHour;
		private int startIndexMinute;
	//#endif
		
		
	/**
	 * Creates a <code>DateField</code> object with the specified
	 * label and mode. This call
	 * is identical to <code>DateField(label, mode, null)</code>.
	 * 
	 * @param label item label
	 * @param mode the input mode, one of DATE, TIME or DATE_TIME
	 * @throws IllegalArgumentException if the input mode's value is invalid
	 */
	public DateField( String label, int mode)
	{
		this( label, mode, null, null );
	}

	/**
	 * Creates a <code>DateField</code> object with the specified
	 * label and mode. This call
	 * is identical to <code>DateField(label, mode, null)</code>.
	 * 
	 * @param label item label
	 * @param mode the input mode, one of DATE, TIME or DATE_TIME
	 * @param style the CSS style for this item
	 * @throws IllegalArgumentException if the input mode's value is invalid
	 */
	public DateField( String label, int mode, Style style)
	{
		this( label, mode, null, style );
	}
	
	/**
	 * Creates a date field in which calendar calculations are based
	 * on specific
	 * <code>TimeZone</code> object and the default calendaring system for the
	 * current locale.
	 * The value of the <code>DateField</code> is initially in the
	 * &quot;uninitialized&quot; state.
	 * If <code>timeZone</code> is <code>null</code>, the system's
	 * default time zone is used.
	 * 
	 * @param label item label
	 * @param mode the input mode, one of DATE, TIME or DATE_TIME
	 * @param timeZone a specific time zone, or null for the default time zone
	 * @throws IllegalArgumentException if the input mode's value is invalid
	 */
	public DateField( String label, int mode, TimeZone timeZone)
	{
		this( label, mode, timeZone, null );
	}

	/**
	 * Creates a date field in which calendar calculations are based
	 * on specific
	 * <code>TimeZone</code> object and the default calendaring system for the
	 * current locale.
	 * The value of the <code>DateField</code> is initially in the
	 * &quot;uninitialized&quot; state.
	 * If <code>timeZone</code> is <code>null</code>, the system's
	 * default time zone is used.
	 * 
	 * @param label item label
	 * @param mode the input mode, one of DATE, TIME or DATE_TIME
	 * @param timeZone a specific time zone, or null for the default time zone
	 * @param style the CSS style for this item
	 * @throws IllegalArgumentException if the input mode's value is invalid
	 */
	public DateField( String label, int mode, TimeZone timeZone, Style style)
	{
		super( label, null, INTERACTIVE, style );
		this.inputMode = mode;
		if (timeZone != null) {
			this.timeZone = timeZone;
		} else {
			this.timeZone = TimeZone.getDefault();
		}
		setDate( null );
		//#if tmp.directInput
			//#ifdef polish.i18n.useDynamicTranslations
				String clearLabel = Locale.get("polish.command.clear");
				if ( clearLabel != TextField.CLEAR_CMD.getLabel()) {
					TextField.CLEAR_CMD = new Command( Locale.get("polish.command.clear"), Command.ITEM, 2 );
				}
			//#endif
			addCommand( TextField.CLEAR_CMD );
			this.itemCommandListener = this;
		//#endif
		//#if polish.blackberry
			this.blackberryDateField = new PolishDateField( 
					Long.MIN_VALUE,
					PolishDateField.getDateFormat( getDateFormatPattern() ),
					PolishDateField.getStyle(this.inputMode) );
//					PolishDateField.getDateFormat( mode, this.timeZone ),
//					PolishDateField.EDITABLE );
			this.blackberryDateField.setTimeZone(this.timeZone);
			if (this.style != null) {
				this.blackberryDateField.setStyle( this.style );
			}
			this.blackberryDateField.setChangeListener( this );
			this._bbField = this.blackberryDateField;
		//#endif
		//#if tmp.directInput
			setDateFormatPattern( getDateFormatPattern() );
		//#endif
	}
	
	
	/**
	 * Returns date value of this field. Returned value is
	 * <code>null</code> if field  value is not initialized. 
	 * The date object is constructed according the rules of
	 * locale specific calendaring system and defined time zone.
	 * 
	 * In <code>TIME</code> mode field the date components are set to
	 * the &quot;zero
	 * epoch&quot; value of January 1, 1970. If a date object that presents time
	 * beyond one day from this &quot;zero epoch&quot; then this field
	 * is in &quot;not initialized&quot; state and this method returns <code>null</code>.
	 * 
	 * In <code>DATE</code> mode field the time component of the calendar is set
	 * to zero when
	 * constructing the date object.
	 * 
	 * @return date object representing time or date depending on input mode
	 * @see #setDate(java.util.Date)
	 */
	public Date getDate()
	{
		//#if tmp.directInput
			if (this.isFocused && (this.date != null)) {
				checkOutDate();
			}
		//#endif
		return this.date;
	}

	/**
	 * Sets a new value for this field. <code>null</code> can be
	 * passed to set the field
	 * state to &quot;not initialized&quot; state. The input mode of
	 * this field defines
	 * what components of passed <code>Date</code> object is used.<p>
	 * 
	 * In <code>TIME</code> input mode the date components must be set
	 * to the &quot;zero
	 * epoch&quot; value of January 1, 1970. If a date object that presents time
	 * beyond one day then this field is in &quot;not initialized&quot; state.
	 * In <code>TIME</code> input mode the date component of
	 * <code>Date</code> object is ignored and time
	 * component is used to precision of minutes.<p>
	 * 
	 * In <code>DATE</code> input mode the time component of
	 * <code>Date</code> object is ignored.<p>
	 * 
	 * In <code>DATE_TIME</code> input mode the date and time
	 * component of <code>Date</code> are used but
	 * only to precision of minutes.
	 * 
	 * @param date new value for this field
	 * @see #getDate()
	 */
	public void setDate( Date date)
	{
		if ( date != null && this.inputMode == TIME) {
			// check if the year-month-day is set to zero (so that the date starts at 1970-01-01)
			// 1 day =
			// 1000 // == 1 sec
			// * 60 // == 1 min
			// * 60 // == 1 hour 
			// * 24 // == 1 day
			// = 86.400.000 ==  0x5265C00
			if ( date.getTime() > 86400000 ) {
				if (this.calendar == null) {
					this.calendar = Calendar.getInstance();
					this.calendar.setTimeZone(this.timeZone);
				}
				this.calendar.setTime(date);
				long timeOnly = this.calendar.get( Calendar.MILLISECOND )
					+ this.calendar.get( Calendar.SECOND ) * 1000
					+ this.calendar.get( Calendar.MINUTE ) * 60 * 1000
					+ this.calendar.get( Calendar.HOUR_OF_DAY ) * 60 * 60 * 1000;
				date.setTime(timeOnly);
				//System.out.print("Adjusting date from " + this.calendar + " to " );
				//this.calendar.setTime(date);
				//System.out.println( this.calendar );
			}
		}
		//#if polish.blackberry
			if (this.blackberryDateField != null && date != this.date ) {
				Object bbLock = UiApplication.getEventLock();
				synchronized (bbLock) {
					if (date != null) {
						this.blackberryDateField.setDate( date.getTime() );
					} else {
						this.blackberryDateField.setDate( Long.MIN_VALUE );
					}
				}
			}
		//#endif
			
		this.date = date;
		//#if tmp.useMidp
		if (this.midpDateField != null) {
			this.midpDateField.setDate( date );
		}
		//#endif
		// set date:
		if (date == null ) {
			if (this.inputMode == DATE || this.inputMode == DATE_TIME) {
				//#if polish.i18n.useDynamicTranslations
					this.text = Locale.get("polish.DateFormatEmptyText");
				//#elif polish.DateFormatEmptyText:defined
					//#= this.text = "${polish.DateFormatEmptyText}";
				//#elif polish.DateFormat == us || polish.DateFormat == mdy
					this.text = "MM-DD-YYYY";
				//#elif polish.DateFormat == de
					this.text = "TT.MM.JJJJ";
				//#elif polish.DateFormat == fr
					this.text = "JJ/MM/AAAA";
				//#elif polish.DateFormat == dmy
					this.text = "DD-MM-YYYY";
				//#else
					// default to ymd
					this.text = "YYYY-MM-DD";
				//#endif
				if (this.inputMode == DATE_TIME) {
					this.text += " hh:mm";
				}
			} else if (this.inputMode == TIME) {
				this.text = "hh:mm";
			}
		} else {
			if (this.calendar == null) {
				this.calendar = Calendar.getInstance();
				this.calendar.setTimeZone(this.timeZone);
			}
			this.calendar.setTime(date);
			this.text = Locale.formatDate(this.calendar, getDateFormatPattern() );
			/*
			StringBuffer buffer = new StringBuffer(10);
			if ((this.inputMode == DATE) || (this.inputMode == DATE_TIME)) {
				int year = this.calendar.get(Calendar.YEAR);
				int month = this.calendar.get( Calendar.MONTH );
				int day = this.calendar.get( Calendar.DAY_OF_MONTH );
				//#if polish.DateFormat == us
					if (month < 9) {
						buffer.append('0');
					}
					buffer.append( ++month )
				          .append("-");
					if (day < 10) {
						buffer.append( '0' );
					}
					buffer.append( day )
						  .append("-");
					if (year < 10) {
						buffer.append( "000");
					} else if (year < 100) {
						buffer.append( "00");
					} else if (year < 1000) {
						buffer.append( "0");
					}
					buffer.append( year );
				//#elif polish.DateFormat == de
					if (day < 10) {
						buffer.append( '0' );
					}
					buffer.append( day )
						  .append(".");
					if (month < 9) {
						buffer.append('0');
					}
					buffer.append( ++month )
				          .append(".");
					if (year < 10) {
						buffer.append( "000");
					} else if (year < 100) {
						buffer.append( "00");
					} else if (year < 1000) {
						buffer.append( "0");
					}
					buffer.append( year );
				//#elif polish.DateFormat == fr
					if (day < 10) {
						buffer.append( '0' );
					}
					buffer.append( day )
						.append("/");
					if (month < 9) {
						buffer.append('0');
					}
					buffer.append( ++month )
					    .append("/");
					if (year < 10) {
						buffer.append( "000");
					} else if (year < 100) {
						buffer.append( "00");
					} else if (year < 1000) {
						buffer.append( "0");
					}
					buffer.append( year );
				//#elif polish.DateFormat == mdy
					if (month < 9) {
						buffer.append('0');
					}
					buffer.append( ++month )
					//#if polish.DateFormatSeparator:defined
						//#= .append("${polish.DateFormatSeparator}");
					//#else
					        .append("-");
					//#endif
					if (day < 10) {
						buffer.append( '0' );
					}
					buffer.append( day )
					//#if polish.DateFormatSeparator:defined
						//#= .append("${polish.DateFormatSeparator}");
					//#else
					        .append("-");
					//#endif
					if (year < 10) {
						buffer.append( "000");
					} else if (year < 100) {
						buffer.append( "00");
					} else if (year < 1000) {
						buffer.append( "0");
					}
					buffer.append( year );
				//#elif polish.DateFormat == dmy
					if (day < 10) {
						buffer.append( '0' );
					}
					buffer.append( day )
					//#if polish.DateFormatSeparator:defined
						//#= .append("${polish.DateFormatSeparator}");
					//#else
					        .append("-");
					//#endif
					if (month < 9) {
						buffer.append('0');
					}
					buffer.append( ++month )
					//#if polish.DateFormatSeparator:defined
						//#= .append("${polish.DateFormatSeparator}");
					//#else
					        .append("-");
					//#endif
					if (year < 10) {
						buffer.append( "000");
					} else if (year < 100) {
						buffer.append( "00");
					} else if (year < 1000) {
						buffer.append( "0");
					}
					buffer.append( year );
				//#else
					// default to YMD
					if (year < 10) {
						buffer.append( "000");
					} else if (year < 100) {
						buffer.append( "00");
					} else if (year < 1000) {
						buffer.append( "0");
					}
					buffer.append( year )
					//#if polish.DateFormatSeparator:defined
						//#= .append("${polish.DateFormatSeparator}");
					//#else
					        .append("-");
					//#endif
					if (month < 9) {
						buffer.append('0');
					}
					buffer.append( ++month )
					//#if polish.DateFormatSeparator:defined
						//#= .append("${polish.DateFormatSeparator}");
					//#else
					        .append("-");
					//#endif
					if (day < 10) {
						buffer.append( '0' );
					}
					buffer.append( day );
				//#endif
				if  (this.inputMode == DATE_TIME) {
					buffer.append(' ');
				}
			}
			if ((this.inputMode == TIME) || (this.inputMode == DATE_TIME)) {
				int hour = this.calendar.get( Calendar.HOUR_OF_DAY );
				if (hour < 10) {
					buffer.append('0');
				}
				buffer.append( hour )
				      .append(':');
				int minute = this.calendar.get( Calendar.MINUTE );
				if (minute < 10) {
					buffer.append('0');
				}
				buffer.append( minute );
			}
			this.text = buffer.toString();
			*/
		} // date != null
		if (isInitialized()) {
			this.isTextInitializationRequired = true;
			setInitialized(false);
		}
		repaint();
	}
	
	/**
	 * Sets the date of this DateField. 
	 * @param date the date
	 * @see #setDate(Date)
	 */
	public void setTimePoint( TimePoint date ) {
		setDate( date.getAsDate() );
	}
	
	/**
	 * Sets the date format pattern.
	 * The pattern can consist of arbitrary characters but following characters have a meaning:
	 * <ul>
	 *  <li><b>yyyy</b>: The year in 4 digits, e.g. 2010.</li>
	 *  <li><b>MM</b>: the month as a numerical value between 1 (January) and 12 (December).</li>
	 *  <li><b>dd</b>: the day of the month as a numerical value between 1 and 31.</li>
	 *  <li><b>HH</b>: the hour of the day between 0 and 23.</li>
	 *  <li><b>mm</b>: the minute of the hour between 0 and 59.</li>
	 * </ul>
	 * @param pattern the pattern, e.g. "yyyy-MM-dd HH:mm"
	 */
	public void setDateFormatPattern( String pattern ) {
		this.dateFormatPattern = pattern;
		//#if polish.blackberry
			this.blackberryDateField.setDateFormatPattern( pattern );
		//#endif
		//#if tmp.directInput
			this.startIndexYear = pattern.indexOf("yyyy");
			this.startIndexMonth = pattern.indexOf("MM");
			this.startIndexDay = pattern.indexOf("dd");
			this.startIndexHour = pattern.indexOf("HH");
			this.startIndexMinute = pattern.indexOf("mm");
		//#endif
	}
	
	/**
	 * Retrieves the date format pattern for this date field
	 * @return the pattern, e.g. "yyyy-MM-dd HH:mm"
	 * @see #setDateFormatPattern(String)
	 */
	public String getDateFormatPattern() {
		if (this.dateFormatPattern == null) {
			this.dateFormatPattern = createDefaultDateFormatPattern();
		}
		return this.dateFormatPattern;
	}

	/**
	 * Creates the default pattern for this datefield.
	 * @return the default date format pattern that depends on the polish.DateFormat and polish.DateFormatSeparator preprocessing variables.
	 */
	protected String createDefaultDateFormatPattern() {
		if (this.inputMode == TIME) {
			return "HH:mm";
		} else  {			
			String defaultPattern = Locale.getDefaultDateFormatPattern();
			if (this.inputMode == DATE_TIME &&  defaultPattern.indexOf("HH") == -1) {
				defaultPattern += " HH:mm";
			}
			return defaultPattern;
		}
	}

	/**
	 * Gets input mode for this date field. Valid input modes are
	 * <code>DATE</code>, <code>TIME</code> and <code>DATE_TIME</code>.
	 * 
	 * @return input mode of this field
	 * @see #setInputMode(int)
	 */
	public int getInputMode()
	{
		return this.inputMode;
	}

	/**
	 * Set input mode for this date field. Valid input modes are
	 * <code>DATE</code>, <code>TIME</code> and <code>DATE_TIME</code>.
	 * 
	 * @param mode the input mode, must be one of DATE, TIME or DATE_TIME
	 * @throws IllegalArgumentException if an invalid value is specified
	 * @see #getInputMode()
	 */
	public void setInputMode(int mode)
	{
		//#if polish.blackberry
			if (this.blackberryDateField != null && mode != this.inputMode) {
				this.blackberryDateField.setInputMode( mode );
			}
		//#endif
		this.inputMode = mode;
		//#if tmp.useMidp
		if (this.midpDateField != null) {
			this.midpDateField.setInputMode(mode);
		}
		//#endif
		setDate( this.date ); 
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paint(int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		//#if polish.blackberry
			if (getScreen().isNativeUiShownFor(this)) {
        		y -= this.bbYAdjust;
				this.blackberryDateField.setPaintPosition( x + g.getTranslateX(), y + g.getTranslateY() );
			} else {
				super.paintContent(x, y, leftBorder, rightBorder, g);
			}
		//#else
	    	super.paintContent(x, y, leftBorder, rightBorder, g);
		//#endif
		//#if tmp.directInput && !polish.blackberry
			if ( this.isFocused ) {
				String head = this.text.substring( 0, this.editIndex );
				int headWidth = stringWidth( head );
				char editChar = this.text.charAt( this.editIndex );
				int editWidth = charWidth( editChar );
				if ( this.isLayoutCenter ) {
					int centerX = leftBorder + (rightBorder - leftBorder) / 2;
					//#ifdef polish.css.text-horizontal-adjustment
						centerX += this.textHorizontalAdjustment;
					//#endif
					int completeWidth = stringWidth( this.text );
					x = centerX - ( completeWidth / 2 );
				} else if ( this.isLayoutRight ) {
					int completeWidth = stringWidth( this.text );
					x = rightBorder - completeWidth;					
				}
				g.fillRect( x + headWidth - 1, y  - 1, editWidth + 1, getFontHeight() );
				
				if (this.showCaret) {
					g.setColor( this.textComplementColor );
					//#if polish.css.text-effect
						if (this.textEffect != null) {
							this.textEffect.drawChar(editChar, x + headWidth, y + getFontHeight(), Graphics.BOTTOM | Graphics.LEFT, g );
						} else {
					//#endif
							g.drawChar( editChar, x + headWidth, y + getFontHeight(), Graphics.BOTTOM | Graphics.LEFT );
					//#if polish.css.text-effect
						}
					//#endif
				}
			}
		//#elif !polish.blackberry
			if (this.showCaret) {
				if (this.text == null) {
					// when the text is null the appropriate font and color
					// might not have been set, so set them now:
					g.setFont( this.font );
					g.setColor( this.textColor );
				}
				if (this.isLayoutCenter) {
					int centerX = leftBorder 
						+ (rightBorder - leftBorder) / 2 
						+ this.originalWidth / 2
						+ 2;
					if (this.originalHeight > 0) {
						y += this.originalHeight - getFontHeight();
					}
					g.drawChar('|', centerX, y, Graphics.TOP | Graphics.LEFT );
				} else {
					x += this.originalWidth + 2;
					if (this.originalHeight > 0) {
						y += this.originalHeight - getFontHeight();
					}
					//#if polish.css.text-effect
						if (this.textEffect != null) {
							this.textEffect.drawChar('|', x, y, Graphics.TOP | Graphics.LEFT, g );
						} else {
					//#endif
							g.drawChar('|', x, y, Graphics.TOP | Graphics.LEFT );
					//#if polish.css.text-effect
						}
					//#endif
				}
			}
		//#endif
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem()
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		if (this.date == null) {
			setDate( null );
		}
		// init StringItem:
		super.initContent(firstLineWidth, availWidth, availHeight);
				
		this.originalWidth = this.contentWidth;
		this.originalHeight = this.contentHeight;
		//#if polish.blackberry
			if (!this.isFocused) {
				return;
			}
			// setFont() triggers a re-layout of the BlackBerry manager, so we need to prepare
			// some position settings here (at least on 4.6 this code is wrong):
//			int contX = this.contentX;
//			if (this.label != null && this.label.itemWidth + this.contentWidth <= availWidth) {
//				contX += this.label.itemWidth;
//			}
			if (this.style != null) {
				this.blackberryDateField.setStyle( this.style );
			}
			// allow extra pixels for the cursor:
			int w = this.contentWidth+2;
			//#if polish.blackberry && polish.hasPointerEvents
				//this is needed to open the native DateField-Keyboard from the whole content
				w = availWidth;
			//#endif
			this.blackberryDateField.doLayout( w, this.contentHeight );
			this.bbYAdjust = (this.blackberryDateField.getExtent().height - this.contentHeight) / 2;
			//System.out.println("TextField: editField.getText()="+ this.editField.getText() );
		//#endif			
	}

	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getCssSelector()
	 */
	protected String createCssSelector() {
		return "datefield";
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if tmp.directInput
			this.textComplementColor = DrawUtil.getComplementaryColor( this.textColor );
		//#endif
	}
	
	//#if !polish.blackberry
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate()
	 */
	public boolean animate() {
		long currentTime = System.currentTimeMillis();
		if ( currentTime - this.lastCaretSwitch > 500 ) {
			this.lastCaretSwitch = currentTime;
			this.showCaret = ! this.showCaret;
			return true;
		} else {
			return false;
		}
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		super.defocus(originalStyle);
		this.showCaret = false;
		//#if tmp.directInput
			if (this.date != null) {
				checkOutDate();
			}
		//#endif
		//#if polish.blackberry
			this.blackberryDateField.focusRemove();
		//#endif
	}
	
	
	private void checkOutDate() {
		//#if tmp.directInput && !polish.blackberry
			String current = this.text;
			int start = 0;
			this.currentField = 0;
			while (start < this.text.length()) {
				int end = getNextDateTimeEnd(start);
				this.editIndex = end - 1;
				this.currentFieldStartIndex = start;
				checkField( end, false);
				this.text = current;
				this.currentField++;
				start = end + 1;
			}
			this.currentField = 0;
			this.currentFieldStartIndex = 0;
			this.editIndex = 0;
		//#endif
	}

	private int getNextDateTimeEnd(int start) {
		for (int i=start; i<this.text.length(); i++) {
			char c = this.text.charAt(i);
			if (!Character.isDigit(c) && i>start) {
				return i;
			}
		}
		return this.text.length();
	}

	//#if polish.javaplatform >= Android/1.5
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style newStyle, int direction) {
		if (this.isShown) {
			MidletBridge.instance.showSoftKeyboard();
			this.androidFocusedTime = System.currentTimeMillis();
		}
		return super.focus(newStyle, direction);
	}
	//#endif
	
	//#if polish.javaplatform >= Android/1.5
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.StringItem#showNotify()
	 */
	protected void showNotify() {
		if (this.isFocused) {
			MidletBridge.instance.showSoftKeyboard();
		}
		super.showNotify();
	}
	//#endif


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
	 */
	protected synchronized boolean handleKeyPressed(int keyCode, int gameAction) {
		//#if polish.blackberry
			if (true) {
				return false;
			}
		//#endif
		//#if tmp.directInput
			// there are several possible situations:
			// backspace: delete last char, move internal focus left
			// left, right: move internal focus left/right
			// input of number: a) plausibility check, b) move internal focus right
			// up, down: exit editing mode, set default values.
			// 		parse time/date and so on.

			int foundNumber = -1;

			//#if polish.TextField.numerickeys.1:defined
				char insertChar = (char) (' ' + (keyCode - 32)); 
				String numericKeyStr = null;
				//#= numericKeyStr = "${polish.TextField.numerickeys.1}";
				if (numericKeyStr.indexOf(insertChar) != -1) {
					foundNumber = 1;
				}
				//#= numericKeyStr = "${polish.TextField.numerickeys.2}";
				if (numericKeyStr.indexOf(insertChar) != -1) {
					foundNumber = 2;
				}
				//#= numericKeyStr = "${polish.TextField.numerickeys.3}";
				if (numericKeyStr.indexOf(insertChar) != -1) {
					foundNumber = 3;
				}
				//#= numericKeyStr = "${polish.TextField.numerickeys.4}";
				if (numericKeyStr.indexOf(insertChar) != -1) {
					foundNumber = 4;
				}
				//#= numericKeyStr = "${polish.TextField.numerickeys.5}";
				if (numericKeyStr.indexOf(insertChar) != -1) {
					foundNumber = 5;
				}
				//#= numericKeyStr = "${polish.TextField.numerickeys.6}";
				if (numericKeyStr.indexOf(insertChar) != -1) {
					foundNumber = 6;
				}
				//#= numericKeyStr = "${polish.TextField.numerickeys.7}";
				if (numericKeyStr.indexOf(insertChar) != -1) {
					foundNumber = 7;
				}
				//#= numericKeyStr = "${polish.TextField.numerickeys.8}";
				if (numericKeyStr.indexOf(insertChar) != -1) {
					foundNumber = 8;
				}
				//#= numericKeyStr = "${polish.TextField.numerickeys.9}";
				if (numericKeyStr.indexOf(insertChar) != -1) {
					foundNumber = 9;
				}
				//#= numericKeyStr = "${polish.TextField.numerickeys.0}";
				if (numericKeyStr.indexOf(insertChar) != -1) {
					foundNumber = 0;
				}
			//#endif
				
			int clearKey =
				//#if polish.key.ClearKey:defined
					//#= ${polish.key.ClearKey};
				//#else
					-8;
				//#endif
			//#if polish.javaplatform >= Android/1.5
				if (keyCode == clearKey) {
					long invalidCharInputTime = this.androidLastInvalidCharacterTime;
					if (invalidCharInputTime != 0) {
						this.androidLastInvalidCharacterTime = 0;
						if (invalidCharInputTime - System.currentTimeMillis() <= 10 * 1000) {
							// consume clear, when the last invalid input is less then 10 seconds ago:
							return true;
						}
					}
				}
			//#endif

			// check for input of numbers 
			if ( ( keyCode >= Canvas.KEY_NUM0 && keyCode <= Canvas.KEY_NUM9 )
				 || foundNumber != -1)
			{
				if (this.date == null) {
					setDate( DateUtil.getCurrentTimeZoneDate() );
				}
				char c;
				if (foundNumber != -1) {
					c = Integer.toString( foundNumber ).charAt( 0 );
				} else {
					c = Integer.toString( keyCode - Canvas.KEY_NUM0 ).charAt( 0 );
				}
				String newText = this.text.substring( 0, this.editIndex ) + c;
				if ( this.editIndex < this.text.length() -1 ) {
					newText += this.text.substring( this.editIndex + 1 );
				}
				setText( newText );
				moveForward(true);
			} else if ( this.date != null && (gameAction == Canvas.LEFT || keyCode == clearKey)) {
				moveBackward(true);
			} else if ( this.date != null && gameAction == Canvas.RIGHT ) {
				moveForward(true);
			} else if (gameAction != Canvas.FIRE){
				//#if polish.javaplatform >= Android/1.5
					this.androidLastInvalidCharacterTime = System.currentTimeMillis();
				//#else
					// force check before leaving this date=-field:
					if (this.date != null) {
						moveForward(true);
						this.currentField = 0;
						this.currentFieldStartIndex = 0;
						this.editIndex = 0;
					}
				//#endif
				return false;
			} 

		//#else
			if ( (keyCode >= Canvas.KEY_NUM0 && keyCode <= Canvas.KEY_NUM9) || getScreen().isGameActionFire(keyCode, gameAction) ) 
			{
				showDateForm();
			} else {
				return false;
			}
		//#endif
		return true;
	}
	
	protected boolean handleKeyReleased(int keyCode, int gameAction) {
		int clearKey =
			//#if polish.key.ClearKey:defined
				//#= ${polish.key.ClearKey};
			//#else
				-8;
			//#endif
		
		if(keyCode == clearKey)
		{
			//handled in handleKeyPressed so just return true here
			return true;
		}
		
		boolean handled = super.handleKeyReleased(keyCode, gameAction);
		//#if tmp.directInputPointer
			if (!handled && gameAction == Canvas.FIRE && keyCode != Canvas.KEY_NUM5) {
				showPolishCalendar();
			}
		//#endif
		return handled;
	}

	//#if tmp.directInput
	private void moveBackward(boolean notifyStateListener) {
		if (this.date == null) {
			return;
		}
		int forwardIndex = this.editIndex;
		while (Character.isDigit( this.text.charAt(forwardIndex))) {
			forwardIndex++;
			if (forwardIndex == this.text.length() ) {
				forwardIndex = 0;
				break;
			}
		}
		
		int newIndex = this.editIndex -1;
		while (true) {
			if ( newIndex < 0 ) {
				checkField( forwardIndex, notifyStateListener );
				newIndex = this.text.length() - 1;
				if (this.inputMode == DATE) {
					this.currentField = 2;
				} else if ( this.inputMode == TIME ) {
					this.currentField = 1;
				} else {
					this.currentField = 4;
				}
			}
			char c = this.text.charAt( newIndex );
			// break when the character is a digit:
			if ( Character.isDigit(c) ) {
				break;
			}
			checkField( forwardIndex, notifyStateListener );
			this.currentField--;
			newIndex--;
		}
		this.editIndex = newIndex;
		// now find the correct start-index for the current field:
		if (newIndex == 0) {
			this.currentFieldStartIndex = 0;
		} else {
			while ( Character.isDigit( this.text.charAt( newIndex )) ) {
				newIndex--;
				if (newIndex == -1) {
					break;
				}
			}
			this.currentFieldStartIndex = newIndex + 1;
		}
	}
	//#endif

	//#if tmp.directInput
	private void moveForward(boolean notifyStateListener) {
		int newIndex = this.editIndex + 1;
		while (true) {
			if ( newIndex >= this.text.length() ) {
				newIndex = 0;
				checkField( newIndex, notifyStateListener );
				this.currentField = 0;
				this.currentFieldStartIndex = 0;
			}
			char c = this.text.charAt( newIndex );
			// break when the character is a digit:
			if ( Character.isDigit(c) ) {
				break;
			}
			// okay, we're entering a new field, so check the value of the last field now:
			checkField( newIndex, notifyStateListener );
			newIndex++;
			this.currentFieldStartIndex = newIndex;
			this.currentField++;
		}
		this.editIndex = newIndex;
	}
	//#endif
	
	//#if tmp.directInput
	private void checkField(int newIndex, boolean notifyStateListener) {
		String fieldStr;
		if ( newIndex == 0 ) {
			fieldStr = this.text.substring( this.currentFieldStartIndex );
		} else {
			fieldStr = this.text.substring( this.currentFieldStartIndex, newIndex );
		}
		int fieldValue;
		try {
			fieldValue = Integer.parseInt( fieldStr );
		} catch (NumberFormatException e) {
			return;
		}
		
		// check the actual value:
		if (this.calendar == null) {
			this.calendar = Calendar.getInstance();
			this.calendar.setTimeZone(this.timeZone);
		}
		this.calendar.setTime( this.date );
		int calendarField = -1;
		
		if ((this.inputMode == DATE) || (this.inputMode == DATE_TIME)) {
			//#if (polish.DateFormat == us) || (polish.DateFormat == mdy) 
				switch (this.currentField) {
				case 0: calendarField = Calendar.MONTH; break;
				case 1: calendarField = Calendar.DAY_OF_MONTH; break;
				case 2: calendarField =  Calendar.YEAR; break;
				}
			//#elif (polish.DateFormat == de) || (polish.DateFormat == fr) || (polish.DateFormat == dmy)
				switch (this.currentField) {
				case 0: calendarField = Calendar.DAY_OF_MONTH; break;
				case 1: calendarField = Calendar.MONTH; break;
				case 2: calendarField =  Calendar.YEAR; break;
				}
			//#else
				switch (this.currentField) {
				case 0: calendarField =  Calendar.YEAR; break;
				case 1: calendarField = Calendar.MONTH; break;
				case 2: calendarField = Calendar.DAY_OF_MONTH; break;
				}
			//#endif
		} 
		if ( this.inputMode == DATE_TIME ) {
			switch (this.currentField) {
			case 3: calendarField =  Calendar.HOUR_OF_DAY; break;
			case 4: calendarField = Calendar.MINUTE; break;
			}

		} else if ( this.inputMode == TIME ) {
			switch (this.currentField) {
			case 0: calendarField =  Calendar.HOUR_OF_DAY; break;
			case 1: calendarField = Calendar.MINUTE; break;
			}
		}
		
		if ( calendarField == -1 ) {
			//#debug warn
			System.out.println("Unable to to set field [" + this.currentField + "]: unknown index.");
			return;
		} else {
			switch (calendarField) {
			case Calendar.DAY_OF_MONTH:
				if ( fieldValue < 1 ) {
					fieldValue = 1;
				} else {
					int month = this.calendar.get( Calendar.MONTH );
					int maxDay = DAYS_IN_MONTHS[ month ];
					if ( fieldValue > maxDay ) {
						fieldValue = maxDay;
					}
				}
				break;
			case Calendar.MONTH:
				if (fieldValue < 1) {
					fieldValue = 1;
				} else if ( fieldValue > 12 ) {
					fieldValue = 12;
				}
				fieldValue--;
				break;
			case Calendar.HOUR_OF_DAY:
				if (fieldValue == 24) {
					fieldValue = 0;
				} else if (fieldValue > 24) {
					fieldValue = 23;
				}
				break;
			case Calendar.MINUTE:
				if (fieldValue > 59) {
					fieldValue = 59;
				}
				break;
			}
			this.calendar.set( calendarField, fieldValue );
			boolean changed = false;
			try {
				Date newDate = this.calendar.getTime();
				setDate( newDate );
				changed = true;
			} catch (IllegalArgumentException e) {
				if (calendarField ==  Calendar.DAY_OF_MONTH ||  calendarField == Calendar.MONTH ) {
					int month = calendarField == Calendar.MONTH ? fieldValue : this.calendar.get( Calendar.MONTH );
					int day = calendarField == Calendar.DAY_OF_MONTH ? fieldValue : this.calendar.get( Calendar.DAY_OF_MONTH );
					int maxDay = DAYS_IN_MONTHS[ month ];
					if ( month == 2 ) {
						maxDay = 28;
					}
					if ( day > maxDay ) {
						this.calendar.set( Calendar.DAY_OF_MONTH, maxDay );
						try {
							setDate( this.calendar.getTime() );
							changed = true;
						} catch ( IllegalArgumentException e2 ) {
							// ignore
						}
					}
				}
				//#if polish.debug.error
				if (!changed) {
					//#debug error
					System.out.println("Unable to set date: " + e );
				}
				//#endif
			}
			if ( notifyStateListener && changed ) {
				notifyStateChanged();
			}
		}
		
		//System.out.println("Field [" + this.currentField + "]/[Calendar." + calendarField + "] is: " + fieldStr );

	}
	//#endif

	//#if tmp.useMidp
	/**
	 * Shows the TextBox for entering texts.
	 */
	private void showDateForm() {
		if (this.midpDateField == null) {
			this.midpDateField = new javax.microedition.lcdui.DateField( getLabel(), this.inputMode, this.timeZone );
			//#ifdef polish.Bugs.dateFieldAcceptsNoNullDate
				if (this.date == null) {
					if (this.inputMode == TIME) {
						this.midpDateField.setDate( new Date(0) );
					} else {
						this.midpDateField.setDate( new Date() );
					}
				} else {
					this.midpDateField.setDate( this.date );
				}
			//#else
				this.midpDateField.setDate( this.date );
			//#endif
			this.form = new de.enough.polish.midp.ui.Form( StyleSheet.currentScreen.getTitle() );
			this.form.append( this.midpDateField );
			//TODO add i18n support
			this.form.addCommand(StyleSheet.OK_CMD);
			this.form.addCommand(StyleSheet.CANCEL_CMD);
			this.form.setCommandListener( this );
		}
		this.screen = StyleSheet.currentScreen;
		Display.getInstance().setCurrent( this.form );
	}
	//#endif

	//#if tmp.useMidp || tmp.directInputPointer
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable box) {
		if (cmd == StyleSheet.CANCEL_CMD) {
			//#if !tmp.directInputPointer && tmp.useMidp 
				this.midpDateField.setDate( this.date );
			//#endif
		} else {
			//#if tmp.directInputPointer
				Calendar cal;
				if (this.inputMode != TIME) {
					cal = this.calendarItem.getSelectedCalendar();
				} else {
					cal = Calendar.getInstance();
				}
				if (this.inputMode != DATE) {
					this.timeItem.updateCalendar( cal );
				}
				setDate( cal.getTime() );
			//#else
				setDate( this.midpDateField.getDate() );
			//#endif
			notifyStateChanged();
		}
		Display.getInstance().setCurrent( this.screen );
	}
	//#endif
	
	//#ifdef tmp.directInput
	public void setItemCommandListener(ItemCommandListener l) {
		this.additionalItemCommandListener = l;
	}
	//#endif


	//#if tmp.directInput
	public void commandAction(Command c, Item item) {
		if (c == TextField.CLEAR_CMD) {
			this.editIndex = 0;
			this.currentField = 0;
			this.currentFieldStartIndex = 0;
			setDate( null );
		} else if (this.additionalItemCommandListener != null) {
			this.additionalItemCommandListener.commandAction(c, item);
		}
	}
	//#endif

	//#if polish.hasPointerEvents && (tmp.useMidp || tmp.directInputPointer || polish.javaplatform >= Android/1.5)
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerReleased(int, int)
	 */
	protected boolean handlePointerReleased(int relX, int relY)
	{
		if (isInItemArea( relX, relY )) {
			//#if tmp.directInputPointer
				showPolishCalendar();
			//#elif tmp.useMidp 
				showDateForm();
			//#elif polish.javaplatform >= Android/1.5
				if (this.isFocused && ((System.currentTimeMillis() - this.androidFocusedTime) > 200)) {
					MidletBridge.instance.toggleSoftKeyboard();
					return true;
				}
			//#endif
			return true;
		}
		return super.handlePointerReleased(relX, relY);
	}
	//#endif

	//#if tmp.directInputPointer
	/**
	 * 
	 */
	private void showPolishCalendar() {
		Calendar cal = Calendar.getInstance();
		if (this.date != null) {
			cal.setTime( this.date );
		}
		//#style calendarForm?
		Form calForm = new Form( getLabel() );
		calForm.addCommand( StyleSheet.OK_CMD );
		calForm.addCommand( StyleSheet.CANCEL_CMD );
		calForm.setCommandListener( this );
		if (this.inputMode != TIME) {
			//#style calendar?
			CalendarItem item = new CalendarItem(cal);
			this.calendarItem = item;
			calForm.append( item );
		}
		if (this.inputMode != DATE) {
			//#style time?
			TimeEntryItem time = new TimeEntryItem( null, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE) );
			calForm.append( time );
			this.timeItem = time;
		}
		Display.getInstance().setCurrent( calForm );
	}
	//#endif

	//#if polish.blackberry
	public void fieldChanged(Field field, int context) {
		if (context != FieldChangeListener.PROGRAMMATIC && isInitialized() ) {
			setDate( new Date( this.blackberryDateField.getDate()) );
			Screen scr = getScreen();
			if (scr != null) {
				scr.lastInteractionTime = System.currentTimeMillis();
			}
			notifyStateChanged();
		}
	}
	//#endif
}
