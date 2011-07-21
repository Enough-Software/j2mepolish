/**
 * 
 */
package de.enough.polish.calendar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;
import de.enough.polish.util.TimePeriod;
import de.enough.polish.util.TimePoint;


/**
 * Calendar Entry class provides access to data in events provided in the calendar.
 * @author Ramakrishna
 * @author Nagendra Sharma
 * @author Robert Virkus (clean up)
 * 
 */
public class CalendarEntry implements Externalizable {
	
	private static final int VERSION = 103;

	/** this entry does not recur at all */
	public static final int REOCCURENCE_NONE = 0;
	/** this entry recurs daily */
	public static final int REOCCURENCE_DAILY = 1;
	/** this entry recurs weekly */
	public static final int REOCCURENCE_WEEKLY = 2;
	/** this entry recurs monthly */
	public static final int REOCCURENCE_MONTHLY = 3;
	/** this entry recurs yearly */
	public static final int REOCCURENCE_YEARLY = 4;
	
	/**
	 * For entries that are classified public.
	 */
	public static final String CLASS_PUBLIC = "PUBLIC";
	/**
	 * For entries that are classified private.
	 */
	public static final String CLASS_PRIVATE = "PRIVATE";
	/**
	 * For entries that are classified confidential.
	 */
	public static final String CLASS_CONFIDENTIAL = "CONFIDENTIAL";
	
	/**
	 * field to contain starting date of calendar event 
	 */
	private TimePoint startDate;
	
	/**
	 * field to contain ending date of calendar event
	 */
	private TimePoint endDate;
	
	
	/**
	 * field to contain whether is all day of calendar event
	 */
	private boolean isAllday;
	
	/**
	 * field to contain duration of minutes of calendar event
	 */
	private int durationInMinutes;
	
	/**
	 * field to contain reoccurence of calendar event
	 */
	private int simpleReoccurence;
	
	private int anniversaryYear;
	
	/**
	 * field to contain sequence of calendar event
	 */
	private int sequence;
	
	/**
	 * field to contain summary of calendar event
	 */
	private String summary;
	
	
	/**
	 * field to contain alarm interval value 
	*/
	private int alarm;
	
	/**
	 * field to contain notes of calendar entry
	 */
	private String notes;
	
	/**
	 * a collection variable to hold any device specific fields in the form of name/value pairs.
	 */
	private Hashtable otherFields;
	
	/**
	 * field to contain location of calendar event
	 */
	private String location;
	
	/**
	 * field to contain description of calendar event
	 */
	private String description;
	
	/**
	 * field to contain organizer of calendar event
	 */
	private String organizer;
	
	/**
	 * field to contain status of calendar event
	 */
	private String status;
	
	/**
	 * field to contain type of calendar event
	 */
	private int type;
	
	/**
	 * field to contain id of calendar event
	 */
	private String id;
	
	/**
	 * field to contain a users id of calendar event (e.g. for birthdays and anniversaries)
	 */
	private String userId;
	
	/**
	 * field to contain classType of calendar event
	 */
	private String classType;
	
	/**
	 * field to contain category of calendar event
	 */
	private CalendarCategory category;
	
	/**
	 * field to contain details of alarm 
	 */
	private CalendarAlarm calendarAlarm;


	/**
	 * field to contain the details for repeat rule
	 */
	private EventRepeatRule eventRepeatRule;
	
	
	/**
	 * field to contain timeZone of calendar event
	 */
	private TimeZone timeZone;
	
	private CalendarEntry parent;
	
	/**
	 * Field which contains the children of a repeating entry.
	 */
	private CalendarEntry[] children;

	private CalendarTextResolver textResolver;
	
	/**
	 * Constructor for CalendarEntry
	 */
	public CalendarEntry() {
        
	}

	public CalendarEntry(String summary, CalendarCategory category, int year, int month, int day){
		this(summary, category, year, month, day, null, CalendarEntry.REOCCURENCE_NONE);
	}
	
	public CalendarEntry(String summary, CalendarCategory category, int year, int month, int day, int reoccurence){
		this(summary, category, year, month, day, null, reoccurence);
	}
	
	public CalendarEntry(String summary, CalendarCategory category, int year, int month, int day, String description){
		this(summary, category, year, month, day, description, CalendarEntry.REOCCURENCE_NONE);
	}
	
	public CalendarEntry(String summary, CalendarCategory category, int year, int month, int day, String description, int reoccurence){
		this( summary, category, new TimePoint(year, month, day), description, reoccurence );
	}
	
