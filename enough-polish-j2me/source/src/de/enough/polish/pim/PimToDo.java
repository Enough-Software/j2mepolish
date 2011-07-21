/**
 * 
 */
package de.enough.polish.pim;

import java.util.Date;


/**
 * @author Rama
 *
 */
public class PimToDo {
	/**
	 * field to contain Priority of ToDo Tasks
	 */
	private int priority;
	/**
	 * field to contain Class Type of ToDo Tasks
	 */
	private int classOfToDo;
	
	/**
	 * field to contain date of completion of ToDo Tasks
	 */
	private Date completionDate;
	/**
	 * field to contain due date  of ToDo Tasks
	 */
	private Date dueDate;
	/**
	 * field to contain completed status of ToDo Tasks
	 */
	private boolean isCompleted;
	
	
	/**
	 * field to contain notes of ToDo Tasks
	 */
	private String note;
	/**
	 * field to contain summary of ToDo Tasks
	 */
	private String summary;
	
	/**
	 * field to contain Unique Identifier of PIMItem of ToDo Tasks
	 */
	private String uid;
	/**
	 * field to contain Last revised date of PIMItem of ToDo Tasks
	 */
	private Date lastRevisedDate;
	
	/**
	 * returns priority
	 * @return priority
	 */
	public int getPriority() {
		return this.priority;
	}
	/**
	 * setter method for priority
	 * @param priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	/**
	 * returns priority
	 * @return priority
	 */
	public int getClassOfToDo() {
		return this.classOfToDo;
	}
	/**
	 * setter method for classOfToDo
	 * @param classOfToDo
	 */
	public void setClassOfToDo(int classOfToDo) {
		this.classOfToDo = classOfToDo;
	}
	/**
	 * returns priority
	 * @return priority
	 */
	public Date getCompletionDate() {
		return this.completionDate;
	}
	/**
	 * setter method for completionDate
	 * @param completionDate
	 */
	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}
	/**
	 * returns priority
	 * @return priority
	 */
	public Date getDueDate() {
		return this.dueDate;
	}
	/**
	 * setter method for dueDate
	 * @param dueDate
	 */
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	/**
	 * returns priority
	 * @return priority
	 */
	public boolean isCompleted() {
		return this.isCompleted;
	}
	/**
	 * setter method for isCompleted
	 * @param isCompleted
	 */
	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	/**
	 * returns note
	 * @return note
	 */
	public String getNote() {
		if(this.note != null) {
			return this.note;
		} else {
			return "";
		}
	}
	/**
	 *  setter method for note
	 * @param note
	 */
	public void setNote(String note) {
		this.note = note;
	}
	/**
	 * returns summary
	 * @return summary
	 */
	public String getSummary() {
		if(this.summary != null) {
			return this.summary;
		} else {
			return "";
		}
	}
	/**
	 *  setter method for summary
	 * @param summary
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}
	/**
	 * returns uid
	 * @return uid
	 */
	public String getUid() {
		if(this.uid != null) {
			return this.uid;
		} else {
			return "";
		}
	}
	/**
	 * setter method for uid
	 * @param uid
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	/**
	 * returns lastRevisedDate
	 * @return lastRevisedDate
	 */
	public Date getLastRevisedDate() {
		return this.lastRevisedDate;
	}
	/**
	 * setter method for lastRevisedDate
	 * @param lastRevisedDate
	 */
	public void setLastRevisedDate(Date lastRevisedDate) {
		this.lastRevisedDate = lastRevisedDate;
	}
	
	
}
