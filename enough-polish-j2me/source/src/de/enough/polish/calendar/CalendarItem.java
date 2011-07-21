//#condition polish.usePolishGui
/*
 * Created on Mar 15, 2009 at 12:29:46 PM.
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
package de.enough.polish.calendar;

import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.Canvas;

import de.enough.polish.ui.Command;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TableItem;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.TextUtil;
import de.enough.polish.util.TimePeriod;
import de.enough.polish.util.TimePoint;

/**
 * <p>Displays a calendar for a specific month</p>
 *
 * <p>Copyright Enough Software 2009-2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CalendarItem extends TableItem
{
	
	
	/**
	 * Show mode for displaying the current month and year within the label of this CalendarItem.
	 */
	public static final int SHOW_MODE_LABEL = 0;
	/**
	 * Show mode for displaying the current month and year within the title of this CalendarItem's screen.
	 */
	public static final int SHOW_MODE_TITLE = 1;
	/**
	 * Show mode for displaying the current month and year within the item that has been specified with setMonthItem().
	 */
	public static final int SHOW_MODE_ITEM = 2;
	/**
	 * Show mode for not displaying the current month and year.
	 */
	public static final int SHOW_MODE_NONE = 3;
	
	/**
	 * first day of the week can be Sunday or Monday depending on the country/religion.
	 * ISO mandates Monday is the first day of the week, so we use this as default, unless
	 * polish.CalendarItem.FirstDayOfWeek is set to "Sunday".
	 */
	private static int FIRST_DAY_OF_WEEK =
		//#if ${lowercase(polish.CalendarItem.FirstDayOfWeek)} == sunday
			//#define tmp.startWithSunday
			//# Calendar.SUNDAY;
		//#else
			Calendar.MONDAY;
		//#endif
	private static String WEEKDAY_ABBREVIATIONS =
		//#if tmp.startWithSunday && polish.CalendarItem.WeekdayAbbreviationsSunday:defined
			//#= "${polish.CalendarItem.WeekdayAbbreviationsSunday}";
		//#elif !tmp.startWithSunday && polish.CalendarItem.WeekdayAbbreviationsMonday:defined
			//#= "${polish.CalendarItem.WeekdayAbbreviationsMonday}";
		//#elif polish.CalendarItem.WeekdayAbbreviations:defined
			//#= "${polish.CalendarItem.WeekdayAbbreviations}";
		//#elif tmp.startWithSunday
			//# "S,M,T,W,T,F,S";
		//#else
			"M,T,W,T,F,S,S";
		//#endif
	private static String MONTHS = "January,February,March,April,May,June,July,August,September,October,November,December";

	private TimePoint originalDay;
	private TimePoint shownMonth;
