/*
 * Created on Jun 29, 2010 at 10:41:21 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import de.enough.polish.io.Externalizable;

/**
 * <p>Defines a point in time within the current TimeZone (typically)</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TimePoint
implements Externalizable, Comparator, Comparable
{
	/**
	 * A comparator that can compare different TimePoints for sorting
	 * @see Arrays#sort(Object[], Comparator)
	 */
	public static Comparator COMPARATOR = new TimePoint();
	private static final int VERSION = 100;
	/**
	 * Days per month - note that February might be 28 or 29 days, depending on whether the current year is a leap year
	 */
	private static final int[] DAYS_PER_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	private int millisecond;
	private TimeZone timeZone;
	private Calendar calendar;
	private boolean resetCalendar;
	
	/**
	 * Creates an empty/undefined time point
	 */
	public TimePoint() {
		this(1970, 1, 1, 0, 00, 0, 000, null );
	}
	
	/**
	 * (Re)creates a serialized time point
	 * @param in the input stream
	 * @throws IOException when reading fails
	 */
	public TimePoint(DataInputStream in) throws IOException {
		read( in );
	}

	/**
	 * Clones the specified TimePoint
	 * @param tp the TimePoint that should be cloned
	 */
	public TimePoint(TimePoint tp) {
		this(tp.year, tp.month, tp.day, tp.hour, tp.minute, tp.second, tp.millisecond, tp.timeZone );
	}
	
	/**
	 * Creates a new TimePoint that replicates the specified calendar
	 * @param cal the calendar which should be copied
	 */
	public TimePoint(Calendar cal) {
		this(cal.get( Calendar.YEAR), cal.get( Calendar.MONTH ), cal.get(Calendar.DAY_OF_MONTH), 
				cal.get( Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND), cal.getTimeZone() );
		this.calendar = cal;
	}
	
	/**
	 * Creates a new TimePoint with the specified Date in the default TimeZone
	 * @param date the date of this TimePoint
	 */
	public TimePoint(Date date) {
		setDate( date );
	}
	
	/**
	 * Creates a new TimePoint with the specified Date in the default TimeZone
	 * @param timeInMillis the date of this TimePoint in milliseconds since 1.1.1970
	 */
	public TimePoint(long timeInMillis) {
		setDate( timeInMillis );
	}
	
	/**
	 * Sets the date of this timepoint
	 * @param tp the timepoint which settings should be copied
	 */
	public void setDate( TimePoint tp ) {
		this.year = tp.year;
		this.month = tp.month;
		this.day = tp.day;
		this.hour = tp.hour;
		this.minute = tp.minute;
		this.second = tp.second;
		this.millisecond = tp.millisecond;
		this.timeZone = tp.timeZone;
	}
	
	/**
	 * Sets the date of this timepoint
	 * @param timeInMillis the date of this TimePoint in milliseconds since 1.1.1970
	 */
	public void setDate( long timeInMillis) {
		setDate( new Date( timeInMillis ));
	}

	/**
	 * Sets the date of this TimePoint
	 * @param date the new time 
	 */
	public void setDate(Date date) {
		if (this.calendar != null) {
			this.calendar.setTime(date);
			setDate( this.calendar );
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			setDate( cal );
		}
		
	}
	
	/**
	 * Sets the date of this TimePoint
	 * @param cal the new time 
	 */
	public void setDate(Calendar cal) {
		this.calendar = cal;
		this.timeZone = (cal.getTimeZone() == TimeZone.getDefault()) ? null : cal.getTimeZone();
		this.year = cal.get(Calendar.YEAR);
		this.month = cal.get(Calendar.MONTH);
		this.day = cal.get(Calendar.DAY_OF_MONTH);
		this.hour = cal.get(Calendar.HOUR_OF_DAY);
		this.minute = cal.get(Calendar.MINUTE);
		this.second = cal.get(Calendar.SECOND);
		this.millisecond = cal.get(Calendar.MILLISECOND);
	}
	
	/**
	 * Sets the date of this point in time while keeping the current time
	 * @param year the new year
	 * @param month the new month
	 * @param day the new day within the month
	 */
	public void setDate(int year, int month, int day) {
		setYear( year );
		setMonth( month );
		setDay( day );
	}


	/**
	 * Sets the date of this point in time while keeping the current time
	 * @param year the new year
	 * @param month the new month
	 * @param day the new day within the month
	 * @param hour the new hour within the day (0..23)
	 * @param minute the new minute within the hour (0..59)
	 * @param second the new second within the minute (0..59)
	 * @param millisecond the new millisecond within the second (0..999)
	 */
	public void setDate(int year, int month, int day, int hour, int minute, int second, int millisecond) 
	{
		setYear( year );
		setMonth( month );
		setDay( day );
		setHour( hour );
		setMinute( minute );
		setSecond( second );
		setMillisecond( millisecond );
	}

	/**
	 * Creates a new point in time at the given date at 0:00 (midnight)
	 * @param year the year
	 * @param month the month ranging from 0 (January) to 11 (December) as in java.util.Calendar
	 * @param day the day within the month, ranging from 1 to 31.
	 * @throws IllegalArgumentException when a parameter is invalid
	 */
	public TimePoint( int year, int month, int day ) {
		this( year, month, day, 0, 0, 0, 0, null );
	}
	
	/**
	 * Creates a new point in time at the given date and the given time
	 * @param year the year
	 * @param month the month ranging from 0 (January) to 11 (December) as in java.util.Calendar
	 * @param day the day within the month, ranging from 1 to 31.
	 * @param hour the hour within the day from 0 to 23
	 * @param minute the minute in the our from 0 to 59
	 * @throws IllegalArgumentException when a parameter is invalid
	 */
	public TimePoint( int year, int month, int day, int hour, int minute ) {
		this( year, month, day, hour, minute, 0, 0, null );
	}
	/**
	 * Creates a new point in time at the given date and the given time
	 * @param year the year
	 * @param month the month ranging from 0 (January) to 11 (December) as in java.util.Calendar
	 * @param day the day within the month, ranging from 1 to 31.
	 * @param hour the hour within the day from 0 to 23
	 * @param minute the minute in the our from 0 to 59
	 * @param second the seconds from 0 to 59
	 * @param millisecond the milliseconds from 0 to 999
	 * @throws IllegalArgumentException when a parameter is invalid
	 */
	public TimePoint( int year, int month, int day, int hour, int minute, int second, int millisecond ) {
		this( year, month, day, hour, minute, second, millisecond, null );
	}
	
	/**
	 * Creates a new point in time at the given date at 0:00 (midnight)
	 * @param year the year
	 * @param month the month ranging from 0 (January) to 11 (December) as in java.util.Calendar
	 * @param day the day within the month, ranging from 1 to 31.
	 * @param timeZone the timezone, null for the default time zone
	 * @throws IllegalArgumentException when a parameter is invalid
	 */
	public TimePoint( int year, int month, int day, TimeZone timeZone ) {
		this( year, month, day, 0, 0, 0, 0, timeZone );
	}
	
	/**
	 * Creates a new point in time at the given date and the given time
	 * @param year the year
	 * @param month the month ranging from 0 (January) to 11 (December) as in java.util.Calendar
	 * @param day the day within the month, ranging from 1 to 31.
	 * @param hour the hour within the day from 0 to 23
	 * @param minute the minute in the our from 0 to 59
	 * @param timeZone the timeZone, null for the default time zone
	 * @throws IllegalArgumentException when a parameter is invalid
	 */
	public TimePoint( int year, int month, int day, int hour, int minute, TimeZone timeZone ) {
		this( year, month, day, hour, minute, 0, 0, timeZone );
	}
	/**
	 * Creates a new point in time at the given date and the given time
	 * @param year the year
	 * @param month the month ranging from 0 (January) to 11 (December) as in java.util.Calendar
	 * @param day the day within the month, ranging from 1 to 31.
	 * @param hour the hour within the day from 0 to 23
	 * @param minute the minute in the our from 0 to 59
	 * @param second the seconds from 0 to 59
	 * @param millisecond the milliseconds from 0 to 999
	 * @param timeZone the timeZone, null for the default time zone
	 * @throws IllegalArgumentException when a parameter is invalid
	 */
	public TimePoint( int year, int month, int day, int hour, int minute, int second, int millisecond, TimeZone timeZone ) {
		if (month < Calendar.JANUARY || month > Calendar.DECEMBER || day < 1 || day > 31 || hour < 0 || hour > 23 || minute < 0 || minute > 59 || second < 0 || second > 59 || millisecond < 0 || millisecond > 999) { 
			//throw new IllegalArgumentException();
			//TODO when using TimePoint as an interval we should be able to specify 0 days
		}
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.millisecond = millisecond;
		this.timeZone = timeZone;
	}
	

	/**
	 * Retrieves the time point for right now
	 * @return a new time point initialized with all values for right now
	 */
	public static TimePoint now() {
		return new TimePoint( Calendar.getInstance() );
	}
	
	
	/**
	 * Retrieves the time point for today
	 * @return a new time point representing today at 0:00 AM
	 */
	public static TimePoint today() {
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.HOUR, 0 );
		cal.set( Calendar.MINUTE, 0 );
		cal.set( Calendar.SECOND, 0 );
		cal.set( Calendar.MILLISECOND, 0 );
		return new TimePoint( cal );
	}
	
	
	/**
	 * Retrieves the point in time as a Calendar object
	 * Note that the retrieved Calendar may contain different values in Calendar.MONTH and Calendar.DAY_OF_MONTH as specified
	 * within this TimePoint, when illegal values are used for the day-month-combination, e.g. February 30, or April 31.
	 * @return the Calendar representation of this point in time.
	 */
	public Calendar getAsCalendar() {
		boolean reset = this.resetCalendar;
		if (this.calendar == null) {
			reset = true;
			if (this.timeZone != null) {
				this.calendar = Calendar.getInstance(this.timeZone);
			} else {
				this.calendar = Calendar.getInstance();
			}
		} 
		if (reset) {
			this.calendar.set( Calendar.YEAR, this.year );
			this.calendar.set( Calendar.MONTH, this.month );
			this.calendar.set( Calendar.DAY_OF_MONTH, this.day );
			this.calendar.set( Calendar.HOUR_OF_DAY, this.hour );
			this.calendar.set( Calendar.MINUTE, this.minute );
			this.calendar.set( Calendar.SECOND, this.second );
			this.calendar.set( Calendar.MILLISECOND, this.millisecond );
		}
		return this.calendar;
	}
	
	/**
	 * Retrieves the point in time as Date
	 * @return this point in time as java.util.Date 
	 */
	public Date getAsDate() {
		return getAsCalendar().getTime();
	}

	/**
	 * Checks whether this  point in time is before the given TimePoint
	 * @param tp the given time point
	 * @return true when the this time point is before the given one.
	 */
	public boolean isBefore(TimePoint tp) {
		if (equalsTimeZone( tp )) {
			return (this.year < tp.year)
				|| (this.year == tp.year && this.month < tp.month)
				|| (this.year == tp.year && this.month == tp.month && this.day < tp.day)
				|| (this.year == tp.year && this.month == tp.month && this.day == tp.day && this.hour < tp.hour)
				|| (this.year == tp.year && this.month == tp.month && this.day == tp.day && this.hour == tp.hour && this.minute < tp.minute)
				|| (this.year == tp.year && this.month == tp.month && this.day == tp.day && this.hour == tp.hour && this.minute == tp.minute && this.second < tp.second)
				|| (this.year == tp.year && this.month == tp.month && this.day == tp.day && this.hour == tp.hour && this.minute == tp.minute && this.second == tp.second && this.millisecond < tp.millisecond);
		}
		return getAsCalendar().before( tp.getAsCalendar() );
	}
	
	/**
	 * Checks whether this  point in time is before the given TimePoint
	 * @param tp the given time point
	 * @param scope the dimension for the comparison, the scope is only applied when both timepoints share the same timezone
	 * @return true when the this time point is before the given one.
	 * @see TimePeriod#SCOPE_MILLISECOND
	 * @see TimePeriod#SCOPE_SECOND
	 * @see TimePeriod#SCOPE_HOUR
	 * @see TimePeriod#SCOPE_DAY
	 * @see TimePeriod#SCOPE_MONTH
	 * @see TimePeriod#SCOPE_YEAR
	 */
	public boolean isBefore(TimePoint tp, int scope) {
		boolean result;
		if (equalsTimeZone( tp )) {
			result = (this.year < tp.year);
			if (!result && (this.year == tp.year) && (scope <= TimePeriod.SCOPE_MONTH)) {
				result = (this.month < tp.month);
				if (!result && (this.month == tp.month) && (scope <= TimePeriod.SCOPE_DAY)) {
					result = (this.day < tp.day);
					if (!result && (this.day == tp.day) && (scope <= TimePeriod.SCOPE_HOUR)) {
						result = (this.hour < tp.hour);
						if (!result && (this.hour == tp.hour) && (scope <= TimePeriod.SCOPE_MINUTE)) {
							result = (this.minute < tp.minute);
							if (!result && (this.minute == tp.minute) && (scope <= TimePeriod.SCOPE_SECOND)) {
								result = (this.second < tp.second);
								if (!result && (this.second == tp.second)) {
									result = (this.millisecond < tp.millisecond);
								}
							}
						}
					}
				}
			}
		} else {
			result = getAsCalendar().before( tp.getAsCalendar() );
//			if (!result && (scope > TimePeriod.SCOPE_MILLISECOND)) {
//				
//			}
		}
		return result;
	}

	
	private boolean equalsTimeZone(TimePoint tp) {
		if ( this.timeZone == tp.timeZone ) {
			return true;
		}
		TimeZone defaultTimeZone = TimeZone.getDefault();
		if ( (defaultTimeZone.equals(this.timeZone) && tp.timeZone == null) 
			|| (this.timeZone == null && defaultTimeZone.equals(tp.timeZone))
		) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether this  point in time is after the given TimePoint
	 * @param tp the given time point
	 * @return true when the this time point is after the given one.
	 */
	public boolean isAfter(TimePoint tp) {
		if (equalsTimeZone( tp )) {
			return (this.year > tp.year)
				|| (this.year == tp.year && this.month > tp.month)
				|| (this.year == tp.year && this.month == tp.month && this.day > tp.day)
				|| (this.year == tp.year && this.month == tp.month && this.day == tp.day && this.hour > tp.hour)
				|| (this.year == tp.year && this.month == tp.month && this.day == tp.day && this.hour == tp.hour && this.minute > tp.minute)
				|| (this.year == tp.year && this.month == tp.month && this.day == tp.day && this.hour == tp.hour && this.minute == tp.minute && this.second > tp.second)
				|| (this.year == tp.year && this.month == tp.month && this.day == tp.day && this.hour == tp.hour && this.minute == tp.minute && this.second == tp.second && this.millisecond > tp.millisecond);
		}
		return getAsCalendar().after( tp.getAsCalendar() );
	}
	
	/**
	 * Checks whether this  point in time is after the given TimePoint
	 * @param tp the given time point
	 * @param scope the dimension for the comparison, the scope is only applied when both timepoints share the same timezone
	 * @return true when the this time point is after the given one.
	 * @see TimePeriod#SCOPE_MILLISECOND
	 * @see TimePeriod#SCOPE_SECOND
	 * @see TimePeriod#SCOPE_HOUR
	 * @see TimePeriod#SCOPE_DAY
	 * @see TimePeriod#SCOPE_MONTH
	 * @see TimePeriod#SCOPE_YEAR
	 */
	public boolean isAfter(TimePoint tp, int scope) {
		boolean result;
		if (equalsTimeZone( tp )) {
			result = (this.year > tp.year);
			if (!result && (this.year == tp.year) && (scope <= TimePeriod.SCOPE_MONTH)) {
				result = (this.month > tp.month);
				if (!result && (this.month == tp.month) && (scope <= TimePeriod.SCOPE_DAY)) {
					result = (this.day > tp.day);
					if (!result && (this.day == tp.day) && (scope <= TimePeriod.SCOPE_HOUR)) {
						result = (this.hour > tp.hour);
						if (!result && (this.hour == tp.hour) && (scope <= TimePeriod.SCOPE_MINUTE)) {
							result = (this.minute > tp.minute);
							if (!result && (this.minute == tp.minute) && (scope <= TimePeriod.SCOPE_SECOND)) {
								result = (this.second > tp.second);
								if (!result && (this.second == tp.second)) {
									result = (this.millisecond > tp.millisecond);
								}
							}
						}
					}
				}
			}
		} else {
			result = getAsCalendar().after( tp.getAsCalendar() );
//			if (!result && (scope > TimePeriod.SCOPE_MILLISECOND)) {
//				
//			}
		}
		return result;
	}

	

	/**
	 * Retrieves the time zone of this time point.
	 * @return the timeZone, can be null (which then uses implicitly the local default time zone)
	 */
	public TimeZone getTimeZone() {
		return this.timeZone;
	}

	/**
	 * Uses a specific TimeZone for this point in time
	 * @param timeZone the TimeZone to set, use null for the default time zone.
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
		this.calendar = null;
	}

	/**
	 * Retrieves the year of this timepoint
	 * @return the year
	 */
	public int getYear() {
		return this.year;
	}

	/**
	 * Sets the year of this timepoint
	 * @param year the year
	 */
	public void setYear( int year ) {
		this.year = year;
		this.resetCalendar = true;
	}
	
	/**
	 * Retrieves the month of this point of time
	 * @return the month as defined in java.util.Calendar, e.g. Calendar.JANUARY
	 */
	public int getMonth() {
		return this.month;
	}

	/**
	 * Sets the month of this point of time
	 * @param month the month as defined in java.util.Calendar, e.g. Calendar.JANUARY
	 * @throws IllegalArgumentException when the month is not valid
	 */
	public void setMonth( int month) {
		if (month < Calendar.JANUARY || month > Calendar.DECEMBER) {
			throw new IllegalArgumentException();
		}
		this.month = month;
		this.resetCalendar = true;
	}
	
	/**
	 * Adds the specified number of milliseconds to this time point.
	 * NOTE: Adding millisecond can also change the second setting.
	 * @param numberOfMilliseconds the number of milliseconds that should be added, use a negative number to subtract milliseconds
	 */
	public void addMillisecond(long numberOfMilliseconds) {
		long ms = this.millisecond + numberOfMilliseconds;
		while (ms > 999) {
			addSecond(1);
			ms -= 1000;
		}
		while (ms < 0) {
			addSecond(-1);
			ms += 1000;
		}
		setMillisecond( (int) ms );
	}
	
	/**
	 * Adds the specified number of seconds to this time point.
	 * NOTE: Adding seconds can also change the minute setting.
	 * @param numberOfSeconds the number of seconds that should be added, use a negative number to subtract seconds
	 */
	public void addSecond(int numberOfSeconds) {
		int s = this.second + numberOfSeconds;
		while (s > 59) {
			addMinute(1);
			s -= 60;
		}
		while (s < 0) {
			addMinute(-1);
			s += 60;
		}
		setSecond( s );
	}
	
	
	/**
	 * Adds the specified number of minutes to this time point.
	 * NOTE: Adding minutes can also change the hour setting.
	 * @param numberOfMinutes the number of minutes that should be added, use a negative number to subtract minutes
	 */
	public void addMinute(int numberOfMinutes) {
		int m = this.minute + numberOfMinutes;
		while (m > 59) {
			addHour(1);
			m -= 60;
		}
		while (m < 0) {
			addHour(-1);
			m += 60;
		}
		setMinute( m );
	}
	
	
	/**
	 * Adds the specified number of hours to this time point.
	 * NOTE: Adding hours can also change the day setting.
	 * @param numberOfHours the number of hours that should be added, use a negative number to subtract hours
	 */
	public void addHour(int numberOfHours) {
		int h = this.hour + numberOfHours;
		while (h > 23) {
			addDay(1);
			h -= 24;
		}
		while (h < 0) {
			addDay(-1);
			h += 24;
		}
		setHour( h );
	}
	

	/**
	 * Adds the specified number of days to this time point.
	 * NOTE: When adding several days, the month will be adjusted automatically.
	 * @param numberOfDays the number of days that should be added, use a negative number to subtract days
	 */
	public void addDay(int numberOfDays) {
		int d = this.day + numberOfDays;
		int daysInMonth;
		if (numberOfDays > 0) {
			daysInMonth = getDaysInMonth();
			while (d > daysInMonth) {
				addMonth(1);
				d -= daysInMonth;
				daysInMonth = getDaysInMonth();
			}
		}
		while (d < 1) {
			addMonth(-1);
			daysInMonth = getDaysInMonth();
			d += daysInMonth;
		}
		setDay( d );
	}
	
	
	/**
	 * Advances this time point by the specified number of months.
	 * NOTE: When the current day is to high for new month (e.g. when moving from 30th of January to February), 
	 * then the day setting will be adjusted to the highest maximum day of the increased month.
	 * When increasing the month from December, the year will also be increased automatically.
	 * 
	 * @param numberOfMonths the number of months that should be added, use a negative number to subtract months
	 */
	public void addMonth(int numberOfMonths) {
		int m = this.month;
		m += numberOfMonths;
		while (m > Calendar.DECEMBER) {
			m -= 12;
			addYear(1);
		}
		while (m < 0) {
			m += 12;
			addYear( -1 );
		}
		setMonth( m );
		int daysInMonth = getDaysInMonth();
		if (this.day > daysInMonth) {
			setDay( daysInMonth );
		}
	}
	
	/**
	 * Adds the specified number of years to this point in time.
	 * @param numberOfYears the number of years that should be added, use a negative integer to subtract years
	 */
	public void addYear(int numberOfYears) {
		setYear( this.year + numberOfYears );
	}
	
	/**
	 * Adds another TimePoint to this TimePoint
	 * 
	 * @param tp the timepoint that should be added
	 */
	public void add( TimePoint tp ) {
		if (equalsTimeZone(tp)) {
			addMillisecond( tp.millisecond );
			addSecond( tp.second );
			addHour( tp.hour );
			addDay( tp.day );
			addMinute( tp.minute );
			addMonth( tp.month );
			addYear( tp.year );
		} else {
			setDate( getTimeInMillis() + tp.getTimeInMillis() );
		}
		
	}
	
	/**
	 * Subtracts the given TimePoint from this TimePoint
	 * @param tp the timepoint that should be subtracted
	 */
	public void subtract( TimePoint tp ) {
		if (equalsTimeZone(tp)) {
			addMillisecond( -tp.millisecond );
			addSecond( -tp.second );
			addMinute( -tp.minute );
			addHour( -tp.hour );
			addDay( -tp.day );
			addMonth( -tp.month );
			addYear( -tp.year );
		} else {
			setDate( getTimeInMillis() - tp.getTimeInMillis() );
		}
	}
	
	/**
	 * Retrieves the (positive) difference between this TimePoint and the specified one.
	 * This method has no sideeffect on either the given or this TimePoint.
	 * 
	 * @param tp the TimePoint to which this one is compared
	 * @return the positive difference, i.e. there will be no negative numbers in the result.
	 */
	public TimePoint difference( TimePoint tp) {
		TimePoint diff;
		if (tp.isBefore(this)) {
			diff = new TimePoint(this);
			diff.subtract(tp);
		} else {
			diff = new TimePoint(tp);
			diff.subtract(this);
		}
		return diff;
	}


	/**
	 * Retrieves the day within the month from 1 to 31.
	 * @return the day the day of the month of this point in time
	 */
	public int getDay() {
		return this.day;
	}
	
	/**
	 * Sets the day within the month from 1 to 31.
	 * @param day the day of the month of this point in time
	 * @throws IllegalArgumentException when the day is not valid, note that no check of the calendary days within the given TimePoint is made here, as the month might be also changed laster onwards
	 */
	public void setDay( int day) {
		if (day < 1 || day > 31) {
			throw new IllegalArgumentException("for " + day);
		}
		this.day = day;
		this.resetCalendar = true;
	}


	/**
	 * @return the hour
	 */
	public int getHour() {
		return this.hour;
	}

	public void setHour( int hour) {
		if (hour < 0 || hour > 23) {
			throw new IllegalArgumentException("for " + hour);
		}
		this.hour = hour;
		this.resetCalendar = true;
	}
	
	/**
	 * @return the minute
	 */
	public int getMinute() {
		return this.minute;
	}

	public void setMinute( int minute) {
		if (minute < 0 || minute > 59) {
			throw new IllegalArgumentException();
		}
		this.minute = minute;
		this.resetCalendar = true;
	}
	
	/**
	 * @return the second
	 */
	public int getSecond() {
		return this.second;
	}
	
	/**
	 * Sets the second of this point in time
	 * Typically the second is a value between 0 and 59, however in some circumstances there are leap seconds, in that case '60' is also an allowed value.
	 * @param second the second
	 * @throws IllegalArgumentException when the second is below 0 or higher than 60
	 */
	public void setSecond(int second) {
		if (second < 0 || second > 60) {
			throw new IllegalArgumentException();
		}
		this.second = second;
	}

	/**
	 * @return the millisecond
	 */
	public int getMillisecond() {
		return this.millisecond;
	}
	
	/**
	 * Sets the milliseconds of this point in time.
	 * @param millisecond the milliseconds between 0 and 999
	 * @throws IllegalArgumentException when the specified millisecond is not valid [0..999]
	 */
	public void setMillisecond(int millisecond) {
		if (millisecond < 0 || millisecond > 999) {
			throw new IllegalArgumentException();
		}
		this.millisecond = millisecond;
	}
	
	/**
	 * Tests whether this point in time refers to the same hour as the specified TimePoint.
	 * Note that timezones are ignored for the comparison.
	 * @param tp the other TimePoint that should be compared to this one
	 * @return true when both this and the other TimePoint are within the same hour, day, month and year
	 */
	public boolean equalsHour( TimePoint tp ) {
		return (
				this.hour == tp.hour
				&& this.day == tp.day
				&& this.month == tp.month
				&& this.year == tp.year
				);
	}

	/**
	 * Tests whether this point in time refers to the same day as the specified TimePoint.
	 * Note that timezones are ignored for the comparison.
	 * @param tp the other TimePoint that should be compared to this one
	 * @return true when both this and the other TimePoint are within the same day, month and year
	 */
	public boolean equalsDay( TimePoint tp ) {
		return (
				this.day == tp.day
				&& this.month == tp.month
				&& this.year == tp.year
				);
	}
	
	/**
	 * Tests whether this point in time refers to the same month as the specified TimePoint.
	 * Note that timezones are ignored for the comparison.
	 * @param tp the other TimePoint that should be compared to this one
	 * @return true when both this and the other TimePoint are within the same month and year
	 */
	public boolean equalsMonth( TimePoint tp ) {
		return (
				this.month == tp.month
				&& this.year == tp.year
				);
	}


	/**
	 * Tests whether this point in time refers to the same month as the specified TimePoint.
	 * Note that timezones are ignored for the comparison.
	 * @param tp the other TimePoint that should be compared to this one
	 * @return true when both this and the other TimePoint are within the same month and year
	 */
	public boolean equalsYear( TimePoint tp ) {
		return (
				this.year == tp.year
				);
	}


	/**
	 * Tests whether this point in time refers to the same one as the specified TimePoint in the given scope context.
	 * Note that timezones are ignored for the comparison.
	 * @param tp the other TimePoint that should be compared to this one
	 * @param scope the dimension for the comparison
	 * @return true when the this time point is the same one as the given one in the provided scope.
	 * @see TimePeriod#SCOPE_MILLISECOND
	 * @see TimePeriod#SCOPE_SECOND
	 * @see TimePeriod#SCOPE_HOUR
	 * @see TimePeriod#SCOPE_DAY
	 * @see TimePeriod#SCOPE_MONTH
	 * @see TimePeriod#SCOPE_YEAR
	 */
	public boolean equals( TimePoint tp, int scope ) {
		boolean result = (this.year == tp.year);
		if (result && (scope <= TimePeriod.SCOPE_MONTH)) {
			result = (this.month == tp.month);
			if (result && (scope <= TimePeriod.SCOPE_DAY)) {
				result = (this.day == tp.day);
				if (result && (scope <= TimePeriod.SCOPE_HOUR)) {
					result = (this.hour == tp.hour);
					if (result && (scope <= TimePeriod.SCOPE_MINUTE)) {
						result = (this.minute == tp.minute);
						if (result && (scope <= TimePeriod.SCOPE_SECOND)) {
							result = (this.second == tp.second);
							if (result && (scope <= TimePeriod.SCOPE_MILLISECOND)) {
								result = (this.millisecond == tp.millisecond);
							}
						}
					}
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof TimePoint) {
			TimePoint tp = (TimePoint) o;
			if (equalsTimeZone(tp)) {
				return (this.year == tp.year) && (this.month == tp.month) && (this.day == tp.day) 
						&& (this.hour == tp.hour) && (this.minute == tp.minute) 
						&& (this.second == tp.second) && (this.millisecond == tp.millisecond);
			} else {
				return getAsCalendar().equals(tp.getAsCalendar());
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int hashCode = (this.year | this.month) | ((this.day | this.hour) >> 8) | ((this.minute | this.second | this.millisecond) >> 16);
		if (this.timeZone != null) {
			hashCode |= this.timeZone.getID().hashCode();
		}
		return hashCode;
	}

	/**
	 * Retrieves the GMT time in milliseconds since 01.01.1970 0:00.
	 * @return the time in milliseconds
	 */
	public long getTimeInMillis() {
		return getAsDate().getTime();
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version > VERSION) {
			throw new IOException("invalid version " + version);
		}
		boolean notNull = in.readBoolean();
		if (notNull) {
			this.timeZone = TimeZone.getTimeZone( in.readUTF() );
		}
		this.year = in.readInt();
		this.month = in.readByte();
		this.day = in.readByte();
		this.hour = in.readByte();
		this.minute = in.readByte();
		this.second = in.readByte();
		this.millisecond = in.readShort();
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		boolean notNull = (this.timeZone != null);
		out.writeBoolean( notNull );
		if (notNull) {
			out.writeUTF( this.timeZone.getID() );
		}
		out.writeInt( this.year );
		out.writeByte( this.month );
		out.writeByte( this.day );
		out.writeByte( this.hour );
		out.writeByte( this.minute );
		out.writeByte( this.second );
		out.writeShort( this.millisecond );
	}

	/**
	 * Retrieves the weekday of this TimePoint
	 * @return the weekday
	 * @see Calendar#MONDAY etc
	 */
	public int getDayOfWeek() {
		return getAsCalendar().get(Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * Retrieves the days within the month of this TimePoint
	 * @return the number of days within this month
	 */
	public int getDaysInMonth() {
		int daysInMonth = DAYS_PER_MONTH[ this.month ];
		if (daysInMonth == 28) {
			// we need to check if this February has 28 or 29 days: 
			Calendar cal = getAsCalendar();
			int calMonth = cal.get( Calendar.MONTH );
			if (calMonth != Calendar.FEBRUARY) {
				// we have used an illegal date, try to revert this:
				cal.set( Calendar.DAY_OF_MONTH, 1);
				cal.set( Calendar.MONTH, Calendar.FEBRUARY);
				this.resetCalendar = true;
			}
			// this is February, check for leap year:
			int dayOfMonth = cal.get( Calendar.DAY_OF_MONTH );
			if (dayOfMonth == 29) { // okay, this is easy ;-)
				daysInMonth = 29;
			} else {
				cal.set(Calendar.DAY_OF_MONTH, 1);
				long addedTime = 29L * 24 * 60 * 60 * 1000;
				Date testDate = new Date( cal.getTime().getTime() + addedTime );
				cal.setTime( testDate );
				this.resetCalendar = true;
				if (cal.get( Calendar.DAY_OF_MONTH) == 29) {
					daysInMonth = 29;
				}
			}
		}
		return daysInMonth;
	}

	/**
	 * @deprecated
	 * @see #getTimeInMillis()
	 */
	public long getTime() {
		return getTimeInMillis();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append( this.year ).append('-');
		if( this.month < 9) {
			buffer.append('0');
		}
		buffer.append( (this.month+1)).append('-');
		if (this.day < 10) {
			buffer.append('0');
		}
		buffer.append(this.day);
		if (this.hour != 0 || this.minute != 0 || this.second != 0 || this.millisecond != 0) {
			buffer.append(' ');
			if (this.hour < 10) {
				buffer.append('0');
			}
			buffer.append( this.hour ).append(':');
			if (this.minute < 10) {
				buffer.append('0');
			}
			buffer.append( this.minute ).append(':');
			if (this.second < 10) {
				buffer.append('0');
			}
			buffer.append( this.second ).append(':');
			buffer.append( this.millisecond );
		}
		if (this.timeZone != null) {
			buffer.append(": ").append( this.timeZone.getID() );
		}
		buffer.append(" ~ ").append( super.toString() );
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		TimePoint t1 = (TimePoint) o1;
		return t1.compareTo(o2);
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.util.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object obj) {
		TimePoint tp = (TimePoint) obj;
		int result;
		if (equals(tp)) {
			result = 0;
		} else if (isBefore(tp)) {
			result = -1;
		} else {
			result = 1;
		}
		return result; 
	}
	
	/**
	 * Returns the current time point in a RFC822-compatible format (YYYY-MM-dd'T'HH:MM:SSZZ , where ZZ = +/-HHMM )
	 * @return the current time point in a RFC822-compatible format
	 */
	public String toRfc822() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( this.year ).append('-');
		if( this.month < 9) {
			buffer.append('0');
		}
		buffer.append( (this.month+1)).append('-');
		if (this.day < 10) {
			buffer.append('0');
		}
		buffer.append(this.day);
		buffer.append('T');
		if (this.hour < 10) {
			buffer.append('0');
		}
		buffer.append( this.hour ).append(':');
		if (this.minute < 10) {
			buffer.append('0');
		}
		buffer.append( this.minute ).append(':');
		if (this.second < 10) {
			buffer.append('0');
		}
		buffer.append( this.second );

		if (this.timeZone != null) {
			int rawOffsetMinutes = this.timeZone.getRawOffset() / 60000;
			if ( rawOffsetMinutes > 0 ) {
				buffer.append("+");
			} else {
				buffer.append("-");
				rawOffsetMinutes *= -1;
			}
			int offsetMinutes = rawOffsetMinutes % 60 ;
			int offsetHours = rawOffsetMinutes / 60;			
			if ( offsetHours < 10 ) {
				buffer.append('0');
			}
			buffer.append(offsetHours);
			if ( offsetMinutes < 10 ) {
				buffer.append('0');
			}
			buffer.append(offsetMinutes);
		} else {
			buffer.append("-0000");
		}
		return buffer.toString();
	}
	
	/**
	 * Returns the current time point in a RFC3339-compatible format (YYYY-MM-dd'T'HH:MM:SSZZ , where ZZ = +/-HH:MM )
	 * @return the current time point in a RFC3339-compatible format
	 */
	public String toRfc3339() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( this.year ).append('-');
		if( this.month < 9) {
			buffer.append('0');
		}
		buffer.append( (this.month+1)).append('-');
		if (this.day < 10) {
			buffer.append('0');
		}
		buffer.append(this.day);
		buffer.append('T');
		if (this.hour < 10) {
			buffer.append('0');
		}
		buffer.append( this.hour ).append(':');
		if (this.minute < 10) {
			buffer.append('0');
		}
		buffer.append( this.minute ).append(':');
		if (this.second < 10) {
			buffer.append('0');
		}
		buffer.append( this.second );

		if (this.timeZone != null) {
			int rawOffsetMinutes = this.timeZone.getRawOffset() / 60000;
			if ( rawOffsetMinutes > 0 ) {
				buffer.append("+");
			} else {
				buffer.append("-");
				rawOffsetMinutes *= -1;
			}
			int offsetMinutes = rawOffsetMinutes % 60 ;
			int offsetHours = rawOffsetMinutes / 60;			
			if ( offsetHours < 10 ) {
				buffer.append('0');
			}
			buffer.append(offsetHours);
			buffer.append(':');
			if ( offsetMinutes < 10 ) {
				buffer.append('0');
			}
			buffer.append(offsetMinutes);
		} else {
			buffer.append("-00:00");
		}
		return buffer.toString();
	}

	/**
	 * Parses the given text in RFC3339 format.
	 * 
	 * @param dateTimeText the date time text as defined by RFC3339, e.g. "2010-12-19T16:39:57-08:00"
	 * @return the parsed TimePoint represented by the given dateTime text
	 * @see "http://tools.ietf.org/html/rfc3339"
	 * @throws IllegalArgumentException when the text could not be parsed
	 */
	public static TimePoint parseRfc3339(String dateTimeText) {
		try {
			int year = Integer.parseInt( dateTimeText.substring(0, 4 ));
			int month = Integer.parseInt(dateTimeText.substring( 5, 7)) - 1;
			int day = Integer.parseInt(dateTimeText.substring( 8, 10));
			int hour = 0;
			int minute = 0;
			int second = 0;
			int millisecond = 0;
			TimeZone timeZone = null;
			if (dateTimeText.length() > 10){
				hour = Integer.parseInt(dateTimeText.substring( 11, 13));
				minute = Integer.parseInt( dateTimeText.substring( 14, 16) );
				if (dateTimeText.length() > 17) {
					second = Integer.parseInt( dateTimeText.substring( 17, 19) );
					if (dateTimeText.length() > 19) {
						char c = dateTimeText.charAt(19);
						int index = 20;
						if (c == '.') {
							// this is a fraction of a second
							long divider = 1;
							while (dateTimeText.length() > index && Character.isDigit(dateTimeText.charAt(index)) ) {
								index++;
								divider *= 10;
							}
							millisecond = (int)((1000L * Long.parseLong( dateTimeText.substring( 20, index) )) / divider);
							c = dateTimeText.charAt(index);
							index++;
						}
						if (c == 'Z') {
							// this is a UTC timezone, use GMT:
							timeZone = TimeZone.getTimeZone("GMT");
						} else if (c == '+' ||c == '-'){
							// this is a timezone definition
							int tzHour = Integer.parseInt(dateTimeText.substring(index, index + 2));
							int tzMinute = Integer.parseInt(dateTimeText.substring(index + 3, index + 5));
							long rawOffset =      tzMinute * 60L * 1000L
											+ tzHour * 60L * 60L * 1000L;
							if (c == '-') {
								rawOffset *= -1;
							}
							String[] ids = TimeZone.getAvailableIDs();
							long minDiff = Long.MAX_VALUE;
							TimeZone minDiffTimeZone = null;
							for (int i = 0; i < ids.length; i++) {
								TimeZone tz = TimeZone.getTimeZone( ids[i] );
								long diff = tz.getRawOffset() - rawOffset;
								if (diff == 0) {
									// found the correct one:
									minDiffTimeZone = tz;
									break;
								}
								if (diff < minDiff) {
									minDiffTimeZone = tz;
									minDiff = diff;
								}
							}
							if (minDiffTimeZone != null) {
								timeZone = minDiffTimeZone;
							}
						}
					}
				}
			}
			TimePoint tp = new TimePoint(year, month, day, hour, minute, second, millisecond, timeZone);
			return tp;
		} catch (Exception e) {
			throw new IllegalArgumentException("for " + dateTimeText + ": " + e);
		}
	}



}
