//#condition polish.android
package de.enough.polish.android.pim.enough;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.Contacts;
import android.provider.Contacts.People;
import de.enough.polish.android.pim.Contact;
import de.enough.polish.android.pim.PIMItem;

public class ContactFactory {

	/**
	 * 
	 * @param personCursor the cursor object must be at the right position. It is not cleaned up afterwards. The caller is responsible for that.
	 * @return the contact
	 */
	public static ContactImpl getContactFromCursor(ContentResolver contentResolver, ContactListImpl contactListImpl, Cursor personCursor) {
		int columnIndex;
		
		columnIndex = personCursor.getColumnIndex(People._ID);
		int id = personCursor.getInt(columnIndex);
		
		ContactImpl contactImpl = new ContactImpl(id,contactListImpl);
		
		putNameIntoContact(personCursor,contactImpl);
		putDisplayNameIntoContact(personCursor, contactImpl);
		putNoteIntoContact(personCursor,contactImpl);
		
		contactImpl.setModified(false);
		return contactImpl;
	}
	
	private static void putNameIntoContact(Cursor personCursor, ContactImpl contactImpl) {
		int columnIndex = personCursor.getColumnIndex(People.NAME);
		String name = personCursor.getString(columnIndex);
		String[] names = new String[ContactListImpl.CONTACT_NAME_FIELD_INFO.numberOfArrayElements];
		names[Contact.NAME_OTHER] = name;
		contactImpl.addStringArray(Contact.NAME,PIMItem.ATTR_NONE, names);
	}

	/**
	 * TODO: Collaps this method and putNoteIntoContact so the code duplication is gone.
	 * @param personCursor
	 * @param contactImpl
	 */
	private static void putDisplayNameIntoContact(Cursor personCursor, ContactImpl contactImpl) {
		int columnIndex = personCursor.getColumnIndex(People.DISPLAY_NAME);
		String name = personCursor.getString(columnIndex);
		if(name != null){
			contactImpl.addString(Contact.FORMATTED_NAME, PIMItem.ATTR_NONE, name);
		}
	}

	private static void putNoteIntoContact(Cursor personCursor, ContactImpl contactImpl) {
		int columnIndex = personCursor.getColumnIndex(People.NOTES);
		while(personCursor.moveToNext()) {
			String name = personCursor.getString(columnIndex);
			if(name != null)
				contactImpl.addString(Contact.NOTE, PIMItem.ATTR_NONE, name);
		}
	}
	
}
