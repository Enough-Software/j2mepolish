//#condition polish.android
package de.enough.polish.android.pim.enough;

import java.util.Enumeration;

import de.enough.polish.android.pim.Contact;
import de.enough.polish.android.pim.ContactList;
import de.enough.polish.android.pim.PIMException;
import de.enough.polish.android.pim.PIMItem;
import de.enough.polish.android.pim.UnsupportedFieldException;

public class ContactListImpl extends AbstractPIMList implements ContactList {

	public static final FieldInfo CONTACT_ADDR_FIELD_INFO = new FieldInfo(Contact.ADDR,"Address",7,Contact.ADDR_EXTRA,new int[] {Contact.ADDR_EXTRA}, new int[] {Contact.ATTR_HOME,Contact.ATTR_WORK,Contact.ATTR_OTHER,Contact.ATTR_NONE});
	public static final FieldInfo CONTACT_NAME_FIELD_INFO = new FieldInfo(Contact.NAME,"Name",5,Contact.NAME_OTHER,new int[] {Contact.NAME_OTHER},new int[] {Contact.ATTR_NONE});
	public static final FieldInfo CONTACT_TEL_FIELD_INFO = new FieldInfo(Contact.TEL,PIMItem.STRING,"Phone",new int[] {Contact.ATTR_MOBILE,Contact.ATTR_WORK,Contact.ATTR_HOME,Contact.ATTR_OTHER,Contact.ATTR_PAGER,Contact.ATTR_FAX,Contact.ATTR_NONE});
	public static final FieldInfo CONTACT_EMAIL_FIELD_INFO = new FieldInfo(Contact.EMAIL,PIMItem.STRING,"Email",new int[] {Contact.ATTR_MOBILE,Contact.ATTR_WORK,Contact.ATTR_HOME,Contact.ATTR_OTHER,Contact.ATTR_NONE});
	public static final FieldInfo CONTACT_NOTE_FIELD_INFO = new FieldInfo(Contact.NOTE,PIMItem.STRING,"Note",new int[] {Contact.ATTR_NONE});
	public static final FieldInfo CONTACT_FORMATTED_NAME_FIELD_INFO = new FieldInfo(Contact.FORMATTED_NAME,PIMItem.STRING,"Formatted Name",new int[] {Contact.ATTR_NONE});
	public static final FieldInfo CONTACT_UID_FIELD_INFO = new FieldInfo(Contact.UID,PIMItem.STRING,"Unique identifier",new int[] {Contact.ATTR_NONE});

	private ContactDao contactDao;

	ContactListImpl(String name, int mode, ContactDao contactDao) {
		super(name,mode);
		this.contactDao = contactDao;
		setFieldInfos(new FieldInfo[] {CONTACT_ADDR_FIELD_INFO,CONTACT_EMAIL_FIELD_INFO,CONTACT_NAME_FIELD_INFO,CONTACT_TEL_FIELD_INFO,CONTACT_NOTE_FIELD_INFO,CONTACT_FORMATTED_NAME_FIELD_INFO});
	}

	public Contact createContact() {
		ensureListWriteable();
		ContactImpl contact = new ContactImpl(this);
		return contact;
	}

	public Enumeration items() throws PIMException {
		ensureListReadable();
		Enumeration items;
		try {
			items = this.contactDao.items(this);
		} catch(Exception e) {
			throw new PIMException(e.getMessage());
		}
		return items;
	}

	public Contact importContact(Contact contact) {
		if(contact == null) {
			throw new NullPointerException("Parameter 'contact' must not be null.");
		}
		ensureListWriteable();
		return this.contactDao.importContact((ContactImpl)contact);
	}
	
	public void removeContact(Contact contact) throws PIMException {
		if(contact == null) {
			throw new NullPointerException("Parameter 'contact' must not be null.");
		}
		ensureListWriteable();
		ContactImpl contactImpl = (ContactImpl)contact;
		try {
			this.contactDao.removeContact(contactImpl);
		}
		catch(Exception e) {
			throw new PIMException(e.getMessage(),PIMException.GENERAL_ERROR);
		}
	}