	public CalendarEntry(String summary, CalendarCategory category, TimePoint start){
		this(summary, category, start, null, REOCCURENCE_NONE);
	}
	
	public CalendarEntry(String summary, CalendarCategory category, TimePoint start, int reoccurence){
		this(summary, category, start, null, reoccurence);
	}
	
	public CalendarEntry(String summary, CalendarCategory category, TimePoint start, String description){
		this(summary, category, start, null, CalendarEntry.REOCCURENCE_NONE);
	}
	
	public CalendarEntry(String summary, CalendarCategory category, TimePoint start, String description, int reoccurence){
		this.summary = summary;
		this.category = category;
		this.startDate = start;
		this.description = description;
		this.simpleReoccurence = reoccurence;
	}
	/**
	 * Creates a new CalendarEntry
	 * @param startDate
	 * @param description
	 */
	public CalendarEntry(TimePoint startDate,String description) {
		this.startDate = startDate;
		this.description = description;
	}
	
	/**
	 * Overloaded constructor of CalendarEntry to initialize the below fields
	 * @param startDate
	 * @param endDate
	 * @param isAllday
	 * @param category
	 * @param description
	 * @param timeZone
	 * @param durationInMinutes
	 */
	CalendarEntry(TimePoint startDate, TimePoint endDate, boolean isAllday,
			CalendarCategory category, String description, TimeZone timeZone,
			int durationInMinutes) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.isAllday = isAllday;
		this.category = category;
		this.description = description;
		this.timeZone = timeZone;
		this.durationInMinutes = durationInMinutes;
		
	}
	
	/**
	 * Overloaded constructor of CalendarEntry to initialize the below fields
	 * @param startDate
	 * @param endDate
	 * @param isAllday
	 * @param category
	 * @param description
	 * @param timeZone
	 * @param durationInMinutes
	 * @param location
	 * @param reoccurence
	 * @param organizer
	 */
	CalendarEntry(TimePoint startDate, TimePoint endDate, boolean isAllday,
			CalendarCategory category, String description, TimeZone timeZone,
			int durationInMinutes,String location,	int reoccurence,String organizer) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.isAllday = isAllday;
		this.category = category;
		this.description = description;
		this.timeZone = timeZone;
		this.durationInMinutes = durationInMinutes;
		this.location=location;
		this.simpleReoccurence=reoccurence;
		this.organizer=organizer;
		
	}
	
	/**
	 * Creates a new entry based on the specified one
	 * @param original the original entry of which all relevant settings are copied
	 */
	public CalendarEntry(CalendarEntry original) {
		this.alarm = original.alarm;
		this.calendarAlarm = original.calendarAlarm;
		this.category = original.category;
		this.classType = original.classType;
		this.description = original.description;
		this.durationInMinutes = original.durationInMinutes;
		this.endDate = original.endDate;
		this.eventRepeatRule = original.eventRepeatRule;
		this.id = original.id;
		this.isAllday = original.isAllday;
		this.location = original.location;
		this.notes = original.notes;
		this.organizer = original.organizer;
		this.otherFields = original.otherFields;
		this.simpleReoccurence = original.simpleReoccurence;
		this.sequence = original.sequence;
		this.startDate = original.startDate;
		this.status = original.status;
		this.summary = original.summary;
		this.timeZone = original.timeZone;
		this.type = original.type;
		this.userId = original.userId;
		this.textResolver = original.textResolver;
	}

	/**
	 * @return returns alarm setting of calendar entry
	 */
	public CalendarAlarm getCalendarAlarm() {
		return this.calendarAlarm;
	}

	/**
	 * @return returns category of calendar entry
	 */
	public CalendarCategory getCategory() {
		return this.category;
	}

	/**
	 * @return returns category of calendar entry
	 */
	public String getDescription() {
		if (this.textResolver != null) {
			return this.textResolver.resolveDescription(this.description, this);
		}
		return this.description;
	}
	
	/**
	 * @return returns starting date of calendar entry
	 */
	public TimePoint getStartDate() {
		return this.startDate;
	}
	
	/**
	 * @return returns ending date of calendar entry
	 */
	public TimePoint getEndDate() {
		return this.endDate;
	}
	
	
	/**
	 * @return returns local time zone of calendar entry
	 */
	public TimeZone getTimeZone() {
		return this.timeZone;
	}
	
	/**
	 * @return returns duration in minutes of calendar entry
	 */
	public int getDurationInMinutes() {
		return this.durationInMinutes;
	}
	
	/**
	 * @return returns organizer of calendar entry
	 */
	public String getOrganizer() {
		return this.organizer;
	}
	
	/**
	 * @return returns location of calendar entry
	 */
	public String getLocation() {
		return this.location;
	}
	
	/**
	 * @return returns sequence of calendar entry
	 */
	public int getSequence() {
		return this.sequence;
	}
	
	/**
	 * @return returns status of calendar entry
	 */
	public String getStatus() {
		return this.status;
	}
	
	/**
	 * Retrieves the type of this entry.
	 * This is an extension point that can be used by different implementations.
	 * @return returns type of calendar entry as set by the application
	 */
	public int getType() {
		return this.type;
	}
	
	/**
	 * Specifies a type of this entry.
	 * This can be used to implement different behavior. 
	 * @param type the implementation specific type
	 */
	public void setType(int type) {
		this.type = type;
	}


	/**
	 * @return returns isAllday of calendar entry
	 */
	public boolean isAllday() {
		return this.isAllday;
	}
	
		
	/**
	 * @return returns id of calendar entry
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Retrieves the class of this entry.
	 * By default entries are deemed to be public.
	 * 
	 * @return returns classType of this calendar entry
	 * @see #CLASS_PUBLIC
	 * @see #CLASS_PRIVATE
	 * @see #CLASS_CONFIDENTIAL
	 */
	public String getClassType() {
		return this.classType;
	}
	

	/**
	 * Sets the class of this entry
	 * 
	 * @param classType
	 * @see #CLASS_PUBLIC
	 * @see #CLASS_PRIVATE
	 * @see #CLASS_CONFIDENTIAL
	 */
	public void setClassType(String classType) {
		this.classType = classType;
	}

	
	/**
	 * @return returns summary of calendar entry
	 */
	public String getSummary() {
		
		return getSummary(null);
	}
	
	
	public String getSummary(TimePoint calendarTimePoint) {
		
		String returnSummary;
		
		if (calendarTimePoint != null && this.textResolver != null) {
			returnSummary =  this.textResolver.resolveSummary(this.summary, this, calendarTimePoint);
		} else {
			returnSummary = this.summary;
		}
		
		return returnSummary;
	}

	/**
	 * setter method for CalendarCategory
	 * @param category
	 */
	public void setCategory(CalendarCategory category) {
		this.category = category;
	}
	
	
	
	/**
	 * setter method for calendarAlarm
	 * @param calendarAlarm
	 */
	public void setCalendarAlarm(CalendarAlarm calendarAlarm) {
		this.calendarAlarm = calendarAlarm;
	}

	/**
	 * setter method to set starting date and duration in minutes for calendar entry
	 * @param startDate
	 * @param durationInMinutes
	 */
	public void setStartDate(TimePoint startDate, int durationInMinutes) {
		this.startDate = startDate;
		this.durationInMinutes = durationInMinutes;
	}

	/**
	 * setter method for event description
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * setter method for start date 
	 * @param startDate
	 */
	public void setStartDate(TimePoint startDate) {
		this.startDate = startDate;
	}

	/**
	 * setter method for end date
	 * @param endDate
	 */
	public void setEndDate(TimePoint endDate) {
		this.endDate = endDate;
	}
	
	
	
	/**
	 * setter method for local time zone
	 * @param timeZone
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	

	/**
	 * setter method for duration in minutes
	 * @param durationInMinutes
	 */
	public void setDurationInMinutes(int durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
	}
	
	

	/**
	 * setter method for organizer
	 * @param organizer
	 */
	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	
	/**
	 * setter method for location
	 * @param location
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	

	/**
	 * Specifies a simple reoccurence for this entry.
	 * Use setRepeat() for complex reoccurcence rules.
	 * 
	 * @param reoccurence the reoccurrence for this event.
	 * @see #REOCCURENCE_YEARLY
	 * @see #REOCCURENCE_MONTHLY
	 * @see #REOCCURENCE_WEEKLY
	 * @see #REOCCURENCE_DAILY
	 * @see #REOCCURENCE_NONE
	 * @see #setRepeat(EventRepeatRule)
	 */
	public void setReoccurence(int reoccurence) {
		this.simpleReoccurence = reoccurence;
	}

	/**
	 * Sets a repeat rule for this entry.
	 * 
	 * @param eventRepeatRule the new repeat rule
	 * @see #setReoccurence(int)
	 */
	public void setRepeat(EventRepeatRule eventRepeatRule) {
		this.eventRepeatRule = eventRepeatRule;
	}
	
	/**
	 * setter method for status
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	

	/**
	 * setter method for id
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * setter method for isAllday of calendar entry
	 * @param isAllday
	 */
	public void setAllday(boolean isAllday) {
		this.isAllday = isAllday;
	}

	
	/**
	 * setter method for sequence of calendar entry
	 * @param sequence
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	
	/**
	 * setter method for summary of calendar entry
	 * @param summary
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * Retrieves the event repeat rule for this entry
	 * @return returns the repeat rule of calendar entry
	 */
	public EventRepeatRule getRepeat() {
		EventRepeatRule rule = this.eventRepeatRule;
		if (rule == null && this.simpleReoccurence != REOCCURENCE_NONE) {
			switch (this.simpleReoccurence) {
			case REOCCURENCE_YEARLY:
				rule = EventRepeatRule.RULE_YEARLY;
				break;
			case REOCCURENCE_MONTHLY:
				rule = EventRepeatRule.RULE_MONTHLY;
				break;
			case REOCCURENCE_WEEKLY:
				rule = new EventRepeatRule(EventRepeatRule.INTERVAL_WEEKLY);
				break;
			case REOCCURENCE_DAILY:
				rule = new EventRepeatRule(new TimePoint( 0, 0, 1));
				break;
			}
		}
		return rule;
	}


	/**
	 * @return the alarm value
	 */
	public int getAlarm() {
		return this.alarm;
	}

	/**
	 * @param alarm sets the alarm to the given value 
	 */
	public void setAlarm(int alarm) {
		this.alarm = alarm;
	}

	/**
	 * @return notes gets the notes for this calendar entry  
	 */
	public String getNotes() {
		return this.notes;
	}

	/**
	 * @param notes sets the notes for this calendar entry
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Retrieves another field that is stored within this CalendarEntry
	 * @param name the name of the field key
	 * @return the value of that specified field, can be null
	 * @see #setField(String, Object)
	 */
	public Object getField(String name) {
		if (this.otherFields == null) {
			return null;
		}
		return this.otherFields.get(name);
	}

	/**
	 * Adds an arbitrary field value to this entry
	 * @param name the name of the field
	 * @param value the value of the field
	 * @see #getField(String)
	 */
	public void setField(String name, Object value) {
		if (this.otherFields == null) {
			this.otherFields = new Hashtable();
		}
		this.otherFields.put( name, value);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(VERSION);
		boolean notNull = this.startDate != null;
		out.writeBoolean(notNull);
		if (notNull) {
			this.startDate.write(out);
		}
		notNull = this.endDate != null;
		out.writeBoolean(notNull);
		if (notNull) {
			this.endDate.write(out);
		}
		out.writeBoolean( this.isAllday );
		out.writeInt( this.durationInMinutes );
		out.writeInt( this.simpleReoccurence );
		out.writeInt( this.sequence );
		boolean isNotNull = (this.summary != null);
		out.writeBoolean( isNotNull );
		if (isNotNull) {
			out.writeUTF( this.summary );
		}
		out.writeInt( this.alarm );
		isNotNull = (this.notes != null);
		out.writeBoolean( isNotNull );
		if (isNotNull) {
			out.writeUTF( this.notes );
		}
		isNotNull = (this.otherFields != null);
		out.writeBoolean( isNotNull );
		if (isNotNull) {
			Serializer.serialize(this.otherFields, out);
		}
		isNotNull = (this.location != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.location);
		}
		isNotNull = (this.description != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.description);
		}
		isNotNull = (this.organizer != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.organizer);
		}		
		isNotNull = (this.status != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.status);
		}
		out.writeInt( this.type );
		isNotNull = (this.id != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.id);
		}
		isNotNull = (this.userId != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.userId);
		}
		isNotNull = (this.classType != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.classType);
		}
		isNotNull = (this.category != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			this.category.write( out );
		}
		isNotNull = (this.calendarAlarm != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			this.calendarAlarm.write( out );
		}
		isNotNull = (this.eventRepeatRule != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			this.eventRepeatRule.write( out );
		}
		isNotNull = (this.timeZone != null);
		out.writeBoolean( isNotNull );
		if (isNotNull) {
			out.writeUTF(this.timeZone.getID());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version > VERSION) {
			throw new IOException("unknown version " + version);
		}
		if (version == 100) {
			long time = in.readLong();
			if (time != -1) {
				Calendar cal = Calendar.getInstance();
				cal.setTime( new Date( time ) );
				this.startDate = new TimePoint(cal);
			}
			time = in.readLong();
			if (time != -1) {
				Calendar cal = Calendar.getInstance();
				cal.setTime( new Date( time ) );
				this.endDate = new TimePoint(cal);
			}
		} else {
			boolean notNull = in.readBoolean();
			if (notNull) {
				this.startDate = new TimePoint(in);
			}
			notNull = in.readBoolean();
			if (notNull) {
				this.endDate = new TimePoint(in);
			}
		}
		if (version == 101) {
			in.readLong();
			in.readLong();
			in.readLong();
		}
		this.isAllday = in.readBoolean();
		this.durationInMinutes = in.readInt();
		this.simpleReoccurence = in.readInt();
		this.sequence = in.readInt();
		boolean isNotNull = in.readBoolean();
		if (isNotNull) {
			this.summary = in.readUTF();
		}
		this.alarm = in.readInt();
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.notes = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.otherFields = (Hashtable) Serializer.deserialize(in);
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.location = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.description = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.organizer = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.status = in.readUTF();
		}
		if (version == 102) {
			isNotNull = in.readBoolean();
			if (isNotNull) {
				in.readUTF();
			}
		} else {
			this.type = in.readInt();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.id = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.userId = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.classType = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.category = new CalendarCategory();
			this.category.read(in);
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.calendarAlarm = new CalendarAlarm();
			this.calendarAlarm.read(in);
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.eventRepeatRule = new EventRepeatRule();
			this.eventRepeatRule.read(in);
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			String timeZoneId = in.readUTF();
			this.timeZone = TimeZone.getTimeZone(timeZoneId);
		}
	}

	/**
	 * Clones this calendar entry for the specified date.
	 * @param start the new and only date for the cloned copy
	 * @return a copy of this event with the new date as its only event time
	 */
	public CalendarEntry clone(TimePoint start) {
		CalendarEntry copy = new CalendarEntry( this );
		copy.setStartDate( start, this.durationInMinutes );
		copy.simpleReoccurence = 0;
		copy.eventRepeatRule = null;
		copy.parent = this;
		return copy;
	}


	/**
	 * Checks if this entry either starts or ends in the given time period
	 * @param period the period
	 * @return true when this event falls into the given time sequence
	 */
	public boolean matches(TimePeriod period) {
		return (this.startDate != null && period.matches(this.startDate)) 
			|| (this.endDate != null && period.matches(this.endDate));
	}
	
	/**
	 * Retrieves access to the original calendar entry.
	 * @return the parent entry, can be null if this is not a cloned copy
	 */
	public CalendarEntry getParent() {
		return this.parent;
	}
	
	
	/**
	 * Gets the children of a repeating entry.
	 * @return CalandarEntry[] The array with the children. Null if the entry has no children.
	 */
	public CalendarEntry[] getChildren() {
		
		return this.children;
	}
	
	
	/**
	 * Sets the children of a repeating entry.
	 * @param entryList A CalendarEntryList which contains the children.
	 */
	public void setChildren(CalendarEntryList entryList) {
		
		this.children = entryList.getEntries();
	}
	
	/**
	 * Retrieves the difference in years between the start date of this entry and the specified time point
	 * @param timePoint the time point
	 * @return the difference in years, -1 if no start date has been defined
	 */
	public int getYearsSinceStart( TimePoint timePoint ) {
		return timePoint.getYear() - this.startDate.getYear();
	}

	public void setStartDate(Date date) {
		setStartDate( new TimePoint(date) );
	}

	public void setEndDate(Date date) {
		setEndDate( new TimePoint(date) );
	}

	/**
	 * Sets a text resolver that is used for resolving the summary and description of this CalendarEntry.
	 * Note that a resolver is not serialized when saving this entry.
	 * 
	 * @param resolver the resolver, use null for removing the resolver.
	 */
	public void setTextResolver( CalendarTextResolver resolver ) {
		this.textResolver = resolver;
	}
	
	/**
	 * Retrieves the used text resolver.
	 * @return the text resolver or null if none has been registered
	 */
	public CalendarTextResolver getTextResolver() {
		return this.textResolver;
	}

	/**
	 * Generates a global unique ID for this entry.
	 * 	 
	 * @return the generated GUID
	 */
	public long getGuid() {
		long guid = 0;
		if (this.id != null) {
			guid = this.id.hashCode();
		} else if (this.summary != null) {
			guid = this.summary.hashCode();
		}
		if (this.startDate != null) {
			guid |= (this.startDate.hashCode()) >> 32;
		}
		return guid;
	}
}
