//#condition polish.android
package de.enough.polish.android.pim.enough;

import java.util.Enumeration;

import android.database.Cursor;
import de.enough.polish.android.pim.Contact;

public interface ContactDao {

	public void persist(ContactImpl contact);

	public Enumeration items(ContactListImpl contactListImpl);

	public ContactImpl getContactFromCursor(Cursor peopleCursor, ContactListImpl contactListImpl);

	public void removeContact(ContactImpl contact);

	public Contact importContact(ContactImpl contact);

	public Enumeration items(ContactImpl contact);

	public Enumeration items(String matchingValue);

	public Enumeration itemsByCategory(String category);
	
	public void lazyLoadAddrFields(ContactImpl contactImpl);
	
	public void lazyLoadTelFields(ContactImpl contactImpl);

}