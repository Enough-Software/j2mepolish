package de.enough.polish.calendar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;


/**
 * Allows to add an alarm to a calendar entry.
 * 
 * @author Nagendra Sharma
 * @author Robert Virkus
 *
 */
public class CalendarAlarm implements Externalizable {
	
	private final static int VERSION = 100;
	
	/**
	 * field to contain trigger 
	 */
	private String trigger;
	
	/**
	 * field to contain action to be done
	 */
	private String action;
	
	/**
	 * field to contain description for alarm
	 */
	private String description;
	
	/**
	 * Creates a new empty alarm.
	 */
	public CalendarAlarm() {
		//nothing to implement
	}
	
	
	/**
	 * @return returns trigger for alarm
	 */
	public String getTrigger() {
		return this.trigger;
	}
	/**
	 * setter method for trigger of alarm
	 * @param trigger
	 */
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
	/**
	 * @return returns action for alarm
	 */
	public String getAction() {
		return this.action;
	}
	/**
	 * setter method for action of alarm
	 * @param action
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return returns description for alarm
	 */
	public String getDescription() {
		return this.description;
	}
	/**
	 * setter method for description of alarm
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		boolean isNotNull = (this.trigger != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.trigger);
		}
		isNotNull = (this.action != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.action);
		}
		isNotNull = (this.description != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.description);
		}

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
		boolean isNotNull = in.readBoolean();
		if (isNotNull) {
			this.trigger = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.action = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.description = in.readUTF();
		}

	}

}
