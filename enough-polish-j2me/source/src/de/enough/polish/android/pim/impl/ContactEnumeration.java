//#condition polish.android
package de.enough.polish.android.pim.impl;

import java.util.Enumeration;

/**
 * Enumerates contacts
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ContactEnumeration implements Enumeration{

	private final int count;
	private int position;
	private final ContactListImpl contactListImpl;
	private final ContactDao	dao;

	public ContactEnumeration(ContactListImpl contactListImpl, ContactDao dao) {
		this.contactListImpl = contactListImpl;
		this.dao = dao;
		this.position = -1;
		this.count = dao.getContactsCount();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements() {
		return (this.position < this.count -1);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Enumeration#nextElement()
	 */
	public Object nextElement() {
		this.position++;
		ContactImpl contact = this.dao.loadContact(this.position);
		contact.setContactList(this.contactListImpl);
		return contact;
	}

}
