//#condition polish.android
package de.enough.polish.android.pim.enough;

import java.util.Enumeration;

import de.enough.polish.android.midlet.MidletBridge;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.Contacts.People;

public class ContactEnumeration implements Enumeration{

	private Cursor peopleCursor;
	private ContentResolver contentResolver;
	private final ContactDao contactDao;
	private final int count;
	private int position;
	private final ContactListImpl contactListImpl;

	public ContactEnumeration(ContactDao contactDao, ContactListImpl contactListImpl) {
		this.contactDao = contactDao;
		this.contactListImpl = contactListImpl;
		this.contentResolver = MidletBridge.instance.getContentResolver();
		this.peopleCursor = this.contentResolver.query(People.CONTENT_URI, null, null, null, null);
		this.count = this.peopleCursor.getCount();
		this.position = -1;
	}

	public boolean hasMoreElements() {
		return this.position < this.count -1;
	}

	public Object nextElement() {
		if(this.peopleCursor.isClosed()) {
			return null;
		}
		this.position++;
		// Do not use moveToNext() as it will break if items are removed while iterating the cursor.
		this.peopleCursor.moveToPosition(this.position);
		ContactImpl contactFromCursor = this.contactDao.getContactFromCursor(this.peopleCursor,this.contactListImpl);
		return contactFromCursor;
	}

}
