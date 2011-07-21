package de.enough.polish.calendar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import de.enough.polish.io.Externalizable;
import de.enough.polish.util.TimePeriod;
import de.enough.polish.util.TimePoint;

/**
 * Allows to add complex repeat rules to CalendarEntries or other events.
 * 
 * @author Robert Virkus
 */
public class EventRepeatRule  implements Externalizable {
	
	private static final int VERSION = 101;
	
	/**
	 * Events that are repeated yearly
	 */
	public static final TimePoint INTERVAL_YEARLY = new TimePoint( 1, 0, 0 ); // year-month-day
	/**
	 * Events that are repeated monthly
	 */
	public static final TimePoint INTERVAL_MONTHLY = new TimePoint( 0, 1, 0 ); // year-month-day
	/**
	 * Events that are repeated weekly
	 */
	public static final TimePoint INTERVAL_WEEKLY = new TimePoint( 0, 0, 7 ); // year-month-day

	/**
	 * A simple rule for events that are repeated each year on the same date, e.g. anniversaries, birthdays, Christmas, and so on.
	 */
	public static final EventRepeatRule RULE_YEARLY = new EventRepeatRule( INTERVAL_YEARLY );

	/**
	 * A simple rule for events that are repeated each month on the same date.
	 */
	public static final EventRepeatRule RULE_MONTHLY = new EventRepeatRule( INTERVAL_MONTHLY );
	

	/**
	 * the interval for repeats, typically yearly
	 */
	private TimePoint interval = INTERVAL_YEARLY;
	
	/**
	 * The weekday of the event, e.g. it always falls on a Monday (Calendar.MONDAY)
	 */
	private int weekday = -1;
	
	/**
	 * Which weekday of the given month does match, e.g. the first one (1), second one (2) or last one (-1).
	 */
	private int weekdayMatchInMonth;
	
	/**
	 * Last possible date until events can be repeated
	 */
	private TimePoint untilDate;

		
	/**
	 * Creates a new empty repeat rule.
	 */
	public EventRepeatRule() {
		// use methods for defining the repeat rule
	}
	
	/**
	 * Creates a new interval based rule.
	 * @param interval the interval, e.g. new TimePoint( 1, 0, 0 ) for a yearly interval
	 * @see #INTERVAL_YEARLY
	 * @see #INTERVAL_MONTHLY
	 * @see #INTERVAL_WEEKLY
	 */
	public EventRepeatRule(TimePoint interval) {
		this.interval = interval;
	}


	/**
	 * Creates a yearly repeat rule for a certain weekday
	 * @param weekday the weekday, e.g. Calendar.MONDAY
	 * @param match the day within the month, e.g. the first (1), second (2) or last (-1) weekday
	 */
	public EventRepeatRule( int weekday, int match ) {
		this( INTERVAL_YEARLY, weekday, match);
	}
	
	/**
	 * Creates a repeat rule for a certain weekday
	 * 
	 * @param interval the interval for this rule, e.g. new TimePoint( 0, 1, 0 ) for a monthly interval
	 * @param weekday the weekday, e.g. Calendar.MONDAY
	 * @param match the day within the month, e.g. the first (1), second (2) or last (-1) weekday
	 * @see #INTERVAL_YEARLY
	 * @see #INTERVAL_MONTHLY
	 * @see #INTERVAL_WEEKLY
	 */
	public EventRepeatRule( TimePoint interval, int weekday, int match ) {
		if (weekday < Calendar.SUNDAY || weekday > Calendar.SATURDAY || match == 0 || interval == null) {
			throw new IllegalArgumentException();
		}
		this.interval = interval;
		this.weekday = weekday;
		this.weekdayMatchInMonth = match;
	}
	

	/**
	 * Retrieves the repetition interval of this event.
	 * 
	 * @return returns interval of repeat
	 * @see #INTERVAL_YEARLY
	 * @see #INTERVAL_MONTHLY
	 * @see #INTERVAL_WEEKLY
	 */
	public TimePoint getInterval() {
		return this.interval;
	}

	/**
	 * Sets the interval of this repeat rule
	 * @param interval the new interval, e.g. new TimePoint( 0, 0, 7) for a weekly repeat rule
	 * @see #INTERVAL_YEARLY
	 * @see #INTERVAL_MONTHLY
	 * @see #INTERVAL_WEEKLY
	 */
	public void setInterval(TimePoint interval) {
		this.interval = interval;
	}
	
	

	/**
	 * Retrieves the weekday for which the event should be repeated
	 * @return the weekday, e.g. Calendar.MONDAY; -1 when this repeat rule should not be repeated on a certain weekday of a month
	 * @see #getWeekdayMatch()
	 */
	public int getWeekday() {
		return this.weekday;
	}



