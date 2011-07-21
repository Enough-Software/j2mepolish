//#condition polish.api.pimapi

/**
 * 
 */
package de.enough.polish.pim;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.pim.Contact;
import javax.microedition.pim.Event;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.ToDo;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.EventList;
import javax.microedition.pim.ToDoList;

import de.enough.polish.calendar.CalendarEntry;
import de.enough.polish.util.ArrayList;

/**
 * This is a helper class to access PIM APIs by providing various utility methods
 * to ease usage of PIM APIs. It is handled in device independent manner.
 * 
 * @author RamaKrishna Sharvirala
 * 
 *  
 */

public class PimUtility {
	/**
	 * Pim Instance of PIM database
	 */
	private PIM pimInstance = null;
	
	/**
	 * Constructor for PIM Utility
	 */
	public PimUtility() {
		this.pimInstance = PIM.getInstance();
	}
	
	/**
	 * gets the instance of PIM class
	 * @return returns the instance obtained using singleton PIM class
	 */
	public PIM getPimInstance() {
		return this.pimInstance;
	}


	/**
	 * sets the PIM instance to this class
	 * @param pimInstance instance variable obtained from singleton PIM class
	*/
	public void setPimInstance(PIM pimInstance) {
		this.pimInstance = pimInstance;
	}	
	
	/**
	 * returns the names of different contact lists of contact type
	 * @return array of names of contact lists
	*/
	public String[] getNamesOfContactLists() {
		String[] contactLists = this.pimInstance.listPIMLists(PIM.CONTACT_LIST);
		return contactLists;
	}
	
	/**
	 * returns the names of different event lists of event type
	 * @return array of names of event lists
	*/
	public String[] getNamesOfEventLists() {
		String[] eventLists = this.pimInstance.listPIMLists(PIM.EVENT_LIST);
		return eventLists;
	}
	
	/**
	 * returns the names of different ToDo lists of ToDo type
	 * @return array of names of ToDo lists
	*/
	public String[] getNamesOfToDoLists() {
		String[] toDoLists = this.pimInstance.listPIMLists(PIM.TODO_LIST);
		return toDoLists;
	}
	
	/**
	 * returns the default contact list in read write mode
	 * @return returns default contact list opened using READ_WRITE mode
	 */
	public ContactList getDefaultContactList() {
		return getDefaultContactListHelper(PIM.READ_WRITE);
	}
	
	/**
	 * Method returns the default contact list using specified mode
	 * @param mode To indicate mode of operation to open pim databases. Allowed values are READ_WRITE, READ_ONLY, WRITE_ONLY 
	 * @return returns default contact list opened using given mode or null when mode is invalid
	 */
	public ContactList getDefaultContactList(int mode) {
		if(PIM.READ_WRITE == mode || PIM.READ_ONLY == mode || PIM.WRITE_ONLY == mode) {
			return getDefaultContactListHelper(mode);
		} else {
			return null;
		}
	}
	
	/**
	 * Helper method to return default contact list using specified mode
	 * @param mode To indicate mode of operation to open pim databases. Allowed values are READ_WRITE, READ_ONLY, WRITE_ONLY
	 * @return returns default contact list opened using given mode or null when mode is invalid or there is no supported pim list
	 */
	private ContactList getDefaultContactListHelper(int mode) {
		ContactList contactList = null;
		try {
			contactList = (ContactList) this.pimInstance.openPIMList(PIM.CONTACT_LIST, mode);
		} catch(PIMException e) {
			contactList = null;
		}
		return contactList;
	}
	
	/**
	 * Method to return ContactList  of specified named list in read write mode
	 * returns the contact list using specified name
	 * @param nameOfList used to retrieve pim contact list with this name
	 * @return returns named contact list opened using given mode
	 */
	public ContactList getContactList(String nameOfList) {
		return getContactListHelper(nameOfList, PIM.READ_WRITE);
	}
	
	/**
	 *  Method to return ContactList  of specified named list in specified mode
	 * @param nameOfList specifies the name of the list to retrieve
	 * @param mode used to open pim databases in secure manner. Allowed values are READ_WRITE, READ_ONLY, WRITE_ONLY
	 * @return returns named contact list opened using given mode or null when mode is invalid 
	 */
	public ContactList getContactList(String nameOfList, int mode) {
		if(PIM.READ_WRITE == mode || PIM.READ_ONLY == mode || PIM.WRITE_ONLY == mode) {
			return getContactListHelper(nameOfList, mode);
		} else {
			return null;
		}
	}
	
	/**
	 * Helper Method to return ContactList  of specified named list in specified mode
	 * @param nameOfList specifies the name of the list to retrieve
	 * @param mode used to open pim databases in secure manner. Allowed values are READ_WRITE, READ_ONLY, WRITE_ONLY
	 * @return returns named contact list opened using given mode or null when mode is invalid or there is no supported pim list
	 */
	private ContactList getContactListHelper(String nameOfList, int mode) {
		ContactList contactList = null;
		try {
			contactList = (ContactList) this.pimInstance.openPIMList(PIM.CONTACT_LIST, mode, nameOfList);
		} catch(PIMException e) {
			contactList = null;
		}
		return contactList;
	}
	
	
	/**
	 * Helper Method to return default EventList  in read write mode
	 * @return returns default event list opened using READ_WRITE mode
	 */
	public EventList getDefaultEventList() {
		return getDefaultEventListHelper(PIM.READ_WRITE);
	}
	
	/**
	 * Method to return default EventList in specified mode
	 * @param mode used to open pim databases in secure manner. Allowed values are READ_WRITE, READ_ONLY, WRITE_ONLY
	 * @return returns default event list opened using given mode or null when mode is invalid 
	 */
	public EventList getDefaultEventList(int mode) {
		if(PIM.READ_WRITE == mode || PIM.READ_ONLY == mode || PIM.WRITE_ONLY == mode) {
			return getDefaultEventListHelper(mode);
		} else {
			return null;
		}
	}
	
	/**
	 * Helper Method to return default EventList  in specified mode
	 * @param mode used to open pim databases in secure manner. Allowed values are READ_WRITE, READ_ONLY, WRITE_ONLY
	 * @return returns default event list opened using given mode or null when mode is invalid or there is no supported pim list
	 */
	private EventList getDefaultEventListHelper(int mode) {
		EventList eventList = null;
		try {
			eventList = (EventList) this.pimInstance.openPIMList(PIM.EVENT_LIST, mode);
		} catch(PIMException e) {
			eventList = null;
		}
		return eventList;
	}
	/**
	 * Method to return EventList of specified named list in read write mode
	 * @param nameOfList specifies the name of the list to retrieve
	 * @return returns named event list opened using default mode
	 */
	public EventList getEventList(String nameOfList) {
		return getEventListHelper(nameOfList, PIM.READ_WRITE);
	}
	
	/**
	 * Method to return EventList of specified named list in specified mode
	 * @param nameOfList specifies the name of the list to retrieve
	 * @param mode used to open pim databases in secure manner. Allowed values are READ_WRITE, READ_ONLY, WRITE_ONLY
	 * @return returns named event list opened using given mode or null when mode is invalid 
	 */
	public EventList getEventList(String nameOfList, int mode) {
		if(PIM.READ_WRITE == mode || PIM.READ_ONLY == mode || PIM.WRITE_ONLY == mode) {
			return getEventListHelper(nameOfList, mode);
		} else {
			return null;
		}
	}
	
	/**
	 * Helper Method to return EventList of specified named list in specified mode
	 * @param nameOfList specifies the name of the list to retrieve
	 * @param mode used to open pim databases in secure manner. Allowed values are READ_WRITE, READ_ONLY, WRITE_ONLY
	 * @return returns named event list opened using given mode or null when mode is invalid or there is no supported pim list
	 */
	private EventList getEventListHelper(String nameOfList, int mode) {
		EventList eventList = null;
		try {
			eventList = (EventList) this.pimInstance.openPIMList(PIM.EVENT_LIST, mode, nameOfList);
		} catch(PIMException e) {
			eventList = null;
		}
		return eventList;
	}
	
	/**
	 * Method to return default ToDOList in read write mode
	 * @return returns default todo list opened using READ_WRITE mode
	 */
	public ToDoList getDefaultToDoList() {
		return getDefaultToDoListHelper(PIM.READ_WRITE);
	}
	
	
	/**
	 * Method to return default ToDOList in specified mode
	 * @param mode used to open pim databases in secure manner. Allowed values are READ_WRITE, READ_ONLY, WRITE_ONLY
	 * @return returns default todo list opened using given mode or null when mode is invalid 
	 */
	public ToDoList getDefaultToDoList(int mode) {
		if(PIM.READ_WRITE == mode || PIM.READ_ONLY == mode || PIM.WRITE_ONLY == mode) {
			return getDefaultToDoListHelper(mode);
		} else {
			return null;
		}
	}
	
	/**
	 *  Helper Method to return default ToDOList in specified mode
	 * @param mode used to open pim databases in secure manner. Allowed values are READ_WRITE, READ_ONLY, WRITE_ONLY
	 * @return returns default todo list opened using given mode or null when mode is invalid or there is no supported pim list
	 */
	private ToDoList getDefaultToDoListHelper(int mode) {
		ToDoList toDoList = null;
		try {
			toDoList = (ToDoList) this.pimInstance.openPIMList(PIM.TODO_LIST, mode);
		} catch(PIMException e) {
			toDoList = null;
		}
		return toDoList;
	}
	
	
	/**
	 * Method to return ToDOList with specified name in read write mode
	 * @param nameOfList specifies the name of the list to retrieve
	 * @return returns named todo list opened using READ_WRITE mode
	 */
	public ToDoList getToDoList(String nameOfList) {
		return getToDoListHelper(nameOfList, PIM.READ_WRITE);
	}
	
	/**
	 * Method to return ToDOList with specified name in specified mode
	 * @param nameOfList specifies the name of the list to retrieve
	 * @param mode used to open pim databases in secure manner. Allowed values are READ_WRITE, READ_ONLY, WRITE_ONLY
	 * @return returns named todo list opened using given mode or null when mode is invalid
	 */
	public ToDoList getToDoList(String nameOfList, int mode) {
		if(PIM.READ_WRITE == mode || PIM.READ_ONLY == mode || PIM.WRITE_ONLY == mode) {
			return getToDoListHelper(nameOfList, mode);
		} else {
			return null;
		}
	}
	