	void persist(ContactImpl contact) {
		this.contactDao.persist(contact);
	}

	@Override
	public String getAttributeLabel(int attribute) {
		switch(attribute) {
			case Contact.ATTR_ASST: return "Assistant";
			case Contact.ATTR_AUTO: return "Auto";
			case Contact.ATTR_FAX: return "Fax";
			case Contact.ATTR_HOME: return "Home";
			case Contact.ATTR_MOBILE: return "Mobile";
			case Contact.ATTR_OTHER: return "Other";
			case Contact.ATTR_PAGER: return "Pager";
			case Contact.ATTR_PREFERRED: return "Preferred";
			case Contact.ATTR_SMS: return "SMS";
			case Contact.ATTR_WORK: return "Work";
			default: return super.getAttributeLabel(attribute);
		}
	}

	public String getArrayElementLabel(int stringArrayField, int arrayElement) {
		switch(stringArrayField) {
		case Contact.NAME:
			switch(arrayElement) {
				case Contact.NAME_FAMILY: return "Family";
				case Contact.NAME_GIVEN: return "Given";
				case Contact.NAME_OTHER: return "Other";
				case Contact.NAME_PREFIX: return "Prefix";
				case Contact.NAME_SUFFIX: return "Suffix";
				default: throw new IllegalArgumentException("Array element '"+arrayElement+"' is not valid for field 'Contact.NAME'");
			}
		case Contact.ADDR:
			switch(arrayElement) {
				case Contact.ADDR_COUNTRY: return "Country";
				case Contact.ADDR_EXTRA: return "Extra";
				case Contact.ADDR_LOCALITY: return "Locality";
				case Contact.ADDR_POBOX: return "POBox";
				case Contact.ADDR_POSTALCODE: return "Postalcode";
				case Contact.ADDR_REGION: return "Region";
				case Contact.ADDR_STREET: return "Street";
				default: throw new IllegalArgumentException("Array element '"+arrayElement+"' is not valid for field 'Contact.ADDR'");
			}
		default: throw new UnsupportedFieldException("No field with id '"+stringArrayField+"' present for ContactList.");
		}
	}

	public void close() throws PIMException {
		// TODO: No need for closing?!
	}

	public void addCategory(String category) throws PIMException {
		if(category == null) {
			throw new NullPointerException("Parameter 'category' must not be null.");
		}
		ensureListWriteable();
		throw new UnsupportedOperationException();
	}

	public void deleteCategory(String category, boolean deleteUnassignedItems) throws PIMException {
		throw new UnsupportedOperationException();
	}
	
	public boolean isCategory(String category) throws PIMException {
		if(category == null) {
			throw new NullPointerException();
		}
		throw new UnsupportedOperationException();
	}
	
	public String[] getCategories() throws PIMException {
		return new String[0];
	}
	
	public int maxCategories() {
		return 0;
	}
	
	public void renameCategory(String currentCategory, String newCategory) throws PIMException {
		ensureListWriteable();
		throw new UnsupportedOperationException();
	}
	
	
	public Enumeration items(PIMItem matchingItem) throws PIMException {
		ensureListReadable();
		Enumeration items;
		try {
			items = this.contactDao.items((ContactImpl)matchingItem);
		} catch(Exception e) {
			throw new PIMException(e.getMessage());
		}
		return items;
	}

	public Enumeration items(String matchingValue) throws PIMException {
		ensureListReadable();
		Enumeration items;
		try {
			items = this.contactDao.items(matchingValue);
		} catch(Exception e) {
			throw new PIMException(e.getMessage());
		}
		return items;
	}

	public Enumeration itemsByCategory(String category) throws PIMException {
		ensureListReadable();
		Enumeration items;
		try {
			items = this.contactDao.itemsByCategory(category);
		} catch(Exception e) {
			throw new PIMException(e.getMessage());
		}
		return items;
	}
	public ContactDao getContactDao() {
		return this.contactDao;
	}
}