	/**
	 * Sets the weekday for which the event should be repeated
	 * @param weekday the weekday, e.g. Calendar.MONDAY or -1 when this repeat rule should not be repeated on a certain weekday of a month
	 * @see #setWeekdayMatch(int)
	 */
	public void setWeekday(int weekday) {
		this.weekday = weekday;
	}



	/**
	 * Specifies the matching weekday, e.g. the first (1), second (2) or last (-1) of a given month
	 * @return the match the matching weekday
	 * @see #getWeekday()
	 */
	public int getWeekdayMatch() {
		return this.weekdayMatchInMonth;
	}



	/**
	 * Sets the matching weekday, e.g. the first (1), second (2) or last (-1) of a given month
	 * @param match the matching weekday
	 * @see #setWeekday(int)
	 */
	public void setWeekdayMatch(int match) {
		this.weekdayMatchInMonth = match;
	}


	/**
	 * Retrieves the last date until an event should be repeated
	 * @return returns the end of repeat
	 */
	public TimePoint getUntilDate() {
		return this.untilDate;
	}

	/**
	 * Defines the last date until which an event should be repeated
	 * @param untilDate the last possible recurring date
	 */
	public void setUntilDate(TimePoint untilDate) {
		this.untilDate = untilDate;
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version != VERSION) {
			throw new IOException("unknown version " + version);
		}
		this.interval = new TimePoint(in);
		this.weekday = in.readInt();
		this.weekdayMatchInMonth = in.readInt();
		boolean isNotNull = in.readBoolean();
		if (isNotNull) {
			this.untilDate = new TimePoint( in );
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		this.interval.write(out);
		out.writeInt( this.weekday );
		out.writeInt( this.weekdayMatchInMonth );
		boolean isNotNull = (this.untilDate != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			this.untilDate.write(out);
		}

	}

	/**
	 * Retrieves the next date for the given event within the specified period
	 * @param entry the calendar entry event to which the next repeating event should be found
	 * @param period the allowed time period
	 * @return the next time point, null if there is none matching.
	 */
	public TimePoint getNextDate( CalendarEntry entry, TimePeriod period ) {
//		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//		System.out.println("getNextDate for entry " + entry.getSummary() + ", start=" + entry.getStartDate() + ", period=" + period + ", interval=" + this.interval);
//		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		TimePoint periodStart = period.getStart();
		if (this.untilDate != null && this.untilDate.isBefore(periodStart)) {
			return null;
		}
		TimePoint nextDate = new TimePoint( entry.getStartDate() );
		TimePoint periodEnd = period.getEnd();
		while (!period.matches(nextDate) && !nextDate.isAfter(periodEnd)) {
			nextDate.add( this.interval );
//			System.out.println("(1) added interval to date: " + nextDate + ", interval=" + this.interval);
		}
		if (this.weekday != -1 && this.weekdayMatchInMonth != 0) {
			// now find a the matching weekday, e.g. the first Tuesday of the month or the last Thursday (see weekday and weekdayMatchInMonth)
			int offset = 0;
			boolean addInterval = false;
			if (this.weekdayMatchInMonth > 0) {
				do {
					if (addInterval) {
						nextDate.add( this.interval );
//						System.out.println("(2) added interval to date: " + nextDate);
					}
					nextDate.setDay(1);
					int currentWeekday = nextDate.getDayOfWeek();
					offset = (currentWeekday <= this.weekday) ? this.weekday - currentWeekday : 7 + this.weekday - currentWeekday;
					offset += (this.weekdayMatchInMonth - 1)*7;
					addInterval = true;
					nextDate.setDay( 1 + offset);
//					if (!period.matches(nextDate)) {
//						System.out.println("(2) nextDate " + nextDate + " does not match period " + period);
//					}
				} while (!period.matches(nextDate) && !nextDate.isAfter(periodEnd));
			} else {
				int daysInMonth = nextDate.getDaysInMonth();
				do {
					if (addInterval) {
						nextDate.add( this.interval );
//						System.out.println("(3) added interval to date: " + nextDate);
					}
					nextDate.setDay( daysInMonth );
					int currentWeekday = nextDate.getDayOfWeek();
					offset = (currentWeekday >= this.weekday) ? currentWeekday - this.weekday : 7 - this.weekday + currentWeekday;
					offset -= (this.weekdayMatchInMonth + 1)*7;
					addInterval = true;
					nextDate.setDay( daysInMonth - offset);
//					if (!period.matches(nextDate)) {
//						System.out.println("(3) nextDate " + nextDate + " does not match period " + period);
//					}
				} while (!period.matches(nextDate) && !nextDate.isAfter(periodEnd));
			}
		}
//		System.out.println("nextDate (almos final): " + nextDate);
		if ((this.untilDate != null) && (nextDate.isAfter( this.untilDate ) ) ) {
			nextDate = null;
		} else  if (!period.matches(nextDate)) {
			nextDate = null;
		}

		return nextDate;
	}
}
