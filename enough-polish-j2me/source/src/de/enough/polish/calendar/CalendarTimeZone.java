/**
 * 
 */
package de.enough.polish.calendar;

import de.enough.polish.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Ramakrishna
 * @author Nagendra Sharma
 * 
 *
 */
public class CalendarTimeZone extends TimeZone implements Serializable {
	
	/**
	 * field to contain id of time zone 
	 */
	private String id;
	
	/**
	 * field to contain location of time zone
	 */
	private String location;
	
	/**
	 * field to contain offsetfrom of time zone
	 */
	private String offsetFrom;
	
	/**
	 * field to contain offsetto of time zone
	 */
	private String offsetTo;
	
	/**
	 * field to contain name of time zone
	 */
	private String name;
	
	/**
	 * field to contain starting date of time zone
	 */
	private Date startDate;
	
	
	
	public int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis) {
		return getNativeTimeZone().getOffset( era, year, month, day, dayOfWeek, millis);
	}
	
	public int getRawOffset() {
		return getNativeTimeZone().getRawOffset();
	}

	public boolean useDaylightTime() {
		return getNativeTimeZone().useDaylightTime();
	}
	
	private TimeZone getNativeTimeZone() {
		return TimeZone.getTimeZone("PST");
	}

	/**
	 * @return returns id of time zone
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * setter method for id of time zone
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return returns location of time zone
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * setter method for location of time zone
	 * @param location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * 
	 * @return returns offsetFrom of time zone
	 */
	public String getOffsetFrom() {
		return this.offsetFrom;
	}

	/**
	 * setter method for offsetFrom of time zone
	 * @param offsetFrom
	 */
	public void setOffsetFrom(String offsetFrom) {
		this.offsetFrom = offsetFrom;
	}

	/**
	 * @return returns offsetTo of time zone
	 */
	public String getOffsetTo() {
		return this.offsetTo;
	}

	/**
	 * setter method for offsetTo of time zone
	 * @param offsetTo
	 */
	public void setOffsetTo(String offsetTo) {
		this.offsetTo = offsetTo;
	}

	/**
	 * @return returns name of time zone
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * setter method for name of time zone
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return returns starting date of time zone
	 */
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * setter method for starting date of time zone
	 * @param startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	

	//#if polish.JavaSE || polish.android
	public void setRawOffset(int offsetMillis)
	{
		
	}
	
	/**
	 * Just a a dummy implementation for Java SE compatibility
	 * @param date the date
	 * @return false 
	 */
	public boolean 	inDaylightTime(Date date) {
		return false;
	}
	//#endif
}