	/**
	 *  Helper Method to return ToDOList with specified name in specified mode
	 * @param nameOfList specifies the name of the list to retrieve
	 * @param mode used to open pim databases in secure manner. Allowed values are READ_WRITE, READ_ONLY, WRITE_ONLY
	 * @return returns named todo list opened using given mode or null when mode is invalid or there is no supported pim list
	 */
	public ToDoList getToDoListHelper(String nameOfList, int mode) {
		ToDoList toDoList = null;
		try {
			toDoList = (ToDoList) this.pimInstance.openPIMList(PIM.TODO_LIST, mode, nameOfList);
		} catch(PIMException e) {
			toDoList = null;
		}
		return toDoList;
	}
	

	
	/**
	 * Method to return an array of all PIM items from default ContactList
	 * @return returns all PIM contacts as an array of PimContact Objects
	 * @throws PIMException throws PIM Exception
	*/
	public PimContact[] getAllContacts() throws PIMException {
		ContactList contactList = null;
		contactList = getDefaultContactList();
		return getAllContactsHelper(contactList);
	}
	
	
	/**
	 * Method to return an array of all PIM items from specified named ContactList
	 * @param nameOfList specified the name of list to retrieve
	 * @return returns all PIM contacts as an array of PimContact Objects
	 * @throws PIMException throws PIM Exception
	*/
	public PimContact[] getAllContacts(String nameOfList) throws PIMException {
		ContactList contactList = null;
		contactList = getContactList(nameOfList);
		return getAllContactsHelper(contactList);
	}
	
	/**
	 *  Helper Method to return a collection of all PIM items from specified ContactList
	 * @param contactList specifies contact list for getting all contacts
	 * @return  returns all PIM contacts as an array of PimContact Objects
	 * @throws PIMException throws PIM Exception
	 */
	private PimContact[] getAllContactsHelper(ContactList contactList) throws PIMException {
		return getAllContactsOfEnumeration(contactList, contactList.items());
	}
	
	/**
	 * gets all the contacts being enumerated in the Contact Database using the contact list and enumeration. 
	 * @param contactList an instance of opened PIM list of contact type
	 * @param contacts enumeration of all contacts in a given pim list
	 * @return PimContact[] returns all PIM contacts as an array of PimContact Objects
	 */
	