//	private int shownMonth;
//	private int shownYear;
//	private int lastMonth;
//	private int lastYear;
//	private int lastDay;
	private int showMode;
	
	private Style calendarWeekdayStyle;
	private Style calendarDayInactiveStyle;
	private Style calendarDayStyle;
	private Style calendarCurrentdayStyle;
	private Style calendarDayInvalidStyle;
	private CalendarEntryModel model;
	private boolean isLimitToEnabledEntries;
	private CalendarRenderer renderer;
	private TimePoint firstColumnFirstRowDay;
	private boolean isEditable = true;
	private boolean isBuild;
	private TimePeriod validPeriod;
	private Command cmdDayDefault;
	private boolean calendarInactiveDaysAreInteractice = true;
	private boolean isMonthNameSelectable;
	private Style monthNameStyle;
	private boolean isMonthNameFocused;
	
	
	

	/**
	 * Retrieves the first day of the week, typically either Calendar.SUNDAY or Calendar.MONDAY.
	 * @return the first day of the week in Calendar format
	 */
	public static int getFirstDayOfWeek()
	{
		return FIRST_DAY_OF_WEEK;
	}

	/**
	 * Sets the first day of the week, typically this is either Monday (ISO, Europe) or Sunday (e.g. US)
	 * @param firstDayOfWeek the first day of the week, e.g. Calendar.MONDAY
	 */
	public static void setFirstDayOfWeek(int firstDayOfWeek)
	{
		FIRST_DAY_OF_WEEK = firstDayOfWeek;
	}
	
	/**
	 * Retrieves a comma separated list of abbreviations for week days.
	 * @return the day abbreviations, by default "M,T,W,T,F,S,S"
	 */
	public static String getWeekDayAbbreviations() {
		return WEEKDAY_ABBREVIATIONS;
	}
	
	/**
	 * Sets the comma separated list of abbreviations for week days
	 * @param abbreviations the list of abbreviations, e.g. "Mo,Tu,We,Th,Fr,Sa,Su"
	 */
	public static void setWeekDayAbbreviations( String abbreviations ) {
		WEEKDAY_ABBREVIATIONS = abbreviations;
	}
	
	/**
	 * Retrieves the names of the month in a comma separated list.
	 * @return the names of the months, by default "January,February,March,..."
	 */
	public static String getMonths() {
		return MONTHS;
	}
	
	/**
	 * Sets the names of the month in a comma separated list.
	 * @param months the names of the months, e.g. "Jan,Feb,Mar,..."
	 */
	public static void setMonths(String months) {
		MONTHS = months;
	}


	/**
	 * Creates a new Calendar Item with the current month shown.
	 */
	public CalendarItem()
	{
		this( TimePoint.now(), null, null );
	}

	/**
	 * Creates a new Calendar Item with the current month shown.
	 * 
	 * @param style the style of the calendar item
	 */
	public CalendarItem(Style style)
	{
		this( TimePoint.now(), null, style );
	}
	
	/**
	 * Creates a new Calendar Item.
	 * 
	 * @param cal the month that should be displayed by default.
	 */
	public CalendarItem(Calendar cal)
	{
		this( cal, null );
	}
	
	/**
	 * Creates a new Calendar Item.
	 * 
	 * @param cal the month that should be displayed by default.
	 * @param style the style of the calendar item
	 */
	public CalendarItem(Calendar cal, Style style)
	{
		this( new TimePoint(cal), null, style );
	}
	
	/**
	 * Creates a new Calendar Item.
	 * 
	 * @param timePoint the month that should be displayed by default.
	 */
	public CalendarItem(TimePoint timePoint) {
		this( timePoint, null, null );
	}
	
	/**
	 * Creates a new Calendar Item.
	 * 
	 * @param originalDay the month that should be displayed by default.
	 * @param style the style of the calendar item
	 */
	public CalendarItem(TimePoint originalDay, Style style)
	{
		this( originalDay, null, style );
	}
	
	/**
	 * Creates a new Calendar Item.
	 * 
	 * @param originalDay the month that should be displayed by default.
	 * @param model the model that contains events for this CalendarItem
	 */
	public CalendarItem(TimePoint originalDay, CalendarEntryModel model)
	{
		this( originalDay, model, null);
	}

	/**
	 * Creates a new Calendar Item.
	 * 
	 * @param originalDay the month that should be displayed by default.
	 * @param model the model that contains any calendar entries that might get visualized. Can be null.
	 * @param style the style of the calendar item
	 */
	public CalendarItem(TimePoint originalDay, CalendarEntryModel model, Style style)
	{
		// depending on the month up to 6 rows my be used, typically 5 are enough + 1 for the day abbreviations
		//TODO where are the year and the month name shown? (right now the label is used)
		super( 7, 7, style); 
		this.model = model;
		this.originalDay = originalDay;
		
//		cal.set( Calendar.YEAR, 2009);
//		cal.set( Calendar.MONTH, Calendar.FEBRUARY);
		
		String[] abbreviations = TextUtil.split( WEEKDAY_ABBREVIATIONS, ',' );
		for (int i = 0; i < abbreviations.length; i++)
		{
			String abbreviation = abbreviations[i];
			//#style calendarWeekday?
			StringItem item = new StringItem( null, abbreviation);
			set( i, 0, item );
		}		
		this.shownMonth = new TimePoint( originalDay );
		setSelectionMode( SELECTION_MODE_CELL | SELECTION_MODE_INTERACTIVE );
	}
	
	/**
	 * Builds up this calendar for the specified point in time
	 * @param forMonth the point in time (most notably the month that should be shown)
	 */
	protected void buildCalendar(TimePoint forMonth)
	{
		this.shownMonth = forMonth;
		this.ignoreRepaintRequests = true;
		this.isBuild = true;
		
		
		int selRow = getSelectedRow();
		int selCol = getSelectedColumn();
		
		int currentMonth = forMonth.getMonth();
		int currentYear = forMonth.getYear();
		
		String infoText = TextUtil.split( MONTHS, ',')[currentMonth] + " " + currentYear;
		if (this.showMode == SHOW_MODE_LABEL) {
			setLabel( infoText );
		} else if (this.showMode == SHOW_MODE_TITLE) {
			Screen scr = getScreen();
			if (scr != null) {
				scr.setTitle( infoText );
			}
		}
		
		TimePoint day = new TimePoint( forMonth );
		day.setDay(1);
		int dayOfWeek = day.getDayOfWeek();
		
		int col = getColumn(dayOfWeek);
		while (col > 0) {
			day.addDay(-1);
			col--;
		}
		this.firstColumnFirstRowDay = new TimePoint( day );
		CalendarEntryList eventsList = null;
		CalendarEntry[] entriesForTheDay = null;
		if (this.model != null) {
			int daysToAdd = 7 * (getNumberOfRows() - 1) + 1;
			TimePoint endDate = new TimePoint( day );
			endDate.addDay(daysToAdd);
			TimePeriod period = new TimePeriod( day, true, endDate, false );
			if (this.isLimitToEnabledEntries) {
				eventsList = this.model.getEnabledEntries( period);
			} else {
				eventsList = this.model.getEntries( period);				
			}
		}
		//#debug
		System.out.println(infoText + ": dayOfWeek=" + dayOfWeek + "(" + getDayOfWeekName(dayOfWeek) + "), col=" + col + ", daysInMonth=" + forMonth.getDaysInMonth());
		for (int row = 1; row < getNumberOfRows(); row++) {
			for (col = 0; col < 7; col++) {
				if (eventsList != null) {
					entriesForTheDay = eventsList.getEntriesForDay(day);
				}
				Item item = createCalendaryDay(day, forMonth, this.originalDay, entriesForTheDay, col, row);
				set( col, row, item);
				day.addDay(1);
			}
		}
		
		this.ignoreRepaintRequests = false;
		
		if (selCol != -1 && selRow != -1) {
			setSelectedCell( selCol, selRow );
		} else if (!this.isMonthNameFocused && this.shownMonth.equalsMonth(this.originalDay)) {
			go(this.originalDay);
		}
		if (this.availableWidth != 0) {
			init( this.availableWidth, this.availableWidth, this.availableHeight );
		}
		repaint();
	}

	/**
	 * Creates an item that represents a day within this CalendarItem
	 * @param day the corresponding day
	 * @param currentMonth the month that is currently shown
	 * @param originalCurrentDay the original day (e.g. today) that was used to initialize this CalendarItem (should be highlighted in most cases)
	 * @param entriesForTheDay the events for the day, may be null
	 * @param col the table column for which the item is generated, you cannot expect a certain column for a specific day due to possible design and architecture changes
	 * @param row the table row for which the item is generated, you cannot expect a certain row for a specific day due to possible design and architecture changes
	 * @return the created item, must not be null
	 */
	protected Item createCalendaryDay(TimePoint day, TimePoint currentMonth, TimePoint originalCurrentDay, CalendarEntry[] entriesForTheDay, int col, int row) {
		Item item;
		if (this.renderer != null) {
			item = this.renderer.createCalendaryDay(day, currentMonth, originalCurrentDay, entriesForTheDay, this);
		} else {
			item = createCalendaryDay(day, currentMonth, originalCurrentDay, entriesForTheDay, this);
		}
		if (this.cmdDayDefault != null && day.equalsMonth(currentMonth) && item.getDefaultCommand() == null) {
			item.setDefaultCommand( this.cmdDayDefault );
		}
		if (this.validPeriod != null) {
			if (!this.validPeriod.matches(day, TimePeriod.SCOPE_DAY)) {
				item.setAppearanceMode(Item.PLAIN);
			}
		}
		return item;
	}
	

	/**
	 * Creates an item that represents a day within this CalendarItem
	 * @param day the corresponding day
	 * @param currentMonth the month that is currently shown
	 * @param originalCurrentDay the original day (e.g. today) that was used to initialize this CalendarItem (should be highlighted in most cases)
	 * @param entriesForTheDay the events for the day, may be null
	 * @param parent the parent calendar item, can be null
	 * @return the created item, must not be null
	 */
	public static Item createCalendaryDay(TimePoint day, TimePoint currentMonth, TimePoint originalCurrentDay, CalendarEntry[] entriesForTheDay, CalendarItem parent) 
	{
		StringItem item;
		boolean dayIsInteractive = true;
		if (!day.equalsMonth(currentMonth)) {
			//#style calendarDayInactive?
			item = new StringItem( null, Integer.toString( day.getDay() ));
			if (parent != null) {
				if (parent.calendarDayInactiveStyle != null) {
					item.setStyle(parent.calendarDayInactiveStyle);
				}
				if (!parent.calendarInactiveDaysAreInteractice) {
					dayIsInteractive = false;
				}
			}
		} else {
			if (day.equalsDay(originalCurrentDay)) {
				//#style calendarCurrentday, calendarDay, default
				item = new StringItem( null, Integer.toString( day.getDay() ));				
				if (parent != null && parent.calendarCurrentdayStyle != null) {
					item.setStyle(parent.calendarCurrentdayStyle);
				}
			} else {
				//#style calendarDay?
				item = new StringItem( null, Integer.toString( day.getDay() ));
				if (parent != null) {
					if (parent.validPeriod != null && parent.calendarDayInvalidStyle != null && !parent.validPeriod.matches(day, TimePeriod.SCOPE_DAY)) {
						item.setStyle(parent.calendarDayInvalidStyle);
					} else if (parent.calendarDayStyle != null) {
						item.setStyle(parent.calendarDayStyle);
					}
				}
			}

		}
		if (dayIsInteractive && (parent == null || (parent.selectionMode != TableItem.SELECTION_MODE_NONE))) {
			item.setAppearanceMode( INTERACTIVE );
		}
		return item;
	}

	/**
	 * Specifies a renderer for this CalendarItem
	 * @param renderer a renderer that is responsible for creating calendar items
	 */
	public void setRenderer( CalendarRenderer renderer) {
		this.renderer = renderer;
		if (renderer != null && this.isBuild) {
			buildCalendar(this.shownMonth);
		}
	}
	
	//#if polish.LibraryBuild	
	/**
	 * Sets the default command for days.
	 * This can be used for using the calendar item for an easy day picker.
	 * @param cmd the command that is triggered when a day is selected
	 */
	public void setDayDefaultCommand( javax.microedition.lcdui.Command cmd ) {
		// ignore
	}
	//#endif
	
	/**
	 * Sets the default command for days.
	 * This can be used for using the calendar item for an easy day picker.
	 * @param cmd the command that is triggered when a day is selected
	 */
	public void setDayDefaultCommand( Command cmd ) {
		this.cmdDayDefault = cmd;
	}
	
	/**
	 * Retrieves the column for the specified day
	 * @param day the day
	 * @return the column for the day
	 */
	public int getColumn( TimePoint day) {
		return getColumn(day.getDayOfWeek());
	}
	
	/**
	 * Retrieves the row for the specified day
	 * @param day the day
	 * @return the row for the day
	 */
	public int getRow( TimePoint day) {
		int daysFromStart = day.getDay();
		day.setDay(1);
		int col = getColumn( day ) - 1;
		day.setDay(daysFromStart);
		daysFromStart += col;
		return (daysFromStart / 7) + 1;
	}

	/**
	 * Retrieves the column
	 * @param dayOfWeek the day within 
	 * @return the corresponding column
	 */
	private int getColumn(int dayOfWeek)
	{
		return dayOfWeek >= FIRST_DAY_OF_WEEK ? dayOfWeek - FIRST_DAY_OF_WEEK  :  7 + dayOfWeek - FIRST_DAY_OF_WEEK;
	}

	/**
	 * @param dayOfWeek
	 * @return
	 */
	private String getDayOfWeekName(int dayOfWeek)
	{
		String[] days = {null, "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
		return days[dayOfWeek];
	}

	/**
	 * Retrieves the days within the currently shown month
	 * @return the number of days within the month that is currently shown.
	 */
	public int getDaysInMonth() {
		return this.shownMonth.getDaysInMonth();
	}
	
	/**
	 * Deprecated method to retrieve the number of days of a given calendar
	 * @param cal the calendar
	 * @return the number of days within the month of the given calendar
	 * @deprecated
	 * @see TimePoint#getDaysInMonth()
	 */
	public static int getDaysInMonth(Calendar cal) {
		TimePoint point = new TimePoint( cal );
		return point.getDaysInMonth();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TableItem#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction)
	{
		if (this.isMonthNameFocused) {
			if (gameAction == Canvas.LEFT) {
				return goPreviousMonth();
			} else if (gameAction == Canvas.RIGHT) {
				return goNextMonth();
			} else if (gameAction == Canvas.DOWN) {
				UiAccess.defocus(this.label, this.monthNameStyle);
				this.isMonthNameFocused = false;
				this.shownMonth.setDay(1);
				go(this.shownMonth);
				return true;
			} else {
				return false;
			}
		}
		boolean handled = super.handleKeyPressed(keyCode, gameAction);
		if (!handled && isInteractive()) {
			if (gameAction == Canvas.LEFT || gameAction == Canvas.UP) {
				if (gameAction == Canvas.LEFT && getSelectedColumn() == 0) {
					int selectedRow = getSelectedRow();
					if (selectedRow > 1) {
						selectedRow--;
						int col = 6;
						Object o = get(col, selectedRow);
						if (o instanceof Item && ((Item)o).isInteractive()) {
							setSelectedCell(col, selectedRow);
							return true;
						}
					}
				}
				if (this.isMonthNameSelectable && (gameAction == Canvas.UP)) {
					this.monthNameStyle = UiAccess.focus(this.label, 0, null);
					this.isMonthNameFocused = true;
					setSelectedCell(-1, -1);
					return true;
				}
				return goPreviousMonth();
			} else if (gameAction == Canvas.RIGHT || gameAction == Canvas.DOWN) {
				if (gameAction == Canvas.RIGHT && getSelectedColumn() == 6) {
					int selectedRow = getSelectedRow();
					if (selectedRow < 6) {
						selectedRow++;
						int col = 0;
						Object o = get(col, selectedRow);
						if (o instanceof Item && ((Item)o).isInteractive()) {
							setSelectedCell(col, selectedRow);
							return true;
						}
					}
				}
				return goNextMonth();
			}
		}
		return handled;
	}

	/**
	 * Moves this calendar to the next month
	 * @return true when the previous month is in the valid period or when no valid range is specified
	 */
	public boolean goNextMonth()
	{
		TimePeriod range = this.validPeriod;
		TimePoint nextMonth;
		if (range == null) {
			nextMonth = this.shownMonth;
		} else {
			nextMonth = new TimePoint(this.shownMonth);
		}
		nextMonth.addMonth(1);
		nextMonth.setDay(1);
		if ( (range == null) || range.matches(nextMonth, TimePeriod.SCOPE_DAY)) {
			buildCalendar( nextMonth );
			if (!this.isMonthNameFocused) {
				go( nextMonth );
			}
			return true;			
		}
		return false;
	}

	/**
	 * Moves this calendar to the previous month
	 * @return true when the previous month is in the valid period or when no valid range is specified
	 */
	public boolean goPreviousMonth()
	{
		TimePeriod range = this.validPeriod;
		TimePoint nextMonth;
		if (range == null) {
			nextMonth = this.shownMonth;
		} else {
			nextMonth = new TimePoint(this.shownMonth);
		}
		nextMonth.addMonth(-1);
		if (range != null) {
			nextMonth.setDay( nextMonth.getDaysInMonth() );
			if (!range.matches(nextMonth, TimePeriod.SCOPE_DAY)) {
				return false;
			}
		}
		nextMonth.setDay(1);
		buildCalendar( nextMonth );
		if (!this.isMonthNameFocused) {
			if (range != null && range.getStart().equalsMonth(nextMonth)) 
			{
				nextMonth.setDay( nextMonth.getDaysInMonth() );
			}
			go( nextMonth );
		}
		return true;
	}
	
	/**
	 * Goes to the specified day.
	 * When the day is outside of the scope of the specified date range, it will be either moved to the beginning of the 
	 * valid range or to its end.
	 * @param day the day that should be shown
	 * @see #setValidPeriod(TimePeriod)
	 */
	public void go(TimePoint day) {
		if (this.validPeriod != null && !this.validPeriod.matches(day, TimePeriod.SCOPE_DAY)) {
			if (day.equalsMonth(this.validPeriod.getStart()) || (day.isBefore(this.validPeriod.getStart(), TimePeriod.SCOPE_DAY))) {
				day.setDate( this.validPeriod.getStart() );
				if (!this.validPeriod.isIncludeStart()) {
					day.addDay(1);
				}
			} else {
				day.setDate( this.validPeriod.getEnd() );
				if (!this.validPeriod.isIncludeEnd()) {
					day.addDay(-1);
				}
			}
		}
		if (this.isBuild) {
			if (!day.equalsMonth(this.shownMonth)) {
				buildCalendar( day );
			}
			int col = getColumn(day);
			int row = getRow(day);
			setSelectedCell(col, row);
		} else {
			this.originalDay = day;
			this.shownMonth = new TimePoint( day );
		}
	}

	
	//#if polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TableItem#handlePointerReleased(int, int)
	 */
	protected boolean handlePointerReleased(int relX, int relY)
	{
		if (isInteractive() && relY <= this.contentY && relY >= 0) {
			if (relX <= this.itemWidth / 2) {
				goPreviousMonth();
				return true;
			} else if (relX <= this.itemWidth) {
				goNextMonth();
				return true;
			}
		} else if (this.isMonthNameFocused) {
			UiAccess.defocus(this.label, this.monthNameStyle);
			this.isMonthNameFocused = false;
			this.shownMonth.setDay(1);
			go(this.shownMonth);
		}
		return super.handlePointerReleased(relX, relY);
	}
	//#endif
	
	/**
	 * Retrieves the TimePoint of the given row and column within the shown Calendar
	 * @param row the row index
	 * @param col the column index
	 * @return the corresponding TimePoint
	 */
	public TimePoint getCellTimePoint(int row, int col){
		if (this.firstColumnFirstRowDay == null) {
			TimePoint day = new TimePoint( this.originalDay );
			day.setDay(1);
			int dayOfWeek = day.getDayOfWeek();
			
			int daycol = getColumn(dayOfWeek);
			while (daycol > 0) {
				day.addDay(-1);
				daycol--;
			}
			this.firstColumnFirstRowDay = new TimePoint( day );
		}
		TimePoint tp = new TimePoint( this.firstColumnFirstRowDay );
		tp.addDay( (row * 7) + col );
		return tp;
	}
	
	/**
	 * Retrieves the Calendar of the given row and column within the shown Calendar
	 * @param row the row index
	 * @param col the column index
	 * @return the corresponding Calendar
	 */
	public Calendar getCellCalendar(int row, int col){
		return getCellTimePoint(row, col).getAsCalendar();

	}
	
	/**
	 * Retrieves the currently selected day as a TimePoint
	 * @return a TimePoint representing the currently selected day
	 * @see #go(TimePoint)
	 */
	public TimePoint getSelectedTimePoint()
	{
		int col = getSelectedColumn();
		int row = getSelectedRow() - 1;
		return getCellTimePoint(row, col);
	}

	
	/**
	 * Retrieves the currently selected day as a Calendar
	 * @return a Calendar representing the currently selected day
	 * @see #go(TimePoint)
	 */
	public Calendar getSelectedCalendar()
	{
		int col = getSelectedColumn();
		int row = getSelectedRow() - 1;
		return getCellCalendar(row, col);
	}

	/**
	 * Retrieves the currently selected day as a Date
	 * @return a Date representing the currently selected day
	 * @see #go(TimePoint)
	 */
	public Date getSelectedDate()
	{
		return getSelectedCalendar().getTime();
	}
	
	/**
	 * Enables or disables the interactivity state of this calendar item.
	 * @param isInteractive true when the item should be editable and selectable
	 */
	public void setEditable( boolean isInteractive ) {
		this.isEditable = isInteractive;
		if (isInteractive) {
			setAppearanceMode( INTERACTIVE );
		} else {
			setAppearanceMode( PLAIN );
		}
	}
	
	/**
	 * Specifies whether the user can select the label that displays the month names.
	 * When the user can select the month name, she can move between the months using LEFT and RIGHT key actions.
	 * 
	 * @param isSelectable true when the user should be able to select the month names, by default this is false
	 */
	public void setMonthNameSelectable( boolean isSelectable) {
		this.isMonthNameSelectable = isSelectable;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TableItem#initContent(int, int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		if (!this.isBuild) {
			buildCalendar(this.shownMonth);
		}
		super.initContent(firstLineWidth, availWidth, availHeight);
		if (!this.isEditable || (this.selectionMode == SELECTION_MODE_NONE)) {
			this.appearanceMode = PLAIN;
		}
	}

	/**
	 * Gets the style for the headings of this calendar, if different from .calendarWeekday
	 * @return the style
	 */
	public Style getCalendarWeekdayStyle() {
		return this.calendarWeekdayStyle;
	}

	/**
	 * Sets the style for the headings of this calendar
	 * @param calendarWeekdayStyle the style
	 */
	public void setCalendarWeekdayStyle(Style calendarWeekdayStyle) {
		this.calendarWeekdayStyle = calendarWeekdayStyle;
	}

	/**
	 * Gets the style for days of another month, if different from .calendarDayInactive
	 * @return the style
	 */
	public Style getCalendarDayInactiveStyle() {
		return this.calendarDayInactiveStyle;
	}

	/**
	 * Sets the style for days of another month
	 * @param calendarDayInactiveStyle the style
	 */
	public void setCalendarDayInactiveStyle(Style calendarDayInactiveStyle) {
		this.calendarDayInactiveStyle = calendarDayInactiveStyle;
	}

	/**
	 * Gets the style for a normal calendar day entry, if different from .calendarDay
	 * @return the style
	 */
	public Style getCalendarDayStyle() {
		return this.calendarDayStyle;
	}

	/**
	 * Sets the style for a normal calendar day entry
	 * @param calendarDayStyle the style
	 */
	public void setCalendarDayStyle(Style calendarDayStyle) {
		this.calendarDayStyle = calendarDayStyle;
	}

	/**
	 * Gets the style for the currently selected calendar day entry, if different from .calendarCurrentDay
	 * @return the style
	 */
	public Style getCalendarCurrentdayStyle() {
		return this.calendarCurrentdayStyle;
	}

	/**
	 * Sets the style for the currently selected calendar day entry
	 * @param calendarCurrentdayStyle the style
	 */
	public void setCalendarCurrentdayStyle(Style calendarCurrentdayStyle) {
		this.calendarCurrentdayStyle = calendarCurrentdayStyle;
	}
	
	/**
	 * Specifies whether days that are outside of the current month can be selected
	 * @param isInteractive true when they should be selectable (this is the default state), false if not.
	 */
	public void setCalendarInactiveDaysAreInteractive( boolean isInteractive) {
		this.calendarInactiveDaysAreInteractice  = isInteractive;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TableItem#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.customitem-inactive
			Boolean inactiveBool = style.getBooleanProperty("customitem-inactive");
			if (inactiveBool != null) {
				setEditable( !inactiveBool.booleanValue() );
			}
		//#endif
		//#if polish.css.calendar-weekday-style
			Style headingStyle = (Style) style.getObjectProperty("calendar-weekday-style");
			if (headingStyle != null) {
				this.calendarWeekdayStyle = headingStyle;
				for (int i=0; i<7; i++) {
					Item item = (Item) get( i, 0 );
					if (item != null) {
						item.setStyle( headingStyle );
					}
				}
			}
		//#endif
		//#if polish.css.calendar-day-inactive-style
			Style dayInactiveStyle = (Style) style.getObjectProperty("calendar-day-inactive-style");
			if (dayInactiveStyle != null) {
				this.calendarDayInactiveStyle = dayInactiveStyle;
			}
		//#endif
		//#if polish.css.calendar-day-style
			Style dayStyle = (Style) style.getObjectProperty("calendar-day-style");
			if (dayStyle != null) {
				this.calendarDayStyle = dayStyle;
			}
		//#endif
		//#if polish.css.calendar-current-day-style
			Style currentDayStyle = (Style) style.getObjectProperty("calendar-current-day-style");
			if (currentDayStyle != null) {
				this.calendarCurrentdayStyle = currentDayStyle;
			}
		//#endif		
			//#if polish.css.calendar-day-invalid-style
			Style dayInvalidStyle = (Style) style.getObjectProperty("calendar-day-invalid-style");
			if (dayInactiveStyle != null) {
				this.calendarDayInvalidStyle = dayInvalidStyle;
			}
		//#endif
		//#if polish.css.calendar-show-mode
			Integer showModeInt = style.getIntProperty("calendar-show-mode");
			if (showModeInt != null) {
				this.showMode = showModeInt.intValue();
			}
		//#endif
	}
	
	/**
	 * Sets the style for the current day
	 * @param dayStyle the style for the current day
	 */
	public void setStyleCurrentDay(Style dayStyle) {
		this.calendarCurrentdayStyle = dayStyle;
	}
	
	/**
	 * Sets the style for days that do not belong to the current month
	 * @param dayStyle the style for days that don't belong to the current month
	 */
	public void setStyleInactiveDay(Style dayStyle) {
		this.calendarDayInactiveStyle = dayStyle;
	}
	
	/**
	 * Sets the style for days of the current month
	 * @param dayStyle the style for days of the current month
	 */
	public void setStyleDay(Style dayStyle) {
		this.calendarDayStyle = dayStyle;
	}
	
	/**
	 * Sets the style for days for days that are not valid
	 * @param dayStyle the style for days that are in the current month but not valid
	 * @see #setValidPeriod(TimePeriod)
	 */
	public void setStyleInvalidDay( Style dayStyle ) {
		this.calendarDayInvalidStyle = dayStyle;
	}
	
	/**
	 * Retrieves the shown month
	 * @return the shown month as in java.util.Calendar, e.g. java.util.Calendar.JANUARY
	 */
	public int getShownMonth(){
		return this.shownMonth.getMonth();
	}
	
	/**
	 * Retrieves the shown month as a TimePoint
	 * @return the currently shown month as a TimePoint
	 */
	public TimePoint getShownTimePoint() {
		return this.shownMonth;
	}

	/**
	 * Sets a data model that contains calendar events for this CalendarItem.
	 * @param model the model use null to remove the model 
	 * @see #setModelLimitToEnabledCategories(boolean)
	 */
	public void setModel( CalendarEntryModel model ) {
		this.model = model;
		if (this.renderer != null && this.isBuild) {
			buildCalendar(this.shownMonth);
		}
	}
	
	/**
	 * Retrieves the model used in this CalendarItem
	 * @return the model used, can be null
	 */
	public CalendarEntryModel getModel() {
		return this.model;
	}

	/**
	 * Specifies whether only enabled categories should be displayed in this CalendarItem
	 * @param limit true when only enabled categories should be shown
	 * @see #setModel(CalendarEntryModel)
	 */
	public void setModelLimitToEnabledCategories( boolean limit ) {
		this.isLimitToEnabledEntries = limit;
	}
	
	/**
	 * Specifies the visualization for the year and month
	 * @param mode the mode
	 * @see #SHOW_MODE_ITEM
	 * @see #SHOW_MODE_LABEL
	 * @see #SHOW_MODE_NONE
	 * @see #SHOW_MODE_TITLE
	 * @see #getShowMode()
	 */
	public void setShowMode( int mode ) {
		if (mode < SHOW_MODE_LABEL || mode > SHOW_MODE_NONE) {
			throw new IllegalArgumentException();
		}
		if (mode != this.showMode) {
		this.showMode = mode;
			if (isInitialized()) {
				requestInit();
			}
		}
	}
	
	/**
	 * Retrieves the visualization for the year and month
	 * @return the mode
	 * @see #SHOW_MODE_ITEM
	 * @see #SHOW_MODE_LABEL
	 * @see #SHOW_MODE_NONE
	 * @see #SHOW_MODE_TITLE
	 * @see #setShowMode(int)
	 */
	public int getShowMode() {
		return this.showMode;
	}
	
	/**
	 * Sets the time range that is valid for this instance.
	 * The user cannot navigate outside of the specified validity period.
	 * @param validPeriod the valid range, use null to remove any boundaries
	 * @see #getValidPeriod()
	 */
	public void setValidPeriod( TimePeriod validPeriod ) {
		this.validPeriod = validPeriod;
		if (validPeriod != null && this.isBuild) {
			TimePoint tp = getSelectedTimePoint();
			if (!validPeriod.matches(tp, TimePeriod.SCOPE_DAY)) {
				if (tp.isBefore(validPeriod.getStart(), TimePeriod.SCOPE_DAY)) {
					go( validPeriod.getStart() );
				} else {
					go( validPeriod.getEnd() );
				}
			}
		}
	}

	/**
	 * Retrieves the time range that is valid for this instance.
	 * @return the specified time period, may be null
	 * @see #setValidPeriod(TimePeriod)
	 */
	public TimePeriod getValidPeriod() {
		return this.validPeriod;
	}
}
