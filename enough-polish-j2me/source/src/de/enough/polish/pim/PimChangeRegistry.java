//#condition polish.api.pimapi
/**
 * 
 */
package de.enough.polish.pim;

import java.io.IOException;
import java.util.Vector;

import de.enough.polish.calendar.CalendarEntry;
import de.enough.polish.io.RmsStorage;
import de.enough.polish.util.ArrayList;


/**
 * Stores information needed to identify changes made to PIMItems held in a PIMList. 
 * This information includes the item's unique identifier, UID, the type of item
 * (Contact, Event or ToDo) as well as the type of modification made 
 * (added, modified or removed)
 * 
 * @author Ramakrishna Sharvirala
 *
 */
public class PimChangeRegistry {
	
	/**
	 * rms storage to hold local snapshot of PIM database
	 */
	private RmsStorage storage;
	
	/**
	 * field to hold a collection of PIM items which are added, removed or modified. 
	 */
	private Vector items;
	
	/**
	 * Utility class reference variable
	 */
	private PimUtility pimUtility;
	
	
	/**
	 * constructor to initialize pim change listener
	 */
	public PimChangeRegistry() {
		this.storage = new RmsStorage();
		this.pimUtility = new PimUtility();
		try {
			this.items = (Vector) this.storage.read( "PimItems" );
		} catch (IOException e) {
			createSnapShot();
		}
	}
	
	/**
	 * Returns all PIM contacts added outside the application.
	 */
	public PimContact[] getAllAddedContacts() {
		ArrayList addedContacts = new ArrayList();
		try {
			this.items = (Vector) this.storage.read( "PimItems" );
			PimContact[] allContacts = this.pimUtility.getAllContacts();
			
			int localPimItemsSize =  this.items.size();
			int outerPimItemsSize = allContacts.length;
			
			
			for(int i = 0 ; i < outerPimItemsSize ; i++) {
				PimContact pimContact = allContacts[i];
				boolean flag = false;
				for(int j = 0 ; j < localPimItemsSize ; j++) {
					PimChangeItem changedItem = (PimChangeItem)this.items.elementAt(j);
					if(changedItem.getTypeOfItem().equals(PimConstants.CONTACT_TOKEN)) {
						if(changedItem.getUid().equals(pimContact.getUid())) {
							flag = true;
						}
					}
				}
				if(!flag) {
					addedContacts.add(this.pimUtility.findContactByUid(pimContact.getUid()));
				}
			}
			
		} catch (Exception e) {
			this.items = new Vector();
		}
		return (PimContact[])addedContacts.toArray(new PimContact[addedContacts.size()]);
	}
	
