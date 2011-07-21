/*
 * Created on Jun 29, 2010 at 11:13:38 PM.
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

import de.enough.polish.io.Externalizable;

/**
 * <p>Represents a period of time</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TimePeriod
implements Externalizable
{
	/**
	 * Default match scope that compares the millisecond settings of timepoints.
	 * @see #setMatchScope(int)  
	 * @see #matches(TimePoint, int)
	 */
	public static int SCOPE_MILLISECOND = 0;

	/**
	 * Match scope that compares the second settings of timepoints.
	 * @see #setMatchScope(int)  
	 * @see #matches(TimePoint, int)
	 */
	public static int SCOPE_SECOND = 1;
	
	/**
	 * Match scope that compares the minute settings of timepoints.
	 * @see #setMatchScope(int)  
	 * @see #matches(TimePoint, int)
	 */
	public static int SCOPE_MINUTE = 2;
	
	/**
	 * Match scope that compares the hour settings of timepoints.
	 * @see #setMatchScope(int)  
	 * @see #matches(TimePoint, int)
	 */
	public static int SCOPE_HOUR = 3;
	/**
	 * Match scope that compares the day settings of timepoints.
	 * @see #setMatchScope(int)  
	 * @see #matches(TimePoint, int)
	 */
	public static int SCOPE_DAY = 4;
	/**
	 * Match scope that compares the month settings of timepoints.
	 * @see #setMatchScope(int)  
	 * @see #matches(TimePoint, int)
	 */
	public static int SCOPE_MONTH = 5;
	/**
	 * Match scope that compares the year settings of timepoints.
	 * @see #setMatchScope(int)  
	 * @see #matches(TimePoint, int)
	 */
	public static int SCOPE_YEAR = 6;



	private static final int VERSION = 100;

	private TimePoint start;
	private boolean includeStart;
	private TimePoint end;
	private boolean includeEnd;

	private int matchScope;
	
	/**
	 * Creates a new uninitialized time period
	 */
	public TimePeriod() {
		// just used for serialization
	}
	
	/**
	 * Creates a new time period that excludes the start, but includes the end time ]start...end]
	 * 
	 * @param start the start time in RFC 3339 format
	 * @param end the end time in RFC 3339 format
	 * @see TimePoint#parseRfc3339(String)
	 */
	public TimePeriod( String start, String end ) {
		this( start, false, end, true );
	}
	
	/**
	 * Creates a new time period
	 * 
	 * @param start the start time in RFC 3339 format
	 * @param includeStart true when this should include the start time, false when the start time should be excluded 
	 * @param end the end time in RFC 3339 format
	 * @param includeEnd true when this should include the end time, false when the end time should be excluded
	 */
	public TimePeriod( String start, boolean includeStart, String end, boolean includeEnd ) {
		this( TimePoint.parseRfc3339(start), includeStart, TimePoint.parseRfc3339(end), includeEnd );
	}

	/**
	 * Creates a new time period that excludes the start, but includes the end time ]start...end]
	 * 
	 * @param start the start time
	 * @param end the end time
	 */
	public TimePeriod( TimePoint start, TimePoint end ) {
		this( start, false, end, true );
	}
	
	/**
	 * Creates a new time period
	 * 
	 * @param start the start time
	 * @param includeStart true when this should include the start time, false when the start time should be excluded 
	 * @param end the end time
	 * @param includeEnd true when this should include the end time, false when the end time should be excluded
	 */
	public TimePeriod( TimePoint start, boolean includeStart, TimePoint end, boolean includeEnd ) {
		this.start = start;
		this.includeStart = includeStart;
		this.end = end;
		this.includeEnd = includeEnd;
	}
	
	/**
	 * Creates a new time period by copying the given one
	 * @param period the period which should be cloned
	 */
	public TimePeriod(TimePeriod period) {
		this.start = period.start;
		this.includeStart = period.includeStart;
		this.end = period.end;
		this.includeEnd = period.includeEnd;
	}
	
	
	/**
	 * Sets the start point
	 * @param start the start point
	 */
	public void setStart( TimePoint start ) {
		this.start = start;
	}

	
	/**
	 * Sets the start point
	 * @param start the start point
	 * @param includeStart true when the start point should be included in this period
	 */
	public void setStart( TimePoint start, boolean includeStart ) {
		this.start = start;
		this.includeStart = includeStart;
	}
	
	
	/**
	 * Sets the end point
	 * @param end the end point
	 */
	public void setEnd( TimePoint end ) {
		this.end = end;
	}

	
	/**
	 * Sets the end point
	 * @param end the end point
	 * @param includeEnd true when the end point should be included in this period
	 */
	public void setEnd( TimePoint end, boolean includeEnd ) {
		this.end = end;
		this.includeEnd = includeEnd;
	}

	/**
	 * Checks whether this given point in time falls within this period.
	 * @param timePoint the time point that should be checked
	 * @return true when the point in time falls within this period
	 */
	public boolean matches( TimePoint timePoint ){
		return matches( timePoint, this.matchScope );
	}
	
	/**
	 * Checks whether this given point in time falls within this period.
	 * @param timePoint the time point that should be checked
	 * @param scope the scope that is used for comparing time points
	 * @return true when the point in time falls within this period
	 */
	public boolean matches( TimePoint timePoint, int scope ){
		if (this.start != null) {
			if (timePoint.isBefore( this.start, scope )) {
				return false;
			}
			if (!this.includeStart && this.start.equals(timePoint, scope)) {
				return false;
			}
		}
		if (this.end != null) {
			if (timePoint.isAfter( this.end, scope )) {
				return false;
			}
			if (!this.includeEnd && this.end.equals(timePoint, scope)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Retrieves the start point of this period
	 * @return the start point in time
	 */
	public TimePoint getStart() {
		return this.start;
	}
	
	
	/**
	 * Retrieves the end point of this period
	 * @return the end point in time
	 */
	public TimePoint getEnd() {
		return this.end;
	}

	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version != VERSION) {
			throw new IOException("invalid version " + version);
		}
		this.start = new TimePoint(in);
		this.includeStart = in.readBoolean();
		this.end = new TimePoint(in);
		this.includeEnd = in.readBoolean();
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		this.start.write(out);
		out.writeBoolean( this.includeStart );
		this.end.write(out);
		out.writeBoolean(this.includeEnd);
	}

	/**
	 * Determines whether the start TimePoint should be included into this period
	 * @return true when it's included, false when excluded
	 */
	public boolean isIncludeStart() {
		return this.includeStart;
	}
	
	/**
	 * Determines whether the end TimePoint should be included into this period
	 * @return true when it's included, false when excluded
	 */
	public boolean isIncludeEnd() {
		return this.includeEnd;
	}
	
	/**
	 * Sets the scope that is used for the matches() method
	 * @param scope the scope
	 * @see #matches(TimePoint)
	 */
	public void setMatchScope( int scope ) {
		this.matchScope = scope;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(200);
		if (this.includeStart) {
			buffer.append('[');
		} else {
			buffer.append(']');
		}
		buffer.append( this.start );
		buffer.append(" -> ");
		buffer.append( this.end );
		if (this.includeEnd) {
			buffer.append(']');
		} else {
			buffer.append('[');
		}
		buffer.append( super.toString() );
		return buffer.toString();
	}

}
