package de.enough.polish.calendar;



public interface CalendarSubject {

	public void addObserver(CalendarObserver observer);
	
	public void removeObserver(CalendarObserver observer);
}