	/**
	 * Returns all PIM contacts modified outside of application.
	 */
	public PimContact[] getAllModifiedContacts() {
		ArrayList modifiedContacts = new ArrayList();
		try {
			this.items = (Vector) this.storage.read( "PimItems" );
			PimContact[] allContacts = this.pimUtility.getAllContacts();
			
			int localPimItemsSize =  this.items.size();
			int outerPimItemsSize = allContacts.length;
			
			
			for(int i = 0 ; i < outerPimItemsSize ; i++) {
				PimContact pimContact = allContacts[i];
				//boolean flag = false;
				for(int j = 0 ; j < localPimItemsSize ; j++) {
					PimChangeItem changedItem = (PimChangeItem)this.items.elementAt(j);
					if(changedItem.getTypeOfItem().equals(PimConstants.CONTACT_TOKEN)) {
						if(changedItem.getUid().equals(pimContact.getUid())) {
							if(changedItem.getRevisionDate().getTime() == pimContact.getLastRevision().getTime()) {
								//flag = true;
							} else {
								modifiedContacts.add(this.pimUtility.findContactByUid(pimContact.getUid()));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			this.items = new Vector();
		}
		return (PimContact[])modifiedContacts.toArray(new PimContact[modifiedContacts.size()]);
	}
	
	/**
	 * Returns all PIM contacts removed outside of application.
	 */
	public void getAllRemovedContacts() {
		try {
			this.items = (Vector) this.storage.read( "PimItems" );
			PimContact[] allContacts = this.pimUtility.getAllContacts();
			
			int localPimItemsSize =  this.items.size();
			int outerPimItemsSize = allContacts.length;
			Vector removedContacts = new Vector();
			for(int i = 0 ; i < localPimItemsSize ; i++) {
				PimChangeItem changedItem = (PimChangeItem)this.items.elementAt(i);
				boolean flag = false;
				for(int j = 0 ; j < outerPimItemsSize ; j++) {
					PimContact pimContact = allContacts[j];
					if(changedItem.getTypeOfItem().equals(PimConstants.CONTACT_TOKEN)) {
						if(changedItem.getUid().equals(pimContact.getUid())) {
							flag = true;
						}
					}
				}
				if(flag) {
					removedContacts.addElement(changedItem.getUid());
				}
			}
		} catch (Exception e) {
			this.items = new Vector();
		}		
	}
	
	/**
	 * Returns all PIM events added outside of application.
	 */
	public CalendarEntry[] getAllAddedEvents() {
		ArrayList addedEvents = new ArrayList();
		try {
			this.items = (Vector) this.storage.read( "PimItems" );
			CalendarEntry[] allEvents = this.pimUtility.getAllEvents();
			
			int localPimItemsSize =  this.items.size();
			int outerPimItemsSize = allEvents.length;
			
			for(int i = 0 ; i < outerPimItemsSize ; i++) {
				CalendarEntry calendarEntry = allEvents[i];
				boolean flag = false;
				for(int j = 0 ; j < localPimItemsSize ; j++) {
					PimChangeItem changedItem = (PimChangeItem)this.items.elementAt(j);
					if(changedItem.getTypeOfItem().equals(PimConstants.EVENT_TOKEN)) {
						if(changedItem.getUid().equals(calendarEntry.getId())) {
							flag = true;
						}
					}
				}
				if(!flag) {
					addedEvents.add(this.pimUtility.findEventByUid(calendarEntry.getId()));
				}
			}
			
		} catch (Exception e) {
			this.items = new Vector();
		}
		return (CalendarEntry[])addedEvents.toArray(new CalendarEntry[addedEvents.size()]);
	}
	
	/**
	 * Returns all PIM events modified outside of application.
	 */
	public CalendarEntry[] getAllModifiedEvents() {
		ArrayList modifiedEvents = new ArrayList();
		try {
			this.items = (Vector) this.storage.read( "PimItems" );
			CalendarEntry[] allEvents = this.pimUtility.getAllEvents();
			
			int localPimItemsSize =  this.items.size();
			int outerPimItemsSize = allEvents.length;
			
			for(int i = 0 ; i < outerPimItemsSize ; i++) {
				CalendarEntry calendarEntry = allEvents[i];
				//boolean flag = false;
				for(int j = 0 ; j < localPimItemsSize ; j++) {
					PimChangeItem changedItem = (PimChangeItem)this.items.elementAt(j);
					if(changedItem.getTypeOfItem().equals(PimConstants.EVENT_TOKEN)) {
						if(changedItem.getUid().equals(calendarEntry.getId())) {
							//TODO implement modified support without modified date support in CalendarEntry
//							if(changedItem.getRevisionDate().getTime() == calendarEntry.getLastModifiedDate().getTime()) {
//								//flag = true;
//							} else {
								modifiedEvents.add(this.pimUtility.findEventByUid(calendarEntry.getId()));
//							}
						}
					}
				}
			}
		} catch (Exception e) {
			this.items = new Vector();
		}
		return (CalendarEntry[])modifiedEvents.toArray(new CalendarEntry[modifiedEvents.size()]);
	}
	
	/**
	 * Returns all PIM events removed outside of application.
	 */
	public void getAllRemovedEvents() {
		try {
			this.items = (Vector) this.storage.read( "PimItems" );
			CalendarEntry[] allEvents = this.pimUtility.getAllEvents();
			
			int localPimItemsSize =  this.items.size();
			int outerPimItemsSize = allEvents.length;
			Vector removedEvents = new Vector();
			for(int i = 0 ; i < localPimItemsSize ; i++) {
				PimChangeItem changedItem = (PimChangeItem)this.items.elementAt(i);
				boolean flag = false;
				for(int j = 0 ; j < outerPimItemsSize ; j++) {
					CalendarEntry calendarEntry = allEvents[j];
					if(changedItem.getTypeOfItem().equals(PimConstants.EVENT_TOKEN)) {
						if(changedItem.getUid().equals(calendarEntry.getId())) {
							flag = true;
						}
					}
				}
				if(flag) {
					removedEvents.addElement(changedItem.getUid());
				}
			}
		} catch (Exception e) {
			this.items = new Vector();
		}		
	}
	
	
	/**
	 * Returns all PIM ToDos added outside of application.
	 */
	public PimToDo[] getAllAddedToDos() {
		ArrayList addedToDos = new ArrayList();
		try {
			this.items = (Vector) this.storage.read( "PimItems" );
			PimToDo[] allToDos = this.pimUtility.getAllToDos();
			
			int localPimItemsSize =  this.items.size();
			int outerPimItemsSize = allToDos.length;
			
			for(int i = 0 ; i < outerPimItemsSize ; i++) {
				PimToDo pimToDo = allToDos[i];
				boolean flag = false;
				for(int j = 0 ; j < localPimItemsSize ; j++) {
					PimChangeItem changedItem = (PimChangeItem)this.items.elementAt(j);
					if(changedItem.getTypeOfItem().equals(PimConstants.TODO_TOKEN)) {
						if(changedItem.getUid().equals(pimToDo.getUid())) {
							flag = true;
						}
					}
				}
				if(!flag) {
					addedToDos.add(this.pimUtility.findToDoByUid(pimToDo.getUid()));
				}
			}
		} catch (Exception e) {
			this.items = new Vector();
		}
		return (PimToDo[])addedToDos.toArray(new PimToDo[addedToDos.size()]);
	}
	
	/**
	 * Returns all PIM ToDos modified outside of application.
	 */
	public PimToDo[] getAllModifiedToDos() {
		ArrayList modifiedToDos = new ArrayList();
		try {
			this.items = (Vector) this.storage.read( "PimItems" );
			PimToDo[] allToDos = this.pimUtility.getAllToDos();
			
			int localPimItemsSize =  this.items.size();
			int outerPimItemsSize = allToDos.length;
			
			for(int i = 0 ; i < outerPimItemsSize ; i++) {
				PimToDo pimToDo = (PimToDo)allToDos[i];
				//boolean flag = false;
				for(int j = 0 ; j < localPimItemsSize ; j++) {
					PimChangeItem changedItem = (PimChangeItem)this.items.elementAt(j);
					if(changedItem.getTypeOfItem().equals(PimConstants.TODO_TOKEN)) {
						if(changedItem.getUid().equals(pimToDo.getUid())) {
							if(changedItem.getRevisionDate().getTime() == pimToDo.getLastRevisedDate().getTime()) {
								//flag = true;
							} else {
								modifiedToDos.add(this.pimUtility.findToDoByUid(pimToDo.getUid()));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			this.items = new Vector();
		}
		return (PimToDo[])modifiedToDos.toArray(new PimToDo[modifiedToDos.size()]);
	}
	
	/**
	 * Returns all PIM ToDos removed outside of application.
	 */
	public void getAllRemovedToDos() {
		try {
			this.items = (Vector) this.storage.read( "PimItems" );
			PimToDo[] allToDos = this.pimUtility.getAllToDos();
			
			int localPimItemsSize =  this.items.size();
			int outerPimItemsSize = allToDos.length;
			Vector removedToDos = new Vector();
			for(int i = 0 ; i < localPimItemsSize ; i++) {
				PimChangeItem changedItem = (PimChangeItem)this.items.elementAt(i);
				boolean flag = false;
				for(int j = 0 ; j < outerPimItemsSize ; j++) {
					PimToDo pimToDo = allToDos[j];
					if(changedItem.getTypeOfItem().equals(PimConstants.TODO_TOKEN)) {
						if(changedItem.getUid().equals(pimToDo.getUid())) {
							flag = true;
						}
					}
				}
				if(flag) {
					removedToDos.addElement(changedItem.getUid());
				}
			}
		} catch (Exception e) {
			this.items = new Vector();
		}		
	}
	
	/**
	 * method synchronize local snapshot of PIM items with actual PIM database
	 */
	public void updateSnapShot() {
		try {
			this.storage.delete("PimItems");
			this.createSnapShot();
		}  catch (Exception e) {
			// Ignored.
		}	
	}
	
	/**
	 * helper method to populate local snap shot of PIM Items
	 */
	private void createSnapShot() {
		this.items = new Vector();
		try {
			PimContact[] allContacts = this.pimUtility.getAllContacts();
			int size = allContacts.length;
			PimChangeItem pimChangeItem = null;
			for(int i = 0 ; i < size ; i++) {
				PimContact pimContact = allContacts[i];
				pimChangeItem = new PimChangeItem();
				pimChangeItem.setUid(pimContact.getUid());
				pimChangeItem.setRevisionDate(pimContact.getLastRevision());
				pimChangeItem.setTypeOfItem(PimConstants.CONTACT_TOKEN);
				this.items.addElement(pimChangeItem);
			}
			
			CalendarEntry[] allEvents = this.pimUtility.getAllEvents();
			size = allEvents.length;
			for(int i = 0 ; i < size ; i++) {
				CalendarEntry calendarEntry = (CalendarEntry)allEvents[i];
				pimChangeItem = new PimChangeItem();
				pimChangeItem.setUid(calendarEntry.getId());
				//pimChangeItem.setRevisionDate(calendarEntry.getLastModifiedDate());
				pimChangeItem.setTypeOfItem(PimConstants.EVENT_TOKEN);
				this.items.addElement(pimChangeItem);
			}
			
			PimToDo[] allToDos = this.pimUtility.getAllToDos();
			size = allToDos.length;
			for(int i = 0 ; i < size ; i++) {
				PimToDo pimToDo = (PimToDo)allToDos[i];
				pimChangeItem = new PimChangeItem();
				pimChangeItem.setUid(pimToDo.getUid());
				pimChangeItem.setRevisionDate(pimToDo.getLastRevisedDate());
				pimChangeItem.setTypeOfItem(PimConstants.TODO_TOKEN);
				this.items.addElement(pimChangeItem);
			}
			this.storage.save( this.items, "PimItems" );

		} catch(Exception pe) {
			pe.printStackTrace();
		}
	}
}
