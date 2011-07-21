package de.enough.polish.calendar;

/**
 * <p>Adds a listener/observer to the calendar entry model.</p>
 */

public interface CalendarObserver {

	/**
	 * Notifies the listener about changes in the calendar entry model.
	 * @param subject The changed calendar entry model.
	 * @param changedEntry An event which was changed/added in the model. Null if no event has been changed.
	 * @param changedCategory A category which was changed/added in the model. Null if no category has been changed.
	 */
	public void updatedCalendarModel(CalendarSubject subject, CalendarEntry changedEntry, CalendarEntry deletedEntry, CalendarCategory changedCategory);

	
	/**
	 * Removes the listener.
	 */
	public void removeObserver();
}
