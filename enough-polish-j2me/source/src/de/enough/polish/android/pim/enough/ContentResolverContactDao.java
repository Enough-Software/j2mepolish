//#condition polish.android
package de.enough.polish.android.pim.enough;

import java.util.Enumeration;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.Contacts.People;
import de.enough.polish.android.midlet.MidletBridge;
import de.enough.polish.android.pim.Contact;
import de.enough.polish.android.pim.PIMItem;

/**
 * This Data Access Object will manage JavaME PIM contact objects and talks to the Contacts ContentResolver on android.
 * It uses API level 3 and not the new ContactsContract.
 * @author rickyn
 *
 */
public class ContentResolverContactDao implements ContactDao {

	private final ContentResolver contentResolver;

	public ContentResolverContactDao() {
		this.contentResolver = MidletBridge.instance.getContentResolver();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.android.pim.enough.ContactDao#persist(de.enough.polish.android.pim.enough.ContactImpl)
	 */
	public void persist(ContactImpl contact) {
		final boolean isNew = contact.isNew();
		final Uri personUri;
		final ContentValues values = new ContentValues();
		final long id;
		if(isNew) {
			personUri = this.contentResolver.insert(People.CONTENT_URI, values);
			id = ContentUris.parseId(personUri);
			// insert this contact into "My Contacts" group in order to see it in the Contact viewer
			Contacts.People.addToMyContactsGroup(this.contentResolver, id);
		} else {
			id = contact.getId();
			personUri = ContentUris.withAppendedId(People.CONTENT_URI, id);
		}

		// Update the name
		values.clear();
		StringBuffer buffer = new StringBuffer();
		String[] names = contact.getStringArray(Contact.NAME, 0);
		boolean inputSpace = false;
		if(names[Contact.NAME_PREFIX] != null) {
			buffer.append(names[Contact.NAME_PREFIX]);
			inputSpace = true;
		}
		if(names[Contact.NAME_GIVEN] != null) {
			if(inputSpace) {
				buffer.append(" ");
			}
			buffer.append(names[Contact.NAME_GIVEN]);
			inputSpace = true;
		}
		if(names[Contact.NAME_FAMILY] != null) {
			if(inputSpace) {
				buffer.append(" ");
			}
			buffer.append(names[Contact.NAME_FAMILY]);
			inputSpace = true;
		}
		if(names[Contact.NAME_SUFFIX] != null) {
			if(inputSpace) {
				buffer.append(" ");
			}
			buffer.append(names[Contact.NAME_SUFFIX]);
			inputSpace = true;
		}
		if(names[Contact.NAME_OTHER] != null) {
			if(inputSpace) {
				buffer.append(" ");
			}
			buffer.append("(");
			buffer.append(names[Contact.NAME_OTHER]);
			buffer.append(")");
			inputSpace = true;
		}
		
		values.put(People.NAME, buffer.toString());
		
		// Update the Display name
//		int numberOfDisplayNames = contact.countValues(Contact.FORMATTED_NAME);
//		if(numberOfDisplayNames > 0) {
//			String name = contact.getString(Contact.FORMATTED_NAME, 0);
//			values.put(People.NAME, name);
//		}
		
		// Update the note
		int numberOfNotes = contact.countValues(Contact.NOTE);
		if(numberOfNotes > 0) {
			String name = contact.getString(Contact.NOTE, 0);
			values.put(People.NOTES, name);
		}
		this.contentResolver.update(personUri, values, null, null);

		// Update the address.
		int numberOfAddresses = contact.countValues(Contact.ADDR);
		for(int i = 0; i < numberOfAddresses; i++) {
			Uri addressUri = Uri.withAppendedPath(personUri, Contacts.People.ContactMethods.CONTENT_DIRECTORY);
			values.clear();
			values.put(Contacts.ContactMethods.KIND,new Integer(Contacts.KIND_POSTAL));
			
			int attributes = contact.getAttributes(Contact.ADDR, i);
			int type = convertAttrToAddressType(attributes);
			values.put(Contacts.ContactMethods.TYPE,new Integer(type));
			
			String[] addressElements = contact.getStringArray(Contact.ADDR, i);
			buffer = new StringBuffer();
			inputSpace = false;
			if(addressElements[Contact.ADDR_POBOX] != null) {
				buffer.append("PoBox ");
				buffer.append(addressElements[Contact.ADDR_POBOX]);
				inputSpace = true;
			}
			if(addressElements[Contact.ADDR_STREET] != null) {
				if(inputSpace) {
					buffer.append(" ");
				}
				buffer.append(addressElements[Contact.ADDR_STREET]);
				inputSpace = true;
			}
			if(addressElements[Contact.ADDR_POSTALCODE] != null) {
				if(inputSpace) {
					buffer.append(" ");
				}
				buffer.append(addressElements[Contact.ADDR_POSTALCODE]);
				inputSpace = true;
			}
			if(addressElements[Contact.ADDR_LOCALITY] != null) {
				if(inputSpace) {
					buffer.append(" ");
				}
				buffer.append(addressElements[Contact.ADDR_LOCALITY]);
				inputSpace = true;
			}
			if(addressElements[Contact.ADDR_COUNTRY] != null) {
				if(inputSpace) {
					buffer.append(" ");
				}
				buffer.append(addressElements[Contact.ADDR_COUNTRY]);
				inputSpace = true;
			}
			if(addressElements[Contact.ADDR_EXTRA] != null) {
				if(inputSpace) {
					buffer.append(" ");
				}
				buffer.append(addressElements[Contact.ADDR_EXTRA]);
				inputSpace = true;
			}
			
			values.put(Contacts.ContactMethods.DATA,buffer.toString());
			this.contentResolver.insert(addressUri, values);
		}
		
		int numberOfEMails = contact.countValues(Contact.EMAIL);
		for(int i = 0; i < numberOfEMails; i++) {
			Uri addressUri = Uri.withAppendedPath(personUri, Contacts.People.ContactMethods.CONTENT_DIRECTORY);
			values.clear();
			buffer = new StringBuffer();
			buffer.append(contact.getString(Contact.EMAIL, i));
			values.put(Contacts.ContactMethods.DATA,buffer.toString());

			values.put(Contacts.ContactMethods.KIND,new Integer(Contacts.KIND_EMAIL));
			
			int attributes = contact.getAttributes(Contact.EMAIL, i);
			int type = convertAttrToAddressType(attributes);
			values.put(Contacts.ContactMethods.TYPE,new Integer(type));
			
			this.contentResolver.insert(addressUri, values);
		}
		
		// Update the telephone number.
		int numberOfTelephoneNumbers = contact.countValues(Contact.TEL);
		for(int i = 0; i < numberOfTelephoneNumbers; i++) {
			Uri phoneUri = Uri.withAppendedPath(personUri, Contacts.People.Phones.CONTENT_DIRECTORY);
			values.clear();
			String telephoneNumber = contact.getString(Contact.TEL, i);
			values.put(Contacts.People.Phones.NUMBER,telephoneNumber);
			
			int attributes = contact.getAttributes(Contact.TEL, i);
			int type = convertAttrToTelType(attributes);
			values.put(Contacts.ContactMethods.TYPE,new Integer(type));
			
			this.contentResolver.insert(phoneUri, values);
		}
		
	}
	
	private int convertAttrToAddressType(int attribute) {
		if((attribute & Contact.ATTR_HOME) == Contact.ATTR_HOME){
			return Contacts.ContactMethods.TYPE_HOME;
		}
		if((attribute & Contact.ATTR_WORK) == Contact.ATTR_WORK){
			return Contacts.ContactMethods.TYPE_WORK;
		}
		if((attribute & Contact.ATTR_OTHER) == Contact.ATTR_OTHER){
			return Contacts.ContactMethods.TYPE_OTHER;
		}
		return Contacts.ContactMethods.TYPE_OTHER;
	}
	
	private int convertAttrToTelType(int attribute) {
		if((attribute & Contact.ATTR_MOBILE) == Contact.ATTR_MOBILE){
			return Contacts.People.Phones.TYPE_MOBILE;
		}
		if((attribute & Contact.ATTR_FAX) == Contact.ATTR_FAX){
			if((attribute & Contact.ATTR_HOME) == Contact.ATTR_HOME){
				return Contacts.People.Phones.TYPE_FAX_HOME;
			}
			if((attribute & Contact.ATTR_WORK) == Contact.ATTR_WORK){
				return Contacts.People.Phones.TYPE_FAX_WORK;
			}
			return Contacts.People.Phones.TYPE_FAX_WORK;
		}
		if((attribute & Contact.ATTR_HOME) == Contact.ATTR_HOME){
			return Contacts.People.Phones.TYPE_HOME;
		}
		if((attribute & Contact.ATTR_WORK) == Contact.ATTR_WORK){
			return Contacts.People.Phones.TYPE_WORK;
		}
		if((attribute & Contact.ATTR_PAGER) == Contact.ATTR_PAGER){
			return Contacts.People.Phones.TYPE_PAGER;
		}
		if((attribute & Contact.ATTR_OTHER) == Contact.ATTR_OTHER){
			return Contacts.People.Phones.TYPE_OTHER;
		}
		return Contacts.People.Phones.TYPE_OTHER;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.android.pim.enough.ContactDao#items(de.enough.polish.android.pim.enough.ContactListImpl)
	 */
	public Enumeration items(ContactListImpl contactListImpl) {
		return new ContactEnumeration(this,contactListImpl);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.android.pim.enough.ContactDao#getContactFromCursor(android.database.Cursor, de.enough.polish.android.pim.enough.ContactListImpl)
	 */
	public ContactImpl getContactFromCursor(Cursor peopleCursor,ContactListImpl contactListImpl) {
		ContactImpl contact = ContactFactory.getContactFromCursor(this.contentResolver, contactListImpl, peopleCursor);
		return contact;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.android.pim.enough.ContactDao#removeContact(de.enough.polish.android.pim.enough.ContactImpl)
	 */
	public void removeContact(ContactImpl contact) {
		long id = contact.getId();
		Uri personUri = ContentUris.withAppendedId(People.CONTENT_URI, id);
		this.contentResolver.delete(personUri, null, null);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.android.pim.enough.ContactDao#importContact(de.enough.polish.android.pim.enough.ContactImpl)
	 */
	public Contact importContact(ContactImpl contact) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.android.pim.enough.ContactDao#items(de.enough.polish.android.pim.enough.ContactImpl)
	 */
	public Enumeration items(ContactImpl contact) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.android.pim.enough.ContactDao#items(java.lang.String)
	 */
	public Enumeration items(String matchingValue) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.android.pim.enough.ContactDao#itemsByCategory(java.lang.String)
	 */
	public Enumeration itemsByCategory(String category) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Use this method to put the address information into the contact
	 * @param contactImpl
	 */
	public void lazyLoadAddrFields(ContactImpl contactImpl) {
		String where;
		int dataColumn;
		int typeColumn;
		long id = contactImpl.getId();
		where = Contacts.ContactMethods.PERSON_ID + " == " + id + " AND " + Contacts.ContactMethods.KIND + " == " + Contacts.KIND_POSTAL;
		
		Cursor addressCursor = contentResolver.query(Contacts.ContactMethods.CONTENT_URI, null, where, null, null);
		
		dataColumn = addressCursor.getColumnIndex(Contacts.ContactMethods.DATA);
		typeColumn = addressCursor.getColumnIndex(Contacts.ContactMethods.TYPE);
		
		while(addressCursor.moveToNext()) {
			int androidType = addressCursor.getInt(typeColumn);
			Integer attribute = convertAddressTypeToAttribute(androidType);
			String address = addressCursor.getString(dataColumn);
			String[] value = new String[ContactListImpl.CONTACT_ADDR_FIELD_INFO.numberOfArrayElements];
			value[Contact.ADDR_EXTRA] = address;
			contactImpl.addStringArray(Contact.ADDR, attribute.intValue(), value);
		}
		addressCursor.close();
	}
	
	public void lazyLoadTelFields(ContactImpl contactImpl) {
		long id = contactImpl.getId();
		String where = Contacts.ContactMethods.PERSON_ID + " == " + id;
		Cursor phoneCursor = contentResolver.query(Contacts.Phones.CONTENT_URI, null, where, null, null);
		int dataColumn = phoneCursor.getColumnIndex(Contacts.Phones.NUMBER);
		int typeColumn = phoneCursor.getColumnIndex(Contacts.Phones.TYPE);
		while(phoneCursor.moveToNext()) {
			int androidType = phoneCursor.getInt(typeColumn);
			Integer attribute = convertPhoneTypeToAttribute(androidType);
			String number = phoneCursor.getString(dataColumn);
			contactImpl.addString(Contact.TEL, attribute.intValue(), number);
		}
		phoneCursor.close();
	}
	
	private static Integer convertAddressTypeToAttribute(int addressType) {
		switch(addressType) {
		case Contacts.ContactMethods.TYPE_HOME: return new Integer(Contact.ATTR_HOME);
		case Contacts.ContactMethods.TYPE_WORK: return new Integer(Contact.ATTR_WORK);
		case Contacts.ContactMethods.TYPE_OTHER: return new Integer(Contact.ATTR_OTHER);
		case Contacts.ContactMethods.TYPE_CUSTOM: return new Integer(Contact.ATTR_OTHER);
		}
		return new Integer(PIMItem.ATTR_NONE);
	}
	
	private static Integer convertPhoneTypeToAttribute(int addressType) {
		switch(addressType) {
			case Contacts.Phones.TYPE_HOME: return new Integer(Contact.ATTR_HOME);
			case Contacts.Phones.TYPE_WORK: return new Integer(Contact.ATTR_WORK);
			case Contacts.Phones.TYPE_OTHER: return new Integer(Contact.ATTR_OTHER);
			case Contacts.Phones.TYPE_MOBILE: return new Integer(Contact.ATTR_MOBILE);
			case Contacts.Phones.TYPE_FAX_HOME: return new Integer(Contact.ATTR_FAX|Contact.ATTR_HOME);
			case Contacts.Phones.TYPE_FAX_WORK: return new Integer(Contact.ATTR_FAX|Contact.ATTR_WORK);
			case Contacts.Phones.TYPE_PAGER: return new Integer(Contact.ATTR_PAGER);
		}
		return new Integer(PIMItem.ATTR_NONE);
	}
	
}