	private PimContact[] getAllContactsOfEnumeration(ContactList contactList, Enumeration contacts) {
		ArrayList pimList = new ArrayList();
		PimContact pimContact = null;
		while(contacts.hasMoreElements()) {
			Contact contact = (Contact)contacts.nextElement();
			pimContact = new PimContact();
			int[] fields = contact.getPIMList().getSupportedFields();
			// For each supported PIM field
			for (int i = 0; i < fields.length; i++) {
				int field = fields[i];

		        // Skip CLASS fields - don't load class fields
		        if (field == Contact.CLASS) {
		            continue;
		        }

		        // Skip field with NO values to load
		        if (contact.countValues(field) == 0) {
		            continue;
		        }
		        // Get the field's data type
		        int dataType = contact.getPIMList().getFieldDataType(field);
		        // Get the field's label
		        //String label = contact.getPIMList().getFieldLabel(field);
		        switch (dataType) {
		        	case PIMItem.STRING: {
		        		if(field == Contact.FORMATTED_NAME) {
		        			pimContact.setFormattedName(contact.getString(field, 0));
		        		} else if(field == Contact.FORMATTED_ADDR) {
		        			pimContact.setFormattedAddress(contact.getString(field, 0));
		        		} else if(field == Contact.EMAIL) {
		        			pimContact.setEmailAddress(contact.getString(field, 0));
		        		} else if(field == Contact.NICKNAME) {
		        			pimContact.setNickName(contact.getString(field, 0));
		        		} else if(field == Contact.NOTE) {
		        			pimContact.setNotes(contact.getString(field, 0));
		        		} else if(field == Contact.TEL) {
		        			pimContact.setTelephoneNumber(contact.getString(field, 0));
		        		} else if(field == Contact.URL) {
		        			pimContact.setUrlForContact(contact.getString(field, 0));
		        		} else if(field == Contact.PHOTO_URL) {
		        			pimContact.setPhotoUrl(contact.getString(field, 0));
		        		} else if(field == Contact.PUBLIC_KEY_STRING) {
		        			pimContact.setPublicKeyString(contact.getString(field, 0));
		        		} else if(field == Contact.UID) {
		        			pimContact.setUid(contact.getString(field, 0));
		        		}
		        		break;
		        	}
		        	case PIMItem.BOOLEAN: {
		                break;
		            }
		        	case PIMItem.STRING_ARRAY: {
		        		if(field == Contact.ADDR) {
		        			String[] addressArray = contact.getStringArray(field, 0);
		        			ContactAddress address = new ContactAddress();
		        			if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_COUNTRY)) {
		        				address.setCountryOfAddress(addressArray[Contact.ADDR_COUNTRY]);
		        				//address.setCountryOfAddress(contact.getString(Contact.ADDR_COUNTRY, 0));
		        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_EXTRA)) {
		        				address.setExtraInfoOfAddress(addressArray[Contact.ADDR_EXTRA]);
		        				//address.setExtraInfoOfAddress(contact.getString(Contact.ADDR_EXTRA, 0));
		        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_LOCALITY)) {
		        				address.setLocalityOfAddress(addressArray[Contact.ADDR_LOCALITY]);
		        				//address.setLocalityOfAddress(contact.getString(Contact.ADDR_LOCALITY, 0));
		        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_POBOX)) {
		        				address.setPoBoxOfAddress(addressArray[Contact.ADDR_POBOX]);
		        				//address.setPoBoxOfAddress(contact.getString(Contact.ADDR_POBOX, 0));
		        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_POSTALCODE)) {
		        				address.setPostalCode(addressArray[Contact.ADDR_POSTALCODE]);
		        				//address.setPostalCode(contact.getString(Contact.ADDR_POSTALCODE, 0));
		        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_REGION)) {
		        				address.setRegionOfAddress(addressArray[Contact.ADDR_REGION]);
		        				//address.setRegionOfAddress(contact.getString(Contact.ADDR_REGION, 0));
		        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_STREET)) {
		        				address.setStreetAddress(addressArray[Contact.ADDR_STREET]);
		        				//address.setStreetAddress(contact.getString(Contact.ADDR_STREET, 0));
		        			}
		        			pimContact.setContactAddress(address);
		        		} else if(field == Contact.NAME) {
		        			String[] nameArray = contact.getStringArray(field, 0);
		        			ContactName contactName = new ContactName();
		        			if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_FAMILY)) {
		        				contactName.setFamilyName(nameArray[Contact.NAME_FAMILY]);
		        				//contactName.setFamilyName(contact.getString(Contact.NAME_FAMILY, 0));
		        			} else if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_GIVEN)) {
		        				contactName.setGivenName(nameArray[Contact.NAME_GIVEN]);
		        				//contactName.setGivenName(contact.getString(Contact.NAME_GIVEN, 0));
		        			} else if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_OTHER)) {
		        				contactName.setOtherName(nameArray[Contact.NAME_OTHER]);
		        				//contactName.setOtherName(contact.getString(Contact.NAME_OTHER, 0));
		        			} else if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_PREFIX)) {
		        				contactName.setPrefixOfName(nameArray[Contact.NAME_PREFIX]);
		        				//contactName.setPrefixOfName(contact.getString(Contact.NAME_PREFIX, 0));
		        			} else if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_SUFFIX)) {
		        				contactName.setSuffixOfName(nameArray[Contact.NAME_SUFFIX]);
		        				//contactName.setSuffixOfName(contact.getString(Contact.NAME_SUFFIX, 0));
		        			}  
		        			pimContact.setContactName(contactName);
		        		} 
		        		break;
		            }
		        	case PIMItem.DATE: {
		        		if(field == Contact.BIRTHDAY) {
		        			pimContact.setDateOfBirth(new Date(contact.getDate(field, 0)));
		        		} else if(field == Contact.REVISION) {
		        			pimContact.setLastRevision(new Date(contact.getDate(field, 0)));
		        		}
		                break;
		            }
		        	case PIMItem.INT: {
		        		if(field == Contact.CLASS) {
		        			pimContact.setClassOfContact(contact.getInt(field, 0));
		        		}
		                break;
		            }
		            case PIMItem.BINARY: {
		            	if(field == Contact.PHOTO) {
		        			pimContact.setPhoto(contact.getBinary(field, 0));
		        		} else if(field == Contact.PUBLIC_KEY) {
		        			pimContact.setPublicKey(contact.getBinary(field, 0));
		        		}
		            	break;
		            }
		        }
			}
			pimList.add(pimContact);
		}
		return (PimContact[]) pimList.toArray(new PimContact[pimList.size()]);
	}
	
	/**
	 * Method to return a collection of all PIM items from default EventList
	 * @return returns all PIM Events as an array of CalendarEntry Objects
	 * @throws PIMException throws PIM Exception
	*/
	public CalendarEntry[] getAllEvents() throws PIMException {
		EventList eventList = null;
		eventList = this.getDefaultEventList();
		return getAllEventsHelper(eventList);
	}
	
	/**
	 * Method to return a collection of all PIM items from specified named EventList
	 * @param nameOfList specifies the name of PIM List
	 * @return returns all PIM Events as an array of CalendarEntry Objects
	 * @throws PIMException throws PIM Exception
	 */
	public CalendarEntry[] getAllEvents(String nameOfList) throws PIMException {
		EventList eventList = null;
		eventList = this.getEventList(nameOfList);
		return getAllEventsHelper(eventList);
	}
	
	/**
	 * Helper Method to return a collection of all PIM items from specified EventList
	 * @param eventList an instance of opened PIM list of event type
	 * @return CalendarEntry[] returns all PIM Events as an array of CalendarEntry Objects
	 */
	private CalendarEntry[] getAllEventsHelper(EventList eventList) throws PIMException {
		Enumeration events = eventList.items();
		return getAllEventsOfEnumeration(eventList, events);
	}
	
	/**
	 * Method to return all the events in the PIM list being enumerated. 
	 * @param eventList an instance of opened PIM list of event type
	 * @param events an enumeration of all PIM Events of the given pim list
	 * @return CalendarEntry[] returns all PIM Events as an array of CalendarEntry Objects
	 */
	private CalendarEntry[] getAllEventsOfEnumeration(EventList eventList, Enumeration events) {	
			ArrayList pimEventList = new ArrayList();
			CalendarEntry calendarEntry = null;
			while(events.hasMoreElements()) {
				Event event = (Event)events.nextElement();
				calendarEntry = new CalendarEntry();
				int[] fields = event.getPIMList().getSupportedFields();
				// For each supported PIM field
				for (int i = 0; i < fields.length; i++) {
					int field = fields[i];

			        // Skip CLASS fields - don't load class fields
			        if (field == Contact.CLASS) {
			            continue;
			        }
			        // Skip field with NO values to load
			        if (event.countValues(field) == 0) {
			            continue;
			        }
			        // Get the field's data type
			        int dataType = event.getPIMList().getFieldDataType(field);
			        // Get the field's label
			        //String label = event.getPIMList().getFieldLabel(field);
			        switch (dataType) {
			        	case PIMItem.STRING: {
			        		if(field == Event.LOCATION) {
			        			calendarEntry.setLocation(event.getString(field, 0));
			        		} else if(field == Event.NOTE) {
			        			calendarEntry.setNotes(event.getString(field, 0));
			        		} else if(field == Event.SUMMARY) {
			        			calendarEntry.setSummary(event.getString(field, 0));
			        		} else if(field == Event.UID) {
			        			calendarEntry.setId(event.getString(field, 0));
			        		} 
			        		break;
			        	}
			        	case PIMItem.BOOLEAN: {
			                break;
			            }
			        	case PIMItem.STRING_ARRAY: {
			        		
			        		break;
			            }
			        	case PIMItem.DATE: {
			        		if(field == Event.START) {
			        			calendarEntry.setStartDate(new Date(event.getDate(field, 0)));
			        		} else if(field == Event.END) {
			        			calendarEntry.setEndDate(new Date(event.getDate(field, 0)));
//			        		} else if(field == Event.REVISION) {
//			        			calendarEntry.setLastModifiedDate(new Date(event.getDate(field, 0)));
			        		}
			                break;
			            }
			        	case PIMItem.INT: {
			        		if(field == Event.CLASS) {
			        			calendarEntry.setField("ClassOfEvent", new Integer(event.getInt(field, 0)));
			        		} else if(field == Event.ALARM) {
			        			calendarEntry.setAlarm(event.getInt(field, 0));
			        		}
			                break;
			            }
			            case PIMItem.BINARY: {
			            	break;
			            }
			        }
				}
				pimEventList.add(calendarEntry);
			}
		return (CalendarEntry[])pimEventList.toArray(new CalendarEntry[pimEventList.size()]);
	}
	
	
	/**
	 * Method to return a collection of all PIM items from default TODOList
	 * @return returns all PIM todos as an array of PimToDO objects
	 * @throws PIMException throws PIM Exception
	 */
	public PimToDo[] getAllToDos() throws PIMException {
		ToDoList toDoList = null;
		toDoList = this.getDefaultToDoList();
		return getAllToDosHelper(toDoList);
	}
	
	/**
	 * Method to return a collection of all PIM items from specified named TODOList
	 * @param nameOfList specifies the name of PIM List
	 * @return returns all PIM todos as an array of PimToDO objects
	 * @throws PIMException throws PIM Exception
	 */
	public PimToDo[] getAllToDos(String nameOfList) throws PIMException {
		ToDoList toDoList = null;
		toDoList = this.getToDoList(nameOfList);
		return getAllToDosHelper(toDoList);
	}
	
	/**
	 * Helper Method to return a collection of all PIM items from specified TODOList
	 * @return returns vector of all ToDO Items of specified TODOList
	 * @throws PIMException throws PIM Exception
	*/
	private PimToDo[] getAllToDosHelper(ToDoList toDoList) throws PIMException {
		Enumeration toDos = toDoList.items();
		return getAllToDosOfEnumeration(toDoList, toDos);
	}
	
	
	/**Method to return all the ToDo items being enumerated in the PIM list. 
	 * @param toDoList an instance of opened PIM List of ToDo Type
	 * @param toDos an enumeration of all ToDo Items of given Pim List
	 * @return returns all PIM todos as an array of PimToDO objects
	 */
	private PimToDo[] getAllToDosOfEnumeration(ToDoList toDoList, Enumeration toDos) {	
			ArrayList pimToDoList = new ArrayList();
			PimToDo pimToDo = null;
			while(toDos.hasMoreElements()) {
				ToDo toDo = (ToDo)toDos.nextElement();
				pimToDo = new PimToDo();
				
				int[] fields = toDo.getPIMList().getSupportedFields();
				// For each supported PIM field
				for (int i = 0; i < fields.length; i++) {
					int field = fields[i];
			        // Skip CLASS fields - don't load class fields
			        if (field == Contact.CLASS) {
			            continue;
			        }
			        // Skip field with NO values to load
			        if (toDo.countValues(field) == 0) {
			            continue;
			        }
			        // Get the field's data type
			        int dataType = toDo.getPIMList().getFieldDataType(field);
			        switch (dataType) {
			        	case PIMItem.STRING: {
			        		if(field == ToDo.NOTE) {
			        			pimToDo.setNote(toDo.getString(field, 0));
			        		} else if(field == ToDo.SUMMARY) {
			        			pimToDo.setSummary(toDo.getString(field, 0));
			        		} else if(field == ToDo.UID) {
			        			pimToDo.setUid(toDo.getString(field, 0));
			        		} 
			        		break;
			        	}
			        	case PIMItem.BOOLEAN: {
			        		if(field == ToDo.COMPLETED) {
			        			pimToDo.setCompleted(toDo.getBoolean(field, 0));
			        		} 
			                break;
			            }
			        	case PIMItem.STRING_ARRAY: {
			        		break;
			            }
			        	case PIMItem.DATE: {
			        		if(field == ToDo.COMPLETION_DATE) {
			        			pimToDo.setCompletionDate(new Date(toDo.getDate(field, 0)));
			        		} else if(field == ToDo.DUE) {
			        			pimToDo.setDueDate(new Date(toDo.getDate(field, 0)));
			        		} else if(field == ToDo.REVISION) {
			        			pimToDo.setLastRevisedDate(new Date(toDo.getDate(field, 0)));
			        		}
			                break;
			            }
			        	case PIMItem.INT: {
			        		if(field == ToDo.CLASS) {
			        			pimToDo.setClassOfToDo(toDo.getInt(field, 0));
			        		}
			                break;
			            }
			            case PIMItem.BINARY: {
			            	break;
			            }
			        }
				}
				pimToDoList.add(pimToDo);
			}
		return (PimToDo[])pimToDoList.toArray(new PimToDo[pimToDoList.size()]);
	}
	
	
	
	
	
	/**
	 * Method to add Contact entry with specified data PimContact to default ContactList
	 * @param pimContact an instance of PimContact 
	 * @throws PIMException throws PIM Exception
	 */
	public void addContact(PimContact pimContact) throws PIMException {
		ContactList contactList = null;
		contactList = (ContactList) this.getDefaultContactList();
		addContactHelper(pimContact, contactList);
	}
	
	/**
	 *  Method to add Contact entry with specified data PimContact to the specified named ContactList
	 * @param pimContact an instance of PimContact 
	 * @param nameOfList specifies the name of PIM List
	 * @throws PIMException throws PIM Exception
	 */
	public void addContact(PimContact pimContact, String nameOfList) throws PIMException {
		ContactList contactList = null;
		contactList = getContactList(nameOfList);
		addContactHelper(pimContact, contactList);
	}
	
	/**
	 *  Helper Method to add Contact entry with specified data PimContact to the specified ContactList
	 * @param pimContact an instance of PimContact 
	 * @throws PIMException throws PIM Exception
	*/
	private void addContactHelper(PimContact pimContact, ContactList contactList) throws PIMException {
		try {
			Contact contact = contactList.createContact();
			int[] fields = contact.getPIMList().getSupportedFields();
			// For each supported PIM field
			for (int i = 0; i < fields.length; i++) {
				int field = fields[i];
		        // Get the field's data type
		        int dataType = contact.getPIMList().getFieldDataType(field);
		        switch (dataType) {
		        	case PIMItem.STRING: {
		        		if(field == Contact.FORMATTED_NAME && pimContact.getFormattedName() != null) {
		        			contact.addString(Contact.FORMATTED_NAME, Contact.ATTR_NONE, pimContact.getFormattedName());
		        		} else if(field == Contact.FORMATTED_ADDR && pimContact.getFormattedAddress() != null) {
		        			contact.addString(Contact.FORMATTED_ADDR, Contact.ATTR_NONE, pimContact.getFormattedAddress());
		        		} else if(field == Contact.EMAIL && pimContact.getEmailAddress() != null) {
		        			contact.addString(Contact.EMAIL, Contact.ATTR_NONE, pimContact.getEmailAddress());
		        		} else if(field == Contact.NICKNAME && pimContact.getNickName() != null) {
		        			contact.addString(Contact.NICKNAME, Contact.ATTR_NONE, pimContact.getNickName());
		        		} else if(field == Contact.NOTE && pimContact.getNotes() != null) {
		        			contact.addString(Contact.NOTE, Contact.ATTR_NONE, pimContact.getNotes());
		        		} else if(field == Contact.TEL && pimContact.getTelephoneNumber() != null) {
		        			contact.addString(Contact.TEL, Contact.ATTR_NONE, pimContact.getTelephoneNumber());
		        		} else if(field == Contact.URL && pimContact.getUrlForContact() != null) {
		        			contact.addString(Contact.URL, Contact.ATTR_NONE, pimContact.getUrlForContact());
		        		} else if(field == Contact.PHOTO_URL && pimContact.getPhotoUrl() != null) {
		        			contact.addString(Contact.PHOTO_URL, Contact.ATTR_NONE, pimContact.getPhotoUrl());
		        		} else if(field == Contact.PUBLIC_KEY_STRING && pimContact.getPublicKeyString() != null) {
		        			contact.addString(Contact.PUBLIC_KEY_STRING, Contact.ATTR_NONE, pimContact.getPublicKeyString());
		        		} else if(field == Contact.UID) {
		        			//pimContact.setUid(contact.getString(field, 0));
		        		}
		        		break;
		        	}
		        	case PIMItem.BOOLEAN: {
		                break;
		            }
		        	case PIMItem.STRING_ARRAY: {
		        		if(field == Contact.ADDR ) {
		        			String[] addressArray =  new String[contactList.stringArraySize(Contact.ADDR)];
		        			if(pimContact.getContactAddress() != null) {
			        			if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_COUNTRY) && pimContact.getContactAddress().getCountryOfAddress() != null) {
			        				addressArray[Contact.ADDR_COUNTRY] = pimContact.getContactAddress().getCountryOfAddress();
			        				//contact.addString(Contact.ADDR_COUNTRY, Contact.ATTR_NONE, pimContact.getContactAddress().getCountryOfAddress());
			        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_EXTRA) && pimContact.getContactAddress().getExtraInfoOfAddress() != null) {
			        				addressArray[Contact.ADDR_EXTRA] = pimContact.getContactAddress().getExtraInfoOfAddress();
			        				//contact.addString(Contact.ADDR_EXTRA, Contact.ATTR_NONE, pimContact.getContactAddress().getExtraInfoOfAddress());
			        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_LOCALITY) && pimContact.getContactAddress().getLocalityOfAddress() != null) {
			        				addressArray[Contact.ADDR_LOCALITY] = pimContact.getContactAddress().getLocalityOfAddress();
			        				//contact.addString(Contact.ADDR_LOCALITY, Contact.ATTR_NONE, pimContact.getContactAddress().getLocalityOfAddress());
			        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_POBOX) && pimContact.getContactAddress().getPoBoxOfAddress() != null) {
			        				addressArray[Contact.ADDR_POBOX] = pimContact.getContactAddress().getPoBoxOfAddress();
			        				//contact.addString(Contact.ADDR_POBOX, Contact.ATTR_NONE, pimContact.getContactAddress().getPoBoxOfAddress());
			        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_POSTALCODE) && pimContact.getContactAddress().getPostalCode() != null) {
			        				addressArray[Contact.ADDR_POSTALCODE] = pimContact.getContactAddress().getPostalCode();
			        				//contact.addString(Contact.ADDR_POSTALCODE, Contact.ATTR_NONE, pimContact.getContactAddress().getPostalCode());
			        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_REGION) && pimContact.getContactAddress().getRegionOfAddress() != null) {
			        				addressArray[Contact.ADDR_REGION] = pimContact.getContactAddress().getRegionOfAddress();
			        				//contact.addString(Contact.ADDR_REGION, Contact.ATTR_NONE, pimContact.getContactAddress().getRegionOfAddress());
			        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_STREET) && pimContact.getContactAddress().getStreetAddress() != null) {
			        				addressArray[Contact.ADDR_STREET] = pimContact.getContactAddress().getStreetAddress();
			        				//contact.addString(Contact.ADDR_STREET, Contact.ATTR_NONE, pimContact.getContactAddress().getStreetAddress());
			        			}
		        			}
		        			contact.addStringArray(Contact.ADDR, Contact.ATTR_NONE, addressArray);
		        		} else if(field == Contact.NAME) {
		        			String[] nameArray = new String[contactList.stringArraySize(Contact.NAME)];
		        			if(pimContact.getContactName() != null) {
			        			if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_FAMILY) && pimContact.getContactName().getFamilyName() != null) {
			        				nameArray[Contact.NAME_FAMILY] = pimContact.getContactName().getFamilyName();
			        				//contact.addString(Contact.NAME_FAMILY, Contact.ATTR_NONE, pimContact.getContactName().getFamilyName());
			        			} else if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_GIVEN) && pimContact.getContactName().getGivenName() != null) {
			        				nameArray[Contact.NAME_GIVEN] = pimContact.getContactName().getGivenName();
			        				//contact.addString(Contact.NAME_GIVEN, Contact.ATTR_NONE, pimContact.getContactName().getGivenName());
			        			} else if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_OTHER) && pimContact.getContactName().getOtherName() != null) {
			        				nameArray[Contact.NAME_OTHER] = pimContact.getContactName().getOtherName();
			        				//contact.addString(Contact.NAME_OTHER, Contact.ATTR_NONE, pimContact.getContactName().getOtherName());
			        			} else if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_PREFIX) && pimContact.getContactName().getPrefixOfName() != null) {
			        				nameArray[Contact.NAME_PREFIX] = pimContact.getContactName().getPrefixOfName();
			        				//contact.addString(Contact.NAME_PREFIX, Contact.ATTR_NONE, pimContact.getContactName().getPrefixOfName());
			        			} else if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_SUFFIX) && pimContact.getContactName().getSuffixOfName() != null) {
			        				nameArray[Contact.NAME_SUFFIX] = pimContact.getContactName().getSuffixOfName();
			        				//contact.addString(Contact.NAME_SUFFIX, Contact.ATTR_NONE, pimContact.getContactName().getSuffixOfName());
			        			}
		        			}
		        			contact.addStringArray(Contact.NAME, Contact.ATTR_NONE, nameArray);
		        		} 
		        		break;
		            }
		        	case PIMItem.DATE: {
		        		if(field == Contact.BIRTHDAY && pimContact.getDateOfBirth() != null) {
		        			contact.addDate(Contact.BIRTHDAY, Contact.ATTR_NONE, pimContact.getDateOfBirth().getTime());
		        		} else if(field == Contact.REVISION && pimContact.getLastRevision() != null) {
		        			contact.addDate(Contact.REVISION, Contact.ATTR_NONE, pimContact.getLastRevision().getTime());
		        		}
		                break;
		            }
		        	case PIMItem.INT: {
		        		if(field == Contact.CLASS && pimContact.getClassOfContact() != 0) {
		        			contact.addInt(Contact.CLASS, Contact.ATTR_NONE, pimContact.getClassOfContact());
		        		}
		                break;
		            }
		            case PIMItem.BINARY: {
		            	if(field == Contact.PHOTO && pimContact.getPhoto() != null) {
		            		contact.addBinary(Contact.PHOTO, Contact.ATTR_NONE, pimContact.getPhoto(), 0, pimContact.getPhoto().length);
		        		} else if(field == Contact.PUBLIC_KEY && pimContact.getPublicKey() != null) {
		        			contact.addBinary(Contact.PUBLIC_KEY, Contact.ATTR_NONE, pimContact.getPublicKey(), 0, pimContact.getPublicKey().length);
		        		}
		            	break;
		            }
		        }
			}
		} catch(Exception e) {
			throw new PIMException(PimConstants.ADD_CONTACT_EXCEPTION_STRING + e.getMessage());
		}
	}
	
	
	
	/**
	 * Method to add Event entry with specified data CalendarEntry to named EventList
	 * @param calendarEntry an instance of CalendarEntry 
	 * @throws PIMException throws PIM Exception
	 */
	public void addEvent(CalendarEntry calendarEntry) throws PIMException {
		EventList eventList = null;
		eventList = getDefaultEventList();
		addEventHelper(calendarEntry, eventList);
	}
	
	/**
	 * Method to add Event entry with specified data CalendarEntry to the specified namedEventList
	 * @param calendarEntry an instance of CalendarEntry 
	 * @param nameOfList specifies the name of PIM List
	 * @throws PIMException throws PIM Exception
	 */
	public void addEvent(CalendarEntry calendarEntry, String nameOfList) throws PIMException {
		EventList eventList = null;
		eventList = getEventList(nameOfList);
		addEventHelper(calendarEntry, eventList);
	}
	
	/**
	 * 
	 * Helper Method to add Event entry with specified data CalendarEntry to the specified EventList
	 * @param calendarEntry an instance of CalendarEntry 
	 * @throws PIMException throws PIM Exception
	*/
	private void addEventHelper(CalendarEntry calendarEntry, EventList eventList) throws PIMException {
		try {
			Event event = eventList.createEvent();
			int[] fields = event.getPIMList().getSupportedFields();
			// For each supported PIM field
			for (int i = 0; i < fields.length; i++) {
				int field = fields[i];
	
		        // Get the field's data type
		        int dataType = event.getPIMList().getFieldDataType(field);
		        switch (dataType) {
		        	case PIMItem.STRING: {
		        		if(field == Event.LOCATION) {
		        			event.addString(Event.LOCATION, PIMItem.ATTR_NONE, calendarEntry.getLocation());
		        		} else if(field == Event.NOTE) {
		        			event.addString(Event.NOTE, PIMItem.ATTR_NONE, calendarEntry.getNotes());
		        		} else if(field == Event.SUMMARY) {
		        			event.addString(Event.SUMMARY, PIMItem.ATTR_NONE, calendarEntry.getSummary());
		        		} else if(field == Event.UID) {
		        			//calendarEntry.setUid(event.getString(field, 0));
		        		} 
		        		break;
		        	}
		        	case PIMItem.BOOLEAN: {
		                break;
		            }
		        	case PIMItem.STRING_ARRAY: {
		        		break;
		            }
		        	case PIMItem.DATE: {
		        		if(field == Event.START) {
		        			event.addDate(Event.START, PIMItem.ATTR_NONE, calendarEntry.getStartDate().getTimeInMillis());
		        		} else if(field == Event.END) {
		        			event.addDate(Event.END, PIMItem.ATTR_NONE, calendarEntry.getEndDate().getTimeInMillis());
//		        		} else if(field == Event.REVISION) {
//		        			event.addDate(Event.REVISION, PIMItem.ATTR_NONE, calendarEntry.getLastModifiedDate().getTime());
		        		}
		                break;
		            }
		        	case PIMItem.INT: {
		        		if(field == Event.CLASS) {
		        			Integer classOfEvent = (Integer)calendarEntry.getField("ClassOfEvent");
		        			if (classOfEvent != null) {
		        				event.addInt(Event.CLASS, PIMItem.ATTR_NONE, classOfEvent.intValue());
		        			}
		        		} else if(field == Event.ALARM) {
		        			event.addInt(Event.ALARM, PIMItem.ATTR_NONE, calendarEntry.getAlarm());
		        		}
		                break;
		            }
		            case PIMItem.BINARY: {
		            	break;
		            }
		        }
			}
		} catch(Exception e) {
			throw new PIMException(PimConstants.ADD_EVENT_EXCEPTION_STRING + e.getMessage());
		}
	}
	
	
	/**
	 *  Method to add ToDo entry with specified data PimTODo to default ToDoList
	 * @param pimToDo an instance of PimToDo 
	 * @throws PIMException throws PIM Exception
	 */
	public void addToDo(PimToDo pimToDo) throws PIMException {
		ToDoList toDoList = null;
		toDoList = getDefaultToDoList();
		addToDoHelper(pimToDo, toDoList);
	}
	
	/**
	 * Method to add ToDo entry with specified data PimTODo to the specified named ToDoList
	 * @param pimToDo an instance of PimToDo 
	 * @param nameOfList specifies the name of PIM List
	 * @throws PIMException throws PIM Exception
	 */
	public void addToDo(PimToDo pimToDo, String nameOfList) throws PIMException {
		ToDoList toDoList = null;
		toDoList = (ToDoList) this.getToDoList(nameOfList);
		addToDoHelper(pimToDo, toDoList);
	}
	
	/**
	 * Helper Method to add ToDo entry with specified data PimTODo to the specified ToDoList
	 * @param pimToDo an instance of PimToDo 
	 * @throws PIMException throws PIM Exception
	*/
	private void addToDoHelper(PimToDo pimToDo, ToDoList toDoList) throws PIMException {
		try {
			ToDo toDo = toDoList.createToDo();
			int[] fields = toDo.getPIMList().getSupportedFields();
			// For each supported PIM field
			for (int i = 0; i < fields.length; i++) {
				int field = fields[i];
	
		        // Get the field's data type
		        int dataType = toDo.getPIMList().getFieldDataType(field);
		        switch (dataType) {
		        	case PIMItem.STRING: {
		        		if(field == ToDo.NOTE) {
		        			toDo.addString(ToDo.NOTE, PIMItem.ATTR_NONE, pimToDo.getNote());
		        		} else if(field == ToDo.SUMMARY) {
		        			toDo.addString(ToDo.SUMMARY, PIMItem.ATTR_NONE, pimToDo.getSummary());
		        		} else if(field == ToDo.UID) {
		        			//pimToDo.setUid(toDo.getString(field, 0));
		        		} 
		        		break;
		        	}
		        	case PIMItem.BOOLEAN: {
		        		if(field == ToDo.COMPLETED) {
		        			toDo.addBoolean(ToDo.COMPLETED, PIMItem.ATTR_NONE, pimToDo.isCompleted());
		        		} 
		                break;
		            }
		        	case PIMItem.STRING_ARRAY: {
		        		break;
		            }
		        	case PIMItem.DATE: {
		        		if(field == ToDo.COMPLETION_DATE) {
		        			toDo.addDate(ToDo.COMPLETION_DATE, PIMItem.ATTR_NONE, pimToDo.getCompletionDate().getTime());
		        		} else if(field == ToDo.DUE) {
		        			toDo.addDate(ToDo.DUE, PIMItem.ATTR_NONE, pimToDo.getDueDate().getTime());
		        		} else if(field == ToDo.REVISION) {
		        			toDo.addDate(ToDo.REVISION, PIMItem.ATTR_NONE, pimToDo.getLastRevisedDate().getTime());
		        		}
		                break;
		            }
		        	case PIMItem.INT: {
		        		if(field == ToDo.CLASS) {
		        			toDo.addInt(ToDo.CLASS, PIMItem.ATTR_NONE, pimToDo.getClassOfToDo());
		        		}
		                break;
		            }
		            case PIMItem.BINARY: {
		            	break;
		            }
		        }
			}
		} catch(Exception e) {
			throw new PIMException(PimConstants.ADD_TODO_EXCEPTION_STRING + e.getMessage());
		}
	}
	
	
	/**
	 * Method to delete Contact entry with specified UID in PimContact in default ContactList
	 * @param pimContact an instance of PimContact 
	 * @throws PIMException throws PIM Exception
	 */
	public void removeContact(PimContact pimContact) throws PIMException {
		ContactList contactList = this.getDefaultContactList();
		removeContactHelper(pimContact, contactList);
	}
	
	/**
	 * Method to delete Contact entry with specified UID in PimContact in the specified named ContactList
	 * @param pimContact an instance of PimContact 
	 * @param nameOfList specifies the name of PIM List
	 * @throws PIMException throws PIM Exception
	 */
	public void removeContact(PimContact pimContact, String nameOfList) throws PIMException {
		ContactList contactList = this.getContactList(nameOfList);
		removeContactHelper(pimContact, contactList);
	}
	
	/**
	 * Helper method to delete Contact entry with specified UID in PimContact in the specified ContactList
	 * @param pimContact an instance of PimContact 
	 * @param contactList an instance of PIM List of contact type
	 * @throws PIMException throws PIM Exception
	 */
	private void removeContactHelper(PimContact pimContact, ContactList contactList) throws PIMException {
		Contact matchingContact = contactList.createContact();
		if(pimContact.getFormattedName() != null) {
			matchingContact.addString(Contact.FORMATTED_NAME, Contact.ATTR_NONE, pimContact.getFormattedName());
		}
		if(pimContact.getUid() != null) {
			matchingContact.addString(Contact.UID, Contact.ATTR_NONE, pimContact.getUid());
		}
		matchingContact.addInt(Contact.CLASS, PIMItem.ATTR_NONE, Contact.CLASS_PUBLIC);
		Enumeration matchingItems = null;
		matchingItems = contactList.items(matchingContact);
		Vector contactItems = new Vector();
		while(matchingItems.hasMoreElements()) {
			Contact contactItem = (Contact)matchingItems.nextElement();
			contactItems.addElement(contactItem);
		}
		if(contactItems.size() == 1) {
			contactList.removeContact((Contact)contactItems.elementAt(0));  
		}
	}
	
	
	/**
	 * Method to delete Event entry with specified UID in CalendarEntry in the default EventList
	 * @param calendarEntry an instance of CalendarEntry 
	 * @throws PIMException throws PIM Exception
	 */
	public void removeEvent(CalendarEntry calendarEntry) throws PIMException {
		EventList eventList = this.getDefaultEventList();
		removeEventHelper(calendarEntry, eventList);
	}
	
	/**
	 * Method to delete Event entry with specified UID in CalendarEntry in the specified named EventList
	 * @param calendarEntry an instance of CalendarEntry 
	 * @param nameOfList  specifies the name of PIM List
	 * @throws PIMException throws PIM Exception
	 */
	public void removeEvent(CalendarEntry calendarEntry, String nameOfList) throws PIMException {
		EventList eventList = this.getEventList(nameOfList);
		removeEventHelper(calendarEntry, eventList);
	}
	
	/**
	 * Helper method to delete Event entry with specified UID in CalendarEntry in the specified EventList
	 * @param calendarEntry an instance of CalendarEntry 
	 * @param eventList an instance of PIM List of event type 
	 * @throws PIMException throws PIM Exception
	 */
	private void removeEventHelper(CalendarEntry calendarEntry, EventList eventList) throws PIMException {
		Event matchingEvent = eventList.createEvent();
		if(calendarEntry.getSummary() != null) {
			matchingEvent.addString(Event.SUMMARY, PIMItem.ATTR_NONE, calendarEntry.getSummary());
		}
		if(calendarEntry.getLocation() != null) {
			matchingEvent.addString(Event.LOCATION, PIMItem.ATTR_NONE, calendarEntry.getLocation());
		}
		if(calendarEntry.getStartDate() != null) {
			matchingEvent.addDate(Event.START, PIMItem.ATTR_NONE, calendarEntry.getStartDate().getTimeInMillis());
		}
		if(calendarEntry.getId() != null) {
			matchingEvent.addString(Event.UID, PIMItem.ATTR_NONE, calendarEntry.getId());
		}
		matchingEvent.addInt(Event.CLASS, PIMItem.ATTR_NONE, Event.CLASS_PUBLIC);
		Enumeration matchingItems = null;
		matchingItems = eventList.items(matchingEvent);
		Vector eventItems = new Vector();
		while(matchingItems.hasMoreElements()) {
			Event eventItem = (Event)matchingItems.nextElement();
			eventItems.addElement(eventItem);
		}
		
		if(eventItems.size() == 1) {
			eventList.removeEvent((Event)eventItems.elementAt(0));  
		}
	}
	
	
	/**
	 * Method to delete ToDo entry with specified UID in PimTODO in the default ToDoList
	 * @param pimToDo an instance of PimToDo 
	 * @throws PIMException throws PIM Exception
	 */
	public void removeToDo(PimToDo pimToDo) throws PIMException {
		ToDoList toDoList = this.getDefaultToDoList();
		removeToDoHelper(pimToDo, toDoList);
	}
	
	/**
	 * Method to delete ToDo entry with specified UID in PimTODO in the specified named ToDoList
	 * @param pimToDo an instance of PimToDo 
	 * @param nameOfList specifies the name of PIM List
	 * @throws PIMException throws PIM Exception
	 */
	public void removeToDo(PimToDo pimToDo, String nameOfList) throws PIMException {
		ToDoList toDoList = this.getToDoList(nameOfList);
		removeToDoHelper(pimToDo, toDoList);
	}
	
	/**
	 * Helper method to delete ToDo entry with specified UID in PimTODO in the specified ToDoList
	 * @param pimToDo an instance of PimToDo 
	 * @param toDoList an instance of Pim List of ToDo Type
	 * @throws PIMException throws PIM Exception
	 */
	private void removeToDoHelper(PimToDo pimToDo, ToDoList toDoList) throws PIMException {
		ToDo matchingToDo = (ToDo)toDoList.createToDo();
		if(pimToDo.getNote() != null) {
			matchingToDo.addString(ToDo.NOTE, PIMItem.ATTR_NONE, pimToDo.getSummary());
		}
		if(pimToDo.getPriority() <= 9 && pimToDo.getPriority() >= 0) {
			matchingToDo.addInt(ToDo.PRIORITY, PIMItem.ATTR_NONE, pimToDo.getPriority());
		}
		if(pimToDo.getDueDate() != null) {
			matchingToDo.addDate(ToDo.DUE, PIMItem.ATTR_NONE, pimToDo.getDueDate().getTime());
		}
		if(pimToDo.getUid() != null) {
			matchingToDo.addString(ToDo.UID, PIMItem.ATTR_NONE, pimToDo.getUid());
		}
		matchingToDo.addInt(ToDo.CLASS, PIMItem.ATTR_NONE, ToDo.CLASS_PUBLIC);
		Enumeration matchingItems = null;
		matchingItems = toDoList.items(matchingToDo);
		Vector toDoItems = new Vector();
		while(matchingItems.hasMoreElements()) {
			ToDo toDoItem = (ToDo)matchingItems.nextElement();
			toDoItems.addElement(toDoItem);
		}
		if(toDoItems.size() == 1) {
			toDoList.removeToDo((ToDo)toDoItems.elementAt(0));  
		}
	}
	
	
	/**
	 * method to update Contact entry with given data in PimContact in the default ContactList
	 * @param pimContact an instance of PimContact 
	 * @throws PIMException throws PIM Exception
	 */
	public void updateContact(PimContact pimContact) throws PIMException {
		ContactList contactList = this.getDefaultContactList();
		updateContactHelper(pimContact, contactList);
	}
	
	/**
	 * Method to update Contact entry with given data in PimContact in the specified named ContactList
	 * @param pimContact an instance of PimContact 
	 * @param nameOfList specifies the name of PIM List
	 * @throws PIMException throws PIM Exception
	 */
	public void updateContact(PimContact pimContact, String nameOfList) throws PIMException {
		ContactList contactList = this.getContactList(nameOfList);
		updateContactHelper(pimContact, contactList);
	}
	
	/** 
	 * Helper method to update Contact entry with given data in PimContact in the specified ContactList
	 * @param pimContact an instance of PimContact 
	 * @param contactList an instance of Pim List of Contact Type
	 * @throws PIMException throws PIM Exception
	 */
	private void updateContactHelper(PimContact pimContact, ContactList contactList) throws PIMException {
		try {
			Contact contact = contactList.createContact();
			int[] fields = contact.getPIMList().getSupportedFields();
			// For each supported PIM field
			for (int i = 0; i < fields.length; i++) {
				int field = fields[i];
	
		        // Get the field's data type
		        int dataType = contact.getPIMList().getFieldDataType(field);
		        switch (dataType) {
		        	case PIMItem.STRING: {
		        		if(field == Contact.FORMATTED_NAME && pimContact.getFormattedName() != null) {
		        			contact.setString(Contact.FORMATTED_NAME, 0, Contact.ATTR_NONE, pimContact.getFormattedName());
		        		} else if(field == Contact.FORMATTED_ADDR && pimContact.getFormattedAddress() != null) {
		        			contact.setString(Contact.FORMATTED_ADDR, 0, Contact.ATTR_NONE, pimContact.getFormattedAddress());
		        		} else if(field == Contact.EMAIL && pimContact.getEmailAddress() != null) {
		        			contact.setString(Contact.EMAIL, 0, Contact.ATTR_NONE, pimContact.getEmailAddress());
		        		} else if(field == Contact.NICKNAME && pimContact.getNickName() != null) {
		        			contact.setString(Contact.NICKNAME, 0, Contact.ATTR_NONE, pimContact.getNickName());
		        		} else if(field == Contact.NOTE && pimContact.getNotes() != null) {
		        			contact.setString(Contact.NOTE, 0, Contact.ATTR_NONE, pimContact.getNotes());
		        		} else if(field == Contact.TEL && pimContact.getTelephoneNumber() != null) {
		        			contact.setString(Contact.TEL, 0, Contact.ATTR_NONE, pimContact.getTelephoneNumber());
		        		} else if(field == Contact.URL && pimContact.getUrlForContact() != null) {
		        			contact.setString(Contact.URL, 0, Contact.ATTR_NONE, pimContact.getUrlForContact());
		        		} else if(field == Contact.PHOTO_URL && pimContact.getPhotoUrl() != null) {
		        			contact.setString(Contact.PHOTO_URL, 0, Contact.ATTR_NONE, pimContact.getPhotoUrl());
		        		} else if(field == Contact.PUBLIC_KEY_STRING && pimContact.getPublicKeyString() != null) {
		        			contact.setString(Contact.PUBLIC_KEY_STRING, 0, Contact.ATTR_NONE, pimContact.getPublicKeyString());
		        		} else if(field == Contact.UID) {
		        			//pimContact.setUid(contact.getString(field, 0));
		        		}
		        		break;
		        	}
		        	case PIMItem.BOOLEAN: {
		                break;
		            }
		        	case PIMItem.STRING_ARRAY: {
		        		if(field == Contact.ADDR ) {
		        			String[] addressArray =  new String[contactList.stringArraySize(Contact.ADDR)];
		        			if(pimContact.getContactAddress() != null) {
			        			if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_COUNTRY) && pimContact.getContactAddress().getCountryOfAddress() != null) {
			        				addressArray[Contact.ADDR_COUNTRY] = pimContact.getContactAddress().getCountryOfAddress();
			        				//contact.setString(Contact.ADDR_COUNTRY, 0, Contact.ATTR_NONE, pimContact.getContactAddress().getCountryOfAddress());
			        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_EXTRA) && pimContact.getContactAddress().getExtraInfoOfAddress() != null) {
			        				addressArray[Contact.ADDR_EXTRA] = pimContact.getContactAddress().getExtraInfoOfAddress();
			        				//contact.setString(Contact.ADDR_EXTRA, 0, Contact.ATTR_NONE, pimContact.getContactAddress().getExtraInfoOfAddress());
			        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_LOCALITY) && pimContact.getContactAddress().getLocalityOfAddress() != null) {
			        				addressArray[Contact.ADDR_LOCALITY] = pimContact.getContactAddress().getLocalityOfAddress();
			        				//contact.setString(Contact.ADDR_LOCALITY, 0, Contact.ATTR_NONE, pimContact.getContactAddress().getLocalityOfAddress());
			        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_POBOX) && pimContact.getContactAddress().getPoBoxOfAddress() != null) {
			        				addressArray[Contact.ADDR_POBOX] = pimContact.getContactAddress().getPoBoxOfAddress();
			        				//contact.setString(Contact.ADDR_POBOX, 0, Contact.ATTR_NONE, pimContact.getContactAddress().getPoBoxOfAddress());
			        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_POSTALCODE) && pimContact.getContactAddress().getPostalCode() != null) {
			        				addressArray[Contact.ADDR_POSTALCODE] = pimContact.getContactAddress().getPostalCode();
			        				//contact.setString(Contact.ADDR_POSTALCODE, 0, Contact.ATTR_NONE, pimContact.getContactAddress().getPostalCode());
			        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_REGION) && pimContact.getContactAddress().getRegionOfAddress() != null) {
			        				addressArray[Contact.ADDR_REGION] = pimContact.getContactAddress().getRegionOfAddress();
			        				//contact.setString(Contact.ADDR_REGION, 0, Contact.ATTR_NONE, pimContact.getContactAddress().getRegionOfAddress());
			        			} else if(contactList.isSupportedArrayElement(Contact.ADDR, Contact.ADDR_STREET) && pimContact.getContactAddress().getStreetAddress() != null) {
			        				addressArray[Contact.ADDR_STREET] = pimContact.getContactAddress().getStreetAddress();
			        				//contact.setString(Contact.ADDR_STREET, 0, Contact.ATTR_NONE, pimContact.getContactAddress().getStreetAddress());
			        			}
		        			}
		        			contact.setStringArray(Contact.ADDR, 0, Contact.ATTR_NONE, addressArray);
		        		} else if(field == Contact.NAME) {
		        			String[] nameArray = new String[contactList.stringArraySize(Contact.NAME)];
		        			if(pimContact.getContactName() != null) {
			        			if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_FAMILY) && pimContact.getContactName().getFamilyName() != null) {
			        				nameArray[Contact.NAME_FAMILY] = pimContact.getContactName().getFamilyName();
			        				//contact.setString(Contact.NAME_FAMILY, 0, Contact.ATTR_NONE, pimContact.getContactName().getFamilyName());
			        			} else if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_GIVEN) && pimContact.getContactName().getGivenName() != null) {
			        				nameArray[Contact.NAME_GIVEN] = pimContact.getContactName().getGivenName();
			        				//contact.setString(Contact.NAME_GIVEN, 0, Contact.ATTR_NONE, pimContact.getContactName().getGivenName());
			        			} else if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_OTHER) && pimContact.getContactName().getOtherName() != null) {
			        				nameArray[Contact.NAME_OTHER] = pimContact.getContactName().getOtherName();
			        				//contact.setString(Contact.NAME_OTHER, 0, Contact.ATTR_NONE, pimContact.getContactName().getOtherName());
			        			} else if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_PREFIX) && pimContact.getContactName().getPrefixOfName() != null) {
			        				nameArray[Contact.NAME_PREFIX] = pimContact.getContactName().getPrefixOfName();
			        				//contact.setString(Contact.NAME_PREFIX, 0, Contact.ATTR_NONE, pimContact.getContactName().getPrefixOfName());
			        			} else if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_SUFFIX) && pimContact.getContactName().getSuffixOfName() != null) {
			        				nameArray[Contact.NAME_SUFFIX] = pimContact.getContactName().getSuffixOfName();
			        				//contact.setString(Contact.NAME_SUFFIX, 0, Contact.ATTR_NONE, pimContact.getContactName().getSuffixOfName());
			        			}
		        			}
		        			contact.addStringArray(Contact.NAME, Contact.ATTR_NONE, nameArray);
		        		} 
		        		break;
		            }
		        	case PIMItem.DATE: {
		        		if(field == Contact.BIRTHDAY && pimContact.getDateOfBirth() != null) {
		        			contact.setDate(Contact.BIRTHDAY, 0, Contact.ATTR_NONE, pimContact.getDateOfBirth().getTime());
		        		} else if(field == Contact.REVISION && pimContact.getLastRevision() != null) {
		        			contact.setDate(Contact.REVISION, 0, Contact.ATTR_NONE, pimContact.getLastRevision().getTime());
		        		}
		                break;
		            }
		        	case PIMItem.INT: {
		        		if(field == Contact.CLASS && pimContact.getClassOfContact() != 0) {
		        			contact.setInt(Contact.CLASS, 0, Contact.ATTR_NONE, pimContact.getClassOfContact());
		        		}
		                break;
		            }
		            case PIMItem.BINARY: {
		            	if(field == Contact.PHOTO && pimContact.getPhoto() != null) {
		            		contact.setBinary(Contact.PHOTO, 0, Contact.ATTR_NONE, pimContact.getPhoto(), 0, pimContact.getPhoto().length);
		        		} else if(field == Contact.PUBLIC_KEY && pimContact.getPublicKey() != null) {
		        			contact.setBinary(Contact.PUBLIC_KEY, 0, Contact.ATTR_NONE, pimContact.getPublicKey(), 0, pimContact.getPublicKey().length);
		        		}
		            	break;
		            }
		        }
			}
		} catch(Exception e) {
			throw new PIMException(PimConstants.UPDATE_CONTACT_EXCEPTION_STRING + e.getMessage());
		}
	}
	
	
	/**
	 * Method to update Event entry with given data in CalendarEntry in the default EventList
	 * @param calendarEntry an instance of CalendarEntry 
	 * @throws PIMException throws PIM Exception
	 */
	public void updateEvent(CalendarEntry calendarEntry) throws PIMException {
		EventList eventList = this.getDefaultEventList();
		updateEventHelper(calendarEntry, eventList);
	}
	
	/**
	 * Method to update Event entry with given data in CalendarEntry in the specified named EventList
	 * @param calendarEntry an instance of CalendarEntry 
	 * @param nameOfList specifies the name of PIM List
	 * @throws PIMException throws PIM Exception
	 */
	public void updateEvent(CalendarEntry calendarEntry, String nameOfList) throws PIMException {
		EventList eventList = this.getEventList(nameOfList);
		updateEventHelper(calendarEntry, eventList);
	}
	
	/**
	 * Helper method to update Event entry with given data in CalendarEntry in the specified EventList
	 * @param calendarEntry an instance of CalendarEntry 
	 * @param eventList an instance of PIM List of Event type
	 * @throws PIMException throws PIM Exception
	 */
	private void updateEventHelper(CalendarEntry calendarEntry, EventList eventList) throws PIMException {
		try {
			Event event = eventList.createEvent();
			int[] fields = event.getPIMList().getSupportedFields();
			// For each supported PIM field
			for (int i = 0; i < fields.length; i++) {
				int field = fields[i];
	
		        // Get the field's data type
		        int dataType = event.getPIMList().getFieldDataType(field);
		        switch (dataType) {
		        	case PIMItem.STRING: {
		        		if(field == Event.LOCATION) {
		        			event.setString(Event.LOCATION, 0, PIMItem.ATTR_NONE, calendarEntry.getLocation());
		        		} else if(field == Event.NOTE) {
		        			event.setString(Event.NOTE, 0, PIMItem.ATTR_NONE, calendarEntry.getNotes());
		        		} else if(field == Event.SUMMARY) {
		        			event.setString(Event.SUMMARY, 0, PIMItem.ATTR_NONE, calendarEntry.getSummary());
		        		} else if(field == Event.UID) {
		        			//calendarEntry.setUid(event.getString(field, 0));
		        		} 
		        		break;
		        	}
		        	case PIMItem.BOOLEAN: {
		                break;
		            }
		        	case PIMItem.STRING_ARRAY: {
		        		
		        		break;
		            }
		        	case PIMItem.DATE: {
		        		if(field == Event.START) {
		        			event.setDate(Event.START, 0, PIMItem.ATTR_NONE, calendarEntry.getStartDate().getTimeInMillis());
		        		} else if(field == Event.END) {
		        			event.setDate(Event.END, 0, PIMItem.ATTR_NONE, calendarEntry.getEndDate().getTimeInMillis());
//		        		} else if(field == Event.REVISION) {
//		        			event.setDate(Event.REVISION, 0, PIMItem.ATTR_NONE, calendarEntry.getLastModifiedDate().getTime());
		        		}
		                break;
		            }
		        	case PIMItem.INT: {
		        		if(field == Event.CLASS) {
		        			Integer classOfEvent = (Integer)calendarEntry.getField("ClassOfEvent");
		        			if (classOfEvent != null) {
		        				event.setInt(Event.CLASS, 0, PIMItem.ATTR_NONE, classOfEvent.intValue());
		        			}
		        		} else if(field == Event.ALARM) {
		        			event.setInt(Event.ALARM, 0, PIMItem.ATTR_NONE, calendarEntry.getAlarm());
		        		}
		                break;
		            }
		            case PIMItem.BINARY: {
		            	break;
		            }
		        }
			}
		} catch(Exception e) {
			throw new PIMException(PimConstants.UPDATE_EVENT_EXCEPTION_STRING + e.getMessage());
		}
	}
	
	
	/**
	 * Method to update ToDo entry with given data in PimToDo in the default TODOList
	 * @param pimToDo an instance of PimToDo   
	 * @throws PIMException throws PIM Exception
	 */
	public void updateToDo(PimToDo pimToDo) throws PIMException {
		ToDoList toDoList = this.getDefaultToDoList();
		updateToDoHelper(pimToDo, toDoList);
	}
	
	/**
	 * Method to update ToDo entry with given data in PimToDo in the specified named TODOList
	 * @param pimToDo an instance of PimToDo  
	 * @param nameOfList specifies the name of PIM List
	 * @throws PIMException throws PIM Exception
	 */
	public void updateToDo(PimToDo pimToDo, String nameOfList) throws PIMException {
		ToDoList toDoList = this.getToDoList(nameOfList);
		updateToDoHelper(pimToDo, toDoList);
	}
	
	/**
	 * Helper method to update ToDo entry with given data in PimToDo in the specified TODOList
	 * @param pimToDo an instance of PimToDo  
	 * @param toDoList an instance of PIM list of type ToDo
	 * @throws PIMException throws PIM Exception
	 */
	private void updateToDoHelper(PimToDo pimToDo, ToDoList toDoList) throws PIMException {
		try {
			ToDo toDo = toDoList.createToDo();
			int[] fields = toDo.getPIMList().getSupportedFields();
			// For each supported PIM field
			for (int i = 0; i < fields.length; i++) {
				int field = fields[i];
	
		        // Get the field's data type
		        int dataType = toDo.getPIMList().getFieldDataType(field);
		        switch (dataType) {
		        	case PIMItem.STRING: {
		        		if(field == ToDo.NOTE) {
		        			toDo.setString(ToDo.NOTE, 0, PIMItem.ATTR_NONE, pimToDo.getNote());
		        		} else if(field == ToDo.SUMMARY) {
		        			toDo.setString(ToDo.SUMMARY, 0, PIMItem.ATTR_NONE, pimToDo.getSummary());
		        		} else if(field == ToDo.UID) {
		        			//pimToDo.setUid(toDo.getString(field, 0));
		        		} 
		        		break;
		        	}
		        	case PIMItem.BOOLEAN: {
		        		if(field == ToDo.COMPLETED) {
		        			toDo.setBoolean(ToDo.COMPLETED, 0,  PIMItem.ATTR_NONE, pimToDo.isCompleted());
		        		} 
		                break;
		            }
		        	case PIMItem.STRING_ARRAY: {
		        		break;
		            }
		        	case PIMItem.DATE: {
		        		if(field == ToDo.COMPLETION_DATE) {
		        			toDo.setDate(ToDo.COMPLETION_DATE, 0, PIMItem.ATTR_NONE, pimToDo.getCompletionDate().getTime());
		        		} else if(field == ToDo.DUE) {
		        			toDo.setDate(ToDo.DUE, 0, PIMItem.ATTR_NONE, pimToDo.getDueDate().getTime());
		        		} else if(field == ToDo.REVISION) {
		        			toDo.setDate(ToDo.REVISION, 0, PIMItem.ATTR_NONE, pimToDo.getLastRevisedDate().getTime());
		        		}
		                break;
		            }
		        	case PIMItem.INT: {
		        		if(field == ToDo.CLASS) {
		        			toDo.setInt(ToDo.CLASS, 0, PIMItem.ATTR_NONE, pimToDo.getClassOfToDo());
		        		}
		                break;
		            }
		            case PIMItem.BINARY: {
		            	break;
		            }
		        }
			}
		} catch(Exception e) {
			throw new PIMException(PimConstants.UPDATE_TODO_EXCEPTION_STRING + e.getMessage());
		}
	}
	
	
	/**
	 * retrieves all categories in the specified contact list
	 * @param contactList an instance of PIM List of type CONTACT
	 * @return returns an array of names of categories associated with contact list
	 * @throws PIMException throws PIM Exception
	 */
	public String[] getAllCategoriesOfContactList(ContactList contactList) throws PIMException {
		String[] categories = null;
		categories = contactList.getCategories();
		return categories;

	}
	
	/**
	 * retrieves all categories in the specified event list
	 * @param eventList an instance of PIM List of type EVENT
	 * @return returns an array of names of categories associated with event list
	 * @throws PIMException throws PIM Exception
	 */
	public String[] getAllCategoriesOfEventList(EventList eventList) throws PIMException {
		String[] categories = null;
		categories = eventList.getCategories();
		return categories;
	}
	
	/**
	 * retrieves all categories in the specified to do list
	 * @param toDoList an instance of PIM List of type Todo
	 * @return returns an array of names of categories associated with todo list
	 * @throws PIMException throws PIM Exception
	 */
	public String[] getAllCategoriesOfToDoList(ToDoList toDoList) throws PIMException {
		String[] categories = null;
		categories = toDoList.getCategories();
		return categories;
	}
	
	
	/**Method to add a new category to the Contact List.
	 * @param contactList an instance of PIM List of type CONTACT
	 * @param nameOfCategory specifies the name of category
	 * @throws PIMException throws PIM Exception
	 */
	public void addCategoryToContactList(ContactList contactList, String nameOfCategory) throws PIMException {
		String[] categories  = contactList.getCategories();
		if(contactList.maxCategories() == categories.length) {
			throw new PIMException(PimConstants.CATEGORIES_EXCEPTION_STRING);
		}
		contactList.addCategory(nameOfCategory);
	}
	
	/**Method to add a new category to the Event List.
	 * @param eventList an instance of PIM List of type EVENT
	 * @param nameOfCategory specifies the name of category
	 * @throws PIMException throws PIM Exception
	 */
	public void addCategoryToEventList(EventList eventList, String nameOfCategory) throws PIMException {
		String[] categories  = eventList.getCategories();
		if(eventList.maxCategories() == categories.length) {
			throw new PIMException(PimConstants.CATEGORIES_EXCEPTION_STRING);
		}
		eventList.addCategory(nameOfCategory);
	}
	
	/**Method to add a new category to the ToDo List.
	 * @param toDoList an instance of PIM List of type Todo
	 * @param nameOfCategory specifies the name of category
	 * @throws PIMException throws PIM Exception
	 */
	public void addCategoryToToDoList(ToDoList toDoList, String nameOfCategory) throws PIMException {
		String[] categories  = toDoList.getCategories();
		if(toDoList.maxCategories() == categories.length) {
			throw new PIMException(PimConstants.CATEGORIES_EXCEPTION_STRING);
		}
		toDoList.addCategory(nameOfCategory);
	}
	
	
	/**
	 * returns all associated contact objects with given contact list and category name
	 * @param contactList an instance of PIM List of type CONTACT
	 * @param nameOfCategory specifies the name of category
	 * @return returns all PIM Contacts as an array of PimContact Objects of given category or null if there is no category with given name
	 * @throws PIMException throws PIM Exception
	 */
	public PimContact[] getAllContactsByCategory(ContactList contactList, String nameOfCategory) throws PIMException{
		if(contactList.isCategory(nameOfCategory)) {
			Enumeration enumeratedItems = contactList.itemsByCategory(nameOfCategory);
			return this.getAllContactsOfEnumeration(contactList, enumeratedItems);
		}
		return null;
	}
	
	/**
	 * returns all associated event objects with given event list and category name
	 * @param eventList an instance of PIM List of type EVENT
	 * @param nameOfCategory specifies the name of category
	 * @return returns all PIM Events as an array of CalendarEntry Objects  of given category or null if there is no category with given name
	 * @throws PIMException throws PIM Exception
	 */
	public CalendarEntry[] getAllEventsByCategory(EventList eventList, String nameOfCategory) throws PIMException{
		if(eventList.isCategory(nameOfCategory)) {
			Enumeration enumeratedItems = eventList.itemsByCategory(nameOfCategory);
			return this.getAllEventsOfEnumeration(eventList, enumeratedItems);
		}
		return null;
	}
	
	/**
	 * returns all associated todo objects with given todolist and category name
	 * @param toDoList an instance of PIM List of type Todo
	 * @param nameOfCategory specifies the name of category
	 * @return returns all PIM todos as an array of PimToDo Objects  of given category or null if there is no category with given name
	 * @throws PIMException throws PIM Exception
	 */
	public PimToDo[] getAllToDosByCategory(ToDoList toDoList, String nameOfCategory) throws PIMException{
		if(toDoList.isCategory(nameOfCategory)) {
			Enumeration enumeratedItems = toDoList.itemsByCategory(nameOfCategory);
			return this.getAllToDosOfEnumeration(toDoList, enumeratedItems);
		}
		return null;
	}
	
	/**
	 * deletes category along with items from pim list of given categoric name
	 * @param pimList an instance of PIM list
	 * @param nameOfCategory specifies the name of category 
	 * @throws PIMException throws PIM Exception
	 */
	public void deleteCategoryFromPimList(PIMList pimList, String nameOfCategory) throws PIMException {
		if(pimList.isCategory(nameOfCategory)) {
			pimList.deleteCategory(nameOfCategory, false);
		}
	}
	
	
	/**
	 * Renames a category if exists to another specified category name
	 * @param pimList an instance of PIM list
	 * @param fromNameOfCategory specifies the name of from category 
	 * @param toNameOfCategory specifies the name of to category 
	 * @throws PIMException throws PIM Exception
	 */
	public void renameCategory(PIMList pimList, String fromNameOfCategory, String toNameOfCategory) throws PIMException {
		if(pimList.isCategory(fromNameOfCategory)) {
			pimList.renameCategory(fromNameOfCategory, toNameOfCategory);
		}
	}
	
	
	/**Method to return all the names of the field labels of the Contact List. 
	 * @param nameOfList specifies the name of PIM List
	 * @return returns all field labels specific to device of a given pim contact list
	 * @throws PIMException throws PIM Exception
	 */
	public String[] getFieldLabelsOfContactList(String nameOfList) throws PIMException {
		ContactList contactList= this.getContactList(nameOfList);
		int[] fields = contactList.getSupportedFields();
		
		String[] fieldLabels = new String[fields.length];
		for(int i = 0 ; i < fields.length ; i++) {
			fieldLabels[i] = contactList.getFieldLabel(fields[i]);
		}
		return fieldLabels;
	}
	
	
	/**Method to return all the names of the field labels of the Event List. 
	 * @param nameOfList specifies the name of PIM List
	 * @return returns all field labels specific to device of a given pim event list
	 * @throws PIMException throws PIM Exception
	 */
	public String[] getFieldLabelsOfEventList(String nameOfList) throws PIMException {
		EventList eventList= this.getEventList(nameOfList);
		int[] fields = eventList.getSupportedFields();
		String[] fieldLabels = new String[fields.length];
		for(int i = 0 ; i < fields.length ; i++) {
			fieldLabels[i] = eventList.getFieldLabel(fields[i]);
		}
		return fieldLabels;
	}
	
	/**Method to return all the names of the field labels of the ToDo List. 
	 * @param nameOfList specifies the name of PIM List
	 * @return returns all field labels specific to device of a given pim todo list
	 * @throws PIMException throws PIM Exception
	 */
	public String[] getFieldLabelsOfToDoList(String nameOfList) throws PIMException {
		ToDoList toDoList = this.getToDoList(nameOfList);
		int[] fields = toDoList.getSupportedFields();
		String[] fieldLabels = new String[fields.length];
		for(int i = 0 ; i < fields.length ; i++) {
			fieldLabels[i] = toDoList.getFieldLabel(fields[i]);
		}
		return fieldLabels;
	}
	
	/**Method to return the PIM Contact item entitled by the unique id.
	 * @param uid unique identifier to identify PIM item with in pim lists
	 * @return returns the matched PimContact object for the given unique id or null if there is no matching record
	 * @throws PIMException throws PIM Exception
	 */
	public PimContact findContactByUid(String uid) throws PIMException {
		ContactList contactList = this.getDefaultContactList();
		Contact matchingContact = contactList.createContact();
		if(uid != null) {
			matchingContact.addString(Contact.UID, Contact.ATTR_NONE, uid);
		}
		Enumeration matchingItems = null;
		matchingItems = contactList.items(matchingContact);
		
		PimContact[] contactItems = this.getAllContactsOfEnumeration(contactList, matchingItems);
		if(contactItems != null && contactItems.length >= 0) {
			return contactItems[0]; 
		}
		return null;
	}
	
	/**Method to return the PIMEvent item entitled by the unique id.
	 * @param uid unique identifier to identify PIM item with in pim lists
	 * @return returns matched PIM Event as an CalendarEntry Object or null if there is no matching record
	 * @throws PIMException throws PIM Exception
	 */
	public CalendarEntry findEventByUid(String uid) throws PIMException {
		EventList eventList = this.getDefaultEventList();
		Event matchingEvent = eventList.createEvent();
		if(uid != null) {
			matchingEvent.addString(Contact.UID, Contact.ATTR_NONE, uid);
		}
		Enumeration matchingItems = null;
		matchingItems = eventList.items(matchingEvent);
		
		CalendarEntry[] eventItems = this.getAllEventsOfEnumeration(eventList, matchingItems);
		if(eventItems != null && eventItems.length >= 0) {
			return eventItems[0]; 
		}
		return null;
	}
	
	/**Method to return the PIMToDo item entitled by the unique id.
	 * @param uid unique identifier to identify PIM item with in pim lists
	 * @return returns the matched PimToDo object for the given unique id or null if there is no matching record
	 * @throws PIMException throws PIM Exception
	 */
	public PimToDo findToDoByUid(String uid) throws PIMException {
		ToDoList toDoList = this.getDefaultToDoList();
		ToDo matchingToDo = toDoList.createToDo();
		if(uid != null) {
			matchingToDo.addString(Contact.UID, Contact.ATTR_NONE, uid);
		}
		Enumeration matchingItems = null;
		matchingItems = toDoList.items(matchingToDo);
		
		PimToDo[] toDoItems = this.getAllToDosOfEnumeration(toDoList, matchingItems);
		if(toDoItems != null && toDoItems.length >= 0) {
			return toDoItems[0]; 
		}
		return null;
	}
	
	
	/**Method to return all the names of the array elements included in the given Contact List.
	 * @param fieldLabel specifies the name/label of the field of array type
	 * @param nameOfList specifies the name of PIM List
	 * @return returns an array of names of given array type field elements
	 * @throws PIMException throws PIM Exception
	 */
	public String[] getArrayElementNamesOfContactList(String fieldLabel, String nameOfList)  throws PIMException {
		String[] arrayElementNames = null;
		ContactList contactList = null;
		if(nameOfList == null) {
			contactList = getDefaultContactList();
		} else {
			contactList = getContactList(nameOfList);
		}
		
		int[] fields = contactList.getSupportedFields();
		// For each supported PIM field
		for (int i = 0; i < fields.length; i++) {
			int field = fields[i];
	        // Get the field's data type
	        int dataType = contactList.getFieldDataType(field);
	        // Get the field's label
	        String label = contactList.getFieldLabel(field);
	        if(dataType == PIMItem.STRING_ARRAY) {
	        	if(fieldLabel.equals(label)) {
	        		int[] supportedArrayElements = contactList.getSupportedArrayElements(field);
	        		arrayElementNames = new String[supportedArrayElements.length];
	        		for(int j = 0 ; j < supportedArrayElements.length ; j++) {
	        			arrayElementNames[j] = contactList.getArrayElementLabel(field, supportedArrayElements[j]);
	        		}
	        	}
	        }
		}	
		return arrayElementNames;
	}
	
	/**Method to return all the names of the attributes included in the given Contact List.
	 * @param fieldLabel specifies the name/label of the field of array type
	 * @param nameOfList specifies the name of PIM List
	 * @return returns an array of names of attributes of given field
	 * @throws PIMException throws PIM Exception
	 */
	public String[] getAttributeNamesOfContactList(String fieldLabel,  String nameOfList)  throws PIMException {
		String[] attributeNames = null;
		ContactList contactList = null;
		if(nameOfList == null) {
			contactList = getDefaultContactList();
		} else {
			contactList = getContactList(nameOfList);
		}
		int[] fields = contactList.getSupportedFields();
		// For each supported PIM field
		for (int i = 0; i < fields.length; i++) {
			int field = fields[i];
	        // Get the field's data type
	        String label = contactList.getFieldLabel(field);
	        
	        if(fieldLabel.equals(label)) {
	        	int[] supportedAttributes = contactList.getSupportedAttributes(field);
        		if(supportedAttributes != null) {
	        		attributeNames = new String[supportedAttributes.length];
	        		for(int j = 0 ; j < supportedAttributes.length ; j++) {
	        			attributeNames[j] = contactList.getAttributeLabel(supportedAttributes[j]);
	        		}
	        	}	
	        }
	    }	
		return attributeNames;
	}
	
	/**Method to return all the names of the array elements included in the given Event List.
	 * @param fieldLabel specifies the name/label of the field of array type
	 * @param nameOfList specifies the name of PIM List
	 * @return returns an array of names of given array type field elements
	 * @throws PIMException throws PIM Exception
	 */
	public String[] getArrayElementNamesOfEventList(String fieldLabel, String nameOfList)  throws PIMException {
		String[] arrayElementNames = null;
		EventList eventList = null;
		if(nameOfList == null) {
			eventList = getDefaultEventList();
		} else {
			eventList = getEventList(nameOfList);
		}
		int[] fields = eventList.getSupportedFields();
		// For each supported PIM field
		for (int i = 0; i < fields.length; i++) {
			int field = fields[i];
	        // Get the field's data type
	        int dataType = eventList.getFieldDataType(field);
	        // Get the field's label
	        String label = eventList.getFieldLabel(field);
	        if(dataType == PIMItem.STRING_ARRAY) {
	        	if(fieldLabel.equals(label)) {
	        		int[] supportedArrayElements = eventList.getSupportedArrayElements(field);
	        		arrayElementNames = new String[supportedArrayElements.length];
	        		for(int j = 0 ; j < supportedArrayElements.length ; j++) {
	        			arrayElementNames[j] = eventList.getArrayElementLabel(field, supportedArrayElements[j]);
	        		}
	        	}
	        }
		}	
		return arrayElementNames;
	}
	
	/**Method to return all the names of the attributes included in the given Event List.
	 * @param fieldLabel specifies the name/label of the field of array type
	 * @param nameOfList specifies the name of PIM List
	 * @return returns an array of names of attributes of given field
	 * @throws PIMException throws PIM Exception
	 */
	public String[] getAttributeNamesOfEventList(String fieldLabel,  String nameOfList)  throws PIMException {
		String[] attributeNames = null;
		EventList eventList = null;
		if(nameOfList == null) {
			eventList = getDefaultEventList();
		} else {
			eventList = getEventList(nameOfList);
		}
		int[] fields = eventList.getSupportedFields();
		// For each supported PIM field
		for (int i = 0; i < fields.length; i++) {
			int field = fields[i];
	        // Get the field's data type
	        String label = eventList.getFieldLabel(field);
	        if(fieldLabel.equals(label)) {
	        	int[] supportedAttributes = eventList.getSupportedAttributes(field);
	        	if(supportedAttributes != null) {
	        		attributeNames = new String[supportedAttributes.length];
	        		for(int j = 0 ; j < supportedAttributes.length ; j++) {
	        			attributeNames[j] = eventList.getAttributeLabel(supportedAttributes[j]);
	        		}
	        	}	
	        }
	    }	
		return attributeNames;
	}
	
	/**Method to return all the names of the array elements included in the given ToDo List.
	 * @param fieldLabel specifies the name/label of the field of array type
	 * @param nameOfList specifies the name of PIM List
	 * @return returns an array of names of given array type field elements
	 * @throws PIMException throws PIM Exception
	 */
	public String[] getArrayElementNamesOfToDoList(String fieldLabel, String nameOfList)  throws PIMException {
		String[] arrayElementNames = null;
		ToDoList toDoList = null;
		if(nameOfList == null) {
			toDoList = (ToDoList) this.getDefaultToDoList();
		} else {
			toDoList = (ToDoList) this.getToDoList(nameOfList);
		}
		int[] fields = toDoList.getSupportedFields();
		// For each supported PIM field
		for (int i = 0; i < fields.length; i++) {
			int field = fields[i];
	        // Get the field's data type
	        int dataType = toDoList.getFieldDataType(field);
	        // Get the field's label
	        String label = toDoList.getFieldLabel(field);
	        if(dataType == PIMItem.STRING_ARRAY) {
	        	if(fieldLabel.equals(label)) {
	        		int[] supportedArrayElements = toDoList.getSupportedArrayElements(field);
	        		arrayElementNames = new String[supportedArrayElements.length];
	        		for(int j = 0 ; j < supportedArrayElements.length ; j++) {
	        			arrayElementNames[j] = toDoList.getArrayElementLabel(field, supportedArrayElements[j]);
	        		}
	        	}
	        }
		}	
		return arrayElementNames; 
	}
	
	/**Method to return all the names of the attributes included in the given ToDo List. 
	 * @param fieldLabel specifies the name/label of the field of array type
	 * @param nameOfList specifies the name of PIM List
	 * @return returns an array of names of attributes of given field
	 * @throws PIMException throws PIM Exception
	 */
	public String[] getAttributeNamesOfToDoList(String fieldLabel,  String nameOfList)  throws PIMException {
		String[] attributeNames = null;
		ToDoList toDoList = null;
		if(nameOfList == null) {
			toDoList = (ToDoList) this.getDefaultToDoList();
		} else {
			toDoList = (ToDoList) this.getToDoList(nameOfList);
		}
		int[] fields = toDoList.getSupportedFields();
		//For each supported PIM field
		for (int i = 0; i < fields.length; i++) {
			int field = fields[i];
	        //Get the field's label
	        String label = toDoList.getFieldLabel(field);
	        
	        if(fieldLabel.equals(label)) {
        		int[] supportedAttributes = toDoList.getSupportedAttributes(field);
	        	if(supportedAttributes != null) {
	        		attributeNames = new String[supportedAttributes.length];
	        		for(int j = 0 ; j < supportedAttributes.length ; j++) {
	        			attributeNames[j] = toDoList.getAttributeLabel(supportedAttributes[j]);
	        		}
        		}
	        }
	    }	
		return attributeNames;
	}
}
