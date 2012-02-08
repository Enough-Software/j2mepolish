package de.enough.polish.app.model;

import de.enough.polish.util.TimePoint;

/**
 * 
 * @author robert virkus, j2mepolish@enough.de
 *
 */
public class Message {
	
	private String sender;
	private String receiver;
	private String message;
	private TimePoint time;
	
	public Message() {
		
	}
	
	public Message( String sender, String message) {
		this.sender = sender;
		this.message = message;
	}
	
	public boolean isFromMe() {
		return "me".equals(this.sender);
	}
	
	public String getSender() {
		return this.sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getReceiver() {
		return this.receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getMessage() {
		return this.message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public TimePoint getTime() {
		return this.time;
	}
	public void setTime(TimePoint time) {
		this.time = time;
	}
	
	

}
